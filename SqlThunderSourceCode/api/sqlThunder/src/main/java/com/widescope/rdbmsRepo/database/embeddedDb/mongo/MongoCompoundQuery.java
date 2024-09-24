package com.widescope.rdbmsRepo.database.embeddedDb.mongo;





import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class MongoCompoundQuery implements RestInterface{
	
	private String clusterUniqueName;
	private String mongoDbName;
	private String collectionName;
	private String queryContent;
	private String uuid;

	public String getClusterUniqueName() { return clusterUniqueName; }
	public void setClusterUniqueName(String clusterUniqueName) { this.clusterUniqueName = clusterUniqueName; }
	public String getMongoDbName() { return mongoDbName; }
	public void setMongoDbName(String mongoDbName) { this.mongoDbName = mongoDbName; }
	public String getCollectionName() { return collectionName; }
	public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
	public String getQueryContent() { return queryContent; }
	public void setQueryContent(String queryContent) { this.queryContent = queryContent; }
	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
