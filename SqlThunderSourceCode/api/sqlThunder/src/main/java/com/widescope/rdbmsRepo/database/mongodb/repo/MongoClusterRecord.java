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

package com.widescope.rdbmsRepo.database.mongodb.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class MongoClusterRecord implements RestInterface{

	private	int clusterId;
	private String uniqueName;
	private	String connString;
	
	private String storageType;
    private int controllerId;
    private long startPeriod;  
	private long endPeriod;
	private long sizeMb;
	
	private int countObjects;
	
	private String tunnelLocalPort;
	
	private String tunnelRemoteHostAddress;
	private String tunnelRemoteHostPort;
	private String tunnelRemoteUser;
	private String tunnelRemoteUserPassword;
	private String tunnelRemoteRsaKey;
	
	private MongoClusterMaxRecord minDbTemp;
	private MongoClusterMaxRecord minDbUser;
	private long maxCountUser;
	
	
	private Map<String, MongoClusterMaxRecord> mongoDbTempDbsMap = new ConcurrentHashMap<String, MongoClusterMaxRecord>();
	private Map<String, MongoClusterMaxRecord> mongoDbUserDbsMap = new ConcurrentHashMap<String, MongoClusterMaxRecord>();


	public MongoClusterRecord()	{
		this.setClusterId(0);
		this.setUniqueName("");
		this.setConnString("");
		this.setStorageType("");
		this.setControllerId(0);
		this.setStartPeriod(0);
		this.setEndPeriod(0);

		this.setTunnelLocalPort("");
		this.setTunnelRemoteHostAddress("");
		this.setTunnelRemoteHostPort("");
		this.setTunnelRemoteUser("");
		this.setTunnelRemoteUserPassword("");
		this.setTunnelRemoteRsaKey("");


		mongoDbTempDbsMap.put("temp1", new MongoClusterMaxRecord("temp1") );
		mongoDbTempDbsMap.put("temp2", new MongoClusterMaxRecord("temp2") );
		mongoDbTempDbsMap.put("temp3", new MongoClusterMaxRecord("temp3") );

		mongoDbUserDbsMap.put("user1", new MongoClusterMaxRecord("user1") );
		mongoDbUserDbsMap.put("user2", new MongoClusterMaxRecord("user2") );
		mongoDbUserDbsMap.put("user3", new MongoClusterMaxRecord("user3") );


		this.minDbTemp = this.getMinDbTemp_();
		this.minDbUser = this.getMinDbUser_();
		this.maxCountUser = this.getMaxCountUser_();


	}

	
	
	public MongoClusterRecord(final int clusterId, 
								final String uniqueName, 
								final String connString,
								final String storageType,
								final int controllerId,
								final long startPeriod,
								final long endPeriod,
								final String tunnelLocalPort,
								final String tunnelRemoteHostAddress,
								final String tunnelRemoteHostPort,
								final String tunnelRemoteUser,
								final String tunnelRemoteUserPassword,
								final String tunnelRemoteRsaKey
								)
	{
		this.setClusterId(clusterId);
		this.setUniqueName(uniqueName);
		this.setConnString(connString);
		this.setStorageType(storageType);
		this.setControllerId(controllerId);
		this.setStartPeriod(startPeriod);
		this.setEndPeriod(endPeriod);
		
		this.setTunnelLocalPort(tunnelLocalPort);
		this.setTunnelRemoteHostAddress(tunnelRemoteHostAddress);
		this.setTunnelRemoteHostPort(tunnelRemoteHostPort);
		this.setTunnelRemoteUser(tunnelRemoteUser);
		this.setTunnelRemoteUserPassword(tunnelRemoteUserPassword);
		this.setTunnelRemoteRsaKey(tunnelRemoteRsaKey);
		
		
		mongoDbTempDbsMap.put("temp1", new MongoClusterMaxRecord("temp1") );
		mongoDbTempDbsMap.put("temp2", new MongoClusterMaxRecord("temp2") );
		mongoDbTempDbsMap.put("temp3", new MongoClusterMaxRecord("temp3") );
		
		mongoDbUserDbsMap.put("user1", new MongoClusterMaxRecord("user1") );
		mongoDbUserDbsMap.put("user2", new MongoClusterMaxRecord("user2") );
		mongoDbUserDbsMap.put("user3", new MongoClusterMaxRecord("user3") );
		
		
		this.minDbTemp = this.getMinDbTemp_();
		this.minDbUser = this.getMinDbUser_();
		this.maxCountUser = this.getMaxCountUser_();
		
		
	}

	public int getClusterId() { return clusterId; }
	public void setClusterId(int clusterId) { this.clusterId = clusterId; }

	public String getUniqueName() { return uniqueName; }
	public void setUniqueName(String uniqueName) { this.uniqueName = uniqueName; }

	public String getConnString() { return connString; }
	public void setConnString(String connString) { this.connString = connString; }
	
	public String getStorageType() { return storageType; }
	public void setStorageType(String storageType) { this.storageType = storageType; }

	public int getControllerId() { return controllerId; }
	public void setControllerId(int controllerId) { this.controllerId = controllerId; }

	public long getStartPeriod() { return startPeriod; }
	public void setStartPeriod(long startPeriod) { this.startPeriod = startPeriod; }

	public long getEndPeriod() { return endPeriod; }
	public void setEndPeriod(long endPeriod) { this.endPeriod = endPeriod; }
	
	public String getTunnelLocalPort() {	return tunnelLocalPort; }
	public void setTunnelLocalPort(String tunnelLocalPort) { this.tunnelLocalPort = tunnelLocalPort; }
	
	public String getTunnelRemoteHostAddress() { return tunnelRemoteHostAddress; }
	public void setTunnelRemoteHostAddress(String tunnelRemoteHostAddress) { this.tunnelRemoteHostAddress = tunnelRemoteHostAddress; }
	
	public String getTunnelRemoteHostPort() { return tunnelRemoteHostPort; }
	public void setTunnelRemoteHostPort(String tunnelRemoteHostPort) {	this.tunnelRemoteHostPort = tunnelRemoteHostPort; }
	
	public String getTunnelRemoteUserPassword() { return tunnelRemoteUserPassword; }
	public void setTunnelRemoteUserPassword(String tunnelRemoteUserPassword) { this.tunnelRemoteUserPassword = tunnelRemoteUserPassword; }
	
	public String getTunnelRemoteUser() { return tunnelRemoteUser; }
	public void setTunnelRemoteUser(String tunnelRemoteUser) { this.tunnelRemoteUser = tunnelRemoteUser; }
	
	public String getTunnelRemoteRsaKey() {	return tunnelRemoteRsaKey; }
	public void setTunnelRemoteRsaKey(String tunnelRemoteRsaKey) { this.tunnelRemoteRsaKey = tunnelRemoteRsaKey; }

	public long getSizeMb() { return sizeMb; }
	public void setSizeMb(long sizeMb) { this.sizeMb = sizeMb; }
	
	public int getCountObjects() {	return countObjects; }
	public void setCountObjects(int countObjects) { this.countObjects = countObjects; }
	public void incrementCountObjects(){
		this.countObjects++;
	}

	public Map<String, MongoClusterMaxRecord> getMongoDbTempDbsMap() { return mongoDbTempDbsMap; }
	public void setMongoDbTempDbsMap(Map<String, MongoClusterMaxRecord> mongoDbTempDbsMap) {	this.mongoDbTempDbsMap = mongoDbTempDbsMap;	}
	public void setMongoDbTempDbsMap(MongoClusterMaxRecord mongoClusterMaxRecord) {	this.mongoDbTempDbsMap.put(mongoClusterMaxRecord.getUniqueName(), mongoClusterMaxRecord);	}

	public Map<String, MongoClusterMaxRecord> getMongoDbUserDbsMap() { return mongoDbUserDbsMap;	}
	public void setMongoDbUserDbsMap(Map<String, MongoClusterMaxRecord> mongoDbUserDbsMap) { this.mongoDbUserDbsMap = mongoDbUserDbsMap; }
	public void setMongoDbUserDbsMap(MongoClusterMaxRecord mongoClusterUserDbRecord) {	this.mongoDbUserDbsMap.put(mongoClusterUserDbRecord.getUniqueName(), mongoClusterUserDbRecord);	}
	
	public MongoClusterMaxRecord getMinDbTemp() { return minDbTemp; }
	public MongoClusterMaxRecord getMinDbUser() { return minDbUser; }
	public long getMaxCountUser() { return maxCountUser; }
	
	
	
	public long getMaxCountTemp() {
		long total = 0;
		for (MongoClusterMaxRecord mongoClusterMaxRecord : mongoDbTempDbsMap.values())	{
			total+=mongoClusterMaxRecord.getMaxCount();
    	}
		return total;
	}
	
	private MongoClusterMaxRecord getMinDbTemp_() {
		MongoClusterMaxRecord mongoClusterMaxRecord = null;
		for (MongoClusterMaxRecord item : mongoDbTempDbsMap.values())	{
			if(mongoClusterMaxRecord == null)
				mongoClusterMaxRecord = item;
			else {
				if(item.getMaxCount() > mongoClusterMaxRecord.getMaxCount())
					mongoClusterMaxRecord = item;
			}
				
    	}
		return mongoClusterMaxRecord;
	}
	
	private MongoClusterMaxRecord getMinDbUser_() {
		MongoClusterMaxRecord mongoClusterMaxRecord = null;
		for (MongoClusterMaxRecord item : mongoDbUserDbsMap.values())	{
			if(mongoClusterMaxRecord == null)
				mongoClusterMaxRecord = item;
			else {
				if(item.getMaxCount() > mongoClusterMaxRecord.getMaxCount())
					mongoClusterMaxRecord = item;
			}
				
    	}
		return mongoClusterMaxRecord;
	}
	
	private long getMaxCountUser_() {
		long total = 0;
		for (MongoClusterMaxRecord mongoClusterMaxRecord : mongoDbUserDbsMap.values())	{
			total+=mongoClusterMaxRecord.getMaxCount();
    	}
		return total;
	}




	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
