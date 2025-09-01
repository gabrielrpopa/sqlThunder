package com.widescope.scripting.execution;

import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Object used by the gate to concat out by executor nodes
 */
public class InMemScriptData {

    private static final String rootFolder = "./storage/temp/";

    private static final
    ConcurrentHashMap<String, /*requestId*/ ConcurrentHashMap<String, /*machine base url*/ ScriptingReturnObject>> nodeExecutionOutput= new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ScriptingReturnObject >
    getRequestOutput(String requestId) {
        return nodeExecutionOutput.get(requestId);
    }

    public static ScriptingReturnObject
    getRequestMachineOutput(String requestId, String machineNode) {
        return nodeExecutionOutput.get(requestId).get(machineNode);
    }

    public static boolean
    isRequestCompleted(String requestId) {
        return !nodeExecutionOutput.get(requestId).entrySet().parallelStream().map((entry) -> entry.getValue().getIsCompleted()).toList().contains("N");
    }


    public static ConcurrentHashMap <String, ScriptingReturnObject>
    deleteRequestOutput(String requestId) {
        return nodeExecutionOutput.remove(requestId);
    }


    public static void
    addRequestData(String requestId,
                   String machineNode,
                   ScriptingReturnObject outputList) {
        ConcurrentHashMap<String, ScriptingReturnObject> output = new ConcurrentHashMap<>();
        output.put(machineNode, outputList);
        nodeExecutionOutput.putIfAbsent(requestId, output);
        nodeExecutionOutput.get(requestId).putIfAbsent(machineNode, outputList);
        nodeExecutionOutput.get(requestId).get(machineNode).putIfAbsent(outputList.getScriptingSharedDataObject());
        nodeExecutionOutput.get(requestId).get(machineNode).addLogDetails(outputList.getLogDetailList());
    }


    /**
     * Used by the Gate to serialize locally the output script for a certain requestId from a certain execution node.
     * sinkNodeCaptureData /scripting/node/sink:data endpoint is the only user of it
     */
    public static synchronized void
    serializeMachineScriptOutput(final String user,
                                 final String requestId,
                                 final String machineNode,
                                 final AppConstants appConstants) throws Exception {
        ScriptingReturnObject scriptOutput = nodeExecutionOutput.get(requestId).remove(machineNode);
        String requestIdFolder = rootFolder + requestId;
        if( !FileUtilWrapper.createDirectory(requestIdFolder) ) {
            throw new Exception("Folder creation failed: " + requestIdFolder);
        }
        String fileFullPath = requestIdFolder + "/" + machineNode;
        if(FileUtilWrapper.isFilePresent(fileFullPath)) { return; }  /*DO NOT OVERWRITE IT*/
        FileUtilWrapper.writeBufferedFile(fileFullPath, scriptOutput.toString()) ;

    }



    /**
     * Used by finalizeScriptOutput when finalizing the entire process for a requestId
     */
    private static void
    serializeMachineOutput(final String requestId,
                           final String machineNode,
                           final ScriptingReturnObject sOutput) throws Exception {
        String requestIdFolder = rootFolder + requestId;
        String mFile = requestIdFolder + "/" + machineNode;
        if(FileUtilWrapper.isFilePresent(mFile)) { return; }
        if( !FileUtilWrapper.createDirectory(requestIdFolder) ) {
            throw new Exception("Folder creation failed: " + requestIdFolder);
        }
        FileUtilWrapper.writeBufferedFile(mFile, sOutput.toString()) ;
    }


    /**
     * Used by the Gate to finalize the entire script output. only used by sinkNodeCaptureData /scripting/node/sink:data endpoint
     * @param user - username / email
     * @param requestId - user request id
     * @param appConstants - AppConstants
     * @return boolean true if requestId allocation is empty, otherwise false
     */
    public static synchronized boolean
    finalizeScriptOutput(final String user, final String requestId, final AppConstants appConstants) {
        InMemScriptData.getRequestOutput(requestId).forEach( (key, value) -> {
            try {
                serializeMachineOutput(requestId, key, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return InMemScriptData.deleteRequestOutput(requestId).isEmpty();
    }


    public static long getCurrentScriptsRunning() {
        long cnt = 0;
        for(String requestId : nodeExecutionOutput.keySet()) {
            cnt += nodeExecutionOutput.get(requestId).size();
        }
        return cnt;
    }
}
