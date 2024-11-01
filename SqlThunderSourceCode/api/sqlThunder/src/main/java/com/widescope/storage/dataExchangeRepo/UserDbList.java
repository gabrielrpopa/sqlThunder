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

package com.widescope.storage.dataExchangeRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class UserDbList implements RestInterface{
	
	private List<UserDbRecord> userDbLst;
	
	public UserDbList(List<UserDbRecord> userDbLst) {
		this.setUserDbLst(userDbLst);
	}
			
	public UserDbList() {
		this.setUserDbLst(new ArrayList<UserDbRecord>());
	}
	public List<UserDbRecord> getUserDbLst() 
	{ return userDbLst; }
	
	public void setUserDbLst(List<UserDbRecord> userDbListLst) 
	{ this.userDbLst = userDbListLst; }
	
	public void addUser(UserDbRecord u) 
	{ this.userDbLst.add(u); }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
