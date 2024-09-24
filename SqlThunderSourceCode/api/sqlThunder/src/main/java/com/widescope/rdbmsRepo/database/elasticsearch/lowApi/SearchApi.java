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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticQuery.QueryType;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.management.IndexCharacteristic;
public class SearchApi {


	public static boolean 
	validateDsl(final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String indexName, 
				final JSONObject dslObject ) throws Exception {
		try {
			Request request = new Request("GET","/" + indexName + "/_validate/query");
			request.setJsonEntity(dslObject.toJSONString());
			
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());

			JSONParser parser = new JSONParser(); 
			JSONObject json = (JSONObject) parser.parse(responseBody);
            return (Boolean)json.get("valid");
			
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	

	public static List<JSONObject> 
	parseDqlResponse(final String responseBody) throws Exception {

		JSONParser parser = new JSONParser(); 
		JSONObject json = null;
		List<JSONObject> arrayOfDocs = new ArrayList<>();
		try {
			json = (JSONObject) parser.parse(responseBody);
			String jsonHits = json.get("hits").toString();
			json = (JSONObject) parser.parse(jsonHits);
			JSONArray arrayOfObjects = (JSONArray)json.get("hits");

            for (Object arrayOfObject : arrayOfObjects) {
                String obj = arrayOfObject.toString();
                json = (JSONObject) parser.parse(obj);
                String docStr = json.get("_source").toString();
                JSONObject doc = (JSONObject) parser.parse(docStr);
                arrayOfDocs.add(doc);
            }
			
			return arrayOfDocs;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	
	private static List<IndexCharacteristic> 
	parseResponseList(final String responseBody,
						final String index) throws Exception {

		JSONParser parser = new JSONParser(); 
		JSONArray arrayOfObjects = null;
		List<IndexCharacteristic> arrayOfDocs = new ArrayList<>();
		int count = 0;
		try {
			arrayOfObjects = (JSONArray) parser.parse(responseBody);
            for (Object arrayOfObject : arrayOfObjects) {
                String obj = arrayOfObject.toString();
                JSONObject json = (JSONObject) parser.parse(obj);
                IndexCharacteristic indexCharacteristic = new IndexCharacteristic();
                indexCharacteristic.setPosition(count);
                indexCharacteristic.setHealth(json.get("health").toString());
                indexCharacteristic.setStatus(json.get("status").toString());
                indexCharacteristic.setIndex(json.get("index").toString());
                indexCharacteristic.setUuid(json.get("uuid").toString());
                indexCharacteristic.setPri(json.get("pri").toString());
                indexCharacteristic.setRep(json.get("rep").toString());
                indexCharacteristic.setDocs_count(json.get("docs.count").toString());
                indexCharacteristic.setDocs_deleted(json.get("docs.deleted").toString());

                indexCharacteristic.setStore_size(json.get("store.size").toString());
                indexCharacteristic.setPri_store_size(json.get("pri.store.size").toString());

                if (indexCharacteristic.getIndex().charAt(0) == '.') {
                    indexCharacteristic.setType("SYSTEM");
                } else {
                    indexCharacteristic.setType("USER");
                }

                if (indexCharacteristic.getIndex().toLowerCase().contains(index.toLowerCase())) {
                    arrayOfDocs.add(indexCharacteristic);
                    count++;
                }
            }
			return arrayOfDocs;
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	

	public static List<JSONObject> 
	searchMatch(final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String indexName, 
				final String term, 
				final String match,
				final Integer fromRecno,
				final Integer size) throws Exception {

		try {
			Request request = new Request("GET","/" + indexName + "/_search");
			//construct a JSON query like {"query": {"match": {"<term>": "<match"}}
			Map<String, String> termJson = new HashMap<>();
			termJson.put(term, match);
			Map<String, Map<String, String>> matchJson = new HashMap<>();
			matchJson.put("match", termJson);
			Map<String, Object > queryJson = new HashMap<>();
			queryJson.put("query", matchJson);
			if(fromRecno > 0 && size > 0) {
				queryJson.put("from", fromRecno );
				queryJson.put("size", size );
			}
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(queryJson);
			request.setJsonEntity(queryJsonString);
			
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			
			return parseDqlResponse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	

	public static List<JSONObject> 
	searchFuzzy(final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String indexName, 
				final QueryType queryType,
				final Integer fromRecno,
				final Integer size) throws Exception {

		try {
			Request request = new Request("GET","/" + indexName + "/_search");
			Map<String, Object> query = new HashMap<>();
			
			Map<String, Map<String, Object> > fuzzy = new HashMap<>();
			if(queryType.getQueryType().equalsIgnoreCase("fuzzy")) {
				
				
				Map<String, Object> t = new HashMap<>();
				if(queryType.getValues().size() == 1) {
					t.put("value", queryType.getValues().get(0));
				} else {
					throw new Exception ("Expected only one value for fuzzy search");
				}
				fuzzy.put(queryType.getFieldName() + ".keyword", t);
			} else {
				throw new Exception("queryType not provided properly");
			}
			
			query.put("fuzzy", fuzzy);
			
			
			Map<String, Object> fullBody = new HashMap<>();
			fullBody.put("query", query);
			if(fromRecno > 0 && size > 0) {
				fullBody.put("from", fromRecno );
				fullBody.put("size", size );
			}
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(fullBody);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			return parseDqlResponse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static List<JSONObject> 
	searchPrefix(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
					final String indexName, 
					final QueryType queryType,
					final Integer fromRecno,
					final Integer size) throws Exception {

		try {
			Request request = new Request("GET","/" + indexName + "/_search");
			Map<String, Object> query = new HashMap<>();
			
			Map<String, Map<String, Object> > prefix = new HashMap<>();
			if(queryType.getQueryType().equalsIgnoreCase("prefix")) {
				Map<String, Object> t = new HashMap<>();
				if(queryType.getValues().size() == 1) {
					t.put("value", queryType.getValues().get(0));
				} else {
					throw new Exception ("Expected only one value for prefix search");
				}
				prefix.put(queryType.getFieldName() + ".keyword", t);
			}
			query.put("prefix", prefix);
			
			Map<String, Object> fullBody = new HashMap<>();
			fullBody.put("query", query);
			if(fromRecno > 0 && size > 0) {
				fullBody.put("from", fromRecno );
				fullBody.put("size", size );
			}
			
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(fullBody);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			return parseDqlResponse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static List<JSONObject> 
	searchTerm(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String indexName, 
				final QueryType queryType,
				final Integer fromRecno,
				final Integer size) throws Exception {

		try {
			Request request = new Request("GET","/" + indexName + "/_search");
			Map<String, Object> query = new HashMap<>();
			Map<String, Map<String, Object> > prefix = new HashMap<>();
			Map<String, Object> t = new HashMap<>();
			
			if( !queryType.getQueryType().equalsIgnoreCase("term") ||
					!queryType.getQueryType().equalsIgnoreCase("terms")) {
				throw new Exception ("Expected term or terms query type keyword");
			}
			
			if( queryType.getValues().size() == 1 ) {
				t.put("value", queryType.getValues().get(0));
				prefix.put(queryType.getFieldName() + ".keyword", t);
				query.put("term", prefix);
			} else if ( queryType.getValues().size() > 1 ) {
				t.put("value", queryType.getValues() );
				prefix.put(queryType.getFieldName() + ".keyword", t);
				query.put("terms", prefix);
			}

			Map<String, Object> fullBody = new HashMap<>();
			fullBody.put("query", query);
			if(fromRecno > 0 && size > 0) {
				fullBody.put("from", fromRecno );
				fullBody.put("size", size );
			}
			
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(fullBody);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			return parseDqlResponse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static List<JSONObject> 
	searchRange(final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String indexName, 
				final QueryType queryType,
				final Integer fromRecno,
				final Integer size) throws Exception {

		try {
			Request request = new Request("GET","/" + indexName + "/_search");
			Map<String, Object> query = new HashMap<>();
			
			Map<String, Map<String, Object> > range = new HashMap<>();
			if(queryType.getQueryType().equalsIgnoreCase("range")) {
				Map<String, Object> t = new HashMap<>();
				if(queryType.getValues().size() != 2) {
					throw new Exception ("Expected two params. One for gte, another one for lte");
				}
				t.put("gte", queryType.getValues().get(0));
				t.put("lte", queryType.getValues().get(1));

				range.put(queryType.getFieldName() + ".keyword", t);
			}
			query.put("range", range);
			
			Map<String, Object> fullBody = new HashMap<>();
			fullBody.put("query", query);
			if(fromRecno > 0 && size > 0) {
				fullBody.put("from", fromRecno );
				fullBody.put("size", size );
			}
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(fullBody);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			return parseDqlResponse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	

	public static List<JSONObject> 
	searchMatch(final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String indexName, 
				final QueryType queryType,
				final Integer fromRecno,
				final Integer size) throws Exception {

		try {
			Request request = new Request("GET","/" + indexName + "/_search");
			Map<String, Object> query = new HashMap<>();
			
			Map<String, Map<String, Object> > match = new HashMap<>();
			if(queryType.getQueryType().equalsIgnoreCase("match")) {
				Map<String, Object> t = new HashMap<>();
				if(queryType.getValues().size() != 1) {
					throw new Exception ("Expected two params. One for gte, another one for lte");
				}
				t.put("query", queryType.getValues().get(0));
				match.put(queryType.getFieldName() + ".keyword", t);
			}
			query.put("match", match);
			
			Map<String, Object> fullBody = new HashMap<>();
			fullBody.put("query", query);
			if(fromRecno > 0 && size > 0) {
				fullBody.put("from", fromRecno );
				fullBody.put("size", size );
			}
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(fullBody);
			request.setJsonEntity(queryJsonString);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			return parseDqlResponse(responseBody);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	

	public static String 
	getDocIDLastOrFirstInserted(final ElasticLowLevelWrapper elasticLowLevelWrapper,
								final String indexName,
								final boolean isLast) throws Exception {

		try {
			Request request = new Request("POST", indexName + "/_search");
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("size", 1);
			Map<String, Object> sort = new HashMap<>();
			if(isLast)
				sort.put("_id", "desc");
			else
				sort.put("_id", "asc");
			
			Map<String, Object> query = new HashMap<>();
			query.put("match_all", new HashMap<Object, Object>());
			
			requestBody.put("sort", sort );
			requestBody.put("query", query );
			
			Gson gson = new Gson();
			String queryJsonString = gson.toJson(requestBody);
			request.setJsonEntity(queryJsonString);
			
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			
			JSONParser parser = new JSONParser(); 
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(responseBody);
				String jsonHits = json.get("hits").toString();
				
				json = (JSONObject) parser.parse(jsonHits);
				JSONArray obj = (JSONArray)json.get("hits");
				JSONObject o = (JSONObject)obj.get(0);
                return o.get("_id").toString();
			} catch(Exception ex) {
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
			}
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	
	public static List<IndexCharacteristic>
	listIndexes(final ElasticLowLevelWrapper elasticLowLevelWrapper,
				final String index) throws Exception {
		try {
			/*GET _cat/indices?format=json&pretty=true*/
			Request request = new Request("GET","/_cat/indices?format=json&bytes=b&s=store.size:desc,index:asc&v=true");
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			String responseBody = EntityUtils.toString(response.getEntity());
            return parseResponseList(responseBody, index);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
}
