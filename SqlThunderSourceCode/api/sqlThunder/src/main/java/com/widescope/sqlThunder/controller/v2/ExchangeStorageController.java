package com.widescope.sqlThunder.controller.v2;



import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.widescope.logging.AppLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.widescope.rest.GenericResponse;
import com.widescope.rest.MimeTypes;
import com.widescope.rest.RestObject;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRepo;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDbRecord;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.restApiClient.ExchangeEndPointWrapper;
import com.widescope.sqlThunder.utils.security.SHA512Hasher;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.dataExchangeRepo.UserDbRecord;
import com.widescope.storage.dataExchangeRepo.UserToExchangeDbList;
import com.widescope.storage.dataExchangeRepo.UserToExchangeDbRecordExtendedList;
import com.widescope.storage.dataExchangeRepo.ExchangeDb;
import com.widescope.storage.dataExchangeRepo.ExchangeFileDbList;
import com.widescope.storage.dataExchangeRepo.ExchangeFileDbRecord;
import com.widescope.storage.dataExchangeRepo.ExchangeList;
import com.widescope.storage.dataExchangeRepo.ExchangeRecord;
import com.widescope.storage.dataExchangeRepo.FileDescriptorList;
import com.widescope.storage.dataExchangeRepo.UserDbList;
import com.widescope.storage.dataExchangeRepo.service.ExchangeStorageService;
import com.widescope.storage.wrappers.HttpRequestResponse;



@CrossOrigin
@RestController
@Schema(title = "Data Exchange Server Controller")
public class ExchangeStorageController {


	@Autowired
	private ExchangeStorageService exchangeStorageService;

	@Autowired
	private ExchangeDb exchangeDb;
	
	@Autowired
	private	EmbeddedDbRepo embeddedDbRepo;
	
	@Autowired
	private ConfigRepoDb configRepoDb;

