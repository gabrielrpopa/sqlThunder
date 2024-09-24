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

package com.widescope.rdbmsRepo.database.elasticsearch.repo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class ElasticExecutedQuery implements RestInterface{
	
	private	long id;
	private String type;
	private String source; /*A-ADHOC or R-REPO*/
	private	String user;
	private String content;
	private String jsonParam;
	private long timestamp;

	public ElasticExecutedQuery(final long id, 
								final String type, 
								final String source,
								final String user,
								final String content,
								final String jsonParam,
								final long timestamp
							)
	{
		this.setId(id);
		this.setType(type);
		this.setSource(source);
		this.setUser(user);
		this.setContent(content);
		this.setJsonParam(jsonParam);
		this.setTimestamp(timestamp);
	}


	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getUser() { return user; }
	public String getSource() { return source;}
	public void setSource(String source) { this.source = source; }
	public void setUser(String user) { this.user = user; }
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	public String getJsonParam() { return jsonParam; }
	public void setJsonParam(String jsonParam) { this.jsonParam = jsonParam; }
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	
}
