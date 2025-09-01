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


package com.widescope.sqlThunder.utils.cache;




import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.widescope.logging.AppLogger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;




public class RestApiCacheClient {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	@Autowired
	RestTemplate restTemplate;
	
	public static CacheObject set(String serverBaseAddress, 
			                      String passcode, 
			                      String userId,
			                      String validFor,
			                      String notificationProxy,
			                      String value) throws Exception {
		String setEndPoint = serverBaseAddress + "/set";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("passcode", passcode);
			headers.add("userId", userId);
			headers.add("validFor", validFor);
			headers.add("notificationProxy", notificationProxy);
			
			Map<String, String> map = new HashMap<>();
			map.put("value", value);
			JSONObject payload = new JSONObject(map);
		
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(payload, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, JSONObject.class);
			
			String key_ = Objects.requireNonNull(restObject.getBody()).get("key").toString();
			String value_ = restObject.getBody().get("value").toString();
			String userId_ = restObject.getBody().get("userId").toString();
			String message_ = restObject.getBody().get("message").toString();
			return CacheObject.makeCacheObject(key_, value_, userId_, message_);
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}

    }

	public static CacheObject delete(String serverBaseAddress, 
						            String passcode, 
						            String userId,
						            String key) throws Exception {
		String deleteEndPoint = serverBaseAddress + "/delete";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("passcode", passcode);
			headers.add("userId", userId);
			headers.add("key", key);
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(deleteEndPoint, HttpMethod.POST, entity, JSONObject.class);
			
			String key_ = Objects.requireNonNull(restObject.getBody()).get("key").toString();
			String value_ = restObject.getBody().get("value").toString();
			String userId_ = restObject.getBody().get("userId").toString();
			String message_ = restObject.getBody().get("message").toString();
			
			return CacheObject.makeCacheObject(key_, value_, userId_, message_);
			
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
	
	public static CacheObject updateValidFor(String serverBaseAddress, 
									         String passcode, 
									         String userId,
									         String key,
									         String validFor) throws Exception	{
		String setEndPoint = serverBaseAddress + "/updateValidFor";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("passcode", passcode);
			headers.add("userId", userId);
			headers.add("key", key);
			headers.add("validFor", validFor);
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, JSONObject.class);
			
			String key_ = Objects.requireNonNull(restObject.getBody()).get("key").toString();
			String value_ = restObject.getBody().get("value").toString();
			String userId_ = restObject.getBody().get("userId").toString();
			String message_ = restObject.getBody().get("message").toString();
			
			return CacheObject.makeCacheObject(key_, value_, userId_, message_);
		
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
	
	
	public static CacheObject updateValue(String serverBaseAddress, 
							            String passcode, 
							            String userId,
							            String key,
							            String value) throws Exception	{
		String setEndPoint = serverBaseAddress + "/updateValue";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("passcode", passcode);
			headers.add("userId", userId);
			headers.add("key", key);
			
			Map<String, String> map = new HashMap<>();
			map.put("value", value);
			JSONObject payload = new JSONObject(map);
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(payload, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(setEndPoint, HttpMethod.POST, entity, JSONObject.class);
			
			String key_ = Objects.requireNonNull(restObject.getBody()).get("key").toString();
			String value_ = restObject.getBody().get("value").toString();
			String userId_ = restObject.getBody().get("userId").toString();
			String message_ = restObject.getBody().get("message").toString();
			
			return CacheObject.makeCacheObject(key_, value_, userId_, message_);
		
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
	public static CacheObject update(String serverBaseAddress, 
						            String passcode, 
						            String userId,
						            String key,
						            String validFor,
						            String value) throws Exception {
		String updateEndPoint = serverBaseAddress + "/update";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("passcode", passcode);
			headers.add("userId", userId);
			headers.add("key", key);
			headers.add("validFor", validFor);
			
			
			Map<String, String> map = new HashMap<>();
			map.put("value", value);
			JSONObject payload = new JSONObject(map);
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(payload, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(updateEndPoint, HttpMethod.POST, entity, JSONObject.class);
			
			String key_ = Objects.requireNonNull(restObject.getBody()).get("key").toString();
			String value_ = restObject.getBody().get("value").toString();
			String userId_ = restObject.getBody().get("userId").toString();
			String message_ = restObject.getBody().get("message").toString();
			
			return CacheObject.makeCacheObject(key_, value_, userId_, message_);
		
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
	
	public static CacheObject get(String serverBaseAddress, 
						          String passcode, 
						          String userId,
						          String key) throws Exception	{
		String getEndPoint = serverBaseAddress + "/get";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("passcode", passcode);
			headers.add("userId", userId);
			headers.add("key", key);
			
			
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(getEndPoint, HttpMethod.GET, entity, JSONObject.class);
			
			String key_ = Objects.requireNonNull(restObject.getBody()).get("key").toString();
			String value_ = restObject.getBody().get("value").toString();
			String userId_ = restObject.getBody().get("userId").toString();
			String message_ = restObject.getBody().get("message").toString();
			
			
			return CacheObject.makeCacheObject(key_, value_, userId_, message_);
		
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
	}
	
	

	
}
