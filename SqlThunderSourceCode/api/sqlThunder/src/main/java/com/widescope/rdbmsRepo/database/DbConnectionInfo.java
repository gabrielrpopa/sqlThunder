/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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

package com.widescope.rdbmsRepo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;



public class DbConnectionInfo {
	private String dbType;
	private String dbName;
	private String server;
	private String port;
	private String userName;
	private String password;
	private String jdbcDriver;
	private String dbUrl;
	private String description;
	private String warehouse;
	private String account;
	private String other;
	private String connType;
	private String tunnelLocalPort;
	private String tunnelRemoteHostAddress;
	private String tunnelRemoteHostPort;
	private String tunnelRemoteUser;
	private String tunnelRemoteUserPassword;
	private String tunnelRemoteRsaKey;


	
	public String getDbType() {	return dbType; }
	public void setDbType(final String dbType) {	this.dbType = dbType; }
	private String service;
	public String getService() {	return service; }
	public void setService(final String service) {	this.service = service; }
	private String schema;
	public String getSchema() {	return schema; }
	public void setSchema(final String schema) {	this.schema = schema; }
	public String getDbName() {	return dbName; }
	public void setDbName(final String dbName) {	this.dbName = dbName; }
	public String getServer() {	return server; }
	public void setServer(final String server) {	this.server = server; }
	public String getPort() {	return port; }
	public void setPort(final String port) {	this.port = port; }
	public String getUserName() {	return userName; }
	public void setUserName(final String userName) {	this.userName = userName; }
	public String getPassword() { return password; }
	public void setPassword(final String password) { this.password = password; }
	public String getJdbcDriver() { return jdbcDriver; }
	public void setJdbcDriver(final String jdbcDriver) { this.jdbcDriver = jdbcDriver; }
	public String getDbUrl() { return dbUrl; }
	public void setDbUrl(final String dbUrl) { this.dbUrl = dbUrl; }
	public String getDescription() { return description; }
	public void setDescription(final String description) {	this.description = description; }
	public String getWarehouse() { return warehouse; }
	public void setWarehouse(final String warehouse) { this.warehouse = warehouse; } 
	public String getAccount() { return account; }
	public void setAccount(String account) { this.account = account; }
	public String getOther() { return other; }
	public void setOther(String other) { this.other = other; }
	
	public String getConnType() { return connType; }
	public void setConnType(final String connType) { this.connType = connType; }
	public String getTunnelLocalPort() {	return tunnelLocalPort; }
	public void setTunnelLocalPort(final String tunnelLocalPort) { this.tunnelLocalPort = tunnelLocalPort; }
	public String getTunnelRemoteHostAddress() { return tunnelRemoteHostAddress; }
	public void setTunnelRemoteHostAddress(final String tunnelRemoteHostAddress) { this.tunnelRemoteHostAddress = tunnelRemoteHostAddress; }
	public String getTunnelRemoteHostPort() { return tunnelRemoteHostPort; }
	public void setTunnelRemoteHostPort(final String tunnelRemoteHostPort) {	this.tunnelRemoteHostPort = tunnelRemoteHostPort; }
	public String getTunnelRemoteUserPassword() { return tunnelRemoteUserPassword; }
	public void setTunnelRemoteUserPassword(final String tunnelRemoteUserPassword) { this.tunnelRemoteUserPassword = tunnelRemoteUserPassword; }
	public String getTunnelRemoteUser() { return tunnelRemoteUser; }
	public void setTunnelRemoteUser(final String tunnelRemoteUser) { this.tunnelRemoteUser = tunnelRemoteUser; }
	public String getTunnelRemoteRsaKey() {	return tunnelRemoteRsaKey; }
	public void setTunnelRemoteRsaKey(final String tunnelRemoteRsaKey) { this.tunnelRemoteRsaKey = tunnelRemoteRsaKey; }
	
	private boolean isActive;
	public boolean getIsActive() { return isActive; }
	public void setIsActive(final boolean isActive) {	this.isActive = isActive; }


	
	
