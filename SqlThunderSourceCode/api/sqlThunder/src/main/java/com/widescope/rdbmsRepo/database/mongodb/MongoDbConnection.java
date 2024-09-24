/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.widescope.rdbmsRepo.database.mongodb;



import java.util.ArrayList;
import java.util.Map;


import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import org.bson.codecs.configuration.CodecRegistry;



public class MongoDbConnection {

	private MongoClient mongoClient = null;
	private String clusterName = null;
	private long clusterId = -1;
	private boolean isConnected = false;
	
	public MongoDbConnection() {
		this.mongoClient = null;
		this.setClusterName(null);
	}
	
	
	public MongoDbConnection(	final String connString, 
								final long clusterId,
								final String clusterName) {
		this.mongoClient = MongoClients.create(connString);
		this.setClusterName(clusterName);
		this.setClusterId(clusterId);
		this.isConnected = true;
	}
	
	public MongoDbConnection(final MongoClusterRecord mongoClusterRecord) {
		this.mongoClient = MongoClients.create(mongoClusterRecord.getConnString());
		this.setClusterName(mongoClusterRecord.getUniqueName());
		this.isConnected = true;
	}


	public MongoDbConnection(final Map<String, ServerAddress> connString, final String clusterName, CodecRegistry pojoCodecRegistry) {
		try {
			MongoClientSettings settings = MongoClientSettings
					.builder()
					.codecRegistry(pojoCodecRegistry)
					.applyToClusterSettings(builder -> builder.hosts(new ArrayList<ServerAddress>(connString.values())) )
					.applyToSocketSettings(builder -> {
								builder.connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS);
								builder.readTimeout(5, java.util.concurrent.TimeUnit.SECONDS);
							}
					)
					.retryWrites(true)
					.build();
			this.mongoClient = MongoClients.create(settings);
			this.setClusterName(clusterName);
			this.isConnected = true;
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			this.isConnected = false;
		}

	}


	public MongoDbConnection(final Map<String, ServerAddress> connString, final String clusterName) {
		try {
			MongoClientSettings settings = MongoClientSettings
					.builder()
					.applyToClusterSettings(builder -> builder.hosts(new ArrayList<>(connString.values())) )
					.applyToSocketSettings(builder -> {
								builder.connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS);
								builder.readTimeout(5, java.util.concurrent.TimeUnit.SECONDS);
							}
					)
					.retryWrites(true)
					.build();
			mongoClient = MongoClients.create(settings);
			this.setClusterName(clusterName);
			this.isConnected = true;
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			this.isConnected = false;
		}
	}
	
	
	public void disconnect() {
		this.mongoClient.close();
		this.setClusterName(null);
		this.isConnected = false;
	}
		
	public MongoClient getMongoClient() { return mongoClient; }
	public String getClusterName() { return clusterName; }
	public void setClusterName(String clusterName) { this.clusterName = clusterName; }
	public boolean isConnected() { return isConnected; }
	
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	

}
