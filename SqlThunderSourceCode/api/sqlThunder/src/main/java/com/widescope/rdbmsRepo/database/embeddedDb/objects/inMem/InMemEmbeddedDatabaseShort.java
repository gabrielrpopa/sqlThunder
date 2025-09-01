package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedInMemCluster;

public class InMemEmbeddedDatabaseShort implements RestInterface {
	private long clusterId;
	private String dbName;
	private String type;  /*SQL or TABLE*/
	private String connectionType;  /*EMBEDDED / RDBMS / MONGO / ELASTIC */
	private String connectionName;
	private List<String> tableName; /*SQL statements or TABLE names*/
	public InMemEmbeddedDatabaseShort(final EmbeddedInMemCluster c) {
		this.setTableName(new ArrayList<String>());
	}

	public String getDbName() {	return dbName;	}
	public void setDbName(String dbName) {	this.dbName = dbName; }
	public List<String> getTableName() { return tableName; }
	public void setTableName(List<String> tableName) { this.tableName = tableName; }
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
