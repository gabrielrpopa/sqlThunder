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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;




public class ResultQueryAsList implements RestInterface
{
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
	private boolean isMixedMetadata;
	public boolean getIsMixedMetadata() {	return isMixedMetadata; }
	public void setIsMixedMetadata(final boolean isMixedMetadata) { this.isMixedMetadata = isMixedMetadata; }

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
	 * used for storing metadata info, such as column names an types
	 */
	private List<ResultMetadata> metadata;
	public List<ResultMetadata> getMetadata() {	return metadata; }
	public void setMetadata(final List<ResultMetadata> metadata) { this.metadata = metadata; }
	
	
	/**
	 * used for storing CSV payload
	 */
	
	private List<HashMap<String, Object>> resultQuery;
	public List<HashMap<String, Object>> getResultQuery() {	return resultQuery; }
	public void setResultQuery(final List<HashMap<String, Object>> resultQuery) { this.resultQuery = resultQuery; }
	
	
	public ResultQueryAsList() {
		this.metadata = new ArrayList<>();
		this.resultQuery = null;
		this.isMixedMetadata = false;
	}

	public ResultQueryAsList(List<ResultMetadata> metadata) {
		this.metadata = metadata;
		this.resultQuery = null;
		this.isMixedMetadata = false;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static Map<String, ResultMetadata> metadataListToMap(final List<ResultMetadata> metadata) {
		return metadata.stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
	}
	
	
	public static boolean isMetadata(final ResultQueryAsList v1,
									 final ResultQueryAsList v2) {
		Map<String, ResultMetadata> map1 = v1.getMetadata().stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
		Map<String, ResultMetadata> map2 = v2.getMetadata().stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
		return map1.keySet().equals(map2.keySet());

	}
	
	public static boolean aggregateMatadata(final ResultQueryAsList v1,
											final ResultQueryAsList v2) {
		
		Map<String, ResultMetadata> map1 = v1.getMetadata().stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
		Map<String, ResultMetadata> map2 = v2.getMetadata().stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
		
		for ( String column : map2.keySet() ) {
			if(!map1.containsKey(column)) {
				v1.getMetadata().add(map2.get(column));
			}
		}
            
		
		
		return map1.keySet().equals(map2.keySet());
	
	}
	
	
	public static ResultQueryAsList compound(	final ResultQueryAsList v1,
												final ResultQueryAsList v2) {
		if(!v1.getMetadata().isEmpty()) {
			if(isMetadata(v1, v2)) {
				v1.getResultQuery().addAll(v2.getResultQuery());
				v1.setRecordsAffected(v1.getRecordsAffected() + v2.getRecordsAffected());
			} else {
				v1.setIsMixedMetadata(true);
				aggregateMatadata(v1, v2);				
				v1.getResultQuery().addAll(v2.getResultQuery());
				v1.setRecordsAffected(v1.getRecordsAffected() + v2.getRecordsAffected());
			}
		} else {
			v1.getResultQuery().addAll(v2.getResultQuery());
			v1.setRecordsAffected(v1.getRecordsAffected() + v2.getRecordsAffected());
		}
		
		return v1;
	}
	
}
