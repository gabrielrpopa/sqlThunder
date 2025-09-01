package com.widescope.rdbmsRepo.database.embeddedDb.repo.sqlRule;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;


public class SqlRule implements RestInterface {
	private String rule;
	public SqlRule() {
		this.setRule(null);
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static SqlRule toSqlRule(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, SqlRule.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}


}
