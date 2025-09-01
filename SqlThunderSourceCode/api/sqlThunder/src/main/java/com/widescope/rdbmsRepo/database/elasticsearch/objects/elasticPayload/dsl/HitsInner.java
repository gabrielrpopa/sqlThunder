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
import com.widescope.sqlThunder.tcpServer.types.JSONError;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class HitsInner {

	private String _index;
	private String _type;
	private String _id;
	private double _score;
	private JSONObject _source;

	public HitsInner(	final String _index,
						final String _type,
						final String _id,
						final int _score,
						final JSONObject _source) {
		this.set_index(_index);
		this.set_type(_type);
		this.set_id(_id);
		this.set_score(_score);
		this.set_source(_source);
	}

	public JSONObject get_source() { return _source; }
	public void set_source(JSONObject _source) { this._source = _source; }

	public String get_type() { return _type; }
	public void set_type(String _type) { this._type = _type; }

	public String get_index() { return _index; }
	public void set_index(String _index) { this._index = _index; }

	public double get_score() { return _score; }
	public void set_score(double _score) { this._score = _score; }

	public String get_id() { return _id; }
	public void set_id(String _id) { this._id = _id; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static HitsInner toHitsInner(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, HitsInner.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
	
	public static HitsInner[] toListHitsInner(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, HitsInner[] .class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
	
	
}
