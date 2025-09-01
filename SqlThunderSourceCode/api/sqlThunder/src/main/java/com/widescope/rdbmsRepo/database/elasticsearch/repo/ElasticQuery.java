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

package com.widescope.rdbmsRepo.database.elasticsearch.repo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;


public class ElasticQuery implements RestInterface {
	
	private int queryId;
	public int getQueryId() {	return queryId; }
	public void setQueryId(final int queryId) { this.queryId = queryId; }
	
	private String verb;
	public String getVerb() {	return verb; }
	public void setVerb(final String verb) { this.verb = verb; }
	
	private String queryReturnType;
	public String getQueryReturnType() {	return queryReturnType; }
	public void setQueryReturnType(final String queryReturnType) { this.queryReturnType = queryReturnType; }
	
	private String queryType;
	public String getQueryType() {	return queryType; }
	public void setQueryType(final String queryType) { this.queryType = queryType; }
	
	private String elasticApi;
	public String getElasticApi() { return elasticApi; }
	public void setElasticApi(final String elasticApi) { this.elasticApi = elasticApi; }
	
	private String indexName;
	public String getIndexName() { return indexName; }
	public void setIndexName(final String indexName) { this.indexName = indexName; }
	
	private String clusterName;
	public String getClusterName() { return clusterName; }
	public void setClusterName(final String clusterName) { this.clusterName = clusterName; }
	
	private String queryCategory;
	public String getQueryCategory() {	return queryCategory; }
	public void setQueryCategory(final String queryCategory) { this.queryCategory = queryCategory; }
	
	private String queryName;
	public String getQueryName() {	return queryName; }
	public void setQueryName(final String queryName) { this.queryName = queryName; }
	
	private String queryDescription;
	public String getQueryDescription() {	return queryDescription; }
	public void setQueryDescription(final String queryDescription) { this.queryDescription = queryDescription; }
	
	private String endPoint;
	public String getEndPoint() {	return endPoint; }
	public void setEndPoint(final String endPoint) { this.endPoint = endPoint; }
	
	private String queryContent;
	public String getQueryContent() {	return queryContent; }
	public void setQueryContent(final String queryContent) { this.queryContent = queryContent; }
	
	private int active;
	public int getActive() { return active; }
	public void setActive(final int active) { this.active = active; }
	

	private List<ElasticQueryParam> elasticQueryParamList;
	public List<ElasticQueryParam> getElasticQueryParamList() {	return elasticQueryParamList; }
	public void setElasticQueryParamList(final List<ElasticQueryParam> elasticQueryParamList) { this.elasticQueryParamList = elasticQueryParamList; }

	public ElasticQueryParam getElasticQueryParam(int queryParamId) {
		for (ElasticQueryParam elasticQueryParam : this.elasticQueryParamList) {
            if(elasticQueryParam.getQueryParamId() == queryParamId)
            	return elasticQueryParam;
        }	
		return null;
	}

	public ElasticQuery() {
		this.queryId = -1;
		this.verb = "";
		this.queryReturnType = "";
		this.queryType = "";
		this.elasticApi = "";
		this.indexName = "";
		this.queryCategory = "";
		this.queryName = "";
		this.queryDescription = "";
		this.endPoint = "";
		this.queryContent = "";
		this.active = 0;
		this.elasticQueryParamList = new ArrayList<>();
	}

	public ElasticQuery(final int queryId,
						final String verb,
						final String queryReturnType,
						final String queryType, 
						final String elasticApi,
						final String indexName,
						final String queryCategory, 
						final String queryName,
						final String queryDescription,
						final String endPoint,
						final String queryContent,
						final int active
						) throws Exception	{
		this.queryId = queryId;
		this.verb = verb;
		this.queryReturnType = queryReturnType;
		this.queryType = queryType;
		this.elasticApi = elasticApi;
		this.indexName = indexName;
		this.queryCategory = queryCategory;
		this.queryName = queryName;
		this.queryDescription = queryDescription;
		this.endPoint = endPoint;
		this.queryContent = queryContent;
		this.active = active;

		this.elasticQueryParamList = new ArrayList<>();
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static ElasticQuery toElasticQuery(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
