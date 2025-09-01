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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
import com.google.gson.GsonBuilder;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.HitsInner2;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload2;


public class ElasticInfo {

	@SuppressWarnings("unchecked")
	public static Object 
	executeGeneric(ElasticLowLevelWrapper elasticLowLevelWrapper,
					final String httpVerb,
					final String endpoint,
					final String isOriginalFormat,
					final String obj) throws Exception {

		JSONParser parser = new JSONParser();
		JSONObject json = null;
		final boolean b = obj != null && !obj.isBlank() && !obj.isEmpty();
		if(b)
			json = (JSONObject) parser.parse(obj);
		
		Request request = new Request(httpVerb, endpoint);
		if(b)
			request.setJsonEntity(json.toJSONString());
	
		
		if(isOriginalFormat.equalsIgnoreCase("Y")) {
			try {
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				return  parser.parse(EntityUtils.toString(response.getEntity()));
			} catch(Exception ex) {
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
			}
		} else {
			try {
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				JSONObject ret = new JSONObject();
				JSONObject retObject = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
				List<JSONObject> lstObjects = SearchApi.parseDqlResponse(retObject.toJSONString());
				JSONArray jArray = new JSONArray();
				jArray.addAll(lstObjects);
				ret.put("listOfObjects", jArray);
				return ret;
			} catch(Exception ex) {
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
			}
		}

	}
	
	
	

	public static ElasticPayload2
	executeGenericForPayload2(	ElasticLowLevelWrapper elasticLowLevelWrapper,
								final String httpVerb,
								final String endpoint,
								final String obj) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		final boolean b = obj != null && !obj.isBlank() && !obj.isEmpty();
		if(b)
			json = (JSONObject) parser.parse(obj);
		
		Request request = new Request(httpVerb, endpoint);
		if(b)
			request.setJsonEntity(json.toJSONString());
		
		Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);

		String responseBody = EntityUtils.toString(response.getEntity());
		return ElasticPayload2.toElasticPayload2(responseBody);
		
	}
	
	public static ElasticPayload parseResponse(Object resp) throws Exception {
		try {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(resp.toString(), ElasticPayload.class);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}

	/**
	 * 
	 * @param response
	 * @param charSet: US-ASCII, ISO-8859-1, UTF-8, UTF-16BE, UTF-16LE, UTF-16
	 * @return
	 * @throws IOException
	 */
	public static ElasticPayload2 parseResponse(Response response, String charSet) throws Exception {
		final int code = response.getStatusLine().getStatusCode();
		
		/* 	US-ASCII, ISO-8859-1, UTF-8, UTF-16BE, UTF-16LE, UTF-16 */
		Charset c = Charset.forName(charSet);
		
		if (code >= 200 & code < 300) {
			InputStream inputStream = response.getEntity().getContent();
		    byte[] result = org.apache.commons.io.IOUtils.toByteArray(inputStream);
		    inputStream.close();
            return ElasticPayload2.toElasticPayload2(new String(result, c));
		} else {
		    String errorMessage 
		    = String.format("ElasticSearch reported an error while trying to run the query: %s",
		    				response.getStatusLine().getReasonPhrase());
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, errorMessage)) ;
		}
	}
	
	
	public static Map<String, String> getMetadata(final ElasticPayload2 payload) {
		Map<String, String> metadata = new HashMap<String, String>();
		List<HitsInner2> lHits = payload.getHits().getHits();
		for(HitsInner2 hit: lHits) {
			Map<String, Object> fRow = hit.get_source();
			for (Map.Entry<String, Object> set : fRow.entrySet()) {
				metadata.put(set.getKey(), set.getValue().getClass().getCanonicalName());
	        }
			break;
		}
		return metadata;
	}
	
	
	public static List<Map<String, Object>> getRows(final ElasticPayload2 payload) {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		List<HitsInner2> lHits = payload.getHits().getHits();
		for(HitsInner2 h: lHits) {
			rows.add(h.get_source());
		}
		return rows;
	}
	
	
}
