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

public class ElasticIndex {

	private	int indexId;
	private	String indexName;
	private	int clusterId;
	private String description;


	public ElasticIndex(	final int docId, 
							final int clusterId, 
							final String description
							) {
		this.setIndexId(docId);
		this.setClusterId(clusterId);
		this.setDescription(description);
	}

	public int getIndexId() {	return indexId; }
	public void setIndexId(int indexId) { this.indexId = indexId; }
	
	public String getIndexName() {	return indexName; }
	public void setIndexName(String indexName) { this.indexName = indexName; }
	
	public int getClusterId() {	return clusterId; }
	public void setClusterId(int clusterId) { this.clusterId = clusterId; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
