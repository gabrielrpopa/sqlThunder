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


package com.widescope.rdbmsRepo.database.elasticsearch.repo;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.List;


import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;



@Component
public class ElasticExecutedQueriesRepoDb {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./elasticExecutedQueriesRepoDb;MODE=PostgreSQL";  
	
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public ElasticExecutedQueriesRepoDb()	{}
	
		   
	
	private 
	void 
	closeHandles(	Connection conn, 
					Statement statement, 
					ResultSet rs){
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	
	

	private 
	void
	createSchema(final List<String> ddlList) throws Exception	{
		Connection conn = null;
		Statement statement = null; 
		try { 
			// STEP 1: Register JDBC driver 
			Class.forName(JDBC_DRIVER); 
			//STEP 2: Open a connection 
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);  
			//STEP 3: Execute a query 
			statement = conn.createStatement(); 
			for (String ddl : ddlList) {
				statement.executeUpdate(ddl);
	        }
			
			statement.close();
			conn.commit();
			conn.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeHandles(conn, statement, null);
	    } 
	}

	
		

	
	
	public 
	static 
	void generateSchema() throws Exception {
		try {
			List<String> ddlList = new java.util.ArrayList<String>();
			ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesTable);
			ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesTable_index1);

			ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesUserTable);
			ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesUserTable_index1);

			ElasticExecutedQueriesRepoDb executedQueriesRepoDb = new ElasticExecutedQueriesRepoDb();
			executedQueriesRepoDb.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	

	public static String 
	executedQueriesTable = "CREATE TABLE IF NOT EXISTS "
							+ " executedQueriesTable (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
												+ "	type VARCHAR(99) ,\r\n"  /*SQL or DSL*/
												+ "	source VARCHAR(1) ,\r\n"  /*A-ADHOC or R-REPO*/
												+ "	usr VARCHAR(999) ,\r\n"
												+ "	content VARCHAR(4000),\r\n"
												+ "	jsonParam VARCHAR(999) ,\r\n"
												+ "	timestamp BIGINT )";
	
		
	public static String 
	executedQueriesTable_index1 = "CREATE INDEX IF NOT EXISTS "
								+ " idx_executedQueriesTable_2 ON executedQueriesTable(usr);";


		

	public static String 
	executedQueriesUserTable = "CREATE TABLE IF NOT EXISTS "
								+ " executedQueriesUserTable (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
													+ "	queryId BIGINT ,\r\n" 
													+ "	usr VARCHAR(999) )";	
	public static String 
	executedQueriesUserTable_index1 = "CREATE INDEX IF NOT EXISTS "
									+ " idx_executedQueriesUSerTable_1 ON executedQueriesUserTable(queryId, usr);";

	
	
	
	
	
	public ElasticExecutedQueryList
	getStatementByUser( final String usr) throws Exception {
		ElasticExecutedQueryList ret = new ElasticExecutedQueryList();
		String selectStm = "SELECT id, type, source, content, jsonParam, timestamp FROM elasticExecutedQueriesTable WHERE user = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			
			preparedStatement.setString(1, usr);
			
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				ElasticExecutedQuery r 
				= new ElasticExecutedQuery(	rs.getLong("id"), 
											rs.getString("type"),
											rs.getString("source"), 
											usr,
											rs.getString("content"),
											rs.getString("jsonParam"),
											rs.getLong("timestamp")
				       			          );
				
				
				ret.addElasticExecutedQuery(r);
			}
			rs.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	
	
	public void 
	addStatement(	final String type,
					final String source, 
					final String usr,
					final String content,
					final String jsonParam,
					final long timestamp) throws Exception {

 		String addSql = "INSERT INTO elasticExecutedQueriesTable(type, source, usr, content, jsonParam, timestamp) "
						+ " VALUES (?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(addSql))	{
			
			preparedStatement.setString(1, type);
			preparedStatement.setString(2, source);
			preparedStatement.setString(3, usr);
			preparedStatement.setString(4, content);
			preparedStatement.setString(5, jsonParam);
			preparedStatement.setLong(6, timestamp);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public void 
	deleteStatement(final long id) throws Exception {
		if(id <= 0)	throw new Exception("Query Id is negative");
		String deleteDslParam = "DELETE elasticExecutedQueriesTable WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
			preparedStatement.setLong(1, id);
			preparedStatement.execute();
		} catch (SQLException e) {
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void 
	deleteStatementByUser(final String user) throws Exception {
		String deleteDslParam = "DELETE elasticExecutedQueriesTable WHERE user = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
			
			preparedStatement.setString(1, user);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
}
