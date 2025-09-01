package com.widescope.scripting;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.scripting.websock.ScriptFooterOutput;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.compression.ZipDirectory;
import com.widescope.sqlThunder.utils.restApiClient.RestApiScriptingClient;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.stream.Stream;

public class ScriptUtils {

    public static String prepareNodeScriptFolder(final String session,
                                                 final String requestId,
                                                 final String tmpPath,
                                                 final String user,
                                                 final MultipartFile attachment) throws IOException {
        String separator = FileSystems.getDefault().getSeparator();
        final String destPath = tmpPath + separator + user + separator + requestId;
        String zipFileName = StringUtils.generateUniqueString16();
        File attachmentFile = File.createTempFile(zipFileName, ".temp");
        String absolutePathForTempFile = attachmentFile.getAbsolutePath();
        FileOutputStream o = new FileOutputStream(attachmentFile);
        IOUtils.copy(attachment.getInputStream(), o);
        o.close();
        ZipDirectory.unzip(absolutePathForTempFile, destPath);
        return destPath;
    }

    public static void checkRepoScriptFolder(final String scriptVersionPath, final ScriptDetail scriptInfo) throws Exception {
        String separator = FileSystems.getDefault().getSeparator();
        String scriptVersionMainFilePath = scriptVersionPath + separator + scriptInfo.getMainFile() ;
        File pathMainFile = new File(scriptVersionMainFilePath);
        if( ! pathMainFile.exists() ) {
            throw new Exception("Fatal error. Script main file " +  scriptVersionPath + " is not present in the corresponding folder");
        }
    }


    public static void interpolateRepoScript(final ScriptParamRepoList scriptParameters,
                                             final AppConstants appConstants,
                                             final String scriptVersionPath,
                                             final String user,
                                             final String session,
                                             final long userId,
                                             final String requestId,
                                             final ScriptDetail scriptInfo) throws Exception {

        String separator = FileSystems.getDefault().getSeparator();
        for(ScriptParamList2 node: scriptParameters.getScriptParamRepoList()) {
            String zipFileName = StringUtils.generateUniqueString16();
            final String tmpPath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + requestId;
            final String zipPath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + zipFileName + ".zip";
            FileUtilWrapper.copyFolder(scriptVersionPath, tmpPath);
            FileUtilWrapper.interpolateParams(tmpPath, node.getScriptParamList());
            FileUtilWrapper.interpolateParams(tmpPath, user, session, requestId);
            ZipDirectory.zip(tmpPath, zipPath);
            RestApiScriptingClient.runNodeRepoScript(node.getNodeUrl(), user, session, appConstants, scriptInfo, requestId, zipPath);
            FileUtilWrapper.deleteFile(zipPath);
            FileUtilWrapper.deleteDirectoryWithAllContent(tmpPath);
        }
    }

    public static String getScriptCommand(final ScriptDetail scriptInfo, final AppConstants appConstants) {
        String separator = FileSystems.getDefault().getSeparator();
        final String executionFolder = ScriptingHelper.getScriptExecutionFolder(scriptInfo.getScriptName(), appConstants);
        return executionFolder + separator	+ scriptInfo.getCommand() + " "	+ executionFolder + separator + scriptInfo.getMainFile();
    }

    public static void sendNotificationWithError(final String requestId, final String user, final Exception e) {
        AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, e.getMessage(), ClusterDb.ownBaseUrl);
        WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
        ScriptFooterOutput scriptFooterOutput = new ScriptFooterOutput(0);
        wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, scriptFooterOutput, ClusterDb.ownBaseUrl);
        WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
    }

    public static List<String> getMachineList(final String machineList) {
        return Stream.of(machineList.split(",", -1)).toList();
    }

}
