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


package com.widescope.sqlThunder.utils.restApiClient;

import java.util.Optional;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
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
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.utils.StaticUtils;

public class RestStorageApiClient {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	@Autowired
	RestTemplate restTemplate;
	
	private final static String XRequestedWith = "STORAGEAPI"; 
	
	
	/**
	 * InitiateThisToThatBackupApiCall is calling initiateThatToThisBackup on that node
	 * Always remember "this" is local node/server, and "that" is remote node

	 */
	public static Optional<String>
	InitiateThisToThatBackupApiCall(String email,
								   String sessionId,
								   String isEncrypted,
								   String thisNode,
								   String thatNode,
								   String bodyLoad) throws Exception {
		String reloadSqlRepoEndPoint = thatNode + "/storageApi/v1/initiateThatToThisBackup";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestStorageApiClient.XRequestedWith);
			
			headers.add("email", email);
			headers.add("sessionId", sessionId);
			headers.add("isEncrypted", isEncrypted);
			headers.add("thatNode", thatNode);
			
			
			HttpEntity<String> entity = new HttpEntity<String>(bodyLoad, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.POST, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();

			if(restObject.getStatusCode() == HttpStatus.OK)	{
				GenericResponse genericResponse = mapper.convertValue(rest_object_json.get("payload"), GenericResponse.class);
				return Optional.of(genericResponse.getGenericPayload());
			}

			return Optional.empty();
			
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	/**
	 * thisToThatBackupFileApiCall calls thatToThisBackupFile endpoint on remote node

	 */
	public static Optional<String> thisToThatBackupFileApiCall(String backRecoveryFile, 
																 String email, 
																 String sessionId, 
																 String taskId,
																 String isEncrypted, 
																 String thisNode,
																 String thatNode,
																 String bodyLoad
								                                ) throws Exception
	{

		
		String reloadSqlRepoEndPoint = thatNode + "/storageApi/v1/thatToThisBackupFile";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestStorageApiClient.XRequestedWith);
			
			headers.add("backRecoveryFile", backRecoveryFile);
			headers.add("email", email);
			headers.add("sessionId", sessionId);
			headers.add("taskId", taskId);
			headers.add("isEncrypted", isEncrypted);
			headers.add("thatNode", thisNode); // thatNode received by that node is this node, inverse roles

			
			HttpEntity<String> entity = new HttpEntity<String>(bodyLoad, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(reloadSqlRepoEndPoint, HttpMethod.POST, entity, String.class);
			
			
			JSONParser parser = new JSONParser(); 
			JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
			ObjectMapper mapper = new ObjectMapper();

			if(restObject.getStatusCode() == HttpStatus.OK)	{
				GenericResponse genericResponse = mapper.convertValue(rest_object_json.get("payload"), GenericResponse.class);
				return Optional.of(genericResponse.getGenericPayload());
			}
			return Optional.empty();
			
		}	catch(Exception ex)		{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}

	
}
