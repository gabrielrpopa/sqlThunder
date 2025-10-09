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


package com.widescope.rdbmsRepo.database.mongodb.repo;

/*
 * Mongo Query Statement History Database
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.*;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.storage.RepoHistoryInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/*Mongo Statement Execution History Database*/

@Component
public class MongoExecutedQueriesRepoDb implements RepoHistoryInterface {

	@Override
	public String getRepoTitle() { return RepoStaticDesc.mongoRepo; }


	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";
	private final String DB_URL_DISK = "jdbc:h2:file:./mongoExecutedQueriesRepoDb;MODE=PostgreSQL";

	//  Database credentials 
	private final String USER = "sa";
	private final String PASS = "sa";

	// Getters for access wrappers
	public String getDB_URL_DISK() { return DB_URL_DISK; }
	public String getUSER() { return USER; }
	public String getJDBC_DRIVER() { return JDBC_DRIVER; }
	public String getPASS()  { return PASS; }

	public MongoExecutedQueriesRepoDb()	{}
	
		   
	
	private 
	void 
	closeHandles(	Connection conn, 
					Statement statement, 
					ResultSet rs){
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}


	public static void createDatabase() {
		String fileName = "./mongoExecutedQueriesRepoDb.mv.db";
		if(!FileUtilWrapper.isFilePresent(fileName)) {
			try {
				MongoExecutedQueriesRepoDb.generateSchema();
				AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "MongoExecutedQueriesRepoDb created");
			} catch(Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}
	}
	

	private 
	void
	createSchema(final List<String> ddlList) throws Exception	{
		Connection conn = null;
		Statement statement = null; 
		try { 
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
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
			final List<String> ddlList = getStrings();
			MongoExecutedQueriesRepoDb mongoExecutedQueriesRepoDb = new MongoExecutedQueriesRepoDb();
			mongoExecutedQueriesRepoDb.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}


	}

	@NotNull
	private static List<String> getStrings() {
		List<String> ddlList = new java.util.ArrayList<>();

		ddlList.add(ExecutionGroup.groupTable);
		ddlList.add(ExecutionGroup.groupTableIndex1);
		ddlList.add(ExecutionGroup.createTestGroup);
		ddlList.add(ExecutionGroup.createDefaultWebGroup);

		ddlList.add(ExecutionUserAccess.accessRefTable);
		ddlList.add(ExecutionUserAccess.accessRefTableIndex1);
		ddlList.add(ExecutionUserAccess.accessRefTableConst1);

		ddlList.add(MongoExecutedQueriesRepoDb.executedQueriesTable);
		ddlList.add(MongoExecutedQueriesRepoDb.executedQueriesTable_index2);
		ddlList.add(MongoExecutedQueriesRepoDb.mongoExecutedQueriesTable_const1);
		ddlList.add(MongoExecutedQueriesRepoDb.mongoExecutedQueriesTable_const2);
		ddlList.add(MongoExecutedQueriesRepoDb.executedQueriesTableFk1);
		ddlList.add(MongoExecutedQueriesRepoDb.accessRefTableFk);

		return ddlList;
	}






	public static final String
	executedQueriesTable = "CREATE TABLE IF NOT EXISTS executedQueriesTable(id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY, " +
																				"requestId VARCHAR(MAX), " +
																				"statementId BIGINT, " +
																				"statementName VARCHAR(MAX), " +
																				"statement VARCHAR(MAX), " +
																				"statementType VARCHAR(MAX), " +
																				"jsonParam VARCHAR(MAX), " +
																				"clusterId BIGINT, " +
																				"database VARCHAR(MAX), " +
																				"collection VARCHAR(MAX), " +
																				"groupId BIGINT, " +    /* user defined group. reference to groupTable defined in ExecutionGroup class */
																				"src VARCHAR(MAX) DEFAULT 'A', " +   /*A-ADHOC or R-REPO defined in Constants class adhocShort/repoShort */
																				"userId BIGINT, " +
																				"repPath VARCHAR(MAX), " +
																				"comment VARCHAR(MAX), " +
																				"timestamp BIGINT, " +
																				"isValid VARCHAR(1) DEFAULT 'N' ) ";






	/*A-ADHOC or R-REPO*/
	public static final
	String mongoExecutedQueriesTable_const1 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_1 "
			+ "CHECK (src IN (" + PersistenceSourceList.genCommaSeparatedSourceList() + ") );";  /*A-adhoc, R-repository*/

	private static final
	String mongoExecutedQueriesTable_const2 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_2  CHECK (isValid IN ('Y', 'N') );";

	public static final String
	executedQueriesTable_index2 = "CREATE INDEX IF NOT EXISTS  idx_executedQueriesTable_2 ON executedQueriesTable(userId, timestamp);";

