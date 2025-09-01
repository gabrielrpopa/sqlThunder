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


package com.widescope.rdbmsRepo.database.table.scripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.List;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.table.model.Table;



public class ArtifactTableIndexH2Db {

	public enum TableType {
	    TABLE (1),
	    PIVOT (2),
	    BOTH  (3); 

		private final int tableType;
	    private TableType(int levelCode) { this.tableType = levelCode; }
	    public int getTableType() { return this.tableType; }
	    
	}

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   

	private final String DB_URL_DISK_REF = "jdbc:h2:file:./storage/artifact/@job@/@artifact@_@table_type@index;MODE=PostgreSQL";
  
	private String job = "";   
	private String artifact = "";
	private final Table table = new Table();
	TableType tableType = TableType.TABLE;
	
	   
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public ArtifactTableIndexH2Db(	final String job, 
									final String artifact, 
									final TableType tableType)	{
		this.job = job;
		this.artifact = artifact;
		this.tableType = tableType;
	}
		   

	
	
	
	
	public boolean createSchema(final Table table) throws Exception {

        String artifactTable = DDLScriptsRegularTable_H2.createTableString("table", table.getHeader());
        List<String> indexList = DDLScriptsRegularTable_H2.getIndexStrings("table", table.getHeader());
		List<String> ddlList = new java.util.ArrayList<String>();

		ddlList.add(artifactTable);
		ddlList.addAll(indexList);
	
		boolean isOK = false;
		Connection conn = null; 
		Statement statement = null; 
		try { 
			// STEP 1: Register JDBC driver 
			Class.forName(JDBC_DRIVER); 

			String dbUrl = this.DB_URL_DISK_REF;
			dbUrl = dbUrl.replace("@job@", this.job);
			dbUrl = dbUrl.replace("@artifact@", this.artifact);
			
			//STEP 2: Open a connection 
			conn = DriverManager.getConnection(this.DB_URL_DISK_REF, USER, PASS);  
			//STEP 3: Execute a query 
			statement = conn.createStatement(); 
			
			for (String ddl : ddlList) {
				isOK = statement.execute(ddl);
	        }
			
			statement.close();
			conn.commit();
			conn.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return isOK;
	}
	
	
	public boolean isFound(final String str) throws Exception {
		boolean ret = false;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "";
		if( DDLScriptsRegularTable_H2.isNumber(str) ) {
			sqlString = DDLScriptsRegularTable_H2.getSearchSqlALL("table", this.table.getHeader(), true);
		}
		else {
			sqlString = DDLScriptsRegularTable_H2.getSearchSqlVARCHAR("table", this.table.getHeader(), true);
		}
		
		sqlString = sqlString.replaceAll("@VALUE@", str);
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK_REF, USER, PASS); PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				ret = true;
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	public Table getMatchingRecords(String str) throws Exception {
		Table ret = new Table();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "";
		if( DDLScriptsRegularTable_H2.isNumber(str) ) {
			sqlString = DDLScriptsRegularTable_H2.getSearchSqlALL("table", this.table.getHeader(), false);
		}
		else {
			sqlString = DDLScriptsRegularTable_H2.getSearchSqlVARCHAR("table", this.table.getHeader(), false);
		}
		
		sqlString = sqlString.replaceAll("@VALUE@", str);
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK_REF, USER, PASS); PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				/*To Be implemented*/
		    }
			 

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	

}

