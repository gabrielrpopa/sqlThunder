package com.widescope.rdbmsRepo.database.tableFormat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.StringUtils;

public class RowValue {
	private List<CellValue> row;
	private long t;

	public RowValue() {
		this.setRow(null);
		this.t = DateTimeUtils.millisecondsSinceEpoch();
	}
	public RowValue(final List<CellValue> row) {
		this.setRow(row);
		this.t = DateTimeUtils.millisecondsSinceEpoch();

	}
	public List<CellValue> getRow() { return row; }
	public void setRow(List<CellValue> row) {
		this.row = row;
		this.t = DateTimeUtils.millisecondsSinceEpoch();
	}

	public void addRow(List<CellValue> row) { this.row.addAll(row);}
	public void addRow(CellValue c) { this.row.add(c);}

	public long getT() { return t; }
	public void setT(long timeStamp) { this.t = timeStamp; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	public static RowValue toRowValue(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, RowValue.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
