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

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class UserRestApiRequestDetail implements RestInterface{

	private long id;
	private String name;
	private String description;
	private int verbId;
	private long userId;
	private UserRestApiRequest userRequest;
	
	public UserRestApiRequestDetail() {
		this.setId(-1);
		this.setName("");
		this.setDescription("");
		this.setVerbId(-1);
		this.setUserId(-1);
		this.setUserRequest(new UserRestApiRequest());
	}
	
	public UserRestApiRequestDetail(final long id,
							 final String name,
							 final String description,
							 final int verbId,
							 final long userId,
							 final UserRestApiRequest userRequest) {
		
		this.setId(id);
		this.setName(name);
		this.setDescription(description);
		this.setVerbId(verbId);
		this.setUserId(userId);
		this.setUserRequest(userRequest);
		
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public int getVerbId() {	return verbId; }
	public void setVerbId(int requestId) { this.verbId = requestId; }
		
	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public UserRestApiRequest getUserRequest() { return userRequest; }
	public void setUserRequest(UserRestApiRequest userRequest) { this.userRequest = userRequest; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
