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


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import io.swagger.v3.oas.annotations.Operation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.widescope.cache.service.CacheResponse;
import com.widescope.cache.service.GlobalStorage;
import com.widescope.cache.service.CacheStringPayload;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;



@CrossOrigin
@RestController
@Schema(title = "Cache Controller")
public class CacheController {
	@Autowired
	private AppConstants appConstants;

	@PostConstruct
	public void initialize() {

	}

	private JSONObject 
	getAllKeys(Object[] keys) {
		Map<Integer, String> map = new HashMap<>();
		for(int i = 0; i < keys.length; i++) {
			map.put(i, keys[i].toString());
		}
		return new JSONObject(map);
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:clear", method = RequestMethod.DELETE)
	@Operation(summary = "Clear the entire cache")
	public ResponseEntity<RestObject>
	clearCacheStore (@RequestHeader(value="requestId", defaultValue = "") String requestId,
					 @RequestHeader(value="user")  final String user) {

		requestId = StringUtils.generateRequestId(requestId);

		try	{
			GlobalStorage.stringMap.clear();
			CacheResponse cResp = new CacheResponse("","", user, "CLEARED");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/keys:query", method = RequestMethod.GET)
	@Operation(summary = "Get all keys with associated value stored in the cache") /*response = String.class*/
	public ResponseEntity<RestObject> 
	getAllCacheKeys (@RequestHeader(value="user")  final String user,
					 @RequestHeader(value="requestId", defaultValue = "") String requestId,
					 @RequestHeader(value="keyList", required = false) final String keyList) {

		requestId = StringUtils.generateRequestId(requestId);

		try	{
			Object[] keys;
			if(keyList == null || keyList.isBlank() || keyList.isEmpty())
				keys = GlobalStorage.stringMap.entrySet().toArray();
			else {
				List<String> keyList_ = Arrays.asList(keyList.split(",", -1));
				keys = GlobalStorage.stringMap.entrySet().stream().filter(e -> keyList_.contains(e.getKey())).toArray();
			}
			JSONObject response = getAllKeys( keys );
			CacheResponse cResp = new CacheResponse(keyList,response.toJSONString(), user, "");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:set")
	@Operation(summary = "Set a new key with its value to cache")
	public ResponseEntity<RestObject> 
	setCacheKey(@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="user")  final String user,
				@RequestHeader(value="key", defaultValue = "") String key,
				@RequestHeader(value="validFor")  final long validFor,
				@RequestBody final String jsonObject) {


		requestId = StringUtils.generateRequestId(requestId);
		if(GlobalStorage.internalFreeMemory < appConstants.getCacheFreeMemory()) {
			RestObject.retException(requestId,Thread.currentThread().getStackTrace()[1].getMethodName(), "HEAP_ERROR");
		}

		if(key.isEmpty() || key.isBlank())
			key = StringUtils.generateKey(user);

        assert key != null;
        if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retExceptionWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		long validForLong = GlobalStorage.secondsSinceEpoch() + validFor;

		try {
			CacheStringPayload stringPayload = new CacheStringPayload(jsonObject, validForLong, null);
			GlobalStorage.stringMap.put(key, stringPayload);
			CacheResponse cResp = new CacheResponse(key,"", user, "SAVED");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete a key and its value")
	public ResponseEntity<RestObject>
	deleteCacheKey(@RequestHeader(value="user")  final String user,
				   @RequestHeader(value="requestId", defaultValue = "") String requestId,
				   @RequestHeader(value="key")  final String key) {

		requestId = StringUtils.generateRequestId(requestId);
		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		try	{
			String keyToSearch = StringUtils.getKey(key, user);
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				GlobalStorage.stringMap.remove(keyToSearch);
				CacheResponse cResp = new CacheResponse(key,"", user, "DELETED");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				CacheResponse cResp = new CacheResponse(key,"", user, "NOT_FOUND");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:updateValidFor", method = RequestMethod.POST)
	@Operation(summary = "Update validity period of a key")
	public ResponseEntity<RestObject> 
	updateCacheKeyValidFor(@RequestHeader(value="user")  final String user,
						   @RequestHeader(value="requestId", defaultValue = "") String requestId,
						   @RequestHeader(value="key")  final String key,
						   @RequestHeader(value="validFor", defaultValue = "0") long validFor) {
		requestId = StringUtils.generateRequestId(requestId);
		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		try	{
			CacheStringPayload stringPayload;
			String keyToSearch = StringUtils.getKey(key, user);
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				stringPayload = GlobalStorage.stringMap.get(keyToSearch);
				validFor = GlobalStorage.secondsSinceEpoch() + validFor;
				stringPayload.setExpiry( validFor );
				GlobalStorage.stringMap.put(keyToSearch, stringPayload);
				CacheResponse cResp = new CacheResponse(key,"", user, "UPDATED");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				CacheResponse cResp = new CacheResponse(key,"", user, "NOT_FOUND");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:updateValue", method = RequestMethod.POST)
	@Operation(summary = "Update Value of a key")
	public ResponseEntity<RestObject> 
	updateCacheKeyValue(@RequestHeader(value="user")  final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="key")  final String key,
						@RequestBody final String jsonObject)  {

		requestId = StringUtils.generateRequestId(requestId);
		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		
		try	{
			JsonObject jsonObj = JsonParser.parseString(jsonObject).getAsJsonObject();
			String keyToSearch = StringUtils.getKey(key, user);
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				CacheStringPayload stringPayload = GlobalStorage.stringMap.get(keyToSearch);
				stringPayload.setValue(jsonObj.get("value").toString() );
				GlobalStorage.stringMap.put(keyToSearch, stringPayload);
				CacheResponse cResp = new CacheResponse(key,"", user, "UPDATED");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				CacheResponse cResp = new CacheResponse(key,"", user, "NOT_FOUND");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:update", method = RequestMethod.POST)
	@Operation(summary = "Update entire object of a key")
	public ResponseEntity<RestObject>
	updateCacheKeyValueAndValidity(@RequestHeader(value="user")  final String user,
								   @RequestHeader(value="requestId", defaultValue = "") String requestId,
								   @RequestHeader(value="key")  final String key,
								   @RequestHeader(value="validFor")  final String validFor,
								   @RequestBody final String jsonObject) {

		requestId = StringUtils.generateRequestId(requestId);
		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		
		try {
			JsonObject jsonObj = JsonParser.parseString(jsonObject).getAsJsonObject();
			String keyToSearch = StringUtils.getKey(key, user);
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				CacheStringPayload stringPayload = GlobalStorage.stringMap.get(keyToSearch);
				String value = jsonObj.get("value").toString();
				long validForLong = 0;
				if(validFor != null)
					validForLong = GlobalStorage.secondsSinceEpoch() + Long.parseLong(validFor);
				
				stringPayload.setExpiry( validForLong );
				stringPayload.setValue( value );
				GlobalStorage.stringMap.put(keyToSearch, stringPayload);
				
				CacheResponse cResp = new CacheResponse(key,"", user, "UPDATED");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				CacheResponse cResp = new CacheResponse(key,"", user, "NOT_FOUND");
				RestObject transferableObject = new RestObject(cResp, 
																requestId, 
																"",  
																"", 
																ErrorCode.OK, 
																Sources.NONE, 
																ErrorSeverity.NONE,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				
				return new ResponseEntity<> (transferableObject, HttpStatus.NOT_FOUND);
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:get", method = RequestMethod.GET)
	@Operation(summary = "Get an object from the cache key")
	public ResponseEntity<RestObject> 
	getCacheKey(@RequestHeader(value="user")  final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="key")  final String key) {

		requestId = StringUtils.generateRequestId(requestId);
		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		CacheStringPayload stringPayload = new CacheStringPayload();
		try {
			String keyToSearch = StringUtils.getKey(key, user);
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				stringPayload = GlobalStorage.stringMap.get(keyToSearch);
				if( stringPayload.getExpiry() <= GlobalStorage.secondsSinceEpoch()) {
					stringPayload.zeroOut();
					GlobalStorage.stringMap.remove(keyToSearch);
					CacheResponse cResp = new CacheResponse(key,"", user, "EXPIRED");
					return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
					
				} else {
					CacheResponse cResp = new CacheResponse(key,stringPayload.getValue(), user, "FOUND");
					return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
			} else {
				CacheResponse cResp = new CacheResponse(key,stringPayload.getValue(), user, "NOT_FOUND");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}


	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:isKey", method = RequestMethod.GET)
	@Operation(summary = "Check if a key exists in the cache")
	public ResponseEntity<RestObject> 
	isCacheKey(@RequestHeader(value="user")  final String user,
			   @RequestHeader(value="requestId", defaultValue = "") String requestId,
			   @RequestHeader(value="key")  final String key) {

		requestId = StringUtils.generateRequestId(requestId);
		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());		}
		
		String keyToSearch = StringUtils.getKey(key, user);
		try {
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				CacheStringPayload stringPayload = GlobalStorage.stringMap.get(keyToSearch);
				if( stringPayload.getExpiry() <= GlobalStorage.secondsSinceEpoch() ) {
					try {
						GlobalStorage.stringMap.remove(keyToSearch);
						CacheResponse cResp = new CacheResponse(key,"", user, "EXPIRED");
						return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
						
					} catch(Exception ex){
						CacheResponse cResp = new CacheResponse(key,"", user, ex.getMessage());
						return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
					}
				} else {
					CacheResponse cResp = new CacheResponse(key,"", user, "FOUND");
					return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
			} else {
				CacheResponse cResp = new CacheResponse(key,"", user, "NOT_FOUND");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

}
