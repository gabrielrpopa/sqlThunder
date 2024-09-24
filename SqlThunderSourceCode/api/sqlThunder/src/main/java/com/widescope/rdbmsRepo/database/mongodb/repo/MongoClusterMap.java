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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class MongoClusterMap implements RestInterface{
	
	private Map<String, MongoClusterRecord> mongoClusterDbMap;
	
	public MongoClusterMap(Map<String, MongoClusterRecord> mongoClusterDbMap) {
		this.setMongoClusterDbMap(mongoClusterDbMap);
	}
	public MongoClusterMap() {
		this.setMongoClusterDbMap(new HashMap<String, MongoClusterRecord>());
	}
	public Map<String, MongoClusterRecord> getMongoClusterDbMap() {
		return mongoClusterDbMap;
	}
	public void setMongoClusterDbMap(Map<String, MongoClusterRecord> mongoClusterDbMap) {
		this.mongoClusterDbMap = mongoClusterDbMap;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
