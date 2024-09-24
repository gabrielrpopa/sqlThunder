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
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class MongoClusterList implements RestInterface {
	
	private List<MongoClusterRecord> mongoClusterLst;

	public MongoClusterList(Map<String, MongoClusterRecord>  mongoDbMap) {
		this.mongoClusterLst = new ArrayList<MongoClusterRecord>();
		for (Map.Entry<String, MongoClusterRecord> element : mongoDbMap.entrySet()) {
			MongoClusterRecord mongoClusterRecord = element.getValue();
			mongoClusterLst.add(mongoClusterRecord);
	    }
	}

	public List<MongoClusterRecord> getMongoClusterLst() {	return mongoClusterLst; }
	public void setMongoClusterLst(List<MongoClusterRecord> mongoClusterLst) { this.mongoClusterLst = mongoClusterLst; }
	public void addMongoClusterLst(MongoClusterRecord mongoCluster) { this.mongoClusterLst.add(mongoCluster); }
	
	
	public void blockPassword() {
		for(MongoClusterRecord x: mongoClusterLst) {
			x.setTunnelRemoteRsaKey("**********");
			x.setTunnelRemoteUserPassword("**********");
		}
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
