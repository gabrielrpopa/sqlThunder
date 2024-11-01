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



import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.widescope.logging.AppLogger;
import com.widescope.logging.loggingDB.LogRecordList;
import com.widescope.logging.management.EnvironmentManagement;
import com.widescope.logging.repo.ApplicationPartitionRecord;
import com.widescope.logging.repo.ApplicationRecord;
import com.widescope.logging.repo.ApplicationRecordList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.rest.about.LogContent;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;




@CrossOrigin
@RestController
@Schema(title = "Logging Controller")
public class LoggingController {
	

	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private AppConstants appConstants;


	@PostConstruct
	public void initialize() {
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/internal/logs:get", method = RequestMethod.GET)
	@Operation(summary = "Query internal logs")
	public 
	ResponseEntity<RestObject> 
	queryInternalLog(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="startTime") String startTime,
						@RequestHeader(value="endTime") String endTime,
						@RequestHeader(value="stringToSearch") String stringToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LogContent logList = new LogContent();
		try {
			List<String> allLines = Files.readAllLines(Paths.get(appConstants.getLoggingFile()));
			if(!stringToSearch.isEmpty()) {
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dateLog;
				Date dStart;
				Date dEnd;
				for (String line : allLines) {
					try {
						String dateTime = line.substring(0, Math.min(line.length(), 19)); // 2023-03-14 10:05:55
						dateLog = sf.parse(dateTime);
						dStart = DateTimeUtils.epochMillisecToDate(Long.parseLong(startTime));
						dEnd = DateTimeUtils.epochMillisecToDate(Long.parseLong(endTime));
						if( line.contains(stringToSearch) && dateLog.compareTo(dStart) == 1 && dateLog.compareTo(dEnd) == -1) {
							logList.addLogContent(line);
						}
					} catch(Exception ignored) {	}
					
				}
			} else {
				for (String line : allLines) {
					logList.addLogContent(line);
				}
			}
			return RestObject.retOKWithPayload(logList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application:set", method = RequestMethod.PUT)
	@Operation(summary = "Set application")
	public 
	ResponseEntity<RestObject> 
	setApplication(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="application") String application,
					@RequestHeader(value="partitionType") String partitionType,
					@RequestHeader(value="repositoryType") String repositoryType,
					@RequestHeader(value="repositoryId") String repositoryId) {
		

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			if(!EnvironmentManagement.repositoryTypes.contains(repositoryType)) {
				throw new Exception ("Wrong repository type");
			}
			if(!EnvironmentManagement.partitions.contains(partitionType)) {
				throw new Exception ("Wrong partition type");
			}
			
			ApplicationRecord ret = EnvironmentManagement.setApplication(application, partitionType, repositoryType, Long.parseLong(repositoryId));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove Application Logs")
	public 
	ResponseEntity<RestObject> 
	deleteApplication(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="applicationId") String applicationId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			boolean isOk  = EnvironmentManagement.removeApplication( Long.parseLong(applicationId) );
			if(isOk)
				return RestObject.retOK(requestId, methodName);
			else 
				return RestObject.retException(requestId, methodName, "Removing Application Failed");
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application:query", method = RequestMethod.GET)
	@Operation(summary = "Get All Applications")
	public 
	ResponseEntity<RestObject> 
	getAllApplications(	@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			List<ApplicationRecord> lst = EnvironmentManagement.getAllApplications();
			ApplicationRecordList applicationRecordList = new ApplicationRecordList(lst);
			return RestObject.retOKWithPayload(applicationRecordList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/partition:query", method = RequestMethod.GET)
	@Operation(summary = "Get All Partitions of an application")
	public 
	ResponseEntity<RestObject> 
	getApplicationPartitions(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="applicationId") String applicationId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			ApplicationRecord applicationRecord = EnvironmentManagement.getApplication(Long.parseLong(applicationId) );
			List<ApplicationPartitionRecord> appPartitionList = EnvironmentManagement.getAllApplicationPartitions(Long.parseLong(applicationId));
			applicationRecord.setApplicationPartitionRecordList(appPartitionList);
			return RestObject.retOKWithPayload(applicationRecord, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/logs:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove Application Logs")
	public 
	ResponseEntity<RestObject> 
	deleteApplicationLogs(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="applicationId") String applicationId,
							@RequestHeader(value="fromEpochMilliseconds") String fromEpochMilliseconds,
							@RequestHeader(value="toEpochMilliseconds") String toEpochMilliseconds) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

		try	{
			
			if(Long.parseLong(fromEpochMilliseconds) > Long.parseLong(toEpochMilliseconds)) {
				throw new Exception ("Range of periods issue. From greater than to");
			}
			boolean isOk  = EnvironmentManagement.removeApplicationLogs(Long.parseLong(applicationId), 
																		Long.parseLong(fromEpochMilliseconds), 
																		Long.parseLong(toEpochMilliseconds) );
			if(isOk)
				return RestObject.retOK(requestId, methodName);
			else
				return RestObject.retException(requestId, methodName, "Error Removing Application Logs");
			
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/logs/entry:add", method = RequestMethod.POST)
	@Operation(summary = "Add new log entry")
	public 
	ResponseEntity<RestObject> 
	addLogEntry(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="applicationId") String applicationId,
				@RequestHeader(value="message") String message,
				@RequestHeader(value="messageType") String messageType,
				HttpServletRequest request) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
 		String host = request.getRemoteHost();
		try {
			if(!EnvironmentManagement.messageType.contains(messageType)) {
				throw new Exception ("Wrong message type");
			}
			if(!EnvironmentManagement.isApplication(Long.parseLong(applicationId))) {
				throw new Exception ("Wrong application Id");
			}
			final long timestamp = DateTimeUtils.millisecondsSinceEpoch();
			if(!EnvironmentManagement.createH2LogDatabase(Long.parseLong(applicationId), timestamp)) {
				throw new Exception("Cannot create log partition");
			} else {
				boolean isOk = 	EnvironmentManagement.addNewLogEntry( Long.parseLong(applicationId), host, timestamp, message, messageType,	timestamp,"","");
				if(isOk)
					return RestObject.retOK(requestId, methodName);
				else
					return RestObject.retException(requestId, methodName, "Error adding new entry");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/logs/artifact:add", method = RequestMethod.POST)
	@Operation(summary = "Add new log entry with artifact/attached file")
	public 
	ResponseEntity<RestObject> 
	addLogEntryWithArtifact(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="applicationId") String applicationId,
							@RequestHeader(value="message") String message,
							@RequestHeader(value="messageType") String messageType,
							@RequestHeader(value="artifactName") String artifactName,
							@RequestHeader(value="artifactType") String artifactType,
							HttpServletRequest request
							) {
		String host = request.getRemoteHost();

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			if(!EnvironmentManagement.messageType.contains(messageType)) {
				throw new Exception ("Wrong message type");
			}
			if(!EnvironmentManagement.messageType.contains(artifactType)) {
				throw new Exception ("Wrong artifact type");
			}
			
			if(!EnvironmentManagement.isApplication(Long.parseLong(applicationId))) {
				throw new Exception ("Wrong application Id");
			}
			
			final long timestamp = DateTimeUtils.millisecondsSinceEpoch();
			if(!EnvironmentManagement.createH2LogDatabase(Long.parseLong(applicationId), timestamp)) {
				throw new Exception("Cannot create log partition");
			} else {
				
				boolean isOk = 
				EnvironmentManagement.addNewLogEntry( Long.parseLong(applicationId), 
												host, 
												timestamp, 
												message,
												messageType, 
												timestamp, 
												artifactName,
												artifactType);
				if(isOk)
					return RestObject.retOK(requestId, methodName);
				else
					return RestObject.retException(requestId, methodName, "Error adding new entry");

			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/logs:query", method = RequestMethod.GET)
	@Operation(summary = "Query logs")
	public 
	ResponseEntity<RestObject> 
	queryLogs(	@RequestHeader(value="user") String user,
				@RequestHeader(value="session") String session,
				@RequestHeader(value="applicationId") String applicationId,
				@RequestHeader(value="fromEpochMilliseconds") String fromEpochMilliseconds,
				@RequestHeader(value="toEpochMilliseconds") String toEpochMilliseconds,
				@RequestHeader(value="textToSearch") String textToSearch) {
		

		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String requestId = StaticUtils.getUUID();
		//String host = incomingHeaders.getHost().getHostName();
		if( !authUtil.isSessionAuthenticated(user, session) ) {
			return RestObject.retAuthError(requestId);
		}

		try {
			ApplicationRecord appRecord 
			= EnvironmentManagement.getLogFiles(Long.parseLong(applicationId), 
												Long.parseLong( fromEpochMilliseconds ), 
												Long.parseLong( toEpochMilliseconds ));
			LogRecordList lst 
			= EnvironmentManagement.getLogEntries(	appRecord, 
													Long.parseLong( fromEpochMilliseconds ), 
													Long.parseLong( toEpochMilliseconds ), 
													textToSearch);
			return RestObject.retOKWithPayload(lst, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/logs:get", method = RequestMethod.GET)
	@Operation(summary = "Query logs")
	public 
	ResponseEntity<RestObject> 
	getLogEntry(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="applicationId") String applicationId,
				@RequestHeader(value="timestampMilliseconds") String timestampMilliseconds, 
				@RequestHeader(value="entryId") String entryId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			ApplicationRecord appRecord 
			= EnvironmentManagement.getLogFiles(Long.parseLong(applicationId), 
												Long.parseLong( timestampMilliseconds ), 
												Long.parseLong( timestampMilliseconds ));
			
			
			LogRecordList lst 
			= EnvironmentManagement.getLogEntries(	appRecord, 
													Long.parseLong( timestampMilliseconds ), 
													Long.parseLong( timestampMilliseconds ), 
													Long.parseLong(entryId));
			return RestObject.retOKWithPayload(lst, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/logging/application/artifact:get", method = RequestMethod.GET)
	@Operation(summary = "Query logs")
	public 
	ResponseEntity<RestObject> 
	getLogArtifact( @RequestHeader(value="user") String user,
					@RequestHeader(value="session") String session,
					@RequestHeader(value="applicationId") String applicationId,
					@RequestHeader(value="timestampMilliseconds") String timestampMilliseconds, 
					@RequestHeader(value="entryId") String entryId,
					@RequestHeader(value="artifactName") String artifactName,
					@RequestHeader(value="artifactType") String artifactType) {
		

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String requestId = StaticUtils.getUUID();
		if( !authUtil.isSessionAuthenticated(user, session) ) {
			return RestObject.retAuthError(requestId);
		}
		try {
			ApplicationRecord appRecord
			= EnvironmentManagement.getLogFiles(Long.parseLong(applicationId), 
												Long.parseLong( timestampMilliseconds ), 
												Long.parseLong( timestampMilliseconds ));
			
			
			LogRecordList lst
			= EnvironmentManagement.getLogEntries(	appRecord,
													Long.parseLong( timestampMilliseconds ), 
													Long.parseLong( timestampMilliseconds ), 
													Long.parseLong(entryId));
			return RestObject.retOKWithPayload(lst, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

}
