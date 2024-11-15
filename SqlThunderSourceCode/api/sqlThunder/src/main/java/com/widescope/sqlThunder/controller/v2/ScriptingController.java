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
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.widescope.logging.AppLogger;
import com.widescope.scripting.execution.ScriptExecThread;
import com.widescope.scripting.execution.ScriptExecutionUtils;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
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
import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.rest.RestObjectShort;
import com.widescope.scripting.DownloadScriptStructure;
import com.widescope.scripting.DownloadScriptStructureList;
import com.widescope.scripting.LogDetail;
import com.widescope.scripting.ScriptDetail;
import com.widescope.scripting.ScriptDetailObject;
import com.widescope.scripting.ScriptParamCompoundObject;
import com.widescope.scripting.ScriptParamDetail;
import com.widescope.scripting.ScriptParamList2;
import com.widescope.scripting.ScriptParamObject;
import com.widescope.scripting.ScriptParamRepoList;
import com.widescope.scripting.ScriptingHelper;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.scripting.ScriptingSharedData;
import com.widescope.scripting.ScriptingSharedLogs;
import com.widescope.scripting.db.InterpreterType;
import com.widescope.scripting.db.MachineNodeToScriptBridge;
import com.widescope.scripting.db.MachineNodeToScriptBridgeList;
import com.widescope.scripting.db.InterpreterList;
import com.widescope.scripting.db.ScriptingInternalDb;
import com.widescope.scripting.storage.HistScriptFileManagement;
import com.widescope.scripting.storage.HistScriptList;
import com.widescope.scripting.streaming.HeaderDef;
import com.widescope.scripting.streaming.RowVal;
import com.widescope.scripting.streaming.StreamingStatic;
import com.widescope.scripting.streaming.TableVal;
import com.widescope.scripting.websock.ScriptFooterOutput;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.FileCharacteristic;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.compression.ZipDirectory;
import com.widescope.sqlThunder.utils.restApiClient.RestApiScriptingClient;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.sqlThunder.utils.user.UserShort;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;



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

	private static final String updateGlobalVars = "updateGlobalVars";

	@PostConstruct
	public void initialize() {

	}
	
		
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/interpreter:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Interpreter with associated information",	description= "...")
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
			List<InterpreterType> lstInterpreter = scriptingInternalDb.interpreterByNameAndVersionGet(interpreterName, interpreterVersion);
			InterpreterList lst = new InterpreterList(lstInterpreter);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/interpreter:update", method = RequestMethod.POST)
	@Operation(summary = "Update Interpreter",	description= "...")
	public ResponseEntity<RestObject> 
	interpreterUpdate(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterId") final String interpreterId,
						@RequestHeader(value="interpreterName") final String interpreterName,
						@RequestHeader(value="interpreterVersion") final String interpreterVersion,
						@RequestHeader(value="interpreterPath") final String interpreterPath,
						@RequestHeader(value="command") final String command,
						@RequestHeader(value="fileExtensions") final String fileExtensions) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.interpreterUpdate(Integer.parseInt(interpreterId),  interpreterName, interpreterVersion, interpreterPath, command, fileExtensions);
			InterpreterType lstInterpreter = scriptingInternalDb.interpreterByIdGet(Integer.parseInt(interpreterId));
			InterpreterList lst = new InterpreterList(lstInterpreter);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/interpreter:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Interpreter",	description= "...")
	public ResponseEntity<RestObject> 
	interpreterDelete(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterId") final String interpreterId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.interpreterDelete( Integer.parseInt(interpreterId) );
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/interpreter:search", 
					method = RequestMethod.GET)
	@Operation(summary = "Get A List Of Interpreter Versions providing a filter for the name",	description= "...")
	public ResponseEntity<RestObject> 
	searchInterpreter(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterName") final String interpreterName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> InterpreterList = scriptingInternalDb.interpreterByNameGet(interpreterName);
			InterpreterList interpreterDetailList = new InterpreterList(InterpreterList);
			return RestObject.retOKWithPayload(interpreterDetailList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/interpreter:list", method = RequestMethod.GET)
	@Operation(summary = "Get List of Interpreters",	description= "...")
	public ResponseEntity<RestObject> 
	listAllInterpreters(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> InterpreterList = scriptingInternalDb.interpreterAllGet();
			InterpreterList interpreterDetailList = new InterpreterList(InterpreterList);
			return RestObject.retOKWithPayload(interpreterDetailList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Script to Repository",	description= "...")
	public ResponseEntity<RestObject> 
	addScript(	@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="scriptName") final String scriptName,
				@RequestHeader(value="paramString", required = false, defaultValue = "") final String paramString,
				@RequestHeader(value="mainFile") final String mainFile,
				@RequestHeader(value="interpreterId") String interpreterId,
				@RequestHeader(value="fileType") final String fileType,
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
			
			String folderCreated = ScriptingHelper.createFolder(String.valueOf(userId), scriptName, appConstants.getScriptStoragePath());
			
			
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
															Integer.parseInt(interpreterId),
															currentVersion);
									

			return RestObject.retOKWithPayload(script, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/content:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Script to Repository",	description= "...")
	public ResponseEntity<RestObject> 
	addScriptByContent(	@RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptName") final String scriptName,
						@RequestHeader(value="comment") final String comment,
						@RequestHeader(value="paramString", required = false, defaultValue = "") final String paramString,
						@RequestHeader(value="interpreterId") final String interpreterId,
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
			
			String folderCreated = ScriptingHelper.createFolder(String.valueOf(userId), scriptName, appConstants.getScriptStoragePath());
			FileUtilWrapper.overwriteFile (folderCreated, scriptName, scriptContent);
			FileUtilWrapper.overwriteFile (folderCreated, commentFile, comment);
			ScriptDetail script = scriptingInternalDb.scriptAdd(userId, 
																scriptName, 
																scriptName, 
																paramString, 
																"", //predictFile 
																"", // predictFunc 
																Integer.parseInt(interpreterId),
																currentVersion);

			
			return RestObject.retOKWithPayload(script, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:content", method = RequestMethod.GET)
	@Operation(summary = "Get Script and versions, scriptName can also be only part of name to wider range for searching",	description= "...")
	public ResponseEntity<Resource> 
	getScriptContent(	@RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptId", required = false) final String scriptId) {
		DownloadScriptStructureList res = new DownloadScriptStructureList();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptDetail scriptDetail = scriptingInternalDb.getScript(Integer.parseInt(scriptId));
			String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String scriptVersionRelativePath = appConstants.getScriptStoragePath() + separator + userId + separator + scriptDetail.getScriptName();
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
				String tmpPath = appConstants.getScriptTempPath() + separator + userId + separator + UUID.randomUUID().toString();
				FileUtilWrapper.overwriteFile(tmpPath, "attachment", res.toString());
				
				
				
				File file2Download = new File(tmpPath + "/attachment");
				InputStreamResource resource = new InputStreamResource(new FileInputStream(file2Download));
			
				ResponseEntity<Resource> ret = ResponseEntity.ok().headers(headers)
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
		} catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		}
	}
	

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:search", method = RequestMethod.GET)
	@Operation(summary = "Get Script and versions, scriptName can also be only part of name to wider range for searching",	description= "...")
	public ResponseEntity<RestObject>
	scriptSearch(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
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
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:user", method = RequestMethod.GET)
	@Operation(summary = "Get Scripts, specific to a user",	description= "...")
	public ResponseEntity<RestObject> 
	userScripts(@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long userId = authUtil.getUser(user).getId();
			scriptingInternalDb.getUserAccessByUser(userId);
			ScriptDetailObject ret =  new ScriptDetailObject( scriptingInternalDb.getScriptByUser(userId) );
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:get", method = RequestMethod.GET)
	@Operation(summary = "Get Specific Script Information",	description= "...")
	public ResponseEntity<RestObject> 
	getScript(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="scriptId") final String scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			ScriptDetail script = scriptingInternalDb.getScript(Integer.parseInt(scriptId));
			List<ScriptParamDetail> params = scriptingInternalDb.getScriptParams(Integer.parseInt(scriptId));
			script.setScriptParamDetailList(params);
			List<MachineNodeToScriptBridge> bridges = scriptingInternalDb.machineNodesBridgeToScriptByScriptGet(Integer.parseInt(scriptId));
			script.setMachineNodeToScriptBridgeList(bridges);
			return RestObject.retOKWithPayload(script, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove Script and All its versions from Repository",	description= "...")
	public ResponseEntity<RestObject> 
	scriptRemove(	@RequestHeader(value="user") final String user,
					@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="scriptId") final String scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			ScriptDetail scriptDetail = scriptingInternalDb.getScript(Integer.parseInt(scriptId));
			boolean isScriptHomeFolderDeleted = ScriptingHelper.removeScriptFromFS(	appConstants.getScriptStoragePath(), 
																					userId,
																					scriptDetail.getScriptName());
			
			if(isScriptHomeFolderDeleted) {
				scriptingInternalDb.scriptDeleteAll(Integer.parseInt(scriptId));
				return RestObject.retOKWithPayload(scriptDetail, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} 
			
			return RestObject.retException(scriptDetail, requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cannot delete script");

		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/version:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove Script Version Of A Script from Repository")
	public ResponseEntity<RestObject> 
	scriptVersionRemove(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptName") final String scriptName,
						@RequestHeader(value="scriptVersion") final String scriptVersion) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean isScriptVersionHomeFolderDeleted ;
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.scriptVersionDelete(scriptName, Integer.parseInt(scriptVersion) );
			isScriptVersionHomeFolderDeleted = ScriptingHelper.removeScriptVersionFromFS(	appConstants.getScriptStoragePath(),
																							scriptName,
																							Integer.parseInt(scriptVersion));
			
			String message = "Script version deleted from database.";
			message = message + ( isScriptVersionHomeFolderDeleted ? " Version Home Folder deleted." 
																			: " Version Home Folder not deleted.");

			return RestObject.retOKWithPayload(new GenericResponse(message), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/param:get", method = RequestMethod.GET)
	@Operation(summary = "Get Script Parameters in either form, detailed or for execution, or both",	description= "...")
	public ResponseEntity<RestObject>
	getScriptParam( @RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="scriptName") final String scriptName,
					@RequestHeader(value="scriptVersion") final String scriptVersion){
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			ScriptParamCompoundObject ret =  scriptingInternalDb.getScriptParams(scriptName, Integer.parseInt(scriptVersion) );
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/param:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Script Param for a corresponding Script",	description= "...")
	public ResponseEntity<RestObject> 
	scriptParamAdd(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="scriptId") final String scriptId,
					@RequestHeader(value="paramName") final String paramName,
					@RequestHeader(value="paramType") final String paramType,
					@RequestHeader(value="paramDimension") final String paramDimension,
					@RequestHeader(value="paramDefaultValue") final String paramDefaultValue,
					@RequestHeader(value="paramPosition") final String paramPosition,
					@RequestHeader(value="paramOrder") final String paramOrder)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptParamDetail ret = scriptingInternalDb.scriptParamAdd(	Integer.parseInt(scriptId) , 
																		paramName, 
																		paramType, 
																		paramDimension, 
																		paramDefaultValue, 
																		paramPosition, 
																		Integer.parseInt(paramOrder) 
																		);
			
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/param:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Script Param",	description= "...")
	public ResponseEntity<RestObject> 
	scriptParamDelete(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="scriptId") final String scriptId,
						@RequestHeader(value="scriptParamId") final String scriptParamId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ScriptingInternalDb scriptingInternalDb = new ScriptingInternalDb();
			scriptingInternalDb.scriptParamDelete(Integer.parseInt(scriptParamId), Integer.parseInt(scriptId));
			return RestObject.retOKWithPayload(new GenericResponse("Script Param deleted"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	/* Nodes Management Section*/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/bridge:add", method = RequestMethod.PUT)
	@Operation(summary = "Add association of a Script to machine node",	description= "...")
	public ResponseEntity<RestObject> 
	machineNodeBridgeToScriptAdd(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="nodeId ") final String nodeId ,
									@RequestHeader(value="scriptId") final String scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.machineNodeBridgeToScriptAdd(Integer.parseInt(nodeId), Integer.parseInt(scriptId) ) ;
			List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList = scriptingInternalDb.machineNodesBridgeToScriptGet(Integer.parseInt(nodeId), Integer.parseInt(scriptId));
			MachineNodeToScriptBridgeList lst = new MachineNodeToScriptBridgeList(machineNodeToScriptBridgeList);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/bridge:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete association of a script to machine node",	description= "...")
	public ResponseEntity<RestObject> 
	machineNodeBridgeToScriptDelete(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="id") final String id) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			scriptingInternalDb.machineNodeBridgeToScriptDelete( Integer.parseInt(id) );
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/bridge/node:get", method = RequestMethod.GET)
	@Operation(summary = "Get associations to scripts for a node",	description= "...")
	public ResponseEntity<RestObject> 
	nodeBridgeToScriptForNode(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="nodeId") final String nodeId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList = scriptingInternalDb.machineNodesBridgeToScriptByNodeGet(Integer.parseInt(nodeId));
			MachineNodeToScriptBridgeList lst = new MachineNodeToScriptBridgeList(machineNodeToScriptBridgeList);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/bridge/script:get", method = RequestMethod.DELETE)
	@Operation(summary = "Get associations of script for nodes",	description= "...")
	public ResponseEntity<RestObject> 
	nodeBridgeToScriptForScript(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="scriptId") final String scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList = scriptingInternalDb.machineNodesBridgeToScriptByScriptGet(Integer.parseInt(scriptId));
			MachineNodeToScriptBridgeList lst = new MachineNodeToScriptBridgeList(machineNodeToScriptBridgeList);
			return RestObject.retOKWithPayload(lst, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	/* Script Execution Section */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/repo:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute Repo Script Version with a set of parameters",
				description= "Web client or thick client request channel")
	public ResponseEntity<RestObject> 
	runRepoScriptViaClient(@RequestHeader(value="user") final String user,
						  @RequestHeader(value="session") final String session,
						  @RequestHeader(value="requestId", defaultValue = "") String requestId,
						  @RequestHeader(value="scriptId") final String scriptId,
						  @RequestHeader(value="machineList") final String machineList, /*comma separated */
						  @RequestBody final String scriptParameters) {

		requestId = StringUtils.generateRequestId(requestId);
		if(machineList.isEmpty()) {
			return RestObject.retOKWithPayload("Error ", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		ScriptingReturnObject scriptRet = new ScriptingReturnObject();
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
		boolean isHttpSessionId = false;
		try {
			isHttpSessionId = WebSocketsWrapper.isUser(session);
		} catch(Exception ignored) {}
		
		User u = authUtil.getUser(user);
		long userId = u.getId();
		String folder = UUID.randomUUID().toString();
		String zipFileName = StringUtils.generateUniqueString16(); 
		final String tmpPath = appConstants.getScriptTempPath() + separator + userId + separator + folder;
		
		final String zipPath = appConstants.getScriptTempPath() + separator + userId + separator + zipFileName + ".zip";
		try {
			ScriptDetail scriptInfo = scriptingInternalDb.getScript(Integer.parseInt(scriptId));
			Map<String, String> scriptParamObject = ScriptParamObject.convertStringListToMap(scriptParameters);
			//String scriptVersionLogPath = appConstants.getScriptLogPath() + separator + scriptInfo.getScriptName();
			String scriptVersionPath = appConstants.getScriptStoragePath() + separator + userId + separator + scriptInfo.getScriptName();
			String scriptVersionMainFilePath = scriptVersionPath + separator + scriptInfo.getMainFile() ;
			File pathMainFile = new File(scriptVersionMainFilePath);
			if( ! pathMainFile.exists() ) {
				scriptRet.addLogDetail(new LogDetail("exception", "Cannot find the main file of the script in the repo folder"));
				scriptRet.setIsStreaming("N");
				return RestObject.retOKWithPayload(scriptRet, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			
			
			FileUtilWrapper.copyFolder(scriptVersionPath, tmpPath);
			FileUtilWrapper.replaceStringInAllFilesFromFolder(tmpPath, scriptParamObject);
			FileUtilWrapper.replaceStringInAllFilesFromFolder(tmpPath, user, session, requestId);
			ZipDirectory.zip(tmpPath, zipPath);
			System.out.print("File to be sent to cluster: " + zipPath);
			
			boolean isOwnHostExecution = false;
			List<String> mList = Stream.of(machineList.split(",", -1)).toList();
			for(String node: mList) {
				if(node.equals(ClusterDb.ownBaseUrl)) {
					isOwnHostExecution = true;
				} else {
					RestApiScriptingClient.runRepoScriptViaNodeMultipart(	node,
																			user,
																			session,
																			appConstants.getUser(), 
																			appConstants.getUserPasscode(),
																			scriptInfo.getMainFile(),
																			scriptInfo.getInterpreterId(),
																			requestId,
																			zipFileName + ".zip",
																			zipPath,
																			folder
																			);
				}
			}
			
			
			/*At the very end, execute on current host*/
			if(isOwnHostExecution) {
				ScriptingSharedData.addEmptyEntryLog(session,requestId);
				final String command = scriptInfo.getInterpreterPath() + separator	+ scriptInfo.getCommand() + " "	+ tmpPath + separator + scriptInfo.getMainFile();
				if( user != null && !user.isEmpty() && !user.isBlank() && isHttpSessionId) {
					scriptRet.setIsStreaming("Y");
					try {
						ScriptingHelper.runRepoWithNotifications(user, command, requestId);
					} catch (Exception e) {
						AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
						WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, e.getMessage(), ClusterDb.ownBaseUrl);
						WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
						ScriptFooterOutput scriptFooterOutput = new ScriptFooterOutput(0);
						wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, scriptFooterOutput, ClusterDb.ownBaseUrl);
						WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
					}

					FileUtilWrapper.deleteFolder(tmpPath);
					ScriptingSharedData.removeRequestData(session, requestId);
				} else {
					/*run it returning large object with logs*/
					scriptRet.setIsStreaming("N");
					List<LogDetail> logList= ScriptingHelper.runCommandWithReturn(scriptVersionPath, command);
					scriptRet.setLogDetailList(logList);
					FileUtilWrapper.deleteFolder(tmpPath);
					ScriptingSharedData.removeRequestData(session, requestId);
				}
			}
			return RestObject.retOKWithPayload(scriptRet, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			FileUtilWrapper.deleteFile(zipPath);
			FileUtilWrapper.deleteDirectoryWithAllContent(tmpPath);
			ScriptingSharedData.removeRequestData(session,requestId);
		}
		
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/repo/multiple:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute Repo Script Version with individual set of parameters",
				description= "Web client or thick client request channel")
	public ResponseEntity<RestObject> 
	runRepoParameterizedScriptViaClient(@RequestHeader(value="user") final String user,
										@RequestHeader(value="session") final String session,
										@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="machineList")  final String machineList, /*comma separated base Urls */
										@RequestBody final ScriptParamRepoList scriptParameters) {

		requestId = StringUtils.generateRequestId(requestId);
		if(scriptParameters.getRequestId() == null || scriptParameters.getRequestId().trim().isEmpty()) {
			scriptParameters.setRequestId(UUID.randomUUID().toString());
		} 
		ScriptingReturnObject scriptRet = new ScriptingReturnObject();
		String separator = FileSystems.getDefault().getSeparator(); // or FileSystem.getSeparator()
		boolean isHttpSessionId = false;
		try {
			isHttpSessionId = WebSocketsWrapper.isUser(user);
		} catch(Exception ignored) {}
		
		User u = authUtil.getUser(user);
		long userId = u.getId();
		
		try {
			ScriptDetail scriptInfo = scriptingInternalDb.getScript(scriptParameters.getScriptId());
			String scriptVersionPath = appConstants.getScriptStoragePath() + separator + userId + separator + scriptInfo.getScriptName();
			String scriptVersionMainFilePath = scriptVersionPath + separator + scriptInfo.getMainFile() ;
			File pathMainFile = new File(scriptVersionMainFilePath);
			if( ! pathMainFile.exists() ) {
				scriptRet.addLogDetail(new LogDetail("exception", "Cannot find the main file of the script in the repo folder"));
				scriptRet.setIsStreaming("N");
				return RestObject.retOKWithPayload(scriptRet, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			
			ScriptingSharedData.addEmptyEntryLog(session,requestId);
			
			
			boolean isOwnHostExecution = false;
			ScriptParamList2 localNode = null;
			for(ScriptParamList2 node: scriptParameters.getScriptParamRepoList()) {
				if(node.getNodeUrl().equals(ClusterDb.ownBaseUrl)) {
					isOwnHostExecution = true;
					localNode = node;
				} else {

					String folder = UUID.randomUUID().toString();
					String zipFileName = StringUtils.generateUniqueString16(); 
					final String tmpPath = appConstants.getScriptTempPath() + separator + userId + separator + folder;
					final String zipPath = appConstants.getScriptTempPath() + separator + userId + separator + zipFileName + ".zip";
					FileUtilWrapper.copyFolder(scriptVersionPath, tmpPath);
					FileUtilWrapper.replaceStringInAllFilesFromFolder(tmpPath, node.getScriptParamList());
					FileUtilWrapper.replaceStringInAllFilesFromFolder(tmpPath, user, session, requestId);
					ZipDirectory.zip(tmpPath, zipPath);
					System.out.print("File to be sent to cluster: " + zipPath);

					RestApiScriptingClient.runRepoScriptViaNodeMultipart(	node.getNodeUrl(),
																			user,
																			session,
																			appConstants.getUser(), 
																			appConstants.getUserPasscode(),
																			scriptInfo.getMainFile(),
																			scriptInfo.getInterpreterId(),
																			requestId,
																			zipFileName + ".zip",
																			zipPath,
																			folder
																			);
					
					FileUtilWrapper.deleteFile(zipPath);
					FileUtilWrapper.deleteDirectoryWithAllContent(tmpPath);
				}
			}
			
			
			/*At the very end, execute on current host*/
			if(isOwnHostExecution) {
				
				final String tmpPath = appConstants.getScriptTempPath() + separator + userId + separator + UUID.randomUUID().toString();
				FileUtilWrapper.copyFolder(scriptVersionPath, tmpPath);
				FileUtilWrapper.replaceStringInAllFilesFromFolder(tmpPath, localNode.getScriptParamList());
				FileUtilWrapper.replaceStringInAllFilesFromFolder(tmpPath, user, session, requestId);
				
				final String command = scriptInfo.getInterpreterPath() + separator	+ scriptInfo.getCommand() + " "	+ tmpPath + separator + scriptInfo.getMainFile();
				if( user != null && !user.isEmpty() && !user.isBlank() && isHttpSessionId) {
					scriptRet.setIsStreaming("Y");
					try {
						ScriptingHelper.runRepoWithNotifications(user, command,	requestId);
					} catch (Exception e) {
						AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
						WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, e.getMessage(), ClusterDb.ownBaseUrl);
						WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
						ScriptFooterOutput scriptFooterOutput = new ScriptFooterOutput(0);
						wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScript, scriptFooterOutput, ClusterDb.ownBaseUrl);
						WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
					}
				} else {
					/*run it returning large object with logs*/
					scriptRet.setIsStreaming("N");
					List<LogDetail> logList= ScriptingHelper.runCommandWithReturn(scriptVersionPath, command);
					scriptRet.setLogDetailList(logList);
				}
				FileUtilWrapper.deleteDirectoryWithAllContent(tmpPath);
			}
			return RestObject.retOKWithPayload(scriptRet, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			ScriptingSharedData.removeRequestData(session,requestId);
		}
		
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/adhoc:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute ad-hoc script",
				description= "Web client or thick client request channel")
	public ResponseEntity<RestObject> 
	runAdhocScriptViaClient(@RequestHeader(value="user") final String user,
							@RequestHeader(value="session") final String session,
							@RequestHeader(value="requestId", defaultValue = "") String requestId_,
							@RequestHeader(value="scriptName") final String scriptName,
							@RequestHeader(value="interpreterId") final String interpreterId,
							@RequestHeader(value="machineList")  final String machineList, /*comma separated base Urls */
							@RequestBody String scriptContent) {


		final String requestId = StringUtils.generateRequestId(requestId_);
		ScriptingReturnObject scriptRet = new ScriptingReturnObject();
		ScriptingSharedData.addEmptyEntryLog(session,requestId);
		String auth = SpringSecurityWrapper.getUserAuthorization();
		scriptContent = ScriptExecutionUtils.interpolateVars(user, session, auth, requestId, scriptContent, updateGlobalVars, appConstants);
		List<String> mList = Stream.of(machineList.split(",", -1)).toList();
		boolean isHttpSessionId = false;
		try {
			isHttpSessionId = WebSocketsWrapper.isUser(user);
		} catch(Exception ignored) {}

		final boolean isWebSocket = user != null && !user.isEmpty() && !user.isBlank() && isHttpSessionId;
		if( ScriptExecutionUtils.isLocalNode(mList, ClusterDb.ownBaseUrl) ) { /*run locally*/
			if(isWebSocket) {
				scriptRet = ScriptExecutionUtils.execScriptAsync(appConstants, scriptName, interpreterId, requestId, user, session, scriptContent);
			} else {
				scriptRet = ScriptExecutionUtils.execScriptSync(appConstants, scriptName, interpreterId, requestId, user, session, scriptContent) ;
			}
		} else { /*run distributed*/
			if(isWebSocket) {
				scriptRet = ScriptExecThread.execParallelSync(mList, user, session, appConstants, scriptName, interpreterId, requestId, scriptContent) ;
			} else {
				scriptRet = ScriptExecThread.execParallelAsync(mList, user, session, appConstants, scriptName, interpreterId, requestId, scriptContent) ;
			}
		}

		ScriptExecThread.saveAdhocScriptAsync(appConstants, scriptingInternalDb, interpreterId, authUtil, scriptName, scriptContent, user);
		ScriptingSharedData.removeRequestData(session,requestId);
		return RestObject.retOKWithPayload(scriptRet,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/adhoc/node:run", method = RequestMethod.POST)
	@Operation(summary = "Run/Execute ad-hoc Script Version via cluster node. Script provided as single text file",	description= "Cluster node request channel")
	public ResponseEntity<RestObjectShort> 
	runAdhocScriptViaNode(@RequestHeader(value="user") final String user,
						  @RequestHeader(value="session") final String session,
						  @RequestHeader(value="requestId", defaultValue = "") String requestId,
						  @RequestHeader(value="internalUser") final String internalUser,
						  @RequestHeader(value="internalPassword") final String internalPassword,
						  @RequestHeader(value="scriptName") final String scriptName,
						  @RequestHeader(value="interpreterId") final String interpreterId,
						  @RequestHeader(value="senderBaseUrl", required = false) final String senderBaseUrl,
						  @RequestBody String scriptContent,
						  HttpServletRequest request) {

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isIpInAllowedClusterNodesList(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retAuthError(requestId);
		}

		ScriptingSharedData.addEmptyEntryLog(session,requestId);
		UserShort us = new UserShort(user, session, senderBaseUrl);
		InternalUserDb.loggedUsers.put(session, us);
		try {
			ScriptingHelper.runAdhocWithNotificationsToNode(appConstants.getScriptTempPath(), scriptName, Integer.parseInt( interpreterId ), requestId, user, session, appConstants.getUser(), appConstants.getUserPasscode(), scriptContent, senderBaseUrl);
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			ScriptFooterOutput scriptHeaderOutput = new ScriptFooterOutput(0);
			RestApiScriptingClient.loopbackScriptStdin(	senderBaseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.detailScript, scriptHeaderOutput.toString());
			RestApiScriptingClient.loopbackScriptStdin(	senderBaseUrl, user, session, internalUser, internalPassword, requestId, WebsocketMessageType.footerScript, scriptHeaderOutput.toString());
		} finally {
			InternalUserDb.loggedUsers.remove(session);
			ScriptingSharedData.removeRequestData(session,requestId);
		}
		return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}




	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/repo/multipart/node:run", method = RequestMethod.POST)
	@Operation(	summary = "Run/Execute repo scripting project via cluster node. Script provided as zip file",
				description= "Cluster node request channel")
	public ResponseEntity<RestObjectShort> 
	runRepoScriptViaNodeMultipart(@RequestHeader(value="user") final String user,
								  @RequestHeader(value="session") final String session,
								  @RequestHeader(value="requestId", defaultValue = "") String requestId,
								  @RequestHeader(value="internalUser") final String internalUser,
								  @RequestHeader(value="internalPassword") final String internalPassword,
								  @RequestHeader(value="mainFileName") final String mainFileName,
								  @RequestHeader(value="interpreterId") final String interpreterId,
								  @RequestHeader(value="baseUrl", required = false) final String baseUrl,
								  @RequestHeader(value="baseFolder", required = false) final String baseFolder,
								  @RequestParam("file") final MultipartFile attachment) {

		requestId = StringUtils.generateRequestId(requestId);
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retAuthError(requestId);
		}

		try {
			ScriptingSharedData.addEmptyEntryLog(session,requestId);
			UserShort us = new UserShort(user, session, baseUrl);
			InternalUserDb.loggedUsers.put(session, us);

			String separator = FileSystems.getDefault().getSeparator();
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String zipFileName = StringUtils.generateUniqueString16(); 
			File attachmentFile = File.createTempFile(zipFileName, ".temp");
			String absolutePathForTempFile = attachmentFile.getAbsolutePath();
			FileOutputStream o = new FileOutputStream(attachmentFile);
			IOUtils.copy(attachment.getInputStream(), o);
			o.close();
			
			final String destPath = appConstants.getScriptTempPath() + separator + userId ;
			ZipDirectory.unzip(absolutePathForTempFile, destPath);
			
			try {
				ScriptingHelper.runAdhocWithNotificationsToNode(destPath + separator + baseFolder,
																mainFileName,
																Integer.parseInt( interpreterId ),
																requestId,
																user,
																session,
																internalUser,
																internalPassword,
																baseUrl);
			} catch (IOException e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				RestApiScriptingClient.loopbackScriptStdin(	baseUrl,
															user, 
															session, 
															internalUser,
															internalPassword,
															requestId, 
															WebsocketMessageType.detailScript,
															e.getMessage());
				
				
				ScriptFooterOutput scriptHeaderOutput = new ScriptFooterOutput(0);
				RestApiScriptingClient.loopbackScriptStdin(	baseUrl, 
															user, 
															session, 
															internalUser,
															internalPassword,
															requestId, 
															WebsocketMessageType.footerScript, 
															scriptHeaderOutput.toString());
				
			} 
			
			return RestObjectShort.retOKWithPayload("OK", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObjectShort.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			InternalUserDb.loggedUsers.remove(session);
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script/adhoc/node:sink", method = RequestMethod.POST)
	@Operation(	summary = "Sink channel for incoming results processed by parallel node execution. Not for use via client ",
				description= "Cluster node request channel")
	public ResponseEntity<RestObjectShort>
	sinkNodeResult(@RequestHeader(value="user") final String user,
				   @RequestHeader(value="session") final String session,
				   @RequestHeader(value="requestId", defaultValue = "") String requestId,
				   @RequestHeader(value="internalUser") final String internalUser,
				   @RequestHeader(value="internalPassword") final String internalPassword,
				   @RequestHeader(value="scriptName") final String scriptName,
				   @RequestHeader(value="interpreterId") final String interpreterId,
				   @RequestHeader(value="baseUrl", required = false) final String baseUrl,
				   @RequestBody ScriptingReturnObject retObject,
				   HttpServletRequest request) {

		ScriptingReturnObject scriptRet = new ScriptingReturnObject();

		/* Request must come from a cluster node, such as the gate */
		if(!ConfigRepoDb.isIpInAllowedClusterNodesList(request) ) {
			return RestObjectShort.retAuthError(requestId);
		}

		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retAuthError(requestId);
		}

		return RestObjectShort.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:header", method = RequestMethod.POST)
	@Operation(	summary = "Loopback output from executing scripts in String or JSON format",
				description= "Push notification to web or thick client via web socket of the data header ")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataHeader(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="session") final String session,
								@RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
								@RequestBody final String tDefinition) {
		boolean isHttpSessionId = false;
		requestId = StringUtils.generateRequestId(requestId);
		UserShort uShort = authUtil.isUserAuthenticated(session);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0) {
			try {
				isHttpSessionId = WebSocketsWrapper.isUser(session);
			} catch(Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}

		TableDefinition tableDefinition = TableDefinition.toTableDefinition(tDefinition);
		try	{
			if(isHttpSessionId) {
                assert tableDefinition != null;
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.headerScriptData, tableDefinition, uShort.getBaseUrl());
                WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			}
			else {
				System.out.println("Header Data sent to " + uShort.getBaseUrl());
				RestApiScriptingClient.loopbackScriptdataHeader(uShort.getBaseUrl(), user, session, appConstants.getUser(), appConstants.getUserPasscode(), requestId, tableDefinition);
			}

			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObjectShort.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:footer", method = RequestMethod.POST)
	@Operation(	summary = "Loopback output from executing scripts in String or json format",
				description= "Push notification to web or thick client via web socket of the data footer ")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataFooter(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="session") final String session,
								@RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
								@RequestBody final String rValue) {
		boolean isHttpSessionId = false;
		requestId = StringUtils.generateRequestId(requestId);
		UserShort uShort = authUtil.isUserAuthenticated(session);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0) {
			isHttpSessionId = WebSocketsWrapper.isUser(user);
		}
		
		RowValue rowValue = RowValue.toRowValue(rValue);

		try	{
			if(isHttpSessionId) {
                assert rowValue != null;
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.footerScriptData, rowValue, ClusterDb.ownBaseUrl);
                WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			}
			else {
				System.out.println("Footer Data sent to " + uShort.getBaseUrl());
				RestApiScriptingClient.loopbackScriptDataFooter(uShort.getBaseUrl(),
																user,
																session,
																appConstants.getUser(),
																appConstants.getUserPasscode(),
																requestId,
																rowValue);
						
			}

			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObjectShort.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:detail", method = RequestMethod.POST)
	@Operation(	summary = "Loopback output from executing scripts in String or json format",
				description= "Push notification to web or thick client via web socket of the data row ")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataDetail(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="session") final String session,
								@RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
								@RequestBody final String rValue) {
		boolean isHttpSessionId = false;
		requestId = StringUtils.generateRequestId(requestId);
		UserShort uShort = authUtil.isUserAuthenticated(session);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0) {
			isHttpSessionId = WebSocketsWrapper.isUser(user);
		}
		
		RowValue rowValue = RowValue.toRowValue(rValue);
		try	{
			
			if(isHttpSessionId) {
                assert rowValue != null;
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScriptData, rowValue, ClusterDb.ownBaseUrl);
                WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			}
			else {
				System.out.println("Detail Data sent to " + uShort.getBaseUrl());
				RestApiScriptingClient.loopbackScriptDataDetail(uShort.getBaseUrl(),
																user,
																session,
																appConstants.getUser(),
																appConstants.getUserPasscode(),
																requestId,
																rowValue);
			}
			
			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObjectShort.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/data:details", method = RequestMethod.POST)
	@Operation(	summary = "Loopback output from executing scripts in String or json format",
				description= "Push notification to web or thick client via web socket of multiple data rows (in bulk) ")
	public ResponseEntity<RestObjectShort>
	loopbackScriptDataDetails(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="session") final String session,
								@RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
								@RequestBody final List<RowValue> rowValues) {
		boolean isHttpSessionId = false;
		requestId = StringUtils.generateRequestId(requestId);
		UserShort uShort = authUtil.isUserAuthenticated(session);
		if(uShort.getBaseUrl().compareTo(ClusterDb.ownBaseUrl) == 0) {
			isHttpSessionId = WebSocketsWrapper.isUser(user);
        }
		try	{
			if(isHttpSessionId) {
				WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailScriptData, rowValues, ClusterDb.ownBaseUrl);
				WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
			}
			else {
				RestApiScriptingClient.loopbackScriptDataDetails(	uShort.getBaseUrl(),
																	user,
																	session,
																	appConstants.getUser(),
																	appConstants.getUserPasscode(),
																	requestId,
																	rowValues);
			}
			return RestObjectShort.retOKWithPayload("OK",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObjectShort.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/loopback/log:stdin", method = RequestMethod.POST)
	@Operation(	summary = "Loopback output from executing scripts in String or json format",
				description= "Push notification to web or thick client via web socket of log lines ")
	public ResponseEntity<RestObjectShort>
	loopbackScriptStdin(@RequestHeader(value="user") final String user,
						@RequestHeader(value="session") final String session,
						@RequestHeader(value="requestId", defaultValue = "") String requestId, /*from script:run or repo:run*/
						@RequestHeader(value="internalUser") final  String internalUser,
						@RequestHeader(value="internalPassword") final String internalPassword,
						@RequestHeader(value="baseUrl") final String baseUrl, /*source Url*/
						@RequestHeader(value="websocketMessageType") final String websocketMessageType, /* hs/ds/fs */
						@RequestBody final String line /*for header is script time, for footer is count*/) {
		requestId = StringUtils.generateRequestId(requestId);
		if( !authUtil.isInternalUserAuthenticated(internalUser, internalPassword) )	{
			return RestObjectShort.retAuthError(requestId);
		}

		boolean isHttpSessionId = false;
		try {
			isHttpSessionId = WebSocketsWrapper.isUser(user);
		} catch(Exception ignored) { }
		
		try	{
			if(isHttpSessionId) {
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
		} catch(Exception ex) {
			return RestObjectShort.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObjectShort.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	/*History */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/history/script:get", method = RequestMethod.GET)
	@Operation(	summary = "Get the list of executed scripts",
				description= "Get the history of executed scripts")
	public ResponseEntity<RestObject> 
	getScriptHist(@RequestHeader(value="user") final String user,
				  @RequestHeader(value="requestId", defaultValue = "") String requestId,
				  @RequestHeader(value="type") final String type,
				  @RequestHeader(value="interpreterName") final String interpreterName) {
		requestId = StringUtils.generateRequestId(requestId);
		HistScriptList ret;
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getScriptStoragePath();
			ret = HistScriptFileManagement.getScripts(mainFolder, type, interpreterName, userId);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/history/adhoc/script:copy", method = RequestMethod.POST)
	@Operation(	summary = "Copy historical adhoc scripts",
				description= "Copy ad-hoc scripts executed in the past to another user's profile")
	public ResponseEntity<RestObject>
	copyAdhocScriptHist(@RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="toUserId") final String toUserId,
						@RequestHeader(value="type") final String type,
						@RequestHeader(value="interpreterName") final String interpreterName,
						@RequestHeader(value="scriptName") final String scriptName,
						@RequestHeader(value="shaHash") final String shaHash) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long fromUserId = u.getId();
			String mainFolder = appConstants.getScriptStoragePath();
			HistScriptFileManagement.addExistingScriptToNewUser(fromUserId, mainFolder, type, interpreterName, Long.parseLong(toUserId), scriptName, shaHash);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex) {
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/history/repo/script:copy", method = RequestMethod.POST)
	@Operation(summary = "Copy Repo Scripts to another user",	description= "Copy a script from your profile to another user's profile")
	public ResponseEntity<RestObject> 
	copyRepoScriptHist( @RequestHeader(value="user") final String user,
					    @RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="toUserId") final String toUserId,
						@RequestHeader(value="interpreterId") final String interpreterId,
						@RequestHeader(value="scriptId") final String scriptId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long fromUserId = u.getId();
			String separator = FileSystems.getDefault().getSeparator();
			ScriptDetail si = scriptingInternalDb.getScript(Integer.parseInt(scriptId));
			String fromScriptVersionPath = appConstants.getScriptStoragePath() + separator + fromUserId + separator + si.getScriptName();
			File from = new File(fromScriptVersionPath);
			if(!from.exists()) {
				return RestObject.retOKWithPayload(new GenericResponse("The script Could not be found"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			
			String toScriptVersionPath = appConstants.getScriptStoragePath() + separator + toUserId + separator + si.getScriptName();
			File to = new File(toScriptVersionPath);
			if(to.exists()) {
				return RestObject.retOKWithPayload(new GenericResponse("The script already exists to user profile"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				FileUtilWrapper.copyFolder(fromScriptVersionPath, toScriptVersionPath);
				if(!to.exists()) {
					return RestObject.retOKWithPayload(new GenericResponse("The script could not be added to user profile"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				} else {
					scriptingInternalDb.scriptAdd(	Long.parseLong(toUserId) , si.getScriptName(), si.getMainFile(), si.getParamString(), "", "", Integer.parseInt(interpreterId), si.getScriptVersion());
					return RestObject.retOKWithPayload(new GenericResponse("The script has been added to user profile"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
					
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/history/script:remove", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete script from user profile",
				description= "Delete script from user profile. This is done by current user")
	public ResponseEntity<RestObject> 
	deleteScriptHist(	@RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="type") final String type,
						@RequestHeader(value="interpreterName") final String interpreterName,
						@RequestHeader(value="scriptName") final String scriptName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getScriptStoragePath();
			boolean ret = HistScriptFileManagement.deleteScript(mainFolder, type, interpreterName, userId, scriptName);
			return RestObject.retOKWithPayload(new GenericResponse(String.valueOf(ret)), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/scripting/script:download", method = RequestMethod.GET)
	@Operation(summary = "Download script",	description= "Download script in a browser")
	public ResponseEntity<Resource> 
	downloadScript(	@RequestHeader(value="user") final String user,
					@RequestHeader(value="scriptId") final String scriptId) {

		String separator = FileSystems.getDefault().getSeparator();
		User u = authUtil.getUser(user);
		long userId = u.getId();

		String zipFileName = StringUtils.generateUniqueString16(); 
		final String zipPath = appConstants.getScriptTempPath() + separator + userId + separator + zipFileName + ".zip";
				
		try {
			ScriptDetail scriptInfo = scriptingInternalDb.getScript(Integer.parseInt(scriptId));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Access-Control-Expose-Headers", "*");
			headers.add("fileName", scriptInfo.getScriptName() + ".zip");
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			String scriptVersionPath = appConstants.getScriptStoragePath() + separator + userId + separator + scriptInfo.getScriptName();
			File pathMainFile = new File(scriptVersionPath);
			if( ! pathMainFile.exists() ) {
				InputStreamResource resource = new InputStreamResource(new FileInputStream(""));
								
				ResponseEntity<Resource> ret = ResponseEntity.ok()	.headers(headers)
																	.contentLength(0)
																	.contentType(MediaType.parseMediaType("application/octet-stream"))
																	.body(resource);
				System.out.println(ret.toString());
			}
			
			ZipDirectory.zip(scriptVersionPath, zipPath);
			System.out.print("File to be downloaded: " + zipPath);
			byte[] f = FileUtilWrapper.readFile(zipPath);
			InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(f) );

            return ResponseEntity.ok()	.headers(headers)
										.contentLength(f.length)
										.contentType(MediaType.parseMediaType("application/octet-stream"))
										.body(resource);
			
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		} catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		} finally {
			FileUtilWrapper.deleteFile(zipPath);
		}
	}

}
