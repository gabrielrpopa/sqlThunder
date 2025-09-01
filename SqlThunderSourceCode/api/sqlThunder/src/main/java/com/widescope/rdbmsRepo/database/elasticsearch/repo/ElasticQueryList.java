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
import com.widescope.sqlThunder.rest.RestInterface;

public class ElasticQueryList implements RestInterface {
	private List<ElasticQuery> elasticQueryLst;
	
	public ElasticQueryList(List<ElasticQuery> elasticRepoDynamicSqlLst) {
		this.setElasticQueryLst(elasticRepoDynamicSqlLst);
	}
	
	public ElasticQueryList() {
		this.setElasticQueryLst(new ArrayList<ElasticQuery>());
	}

	public List<ElasticQuery> getElasticQueryLst() { return elasticQueryLst; }
	public void setElasticQueryLst(List<ElasticQuery> elasticQueryLst) {	this.elasticQueryLst = elasticQueryLst; }
	public void addElasticQuery(ElasticQuery elasticQuery) {	this.elasticQueryLst.add(elasticQuery); }
	public void addElasticQueryLst(List<ElasticQuery> elasticQueryLst) {	this.elasticQueryLst.addAll(elasticQueryLst); }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
