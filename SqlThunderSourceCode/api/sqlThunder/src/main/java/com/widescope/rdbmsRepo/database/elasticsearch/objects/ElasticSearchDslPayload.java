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

import com.google.gson.Gson;
import com.widescope.logging.AppLogger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ElasticSearchDslPayload {

	private String took;
	private long hitsTotal;
	private String index;
	private String id;
	private String type;
	private String fields;
	
	public String getTook() {	return took; }
	public void setTook(String took) { this.took = took; }
	public long getHitsTotal() { return hitsTotal; }
	public void setHitsTotal(long hitsTotal) { this.hitsTotal = hitsTotal; }
	public String getIndex() { return index; }
	public void setIndex(String index) { this.index = index; }
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getFields() { return fields; }
	public void setFields(String fields) { this.fields = fields; }
	
	public ElasticSearchDslPayload() {
		this.setTook(null);
		this.setHitsTotal(-1);
		this.setIndex(null);
		this.setId(null);
		this.setType(null);
		this.setFields(null);
	}
	
	
	public ElasticSearchDslPayload(	final String took,
									final long hitsTotal,
									final String index,
									final String id,
									final String type,
									final String fields) {
		this.setTook(took);
		this.setHitsTotal(hitsTotal);
		this.setIndex(index);
		this.setId(id);
		this.setType(type);
		this.setFields(fields);
	}

	public static ElasticSearchDslPayload parseSearchPayload(String responseBody) throws Exception {

		ElasticSearchDslPayload elasticSearchDslPayload = new ElasticSearchDslPayload();
		if(responseBody == null || responseBody.isBlank() || responseBody.isEmpty())
			return elasticSearchDslPayload;

		try {
			JSONParser parser = new JSONParser();  
			JSONObject json = (JSONObject) parser.parse(responseBody);  
			String took = (String) json.get("took");
			String hitsStr = (String) json.get("hits");
			JSONObject jsonHits = (JSONObject) parser.parse(hitsStr);
			String total = (String) jsonHits.get("total");
			JSONObject totalJson = (JSONObject) parser.parse(total);
			long totalValue = Long.parseLong( totalJson.get("value").toString() ) ;
			String index = (String) jsonHits.get("_index");
			String id = (String) jsonHits.get("_id");
			String type = (String) jsonHits.get("_type");
			String fields = (String) jsonHits.get("fields");
			elasticSearchDslPayload = new ElasticSearchDslPayload(took, totalValue, index, id, type, fields);
		} catch (JSONException e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
		return elasticSearchDslPayload;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
