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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class ElasticHostList implements RestInterface {

	private List<ElasticHost> elasticHostLst;

	public ElasticHostList()	{
		this.elasticHostLst = new ArrayList<>();
	}

	public List<ElasticHost> getElasticHostLst() { return elasticHostLst; }
	public void setElasticHostLst(List<ElasticHost> elasticHostLst) { this.elasticHostLst = elasticHostLst; }
	public void addElasticClusterDbLst(ElasticHost elasticHost) { this.elasticHostLst.add(elasticHost); }

	

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static ElasticHostList toElasticHostList(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticHostList.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
