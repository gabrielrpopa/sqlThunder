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



import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;

import java.util.Objects;


/**
 * 
 * @author Gabriel Popa
 * @since   August 2020
 */

public class RestApiEnvironmentClient {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	@Autowired
	RestTemplate restTemplate;
	
	public static NodeInfo infoGet(final String serverBaseAddress,
								   final String context,
						           final String admin, 
						           final String adminPasscode) throws Exception	{
		String setEndPoint = serverBaseAddress + "/" + context + "/environment/get";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Current-Admin", admin);
			headers.add("Current-Admin-Passcode", adminPasscode);
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, RestObject.class);
            return (NodeInfo) Objects.requireNonNull(restObject.getBody()).getPayload();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}

	
	
	
	public static boolean infoSet(final String serverBaseAddress,
								   final String context,
						           final String currentAdmin, 
						           final String currentAdminPasscode,
						           final String newNodeType,
								   final String newControllerId,
								   final String newUser,
								   final String newUserPasscode,
								   final String newAdmin,
								   final String newAdminPasscode
						           ) throws Exception {
		String setEndPoint = serverBaseAddress + "/" + context + "/environment/set";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Current-Admin", currentAdmin);
			headers.add("Current-Admin-Passcode", currentAdminPasscode);
			
			headers.add("New-Type", newNodeType);
			headers.add("New-ControllerId", newControllerId);
			headers.add("New-User", newUser);
			headers.add("New-User-Passcode", newUserPasscode);
			headers.add("New-Admin", newAdmin);
			headers.add("New-Admin-Passcode", newAdminPasscode);
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, RestObject.class);
			GenericResponse response = (GenericResponse) Objects.requireNonNull(restObject.getBody()).getPayload();
			return response.getGenericPayload().equalsIgnoreCase("OK");
		
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	public static boolean infoSetNodeType(final String serverBaseAddress,
										   final String context,
								           final String currentAdmin, 
								           final String currentAdminPasscode,
								           final String newNodeType
								           ) throws Exception {
		String setEndPoint = serverBaseAddress + "/" + context + "/environment/set/nodeType";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Current-Admin", currentAdmin);
			headers.add("Current-Admin-Passcode", currentAdminPasscode);
			headers.add("New-Type", newNodeType);
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, RestObject.class);
			GenericResponse response = (GenericResponse) Objects.requireNonNull(restObject.getBody()).getPayload();
			return response.getGenericPayload().equalsIgnoreCase("OK");
		
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	public static boolean infoSetControllerId(final String serverBaseAddress,
											   final String context,
									           final String currentAdmin, 
									           final String currentAdminPasscode,
									           final String newControllerId
									           ) throws Exception {
		String setEndPoint = serverBaseAddress + "/" + context + "/environment/set/nodeType";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Current-Admin", currentAdmin);
			headers.add("Current-Admin-Passcode", currentAdminPasscode);
			headers.add("New-ControllerId", newControllerId);
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, RestObject.class);
			GenericResponse response = (GenericResponse) Objects.requireNonNull(restObject.getBody()).getPayload();
			return response.getGenericPayload().equalsIgnoreCase("OK");
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	public static boolean infoSetNodeTypeAndControllerId(final String serverBaseAddress,
														   final String context,
												           final String currentAdmin, 
												           final String currentAdminPasscode,
												           final String newNodeType,
												           final String newControllerId
												           ) throws Exception {
		String setEndPoint = serverBaseAddress + "/" + context + "/environment/set/nodeType";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Current-Admin", currentAdmin);
			headers.add("Current-Admin-Passcode", currentAdminPasscode);
			headers.add("New-Type", newNodeType);
			headers.add("New-ControllerId", newControllerId);
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, RestObject.class);
			GenericResponse response = (GenericResponse) Objects.requireNonNull(restObject.getBody()).getPayload();
			return response.getGenericPayload().equalsIgnoreCase("OK");
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
}
