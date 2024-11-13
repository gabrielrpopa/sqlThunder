package com.widescope.rdbmsRepo.database.tableFormat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class TableDefinition {

	private String tableName;
	private String tableScript;
	private String tableInsert;
	private List<ColumnDefinition> metadata;

	public TableDefinition(	final String tableName,
							final String tableScript,
							final String tableInsert,
							final List<ColumnDefinition> metadata) {
		
		this.setTableName(tableName);
		this.setTableScript(tableScript);
		this.setTableInsert(tableInsert);
		this.setMetadata(metadata);
	}
	
	public TableDefinition(	) {
		this.setTableName(null);
		this.setTableScript(null);
		this.setTableInsert(null);
		this.setMetadata(new ArrayList<ColumnDefinition>());
	}

	public String getTableName() { return tableName; }
	public void setTableName(String tableName) { this.tableName = tableName; }

	public List<ColumnDefinition> getMetadata() { return metadata; }
	public void setMetadata(List<ColumnDefinition> metadata) { this.metadata = metadata; }

	public String getTableScript() { return tableScript; }
	public void setTableScript(String tableScript) { this.tableScript = tableScript; }

	public String getTableInsert() { return tableInsert; }
	public void setTableInsert(String tableInsert) { this.tableInsert = tableInsert; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	public static TableDefinition toTableDefinition(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, TableDefinition.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

	public void toTableDefinition(TableDefinition t) {
		this.tableName = t.getTableName();
		this.tableScript = t.getTableScript();
		this.tableInsert = t.getTableInsert();
		this.metadata = t.getMetadata();
	}

}
