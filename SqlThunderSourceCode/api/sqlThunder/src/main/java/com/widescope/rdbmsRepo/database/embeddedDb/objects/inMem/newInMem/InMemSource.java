package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class InMemSource implements RestInterface {
	
	private long clusterId;
	private String schemaName;
	private String sourceType;  /*EMBEDDED / RDBMS / MONGO / ELASTIC */
	private String connectionName;  
	private String snapshotType;  /*SQL or TABLE*/
	private List<InMemDbSource> inMemDbSourceList;
	
	public InMemSource() {
		this.setSourceType(null);
		this.setClusterId(-1);
		this.setSchemaName(null);
		this.setSnapshotType(null);
		this.setConnectionName(null);
		this.setInMemDbSourceList(new ArrayList<InMemDbSource>());
		
	}
	

	public InMemSource( final String sourceType,
						final long clusterId,
						final String snapshotType,  
						final String connectionName) {
		this.setSourceType(sourceType);
		this.setClusterId(clusterId);
		this.setSchemaName(schemaName);
		this.setSnapshotType(snapshotType);
		this.setConnectionName(connectionName);
		this.setInMemDbSourceList(new ArrayList<InMemDbSource>());
	
	}

	public String getSourceType() { return sourceType; }
	public void setSourceType(String sourceType) { this.sourceType = sourceType; }

	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	public String getSchemaName() { return schemaName; }
	public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

	public String getSnapshotType() { return snapshotType; }
	public void setSnapshotType(String snapshotType) { this.snapshotType = snapshotType; }

	public String getConnectionName() { return connectionName; }
	public void setConnectionName(String connectionName) { this.connectionName = connectionName; }

	public List<InMemDbSource> getInMemDbSourceList() { return inMemDbSourceList; }
	public void setInMemDbSourceList(List<InMemDbSource> inMemDbSourceList) { this.inMemDbSourceList = inMemDbSourceList; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

}
