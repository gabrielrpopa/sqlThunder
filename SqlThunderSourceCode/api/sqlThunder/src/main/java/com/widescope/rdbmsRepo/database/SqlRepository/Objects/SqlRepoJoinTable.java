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



public class SqlRepoJoinTable {

	private Integer dynamic_sql_join_table_id;
	public Integer getDynamicSqlJoinTableId() {	return dynamic_sql_join_table_id; }
	public void setDynamicSqlJoinTableId(final Integer dynamic_sql_join_table_id) { this.dynamic_sql_join_table_id = dynamic_sql_join_table_id; }
	
	private Integer dynamic_sql_id;
	public Integer getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final Integer dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }
	
	private String dynamic_sql_join_table;
	public String getModuleDescription() {	return dynamic_sql_join_table; }
	public void setModuleDescription(final String dynamic_sql_join_table) { this.dynamic_sql_join_table = dynamic_sql_join_table; }
	
}
