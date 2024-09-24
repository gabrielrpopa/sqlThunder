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

package com.widescope.rdbmsRepo.database.mongodb.repo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class MongoExecutedQuery implements RestInterface{
	
	private	long id;
	private String mqlClass;
	private String source; /*A-ADHOC or R-REPO*/
	private	String usr;
	private String mqlContent;
	private String jsonParam;
	private long timestamp;
	
	
	public MongoExecutedQuery(	final long id, 
								final String mqlClass, 
								final String source,
								final String usr,
								final String mqlContent,
								final String jsonParam,
								final long timestamp ) {
		this.setId(id);
		this.setMqlClass(mqlClass);
		this.setSource(source);
		this.setUsr(usr);
		this.setMqlContent(mqlContent);
		this.setJsonParam(jsonParam);
		this.setTimestamp(timestamp);
	}


	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	public String getMqlClass() { return mqlClass; }
	public void setMqlClass(String mqlClass) { this.mqlClass = mqlClass; }
	public String getUsr() { return usr; }
	public void setUsr(String user) { this.usr = user; }
	public String getMqlContent() { return mqlContent; }
	public void setMqlContent(String mqlContent) { this.mqlContent = mqlContent; }
	public String getJsonParam() { return jsonParam; }
	public void setJsonParam(String jsonParam) { this.jsonParam = jsonParam; }
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public String getSource() {	return source;	}
	public void setSource(String source) {	this.source = source; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