	public static DbConnectionInfo makeDbConnectionInfo_(final SqlRepoDatabase sqlRepoDatabase) throws Exception {
		return new DbConnectionInfo(sqlRepoDatabase.getDatabaseType(), 
									sqlRepoDatabase.getSchemaService(), 
									sqlRepoDatabase.getSchemaService(), 
									sqlRepoDatabase.getDatabaseName(), 
									sqlRepoDatabase.getDatabaseServer(), 
									sqlRepoDatabase.getDatabasePort(), 
									sqlRepoDatabase.getSchemaName(), 
									sqlRepoDatabase.getSchemaPassword(), 
									sqlRepoDatabase.getDatabaseDescription(), 
									sqlRepoDatabase.getDatabaseWarehouseName(),
									"", /*account*/
									"", /*other*/
									sqlRepoDatabase.getTunnelLocalPort(),
									sqlRepoDatabase.getTunnelRemoteHostAddress(),
									sqlRepoDatabase.getTunnelRemoteHostPort(),
									sqlRepoDatabase.getTunnelRemoteHostUser(),
									sqlRepoDatabase.getTunnelRemoteHostUserPassword(),
									sqlRepoDatabase.getTunnelRemoteHostRsaKey()
									);
	}
	
	
	public static DbConnectionInfo makeDbConnectionInfo(final String dbType, 
														final String service, 
														final String schema, 
														final String dbName, 
														final String server, 
														final String port, 
														final String userName, 
														final String password, 
														final String description,
														final String warehouse,
														final String account,
														final String other,
														final String localPort,
														final String remoteHostAddress,
														final String remoteHostPort,
														final String remoteUser,
														final String remoteUserPassword,
														final String remoteRsaKey ) throws Exception {
		return new DbConnectionInfo(dbType, 
									service, 
									schema, 
									dbName, 
									server, 
									port, 
									userName, 
									password, 
									description, 
									warehouse,
									account,
									other,
									localPort,
									remoteHostAddress,
									remoteHostPort,
									remoteUser,
									remoteUserPassword,
									remoteRsaKey
		);
	}

	public static DbConnectionInfo makeDbConnectionInfo(final SqlRepoDatabase db) throws Exception	{
		return new DbConnectionInfo(db.getDatabaseType(),
									db.getSchemaService(), 
									db.getSchemaName(), 
									db.getDatabaseName(), 
									db.getDatabaseServer(), 
									db.getDatabasePort(), 
									db.getSchemaName(), 
									db.getSchemaPassword(), 
									db.getDatabaseDescription(),
									db.getDatabaseWarehouseName(),
									db.getDatabaseAccount(),
									db.getDatabaseOther(),
									db.getTunnelLocalPort(),
									db.getTunnelRemoteHostAddress(),
									db.getTunnelRemoteHostPort(),
									db.getTunnelRemoteHostUser(),
									db.getTunnelRemoteHostUserPassword(),
									db.getTunnelRemoteHostRsaKey()
									);
	}

	public static DbConnectionInfo makeH2ConnectionInfo(final String dbName,
														final String clusterId,
														final String userName,
														final String userPassword
														) throws Exception {
		return new DbConnectionInfo("H2", dbName, clusterId, userName, userPassword);
	}

	
	public static DbConnectionInfo makeH2InMemConnectionInfo(final String dbName,
															final String userName,
															final String userPassword
															) throws Exception {
		return new DbConnectionInfo("H2", dbName, userName, userPassword);
	}



	public
	DbConnectionInfo() {
		this.dbType = "";
		this.service = "";
		this.schema = "";
		this.dbName = "";
		this.server = "";
		this.port = "";
		this.userName = "";
		this.password = "";
		this.description = "";
		this.isActive = false;
		this.warehouse = "";
		this.account = "";
		this.other = "";

		this.setConnType("TUNNEL");
		this.setTunnelLocalPort("");
		this.setTunnelRemoteHostAddress("");
		this.setTunnelRemoteHostPort("");
		this.setTunnelRemoteUser("");
		this.setTunnelRemoteUserPassword("");
		this.setTunnelRemoteRsaKey("");
		this.dbUrl = "";
		this.jdbcDriver = "";
	}





