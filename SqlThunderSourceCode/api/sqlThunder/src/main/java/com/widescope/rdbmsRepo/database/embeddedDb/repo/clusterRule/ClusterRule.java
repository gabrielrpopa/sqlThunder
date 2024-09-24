package com.widescope.rdbmsRepo.database.embeddedDb.repo.clusterRule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;


public class ClusterRule implements RestInterface{

	private String rule;
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}

	public ClusterRule() {
		this.setRule(null);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ClusterRule toClusterRule(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ClusterRule.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}


	
}
