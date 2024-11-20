package com.widescope.scripting.execution;

import com.widescope.logging.AppLogger;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemScriptLog {

    private static final
    ConcurrentHashMap<String, /*requestId*/ ConcurrentHashMap<String, /*machine base url*/ List<ScriptingReturnObject>>> nodeExecutionOutput= new ConcurrentHashMap<>();

    private	static final
    ConcurrentHashMap<String /*requestId*/, ScriptRequestTime> requestIdNodeReference= new ConcurrentHashMap<>();

    private static synchronized long
    getMillisecondsSinceEpoch() {
        return DateTimeUtils.millisecondsSinceEpoch();
    }

    public static ConcurrentHashMap<String, List<ScriptingReturnObject> >
    getRequestOutput(String requestId) {
        return nodeExecutionOutput.get(requestId);
    }

    public static void
    addRequestOutput(String requestId, String machineNode, List<ScriptingReturnObject> outputList, String isTerminal) {
        long timeStamp = getMillisecondsSinceEpoch();
        requestIdNodeReference.putIfAbsent(requestId, new ScriptRequestTime(timeStamp));
        requestIdNodeReference.get(requestId).setCurrentTime(timeStamp, isTerminal);


        ConcurrentHashMap<String, List<ScriptingReturnObject>> output = new ConcurrentHashMap<>();
        output.put(machineNode, outputList);
        nodeExecutionOutput.putIfAbsent(requestId, output);

        if( nodeExecutionOutput.get(requestId).containsKey(machineNode) ) {
            nodeExecutionOutput.get(requestId).get(machineNode).addAll(outputList);
        } else {
            nodeExecutionOutput.get(requestId).putIfAbsent(machineNode, outputList);
        }
    }

    public static ConcurrentHashMap <String, List<ScriptingReturnObject>>
    deleteRequestOutput(String requestId) {
        return nodeExecutionOutput.remove(requestId);
    }

    public static List<ScriptingReturnObject>
    deleteRequestOutput(String requestId, String machineNode) {
        return nodeExecutionOutput.get(requestId).remove(machineNode);
    }


    public static synchronized void
    serializeScriptOutput(final String requestId, final String machineNode, final AppConstants appConstants) {
        try( FileWriter fw = new FileWriter(machineNode, true);  BufferedWriter bw = new BufferedWriter(fw);  PrintWriter pw = new PrintWriter(bw)) {
            String requestIdFolder = appConstants.getScriptStoragePath() + "/" + requestId;
            FileUtilWrapper.makeDirectoryIfNotExist(requestIdFolder);
            List<ScriptingReturnObject> lst = nodeExecutionOutput.get(requestId).remove(machineNode);
            for(ScriptingReturnObject o: lst) {
                pw.write(o + System.lineSeparator());
            }
        } catch (IOException ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
    }


    public static synchronized void
    serializeScriptOutput() {

    }

}
