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

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class UserRestApiRequestDetailList implements RestInterface{
	
	private List<UserRestApiRequestDetail> listOfUserRequestDetail;
	
	public UserRestApiRequestDetailList() {
		this.setListOfUserRequestDetail(new ArrayList<UserRestApiRequestDetail>());
	}
	
	public UserRestApiRequestDetailList(final List<UserRestApiRequestDetail> listOfUserRequestDetail) {
		this.setListOfUserRequestDetail(listOfUserRequestDetail);
	}

	public List<UserRestApiRequestDetail> getListOfUserRequestDetail() {
		return listOfUserRequestDetail;
	}

	public void setListOfUserRequestDetail(List<UserRestApiRequestDetail> listOfUserRequestDetail) {
		this.listOfUserRequestDetail = listOfUserRequestDetail;
	}
	
	public void addUserRequestDetail(UserRestApiRequestDetail userRequestDetail) {
		this.listOfUserRequestDetail.add(userRequestDetail);
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
