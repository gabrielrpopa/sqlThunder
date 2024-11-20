package com.widescope.scripting.execution;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.scripting.ScriptingHelper;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;

import java.io.IOException;
import java.util.List;

public class ScriptExecutionUtils {

    public static String interpolateVars(final String user,
                                         final String session,
                                         final String auth,
                                         final String requestId,
                                         String scriptContent,
                                         final String updateGlobalVars,
                                         final AppConstants appConstants) {

        if(scriptContent.toLowerCase().contains(updateGlobalVars.toLowerCase())) {
            scriptContent = ScriptingHelper.replaceScriptUserSession(	user,
                    session,
                    auth,
                    appConstants.getServerSslEnabled() ? "https":"http",
                    appConstants.getServerPort(),
                    requestId,
                    scriptContent);
        } else {
            scriptContent = ScriptingHelper.addLibraryGlobalParam(	auth,
                    user,
                    session,
                    appConstants.getAdmin(),
                    appConstants.getAdminPasscode(),
                    appConstants.getServerSslEnabled() ? "https":"http",
                    appConstants.getServerPort()  );
        }

        return scriptContent;
    }

    /**
     * Decides if execution will take place on current machine or distributed across the cluster machines
     * */
    public static boolean isLocalNode(List<String> mList, String localBaseUrl) {
        return (mList == null || mList.isEmpty() || ( mList.size() == 1 && mList.get(0).compareToIgnoreCase(localBaseUrl) == 0)) ;
    }


    /**
     * Run the script by sending websockets notifications to web or thick client
     * @param appConstants
     * @param scriptName
     * @param interpreterId
     * @param requestId
     * @param user
     * @param session
     * @param scriptContent
     * @return ScriptingReturnObject, which in this case is just empty stating streaming is on
     */
    public static ScriptingReturnObject
    execScriptAsyncWith(final AppConstants appConstants,
                        final String scriptName,
                        final String interpreterId,
                        final String requestId,
                        final String user,
                        final String session,
                        final String scriptContent) {

        ScriptingReturnObject scriptRet = new ScriptingReturnObject(requestId, "Y");

        try {
            ScriptingHelper.runAdhocWithNotificationsToClient(  appConstants.getScriptTempPath(),
                                                                scriptName,
                                                                Integer.parseInt( interpreterId ),
                                                                requestId,
                                                                user,
                                                                session,
                                                                scriptContent);


        } catch (IOException e) {
            AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, e.getMessage(), ClusterDb.ownBaseUrl);
            WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
        }

        return scriptRet;

    }


    public static ScriptingReturnObject
    execScriptSync(final AppConstants appConstants,
                   final String scriptName,
                   final String interpreterId,
                   final String requestId,
                   final String user,
                   final String session,
                   final String scriptContent) {
        ScriptingReturnObject scriptRet = new ScriptingReturnObject(requestId, "N");
        try {
            scriptRet = ScriptingHelper.runAdhocWithRet(appConstants.getScriptTempPath(), scriptName, Integer.parseInt( interpreterId ), requestId, session, scriptContent);
            scriptRet.setIsStreaming("N");
        } catch (IOException e) {
            AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
        return scriptRet;
    }

}
