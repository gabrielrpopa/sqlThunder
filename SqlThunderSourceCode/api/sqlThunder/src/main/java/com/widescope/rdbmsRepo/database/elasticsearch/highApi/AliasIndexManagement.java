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


import java.util.Map;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.DeleteAliasRequest;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.rest.RestStatus;

public class AliasIndexManagement {
	

	public Map<String, Set<AliasMetadata>> getAliases(RestHighLevelClient restHighLevelClient, String[] aliasesToSearch) throws Exception {
		GetAliasesRequest request = new GetAliasesRequest();
		if(aliasesToSearch.length > 0)
			request = new GetAliasesRequest(aliasesToSearch);
		
		GetAliasesResponse response = null;
		
		try {
			response = restHighLevelClient.indices().getAlias(request, RequestOptions.DEFAULT);
			if(response.status() == RestStatus.OK) {
                return response.getAliases();
			}
			else {
				throw new Exception("NOT_FOUND");
			}
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        throw new Exception("NOT_FOUND");
		    } else {
				throw new Exception("UNKNOWN");
			}
		}
	}
	
	
	public boolean aliasExists(RestHighLevelClient restHighLevelClient, String[] aliasesToSearch) throws Exception {
		GetAliasesRequest request = new GetAliasesRequest();
		if(aliasesToSearch.length > 0)
			request = new GetAliasesRequest(aliasesToSearch);

		try {
			return restHighLevelClient.indices().existsAlias(request, RequestOptions.DEFAULT);
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        throw new Exception("NOT_FOUND");
		    } else {
				throw new Exception("UNKNOWN");
			}
		}
	}
	
	
	public boolean deleteAlias(RestHighLevelClient restHighLevelClient, String indexName, String alias) throws Exception {
		DeleteAliasRequest request = new DeleteAliasRequest(indexName, alias);
		
		try {
			org.elasticsearch.client.core.AcknowledgedResponse deleteAliasResponse =
					restHighLevelClient.indices().deleteAlias(request, RequestOptions.DEFAULT);

            return deleteAliasResponse.isAcknowledged();
			
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        throw new Exception("NOT_FOUND");
		    } else {
				throw new Exception("UNKNOWN");
			}
		}
	}
	
	
}
