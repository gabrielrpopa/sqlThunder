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

public class EmbeddedDbRecordList implements RestInterface{

	private List<EmbeddedDbRecord> embeddedDbRecordList;
	public EmbeddedDbRecordList() {
		this.embeddedDbRecordList = new ArrayList<EmbeddedDbRecord>();
	}

	public List<EmbeddedDbRecord> getEmbeddedDbRecordList() {	return embeddedDbRecordList; }
	public void setEmbeddedDbRecordList(List<EmbeddedDbRecord> embeddedDbRecordLst) { this.embeddedDbRecordList = embeddedDbRecordLst; }
	public void addEmbeddedDbRecord(EmbeddedDbRecord embeddedDbRecordLst) { this.embeddedDbRecordList.add(embeddedDbRecordLst); }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	
}