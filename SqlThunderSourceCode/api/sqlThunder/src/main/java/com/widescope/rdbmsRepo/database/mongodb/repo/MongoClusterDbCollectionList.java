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

package com.widescope.rdbmsRepo.database.mongodb.repo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class MongoClusterDbCollectionList implements RestInterface {
	
	private List<MongoCollectionInfo> mongoClusterDbCollectionLst;
	
	public MongoClusterDbCollectionList(List<MongoCollectionInfo> mongoClusterDbCollectionLst) {
		this.setMongoClusterDbCollectionLst(mongoClusterDbCollectionLst);
	}
	public MongoClusterDbCollectionList()
	{
		this.setMongoClusterDbCollectionLst(new ArrayList<MongoCollectionInfo>());
	}
	public List<MongoCollectionInfo> getMongoClusterDbCollectionLst() { return mongoClusterDbCollectionLst; }
	public void setMongoClusterDbCollectionLst(List<MongoCollectionInfo> mongoClusterDbCollectionLst) { this.mongoClusterDbCollectionLst = mongoClusterDbCollectionLst; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}