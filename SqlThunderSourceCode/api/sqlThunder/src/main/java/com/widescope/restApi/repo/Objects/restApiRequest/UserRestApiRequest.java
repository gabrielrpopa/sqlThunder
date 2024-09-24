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


package com.widescope.restApi.repo.Objects.restApiRequest;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;
import com.widescope.restApi.repo.Objects.auth.UserAuthorization;
import com.widescope.restApi.repo.Objects.body.UserBodyApplication;
import com.widescope.restApi.repo.Objects.body.UserBodyFile;
import com.widescope.restApi.repo.Objects.body.UserBodyMultipart;
import com.widescope.restApi.repo.Objects.body.UserBodyRawInput;
import com.widescope.restApi.repo.Objects.headers.UserHeader;

public class UserRestApiRequest implements RestInterface{
	private long id;
	private UserBodyApplication bodiesApplication;
	private UserBodyRawInput bodyRawInput;
	private List<UserBodyMultipart> bodyMultiparts;
    private UserBodyFile bodyFile;
	private List<UserHeader> userHeaders;
	private UserAuthorization userAuthorization;
	
	public UserRestApiRequest() {
		this.setId(-1);
		this.setUserBodyApplication(new UserBodyApplication());
		this.setBodyRawInput(new UserBodyRawInput());
		this.setBodyMultiparts(new ArrayList<UserBodyMultipart>());
		this.setBodyFile(new UserBodyFile());
		this.setUserHeaders(new ArrayList<>());
		this.setUserAuthorization(new UserAuthorization());
	}


	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	
	public UserBodyApplication getUserBodyApplication() { return bodiesApplication; }
	public void setUserBodyApplication(UserBodyApplication bodiesApplication) { this.bodiesApplication = bodiesApplication; }
			
	public UserBodyRawInput getBodyRawInput() { return bodyRawInput; }
	public void setBodyRawInput(UserBodyRawInput bodyRawInput) { this.bodyRawInput = bodyRawInput; }
	
	public List<UserBodyMultipart> getBodyMultiparts() { return bodyMultiparts; }
	public void setBodyMultiparts(List<UserBodyMultipart> bodyMultiparts) { this.bodyMultiparts = bodyMultiparts; }


	public UserBodyFile getBodyFile() {	return bodyFile; }
	public void setBodyFile(UserBodyFile bodyFile) { this.bodyFile = bodyFile; }
	
	public List<UserHeader> getUserHeaders() { return userHeaders; }
	public void setUserHeaders(List<UserHeader> headers) { this.userHeaders = headers; }

	public UserAuthorization getUserAuthorization() { return userAuthorization; }
	public void setUserAuthorization(UserAuthorization authorization) { this.userAuthorization = authorization; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static UserRestApiRequest toUserRestApiRequest(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, UserRestApiRequest.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}





	

		
}
