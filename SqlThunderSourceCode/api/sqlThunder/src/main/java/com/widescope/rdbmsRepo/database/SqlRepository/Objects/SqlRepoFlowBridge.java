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


public class SqlRepoFlowBridge {
	private int dynamic_sql_flow_bridge_id;
	private int dynamic_sql_flow_id;
	private int database_id;
	private int schema_id;
	private boolean dynamic_sql_flow_bridge_active;
	
	public SqlRepoFlowBridge() {
		this.setDynamic_sql_flow_bridge_id(0);
		this.setDynamic_sql_flow_id(0);
		this.setDatabase_id(0);
		this.setSchema_id(0);
		this.setDynamic_sql_flow_bridge_active(false);
	}
	
	
	public SqlRepoFlowBridge(int dynamic_sql_flow_bridge_id, 
							 int dynamic_sql_flow_id, 
							 int database_id, 
							 int schema_id, 
							 boolean dynamic_sql_flow_bridge_active) {
		this.setDynamic_sql_flow_bridge_id(dynamic_sql_flow_bridge_id);
		this.setDynamic_sql_flow_id(dynamic_sql_flow_id);
		this.setDatabase_id(database_id);
		this.setSchema_id(schema_id);
		this.setDynamic_sql_flow_bridge_active(dynamic_sql_flow_bridge_active);
	}


	public int getDynamic_sql_flow_bridge_id() { return dynamic_sql_flow_bridge_id;	}
	public void setDynamic_sql_flow_bridge_id(int dynamic_sql_flow_bridge_id) {	this.dynamic_sql_flow_bridge_id = dynamic_sql_flow_bridge_id; }
	public int getDynamic_sql_flow_id() { return dynamic_sql_flow_id; }
	public void setDynamic_sql_flow_id(int dynamic_sql_flow_id) { this.dynamic_sql_flow_id = dynamic_sql_flow_id; }
	public int getDatabase_id() { return database_id; }
	public void setDatabase_id(int database_id) { this.database_id = database_id; }
	public int getSchema_id() {	return schema_id; }
	public void setSchema_id(int schema_id) { this.schema_id = schema_id; }
	public boolean isDynamic_sql_flow_bridge_active() {	return dynamic_sql_flow_bridge_active; }
	public void setDynamic_sql_flow_bridge_active(boolean dynamic_sql_flow_bridge_active) {	this.dynamic_sql_flow_bridge_active = dynamic_sql_flow_bridge_active; }
	
}
