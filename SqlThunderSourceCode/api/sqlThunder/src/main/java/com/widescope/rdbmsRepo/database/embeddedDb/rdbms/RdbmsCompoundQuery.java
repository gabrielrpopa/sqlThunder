package com.widescope.rdbmsRepo.database.embeddedDb.rdbms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class RdbmsCompoundQuery implements RestInterface{

	private String schemaUniqueName;
	private String sqlContent;
	private String uuid;
	
	public RdbmsCompoundQuery(	final String schemaUniqueName,
								final String sqlContent,
								final String uuid) {
		
		this.setSchemaUniqueName(schemaUniqueName);
		this.setSqlContent(sqlContent);
		this.setUuid(uuid);
		
	}
	
	public String getSchemaUniqueName() { return schemaUniqueName; }
	public void setSchemaUniqueName(String schemaUniqueName) { this.schemaUniqueName = schemaUniqueName; }
	public String getSqlContent() { return sqlContent;}
	public void setSqlContent(String sqlContent) { this.sqlContent = sqlContent; }
	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
