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

package com.widescope.sqlThunder.utils;

import com.google.gson.Gson;

public class AngularPortalConfig {
	private String baseUrl;
	private String webSockets;
	
	public AngularPortalConfig(final String baseUrl,
								final String webSockets) {
		this.setBaseUrl(baseUrl);
		this.setWebSockets(webSockets);
	}
	
	public AngularPortalConfig() {
		this.setBaseUrl(null);
		this.setWebSockets(null);
	}

	public String getBaseUrl() { return baseUrl; }
	public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

	public String getWebSockets() { return webSockets; }
	public void setWebSockets(String webSockets) { this.webSockets = webSockets; }
	
	public void setConfig(String serverName, 
							boolean isHttpSecured,
							int httpPort,
							int wsPort) { 
		baseUrl = !isHttpSecured ? "http": "https";
		baseUrl+= "://" + serverName + ":" + String.valueOf(httpPort) + "/sqlThunder";
		webSockets = "ws://" + serverName + ":" + String.valueOf(wsPort) + "/chat";
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
