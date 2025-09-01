package com.widescope.rdbmsRepo.database.mongodb.associations;

import com.google.gson.Gson;

public class RepoCollectionTable {

	private long collectionId;
	private long clusterId;
	private String collectionName;
	
	public RepoCollectionTable(	final long collectionId,
								final long clusterId,
								final String collectionName) {
		
		this.setCollectionId(collectionId);
		this.setClusterId(clusterId);
		this.setCollectionName(collectionName);
		
	}

	public long getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(long collectionId) {
		this.collectionId = collectionId;
	}
	public long getClusterId() {
		return clusterId;
	}
	public void setClusterId(long clusterId) {
		this.clusterId = clusterId;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
