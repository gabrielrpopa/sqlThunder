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

/**
 * Manages Temp databases in the cluster/MongoDB server. By default it uses three databases, Temp1, Temp2, Temp3 per each Server/cluster, 
 * so these databases can be placed on different disks if required. Each user will have own collection in each of these databases 
 * @author popa_
 *
 */
public class MongoClusterMaxRecord {

	private String uniqueName;
	private volatile long maxCount; 
	
		
	public MongoClusterMaxRecord(final String uniqueName){
		this.setMaxCount(0);
		this.setUniqueName(uniqueName);
	}

	public long getMaxCount() {	return maxCount;	}
	public void setMaxCount(long maxCount) {	this.maxCount = maxCount;	}

	public String getUniqueName() {	return uniqueName; }
	public void setUniqueName(String uniqueName) { this.uniqueName = uniqueName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
			
}
