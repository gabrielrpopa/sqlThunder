package com.widescope.scripting;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;


public class ScriptingReturnObject implements RestInterface {
	private TableDefinition tableDefinition;
	private List<RowValue> rowValueList;
	private RowValue tableFooter;
	private List<LogDetail> logDetailList;
	private String isStreaming;  /*Y/N*/
	
	public ScriptingReturnObject() {
		this.setTableDefinition(new TableDefinition());
		this.rowValueList = new ArrayList<>();
		this.setTableFooter(new RowValue());
		this.setLogDetailList(new ArrayList<LogDetail>());
		this.setIsStreaming("Y"); 
	}
	
	


	public List<RowValue> getPoolData() {
		return rowValueList;
	}
	public void setPoolData(List<RowValue> poolData) {
		this.rowValueList = poolData;
	}
	public void addPoolData(RowValue rowValue) {
		this.rowValueList.add(rowValue);
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
	public List<LogDetail> getLogDetailList() {
		return logDetailList;
	}
	public void setLogDetailList(List<LogDetail> logDetailList) {
		this.logDetailList = logDetailList;
	}
	public void addLogDetail(LogDetail logDetail) {
		this.logDetailList.add(logDetail);
	}
	public String getIsStreaming() {
		return isStreaming;
	}
	public void setIsStreaming(String isStreaming) {
		this.isStreaming = isStreaming;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	public static ScriptingReturnObject toScriptingReturnObject(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ScriptingReturnObject.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}

	public static ScriptingReturnObject toScriptingReturnObject(ScriptingSharedDataObject s) {
		ScriptingReturnObject ret = new ScriptingReturnObject();
		try	{
			ret.setTableDefinition(s.getTableDefinition());
			ret.setTableFooter(s.getTableFooter());
			for(RowValue l: s.getPoolData().values()) {
				ret.addPoolData(l); 
			}
		} catch(Exception ex) {
			return null;
		}
		return ret;
	}




	




	
	
}
