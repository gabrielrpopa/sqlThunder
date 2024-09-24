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


package com.widescope.restApi.repo.Objects.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class UserAuthorization {
	private int id;
	private String authorizationName;
	private AuthorizationValue authorizationValue;
	
	public UserAuthorization() {
		this.setId(-1);
		this.setAuthorizationName(null);
		this.setAuthorizationValue(new AuthorizationValue());
	}
	
	public UserAuthorization(	final int id,
							final String headerName,
							final AuthorizationValue authorizationValue) {
		this.setId(id);
		this.setAuthorizationName(headerName);
		this.setAuthorizationValue(authorizationValue);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getAuthorizationName() { return authorizationName; }
	public void setAuthorizationName(String authorizationName) { this.authorizationName = authorizationName; }

	public AuthorizationValue getAuthorizationValue() { return authorizationValue; }
	public void setAuthorizationValue(AuthorizationValue authorizationValue) { this.authorizationValue = authorizationValue; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
