package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class InMemDbSource implements RestInterface {
	private long clusterId;
	private String schemaName;
	private String databaseName;
	private List<String> tableNames; /*SQL statements or TABLE names*/

	public InMemDbSource(	final long clusterId,
							final String schemaName,
							final String databaseName,
							final List<String> tableNames) {
		this.setClusterId(clusterId);
		this.setSchemaName(schemaName);
		this.setDatabaseName(databaseName);
		this.setTableNames(tableNames);
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
