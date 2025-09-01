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
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoList; 


public class AuthRestApiClient {
	
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	@Autowired
	RestTemplate restTemplate;
	
	public static RestObject userAuth(	final String serverBaseAddress, 
										final String email, 
										final String password) throws Exception	{

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String reloadSqlRepoEndPoint = serverBaseAddress + "/auth/v2/userAuth";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("email", email);
			headers.add("password", password);
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
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
				SqlRepoList sqlRepoList = mapper.convertValue(rest_object_json.get("payload"), SqlRepoList.class);
				transferableObject = new RestObject(sqlRepoList, requestId, errorMessage, debugMessage, errorCode, errorSource, errorSeverity, "reloadSqlRepo");
			} else {
				transferableObject = new RestObject(null, requestId, errorMessage, debugMessage, errorCode, errorSource, errorSeverity, "reloadSqlRepo");
			}
			transferableObject.setTimestamp(timestamp);
			return transferableObject;
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, methodName, AppLogger.ctrl)) ;
		}
	}
	
	
	
}
