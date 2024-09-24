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
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import com.widescope.logging.AppLogger;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.AllowedDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.AllowedDatabaseList;



public final class DbUtil {

	public static Map<String, DbConnectionInfo> connectionDetailsTable = new ConcurrentHashMap<String, DbConnectionInfo>();
	public static DbConnectionInfo GetConnectionDetails(final String dbName)	{
		return connectionDetailsTable.get(dbName);
	}

	private static void closeHandles(Connection conn,  Statement statement, ResultSet rs)	{
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
			
			
			if(!AllowedDatabaseList.isDatabase(connectionDetailInfo.getDbType())) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Database Type not allowed"));
			}
			
			
			if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.oracle) == 0)	{
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 FROM dual");
				isActive = true;
				conn.close();
				
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.postgresql) == 0) {
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.sqlserver) == 0)	{
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.mariadb) == 0) {
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.mysql) == 0)	{
				conn = DriverManager.getConnection(	connectionDetailInfo.getDbUrl(), connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.snowflake) == 0) {
				Properties properties = new Properties();
			    properties.put("user", connectionDetailInfo.getUserName());     
			    properties.put("password", connectionDetailInfo.getPassword()); 
			    properties.put("account", connectionDetailInfo.getAccount());  
			    conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(), properties);
			    statement = conn.createStatement();
				rs = statement.executeQuery("select 1 ");
				isActive = true;
			}
			else if(connectionDetailInfo.getDbType().toUpperCase().compareTo(AllowedDatabase.h2) == 0) {
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
