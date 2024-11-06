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



import com.widescope.logging.AppLogger;
import com.widescope.rest.RestObject;
import com.widescope.restApi.repo.Objects.restApiRequest.UserRestApiRequest;
import com.widescope.restApi.repo.Objects.restApiRequest.UserRestApiRequestDetail;
import com.widescope.restApi.repo.Objects.restApiRequest.UserRestApiRequestDetailList;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.widescope.restApi.repo.RestApiDb;
import com.widescope.sqlThunder.utils.user.AuthUtil;

import java.util.UUID;

@CrossOrigin
@RestController
@Schema(title = "Rest Api Repo Control and Execution")
public class RestApiController {
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private RestApiDb restApiDb;


	@PostConstruct
	public void initialize() {

	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/restapi/requests:get", method = RequestMethod.GET)
	@Operation(summary = "Get all user saved requests")
	public ResponseEntity<RestObject>
	getAllRequests(	@RequestHeader(value="user") final String user,
					@RequestHeader(value="requestId", defaultValue = "") String requestId)	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			UserRestApiRequestDetailList userRestApiRequestDetailList
			= restApiDb.getUserRestApiRequests(authUtil.getUser(user).getId(), false);
			return RestObject.retOKWithPayload(userRestApiRequestDetailList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/restapi/request:get", method = RequestMethod.GET)
	@Operation(summary = "Get saved request")
	public ResponseEntity<RestObject> 
	getRequest(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="restApiId") final String restApiId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			UserRestApiRequestDetail userRestApiRequestDetail
			= restApiDb.getUserRestApiRequest(Integer.parseInt(restApiId));
			return RestObject.retOKWithPayload(userRestApiRequestDetail, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
 	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/restapi/request:save", method = RequestMethod.GET)
	@Operation(summary = "Save request")
	public ResponseEntity<RestObject> 
	saveRequest(@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="name") final String name,
				@RequestHeader(value="description") final String description,
				@RequestHeader(value="verbId") final String verbId,
				@RequestHeader(value="userRestApiRequest") final String userRestApiRequest) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			long restApiId = restApiDb.addUserRestApiRequest(	name, 
																description,
																Integer.parseInt(verbId),
																authUtil.getUser(user).getId(),
																UserRestApiRequest.toUserRestApiRequest(requestId));
			
			UserRestApiRequest restApiRequest = new UserRestApiRequest();
			return RestObject.retOKWithPayload(restApiRequest, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	
}
