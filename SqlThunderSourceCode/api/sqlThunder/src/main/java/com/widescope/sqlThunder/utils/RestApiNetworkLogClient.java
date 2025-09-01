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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;



public class RestApiNetworkLogClient {
	
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	@Autowired
	RestTemplate restTemplate;

	static final String XRequestedWith = "RESTAPI";

	 
	public static void addLogWithArtifact(	final String serverBaseAddress, 
											final String userId, 
											final String sessionId,
											final String job,
											final String hostname,
											final String message,
											final String artifactOriginalName,
											final String artifactTypeId,
											final String artifactMetadata,
											final byte[] artifact
					                      ) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/v2/addLogWithArtifact";
		
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			headers.add("X-Requested-With", RestApiNetworkLogClient.XRequestedWith);
			
			headers.add("userId", userId);
			headers.add("sessionId", sessionId);
			headers.add("job", job);
			headers.add("hostname", hostname);
			headers.add("application", RestApiNetworkLogClient.XRequestedWith);
			headers.add("message", message);

			headers.add("artifactTypeId", artifactTypeId);
			headers.add("artifactMetadata", artifactMetadata);
			
			
			
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("artifact", artifact);
			
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			RestTemplate restTemplate = new RestTemplate();
			
			
			Runnable task = () -> {

				String errorMessage = "";
				int errorCode = 0;
				String debugMessage = "";
				String errorSource = "";
				String timestamp = "";
				String errorSeverity = "";
				String requestId = "";
				
				
			    try {
			    	
			    	ResponseEntity<String> restObject = restTemplate.exchange(	reloadSqlRepoEndPoint, 
			    																HttpMethod.PUT, 
			    																requestEntity, 
			    																String.class);
			    	
			    	JSONParser parser = new JSONParser(); 
					JSONObject rest_object_json = (JSONObject) parser. parse(restObject.getBody());
					ObjectMapper mapper = new ObjectMapper();
					
					errorMessage = mapper.convertValue(rest_object_json.get("errorMessage"), String.class);
					errorCode = mapper.convertValue(rest_object_json.get("errorCode"), Integer.class).intValue();
					debugMessage = mapper.convertValue(rest_object_json.get("debugMessage"), String.class);
					errorSource = mapper.convertValue(rest_object_json.get("errorSource"), String.class);
					timestamp = mapper.convertValue(rest_object_json.get("timestamp"), String.class);
					errorSeverity = mapper.convertValue(rest_object_json.get("errorSeverity"), String.class);
					requestId = mapper.convertValue(rest_object_json.get("requestId"), String.class);
					if(restObject.getStatusCode() != HttpStatus.OK)	{
						String error = "addLogWithArtifact Error Request Id: " + requestId +
																" Message: " + errorMessage +
																" Debug: " + debugMessage +
																" Code: " + errorCode +
																" Source: " + errorSource +
																" Severity: " + errorSeverity +
																" Timestamp: " + timestamp;

						AppLogger.logError(className, methodName, AppLogger.obj, error);
					}
			    }  catch (Exception ex) {
					AppLogger.logException(ex, className, methodName, AppLogger.ctrl);
			    }
			};
			
			
			Thread t = new Thread(task);
			t.start();
		} catch(Exception ex) {
			AppLogger.logException(ex, className, methodName, AppLogger.ctrl);
		}
	}
	
	
		
	
	
	
}
