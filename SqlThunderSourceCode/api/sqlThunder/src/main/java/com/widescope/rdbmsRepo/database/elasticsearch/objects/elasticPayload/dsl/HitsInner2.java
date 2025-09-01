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



import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


public class HitsInner2 {

	private String _index;
	private String _type;
	private String _id;
	private double _score;
	private Map<String, Object> _source;

	public HitsInner2(	final String _index,
						final String _type,
						final String _id,
						final int _score,
						final Map<String, Object> _source) {
		this.set_index(_index);
		this.set_type(_type);
		this.set_id(_id);
		this.set_score(_score);
		this.set_source(_source);
	}

	public Map<String, Object> get_source() { return _source; }
	public void set_source(Map<String, Object> _source) { this._source = _source; }

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
	
}
