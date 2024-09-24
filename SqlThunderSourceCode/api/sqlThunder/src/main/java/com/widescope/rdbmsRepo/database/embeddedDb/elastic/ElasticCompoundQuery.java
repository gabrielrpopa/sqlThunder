package com.widescope.rdbmsRepo.database.embeddedDb.elastic;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class ElasticCompoundQuery implements RestInterface{
	
	private String clusterUniqueName;
	private String indexName;
	
	private String sqlType;  /*DSL/SQL*/
	private String httpVerb;
	private String elasticApi;
	private String endPoint;
	private String sqlContent;
	private String uuid;
	
	public ElasticCompoundQuery(final String clusterUniqueName,
								final String indexName,
								final String sqlType,
								final String httpVerb,
								final String elasticApi,
								final String endPoint,
								final String sqlContent,
								final String uuid) {
		
		this.setClusterUniqueName(clusterUniqueName);
		this.setIndexName(indexName);
		this.setSqlType(sqlType);
		this.setHttpVerb(httpVerb);
		this.setElasticApi(elasticApi);
		this.setEndPoint(endPoint);
		this.setSqlContent(sqlContent);
		this.setUuid(uuid);
		
	}

	public String getClusterUniqueName() { return clusterUniqueName; }
	public void setClusterUniqueName(String clusterUniqueName) { this.clusterUniqueName = clusterUniqueName; }
	public String getIndexName() { return indexName; }
	public void setIndexName(String indexName) { this.indexName = indexName; }
	public String getSqlType() { return sqlType; }
	public void setSqlType(String sqlType) { this.sqlType = sqlType; }
	public String getHttpVerb() { return httpVerb; }
	public void setHttpVerb(String httpVerb) { this.httpVerb = httpVerb; }
	public String getElasticApi() { return elasticApi; }
	public void setElasticApi(String elasticApi) { this.elasticApi = elasticApi; }
	public String getEndPoint() { return endPoint; }
	public void setEndPoint(String endPoint) { this.endPoint = endPoint; }
	public String getSqlContent() { return sqlContent;}
	public void setSqlContent(String sqlContent) { this.sqlContent = sqlContent; }
	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
