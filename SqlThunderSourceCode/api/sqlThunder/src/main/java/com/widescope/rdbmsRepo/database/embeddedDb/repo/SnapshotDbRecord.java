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
import com.widescope.rest.RestInterface;

public class SnapshotDbRecord implements RestInterface{

    private long snapshotId;
    private	String fileName;
    private	String sqlName;
    private String type;
    private long userId;
    private long timestamp;
    private String sqlStatement;

    public SnapshotDbRecord() {

    }

    public SnapshotDbRecord(final long snapshotId,
                            final String fileName,
                            final String sqlName,
                            final String type,
                            final long userId,
                            final long timestamp,
                            final String sqlStatement
                           ) {
        this.setSnapshotId(snapshotId);
        this.setFileName(fileName);
        this.setSqlName(sqlName);
        this.setType(type);
        this.setUserId(userId);
        this.setTimestamp(timestamp);
        this.setSqlStatement(sqlStatement);
    }

	public long getSnapshotId() {	return snapshotId; }
	public void setSnapshotId(long snapshotId) { this.snapshotId = snapshotId; }

	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getSqlName() { return sqlName; }
	public void setSqlName(String sqlName) { this.sqlName = sqlName; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	
	public long getTimestamp() {	return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

	public String getSqlStatement() { return sqlStatement; }
	public void setSqlStatement(String sqlStatement) { this.sqlStatement = sqlStatement; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
	
}
