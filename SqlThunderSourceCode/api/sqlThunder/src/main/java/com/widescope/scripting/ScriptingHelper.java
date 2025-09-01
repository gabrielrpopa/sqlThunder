/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.widescope.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import com.widescope.logging.AppLogger;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.scripting.db.ScriptExecutedRecord;
import com.widescope.scripting.websock.LogDetailTypes;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.scripting.attachment.ScriptAttachment;
import com.widescope.scripting.attachment.ScriptAttachmentList;
import com.widescope.scripting.db.InterpreterType;
import com.widescope.scripting.db.ScriptingInternalDb;
import com.widescope.scripting.websock.ScriptFooterOutput;
import com.widescope.scripting.websock.ScriptHeaderOutput;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileCharacteristic;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.compression.ZipDirectory;
import com.widescope.sqlThunder.utils.restApiClient.RestApiScriptingClient;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class ScriptingHelper {




	/**
	 * Runs script or command and returns log to calling function. This function saves the script for later use with pWrap.saveScriptExecution
	 */
	public static ScriptingReturnObject
	runRepoScriptWithCollect(final AppConstants appConstants,
							 final ScriptDetail scriptInfo,
							 final String requestId,
							 final long timeStamp,
							 final User u,
							 final long groupId,
							 PersistenceWrap pWrap)  {
		int count = 0;
		ScriptingReturnObject fullObject = new ScriptingReturnObject(requestId);
		fullObject.getScriptingSharedDataObject().setScriptName(scriptInfo.getScriptName());
		fullObject.getScriptingSharedDataObject().setInterpreterName(scriptInfo.getInterpreterName());
		fullObject.getScriptingSharedDataObject().setScriptType("REPO");
		final String command = ScriptUtils.getScriptCommand( scriptInfo,  appConstants);
        try {
        	System.out.println(command);
            Process p = Runtime.getRuntime().exec(command);
			count = count + readStdin(p, fullObject);
			count = count + readStdErr(p, fullObject);
        }
        catch (Exception e) {
			count = count + readException( e, fullObject);
			fullObject.setIsError("Y");
        } finally {
			fullObject.setIsCompleted("Y");
			defaultRuntimeVarMultipart(appConstants, scriptInfo, timeStamp);
			final ScriptExecutedRecord rec = new ScriptExecutedRecord(-1, scriptInfo.getScriptId(), requestId, scriptInfo.getScriptName(), "this-machine", fullObject.toStringPretty(), scriptInfo.getInterpreterId(), groupId, Constants.repoShort, u.getId(), command, "", "", "", timeStamp, -1, "Y");
			pWrap.saveExecution(rec, fullObject, "Y");
			System.out.println("Collected :" + count + " items");
		}
		return fullObject;
	}


	/**
	 * Run Repo script and push log to calling user via websockets, assuming user is connected via websockets at all times. It does save the script and results
	 */
	public static ScriptingReturnObject
	runRepoScriptWithPush(final AppConstants appConstants,
						  final User u,
						  final ScriptDetail scriptInfo,
						  final String requestId,
						  final long timeStamp,
						  final long groupId,
						  PersistenceWrap pWrap) {
		ScriptingReturnObject fullObject = new ScriptingReturnObject(requestId, "Y");
		fullObject.getScriptingSharedDataObject().setScriptName(scriptInfo.getScriptName());
		fullObject.getScriptingSharedDataObject().setInterpreterName(scriptInfo.getInterpreterName());
		fullObject.getScriptingSharedDataObject().setScriptType("REPO");
		final String command = ScriptUtils.getScriptCommand( scriptInfo,  appConstants);

		int count = 0;
		try {
			ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("REPO");
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, u.getUser(), u.getUser(), WebsocketMessageType.headerScript, scriptHeaderOutput, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
			Process p = Runtime.getRuntime().exec(command);
			count = count + readStdinToWebSock(requestId, u.getUser(), p, fullObject);
			count = count + readStdErrToWebSock(requestId, u.getUser(), p, fullObject);
		}
		catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			count = count + readExceptionToWebSock(requestId, u.getUser(), e, fullObject, count);
			fullObject.setIsError("Y");
		} finally {
			finalizeToWebSock(requestId, u.getUser(), count);
			final ScriptExecutedRecord rec = new ScriptExecutedRecord(-1, scriptInfo.getScriptId(), requestId, scriptInfo.getScriptName(), "this-machine", fullObject.toStringPretty(), scriptInfo.getInterpreterId(), groupId, Constants.repoShort, u.getId(), command, "", "", "", timeStamp, -1, "Y");
			pWrap.saveExecution(rec, fullObject, "Y");
			defaultRuntimeVarMultipart(appConstants, scriptInfo, timeStamp);
			fullObject.clearData();
			fullObject.setIsCompleted("Y");
			System.out.println("Collected :" + count + " items");
		}

		return fullObject;
	}


	/**
	 * Runs an ad-hoc script by the execution node. The request coming from the gate. Does not save script or result
	 * @param scriptName
	 * @param interpreter
	 * @param requestId
	 * @param u
	 * @param session
	 * @param content
	 * @param baseUrl
	 * @param appConstants
	 * @param timeStamp
	 * @return
	 * @throws Exception
	 */
	public static ScriptingReturnObject
	runAdhocWithNotificationsToGate(final String scriptName,
									final String interpreter,    /*com.widescope.scripting.interpreter*/
									final String requestId,
									final User u,
									final String session,
									final String content,
									final String baseUrl,
									final AppConstants appConstants,
									final long timeStamp) throws Exception {
		int count = 0;

		ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
		InterpreterType interpreterType = scriptingInternalDb.interpreter(interpreter);
		ScriptingReturnObject fullObject = new ScriptingReturnObject(requestId);
		String fullCommand = "";
		try {
			String savedScriptName = scriptName + "_" + timeStamp + "." + interpreterType.getFileExtensions();
			FileUtilWrapper.overwriteFile (appConstants.getTempPath() + Constants.scriptFolder,	savedScriptName , content);
			RestApiScriptingClient.updateGateSink(u.getUser(), session, requestId, baseUrl,  new ScriptingReturnObject(requestId, new LogDetail(LogDetailTypes.header, "")));

			if(interpreterType.getInterpreterName().toUpperCase().compareTo("WINDOWS BATCH") == 0 ||
					interpreterType.getInterpreterName().toUpperCase().compareTo("BASH") == 0 ) {
				fullCommand = appConstants.getTempPath() + Constants.scriptFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions();
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", fullCommand);
				System.out.println(builder.command());
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
				count = count + readStdinGate(requestId,  u.getUser(), session, baseUrl, p, fullObject);
			} else {
				fullCommand = interpreterType.getCommand() + " " + appConstants.getTempPath() + Constants.scriptFolder + "\\" + savedScriptName ;
				System.out.println(fullCommand);
				Process p = Runtime.getRuntime().exec(fullCommand);
				count = count + readStdinGate(requestId,  u.getUser(), session, baseUrl, p, fullObject);
				count = count + readStdErrGate(requestId,  u.getUser(), session, baseUrl, p, fullObject);
			}
			finalizeToGate(requestId, u.getUser(), session, baseUrl, count, "N");
		} catch (Exception e) {
			count = count + readExceptionGate(requestId,  u.getUser(), session, baseUrl, fullObject, e);
			finalizeToGate(requestId, u.getUser(), session, baseUrl, count, "Y");
		} finally {
			defaultRuntimeVarAdhoc(appConstants.getTempPath());
			System.out.println("Collected :" + count + " items");
		}

		return fullObject;
	
	}

	/**
	 * Runs an ad-hoc script by the execution node. The request coming from the gate. This function executed in the node does not save the script or output
	 */
	public static ScriptingReturnObject
	runMultipartWithNotificationsToGate(final String scriptName,
										final String interpreterName,
										final String requestId,
										final String user,
										final String session,
										final String destPath,
										final String baseUrl,
										final AppConstants appConstants,
										final long timeStamp) throws Exception {
		int count = 0;
		ScriptingReturnObject fullObject = new ScriptingReturnObject(requestId);
		ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
		InterpreterType interpreterType = scriptingInternalDb.getInterpreterByName(interpreterName);

		String executionFolder = appConstants.getTempPath() + Constants.scriptFolder + "/" + scriptName;
		try {

			fullObject.addLogDetail(new LogDetail(LogDetailTypes.header, ""));
			RestApiScriptingClient.updateGateSink(user, session, requestId, baseUrl,  new ScriptingReturnObject(requestId, "N", new LogDetail(LogDetailTypes.header, "")));

			if(interpreterType.getInterpreterName().toUpperCase().compareTo("WINDOWS BATCH") == 0 ||
					interpreterType.getInterpreterName().toUpperCase().compareTo("BASH") == 0 ) {
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", appConstants.getTempPath() + Constants.scriptFolder + "\\" + scriptName);
				System.out.println(builder.command());
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
				count = count + readStdinGate(requestId,  user, session, baseUrl, p, fullObject);
			} else {
				String fullCommand = interpreterType.getCommand() + " " + destPath + "\\" + scriptName ;
				System.out.println(fullCommand);
				Process p = Runtime.getRuntime().exec(fullCommand);
				count = count + readStdinGate(requestId,  user, session, baseUrl, p, fullObject);
				count = count + readStdErrGate(requestId,  user, session, baseUrl, p, fullObject);
			}
			finalizeToGate(requestId, user,session, baseUrl, count, "N");
		} catch (Exception e) {
			count = count + readExceptionGate(requestId, user, session, baseUrl, fullObject, e);
			finalizeToGate(requestId, user,session, baseUrl, count, "Y");
		} finally {
			defaultRuntimeVarMultipart(destPath, scriptName, timeStamp);
			System.out.println(scriptName + " collected :" + count + " items");
		}
		return fullObject;
	}

	public static ScriptingReturnObject
	copyScripts(final User u,
				final String session,
				AppConstants appConstants,
				InterpreterType interpreterType,
				String scriptName,
				String scriptContent,
				String requestId,
				long timeStamp) throws Exception {
		ScriptingReturnObject ret = new ScriptingReturnObject(requestId, "N", "Y");
		final String executionFolder = ScriptingHelper.getScriptExecutionFolder(scriptName, appConstants);
		final String fullScriptName = ScriptingHelper.getScriptFullName(scriptName, timeStamp, interpreterType);
		FileUtilWrapper.overwriteFile (executionFolder,	fullScriptName, scriptContent); /*adhoc script*/

		Resource r = new ClassPathResource("libs/python/python3/sqlThunderRestClient.py");
		String apiUrl = "http://" + appConstants.getServerIpStatic() + ":" + appConstants.getServerPort()  + appConstants.getServerServletContextPath() ;
		String libContent = ScriptingHelper.replaceLibraryRuntimeVars(u.getUser(), session, r.getContentAsString(Charset.defaultCharset()), apiUrl);
		FileUtilWrapper.deleteFile(executionFolder +	"/sqlThunderRestClient.py");
		FileUtilWrapper.overwriteFile (executionFolder,"sqlThunderRestClient.py", libContent);
		ret.getScriptingSharedDataObject().setScriptName(scriptName);
		ret.getScriptingSharedDataObject().setInterpreterName(interpreterType.getInterpreterName());
		return ret;
	}


	public static void defaultRuntimeVarAdhoc(String tmpFolder)  {
		try {
			FileUtilWrapper.deleteFile(tmpFolder + "/sqlThunderRestClient.py");
		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}

	public static void defaultRuntimeVarMultipart(final AppConstants appConstants,
												  final ScriptDetail scriptInfo,
												  final long timeStamp)  {
		try {
			final String executionFolder = ScriptingHelper.getScriptExecutionFolder(scriptInfo.getScriptName(), appConstants);
			String ext = FileUtilWrapper.getFileExtension(executionFolder + "/" + scriptInfo.getScriptName());
			String fileNoExt = FilenameUtils.removeExtension(executionFolder + "/" + scriptInfo.getScriptName());
			FileUtilWrapper.deleteFile(executionFolder + "/" + scriptInfo.getScriptName());
			FileUtilWrapper.overwriteFile (executionFolder, fileNoExt + "_" + timeStamp + "." + ext, scriptInfo.getScriptName());
			FileUtilWrapper.deleteFile(executionFolder + "/sqlThunderRestClient.py");
		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}


	public static void defaultRuntimeVarMultipart(final String tmpFolder,
												  final String  scriptName,
												  final long timeStamp)  {
		try {
			String ext = FileUtilWrapper.getFileExtension(tmpFolder + "/" + scriptName);
			String fileNoExt = FilenameUtils.removeExtension(tmpFolder + "/" + scriptName);
			FileUtilWrapper.deleteFile(tmpFolder + "/" + scriptName);
			FileUtilWrapper.overwriteFile (tmpFolder, fileNoExt + "_" + timeStamp + "." + ext, scriptName);
			FileUtilWrapper.deleteFile(tmpFolder + "/sqlThunderRestClient.py");
		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}


	/**
	 * Runs an adhoc script pushing notifications to caller. It does save the script for later use.
	 */
	public static void
	runLocalAdhocWithPush(final String scriptName,
						  final InterpreterType interpreterType,
						  final String requestId,
						  final User u,
						  final String content,
						  final AppConstants appConstants,
						  final long timeStamp,
						  final long groupId,
						  com.widescope.persistence.PersistenceWrap pWrap)  {

		ScriptingReturnObject fullObject = new ScriptingReturnObject(requestId);
		int count = 0;
		String fullCommand = "";
		try {
			final String executionFolder = ScriptingHelper.getScriptExecutionFolder(scriptName, appConstants);
			final String fullScriptName = ScriptingHelper.getScriptFullName(scriptName, timeStamp, interpreterType );
			FileUtilWrapper.overwriteFile (executionFolder,	fullScriptName, content);

			if(interpreterType.getInterpreterName().toUpperCase().compareTo("WINDOWS BATCH") == 0 ||
					interpreterType.getInterpreterName().toUpperCase().compareTo("BASH") == 0 ) {
				fullCommand = executionFolder + "\\" + fullScriptName;
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", fullCommand);
		        builder.redirectErrorStream(true);
				System.out.println(builder.command());
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, u.getUser(), u.getUser(), WebsocketMessageType.headerScript, scriptHeaderOutput, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
				Process p = builder.start();
				count = count + readStdinToWebSock(requestId, u.getUser(), p,  fullObject);
			} else {
				fullCommand = interpreterType.getCommand() + " " + executionFolder + "\\" + fullScriptName;
				System.out.println(fullCommand);
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, u.getUser(), u.getUser(), WebsocketMessageType.headerScript, scriptHeaderOutput, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
				Process p = Runtime.getRuntime().exec(fullCommand);
				count = count + readStdinToWebSock(requestId, u.getUser(), p,  fullObject);
				count = count + readStdErrToWebSock(requestId, u.getUser(), p,  fullObject);
			}
		}
		catch (Exception e) {
			count = count + readExceptionToWebSock(requestId, u.getUser(), e, fullObject, count);
		} finally {
			final ScriptExecutedRecord rec = new ScriptExecutedRecord(-1, -1, requestId, scriptName, "this-machine", fullObject.toStringPretty(), interpreterType.getInterpreterId(), groupId, Constants.repoShort, u.getId(), fullCommand, "", "", "", timeStamp, -1, "Y");
			pWrap.saveExecution(rec, fullObject, "Y");
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, u.getUser(), u.getUser(), WebsocketMessageType.footerScript, new ScriptFooterOutput(count), ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			defaultRuntimeVarAdhoc(appConstants.getTempPath());
			System.out.println(scriptName + " collected :" + count + " items");

		}
	
	}


	/**
	 * It runs the script locally, collecting full log and returns it in its entirety to be sent back to user. It does save the script
	 */
	public static ScriptingReturnObject
	runLocalAdhocWithCollect(final String scriptName,
							 final InterpreterType interpreterType,
							 final String requestId,
							 final User u,
							 final String content,
							 final AppConstants appConstants,
							 final long timeStamp,
							 final long groupId,
							 PersistenceWrap pWrap)  {
		int count = 0;

		String executionFolder =   ScriptingHelper.getScriptExecutionFolder(scriptName, appConstants);
		String fullScriptName =   ScriptingHelper.getScriptFullName(scriptName, timeStamp, interpreterType);
		ScriptingReturnObject fullObject = new ScriptingReturnObject(requestId);
		String fullCommand = "";
		try {
			FileUtilWrapper.overwriteFile (executionFolder,	fullScriptName, content);

			if(interpreterType.getInterpreterName().toLowerCase().compareTo("WINDOWS BATCH") == 0) {
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", "cd \"C:\" && dir");
				fullCommand = builder.toString();
				printCommand(builder);
				builder.redirectErrorStream(true);
				Process p = builder.start();
				count = count + readStdin(p, fullObject);
			} else {
				fullCommand = interpreterType.getCommand() + " " + executionFolder + "//" + fullScriptName;
				System.out.println(fullCommand);
				Process p = Runtime.getRuntime().exec(fullCommand);
				count = count + readStdin(p, fullObject);
				count = count + readStdErr(p, fullObject);
			}
		}
		catch (Exception e) {
			count = count + readException(e, fullObject);
		} finally {
			fullObject.setIsCompleted("Y");
			System.out.println(scriptName + " collected :" + count + " items");
			final ScriptExecutedRecord rec = new ScriptExecutedRecord(-1, -1, requestId, fullScriptName, "this-machine", content, interpreterType.getInterpreterId(), groupId, Constants.repoShort, u.getId(), fullCommand, "", "", "no comment", timeStamp, -1, "Y");
			pWrap.saveExecution(rec, fullObject, "Y");
			defaultRuntimeVarAdhoc(appConstants.getTempPath() + Constants.scriptFolder);
		}
		return fullObject;
	}

	public static Map<String, String> stringToScriptParameterMap(final String str)	{
		if(str == null || str.isBlank() || str.isEmpty() ) return new HashMap<String, String>();
		Map<String, String> ret = new HashMap<String, String>();
		String[] pairs = str.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            ret.put(keyValue[0], keyValue[1]);
        }
		return ret;
	}
	
	
	
	public static int 
	getScriptNextVersion(	final long userCreatorId,
							final String scriptName,
							final String scriptFolder) throws Exception {
		String separator = FileSystems.getDefault().getSeparator();
		int nextVersion = 0;
		String pathFolderScript = scriptFolder + separator + scriptName;
		File pathFolder = new File(pathFolderScript);
		try {
			if(pathFolder.exists()) {
				// Just add a new version
				Set<String> subFolders = FileUtilWrapper.getFolders(pathFolder.getAbsolutePath());
                subFolders.removeIf(element -> !StringUtils.isNumeric(element));
				Set<Integer> setOfInteger = subFolders.stream().map(Integer::valueOf).collect(Collectors.toSet());
				nextVersion = setOfInteger.stream().mapToInt(v -> v).max().orElse(0) + 1;
			}
			else {
				Files.createDirectories(Paths.get(pathFolderScript));
				nextVersion =  1;
			}
		}
		catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		return nextVersion;
	}
	
	
	public static void addOrUpdateScript(	final String scriptFolder,
											final long userId,
											final ScriptAttachmentList scriptAttachmentList
											) throws Exception {
		ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
		InterpreterType interpreterType = new InterpreterType();
		try {
			interpreterType = scriptingInternalDb.interpreterById( scriptAttachmentList.getInterpreterId() );
			FileUtilWrapper.overwriteFile (scriptFolder,	scriptAttachmentList.getMainScriptName() + "." + interpreterType.getFileExtensions(), scriptAttachmentList.getScriptContent());
			
			for(ScriptAttachment scriptAttachment: scriptAttachmentList.getScriptAttachmentLst()) {
				interpreterType = scriptingInternalDb.interpreterById( scriptAttachment.getInterpreterId() );
				FileUtilWrapper.overwriteFile (scriptFolder,	scriptAttachment.getScriptName() + "." + interpreterType.getFileExtensions(), scriptAttachment.getScriptContent());
			}
			
			 
			scriptingInternalDb.scriptUpdate(	userId, // creatorUserId
												scriptAttachmentList.getMainScriptName(), // scriptName
												scriptAttachmentList.getMainScriptName() + "." + interpreterType.getFileExtensions(), // mainFile
												scriptAttachmentList.getParamString(), // paramString
												scriptAttachmentList.getPredictFile(), // predictFile
												scriptAttachmentList.getPredictFunc(), // predictFunc
												scriptAttachmentList.getInterpreterId(),  // interpreterId
												0 // scriptVersion
											 );
						
						
			
			
		} catch(Exception ex) {
			FileUtilWrapper.deleteDirectoryWithAllContent(scriptFolder) ;
			//replaceRuntimeVar(tmpFolder,  "");
			// We also have to restore old one
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	
	
	public static String createFolder(	final String userName,
										final String scriptName,
										final String scriptFolder) throws Exception {
		
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
		String pathFolderScriptVersion = scriptFolder + separator + userName + separator + scriptName ;
		Files.createDirectories(Paths.get(pathFolderScriptVersion));
		return pathFolderScriptVersion;
	}
	
	
	public static String copyAttachmentFileToTempFolder(final String scriptTempPath, 
														final InputStream input,
														final String fileName) throws IOException {
		File zip = File.createTempFile(fileName, ".zip", new File(scriptTempPath) );
	    FileOutputStream o = new FileOutputStream(zip);
	    IOUtils.copy(input, o);
		o.close();
		return zip.getCanonicalPath();
	}
	
	public static String copyAttachmentFileToTempFolder(final ScriptAttachmentList scriptAttachmentList,
														final String sessionId,
														final String tempFolder,
														final String scriptName) throws Exception {
		
		String tmpFolder = tempFolder + "/" + sessionId + "/" + scriptName;
		Files.createDirectories(Paths.get(tmpFolder));
		for(ScriptAttachment scriptAttachment : scriptAttachmentList.getScriptAttachmentLst()) {
			String folder = tmpFolder + "/" + scriptAttachment.getScriptRelativePath();
			String name = scriptAttachment.getScriptName();
			String content = scriptAttachment.getScriptContent();
			if(!FileUtilWrapper.isFolder(folder)) {
				Files.createDirectories(Paths.get(folder));
			}
			FileUtilWrapper.createFile(name);
			FileUtilWrapper.writeToFile(name, content);
		}
		
		return tmpFolder;
	}
	
	public static boolean copyAttachmentContent(final String scriptStoragePath,
												final String scriptTempPath,
												final String scriptName,
												final String mainFile,
												final int version,
												String zipCanonicalPath) throws Exception {
		boolean isMainFile = false;
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
		String newPathFile = scriptStoragePath	+ separator	+ scriptName + separator + version;
		ZipDirectory.unzip(zipCanonicalPath, newPathFile);
		FileUtilWrapper.deleteFile(zipCanonicalPath);
		deleteOldTempFolder(scriptTempPath);
		return isMainFile;
	}

	public static long updateDatabase(	final long userCreatorId,
										final String scriptName,
										final String mainFile,
										final String paramString,
										final String predictFile,
										final String predictFunc, 
										final int interpreterId,
										final int scriptVersion	) throws Exception {
		ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
		List<ScriptDetail> scriptInfo;
		try {
			scriptInfo = scriptingInternalDb.getScriptByNameAndVersion(scriptName, scriptVersion);
			if(scriptInfo.size() == 1) {
				scriptingInternalDb.scriptUpdate(userCreatorId, scriptName, mainFile, paramString, predictFile, predictFunc, interpreterId, scriptVersion);
				return scriptInfo.get(0).getScriptId();
			} else if(scriptInfo.size() > 1) {
				return scriptingInternalDb.scriptAdd(userCreatorId, scriptName, mainFile, paramString, predictFile, predictFunc, interpreterId, scriptVersion).getScriptId();
			} else {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sync Error: more than one record with same version")) ;
			}
		} catch (Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static boolean removeScriptVersionFromFS(final String scriptStoragePath,
													final String scriptName,
													final long scriptVersion) throws Exception {
		boolean isScriptVersionHomeFolderDeleted;
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
		String scriptHomeFolder = scriptStoragePath	+ separator	+ scriptName;
		File pathFolder = new File(scriptHomeFolder);
		if( pathFolder.exists() ) {
			String scriptVersionHomeFolder = scriptStoragePath 
											+ separator 
											+ scriptName 
											+ separator 
											+ scriptVersion ;
			
			File scriptVersionHome = new File(scriptVersionHomeFolder);
			if(scriptVersionHome.exists()) {
				isScriptVersionHomeFolderDeleted = FileUtilWrapper.deleteDirectoryWithAllContent(scriptVersionHomeFolder);
			}
			else {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Script version does not exist")) ;
			}
		} else {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Script/Script folder  does not exist")) ;
		}
		
		return isScriptVersionHomeFolderDeleted;
	}
	
	
	public static boolean removeScriptFromFS(	final String scriptStoragePath,
												final long userId,
												final String scriptName	) {
		boolean isScriptVersionHomeFolderDeleted;
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
		String scriptHomeFolder = scriptStoragePath	+ separator + userId + separator + scriptName;
		File pathFolder = new File(scriptHomeFolder);
		if( pathFolder.exists() ) {
			isScriptVersionHomeFolderDeleted = FileUtilWrapper.deleteDirectoryWithAllContent(scriptHomeFolder);
		} else {
			isScriptVersionHomeFolderDeleted = true;
		}
		return isScriptVersionHomeFolderDeleted;
	}
	
	
	public static void deleteOldTempFolder(final String scriptTempPath) {
		try {
			FileCharacteristic rootCharacteristics = FileUtilWrapper.getFilePermissions(scriptTempPath);
			List<FileCharacteristic> ret = FileUtilWrapper.getFolderContentRecursivelyWithAttr(scriptTempPath);
			for(FileCharacteristic fileCharacteristic : ret) {
				long diff = DateTimeUtils.millisecondsSinceEpoch() -  fileCharacteristic.getCreationTime();
				if( diff > 60 * 1000) {
					FileUtilWrapper.deleteFile(fileCharacteristic.getCanonicalPath());
				}
			}
			ret = FileUtilWrapper.getFolderContentRecursivelyWithAttrForFolders(scriptTempPath);
			for(FileCharacteristic fileCharacteristic : ret) {
				if(rootCharacteristics.getCanonicalPath().endsWith(fileCharacteristic.getCanonicalPath())) continue;
				long diff = DateTimeUtils.millisecondsSinceEpoch() -  fileCharacteristic.getCreationTime();
				if( diff > 60 * 1000) {
					FileUtilWrapper.deleteDirectoryWithAllContent(fileCharacteristic.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}


	public static String getInterpreterCommand(String interpreter) {
        return switch (interpreter.toUpperCase()) {
            case "PYTHON3" -> "python";
            case "PYTHON2" -> "py";
            case "GROOVY" -> "groovy -e";
            case "PERL" -> "perl -c";
            case "R" -> "r";
            case "RUBY" -> "ruby";
            case "BATCH" -> "batch";
            case "BASH" -> "sh";
            case "POWERSHELL" -> "powershell";
            case "JULIA" -> "julia";
            default -> "";
        };
	}

	/**
	 * The following runtime vars must be replaced at runtime:
		 api_url_stem = "@api_url_stem@"
		 authorization = "@authorization@"
		 internalUserName = "@internalUserName@"
		 internalUserPassword = "@internalUserPassword@"
		 internalAdmin = "@internalAdmin@"
		 internalAdminPassword = "@internalAdminPassword@"
		 user = "@user@"
		 session = "@session@"
		 requestId = "@requestId@"

	 */
	public static String replaceLibraryRuntimeVars(final String user,
												   final String session,
												   String scriptContent,
												   String apiUrlStem)  {

		scriptContent = scriptContent.replace("@api_url_stem@", apiUrlStem);
		scriptContent = scriptContent.replace("@user@", user);
		scriptContent = scriptContent.replace("@session@", session);
		scriptContent = scriptContent.replace("@internalUserName@", SpringSecurityWrapper.username);
		scriptContent = scriptContent.replace("@internalUserPassword@", SpringSecurityWrapper.userPassword);
		scriptContent = scriptContent.replace("@internalAdmin@", SpringSecurityWrapper.adminName);
		scriptContent = scriptContent.replace("@internalAdminPassword@", SpringSecurityWrapper.adminPassword);
		scriptContent = scriptContent.replace("@authorization@", "Basic " + SpringSecurityWrapper.encodeDefaultUserAuthorization());
		return scriptContent;
	}


	public static String replaceScriptRuntimeVars(String scriptContent) {
		String retScriptContent = "from sqlThunderRestClient import * " + "\n";
		retScriptContent += "\n";
		retScriptContent += scriptContent;
		return retScriptContent;
	}



	/*STDIN, STDOUT, STDERR reading functions*/

	private static int readStdin(final Process p, ScriptingReturnObject fullObject) throws IOException {
		String line;
		int count = 0;
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = stdInput.readLine()) != null) {
			count++;
			fullObject.addLogDetail(new LogDetail(LogDetailTypes.stdin, line));
		}
		return count;
	}

	private static int readStdinToWebSock(final String requestId,
										  final String user,
										  final Process p,
										  ScriptingReturnObject fullObject) throws IOException {
		String line;
		int count = 0;
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = stdInput.readLine()) != null) {
			LogDetail l = new LogDetail(LogDetailTypes.stdin, line);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl));
			fullObject.addLogDetail(l);
			count++;
		}
		return count;
	}

	private static int readStdinGate(final String requestId,
									  final String user,
									  final String session,
									  final String baseUrl,
									  final Process p,
									  ScriptingReturnObject fullObject) throws Exception {
		String line;
		int count = 0;
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = stdInput.readLine()) != null) {
			count++;
			LogDetail l = new LogDetail(LogDetailTypes.stdin, line);
			fullObject.addLogDetail(l);
			RestApiScriptingClient.updateGateSink(user, session, requestId, baseUrl,  new ScriptingReturnObject(requestId, l));
		}
		return count;
	}

	private static void finalizeToGate(final String requestId,
									   final String user,
									   final String session,
									   final String baseUrl,
									   final int count,
									   final String isError) throws Exception {
		LogDetail l = new LogDetail(LogDetailTypes.end, String.valueOf(count) );
		RestApiScriptingClient.updateGateSink(user,
												session,
												requestId,
												baseUrl,
												new ScriptingReturnObject(requestId, "N", "Y",isError,  l));
	}


	private static int readStdErr(final Process p, ScriptingReturnObject fullObject) throws IOException {
		String line;
		int count = 0;
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = stdError.readLine()) != null) {
			count++;
			fullObject.addLogDetail(new LogDetail(LogDetailTypes.stderr, line));
		}
		return count;
	}

	private static int readStdErrToWebSock(final String requestId, final String user, final Process p, ScriptingReturnObject fullObject) throws IOException {
		String line;
		int count = 0;
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = stdError.readLine()) != null) {
			LogDetail l = new LogDetail(LogDetailTypes.stdin, line);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl));
			fullObject.addLogDetail(l);
			count++;
		}
		return count;
	}

	private static int readStdErrGate(final String requestId,
									  final String user,
									  final String session,
									  final String baseUrl,
									  final Process p,
									  ScriptingReturnObject fullObject) throws Exception {
		String line;
		int count = 0;
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = stdError.readLine()) != null) {
			count++;
			LogDetail l = new LogDetail(LogDetailTypes.stderr, line);
			fullObject.addLogDetail(l);
			RestApiScriptingClient.updateGateSink(user, session, requestId, baseUrl,  new ScriptingReturnObject(requestId, l));
		}
		return count;
	}


	private static int readException(final Exception e, ScriptingReturnObject fullObject) {
		LogDetail l =new LogDetail(LogDetailTypes.exception, AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		fullObject.addLogDetail(l); fullObject.setIsError("Y");
		return 1;
	}

	private static int readExceptionToWebSock(final String requestId, final String user, final Exception e, ScriptingReturnObject fullObject, int count) {
		LogDetail l =new LogDetail(LogDetailTypes.exception, AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		fullObject.addLogDetail(l); fullObject.setIsError("Y");
		WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
		WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
		return 1;
	}

	private static int readExceptionGate(final String requestId,
										  final String user,
										  final String session,
										  final String baseUrl,
										  ScriptingReturnObject fullObject,
										  final Exception e) throws Exception {
		LogDetail l =new LogDetail(LogDetailTypes.exception, AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		fullObject.addLogDetail(l); fullObject.setIsError("Y");
		RestApiScriptingClient.updateGateSink(user, session, requestId, baseUrl, new ScriptingReturnObject(requestId, "N", "Y", "Y", l));
		return 1;
	}

	private static void finalizeToWebSock(final String requestId,
										  final String user,
										  int count) {
		ScriptFooterOutput scriptFooterOutput = new ScriptFooterOutput(count);
		WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.footerScript, scriptFooterOutput, ClusterDb.ownBaseUrl);
		WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
	}

	private static void printCommand(final ProcessBuilder builder ) {
		System.out.println(builder.command());
	}


	public static String getScriptFullName(final String scriptName, final long timeStamp, InterpreterType interpreterType ) {
		return scriptName + "_" + timeStamp + "." + interpreterType.getFileExtensions();
	}

	public static String getScriptLogName(final String scriptName, final long timeStamp) {
		return scriptName + "_" + timeStamp + ".log" ;
	}

	public static String getScriptOutputName(final String scriptName, final long timeStamp) {
		return scriptName + "_" + timeStamp + ".json" ;
	}

	public static String getScriptExecutionFolder(final String scriptName, final AppConstants appConstants) {
		String separator = FileSystems.getDefault().getSeparator();
		return appConstants.getTempPath() + Constants.scriptFolder + separator + scriptName;
	}


}
