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


import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.widescope.logging.AppLogger;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public final class DbUtil {
	public static final String oracle = "ORACLE";
	public static final String sqlserver = "SQLSERVER";
	public static final String postgresql = "POSTGRESQL";
	public static final String sybase = "SYBASE";
	public static final String db2 = "DB2";
	public static final String cassandra = "CASSANDRA";
	public static final String mariadb = "MARIADB";
	public static final String mysql = "MYSQL";
	public static final String sqlite = "SQLITE";
	public static final String h2 = "H2";
	public static final String redis = "REDIS";
	public static final String splunk = "SPLUNK";
	public static final String hsqldb = "HSQLDB";
	public static final String teradata = "TERADATA";
	public static final String derby = "DERBY";
	public static final String berkeley = "BERKELEY";
	public static final String neo4j = "NEO4J";
	public static final String snowflake = "SNOWFLAKE";
	public static final String hive = "HIVE";
	public static final String solr = "SOLR";
	public static final String csv = "CSV";
	public static final String parquet = "PARQUET";
	public static final String generic = "GENERIC";

	public
	static
	List<String> dbTypes = Arrays.asList(new String[] { oracle,
														sqlserver,
														postgresql,
														sybase,
														db2,
														cassandra,
														mariadb,
														mysql,
														sqlite,
														h2,
														redis,
														splunk,
														hsqldb,
														teradata,
														derby,
														berkeley,
														neo4j,
														snowflake,
														hive,
														solr,
														csv,
														parquet,
														generic});


	public
	static
	List<String> embeddedDbTypes = Arrays.asList(new String[] { h2, hsqldb, derby, sqlite, berkeley, csv });



	public static boolean isDatabase(String db) {
		return dbTypes.contains(db.toUpperCase());
	}

	public static String genCommaSeparatedAllDb() {
		return DbUtil.dbTypes.stream().collect(Collectors.joining("', '", "'", "'"));
	}

	public static String genCommaSeparatedEmbeddedDb() {
		return DbUtil.embeddedDbTypes.stream().collect(Collectors.joining("', '", "'", "'"));
	}

	public static void closeDbHandles(Connection conn,	Statement statement, ResultSet rs) {
		try { if(rs!=null && !rs.isClosed())	rs.close(); } catch(SQLException se1)	{ System.out.print(se1.getMessage());}
		try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
		try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
	}

	public static Map<String, DbConnectionInfo> connectionDetailsTable = new ConcurrentHashMap<String, DbConnectionInfo>();
	public static DbConnectionInfo GetConnectionDetails(final String dbName)	{
		return connectionDetailsTable.get(dbName);
	}

	public static void closeHandles(Connection conn, Statement statement, ResultSet rs)	{
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
		
	public static boolean checkConnection(final DbConnectionInfo connectionDetailInfo) throws Exception {
		boolean isActive = false;
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		SSHTunnelInfoOut sshTunnelInfoOut = null;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());

			int tunnelLocalPort = 0;
			int tunnelRemoteHostPort = 0;
			try {
				tunnelLocalPort = Integer.parseInt( connectionDetailInfo.getTunnelLocalPort() );
				tunnelRemoteHostPort = Integer.parseInt(connectionDetailInfo.getTunnelRemoteHostPort());
			} catch(Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
			}
			
			
			
			if( tunnelLocalPort > 0 
					&& !connectionDetailInfo.getTunnelRemoteHostAddress().isBlank() 
					&& tunnelRemoteHostPort > 0
					&& !connectionDetailInfo.getTunnelRemoteUser().isBlank()
					&& !connectionDetailInfo.getTunnelRemoteUserPassword().isBlank()
					&& connectionDetailInfo.getTunnelRemoteRsaKey().isBlank() ) {
				
				sshTunnelInfoOut =  tunnelWithRemoteHostPassword(tunnelLocalPort,
																connectionDetailInfo.getTunnelRemoteHostAddress(),
																tunnelRemoteHostPort,
																connectionDetailInfo.getTunnelRemoteUser(),
																connectionDetailInfo.getTunnelRemoteUserPassword(),
																connectionDetailInfo.getServer(),
																Integer.parseInt(connectionDetailInfo.getPort()) 
																);
			}
			else if( tunnelLocalPort > 0 
					&& !connectionDetailInfo.getTunnelRemoteHostAddress().isBlank() 
					&& tunnelRemoteHostPort > 0
					&& !connectionDetailInfo.getTunnelRemoteUser().isBlank()
					&& connectionDetailInfo.getTunnelRemoteUserPassword().isBlank()
					&& !connectionDetailInfo.getTunnelRemoteRsaKey().isBlank() ) {
				
				sshTunnelInfoOut =  tunnelWithRsaKeyFile(tunnelLocalPort,
															connectionDetailInfo.getTunnelRemoteHostAddress(),
															tunnelRemoteHostPort,
															connectionDetailInfo.getTunnelRemoteUser(),
															connectionDetailInfo.getTunnelRemoteRsaKey(),
															connectionDetailInfo.getServer(),
															Integer.parseInt(connectionDetailInfo.getPort()) 
															);
			}
			
			
			if(!DbUtil.isDatabase(connectionDetailInfo.getDbType())) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Database Type not allowed"));
			}
			
			
			if(connectionDetailInfo.getDbType().toUpperCase().compareTo(oracle) == 0)	{
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 FROM dual");
				isActive = true;
				conn.close();
				
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(postgresql) == 0) {
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(sqlserver) == 0)	{
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(mariadb) == 0) {
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(mysql) == 0)	{
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(snowflake) == 0) {
				Properties properties = new Properties();
			    properties.put("user", connectionDetailInfo.getUserName());     
			    properties.put("password", connectionDetailInfo.getPassword()); 
			    properties.put("account", connectionDetailInfo.getAccount());  
			    conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(), properties);
			    statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(h2) == 0) {
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				isActive = true;
			}

		}
		catch (Exception e)	{
			isActive = false;
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
		}
		finally	{
			closeHandles(conn, statement, rs);
		    if( sshTunnelInfoOut != null && sshTunnelInfoOut.getErrorCode() == 0 ) {
				sshTunnelInfoOut.getSession().disconnect();
			}
		}
		return isActive;
	}
	
	
	
	public static void updateConnectionInfo(final DbConnectionInfo connectionDetailInfo) throws Exception {
		try	{
			connectionDetailsTable.put(connectionDetailInfo.getDbName(), connectionDetailInfo);
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public static List<DbConnectionInfo> getDatabaseList()	throws Exception{
		ArrayList<DbConnectionInfo> ret = new ArrayList<>();
		try	{
			for(Map.Entry<String, DbConnectionInfo> entry : connectionDetailsTable.entrySet()) {
				DbConnectionInfo value = entry.getValue();
			    ret.add(value);
			}
			return ret;
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
	}
	
	
	public static SSHTunnelInfoOut tunnelWithRemoteHostPassword(	final int localPort,
																	final String remoteHostAddress,
																	final int remoteHostPort,
																	final String remoteUser,
																	final String remoteUserPassword,
																	final String databaseServer,
																	final int databasePort) {
		Session s = null;
		int assignedPort;
		try {
			JSch jsch = new JSch();
			s = jsch.getSession(remoteUser, remoteHostAddress, remoteHostPort);
			s.setPassword(remoteUserPassword);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("Compression", "yes");
			config.put("ConnectionAttempts", "2");
			s.setConfig(config);
			s.connect();
			assignedPort = s.setPortForwardingL(localPort, databaseServer, databasePort);
			
			if(assignedPort == 0) {
				if(s.isConnected())
					s.disconnect();


				return new SSHTunnelInfoOut(-1, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Assigned Port Error"));
			}


            return new SSHTunnelInfoOut(s, assignedPort);
		}
		catch(JSchException e) {
			if(s != null && s.isConnected())
				s.disconnect();


			return new SSHTunnelInfoOut(-1, AppLogger.logTunnel(e, Thread.currentThread().getStackTrace()[1]));
		}
	
	}


	public static SSHTunnelInfoOut tunnelWithRsaKeyFile(final int localPort,
														final String remoteHostAddress,
														final int remoteHostPort,
														final String remoteUser,
														final String rsaKeyFile,
														final String databaseServer,
														final int databasePort) {
		Session s = null;
		int assignedPort;
		try {
			JSch jsch = new JSch();
			jsch.addIdentity(rsaKeyFile);
			
			s = jsch.getSession(remoteUser, remoteHostAddress, remoteHostPort);
			s.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("Compression", "yes");
			config.put("ConnectionAttempts", "2");
			s.setConfig(config);
			s.connect();
			assignedPort = s.setPortForwardingL(localPort, databaseServer, databasePort);
			
			if(assignedPort == 0) {
				if(s.isConnected())
					s.disconnect();
				

				return new SSHTunnelInfoOut(-1, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Assigned Port Error"));
			}


            return new SSHTunnelInfoOut(s, assignedPort);
		}
		catch(JSchException e) {
			if(s != null && s.isConnected())
				s.disconnect();
			return new SSHTunnelInfoOut(-1, AppLogger.logTunnel(e, Thread.currentThread().getStackTrace()[1]));
		}
	
	}
	
	
	
}
