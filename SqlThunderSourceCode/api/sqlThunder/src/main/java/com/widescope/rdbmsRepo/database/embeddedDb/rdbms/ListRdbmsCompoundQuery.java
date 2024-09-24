package com.widescope.rdbmsRepo.database.embeddedDb.rdbms;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ListRdbmsCompoundQuery {
	private List<RdbmsCompoundQuery> lst;
	private String tableName;
	private String sqlAggregator;

	public List<RdbmsCompoundQuery> getLst() { return lst; }
	public void setLst(List<RdbmsCompoundQuery> lst) { this.lst = lst; }
	
	public String getSqlAggregator() { return sqlAggregator; }
	public void setSqlAggregator(String sqlAggregator) { this.sqlAggregator = sqlAggregator; }

	public String getTableName() { return tableName; }
	public void setTableName(String tableName) { this.tableName = tableName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ListRdbmsCompoundQuery toListRdbmsCompoundQuery(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ListRdbmsCompoundQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}

	

	
	
}