	public
	DbConnectionInfo(	final String dbType, 
						final String service, 
						final String schema, 
						final String dbName, 
						final String server, 
						final String port, 
						final String userName, 
						final String password, 
						final String description,
						final String warehouse,
						final String account,
						final String other,
						
						final String tunnelLocalPort,
						final String tunnelRemoteHostAddress,
						final String tunnelRemoteHostPort,
						final String tunnelRemoteUser,
						final String tunnelRemoteUserPassword,
						final String tunnelRemoteRsaKey) throws Exception {
		this.dbType = dbType;
		this.service = service;
		this.schema = schema;
		this.dbName = dbName;
		this.server = server;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.isActive = false; 
		this.warehouse = warehouse;
		this.account = account;
		this.other = other;
		
		this.setConnType("TUNNEL");
		this.setTunnelLocalPort(tunnelLocalPort);
		this.setTunnelRemoteHostAddress(tunnelRemoteHostAddress);
		this.setTunnelRemoteHostPort(tunnelRemoteHostPort);
		this.setTunnelRemoteUser(tunnelRemoteUser);
		this.setTunnelRemoteUserPassword(tunnelRemoteUserPassword);
		this.setTunnelRemoteRsaKey(tunnelRemoteRsaKey);

		if(dbType.compareTo("SYBASE") == 0) {
			this.dbUrl = "jdbc:sybase:Tds:" + server + ":" + this.port + "/" + dbName + "?charset=iso_1";
			this.jdbcDriver = "com.sybase.jdbc4.jdbc.SybDriver";
		}
		else if(dbType.toUpperCase().compareTo("SQLSERVER") == 0) {
			this.dbUrl = "jdbc:sqlserver://" + server + ":" + port + ";" + dbName;
			this.jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		
		}
		else if(dbType.toUpperCase().compareTo("POSTGRESQL") == 0) {
			this.dbUrl = "jdbc:postgresql://" + server + ":" + port + "/" + dbName;
			this.jdbcDriver = "org.postgresql.Driver";
		}
		else if(dbType.toUpperCase().compareTo("ORACLE") == 0) {
			this.dbUrl = "jdbc:oracle:thin:@//" + server + ":" + port + "/" + dbName;
			this.jdbcDriver = "oracle.jdbc.OracleDriver";
		}
		else if(dbType.toUpperCase().compareTo("MYSQL") == 0) {
			this.dbUrl = "jdbc:mysql://" + server + ":" + port + "/" + dbName + "?charset=iso_1";
			this.jdbcDriver = "com.mysql.jdbc.Driver";
		
		}
		else if(dbType.toUpperCase().compareTo("DB2") == 0)	{
			this.dbUrl = "jdbc:db2://" + server + ":" + port + "/" +dbName + "?charset=iso_1";
			this.jdbcDriver = "com.ibm.db2";
		}
		else if(dbType.toUpperCase().compareTo("MARIADB") == 0)	{
			this.dbUrl = "jdbc:mariadb:" + server + ":" + port + "/" + dbName;
			this.jdbcDriver = "org.mariadb.jdbc.Driver";
		}
		else if(dbType.toUpperCase().compareTo("SNOWFLAKE") == 0) {
			this.dbUrl = "jdbc:snowflake://" + account + "." + other; 
			this.jdbcDriver = "com.snowflake.client.jdbc.SnowflakeDriver";
		}
		else if(dbType.toUpperCase().compareTo("H2") == 0) {
			this.dbUrl = "jdbc:h2:file:./" + dbName + ";MODE=PostgreSQL";
			this.jdbcDriver = "org.h2.Driver";
		}
		else {
			System.err.println("Driver not found for : " + dbType.toUpperCase());
			throw new Exception("Driver not found for : " + dbType.toUpperCase());
		}
	}


