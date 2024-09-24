package com.widescope.rdbmsRepo.database.embeddedDb.embedded;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class EmbeddedInMemCluster {
	
	private Map<String, EmbeddedInterface> cluster;  /*H2 fileName/Object associated */
	private long clusterId;  
		
	private long timestamp;
	private String sessionId;
	private String requestId;
	private String userId;
	private String comment;
	

	public EmbeddedInMemCluster(final long clusterId_,
								final String sessionId_,
								final String requestId_,
								final String userId_,
								final String comment_) {
		this.setClusterId(clusterId_);
		this.setCluster(new HashMap<>());
		this.setSessionId(sessionId_);
		this.setRequestId(requestId_);
		this.setUserId(userId_);
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
		this.setComment(comment_);
	}
	
	
	public EmbeddedInMemCluster(final long clusterId_, 
								final Map<String, EmbeddedInterface> cluster_,
								final String sessionId_,
								final String requestId_,
								final String userId_,
								final String comment_) {
		this.setClusterId(clusterId_);
		this.setCluster(cluster_);
		this.setSessionId(sessionId_);
		this.setRequestId(requestId_);
		this.setUserId(userId_);
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
		this.setComment(comment_);
	}

	public long getClusterId() {	return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	public Map<String, EmbeddedInterface> getCluster() { return cluster; }
	public void setCluster(Map<String, EmbeddedInterface> cluster) { this.cluster = cluster; }
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public String getRequestId() { return requestId; }
	public void setRequestId(String requestId) { this.requestId = requestId; }
	public String getUserId() {	return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
