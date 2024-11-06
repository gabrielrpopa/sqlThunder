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
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.widescope.rest.RestObject;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.DatabaseTypeList;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.rest.out_rest.DatabaseList;
import com.widescope.sqlThunder.utils.StaticUtils;






@CrossOrigin
@RestController
@Schema(title = "SqlRepoEnvironment Information")
public class SqlRepoEnvironmentController {


	@PostConstruct
	public void initialize() {

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/repos", method = RequestMethod.GET)
	@Operation(summary = "Get Repo List")
	public ResponseEntity<RestObject> 
	getRepoDbList(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			DatabaseList ret = new DatabaseList(DbUtil.getDatabaseList());
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/db-types", method = RequestMethod.GET)
	@Operation(summary = "Get Database Types List")
	public ResponseEntity<RestObject> 
	getDatabaseTypes(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			DatabaseTypeList ret = new DatabaseTypeList(SqlRepoUtils.databaseTypesMap);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
}
