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

public class ElasticQueryExec implements RestInterface {



	private int queryId;
	public int getQueryId() {	return queryId; }
	public void setQueryId(final int queryId) { this.queryId = queryId; }
		
	private List<ElasticQueryExecParam> elasticQueryExecParamList;
	public List<ElasticQueryExecParam> getElasticRepoSqlExecParamList() {	return elasticQueryExecParamList; }
	public void setElasticRepoSqlExecParamList(final List<ElasticQueryExecParam> elasticQueryExecParamList) { this.elasticQueryExecParamList = elasticQueryExecParamList; }

	public ElasticQueryExec() {
		this.queryId = 0;
		this.elasticQueryExecParamList = new ArrayList<ElasticQueryExecParam>();
	}

	public ElasticQueryExecParam getElasticQueryParam(int sql_param_id) {
		for (ElasticQueryExecParam elasticQueryExecParam : this.elasticQueryExecParamList) {
            if(elasticQueryExecParam.getQueryParamId() == sql_param_id)
            	return elasticQueryExecParam;
        }	
		return null;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ElasticQueryExec toElasticQueryExec(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticQueryExec.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
