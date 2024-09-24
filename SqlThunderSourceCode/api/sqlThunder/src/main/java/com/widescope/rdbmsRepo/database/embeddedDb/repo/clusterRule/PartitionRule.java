package com.widescope.rdbmsRepo.database.embeddedDb.repo.clusterRule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class PartitionRule {

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static PartitionRule toPartitionRule(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, PartitionRule.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
	
}
