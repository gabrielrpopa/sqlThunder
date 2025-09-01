/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
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

/*
 * 
 * Examples https://github.com/eclipse/deeplearning4j-examples
 * 
 */




import java.util.List;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.scripting.db.InterpreterType;
import com.widescope.scripting.db.ScriptingInternalDb;
import com.widescope.sqlThunder.utils.user.AuthUtil;



@CrossOrigin
@RestController
@Schema(title = "Machine Learning Control and Execution")
public class MachineLearningController {

	
	@Autowired
	private ScriptingInternalDb scriptingInternalDb;


	@PostConstruct
	public void initialize() {
		
	}
		
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/model:run", method = RequestMethod.PUT)
	@Operation(summary = "Run Model")
	public ResponseEntity<RestObject>
	runModel(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="mlApiUniqueName") final String interpreterName,
				@RequestHeader(value="interpreterVersion") final String interpreterVersion) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> scriptInfo = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
																				
			if(scriptInfo.isEmpty()) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Interpreter and version already exists");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/mlApi:get", method = RequestMethod.PUT)
	@Operation(summary = "Get information about ML APi installed")
	public ResponseEntity<RestObject> 
	getAllMlApiServer(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="mlApiUniqueName") final String mlApiUniqueName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/mlApi:start", method = RequestMethod.PUT)
	@Operation(summary = "Start ML API Server")
	public ResponseEntity<RestObject> 
	startMlApiServer(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="interpreterName") final String interpreterName,
						@RequestHeader(value="interpreterVersion") final String interpreterVersion) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> scriptInfo = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
			if(scriptInfo.isEmpty()) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Interpreter and version already exists");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/mlApi:new", method = RequestMethod.PUT)
	@Operation(summary = "Create New ML API Server")
	public ResponseEntity<RestObject> 
	createNewMlApiServer(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="interpreterName") final String interpreterName,
							@RequestHeader(value="interpreterVersion") final String interpreterVersion) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> scriptInfo = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
			if(scriptInfo.isEmpty()) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Interpreter and version already exists");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/mlApi:stop", method = RequestMethod.PUT)
	@Operation(summary = "Stop ML API Server")
	public ResponseEntity<RestObject> 
	stopMlApiServer(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="interpreterName") final String interpreterName,
					@RequestHeader(value="interpreterVersion") final String interpreterVersion) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> scriptInfo = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
																									
			if(scriptInfo.isEmpty()) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Interpreter and version already exists");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/mlApi/deployment:add", method = RequestMethod.PUT)
	@Operation(summary = "Add ML API Stub")
	public ResponseEntity<RestObject> 
	addMlApiStub(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="interpreterName") final String interpreterName,
					@RequestHeader(value="interpreterVersion") final String interpreterVersion) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> scriptInfo = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
			if(scriptInfo.isEmpty()) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Interpreter and version already exists");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ml/mlApi/deployment:delete", method = RequestMethod.PUT)
	@Operation(summary = "Delete ML API Stub")
	public ResponseEntity<RestObject> 
	deleteMlApiStub(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="interpreterName") final String interpreterName,
					@RequestHeader(value="interpreterVersion") final String interpreterVersion) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<InterpreterType> scriptInfo = scriptingInternalDb.interpreterByNameAndVersion(interpreterName, interpreterVersion);
			if(scriptInfo.isEmpty()) {
				return RestObject.retOK(requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Interpreter and version already exists");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
}
