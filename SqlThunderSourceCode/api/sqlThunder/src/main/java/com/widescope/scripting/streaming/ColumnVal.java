package com.widescope.scripting.streaming;

import com.google.gson.Gson;

public class ColumnVal {
	private short id;
	private Object columnVal;
	
	public ColumnVal(	final short id,
						final Object columnVal) {
		this.setId(id);
		this.setColumnVal(columnVal);
	}

	public short getId() { return id; }
	public void setId(short id) { this.id = id; }
	public Object getColumnVal() { return columnVal; }
	public void setColumnVal(Object columnVal) { this.columnVal = columnVal; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
