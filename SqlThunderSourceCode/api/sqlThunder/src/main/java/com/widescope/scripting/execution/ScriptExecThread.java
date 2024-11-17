package com.widescope.scripting.execution;


import com.widescope.logging.AppLogger;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.scripting.db.InterpreterType;
import com.widescope.scripting.db.ScriptingInternalDb;
import com.widescope.scripting.storage.HistScriptFileManagement;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.restApiClient.RestApiScriptingClient;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ScriptExecThread implements Callable<ScriptingReturnObject>  {

    private final String toBaseUrl;
    private final String user;
    private final String session;
    private final AppConstants appConstants;
    private final String scriptName;
    private final int interpreterId;
    private final String requestId;
    private final String scriptContent;
    
    public ScriptExecThread(final String toBaseUrl,
                            final String user,
                            final String session,
                            final AppConstants appConstants,
                            final String scriptName,
                            final int interpreterId,
                            final String requestId,
                            final String scriptContent) {
        this.toBaseUrl = toBaseUrl;
        this.user = user;
        this.session = session;
        this.appConstants = appConstants;
        this.scriptName = scriptName;
        this.interpreterId = interpreterId;
        this.requestId = requestId;
        this.scriptContent = scriptContent;
    }


    @Override
    public ScriptingReturnObject call() {
        return RestApiScriptingClient.runAdhocScriptViaNode(toBaseUrl,
                                                            user,
                                                            session,
                                                            appConstants.getUser(),
                                                            appConstants.getUserPasscode(),
                                                            scriptName,
                                                            interpreterId,
                                                            requestId,
                                                            scriptContent);
    }



    public static ScriptingReturnObject
    execParallelSync(final List<String> machineList,
                     final String user,
                     final String session,
                     final AppConstants appConstants,
                     final String scriptName,
                     final String interpreterId,
                     final String requestId,
                     final String scriptContent) {
        int iId = Integer.parseInt(interpreterId);
        final String endPoint = "/scripting/script/adhoc/node:sink";
        List<Future<ScriptingReturnObject>> list = new ArrayList<Future<ScriptingReturnObject>>();
        ScriptingReturnObject scriptRet = new ScriptingReturnObject(requestId, "N");
        ExecutorService executor = Executors.newFixedThreadPool(machineList.size());
        for (String node: machineList) {
            Callable<ScriptingReturnObject> task = new ScriptExecThread(node + endPoint, user, session, appConstants, scriptName, iId, requestId, scriptContent);
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


    public static ScriptingReturnObject
    execParallelAsync(final List<String> machineList,
                      final String user,
                      final String session,
                      final AppConstants appConstants,
                      final String scriptName,
                      final String interpreterId,
                      final String requestId,
                      final String scriptContent) {

        int iId = Integer.parseInt(interpreterId);
        final String endPoint = "/scripting/script/adhoc/node:sink";
        ScriptingReturnObject scriptRet = new ScriptingReturnObject();
        List<Future<ScriptingReturnObject>> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(machineList.size());
        for (String node: machineList) {
            Callable<ScriptingReturnObject> task = new ScriptExecThread(node + endPoint, user, session, appConstants, scriptName, iId , requestId, scriptContent);
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


    public static void saveAdhocScriptAsync(final AppConstants appConstants,
                                            final ScriptingInternalDb scriptingInternalDb,
                                            final String interpreterId,
                                            final AuthUtil authUtil,
                                            final String scriptName,
                                            final String scriptContent,
                                            final String user) {
        Runnable t = () -> {
            if( ConfigRepoDb.configValues.get("save-adhoc").getConfigValue().compareTo("Y") == 0) {
                try{
                    User u = authUtil.getUser(user);
                    long userId = u.getId();
                    String mainFolder = appConstants.getScriptStoragePath();
                    InterpreterType interpreterType = scriptingInternalDb.interpreterByIdGet(Integer.parseInt( interpreterId ));
                    String iName = interpreterType.getInterpreterName();
                    String result = HistScriptFileManagement.addNewScript(mainFolder, "adhoc", iName, userId, scriptName, scriptContent, "");
                    if(!result.isEmpty() ) {
                        AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.thread,  "Adhoc Script " + scriptName + " cannot be saved");
                    }
                }
                catch(Exception ex) {
                    AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
                } catch(Throwable ex)	{
                    AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
                }
            }

        };
        t.run();

    }





}
