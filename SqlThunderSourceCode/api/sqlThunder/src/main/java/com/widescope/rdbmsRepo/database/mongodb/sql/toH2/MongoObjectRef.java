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


package com.widescope.rdbmsRepo.database.mongodb.sql.toH2;

import com.google.gson.Gson;

public class MongoObjectRef {
	private String clusterName;
	private String databaseName;
	private String collectionName;
	private String documentId;
	

	public String getClusterName() { return this.clusterName; }
	public void setClusterName(String clusterName) { this.clusterName = clusterName; }
	
	public String getDatabaseName() { return databaseName; }
	public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
	
	public String getCollectionName() { return this.collectionName; }
	public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
	
	public String getDocumentId() { return this.documentId; }
	public void setDocumentId(String documentId) { this.documentId = documentId; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
