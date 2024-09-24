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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class ElasticClusterList implements RestInterface{
	
	private List<ElasticCluster> elasticClusterDbLst;

	public ElasticClusterList(Map<String, ElasticCluster>  elasticClusterMap)	{
		this.elasticClusterDbLst = new ArrayList<ElasticCluster>();
		for (Map.Entry<String, ElasticCluster> element : elasticClusterMap.entrySet()) 
			this.elasticClusterDbLst.add(element.getValue());
	    
	}

	public List<ElasticCluster> getElasticClusterDbLst() { return elasticClusterDbLst; }
	public void setElasticClusterDbLst(List<ElasticCluster> elasticClusterDbLst) { this.elasticClusterDbLst = elasticClusterDbLst; }
	public void addElasticClusterDbLst(ElasticCluster elasticCluster) { this.elasticClusterDbLst.add(elasticCluster); }

	
	public void blockPassword() {
		for(ElasticCluster x: elasticClusterDbLst) {
			for(ElasticHost h: x.getListElasticHosts()) {
				h.setTunnelRemoteRsaKey("**********");
				h.setTunnelRemoteUserPassword("**********");
			}
			
		}
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
