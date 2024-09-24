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


package com.widescope.rdbmsRepo.database.elasticsearch.highApi;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


public class ElasticHighLevelWrapper {
	private RestHighLevelClient restHighLevelClient = null;
	public RestHighLevelClient getRestClient() {	return restHighLevelClient; }
	public void setRestClient(RestHighLevelClient restHighLevelClient) { this.restHighLevelClient = restHighLevelClient; }
	

	public ElasticHighLevelWrapper(HttpHost[] listOfHosts) {
		restHighLevelClient = new RestHighLevelClient(RestClient.builder(listOfHosts));
	}
	
	public void disconnect() throws IOException {
		restHighLevelClient.close();
		restHighLevelClient = null;
	}
}
