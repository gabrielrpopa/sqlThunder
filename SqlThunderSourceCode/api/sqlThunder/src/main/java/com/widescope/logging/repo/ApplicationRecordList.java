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


package com.widescope.logging.repo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class ApplicationRecordList implements RestInterface{
	private List<ApplicationRecord> applicationRecordList;
	public List<ApplicationRecord> getApplicationRecordList() { return applicationRecordList; }
	public void setApplicationRecordList(List<ApplicationRecord> applicationRecordList) { this.applicationRecordList = applicationRecordList; }

	public ApplicationRecordList() {
		this.setApplicationRecordList(new ArrayList<ApplicationRecord>());
	}
	
	public ApplicationRecordList(final List<ApplicationRecord> logRecordFromDbList) {
		this.setApplicationRecordList(logRecordFromDbList);
		
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
