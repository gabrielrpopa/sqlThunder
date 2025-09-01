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


package com.widescope.rdbmsRepo.database.SqlRepository.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.ElasticDataTypes;





public class ResultQueryJsonRows implements RestInterface {
	
	/**
	 * used for storing metadata info, such as column names an types
	 */
	private List<ResultMetadata> metadata;
	public List<ResultMetadata> getMetadata() {	return metadata; }
	public void setMetadata(final List<ResultMetadata> metadata) { this.metadata = metadata; }
	
	/**
	 * used for storing List<String> rows
	 */
	private List<String> resultQueryRows;
	public List<String> getResultQueryRows() {	return resultQueryRows; }
	public void setResultQueryRows(final List<String> resultQueryRows) { this.resultQueryRows = resultQueryRows; }
	public void addResultQueryRow(final String resultQueryRow) { this.resultQueryRows.add(resultQueryRow) ; }

	public ResultQueryJsonRows() {
		this.metadata = new ArrayList<>();
		this.resultQueryRows = new ArrayList<>();
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static Map<String, ResultMetadata> metadataListToMap(final List<ResultMetadata> metadata) {
		return metadata.stream().collect(Collectors.toMap(ResultMetadata::getColumnName, Function.identity()));
	}
	
	
	public static Map<String, String> metadataToFieldMapping(final List<ResultMetadata> metadata) {
		Map<String, String> ret = new HashMap<String, String>();
		ElasticDataTypes elasticDataTypes = new ElasticDataTypes();
		for(ResultMetadata resultMetadata : metadata) {
			String elasticType = elasticDataTypes.javaToElasticTypes.get(resultMetadata.getColumnTypeName().toLowerCase());
			ret.put(resultMetadata.getColumnName(), elasticType);
		}
		return ret;
	}


}
