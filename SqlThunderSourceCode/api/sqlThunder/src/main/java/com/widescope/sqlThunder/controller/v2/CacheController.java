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

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import org.springframework.http.MediaType;





@CrossOrigin
@RestController
@Schema(title = "Cache Controller")
public class CacheController {
	@Autowired
	private AppConstants appConstants;

	@Autowired
	private AuthUtil authUtil;

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
	@RequestMapping(value = "/cache/store:clear", method = RequestMethod.POST)
	@Operation(summary = "Set an object in the cache") 
	public ResponseEntity<RestObject>
	clearStore (@RequestHeader(value="internalAdmin") String internalAdmin,
				@RequestHeader(value="internalAdminPasscode") String internalAdminPasscode) {

		String requestId = StaticUtils.getUUID();
		if( !authUtil.isInternalAdminAuthenticated(internalAdmin, internalAdminPasscode) )	{
			return RestObject.retAuthError(requestId);
		}

		try	{
			GlobalStorage.stringMap.clear();
			CacheResponse cResp = new CacheResponse("","", internalAdmin, "CLEARED");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/keys:query", method = RequestMethod.POST)
	@Operation(summary = "Set an object in the cache") /*response = String.class*/
	public ResponseEntity<RestObject> 
	getAllKeys (@RequestHeader(value="internalAdmin") String internalAdmin,
				@RequestHeader(value="internalAdminPasscode") String internalAdminPasscode,
				@RequestHeader(value="keyList", required = false) String keyList) {

		String requestId = StaticUtils.getUUID();
		if( !authUtil.isInternalAdminAuthenticated(internalAdmin, internalAdminPasscode) )	{
			return RestObject.retAuthError(requestId);
		}

		JSONObject response;
		try	{
			Object[] keys;
			if(keyList == null || keyList.isBlank() || keyList.isEmpty())
				keys = GlobalStorage.stringMap.entrySet().toArray();
			else {
				List<String> keyList_ = Arrays.asList(keyList.split(",", -1));
				keys = GlobalStorage.stringMap.entrySet().stream().filter(e -> keyList_.contains(e.getKey())).toArray();
			}
			response = getAllKeys( keys );
			CacheResponse cResp = new CacheResponse("",response.toJSONString(), internalAdmin, "");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:set", method = RequestMethod.POST, 
					produces = { MediaType.APPLICATION_JSON_VALUE }, 
					consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Set string to cache")
	@ApiResponses(value = {
							@ApiResponse(responseCode = "200",
										description = "Ok",
										content = { @Content(mediaType = "application/json",
													schema = @Schema(implementation = String.class))
													}),
							@ApiResponse(responseCode = "403", description = "IP Address Not supported"),
							@ApiResponse(responseCode = "500", description = "Internal server error", content =
							{ @Content(mediaType = "application/json", schema =	@Schema(implementation = String.class)) }) })
	public ResponseEntity<RestObject> 
	set(@RequestHeader(value="user")  String user,
		@RequestHeader(value="requestId") String requestId,
        @RequestHeader(value="validFor", required = false)  String validFor,
        @RequestHeader(value="notificationProxy", required = false)  JSONObject notificationProxy,
        @RequestBody JSONObject jsonObj) {

		if(GlobalStorage.internalFreeMemory < appConstants.getCacheFreeMemory()) {
			RestObject.retException(requestId,Thread.currentThread().getStackTrace()[1].getMethodName(), "HEAP_ERROR");
		}
		
		String key = StringUtils.generateKey(user);
        assert key != null;
        if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retExceptionWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		long validForLong;
		try	{
			validForLong = GlobalStorage.secondsSinceEpoch() + Long.parseLong(validFor);
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
			
		
		try {
			String keyToSearch = StringUtils.getKey(key, user);
			String value = jsonObj.get("value").toString();
			
			CacheStringPayload stringPayload = new CacheStringPayload(value, validForLong, null);
			GlobalStorage.stringMap.put(keyToSearch, stringPayload);
			CacheResponse cResp = new CacheResponse(key,"", user, "SAVED");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:delete", 
					method = RequestMethod.POST, 
					produces = { MediaType.APPLICATION_JSON_VALUE },
					consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Delete a key and its value")
	public ResponseEntity<RestObject>
	delete(	@RequestHeader(value="user")  String user,
			@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="key")  String key) {

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
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:updateValidFor", 
					method = RequestMethod.POST, 
					produces = { MediaType.APPLICATION_JSON_VALUE }, 
					consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Update Validity")
	public ResponseEntity<RestObject> 
	updateValidFor(	@RequestHeader(value="user")  String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="key")  String key,
					@RequestHeader(value="validFor", required = false)  String validFor) {

		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		
		try	{
			CacheStringPayload stringPayload;
			String keyToSearch = StringUtils.getKey(key, user);
			if(GlobalStorage.stringMap.containsKey(keyToSearch)) {
				stringPayload = GlobalStorage.stringMap.get(keyToSearch);
				long validForLong = 0;
				if(validFor != null)
					validForLong = GlobalStorage.secondsSinceEpoch() + Long.parseLong(validFor);
				
				stringPayload.setExpiry( validForLong );
				GlobalStorage.stringMap.put(keyToSearch, stringPayload);
				
				CacheResponse cResp = new CacheResponse(key,"", user, "UPDATED");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				CacheResponse cResp = new CacheResponse(key,"", user, "NOT_FOUND");
				return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());			}
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:updateValue", 
					method = RequestMethod.POST, 
					produces = { MediaType.APPLICATION_JSON_VALUE }, 
					consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Update Value")
	public ResponseEntity<RestObject> 
	updateValue(@RequestHeader(value="user")  String user,
				@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="key")  String key,
				@RequestBody JSONObject jsonObj)  {

		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		
		try	{
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
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:update", 
					method = RequestMethod.POST, 
					produces = { MediaType.APPLICATION_JSON_VALUE }, 
					consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Update entire object")
	public ResponseEntity<RestObject>
	update(	@RequestHeader(value="user")  String user,
			@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="key")  String key,
			@RequestHeader(value="validFor", required = false)  String validFor,
			@RequestBody JSONObject jsonObj) {
		

		if(key.isBlank() || key.isEmpty()) {
			CacheResponse cResp = new CacheResponse(key,"", user,  "ERROR_KEY");
			return RestObject.retOKWithPayload(cResp, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		
		try {
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
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:get", method = RequestMethod.GET)
	@Operation(summary = "Get an object from the cache")
	public ResponseEntity<RestObject> 
	get(	@RequestHeader(value="user")  String user,
			@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="key")  String key) {
		
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
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
		
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cache/store:isKey", method = RequestMethod.GET)
	@Operation(summary = "Check if an object by key exists in the cache") /*response = String.class*/
	public ResponseEntity<RestObject> 
	isKey(	@RequestHeader(value="user")  String user,
			@RequestHeader(value="requestId") String requestId,
	        @RequestHeader(value="key")  String key) {

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
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

}
