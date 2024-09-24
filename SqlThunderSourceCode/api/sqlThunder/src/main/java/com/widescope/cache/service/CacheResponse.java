/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.cache.service;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;


/**
 * Represents the REST API Cache Response
 */

public class CacheResponse implements RestInterface, Serializable  {
	private static final long serialVersionUID = 5295726579143406770L;

	private String key;
	private String value;
	private String user;
	private String message;
	
	public CacheResponse(	final String key, 
							final String value,
							final String user,
							final String message
						) {
		this.setKey(key);
		this.setValue(value);
		this.setUser(user);
		this.setMessage(message);
		
	}

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }

	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }

	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String toStringPretty() {
		try	{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}

	public static CacheResponse toCacheResponse(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, CacheResponse.class);
		}
		catch(JsonSyntaxException ex) {
            return null;
		}
	}



}
