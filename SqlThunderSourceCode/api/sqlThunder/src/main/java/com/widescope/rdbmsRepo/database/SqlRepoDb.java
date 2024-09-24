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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;





@Component
public class SqlRepoDb {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";
	private final String DB_URL_DISK = "jdbc:h2:file:./sqlRepoDb;MODE=PostgreSQL";
	
	   
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 

	public SqlRepoDb() {	}

	private 
	void 
	closeHandles(	Connection conn,
					Statement statement,
					ResultSet rs) {
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	
	
	

	public 
	void 
	createSchema(final List<String> ddlList) throws Exception {
		Connection conn = null;
		Statement statement = null; 
		try { 
			Class.forName(JDBC_DRIVER); 
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);  
	        statement = conn.createStatement(); 
			
			for (String ddl : ddlList) {
				statement.executeUpdate(ddl);	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}  finally	{
			closeHandles(conn, statement, null);
	    } 
	}

	

	public 
	static Map<String, DbConnectionInfo> 
	generateSchema() throws Exception {
		final String sqlRepo = "sqlRepo";
		final String scriptRepo = "scriptRepo";

		try {
			List<String> ddlList = new java.util.ArrayList<String>();
			ddlList.add(SqlRepoDb.repo);
			ddlList.add(SqlRepoDb.repo_index1);
			ddlList.add(SqlRepoDb.repo_index2);
			ddlList.add(SqlRepoDb.repo_const1);
			SqlRepoDb configRepoDb = new SqlRepoDb();
			configRepoDb.createSchema(ddlList);
			Map<String, DbConnectionInfo> configRepoDbRecs = configRepoDb.getRepos();
			List<String> toBeDeleted = new java.util.ArrayList<String>();
			if(!configRepoDbRecs.containsKey(sqlRepo)) {
				DbConnectionInfo dbConnectionInfo = null;
				if(DbUtil.connectionDetailsTable.containsKey(sqlRepo)) {
					dbConnectionInfo = DbUtil.connectionDetailsTable.get(sqlRepo);
				} else {
					dbConnectionInfo = new DbConnectionInfo(	"H2", 
																"",
																"", 
																"sqlRepo", 
																"localhost",
																"0", 
																"sa", 
																"sa", 
																"Sql Repo DB", 
																"", 
																"0", 
																"", 
																"",
	            												"",
																"0", 
																"", 
																"", 
																""
																);
				}
					
				
				
				configRepoDb.addRepo(dbConnectionInfo);
				configRepoDbRecs.put(dbConnectionInfo.getDbName(), dbConnectionInfo);
			}
			if(!configRepoDbRecs.containsKey(scriptRepo)) {
				
				DbConnectionInfo dbConnectionInfo;
				if(DbUtil.connectionDetailsTable.containsKey(scriptRepo)) {
					dbConnectionInfo = DbUtil.connectionDetailsTable.get(scriptRepo);
				} else {
					dbConnectionInfo = new DbConnectionInfo(	"H2", 
																"",
																"", 
																"scriptRepo", 
																"localhost",
																"0", 
																"sa", 
																"sa", 
																"Script Repo DB", 
																"", 
																"0", 
																"", 
																"",
	            												"",
																"0", 
																"", 
																"", 
																""
																);
				}
			
				configRepoDb.addRepo(dbConnectionInfo);
				configRepoDbRecs.put(dbConnectionInfo.getDbName(), dbConnectionInfo);
			}
			
			
			configRepoDbRecs.forEach((k,v) -> 	{ 
													if(!k.equals(sqlRepo) && !k.equals(scriptRepo)) {
														toBeDeleted.add(k);
													}
												}
			);
			
			
			for(String item : toBeDeleted) {
				configRepoDbRecs.remove(item);
				configRepoDb.deleteRepo(item);
			}
			
			
			DbUtil.connectionDetailsTable = configRepoDbRecs;
			
			return configRepoDbRecs;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	

	
	public static String repo = "CREATE TABLE IF NOT EXISTS repo (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																					+ "	dbType VARCHAR(99),\r\n"
																					+ "	service VARCHAR(99),\r\n"
																					+ "	schema VARCHAR(99),\r\n"
																					+ "	dbUniqueName VARCHAR(99),\r\n"
																					+ "	server VARCHAR(999),\r\n"
																					+ "	port VARCHAR(999),\r\n"
																					+ "	userName VARCHAR(999),\r\n"
																					+ "	password VARCHAR(999),\r\n"
																					+ "	description VARCHAR(999),\r\n"
																					+ "	warehouse VARCHAR(999),\r\n"
																					+ "	account VARCHAR(999),\r\n"
																					+ "	other VARCHAR(999),\r\n"
																					
																					+ "	tunnelLocalPort VARCHAR(999),\r\n"
																					+ "	tunnelRemoteHostAddress VARCHAR(999),\r\n"
																					+ "	tunnelRemoteHostPort VARCHAR(999),\r\n"
																					+ "	tunnelRemoteUser VARCHAR(999),\r\n"
																					+ "	tunnelRemoteUserPassword VARCHAR(999),\r\n"
																					+ "	tunnelRemoteRsaKey VARCHAR(9999) \r\n"
																					+ ")";
	
	public static String repo_index1 = "CREATE INDEX IF NOT EXISTS idx_repo_1 ON repo(service, schema, server, port)";
	public static String repo_index2 = "CREATE INDEX IF NOT EXISTS idx_repo_2 ON repo(dbUniqueName)";
	public static String repo_const1 = "ALTER TABLE repo ADD CONSTRAINT IF NOT EXISTS ck_repo_1 CHECK (dbType IN ('H2', 'ORACLE', 'POSTGRESQL', 'SQLSERVER', 'MARIADB', 'MYSQL') );";

	
	

	public
	void 
	addRepo(final DbConnectionInfo dbConnectionInfo) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "INSERT INTO repo ("
										+ "   dbType, "
										+ "	  service, "
										+ "   schema, "
										+ "   dbUniqueName, "
										+ "   server, "
										+ "   port, "
										+ "   userName, "
										+ "   password, "
										+ "   description, "
										+ "   account, "
										+ "   other, "
										+ "   tunnelLocalPort, "
										+ "   tunnelRemoteHostAddress, "
										+ "   tunnelRemoteHostPort, "
										+ "   tunnelRemoteUser, "
										+ "   tunnelRemoteUserPassword, "
										+ "   tunnelRemoteRsaKey) "
								+ "		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			preparedStatement.setString(1, dbConnectionInfo.getDbType());
            preparedStatement.setString(2, dbConnectionInfo.getService());
            preparedStatement.setString(3, dbConnectionInfo.getService());
            preparedStatement.setString(4, dbConnectionInfo.getDbName());
            preparedStatement.setString(5, dbConnectionInfo.getServer());
            preparedStatement.setString(6, dbConnectionInfo.getPort());
            preparedStatement.setString(7, dbConnectionInfo.getUserName());
            preparedStatement.setString(8, dbConnectionInfo.getPassword());
            preparedStatement.setString(9, dbConnectionInfo.getDescription());
            preparedStatement.setString(10, dbConnectionInfo.getTunnelLocalPort());
            preparedStatement.setString(11, dbConnectionInfo.getTunnelRemoteHostAddress());
            preparedStatement.setString(12, dbConnectionInfo.getTunnelRemoteHostPort());
            preparedStatement.setString(13, dbConnectionInfo.getTunnelRemoteUser());
            preparedStatement.setString(14, dbConnectionInfo.getTunnelRemoteUserPassword());
            preparedStatement.setString(15, dbConnectionInfo.getTunnelRemoteRsaKey());
            
            
            
            preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	


	public 
	Map<String, DbConnectionInfo> 
	getRepos() throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, "
							+ "   dbType, "
							+ "	  service, "
							+ "   schema, "
							+ "   dbUniqueName, "
							+ "   server, "
							+ "   port, "
							+ "   userName, "
							+ "   password, "
							+ "   description, "
							+ "   warehouse, "
							+ "   account, "
							+ "   other, "
							+ "   tunnelLocalPort, "
							+ "   tunnelRemoteHostAddress, "
							+ "   tunnelRemoteHostPort, "
							+ "   tunnelRemoteUser, "
							+ "   tunnelRemoteUserPassword, "
							+ "   tunnelRemoteRsaKey "
						+ "  FROM repo";
		
		Map<String, DbConnectionInfo>  ret = new HashMap<String, DbConnectionInfo> ();
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	DbConnectionInfo b = new DbConnectionInfo(	rs.getString("dbType"), 
            												rs.getString("service"), 
            												rs.getString("schema"), 
            												rs.getString("dbUniqueName"), 
            												rs.getString("server"), 
            												rs.getString("port"),  
            												rs.getString("userName"),
            												rs.getString("password"),
            												rs.getString("description"),
            												rs.getString("warehouse"),
            												rs.getString("account"),
            												rs.getString("other"),
            												rs.getString("tunnelLocalPort"),
            												rs.getString("tunnelRemoteHostAddress"),
            												rs.getString("tunnelRemoteHostPort"),
            												rs.getString("tunnelRemoteUser"),
            												rs.getString("tunnelRemoteUserPassword"),
            												rs.getString("tunnelRemoteRsaKey")
            												);
            	ret.put(b.getDbName(), b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	
	

	public 
	void 
	updateRepo(DbConnectionInfo dbConnectionInfo) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO serverInfo("
												+ "   dbType, "
												+ "	  service, "
												+ "   schema, "
												+ "   server, "
												+ "   port, "
												+ "   userName, "
												+ "   password, "
												+ "   description, "
												+ "   warehouse, "
												+ "   account, "
												+ "   other, "
												+ "   tunnelLocalPort, "
												+ "   tunnelRemoteHostAddress, "
												+ "   tunnelRemoteHostPort, "
												+ "   tunnelRemoteUser, "
												+ "   tunnelRemoteUserPassword, "
												+ "   tunnelRemoteRsaKey "
												+ ") KEY(serverName) "
												+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			
			preparedStatement.setString(1, dbConnectionInfo.getDbType());
            preparedStatement.setString(2, dbConnectionInfo.getService());
            preparedStatement.setString(3, dbConnectionInfo.getService());
            preparedStatement.setString(4, dbConnectionInfo.getDbName());
            preparedStatement.setString(5, dbConnectionInfo.getServer());
            preparedStatement.setString(6, dbConnectionInfo.getPort());
            preparedStatement.setString(7, dbConnectionInfo.getUserName());
            preparedStatement.setString(8, dbConnectionInfo.getPassword());
            preparedStatement.setString(9, dbConnectionInfo.getDescription());
            
            preparedStatement.setString(10, dbConnectionInfo.getWarehouse());
            preparedStatement.setString(11, dbConnectionInfo.getAccount());
            preparedStatement.setString(12, dbConnectionInfo.getOther());
            
            preparedStatement.setString(13, dbConnectionInfo.getTunnelLocalPort());
            preparedStatement.setString(14, dbConnectionInfo.getTunnelRemoteHostAddress());
            preparedStatement.setString(15, dbConnectionInfo.getTunnelRemoteHostPort());
            preparedStatement.setString(16, dbConnectionInfo.getTunnelRemoteUser());
            preparedStatement.setString(17, dbConnectionInfo.getTunnelRemoteUserPassword());
            preparedStatement.setString(18, dbConnectionInfo.getTunnelRemoteRsaKey());
            
            preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	

	public 
	boolean 
	deleteRepo(final String dbUniqueName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE repo WHERE dbUniqueName = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, dbUniqueName);
          
            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}




	public
	void
	loadConnectionList(final List<DbConnectionInfo> ddlList) throws Exception {
		try {
			for (DbConnectionInfo ddl : ddlList) {
				this.addRepo(ddl);
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	
}

