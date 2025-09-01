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


package com.widescope.rdbmsRepo.database.embeddedDb.repo;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RdbmsExecutedQueryList implements RestInterface{

	private List<RdbmsExecutedQuery> rdbmsExecutedQueryList;

	public RdbmsExecutedQueryList(List<RdbmsExecutedQuery> snapshotDbRecordLst)	{
		this.rdbmsExecutedQueryList = new ArrayList<>();
		this.setRdbmsExecutedQueryList(snapshotDbRecordLst);
	}
	public RdbmsExecutedQueryList() {
		this.rdbmsExecutedQueryList = new ArrayList<>();
	}

	public RdbmsExecutedQueryList(Map<String, RdbmsExecutedQuery>  snapshotDbRecordLst) {
		this.rdbmsExecutedQueryList = new ArrayList<>();
		for (Map.Entry<String, RdbmsExecutedQuery> element : snapshotDbRecordLst.entrySet()) {
			RdbmsExecutedQuery snapshotDbRecord = element.getValue();
			rdbmsExecutedQueryList.add(snapshotDbRecord);
	    }
	}

	public List<RdbmsExecutedQuery> getRdbmsExecutedQueryList() {	return rdbmsExecutedQueryList; }
	public void setRdbmsExecutedQueryList(List<RdbmsExecutedQuery> rdbmsExecutedQueryList) { this.rdbmsExecutedQueryList = rdbmsExecutedQueryList; }
	public void addRdbmsExecutedQuery(RdbmsExecutedQuery snapshotDbRecord) { this.rdbmsExecutedQueryList.add(snapshotDbRecord); }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
