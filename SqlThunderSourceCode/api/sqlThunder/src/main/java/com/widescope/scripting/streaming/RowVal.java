package com.widescope.scripting.streaming;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RowVal {
	private List<ColumnVal> columns;
	public RowVal(final List<ColumnVal> columns) {
		this.setColumns(columns);
	}
	public RowVal() {
		this.setColumns(new ArrayList<ColumnVal>());
	}
	public List<ColumnVal> getColumns() { return columns; }
	public void setColumns(List<ColumnVal> columns) { this.columns = columns; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static RowVal toRowVal(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, RowVal.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
