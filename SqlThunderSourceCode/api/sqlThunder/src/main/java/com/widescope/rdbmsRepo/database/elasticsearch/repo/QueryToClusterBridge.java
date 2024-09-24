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

package com.widescope.rdbmsRepo.database.elasticsearch.repo;

import com.google.gson.Gson;

public class QueryToClusterBridge {
	private long id;
	private int queryId;
	private int clusterId;
	private String clusterName;
	private int active;
	
	QueryToClusterBridge (	final long id,
							final int queryId,
							final int clusterId,
							final String clusterName,
							final int active) {
		this.setId(id);
		this.setQueryId(queryId);
		this.setClusterId(clusterId);
		this.setClusterName(clusterName);
		this.setActive(active);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	
	public int getQueryId() {	return queryId; }
	public void setQueryId(int queryId) { this.queryId = queryId; }

	public int getClusterId() { return clusterId; }
	public void setClusterId(int clusterId) { this.clusterId = clusterId; }
	
	public String getClusterName() { return clusterName; }
	public void setClusterName(String clusterName) { this.clusterName = clusterName; }

	public int getActive() { return active; }
	public void setActive(int active) { this.active = active; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	
	
}
