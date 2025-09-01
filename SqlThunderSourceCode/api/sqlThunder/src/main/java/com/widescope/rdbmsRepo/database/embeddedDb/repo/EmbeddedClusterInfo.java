package com.widescope.rdbmsRepo.database.embeddedDb.repo;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.clusterRule.ClusterRule;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.sqlRule.SqlRule;

public class EmbeddedClusterInfo {
	private ClusterRule clusterRule;
	private SqlRule sqlRule;	
	
	public EmbeddedClusterInfo(	final ClusterRule clusterRule,
								final SqlRule sqlRule) {
		this.setClusterRule(clusterRule);
		this.setSqlRule(sqlRule);
	}

	public ClusterRule getClusterRule() {
		return clusterRule;
	}
	public void setClusterRule(ClusterRule clusterRule) {
		this.clusterRule = clusterRule;
	}
	public SqlRule getSqlRule() {
		return sqlRule;
	}
	public void setSqlRule(SqlRule sqlRule) {
		this.sqlRule = sqlRule;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
