package com.widescope.rdbmsRepo.database.tableFormat;

import com.google.gson.Gson;

public class ColumnDefinition {
	private String columnName;
	private String columnType;
	private int columnPrecision; // DECIMAL(6,3) WHERE 6 is precision, while 3 is scale
	private int columnScale;
	
	public ColumnDefinition() {
		this.setColumnName(null);
		this.setColumnType(null);
		this.setColumnPrecision(0);
		this.setColumnScale(0);
	}
		
	
	
	public ColumnDefinition(final String columnName,
							final String columnType,
							final int columnPrecision,
							final int columnScale) {
		this.setColumnName(columnName);
		this.setColumnType(columnType);
		this.setColumnPrecision(columnPrecision);
		this.setColumnScale(columnScale);
		
	}

	public String getColumnName() {	return columnName; }
	public void setColumnName(String columnName) { this.columnName = columnName; }

	public String getColumnType() {	return columnType; }
	public void setColumnType(String columnType) { this.columnType = columnType; }

	public int getColumnPrecision() { return columnPrecision; }
	public void setColumnPrecision(int columnPrecision) { this.columnPrecision = columnPrecision; }

	public int getColumnScale() { return columnScale; }
	public void setColumnScale(int columnScale) { this.columnScale = columnScale; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
