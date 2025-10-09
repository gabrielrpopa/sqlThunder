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


package com.widescope.rdbmsRepo.database.rdbmsRepository;


import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.*;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQueryList;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.storage.RepoHistoryInterface;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class RdbmsExecutedQueriesRepoDb implements RepoHistoryInterface {

	@Override
	public String getRepoTitle() { return RepoStaticDesc.sqlRepo; }


	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "org.h2.Driver";
	private final String DB_URL_DISK = "jdbc:h2:file:./rdbmsExecutedQueriesDbRepo;MODE=PostgreSQL";

	//  Database credentials
	private final String USER = "sa";
	private final String PASS = "sa";

	// Getters for access wrappers
	public String getDB_URL_DISK() { return DB_URL_DISK; }
	public String getUSER() { return USER; }
	public String getJDBC_DRIVER() { return JDBC_DRIVER; }
	public String getPASS()  { return PASS; }


	public static final String sourceAdhoc = "A";  /*Adhoc backup*/
	public static final String sourceScheduled = "S"; /*AScheduled backup*/

	public
	static
	List<String> sourceList = Arrays.asList(new String[] { sourceAdhoc, sourceScheduled });

	public
	static
	Map<String, String> sourceTypeMap = new HashMap<>();


	private static boolean isSource(String p) {
		return sourceList.contains(p.toUpperCase());
	}

	private static String genCommaSeparatedSourceList() {
		return sourceList.stream().collect(Collectors.joining("', '", "'", "'"));
	}



	public RdbmsExecutedQueriesRepoDb()	{

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


	public static void createDatabase() {
		String fileName = "./rdbmsExecutedQueriesDbRepo.mv.db";
		if(!FileUtilWrapper.isFilePresent(fileName)) {
			try {
				RdbmsExecutedQueriesRepoDb.generateSchema();
				AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "RdbmsExecutedQueriesDbRepo created");
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
			List<String> ddlList = new ArrayList<>();

			ddlList.add(ExecutionGroup.groupTable);
			ddlList.add(ExecutionGroup.groupTableIndex1);
			ddlList.add(ExecutionGroup.createTestGroup);
			ddlList.add(ExecutionGroup.createDefaultWebGroup);

			ddlList.add(ExecutionUserAccess.accessRefTable);
			ddlList.add(ExecutionUserAccess.accessRefTableIndex1);
			ddlList.add(ExecutionUserAccess.accessRefTableConst1);

			ddlList.add(RdbmsExecutedQueriesRepoDb.snapshotDbRef);
			ddlList.add(RdbmsExecutedQueriesRepoDb.executedQueriesTable_index1);
			ddlList.add(RdbmsExecutedQueriesRepoDb.executedQueriesTable_const1);
			ddlList.add(RdbmsExecutedQueriesRepoDb.executedQueriesTable_const2);
			ddlList.add(RdbmsExecutedQueriesRepoDb.executedQueriesTable_const3);
			ddlList.add(RdbmsExecutedQueriesRepoDb.executedQueriesTableFk1);
			ddlList.add(RdbmsExecutedQueriesRepoDb.accessRefTableFk3);




			RdbmsExecutedQueriesRepoDb dataSnapshotDbRepo = new RdbmsExecutedQueriesRepoDb();
			dataSnapshotDbRepo.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}




	private
	static final
    String snapshotDbRef = "CREATE TABLE IF NOT EXISTS executedQueriesTable (id BIGINT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
																		+ "	requestId VARCHAR(MAX), "
																		+ " databaseId BIGINT, "
																		+ " statementId BIGINT, "
																		+ "	statementName VARCHAR(MAX), "
																		+ "	statementType VARCHAR(MAX), "
																		+ "	statement VARCHAR(MAX), "
																		+ " jsonParam VARCHAR(MAX), "
																		+ " dbType VARCHAR(MAX), "             /*see List<String> dbTypes*/
																		+ " src VARCHAR(1) DEFAULT 'A', "   /*A-ADHOC or R-REPO defined in Constants class adhocShort/repoShort */
																		+ " groupId BIGINT, " /* user defined group. reference to groupTable defined in ExecutionGroup class */
																		+ "	userId BIGINT, "
																		+ "	timestamp BIGINT, "
																		+ "	repPath VARCHAR(MAX), "
																		+ " comment VARCHAR(MAX), "
																		+ "	isValid VARCHAR(1) DEFAULT 'N' "
																		+ ")";

	private
	static final
    String executedQueriesTable_index1 = "CREATE INDEX IF NOT EXISTS idx_executedQueriesTable_1 ON executedQueriesTable(databaseId, statementName);";
	
	private
	static final
    String executedQueriesTable_const1 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_1  CHECK (src IN (" + PersistenceSourceList.genCommaSeparatedSourceList() + ") );"; /*A-adhoc, R-repository*/



	private
	static final
    String executedQueriesTable_const2 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_2  CHECK (dbType IN (" +  DbUtil.genCommaSeparatedAllDb() + ") );";

	private
	static final
	String executedQueriesTable_const3 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_3  CHECK (isValid IN ('Y', 'N') );";



	public static
	String executedQueriesTableFk1 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS backupTableFk1 FOREIGN KEY ( groupId ) REFERENCES groupTable( groupId );";

	private
	static final
	String accessRefTableFk3 = "ALTER TABLE accessRefTable ADD CONSTRAINT IF NOT EXISTS executedQueriesAccessRefTableFk_1 FOREIGN KEY ( objectId ) REFERENCES executedQueriesTable( id );";



	private RdbmsExecutedQueryList getRdbmsExecutedQueryList(PreparedStatement preparedStatement) throws SQLException {
		RdbmsExecutedQueryList snapshotDbRecordList = new RdbmsExecutedQueryList();
		try(ResultSet rs = preparedStatement.executeQuery()) {
			while (rs.next()) {
				snapshotDbRecordList.addRdbmsExecutedQuery(new RdbmsExecutedQuery(rs.getLong("id"),
																					rs.getString("requestId"),
																					rs.getLong("databaseId"),
																					rs.getLong("statementId"),
																					rs.getString("statementName"),
																					rs.getString("statementType"),
																					rs.getString("statement"),
																					rs.getString("isValid"),
																					rs.getString("jsonParam"),
																					rs.getString("dbType"),
																					rs.getString("src"),
																					rs.getLong("groupId"),
																					rs.getLong("userId"),
																					rs.getLong("timestamp"),
																					rs.getString("repPath"),
																					rs.getString("comment"),
																					rs.getInt("cntAccess")
						)
				);

			}
		}

		return snapshotDbRecordList;
	}



	private RdbmsExecutedQueryList getRdbmsExecutedQueryListWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		RdbmsExecutedQueryList snapshotDbRecordList = new RdbmsExecutedQueryList();
		try(ResultSet rs = preparedStatement.executeQuery()) {
			while (rs.next()) {
				snapshotDbRecordList.addRdbmsExecutedQuery(new RdbmsExecutedQuery(rs.getLong("id"),
																					rs.getString("requestId"),
																					rs.getLong("databaseId"),
																					rs.getLong("statementId"),
																					rs.getString("statementName"),
																					rs.getString("statementType"),
																					rs.getString("statement"),
																					rs.getString("isValid"),
																					rs.getString("jsonParam"),
																					rs.getString("dbType"),
																					rs.getString("src"),
																					rs.getLong("groupId"),
																					rs.getLong("userId"),
																					rs.getLong("timestamp"),
																					rs.getString("repPath"),
																					rs.getString("comment"),
																					-1
						)
				);

			}
		}

		return snapshotDbRecordList;
	}

	private RdbmsExecutedQuery getRdbmsExecutedQuery(PreparedStatement preparedStatement) throws SQLException {
		RdbmsExecutedQuery snapshotDbRecord = new RdbmsExecutedQuery();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				snapshotDbRecord = new RdbmsExecutedQuery(	rs.getLong("id"),
															rs.getString("requestId"),
															rs.getLong("databaseId"),
															rs.getLong("statementId"),
															rs.getString("statementName"),
															rs.getString("statementType"),
															rs.getString("statement"),
															rs.getString("isValid"),
															rs.getString("jsonParam"),
															rs.getString("dbType"),
															rs.getString("src"),
															rs.getLong("groupId"),
															rs.getLong("userId"),
															rs.getLong("timestamp"),
															rs.getString("repPath"),
															rs.getString("comment"),
															rs.getInt("cntAccess")

				);
			}
		}
		return snapshotDbRecord;
	}


	private RdbmsExecutedQuery getRdbmsExecutedQueryWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		RdbmsExecutedQuery snapshotDbRecord = new RdbmsExecutedQuery();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				snapshotDbRecord = new RdbmsExecutedQuery(	rs.getLong("id"),
															rs.getString("requestId"),
															rs.getLong("databaseId"),
															rs.getLong("statementId"),
															rs.getString("statementName"),
															rs.getString("statementType"),
															rs.getString("statement"),
															rs.getString("isValid"),
															rs.getString("jsonParam"),
															rs.getString("dbType"),
															rs.getString("src"),
															rs.getLong("groupId"),
															rs.getLong("userId"),
															rs.getLong("timestamp"),
															rs.getString("repPath"),
															rs.getString("comment"),
															-1

				);
			}
		}
		return snapshotDbRecord;
	}

	private final String selectStr = "SELECT d.id, " +
											"d.requestId, " +
											"d.databaseId, " +
											"d.statementId, " +
											"d.statementName, " +
											"d.statementType, " +
											"d.statement, " +
											"d.isValid, " +
											"d.jsonParam, " +
											"d.dbType, " +
											"d.src, " +
											"d.groupId, " +
											"d.userId, " +
											"d.timestamp, " +
											"d.repPath, " +
											"d.comment ";
	private final String cntColumn = ", (SELECT SUM(userId) FROM accessRefTable WHERE userId = ?) AS cntAccess ";
	private final String fromTable = " FROM executedQueriesTable d ";
	private final String fromTables = " FROM executedQueriesTable d JOIN accessRefTable a ON d.userId = a.userId ";


	public RdbmsExecutedQueryList
	getAllStatementsByUser(final long userId, final long fromDateTime, final long toDateTime) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.timestamp >= ? AND d.timestamp <= ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, fromDateTime);
			preparedStatement.setLong(4, toDateTime);
			return getRdbmsExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQueryList
	getAllStatementsByUser(final long userId, final long fromDateTime, final long toDateTime, final String statement) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.timestamp >= ? AND d.timestamp <= ? AND d.statement LIKE ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, fromDateTime);
			preparedStatement.setLong(4, toDateTime);
			preparedStatement.setString(5, '%' + statement + '%');
			return getRdbmsExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQueryList
	getAllStatementsByUser(final long userId, final String statement) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.statement LIKE ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, '%' + statement + '%');
			return getRdbmsExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public RdbmsExecutedQueryList
	getStatementsByName(final String statementName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + fromTable + " WHERE d.statementName like ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, "%" + statementName + "%");
			return getRdbmsExecutedQueryListWithoutCnt(preparedStatement);
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
			return getRdbmsExecutedQueryListWithoutCnt(preparedStatement).getRdbmsExecutedQueryList().size() == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public RdbmsExecutedQueryList
	getStatementsByName(final String statementName, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables + " WHERE d.statementName like ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setString(2, "%" + statementName + "%");
			return getRdbmsExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQueryList
	getUserExecutedStatements(final long userId, final String statementName, final String src) throws Exception {
		String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.statementName = ? AND d.src = ? ORDER BY d.timestamp DESC";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, '%' + statementName + '%');
			preparedStatement.setString(4, src);
			return getRdbmsExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQueryList
	getAllExecutedStatementsByUser(final long userId) throws Exception {
		String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? " ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			return getRdbmsExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}





	public RdbmsExecutedQuery
	getLastUserStatement(final long userId, final long groupId, final String source) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.groupId = ? AND d.src = ? ORDER BY timestamp DESC LIMIT 1";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, userId);
			preparedStatement.setLong(4, groupId);
			preparedStatement.setString(5, source);
			return getRdbmsExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public RdbmsExecutedQuery
	getStatementById(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + fromTable + " WHERE d.id = ? " ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
			return getRdbmsExecutedQueryWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQuery
	getStatementById(final long id, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + cntColumn + fromTables + " WHERE d.id = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, id);
			return getRdbmsExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQuery
	getStatementByRepPath(final String repPath) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + fromTable + " WHERE d.repPath = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, repPath);
			return getRdbmsExecutedQueryWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public RdbmsExecutedQuery
	getStatementByRepPath(final String repPath, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectStr + cntColumn + fromTables + " WHERE d.repPath = ? AND d.userId = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setString(2, repPath);
			preparedStatement.setLong(3, userId);
			return getRdbmsExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	/**
	 * Used to uniquely identify a record.
	 */
	public RdbmsExecutedQuery
	identifyStatement(final String requestId, final long timeStamp) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr +  fromTable + " WHERE d.requestId = ? AND d.timestamp = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, requestId);
			preparedStatement.setLong(1, timeStamp);
			return getRdbmsExecutedQueryWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public List<Long>
	getUserStatementId(final long userId) throws Exception {
		List<Long> ret = new ArrayList<>();
		String select = "SELECT id FROM executedQueriesTable WHERE userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{

			preparedStatement.setLong(1, userId);

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



	
	public
	void
	addExecutedStatement(final RdbmsExecutedQuery query)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "INSERT INTO executedQueriesTable ( requestId, statementId, statementName, statementType, statement, isValid, jsonParam, dbType, src, groupId, userId, timestamp, repPath, comment) " +
							" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, query.getRequestId());
			preparedStatement.setLong(2, query.getStatementId());
			preparedStatement.setString(3, query.getStatementName());
			preparedStatement.setString(4, query.getStatementType());
			preparedStatement.setString(5, query.getStatement());
			preparedStatement.setString(6, query.getIsValid());
			preparedStatement.setString(7, query.getJsonParam());
			preparedStatement.setString(8, query.getDbType());
			preparedStatement.setString(9, query.getSource());
			preparedStatement.setLong(10, query.getGroupId());
			preparedStatement.setLong(11, query.getUserId());
			preparedStatement.setLong(12, query.getTimestamp());
			preparedStatement.setString(13, query.getRepPath());
			preparedStatement.setString(14, query.getComment());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public void
	deleteStatementById(final long id) throws Exception {
		if(id <= 0)	throw new Exception("Statement Id is negative");
		String deleteDslParam = "DELETE executedQueriesTable WHERE id = ?";
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

	/*Delete execution by owner */
	public void
	deleteStatementByIdAndUser(final long id, final long userId) throws Exception {
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

	/*Compound access functions*/

	public RdbmsExecutedQuery
	deleteExecutedStatementAccess(final long id, final long userId) throws Exception {
		RdbmsExecutedQuery ret = getStatementById(id); /*Get the record before potentially deleting it*/
		long countUsers = ExecutionUserAccess.countArtifactAccess(id, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		ExecutionUserAccess.deleteArtifactAccess(id, userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		ret.setFlag(ExecutedStatementFlag.accessRecordDeleted);
		if(countUsers == 1 && ret.getUserId() == userId) {
			deleteStatementById(id);
			ret.setFlag(ExecutedStatementFlag.executionRecordDeleted);
		}
		return ret;
	}


	/**
	 *  Deletes all execution and associated access to users to own execution scripts
	 */
	public List<RdbmsExecutedQuery>
	deleteExecutedStatement(final List<Long> idList, final long userId, final boolean force) throws Exception {
		List<RdbmsExecutedQuery> ret = new ArrayList<>();
		for (Long executionId :  idList) {
			RdbmsExecutedQuery rec = getStatementById(executionId); /*Get the record before potentially deleting it*/
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
	public List<RdbmsExecutedQuery>
	deleteAllExecutedStatementByUserId(final long userId) throws Exception {
		List<RdbmsExecutedQuery> ret = new ArrayList<>();
		ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		List<Long> lst = getUserStatementId(userId);
		for (Long executionId :  lst) {
			RdbmsExecutedQuery rec = getStatementById(executionId);
			if(rec.getUserId() == userId) {
				deleteStatementByIdAndUser(executionId, userId);
				ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
			}
			ret.add(rec);
		}
		return ret;
	}






	public static void
	testRdbmsExecutedQueriesDb() {
		try {
			final String pathF = "c:/f.txt";
			final String statementName = "statementNameTest";
			final long userId = -1L;
			final String statement = "SELECT col1 FROM table1";
			RdbmsExecutedQueriesRepoDb d = new RdbmsExecutedQueriesRepoDb();
			RdbmsExecutedQueriesRepoDb.createDatabase();

			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroupList pgList = ExecutionGroup.getArtifactGroups("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroup pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.deleteArtifactGroup(pg.getGroupId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);


			RdbmsExecutedQuery q = new RdbmsExecutedQuery(-1L, "####", -1L, -1L, statementName, "N", Constants.RDBMS_DDL, statement, "", DbUtil.h2, Constants.adhocShort, pg.getGroupId(), userId, 0L, pathF, "c", -1);
			d.addExecutedStatement(q);
			q = d.getStatementByRepPath(pathF);
			ExecutionUserAccess.addArtifactAccess(q.getId(), userId, PersistencePrivilege.pTypeAdmin,  d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			long cnt = ExecutionUserAccess.countArtifactAccess(q.getId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			System.out.println(cnt);



			RdbmsExecutedQuery qR2 = d.getStatementByRepPath(pathF, userId);
			RdbmsExecutedQuery qR1 = d.getStatementById(q.getId());
			RdbmsExecutedQuery qR3 = d.getStatementById(q.getId(), userId);
			RdbmsExecutedQueryList qRList1 = d.getStatementsByName(statementName);
			RdbmsExecutedQueryList qRList2 = d.getStatementsByName(statementName, userId);
			RdbmsExecutedQueryList lst = d.getAllStatementsByUser(userId, 0, DateTimeUtils.millisecondsSinceEpoch());
			System.out.println(lst.toString());
			lst = d.getAllStatementsByUser(userId, 0, DateTimeUtils.millisecondsSinceEpoch(), statement);
			System.out.println(lst.toString());
			lst = d.getAllStatementsByUser(userId, statement);
			System.out.println(lst.toString());
			RdbmsExecutedQuery last = d.getLastUserStatement(userId, q.getGroupId(), Constants.adhocShort);
			System.out.println(last.toString());
			q = d.getStatementById(q.getId());
			System.out.println(q.toString());
			q =  d.deleteExecutedStatementAccess(q.getId(), userId);
			System.out.println(q.toString());
		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
		}

	}

}

