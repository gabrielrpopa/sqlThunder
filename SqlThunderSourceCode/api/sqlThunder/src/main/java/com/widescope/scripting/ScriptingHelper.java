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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
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

public class ScriptingHelper {

	/**
	 * Description: Runs script or command and returns log to calling function
	 * @param command for the shell
	 * @return List<LogDetail>
	 * Used by runClientScript1, runClientScript2
	 */
	public static List<LogDetail>
	runAdhocScriptAndReturnLog(final String command)  {
		List<LogDetail> ret = new ArrayList<>();
		String s;

        try {
        	System.out.println(command);
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            System.out.println("Standard Input:\n");
            while ((s = stdInput.readLine()) != null) {
            	ret.add( new LogDetail("stdin", s) );
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Standard error:\n");
            while ((s = stdError.readLine()) != null) {
            	ret.add( new LogDetail("stderr", s) );
                System.out.println(s);
            }
            return ret;
        }
        catch (Exception e) {
	       	ret.add( new LogDetail("Exception", AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) );
        }
		return ret;
	}


	/**
	 * Description: Runs Repo script and push log to calling user via websockets, assuming user is connected via websockets at all times
	 * @param user
	 * @param command
	 * @param requestId
	 * Used by runClientScript1, runClientScript2
	 */
	public static void
	runRepoScriptAndPushLogWithNotificationToUser(final String user,
												  final String command,
												  final String requestId) {
		String s;

		int count = 0;
		try {
			Process p = Runtime.getRuntime().exec(command);
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			// read the output from the command
			System.out.println("Standard Input:\n");
			ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("REPO");
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.headerScript, scriptHeaderOutput, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
			while ((s = stdInput.readLine()) != null) {
				LogDetail l =new LogDetail("stdin", s);
				wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
				System.out.println(s);
				count++;
			}
			
			// read any errors from the attempted command
			System.out.println("Standard error:\n");
			while ((s = stdError.readLine()) != null) {
				LogDetail l =new LogDetail("stdin", s);
				wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
				System.out.println(s);
				count++;
			}
		}
		catch (Exception e) {
			LogDetail l =new LogDetail("Exception", AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			count++;
		} finally {
			//ScriptingHelper.writeLogScriptExec(scriptVersionLogPath, user);
			ScriptFooterOutput scriptFooterOutput = new ScriptFooterOutput(count);
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.footerScript, scriptFooterOutput, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
		}
	}


	/**
	 *
	 * @param tmpFolder
	 * @param scriptName
	 * @param interpreterId
	 * @param requestId
	 * @param user
	 * @param session
	 * @param internalUser
	 * @param internalPassword
	 * @param content
	 * @param baseUrl
	 * @throws IOException
	 */
	public static void
	runAdhocWithNotificationsToGate(final String tmpFolder,
									final String scriptName,
									final int interpreterId,
									final String requestId,
									final String user,
									final String session,
									final String internalUser,
									final String internalPassword,
									final String content,
									final String baseUrl) throws IOException  {
		String s = null;
		int count = 0;
		InterpreterType interpreterType = new InterpreterType();
		String savedScriptName = scriptName + "_" + session;
		try {
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			interpreterType = scriptingInternalDb.interpreterByIdGet(interpreterId);
			FileUtilWrapper.overwriteFile (tmpFolder,	savedScriptName + "." + interpreterType.getFileExtensions(), content);
			if(interpreterType.getInterpreterName().toUpperCase().compareTo("WINDOWS BATCH") == 0 ||
					interpreterType.getInterpreterName().toUpperCase().compareTo("BASH") == 0 ) {
				
				String fullCommand = tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions();
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", fullCommand);
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        
		        System.out.println("Standard Input:\n");
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");


				RestApiScriptingClient.loopbackScriptStdin(	baseUrl,
															user, 
															session,
															internalUser,
															internalPassword,
															requestId,
															WebsocketMessageType.headerScript, 
															scriptHeaderOutput.toString());
				
				

								
		        String line;
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            LogDetail l = new LogDetail("stdin", s);
		            RestApiScriptingClient.
					loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
					System.out.println(s);
					count++;
		        }
			} else {
				String fullCommand = interpreterType.getCommand() + " " + tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions();
				System.out.println(fullCommand);
				
				Process p = Runtime.getRuntime().exec(fullCommand);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				System.out.println("Standard Input:\n");
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");
				RestApiScriptingClient.
				loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.headerScript, scriptHeaderOutput.toString()	);
				
				while ((s = stdInput.readLine()) != null) {
					LogDetail l =new LogDetail("stdin", s);
					RestApiScriptingClient.
					loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
					System.out.println(s);
					count++;

				}
			
				// read any errors from the attempted command
				System.out.println("Standard error:\n");
				while ((s = stdError.readLine()) != null) {
					LogDetail l =new LogDetail("stdin", s);
					RestApiScriptingClient.
					loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
					System.out.println(s);
				}
				
			}
		} catch (Exception e) {
			LogDetail l =new LogDetail("stdin", AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
			RestApiScriptingClient.
			loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
			count++;
		} finally {
			ScriptFooterOutput scriptfooterOutput = new ScriptFooterOutput(count);
			RestApiScriptingClient.
			loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.footerScript, scriptfooterOutput.toString()	);
			FileUtilWrapper.deleteFile(tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions()) ;
		}
	
	}
	

