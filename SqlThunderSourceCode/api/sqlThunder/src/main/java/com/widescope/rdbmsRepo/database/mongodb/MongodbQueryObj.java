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


package com.widescope.rdbmsRepo.database.mongodb;

import com.google.gson.Gson;

public class MongodbQueryObj {
	
	private String type;
	private String clusterName;
	private String databaseName;
	private String collectionName;
	private String obj;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getObj() {
		return obj;
	}
	public void setObj(String obj) {
		this.obj = obj;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public MongodbQueryObj(final String type, 
					final String databaseName,
					final String clusterName,
					final String collectionName,
					final String obj) {
		this.setType(type);
		this.setClusterName(clusterName);
		this.setDatabaseName(databaseName);
		this.setCollectionName(collectionName);
		this.setObj(obj);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
