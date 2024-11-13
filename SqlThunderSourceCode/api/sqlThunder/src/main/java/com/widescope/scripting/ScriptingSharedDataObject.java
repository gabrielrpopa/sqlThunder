package com.widescope.scripting;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.sqlThunder.utils.StringUtils;

public class ScriptingSharedDataObject {
	
	private TableDefinition tableDefinition;
	private Map<String, RowValue> poolData;
	private RowValue tableFooter;
	private String requestId;

	public ScriptingSharedDataObject() {
		this.setTableDefinition(new TableDefinition());
		this.poolData = new HashMap<>();
		this.setTableFooter(new RowValue());
		this.setRequestId(null);
	}


	public Map<String, RowValue> getPoolData() {
		return poolData;
	}
	public void setPoolData(Map<String, RowValue> poolData) {
		this.poolData = poolData;
	}
	
	
	
	public void addLogsToPool(List<RowValue> poolLogs) { 
		for(RowValue l: poolLogs) {
			this.poolData.put(StringUtils.generateUniqueString16(), l); 
		}
	}
	
	public void addLogToPool(RowValue line) {
		this.poolData.put(StringUtils.generateUniqueString16(), line); 
	}
	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}
	public void setTableDefinition(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
	}
	public RowValue getTableFooter() {
		return tableFooter;
	}
	public void setTableFooter(RowValue tableFooter) {
		this.tableFooter = tableFooter;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
