/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
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

package com.widescope.rdbmsRepo.database.warehouse.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class RepoCubeRecordList implements RestInterface{
	
	private List<RepoCubeRecord> repoCubeRecordList;
	
	public RepoCubeRecordList(List<RepoCubeRecord> mongoClusterDbLst)	{
		this.repoCubeRecordList = new ArrayList<RepoCubeRecord>();
		this.setRepoCubeRecordList(mongoClusterDbLst);
	}
	public RepoCubeRecordList() {
		this.repoCubeRecordList = new ArrayList<RepoCubeRecord>();
	}
	
	public RepoCubeRecordList(Map<String, RepoCubeRecord>  mongoDbMap) {
		this.repoCubeRecordList = new ArrayList<RepoCubeRecord>();
		for (Map.Entry<String, RepoCubeRecord> element : mongoDbMap.entrySet()) {
			RepoCubeRecord mongoClusterRecord = element.getValue();
			repoCubeRecordList.add(mongoClusterRecord);
	    }
	}

	public List<RepoCubeRecord> getRepoCubeRecordList() {	return repoCubeRecordList; }
	public void setRepoCubeRecordList(List<RepoCubeRecord> repoCubeRecordList) { this.repoCubeRecordList = repoCubeRecordList; }
	public void addRepoCubeRecord(RepoCubeRecord repoCubeRecord) { this.repoCubeRecordList.add(repoCubeRecord); }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
