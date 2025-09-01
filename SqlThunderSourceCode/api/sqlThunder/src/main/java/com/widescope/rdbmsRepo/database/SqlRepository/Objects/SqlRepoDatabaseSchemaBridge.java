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


public class SqlRepoDatabaseSchemaBridge {
	private long database_schema_bridge_id;
	public long getDatabaseSchemaBridgeId() {	return database_schema_bridge_id; }
	public void setDatabaseSchemaBridgeId(final long database_schema_bridge_id) { this.database_schema_bridge_id = database_schema_bridge_id; }
	
	private long dynamic_sql_id;
	public long getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final long dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }


	private long database_id;
	public long getDatabaseId() {	return database_id; }
	public void setDatabaseId(final long database_id) { this.database_id = database_id; }
	
	
	private String schemaUniqueName;
	public String getSchemaUniqueName() {	return schemaUniqueName; }
	public void setSchemaUniqueName(final String schemaUniqueName) { this.schemaUniqueName = schemaUniqueName; }
		
	private int database_schema_bridge_active;
	public int getDatabaseSchemaBridgeActive() { return this.database_schema_bridge_active ; } 
	public void getDatabaseSchemaBridgeActive(final int database_schema_bridge_active) {  this.database_schema_bridge_active = database_schema_bridge_active;}


	public SqlRepoDatabaseSchemaBridge(	final long database_schema_bridge_id,
										final long dynamic_sql_id, 
										final long database_id,
										final String schemaUniqueName,
										final int database_schema_bridge_active) throws Exception	{
		this.database_schema_bridge_id = database_schema_bridge_id;
		this.dynamic_sql_id = dynamic_sql_id;
		this.database_id = database_id;
		this.schemaUniqueName = schemaUniqueName;
		this.database_schema_bridge_active = database_schema_bridge_active;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
