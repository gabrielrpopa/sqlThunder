package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedInMemCluster;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2InMem;

public class InMemEmbeddedDatabase implements RestInterface {

	private long clusterId;
	private String dbName;
	private String type;  /*SQL or TABLE*/
	private List<String> tableName; /*SQL statements or TABLE names*/
	
	private long timestamp;
	private String sessionId;
	private String requestId;
	private String userId;

	public InMemEmbeddedDatabase(	final long clusterId_,
									final String sessionId_,
									final String requestId_,
									final String userId_,
									final List<String> tableName_) throws Exception {
		this.setSessionId(sessionId_);
		this.setRequestId(requestId_);
		this.setUserId(userId_);
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
		this.setClusterId(clusterId_);
		this.setTableName(tableName_);
	}
	

	public String getDbName() { return dbName; }
	public void setDbName(String dbName) { this.dbName = dbName; }
	public List<String> getTableName() { return tableName; }
	public void setTableName(List<String> tableName) { this.tableName = tableName; }
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public String getRequestId() { return requestId; }
	public void setRequestId(String requestId) {this.requestId = requestId;}
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public static InMemEmbeddedDatabase fromEmbeddedInterface(final EmbeddedInterface i) throws Exception {
        return new InMemEmbeddedDatabase( ((H2InMem)i).getClusterId(),
										((H2InMem)i).getSessionId(),
										((H2InMem)i).getRequestId(),
										((H2InMem)i).getUserId(),
										((H2InMem)i).getUserTables());
		
	}
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
