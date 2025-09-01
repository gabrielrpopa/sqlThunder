package com.widescope.scripting.streaming;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class HeaderDef {
	private List<ColumnDef> columns;
	
	public HeaderDef(final List<ColumnDef> columns) {
		this.setColumns(columns);
	}
	public HeaderDef() {
		this.setColumns(new ArrayList<ColumnDef>());
	}
	public List<ColumnDef> getColumns() { return columns; }
	public void setColumns(List<ColumnDef> columns) { this.columns = columns; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static HeaderDef toHeaderDef(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, HeaderDef.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
