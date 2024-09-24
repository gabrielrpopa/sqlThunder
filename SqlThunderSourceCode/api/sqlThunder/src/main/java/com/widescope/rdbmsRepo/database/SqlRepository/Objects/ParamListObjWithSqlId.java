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


import com.google.gson.Gson;

public class ParamListObjWithSqlId {
	private int dynamic_sql_id;
	private int dynamic_sql_flow_detail_id;
	private int execution_order;
	private ParamListObj paramListObj;


	public ParamListObjWithSqlId(	int dynamic_sql_id,
									int dynamic_sql_flow_detail_id, 
									int execution_order, 
									ParamListObj paramListObj) {
		this.setParamListObj(paramListObj);
		this.setDynamic_sql_id(dynamic_sql_id);
		this.setDynamic_sql_flow_detail_id(dynamic_sql_flow_detail_id);
		this.setExecution_order(execution_order);
	}

	public int getDynamic_sql_id() {	return dynamic_sql_id; }
	public void setDynamic_sql_id(int dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }

	public ParamListObj getParamListObj() { return paramListObj; }
	public void setParamListObj(ParamListObj paramListObj) { this.paramListObj = paramListObj; }

	public int getDynamic_sql_flow_detail_id() { return dynamic_sql_flow_detail_id;	}
	public void setDynamic_sql_flow_detail_id(int dynamic_sql_flow_detail_id) {	this.dynamic_sql_flow_detail_id = dynamic_sql_flow_detail_id; }

	public int getExecution_order() { return execution_order; }
	public void setExecution_order(int execution_order) { this.execution_order = execution_order; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
