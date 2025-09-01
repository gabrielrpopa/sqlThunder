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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.simple.JSONObject;

import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.table.model.Table;





public class ResultQuery implements RestInterface {
	
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
	public long getSqlId() {	return sqlId; }
	public void setSqlId(final long sqlId) { this.sqlId = sqlId; }
	
	private String sqlName;
	public String getSqlName() {	return sqlName; }
	public void setSqlName(final String sqlName) { this.sqlName = sqlName; }
	
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
	 * used for storing CSV payload
	 */
	
	private String resultQuery;
	public String getResultQuery() {	return resultQuery; }
	public void setResultQuery(final String resultQuery) { this.resultQuery = resultQuery; }
	
	
	/**
	 * used for storing JSON payload
	 */
	private JSONObject resultQueryJson;
	public JSONObject getResultQueryJson() {	return resultQueryJson; }
	public void setResultQueryJson(final JSONObject resultQueryJson) { this.resultQueryJson = resultQueryJson; }
	
	
	/**
	 * used for storing Table payload
	 */
	private Table resultQueryTable;
	public Table getResultQueryTable() {	return resultQueryTable; }
	public void setResultQueryTable(final Table resultQueryTable) { this.resultQueryTable = resultQueryTable; }
	
	
	/**
	 * used for storing compressed payload
	 */
	private byte[] resultQueryByteArray;
	public byte[] getResultQueryByteArray() {	return resultQueryByteArray; }
	public void setResultQueryByteArray(final byte[] resultQueryByteArray) { this.resultQueryByteArray = resultQueryByteArray; }
	
	
	/**
	 * User comment
	 */
	
	private String comment;
	public String getComment() {	return comment; }
	public void setComment(final String comment) { this.comment = comment; }
	
	
	public ResultQuery() {
		// init with default
		this.outputFormat = "json";
		this.outputPackaging = "plain";
		this.metadata = new ArrayList<ResultMetadata>();
		this.resultQuery = null;
		this.resultQueryJson = null;
		this.recordsAffected = -1;
		this.resultQueryByteArray = new byte[0];
		this.resultQueryTable = new Table();
		//this.mongoId = new MongoDbId();
		this.comment = null;
		this.sqlName = null;
	}
	
	
	public ResultQuery(final List<ResultMetadata> metadata, 
			           final String resultQuery, 
			           final JSONObject resultQueryJson,
			           final Table resultQueryTable,
			           String outputFormat, 
			           String outputPackaging,
			           int recordsAffected) throws Exception {
		this.metadata = metadata;
		this.outputFormat = outputFormat;
		this.outputPackaging = outputPackaging;
		this.resultQuery = resultQuery;
		this.resultQueryJson = resultQueryJson;
		this.recordsAffected = recordsAffected;
		//this.resultQueryByteArray = new byte[0];
		this.resultQueryTable = resultQueryTable;
		
		//this.mongoId = new MongoDbId();
		this.comment = null;
		this.sqlName = null;
	}

	public ResultQuery(final int recordsAffected) throws Exception {
		this.outputFormat = "json";
		this.outputPackaging = "plain";
		this.metadata = new ArrayList<ResultMetadata>();
		this.resultQuery = null;
		this.resultQueryJson = null;
		this.recordsAffected = recordsAffected;
		this.resultQueryByteArray = new byte[0];
		this.resultQueryTable = new Table();
		//this.mongoId = new MongoDbId();
		this.comment = null;
		this.sqlName = null;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}



	public static ResultQuery toResultQuery(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ResultQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

	public static ResultQuery toResultQuery(byte[] bytes) {
		Gson gson = new Gson();
		try	{
			String str = new String(bytes, StandardCharsets.UTF_8);
			return gson.fromJson(str, ResultQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}
	
	public static Map<String, ResultMetadata> 
	metadataListToMap(final List<ResultMetadata> metadata) {
		return metadata.stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
	}
	
}
