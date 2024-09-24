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
import java.util.ArrayList;
import java.util.List;


import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;


public class GetIndexApi {

	
	public String getIndex(RestHighLevelClient restHighLevelClient, 	
							String indexName, 
							String documentId) throws Exception {
		GetRequest request = new GetRequest(indexName, "1"); 
		
		String[] includes = new String[]{"message", "*Date"};
		String[] excludes = new String[]{"message"};

		FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
		request.fetchSourceContext(fetchSourceContext);
		
		request.storedFields("message");

		GetResponse response = null;
		try {
			response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
		   	if (response.isExists()) {
                return response.getField("message").getValue();
			}
			else {
				throw new Exception("UNKNOWN");
			}
		    
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        throw new Exception("NOT_FOUND");
		    } else {
				throw new Exception("UNKNOWN");
			}
		}
		
	}
	
	
	
	
	
	public List<String> searchByColor(RestHighLevelClient restHighLevelClient, String color) throws IOException {
        return search(restHighLevelClient, "color", color);
    }

	
	
    public List<String> searchByName(RestHighLevelClient restHighLevelClient, String name) throws IOException {
        return search(restHighLevelClient, "name", name);
    }

    
    
	private List<String> search(RestHighLevelClient restHighLevelClient,
								String term, 
								String match) throws IOException 
	{
        SearchRequest searchRequest = new SearchRequest("fruits");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(term, match));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        List<String> results = new ArrayList<>(hits.getHits().length);
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            results.add(sourceAsString);
        }
        return results;
    }
	
	
}
