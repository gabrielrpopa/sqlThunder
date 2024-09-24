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

package com.widescope.sqlThunder.utils.user;

import com.widescope.rest.RestInterface;

public class UserSession implements RestInterface {
	private String user;
	private String userType;
	private String sessionId;
	private int errorNumber;
	private String errorMessage;
	
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
	public String getUserType() {	return userType;}
	public void setUserType(String userType) { this.userType = userType; }
	
	private String firstName;
	public String getFirstName() {	return firstName; }
	public void setFirstName(final String firstName) { this.firstName = firstName; }
	
	private String lastName;
	public String getLastName() {	return lastName; }
	
	public void setLastName(final String lastName) { this.lastName = lastName; }
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public int getErrorNumber() { return errorNumber; }
	public void setErrorNumber(int errorNumber) { this.errorNumber = errorNumber; }
	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
	
	
	public UserSession(	final User u, 
						final int errorNumber, 
						final String errorMessage) {
		this.user = u.getUser();
		this.userType = u.getUserType();
		this.sessionId = u.getSession();
		this.firstName = u.getFirstName();
		this.lastName = u.getLastName();
		this.errorNumber = errorNumber;
		this.errorMessage = errorMessage;
	}
}
