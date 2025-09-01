package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem;


import java.util.List;
import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class InMemDbDest implements RestInterface {

	private long clusterId;
	private String schemaName;
	private String databaseName;
	private List<String> tableNames; /*These are the actual table names*/
	
	
	public InMemDbDest(	final long clusterId,
						final String schemaName,
						final String databaseName,
						final List<String> tableNames) {
		this.clusterId = clusterId;
		this.schemaName = schemaName;
		this.databaseName = databaseName;
		this.tableNames = tableNames;
	}

	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	public String getSchemaName() { return schemaName; }
	public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

	public String getDatabaseName() { return databaseName; }
	public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }

	public List<String> getTableNames() { return tableNames; }
	public void setTableNames(List<String> tableNames) { this.tableNames = tableNames; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

}
