package com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class DataTransfer implements RestInterface {

	private long countRecord;
	private String embeddedInMemDbName;
	private List<TableAffected> lstTables;
	
	
	public DataTransfer() {
		this.setEmbeddedInMemDbName("");
		this.setLstTables(new ArrayList<TableAffected>());
		this.setCountRecord(0);
	}
	public DataTransfer(final String embeddedInMemDbName,
						final List<TableAffected> lstTables) {
		this.setEmbeddedInMemDbName(embeddedInMemDbName);
		this.setLstTables(new ArrayList<TableAffected>());
		this.setCountRecord(0);
	}

	public List<TableAffected> getLstTables() { return lstTables; }
	public void setLstTables(List<TableAffected> lstTables) { this.lstTables = lstTables; }
	public String getEmbeddedInMemDbName() { return embeddedInMemDbName; }
	public void setEmbeddedInMemDbName(String embeddedInMemDbName) { this.embeddedInMemDbName = embeddedInMemDbName; }
	public long getCountRecord() { return countRecord; }
	public void setCountRecord(long countRecord) { this.countRecord = countRecord; }
	public void incrementCountRecord() { this.countRecord++; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
