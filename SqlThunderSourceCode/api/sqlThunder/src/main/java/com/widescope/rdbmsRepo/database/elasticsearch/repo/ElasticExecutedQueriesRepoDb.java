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
import java.util.ArrayList;
import java.util.List;


import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.*;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.scripting.db.ScriptExecutedRecord;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.storage.RepoHistoryInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/*Elasticsearch Statement Execution History Database*/

@Component
public class ElasticExecutedQueriesRepoDb implements RepoHistoryInterface {

	@Override
	public String getRepoTitle() { return RepoStaticDesc.elasticRepo; }



	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./elasticExecutedQueriesRepoDb;MODE=PostgreSQL";  
	
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa";

	// Getters for access wrappers
	public String getDB_URL_DISK() { return DB_URL_DISK; }
	public String getUSER() { return USER; }
	public String getJDBC_DRIVER() { return JDBC_DRIVER; }
	public String getPASS()  { return PASS; }
	
	public ElasticExecutedQueriesRepoDb()	{
		/*


		storageTypeMap.put(storageAiModel, "Ai Models");
		storageTypeMap.put(storageAiOperation, "Ai Operation");
		storageTypeMap.put(storageFileBackup, "File System Backup");


		*/
	}

	public static void createDatabase() {
		String fileName = "./elasticExecutedQueriesRepoDb.mv.db";
		if(!FileUtilWrapper.isFilePresent(fileName)) {
			try {
				ElasticExecutedQueriesRepoDb.generateSchema();
				AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ElasticExecutedQueriesRepoDb created");
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
			DbUtil.closeHandles(conn, statement, null);
	    } 
	}

	
		

	
	
	public 
	static 
	void generateSchema() throws Exception {
		try {
			final List<String> ddlList = getStrings();
			ElasticExecutedQueriesRepoDb executedQueriesRepoDb = new ElasticExecutedQueriesRepoDb();
			executedQueriesRepoDb.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}

	@NotNull
	private static List<String> getStrings() {
		List<String> ddlList = new ArrayList<  >();

		ddlList.add(ExecutionGroup.groupTable);
		ddlList.add(ExecutionGroup.groupTableIndex1);
		ddlList.add(ExecutionGroup.createTestGroup);
		ddlList.add(ExecutionGroup.createDefaultWebGroup);

		ddlList.add(ExecutionUserAccess.accessRefTable);
		ddlList.add(ExecutionUserAccess.accessRefTableIndex1);
		ddlList.add(ExecutionUserAccess.accessRefTableConst1);

		ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesTable);
		ddlList.add(ElasticExecutedQueriesRepoDb.mongoExecutedQueriesTable_const1);
		ddlList.add(ElasticExecutedQueriesRepoDb.mongoExecutedQueriesTable_const2);
		ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesTable_index1);
		ddlList.add(ElasticExecutedQueriesRepoDb.executedQueriesTableFk3);
		ddlList.add(ElasticExecutedQueriesRepoDb.accessRefTableFk);

		return ddlList;
	}


	public static final String
	executedQueriesTable = "CREATE TABLE IF NOT EXISTS "
							+ " executedQueriesTable (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY, "
			                                        + "requestId VARCHAR(MAX), "
													+ "statementId BIGINT, "
													+ "statementName VARCHAR(MAX), "
													+ "statementType VARCHAR(MAX), "   /*SQL or DSL*/
                                                    + "statement VARCHAR(MAX), "
													+ "isValid VARCHAR(1) DEFAULT 'N', "
                                                    + "jsonParam VARCHAR(MAX), "
													+ "groupId BIGINT, " /* user defined group. reference to groupTable defined in ExecutionGroup class */
													+ "src VARCHAR(1) DEFAULT 'A', "  /*A-ADHOC or R-REPO defined in Constants class adhocShort/repoShort */
													+ "userId BIGINT, "
													+ "clusterId BIGINT, "
													+ "httpVerb VARCHAR(MAX), "
													+ "elasticApi VARCHAR(MAX), "
													+ "indexName VARCHAR(MAX), "
													+ "endPoint VARCHAR(MAX), "
													+ "isOriginalFormat VARCHAR(1), "
													+ "comment VARCHAR(MAX), "
													+ "repPath VARCHAR(MAX), "
													+ "timestamp BIGINT )";

	/*A-ADHOC or R-REPO*/
	public static final
	String mongoExecutedQueriesTable_const1 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_1 "
			+ "CHECK (src IN (" + PersistenceSourceList.genCommaSeparatedSourceList() + ") );";  /*A-adhoc, R-repository*/

