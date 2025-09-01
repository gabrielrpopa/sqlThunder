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

package com.widescope.storage.internalRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.persistence.execution.AccessRefPrivilege;
import com.widescope.sqlThunder.rest.RestInterface;

public class InternalStoragePrivilegeList implements RestInterface{
	
	private List<AccessRefPrivilege> storagePrivRepoDbRecordbLst;
	
	public InternalStoragePrivilegeList(List<AccessRefPrivilege> storagePrivRepoDbRecordbLst) {
		this.setStoragePrivRepoDbRecordLst(storagePrivRepoDbRecordbLst);
	}
			
	public InternalStoragePrivilegeList() {
		this.setStoragePrivRepoDbRecordLst(new ArrayList<AccessRefPrivilege>());
	}
	public List<AccessRefPrivilege> getStoragePrivRepoDbRecordLst() { return storagePrivRepoDbRecordbLst; }
	public void setStoragePrivRepoDbRecordLst(List<AccessRefPrivilege> storagePrivRepoDbRecordbLst) { this.storagePrivRepoDbRecordbLst = storagePrivRepoDbRecordbLst; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