	@Autowired
	private AuthUtil authUtil;

	
	@PostConstruct
	public void initialize() {
		
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/file/receive:remote", 
					method = RequestMethod.POST,
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
					)
	@Operation(summary = "Receive a file from")
	public ResponseEntity<RestObject> 
	receiveFilesFromRemoteExchange(	@RequestHeader(value="externalUserEmail") String externalUserEmail,
									@RequestHeader(value="externalExchangeUid") String externalExchangeUid,  /*for verification*/
									@RequestHeader(value="externalUserPassword") String externalUserPassword,
									@RequestHeader(value="toUserEmail") String toUserEmail,
									@RequestParam("files") MultipartFile[] files)  {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String requestId = StaticUtils.getUUID();

		try {
			if( exchangeDb.isUser(externalUserEmail, externalUserPassword) ) {
				return RestObject.retAuthError(requestId);
			}
			
			UserDbRecord fromUserDbRecord = exchangeDb.getUserByEmail(externalUserEmail);
			ExchangeRecord fromExchangeRecord = exchangeDb.getExchangeById(fromUserDbRecord.getExchangeId());
			if(externalExchangeUid.equals(fromExchangeRecord.getExchangeUid())) {
				return RestObject.retAuthError(requestId);
			}
			
			UserDbRecord toUserDbRecord = exchangeDb.getUserByEmail(toUserEmail);
			UserDbRecord userToExchange = exchangeDb.getUserByEmail(externalUserEmail);
			
			/*Check if toUser is associated to senders exchange*/
			boolean isUserToExchange = exchangeDb.isUserToExchange(userToExchange.getId(), fromExchangeRecord.getId());
			
			if(!isUserToExchange) { /*Check if user is allowed to drop files for this partner's exchange*/
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "** External User Not in the exchange 2");
				return RestObject.retAuthError(requestId);
			}
			
			ConfigRepoDbRecord owner = configRepoDb.getConfigRec("owner");
			ExchangeRecord ownExchange = exchangeDb.getOwnExchange(owner.getConfigValue());
			final int eid = ownExchange.getId();
			final long fid = fromUserDbRecord.getId();
			final long tid = toUserDbRecord.getId();

			for(MultipartFile file : files) {
				ExchangeFileDbRecord eRec = new ExchangeFileDbRecord(-1, eid, fid, tid, file.getName(), file.getContentType());
				exchangeStorageService.store(file, eRec, requestId);
			}

			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/file/upload:remote", 
					method = RequestMethod.POST,
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
					)
	@Operation(summary = "Upload a generic file, and send it to a remote exchange")
	public ResponseEntity<RestObject> 
	uploadFilesAndSendToRemote(	@RequestHeader(value="user") String user,
								@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="externalExchangeUid") String externalExchangeUid,
								@RequestHeader(value="externalUserPassword") String externalUserPassword,
								@RequestHeader(value="toUserEmail") String toUserEmail,
								@RequestHeader(value="remoteExchangeUrl") String remoteExchangeUrl,
								@RequestParam("file") MultipartFile[] files) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			ExchangeEndPointWrapper.uploadFilesToExternalExchange(	user,
																	externalExchangeUid, 
																	externalUserPassword, 
																	toUserEmail,
																	remoteExchangeUrl,
																	files);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/file/send:remote", method = RequestMethod.POST)
	@Operation(summary = "Send a file from this exchange/instance to a remote exchange/instance")
	public ResponseEntity<RestObject> 
	sendFileToRemoteExchange(	@RequestHeader(value="user") String user,
								@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="toExchangeId") String toExchangeId, 
								@RequestHeader(value="externalUserPassword") String externalUserPassword,
								@RequestHeader(value="toUserEmail") String toUserEmail,
								@RequestBody (required=false) FileDescriptorList fileDescriptorList)  {
		String uploadFileFromRemoteExchange = "/exchange/file/upload:remote";
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			ConfigRepoDbRecord owner = configRepoDb.getConfigRec("owner");
			ExchangeRecord fromExchangeUid = exchangeDb.getOwnExchange(owner.getConfigValue());
			Map<String, String> headers = new HashMap<>();
			headers.put("externalUserEmail", user);
			headers.put("externalExchangeUid", fromExchangeUid.getExchangeUid());
			headers.put("externalUserPassword", externalUserPassword);
			headers.put("toUserEmail", toUserEmail);
			ExchangeRecord toExchangeRecord = exchangeDb.getExchangeById( Integer.parseInt(toExchangeId));
			return HttpRequestResponse.uploadFileFromRemoteExchange(toExchangeRecord.getExchangeAddress() + uploadFileFromRemoteExchange,
                                                                    fileDescriptorList,
                                                                    headers);

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/file/hist:get", method = RequestMethod.GET)
	@Operation(summary = "Get List of Files from a saved Files Repo")
	public ResponseEntity<RestObject> 
	getSnapshot(@RequestHeader(value="user") String user,
				@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="startTime") String startTime,
				@RequestHeader(value="endTime") String endTime)  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			long userId = authUtil.getUser(user).getId();
			SnapshotDbRepo snp = new SnapshotDbRepo();
			SnapshotDbRecordList ret = snp.getUserSnapshotDb(	userId, 
																Long.parseLong(startTime),
																Long.parseLong(endTime));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/file/delete:local", method = RequestMethod.DELETE)
	@Operation(summary = "Delete a file on this Data Exchange Server or own file on remote")
	public ResponseEntity<RestObject> 
	deleteFileFromLocalRequest(	@RequestHeader(value="user") String user,
								@RequestHeader(value="session") String session,
								@RequestHeader(value="fileId") String fileId,
								@RequestHeader(value="requestId") String requestId)  {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			ConfigRepoDbRecord owner = configRepoDb.getConfigRec("owner");
			ExchangeRecord ownExchange = exchangeDb.getOwnExchange(owner.getConfigValue());
			UserDbRecord userToExchange = exchangeDb.getUserByEmail(user);
			boolean isUserToExchange = exchangeDb.isUserToExchange(userToExchange.getId(), ownExchange.getId());
			if(!isUserToExchange) { /*Check if user is allowed to drop files for this partner's exchange*/
				return RestObject.retAuthError(requestId);
			}
			
			ExchangeFileDbRecord  fRec = exchangeDb.getFile(Integer.parseInt(fileId));
			exchangeDb.deleteFile(Integer.parseInt(fileId));
			exchangeStorageService.delete(fRec, requestId);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/file/attach-h2", method = RequestMethod.PUT)
	@Operation(summary = "Move and attach embedded db from Exchange")
	public ResponseEntity<RestObject> 
	moveAndAttachH2FromExchange(@RequestHeader(value="user") String user,
								@RequestHeader(value="fileId") String fileId,
								@RequestHeader(value="clusterId") String clusterId,
								@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			String pathCluster = H2Static.getClusterPath(Long.parseLong(clusterId) );
			ExchangeFileDbRecord rec = exchangeDb.getFile(Long.parseLong(fileId));
			String fullPathFrom= exchangeStorageService.getFilePath(rec, requestId);
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
	

	
	/*Exchange Info operations*/

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/exchange:new", method = RequestMethod.PUT)
	@Operation(summary = "Add new remote exchange, local exchange can interact with")
	public ResponseEntity<RestObject> 
	addNewRemoteExchange(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="exchangeAddress") String exchangeAddress,
							@RequestHeader(value="exchangeName") String exchangeName,
							@RequestHeader(value="exchangeUid", required = false) String exchangeUid) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(exchangeUid == null || exchangeUid.isBlank() || exchangeUid.isEmpty()) {
			exchangeUid = StringUtils.generateUniqueString();
		}

		ExchangeRecord exchangeRecord  = new ExchangeRecord(-1,	exchangeAddress, exchangeName, exchangeUid);
		try	{
			exchangeDb.mergeExchange(exchangeRecord);
			exchangeRecord = exchangeDb.getExchangeId(exchangeRecord);
			return RestObject.retOKWithPayload(exchangeRecord, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/exchange:update", method = RequestMethod.POST)
	@Operation(summary = "Update remote exchange, local exchange can interact with. This end point will not update own exchange")
	public ResponseEntity<RestObject> 
	updateRemoteExchange(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="id") String id,
							@RequestHeader(value="exchangeAddress") String exchangeAddress,
							@RequestHeader(value="exchangeName") String exchangeName,
							@RequestHeader(value="exchangeUid", required = false) String exchangeUid) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(Integer.parseInt(id) == 1) {
			return RestObject.retException(requestId, methodName, " Cannot update own exchange. Only via change company name"  );
		}
		
		ExchangeRecord exchangeRecord = new ExchangeRecord(Integer.parseInt(id), exchangeAddress, exchangeName, exchangeUid);
		try	{
			exchangeDb.updateExchange(exchangeRecord);
			exchangeRecord = exchangeDb.getExchangeId(exchangeRecord);
			return RestObject.retOKWithPayload(exchangeRecord, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/exchange:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Get list of Sql Commands")
	public ResponseEntity<RestObject> 
	deleteRemoteExchange(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="exchangeUid", required = false) String exchangeUid) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			ExchangeRecord e = exchangeDb.getExchange(exchangeUid);
			if(e.getId() == 1) {
				return RestObject.retException(requestId, methodName, " Cannot delete own exchange"  );
			}
			exchangeDb.deleteExchange(exchangeUid);
			ExchangeRecord exchangeRecord = exchangeDb.getExchange(exchangeUid);
			return RestObject.retOKWithPayload(exchangeRecord, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/exchange:get", method = RequestMethod.GET)
	@Operation(summary = "Get exchange info")
	public ResponseEntity<RestObject> 
	getAllExchanges(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			ExchangeList eList = exchangeDb.getAllExchanges();
			return RestObject.retOKWithPayload(eList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/exchange/search:get", method = RequestMethod.GET)
	@Operation(summary = "Search exchanges")
	public ResponseEntity<RestObject> 
	searchExchanges(@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="exchange") String exchange) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			ExchangeList eList = exchangeDb.searchExchange(exchange);
			return RestObject.retOKWithPayload(eList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/exchange/associated:get", method = RequestMethod.GET)
	@Operation(summary = "Get exchange info")
	public ResponseEntity<RestObject> 
	getAssociatedExchanges(	@RequestHeader(value="user") String user,
							@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			ExchangeList eList = exchangeDb.getAssociatedExchanges(u.getId());
			return RestObject.retOKWithPayload(eList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	
	/** Users */
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/users/email:get", method = RequestMethod.GET)
	@Operation(summary = "Get users")
	public ResponseEntity<RestObject> 
	getExchangeUsers(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="email") String email) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			UserDbList u = exchangeDb.getUsers(email);
			return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/users/exchange:get", method = RequestMethod.GET)
	@Operation(summary = "Get users by Exchange")
	public ResponseEntity<RestObject> 
	getUsersByExchange(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="exchangeId") String exchangeId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			UserDbList u = exchangeDb.getUsersByExchange(Integer.parseInt(exchangeId));
			return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/users:get", method = RequestMethod.GET)
	@Operation(summary = "Get users associated to Exchange")
	public ResponseEntity<RestObject> 
	getAssociatedUsersToExchange(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="exchangeId") String exchangeId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			UserDbList u = exchangeDb.getUsersByAssociatedExchange(Integer.parseInt(exchangeId));
			return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/exchanges:get", method = RequestMethod.GET)
	@Operation(summary = "Get associated exchanges to a user")
	public ResponseEntity<RestObject> 
	getAssociatedExchangesToUser(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="userId") String userId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			ExchangeList u = exchangeDb.getAssociatedExchangesToUser(Long.parseLong(userId));
			return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/own:get", method = RequestMethod.GET)
	@Operation(summary = "Get own user info related to exchanges")
	public ResponseEntity<RestObject> 
	getCurrentUserInfo( @RequestHeader(value="user") String user,
						@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			UserDbRecord ur = exchangeDb.getUserByInternalUserId(u.getId());
			return RestObject.retOKWithPayload(ur, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user:add", method = RequestMethod.PUT)
	@Operation(summary = "Add new user to this exchange")
	public ResponseEntity<RestObject> 
	addNewExchangeUser(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="email") String email,
						@RequestHeader(value="exchangeId") String exchangeId,
						@RequestHeader(value="isAdmin") String isAdmin,
						@RequestHeader(value="userPassword") String userPassword) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String hashedPassword = SHA512Hasher.hash(userPassword);
		long internalUserId = -1;
		User internalUser = authUtil.getUser(email);
		if(internalUser != null) {
			internalUserId = internalUser.getId();
		}
		
		try	{
			UserDbRecord u = new UserDbRecord(	-1, internalUserId, email, Integer.parseInt(exchangeId) , isAdmin, hashedPassword);
			exchangeDb.addUser(u);
			u = exchangeDb.getUserByEmail(email);
			return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete user from this exchange")
	public ResponseEntity<RestObject> 
	deleteExchangeUser(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="id") String id) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			exchangeDb.deleteUser(Long.parseLong(id));
			exchangeDb.deleteUserToExchanges(Long.parseLong(id));
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user:update", method = RequestMethod.DELETE)
	@Operation(summary = "Update external users password")
	public ResponseEntity<RestObject> 
	updateExternalUserPasswordByAdmin(	@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="id") String id,
										@RequestHeader(value="password") String password) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			exchangeDb.updateUserPassword(Long.parseLong(id), password);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/self:update", method = RequestMethod.DELETE)
	@Operation(summary = "Update user from this exchange")
	public ResponseEntity<RestObject> 
	updateExternalUserPasswordByUser(	@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="externalUserEmail") String externalUserEmail,
										@RequestHeader(value="externalExchangeUid") String externalExchangeUid,  /*for verification*/
										@RequestHeader(value="externalUserPassword") String externalUserPassword,
										@RequestHeader(value="newExternalUserPassword") String newExternalUserPassword) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if( exchangeDb.isUser(externalUserEmail, externalUserPassword) ) {
				return RestObject.retAuthError(requestId);
			}
			/*Now check that user belongs to know exchange*/
			UserDbRecord u = exchangeDb.getUserByEmail(externalUserEmail);
			ExchangeRecord e = exchangeDb.getExchangeById(u.getExchangeId());
			if(!externalExchangeUid.equals(e.getExchangeUid())) {
				return RestObject.retAuthError(requestId);
			}
			
			exchangeDb.updateUserPassword(externalUserEmail, newExternalUserPassword);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	/*User to exchange */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/exchange:get", method = RequestMethod.GET)
	@Operation(summary = "Get user to exchange info")
	public ResponseEntity<RestObject> 
	getUserToExchanges(	@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			UserToExchangeDbList ret = exchangeDb.getUserToExchange();
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/exchange:getExtended", method = RequestMethod.GET)
	@Operation(summary = "Get user to exchange extended info")
	public ResponseEntity<RestObject> 
	getUserToExchangesExtended(	@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			UserToExchangeDbRecordExtendedList ret = exchangeDb.getUserToExchangeExtended();
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/exchange:add", method = RequestMethod.PUT)
	@Operation(summary = "Add new user to this exchange")
	public ResponseEntity<RestObject> 
	addUserToExchange(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="userId") String userId,
						@RequestHeader(value="exchangeId") String exchangeId,
						@RequestHeader(value="isAdmin", defaultValue = "N") String isAdmin) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long userId_ = Long.parseLong(userId);
			int exchangeId_ = Integer.parseInt(exchangeId);
			exchangeDb.mergeUserToExchange(userId_, exchangeId_, isAdmin);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/user/exchange:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete user from this exchange")
	public ResponseEntity<RestObject> 
	deleteUserToExchange(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="userId") String userId,
							@RequestHeader(value="exchangeId") String exchangeId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			exchangeDb.deleteUserToExchange(Long.parseLong(userId), Integer.parseInt(exchangeId));
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	/**Query files operations */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/local:get", method = RequestMethod.GET)
	@Operation(summary = "Query Files, sent or received")
	public ResponseEntity<RestObject> 
	queryExchange(	@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="externalUserPassword", required = false) String externalUserPassword,
					@RequestHeader(value="fromUserEmail", required = false) String fromUserEmail,
					@RequestHeader(value="toUserEmail") String toUserEmail) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			UserDbRecord requestingUser = exchangeDb.getUserByEmail(user);
			if(requestingUser.getIsAdmin().equals("N")) { fromUserEmail = user;	}
			if(fromUserEmail == null || fromUserEmail.isEmpty()) {	fromUserEmail = user; }
			
			ConfigRepoDbRecord owner = configRepoDb.getConfigRec("owner");
			/*Always toExchange its own exchange*/
			ExchangeRecord exchangeRecord = exchangeDb.getOwnExchange(owner.getConfigValue());
			ExchangeFileDbList ret;
			if(exchangeRecord.getId() == Integer.parseInt(requestId) ) {
				if(fromUserEmail == null || fromUserEmail.isBlank() || fromUserEmail.isEmpty()) {
					UserDbRecord u =exchangeDb.getUserByEmail(toUserEmail);
					ret = exchangeDb.getFilesForUser(u.getId() );
				} else {
					UserDbRecord uFrom =exchangeDb.getUserByEmail(fromUserEmail);
					UserDbRecord uTo =exchangeDb.getUserByEmail(toUserEmail);
					ret = exchangeDb.getFiles(uFrom.getId(), uTo.getId() );
				}
				
				return RestObject.retOKWithPayload(ret, requestId, methodName);
			} else {
				ExchangeRecord remoteExchangeRecord = exchangeDb.getExchangeById(Integer.parseInt(requestId));
				ret = HttpRequestResponse.queryFromRemoteExchange(remoteExchangeRecord.getExchangeAddress(), 
															user, externalUserPassword);
			}
			return RestObject.retOKWithPayload(ret, requestId, methodName);

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/remote:query", method = RequestMethod.POST)
	@Operation(summary = "Query Files from Remote Exchange")
	public ResponseEntity<RestObject> 
	queryFromRemoteExchange(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="externalUserEmail") String externalUserEmail,
							@RequestHeader(value="externalUserPassword") String externalUserPassword) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if( exchangeDb.isUser(externalUserEmail, externalUserPassword) ) {
				return RestObject.retAuthError(requestId);
			}

			UserDbRecord u = exchangeDb.getUserByEmail(externalUserEmail);
			ExchangeFileDbList ret = exchangeDb.getFilesForUser(u.getId());
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/*Generate UUID*/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/generate:uid", method = RequestMethod.PUT)
	@Operation(summary = "Generate uid")
	public ResponseEntity<RestObject> 
	generateUid(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String uid = StringUtils.generateUniqueString();
			return RestObject.retOKWithPayload(new GenericResponse(uid), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/exchange/generate:password", method = RequestMethod.PUT)
	@Operation(summary = "Generate strong password")
	public ResponseEntity<RestObject> 
	generateStrongPassword(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String strongPassword = StaticUtils.strongPasswordGenerator();
			return RestObject.retOKWithPayload(new GenericResponse(strongPassword), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
}
