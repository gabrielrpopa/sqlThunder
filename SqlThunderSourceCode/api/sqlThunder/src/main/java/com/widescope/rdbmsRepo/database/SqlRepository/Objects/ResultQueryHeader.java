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


package com.widescope.rdbmsRepo.database.SqlRepository.Objects;

import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;






public class ResultQueryHeader implements RestInterface {
	
	private MongoDbId mongoId;
	public MongoDbId getMongoDbId() {	return mongoId; }
	public void setMongoDbId(final MongoDbId mongoId) { this.mongoId = mongoId; }
	
	/*Tells the UI, if results of the query comes in streaming / push notifications*/
	private String streaming;
	public String getStreaming() {	return streaming; }
	public void setStreaming(final String streaming) { this.streaming = streaming; }
	
	/*Tells the UI, if the fired command is a DQL, DML, DDL, SP, */
	private String sqlType;
	public String getSqlType() {	return sqlType; }
	public void setSqlType(final String sqlType) { this.sqlType = sqlType; }
	
	/**
	 * States the number of records affected 
	 */
	private String user;
	public String getUser() {	return user; }
	public void setUser(final String user) { this.user = user; }
	
	/**
	 * States the number of records affected 
	 */
	private int recordsAffected;
	public int getRecordsAffected() {	return recordsAffected; }
	public void setRecordsAffected(final int recordsAffected) { this.recordsAffected = recordsAffected; }
	
	/**
	 * States the number of columns affected 
	 */
	private int columnsAffected;
	public int getColumnsAffected() {	return columnsAffected; }
	public void setColumnsAffected(final int columnsAffected) { this.columnsAffected = columnsAffected; }
	
	/**
	 * Hash 
	 */
	private long sqlHash;
	public long getSqlHash() {	return sqlHash; }
	public void setSqlHash(final long sqlHash) { this.sqlHash = sqlHash; }
	
	/**
	 * issue date 
	 */
	private long timestamp;
	public long getTimestamp() {	return timestamp; }
	public void setTimestamp(final long timestamp) { this.timestamp = timestamp; }
	
	/**
	 * sql id 
	 */
	private long sqlId;
	public long getSqlId() { return sqlId; }
	public void setSqlId(final long sqlId) { this.sqlId = sqlId; }
	
	
	/**
	 * sqlStm 
	 */
	private String sqlStm;
	public String getSqlStm() {	return sqlStm; }
	public void setSqlStm(final String sqlStm) { this.sqlStm = sqlStm; }
	
	
	/**
	 * used for specifying CSV or JSON format
	 */
	
	private String outputFormat;
	public String getOutputFormat() {	return outputFormat; }
	public void setOutputFormat(final String outputFormat) { this.outputFormat = outputFormat; }
	
	
	/**
	 * used for specifying PLAIN or COMPRESS packaging
	 */
	
	private String outputPackaging;
	public String getOutputPackaging() {	return outputPackaging; }
	public void setOutputPackaging(final String outputPackaging) { this.outputPackaging = outputPackaging; }
	
	
	/**
	 * used for storing metadata info, such as column names an types
	 */
	private List<ResultMetadata> metadata;
	public List<ResultMetadata> getMetadata() {	return metadata; }
	public void setMetadata(final List<ResultMetadata> metadata) { this.metadata = metadata; }
	
	
	/**
	 * User comment
	 */
	private String comment;
	public String getComment() {	return comment; }
	public void setComment(final String comment) { this.comment = comment; }
	
	
	
	public ResultQueryHeader() {
		this.user = "";
		this.recordsAffected = 0;
		this.columnsAffected = 0;
		this.timestamp = 0;
		this.sqlId = 0;
		this.sqlHash = 0;
		this.sqlStm = "";
		this.outputFormat = "";
		this.outputPackaging = "";

		this.metadata = new ArrayList<ResultMetadata>();
		this.mongoId = new MongoDbId();
	}
	
	
	public ResultQueryHeader(final ResultQuery resultQuery) {
		//this.mongoId = resultQuery.getMongoDbId();
		this.user = resultQuery.getUser();
		this.recordsAffected = resultQuery.getRecordsAffected();
		this.columnsAffected = resultQuery.getColumnsAffected();
		this.timestamp = resultQuery.getSqlId();
		this.sqlId = resultQuery.getSqlId();
		this.sqlHash = resultQuery.getSqlHash();
		this.sqlStm = resultQuery.getSqlStm();
		this.outputFormat = resultQuery.getOutputFormat();
		this.outputPackaging = resultQuery.getOutputPackaging();
		this.setMetadata(resultQuery.getMetadata());
	}
	
	public ResultQueryHeader(final List<ResultMetadata> metadata, 
							final String outputFormat, 
							final String outputPackaging,
							final int recordsAffected,
							final int columnsAffected,
							final String user,
							final long timestamp,
							final int sqlId,
							final long sqlHash,
							final String sqlStm
							) throws Exception {
		this.metadata = metadata;
		this.outputFormat = outputFormat;
		this.outputPackaging = outputPackaging;
		this.recordsAffected = recordsAffected;
		this.columnsAffected = columnsAffected;
		this.user = user;
		this.timestamp = timestamp;
		this.sqlId = sqlId;
		this.sqlHash = sqlHash;
		this.sqlStm = sqlStm;
		this.mongoId = new MongoDbId();
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
