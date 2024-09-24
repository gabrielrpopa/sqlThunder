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

package com.widescope.rdbmsRepo.database.embeddedDb.embedded;


import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.RecordsAffected;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.CompoundIndex;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.Metadata;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.utils.security.HashWrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class H2Static implements  EmbeddedInterface {

	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "org.h2.Driver";
	private String DB_URL_DISK = "jdbc:h2:file:../storage/embedded/@cluster@/@dbName@;MODE=PostgreSQL";
	private static final String CLUSTER_PATH = "../storage/embedded/@cluster@";

	//  Database credentials
	private static final String USER = "sa";
	private static final String PASS = "sa";
	
	
	private long timestamp;
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

	private long clusterId;
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	
	private long dbId;
	public long getDbId() { return dbId; }
	public void setDbId(long dbId) { this.dbId = dbId; }
	
	private String dbName;
	public String getDbName() { return dbName; }
	public void setDbName(String dbName) { this.dbName = dbName; }
	
	
	
	public static String getClusterPath(final long _clusterId) { 
		return H2Static.CLUSTER_PATH.replace("@cluster@", String.valueOf(_clusterId));
	}
	
	public static String getDbFilePath(final long _clusterId, final String dbName) { 
		return H2Static.CLUSTER_PATH.replace("@cluster@", String.valueOf(_clusterId)) + "/" + dbName;
	}
	
	

	public H2Static()	{
		
	}
	
	
	public H2Static(final long _clusterId,
					final long _dbId) throws Exception	{
		try {
			EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
			EmbeddedDbRecordList recCluster = embeddedDbRepo.getClusterEmbeddedDb(_clusterId);
			List<EmbeddedDbRecord> r = recCluster.getEmbeddedDbRecordList().stream().filter(x->x.getDbId() == _dbId).toList();
			this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", r.get(0).getFileName());
			this.DB_URL_DISK = this.DB_URL_DISK.replace("@cluster@", String.valueOf(_clusterId));
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}

	
	public H2Static(final long _clusterId,
					final String _dbName) 	{

		this.setDbName(_dbName);
		this.setClusterId(_clusterId);
		this.setDbId(-1);		
		
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", _dbName);
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@cluster@", String.valueOf(_clusterId));
	}

	public String getDbUrl(final long clusterId,
							final String  dbName) {
		String ret = "../storage/embedded/@cluster@/@dbName@";
		ret = ret.replace("@dbName@", dbName);
		ret = ret.replace("@cluster@", String.valueOf(clusterId));
		return ret;
	}
	

	public void setDbUrl(	final long _clusterId,
							final long _dbId) throws Exception {
		try {
			EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
			EmbeddedDbRecordList recCluster = embeddedDbRepo.getClusterEmbeddedDb(_clusterId);
			List<EmbeddedDbRecord> r = recCluster.getEmbeddedDbRecordList().stream().filter(x->x.getDbId() == _dbId).collect(Collectors.toList());
			this.setDbName(r.get(0).getFileName());
			this.setClusterId(_clusterId);
			this.setDbId(_dbId);
			this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
			this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", this.getDbName());
			this.DB_URL_DISK = this.DB_URL_DISK.replace("@cluster@", String.valueOf(_clusterId));
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	
	
	
	public static String getUserName() {
		return USER;
	}
	
	public static String getUserPassword() {
		return PASS;
	}


	private 
	void 
	closeHandles(	Connection conn, 
					Statement statement, 
					ResultSet rs){
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	

	@Override
	public Connection getConnection() throws Exception {
		try {
			Connection conn;
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			return conn;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}


	private 
	boolean 
	createSchema(final List<String> ddlList) throws Exception	{
		boolean isOK = false;
		Connection conn = null; 
		Statement statement = null; 
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement();
			for (String ddl : ddlList) {
				isOK = statement.execute(ddl);
			}

			statement.close();
			conn.commit();
			conn.close();
			return isOK;

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
	boolean 
	createUserTable(DbConnectionInfo conn, 
					final String sqlContent) throws Exception	{
		if(sqlContent.contains("__userTable__")) {
			throw new Exception("__userTable__ is reserved table");
		}
		
		try {
			/*Identify tables created by user**/
			String createUseTable = "CREATE TABLE IF NOT EXISTS __userTable__(tName VARCHAR(200) NOT NULL);";
			SqlQueryExecUtils.execStaticDdl(conn, createUseTable);
			List<String> tableListInitial = SqlMetadataWrapper.getAllTableList(conn,"");
			SqlQueryExecUtils.execStaticDdl(conn, sqlContent);
			List<String> tableListFinal = SqlMetadataWrapper.getAllTableList(conn,"");
			ArrayList<String> result =  new ArrayList<>(CollectionUtils.subtract(tableListFinal, tableListInitial));
			if(result.size() == 1) {
				String insertStm = "INSERT INTO __userTable__ VALUES('" + result.get(0) + "')";
				int recNo = SqlQueryExecUtils.execStaticDml(conn, insertStm);
                return recNo == 1;
			} else {
				return false;
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public 
	boolean 
	createUserTable(final String sqlContent) throws Exception	{
		try {
			Connection conn;
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			/*Identify tables created by user**/
			String createUseTable = "CREATE TABLE IF NOT EXISTS __userTable__(tName VARCHAR(200) NOT NULL);";
			boolean isCreated = SqlQueryExecUtils.execStaticDdl(conn, createUseTable);
			if(!isCreated) return isCreated;
			
			List<String> tableListInitial = SqlMetadataWrapper.getAllTableList(conn,"");
			SqlQueryExecUtils.execStaticDdl(conn, sqlContent);
			List<String> tableListFinal = SqlMetadataWrapper.getAllTableList(conn,"");
			ArrayList<String> result =  new ArrayList<>(CollectionUtils.subtract(tableListFinal, tableListInitial));
			if(result.size() == 1) {
				String insertStm = "INSERT INTO __userTable__ VALUES('" + result.get(0) + "')";
				int recNo = SqlQueryExecUtils.execStaticDml(conn, insertStm);
                isCreated = recNo == 1;
			}
			return isCreated;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		
	}
	
	public 
	static 
	String 
	generateEmptySchema(final long clusterId,
						final String  dbName
						) throws Exception {
		try {
			H2Static embeddedDbRepo = new H2Static(clusterId,dbName);
			List<String> ddlList = new ArrayList<String>();
			ddlList.add("CREATE TABLE IF NOT EXISTS __userTable__(tName VARCHAR(200) NOT NULL)");
			embeddedDbRepo.createSchema(ddlList);
			return embeddedDbRepo.getDbUrl(clusterId, dbName);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	
	
	public 
	static 
	void generateSchema(final long clusterId,
						final String  dbName,
						final Map<String, List<Metadata>> schema,//   list of columns
						final List<CompoundIndex> compoundIndexList) throws Exception {

		List<String> ddlList = new ArrayList<>();

		// Create table statements first
		for (String tableName : schema.keySet()) {
			StringBuilder ddlStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(@content@)");
			StringBuilder content = new StringBuilder();
			List<Metadata> tableSchemaList = schema.get(tableName);
			for (int i = 0; i < tableSchemaList.size(); i++){
				content.append(tableSchemaList.get(i).getColumnName()).append(tableSchemaList.get(i).getColumnName()).append(tableSchemaList.get(i).getSqlType().getColumnType());
				if(tableSchemaList.get(i).getLength() > 0 && tableSchemaList.get(i).getScale() > 0) {
					ddlStatement.append("(").append(tableSchemaList.get(i).getLength()).append(",").append(tableSchemaList.get(i).getScale()).append(")");
				} else if(tableSchemaList.get(i).getLength() > 0 && tableSchemaList.get(i).getScale() == 0) {
					ddlStatement.append("(").append(tableSchemaList.get(i).getLength()).append(")");
				} else if(tableSchemaList.get(i).getLength() == 0 && tableSchemaList.get(i).getScale() == 0) {

				} else {

				}
				if(i != tableSchemaList.size() -1) {
					ddlStatement.append(",");
				}
			}
			ddlStatement = new StringBuilder(ddlStatement.toString().replace("@content@", content.toString()));
			ddlList.add(ddlStatement.toString());
		}

		// Creating individual indexes on the tables
		for (String tableName : schema.keySet()) {
			List<Metadata> tableSchemaList = schema.get(tableName);
			for (int i = 0; i < tableSchemaList.size(); i++) {
				if(tableSchemaList.get(i).getIsIndex()) {
					String ddlStatement = "CREATE " + tableSchemaList.get(i).getUniqueIndex() + " INDEX IF NOT EXISTS " + tableName + i + " ON " + tableName + "(" + tableSchemaList.get(i).getColumnName() + ")";
					ddlList.add(ddlStatement);
				}
			}
		}

		// Creating compound indexes on the tables
		for (String tableName : schema.keySet()) {
			List<Metadata> tableSchemaList = schema.get(tableName);
			for (int i = 0; i < tableSchemaList.size(); i++) {
				if(tableSchemaList.get(i).getIsIndex()) {
					String ddlStatement = "CREATE " + tableSchemaList.get(i).getUniqueIndex() + " INDEX IF NOT EXISTS " + tableName + i + " ON " + tableName + "(" + tableSchemaList.get(i).getColumnName() + ")";
					ddlList.add(ddlStatement);
				}
			}
		}



		H2Static embeddedDbRepo = new H2Static(clusterId,dbName);
		embeddedDbRepo.createSchema(ddlList);
	}

	@Override
	public String getDbType() {
		return "H2";
	}
	
	
	
	@Override
	public ResultQuery execStaticQueryWithResultSet(String staticQuery) throws Exception {
		if(!SqlParser.isSqlDQL(staticQuery)) {

			throw new Exception("Not a query");
		}
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret = new ResultQuery();
		long hash = HashWrapper.hash64FNV(staticQuery);
		
		try	{
			Class.forName(JDBC_DRIVER);
			//STEP 2: Open a connection 
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(staticQuery);
			
			ret = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromResultSet(rs, ret);
			ret.setOutputPackaging("plain");
			ret.setOutputFormat("json");
			ret.setSqlHash(hash);
			
			int rows = 0 ;
			rs.beforeFirst();
			if (rs.last()) {
				rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeHandles(conn, statement,rs);		}
		return ret;
	}


	@Override
	public TableFormatMap execStaticQueryWithTableFormat(String staticQuery) throws Exception {
		if(!SqlParser.isSqlDQL(staticQuery)) {
			throw new Exception("Not a query");
		}
		TableFormatMap ret;
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try	{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(staticQuery);
			ret = SqlQueryExecUtils.buildUpMetadataWithReturnTableFormat(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromMigrationReturn(rs, ret);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeHandles(conn, statement,rs);
		}

		return ret;
	}
	

	

	@Override
	public ResultQuery execStaticQueryToWebsocket(	final String user,
													final long userId,
													final String requestId,
													final String jobId,
													final String staticQuery,
													final String sqlName,
													final String httpSession,
													final String persist) throws Exception	{


		String dbRelativePath = "./" + this.clusterId + "/" + this.getDbName();
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeH2ConnectionInfo(dbRelativePath, String.valueOf(clusterId), USER, PASS);

		if(SqlParser.isSqlDQL(staticQuery)) {
			SqlQueryExecUtils.execStaticQueryWithWebsocket( connectionDetailInfo, 
														staticQuery, 
														sqlName,
														httpSession,
														requestId,
														user,
														userId,
														persist,
														jobId);
            return new ResultQuery(0);
		} else if(SqlParser.isSqlDML(staticQuery)) {
			int noAffected = SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
            return new ResultQuery(noAffected);
		} else if(SqlParser.isSqlDDL(staticQuery)) {
			boolean isOK = SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, staticQuery);
			return new ResultQuery(isOK ? 1 : 0);
		} else {
			return new ResultQuery(0);
		}
	}

	
	public RecordsAffected 
	insertBulkIntoEmbeddedTable(final String schemaName,
								final String tableName,
								final List<Map<String, Object>> rows, 
								final Map<String, String> metadata) throws Exception {
		RecordsAffected ret = new RecordsAffected("INSERT", 0, 0);
		Connection conn = getConnection();
		try	{
			String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(metadata, tableName);
			String sqlStm = SqlMetadataWrapper.generateInsertTableStm(metadata, schemaName, tableName);
			boolean isCreated = createUserTable(createTableStm);
			if(isCreated) {
				Statement statement = conn.createStatement();
				for(Map<String, Object> row: rows) {
					String execInsert = SqlMetadataWrapper.generateExecutableInsertTableStm(sqlStm,metadata,row);
					try {
						int recordsAffected = statement.executeUpdate(execInsert);		
						ret.addRecAffected(recordsAffected);
					} catch(Exception ex) {
						ret.incrementRecFailed();
						ret.setMessage(ex.getMessage());
					}
				}
			} else {
				ret.setMessage("User Table could not be created");
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	
	@Override
	public boolean bulkInsert(	final ResultQuery resultQuery,
								final String sqlName) throws Exception {
		boolean isOK = false;
		Connection conn = null; 
		Statement statement = null; 
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement();
			statement.close();
			conn.commit();
			conn.close();
			return isOK;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeHandles(conn, statement, null);
	    } 
	}


	@Override
	public boolean copyEmbeddedFileBasedDb(final EmbeddedInterface db) {
		// TODO Auto-generated method stub
		return false;
	}



}

