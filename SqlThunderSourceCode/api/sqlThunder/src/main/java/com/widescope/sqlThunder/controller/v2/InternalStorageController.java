package com.widescope.sqlThunder.controller.v2;




import java.io.File;
import java.io.FileInputStream;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.rest.out_rest.FileList;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.InternalFileStorageList;
import com.widescope.storage.internalRepo.InternalFileStorageRecord;
import com.widescope.storage.internalRepo.InternalStoragePrivList;
import com.widescope.storage.internalRepo.InternalStorageRepoDb;
import com.widescope.storage.internalRepo.service.StorageService;
import com.widescope.storage.wrappers.DetectFileType;


@CrossOrigin
@RestController
@Schema(title = "File Upload/Download Information")
public class InternalStorageController {


	
	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private StorageService storageService;

	@Autowired
	private InternalStorageRepoDb storageRepoDb;

	@Autowired
	private	EmbeddedDbRepo embeddedDbRepo;

	@PostConstruct
	public void initialize() {
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/uploadFile", 
					method = RequestMethod.POST,
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
					)
	@Operation(summary = "Upload a generic file, on internal storage")
	public ResponseEntity<RestObject> 
	uploadFile(	@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="machineName") String machineName,
				@RequestHeader(value="fullPath") String fullPath,
				@RequestHeader(value="lastModified") final String lastModified,
				@RequestHeader(value="storageType") final String storageType,
				@RequestParam("file") final MultipartFile file) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		long lastModified_ = 0;
		String fileName = "";
		try { lastModified_ = Long.parseUnsignedLong(lastModified);	} catch(Exception ignored) {	}
		try { fileName = file.getResource().getFilename();	} catch(Exception ignored) {	}
		
		try {
			long userId = authUtil.getUser(user).getId();
			if(machineName.isEmpty() || machineName.isBlank()) {
				machineName = String.valueOf(userId);
			} 
			
			if(fullPath.isEmpty() || fullPath.isBlank()) {
				fullPath = "web";
			} 
		

			
			InternalFileStorageRecord f 
			= new InternalFileStorageRecord(-1,
											userId,
											machineName.toLowerCase(),
											fileName, 
											fullPath,
											storageType,
											lastModified_,
											DateTimeUtils.secondsSinceEpoch());
			
			String fullFilePath = storageService.store(file, machineName.toLowerCase(), fullPath.toLowerCase(), fileName, lastModified_);
			storageRepoDb.addFile(f);
			return RestObject.retOKWithPayload(fullFilePath, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/downloadFile", method = RequestMethod.GET)
	@Operation(summary = "Download file from internal storage")
	public ResponseEntity<Resource> 
	downloadFile(	@RequestHeader(value="machineName") final String machineName,
					@RequestHeader(value="fullPath") final String fullPath,
					@RequestHeader(value="filename") final String filename,
					@RequestHeader(value="lastModified") final String lastModified) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Access-Control-Expose-Headers", "*");
			headers.add("fileName", filename);
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			File file2Download = new File(StorageService.uploadFolderPath + "/" + machineName + "/" + fullPath + "/" + filename + "/" + lastModified);
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
	@RequestMapping(value = "/internalStorage/deleteFile", method = RequestMethod.DELETE)
	@Operation(summary = "Delete file from internal storage")
	public ResponseEntity<RestObject> 
	deleteFile(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="storageId") final String storageId,
				@RequestHeader(value="fileName") final String fileName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			InternalFileStorageRecord r = storageRepoDb.getFile(Long.parseLong(storageId) );
			if(!r.getFileName().equals(fileName)) {
				return RestObject.retException(requestId, methodName, "INCORRECT_FILE_NAME");
			}
			
			boolean isSuccess = storageService.delete(r.getMachineName(), r.getFullFilePath(), r.getFileName(), r.getLastModified() );
			if(isSuccess) {
				isSuccess = storageRepoDb.deleteFile(Long.parseLong(storageId));
			}
			if(isSuccess) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "ERROR_DELETE");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/list", method = RequestMethod.GET)
	@Operation(summary = "List all files in internal storage")
	public ResponseEntity<RestObject> 
	listFilesInFolder(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="appName") final String appName,
						@RequestHeader(value="folder") final String folder) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			FileList ret = new FileList(storageService.listFiles(appName.toLowerCase(), folder.toLowerCase()));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/user/list", method = RequestMethod.GET)
	@Operation(summary = "List all files in internal storage for current user")
	public ResponseEntity<RestObject> 
	getFilesByUser(	@RequestHeader(value="user") final String user,
					@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			long userId = authUtil.getUser(user).getId();
			InternalFileStorageList ret = storageRepoDb.getFiles( userId );
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/storage/priv", method = RequestMethod.GET)
	@Operation(summary = "Get user privileges for a stored file")
	public ResponseEntity<RestObject> 
	getFilePriv(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="storageId") final String storageId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			InternalStoragePrivList ret = storageRepoDb.getUsersAssociatedToFile( Long.parseLong(storageId) );
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	/*External / Exchange Users*/
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/user:add", method = RequestMethod.PUT)
	@Operation(summary = "Add internal user to a file uploaded by another internal user")
	public ResponseEntity<RestObject> 
	addUserToFile(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="storageId") final String storageId,
					@RequestHeader(value="userId") final String userId,
					@RequestHeader(value="privType") final String privType) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			storageRepoDb.mergePrivs(Long.parseLong(storageId),	Long.parseLong(userId),	privType);
			storageRepoDb.getPrivs(Long.parseLong(storageId),	Long.parseLong(userId));
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/user:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete user to a file link")
	public ResponseEntity<RestObject> 
	deleteUserToFile(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="storageId") final String storageId,
						@RequestHeader(value="userId") final String userId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			storageRepoDb.deletePrivs(Long.parseLong(storageId),	Long.parseLong(userId));
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/storage/type", method = RequestMethod.GET)
	@Operation(summary = "Get file type")
	public ResponseEntity<RestObject> 
	getFileType(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="storageId") final String storageId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			InternalFileStorageRecord rec = storageRepoDb.getFile(Long.parseLong(storageId));
			String fullPath= storageService.getFilePath(rec.getMachineName(), rec.getFullFilePath(), rec.getFileName(), rec.getLastModified());
			DetectFileType ret = DetectFileType.findOut(fullPath, rec);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/storage/attach-h2", method = RequestMethod.PUT)
	@Operation(summary = "Move and attach embedded db from storage")
	public ResponseEntity<RestObject> 
	moveAndAttachH2FromStorage(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="storageId") final String storageId,
								@RequestHeader(value="clusterId") final String clusterId) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			String pathCluster = H2Static.getClusterPath(Long.parseLong(clusterId) );
			InternalFileStorageRecord rec = storageRepoDb.getFile(Long.parseLong(storageId));
			String fullPathFrom= storageService.getFilePath(rec.getMachineName(), rec.getFullFilePath(), rec.getFileName(), rec.getLastModified());
			
			String dbName = rec.getFileName();
			
			EmbeddedDbRecordList r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId) );
			
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().toUpperCase().equals(dbName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord eRec = new EmbeddedDbRecord(-1,
															rec.getFileName(),
															"H2",
									                        u.getId(),
									                        Long.parseLong(clusterId),
									                        "",
									                        "");
				embeddedDbRepo.addEmbeddedDb(eRec);	
				FileUtilWrapper.moveFile(fullPathFrom, pathCluster + "/" + dbName);
			}

			EmbeddedDbRecordList ret = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/internalStorage/storage/import", method = RequestMethod.PUT)
	@Operation(summary = "Import files such as JSON tables or RestResponse into attached systems")
	public ResponseEntity<RestObject> 
	importFile( @RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="storageId") final String storageId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			InternalFileStorageRecord rec = storageRepoDb.getFile(Long.parseLong(storageId));
			String fullPath= storageService.getFilePath(rec.getMachineName(), rec.getFullFilePath(), rec.getFileName(), rec.getLastModified());
			DetectFileType ret = DetectFileType.findOut(fullPath, rec);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
}
