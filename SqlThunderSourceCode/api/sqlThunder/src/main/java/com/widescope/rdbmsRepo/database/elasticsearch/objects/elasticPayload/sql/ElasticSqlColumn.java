package com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.sql;

import com.google.gson.Gson;

public class ElasticSqlColumn {
	private String name;
	private String type;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
