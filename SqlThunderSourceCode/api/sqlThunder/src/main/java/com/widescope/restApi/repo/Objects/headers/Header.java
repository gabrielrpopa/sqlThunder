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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Header {
	
	private int id;
	private String headerName;
	private List<HeaderValue> headerValueList;
	
	public Header() {
		this.setId(-1);
		this.setHeaderName(null);
		this.setHeaderValue(new ArrayList<HeaderValue>());
	}
	
	public Header(	final int id,
					final String headerName,
					final List<HeaderValue> headerValueList) {
		this.setId(id);
		this.setHeaderName(headerName);
		this.setHeaderValue(headerValueList);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getHeaderName() { return headerName; }
	public void setHeaderName(String headerName) { this.headerName = headerName; }

	public List<HeaderValue> getHeaderValue() { return headerValueList; }
	public void setHeaderValue(List<HeaderValue> headerValueList) {	this.headerValueList = headerValueList; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
