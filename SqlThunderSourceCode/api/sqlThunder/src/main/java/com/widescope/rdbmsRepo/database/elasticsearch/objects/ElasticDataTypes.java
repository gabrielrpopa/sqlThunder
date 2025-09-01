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

import java.util.HashMap;
import java.util.Map;

public class ElasticDataTypes {

	public Map<String, String> elasticToJavaTypes ;
	public Map<String, String> javaToElasticTypes ;
	public ElasticDataTypes() {
		elasticToJavaTypes = new HashMap<String, String>();
		elasticToJavaTypes.put("object", "Object"); // object/ inner JSON object.
		elasticToJavaTypes.put("binary", "byte[]"); // Binary value encoded as a Base64 string.
		elasticToJavaTypes.put("boolean", "boolean"); // true and false values.
		
		// Keywords
		elasticToJavaTypes.put("keyword", "String"); // keyword
		elasticToJavaTypes.put("constant_keyword", "String"); // keyword
		elasticToJavaTypes.put("wildcard", "json"); // keyword
		
		// Numbers
		elasticToJavaTypes.put("long", "long"); // long
		elasticToJavaTypes.put("double", "double"); // double
		elasticToJavaTypes.put("integer", "integer"); // integer
		elasticToJavaTypes.put("short", "short"); // short
		elasticToJavaTypes.put("float", "float"); // short
		elasticToJavaTypes.put("byte", "byte"); // byte
		
		
		// Dates
		elasticToJavaTypes.put("date", "Date"); // date
		elasticToJavaTypes.put("date_nanos", "Timestamp"); // date

		
		
		javaToElasticTypes = new HashMap<String, String>();
		javaToElasticTypes.put("Object", "object"); // object/ inner JSON object.
		javaToElasticTypes.put("byte[]", "binary"); // Binary value encoded as a Base64 string.
		javaToElasticTypes.put("boolean", "boolean"); // true and false values.
		
		// Keywords
		javaToElasticTypes.put("varchar", "keyword"); // keyword
		javaToElasticTypes.put("text", "text"); // keyword
		javaToElasticTypes.put("String", "constant_keyword"); // keyword
		javaToElasticTypes.put("varchar[]", "wildcard"); // keyword
		
		// Numbers
		javaToElasticTypes.put("bigint", "long"); // long
		javaToElasticTypes.put("long", "long"); // long
		javaToElasticTypes.put("double", "double"); // double
		javaToElasticTypes.put("integer", "integer"); // integer
		javaToElasticTypes.put("short", "short"); // short
		javaToElasticTypes.put("float", "float"); // short
		javaToElasticTypes.put("byte", "byte"); // byte
		
		
		// Dates
		javaToElasticTypes.put("Date", "date"); // date
		javaToElasticTypes.put("Timestamp", "date_nanos"); // date
		
		
		
		
		
		
		
	}

}
