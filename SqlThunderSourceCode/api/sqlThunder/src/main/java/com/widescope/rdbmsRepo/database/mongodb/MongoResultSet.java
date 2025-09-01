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

package com.widescope.rdbmsRepo.database.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.FindIterable;
import com.widescope.rdbmsRepo.database.DbConnectionInfoList;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;


public class MongoResultSet implements RestInterface {

	private Map<String, String> metadata;  
	private List<org.bson.Document> resultSet;  
	private long countQuery;
	private long countCollection;
	
	public Map<String, String> getMetadata() {
		return metadata;
	}
	
	public void setMetadata(final Map<String, String> metadata) {
		this.metadata=metadata;
	}
	
	public MongoResultSet(final List<org.bson.Document> resultSet, final long countQuery, final long countCollection) {
		this.setResultSet(resultSet);
		this.setCountQuery(countQuery);
		this.setCountCollection(countCollection);
	}
	
	public MongoResultSet(final List<org.bson.Document> resultSet) {
		this.setResultSet(new ArrayList<>(resultSet));
		this.setCountQuery(resultSet.size());
		this.setCountCollection(resultSet.size());
	}

	public MongoResultSet(final org.bson.Document doc) {
		List<org.bson.Document> e = new ArrayList<>();
		e.add(doc);
		this.setResultSet(e);
		this.setCountQuery(resultSet.size());
		this.setCountCollection(resultSet.size());
	}
	
	
	public MongoResultSet()	{
		this.setResultSet(new ArrayList<org.bson.Document>());
		this.setCountQuery(0);
		this.setCountCollection(0);
	}


	public static List<String> getResultSetAsJson(List<org.bson.Document> resultSet) throws JsonProcessingException {
		List<String> ret = new ArrayList<>();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		for(Object object: resultSet ) {
			ret.add(ow.writeValueAsString(object)) ;
		}
		return ret;
	}



	public List<org.bson.Document> getResultSet() { return resultSet; }
	public void setResultSet(List<org.bson.Document> resultSet) { this.resultSet = resultSet; }
	
	public static List<String> getResultSet(List<org.bson.Document> resultSet) {
		List<String> ret = new ArrayList<String>();
		for(org.bson.Document doc: resultSet) {
			ret.add(doc.toJson());
		}
		return ret;
		
	}

	public long getCountQuery() { return countQuery; }
	public void setCountQuery(long countQuery) { this.countQuery = countQuery; }

	public long getCountCollection() { return countCollection; }
	public void setCountCollection(long countCollection) { this.countCollection = countCollection; }

	public void addResultSet(final org.bson.Document doc, final boolean isDict) {
		this.resultSet.add(doc);
		if(isDict) {
			for(String fieldName: doc.keySet()) {
				Object fieldValue = doc.get(fieldName);
				System.out.print(fieldName + ":" + fieldValue);
				this.metadata.put(fieldName, fieldName.getClass().toString());
			}
		}
		
	}
	

	
	public void add(final MongoResultSet mongoResultSet) {
		this.resultSet.addAll(mongoResultSet.getResultSet());
		this.countQuery+=mongoResultSet.getCountQuery();
		this.countCollection+=mongoResultSet.getCountCollection();
	}
	

	public void addObject(final FindIterable<org.bson.Document> objList) {
		this.countQuery = 0;
		this.countCollection = 0;
		for(org.bson.Document doc: objList) {
			resultSet.add(doc);
			countQuery++;
		}
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String toStringPretty() {
		try	{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}



	public static MongoResultSet toMongoResultSet(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, MongoResultSet.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}


	public void analyseSchemaDeep() {
		this.metadata = analyseSchemaDeep(resultSet);
	}
	
	public static Map<String, String> 
	analyseSchemaDeep(final List<org.bson.Document> resultSet) {
		Map<String, String> metadata = new HashMap<>();
		for(org.bson.Document doc: resultSet) {
			for(String fieldName: doc.keySet()) {
				metadata.put(fieldName, fieldName.getClass().getCanonicalName());
			}
		}
		return metadata;
	}
	
	public static HashMap<String, String> 
	analyseSchemaFirst(final List<org.bson.Document> resultSet) {
		HashMap<String, String> metadata = new HashMap<>();
		for(org.bson.Document doc: resultSet) {
			for(String fieldName: doc.keySet()) {
				metadata.put(fieldName, fieldName.getClass().getCanonicalName());
			}
			break;
		}
		return metadata;
	}
	
	public static List<Map<String, Object>> 
	getRecords(final List<org.bson.Document> resultSet) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		analyseSchemaFirst(resultSet);
		for(org.bson.Document doc: resultSet) {
			Map<String, Object> row = new HashMap<String, Object>();
			for(String fieldName: doc.keySet()) {
				row.put(fieldName, doc.get(fieldName));
			}
			ret.add(row);
		}
		return ret;
	}
	
	
	public static RdbmsTableSetup toRdbmsTableSetup(final MongoResultSet mongoResultSet, final String collectionName) {
		String createTblStm = SqlMetadataWrapper.createRdbmsTableStm(mongoResultSet.getMetadata(), collectionName);
		String insertStm = SqlMetadataWrapper.generateInsertTableStm(mongoResultSet.getMetadata(), "", collectionName);
		final TableFormatMap _tableFormatMap = new TableFormatMap();
        return new RdbmsTableSetup(collectionName, createTblStm, insertStm, _tableFormatMap);
		
	}
		
	
}
