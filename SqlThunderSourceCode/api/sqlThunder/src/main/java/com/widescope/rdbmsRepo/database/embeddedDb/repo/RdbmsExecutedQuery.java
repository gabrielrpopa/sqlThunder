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


package com.widescope.rdbmsRepo.database.embeddedDb.repo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.ExecutedStatement;


public class RdbmsExecutedQuery extends ExecutedStatement  {

    private long databaseId;
    private long statementId;
    private	String statementName;
    private String statementType;
    private String statement;
    private	String jsonParam;
    private	String dbType;      /*RdbmsExecutedQueriesDbRepo.dbTypes*/


    public RdbmsExecutedQuery() {
        this.setId(-1);
        this.repPath = "";
        setCntAccess(-1);
        this.setFlag(-1);
        this.setIsValid("N");
    }

    public RdbmsExecutedQuery(final long id,
                              final String requestId,
                              final long databaseId,
                              final long statementId,
                              final String statementName,
                              final String statementType,
                              final String statement,
                              final String isValid,
                              final String jsonParam,
                              final String dbType,
                              final String source,
                              final long groupId,
                              final long userId,
                              final long timestamp,
                              final String repPath,
                              final String comment,
                              final int cntAccess) {

        this.setId(id);
        this.setRequestId(requestId);
        this.setStatementId(statementId);
        this.setDatabaseId(databaseId);
        this.setStatementName(statementName);
        this.setStatementType(statementType);
        this.setStatement(statement);
        this.setIsValid(isValid);
        this.setJsonParam(jsonParam);
        this.setDbType(dbType);
        this.setSource(source);
        this.setGroupId(groupId);
        this.setUserId(userId);
        this.setTimestamp(timestamp);
        this.setRepPath(repPath);
        this.setComment(comment);
        this.setFlag(-1);
        this.setCntAccess(cntAccess);
    }


    public long getDatabaseId() { return databaseId; }
    public void setDatabaseId(long databaseId) { this.databaseId = databaseId; }

    public long getStatementId() { return statementId; }
    public void setStatementId(long statementId) { this.statementId = statementId; }

    public String getDbType() { return dbType; }
    public void setDbType(String dbType) { this.dbType = dbType; }

    public String getStatementType()  { return statementType; }
    public void setStatementType(String statementType) { this.statementType = statementType; }

    public String getStatementName() { return statementName; }
    public void setStatementName(String statementName) { this.statementName = statementName; }

    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    public String getJsonParam() { return jsonParam; }
    public void setJsonParam(String jsonParam) { this.jsonParam = jsonParam; }


    public static RdbmsExecutedQuery toRdbmsExecutedQuery(String j) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(j, RdbmsExecutedQuery.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
