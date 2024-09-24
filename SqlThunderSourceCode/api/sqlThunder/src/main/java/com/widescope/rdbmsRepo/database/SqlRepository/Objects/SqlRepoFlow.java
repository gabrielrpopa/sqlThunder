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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.widescope.rest.RestInterface;



public class SqlRepoFlow implements RestInterface
{
	
	
	private int dynamic_sql_flow_id;
	private String dynamic_sql_flow_name;
	private String dynamic_sql_description;
	private boolean dynamic_sql_flow_active;
	
	private List<SqlRepoFlowDetail> mapSqlRepoFlowDetail; // key = flow_order, value SqlRepoFlowDetail
	
	private Map<Integer, SqlRepoFlowBridge> mapSqlRepoFlowBridge; // key = dynamic_sql_flow_bridge_id, value SqlRepoFlowBridge

	private List<ParamListObj> listParamListObj;
	
	
	public SqlRepoFlow() {
		this.setDynamic_sql_flow_id(0);
		this.setDynamic_sql_flow_name("");
		this.setDynamic_sql_description("");
		this.setDynamic_sql_flow_active(false);

		setMapSqlRepoFlowDetail(new ArrayList<SqlRepoFlowDetail>());
		setMapSqlRepoFlowBridge(new HashMap<Integer, SqlRepoFlowBridge>() );
		setListParamListObj(new ArrayList<ParamListObj>());
	}
	public SqlRepoFlow(	int dynamic_sql_flow_id,	
						String dynamic_sql_flow_name, 
						String dynamic_sql_description,
						boolean dynamic_sql_flow_active) { 
		this.setDynamic_sql_flow_id(dynamic_sql_flow_id);
		this.setDynamic_sql_flow_name(dynamic_sql_flow_name);
		this.setDynamic_sql_description(dynamic_sql_description);
		this.setDynamic_sql_flow_active(dynamic_sql_flow_active);

		setMapSqlRepoFlowDetail(new ArrayList<SqlRepoFlowDetail>());
		setMapSqlRepoFlowBridge(new HashMap<Integer, SqlRepoFlowBridge>() );
		setListParamListObj(new ArrayList<ParamListObj>());
	}
	public int getDynamic_sql_flow_id() { return dynamic_sql_flow_id; }
	public void setDynamic_sql_flow_id(int dynamic_sql_flow_id) { this.dynamic_sql_flow_id = dynamic_sql_flow_id; }
	public String getDynamic_sql_flow_name() { return dynamic_sql_flow_name; }
	public void setDynamic_sql_flow_name(String dynamic_sql_flow_name) { this.dynamic_sql_flow_name = dynamic_sql_flow_name; }
	public String getDynamic_sql_description() { return dynamic_sql_description; }
	public void setDynamic_sql_description(String dynamic_sql_description) { this.dynamic_sql_description = dynamic_sql_description; }
	public boolean isDynamic_sql_flow_active() { return dynamic_sql_flow_active; }
	public void setDynamic_sql_flow_active(boolean dynamic_sql_flow_active) { this.dynamic_sql_flow_active = dynamic_sql_flow_active; }
	public List<SqlRepoFlowDetail> getMapSqlRepoFlowDetail() {	return mapSqlRepoFlowDetail; }
	public void setMapSqlRepoFlowDetail(List<SqlRepoFlowDetail> mapSqlRepoFlowDetail) {	this.mapSqlRepoFlowDetail = mapSqlRepoFlowDetail; }
	public Map<Integer, SqlRepoFlowBridge> getMapSqlRepoFlowBridge() { return mapSqlRepoFlowBridge;	}
	public void setMapSqlRepoFlowBridge(Map<Integer, SqlRepoFlowBridge> mapSqlRepoFlowBridge) { this.mapSqlRepoFlowBridge = mapSqlRepoFlowBridge; }
	public List<ParamListObj> getListParamListObj() { return listParamListObj;	}
	public void setListParamListObj(List<ParamListObj> listParamListObj) { this.listParamListObj = listParamListObj; }


}
