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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class CacheStringPayload {


	private String value;
	private long expiry;
	private String notifKey;
	
	public CacheStringPayload(final String value, final long expiry, final String notifKey) {
		this.value = value;
		this.expiry = expiry;
		this.notifKey = notifKey;
	}
	
	public CacheStringPayload() {
		this.value = null;
		this.expiry = 0;
		this.notifKey = null;
	}
	
	public void zeroOut() {
		this.value = null;
		this.expiry = 0;
		this.notifKey = null;
	}

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public long getExpiry() { return expiry; }
    public void setExpiry(long expiry) { this.expiry = expiry; }
    public String getNotifKey() { return notifKey; }
    public void setNotifKey(String notifKey) { this.notifKey = notifKey; }

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

	public static CacheStringPayload toCacheStringPayload(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, CacheStringPayload.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}


}
