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

import com.google.gson.Gson;

public class MongoClusterDbRecord {

	private	int id;
	private String uniqueName;
	private	String connString;
	
	private String storageType;
    private int controllerId;
    private long startPeriod;  
	private long endPeriod;
	
	
	public MongoClusterDbRecord(final int id, 
								final String uniqueName, 
								final String connString,
								String storageType,
								int controllerId,
								long startPeriod,
								long endPeriod
								)
	{
		this.setId(id);
		this.setUniqueName(uniqueName);
		this.setConnString(connString);
		this.setStorageType(storageType);
		this.setControllerId(controllerId);
		this.setStartPeriod(startPeriod);
		this.setEndPeriod(endPeriod);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getUniqueName() { return uniqueName; }
	public void setUniqueName(String uniqueName) { this.uniqueName = uniqueName; }

	public String getConnString() { return connString; }
	public void setConnString(String connString) { this.connString = connString; }
	
	public String getStorageType() { return storageType; }
	public void setStorageType(String storageType) { this.storageType = storageType; }

	public int getControllerId() { return controllerId; }
	public void setControllerId(int controllerId) { this.controllerId = controllerId; }

	public long getStartPeriod() { return startPeriod; }
	public void setStartPeriod(long startPeriod) { this.startPeriod = startPeriod; }

	public long getEndPeriod() { return endPeriod; }
	public void setEndPeriod(long endPeriod) { this.endPeriod = endPeriod; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
