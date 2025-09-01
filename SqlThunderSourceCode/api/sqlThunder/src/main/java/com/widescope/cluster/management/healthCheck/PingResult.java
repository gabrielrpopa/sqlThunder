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

package com.widescope.cluster.management.healthCheck;

import com.google.gson.Gson;

public class PingResult {

	private String baseUrl;
	private String isReachable;

	
	PingResult(final String baseUrl,
			   final String isReachable) {
		this.setBaseUrl(baseUrl);
		this.setIsReachable(isReachable);
	}

	public String getIsReachable() { return isReachable; }
	public void setIsReachable(String isReachable) { this.isReachable = isReachable; }

	public String getBaseUrl() {	return baseUrl; }
	public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
