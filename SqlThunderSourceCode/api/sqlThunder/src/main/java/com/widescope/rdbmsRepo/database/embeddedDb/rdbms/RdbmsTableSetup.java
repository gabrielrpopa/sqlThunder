package com.widescope.rdbmsRepo.database.embeddedDb.rdbms;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;

public class RdbmsTableSetup {
	private String tableName;
	private String createTableStm;
	private String insertTableStm;
	private TableFormatMap tableFormatMap;
	
	public RdbmsTableSetup(	final String _tableName,
							final String _createTableStm,
							final String _insertTableStm,
							final TableFormatMap _tableFormatMap) {
		this.setTableName(_tableName);
		this.setCreateTableStm(_createTableStm);
		this.setInsertTableStm(_insertTableStm);
		this.setTableFormatMap(_tableFormatMap);
		
	}
	public RdbmsTableSetup() {
		this.setTableName(null);
		this.setCreateTableStm(null);
		this.setInsertTableStm(null);
		this.setTableFormatMap(new TableFormatMap());
	}
	public String getTableName() { return tableName; }
	public void setTableName(String tableName) { this.tableName = tableName; }
	public String getCreateTableStm() { return createTableStm; }
	public void setCreateTableStm(String createTableStm) { this.createTableStm = createTableStm; }
	public String getInsertTableStm() { return insertTableStm; }
	public void setInsertTableStm(String insertTableStm) { this.insertTableStm = insertTableStm; }
	public TableFormatMap getTableFormatMap() { return tableFormatMap; }
	public void setTableFormatMap(TableFormatMap tableFormatMap) { this.tableFormatMap = tableFormatMap; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
