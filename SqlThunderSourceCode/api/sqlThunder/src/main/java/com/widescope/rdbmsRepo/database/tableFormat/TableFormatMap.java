/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")){
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


package com.widescope.rdbmsRepo.database.tableFormat;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.simple.JSONObject;

import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;


public class TableFormatMap implements RestInterface  {
	
	private Map<String, String> metadata;
	private Map<String, ResultMetadata> extendedMetadata;
	
	private List<Map<String,Object>> rows;
	private int colCount;
	private long rowCount;
	
	public Map<String, String> getMetadata() { return metadata; }
	public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
	public void addMetadata(final String name, final String type) { 
		this.metadata.put(name, type); 
	}
	
	public List<Map<String,Object>> getRows() { return rows; }
	public void setRows(List<Map<String,Object>> rows) { this.rows = rows; }
	public void addRows(List<Map<String,Object>> rows) { this.rows.addAll(rows); }
	public void addRow(Map<String,Object> row) { this.rows.add(row); }
	
	public int getColCount() { return colCount; }
	public void setColCount(int colCount) { this.colCount = colCount; }
	public long getRowCount() { return rowCount; }
	public void setRowCount(long rowCount) { this.rowCount = rowCount; }
	public Map<String, ResultMetadata> getExtendedMetadata() { return extendedMetadata; }
	public void setExtendedMetadata(Map<String, ResultMetadata> extendedMetadata) { this.extendedMetadata = extendedMetadata; }

	
	public TableFormatMap() {
		this.setMetadata(new HashMap<String, String>());
		this.setRows(new ArrayList<Map<String,Object>>());
	}
	
	public List<String> getListOfColumns() {
		return new ArrayList<> (metadata.keySet()).stream().sorted().collect(Collectors.toList());
	}
	
	public List<String> getListOfRows() {
		List<String> ret = new ArrayList<>();
		for(Map<String,Object> o: rows) {
			ret.add(new JSONObject(o).toJSONString());
		}
		return ret;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static TableFormatMap toTableFormatMap(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, TableFormatMap.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

		
}
