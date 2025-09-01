package com.widescope.scripting.execution;


import com.widescope.logging.AppLogger;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.scripting.db.InterpreterType;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.restApiClient.RestApiScriptingClient;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * Used by the Gate Node in order to distribute jobs to Executor Nodes that is a simple script or command
 */
public class ScriptExecThread implements Callable<ScriptingReturnObject>  {

    private final String toBaseUrl;
    private final String user;
    private final String session;
    private final AppConstants appConstants;
    private final String scriptName;
    private final String interpreter;
    private final String requestId;
    private final String scriptContent;
    private long timeStamp;

    public ScriptExecThread(final String toBaseUrl,
                            final String user,
                            final String session,
                            final AppConstants appConstants,
                            final String scriptName,
                            final String interpreter,
                            final String requestId,
                            final String scriptContent,
                            final long timeStamp) {
        this.toBaseUrl = toBaseUrl;
        this.user = user;
        this.session = session;
        this.appConstants = appConstants;
        this.scriptName = scriptName;
        this.interpreter = interpreter;
        this.requestId = requestId;
        this.scriptContent = scriptContent;
        this.timeStamp = timeStamp;
    }


    @Override
    public ScriptingReturnObject call() {
        return RestApiScriptingClient.runNodeAdhocScript(toBaseUrl, user, session, appConstants.getUser(), appConstants.getUserPasscode(), scriptName, interpreter, requestId, scriptContent, timeStamp);
    }



    public static ScriptingReturnObject
    runAdhocScriptDistributed(final List<String> machineList,
                              final String user,
                              final String session,
                              final AppConstants appConstants,
                              final String scriptName,
                              final InterpreterType interpreterType,
                              final String requestId,
                              final String scriptContent,
                              final long timeStamp ) {
        ScriptingReturnObject scriptRet = new ScriptingReturnObject(requestId, "N");
        List<Future<ScriptingReturnObject>> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(machineList.size());
        for (String node: machineList) {
            Callable<ScriptingReturnObject> task = new ScriptExecThread(node, user, session, appConstants, scriptName, interpreterType.getInterpreterName() , requestId, scriptContent, timeStamp);
            Future<ScriptingReturnObject> future = executor.submit(task);
            list.add(future);
        }

        for(Future<ScriptingReturnObject> future : list){
            try {
                ScriptingReturnObject result = future.get();
                scriptRet.concatScriptingReturnObject(result);
            } catch (InterruptedException | ExecutionException e) {
                AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return scriptRet;
    }


}
