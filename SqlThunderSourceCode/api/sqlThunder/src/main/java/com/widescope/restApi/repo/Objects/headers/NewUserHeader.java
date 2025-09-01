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



package com.widescope.restApi.repo.Objects.headers;

import java.util.List;

import com.google.gson.Gson;

public class NewUserHeader {
	
	private long id;
	private String headerName;
	private long userId;
	private List<HeaderValue> listOfHeaderValue;

	public NewUserHeader(	final long id,
							final String headerName,
							final long userId,
							final List<HeaderValue> listOfHeaderValue) {
		this.setId(id);
		this.setHeaderName(headerName);
		this.setUserId(userId);
		this.setListOfHeaderValue(listOfHeaderValue);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getHeaderName() { return headerName; }
	public void setHeaderName(String headerName) { this.headerName = headerName; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public List<HeaderValue> getListOfHeaderValue() { return listOfHeaderValue; }
	public void setListOfHeaderValue(List<HeaderValue> listOfHeaderValue) {	this.listOfHeaderValue = listOfHeaderValue; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