	public
	DbConnectionInfo(	final String dbType,
						 final String service,
						 final String schema,
						 final String dbName,
						 final String server,
						 final String port,
						 final String userName,
						 final String password,
						 final String description,
						 final String warehouse,
						 final String account,
						 final String other
						) throws Exception	{
		this.dbType = dbType;
		this.service = service;
		this.schema = schema;
		this.dbName = dbName;
		this.server = server;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.isActive = false;
		this.warehouse = warehouse;
		this.account = account;
		this.other = other;

		if(dbType.compareTo("SYBASE") == 0) {
			this.dbUrl = "jdbc:sybase:Tds:" + server + ":" + this.port + "/" + dbName + "?charset=iso_1";
			this.jdbcDriver = "com.sybase.jdbc4.jdbc.SybDriver";
		}
		else if(dbType.toUpperCase().compareTo("SQLSERVER") == 0) {
			this.dbUrl = "jdbc:sqlserver://" + server + ":" + port + ";" + dbName;
			this.jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

		}
		else if(dbType.toUpperCase().compareTo("POSTGRESQL") == 0) {
			this.dbUrl = "jdbc:postgresql://" + server + ":" + port + "/" + dbName;
			this.jdbcDriver = "org.postgresql.Driver";
		}
		else if(dbType.toUpperCase().compareTo("ORACLE") == 0) {
			this.dbUrl = "jdbc:oracle:thin:@//" + server + ":" + port + "/" + dbName;
			this.jdbcDriver = "oracle.jdbc.OracleDriver";
		}
		else if(dbType.toUpperCase().compareTo("MYSQL") == 0) {
			this.dbUrl = "jdbc:mysql://" + server + ":" + port + "/" + dbName + "?charset=iso_1";
			this.jdbcDriver = "com.mysql.jdbc.Driver";

		}
		else if(dbType.toUpperCase().compareTo("DB2") == 0)	{
			this.dbUrl = "jdbc:db2://" + server + ":" + port + "/" +dbName + "?charset=iso_1";
			this.jdbcDriver = "com.ibm.db2";
		}
		else if(dbType.toUpperCase().compareTo("MARIADB") == 0)	{
			this.dbUrl = "jdbc:mariadb:" + server + ":" + port + "/" + dbName;
			this.jdbcDriver = "org.mariadb.jdbc.Driver";
		}
		else if(dbType.toUpperCase().compareTo("SNOWFLAKE") == 0) {
			this.dbUrl = "jdbc:snowflake://" + account + "." + other;
			this.jdbcDriver = "com.snowflake.client.jdbc.SnowflakeDriver";
		}
		else if(dbType.toUpperCase().compareTo("H2") == 0) {
			this.dbUrl = "jdbc:h2:file:./" + dbName + ";MODE=PostgreSQL";
			this.jdbcDriver = "org.h2.Driver";
		}
		else {
			System.err.println("Driver not found for : " + dbType.toUpperCase());
			throw new Exception("Driver not found for : " + dbType.toUpperCase());
		}
	}


	/**
	 * For Embedded Databases
	 * @param dbType
	 * @param dbName
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public
	DbConnectionInfo(final String dbType,
					 final String dbName,
					 final String clusterId,
					 final String userName,
					 final String password
	) throws Exception {
		this.dbType = dbType;
		this.dbName = dbName;

		this.userName = userName;
		this.password = password;
		this.isActive = false;

		if(dbType.toUpperCase().compareTo("H2") == 0) {
			this.dbUrl = "jdbc:h2:file:../storage/embedded/" + clusterId + "/" + dbName + ";MODE=PostgreSQL";
			this.jdbcDriver = "org.h2.Driver";
		} else if(dbType.toUpperCase().compareTo("SQLITE") == 0) {
			this.dbUrl = "jdbc:h2:file:../storage/embedded/" + clusterId + "/" + dbName + ";MODE=PostgreSQL";
			this.jdbcDriver = "org.h2.Driver";
		}
		else {
			System.err.println("Driver not found for : " + dbType.toUpperCase());
			throw new Exception("Driver not found for : " + dbType.toUpperCase());
		}
	}
	
	
	public
	DbConnectionInfo(final String dbType,
					 final String dbName,
					 final String userName,
					 final String password) throws Exception	{
		this.dbType = dbType;
		this.dbName = dbName;

		this.userName = userName;
		this.password = password;
		this.isActive = false;


		if(dbType.toUpperCase().compareTo("H2") == 0) {
			this.dbUrl = "dbc:h2:mem:" + dbName + ";MODE=PostgreSQL";
			this.jdbcDriver = "org.h2.Driver";
		} else if(dbType.toUpperCase().compareTo("SQLITE") == 0) {
			this.dbUrl = "dbc:h2:mem:" + dbName + ";MODE=PostgreSQL";
			this.jdbcDriver = "org.h2.Driver";
		}
		else {
			System.err.println("Driver not found for : " + dbType.toUpperCase());
			throw new Exception("Driver not found for : " + dbType.toUpperCase());
		}
	}
	
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(getJdbcDriver());
		return DriverManager.getConnection(getDbUrl(),getUserName(), getPassword());
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
