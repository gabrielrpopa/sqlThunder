package com.widescope.scripting.execution;


import com.widescope.logging.AppLogger;
import com.widescope.scripting.ScriptDetail;
import com.widescope.scripting.ScriptParamRepoList;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.restApiClient.RestApiScriptingClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * Used by the Gate Node in order to distribute jobs to Executor Nodes that are complex folder projects
 */
public class ProjectExecThread implements Callable<ScriptingReturnObject>  {

    private final String toBaseUrl;
    private final String user;
    private final String session;
    private final AppConstants appConstants;
    private final ScriptParamRepoList scriptParamRepoList;
    private final ScriptDetail scriptInfo;
    private final String filePath;


    public ProjectExecThread(final String toBaseUrl,
                             final String user,
                             final String session,
                             final AppConstants appConstants,
                             final ScriptParamRepoList scriptParamRepoList,
                             final ScriptDetail scriptInfo,
                             final String filePath) {
        this.toBaseUrl = toBaseUrl;
        this.user = user;
        this.session = session;
        this.appConstants = appConstants;
        this.scriptParamRepoList = scriptParamRepoList;
        this.scriptInfo = scriptInfo;
        this.filePath = filePath;
    }

    @Override
    public ScriptingReturnObject call() {
        return RestApiScriptingClient.runNodeRepoScript(   this.toBaseUrl,
                                                                this.user,
                                                                this.session,
                                                                this.appConstants,
                                                                this.scriptInfo,
                                                                this.scriptParamRepoList.getRequestId(),
                                                                this.filePath);
    }


    public static ScriptingReturnObject
    runRepoScriptDistributed(final List<String> machineList,
                             final String user,
                             final String session,
                             final AppConstants appConstants,
                             final ScriptParamRepoList scriptParamRepoList,
                             final ScriptDetail scriptInfo,
                             final String filePath) {
        ScriptingReturnObject scriptRet = new ScriptingReturnObject(scriptParamRepoList.getRequestId(), "N");
        scriptRet.getScriptingSharedDataObject().setScriptName(scriptInfo.getScriptName());
        scriptRet.getScriptingSharedDataObject().setInterpreterName(scriptInfo.getInterpreterName());
        scriptRet.getScriptingSharedDataObject().setScriptType("REPO");

        List<Future<ScriptingReturnObject>> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(machineList.size());
        for (String toBaseUrl: machineList) {
            Callable<ScriptingReturnObject> task = new ProjectExecThread(toBaseUrl, user, session, appConstants, scriptParamRepoList, scriptInfo, filePath);
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
        scriptRet.setIsCompleted("Y"); scriptRet.setIsError("N");
        return scriptRet;
    }

}
