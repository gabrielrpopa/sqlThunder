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


package com.widescope.rdbmsRepo.database.table.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;

public class TableUtils {
	
	
	public static TableHeader createHeader(final ResultQuery resultQuery) throws Exception {
		TableHeader tableHeader = new TableHeader();
		int counter = 0;
		for(ResultMetadata resultMetadata : resultQuery.getMetadata() ) {
			counter++;
			tableHeader.addColumn(	counter, 
									new TableColumn (counter,
													resultMetadata.getColumnName(), 
													resultMetadata.getColumnTypeId(), 
													resultMetadata.getColumnTypeName(), 
													resultMetadata.getLength(), 
													resultMetadata.getScale()));
		}
		
		return tableHeader;
	}
	

	public static Table toTable(final ResultQuery resultQuery) {
		Table t = new Table();
		HashMap<String, List<HashMap<String, Object>> > mapping = null;
		JSONObject resultQueryJson = resultQuery.getResultQueryJson();
		TypeReference<HashMap<String, List<HashMap<String, Object>> >> typeRef = new TypeReference<>() {};
		try {
			t.setHeader(createHeader(resultQuery));
			mapping = new ObjectMapper().readValue(resultQueryJson.toJSONString(), typeRef);
			List<HashMap<String, Object>> lst = mapping.get("table");
			for(HashMap<String, Object> row : lst) { // rows
				Map<Integer, Object> tRow = new HashMap<Integer, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
				    System.out.println(entry.getKey() + "/" + entry.getValue());
				    Integer index = t.getHeader().gethListNameToIndex().get(entry.getKey());
				    tRow.put(index, entry.getValue());
				}
				TableRow tr = new TableRow(tRow);
				t.AddTableRow(tr);
			}
			
		} catch (Exception e1) {
			return null;
		}
		return t;
	}

}
