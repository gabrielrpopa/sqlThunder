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
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rest.RestInterface;



public class ParamFlowObj implements RestInterface {

	private int flowID;
	private List<ParamListObjWithSqlId> listOfParamListObjWithSqlId;
	
	
	public ParamFlowObj(int flowID)	{
		this.flowID = flowID;
		this.listOfParamListObjWithSqlId = new ArrayList<ParamListObjWithSqlId>();
	}
	
	public ParamFlowObj(final List<ParamListObjWithSqlId> listOfParamListObjWithSqlId) {
		this.listOfParamListObjWithSqlId = listOfParamListObjWithSqlId;
	}
	
	public void addFlowObj(final ParamListObjWithSqlId paramListObjWithSqlId) {
		this.listOfParamListObjWithSqlId.add(paramListObjWithSqlId);
	}
	
	
	
	public int getFlowID() { return flowID;	}
	public void setFlowID(int flowID) { this.flowID = flowID; }
	public List<ParamListObjWithSqlId> getListOfParamListObjWithSqlId() { return listOfParamListObjWithSqlId; }
	public void setListOfParamListObjWithSqlId(List<ParamListObjWithSqlId> listOfParamListObjWithSqlId) { this.listOfParamListObjWithSqlId = listOfParamListObjWithSqlId; }


	public static ParamFlowObj getParamFlowObjFromLocalRepo(int flowID) throws Exception {
		ParamFlowObj paramFlowObj = new ParamFlowObj(flowID);
		int counter = 0;
		List<SqlRepoFlowDetail> mapSqlRepoFlowDetail = SqlRepoUtils.sqlRepoFlowMap.get( Long.valueOf(flowID) ).getMapSqlRepoFlowDetail();
		for (SqlRepoFlowDetail sqlRepoFlowDetail : mapSqlRepoFlowDetail) {
			counter++;
			ParamListObj ret = SqlQueryRepoUtils.getBulkParamToPopulateJson(sqlRepoFlowDetail.getDynamic_sql_id());
			ParamListObjWithSqlId paramListObjWithSqlId = new ParamListObjWithSqlId(sqlRepoFlowDetail.getDynamic_sql_id(), sqlRepoFlowDetail.getDynamic_sql_flow_detail_id(),  counter, ret);
			paramFlowObj.addFlowObj(paramListObjWithSqlId);
		}
		
		return paramFlowObj;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
