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


package com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticQuery;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class QueryInputWrapper implements RestInterface {

	private QueryList queryList;
	private QueryType queryType;

	public QueryInputWrapper() throws Exception {
		this.setQueryDSL(new QueryList(new ArrayList<QueryType>()));
		this.setQueryDslType(new QueryType("", 
											new ArrayList<Object>(),
											"fuzzy",
											""));
	}

	public QueryList getQueryDSL() { return queryList; }
	public void setQueryDSL(QueryList queryList) { this.queryList = queryList; }

	public QueryType getQueryDslType() { return queryType; }
	public void setQueryDslType(QueryType queryType) { this.queryType = queryType; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
