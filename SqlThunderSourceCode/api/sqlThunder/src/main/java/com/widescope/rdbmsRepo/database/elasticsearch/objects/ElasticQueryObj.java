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
import com.google.gson.JsonSyntaxException;
import org.json.simple.JSONObject;

public class ElasticQueryObj {

	private String type;
	private String name;
	private JSONObject obj;

	public ElasticQueryObj(final String type, 
							final String name,
							final JSONObject obj) {
		this.setType(type);
		this.setName(name);
		this.setObj(obj);
	}



	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JSONObject getObj() {
		return obj;
	}
	public void setObj(JSONObject obj) {
		this.obj = obj;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public static ElasticQueryObj toElasticQueryObj(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticQueryObj.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
