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


package com.widescope.rdbmsRepo.database;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryAsList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryJsonRows;

public class SqlRepoExecReturn {
	
	private int errorCode;
	private String errorMessage;
	private String exceptionMessage;
	private ResultQuery results;
	private ResultQueryAsList resultsAsList; 
	
	
	
	public SqlRepoExecReturn(final int errorCode,
							final String errorMessage,
							final String exceptionMessage,
							final ResultQuery results) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
		this.setResults(results);
		this.setResultsAsList(null);
	}
	
	public SqlRepoExecReturn(final int errorCode,
								final String errorMessage,
								final String exceptionMessage,
								final ResultQueryAsList resultsAsList) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
		this.setResultsAsList(resultsAsList);
		this.setResults(null);
	}
	
	public SqlRepoExecReturn(final int errorCode,
							final String errorMessage,
							final String exceptionMessage
							) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
		this.setResultsAsList(resultsAsList);
		this.setResults(null);
		this.setResultsAsList(null);
	}
	
	public SqlRepoExecReturn() {
		this.setErrorCode(0);
		this.setErrorMessage(null);
		this.setResults(null);
	}

	public int getErrorCode() {	return errorCode; }
	public void setErrorCode(int errorCode) { this.errorCode = errorCode; }

	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

	public ResultQuery getResults() { return results; }
	public void setResults(ResultQuery results) { this.results = results; }
	
	public ResultQueryAsList getResultsAsList() { return resultsAsList; }
	public void setResultsAsList(ResultQueryAsList resultsAsList) { this.resultsAsList = resultsAsList; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static List<HashMap<String, Object>> toResultQueryBody(final String resultQuery) {
		List<HashMap<String, Object>> lst = null;
		TypeReference<HashMap<String, List<HashMap<String, Object>> >> typeRef = new TypeReference<>() {};
		
		try {
			HashMap<String, List<HashMap<String, Object>> > mapping =  new ObjectMapper().readValue(resultQuery, typeRef);
			return mapping.get("table");
		} catch (Exception e1) {
			return null;
		}
	}
	
	public static ResultQueryJsonRows 
	resultQueryToJsonRows(final ResultQuery resultQuery) throws Exception {
		ResultQueryJsonRows resultQueryJsonRows = new ResultQueryJsonRows();
		Map<String, ResultMetadata> metadataListToMap = ResultQuery.metadataListToMap(resultQuery.getMetadata());
		// Set the metadata
		resultQueryJsonRows.setMetadata(new ArrayList<ResultMetadata>(metadataListToMap.values()));

		List<HashMap<String, Object>> body = toResultQueryBody(resultQuery.getResultQueryJson().toJSONString());

        assert body != null;
        for(HashMap<String, Object> row : body) { // rows
			String line = JSONObject.toJSONString(row);
			resultQueryJsonRows.addResultQueryRow(line);
		}
		
		return resultQueryJsonRows;
	}

	public String getExceptionMessage() { return exceptionMessage; }
	public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
	
	
}
