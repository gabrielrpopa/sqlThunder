package com.widescope.scripting.streaming;

import com.google.gson.Gson;

public class ColumnDef {
	private short id;
	private String columnName;
	private String columnType;
	
	public ColumnDef(	final short id,
						final String columnName,
						final String columnType) {
		this.setId(id);
		this.setColumnName(columnName);
		this.setColumnType(columnType);
	}

	public short getId() { return id; }
	public void setId(short id) { this.id = id; }
	public String getColumnName() {	return columnName; }
	public void setColumnName(String columnName) { this.columnName = columnName; }
	public String getColumnType() { return columnType; }
	public void setColumnType(String columnType) { this.columnType = columnType; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
