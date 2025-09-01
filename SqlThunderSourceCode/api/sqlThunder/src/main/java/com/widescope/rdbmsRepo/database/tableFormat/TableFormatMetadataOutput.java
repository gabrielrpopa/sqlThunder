package com.widescope.rdbmsRepo.database.tableFormat;

import com.google.gson.Gson;

public class TableFormatMetadataOutput implements Comparable<TableFormatMetadataOutput> {
	
	private String colName;
	private String colType;
	private int colPosition;

	public String getColName() { return colName; }
	public void setColName(String colName) { this.colName = colName; }
	public String getColType() { return colType; }
	public void setColType(String colType) { this.colType = colType; }
	public int getColPosition() { return colPosition; }
	public void setColPosition(int colPosition) { this.colPosition = colPosition; }

	public TableFormatMetadataOutput(	final String colName,
										final String colType,
										final int colPosition) {
		this.setColName(colName);
		this.setColType(colType);
		this.setColPosition(colPosition);
	}
	
	
	@Override
	public int compareTo(TableFormatMetadataOutput u) {
		return this.getColPosition() > u.getColPosition() ? 0 : 1;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}



	
}