	public static void
	runAdhocWithNotificationsToGate(final String folder,
									final String mainFileName,
									final int interpreterId,
									final String requestId,
									final String user,
									final String session,
									final String internalUser,
									final String internalPassword,
									final String baseUrl) throws IOException  {
		String s = null;
		int count = 0;
		InterpreterType interpreterType;
		try {
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			interpreterType = scriptingInternalDb.interpreterByIdGet(interpreterId);
			if(interpreterType.getInterpreterName().toUpperCase().compareTo("WINDOWS BATCH") == 0 ||
					interpreterType.getInterpreterName().toUpperCase().compareTo("BASH") == 0 ) {
				
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", folder + "\\" + mainFileName);
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Repo");
				RestApiScriptingClient.loopbackScriptStdin(	baseUrl, 
															user, 
															session, 
															internalUser,
															internalPassword,
															requestId, 
															WebsocketMessageType.headerScript, 
															scriptHeaderOutput.toString());
				
				

								
		        String line;
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            LogDetail l = new LogDetail("stdin", s);
		            RestApiScriptingClient.
					loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
					System.out.println(s);
					count++;
		        }
			} else {
				String fullCommand = interpreterType.getCommand() + " " + folder + "\\" + mainFileName ;
				System.out.println(fullCommand);
				
				Process p = Runtime.getRuntime().exec(fullCommand);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				System.out.println("Standard Input:\n");
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");
				RestApiScriptingClient.
				loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.headerScript, scriptHeaderOutput.toString()	);
				
				while ((s = stdInput.readLine()) != null) {
					LogDetail l =new LogDetail("stdin", s);
					RestApiScriptingClient.
					loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
					System.out.println(s);
					count++;
				}
			
				// read any errors from the attempted command
				System.out.println("Standard error:\n");
				while ((s = stdError.readLine()) != null) {
					LogDetail l =new LogDetail("stdin", s);
					RestApiScriptingClient.
					loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
					System.out.println(s);
				}
				
			}
		} catch (Exception e) {
			LogDetail l =new LogDetail("stdin", AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
			RestApiScriptingClient.
			loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, l.toString()	);
			System.out.println("Error:  " + e.getMessage());
			count++;
		} finally {
			ScriptFooterOutput scriptfooterOutput = new ScriptFooterOutput(count);
			RestApiScriptingClient.
			loopbackScriptStdin(baseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.footerScript, scriptfooterOutput.toString()	);
			FileUtilWrapper.deleteDirectoryWithAllContent(folder) ;
		}
	
	}
	

	public static void 
	runAdhocWithNotificationsToClient(	final String tmpFolder,
										final String scriptName,
										final int interpreterId,
										final String requestId,
										final String user,
										final String session,
										final String content) throws IOException  {
		String s = null;
		int count = 0;
		InterpreterType interpreterType = new InterpreterType();
		String savedScriptName = scriptName + "_" + session;
		try {
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			interpreterType = scriptingInternalDb.interpreterByIdGet(interpreterId);
			FileUtilWrapper.overwriteFile (tmpFolder,	savedScriptName + "." + interpreterType.getFileExtensions(), content);
			if(interpreterType.getInterpreterName().toUpperCase().compareTo("WINDOWS BATCH") == 0 ||
					interpreterType.getInterpreterName().toUpperCase().compareTo("BASH") == 0 ) {
				
				String fullCommand = tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions();
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", fullCommand);
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        
		        System.out.println("Standard Input:\n");
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.headerScript, scriptHeaderOutput, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
		        String line;
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            LogDetail l = new LogDetail("stdin", s);
					wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
					WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
		            System.out.println(s);
					count++;
		        }
			} else {
				String fullCommand = interpreterType.getCommand() + " " + tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions();
				System.out.println(fullCommand);
				
				Process p = Runtime.getRuntime().exec(fullCommand);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				System.out.println("Standard Input:\n");
				ScriptHeaderOutput scriptHeaderOutput = new ScriptHeaderOutput("Ad-Hoc");
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.headerScript, scriptHeaderOutput, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);


				while ((s = stdInput.readLine()) != null) {
					LogDetail l =new LogDetail("stdin", s);
					wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
					WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
					System.out.println(s);
					count++;
				}
			
				// read any errors from the attempted command
				System.out.println("Standard error:\n");
				while ((s = stdError.readLine()) != null) {
					LogDetail l =new LogDetail("stdin", s);
					wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
					WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
					System.out.println(s);
				}
				
			}
		}
		catch (Exception e) {
			LogDetail l =new LogDetail("stdin", e.getMessage());
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, l, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			count++;
		} finally {
			ScriptFooterOutput scriptfooterOutput = new ScriptFooterOutput(count);
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.footerScript, scriptfooterOutput, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			FileUtilWrapper.deleteFile(tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions()) ;
		}
	
	}
	
	
	public static ScriptingReturnObject 
	runAdhocWithRet(final String tmpFolder,
					final String scriptName,
					final int interpreterId,
					final String requestId,
					final String sessionId,
					final String content) throws IOException  {
		String s = null;
		ScriptingReturnObject scriptRet;
		List<LogDetail> logDetailList = new ArrayList<LogDetail>();
		
		try {
			
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			InterpreterType interpreterType = scriptingInternalDb.interpreterByIdGet(interpreterId);
			
			if(interpreterType.getInterpreterName().toLowerCase().compareTo("WINDOWS BATCH") == 0) {
				ProcessBuilder builder = new ProcessBuilder(interpreterType.getCommand(), "/c", "cd \"C:\" && dir");
			        builder.redirectErrorStream(true);
			        Process p = builder.start();
			        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			        String line;
			        while (true) {
			            line = r.readLine();
			            System.out.println(line);
			            if (line == null) { break; }
			            logDetailList.add(new LogDetail("stdin", line));
			        }
			        
			        
			} else {
				String savedScriptName = scriptName + "_" + sessionId;
				FileUtilWrapper.overwriteFile (tmpFolder,	savedScriptName + "." + interpreterType.getFileExtensions(), content);
				String fullCommand = interpreterType.getCommand() + " " + tmpFolder + "\\" + savedScriptName + "." + interpreterType.getFileExtensions();
				System.out.println(fullCommand);

				Process p = Runtime.getRuntime().exec(fullCommand);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				System.out.println("Standard Input:\n");
	            while ((s = stdInput.readLine()) != null) {
		            logDetailList.add(new LogDetail("stdin", s));
	                System.out.println(s);
	                
	            }

	            // read any errors from the attempted command
	            System.out.println("Standard error:\n");
	            while ((s = stdError.readLine()) != null) {
		            logDetailList.add(new LogDetail("stderr", s));
	                System.out.println(s);
	            }
			}
		}
		catch (Exception e) {
            logDetailList.add(new LogDetail("exception", s));
			System.out.println(s);
		} finally {
			scriptRet = ScriptingSharedData.getCollectedDataToUser(sessionId,requestId);
			scriptRet.setLogDetailList(logDetailList);
		}
		return scriptRet;
	
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
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
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
			interpreterType = scriptingInternalDb.interpreterByIdGet( scriptAttachmentList.getInterpreterId() );
			FileUtilWrapper.overwriteFile (scriptFolder,	scriptAttachmentList.getMainScriptName() + "." + interpreterType.getFileExtensions(), scriptAttachmentList.getScriptContent());
			
			for(ScriptAttachment scriptAttachment: scriptAttachmentList.getScriptAttachmentLst()) {
				interpreterType = scriptingInternalDb.interpreterByIdGet( scriptAttachment.getInterpreterId() );
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

	public static int updateDatabase(	final long userCreatorId,
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
													final int scriptVersion) throws Exception {
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
	
	public static String getInterpreterCommand(int interpreterId) {
		return "";
	}
	
	public static String replaceScriptUserSession(final String user,
												  final String session,
												  final String basicAuth,
												  final String http,
												  final String port,
												  String request,
												  String scriptContent) {

		scriptContent = scriptContent.replace("@user@", user);
		scriptContent = scriptContent.replace("@session@", session);
		scriptContent = scriptContent.replace("@basicAuth@", basicAuth);
		scriptContent = scriptContent.replace("@host@", http + "://localhost:" + port);
		scriptContent = scriptContent.replace("@request@", request);
		return scriptContent;
	}

	public static String addLibraryGlobalParam(	String script,
												String user,
												String session,
												String internalAdmin,
												String internalAdminPasscode,
												String http,
												String port  ) {
		String hostVar = http + "://localhost:" + port;
		String auth =  SpringSecurityWrapper.getUserAuthorization();
		String header = "updateGlobalVars('" + auth + "', '" + user + "', '" + session + "', '" + internalAdmin + "', '" + internalAdminPasscode + "', '" + hostVar + "')";
		return header + "\n" + script;

	}
	
}