	private
	static final
	String mongoExecutedQueriesTable_const2 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS ck_executedQueriesTable_2  CHECK (isValid IN ('Y', 'N') );";

	public static final
	String executedQueriesTable_index1 = "CREATE INDEX IF NOT EXISTS idx_executedQueriesTable_2 ON executedQueriesTable(userId);";


	public static
	String executedQueriesTableFk3 = "ALTER TABLE executedQueriesTable ADD CONSTRAINT IF NOT EXISTS backupTableFk1 FOREIGN KEY ( groupId ) REFERENCES groupTable( groupId );";


	public static
	String accessRefTableFk = "ALTER TABLE accessRefTable ADD CONSTRAINT IF NOT EXISTS accessRefTableFk1 FOREIGN KEY ( objectId ) REFERENCES executedQueriesTable( id );";



	private ElasticExecutedQuery getElasticExecutedQuery(PreparedStatement preparedStatement) throws SQLException {
		ElasticExecutedQuery snapshotDbRecord = new ElasticExecutedQuery();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				snapshotDbRecord = new ElasticExecutedQuery(rs.getLong("id"),
															rs.getString("requestId"),
															rs.getLong("statementId"),
															rs.getString("statementName"),
															rs.getString("statementType"),
															rs.getString("statement"),
															rs.getString("isValid"),
															rs.getString("jsonParam"),
															rs.getLong("groupId"),
															rs.getString("src"),
															rs.getLong("userId"),
															rs.getLong("clusterId"),
															rs.getString("httpVerb"),
															rs.getString("elasticApi"),
															rs.getString("indexName"),
															rs.getString("endPoint"),
															rs.getString("isOriginalFormat"),
															rs.getString("comment"),
															rs.getString("repPath"),
															rs.getLong("timestamp"),
															rs.getInt("cntAccess")

				);
			}
		}
		return snapshotDbRecord;
	}


	private ElasticExecutedQuery getElasticExecutedQueryWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		ElasticExecutedQuery snapshotDbRecord = new ElasticExecutedQuery();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				snapshotDbRecord = new ElasticExecutedQuery(rs.getLong("id"),
															rs.getString("requestId"),
															rs.getLong("statementId"),
															rs.getString("statementName"),
															rs.getString("statementType"),
															rs.getString("statement"),
															rs.getString("isValid"),
															rs.getString("jsonParam"),
															rs.getLong("groupId"),
															rs.getString("src"),
															rs.getLong("userId"),
															rs.getLong("clusterId"),
															rs.getString("httpVerb"),
															rs.getString("elasticApi"),
															rs.getString("indexName"),
															rs.getString("endPoint"),
															rs.getString("isOriginalFormat"),
															rs.getString("comment"),
															rs.getString("repPath"),
															rs.getLong("timestamp"),
															-1

				);
			}
		}
		return snapshotDbRecord;
	}

	private ElasticExecutedQueryList getElasticExecutedQueryList(PreparedStatement preparedStatement) throws SQLException {
		ElasticExecutedQueryList snapshotDbRecordList = new ElasticExecutedQueryList();
		try (ResultSet rs = preparedStatement.executeQuery()) {
			while (rs.next()) {
				ElasticExecutedQuery r
						= new ElasticExecutedQuery(	rs.getLong("id"),
													rs.getString("requestId"),
													rs.getLong("statementId"),
													rs.getString("statementName"),
													rs.getString("statementType"),
													rs.getString("statement"),
													rs.getString("isValid"),
													rs.getString("jsonParam"),
													rs.getLong("groupId"),
													rs.getString("src"),
													rs.getLong("userId"),
													rs.getLong("clusterId"),
													rs.getString("httpVerb"),
													rs.getString("elasticApi"),
													rs.getString("indexName"),
													rs.getString("endPoint"),
													rs.getString("isOriginalFormat"),
													rs.getString("comment"),
													rs.getString("repPath"),
													rs.getLong("timestamp"),
													rs.getInt("cntAccess"));

				snapshotDbRecordList.addElasticExecutedQuery(r);
			}
			return snapshotDbRecordList;
		}
	}


	private ElasticExecutedQueryList getElasticExecutedQueryListWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		ElasticExecutedQueryList snapshotDbRecordList = new ElasticExecutedQueryList();
		try (ResultSet rs = preparedStatement.executeQuery()) {
			while (rs.next()) {
				ElasticExecutedQuery r
						= new ElasticExecutedQuery(	rs.getLong("id"),
													rs.getString("requestId"),
													rs.getLong("statementId"),
													rs.getString("statementName"),
													rs.getString("statementType"),
													rs.getString("statement"),
													rs.getString("isValid"),
													rs.getString("jsonParam"),
													rs.getLong("groupId"),
													rs.getString("src"),
													rs.getLong("userId"),
													rs.getLong("clusterId"),
													rs.getString("httpVerb"),
													rs.getString("elasticApi"),
													rs.getString("indexName"),
													rs.getString("endPoint"),
													rs.getString("isOriginalFormat"),
													rs.getString("comment"),
													rs.getString("repPath"),
													rs.getLong("timestamp"),
													-1);

				snapshotDbRecordList.addElasticExecutedQuery(r);
			}
			return snapshotDbRecordList;
		}
	}


	private final String selectStr = "SELECT d.id, " +
											"d.requestId, " +
											"d.statementId, " +
											"d.statementName, " +
											"d.statementType, " +
											"d.statement, " +
											"d.isValid, " +
											"d.jsonParam, " +
											"d.groupId, " +
											"d.src, " +
											"d.userId, " +
											"d.clusterId, " +
											"d.httpVerb, " +
											"d.elasticApi, " +
											"d.indexName, " +
											"d.endPoint, " +
											"d.isOriginalFormat, " +
											"d.comment, " +
											"d.repPath, " +
											"d.timestamp " ;

	private final String cntColumn = ", (SELECT SUM(userId) FROM accessRefTable WHERE userId = ?) AS cntAccess ";
	private final String fromTables = " FROM executedQueriesTable d JOIN accessRefTable a ON d.userId = a.userId ";
	private final String fromTable = " FROM executedQueriesTable d ";



	public ElasticExecutedQueryList
	getAllStatementsByUser(final long userId, final long fromDateTime, final long toDateTime) throws Exception {
		Class.forName(JDBC_DRIVER);
		String selectStm = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.timestamp >= ? AND d.timestamp <= ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, fromDateTime);
			preparedStatement.setLong(4, toDateTime);
			return getElasticExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	public ElasticExecutedQueryList
	getAllStatementsByUser(final long userId, final long fromDateTime, final long toDateTime, final String statement) throws Exception {
		Class.forName(JDBC_DRIVER);
		String selectStm = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.timestamp >= ? AND d.timestamp <= ? AND d.statement LIKE ? ";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, fromDateTime);
			preparedStatement.setLong(4, toDateTime);
			preparedStatement.setString(5, '%' + statement + '%');
			return getElasticExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public ElasticExecutedQueryList
	getAllStatementsByUser(final long userId, final String statement) throws Exception {
		Class.forName(JDBC_DRIVER);
		String selectStm = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.statement LIKE ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, '%' + statement + '%');
			return getElasticExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public ElasticExecutedQueryList
	getStatementByName(final String statementName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + fromTable + " WHERE d.statementName = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, statementName);
			return getElasticExecutedQueryList(preparedStatement);
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
			return getElasticExecutedQueryListWithoutCnt(preparedStatement).getElasticExecutedQueryLst().size() == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public ElasticExecutedQueryList
	getUserExecutedStatements(final long userId, final String statementName, final String src) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.statementName = ? AND d.src = ? ORDER BY d.timestamp DESC";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, '%' + statementName + '%');
			preparedStatement.setString(4, src);
			return getElasticExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public ElasticExecutedQueryList
	getAllExecutedStatementsByUser(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? " ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			return getElasticExecutedQueryList(preparedStatement);
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


	public ElasticExecutedQuery
	getLastUserStatement(final long userId, final long groupId, final String source ) throws Exception {
		Class.forName(JDBC_DRIVER);
		String selectStm = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.src = ? AND d.groupId = ? ORDER BY d.timestamp DESC LIMIT 1";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, source);
			preparedStatement.setLong(4, groupId);

			return getElasticExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public ElasticExecutedQueryList
	getStatementsByName(final String statementName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + fromTable + " WHERE d.statementName like ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, "%" + statementName + "%");
			return getElasticExecutedQueryListWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public ElasticExecutedQueryList
	getStatementsByName(final String statementName, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTable + " WHERE d.statementName like ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setString(2, "%" + statementName + "%");
			return getElasticExecutedQueryList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public ElasticExecutedQuery
	getStatementById(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String selectStm = selectStr + fromTable + " WHERE d.id = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			preparedStatement.setLong(1, id);
			return getElasticExecutedQueryWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public ElasticExecutedQuery
	getStatementById(final long id, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String selectStm = selectStr + cntColumn + fromTables + " WHERE d.id = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(selectStm))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, id);
			return getElasticExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	/*Function used to get script record based of report path after serializing script result on disk with a random name. */
	public ElasticExecutedQuery
	getStatementByRepPath(final String repPath) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + fromTable + " WHERE d.repPath = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, repPath);
			return getElasticExecutedQueryWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public ElasticExecutedQuery
	getStatementByRepPath(final String repPath, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr + cntColumn + fromTables +  " WHERE d.repPath = ? AND d.userId = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setString(2, repPath);
			preparedStatement.setLong(3, userId);
			return getElasticExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	/**
	 * Used to uniquely identify a record.
	 */
	public ElasticExecutedQuery
	identifyStatement(final String requestId, final long timeStamp) throws Exception {
		Class.forName(JDBC_DRIVER);
		String select = selectStr +  fromTable + " WHERE d.requestId = ? AND d.timestamp = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setString(1, requestId);
			preparedStatement.setLong(1, timeStamp);
			return getElasticExecutedQuery(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public void
	addExecutedStatement(final ElasticExecutedQuery q) throws Exception {
		addExecutedStatement(	q.getRequestId(),
								q.getStatementId(),
								q.getStatementName(),
								q.getStatement(),
								q.getIsValid(),
								q.getStatementType(),
								q.getJsonParam(),
								q.getGroupId(),
								q.getSource(),
								q.getUserId(),
								q.getClusterId(),
								q.getHttpVerb(),
								q.getElasticApi(),
								q.getIndexName(),
								q.getEndPoint(),
								q.getIsOriginalFormat(),
								q.getComment(),
								q.getRepPath(),
								q.getTimestamp());
	}

	
	public void
	addExecutedStatement(final String requestId,
							 final long statementId,
							 final String statementName,
							 final String statement,
						 	final String isValid,
							 final String statementType, /*Language Paradigm   Constant.lang**/
						 	final String jsonParam,
							 final long groupId,
							 final String source,
							 final long userId,
							 final long clusterId,
							 final String httpVerb,
							 final String elasticApi,
							 final String indexName,
							 final String endPoint,
							 final String isOriginalFormat,
							 final String comment,
							 final String repPath,
							 final long timestamp) throws Exception {

 		String addSql = "INSERT INTO executedQueriesTable(requestId, statementId, statementName, statementType, statement, isValid, jsonParam, groupId, src, userId, clusterId, httpVerb, elasticApi, indexName, endPoint, isOriginalFormat, comment, repPath, timestamp) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(addSql))	{
			preparedStatement.setString(1, requestId);
			preparedStatement.setLong(2, statementId);
			preparedStatement.setString(3, statementName);
			preparedStatement.setString(4, statementType);
			preparedStatement.setString(5, statement);
			preparedStatement.setString(6, statement);
			preparedStatement.setString(7, jsonParam);
			preparedStatement.setLong(8, groupId);
			preparedStatement.setString(9, source);
			preparedStatement.setLong(10, userId);
			preparedStatement.setLong(11, clusterId);
			preparedStatement.setString(12, httpVerb);
			preparedStatement.setString(13, elasticApi);
			preparedStatement.setString(14, indexName);
			preparedStatement.setString(15, endPoint);
			preparedStatement.setString(16, isOriginalFormat);
			preparedStatement.setString(17, comment);
			preparedStatement.setString(18, repPath);
			preparedStatement.setLong(19, timestamp);
			
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


	
	public void 
	deleteStatementByUser(final long userId) throws Exception {
		String deleteDslParam = "DELETE executedQueriesTable WHERE userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public void
	deleteExecutedStatement(final long executedId, final long userId) throws Exception {
		ExecutionUserAccess.deleteArtifactAccess(executedId, userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		if( 0 <= ExecutionUserAccess.countArtifactAccess(executedId, JDBC_DRIVER, DB_URL_DISK, USER, PASS) ) {
			deleteStatementById(executedId);
		}
	}

	public void
	addFullExecutedStatement(final ElasticExecutedQuery query) throws Exception {
		addExecutedStatement(query);
		ElasticExecutedQuery savedQuery = getStatementByRepPath(query.getRepPath());
		ExecutionUserAccess.addArtifactAccess(savedQuery.getId(), savedQuery.getUserId(), PersistencePrivilege.pTypeAdmin, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
	}




	/*Compound access functions*/


	/*Delete executed script access.
    If the access to be removed belongs to owner, the script execution is deleted entirely with all associated access of all third party users
    If the access belongs to a third party user, the access of the third party is removed, but execution and owner access is preserved.
    */
	public ElasticExecutedQuery
	deleteExecutedStatementAccess(final long id, final long userId) throws Exception {
		ElasticExecutedQuery ret = getStatementById(id); /*Get the record before potentially deleting it*/
		long countUsers = ExecutionUserAccess.countArtifactAccess(id, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		ExecutionUserAccess.deleteArtifactAccess(id, userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		ret.setFlag(ExecutedStatementFlag.accessRecordDeleted);
		if(countUsers == 1 && ret.getUserId() == userId) {
			deleteStatementByIdAndUser(id, userId);
			ret.setFlag(ExecutedStatementFlag.executionRecordDeleted);
		}
		return ret;
	}


	/**
	 *  Deletes all execution and associated access to users to own execution scripts
	 */
	public List<ElasticExecutedQuery>
	deleteExecutedStatement(final List<Long> idList, final long userId, final boolean force) throws Exception {
		List<ElasticExecutedQuery> ret = new ArrayList<>();
		for (Long executionId :  idList) {
			ElasticExecutedQuery rec = getStatementById(executionId); /*Get the record before potentially deleting it*/
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
	public List<ElasticExecutedQuery>
	deleteAllExecutedStatementByUserId(final long userId) throws Exception {
		List<ElasticExecutedQuery> ret = new ArrayList<>();
		ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
		List<Long> lst = getUserStatementId(userId);
		for (Long executionId :  lst) {
			ElasticExecutedQuery rec = getStatementById(executionId);
			if(rec.getUserId() == userId) {
				deleteStatementByIdAndUser(executionId, userId);
				ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
			}
			ret.add(rec);
		}
		return ret;
	}


	public static void
	testElasticExecutedQueriesDb() {
		try {
			final String pathF = "c:/f.txt";
			final String statementName = "statementNameTest";
			final long userId = -1L;
			final long clusterId = -1L;
			final long groupId = -1L;
			final String statement = "SELECT col1 FROM table1";
			final String httpVerb = "GET";
			final String elasticApi = "";
			final String indexName = "index1";
			final String endPoint = "";
			final String isOriginalFormat = "Y";
			ElasticExecutedQueriesRepoDb d = new ElasticExecutedQueriesRepoDb();
			ElasticExecutedQueriesRepoDb.createDatabase();

			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroupList pgList = ExecutionGroup.getArtifactGroups("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroup pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.deleteArtifactGroup(pg.getGroupId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);

			ElasticExecutedQuery
					q = new ElasticExecutedQuery(	-1L, "requestId1", -1L, statementName, Constants.langDsl, statement, "Y", "{}", pg.getGroupId(), Constants.adhocShort, userId, clusterId, httpVerb, elasticApi, indexName, endPoint, isOriginalFormat, "", pathF, 0L, 0);
			d.addExecutedStatement(q);

			q = d.getStatementByRepPath(pathF);

			ExecutionUserAccess.addArtifactAccess(q.getId(), userId, PersistencePrivilege.pTypeAdmin,  d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			long cnt = ExecutionUserAccess.countArtifactAccess(q.getId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			System.out.println(cnt);



			ElasticExecutedQuery qR2 = d.getStatementByRepPath(pathF, userId);
			ElasticExecutedQuery qR1 = d.getStatementById(q.getId());
			ElasticExecutedQuery qR3 = d.getStatementById(q.getId(), userId);
			ElasticExecutedQueryList qRList1 = d.getStatementsByName(statementName);
			ElasticExecutedQueryList qRList2 = d.getStatementsByName(statementName, userId);
			ElasticExecutedQueryList lst = d.getAllStatementsByUser(-1, 0, DateTimeUtils.millisecondsSinceEpoch());
			System.out.println(lst.toString());
			lst = d.getAllStatementsByUser(userId, 0, DateTimeUtils.millisecondsSinceEpoch(), statement);
			System.out.println(lst.toString());
			lst = d.getAllStatementsByUser(userId, statement);
			System.out.println(lst.toString());
			ElasticExecutedQuery last = d.getLastUserStatement(userId, q.getGroupId(), Constants.adhocShort);
			System.out.println(last.toString());
			q = d.getStatementById(q.getId());
			System.out.println(q.toString());
			q =  d.deleteExecutedStatementAccess(q.getId(), -1L);
			System.out.println(q.toString());
		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
		}

	}


	
}

