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


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import com.widescope.chat.db.ChatDb;
import com.widescope.chat.users.ChatUser;
import com.widescope.chat.users.OnOffLineThreadToFriends;
import com.widescope.chat.users.UserToChat;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.UserStatus;
import com.widescope.sqlThunder.utils.security.EncryptionAES;
import com.widescope.sqlThunder.utils.user.*;
import com.widescope.storage.internalRepo.InternalStorageRepoDb;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.scripting.ScriptingSharedData;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedWrapper;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@Schema(title = "User Control")
public class UserController {

	@Autowired
	private AppConstants appConstants;

	@Autowired
	private InternalStorageRepoDb storageRepoDb;

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private ChatDb chatDb;


	@PostConstruct
	public void initialize() {
		
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:login", method = RequestMethod.POST)
	@Operation(summary = "Authenticate user and generate session on success")
	public ResponseEntity<RestObject> 
	login(	@RequestHeader(value="user") String user,
			@RequestHeader(value="password") String userPasscode,
			@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="pns", required = false, defaultValue = "") String pns,
			@RequestHeader(value="deviceToken", required = false, defaultValue = "") String deviceToken,
			@RequestBody (required=false) String authBody)  {

		String decUserPasscode = EncryptionAES.decryptText(userPasscode,  appConstants.getEncryptionKey());
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		User u = authUtil.isUserAuthenticated(user.toLowerCase(), decUserPasscode, authBody, ClusterDb.ownBaseUrl, pns, deviceToken);
		if( u == null)	{
			return RestObject.retAuthError(requestId);
		} else if(u.getActive().equalsIgnoreCase("N")) {
			RestObject transferableObject = new RestObject(	new GenericResponse("ACCOUNT_LOCKED"), 
															requestId, 
															"Account is locked/deactivated",  
															"Account is locked/deactivated", 
															ErrorCode.ERROR, 
															Sources.SQLTHUNDER, 
															ErrorSeverity.LOW, 
															methodName);



			return new ResponseEntity<> (transferableObject, HttpStatus.OK);
		} else {

			/*Send notifications to friends that I'm on-line now
			 * Also, send me notifications about the status of each of my friends
			 * */
			try {
				UserToChat uToChat = new UserToChat(u);
				List <ChatUser> chatUsers = chatDb.getUsersTo(u.getUser());
				List<User> friends = authUtil.getUsers(chatUsers.stream().map(ChatUser::getToId).collect(Collectors.toList()));
				new OnOffLineThreadToFriends(friends, uToChat).start();
				//new OnOffLineThreadFromFriends(friends, uToChat).start();
			} catch (Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}

			RestObject transferableObject = new RestObject(u, methodName, requestId);
			return new ResponseEntity<> (transferableObject, HttpStatus.OK);
		}
		
		
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/mobile/user:login", method = RequestMethod.POST)
	@Operation(summary = "Authenticate mobile user and generate session on success")
	public ResponseEntity<RestObject>
	loginMobil( @RequestHeader(value="user") String user,
				@RequestHeader(value="password") String userPasscode,
				@RequestHeader(value="mobileKey") String mobileKey,
				@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="pns") String pns,
				@RequestHeader(value="deviceToken") String deviceToken,
				@RequestBody (required=false) String authBody)  {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String base64Credentials = mobileKey.substring("Basic ".length());
		byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
		String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);
		String[] parts = credentials.split(":");
		String username = parts[0];
		String password = parts[1];
		final String mobileUser = appConstants.getMobileUser();
		final String mobilePassword = appConstants.getMobilePassword();
		if(!username.equals(mobileUser) && !password.equals(mobilePassword)) {
			return RestObject.retAuthError(requestId);
		}
		User u = authUtil.isUserAuthenticated(user.toLowerCase(), userPasscode, authBody, ClusterDb.ownBaseUrl, pns, deviceToken);
		if( u == null)	{
			return RestObject.retAuthError(requestId);
		} else if(u.getActive().equalsIgnoreCase("N")) {
			RestObject transferableObject = new RestObject(	new GenericResponse("ACCOUNT_LOCKED"),
															requestId,
															"Account is locked/deactivated",
															"Account is locked/deactivated",
															ErrorCode.ERROR,
															Sources.SQLTHUNDER,
															ErrorSeverity.LOW,
															methodName);



			return new ResponseEntity< RestObject > (transferableObject, HttpStatus.OK);
		} else {

			/*Send notifications to friends that I'm on-line now
			 * Also, send me notifications about the status of each of my friends
			 * */
			try {
				UserToChat uToChat = new UserToChat(u);
				List <ChatUser> chatUsers = chatDb.getUsersTo(u.getUser());
				List<User> friends = authUtil.getUsers(chatUsers.stream().map(ChatUser::getToId).collect(Collectors.toList()));
				new OnOffLineThreadToFriends(friends, uToChat).start();
				//new OnOffLineThreadFromFriends(friends, uToChat).start();
			} catch (Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}

			RestObject transferableObject = new RestObject(u, methodName, requestId);
			return new ResponseEntity<> (transferableObject, HttpStatus.OK);
		}


	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:logout", method = RequestMethod.POST)
	@Operation(summary = "Logouts")
	public ResponseEntity<RestObject> 
	logout(	@RequestHeader(value="user") String user,
			@RequestHeader(value="session") String session,
			@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="deviceToken", required = false, defaultValue = "") String deviceToken) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean isSession = authUtil.isSessionAuthenticated(user, session);

		/*Send notifications to friends that I'm off-line now */
		try {
			User u = authUtil.getUser(user);
			UserToChat uToChat = new UserToChat(u);
			uToChat.setIsOn("N");
			List <ChatUser> chatUsers = chatDb.getUsersTo(u.getUser());
			List<User> friends = authUtil.getUsers(chatUsers.stream().map(ChatUser::getToId).collect(Collectors.toList()));
			new OnOffLineThreadToFriends(friends, uToChat).start();

			/*Send back disconnect signal*/
			WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
																u.getUser(),
																u.getUser(),
																WebsocketMessageType.socketDisconnect,
																u,
																ClusterDb.ownBaseUrl);

			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);

		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
		}

		WebSocketsWrapper.removeUser(user);
		if(isSession) {
			authUtil.removeSession(user, session);
			if(InternalUserDb.loggedUserDevices.containsKey(user) && !deviceToken.isEmpty()) {
				InternalUserDb.loggedUserDevices.get(user).remove(deviceToken);
			}


			try {
				EmbeddedWrapper.removeInMemDbSessionId(session);
				ScriptingSharedData.removeSessionData(session);
			} catch(Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}
		return RestObject.retOK(requestId, methodName);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:check", method = RequestMethod.GET)
	@Operation(summary = "Check if I'm connected")
	public ResponseEntity<RestObject>
	checkUser(@RequestHeader(value="requestId") String requestId, @RequestHeader(value="checkedUser") String checkedUser) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean isSocket = WebSocketsWrapper.isUser(checkedUser);
		boolean isUserSession = authUtil.isUserSession(checkedUser);
		UserStatus us = new UserStatus(checkedUser, isUserSession, isSocket);
		return RestObject.retOKWithPayload(us, requestId, methodName);
	}




	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users:cleanup", method = RequestMethod.POST)
	@Operation(summary = "Cleanup")
	public ResponseEntity<String> 
	cleanup(@RequestHeader(value="requestId") String requestId, @RequestHeader(value="session") String session) {
		if(!authUtil.getLocalSession().isBlank() && authUtil.getLocalSession().compareTo(session) == 0 ) {
			authUtil.cleanupIdleSessions();
		}
		AuthUtil.setLocalSession("");
		return new ResponseEntity< String > ("OK", HttpStatus.OK);
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/department:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new department")
	public ResponseEntity<RestObject> 
	addDepartment(	@RequestHeader(value="requestId") String requestId,
				  	@RequestHeader(value="newDepartment") String newDepartment,
				  	@RequestHeader(value="newDepartmentDescription") String newDepartmentDescription )	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newDepartment = WordUtils.capitalizeFully(newDepartment);
			newDepartmentDescription = WordUtils.capitalizeFully(newDepartmentDescription);
			Department department = authUtil.addDepartment(new Department( newDepartment, newDepartmentDescription) );
			if(department == null) {
				return RestObject.retException(requestId, methodName, "This Department cannot be added");
			}
			return RestObject.retOKWithPayload(department, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/department:update", method = RequestMethod.POST)
	@Operation(summary = "Update department")
	public ResponseEntity<RestObject> 
	updateDepartment(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="id") String id,
					  	@RequestHeader(value="newDepartment") String newDepartment,
					  	@RequestHeader(value="newDepartmentDescription") String newDepartmentDescription)	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newDepartment = WordUtils.capitalizeFully(newDepartment);
			newDepartmentDescription = WordUtils.capitalizeFully(newDepartmentDescription);
			
			Department department = authUtil.updateDepartment(new Department( Integer.parseInt(id), newDepartment, newDepartmentDescription) );
			if(department == null) {
				return RestObject.retException(requestId, methodName, "Department cannot be updated", "Department cannot be updated");
			}
			return RestObject.retOKWithPayload(department, requestId, methodName);

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/title:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new title")
	public ResponseEntity<RestObject> 
	addTitle(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="newTitle") String newTitle,
				@RequestHeader(value="newTitleDescription") String newTitleDescription) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newTitle = WordUtils.capitalizeFully(newTitle);
			newTitleDescription = WordUtils.capitalizeFully(newTitleDescription);
			Title title = authUtil.addTitle(new Title( newTitle, newTitleDescription) );
			if(title == null) {
				return RestObject.retException(requestId, methodName, "Error creating new Title", "Error creating new Title");
			}
			return RestObject.retOKWithPayload(title, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/title:update", method = RequestMethod.POST)
	@Operation(summary = "Update title")
	public ResponseEntity<RestObject> 
	updateTitle(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="id") String id,
				@RequestHeader(value="newTitle") String newTitle,
				@RequestHeader(value="newTitleDescription") String newTitleDescription) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newTitle = WordUtils.capitalizeFully(newTitle);
			newTitleDescription = WordUtils.capitalizeFully(newTitleDescription);
			
			boolean isOK = authUtil.updateTitle(new Title( Integer.parseInt(id), newTitle, newTitleDescription) );
			if(!isOK) {
				return RestObject.retException(requestId, methodName, "This Title cannot be updated");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new user")
	public ResponseEntity<RestObject> 
	addUser(@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="newUser") String newUser,
			@RequestHeader(value="newUserPassword") String newUserPassword,
			@RequestHeader(value="newUserType") String newUserType,
			@RequestHeader(value="newUserFirstName") String newUserFirstName,
			@RequestHeader(value="newUserLastName") String newUserLastName,
			@RequestHeader(value="newUserEmail") String newUserEmail,
			@RequestHeader(value="newUserDepartment") String newUsereDepartment,
			@RequestHeader(value="newUserTitle") String newUserTitle,
			@RequestHeader(value="newUserManager") String newUserManager,
			@RequestHeader(value="newUserCharacteristic") String newUserCharacteristic,
			@RequestHeader(value="newUserDescription") String newUserDescription) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

		try	{
			newUser = WordUtils.capitalizeFully(newUser);
			newUserFirstName = WordUtils.capitalizeFully(newUserFirstName);
			newUserLastName = WordUtils.capitalizeFully(newUserLastName);
			newUserEmail = newUserEmail.toLowerCase();
			newUserCharacteristic = WordUtils.capitalizeFully(newUserCharacteristic);
			newUserDescription = WordUtils.capitalizeFully(newUserDescription);
			
			User u = authUtil.addUserWithReturn(	new User(newUserType, 
														newUser, 
														newUserPassword, 
														newUserFirstName, 
														newUserLastName, 
														newUserEmail,
														Integer.parseInt(newUsereDepartment) ,
														Integer.parseInt(newUserTitle),
														Integer.parseInt(newUserManager),
														newUserCharacteristic,
														newUserDescription,
														"Y"));
			if(u != null) {
				u.setPassword(null);
				return RestObject.retOKWithPayload(u, requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Error adding user", "Error adding user");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:register", method = RequestMethod.PUT)
	@Operation(summary = "Register user")
	public ResponseEntity<RestObject> 
	registerUser(	@RequestHeader(value="newUser") String newUser,
					@RequestHeader(value="newUserPassword") String newUserPassword,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="newUserType") String newUserType,
					@RequestHeader(value="newUserFirstName") String newUserFirstName,
					@RequestHeader(value="newUserLastName") String newUserLastName,
					@RequestHeader(value="newUserEmail") String newUserEmail) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newUserFirstName = WordUtils.capitalizeFully(newUserFirstName);
			newUserLastName = WordUtils.capitalizeFully(newUserLastName);
			
			boolean ret = authUtil.registerUser(new User(newUserType.toUpperCase(), 
												newUser.toLowerCase(), 
												newUserPassword, 
												newUserFirstName, 
												newUserLastName, 
												newUserEmail.toLowerCase(),
												-1 ,
												-1,
												-1,
												"",
												"",
												""));
			if(ret) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Cannot register user", "Cannot register user");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:approve", method = RequestMethod.PUT)
	@Operation(summary = "Approve a registering used")
	public ResponseEntity<RestObject>
	approveRegisteringUser(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="newUser") String newUser,
							@RequestHeader(value="departmentId") String departmentId,
							@RequestHeader(value="titleId") String titleId,
							@RequestHeader(value="managerId") String managerId,
							@RequestHeader(value="characteristic") String characteristic,
							@RequestHeader(value="description") String description) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{

			boolean u = authUtil.approveRegisteringUser(newUser,
														departmentId,
														titleId,
														managerId,
														characteristic,
														description,
														"Y");
			if(u) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Error Approving Process");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:reject", method = RequestMethod.PUT)
	@Operation(summary = "Reject a registering used")
	public ResponseEntity<RestObject> 
	rejectRegisteringUser(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="newUser") String newUser) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			boolean u = authUtil.rejectRegisteringUser(	newUser);
			if(u) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Error registering");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:update", method = RequestMethod.POST)
	@Operation(summary = "Update user")
	public ResponseEntity<RestObject> 
	updateUser(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="id") String id,
				@RequestHeader(value="newUser") String newUser,
				@RequestHeader(value="newUserPassword") String newUserPassword,
				@RequestHeader(value="newUserType") String newUserType,
				@RequestHeader(value="newUserFirstName") String newUserFirstName,
				@RequestHeader(value="newUserLastName") String newUserLastName,
				@RequestHeader(value="newUserEmail") String newUserEmail,
				@RequestHeader(value="newUserDepartment") String newUserDepartment,
				@RequestHeader(value="newUserTitle") String newUserTitle,
				@RequestHeader(value="newUserManager") String newUserManager,
				@RequestHeader(value="newUserCharacteristic") String newUserCharacteristic,
				@RequestHeader(value="newUserDescription") String newUserDescription,
				@RequestHeader(value="newUserActive") String newUserActive) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newUser = WordUtils.capitalizeFully(newUser);
			newUserFirstName = WordUtils.capitalizeFully(newUserFirstName);
			newUserLastName = WordUtils.capitalizeFully(newUserLastName);
			newUserEmail = WordUtils.capitalizeFully(newUserEmail);
			newUserCharacteristic = WordUtils.capitalizeFully(newUserCharacteristic);
			newUserDescription = WordUtils.capitalizeFully(newUserDescription);
			boolean isOk = authUtil.updateUser(new User(Integer.parseInt(id),
														newUserType, 
														newUser, 
														newUserPassword, 
														newUserFirstName, 
														newUserLastName, 
														newUserEmail,
														Integer.parseInt(newUserDepartment) ,
														Integer.parseInt(newUserTitle),
														Integer.parseInt(newUserManager),
														newUserCharacteristic,
														newUserDescription,
														newUserActive
														));
			if( !isOk ) {
				return RestObject.retException(requestId, methodName, "Cannot update user");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user/quick:update", method = RequestMethod.POST)
	@Operation(summary = "Update user")
	public ResponseEntity<RestObject> 
	quickUserUpdate(@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="id") String id,
					@RequestHeader(value="newUser") String newUser,
					@RequestHeader(value="newUserType") String newUserType,
					@RequestHeader(value="newUserDepartment") String newUsereDepartment,
					@RequestHeader(value="newUserTitle") String newUserTitle,
					@RequestHeader(value="newUserManager") String newUserManager,
					@RequestHeader(value="newUserCharacteristic") String newUserCharacteristic,
					@RequestHeader(value="newUserDescription") String newUserDescription,
					@RequestHeader(value="newUserActive") String newUserActive) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

		try	{
			newUserCharacteristic = WordUtils.capitalizeFully(newUserCharacteristic);
			newUserDescription = WordUtils.capitalizeFully(newUserDescription);
			
			boolean isOk = authUtil.quickUserUpdate(new User(Integer.parseInt(id),
														newUserType, 
														newUser, 
														"", 
														"", 
														"", 
														"",
														Integer.parseInt(newUsereDepartment) ,
														Integer.parseInt(newUserTitle),
														Integer.parseInt(newUserManager),
														newUserCharacteristic,
														newUserDescription,
														newUserActive
														));
			if( !isOk ) {
				return RestObject.retException(requestId, methodName, "Cannot update user");
			} else {
				return RestObject.retOK(requestId, methodName);
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:update-my-names", method = RequestMethod.POST)
	@Operation(summary = "Update my first name and last name")
	public ResponseEntity<RestObject> 
	updateMyNames(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="id") String id,
					@RequestHeader(value="newUserFirstName") String newUserFirstName,
					@RequestHeader(value="newUserLastName") String newUserLastName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			newUserFirstName = WordUtils.capitalizeFully(newUserFirstName);
			newUserLastName = WordUtils.capitalizeFully(newUserLastName);
			boolean isOk = authUtil.updateMe(Long.parseLong(id),
											newUserFirstName, 
											newUserLastName 
											);
			if( !isOk ) {
				return RestObject.retException(requestId, methodName, "Cannot update First Name and Last Name");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:update-my-password", method = RequestMethod.POST)
	@Operation(summary = "Update my password")
	public ResponseEntity<RestObject> 
	updateMyPassword(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="id") String id,
						@RequestHeader(value="password") String password) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			boolean isOk = authUtil.updateMyPassword(	Long.parseLong(id), password );
			if( !isOk ) {
				return RestObject.retException(requestId, methodName, "Cannot update password");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:update-my-email", method = RequestMethod.POST)
	@Operation(summary = "Update my first name and last name")
	public ResponseEntity<RestObject> 
	updateMyEmailAndUserName(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="id") String id,
								@RequestHeader(value="userName") String userName,
								@RequestHeader(value="email") String email) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			boolean isOk = authUtil.updateMyEmailUserName(	Long.parseLong(id),
															userName,
															email
															);
			if( !isOk ) {
				return RestObject.retException(requestId, methodName, "Error changing username and email");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete User")
	public ResponseEntity<RestObject> 
	deleteUser(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="id") String id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			if(authUtil.deleteUser(Integer.parseInt(id))) {
				/*Now delete anything related to this user*/
				storageRepoDb.deleteUserPrivs(Long.parseLong(id));
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Error Deleting user");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/department:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Department")
	public ResponseEntity<RestObject> 
	deleteDepartment(@RequestHeader(value="requestId") String requestId,
					 @RequestHeader(value="id") String id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			if(!authUtil.deleteDepartment(Integer.parseInt(id))) {
				return RestObject.retException(requestId, methodName, "This department cannot be deleted");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/title:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Title")
	public ResponseEntity<RestObject> 
	deleteTitle(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="id") String id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			if(!authUtil.deleteTitle(Integer.parseInt(id))) {
				return RestObject.retException(requestId, methodName, "This title cannot be deleted");
			} else {
				return RestObject.retOK(requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users:query", method = RequestMethod.GET)
	@Operation(summary = "Get Users")
	public ResponseEntity<RestObject> 
	getUsers(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="patternToSearch") String patternToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			List<User> listOfUsers = authUtil.getUsers(patternToSearch);
			UserList uList = new UserList(listOfUsers);
			return RestObject.retOKWithPayload(uList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/minus:query", method = RequestMethod.GET)
	@Operation(summary = "Get Users minus current")
	public ResponseEntity<RestObject> 
	getUsersMinusCurrent(	@RequestHeader(value="user") String user,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="patternToSearch") String patternToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			User u = authUtil.getUser(user);
			List<User> listOfUsers = authUtil.getUsersMinusUser(patternToSearch, u);
			UserList uList = new UserList(listOfUsers);
			return RestObject.retOKWithPayload(uList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users:registering", method = RequestMethod.GET)
	@Operation(summary = "Get Registering Users")
	public ResponseEntity<RestObject> 
	getRegisteringUsers(@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="patternToSearch") String patternToSearch) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			List<User> listOfUsers = authUtil.getRegisteringUsers(patternToSearch);
			UserList uList = new UserList(listOfUsers);
			return RestObject.retOKWithPayload(uList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/managers:query", method = RequestMethod.GET)
	@Operation(summary = "Get Managers")
	public ResponseEntity<RestObject> 
	getManagers(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="patternToSearch") String patternToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			List<ManagerShort> listOfUsers = authUtil.getManagers(patternToSearch);
			ManagerShortList uList = new ManagerShortList(listOfUsers);
			return RestObject.retOKWithPayload(uList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/user:get", method = RequestMethod.GET)
	@Operation(summary = "Get specific User based on id")
	public ResponseEntity<RestObject> 
	getUser(@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="id") String id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			User u = authUtil.getUser(Integer.parseInt(id));
			UserList uList = new UserList(u);
			return RestObject.retOKWithPayload(uList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/departments:query", method = RequestMethod.GET)
	@Operation(summary = "Search Departments")
	public ResponseEntity<RestObject> 
	getDepartments(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="patternToSearch") String patternToSearch) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			List<Department> listOfDepartments = authUtil.getDepartments(patternToSearch);
			DepartmentList dList = new DepartmentList(listOfDepartments);
			return RestObject.retOKWithPayload(dList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/department:get", method = RequestMethod.GET)
	@Operation(summary = "Get a specific Department, from an id")
	public ResponseEntity<RestObject> 
	getDepartment(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="id") String id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			Department dept = authUtil.getDepartmentById(Integer.parseInt(id));
			DepartmentList dList = new DepartmentList(dept);
			return RestObject.retOKWithPayload(dList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/titles:query", method = RequestMethod.GET)
	@Operation(summary = "Search Titles")
	public ResponseEntity<RestObject> 
	getTitles(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="patternToSearch") String patternToSearch) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			List<Title> listOfTitles = authUtil.getTitles(patternToSearch);
			TitleList dList = new TitleList(listOfTitles);
			return RestObject.retOKWithPayload(dList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/title:get", method = RequestMethod.GET)
	@Operation(summary = "Get a specific title, based on an id")
	public ResponseEntity<RestObject> 
	getTitleById(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="id") String id) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			Title title = authUtil.getTitle(Integer.parseInt(id));
			TitleList dList = new TitleList(title);
			return RestObject.retOKWithPayload(dList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/title:search", method = RequestMethod.GET)
	@Operation(summary = "Get Title by Name")
	public ResponseEntity<RestObject> 
	getTitleByName( @RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="title") String t) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			Title title = authUtil.getTitle(t);
			TitleList dList = new TitleList(title);
			return RestObject.retOKWithPayload(dList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/department:search", method = RequestMethod.GET)
	@Operation(summary = "Get Department by Name")
	public ResponseEntity<RestObject> 
	getDepartmentByName(@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="department") String d) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			Department department = authUtil.getDepartmentByName(d);
			DepartmentList dList = new DepartmentList(department);
			return RestObject.retOKWithPayload(dList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	@CrossOrigin(origins = "*") 
	@RequestMapping(value = "/users/generateSyntheticSession", method = RequestMethod.GET)
	@Operation(summary = "Generates a synthetic session for debug only in DEV and QA environment.")
	public ResponseEntity<RestObject>
	generateSyntheticSession(	@RequestHeader(value="admin") String admin,
								@RequestHeader(value="password") String password,
								@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if (appConstants.getSpringProfilesActive().equalsIgnoreCase("PROD")) {
			return RestObject.retAuthError(requestId);
		}

		if( !authUtil.isSessionAuthenticated(admin, password) )	{
			return RestObject.retAuthError(requestId);
		}

		String syntheticSession = UUID.randomUUID().toString();
		String session = StaticUtils.generateSecureString32();

		User userObject = new User(0, syntheticSession, syntheticSession, syntheticSession, syntheticSession, syntheticSession, null, 0, 0, 0, "", "", "Y");
		UserShort us = new UserShort(session, userObject);
		InternalUserDb.loggedUsers.put(session, us);
		return RestObject.retOKWithPayload(us, requestId, methodName);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/timer:subscribe", method = RequestMethod.GET)
	@Operation(summary = "50 milliseconds timer subscription.",	description= "Subscribe to 50 milliseconds timer generated from server to clients for sampling video/audio communication")
	public ResponseEntity<RestObject>
	subscribeToTimer(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="session") String session) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		InternalUserDb.timedUsers.remove(session);
		return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/users/timer:unsubscribe", method = RequestMethod.GET)
	@Operation(summary = "unsubscribe from 50 milliseconds timer.",	description= "Unsubscribe from 50 milliseconds timer generated from server to clients.")
	public ResponseEntity<RestObject>
	unsubscribeToTimer(	@RequestHeader(value="user") String user,
						@RequestHeader(value="session") String session,
						@RequestHeader(value="requestId") String requestId) {
		InternalUserDb.timedUsers.put(session, user);
		return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
	}


}