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


package com.widescope.sqlThunder.controller.v2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.widescope.logging.AppLogger;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.scripting.*;
import com.widescope.scripting.db.*;
import com.widescope.scripting.execution.*;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.*;
import com.widescope.sqlThunder.utils.user.*;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.rest.RestObjectShort;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.compression.ZipDirectory;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;

/*
*
* in mem scripting logs:
* com.widescope.scripting.ScriptingSharedLogs
* com.widescope.scripting.executionInMemScriptLog
*
* */

@CrossOrigin
@RestController
@Schema(title = "Scripting Control and Execution")
public class ScriptingController {
	
	@Autowired
	private AppConstants appConstants;
	
	@Autowired
	private ScriptingInternalDb scriptingInternalDb;

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private PersistenceWrap pWrap;

	@PostConstruct
	public void initialize() {

	}




		
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/interpreter:add", method = RequestMethod.PUT)
	@Operation(	summary = "Add interpreter with associated information",
				description= "Add interpreter to the repository with associated information such as path, version, command line, recognized file extension ")
	public ResponseEntity<RestObject> 
	interpreterAdd(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="interpreterName") final String interpreterName,
					@RequestHeader(value="interpreterVersion") final String interpreterVersion,
					@RequestHeader(value="interpreterPath") final String interpreterPath,
					@RequestHeader(value="command") final String command,
					@RequestHeader(value="fileExtensions") final String fileExtensions) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.interpreterAdd(interpreterName, interpreterVersion, interpreterPath, command, fileExtensions);
			List<InterpreterType> lstInterpreter = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
			InterpreterList lst = new InterpreterList(lstInterpreter);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/interpreter:update", method = RequestMethod.POST)
	@Operation(	summary = "Update Interpreter",
				description= "Update interpreter information in the repository such as path, version, command line, recognized file extension ")
	public ResponseEntity<RestObject> 
	interpreterUpdate(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterId") final int interpreterId,
						@RequestHeader(value="interpreterName") final String interpreterName,
						@RequestHeader(value="interpreterVersion") final String interpreterVersion,
						@RequestHeader(value="interpreterPath") final String interpreterPath,
						@RequestHeader(value="command") final String command,
						@RequestHeader(value="fileExtensions") final String fileExtensions) {

		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.interpreterUpdate(interpreterId,  interpreterName, interpreterVersion, interpreterPath, command, fileExtensions);
			InterpreterType lstInterpreter = scriptingInternalDb.interpreterById(interpreterId);
			InterpreterList lst = new InterpreterList(lstInterpreter);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/interpreter:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete interpreter",
				description= "Delete interpreter reference from the repository with all associated information")
	public ResponseEntity<RestObject> 
	interpreterDelete(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterId") final int interpreterId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.interpreterDelete( interpreterId );
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/interpreter:search",
					method = RequestMethod.GET)
	@Operation(	summary = "Get interpreter versions filtered by name",
				description= "Get a list of interpreter versions providing a filter for the name from the repository")
	public ResponseEntity<RestObject> 
	searchInterpreter(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterName") final String interpreterName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> InterpreterList = scriptingInternalDb.interpreterByName(interpreterName);
			InterpreterList interpreterDetailList = new InterpreterList(InterpreterList);
			return RestObject.retOKWithPayload(interpreterDetailList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/interpreter:list", method = RequestMethod.GET)
	@Operation(	summary = "Get a list of interpreters",
				description= "List all registered interpreters")
	public ResponseEntity<RestObject> 
	listAllInterpreters(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> InterpreterList = scriptingInternalDb.allInterpreters();
			InterpreterList interpreterDetailList = new InterpreterList(InterpreterList);
			return RestObject.retOKWithPayload(interpreterDetailList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:add", method = RequestMethod.PUT)
	@Operation( summary = "Add script project/folder to the repository",
				description= "Add script folder in zip format to the repository with associated information such as potential parameters, interpreter, main file, file type and extension and name")
	public ResponseEntity<RestObject> 
	addRepoScript(	@RequestHeader(value="user") final String user,
					@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="scriptName") final String scriptName,
					@RequestHeader(value="paramString", required = false, defaultValue = "") final String paramString,
					@RequestHeader(value="mainFile") final String mainFile,
					@RequestHeader(value="interpreterId") int interpreterId,
					@RequestHeader(value="fileType") final String fileType, /*HTTP mime type*/
					@RequestParam("attachment") final MultipartFile attachment) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<ScriptDetail> lstScripts = scriptingInternalDb.getScriptByName(scriptName);

			int currentVersion = 0;
			if(!lstScripts.isEmpty()) {
				currentVersion = lstScripts	.stream()
											.max(Comparator.comparing(ScriptDetail::getScriptVersion))
											.orElse(new ScriptDetail())
											.getScriptVersion() + 1;
			}
			
			User u = authUtil.getUser(user);
			long userId = u.getId();
			File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), ".temp");
			String absolutePathForTempFile = attachmentFile.getAbsolutePath();
			FileOutputStream o = new FileOutputStream(attachmentFile);
			IOUtils.copy(attachment.getInputStream(), o);
			o.close();
			
			String folderCreated = ScriptingHelper.createFolder(String.valueOf(userId), scriptName, appConstants.getTempPath() + Constants.scriptFolder);
			
			
			if(fileType.equals("application/zip") || fileType.equals("application/x-zip-compressed")) {
				String newZipFolder = UUID.randomUUID().toString();
				String newZipFolderAbsolutePath = "";
				try {
					newZipFolderAbsolutePath = Files.createTempDirectory(newZipFolder).toFile().getAbsolutePath();
					ZipDirectory.unzip(absolutePathForTempFile, newZipFolderAbsolutePath);
					List<FileCharacteristic> allFiles = FileUtilWrapper.getListOfFilesFromFolder(newZipFolderAbsolutePath);
					
					FileUtilWrapper.copyFolder(newZipFolderAbsolutePath, folderCreated);
					
				} catch(Exception ex) {
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				} finally {
					FileUtilWrapper.deleteFolder(newZipFolderAbsolutePath);
				}
				
			} else {
				String content = FileUtilWrapper.getFileContent(absolutePathForTempFile);
				FileUtilWrapper.overwriteFile (folderCreated, mainFile, content);
			}
			FileUtilWrapper.deleteFile(absolutePathForTempFile);

			ScriptDetail script = scriptingInternalDb.scriptAdd(	userId, 
															scriptName, 
															mainFile, 
															paramString, 
															"", //predictFile 
															"", // predictFunc 
															interpreterId,
															currentVersion);
									

			return RestObject.retOKWithPayload(script, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script/content:add", method = RequestMethod.PUT)
	@Operation(	summary = "Add script single file content to repository",
				description= "Add script single file content to the repository with associated information such as potential parameters, interpreter and name")
	public ResponseEntity<RestObject>
	addRepoScriptContent(	@RequestHeader(value="user") final String user,
							@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="scriptName") final String scriptName,
							@RequestHeader(value="comment") final String comment,
							@RequestHeader(value="paramString", required = false, defaultValue = "") final String paramString,
							@RequestHeader(value="interpreterId") final int interpreterId,
							@RequestBody final String scriptContent) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<ScriptDetail> lstScripts = scriptingInternalDb.getScriptByName(scriptName);
			
			int currentVersion = 0;
			if(!lstScripts.isEmpty()) {
				currentVersion = lstScripts	.stream()
											.max(Comparator.comparing(ScriptDetail::getScriptVersion))
											.orElse(new ScriptDetail())
											.getScriptVersion() + 1;
			}
			
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String commentFile = "c_" + scriptName;
			String folderCreated = ScriptingHelper.createFolder(String.valueOf(userId), scriptName, appConstants.getTempPath() + Constants.scriptFolder);
			FileUtilWrapper.overwriteFile (folderCreated, scriptName, scriptContent);
			FileUtilWrapper.overwriteFile (folderCreated, commentFile, comment);
			ScriptDetail script = scriptingInternalDb.scriptAdd(userId, 
																scriptName, 
																scriptName, 
																paramString, 
																"", //predictFile 
																"", // predictFunc 
																interpreterId,
																currentVersion);

			
			return RestObject.retOKWithPayload(script, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:content", method = RequestMethod.GET)
	@Operation(	summary = "Download script content",
				description= "Get script content by downloading it via browser")
	public ResponseEntity<Resource>
	getRepoScriptContent(	@RequestHeader(value="user") final String user,
							@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="scriptId", required = false) final long scriptId) {
		DownloadScriptStructureList res = new DownloadScriptStructureList();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptDetail scriptDetail = scriptingInternalDb.getScript(scriptId);
			String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String scriptVersionRelativePath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + scriptDetail.getScriptName();
			String scriptVersionMainFileRelativePath = scriptVersionRelativePath + separator + scriptDetail.getMainFile() ;
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Access-Control-Expose-Headers", "*");
			headers.add("fileName", scriptDetail.getScriptName());
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("RequestId", requestId);
			
			if(FileUtilWrapper.isFilePresent(scriptVersionMainFileRelativePath)) {
				String content = FileUtilWrapper.getFileContent(scriptVersionMainFileRelativePath);
				String fType = FileUtilWrapper.getFileType(scriptVersionMainFileRelativePath);
				DownloadScriptStructure mainFileStructure = new DownloadScriptStructure(UUID.randomUUID().toString(),
																						scriptDetail.getMainFile(),
																						content,
																					".",
																					"Y",
																						fType,
																						scriptDetail);
				res.addDownloadScriptStructure(mainFileStructure);
				List<FileCharacteristic> allFiles = FileUtilWrapper.getListOfFilesExcluding(scriptVersionRelativePath,
																							scriptVersionMainFileRelativePath);
				res.addBulkDownloadScriptStructure(allFiles);
				String tmpPath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + UUID.randomUUID().toString();
				FileUtilWrapper.overwriteFile(tmpPath, "attachment", res.toString());
				
				
				
				File file2Download = new File(tmpPath + "/attachment");
				InputStreamResource resource = new InputStreamResource(new FileInputStream(file2Download));
			
				ResponseEntity<Resource> ret = ResponseEntity.ok()
															.headers(headers)
															.contentLength(file2Download.length())
															.contentType(MediaType.parseMediaType("text/plain"))
															.body(resource);
				
				System.out.println(ret);
				return ret;
			} else {
				return new ResponseEntity<>(HttpStatusCode.valueOf(500));
			}
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		}
	}
	

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:search", method = RequestMethod.GET)
	@Operation(	summary = "Search for a script and associated versions",
				description= "Search for a script and associated versions, providing a filter for the search")
	public ResponseEntity<RestObject>
	searchRepoScript(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptName", required = false) final String scriptName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<ScriptDetail> lstScripts = scriptingInternalDb.getScriptByName(scriptName);		
			List<Long> userIds =  lstScripts.stream().map(ScriptDetail::getUserCreatorId).distinct().collect(Collectors.toList()); 
			List<User> lstUsers = authUtil.getUsers(userIds);
			for(ScriptDetail t: lstScripts) {
				List<ScriptParamDetail> params = scriptingInternalDb.getScriptParams(t.getScriptId());
				t.setScriptParamDetailList(params);
				List<MachineNodeToScriptBridge> bridges = scriptingInternalDb.machineNodesBridgeToScriptByScriptGet(t.getScriptId());
				t.setMachineNodeToScriptBridgeList(bridges);
				Optional<User> u = lstUsers.stream().filter(p -> p.getId() == t.getUserCreatorId()).findFirst();
				u.ifPresent(value -> t.setUserEmail(value.getEmail()));
			}
			ScriptDetailObject ret =  new ScriptDetailObject( lstScripts );
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:user", method = RequestMethod.GET)
	@Operation(summary = "Get user scripts",	description= "This is specific to the requesting user")
	public ResponseEntity<RestObject> 
	userRepoScripts(@RequestHeader(value="user") final String user,
					@RequestHeader(value="requestId", defaultValue = "") String requestId)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long userId = authUtil.getUser(user).getId();
			scriptingInternalDb.getUserAccessByUser(userId);
			ScriptDetailObject ret =  new ScriptDetailObject( scriptingInternalDb.getScriptByUser(userId) );
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:get", method = RequestMethod.GET)
	@Operation(summary = "Get script and associated information",	description= "Get script and associated information.")
	public ResponseEntity<RestObject> 
	getRepoScript(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="scriptId") final long scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			ScriptDetail script = scriptingInternalDb.getScript(scriptId);
			List<ScriptParamDetail> params = scriptingInternalDb.getScriptParams(scriptId);
			script.setScriptParamDetailList(params);
			List<MachineNodeToScriptBridge> bridges = scriptingInternalDb.machineNodesBridgeToScriptByScriptGet(scriptId);
			script.setMachineNodeToScriptBridgeList(bridges);
			return RestObject.retOKWithPayload(script, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:remove", method = RequestMethod.DELETE)
	@Operation(	summary = "Remove script",
				description= "Remove script and all its versions from repository. The script must be owned by user deleting it")
	public ResponseEntity<RestObject> 
	removeRepoScript(	@RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptId") final long scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			ScriptDetail scriptDetail = scriptingInternalDb.getScript(scriptId);
			boolean isScriptHomeFolderDeleted = ScriptingHelper.removeScriptFromFS(	appConstants.getTempPath() + Constants.scriptFolder,
																					userId,
																					scriptDetail.getScriptName());
			
			if(isScriptHomeFolderDeleted) {
				scriptingInternalDb.scriptDeleteAll(scriptId);
				return RestObject.retOKWithPayload(scriptDetail, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} 
			
			return RestObject.retException(scriptDetail, requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cannot delete script");

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script/version:remove", method = RequestMethod.DELETE)
	@Operation(	summary = "Remove script version from repository",
				description= "Remove script version from repository. The script must be owned by user deleting it")
	public ResponseEntity<RestObject>
	removeScriptVersion(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptName") final String scriptName,
						@RequestHeader(value="scriptVersion") final long scriptVersion) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean isScriptVersionHomeFolderDeleted ;
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.scriptVersionDelete(scriptName, scriptVersion);
			isScriptVersionHomeFolderDeleted = ScriptingHelper.removeScriptVersionFromFS(	appConstants.getTempPath() + Constants.scriptFolder,
																							scriptName,
																							scriptVersion);
			
			String message = "Script version deleted from database.";
			message = message + ( isScriptVersionHomeFolderDeleted ? " Version Home Folder deleted." 
																			: " Version Home Folder not deleted.");

			return RestObject.retOKWithPayload(new GenericResponse(message), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:copy", method = RequestMethod.POST)
	@Operation(	summary = "Copy Repo Scripts to another user",
			description= "Copy a script from your profile to another user's profile")
	public ResponseEntity<RestObject>
	copyRepoScriptHist( @RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="toUserId") final long toUserId,
						@RequestHeader(value="interpreterId") final int interpreterId,
						@RequestHeader(value="scriptId") final int scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long fromUserId = u.getId();
			String separator = FileSystems.getDefault().getSeparator();
			ScriptDetail si = scriptingInternalDb.getScript(scriptId);
			String fromScriptVersionPath = appConstants.getTempPath() + Constants.scriptFolder + separator + fromUserId + separator + si.getScriptName();
			File from = new File(fromScriptVersionPath);
			if(!from.exists()) {
				return RestObject.retOKWithPayload(new GenericResponse("The script Could not be found"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}

			String toScriptVersionPath = appConstants.getTempPath() + Constants.scriptFolder + separator + toUserId + separator + si.getScriptName();
			File to = new File(toScriptVersionPath);
			if(to.exists()) {
				return RestObject.retOKWithPayload(new GenericResponse("The script already exists to user profile"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				FileUtilWrapper.copyFolder(fromScriptVersionPath, toScriptVersionPath);
				if(!to.exists()) {
					return RestObject.retOKWithPayload(new GenericResponse("The script could not be added to user profile"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				} else {
					scriptingInternalDb.scriptAdd(	toUserId , si.getScriptName(), si.getMainFile(), si.getParamString(), "", "", interpreterId, si.getScriptVersion());
					return RestObject.retOKWithPayload(new GenericResponse("The script has been added to user profile"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
			}

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script/param:get", method = RequestMethod.GET)
	@Operation(	summary = "Get script parameters",
				description= "Get script parameters for a specific version, in a short and detailed format")
	public ResponseEntity<RestObject>
	getRepoScriptParam( @RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptName") final String scriptName,
						@RequestHeader(value="scriptVersion") final long scriptVersion){
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			ScriptParamCompoundObject ret =  scriptingInternalDb.getScriptParams(scriptName, scriptVersion );
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script/param:add", method = RequestMethod.PUT)
	@Operation(	summary = "Add script param for a corresponding script",
				description= "Add script param for a corresponding script to the repository")
	public ResponseEntity<RestObject>
	addRepoScriptParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptId") final long scriptId,
						@RequestHeader(value="paramName") final String paramName,
						@RequestHeader(value="paramType") final String paramType,
						@RequestHeader(value="paramDimension") final String paramDimension,
						@RequestHeader(value="paramDefaultValue") final String paramDefaultValue,
						@RequestHeader(value="paramPosition") final String paramPosition,
						@RequestHeader(value="paramOrder") final int paramOrder)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptParamDetail ret = scriptingInternalDb.scriptParamAdd(	scriptId ,
																		paramName, 
																		paramType, 
																		paramDimension, 
																		paramDefaultValue, 
																		paramPosition, 
																		paramOrder);
			
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script/param:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete script parameters",
				description= "Delete script parameters in the repository")
	public ResponseEntity<RestObject>
	deleteRepoScriptParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="scriptId") final long scriptId,
							@RequestHeader(value="scriptParamId") final long scriptParamId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			scriptingInternalDb.scriptParamDelete(scriptParamId, scriptId);
			return RestObject.retOKWithPayload(new GenericResponse("Script Param deleted"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:download", method = RequestMethod.GET)
	@Operation(summary = "Download repo script",	description= "Download repo script in a browser")
	public ResponseEntity<Resource>
	downloadRepoScript(@RequestHeader(value="user") final String user,
					   @RequestHeader(value="scriptId") final String scriptId) {

		String separator = FileSystems.getDefault().getSeparator();
		User u = authUtil.getUser(user);
		long userId = u.getId();

		String zipFileName = StringUtils.generateUniqueString16();
		final String zipPath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + zipFileName + ".zip";

		try {
			ScriptDetail scriptInfo = scriptingInternalDb.getScript(scriptId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Access-Control-Expose-Headers", "*");
			headers.add("fileName", scriptInfo.getScriptName() + ".zip");
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			String scriptVersionPath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + scriptInfo.getScriptName();
			File pathMainFile = new File(scriptVersionPath);
			if( ! pathMainFile.exists() ) {
				InputStreamResource resource = new InputStreamResource(new FileInputStream(""));
				ResponseEntity<Resource> ret = ResponseEntity.ok()	.headers(headers)
						.contentLength(0)
						.contentType(MediaType.parseMediaType("application/octet-stream"))
						.body(resource);
			}

			ZipDirectory.zip(scriptVersionPath, zipPath);
			byte[] f = FileUtilWrapper.readFile(zipPath);
			InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(f) );
			return ResponseEntity.ok()	.headers(headers)
					.contentLength(f.length)
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(resource);

		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		} finally {
			FileUtilWrapper.deleteFile(zipPath);
		}
	}
	
	
	
	/* Nodes Management Section*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/bridge:add", method = RequestMethod.PUT)
	@Operation(summary = "Add association of a script to machine node",	description= "Add association of a script to a machine node.")
	public ResponseEntity<RestObject>
	addNodeToScriptBridge(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						  @RequestHeader(value="nodeId") final long nodeId ,
						  @RequestHeader(value="scriptId") final long scriptId) {

		requestId = StringUtils.generateRequestId(requestId);

		try	{
			scriptingInternalDb.machineNodeBridgeToScriptAdd(nodeId, scriptId) ;
			List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList = scriptingInternalDb.machineNodesBridgeToScriptGet(nodeId, scriptId);
			MachineNodeToScriptBridgeList lst = new MachineNodeToScriptBridgeList(machineNodeToScriptBridgeList);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/bridge:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete association of a script to machine node",
				description= "Delete association of a script to machine node")
	public ResponseEntity<RestObject>
	deleteNodeToScriptBridge(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							 @RequestHeader(value="id") final long id) {

		requestId = StringUtils.generateRequestId(requestId);

		try	{
			scriptingInternalDb.machineNodeBridgeToScriptDelete( id );
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/bridge/node:list", method = RequestMethod.GET)
	@Operation(	summary = "Get associations of all scripts for a specific node",
				description= "Get associations of all scripts for a specific node")
	public ResponseEntity<RestObject>
	listNodeToScriptBridge(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						   @RequestHeader(value="nodeId") final long nodeId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList = scriptingInternalDb.machineNodesBridgeToScriptByNodeGet(nodeId);
			MachineNodeToScriptBridgeList lst = new MachineNodeToScriptBridgeList(machineNodeToScriptBridgeList);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/bridge/script:list", method = RequestMethod.GET)
	@Operation(	summary = "Get associations of a specific script to nodes",
				description= "Get associations of a specific script to nodes")
	public ResponseEntity<RestObject>
	listScriptToNodeBridge(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						   @RequestHeader(value="scriptId") final long scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList = scriptingInternalDb.machineNodesBridgeToScriptByScriptGet(scriptId);
			MachineNodeToScriptBridgeList lst = new MachineNodeToScriptBridgeList(machineNodeToScriptBridgeList);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	/* Script Execution Section */



	/* Client execution request section */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/script:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute Repo Script Version with individual set of parameters",
				description= "Web client or thick client request channel")
	public ResponseEntity<RestObject>
	runClientRepoScript( @RequestHeader(value="user") final String user,
						 @RequestHeader(value="session") final String session,
						 @RequestHeader(value="requestId", defaultValue = "") String requestId,
						 @RequestHeader(value="persist", defaultValue = "Y") String persist,
						 @RequestHeader(value="group", defaultValue = "Y") String group,
						 @RequestHeader(value="comment", defaultValue = "") String comment,
						 @RequestHeader(value="machineList")  final String machineList, /* comma separated base Urls */
						 @RequestHeader(value="groupId", required = false ,defaultValue = "2")  final long groupId, /*Default WEB*/
						 @RequestBody final ScriptParamRepoList scriptParameters) {

		requestId = StringUtils.generateRequestId(requestId);
		ScriptingReturnObject scriptRet;
		String separator = FileSystems.getDefault().getSeparator();
		User u = authUtil.getUser(user);
		long userId = u.getId();
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		List<String> mList = ScriptUtils.getMachineList(machineList);
		try {
			ScriptDetail scriptInfo = scriptingInternalDb.getScript(scriptParameters.getScriptName());
			String scriptVersionPath = appConstants.getTempPath() + Constants.scriptFolder + separator + userId + separator + scriptInfo.getScriptName();
			ScriptUtils.checkRepoScriptFolder(scriptVersionPath, scriptInfo);
			ScriptUtils.interpolateRepoScript(scriptParameters, appConstants, scriptVersionPath, user, session, userId, requestId, scriptInfo);

			final String command = ScriptUtils.getScriptCommand( scriptInfo,  appConstants);
			if( ScriptExecutionUtils.isLocalNode(mList, ClusterDb.ownBaseUrl) ) {  /*run script locally*/
				if(UserUtils.isWebSocket(user)) {
					scriptRet = ScriptingHelper.runRepoScriptWithPush(appConstants, u, scriptInfo, requestId, timeStamp, groupId, pWrap); /*run script locally with push notifications*/
				} else {
					scriptRet= ScriptingHelper.runRepoScriptWithCollect(appConstants, scriptInfo, requestId, timeStamp, u, groupId, pWrap); /*run script locally with collect and return */
				}
			} else {
				/*run script distributed*/
				scriptRet = ProjectExecThread.runRepoScriptDistributed(ScriptUtils.getMachineList(machineList), user, session, appConstants, scriptParameters, scriptInfo, scriptVersionPath);
			}
			return RestObject.retOKWithPayload(scriptRet, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/adhoc/script:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute ad-hoc script",
				description= "Web client or thick client request channel")
	public ResponseEntity<RestObject>
	runClientAdhocScript(@RequestHeader(value="user") final String user,
						 @RequestHeader(value="session") final String session,
						 @RequestHeader(value="requestId", defaultValue = "") String requestId,
						 @RequestHeader(value="comment", defaultValue = "") String comment,
						 @RequestHeader(value="scriptName") String scriptName,
						 @RequestHeader(value="interpreterId") final int interpreterId,
						 @RequestHeader(value="machineList")  final String machineList,
						 @RequestHeader(value="groupId", required = false ,defaultValue = "2")  final long groupId, /*Default WEB*/
						 @RequestBody String scriptContent) {

		requestId = StringUtils.generateRequestId(requestId);
		scriptName = StringUtils.generateUniqueScriptName(scriptName);
		ScriptingReturnObject scriptRet;
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		User u = authUtil.getUser(user);
		try {
			scriptContent = ScriptingHelper.replaceScriptRuntimeVars(scriptContent);
			InterpreterType interpreterType = scriptingInternalDb.interpreterById(interpreterId);
			List<String> mList = new ArrayList<>();
			if(!machineList.trim().isEmpty()) {
				mList = Stream.of(machineList.split(",", -1)).toList();
			}
			final boolean isWebSocket = user != null && !user.isEmpty() && !user.isBlank() && WebSocketsWrapper.isUser(user);
			if( ScriptExecutionUtils.isLocalNode(mList, ClusterDb.ownBaseUrl) ) { /*run locally*/

				if(isWebSocket) {
					scriptRet = ScriptExecutionUtils.runLocalAdhocWithPush(u, session, appConstants, interpreterType, scriptName, requestId,  scriptContent, timeStamp, groupId, pWrap);
				} else {
					scriptRet =  ScriptExecutionUtils.runLocalAdhocWithCollect(u, session, appConstants, interpreterType, scriptName, requestId,  scriptContent, timeStamp, groupId, pWrap);
				}
			} else { /*run distributed*/
				scriptRet = ScriptExecThread.runAdhocScriptDistributed(mList, user, session, appConstants, scriptName, interpreterType, requestId, scriptContent, timeStamp) ;
				final ScriptExecutedRecord rec = new ScriptExecutedRecord(requestId, scriptName, "this-machine", machineList, interpreterId, groupId, Constants.repoShort, u.getId(), scriptContent, "", comment, timeStamp);
				pWrap.saveExecution(rec, null, "Y");
			}

			return RestObject.retOKWithPayload(scriptRet,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}





	/* #######################################   Node request cluster section ##################################################*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/adhoc/node/script:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute ad-hoc script via gate node. Script provided as single text file",
				description= "Cluster node request channel. It does not save script. Result will be returned to caller, in this case to Gate")
	public ResponseEntity<RestObjectShort>
	runNodeAdhocScript(@RequestHeader(value="user") final String user,
					   @RequestHeader(value="session") final String session,
					   @RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="internalUser") final String internalUser,
					   @RequestHeader(value="internalPassword") final String internalPassword,
					   @RequestHeader(value="scriptName") final String scriptName,
					   @RequestHeader(value="interpreter") final String interpreter,
					   @RequestHeader(value="senderBaseUrl", required = false) final String senderBaseUrl,
					   @RequestHeader(value="timeStamp") final long timeStamp,
					   @RequestHeader(value="groupId", required = false ,defaultValue = "2")  final long groupId, /*Default WEB*/
					   @RequestBody String scriptContent,
					   HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		User u = authUtil.getUser(user);

		/*For behind the firewall execution nodes, session auth is ignored , need rudimentary security that is checked here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retAuthError(requestId);
		}

		UserShort us = new UserShort(user, session, senderBaseUrl);
		InternalUserDb.loggedUsers.put(session, us);
		try {
			ScriptingReturnObject scriptRet = ScriptingHelper.runAdhocWithNotificationsToGate(scriptName, interpreter, requestId, u, session, scriptContent, senderBaseUrl, appConstants, timeStamp);
			return RestObjectShort.retOKWithPayload(scriptRet.toStringPretty(), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}  finally {
			InternalUserDb.loggedUsers.remove(session);
			InMemScriptData.deleteRequestOutput(requestId);
		}
	}




	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/repo/node/script:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute repo scripting project via cluster node. Script(s) provided in zip format",
				description= "Cluster node request channel")
	public ResponseEntity<RestObjectShort>
	runNodeMultipartScript(@RequestHeader(value="user") final String user,
						   @RequestHeader(value="session") final String session,
						   @RequestHeader(value="requestId", defaultValue = "") String requestId,
						   @RequestHeader(value="internalUser") final String internalUser,
						   @RequestHeader(value="internalPassword") final String internalPassword,

						   @RequestHeader(value="mainFileName") final String mainFileName,
						   @RequestHeader(value="scriptName") final String scriptName,
						   @RequestHeader(value="interpreterName") final String interpreterName,

						   @RequestHeader(value="senderBaseUrl", required = false) final String senderBaseUrl,
						   @RequestHeader(value="timeStamp") final long timeStamp,
						   @RequestParam("file") final MultipartFile attachment,
						   HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		/*For behind the firewall execution nodes, session auth is ignored , need rudimentary security that is checked here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retIpError(requestId);
		}

		try {
			String destPath = ScriptUtils.prepareNodeScriptFolder(session, requestId, appConstants.getTempPath() + Constants.scriptFolder, user, attachment);
			ScriptingReturnObject scriptRet = ScriptingHelper.runMultipartWithNotificationsToGate(scriptName, interpreterName, requestId, user, session, destPath, senderBaseUrl, appConstants, timeStamp);
			return RestObjectShort.retOKWithPayload(scriptRet.toStringPretty(), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			InternalUserDb.loggedUsers.remove(session);
		}
	}


	/**
	 * Receives notifications from remote nodes running scripts.
	 * The requests are coming from other nodes runNodeAdhocScript endpoint (via RestApiScriptingClient.updateGateSink)
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/node/sink:data", method = RequestMethod.POST)
	@Operation(	summary = "Sink channel for incoming processed data captured by node parallel execution." +
							"Not for use via web or think client, instead is used by running script to feed results into the node",
				description= "Cluster node request channel")
	public ResponseEntity<RestObjectShort>
	sinkNodeCaptureData( @RequestHeader(value="user") final String user,
						 @RequestHeader(value="requestId", defaultValue = "") String requestId,
						 @RequestHeader(value="baseUrl", required = false) final String fromBaseUrl,
						 @RequestBody ScriptingReturnObject retObject,
						 HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		final boolean isWebSocket = user != null && !user.isEmpty() && !user.isBlank() && WebSocketsWrapper.isUser(user);
		if(isWebSocket) { /*if user is hard connected, stream it  */
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.message, retObject.toString(), ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
		}

		/*Save it and serialize full output if done*/
		InMemScriptData.addRequestData(requestId,  fromBaseUrl, retObject);
		if(retObject.getIsCompleted().compareToIgnoreCase("Y")==0) {
            try {
                InMemScriptData.serializeMachineScriptOutput(user, requestId, fromBaseUrl, appConstants); /*serialize machine's output*/
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(InMemScriptData.isRequestCompleted(requestId)) { /*Is the entire request fully completed? */
				if( !InMemScriptData.finalizeScriptOutput(user, requestId, appConstants) ) { /*if completed, re-try to serialize remaining nodes, but this is most likely already done, therefore it might be redundant*/
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Error InMemScriptData.finalizeScriptOutput");
				}
			}
		}

		return RestObjectShort.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}



	/* Scripting Push Notification Section  */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/log:stdin", method = RequestMethod.POST)
	@Operation(	summary = "Push notification to web or thick client via web socket of log lines",
			description= "Scripting websocket channel")
	public ResponseEntity<RestObjectShort>
	loopbackScriptStdin(@RequestHeader(value="user") final String user,
						@RequestHeader(value="session") final String session,
						@RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
						@RequestHeader(value="internalUser") final  String internalUser,
						@RequestHeader(value="internalPassword") final String internalPassword,
						@RequestHeader(value="baseUrl") final String baseUrl, /*source Url*/
						@RequestHeader(value="websocketMessageType") final String websocketMessageType, /* hs/ds/fs */
						@RequestBody final String line /*for header is script time, for footer is count*/,
						HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		/*this end-point is behind the firewall, therefore session auth is ignored , a rudimentary security must be used here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retIpError(requestId);
		}

		if(WebSocketsWrapper.isUser(user)) {
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, websocketMessageType, line, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
		}
		else {
			if(websocketMessageType.compareToIgnoreCase(WebsocketMessageType.headerScript) == 0) {
				ScriptingSharedLogs.setStart(session,  requestId, line);
			} else if(websocketMessageType.compareToIgnoreCase(WebsocketMessageType.detailScript) == 0) {
				ScriptingSharedLogs.setStart(session,  requestId, line);
			}  else if(websocketMessageType.compareToIgnoreCase(WebsocketMessageType.footerScript) == 0) {
				ScriptingSharedLogs.setEnd(session,  requestId, line);
			}
		}
		return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}




	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:header", method = RequestMethod.POST)
	@Operation(	summary = "Scripting push notification to web or thick client via web socket of the data header",
				description= "Scripting websocket channel")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataHeader(@RequestHeader(value="user") final String user,
							 @RequestHeader(value="session") final String session,
							 @RequestHeader(value="requestId", defaultValue = "") String requestId,
							 @RequestHeader(value="internalUser") final  String internalUser,
							 @RequestHeader(value="internalPassword") final String internalPassword,
							 @RequestBody final String tDefinition,
							 HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		/*this end-point is behind the firewall, therefore session auth is ignored , a rudimentary security must be used here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retIpError(requestId);
		}


		UserShort uShort = authUtil.isUserAuthenticated(session);
		TableDefinition tableDefinition = TableDefinition.toTableDefinition(tDefinition);
		assert tableDefinition != null;
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0 && WebSocketsWrapper.isUser(session) ) {
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.headerScriptData, tableDefinition, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		return RestObjectShort.retExceptionWithPayload("ERROR",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}




	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:footer", method = RequestMethod.POST)
	@Operation(	summary = "Scripting push notification to web or thick client via web socket of the data footer ",
				description= "Scripting websocket channel")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataFooter(@RequestHeader(value="user") final String user,
							 @RequestHeader(value="session") final String session,
							 @RequestHeader(value="requestId", defaultValue = "") String requestId,
							 @RequestHeader(value="internalUser") final  String internalUser,
							 @RequestHeader(value="internalPassword") final String internalPassword,
							 @RequestBody final String rValue,
							 HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		/*this end-point is behind the firewall, therefore session auth is ignored , a rudimentary security must be used here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retIpError(requestId);
		}


		UserShort uShort = authUtil.isUserAuthenticated(session);
		RowValue rowValue = RowValue.toRowValue(rValue);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0 && WebSocketsWrapper.isUser(user)) {
			assert rowValue != null;
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.footerScriptData, rowValue, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		return RestObjectShort.retExceptionWithPayload("ERROR",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:detail", method = RequestMethod.POST)
	@Operation(	summary = "Scripting push notification to web or thick client via web socket of the data row",
				description= "Scripting websocket channel")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataDetail(@RequestHeader(value="user") final String user,
							 @RequestHeader(value="session") final String session,
							 @RequestHeader(value="requestId", defaultValue = "") String requestId,
							 @RequestHeader(value="internalUser") final  String internalUser,
							 @RequestHeader(value="internalPassword") final String internalPassword,
							 @RequestBody final String rValue,
							 HttpServletRequest request) {
		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		/*this end-point is behind the firewall, therefore session auth is ignored , a rudimentary security must be used here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retIpError(requestId);
		}


		UserShort uShort = authUtil.isUserAuthenticated(session);
		RowValue rowValue = RowValue.toRowValue(rValue);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0 && WebSocketsWrapper.isUser(user)) {
			assert rowValue != null;
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScriptData, rowValue, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		return RestObjectShort.retExceptionWithPayload("ERROR",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:details", method = RequestMethod.POST)
	@Operation(	summary = "Scripting push notification to web or thick client via web socket of multiple data rows (in bulk) ",
				description= "Scripting websocket channel")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataDetails(@RequestHeader(value="user") final String user,
							  @RequestHeader(value="session") final String session,
							  @RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
							  @RequestHeader(value="internalUser") final  String internalUser,
							  @RequestHeader(value="internalPassword") final String internalPassword,
							  @RequestBody final List<RowValue> rowValues,
							  HttpServletRequest request) {
		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isClusterAddress(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		/*this end-point is behind the firewall, therefore session auth is ignored , a rudimentary security must be used here */
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retIpError(requestId);
		}

		requestId = StringUtils.generateRequestId(requestId);
		UserShort uShort = authUtil.isUserAuthenticated(session);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0 && WebSocketsWrapper.isUser(user)) {
			WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScriptData, rowValues, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		return RestObjectShort.retExceptionWithPayload("ERROR",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

}
