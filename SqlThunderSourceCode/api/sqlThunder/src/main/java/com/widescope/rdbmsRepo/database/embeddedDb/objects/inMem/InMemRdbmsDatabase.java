package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem;

import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class InMemRdbmsDatabase implements RestInterface {

	
	private String connectionName;
	private String type;  /*QUERY or TABLE*/
	private List<String> tableName; /*QUERY string or TABLE*/
	private long timestamp;
	private String sessionId;
	private String requestId;
	private String userId;

	public String getConnectionName() { return connectionName; }
	public void setConnectionName(String connectionName) { this.connectionName = connectionName; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
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

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
