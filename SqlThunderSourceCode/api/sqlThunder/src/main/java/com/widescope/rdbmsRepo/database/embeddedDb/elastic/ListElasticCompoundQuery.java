package com.widescope.rdbmsRepo.database.embeddedDb.elastic;


import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ListElasticCompoundQuery {

	private List<ElasticCompoundQuery> lst;
	private String tableName;
	private String sqlAggregator;

	public List<ElasticCompoundQuery> getLst() { return lst; }
	public void setLst(List<ElasticCompoundQuery> lst) { this.lst = lst; }
	
	public String getSqlAggregator() { return sqlAggregator; }
	public void setSqlAggregator(String sqlAggregator) { this.sqlAggregator = sqlAggregator; }

	public String getTableName() { return tableName; }
	public void setTableName(String tableName) { this.tableName = tableName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ListElasticCompoundQuery toListElasticCompoundQuery(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ListElasticCompoundQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
