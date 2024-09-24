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


import java.util.List;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.service.GeneralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.rest.about.DatabaseEnvironment;
import com.widescope.sqlThunder.rest.about.DatabaseEnvironmentList;
import com.widescope.sqlThunder.rest.about.LogContent;
import com.widescope.sqlThunder.rest.about.ServerEnvironment;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;



@CrossOrigin
@RestController
@Schema(title = "Environment Information")
public class EnvironmentController {


	@Autowired
	private AppConstants appConstants;
	
	@Autowired
	private GeneralService generalService;
	
	@Autowired
	private AuthUtil authUtil;

	@PostConstruct
	public void initialize() {
		
	}
		
	@RequestMapping(value = "/environment:about", method = RequestMethod.GET)
	@Operation(summary = "About this API")
	public ResponseEntity<RestObject> 
	about(	@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String appDesc = "Sql Thunder";
		try	{
			StaticUtils.getHostInfo();
			List<DatabaseEnvironment> listOfDatabases = generalService.getAllAvailableDatabases();
			DatabaseEnvironmentList databaseEnvironmentList = new DatabaseEnvironmentList(listOfDatabases);
			ServerEnvironment target = new ServerEnvironment(	databaseEnvironmentList, 
																null, 
																null, 
																appDesc, 
																appConstants.getSpringProfilesActive(), 
																null);
			return RestObject.retOKWithPayload(target, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}



	@RequestMapping(value = "/client:ip", method = RequestMethod.GET)
	@Operation(summary = "Get client IP Address")
	public ResponseEntity<RestObject>
	clientIpAddress(@RequestHeader(value="requestId") String requestId, HttpServletRequest request) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String ipAddress = request.getRemoteAddr();
		return RestObject.retOKWithPayload(ipAddress, requestId, methodName);
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/environment/log:query", method = RequestMethod.GET)
	@Operation(summary = "Get the log")
	public ResponseEntity<RestObject>
	getLog( @RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="stringToSearch") String stringToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			LogContent logContent = generalService.getLogContent(stringToSearch);
			return RestObject.retOKWithPayload(logContent, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/environment/be:version", method = RequestMethod.GET)
	@Operation(summary = "Get the backend version")
	public ResponseEntity<RestObject> 
	getBEVersion(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			return RestObject.retOKWithPayload(new GenericResponse("1.3.2"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
}
