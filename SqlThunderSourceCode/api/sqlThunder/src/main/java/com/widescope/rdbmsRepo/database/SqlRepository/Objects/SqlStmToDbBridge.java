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


public class SqlStmToDbBridge {
	private long id;
	public long getId() {	return id; }
	public void setId(final long id) { this.id = id; }
	
	private long sql_id;
	public long getSqlId() {	return sql_id; }
	public void setSqlId(final long sql_id) { this.sql_id = sql_id; }
	
	private long database_id;
	public long getDatabaseId() {	return database_id; }
	public void setDatabaseId(final long database_id) { this.database_id = database_id; }
	
	private String database_name;
	public String getDatabaseName() {	return database_name; }
	public void setDatabaseName(final String database_name) { this.database_name = database_name; }
	
	private int active;
	public int getActive() {	return active; }
	public void setActive(final int active) { this.active = active; }


	public SqlStmToDbBridge(final long id,
			        final long sql_id,
					final long database_id,
					final String database_name,
					final int active) throws Exception {
		this.id = id;
		this.sql_id = sql_id;
		this.database_id = database_id;
		this.database_name = database_name;
		this.active = active;
	}
	
}
