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


package com.widescope.rdbmsRepo.database.mongodb.sql.toH2;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;

import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbConnection;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.DdlDmlUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.types.ColumnTypeTable;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.security.HashWrapper;




@Component
public class MongoToH2Sql {
	private final String JDBC_DRIVER = "org.h2.Driver";
	private String DB_URL_DISK = "jdbc:h2:file:./sqlSnapshots/@user@/@dbname@;@keepInMemory@;MODE=PostgreSQL";
	private String DB_FILE_DISK = "./sqlSnapshots/@user@/@dbname@";
	private String DB_URL_MEM = "jdbc:h2:mem:@dbname@;@keepInMemory@;MODE=PostgreSQL";  
	private Connection conn = null; 
	private boolean isInMem;
		   
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 

	public MongoToH2Sql() {	}
	

	public MongoToH2Sql(final String dbName,
						final String user, 
						final boolean isInMem,
						final int delayKeepInMemory) {

		this.DB_URL_DISK = this.DB_URL_DISK.replaceFirst("@dbname@", dbName);
		this.DB_URL_DISK = this.DB_URL_DISK.replaceFirst("@user@", user);
		String keepInMemory = "";
		if(delayKeepInMemory > 0) keepInMemory = "DB_CLOSE_DELAY=" + String.valueOf(delayKeepInMemory) ;
			
		this.DB_URL_DISK = this.DB_URL_DISK.replaceFirst("@keepInMemory@", keepInMemory);
		
		this.DB_URL_MEM = this.DB_URL_MEM.replaceFirst("@dbname@", dbName);
		this.DB_URL_MEM = this.DB_URL_MEM.replaceFirst("@user@", user);
		this.DB_URL_MEM = this.DB_URL_MEM.replaceFirst("@keepInMemory@", keepInMemory);
		
		
		this.DB_FILE_DISK = this.DB_FILE_DISK.replaceFirst("@dbname@", dbName);
		this.DB_FILE_DISK = this.DB_FILE_DISK.replaceFirst("@user@", user);

		this.isInMem = isInMem;
	}
	
