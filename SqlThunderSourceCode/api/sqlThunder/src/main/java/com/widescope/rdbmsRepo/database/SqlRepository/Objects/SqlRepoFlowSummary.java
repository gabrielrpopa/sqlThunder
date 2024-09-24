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






public class SqlRepoFlowSummary {
	private int dynamic_sql_flow_id;
	private String dynamic_sql_flow_name;
	private String dynamic_sql_description;
	private boolean dynamic_sql_flow_active;
	
	public SqlRepoFlowSummary(SqlRepoFlow sqlRepoFlow) {
		this.dynamic_sql_flow_id = sqlRepoFlow.getDynamic_sql_flow_id();
		this.dynamic_sql_flow_name= sqlRepoFlow.getDynamic_sql_flow_name();
		this.dynamic_sql_description= sqlRepoFlow.getDynamic_sql_description();
		this.dynamic_sql_flow_active= sqlRepoFlow.isDynamic_sql_flow_active();
	}


	public int getDynamic_sql_flow_id() { return dynamic_sql_flow_id; }
	public void setDynamic_sql_flow_id(int dynamic_sql_flow_id) { this.dynamic_sql_flow_id = dynamic_sql_flow_id; }

	public String getDynamic_sql_flow_name() {	return dynamic_sql_flow_name;	}
	public void setDynamic_sql_flow_name(String dynamic_sql_flow_name) { this.dynamic_sql_flow_name = dynamic_sql_flow_name; }

	public String getDynamic_sql_description() { return dynamic_sql_description; }
	public void setDynamic_sql_description(String dynamic_sql_description) { this.dynamic_sql_description = dynamic_sql_description; }

	public boolean isDynamic_sql_flow_active() { return dynamic_sql_flow_active; }
	public void setDynamic_sql_flow_active(boolean dynamic_sql_flow_active) { this.dynamic_sql_flow_active = dynamic_sql_flow_active; }
}
