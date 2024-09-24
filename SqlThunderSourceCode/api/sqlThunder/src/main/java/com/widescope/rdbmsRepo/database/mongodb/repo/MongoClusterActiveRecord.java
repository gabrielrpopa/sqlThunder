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

public class MongoClusterActiveRecord {
	private String uniqueNameActiveConnection;
	private String uniqueNameActiveTemp;
	private String uniqueNameActiveUser;
	
	private long maxActiveTemp;
	private long maxActiveUser;

	public MongoClusterActiveRecord() {
		this.setUniqueNameActiveUser(null);
		this.setUniqueNameActiveTemp(null);
		this.setUniqueNameActiveUser(null);
		this.setMaxActiveTemp(0);
		this.setMaxActiveUser(0);
	}

	public String getUniqueNameActiveConnection() {	return uniqueNameActiveConnection; }
	public void setUniqueNameActiveConnection(String uniqueNameActiveConnection) { this.uniqueNameActiveConnection = uniqueNameActiveConnection; }

	public String getUniqueNameActiveTemp() { return uniqueNameActiveTemp; }
	public void setUniqueNameActiveTemp(String uniqueNameActiveTemp) { this.uniqueNameActiveTemp = uniqueNameActiveTemp; }

	public String getUniqueNameActiveUser() { return uniqueNameActiveUser; }
	public void setUniqueNameActiveUser(String uniqueNameActiveUser) { this.uniqueNameActiveUser = uniqueNameActiveUser; }

	public long getMaxActiveTemp() { return maxActiveTemp; }
	public void setMaxActiveTemp(long maxActiveTemp) { this.maxActiveTemp = maxActiveTemp; }
	public void addMaxActiveTemp(long maxActiveTemp) { this.maxActiveTemp+= maxActiveTemp; }
	
	public long getMaxActiveUser() { return maxActiveUser; }
	public void setMaxActiveUser(long maxActiveUser) { this.maxActiveUser = maxActiveUser; }
	public void addMaxActiveUser(long maxActiveTemp) { this.maxActiveUser+= maxActiveUser; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
