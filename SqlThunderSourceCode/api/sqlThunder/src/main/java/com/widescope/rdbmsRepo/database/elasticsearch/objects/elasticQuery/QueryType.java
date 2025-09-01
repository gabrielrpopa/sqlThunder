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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;


public class QueryType implements RestInterface {
	private List<String> values;
	private String queryType;
	private String fieldName;
	private String typeName;

	public QueryType() {

	}
	
	public QueryType(final List<String> values,
					 final String typeName,
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
		this.setTypeName(typeName);
	}

	public List<String> getValues() { return values; }
	public void setValues(List<String> values) { this.values = values; }
	
	public String getQueryType() { return queryType; }
	public void setQueryType(String queryType) { this.queryType = queryType; }
	
	public String getFieldName() { return fieldName; }
	public void setFieldName(String fieldName) { this.fieldName = fieldName; }

	public String getTypeName() { return typeName; }
	public void setTypeName(String typeName) { this.typeName = typeName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static QueryType toQueryType(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, QueryType.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}


}
