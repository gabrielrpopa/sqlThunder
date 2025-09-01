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




public class SqlRepoDatabase {
	
	private int database_id;
	public int getDatabaseId() {	return database_id; }
	public void setDatabaseId(final int database_id) { this.database_id = database_id; }
	
	
	private String database_type;
	public String getDatabaseType() {	return database_type; }
	public void setDatabaseType(final String database_type) { this.database_type = database_type; }
	
	private String database_name;
	public String getDatabaseName() {	return database_name; }
	public void setDatabaseName(final String database_name) { this.database_name = database_name; }
	
	private String database_warehouse_name;
	public String getDatabaseWarehouseName() {	return database_warehouse_name; }
	public void setDatabaseWarehouseName(final String database_warehouse_name) { this.database_warehouse_name = database_warehouse_name; }
	
	private String database_account;
	public String getDatabaseAccount() { return database_account; }
	public void setDatabaseAccount(String database_account) { this.database_account = database_account; }
	
	private String database_other;
	public String getDatabaseOther() { return database_other; }
	public void setDatabaseOther(String database_other) { this.database_other = database_other; }
	
	
	private String database_server;
	public String getDatabaseServer() {	return database_server; }
	public void setDatabaseServer(final String database_server) { this.database_server = database_server; }
	
	private String database_port;
	public String getDatabasePort() {	return database_port; }
	public void setDatabasePort(final String database_port) { this.database_port = database_port; }
	
	private String database_description;
	public String getDatabaseDescription() {	return database_description; }
	public void setDatabaseDescription(final String database_description) { this.database_description = database_description; }
	
	private String schema_name;
	public String getSchemaName() {	return schema_name; }
	public void setSchemaName(final String schema_name) { this.schema_name = schema_name; }
		
	private String schema_service;
	public String getSchemaService() {	return schema_service; }
	public void setSchemaService(final String schema_service) { this.schema_service = schema_service; }
	
	private String schema_password;
	public String getSchemaPassword() {	return schema_password; }
	public void setSchemaPassword(final String schema_password) { this.schema_password = schema_password; }
	
	private String schema_unique_user_name;
	public String getSchemaUniqueUserName() {	return schema_unique_user_name; }
	public void setSchemaUniqueUserName(final String schema_unique_user_name) { this.schema_unique_user_name = schema_unique_user_name; }
	
	
	
	
	
	private int database_active;
	public int getDatabaseActive() {	return database_active; }
	public void setDatabaseActive(final int database_active) { this.database_active = database_active; }
	
	private String tunnel_local_port;
	public String getTunnelLocalPort() {	return tunnel_local_port; }
	public void setTunnelLocalPort(String tunnel_local_port) { this.tunnel_local_port = tunnel_local_port; }
	
	private String tunnel_remote_host_address;
	public String getTunnelRemoteHostAddress() {	return tunnel_remote_host_address; }
	public void setTunnelRemoteHostAddress(String tunnel_remote_host_address) { this.tunnel_remote_host_address = tunnel_remote_host_address; }
		
	private String tunnel_remote_host_port;
	public String getTunnelRemoteHostPort() { return tunnel_remote_host_port; }
	public void setTunnelRemoteHostPort(String tunnel_remote_host_port) { this.tunnel_remote_host_port = tunnel_remote_host_port; }
	
	private String tunnel_remote_host_user;
	public String getTunnelRemoteHostUser() { return tunnel_remote_host_user; }
	public void setTunnelRemoteHostUser(String tunnel_remote_host_user) { this.tunnel_remote_host_user = tunnel_remote_host_user; }
	
	private String tunnel_remote_host_user_password;
	public String getTunnelRemoteHostUserPassword() { return tunnel_remote_host_user_password; }
	public void setTunnelRemoteHostUserPassword(String tunnel_remote_host_user_password) { this.tunnel_remote_host_user_password = tunnel_remote_host_user_password; }
	
