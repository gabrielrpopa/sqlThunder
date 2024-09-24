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
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec.EmbeddedExecTableList;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.CompoundIndex;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.Metadata;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.DataTransfer;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.TableAffected;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatExtMetadataOutput;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatOutput;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatRowOutput;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.security.HashWrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class H2InMem implements  EmbeddedInterface {

	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "org.h2.Driver";
	private String DB_URL_DISK = "jdbc:h2:mem:@dbName@;MODE=PostgreSQL"; // DB_CLOSE_DELAY=-1;
	
	
	//  Database credentials
	private static final String USER = "sa";
	private static final String PASS = "sa";
	
	private Connection conn = null; 
	
	private long timestamp;
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	
	private String sessionId;
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	
	private String requestId;
	public String getRequestId() { return requestId; }
	public void setRequestId(String requestId) { this.requestId = requestId; }
	
	private String userId;
	public String getUserId() {	return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	
	private String source; /* __EMBEDDED__ / __RDBMS__  / __ELASTIC__, __MONGODB__  */
	public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }
	
	/* embedded cluster name/elastic cluster name/ mongodb cluster name, rdbms connection  */
	private String sourceName; 
	public String getSourceName() { return sourceName; }
	public void setSourceName(String sourceName) { this.sourceName = sourceName; }
	
	/*embedded clusterid id*/
	private long clusterId;
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }
	
	/*embedded db id*/
	private long dbId;
	public long getDbId() { return dbId; }
	public void setDbId(long dbId) { this.dbId = dbId; }
	
	private String dbName;
	public String getDbName() { return dbName; }
	public void setDbName(String dbName) { this.dbName = dbName; }
	
	/* source: QUERY / TABLE / INDEX / COLLECTION*/
	private String type;  
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	/*source: QUERY statement / TABLE name/ Collection Names/ Index Names*/
	private List<String> userTables;  
	public List<String> getUserTables() { return userTables; }
	public void setUserTables(List<String> userTables) { this.userTables = userTables; }
	public void addUserTables(String userTables) { this.userTables.add(userTables); }
	
	private List<TableDefinition> userTablesExtended;  
	public List<TableDefinition> getUserTablesExtended() { return userTablesExtended; }
	public void setUserTablesExtended(List<TableDefinition> userTablesExtended) { this.userTablesExtended = userTablesExtended; }
	public void addUserTableExtended(TableDefinition userTable) { 
		if(userTablesExtended == null) {
			this.userTablesExtended = new ArrayList<TableDefinition>();
		}
		this.userTablesExtended.add(userTable); 
	}
	
	private EmbeddedExecTableList execp;
	public EmbeddedExecTableList getExecp() {return execp;}
	public void setExecp(final EmbeddedExecTableList execp) { this.execp = execp; }
	
	
	
	public void removeInMemDb () {
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ex)	{}
		
		System.runFinalization();
	}
	
	
	

	@Override
    protected void finalize() {
	    try {
	    	try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	        System.out.println("Closing Connection in the finalizer");
	    } catch (Exception e) {
	    	System.out.println("Closing Connection failed");
	    }
	}
	
	
	public H2InMem() {
		this.setClusterId(-1);
		this.setDbId(-1);
		this.setType(null);
		this.setSessionId(null);
		this.setRequestId(null);
		this.setUserId(null);
		this.setUserTables(new ArrayList<>());
		this.setSource(null);
		this.setDbName(null);
		this.setSourceName(null);
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
	}
	
	public H2InMem(	final long _clusterId, 
					final long _dbId,
					final String _type,
					final String _sessionId,
					final String _requestId,
					final String _userId,
					final List<String> _userTables,
					final String _source,
					final String _sourceName
					) throws Exception {
		
		if(!_type.equalsIgnoreCase("QUERY") 
				&& !_type.equalsIgnoreCase("TABLE")
				&& !_type.equalsIgnoreCase("INDEX")
				&& !_type.equalsIgnoreCase("COLLECTION")
				) {
			throw new Exception("illegal type");
		}
		
		if(!_source.equalsIgnoreCase("__EMBEDDED__") 
				&& !_source.equalsIgnoreCase("__RDBMS__")
				&& !_source.equalsIgnoreCase("__ELASTIC__")
				&& !_source.equalsIgnoreCase("__MONGODB__")
				) {
			throw new Exception("illegal type");
		}
		
		
	
		
		
		String toDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString16();
		
		this.setDbName(toDbName);
		
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", this.getDbName());
		// STEP 1: Register JDBC driver 
		Class.forName(JDBC_DRIVER);
		//STEP 2: Open a connection 
		conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
		
		
		this.setClusterId(_clusterId);
		this.setDbId(_dbId);
		this.setType(_type);
		this.setSessionId(_sessionId);
		this.setRequestId(_requestId);
		this.setUserId(_userId);
		
		this.setUserTables(_userTables);
		this.setSource(_source);
		this.setSourceName(_sourceName);
		
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
	}
	

	public H2InMem(	final long _clusterId, 
					final long _dbId,
					final String _type,
					final String sessionId_,
					final String requestId_,
					final String userId_) throws Exception	{
		
		if(!_type.equalsIgnoreCase("QUERY") && !_type.equalsIgnoreCase("TABLE")) {
			throw new Exception("illegal type");
		}
		
		EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
		EmbeddedDbRecordList recCluster = embeddedDbRepo.getClusterEmbeddedDb(_clusterId);
		List<EmbeddedDbRecord> r = recCluster.getEmbeddedDbRecordList().stream().filter(x->x.getDbId() == _dbId).toList();
		this.setDbName(r.get(0).getFileName());

		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", this.getDbName());
		// STEP 1: Register JDBC driver 
		Class.forName(JDBC_DRIVER);
		//STEP 2: Open a connection 
		conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
		
		this.setClusterId(_clusterId);
		this.setDbId(_dbId);
		this.setExecp(null);
		this.setType(_type);
		this.setRequestId(requestId_);
		this.setSessionId(sessionId_);
		this.setUserId(userId_);
		this.setDbName(this.getDbName());
		this.setUserTables(new ArrayList<>());
		this.setSource("__EMBEDDED__");
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
	}
	
	public H2InMem(	final long _clusterId, 
					final String dbName,
					final String _type,
					final String sessionId_,
					final String requestId_,
					final String userId_) throws Exception	{

		if(!_type.equalsIgnoreCase("QUERY") && !_type.equalsIgnoreCase("TABLE")) {
			throw new Exception("illegal type");
		}
		
		EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
		EmbeddedDbRecordList recCluster = embeddedDbRepo.getClusterEmbeddedDb(_clusterId);
		List<EmbeddedDbRecord> r = recCluster.getEmbeddedDbRecordList().stream().filter(x->x.getFileName() == dbName).toList();
		
		this.setDbName(r.get(0).getFileName());
		
		
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", this.getDbName());
		// STEP 1: Register JDBC driver 
		Class.forName(JDBC_DRIVER);
		//STEP 2: Open a connection 
		conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
		
		this.setClusterId(_clusterId);
		this.setDbName(dbName);
		this.setExecp(null);
		this.setType(_type);
		this.setRequestId(requestId_);
		this.setSessionId(sessionId_);
		this.setUserId(userId_);
		this.setDbName(this.getDbName());
		this.setUserTables(new ArrayList<>());
		this.setSource("__EMBEDDED__");
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
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
	

	public H2InMem(	final String _connectionName, 
					final String _dbName,
					final String _type,
					final String sessionId_,
					final String requestId_,
					final String userId_) throws Exception	{
		
		if(!_type.equalsIgnoreCase("QUERY") 
				&& !_type.equalsIgnoreCase("TABLE")
				&& !_type.equalsIgnoreCase("INDEX")
				&& !_type.equalsIgnoreCase("COLLECTION")
				) {
			throw new Exception("illegal type");
		}
		
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", _dbName);
		// STEP 1: Register JDBC driver 
		Class.forName(JDBC_DRIVER);
		//STEP 2: Open a connection 
		conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);

		this.setClusterId(-1);
		this.setDbId(-1);
		this.setDbName(null);
		this.setExecp(null);
		this.setSource(_connectionName);
		this.setSessionId(sessionId_);
		this.setRequestId(requestId_);
		this.setUserId(userId_);
		this.setDbName(_dbName);
		this.setType(type);
		this.setUserTables(new ArrayList<>());
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
	}
	

	public H2InMem( final EmbeddedExecTableList execp_, 
					final String sessionId_,
					final String requestId_,
					final String userId_) throws Exception	{

		EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
		EmbeddedDbRecordList recCluster = embeddedDbRepo.getClusterEmbeddedDb(execp_.getClusterId());
		List<EmbeddedDbRecord> r = recCluster.getEmbeddedDbRecordList().stream().filter(x->x.getDbId() == dbId).toList();
		
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", r.get(0).getFileName());
		// STEP 1: Register JDBC driver 
		Class.forName(JDBC_DRIVER);
		//STEP 2: Open a connection 
		conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
		
		this.setClusterId(execp.getClusterId());
		this.setDbId(-1);
		this.setExecp(execp_);
		this.setType(type);
		this.setSessionId(sessionId_);
		this.setRequestId(requestId_);
		this.setUserId(userId_);
		this.setDbName(r.get(0).getFileName());
		this.setUserTables(new ArrayList<>());
		this.setSource("__EMBEDDED__");
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
	}
	
	
	public H2InMem(	final String sessionId_,
					final String requestId_,
					final String userId_,
					final String dbName) throws Exception	{

		
		
		
		type = "CSV";
		
			
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", dbName);
		// STEP 1: Register JDBC driver 
		Class.forName(JDBC_DRIVER);
		//STEP 2: Open a connection 
		conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
		
		this.setClusterId(-1);
		this.setDbId(-1);
		this.setExecp(null);
		this.setType(type);
		this.setSessionId(sessionId_);
		this.setRequestId(requestId_);
		this.setUserId(userId_);
		this.setDbName(dbName);
		this.setUserTables(new ArrayList<>());
		this.setSource("__EMBEDDED__");
		this.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
	}

	@Override
	public Connection getConnection() throws Exception {
		return conn;
	}
	
	
	@Override
	public String getDbType() {
		return "H2";
	}
	
	public String getDbUrl() {
		return DB_URL_DISK;
	}
	
	public void setDbUrl(final String  dbName) {
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@dbName@", dbName);
	}
	

	
	public static String getUserName() {
		return USER;
	}
	
	public static String getUserPassword() {
		return PASS;
	}


	



	public 
	boolean 
	createSchema(final List<String> ddlList) throws Exception	{
		boolean isOK = false;
		Statement statement = null; 
		try {
			// STEP 1: Register JDBC driver 
			statement = conn.createStatement();
			for (String ddl : ddlList) {
				isOK = statement.execute(ddl);
			}
			statement.close();
			return isOK;
	    } catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 
	}

	
	
	public
	synchronized 
	boolean 
	create__userTable__() throws Exception	{
		try {
			/*Identify tables created by user**/
			String createUseTable = "CREATE TABLE IF NOT EXISTS __userTable__(tName VARCHAR(200) NOT NULL);";
			return this.execStaticDdl(createUseTable);
	    } catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
	    } 
	}


	
	public
	synchronized 
	boolean 
	createUserTable(final String sqlContent) throws Exception	{

		if(sqlContent.contains("__userTable__")) {
			throw new Exception("__userTable__ is reserved table");
		}
		
		
		try {
			/*Identify tables created by user**/
			String createUseTable = "CREATE TABLE IF NOT EXISTS __userTable__(tName VARCHAR(200) NOT NULL);";
			boolean isCreated = this.execStaticDdl(createUseTable);
			if(!isCreated) return false;
			List<String> tableListInitial = this.getTableList("");
			if(!this.execStaticDdl(sqlContent)) return false;
			List<String> tableListFinal = this.getTableList("");
			ArrayList<String> result =  new ArrayList<>(CollectionUtils.subtract(tableListFinal, tableListInitial));
			if(result.size() == 1) {
				String insertStm = "INSERT INTO __userTable__ VALUES('" + result.get(0) + "')";
				int recNo = execStaticDml(insertStm);
                return recNo == 1;
			} else {
				return false;
			}
	    } catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } 
	}
	
	
	public
	synchronized 
	boolean 
	createUserTableOnce(final String sqlContent) throws Exception	{
		try {
			return  this.execStaticDdl(sqlContent);
	    } catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } 
	}
	
	
	public 
	synchronized 
	int insertBag(final TableFormatOutput ret, String tableName) throws Exception {
		String insertStm = SqlMetadataWrapper.generateInsertTableStm(ret.getExtendedMetadata(), "",tableName);
		int recordsAffected = 0;
		Statement statement = conn.createStatement();
		for(TableFormatRowOutput row: ret.getRows()) {
			String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(insertStm,row);
			try {
				recordsAffected = statement.executeUpdate(insert);		
				recordsAffected++;
			} catch(SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
		    } catch(Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
		    } finally	{
				closeHandles(conn, statement, null);
		    } 
		}
		
		statement.close();
		return recordsAffected;
	}
	
	
	public 
	synchronized 
	int execStaticDml(final String query) throws Exception {
		Statement statement = null;
		int recordsAffected = 0;
		try	{
			statement = conn.createStatement();
			recordsAffected = statement.executeUpdate(query);
		} catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 
		
		return recordsAffected;
	}
	
	public 
	synchronized 
	boolean execStaticDdl(final String query) throws Exception {
		Connection conn = null;
		Statement statement = null;
		try	{
			Class.forName(JDBC_DRIVER);
			//STEP 2: Open a connection 
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement();
			statement.executeUpdate(query);
			return true;
		} catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 
	}

	
	public 
	synchronized 
	List<String> 
	getTableList(final String schemaName) throws Exception 	{
		List<String> ret = new ArrayList<>();
		try	{
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" });
			if(schemaName!=null && !schemaName.isBlank() && !schemaName.isEmpty()) {
				while (rs.next()) {
					String schema = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");
					//String tableCat = rs.getString("TABLE_CAT");
					//String tableType = rs.getString("TABLE_TYPE");
					//String remarks = rs.getString("REMARKS");
					//String typeSchema = rs.getString("TYPE_SCHEM");
					//String typeName = rs.getString("TYPE_NAME");
					
					if(schema.toUpperCase().compareTo(schemaName.toUpperCase()) == 0) {
						ret.add(tableName);
					}
				}
			} else {
				while (rs.next()) {
					
					
					String tableName = rs.getString("TABLE_NAME");
					//String tableCat = rs.getString("TABLE_CAT");
					//String tableType = rs.getString("TABLE_TYPE");
					//String remarks = rs.getString("REMARKS");
					//String typeSchema = rs.getString("TYPE_SCHEM");
					//String typeName = rs.getString("TYPE_NAME");
					//String refGen = rs.getString("REF_GENERATION");
					if(tableName.compareToIgnoreCase("TUTORIALS_TBL") == 0) {
						//String schema = rs.getString("TABLE_SCHEM");
						
					}
					ret.add(rs.getString(3));
				}
			}
		} catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, null, null);
	    } 
		return ret;
	}
	
	
	public static String 
	getCreateTableStm(final List<TableFormatExtMetadataOutput> extendedMetadata, String finalSql) {
		List<String> lst = SqlParser.getTableNamesFromSql(finalSql);
		StringBuilder ddlStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + lst.get(0) + "(@content@)");
		String content = "";
		for (int i = 0; i < extendedMetadata.size(); i++){
			content = content + extendedMetadata.get(i).getColName() + extendedMetadata.get(i).getColName() + extendedMetadata.get(i).getResultMetadata().getColumnTypeName() ;
			if(extendedMetadata.get(i).getResultMetadata().getLength() > 0 && extendedMetadata.get(i).getResultMetadata().getScale() > 0) {
				ddlStatement.append("(").append(extendedMetadata.get(i).getResultMetadata().getLength()).append(",").append(extendedMetadata.get(i).getResultMetadata().getScale()).append(")");
			} else if(extendedMetadata.get(i).getResultMetadata().getLength() > 0 && extendedMetadata.get(i).getResultMetadata().getScale() == 0) {
				ddlStatement.append("(").append(extendedMetadata.get(i).getResultMetadata().getLength()).append(")");
			} else if(extendedMetadata.get(i).getResultMetadata().getLength() == 0 && extendedMetadata.get(i).getResultMetadata().getScale() == 0) {

			} else {

			}
			if(i != extendedMetadata.size() -1) {
				ddlStatement.append(",");
			}
		}
		return ddlStatement.toString().replace("@content@", content);
	}
	
	
	
	
	public 
	synchronized 
	void generateSchema(final String  dbName,
						final String userName,
						final Map<String, List<Metadata>> schema,//  a map with the list of columns
						final List<CompoundIndex> compoundIndexList) throws Exception {
		List<String> ddlList = new ArrayList< >();

		// Create table statements first
		for (String tableName : schema.keySet()) {
			StringBuilder ddlStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(@content@)");
			String content = "";
			List<Metadata> tableSchemaList = schema.get(tableName);
			for (int i = 0; i < tableSchemaList.size(); i++){
				content = content + tableSchemaList.get(i).getColumnName() + tableSchemaList.get(i).getColumnName() + tableSchemaList.get(i).getSqlType().getColumnType() ;
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
			ddlStatement = new StringBuilder(ddlStatement.toString().replace("@content@", content));
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
		createSchema(ddlList);
	}

	

	@Override
	public 
	synchronized 
	ResultQuery execStaticQueryWithResultSet(final String staticQuery) throws Exception {
		Statement statement = null;
		ResultSet rs ;
		ResultQuery ret ;
		long hash = HashWrapper.hash64FNV(staticQuery);
		
		try	{
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
			
		} catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 
		
		return ret;
	}
	
	
	
	public 
	synchronized 
	TableFormatMap
	execStaticQueryWithTable(final String query) throws Exception	{
		TableFormatMap ret ;
		Statement statement = null;
		ResultSet rs;
		
		try	{
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			ret = SqlQueryExecUtils.buildUpMetadataWithReturnTableFormat(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromMigrationReturn(rs, ret);
		} catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 

		return ret;
	}
	
	

	

	
	
	@Override
	public 
	synchronized 
	ResultQuery execStaticQueryToWebsocket(	final String user,
											final long userId,
											final String requestId,
											final String jobId,
											final String staticQuery,
											final String sqlName,
											final String httpSession,
											final String persist) throws Exception	{
		String dbRelativePath = "./" + clusterId + "/" + dbName;
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

	
	@Override
	public 
	synchronized 
	boolean bulkInsert(	final ResultQuery resultQuery,
						final String sqlName) throws Exception {
		boolean isOK = false;
		Statement statement = null; 
		try {
			statement = conn.createStatement();
			statement.close();
			conn.commit();
			conn.close();
			return isOK;
	    } catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 
	}


	@Override
	public 
	synchronized 
	TableFormatMap execStaticQueryWithTableFormat(String staticQuery) throws Exception {
		TableFormatMap ret;
		Statement statement = null;
		ResultSet rs;
		
		try	{
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(staticQuery);
			ret = SqlQueryExecUtils.buildUpMetadataWithReturnTableFormat(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromMigrationReturn(rs, ret);
		} catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
	    } finally	{
			closeHandles(conn, statement, null);
	    } 
		return ret;
	}


	
	
	
	@Override
	public boolean copyEmbeddedFileBasedDb(final EmbeddedInterface db)  {
		return true;
	}

	
	
	public DataTransfer loadH2QueryInMem(final String sqlQuery, final String comment) throws Exception {
		this.getUserTables().add(sqlQuery);
		this.setType("QUERY");
		
		DataTransfer ret = new DataTransfer();
		String resultTable = StringUtils.generateUniqueString();
		
		EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
		EmbeddedDbRecordList recCluster = embeddedDbRepo.getClusterEmbeddedDb(this.clusterId);
		List<EmbeddedDbRecord> r = recCluster.getEmbeddedDbRecordList().stream().filter(x->x.getDbId() == this.dbId).toList();
		
		H2Static h2Static = new H2Static(clusterId , r.get(0).getFileName());
		TableFormatMap result = h2Static.execStaticQueryWithTableFormat(sqlQuery);
		String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(result.getMetadata(), resultTable);
		boolean isCreated = createUserTable(createTableStm);
		if(isCreated) {
			String insertStm = SqlMetadataWrapper.generateInsertTableStm(result.getMetadata(), "",resultTable);
			int recordsAffected = 0;
			Statement statement = conn.createStatement();
			for(Map<String, Object> m: result.getRows()) {
				String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(insertStm, result.getMetadata(), m);
				try {
					
					recordsAffected = statement.executeUpdate(insert);		
					recordsAffected++;
				} catch(SQLException e)	{
					throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
			    } catch(Exception e) {
					throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
			    } finally	{
					closeHandles(conn, statement, null);
			    } 
			}
			ret.getLstTables().add(new TableAffected(recordsAffected, resultTable));
			this.userTables.add(resultTable);
			EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
			return ret;
		} else {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Table Not created")) ;
		}
	}

	
	public DataTransfer loadH2DatabaseInMem(final String comment) throws Exception {
		DataTransfer ret = new DataTransfer(this.getDbName(),new ArrayList<>());
		H2Static h2Static = new H2Static(clusterId, this.getDbName());
		Connection conn = h2Static.getConnection();
		List<String> uTables = h2Static.getUserTables(conn);
		for(String tName: uTables) {
			TableMetadata tMetadata = SqlMetadataWrapper.getTableColumns(tName, conn);
			RdbmsTableSetup setup = SqlMetadataWrapper.createTableStm(tMetadata, "", tName, "H2");
			String q = "SELECT * FROM " + tName;
			TableFormatMap result = h2Static.execStaticQueryWithTableFormat(q);
			boolean isCreated = createUserTable(setup.getCreateTableStm());
			if(isCreated) {
				String insertStm = SqlMetadataWrapper.generateInsertTableStm(result.getMetadata(), "",tName);
				int recordsAffected = 0;
				Statement statement = conn.createStatement();
				for(Map<String, Object> m: result.getRows()) {
					String insertExec = SqlMetadataWrapper.generateExecutableInsertTableStm(insertStm, result.getMetadata(), m);
					try {
						int addedRec = statement.executeUpdate(insertExec);
						recordsAffected+=addedRec;
					} catch(SQLException e)	{
						throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
				    } catch(Exception e) {
						throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
				    } finally	{
						closeHandles(conn, statement, null);
				    } 
				}
				statement.close();
				this.userTables.add(tName);
				ret.getLstTables().add(new TableAffected(recordsAffected, tName));	
			} else {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Cannot create table" + tName)) ;
			}
		}

		this.setType("TABLE");
		EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
		return ret;
		
	}
	
	
	public DataTransfer loadRdbmsQueryInMem(final TableFormatMap result, 
											final String tableName,
											final String sqlStatement,
											final String connectionName,
											final String comment) throws Exception {
		addUserTables(tableName);
		DataTransfer ret = new DataTransfer();
		String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(result.getMetadata(), tableName);
		boolean isCreated = createUserTable(createTableStm);
		if(isCreated) {
			String insertStm = SqlMetadataWrapper.generateInsertTableStm(result.getMetadata(), "",tableName);
			int recordsAffected = 0;
			Statement statement = conn.createStatement();
			try {
				for(Map<String, Object> m: result.getRows()) {
					String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(insertStm, result.getMetadata(), m);
					try {
						recordsAffected = statement.executeUpdate(insert);		
						recordsAffected++;
					} catch(SQLException e)	{
						throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
				    } catch(Exception e) {
						throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
				    } 
				}
			} catch(SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
			} catch(Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db)) ;
			} finally {
				closeHandles(conn, statement, null);
			}
			
			ret.getLstTables().add(new TableAffected(recordsAffected, tableName));	
		}
		

		this.setType("TABLE");
		EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
		return ret;
	}
	
	
	public DataTransfer loadRdbmsQueriesInMem(	final List<RdbmsTableSetup> resultList,
												final String connectionName,
												final String comment) throws Exception {
		List<String> lstQueries = new ArrayList<>();
		DataTransfer ret = new DataTransfer();
		for(RdbmsTableSetup t: resultList) {
			lstQueries.add(t.getTableName());
			boolean isCreated = createUserTable(t.getCreateTableStm());
			if(isCreated) {
				int recordsAffected = 0;
				Statement statement = conn.createStatement();
				for(Map<String, Object> m: t.getTableFormatMap() .getRows()) {
					String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(t.getInsertTableStm(), t.getTableFormatMap(). getMetadata(), m);
					try {
						recordsAffected = statement.executeUpdate(insert);
						recordsAffected++;
					} catch(SQLException e)	{
						throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
				    } catch(Exception e) {
						throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
				    } 
				}
				ret.getLstTables().add(new TableAffected(recordsAffected, t.getTableName()));
				closeHandles(conn, statement, null);
			}
		}
		EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
		return ret;
	}
	
	
	public DataTransfer loadRdbmsQueriesInMem(	final List<RdbmsTableSetup> resultList) throws Exception {
		boolean isCreated = createUserTableOnce(resultList.get(0).getCreateTableStm());
		DataTransfer ret = new DataTransfer();
		if(!isCreated) return ret; 
		for(RdbmsTableSetup t: resultList) {
			int recordsAffected = 0;
			Statement statement = conn.createStatement();
			for(Map<String, Object> m: t.getTableFormatMap() .getRows()) {
				String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(t.getInsertTableStm(), t.getTableFormatMap(). getMetadata(), m);
				try {
					recordsAffected+=statement.executeUpdate(insert);
					ret.incrementCountRecord();
				} catch(SQLException e)	{
					throw new Exception(AppLogger.logDb(e,Thread.currentThread().getStackTrace()[1])) ;
				} catch(Exception e) {
					throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
				}
			}
			ret.getLstTables().add(new TableAffected(recordsAffected, t.getTableName()));
			closeHandles(conn, statement, null);
		}
		EmbeddedWrapper.addInMemEmbeddedQuery(this, "in-mem query execution by aggregating multiple datasets");
		return ret;
	}
	
	

	public DataTransfer loadRdbmsTablesInMem(	final List<RdbmsTableSetup> resultList,
												final String connectionName,
												final String comment) throws Exception {
		List<String> lstTables = new ArrayList<>();
		
		DataTransfer ret = new DataTransfer();
		for(RdbmsTableSetup t: resultList) {
			lstTables.add(t.getTableName());
			boolean isCreated = createUserTable(t.getCreateTableStm());
			if(isCreated) {
				int recordsAffected = 0;
				Statement statement = conn.createStatement();
				for(Map<String, Object> m: t.getTableFormatMap().getRows()) {
					String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(t.getInsertTableStm(), t.getTableFormatMap().getMetadata(), m);
					try {
						recordsAffected = statement.executeUpdate(insert);		
						recordsAffected++;
					} catch(SQLException e)	{
						throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
				    } catch(Exception e) {
						throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
				    } finally	{
						closeHandles(conn, statement, null);
				    } 
				}
				ret.getLstTables().add(new TableAffected(recordsAffected, t.getTableName()));	
			}
		}
		
		this.setType("TABLE");
		
		EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
		
		return ret;
	}
	
	public DataTransfer 
	createAndInsertBulkIntoTable(	final String schemaName,
									final String tableName,
									final List<Map<String, Object>> rows, 
									final Map<String, String> metadata,
									final String comment) throws Exception	{
		addUserTables(tableName);
		Statement statement = null;
		DataTransfer ret = new DataTransfer();
		try	{
			
			String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(metadata, tableName);
			boolean isCreated = createUserTable(createTableStm);
			if(isCreated) {
				String sqlStm = SqlMetadataWrapper.generateInsertTableStm(metadata, schemaName, tableName);
				int recordsAffected = 0;
				for(Map<String, Object> row: rows) {
					String execInsert = SqlMetadataWrapper.generateExecutableInsertTableStm(sqlStm,metadata,row);
					statement = this.conn.createStatement();
					recordsAffected = statement.executeUpdate(execInsert);		
					recordsAffected++;
				}
				ret.getLstTables().add(new TableAffected(recordsAffected, tableName));
				EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
			}
		}
		catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
	    } 
		finally	{
			closeHandles(conn, statement, null);
		}
		return ret;
	}
	
	
	public EmbeddedDbRecord 
	createEmptyTable(	final String schemaName,
						final String dbName,
						final String fileName,
						final long userId,
						final List<TableDefinition> tableDefinitions,
						final String comment) throws Exception	{
		EmbeddedDbRecord embeddedDbRecord = new EmbeddedDbRecord(-1, fileName, "H2", userId, -1, /*clusterId*/ "", "");
		
		
		try	{
			for(TableDefinition t: tableDefinitions) {
				addUserTables(t.getTableName());
				t.setTableScript(SqlMetadataWrapper.createRdbmsTableStm(t)); 
				
				boolean isCreated = createUserTable(t.getTableScript());
				String sqlStm = "";
				if(isCreated) {
					sqlStm = SqlMetadataWrapper.generateInsertTableStm(t, schemaName);
					t.setTableInsert(sqlStm);
				}
				
				embeddedDbRecord.addTableDefinitions(t);
			}
		}
		catch(SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
	    } 
		
		return embeddedDbRecord;
	}
	
	
	public EmbeddedDbRecord 
	createEmptyDB(	final String schemaName,
					final String dbName,
					final String fileName,
					final long userId) throws Exception	{
		EmbeddedDbRecord embeddedDbRecord = new EmbeddedDbRecord(-1, fileName, "H2", userId, -1, /*clusterId*/ "", "");
		create__userTable__();
		EmbeddedWrapper.addInMemEmbeddedQuery(this, "Client request to create an empty DB");
		return embeddedDbRecord;
	}
	
	
	
	public DataTransfer 
	insertBulkIntoTable(	final String schemaName,
							final String tableName,
							final List<Map<String, Object>> rows, 
							final Map<String, String> metadata,
							final String comment) throws Exception {
		Statement statement = null;
		DataTransfer ret = new DataTransfer();
		addUserTables(tableName);
		try	{
			String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(metadata, tableName);
			boolean isCreated = createUserTable(createTableStm);
			if(isCreated) {
				String sqlStm = SqlMetadataWrapper.generateInsertTableStm(metadata, schemaName, tableName);
				int recordsAffected = 0;
				for(Map<String, Object> row: rows) {
					String execInsert = SqlMetadataWrapper.generateExecutableInsertTableStm(sqlStm,metadata,row);
					statement = this.conn.createStatement();
					try {
						
						recordsAffected = statement.executeUpdate(execInsert);		
						recordsAffected++;
					} catch(SQLException e)	{
						throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1])) ;
				    } catch(Exception e) {
						throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
				    } 
				}
				ret.getLstTables().add(new TableAffected(recordsAffected, tableName));
			}
			
			EmbeddedWrapper.addInMemEmbeddedQuery(this, comment);
		}
		finally	{
			closeHandles(conn, statement, null);
		}
		return ret;
	}


}

