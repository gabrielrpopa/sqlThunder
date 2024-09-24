package com.widescope.scripting.streaming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


/**
 * Vertically layout table. Not thread safe. 
 * @author popa_
 *
 */
public class TableVal implements RestInterface {
	
	private List<ColumnDef> columnDefs;
	private List<RowVal> rows;
	private boolean isDone;
	
	public TableVal(final List<ColumnDef> columnDefs,
					final List<RowVal> rows) {
		this.setColumnDefs(columnDefs);
		this.setRows(rows);
	}
	
	
	public TableVal(final List<ColumnDef> columnDefs) {
		this.setColumnDefs(columnDefs);
		this.setRows(new ArrayList<RowVal>());
	}


	public List<ColumnDef> getColumnDefs() { return columnDefs;	}
	public void setColumnDefs(final List<ColumnDef> columnDefs) { this.columnDefs = columnDefs; }
	public List<RowVal> getRows() { return rows; }
	public void setRows(final List<RowVal> rows) { this.rows = rows; }
	public void addRow(final RowVal row) { this.rows.add(row); }
	public void addRow(final RowVal row, final boolean isDone) { 
		this.rows.add(row); 
		this.setDone(isDone); 
	}


	public boolean isDone() { return isDone; }
	public void setDone(boolean isDone) { this.isDone = isDone; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
