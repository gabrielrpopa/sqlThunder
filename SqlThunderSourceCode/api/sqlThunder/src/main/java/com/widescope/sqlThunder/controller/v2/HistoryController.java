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

import com.widescope.logging.AppLogger;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin
@RestController
@Schema(title = "History Control")
public class HistoryController {

	@Autowired
	private PersistenceWrap pWrap;

	@Autowired
	private AuthUtil authUtil;


	@PostConstruct
	public void initialize() {

	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/test:set",
					method = RequestMethod.PUT,
					consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(	summary = "Test save execution data", description= "Test save execution data")
	public ResponseEntity<RestObject>
	testSaveExecution(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					  @RequestHeader(value="record") final String record,
					  @RequestHeader(value="repoName") final String repoName,
					  @RequestHeader(value="persist", required = false, defaultValue = "Y") final String persist,
					  @RequestBody final byte[] o) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			RestInterface rec = pWrap.convert(record, repoName);
			rec = pWrap.saveExecution(rec, o, persist);
			return RestObject.retOKWithPayload(rec, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution:name", method = RequestMethod.GET)
	@Operation(	summary = "Check if artifact execution name exists already",
				description= "Check if new script, statement or file name exists already. This is useful if the user wants to create a unique name for the execution in order to categorize it")
	public Boolean
	isExecutedName(@RequestHeader(value="name") final String name,
				   @RequestHeader(value="repoName") final String repoName) {
		try {
			return pWrap.isExecutedName(repoName, name);
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return false;
		}
    }



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/filter:list", method = RequestMethod.GET)
	@Operation(	summary = "Get the list of executed artifacts",
				description= "Get the list of executed artifacts (script, statement or file)")
	public ResponseEntity<RestObject>
	getUserExecutedArtifactList(@RequestHeader(value="user") final String user,
								 @RequestHeader(value="requestId", defaultValue = "") String requestId,
								 @RequestHeader(value="repoName") final String repoName,
								 @RequestHeader(value="scriptName") final String scriptName,
								 @RequestHeader(value="src", defaultValue = "R") final String src) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();

		try {
			RestInterface ret = pWrap.getUserExecutedArtifactList (repoName, user, scriptName, src) ;
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access:set", method = RequestMethod.POST)
	@Operation(	summary = "Provide executed artifact access to another user",
				description= "Provide executed artifacts (script, statement or file) access to another user")
	public ResponseEntity<RestObject>
	giveExecutedArtifactAccessToUser(@RequestHeader(value="requestId", defaultValue = "") String requestId,
									  @RequestHeader(value="toUser") final String toUser,
									  @RequestHeader(value="repoName") final String repoName,
									  @RequestHeader(value="artifactId") final long artifactId) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			pWrap.giveExecutedArtifactAccessToUser(repoName, toUser, artifactId);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete access to executed script",
			description= "Delete access to executed script. If no other user is granted access to it, then entire execution is deleted")
	public ResponseEntity<RestObject>
	deleteExecutedArtifactAccess(	@RequestHeader(value="user") final String user,
									 @RequestHeader(value="requestId", defaultValue = "") String requestId,
									 @RequestHeader(value="repoName") final String repoName,
									 @RequestHeader(value="artifactId") final long artifactId) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			RestInterface ret = pWrap.deleteExecutedArtifactAccess (repoName, user, artifactId);
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/all:list", method = RequestMethod.GET)
	@Operation(	summary = "Get the list of executed artifacts",
				description= "Get the history of executed artifacts(script, statement or file)")
	public ResponseEntity<RestObject>
	getAllUserArtifactExecutionList(@RequestHeader(value="user") final String user,
							  		@RequestHeader(value="requestId", defaultValue = "") String requestId,
							  		@RequestHeader(value="repoName") final String repoName) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			RestInterface ret = pWrap.getAllArtifactExecutionList(repoName, user);
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}




	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/delete/all", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete all own executed scripts",
				description= "Delete all own executed scripts")
	public ResponseEntity<RestObject>
	deleteAllUserExecutedArtifacts(@RequestHeader(value="user") final String user,
								   @RequestHeader(value="requestId", defaultValue = "") String requestId,
								   @RequestHeader(value="repoName") final String repoName) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			RestInterface ret = pWrap.deleteAllUserExecutedArtifacts(repoName, user);
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/some:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete some executed scripts",
				description= "Delete some executed scripts.")
	public ResponseEntity<RestObject>
	deleteExecutedArtifacts(@RequestHeader(value="user") final String user,
							  @RequestHeader(value="requestId", defaultValue = "") String requestId,
							  @RequestHeader(value="repoName") final String repoName,
							  @RequestHeader(value="ids") final List<Long> ids,
							  @RequestHeader(value="force") final boolean force) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			RestInterface ret =  pWrap.deleteExecutedArtifacts(repoName, user, force, ids);
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/output:get", method = RequestMethod.GET)
	@Operation(	summary = "Get script output",
				description= "Get the output of the executed script")
	public ResponseEntity<RestObject>
	getExecutionOutput(@RequestHeader(value="user") final String user,
					   @RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="repoName") final String repoName,
					   @RequestHeader(value="artifactId") long artifactId)  {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			RestInterface ret =  pWrap.getExecutionOutput(repoName, artifactId);
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/output:remove", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete artifact from user profile",
			description= "Delete script, statement or file and associate output. This can only be done by original user")
	public ResponseEntity<RestObject>
	deleteExecutedOutput(@RequestHeader(value="user") final String user,
						   @RequestHeader(value="requestId", defaultValue = "") String requestId,
						   @RequestHeader(value="repoName") final String repoName,
						   @RequestHeader(value="artifactId") final long artifactId) {
		requestId = StringUtils.generateRequestId(requestId);
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			User u = authUtil.getUser(user);
			RestInterface ret =  pWrap.deleteOutputExecution(repoName, u.getId(), artifactId);
			return RestObject.retOKWithPayload(ret, requestId, method);
		} catch(Exception ex) {
			String log = AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return RestObject.retException(ex.getMessage(), requestId, method, log);
		}
	}



	/*Repo Groups*/

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/group:add", method = RequestMethod.PUT)
	@Operation(	summary = "Create Persistence Group for a certain repository",
			description= "Create Persistence Group for a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	createArtifactGroup(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="groupName") final String groupName,
						@RequestHeader(value="repoName") final String repoName,
						@RequestHeader(value="comment") final String comment) {
		try {
			pWrap.createArtifactGroup(repoName, groupName, comment);
			RestInterface ret =  pWrap.getArtifactGroup(repoName, groupName);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/group:get", method = RequestMethod.GET)
	@Operation(	summary = "Get Persistence Group for a certain repository",
			description= "Get Persistence Group for a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	getArtifactGroup(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="groupName") final String groupName,
						@RequestHeader(value="repoName") final String repoName) {
		try {
			RestInterface ret =  pWrap.getArtifactGroup(repoName, groupName);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/group:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete Persistence Group for a certain repository",
			description= "Delete Persistence Group for a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	deleteArtifactGroup( @RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="groupId") final long groupId,
							@RequestHeader(value="repoName") final String repoName) {
		try {
			pWrap.deleteArtifactGroup(repoName, groupId);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	/*Repo object Access */


	/*see PersistencePrivilege class for privilegeType*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access:add", method = RequestMethod.PUT)
	@Operation(	summary = "Add Object Access to a certain repository",
			description= "Add Object Access to a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	addArtifactAccess(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					  @RequestHeader(value="repoName") final String repoName,
					  @RequestHeader(value="objectId") final long objectId,
					  @RequestHeader(value="userId") final long userId,
					  @RequestHeader(value="privilegeType") final String privilegeType) {
		try {
			pWrap.addArtifactAccess(repoName, objectId, userId, privilegeType);
			RestInterface ret =  pWrap.getArtifactAccessById(repoName, objectId);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access:get", method = RequestMethod.GET)
	@Operation(	summary = "Get Object Access for a certain repository",
			description= "Get Object Access for a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	getArtifactAccessById(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						  @RequestHeader(value="objectId") final long objectId,
						  @RequestHeader(value="repoName") final String repoName) {
		try {
			RestInterface ret =  pWrap.getArtifactAccessById(repoName, objectId);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access/user:get", method = RequestMethod.GET)
	@Operation(	summary = "Get a list of Object Access by user for a certain repository",
			description= "Get a list of Object Access by user for a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	getArtifactAccessByUserId(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="userId") final long userId,
							@RequestHeader(value="repoName") final String repoName) {
		try {
			RestInterface ret =  pWrap.getArtifactAccessByUserId(repoName, userId);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access/object/user:get", method = RequestMethod.GET)
	@Operation(	summary = "Get Object Access by user on a certain repository",
			description= "Get Object Access by user on a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	getArtifactAccess(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="objectId") final long objectId,
					@RequestHeader(value="userId") final long userId,
					@RequestHeader(value="repoName") final String repoName) {
		try {
			RestInterface ret =  pWrap.getArtifactAccess(repoName, objectId, userId);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access/object:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete access to object to all users on a certain repository",
			description= "Delete access to object to all users on a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	deleteAccessByArtifactId(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="objectId") final long objectId,
							@RequestHeader(value="repoName") final String repoName) {
		try {
			pWrap.deleteAccessByArtifactId(repoName, objectId);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access/object/user:delete", method = RequestMethod.DELETE)
	@Operation(	summary = "Delete access to object to a user on a certain repository",
			description= "Delete access to object to a user on a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	deleteArtifactAccess( @RequestHeader(value="requestId", defaultValue = "") String requestId,
						  @RequestHeader(value="objectId") final long objectId,
						  @RequestHeader(value="userId") final long userId,
						  @RequestHeader(value="repoName") final String repoName) {
		try {
			pWrap.deleteArtifactAccessToUser(repoName, objectId, userId);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/history/execution/access/object:count", method = RequestMethod.GET)
	@Operation(	summary = "Get Count Object Access on a certain repository",
			description= "Get Count Object Access on a certain repository, such as Elasticsearch, MongoDb, Rdbms, Scripting or internal storage repository")
	public ResponseEntity<RestObject>
	countArtifactAccess(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="objectId") final long objectId,
						@RequestHeader(value="repoName") final String repoName) {
		try {
			long ret =  pWrap.countAccessRefTable(repoName, objectId);
			return RestObject.retOKWithPayload(new GenericResponse(ret), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


}