	public static
	String executedQueriesTableFk1 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS backupTableFk1 FOREIGN KEY ( groupId ) REFERENCES groupTable( groupId );";


	public static final
	String accessRefTableFk = "ALTER TABLE accessRefTable ADD CONSTRAINT IF NOT EXISTS accessRefTableFk1 FOREIGN KEY ( objectId ) REFERENCES executedQueriesTable( id );";


	private MongoExecutedQuery getMongoExecutedRecordWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		MongoExecutedQuery ret = new MongoExecutedQuery(-1, -1);
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				ret = new MongoExecutedQuery(rs.getLong("id"),
											rs.getString("requestId"),
											rs.getLong("statementId"),
											rs.getString("statementName"),
											rs.getString("statementType"),
											rs.getString("statement"),
											rs.getString("isValid"),
											rs.getString("jsonParam"),
											rs.getLong("clusterId"),
											rs.getString("database"),
											rs.getString("collection"),
											rs.getLong("groupId"),
											rs.getString("src"),
											rs.getLong("userId"),
											rs.getString("repPath"),
											rs.getString("comment"),
											rs.getLong("timestamp"),
											-1
				);

			}
		}

		return ret;
	}

	private MongoExecutedQuery getMongoExecutedRecord(PreparedStatement preparedStatement, final long userId) throws SQLException {
		MongoExecutedQuery ret = new MongoExecutedQuery(-1, userId);
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				ret = new MongoExecutedQuery(rs.getLong("id"),
											rs.getString("requestId"),
											rs.getLong("statementId"),
											rs.getString("statementName"),
											rs.getString("statementType"),
											rs.getString("statement"),
											rs.getString("isValid"),
											rs.getString("jsonParam"),
											rs.getLong("clusterId"),
											rs.getString("database"),
											rs.getString("collection"),
											rs.getLong("groupId"),
											rs.getString("src"),
											rs.getLong("userId"),
											rs.getString("repPath"),
											rs.getString("comment"),
											rs.getLong("timestamp"),
											rs.getInt("cntAccess")
				);

			}
		}

		return ret;
	}





	private MongoExecutedQueryList getMongoExecutedRecordList(PreparedStatement preparedStatement) throws SQLException {
		MongoExecutedQueryList ret = new MongoExecutedQueryList();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			while (rs.next()) {
				MongoExecutedQuery r = new MongoExecutedQuery(rs.getLong("id"),
																rs.getString("requestId"),
																rs.getLong("statementId"),
																rs.getString("statementName"),
																rs.getString("statementType"),
																rs.getString("statement"),
																rs.getString("isValid"),
																rs.getString("jsonParam"),
																rs.getLong("clusterId"),
																rs.getString("database"),
																rs.getString("collection"),
																rs.getLong("groupId"),
																rs.getString("src"),
																rs.getLong("userId"),
																rs.getString("repPath"),
																rs.getString("comment"),
																rs.getLong("timestamp"),
																rs.getInt("cntAccess")
				);
				ret.addMongoExecutedQuery(r);
			}
		}
		return ret;
	}


	private MongoExecutedQueryList getMongoExecutedRecordListWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		MongoExecutedQueryList ret = new MongoExecutedQueryList();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			while (rs.next()) {
				MongoExecutedQuery r = new MongoExecutedQuery(	rs.getLong("id"),
																rs.getString("requestId"),
																rs.getLong("statementId"),
																rs.getString("statementName"),
																rs.getString("statementType"),
																rs.getString("statement"),
																rs.getString("isValid"),
																rs.getString("jsonParam"),
																rs.getLong("clusterId"),
																rs.getString("database"),
																rs.getString("collection"),
																rs.getLong("groupId"),
																rs.getString("src"),
																rs.getLong("userId"),
																rs.getString("repPath"),
																rs.getString("comment"),
																rs.getLong("timestamp"),
																-1
				);
				ret.addMongoExecutedQuery(r);
			}
		}
		return ret;
	}


	private final String selectStr = "SELECT d.id, " +
											"d.requestId, " +
											"d.statementId, " +
											"d.statementName," +
											"d.statementType, " +
											"d.statement, " +
											"isValid, " +
											"d.jsonParam, " +
											"d.clusterId, " +
											"d.database, " +
											"d.collection, " +
											"d.groupId, " +
											"d.src, " +
											"d.userId, " +
											"d.repPath, " +
											"d.comment, " +
											"d.timestamp ";
	private final String cntColumn = ", (SELECT SUM(userId) FROM accessRefTable WHERE userId = ?) AS cntAccess ";
	private final String fromTables = " FROM executedQueriesTable d JOIN accessRefTable a ON d.userId = a.userId ";
	private final String fromTable = " FROM executedQueriesTable d ";


	public MongoExecutedQueryList
	getAllStatementsByUser(final long userId, final long fromDateTime, final long toDateTime) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.timestamp >= ? AND d.timestamp <= ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, fromDateTime);
			preparedStatement.setLong(4, toDateTime);
			return getMongoExecutedRecordList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public MongoExecutedQueryList
	getAllStatementsByUser(final long userId, final long fromDateTime, final long toDateTime, final String statement) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.timestamp >= ? AND d.timestamp <= ? AND d.statement LIKE ? " ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, fromDateTime);
			preparedStatement.setLong(4, toDateTime);
			preparedStatement.setString(5, '%' + statement + '%');
			return getMongoExecutedRecordList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public MongoExecutedQueryList
	getAllStatementsByUser(final long userId, final String statement) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.statement LIKE ? " ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, '%' + statement + '%');
			return getMongoExecutedRecordList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public MongoExecutedQueryList
	getStatementByName(final String statementName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + fromTable + " WHERE d.statementName = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, statementName);
			return getMongoExecutedRecordList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	public boolean
	isStatementName(final String statementName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + fromTable + " WHERE d.statementName = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, "%" + statementName + "%");
			return getMongoExecutedRecordListWithoutCnt(preparedStatement).getMongoExecutedQueryLst().size() == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public MongoExecutedQueryList
	getUserExecutedStatements(final long userId, final String statementName, final String src) throws Exception {
		String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.statementName = ? AND d.src = ? ORDER BY d.timestamp DESC";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, '%' + statementName + '%');
			preparedStatement.setString(4, src);
			return getMongoExecutedRecordList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public MongoExecutedQueryList
	getAllExecutedStatementsByUser(final long userId) throws Exception {
		String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? " ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			return getMongoExecutedRecordList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	public MongoExecutedQuery
	getLastUserStatement(final long userId, final long groupId, final String src) throws Exception {
		String select = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.groupId = ? AND d.src = ? ORDER BY timestamp DESC LIMIT 1";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setString(4, src);
			return getMongoExecutedRecord(preparedStatement, userId);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public MongoExecutedQuery
	getStatementByRepPath(final String repPath) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + fromTable + " WHERE d.repPath = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, repPath);
			return getMongoExecutedRecordWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public MongoExecutedQuery
	getStatementById(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + fromTable + " WHERE d.id = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
			return getMongoExecutedRecordWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	/**
	 * Used to uniquely identify a record.
	 */
	public MongoExecutedQuery
	identifyStatement(final String requestId, final long timeStamp) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr +  fromTable + " WHERE d.requestId = ? AND d.timestamp = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, requestId);
			preparedStatement.setLong(1, timeStamp);
			return getMongoExecutedRecordWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public List<Long>
	getUserStatementId(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		List<Long> ret = new ArrayList<>();
		String select = "SELECT id FROM executedQueriesTable WHERE userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{

			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				ret.add(rs.getLong("id")) ;
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
	addExecutedStatement(MongoExecutedQuery m) throws Exception {
		addExecutedStatement(	m.getRequestId(),
								m.getStatementId(),
								m.getStatementName(),
								m.getStatementType(),
								m.getStatement(),
								m.getIsValid(),
								m.getJsonParam(),
								m.getClusterId(),
								m.getDatabase(),
								m.getCollection(),
								m.getGroupId(),
								m.getSource(),
								m.getUserId(),
								m.getRepPath(),
								m.getComment(),
								m.getTimestamp());
	}
	
	public void
	addExecutedStatement(final String requestId,
						 final long statementId,
						 final String statementName,
						 final String statementType,
						 final String statement,
						 final String isValid,
						 final String jsonParam,
						 final long clusterId,
						 final String database,
						 final String collection,
						 final long groupId,
						 final String source,
						 final long userId,
						 final String repPath,
						 final String comment,
						 final long timestamp) throws Exception {

 		String addMql = "INSERT INTO executedQueriesTable(requestId, statementId, statementName, statementType, statement, isValid, jsonParam, clusterId, database, collection, groupId, src, userId, repPath, comment, timestamp) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(addMql))	{
			preparedStatement.setString(1, requestId);
			preparedStatement.setLong(2, statementId);
			preparedStatement.setString(3, statementName);
			preparedStatement.setString(4, statementType);
			preparedStatement.setString(5, statement);
			preparedStatement.setString(6, isValid);
			preparedStatement.setString(7, jsonParam);
			preparedStatement.setLong(8, clusterId);
			preparedStatement.setString(9, database);
			preparedStatement.setString(10, collection);
			preparedStatement.setLong(11, groupId);
			preparedStatement.setString(12, source);
			preparedStatement.setLong(13, userId);
			preparedStatement.setString(14, repPath);
			preparedStatement.setString(15, comment);
			preparedStatement.setLong(16, timestamp);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}




	
	public void
	deleteStatement(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String deleteMqlParam = "DELETE executedQueriesTable WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteMqlParam))	{
			
			preparedStatement.setLong(1, id);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	/*Delete execution by owner */
	public void
	deleteStatementByIdAndUser(final long id, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String deleteDslParam = "DELETE executedQueriesTable WHERE id = ? and userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
			preparedStatement.setLong(1, id);
			preparedStatement.setLong(2, userId);
			preparedStatement.execute();
		} catch (SQLException e) {
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public void
	deleteStatementById(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String deleteDslParam = "DELETE executedQueriesTable WHERE id = ? ";
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


	/*Compound access functions*/

	public MongoExecutedQuery
	deleteExecutedStatementAccess(final long executionId, final long userId) throws Exception {
		MongoExecutedQuery ret = getStatementById(executionId); /*Get the record before potentially deleting it*/
		long countUsers = ExecutionUserAccess.countArtifactAccess(executionId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		ExecutionUserAccess.deleteArtifactAccess(executionId, userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		ret.setFlag(ExecutedStatementFlag.accessRecordDeleted);
		if(countUsers == 1 && ret.getUserId() == userId) {
			deleteStatementByIdAndUser(executionId, userId);
			ret.setFlag(ExecutedStatementFlag.executionRecordDeleted);
		}
		return ret;
	}


	/**
	 *  Deletes all execution and associated access to users to own execution scripts
	 */
	public List<MongoExecutedQuery>
	deleteExecutedStatement(final List<Long> executionIdList, final long userId, final boolean force) throws Exception {
		List<MongoExecutedQuery> ret = new ArrayList<>();
		for (Long executionId :  executionIdList) {
			MongoExecutedQuery rec = getStatementById(executionId); /*Get the record before potentially deleting it*/
			long countUsers = ExecutionUserAccess.countArtifactAccess(executionId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
			if(rec.getUserId() == userId) {
				if(countUsers == 1 || force) {
					deleteStatementByIdAndUser(executionId, userId);
					ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
					rec.setFlag(ExecutedStatementFlag.accessRecordDeleted);
				}
			}
			ret.add(rec);
		}
		return ret;
	}


	/*Blunt operation to clean up database for the owner of multiple executions. The operation deletes ALL executions of the owner */
	public List<MongoExecutedQuery>
	deleteAllExecutedStatementByUserId(final long userId) throws Exception {
		List<MongoExecutedQuery> ret = new ArrayList<>();
		ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		List<Long> lst = getUserStatementId(userId);
		for (Long executionId :  lst) {
			MongoExecutedQuery rec = getStatementById(executionId);
			if(rec.getUserId() == userId) {
				deleteStatementByIdAndUser(executionId, userId);
				ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
			}
			ret.add(rec);
		}
		return ret;
	}

	public static void
	testMongoExecutedQueriesDb() {
		try {
			final String pathF = "c:/f.txt";
			final long userId = -1L;
			final long clusterId = -1L;
			final long groupId = -1L;
			final String statement = "{}";
			MongoExecutedQueriesRepoDb.createDatabase();
			MongoExecutedQueriesRepoDb d = new MongoExecutedQueriesRepoDb();

			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroupList pgList = ExecutionGroup.getArtifactGroups("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroup pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.deleteArtifactGroup(pg.getGroupId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);

			MongoExecutedQuery q = new MongoExecutedQuery(-1L, "####", -1L, "!", "MQL", statement, "Y", "", clusterId, "db1", "col1", groupId, Constants.adhocShort, userId, pathF, "c", 0L, -1);

			d.addExecutedStatement(q);
			MongoExecutedQuery qR = d.getStatementByRepPath(pathF);
			ExecutionUserAccess.addArtifactAccess(qR.getId(), userId, PersistencePrivilege.pTypeAdmin, d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			long cnt = ExecutionUserAccess.countArtifactAccess(qR.getId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			System.out.println(cnt);
			MongoExecutedQueryList lst = d.getAllStatementsByUser(userId, 0, DateTimeUtils.millisecondsSinceEpoch());
			System.out.println(lst.toString());
			lst = d.getAllStatementsByUser(userId, 0, DateTimeUtils.millisecondsSinceEpoch(), statement);
			System.out.println(lst.toString());
			lst = d.getAllStatementsByUser(userId, statement);
			System.out.println(lst.toString());
			MongoExecutedQuery last = d.getLastUserStatement(userId, groupId, Constants.adhocShort);
			System.out.println(last.toString());
			qR = d.getStatementById(qR.getId());
			System.out.println(qR.toString());
			q = d.deleteExecutedStatementAccess(qR.getId(), userId);
			System.out.println(q.toString());
		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
		}

	}


}

