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
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;

public class ElasticPayload2 {

	private ShardsDescription _shards;
	private HitsOuter2 hits;
	private double took;	
	private boolean timed_out;

	public ElasticPayload2(final ShardsDescription _shards,
						   final HitsOuter2 hits,
						   final Double took,
						   final JSONObject timed_out) {
		this.set_shards(_shards);
		this.setHits(hits);
		this.setTook(took);
		this.setTimed_out(false);
	}

	public ShardsDescription get_shards() { return _shards; }
	public void set_shards(ShardsDescription _shards) { this._shards = _shards; }

	public HitsOuter2 getHits() {return hits; }
	public void setHits(HitsOuter2 hits) { this.hits = hits; }

	public Double getTook() { return took; }
	public void setTook(Double took) { this.took = took; }

	public boolean getTimed_out() { return timed_out; }
	public void setTimed_out(boolean timed_out) { this.timed_out = timed_out; }

	public static ElasticPayload2 toElasticPayload2(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticPayload2.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
	
}

