/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.rest;

import java.util.UUID;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;




public class RestObjectShort {
	public RestObjectShort(String methodName) {
		this.message = null;
		this.errorMessage = "";
		this.debugMessage = "";
		this.errorCode = ErrorCode.OK;
		this.errorSource = Sources.NONE;
		this.timestamp = new java.util.Date().toString();
		this.errorSeverity = ErrorSeverity.NONE;
		this.objectType = methodName;
		this.setRequestId(UUID.randomUUID().toString());
	}
	
	public RestObjectShort(String methodName, String requestId) {
		this.message = null;
		this.errorMessage = "";
		this.debugMessage = "";
		this.errorCode = ErrorCode.OK;
		this.errorSource = Sources.NONE;
		this.timestamp = new java.util.Date().toString();
		this.errorSeverity = ErrorSeverity.NONE;
		this.objectType = methodName;
		this.setRequestId(requestId);
	}
	
	public RestObjectShort(String message, String methodName, String requestId) {
		this.message = message;
		this.errorMessage = "";
		this.debugMessage = "";
		this.errorCode = ErrorCode.OK;
		this.errorSource = Sources.NONE;
		this.timestamp = new java.util.Date().toString();
		this.errorSeverity = ErrorSeverity.NONE;
		this.objectType = methodName;
		this.setRequestId(requestId);
	}
	
	public RestObjectShort()	{
		this.message = null;
		this.errorMessage = "";
		this.debugMessage = "";
		this.errorCode = ErrorCode.OK;
		this.errorSource = Sources.NONE;
		this.timestamp = new java.util.Date().toString();
		this.errorSeverity = ErrorSeverity.NONE;
		this.objectType = "";
		this.setRequestId(UUID.randomUUID().toString());
	}
	
	public RestObjectShort(	final String message,
							final String requestId, 
							final String errorMessage,
							final String debugMessage, 
							final int errorCode, 
							final String errorSource, 
							final String errorSeverity,
							final String objectType 
							) {
		this.message = message;
		this.requestId = requestId;
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.debugMessage = debugMessage;
		this.errorSource = errorSource;
		this.timestamp = new java.util.Date().toString();
		this.errorSeverity = errorSeverity;
		this.objectType=objectType;
	}
	
	
	private String errorMessage;
	public String getErrorMessage() {	return errorMessage; }
	public void setErrorMessage(final String errorMessage) { this.errorMessage = errorMessage; }
	
	private String debugMessage;
	public String getDebugMessage() {	return debugMessage; }
	public void setDebugMessage(final String debugMessage) { this.debugMessage = debugMessage; }
	
	private int errorCode;
	public int getErrorCode() {	return errorCode; }
	public void setErrorCode(final int errorCode) { this.errorCode = errorCode; }
	
	private String errorSource;
	public String getErrorSource() {	return errorSource; }
	public void setErrorSource(final String errorSource) { this.errorSource = errorSource; }
	
	
	public String requestId;
	public String getRequestId() {	return requestId; }
	public void setRequestId(final String requestId) { this.requestId = requestId; }
	
	public String timestamp;
	public String getTimestamp() {	return timestamp; }
	public void setTimestamp(final String timestamp) { this.timestamp = timestamp; }
	
	private String message;
	public String getPayload() {	return message; }
	public void setPayload(final String message) { this.message = message; }
	

	private String errorSeverity;
	public String getErrorSeverity() {	return errorSeverity; }
	public void setErrorSeverity(final String errorSeverity) { this.errorSeverity = errorSeverity; }
	
	private String objectType;
	public String getObjectType() {	return objectType; }
	public void setObjectType(final String objectType) { this.objectType = objectType; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static RestObject getRestObject() {
		return new RestObject();
	}
	
	
	public static ResponseEntity<RestObjectShort> 
	retOK(	final String requestId, final String methodName) {
		RestObjectShort transferableObject = new RestObjectShort("OK", 
														requestId, 
														"",  
														"", 
														ErrorCode.OK, 
														Sources.NONE, 
														ErrorSeverity.NONE, 
														methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
	

	
	public static ResponseEntity<RestObjectShort> 
	retOKWithPayload(	final String payload,
						final String requestId, 
						final String methodName) {
		RestObjectShort transferableObject = new RestObjectShort(payload, 
											requestId, 
											"",  
											"", 
											ErrorCode.OK, 
											Sources.NONE, 
											ErrorSeverity.NONE, 
											methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
	
	public static ResponseEntity<RestObjectShort> 
	retExceptionWithPayload(final String payload,
							final String requestId, 
							final String methodName) {
		RestObjectShort transferableObject = new RestObjectShort(	payload, 
														requestId, 
														"",  
														"", 
														ErrorCode.ERROR, 
														Sources.SQLTHUNDER, 
														ErrorSeverity.LOW, 
														methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
	
	public static ResponseEntity<RestObjectShort> 
	retAuthError(final String requestId, final String methodName) {
		RestObjectShort transferableObject = new RestObjectShort("AUTH_ERROR", 
														requestId, 
														"Wrong User Name or Password",  
														"User name and password does not correspond to any user", 
														ErrorCode.ERROR, 
														Sources.SQLTHUNDER, 
														ErrorSeverity.LOW, 
														methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}

	public static ResponseEntity<RestObjectShort>
	retAuthError(final String requestId) {
		RestObjectShort transferableObject = new RestObjectShort("AUTH_ERROR",
																requestId,
																"Wrong User Name or Password",
																"User name and password does not correspond to any user",
																ErrorCode.ERROR,
																Sources.SQLTHUNDER,
																ErrorSeverity.LOW,
																"");
		return new ResponseEntity<> (transferableObject, HttpStatus.OK);
	}
	
	public static ResponseEntity<RestObjectShort> 
	retException(	final String message,
					final String requestId, 
					final String methodName,
					final String err) {
		RestObjectShort transferableObject = new RestObjectShort(	message, 
													requestId, 
													err,  
													err, 
													ErrorCode.ERROR, 
													Sources.SQLTHUNDER, 
													ErrorSeverity.MEDIUM, 
													methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
	
	
	
	public static ResponseEntity<RestObjectShort> 
	retException(	final String requestId, 
					final String methodName, 
					final String err) {
		RestObjectShort transferableObject = new RestObjectShort("EXCEPTION", 
														requestId, 
														err,  
														err, 
														ErrorCode.ERROR, 
														Sources.SQLTHUNDER, 
														ErrorSeverity.MEDIUM, 
														methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
	
	
	public static ResponseEntity<RestObjectShort> 
	retFatal(	final String requestId, 
				final String methodName, 
				final String err, 
				final String debugError) {
		RestObjectShort transferableObject = new RestObjectShort("FATAL", 
														requestId, 
														err,  
														debugError, 
														ErrorCode.ERROR, 
														Sources.SQLTHUNDER, 
														ErrorSeverity.HIGH, 
														methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
	
	public static ResponseEntity<RestObjectShort> 
	retFatal(	final String requestId, 
				final String methodName, 
				final String err) {
		RestObjectShort transferableObject = new RestObjectShort("FATAL", 
														requestId, 
														err,  
														err, 
														ErrorCode.ERROR, 
														Sources.SQLTHUNDER, 
														ErrorSeverity.HIGH, 
														methodName);
		return new ResponseEntity< RestObjectShort > (transferableObject, HttpStatus.OK);
	}
}

