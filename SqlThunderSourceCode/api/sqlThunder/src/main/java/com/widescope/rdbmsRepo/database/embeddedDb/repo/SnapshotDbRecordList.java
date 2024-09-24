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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnapshotDbRecordList implements RestInterface{

	private List<SnapshotDbRecord> snapshotDbRecordList;

	public SnapshotDbRecordList(List<SnapshotDbRecord> snapshotDbRecordLst)	{
		this.snapshotDbRecordList = new ArrayList<SnapshotDbRecord>();
		this.setSnapshotDbRecordList(snapshotDbRecordLst);
	}
	public SnapshotDbRecordList() {
		this.snapshotDbRecordList = new ArrayList<SnapshotDbRecord>();
	}

	public SnapshotDbRecordList(Map<String, SnapshotDbRecord>  snapshotDbRecordLst) {
		this.snapshotDbRecordList = new ArrayList<SnapshotDbRecord>();
		for (Map.Entry<String, SnapshotDbRecord> element : snapshotDbRecordLst.entrySet()) {
			SnapshotDbRecord snapshotDbRecord = element.getValue();
			snapshotDbRecordList.add(snapshotDbRecord);
	    }
	}

	public List<SnapshotDbRecord> getSnapshotDbRecordList() {	return snapshotDbRecordList; }
	public void setSnapshotDbRecordList(List<SnapshotDbRecord> snapshotDbRecordList) { this.snapshotDbRecordList = snapshotDbRecordList; }
	public void addSnapshotDbRecordList(SnapshotDbRecord snapshotDbRecord) { this.snapshotDbRecordList.add(snapshotDbRecord); }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
