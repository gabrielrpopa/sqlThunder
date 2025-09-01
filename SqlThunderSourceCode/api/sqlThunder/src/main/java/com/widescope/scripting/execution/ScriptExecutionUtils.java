package com.widescope.scripting.execution;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.scripting.ScriptingHelper;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.scripting.db.InterpreterType;
import com.widescope.scripting.db.ScriptExecutedRecord;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import java.util.List;



public class ScriptExecutionUtils {

    /**
     * Decides if execution will take place on current machine or distributed across the cluster machines
     * */
    public static boolean isLocalNode(List<String> mList, String localBaseUrl) {
        return (mList == null || mList.isEmpty() || ( mList.size() == 1 && mList.get(0).compareToIgnoreCase(localBaseUrl) == 0)) ;
    }


    /**
     * Runs the script by sending websockets notifications to web or thick client
     * @return ScriptingReturnObject, which in this case is just empty stating streaming is on
     */
    public static ScriptingReturnObject
    runLocalAdhocWithPush(final User u,
                          final String session,
                          final AppConstants appConstants,
                          final InterpreterType interpreterType,
                          final String scriptName,
                          final String requestId,
                          final String scriptContent,
                          final long timeStamp,
                          final long groupId,
                          com.widescope.persistence.PersistenceWrap pWrap) {

        try {
            ScriptingReturnObject ret = ScriptingHelper.copyScripts(u, session, appConstants, interpreterType, scriptName, scriptContent, requestId, timeStamp);
            ret.getScriptingSharedDataObject().setScriptType("ADHOC");
            ScriptingHelper.runLocalAdhocWithPush(scriptName, interpreterType, requestId, u, scriptContent, appConstants, timeStamp,groupId, pWrap);
            ret.setIsCompleted("Y");
            return ret;
        } catch (Exception e) {
            AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            WebsocketPayload wsPayload = new WebsocketPayload(requestId, u.getUser(), u.getUser(), WebsocketMessageType.detailScript, e.getMessage(), ClusterDb.ownBaseUrl);
            WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
            InMemScriptData.deleteRequestOutput(requestId);
            ScriptingReturnObject ret = new ScriptingReturnObject(requestId, "N", "Y");
            ret.setIsCompleted("Y"); ;
            return ret;
        }

    }


    public static ScriptingReturnObject
    runLocalAdhocWithCollect( final User u,
                              final String session,
                              final AppConstants appConstants,
                              final InterpreterType interpreterType,
                              final String scriptName,
                              final String requestId,
                              final String scriptContent,
                              final long timeStamp,
                              final long groupId,
                              PersistenceWrap pWrap) {

        ScriptingReturnObject ret = null;

        try {
            ScriptingHelper.copyScripts(u, session, appConstants, interpreterType, scriptName, scriptContent, requestId, timeStamp);
            ret = ScriptingHelper.runLocalAdhocWithCollect(scriptName, interpreterType, requestId, u, scriptContent, appConstants, timeStamp, groupId, pWrap);
            ret.setIsCompleted("Y");
            ret.getScriptingSharedDataObject().setScriptType("ADHOC");
            ret.getScriptingSharedDataObject().setScriptName(scriptName);
            ret.getScriptingSharedDataObject().setInterpreterName(interpreterType.getInterpreterName());

            return ret;
        } catch (Exception e) {
            AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            WebsocketPayload wsPayload = new WebsocketPayload(requestId, u.getUser(), u.getUser(), WebsocketMessageType.detailScript, e.getMessage(), ClusterDb.ownBaseUrl);
            WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
            InMemScriptData.deleteRequestOutput(requestId);
            ret = new ScriptingReturnObject(requestId, "N", "Y");
            ret.setIsCompleted("Y"); ;
            return ret;
        }

    }



}
