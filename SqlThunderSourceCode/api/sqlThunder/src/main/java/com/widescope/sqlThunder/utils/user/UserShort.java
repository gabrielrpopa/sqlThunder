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

import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.DateTimeUtils;


public class UserShort implements RestInterface {

	private long userId;
	public long getUserId() {	return userId; }
	public void setUserId(final long userId) { this.userId = userId; }
	
	private String userType;
	public String getUserType() {	return userType; }
	public void setUserType(final String userType) { this.userType = userType; }
	
	private String user;
	public String getUser() {	return user; }
	public void setUser(final String user) { this.user = user; }
	
	private String password;
	public String getPassword() {	return password; }
	public void setPassword(final String password) { this.password = password; }
	
	private String active;
	public String getActive() {	return active; }
	public void setActive(final String active) { this.active = active; }
	
	private String session;
	public String getSession() {	return session; }
	public void setSession(final String session) { this.session = session; }
	
	private String baseUrl;
	public String getBaseUrl() {	return baseUrl; }
	public void setBaseUrl(final String baseUrl) { this.baseUrl = baseUrl; }

	private String avatarUrl;
	public String getAvatarUrl() {	return avatarUrl; }
	public void setAvatarUrl(final String avatarUrl) { this.avatarUrl = avatarUrl; }


	private String pns;
	public String getPns() {	return pns; }
	public void setPns(final String pns) { this.pns = pns; }

	private String deviceToken;
	public String getDeviceToken() {	return deviceToken; }
	public void setDeviceToken(final String deviceToken) { this.deviceToken = deviceToken; }



	
	private long previousTimeStamp;
	private long timeStamp;
	public long getTimeStamp() {	return timeStamp; }
	public void setTimeStamp(final long newTimeStamp) { 
		this.previousTimeStamp = this.timeStamp;
		this.timeStamp = newTimeStamp; 
	}
	
	
	public long getTimeStampDiff() { 
		return this.timeStamp - this.previousTimeStamp;
	}
	
	public UserShort(final String session, 
					 final User u) {
		this.userId = u.getId();
		this.userType = u.getUserType();
		this.user = u.getUser();
		this.password = u.getPassword();
		this.active = u.getActive();
		this.session = session;
		this.timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		this.previousTimeStamp = this.timeStamp;
		this.avatarUrl = u.getAvatarUrl();
	}

	
	public UserShort(	final String user,
					 	final String session,
					 	final String baseUrl) {

		this.user =user;
		this.session = session;
		this.baseUrl = baseUrl;
	}

	
}
