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


package com.widescope.rdbmsRepo.database.mongodb;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoDynamicMql;

public class MongoRepoDynamicMqlList implements RestInterface {
	private List<MongoRepoDynamicMql> mongoRepoDynamicMqlLst;
	
	public MongoRepoDynamicMqlList(List<MongoRepoDynamicMql> mongoRepoDynamicMqlLst) {
		this.setMongoRepoDynamicMqlLst(mongoRepoDynamicMqlLst);
	}
	
	public MongoRepoDynamicMqlList() {
		this.setMongoRepoDynamicMqlLst(new ArrayList<MongoRepoDynamicMql>());
	}

	public List<MongoRepoDynamicMql> getMongoRepoDynamicMqlLst() { return mongoRepoDynamicMqlLst; }
	public void setMongoRepoDynamicMqlLst(List<MongoRepoDynamicMql> mongoRepoDynamicMqlLst) {	this.mongoRepoDynamicMqlLst = mongoRepoDynamicMqlLst; }
	public void addMongoRepoDynamicMql(MongoRepoDynamicMql mongoRepoDynamicMql) {	this.mongoRepoDynamicMqlLst.add(mongoRepoDynamicMql); }
	public void addMongoRepoDynamicMqlLst(List<MongoRepoDynamicMql> mongoRepoDynamicMqlLst) {	this.mongoRepoDynamicMqlLst.addAll(mongoRepoDynamicMqlLst); }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
