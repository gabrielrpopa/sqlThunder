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


package com.widescope.sqlThunder.utils;

import com.widescope.logging.AppLogger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoListShortFormat;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoParamListDetail;

public class RestSqlRepoApiClient {
	

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	@Autowired
	RestTemplate restTemplate;
	
	private final static String XRequestedWith = "SQLREPO"; 
	
	public 
	static 
	RestObject 
	reloadSqlRepo(	final String serverBaseAddress, 
					final String email, 
					final String sessionID) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/reloadSqlRepo";
		
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.GET, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK)	{
				SqlRepoList sqlRepoList = mapper.convertValue(rest_object_json.get("payload"), SqlRepoList.class);
				transferableObject = new RestObject(sqlRepoList, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	
	public
	static 
	RestObject 
	getSqlRepoList(	final String serverBaseAddress, 
					final String email, 
					final String sessionID) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/getSqlRepoList";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.GET, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK) {
				SqlRepoList sqlRepoList = mapper.convertValue(rest_object_json.get("payload"), SqlRepoList.class);
				transferableObject = new RestObject(sqlRepoList, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}


	public 
	static 
	RestObject 
	getSqlRepoListSummary(	final String serverBaseAddress, 
							final String email, 
							final String sessionID) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/getSqlRepoListSummary";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.GET, entity, String.class);

			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK) {
				SqlRepoListShortFormat sqlRepoListShortFormat = mapper.convertValue(rest_object_json.get("payload"), SqlRepoListShortFormat.class);
				transferableObject = new RestObject(sqlRepoListShortFormat, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity,
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	
	

	public 
	static 
	RestObject 
	getSqlDetail(	final String serverBaseAddress, 
					final String email, 
					final String sessionID, 
					final int sqlID) throws Exception
	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/getSqlDetail";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			headers.add("sqlID", String.valueOf(sqlID) );

			HttpEntity<Object> entity = new HttpEntity<Object>(headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(	reloadSqlRepoEndPoint, 
																		HttpMethod.GET, 
																		entity, 
																		String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK)	{
				SqlRepoList sqlRepoList = mapper.convertValue(rest_object_json.get("payload"), SqlRepoList.class);
				transferableObject = new RestObject(sqlRepoList, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	

	public 
	static 
	RestObject 
	getSqlParamListDetail(	final String serverBaseAddress, 
							final String email, 
							final String sessionID, 
							final int sqlID) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/getSqlParamListDetail";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			headers.add("sqlID", String.valueOf(sqlID) );

			HttpEntity<Object> entity = new HttpEntity<Object>(headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.GET, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK)	{
				SqlRepoParamListDetail sqlRepoParamListDetail = mapper.convertValue(rest_object_json.get("payload"), SqlRepoParamListDetail.class);
				transferableObject = new RestObject(sqlRepoParamListDetail, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity,
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	

	public 
	static 
	RestObject 
	getSqlParamList(final String serverBaseAddress, 
					final String email, 
					final String sessionID, 
					final int sqlID) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/getSqlParamList";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			headers.add("sqlID", String.valueOf(sqlID) );

			HttpEntity<Object> entity = new HttpEntity<Object>(headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.GET, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK)	{
				ParamObj paramObj = mapper.convertValue(rest_object_json.get("payload"), ParamObj.class);
				transferableObject = new RestObject(paramObj, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	

	public 
	static 
	RestObject 
	executeSqlRepoOnDefaultDB(	final String serverBaseAddress, 
								final String email, 
								final String sessionID, 
								final int sqlID, 
								final JSONObject jsonObject, 
								final String outputType,
								final String inputCompression,
								final String outputCompression,
								final int batchCount) throws Exception	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/executeSqlRepo";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			headers.add("sqlID", String.valueOf(sqlID) );
			headers.add("outputType", outputType);
			
			headers.add("inputCompression", inputCompression);
			headers.add("outputCompression", outputCompression);
			headers.add("batchCount", String.valueOf(batchCount));


			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(jsonObject, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.POST, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK) {
				ResultQuery paramObj = mapper.convertValue(rest_object_json.get("payload"), ResultQuery.class);
				transferableObject = new RestObject(paramObj, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	
	
	

	public
	static 
	RestObject 
	executeSqlRepoOnDefaultDB(	final String serverBaseAddress, 
								final String email, 
								final String sessionID, 
								final int sqlID, 
								final String _jsonObject,
								final String outputType,
								final String inputCompression,
								final String outputCompression,
								final int batchCount) throws Exception {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/infinetMainApi/v1/executeSqlRepo";
		try	{
			
			JSONParser parser = new JSONParser(); 
			JSONObject jsonObject = (JSONObject) parser. parse(_jsonObject);
			
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			headers.add("email", email);
			headers.add("sessionID", sessionID);
			headers.add("sqlID", String.valueOf(sqlID) );
			headers.add("outputType", outputType);

			headers.add("inputCompression", inputCompression);
			headers.add("outputCompression", outputCompression);
			headers.add("batchCount", String.valueOf(batchCount));


			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(jsonObject, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.POST, entity, String.class);
			
			
			
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();
			
			String errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
			int errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class);
			String debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
			String errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
			String timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
			String errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
			String requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
			RestObject transferableObject = null;
			if(restObject.getStatusCode() == HttpStatus.OK) 	{
				ResultQuery paramObj = mapper.convertValue(rest_object_json.get("payload"), ResultQuery.class);
				transferableObject = new RestObject(paramObj, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			} else {
				transferableObject = new RestObject(null, 
													requestId, 
													errorMessage, 
													debugMessage, 
													errorCode, 
													errorSource, 
													errorSeverity, 
													methodName);
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.obj));
		}
	}
	
	

	public 
	static 
	void 
	jobStatusNotificationToAll(	final String serverBaseAddress, 
								final String jobID, 
								final String application, 
								final String module,
								final String environment,
								final String username,
								final String sessionID, 
								final String status,
								final String percentage,
								final String message,
								final boolean isNotification) throws Exception {
		
		if(!isNotification) return;
		String notificationEndPoint = serverBaseAddress + "/jobStatusNotificationToAll";
		try	{

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			
			headers.add("jobID", jobID);
			headers.add("application", application);
			headers.add("module", module );
			headers.add("environment", environment );
			headers.add("username", username );
			headers.add("sessionID", sessionID );
			headers.add("status", status );
			headers.add("percentage", percentage );
			headers.add("message", message );
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(headers);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.exchange(notificationEndPoint, HttpMethod.POST, entity, String.class);

		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
	

	public
	static
	void 
	jobStatusNotificationToJobSubscribers(	final String serverBaseAddress, 
							            	final String jobID, 
											final String application, 
											final String module,
											final String environment,
											final String username,
											final String sessionID, 
											final String status,
											final String percentage,
											final String message,
											boolean isNotification) throws Exception {
		if(!isNotification) return;
		
		String notificationEndPoint = serverBaseAddress + "/jobStatusNotificationToJobSubscribers";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestSqlRepoApiClient.XRequestedWith);
			
			headers.add("jobID", jobID);
			headers.add("application", application);
			headers.add("module", module );
			headers.add("environment", environment );
			headers.add("username", username );
			headers.add("sessionID", sessionID );
			headers.add("status", status );
			headers.add("percentage", percentage );
			headers.add("message", message );
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(headers);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.exchange(notificationEndPoint, HttpMethod.POST, entity, String.class);

		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
}
