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



public class SqlRepoDynamicSqlShort {
	
	private long dynamic_sql_id;
	public long getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final long dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }
	
	private String dynamic_sql_type;
	public String getDynamicSqlType() {	return dynamic_sql_type; }
	public void setDynamicSqlType(final String dynamic_sql_type) { this.dynamic_sql_type = dynamic_sql_type; }
	
	private String dynamic_sql_name;
	public String getDynamicSqlName() {	return dynamic_sql_name; }
	public void setDynamicSqlName(final String dynamic_sql_name) { this.dynamic_sql_name = dynamic_sql_name; }
	
	private String dynamic_sql_description;
	public String getDynamicSqlDescription() {	return dynamic_sql_description; }
	public void setDynamicSqlDescription(final String dynamic_sql_description) { this.dynamic_sql_description = dynamic_sql_description; }


	public SqlRepoDynamicSqlShort(final long dynamic_sql_id,
				                 final String dynamic_sql_type, 
					             final String dynamic_sql_name,
					             final String dynamic_sql_description) {
		this.dynamic_sql_id = dynamic_sql_id;
		this.dynamic_sql_type = dynamic_sql_type;
		this.dynamic_sql_name = dynamic_sql_name;
		this.dynamic_sql_description = dynamic_sql_description;
	}
}
