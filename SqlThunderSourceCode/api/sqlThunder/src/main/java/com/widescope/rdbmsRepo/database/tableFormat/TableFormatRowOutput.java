package com.widescope.rdbmsRepo.database.tableFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

public class TableFormatRowOutput  {
	private List<TableFormatCellOutput> row;
	
	public TableFormatRowOutput(final List<TableFormatCellOutput> row) {
		this.setRow(row);
	}
	
	public TableFormatRowOutput() {
		this.setRow(new ArrayList<TableFormatCellOutput>());
	}

	public List<TableFormatCellOutput> getRow() { return row; }
	public void setRow(List<TableFormatCellOutput> row) { this.row = row; }
	
	public void order() {
		Collections.sort(row);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
