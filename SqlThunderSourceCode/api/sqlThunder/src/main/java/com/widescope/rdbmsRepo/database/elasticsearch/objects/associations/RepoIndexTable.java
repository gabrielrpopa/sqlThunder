package com.widescope.rdbmsRepo.database.elasticsearch.objects.associations;

import com.google.gson.Gson;

public class RepoIndexTable {

	private long indexId;
	private long clusterId;
	private String indexName;
	
	public RepoIndexTable(	final long indexId,
							final long clusterId,
							final String indexName) {
		this.setIndexId(indexId);
		this.setClusterId(clusterId);
		this.setIndexName(indexName);
	}

	public long getIndexId() {
		return indexId;
	}
	public void setIndexId(long indexId) {
		this.indexId = indexId;
	}
	public long getClusterId() {
		return clusterId;
	}
	public void setClusterId(long clusterId) {
		this.clusterId = clusterId;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
