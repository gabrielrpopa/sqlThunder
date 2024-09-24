package com.widescope.rdbmsRepo.database.tableFormat;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;

public class TableFormatExtMetadataOutput implements Comparable<TableFormatExtMetadataOutput> {

	private String colName;
	private ResultMetadata resultMetadata;
	private int colPosition;
	
	
	public TableFormatExtMetadataOutput(final String colName,
										final ResultMetadata resultMetadata,
										final int colPosition) {
		this.setColName(colName);
		this.setResultMetadata(resultMetadata);
		this.setColPosition(colPosition);
	}
		
	@Override
	public int compareTo(TableFormatExtMetadataOutput u) {
		return this.getColPosition() > u.getColPosition() ? 0 : 1;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public String getColName() { return colName; }
	public void setColName(String colName) { this.colName = colName; }
	public ResultMetadata getResultMetadata() { return resultMetadata; }
	public void setResultMetadata(ResultMetadata resultMetadata) { this.resultMetadata = resultMetadata; }
	public int getColPosition() { return colPosition; }
	public void setColPosition(int colPosition) { this.colPosition = colPosition; }
}
