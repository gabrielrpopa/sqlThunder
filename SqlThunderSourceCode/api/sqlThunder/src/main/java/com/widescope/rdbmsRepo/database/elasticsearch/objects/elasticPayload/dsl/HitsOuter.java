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


package com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl;



import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class HitsOuter {

	private Total total;
	private double max_score;
	private JSONArray hits;

	
	public HitsOuter(	final JSONObject total,
						final double max_score,
						final JSONArray hits) {
		this.setTotal(new  Total());
		this.setMax_score(0.0);
		this.setHits(new  JSONArray());;
	}

	public Total getTotal() { return total; }
	public void setTotal(Total total) { this.total = total; }

	public double getMax_score() { return max_score; }
	public void setMax_score(double max_score) { this.max_score = max_score; }

	public JSONArray getHits() { return hits; }
	public void setHits(JSONArray hits) { this.hits = hits; }
	
	public Object[] getHitsAsArray() {  
		return getHits().toArray();
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
