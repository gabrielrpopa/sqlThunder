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


package com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticQuery;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class QueryType implements RestInterface {
	private List<Object> values;  /*maybe better List<? extends Object>  or List<?> to be reifiable*/
	private String queryType;
	private String fieldName;
	
	public QueryType(final String typeName, 
						final List<Object> values,
						final String queryType,
						final String fieldName) throws Exception {
		
		
		if(!queryType.equalsIgnoreCase("terms") &&
				!queryType.equalsIgnoreCase("term") &&
				!queryType.equalsIgnoreCase("prefix") &&
				!queryType.equalsIgnoreCase("range") &&
				!queryType.equalsIgnoreCase("fuzzy")) {
			
			throw new Exception("No Accepted query type");
		}
		this.setValues(values);
		this.setQueryType(queryType);
		this.setFieldName(fieldName);
	}

	public List<Object> getValues() { return values; }
	public void setValues(List<Object> values) { this.values = values; }
	
	public String getQueryType() { return queryType; }
	public void setQueryType(String queryType) { this.queryType = queryType; }
	
	public String getFieldName() { return fieldName; }
	public void setFieldName(String fieldName) { this.fieldName = fieldName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
