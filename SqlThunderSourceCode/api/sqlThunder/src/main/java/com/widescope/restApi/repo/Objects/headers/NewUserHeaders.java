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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class NewUserHeaders {
	private List<NewUserHeader> listOfNewHeaders = null;
	public NewUserHeaders() {
		this.setListOfNewHeaders(new ArrayList<NewUserHeader>());
	}
	
	public NewUserHeaders(final List<NewUserHeader> listOfNewHeaders) {
		this.setListOfNewHeaders(listOfNewHeaders);
	}

	public List<NewUserHeader> getListOfNewHeaders() { return listOfNewHeaders; }
	public void setListOfNewHeaders(List<NewUserHeader> listOfNewHeaders) { this.listOfNewHeaders = listOfNewHeaders; }
	public void addNewHeader(NewUserHeader newHeader) { this.listOfNewHeaders.add(newHeader); }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
	
