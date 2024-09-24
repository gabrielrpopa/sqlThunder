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


package com.widescope.rdbmsRepo.database.mongodb.repo.timestamp;

import com.google.gson.Gson;

public class CollectionTimestamp {

	private String objectId;
	private long timeStamp;
	private String dbName;
	private String collectionName;
	
	public CollectionTimestamp(final String objectId, final long timeStamp, final String dbName, final String collectionName) {
		this.setObjectId(objectId);
		this.setTimeStamp(timeStamp);
		setDbName(dbName);
		setCollectionName(collectionName);
	}

	public String getObjectId() { return objectId; }
	public void setObjectId(String objectId) { this.objectId = objectId; }
	public long getTimeStamp() { return timeStamp; }
	public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }
	public String getDbName() {	return dbName; }
	public void setDbName(String dbName) { this.dbName = dbName; }
	public String getCollectionName() { return collectionName; }
	public void setCollectionName(String collectionName) { this.collectionName = collectionName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
