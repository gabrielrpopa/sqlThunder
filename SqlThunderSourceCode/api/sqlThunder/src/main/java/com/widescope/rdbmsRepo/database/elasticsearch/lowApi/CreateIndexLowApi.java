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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StringUtils;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryJsonRows;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.ElasticDataTypes;

public class CreateIndexLowApi {

	public static boolean createIndex(	ElasticLowLevelWrapper elasticLowLevelWrapper,
										String indexName,
										final int numberOfShards,
										final int numberOfReplicas) throws Exception {

		if(indexExists(elasticLowLevelWrapper, indexName))	{
			throw new Exception("Index " +  indexName + " exists already");
		}

		String payload = getString(numberOfShards, numberOfReplicas);
		AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.obj, " payload: " + payload);


		Request request = new Request("PUT", "/" + indexName );
		request.addParameter("pretty", "true");
		request.setJsonEntity(payload);
		try {
			final int retCode = elasticLowLevelWrapper.getRestClient().performRequest(request).getStatusLine().getStatusCode();
            return retCode == HttpStatus.OK.value();
		} catch(IOException ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	}

	private static String getString(int numberOfShards, int numberOfReplicas) {
		Gson gson = new Gson();

		// Settings
		Map<String, Integer > settings = new HashMap<>();
		settings.put("number_of_shards", numberOfShards);
		settings.put("number_of_replicas", numberOfReplicas);

		Map<String, Object> payloadJson = new HashMap<>();
		payloadJson.put("settings", settings);
        return gson.toJson(payloadJson);
	}


	public static boolean indexExists(	ElasticLowLevelWrapper elasticLowLevelWrapper, 
										String indexName) throws Exception {

		Request request = new Request("HEAD","/" + indexName ); 
		Response response;
		try {
			response = elasticLowLevelWrapper.getRestClient().performRequest(request); 
			final int retCode = response.getStatusLine().getStatusCode();
            return retCode == HttpStatus.OK.value();
		} catch(IOException ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	public static boolean indexDelete(	ElasticLowLevelWrapper elasticLowLevelWrapper, 
										String indexName) throws Exception {

		if(!indexExists(elasticLowLevelWrapper, indexName))	{
			throw new Exception("Index " +  indexName + " do not exists");
		}
		
		
		Request request = new Request("DELETE",  "/" + indexName ); 
		Response response;
		try {
			response = elasticLowLevelWrapper.getRestClient().performRequest(request); 
			final int retCode = response.getStatusLine().getStatusCode();
            return retCode == HttpStatus.OK.value();
		} catch(IOException ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}



	public void performBulkInsert(RestHighLevelClient client,
								  String indexName,
								  List<String> jsonStrings) throws IOException {
		BulkRequest bulkRequest = new BulkRequest();

		for (int i = 0; i < jsonStrings.size(); i++) {
			bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(i + 1)).source(jsonStrings.get(i), XContentType.JSON));
		}

		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

		if (bulkResponse.hasFailures()) {
			System.err.println("Bulk operation failed: " + bulkResponse.buildFailureMessage());
		} else {
			System.out.println("Bulk operation successful");
		}
	}


	public static int
	addBulkToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
					   final String indexName,
					   final List<String> bulkDoc) throws Exception {

		try {
			Gson gson = new Gson();
			String jsonArray = gson.toJson(bulkDoc);
			Request request = new Request("POST","/" + indexName + "/_bulk");
			request.setJsonEntity(jsonArray);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
			return response.getStatusLine().getStatusCode();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}

	}


