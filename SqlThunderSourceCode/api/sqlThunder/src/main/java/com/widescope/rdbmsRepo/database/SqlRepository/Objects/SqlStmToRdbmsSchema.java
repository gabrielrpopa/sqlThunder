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



public class SqlStmToRdbmsSchema 
{
	
	private int database_schema_bridge_id;
	public int getDatabaseSchemaBridgeId() {	return database_schema_bridge_id; }
	public void setDatabaseSchemaBridgeId(final int database_schema_bridge_id) { this.database_schema_bridge_id = database_schema_bridge_id; }
	
	private boolean database_schema_bridge_active;
	public boolean getDatabaseSchemaBridgeActive() { return this.database_schema_bridge_active ; } 
	public void getDatabaseSchemaBridgeActive(final boolean database_schema_bridge_active) {  this.database_schema_bridge_active = database_schema_bridge_active;}

	////////////////////////////// SQL /////////////////////////////////////////////////////////////////////
	private int dynamic_sql_id;
	public int getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final int dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }

	private String dynamic_sql_name;
	public String getDynamicSqlName() {	return dynamic_sql_name; }
	public void setDynamicSqlName(final String dynamic_sql_name) { this.dynamic_sql_name = dynamic_sql_name; }
	
	////////////////// DATABASE /////////////////////////////////////////////////////////////////////////////
	private int database_id;
	public int getDatabaseId() {	return database_id; }
	public void setDatabaseId(final int database_id) { this.database_id = database_id; }
	
	private String database_id_is_default;
	public String getDatabaseIdIsDefault() {	return database_id_is_default; }
	public void setDatabaseIdIdIsDefault(final String database_id_is_default) { this.database_id_is_default = database_id_is_default; }
	
	private String database_type;
	public String getDatabaseType() {	return database_type; }
	public void setDatabaseType(final String database_type) { this.database_type = database_type; }
	
	private String database_name;
	public String getDatabaseName() {	return database_name; }
	public void setDatabaseName(final String database_name) { this.database_name = database_name; }
	
	private String database_server;
	public String getDatabaseServer() {	return database_server; }
	public void setDatabaseServer(final String database_server) { this.database_server = database_server; }
	
	private String database_port;
	public String getDatabasePort() {	return database_port; }
	public void setDatabasePort(final String database_port) { this.database_port = database_port; }
	
	private String database_description;
	public String getDatabaseDescription() {	return database_description; }
	public void setDatabaseDescription(final String database_description) { this.database_description = database_description; }
	
	private boolean database_active;
	public boolean getDatabaseActive() {	return database_active; }
	public void setDatabaseActive(final boolean database_active) { this.database_active = database_active; }

	///////////////////// Schemas
	private int schema_id;
	public int getSchemaId() {	return schema_id; }
	public void setSchemaId(final int schema_id) { this.schema_id = schema_id; }
	
	private String schema_id_is_default;
	public String getSchemaIdIsDefault() {	return schema_id_is_default; }
	public void setSchemaIdIsDefault(final String schema_id_is_default) { this.schema_id_is_default = schema_id_is_default; }
	
	private String schema_name;
	public String getSchemaName() {	return schema_name; }
	public void setSchemaName(final String schema_name) { this.schema_name = schema_name; }
	
	private String schema_service;
	public String getSchemaService() {	return schema_service; }
	public void setSchemaService(final String schema_service) { this.schema_service = schema_service; }
	
	private String schema_password;
	public String getSchemaPassword() {	return schema_password; }
	public void setSchemaPassword(final String schema_password) { this.schema_password = schema_password; }
	
	private String schema_description;
	public String getSchemaDescription() {	return schema_description; }
	public void setSchemaDescription(final String schema_description) { this.schema_description = schema_description; }
	
	private boolean schema_active;
	public boolean getSchemaActive() {	return schema_active; }
	public void setSchemaActive(final boolean schema_active) { this.schema_active = schema_active; }
	
	public SqlStmToRdbmsSchema() {}
	
	public SqlStmToRdbmsSchema(int database_schema_bridge_id,
											boolean database_schema_bridge_active,
											int dynamic_sql_id,
											String dynamic_sql_name,
											int database_id,
											String database_id_is_default,
											String database_type,
											String database_name,
											String database_server,
											String database_port,
											boolean database_active,
											int schema_id,
											String schema_id_is_default,
											String schema_name,
											String schema_service,
											String schema_password,
											String schema_description,
											boolean schema_active) {
		this.database_schema_bridge_id = database_schema_bridge_id;
		this.database_schema_bridge_active = database_schema_bridge_active;
		this.dynamic_sql_id = dynamic_sql_id;
		this.dynamic_sql_name = dynamic_sql_name;
		this.database_id = database_id;
		this.database_id_is_default = database_id_is_default;
		this.database_type = database_type;
		this.database_name = database_name;
		this.database_server = database_server;
		this.database_port = database_port;
		this.database_active = database_active;
		this.schema_id = schema_id;
		this.schema_id_is_default = schema_id_is_default;
		this.schema_name = schema_name;
		this.schema_service = schema_service;
		this.schema_password = schema_password;
		this.schema_description = schema_description;
		this.schema_active = schema_active;
	}
	
		

}
