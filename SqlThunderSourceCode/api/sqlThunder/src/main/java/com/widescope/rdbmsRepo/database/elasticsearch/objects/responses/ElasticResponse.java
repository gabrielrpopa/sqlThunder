package com.widescope.rdbmsRepo.database.elasticsearch.objects.responses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.json.simple.JSONObject;
import com.widescope.sqlThunder.rest.RestInterface;

public class ElasticResponse implements RestInterface{

	private String verb;
	private String elasticApi;
	private String queryType;
	private String indexName;
	private JSONObject elasticObject;

	public ElasticResponse() {
		this.verb = "";
		this.elasticApi = "";
		this.queryType = "";
		this.indexName = "";
		this.elasticObject = null;
	}
	

	public ElasticResponse(	final String verb,
								final String elasticApi,
								final String queryType,
								final String indexName,
								final JSONObject elasticObject) {
		this.verb = verb;
		this.elasticApi = elasticApi;
		this.queryType = queryType;
		this.indexName = indexName;
		this.setElasticObject(elasticObject);
	}

	public ElasticResponse(final String queryType) {
		this.verb = "";
		this.elasticApi = "";
		this.queryType = queryType;
		this.indexName = "";
		this.setElasticObject(null);
	}

	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) { this.verb = verb; }
	public String getElasticApi() {
		return elasticApi;
	}
	public void setElasticApi(String elasticApi) {
		this.elasticApi = elasticApi;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public JSONObject getElasticObject() {
		return elasticObject;
	}
	public void setElasticObject(JSONObject elasticObject) {
		this.elasticObject = elasticObject;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ElasticResponse toElasticResponse(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticResponse.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}


	public String toStringPretty() {
		try	{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}
}
