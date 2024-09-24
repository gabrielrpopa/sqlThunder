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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;



/**
 * 
 * @author Gabriel Popa
 * @since   August 2020
 */

public class MongoRepoDynamicMqlExecution implements RestInterface {
	
	private int mqlId;
	public int getMqlId() {	return mqlId; }
	public void setMqlId(final int mqlId) { this.mqlId = mqlId; }
	
	private List<MongoRepoMqlParamExecution> mongoRepoMqlParamListList;
	public List<MongoRepoMqlParamExecution> getElasticRepoMqlParamList() {	return mongoRepoMqlParamListList; }
	public void setMongoRepoMqlParamList(final List<MongoRepoMqlParamExecution> mongoRepoMqlParamListList) { this.mongoRepoMqlParamListList = mongoRepoMqlParamListList; }

	public MongoRepoDynamicMqlExecution(final int mqlId,
										 final String mqlReturnType,
										 final String mqlCategory,
										 final String mqlName,
										 final String mqlDescription,
										 final String mqlContent,
										 final int active) throws Exception	{
		this.mqlId = mqlId;
		this.mongoRepoMqlParamListList = new ArrayList<MongoRepoMqlParamExecution>();
	}
	
	
	public MongoRepoMqlParamExecution getMongoRepoMqlParam(int dynamic_mql_param_id) {
		for (MongoRepoMqlParamExecution mongoRepoMqlParam : this.mongoRepoMqlParamListList) {
            if(mongoRepoMqlParam.getId() == dynamic_mql_param_id)
            	return mongoRepoMqlParam;
        }	
		return null;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static MongoRepoDynamicMqlExecution toMongoRepoDynamicMqlExecution(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, MongoRepoDynamicMqlExecution.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}
	
	
	
	public static MongoRepoDynamicMql toMongoRepoDynamicMql(MongoRepoDynamicMqlExecution mongoRepoDynamicMqlExecution) {
		MongoRepoDynamicMql mongoRepoDynamicMql = null;
		try	{
			MongoClusterDb mongoClusterDb = new MongoClusterDb();
			List<MongoRepoDynamicMql> mongoRepoDynamicMqlList = mongoClusterDb.getMql(mongoRepoDynamicMqlExecution.getMqlId());
			if(mongoRepoDynamicMqlList.size() == 1) {
				mongoRepoDynamicMql = mongoRepoDynamicMqlList.get(0);
				for(MongoRepoMqlParam mongoRepoMqlParam : mongoRepoDynamicMql.getMongoRepoMqlParamList()) {
					Object val = mongoRepoDynamicMqlExecution.getMongoRepoMqlParam(mongoRepoMqlParam.getDynamicMqlParamId()).getValue();
					mongoRepoMqlParam.setValue(val);
				}
			}
		}
		catch(Exception ex) {
			return null;
		}
		
		return mongoRepoDynamicMql;
	}
	
	public static String toRawMql(MongoRepoDynamicMql mongoRepoDynamicMql) {
		String mqlString = mongoRepoDynamicMql.getMqlContent();
		for(MongoRepoMqlParam mongoRepoMqlParam : mongoRepoDynamicMql.getMongoRepoMqlParamList()) {
			if(mongoRepoMqlParam.getValue() instanceof Integer 
					|| mongoRepoMqlParam.getValue() instanceof Double 
					|| mongoRepoMqlParam.getValue() instanceof Long
					|| mongoRepoMqlParam.getValue() instanceof Float
					)
				mqlString = mqlString.replaceAll(mongoRepoMqlParam.getDynamicMqlParamName(), mongoRepoMqlParam.getValue().toString());
			else
				mqlString = mqlString.replaceAll(mongoRepoMqlParam.getDynamicMqlParamName(), "'" + mongoRepoMqlParam.getValue().toString() + "'");
				
		}
		return mqlString;
	}

}
