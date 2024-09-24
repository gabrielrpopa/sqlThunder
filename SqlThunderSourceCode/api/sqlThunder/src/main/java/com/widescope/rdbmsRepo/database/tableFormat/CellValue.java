package com.widescope.rdbmsRepo.database.tableFormat;

public class CellValue {

	private String columnName;
	private Object columnValue;
	
	public CellValue() {
		this.setColumnName(null);
		this.setColumnValue(null);
	}
	
	
	public CellValue(final String columnName,
					 final Object columnValue) {
		this.setColumnName(columnName);
		this.setColumnValue(columnValue);
	}

	public String getColumnName() { return columnName; }
	public void setColumnName(String columnName) { this.columnName = columnName; }
	
	public Object getColumnValue() { return columnValue; }
	public void setColumnValue(Object columnValue) { this.columnValue = columnValue; }
	
}
