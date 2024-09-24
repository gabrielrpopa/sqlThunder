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

import java.util.ArrayList;
import java.util.List;


public class ElasticCluster {

	private	int clusterId;
	private String uniqueName;
	private String description;
	private List<ElasticHost> listElasticHosts;
	
	

	public ElasticCluster(	final int clusterId, 
							final String uniqueName,
							final String description,
							final List<ElasticHost> listElasticHosts,
							final List<ElasticIndex> listElasticDocs
							) {
		this.setClusterId(clusterId);
		this.setUniqueName(uniqueName);
		this.setDescription(description);
		this.setListElasticHosts(listElasticHosts);
	}
	
	public ElasticCluster(	final int clusterId, 
							final String uniqueName,
							final String description
							) {
		this.setClusterId(clusterId);
		this.setUniqueName(uniqueName);
		this.setDescription(description);
		this.setListElasticHosts(new ArrayList<ElasticHost>());
	}
	
	public ElasticCluster()	{
		this.setClusterId(-1);
		this.setUniqueName(null);
		this.setDescription(null);
		this.setListElasticHosts(new ArrayList<ElasticHost>());
	}

	public int getClusterId() {	return clusterId; }
	public void setClusterId(int clusterId) { this.clusterId = clusterId; }

	public String getUniqueName() { return uniqueName; }
	public void setUniqueName(String uniqueName) { this.uniqueName = uniqueName; }

	public List<ElasticHost> getListElasticHosts() { return listElasticHosts; }
	public void setListElasticHosts(List<ElasticHost> listElasticHosts) { this.listElasticHosts = listElasticHosts; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