	public void openConnection() throws Exception {
		try {
			Class.forName(JDBC_DRIVER); 
			if(!this.isInMem)
				this.conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			else
				this.conn = DriverManager.getConnection(DB_URL_MEM, USER, PASS);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	
	
	public void deleteDB() throws Exception {
		try {
			if(conn != null) {	conn.close();	conn = null; }
			if(!isInMem) {	FileUtilWrapper.deleteFile(DB_FILE_DISK); }
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	   
	
	private void closeHandles(Connection conn, 
							  Statement statement, 
							  ResultSet rs)	{
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	
	
	
	
	public void createSchema(final List<String> ddlList) throws Exception {
		Statement statement = null;
		try { 
			Class.forName(JDBC_DRIVER); 
			if(!this.isInMem)
				this.conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			else
				this.conn = DriverManager.getConnection(DB_URL_MEM, USER, PASS);

			statement = conn.createStatement(); 
			
			for (String ddl : ddlList) {
				statement.execute(ddl);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeHandles(conn, statement, null);
	    } 

	}

	
	public long 
	bulkInsertTable(final ResultSet rs, 
					final String tableName) throws Exception {
		Statement statement = null;
		long counter = 0;
		
		try { 
			Class.forName(JDBC_DRIVER); 
			if(!this.isInMem)
				this.conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			else
				this.conn = DriverManager.getConnection(DB_URL_MEM, USER, PASS);
			
			  
	         
			ResultSetMetaData metadata = rs.getMetaData();
			final int maxCount = metadata.getColumnCount();
			while(rs.next()) {
				try {
					StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
					
					for(int i = 1; i <= maxCount; i++) {
						int columnType = metadata.getColumnType(i);
						if(columnType == java.sql.Types.VARCHAR) {
							String val = rs.getString(i);
							if(rs.wasNull()) {	val = ""; }
							sql.append("'").append(val).append("'");
						}
						else if(columnType == java.sql.Types.NVARCHAR) {
							String val = rs.getNString(i);
							if(rs.wasNull()) {	val = ""; }
							sql.append("'").append(val).append("'");
						}
						else if(columnType == java.sql.Types.NCHAR) {
							String val = rs.getNString(i);
							if(rs.wasNull()) {	val = ""; }
							sql.append("'").append(val).append("'");
						}
						else if(columnType == java.sql.Types.LONGNVARCHAR) {
							String val = rs.getNString(i);
							if(rs.wasNull()) {	val = ""; }
							sql.append("'").append(val).append("'");
						}
						else if(columnType == java.sql.Types.INTEGER) {
							int val = rs.getInt(i);
							sql.append(val);
						}
						else if(columnType == java.sql.Types.BIGINT) {
							int val = rs.getInt(i);
							sql.append(val);
						}
						else if(columnType == java.sql.Types.SMALLINT) {
							int val = rs.getInt(i);
							sql.append(val);
						}
						else if(columnType == java.sql.Types.FLOAT) {
							float val = rs.getFloat(i);
							sql.append(val);
						}
						else if(columnType == java.sql.Types.DOUBLE) {
							double val =  rs.getDouble(i);
							sql.append(val);
						}
						else if(columnType == java.sql.Types.DECIMAL) {
							double val =  rs.getDouble(i);
							sql.append(val);
						}
						else if(columnType == java.sql.Types.TIMESTAMP)	{
							Timestamp  val = rs.getTimestamp(i);
							if(rs.wasNull())
								sql.append("''");
							else
								sql.append("'").append(val).append("'");
						}
						else if(columnType == java.sql.Types.TIMESTAMP_WITH_TIMEZONE)	{
							Timestamp  val = rs.getTimestamp(i);
							if(rs.wasNull())
								sql.append("''");
							else
								sql.append("'").append(val).append("'");
						}
						else if(columnType == java.sql.Types.DATE) {
							Date val = rs.getDate(i);
							if(rs.wasNull())
								sql.append("\"\"");
							else
								sql.append("\"").append(val.toString()).append("\"");
						}
						else if(columnType == java.sql.Types.CLOB) {
							Clob val = rs.getClob(i);
							if(rs.wasNull())
								sql.append("\"\"");
							else
								sql.append("\"").append(val.toString()).append("\"");
						}
						if(i < maxCount )
							sql.append(", ");
					}
					
					sql.append("); ");
					
					statement = this.conn.createStatement(); 
					statement.executeUpdate(sql.toString());
				} catch(SQLException se) {
					AppLogger.logException(se, Thread.currentThread().getStackTrace()[1], AppLogger.db);
					counter++;
				}
			}
				
			return counter;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public int insertTable(	final ResultQuery resultQuery,
							final DbConnectionInfo connectionDetailInfo,
							final String rdbmsSchema,
							final String rdbmsTable) throws Exception {
		try {
			int countInserted = 0;
			Map<String, ResultMetadata> metadataListToMap = ResultQuery.metadataListToMap(resultQuery.getMetadata());
			List<HashMap<String, Object>> body = DdlDmlUtils.toResultQueryBody(resultQuery.getResultQueryJson().toJSONString());
			ColumnTypeTable columnTypeTable = new ColumnTypeTable();


			String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
			String tbl = rdbmsTable!= null && !rdbmsTable.isBlank() && !rdbmsTable.isEmpty() ? rdbmsTable : resultQuery.getSqlName();



			int columnNumber = resultQuery.getMetadata().size();
            assert body != null;
            for(HashMap<String, Object> row : body) { // rows
				StringBuilder columnNames = new StringBuilder() ;
				StringBuilder columnValues = new StringBuilder() ;
				int count = columnNumber;
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					count--;
					String columnName = entry.getKey();
					Object value = entry.getValue();
					String columnTypeName = metadataListToMap.get(columnName).getColumnTypeName();

					columnNames.append(columnName);
					if(columnTypeTable.columnNoQuote.contains(columnTypeName)) {
						columnValues.append(value);
					}
					else if(columnTypeTable.columnQuote.contains(columnTypeName)) {
						columnValues.append("'").append(value).append("'");
					}
					else {
						columnValues.append("'").append(value).append("'");
					}

					if(count !=0) {
						columnValues.append(",");
						columnNames.append(",");
					}
				}
				String insertStatement = "INSERT INTO " + schema + tbl + "(" + columnNames + ") VALUES (" + columnValues + ")";
				countInserted += SqlQueryExecUtils.execStaticDml(connectionDetailInfo, insertStatement) ;
			}
			return countInserted;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}


	}
	
	
	
	public void 
	closeConnection() throws Exception {
		try	{
			this.conn.commit();
			this.conn.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public static MongoToH2Sql 
	generateSchema(	final String dbName,
					final String user, 
					final boolean isInMem,
					final int delayKeepInMemory,
					final List<String> ddlList) throws Exception {
		MongoToH2Sql mongoToH2Sql = new MongoToH2Sql(dbName, user, isInMem, delayKeepInMemory);
		mongoToH2Sql.createSchema(ddlList);
		return mongoToH2Sql;
	}
	
	public static MongoToH2Sql 
	openConnection(	final String dbName,
					final String user, 
					final boolean isInMem,
					final int delayKeepInMemory) throws Exception {
        return new MongoToH2Sql(dbName, user, isInMem, delayKeepInMemory);
	}
	
	
	
	
	public 
	ResultQuery
	execSql(final String sqlStatement, 
			final boolean isCompressed, 
			final String user) throws Exception {
		ResultQuery resultQuery = new ResultQuery();
		long hash = HashWrapper.hash64FNV(sqlStatement);
		Class.forName(JDBC_DRIVER); 
		
		try (Statement statement = this.conn.createStatement() )	{
			ResultSet rs = statement.executeQuery(sqlStatement);
			resultQuery = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			resultQuery = SqlQueryExecUtils.buildUpJsonFromResultSet(rs, resultQuery);
			resultQuery.setOutputPackaging("plain");
			resultQuery.setOutputFormat("json");
			resultQuery.setSqlHash(hash);
			resultQuery.setComment("");
			resultQuery.setSqlId(-1);
			resultQuery.setUser(user);
			resultQuery.setSqlStm(sqlStatement);
			resultQuery.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
			resultQuery.setSqlName("");
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return resultQuery;
	}
	
	
	
	public int addMongoObjects(	final List<MongoObjectRef> listOfMongoObjects, 
								final String rdbmsDatabaseName,
								final String rdbmsSchemaName,
								final String rdbmsTableName) throws Exception {
		int totalCounter = 0;
		Statement statement = this.conn.createStatement();
		for(MongoObjectRef mongoObjectRef : listOfMongoObjects) {
			MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(mongoObjectRef.getClusterName());
			MongoDbConnection mongoDbConnectionSource = new MongoDbConnection(mongoClusterRecordSource);
					
			try {
				String mongoResultSet = MongoGet.getDocumentById(	mongoDbConnectionSource, 
																	mongoObjectRef.getDatabaseName(), 
																	mongoObjectRef.getCollectionName(), 
																	mongoObjectRef.getDocumentId());
				SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsDatabaseName);
				DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
				ResultQuery resultQuery = ResultQuery.toResultQuery(mongoResultSet);
				String createTableStsm = DdlDmlUtils.createTableStmt(	resultQuery, 
																		connectionDetailInfo.getDbType(),
																		rdbmsSchemaName,
																		rdbmsTableName);
				statement.executeUpdate(createTableStsm);
                assert resultQuery != null;
                int countInserted = this.insertTable(	resultQuery,
														connectionDetailInfo,
														rdbmsSchemaName,
														rdbmsTableName);
				totalCounter+=countInserted;
			} catch (SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
			} catch (Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
		}
		return totalCounter;
	}
	
	
}

