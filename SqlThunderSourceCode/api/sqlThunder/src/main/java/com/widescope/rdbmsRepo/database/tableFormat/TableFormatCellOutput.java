package com.widescope.rdbmsRepo.database.tableFormat;

import com.google.gson.Gson;

public class TableFormatCellOutput implements Comparable<TableFormatCellOutput> {

	private String colName;
	private int colPosition;
	private Object value;
	
	
	public TableFormatCellOutput(	final String colName,
									final int colPosition,
									final Object value) {
		this.setColName(colName);
		this.setColPosition(colPosition);
		this.setValue(value);
	}
	
	
	@Override
	public int compareTo(TableFormatCellOutput u) {
		return this.getColPosition() > u.getColPosition() ? 0 : 1;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String getColName() { return colName; }
	public void setColName(String colName) { this.colName = colName; }
	public Object getValue() { return value; }
	public void setValue(Object value) { this.value = value; }
	public int getColPosition() { return colPosition; }
	public void setColPosition(int colPosition) { this.colPosition = colPosition; }
	
}
