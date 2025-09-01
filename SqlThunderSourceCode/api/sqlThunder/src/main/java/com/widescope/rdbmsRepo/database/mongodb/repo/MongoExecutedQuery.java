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


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.ExecutedStatement;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.sql.SqlResponse;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.StringUtils;
import org.json.simple.JSONObject;


public class MongoExecutedQuery extends ExecutedStatement {

	private	long statementId;
	private	String statementName;
	private	String statementType;
	private String statement;
	private String jsonParam;
	private	long clusterId;
	private String database;
	private String collection;


	public MongoExecutedQuery(final long id,
							  final String requestId,
							  final long statementId,
							  final String statementName,
							  final String statementType,
							  final String statement,
							  final String isValid,
							  final String jsonParam,
							  final long clusterId,
							  final String database,
							  final String collection,
							  final long groupId,
							  final String source,
							  final long userId,
							  final String repPath,
							  final String comment,
							  final long timestamp,
							  final int cntAccess) {
		this.setId(id);
		this.setRequestId(requestId);
		this.setStatementId(statementId);
		this.setStatementName(statementName);
		this.setStatementType(statementType);
		this.setStatement(statement);
		this.setIsValid(isValid);
		this.setJsonParam(jsonParam);
		this.setClusterId(clusterId);
		this.setDatabase(database);
		this.setCollection(collection);
		this.setGroupId(groupId);
		this.setSource(source);
		this.setUserId(userId);
		this.setRepPath(repPath);
		this.setComment(comment);
		this.setTimestamp(timestamp);
		this.setFlag(-1);
		this.setCntAccess(cntAccess);


	}


	public MongoExecutedQuery(final long id, final long userId) {
		this.setId(id);
		this.setRequestId(StringUtils.generateRequestId());
		this.setStatementId(-1);
		this.setClusterId(-1);
		this.setStatementName("");
		this.setGroupId(-1);
		this.setSource("");
		this.setUserId(userId);
		this.setStatement("");
		this.setIsValid("N");
		this.setJsonParam("");
		this.setRepPath("");
		this.setComment("");
		this.setTimestamp(-1);
		this.setFlag(-1);
		this.setCntAccess(-1);
		this.setIsValid("N");
	}

	public MongoExecutedQuery() {
		this.setId(-1);
		this.setRequestId(StringUtils.generateRequestId());
		this.setStatementId(-1);
		this.setClusterId(-1);
		this.setStatementName("");
		this.setGroupId(-1);
		this.setSource("");
		this.setUserId(-1);
		this.setStatement("");
		this.setIsValid("N");
		this.setJsonParam("");
		this.setTimestamp(-1);
		this.setFlag(-1);
		this.setCntAccess(-1);
	}




	public long getStatementId() { return statementId; }
	public void setStatementId(long statementId) { this.statementId = statementId; }
	public String getStatementName() { return statementName; }
	public void setStatementName(String statementName) { this.statementName = statementName; }
	public String getStatementType() { return statementType; }
	public void setStatementType(String statementType) { this.statementType = statementType; }
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	public String getDatabase() { return database; }
	public void setDatabase(String database) { this.database = database; }
	public String getCollection() { return collection; }
	public void setCollection(String collection) { this.collection = collection; }
	public String getStatement() { return statement; }
	public void setStatement(String statement) { this.statement = statement; }
	public String getJsonParam() { return jsonParam; }
	public void setJsonParam(String jsonParam) { this.jsonParam = jsonParam; }



	public static MongoExecutedQuery toMongoExecutedQuery(String j) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(j, MongoExecutedQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
