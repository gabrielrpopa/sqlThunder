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

package com.widescope.rdbmsRepo.database.embeddedDb.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.clusterRule.ClusterRule;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.sqlRule.SqlRule;

public class EmbeddedClusterRecord implements RestInterface{
    private long clusterId;
    private	String clusterName;
    private	String description;
    private ClusterRule clusterRule;
    private SqlRule sqlRule;
    
    public EmbeddedClusterRecord() { }

    public EmbeddedClusterRecord(	final long clusterId,
		                            final String clusterName,
		                            final String description,
		                            final ClusterRule clusterRule,
		                            final SqlRule sqlRule
		                            ) {
        this.setClusterId(clusterId);
        this.setClusterName(clusterName);
        this.setDescription(description);
        this.setClusterRule(clusterRule);
        this.setSqlRule(sqlRule);
    }

	public long getClusterId() {	return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	public String getClusterName() { return clusterName; }
	public void setClusterName(String clusterName) { this.clusterName = clusterName; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public ClusterRule getClusterRule() { return clusterRule; }
    public void setClusterRule(ClusterRule clusterRule) { this.clusterRule = clusterRule; }

	public SqlRule getSqlRule() { return sqlRule; }
	public void setSqlRule(SqlRule sqlRule) { this.sqlRule = sqlRule; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
