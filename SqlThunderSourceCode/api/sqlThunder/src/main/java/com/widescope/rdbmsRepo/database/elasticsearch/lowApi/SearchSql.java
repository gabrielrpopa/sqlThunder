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


package com.widescope.rdbmsRepo.database.elasticsearch.lowApi;

import java.util.HashMap;
import java.util.Map;


import com.widescope.logging.AppLogger;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.sql.ElasticSqlPayload;


public class SearchSql {

	public static JSONObject 
	searchSql(	ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String sqlContent,
				final Integer fetchSize	) throws Exception {
		try {
			Request request = new Request("POST","_sql"); // ?format=txt

			Map<String, Object> body = new HashMap<>();
			body.put("query", sqlContent);

			Gson gson = new Gson();
			String queryJsonString = gson.toJson(body);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			JSONParser parser = new JSONParser(); 
			return (JSONObject) parser.parse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static ElasticSqlPayload 
	searchSqlAsElasticSqlPayload(	ElasticLowLevelWrapper elasticLowLevelWrapper,
									final String sqlContent,
									final Integer fetchSize	) throws Exception {

		try {
			Request request = new Request("POST","_sql"); // ?format=txt

			Map<String, Object> body = new HashMap<>();
			body.put("query", sqlContent);
			
			if(fetchSize > 0)
				body.put("fetch_size", fetchSize);
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(body);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			return ElasticSqlPayload.toElasticSqlPayload(EntityUtils.toString(response.getEntity()));
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static JSONObject 
	translateSql(	ElasticLowLevelWrapper elasticLowLevelWrapper,
					final String sqlContent	) throws Exception {
		try {
			Request request = new Request("POST","_sql/translate");
			Map<String, Object> body = new HashMap<>();
			body.put("query", sqlContent);
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(body);
			request.setJsonEntity(queryJsonString);
			
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			
			JSONParser parser = new JSONParser(); 
			return (JSONObject) parser.parse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	
	
}
