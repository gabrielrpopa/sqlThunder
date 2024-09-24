package com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn;


import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class TableAffected implements RestInterface {

	private int recAffected;
	private String tableName;

	public TableAffected(final int recAffected, final String tableName) {
		this.setRecAffected(recAffected);
		this.setTableName(tableName);
	}

	public int getRecAffected() { return recAffected; }
	public void setRecAffected(int recAffected) { this.recAffected = recAffected; }
	public String getTableName() { return tableName; }
	public void setTableName(String tableName) { this.tableName = tableName; }
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
