package com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class EmbeddedExecTable implements RestInterface {

	private long dbId;
	private String staticSqlStm;

	public long getDbId() { return dbId; }
	public void setDbId(long dbId) { this.dbId = dbId; }
	public String getStaticSqlStm() {	return staticSqlStm; }
	public void setStaticSqlStm(String staticSqlStm) { this.staticSqlStm = staticSqlStm; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
