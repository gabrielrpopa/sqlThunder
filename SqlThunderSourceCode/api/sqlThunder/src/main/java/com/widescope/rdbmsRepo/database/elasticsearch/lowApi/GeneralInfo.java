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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticHost;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.ClusterInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.ElasticIndexInfo;

public class GeneralInfo {

	public static ClusterInfo toClusterInfo(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ClusterInfo.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}



	public static boolean ping(String clusterUniqueName) throws IOException {
		try	{
			ElasticCluster clusterMap = SqlRepoUtils.elasticDbMap.get(clusterUniqueName);
			List<ElasticHost> lst = clusterMap.getListElasticHosts();
			HttpHost[] httpHostArray = new HttpHost[clusterMap.getListElasticHosts().size()];
			int counter = 0;
			for( ElasticHost elasticHost:  clusterMap.getListElasticHosts() ) {
				HttpHost httpHost = new HttpHost(elasticHost.getServer(), elasticHost.getPort(), elasticHost.getProtocol());
				httpHostArray[counter] = httpHost;
				counter++;
			}
			ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
			Request request = new Request("GET", "/");
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			final int code = response.getStatusLine().getStatusCode();
			return code == HttpStatus.OK.value();
		} catch(Exception ex) {
			return false;
		}
	}
}
