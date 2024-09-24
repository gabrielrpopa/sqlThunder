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

public class NotificationInfo {

	private String url;
	private String ip_host;
	private int port;
	public NotificationInfo(String _url, String _ip_host, int _port) throws Exception{
		this.url = _url;
		this.ip_host = _ip_host;
		this.port = _port;
	}

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getIp_host() { return ip_host; }
    public void setIp_host(String ip_host) { this.ip_host = ip_host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

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

	public static NotificationInfo toNotificationInfo(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, NotificationInfo.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}


}
