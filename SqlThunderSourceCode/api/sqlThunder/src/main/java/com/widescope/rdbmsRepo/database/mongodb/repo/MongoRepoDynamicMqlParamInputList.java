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

package com.widescope.rdbmsRepo.database.mongodb.repo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class MongoRepoDynamicMqlParamInputList implements RestInterface {
	
	
	private List<MongoRepoMqlParamInput> mongoRepoMqlParamInputLst;

	
	public MongoRepoDynamicMqlParamInputList(List<MongoRepoMqlParamInput> mongoRepoMqlParamInputLst) {
		this.setMongoRepoMqlParamInputLst(mongoRepoMqlParamInputLst);
	}

	public List<MongoRepoMqlParamInput> getMongoRepoMqlParamLst() { return mongoRepoMqlParamInputLst; }
	public void setMongoRepoMqlParamInputLst(List<MongoRepoMqlParamInput> mongoRepoMqlParamInputLst) {	this.mongoRepoMqlParamInputLst = mongoRepoMqlParamInputLst; }
	public void addMongoRepoMqlParamInput(MongoRepoMqlParamInput mongoRepoMqlParamInput) {	this.mongoRepoMqlParamInputLst.add(mongoRepoMqlParamInput); }
	public void addMongoRepoMqlParamInputLst(List<MongoRepoMqlParamInput> mongoRepoMqlParamLst) {	this.mongoRepoMqlParamInputLst.addAll(mongoRepoMqlParamInputLst); }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static MongoRepoDynamicMqlParamInputList generateParamInputList(int noParams) {
		try	{
			List<MongoRepoMqlParamInput> mongoRepoMqlParamInputLst = new ArrayList<>();
			for(int cnt = 0; cnt < noParams; cnt++) {
				mongoRepoMqlParamInputLst.add(new MongoRepoMqlParamInput(true))	;
			}
            return new MongoRepoDynamicMqlParamInputList(mongoRepoMqlParamInputLst);
		}
		catch(Exception ex) {
			return null;
		}
	}
}