	private static int
	addBulkToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
					final String indexName,
					final String bulkDoc) throws Exception {

		try {
			Request request = new Request("POST","/" + indexName + "/_bulk");
			request.setJsonEntity(bulkDoc);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
            return response.getStatusLine().getStatusCode();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
	}
	
	
	public static int addBulkDocumentsToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper, 
												final String indexName,
												final List<String> documents,
												final int bulk ) throws Exception {

		int countSuccess = 0;
		try {
			String bulkString = "{ \n";
			int counter = 0;
			for (int i = 0; i < documents.size(); i++)  {
				bulkString += documents.get(i) + "\n";
				counter++;
				if(counter == bulk || i == documents.size() - 1) {
					bulkString += "} \n";
					addBulkToIndex(elasticLowLevelWrapper, indexName, bulkString);
					countSuccess += counter;
					counter = 0;
				} 
			}

		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		return countSuccess;
	}
	

	public static int addDocumentToIndex_(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
											final String indexName,
											final List<String> documents,
											final long documentIdStart) throws Exception {

		int countSuccess = 0;
		long documentId = documentIdStart;
		
		try {
			for (String entry : documents) {
				documentId++;
				Request request = null;
				if(documentId == documents.size())
					request = new Request("PUT","/" + indexName + "/_doc/" + documentId + "?refresh=true");
				else
					request = new Request("PUT","/" + indexName + "/_doc/" + documentId);
				
				request.setJsonEntity(entry);
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				//Header[] headers = response.getHeaders(); 
				final int retCode = response.getStatusLine().getStatusCode();
				if(retCode == HttpStatus.OK.value() || retCode == HttpStatus.CREATED.value() || retCode == HttpStatus.ACCEPTED.value())
					countSuccess++;
			}
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
		
		return countSuccess;
	}


	/**
	 * ATTENTION: for columns named id, there is a bug on converting Document to Java String
	 * @param elasticLowLevelWrapper
	 * @param indexName
	 * @param documents
	 * @param documentIdStart
	 * @return
	 * @throws Exception
	 */
	public static int addDocumentToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
											 final String indexName,
											 final List<Document> documents,
											 final long documentIdStart) throws Exception {

		int countSuccess = 0;
		long documentId = documentIdStart;

		try {
			for (Document entry : documents) {
				documentId++;
				Request request = null;
				if(documentId == documents.size())
					request = new Request("PUT","/" + indexName + "/_doc/" + documentId + "?refresh=true");
				else
					request = new Request("PUT","/" + indexName + "/_doc/" + documentId);

				request.setJsonEntity(entry.toString());
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				//Header[] headers = response.getHeaders();
				final int retCode = response.getStatusLine().getStatusCode();
				if(retCode == HttpStatus.OK.value() || retCode == HttpStatus.CREATED.value() || retCode == HttpStatus.ACCEPTED.value())
					countSuccess++;
			}
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}


		return countSuccess;
	}



	/**
	 * ATTENTION: for columns named id, there is a bug on converting Document to Java String
	 * @param elasticLowLevelWrapper
	 * @param indexName
	 * @param documents
	 * @return
	 * @throws Exception
	 */
	public static int addDocumentToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper,
											 final String indexName,
											 final List<Document> documents) throws Exception {

		int countSuccess = 0;

		try {
			for (Document entry : documents) {
				Request request  = new Request("POST","/" + indexName + "/_doc" );
				request.setJsonEntity(entry.toJson());
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				final int retCode = response.getStatusLine().getStatusCode();
				if(retCode == HttpStatus.OK.value() || retCode == HttpStatus.CREATED.value() || retCode == HttpStatus.ACCEPTED.value())
					countSuccess++;
			}
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}


		return countSuccess;
	}
	
		
	public static int addBulkDocumentsToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper, 
												final String indexName,
												final Map<String, String> documents	) throws Exception {

		int countSuccess = 0;
		
		try {
			for (Map.Entry<String, String> entry : documents.entrySet()) {
				Request request = new Request("POST","/" + indexName + "/_bulk/" + entry.getKey()); 
				request.setJsonEntity(entry.getValue());
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				final int retCode = response.getStatusLine().getStatusCode();
				if(retCode == HttpStatus.OK.value())
					countSuccess++;
			}
				
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
		return countSuccess;
	}
	
	
	public static int addDocumentToIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper, 
											final String indexName,
											final Map<String, String> documents,
											Class<?> cls) throws Exception {

		int countSuccess = 0;
		
		try {
			for (Map.Entry<String, String> entry : documents.entrySet()) {
				Request request = new Request("PUT","/" + indexName + "/_doc/" + entry.getKey()); 
				request.setJsonEntity(entry.getValue());
				Response response = elasticLowLevelWrapper.getRestClient().performRequest(request);
				final int retCode = response.getStatusLine().getStatusCode();
				if(retCode == HttpStatus.OK.value())
					countSuccess++;
			}
		
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
		return countSuccess;
	}
	
	
	public static boolean createIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper, 
										final String documentId,
										final String indexName,
										final String mappingJsonString) throws Exception {

		try {
	        Request request = new Request("PUT","/" + indexName + "/_doc/" + documentId); 
	        request.setJsonEntity(mappingJsonString); 
	        Response response = elasticLowLevelWrapper.getRestClient().performRequest(request); 
	        final int retCode = response.getStatusLine().getStatusCode();
            return retCode == HttpStatus.OK.value();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
    }
	
	
	/**
	 * Example:
	 * PUT /my_index_with_body
			{
			  "settings": {
			    "number_of_shards": x,
			    "number_of_replicas": y
			  },
			  mappings: {
			    "properties": {
			      "field1": { "type": "object" }
			    }
			  }
			}
	 */
	public static boolean createIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper, 
										final String indexName,
										final Map<String, String> fields) throws Exception {


		Gson gson = new Gson();
		try {		
				
			Map<String, Map<String, String> > fielsdMap = new HashMap<>();
			for (String name : fields.keySet()) {
				Map<String, String> field = new HashMap<String, String>();
				field.put("type", fields.get(name));
				fielsdMap.put(name, field);
			}
			
			Map<String, Object> properties = new HashMap<>();
			properties.put("properties", fielsdMap);
	
			
			Map<String, Object> payloadJson = new HashMap<String, Object>();
			payloadJson.put("mappings", properties);
			
			String payload = gson.toJson(payloadJson);

			Request request = new Request("PUT", indexName + "/_mapping/_doc");
			request.addParameter("include_type_name", "true");
			request.addParameter("pretty", "true");
			request.setJsonEntity(payload);

			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request); 
			final int retCode = response.getStatusLine().getStatusCode();
            return retCode == HttpStatus.OK.value();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static boolean updateRawMappingIndex(final ElasticLowLevelWrapper elasticLowLevelWrapper, 
												final String indexName,
												final JSONObject properties) throws Exception {

		try {
			Request request = new Request("PUT", "/" + indexName + "/_mapping");
			request.addParameter("pretty", "true");
			request.setJsonEntity(properties.toJSONString());
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request); 
			final int retCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if(retCode == HttpStatus.OK.value())
				return true;
			else
				throw new Exception(responseBody);


		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	
	public static boolean createIndex(	final ElasticLowLevelWrapper elasticLowLevelWrapper, 
										final String indexName,
										final int numberOfShards,
										final int numberOfReplicas,
										final ResultQueryJsonRows resultQuery) throws Exception {

		Gson gson = new Gson();
		
		// Settings
		Map<String, String > settingsJson = new HashMap<>();
		
		// Mappings
		Map<String, Map<String, String> > f = new HashMap<String, Map<String, String> >();
				
				
		try {		
			settingsJson.put("number_of_shards", Integer.valueOf(numberOfShards).toString() );
			settingsJson.put("number_of_replicas", Integer.valueOf(numberOfReplicas).toString());
			Map<String, Map<String, String > > setting = new HashMap<>();
			ElasticDataTypes elasticDataTypes = new ElasticDataTypes();
			for (ResultMetadata resultMetadata : resultQuery.getMetadata() ) {
				Map<String, String > v = new HashMap<String, String>();
				String elasticType = elasticDataTypes.javaToElasticTypes.get( resultMetadata.getColumnTypeName() );
				if(elasticType == null) elasticType = "keyword";
				v.put("type", elasticType);
			}
			
			Map<String, Map<String, Map<String, String> > > properties = new HashMap<>();
			properties.put("properties", f);
			
			Map<String, Object> payloadJson = new HashMap<>();
			payloadJson.put("settings", setting);
			payloadJson.put("mappings", properties);
			String payload = gson.toJson(payloadJson);
			Request request = new Request("PUT","/" + indexName + "/_doc/" + "\n" + payload);
			Response response = elasticLowLevelWrapper.getRestClient().performRequest(request); 
			final int retCode = response.getStatusLine().getStatusCode();
            return retCode == HttpStatus.OK.value();
			
			
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
}
