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

package com.widescope.rdbmsRepo.database.elasticsearch.repo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class QueryToClusterBridgeList implements RestInterface {
	private List<QueryToClusterBridge> queryToClusterBridgeList;
	
	
	public QueryToClusterBridgeList (final List<QueryToClusterBridge> queryToClusterBridgeList) {
		this.setQueryToClusterBridgeList(queryToClusterBridgeList);

	}
	
	public QueryToClusterBridgeList () {
		this.setQueryToClusterBridgeList(new ArrayList<>());
	}
	public List<QueryToClusterBridge> getQueryToClusterBridgeList() { return queryToClusterBridgeList; }
	public void setQueryToClusterBridgeList(List<QueryToClusterBridge> queryToClusterBridgeList) { this.queryToClusterBridgeList = queryToClusterBridgeList; }
	public void addQueryToClusterBridgeList(QueryToClusterBridge queryToClusterBridge) { this.queryToClusterBridgeList.add(queryToClusterBridge); }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
