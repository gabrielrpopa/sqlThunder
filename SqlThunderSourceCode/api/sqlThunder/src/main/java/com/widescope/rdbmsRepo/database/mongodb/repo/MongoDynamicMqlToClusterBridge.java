package com.widescope.rdbmsRepo.database.mongodb.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class MongoDynamicMqlToClusterBridge implements RestInterface {
	
	
	private long id;
	private long  mqlId;
	private long clusterId;
	private String clusterName;
	private int active;
	
	public MongoDynamicMqlToClusterBridge(	final long id,
											final long  mqlId,
											final long clusterId,
											final String clusterName,
											final int active) {
		this.setId(id);
		this.setMqlId(mqlId);
		this.setClusterId(clusterId);
		this.setClusterName(clusterName);
		this.setActive(active);
	}
	
	public MongoDynamicMqlToClusterBridge() {
		this.setId(-1);
		this.setMqlId(-1);
		this.setClusterId(-1);
		this.setClusterName("");
		this.setActive(0);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id;	}

	public long getMqlId() { return mqlId; }
	public void setMqlId(long mqlId) { this.mqlId = mqlId; }

	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	
	public String getClusterName() { return clusterName; }
	public void setClusterName(String clusterName) { this.clusterName = clusterName; }

	public int getActive() { return active; }
	public void setActive(int active) { this.active = active; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
