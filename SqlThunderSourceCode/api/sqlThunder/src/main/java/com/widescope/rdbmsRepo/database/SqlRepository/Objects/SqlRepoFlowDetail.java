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


/**
 * 
 * @author Gabriel Popa
 * @since   August 2022
 */

public class SqlRepoFlowDetail {
	private int dynamic_sql_flow_detail_id;
	private int dynamic_sql_flow_id;
	private int dynamic_sql_id;
	private int database_id;
	private int schema_id;
	private String execution_type;
	private int max_seconds_wait;
	private String is_result;
	private int flow_order;
	private String input_from_previous;
	
	
	public  SqlRepoFlowDetail()	{
		this.setDynamic_sql_flow_detail_id(0);
		this.setDynamic_sql_flow_id(0);
		this.setDynamic_sql_id(0);
		this.setDatabase_id(0);
		this.setSchema_id(0);
		this.setExecution_type("");
		this.setIs_result("");
		this.setFlow_order(0);
		this.setInput_from_previous("");
	}
	
	public  SqlRepoFlowDetail(int dynamic_sql_flow_detail_id,  
								int dynamic_sql_flow_id, 
								int dynamic_sql_id,
								int database_id,
								int schema_id,
								String execution_type,
								int max_seconds_wait,
								String is_result,
								int flow_order,
								String input_from_previous)	{
		this.setDynamic_sql_flow_detail_id(dynamic_sql_flow_detail_id);
		this.setDynamic_sql_flow_id(dynamic_sql_flow_id);
		this.setDynamic_sql_id(dynamic_sql_id);
		this.setDatabase_id(database_id);
		this.setSchema_id(schema_id);
		this.setExecution_type(execution_type);
		this.setMax_seconds_wait(max_seconds_wait);
		this.setIs_result(is_result);
		this.setFlow_order(flow_order);
		this.setInput_from_previous(input_from_previous);
	}

	public int getDynamic_sql_flow_detail_id() { return dynamic_sql_flow_detail_id;	}
	public void setDynamic_sql_flow_detail_id(int dynamic_sql_flow_detail_id) {	this.dynamic_sql_flow_detail_id = dynamic_sql_flow_detail_id; }

	public int getDynamic_sql_flow_id() { return dynamic_sql_flow_id; }
	public void setDynamic_sql_flow_id(int dynamic_sql_flow_id) { this.dynamic_sql_flow_id = dynamic_sql_flow_id; }

	public int getDynamic_sql_id() { return dynamic_sql_id;	}
	public void setDynamic_sql_id(int dynamic_sql_id) {	this.dynamic_sql_id = dynamic_sql_id; }

	public int getDatabase_id() { return database_id; }
	public void setDatabase_id(int database_id) { this.database_id = database_id; }

	public int getSchema_id() {	return schema_id; }
	public void setSchema_id(int schema_id) { this.schema_id = schema_id; }

	public String getExecution_type() {	return execution_type; }
	public void setExecution_type(String execution_type) { this.execution_type = execution_type; }

	public int getMax_seconds_wait() {	return max_seconds_wait; }
	public void setMax_seconds_wait(int max_seconds_wait) {	this.max_seconds_wait = max_seconds_wait; }
	
	public String getIs_result() { return is_result; }
	public void setIs_result(String is_result) { this.is_result = is_result; }

	public int getFlow_order() { return flow_order; }
	public void setFlow_order(int flow_order) {	this.flow_order = flow_order; }

	public String getInput_from_previous() { return input_from_previous; }
	public void setInput_from_previous(String input_from_previous) { this.input_from_previous = input_from_previous; }


}
