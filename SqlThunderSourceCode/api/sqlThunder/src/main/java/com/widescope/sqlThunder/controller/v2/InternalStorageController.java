package com.widescope.sqlThunder.controller.v2;



import java.io.File;
import java.io.FileInputStream;
import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.AccessRefPrivilege;
import com.widescope.persistence.execution.ExecutionUserAccess;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistFileManagement;
import com.widescope.sqlThunder.objects.commonObjects.globals.ListOfStrings;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;




@CrossOrigin
@RestController
@Schema(title = "Internal Backup Storage Controller")
public class InternalStorageController  {

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private InternalStorageRepoDb internalStorageRepoDb;

	@Autowired
	private HistFileManagement histFileManagement;

	@PostConstruct
	public void initialize() {
		
	}

	/* Backup Metadata */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/backup:create", method = RequestMethod.PUT)
	@Operation(summary = "Create backup before checking in any file to it")
	public ResponseEntity<RestObject>
	createBackup(@RequestHeader(value="user") final String user,
				 @RequestHeader(value="requestId", defaultValue = "") String requestId,
				 @RequestHeader(value="machineName") final String machineName,
				 @RequestHeader(value="storageType") final String storageType,
				 @RequestHeader(value="groupId") final long groupId,
				 @RequestHeader(value="source") final String source,
				 @RequestHeader(value="fullPath") final String fullPath,
				 @RequestBody final String comment) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		long timeStart = DateTimeUtils.millisecondsSinceEpoch();
		long userId = authUtil.getUser(user).getId();
		try {
			BackupStorage f = new BackupStorage(userId, machineName, requestId, storageType, comment, groupId, source, fullPath, timeStart, -1);
			internalStorageRepoDb.addNewBackup(f);
			return RestObject.retOKWithPayload(internalStorageRepoDb.getBackupStorage(f), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/backup:end", method = RequestMethod.POST)
	@Operation(summary = "End backup previously started, by setting ending time in the database")
	public ResponseEntity<RestObject>
	endBackup(@RequestHeader(value="requestId", defaultValue = "") String requestId,
			  @RequestHeader(value="backupId") final long backupId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		try {
			internalStorageRepoDb.endBackupStorage(backupId, timeStamp);
			BackupStorage ret = internalStorageRepoDb.getBackupStorageById(backupId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/backup:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete backup with all associate files and privileges to users")
	public ResponseEntity<RestObject>
	deleteBackup(@RequestHeader(value="user") final String user,
				 @RequestHeader(value="requestId", defaultValue = "") String requestId,
				 @RequestHeader(value="backupId") final long backupId,
				 @RequestHeader(value="force") final boolean force) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			boolean isSuccess = false;
			User u = authUtil.getUser(user);
			AccessRefPrivilege p = ExecutionUserAccess.getArtifactAccessByUser( backupId,
																				u.getId(),
																				internalStorageRepoDb.getJDBC_DRIVER(),
																				internalStorageRepoDb.getDB_URL_DISK(),
																				internalStorageRepoDb.getUSER(),
																				internalStorageRepoDb.getPASS());

			if(p.getUserId() == u.getId() && p.getBackupId() == backupId) {
				long cnt = ExecutionUserAccess.countArtifactAccess(	backupId,
																	internalStorageRepoDb.getJDBC_DRIVER(),
																	internalStorageRepoDb.getDB_URL_DISK(),
																	internalStorageRepoDb.getUSER(),
																	internalStorageRepoDb.getPASS());
				if( force || cnt <= 1 ) {
					final BackupStorage backup = internalStorageRepoDb.getBackupById(backupId);
					histFileManagement.deleteFolderWithContent(backup);
					ExecutionUserAccess.deleteAccessByArtifactId(	backupId,
																	internalStorageRepoDb.getJDBC_DRIVER(),
																	internalStorageRepoDb.getDB_URL_DISK(),
																	internalStorageRepoDb.getUSER(),
																	internalStorageRepoDb.getPASS());
					isSuccess = true;
				}
			}

			return RestObject.retOKWithPayload(new GenericResponse(isSuccess), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/backups:get", method = RequestMethod.GET)
	@Operation(summary = "Get a list of executed backups for a certain machine in a time range")
	public ResponseEntity<RestObject>
	getBackups(@RequestHeader(value="user") final String user,
			   @RequestHeader(value="requestId", defaultValue = "") String requestId,
			   @RequestHeader(value="machineName") final String machineName,
			   @RequestHeader(value="timeRangeStart") final long timeRangeStart,
			   @RequestHeader(value="timeRangeEnd") final long timeRangeEnd
			   ) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		long userId = authUtil.getUser(user).getId();
		try {
			BackupStorageList ret = internalStorageRepoDb.getBackupStorageByMachineNameAndUser(userId, machineName, timeRangeStart, timeRangeEnd);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}








	/*Backup Files*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/file:upload",
					method = RequestMethod.POST,
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
					)
	@Operation(summary = "Upload a generic file, on internal storage, that belong to a backup. A backup has to be created prior to this operation")
	public ResponseEntity<RestObject> 
	uploadFile(@RequestHeader(value="requestId", defaultValue = "") String requestId,
			   @RequestHeader(value="backupId") long backupId,
			   @RequestHeader(value="function") final String function,
			   @RequestParam("file") final MultipartFile file) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		try {
			final String backupName = StringUtils.generateUniqueString(16);
			final String fileName = file.getOriginalFilename();
			final String path = file.getResource().getFile().getAbsoluteFile().getPath();
			final long lastModified = file.getResource().lastModified();
			InternalFileStorageRecord fRec
			= new InternalFileStorageRecord(-1, backupId, backupName, fileName, path, file.getSize(), lastModified, timeStamp, file.getContentType(), function);
			String fullFilePath = histFileManagement.addNewArtifactFromMultipartFile(fRec, file);
			fRec.setFullFilePath(fullFilePath);
			internalStorageRepoDb.addFileToBackup(fRec);
			return RestObject.retOKWithPayload(fullFilePath, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/file:download", method = RequestMethod.GET)
	@Operation(summary = "Download file from a specific backup id")
	public ResponseEntity<Resource>
	downloadFile(@RequestHeader(value="user") final String user,
				 @RequestHeader(value="storageId") final long storageId,
				 @RequestHeader(value="backupId") final long backupId)  /*used for security purposes*/
	{
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Access-Control-Expose-Headers", "*");
			headers.add("storageId", Long.toString(storageId) );
			headers.add("backupId", Long.toString(backupId) );
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			long userId = authUtil.getUser(user).getId();
			InternalFileStorageRecord f = internalStorageRepoDb.getFileById(backupId, storageId);
			File file2Download = new File(histFileManagement.getFullPath(f));

			InputStreamResource resource = new InputStreamResource(new FileInputStream(file2Download));
			return ResponseEntity.ok()	.headers(headers)
										.contentLength(file2Download.length())
										.contentType(MediaType.parseMediaType("application/pdf"))
										.body(resource);
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		} catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/file:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete backed up file belonging to a backup, when not needed anymore. ")
	public ResponseEntity<RestObject>
	deleteBackedUpFile(@RequestHeader(value="user") final String user,
					   @RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="backupId") final long backupId,
					   @RequestHeader(value="storageId") final long storageId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			User u = authUtil.getUser(user);
			InternalFileStorageRecord ret = internalStorageRepoDb.getFileById(backupId, storageId);
			ExecutionUserAccess.deleteAccessByArtifactId(   backupId,
															internalStorageRepoDb.getJDBC_DRIVER(),
															internalStorageRepoDb.getDB_URL_DISK(),
															internalStorageRepoDb.getUSER(),
															internalStorageRepoDb.getPASS());

			internalStorageRepoDb.deleteFileRecord(ret.getStorageId(), u.getId()); /*Delete File record by owner*/
			histFileManagement.deleteArtifact(ret);  /*Delete file*/
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/backup:list", method = RequestMethod.GET)
	@Operation(summary = "List all files in internal storage for certain backup")
	public ResponseEntity<RestObject> 
	listBackupFiles(@RequestHeader(value="user") final String user,
					@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="backupId") long backupId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			long userId = authUtil.getUser(user).getId();
			BackupStorage b = internalStorageRepoDb.getBackupStorageByIdAndUser(backupId, userId);
			InternalFileStorageList ret =  internalStorageRepoDb.getAllBackupFiles(b.getId());
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/machines:get", method = RequestMethod.GET)
	@Operation(summary = "Get a list of machines for which backups have been performed")
	public ResponseEntity<RestObject>
	getMachines(@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			ListOfStrings ret = internalStorageRepoDb.getMachines();;
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


}
