package com.widescope.scripting;


import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;


public class ScriptingSharedDataObject {
	
	private TableDefinition tableDefinition;
	private List<RowValue> rowValueList;  /*List of Column Names with associated value*/
	private RowValue tableFooter;
	private String scriptName;
	private String interpreterName;
	private String scriptType; /*ADHOC/REPO*/

	public ScriptingSharedDataObject() {
		this.setTableDefinition(new TableDefinition());
		this.rowValueList = new ArrayList<>();
		this.setTableFooter(new RowValue());
		this.scriptName = "";
		this.interpreterName = "PYTHON";
		this.scriptType = "REPO";
	}

	synchronized public void createIfNotPresent(ScriptingSharedDataObject s) {
		if(this.tableDefinition.getTableName().isEmpty()) {
			this.tableDefinition = s.getTableDefinition();
			this.rowValueList = s.getRowValueList();
			this.tableFooter = s.getTableFooter();
			this.scriptName = s.getScriptName();
			this.scriptType = s.getScriptType();
			this.interpreterName = s.getInterpreterName();
		} else {
			this.rowValueList.addAll(s.getRowValueList());
		}
	}

	public List<RowValue> getRowValueList() { return rowValueList; }
	public void setPoolData(List<RowValue> rowValueList) { this.rowValueList = rowValueList;}
	public void addLogsToPool(List<RowValue> poolLogs) { this.rowValueList.addAll(poolLogs); }
	public void addLogToPool(RowValue line) { this.rowValueList.add(line); }

	public TableDefinition getTableDefinition() { return tableDefinition; }
	public void setTableDefinition(TableDefinition tableDefinition) { this.tableDefinition = tableDefinition; }

	public RowValue getTableFooter() { return tableFooter; }
	public void setTableFooter(RowValue tableFooter) { this.tableFooter = tableFooter; }

	public String getScriptName() { return scriptName; }
	public void setScriptName(String scriptName) { this.scriptName = scriptName; }

	public String getInterpreterName()  { return interpreterName; }
	public void setInterpreterName(String interpreterName) { this.interpreterName = interpreterName; }

	public String getScriptType() { return scriptType; }
	public void setScriptType(String scriptType) { this.scriptType = scriptType; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}



}
