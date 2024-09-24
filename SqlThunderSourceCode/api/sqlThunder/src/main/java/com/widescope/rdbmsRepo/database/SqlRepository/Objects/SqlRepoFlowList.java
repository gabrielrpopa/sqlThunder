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
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rest.RestInterface;

public class SqlRepoFlowList implements RestInterface {

	private List<SqlRepoFlow>  listOfSqlRepoFlow;
	public List<SqlRepoFlow> getListOfSqlRepoFlow() { return listOfSqlRepoFlow;	}
	public void setListOfSqlRepoFlow(List<SqlRepoFlow> listOfSqlRepoFlow) {	this.listOfSqlRepoFlow = listOfSqlRepoFlow;	}

	public SqlRepoFlowList() {
		listOfSqlRepoFlow = new ArrayList<SqlRepoFlow>();
        listOfSqlRepoFlow.addAll(SqlRepoUtils.sqlRepoFlowMap.values());
	}
	
	public static String printNiceFormatSqlRepoFlowList(SqlRepoFlowList sqlRepoFlowList) {
		Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();
        return gson_pretty.toJson(sqlRepoFlowList);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
