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


package com.widescope.rdbmsRepo.database.rdbmsRepository;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQuery;
import com.widescope.sqlThunder.rest.RestInterface;

public class RdbmsExecutedQueryList implements RestInterface {
	private List<RdbmsExecutedQuery> rdbmsExecutedQueryLst;

	public RdbmsExecutedQueryList() {
		this.setRdbmsExecutedQueryLst(new ArrayList<RdbmsExecutedQuery>());
	}

	public List<RdbmsExecutedQuery> getRdbmsExecutedQueryLst() { return rdbmsExecutedQueryLst; }
	public void setRdbmsExecutedQueryLst(List<RdbmsExecutedQuery> rdbmsExecutedQueryLst) {	this.rdbmsExecutedQueryLst = rdbmsExecutedQueryLst; }
	public void addRdbmsExecutedQuery(RdbmsExecutedQuery rdbmsExecutedQuery) {	this.rdbmsExecutedQueryLst.add(rdbmsExecutedQuery); }
	public void addRdbmsExecutedQueryLst(List<RdbmsExecutedQuery> rdbmsExecutedQueryLst) {	this.rdbmsExecutedQueryLst.addAll(rdbmsExecutedQueryLst); }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