	private String tunnel_remote_host_rsa_key;
	public String getTunnelRemoteHostRsaKey() {	return tunnel_remote_host_rsa_key; }
	public void setTunnelRemoteHostRsaKey(String tunnel_remote_host_rsa_key) { this.tunnel_remote_host_rsa_key = tunnel_remote_host_rsa_key; }


	private int totalRecords;
	public int getTotalRecords() {	return totalRecords; }
	public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }

	public void incrementTotalRecords() {
		this.totalRecords++;
	}

	public SqlRepoDatabase(final SqlRepoDatabase s) {
		this.database_id = s.database_id;
		this.database_type = s.database_type;
		this.database_name = s.database_name;
		this.database_server = s.database_server;
		this.database_port = s.database_port;
		this.database_description = s.database_description;
		this.database_warehouse_name = s.database_warehouse_name;
		this.database_account = s.database_account;
		this.database_other = s.database_other;
		this.schema_name = s.schema_name;
		this.schema_service = s.schema_service;
		this.schema_password = s.schema_password;
		this.schema_unique_user_name = s.schema_unique_user_name;
		this.tunnel_local_port = s.tunnel_local_port;
		this.tunnel_remote_host_address = s.tunnel_remote_host_address;
		this.tunnel_remote_host_port = s.tunnel_remote_host_port;
		this.tunnel_remote_host_user = s.tunnel_remote_host_user;
		this.tunnel_remote_host_user_password = s.tunnel_remote_host_user_password;
		this.tunnel_remote_host_rsa_key = s.tunnel_remote_host_rsa_key;
		this.database_active = s.database_active;
		this.totalRecords = s.totalRecords;
	}

	public SqlRepoDatabase() {
		this.database_id = 0;
		this.database_type = "";
		this.database_name = "";
		this.database_server = "";
		this.database_port = "";
		this.database_description = "";
		this.database_warehouse_name = "";
		this.database_account = "";
        this.database_other = "";
		this.schema_name = "";
		this.schema_service = "";
		this.schema_password = "";
		this.schema_unique_user_name = "";
		this.tunnel_local_port = "0";
		this.tunnel_remote_host_address = "";
		this.tunnel_remote_host_port = "0";
		this.tunnel_remote_host_user = "";
		this.tunnel_remote_host_user_password = "";
		this.tunnel_remote_host_rsa_key = "";
		this.database_active = 0;
		this.totalRecords = 0;
	}
	
	
	public SqlRepoDatabase(final int database_id, 
			             final String database_type, 
			             final String database_name, 
			             final String database_server,
			             final String database_port, 
			             final String database_description,
			             final String database_warehouse_name,
			             final String database_account,
			             final String database_other,
			             final String schema_name,  
			             final String schema_service,
			             final String schema_password,
			             final String schema_unique_user_name,
			             final String tunnel_local_port,
			             final String tunnel_remote_host_address,
			             final String tunnel_remote_host_port,
			             final String tunnel_remote_host_user,
			             final String tunnel_remote_host_user_password,
			             final String tunnel_remote_host_rsa_key,
			             final int database_active) throws Exception {
		this.database_id = database_id;
		this.database_type = database_type;
		this.database_name = database_name;
		this.database_server = database_server;
		this.database_port = database_port;
		this.database_description = database_description;
		this.database_warehouse_name = database_warehouse_name;
		this.database_account = database_account;
		this.database_other = database_other;
		this.schema_name = schema_name;
		this.schema_service = schema_service;
		this.schema_password = schema_password;
		this.schema_unique_user_name= schema_unique_user_name;
		this.tunnel_local_port = tunnel_local_port;
		this.tunnel_remote_host_port = tunnel_remote_host_port;
		this.tunnel_remote_host_user = tunnel_remote_host_user;
		this.tunnel_remote_host_user_password = tunnel_remote_host_user_password;
		this.tunnel_remote_host_rsa_key = tunnel_remote_host_rsa_key;
		this.database_active = database_active;
		this.totalRecords = 0;
	}
}
