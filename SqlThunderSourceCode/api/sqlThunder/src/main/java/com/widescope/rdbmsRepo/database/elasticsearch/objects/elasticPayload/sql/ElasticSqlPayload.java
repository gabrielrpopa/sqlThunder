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

package com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.sql;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ElasticSqlPayload {

	private List<ElasticSqlColumn> columns;	
	private List<List<Object>> rows;
	private String cursor;

	public ElasticSqlPayload(	final List<ElasticSqlColumn> columns,
								final List<List<Object>> rows,	
								final String cursor) {
		this.setColumns(columns);
		this.setRows(rows);
		this.setCursor(cursor);
	}
	public List<ElasticSqlColumn> getColumns() { return columns; }
	public void setColumns(List<ElasticSqlColumn> columns) { this.columns = columns; }
	public List<List<Object>> getRows() { return rows; }
	public void setRows(List<List<Object>> rows) { this.rows = rows; }
	public String getCursor() {
		return cursor;
	}
	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public static ElasticSqlPayload toElasticSqlPayload(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticSqlPayload.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

	public static Map<String, String> 
	getMetadataAsMap(final ElasticSqlPayload p) {
		Map<String, String> ret = new HashMap<String, String>();
		for(ElasticSqlColumn c: p.getColumns()) {
			ret.put(c.getName(), c.getType());
		}
		return ret;
	}

	public static List<Map<String, Object>> 
	getRowAsListOfMap(final ElasticSqlPayload p) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for(List<Object> row: p.getRows()) {
			Map<String, Object> rowWithCol = new HashMap<String, Object>();
			for (int i = 0; i < row.size(); i++) {
				rowWithCol.put(p.getColumns().get(i).getName(), row.get(i));
	        }
			ret.add(rowWithCol);
		}
		return ret;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}

