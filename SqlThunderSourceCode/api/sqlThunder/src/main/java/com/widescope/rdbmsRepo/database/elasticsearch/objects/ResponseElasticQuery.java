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

package com.widescope.rdbmsRepo.database.elasticsearch.objects;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.simple.JSONObject;

import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;

public class ResponseElasticQuery implements RestInterface {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
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
	

	private List<JSONObject> docs;
	public List<JSONObject> getDocs() {	return docs; }
	public void setDocs(List<JSONObject> docs) { this.docs = docs; }
	
	
	private long timestamp;
	public long getTimestamp() {	return timestamp; }
	public void setTimestamp(final long timestamp) { this.timestamp = timestamp; }
	
	private int sqlId;
	public int getSqlId() {	return sqlId; }
	public void setSqlId(final int sqlId) { this.sqlId = sqlId; }
	
	private String queryType;
	public String getQueryType() {	return queryType; }
	public void setQueryType(final String queryType) { this.queryType = queryType; }
	
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


	public ResponseElasticQuery() {
		docs = new ArrayList<JSONObject>();
		this.outputFormat = "json";
		this.outputPackaging = "plain";
		this.metadata = new ArrayList<ResultMetadata>();
		this.recordsAffected = -1;
		this.resultQueryByteArray = new byte[0];
		this.comment = null;
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ResponseElasticQuery toResponseElasticQuery(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ResponseElasticQuery.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
	
	

}
