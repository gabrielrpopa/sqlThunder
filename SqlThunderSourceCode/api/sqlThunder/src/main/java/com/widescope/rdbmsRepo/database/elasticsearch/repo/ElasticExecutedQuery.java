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


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.ExecutedStatement;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQuery;
import com.widescope.sqlThunder.rest.RestInterface;


public class ElasticExecutedQuery extends ExecutedStatement {

	private	long statementId;
	private String statementName;
	private String statementType;
	private String statement;
	private String jsonParam;
	private	long clusterId;
	private String httpVerb;
	private String elasticApi;
	private String indexName;
	private String endPoint;
	private String isOriginalFormat;


	public ElasticExecutedQuery() {}

	public ElasticExecutedQuery(final long id,
								final String requestId,
								final long statementId,
								final String statementName,
								final String statementType,
								final String statement,
								final String isValid,
								final String jsonParam,
								final long groupId,
								final String source,
								final long userId,
								final long clusterId,
								final String httpVerb,
								final String elasticApi,
								final String indexName,
								final String endPoint,
								final String isOriginalFormat,
								final String comment,
								final String repPath,
								final long timeStamp,
								final int cntAccess
							)
	{
		this.setId(id);
		this.setRequestId(requestId);
		this.setStatementId(statementId);
		this.setStatementName(statementName);
		this.setStatementType(statementType);
		this.setGroupId(groupId);
		this.setSource(source);
		this.setUserId(userId);
		this.setClusterId(clusterId);
		this.setHttpVerb(httpVerb);
		this.setElasticApi(elasticApi);
		this.setIndexName(indexName);
		this.setEndPoint(endPoint);
		this.setIsOriginalFormat(isOriginalFormat);
		this.setStatement(statement);
		this.setIsValid(isValid);
		this.setJsonParam(jsonParam);
		this.setComment(comment);
		this.setRepPath(repPath);
		this.setTimestamp(timestamp);
		this.setFlag(-1);
		this.setCntAccess(cntAccess);
	}

	public long getStatementId() { return statementId; }
	public void setStatementId(long statementId) { this.statementId = statementId; }
	public String getStatementName() { return statementName; }
	public void setStatementName(String statementName) { this.statementName = statementName; }
	public String getStatementType() { return statementType; }
	public void setStatementType(String statementType) { this.statementType = statementType; }
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	public String getHttpVerb() { return httpVerb; }
	public void setHttpVerb(String httpVerb) { this.httpVerb = httpVerb; }
	public String getElasticApi() { return elasticApi; }
	public void setElasticApi(String elasticApi) { this.elasticApi = elasticApi; }
	public String getIndexName() { return indexName; }
	public void setIndexName(String indexName) { this.indexName = indexName; }
	public String getEndPoint() { return endPoint; }
	public void setEndPoint(String endPoint) { this.endPoint = endPoint; }
	public String getIsOriginalFormat() { return isOriginalFormat; }
	public void setIsOriginalFormat(String isOriginalFormat) { this.isOriginalFormat = isOriginalFormat; }
	public String getStatement() { return statement; }
	public void setStatement(String statement) { this.statement = statement; }
	public String getJsonParam() { return jsonParam; }
	public void setJsonParam(String jsonParam) { this.jsonParam = jsonParam; }



	public static ElasticExecutedQuery toElasticExecutedQuery(String j) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(j, ElasticExecutedQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
