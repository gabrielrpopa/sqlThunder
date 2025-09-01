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


package com.widescope.rdbmsRepo.database.elasticsearch.lowApi;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;


public class ElasticLowLevelWrapper {

	private RestClient restClient = null;

	public RestClient getRestClient() {	return restClient; }
	public void setRestClient(RestClient restClient) { this.restClient = restClient; }

	public ElasticLowLevelWrapper(HttpHost[] listOfHosts) {
		restClient = RestClient.builder(listOfHosts).build();
	}
	
	public ElasticLowLevelWrapper(HttpHost[] listOfHosts, Header[] headerValues) {
		RestClientBuilder builder = RestClient.builder(listOfHosts);
		builder.setDefaultHeaders(headerValues);
	}
	public void disconnect() throws IOException {
		restClient.close();
		restClient = null;
	}
}
