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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class Authorizations {
	private List<Authorization> listOfAuthorizations = null;
	
	public Authorizations() {
		this.setListOfAuthorizations(new ArrayList<Authorization>());
		
	}
	
	public Authorizations(final List<Authorization> listOfAuthorizations) {
		this.setListOfAuthorizations(listOfAuthorizations);
	}

	public List<Authorization> getListOfAuthorizations() { return listOfAuthorizations; }
	public void setListOfAuthorizations(List<Authorization> listOfAuthorizations) { this.listOfAuthorizations = listOfAuthorizations; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
	
