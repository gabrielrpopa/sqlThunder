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


package com.widescope.storage.internalRepo;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.*;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.objects.commonObjects.globals.ListOfStrings;
import com.widescope.sqlThunder.rest.MimeTypes;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.storage.RepoHistoryInterface;
import org.springframework.stereotype.Component;

@Component
public class InternalStorageRepoDb implements RepoHistoryInterface {

	@Override
	public String getRepoTitle() { return RepoStaticDesc.fileRepo; }

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./storageRepo;MODE=PostgreSQL";  
	
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public InternalStorageRepoDb()	{

		storageTypeMap.put(storageAiModel, "Ai Models");
		storageTypeMap.put(storageAiOperation, "Ai Operation");
		storageTypeMap.put(storageFileBackup, "File System Backup");
	}

	// Getters for access wrappers
	public String getDB_URL_DISK() { return DB_URL_DISK; }
	public String getUSER() { return USER; }
	public String getJDBC_DRIVER() { return JDBC_DRIVER; }
	public String getPASS()  { return PASS; }
	

	
	

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
			DbUtil.closeHandles(conn, statement, null);
	    } 
	}

	public static void createDatabase() {
		String fileName = "./storageRepo.mv.db";
		if(!FileUtilWrapper.isFilePresent(fileName)) {
			try {
				InternalStorageRepoDb.generateSchema();
				AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "InternalStorageRepoDb created");
			} catch(Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}
	}
		

	
	
	public 
	static 
	void generateSchema() throws Exception {
		List<String> ddlList = new java.util.ArrayList<String>();

		ddlList.add(ExecutionGroup.groupTable);
		ddlList.add(ExecutionGroup.groupTableIndex1);
		ddlList.add(ExecutionGroup.createTestGroup);
		ddlList.add(ExecutionGroup.createDefaultWebGroup);

		ddlList.add(InternalStorageRepoDb.backupTable);
		ddlList.add(InternalStorageRepoDb.backupTableIndex1);
		ddlList.add(InternalStorageRepoDb.backupTableIndex2);
		ddlList.add(InternalStorageRepoDb.backupTableIndex3);
		ddlList.add(InternalStorageRepoDb.backupTableIndex4);
		ddlList.add(InternalStorageRepoDb.backupTableFk1);
		ddlList.add(InternalStorageRepoDb.backupTable_const1);
		ddlList.add(InternalStorageRepoDb.backupTable_const2);
		ddlList.add(InternalStorageRepoDb.backupTable_const3);


		ddlList.add(ExecutionUserAccess.accessRefTable);
		ddlList.add(ExecutionUserAccess.accessRefTableIndex1);
		ddlList.add(ExecutionUserAccess.accessRefTableConst1);
		ddlList.add(accessRefTableFk);

		ddlList.add(InternalStorageRepoDb.fileStorageTable);
		ddlList.add(InternalStorageRepoDb.fileStorageTableIndex1);
		ddlList.add(InternalStorageRepoDb.fileStorageTableIndex2);

		InternalStorageRepoDb f = new InternalStorageRepoDb();
		f.createSchema(ddlList);
	}
	

	
	////////////////////////////////////// Storage table - //////////////////////////////////////////////////////

	private final String selectBackupColumns = "SELECT b.backupId, b.userId, b.machineName, b.storageType, b.comment, b.groupId, b.src, b.fullPath, b.timeStart, b.timeEnd";
	private final String fromBackupTable  = " FROM backupTable b ";
	private final String fromJoinBackupTable  = " FROM backupTable b INNER JOIN accessRefTable a ON b.userId = a.userId AND b.backupId = a.objectId ";
	private final String cntBackupTableColumn = ", (SELECT SUM(userId) FROM accessRefTable WHERE userId = ?) AS cntAccess ";


	private final String selectFileStorage = "SELECT storageId, backupId, backupName, fileName, fullFilePath, mimeType, function, lastModified, timeStamp, size FROM fileStorageTable";



	/*fileStorageTable.function*/
	public static final String functionRegularFile= "R";   /*Regular Full file, independent */
	public static final String functionPartFile= "P"; /*Regular Part file */
	public static final String functionCompFile= "E"; /*Component of a group of files, associated to Storage Type storageAiModel */

	public
	static
	List<String> functionTypeList = Arrays.asList(new String[] { functionRegularFile, functionPartFile, functionCompFile });

	/*Storage Type (Adhoc/Scheduled)*/
	public static final String storageAiModel= "M";  /*AI MODELS*/
	public static final String storageAiOperation = "O"; /*AI-OPERATION*/
	public static final String storageFileBackup = "F"; /*FILE SYSTEM BACKUP*/

	public
	static
	List<String> storageTypeList = Arrays.asList(new String[] { storageAiModel, storageAiOperation, storageFileBackup });

	public
	static
	Map<String, String> storageTypeMap = new HashMap<>();

	private static boolean isStorageType(String p) {
		return storageTypeList.contains(p.toUpperCase());
	}

	private static String genCommaSeparatedStorageTypeList() {
		return storageTypeList.stream().collect(Collectors.joining("', '", "'", "'"));
	}





	public 
	static 
	String 
	fileStorageTable = "CREATE TABLE IF NOT EXISTS "
				+ " fileStorageTable (storageId BIGINT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
									+ "	backupId BIGINT, "
									+ "	backupName VARCHAR(MAX), "  /*new file name on the backup folder*/
									+ "	fileName VARCHAR(MAX), "  /*original file name, with extension*/
									+ "	fullFilePath VARCHAR(MAX), "  /*original full file path without fileName*/
									+ "	mimeType VARCHAR(MAX), "   /*MimeType, described in com.widescope.sqlThunder.rest.MimeTypes*/
									+ "	function VARCHAR(MAX), "   /*Function associated to this fileType, described in functionTypeList*/
									+ "	lastModified BIGINT, "
									+ "	timeStamp BIGINT, "
									+ "	size BIGINT "
								+ ")";
		
	public static 
	String fileStorageTableIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_fileStorageTable_1 ON fileStorageTable(fileName, timeStamp);";

	public static
	String fileStorageTableIndex2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_fileStorageTable_2 ON fileStorageTable(backupId);";

	public
	static
	String backupTable = "CREATE TABLE IF NOT EXISTS backupTable(backupId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
																+ "	userId BIGINT, "
																+ "	machineName VARCHAR(MAX), "
			                                                    + "	requestId VARCHAR(MAX), "
																+ "	storageType VARCHAR(MAX), "  /*see storageTypeList above*/
																+ "	comment VARCHAR(MAX), "
																+ "	groupId BIGINT, "
																+ "	src VARCHAR(MAX) , "
																+ "	fullPath VARCHAR(MAX), "  /*full Folder path*/
																+ "	timeStart BIGINT, "
																+ "	timeEnd BIGINT, "
																+ " isValid VARCHAR(1)"
																+ ")";




	public static
	String backupTableIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_backupTable_1 ON backupTable(userId);";

	public static
	String backupTableIndex2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_backupTable_2 ON backupTable(machineName);";

	public static
	String backupTableIndex3 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_backupTable_3 ON backupTable(storageType);";

	public static
	String backupTableIndex4 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_backupTable_4 ON backupTable(timeStart, timeEnd);";

	public static
	String backupTableFk1 = "ALTER TABLE backupTable ADD CONSTRAINT IF NOT EXISTS backupTableFk1 FOREIGN KEY ( groupId ) REFERENCES groupTable( groupId );";

	public static final String backupTable_const1 = "ALTER TABLE backupTable ADD CONSTRAINT IF NOT EXISTS ck_backupTable_1  CHECK (storageType IN (" + genCommaSeparatedStorageTypeList() + ") );";
	public static final String backupTable_const2 = "ALTER TABLE backupTable ADD CONSTRAINT IF NOT EXISTS ck_backupTable_2  CHECK (src IN (" + PersistenceSourceList.genCommaSeparatedSourceList() + ") );";
	public static final String backupTable_const3 = "ALTER TABLE backupTable ADD CONSTRAINT IF NOT EXISTS ck_backupTable_1  CHECK (isValid IN ('Y', 'N') );";
	public static String accessRefTableFk = "ALTER TABLE accessRefTable ADD CONSTRAINT IF NOT EXISTS accessRefTableFk1 FOREIGN KEY ( objectId ) REFERENCES backupTable( backupId );";



	/*Backup Table retrieve wrappers*/

	private BackupStorageList getBackupStorageList(PreparedStatement preparedStatement) throws SQLException {
		BackupStorageList backupStorageList = new BackupStorageList();
		try (ResultSet rs = preparedStatement.executeQuery()) {
			while (rs.next()) {
				backupStorageList.addBackupStorage(new BackupStorage(rs.getLong("backupId"),
																	rs.getLong("userId"),
																	rs.getString("requestId"),
																	rs.getString("machineName"),
																	rs.getString("storageType"),
																	rs.getString("comment"),
																	rs.getLong("groupId"),
																	rs.getString("src"),
																	rs.getString("fullPath"),
																	rs.getLong("timeStart"),
																	rs.getLong("timeEnd"),
																	0,
						                                            rs.getString("isValid")
						                                            ));

			}
		}

		return backupStorageList;
	}



	private BackupStorageList getBackupStorageListWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		BackupStorageList snapshotDbRecordList = new BackupStorageList();
			try(ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					snapshotDbRecordList.addBackupStorage(new BackupStorage(rs.getLong("backupId"),
																			rs.getLong("userId"),
							                                                rs.getString("requestId"),
																			rs.getString("machineName"),
																			rs.getString("storageType"),
																			rs.getString("comment"),
																			rs.getLong("groupId"),
																			rs.getString("src"),
																			rs.getString("fullPath"),
																			rs.getLong("timeStart"),
																			rs.getLong("timeEnd"),
																			0,
							                                                rs.getString("isValid"))

					);

				}
			}

		return snapshotDbRecordList;
	}


	private BackupStorage getBackupStorageWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
		BackupStorage snapshotDbRecord = new BackupStorage();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				snapshotDbRecord = new BackupStorage(rs.getLong("backupId"),
													rs.getLong("userId"),
						                            rs.getString("requestId"),
													rs.getString("machineName"),
													rs.getString("storageType"),
													rs.getString("comment"),
													rs.getLong("groupId"),
													rs.getString("src"),
													rs.getString("fullPath"),
													rs.getLong("timeStart"),
													rs.getLong("timeEnd"),
													0,
						                            rs.getString("isValid") );


			}
		}
		return snapshotDbRecord;
	}

	private BackupStorage getBackupStorage(PreparedStatement preparedStatement) throws SQLException {
		BackupStorage snapshotDbRecord = new BackupStorage();
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				snapshotDbRecord = new BackupStorage(rs.getLong("backupId"),
													rs.getLong("userId"),
						                            rs.getString("requestId"),
													rs.getString("machineName"),
													rs.getString("storageType"),
													rs.getString("comment"),
													rs.getLong("groupId"),
													rs.getString("src"),
													rs.getString("fullPath"),
													rs.getLong("timeStart"),
													rs.getLong("timeEnd"),
													0,
						                            rs.getString("isValid"));


			}
		}
		return snapshotDbRecord;
	}


	/*Backup Table*/

	public void
	addNewBackup(final BackupStorage f) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "INSERT INTO backupTable (userId, machineName, requestId, storageType, comment,  groupId, src, fullPath, timeStart, timeEnd, isValid ) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

			preparedStatement.setLong(1, f.getUserId());
			preparedStatement.setString(2, f.getMachineName());
			preparedStatement.setString(3, f.getRequestId());
			preparedStatement.setString(4, f.getStorageType());
			preparedStatement.setString(5, f.getComment());
			preparedStatement.setLong(6, f.getGroupId());
			preparedStatement.setString(7, f.getSource());
			preparedStatement.setString(8, f.getRepPath());
			preparedStatement.setLong(9, f.getTimeStart());
			preparedStatement.setLong(10, f.getTimeEnd());
			preparedStatement.setString(11, f.getIsValid());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public
	ListOfStrings
	getMachines() throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT machineName FROM backupTable";
		ListOfStrings ret = new ListOfStrings();
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 Statement preparedStatement = conn.createStatement())	{
			ResultSet rs = preparedStatement.executeQuery(sqlString);
			while ( rs.next() ) {
				ret.addString(rs.getString("machineName"));
			}
			rs.close();
			return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public
	BackupStorage
	getBackupStorage(final BackupStorage f) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + fromBackupTable + " WHERE f.userId = ? AND f.machineName = ? AND f.timeStart = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

			preparedStatement.setLong(1, f.getUserId());
			preparedStatement.setString(2, f.getMachineName());
			preparedStatement.setLong(3, f.getTimeStart());
			return getBackupStorageWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public
	BackupStorage
	getBackupStorage(final String fullPath) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + fromBackupTable + " WHERE b.fullPath = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, fullPath);
			return getBackupStorageWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public
	BackupStorage
	getBackupStorageById(final long backupId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + fromBackupTable + " WHERE b.backupId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, backupId);
			return getBackupStorageWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	/**
	 *
	 * @param backupId - backupId
	 * @param userId - as a security feature, and to provide a count of total backups the used has access to
	 * @return
	 * @throws Exception
	 */
	public
	BackupStorage
	getBackupStorageByIdAndUser(final long backupId, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE b.backupId = ? AND a.userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, backupId);
			preparedStatement.setLong(3, userId);
			return getBackupStorage(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public
	BackupStorage
	getBackupStorageByIdAndOwner(final long backupId, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE b.backupId = ? AND b.userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, backupId);
			preparedStatement.setLong(3, userId);
			return getBackupStorage(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	public
	BackupStorage
	getLastBackupStorageByUserAndGroupAndSource(final long userId, final long groupId, final String source) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE a.userId = ? AND b.groupId = ? AND b.src = ? ORDER BY b.timeStart DESC LIMIT 1";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setString(4, source);
			return getBackupStorage(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public
	BackupStorage
	getLastBackupStorageByUser(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE a.userId = ?  ORDER BY b.timeStart DESC LIMIT 1";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			return getBackupStorage(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	/**
	 *
	 * @param machineName - Machine Name
	 * @param userId - as a security feature, and to provide a count of total backups the used has access to
	 * @return
	 * @throws Exception
	 */
	public
	BackupStorageList
	getBackupStorageByMachineNameAndUser(final String machineName, final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE b.machineName = ? AND a.userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setString(2, machineName );
			preparedStatement.setLong(3, userId);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	/**
	 *
	 * @param userId
	 * @param timeStartStart
	 * @param timeStartEnd
	 * @return
	 * @throws Exception
	 */
	public
	BackupStorageList
	getBackupStorageByUser(final long userId, final long timeStartStart, final long timeStartEnd) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE a.userId = ? AND b.timeStart >= ? AND b.timeStart <= ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, timeStartStart);
			preparedStatement.setLong(4, timeStartEnd);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public
	void
	endBackupStorage(final long backupId, final long timeEnd) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "UPDATE backupTable SET timeEnd = ? WHERE backupId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, timeEnd);
			preparedStatement.setLong(2, backupId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public
	BackupStorageList
	getBackupStorageListByUserId(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE a.userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public
	BackupStorageList
	getBackupStorageListByOwnerId(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE b.userId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}




	public
	BackupStorageList
	getBackupStorageByMachineNameAndUser(final long userId, final String machineName, final long timeStart1, final long timeStart2) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE a.userId = ? AND b.machineName = ? AND b.timeStart >= ? AND b.timeStart <= ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, machineName);
			preparedStatement.setLong(4, timeStart1);
			preparedStatement.setLong(5, timeStart2);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	public
	BackupStorageList
	getBackupStorageByMachineNameAndUserAndStorageType(final long userId, final String machineName, final String storageType) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE a.userId = ? AND b.machineName = ? AND b.storageType = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, machineName);
			preparedStatement.setString(4, storageType);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}



	public
	BackupStorageList
	getBackupStorageListByUser(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + cntBackupTableColumn + fromJoinBackupTable + " WHERE p.userId = ? ";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

			preparedStatement.setLong(1, userId);
			return getBackupStorageList(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public
	BackupStorage
	getBackupById(final long backupId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectBackupColumns + fromBackupTable + " WHERE b.backupId = ? ";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, backupId);
			return getBackupStorageWithoutCnt(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}




	public void
	deleteBackup(final long backupId) throws Exception {
		ExecutionUserAccess.deleteAccessByArtifactId(backupId, getJDBC_DRIVER(),  getDB_URL_DISK(),  getUSER(), getPASS());

		String sqlString = "DELETE backupTable WHERE backupId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, backupId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

		deleteAllBackupFileRecords(backupId);
	}



	/* fileStorage Table */

	private InternalFileStorageList getInternalFileStorageList(PreparedStatement preparedStatement) throws SQLException {
		InternalFileStorageList ret = new InternalFileStorageList();
		try (ResultSet rs = preparedStatement.executeQuery()) {
			while (rs.next()) {
				InternalFileStorageRecord r = new InternalFileStorageRecord(rs.getLong("storageId"),
																			rs.getLong("backupId"),
																			rs.getString("backupName"),
																			rs.getString("fileName"),
																			rs.getString("fullFilePath"),
																			rs.getLong("size"),
																			rs.getLong("lastModified"),
																			rs.getLong("timeStamp"),
																			rs.getString("mimeType"),
																			rs.getString("function"));

				ret.addFileStorageTableDbRecord(r);

			}
		}

		return ret;
	}


	private InternalFileStorageRecord getInternalFileStorageRecord(PreparedStatement preparedStatement) throws SQLException {
		try(ResultSet rs = preparedStatement.executeQuery() ) {
			if (rs.next()) {
				return new InternalFileStorageRecord(rs.getLong("storageId"),
													rs.getLong("backupId"),
													rs.getString("backupName"),
													rs.getString("fileName"),
													rs.getString("fullFilePath"),
													rs.getLong("size"),
													rs.getLong("lastModified"),
													rs.getLong("timeStamp"),
													rs.getString("mimeType"),
													rs.getString("function"));


			}

			return null;
		}
	}


	public void
	addFileToBackup(final InternalFileStorageRecord f) throws Exception {

		String sqlString = "INSERT INTO fileStorageTable (backupId, backupName, fileName, fullFilePath, mimeType, lastModified, timeStamp, size, function) "
							+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, f.getBackupId());
			preparedStatement.setString(2, f.getBackupName());
			preparedStatement.setString(3, f.getFileName());
			preparedStatement.setString(4, f.getFullFilePath());
			preparedStatement.setString(5, f.getMimeType());
			preparedStatement.setLong(6, f.getLastModified());
			preparedStatement.setLong(7, f.getTimeStamp());
			preparedStatement.setLong(8, f.getSize());
			preparedStatement.setString(9, f.getFunction());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	/**
	 * NOTE: This function is never called without prior call of backup security function
	 * @param backupId
	 * @return
	 * @throws Exception
	 */
	public 
	InternalFileStorageList
	getAllBackupFiles(final long backupId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectFileStorage + " WHERE backupId = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, backupId);
			return getInternalFileStorageList( preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public 
	InternalFileStorageRecord
	getFileById(final long backupId, final long storageId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = selectFileStorage + " WHERE backupId = ? AND storageId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, backupId);
            preparedStatement.setLong(2, storageId);
			return getInternalFileStorageRecord(preparedStatement);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	

	public void
	deleteFileRecord(final long storageId, final long backupId) throws Exception {
		String sqlString = "DELETE fileStorageTable WHERE storageId = ? AND backupId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, storageId);
			preparedStatement.setLong(2, backupId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}

	private void
	deleteAllBackupFileRecords(final long backupId) throws Exception {
		String sqlString = "DELETE fileStorageTable WHERE backupId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, backupId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	

	public static void
	testInternalStorageRepoDb() {
		try {
			final String pathF = "c:/backup1";
			final long userId = 1L;
			final String  machineName = "machineName";
			final String storageType = storageFileBackup;
			InternalStorageRepoDb d = new InternalStorageRepoDb();
			InternalStorageRepoDb.createDatabase();

			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroupList pgList = ExecutionGroup.getArtifactGroups("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			PersistenceGroup pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.deleteArtifactGroup(pg.getGroupId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);


			BackupStorage q = new BackupStorage(-1L, userId, machineName, StringUtils.generateRequestId(), storageType, "just comment", pg.getGroupId(), Constants.adhocShort, pathF, 0, DateTimeUtils.millisecondsSinceEpoch(), 0, "Y");
			d.addNewBackup(q);
			q = d.getBackupStorage(pathF);
			System.out.println(q.toString());
			BackupStorage q1 = d.getBackupById(q.getId());
			System.out.println(q1.toString());
			ExecutionUserAccess.addArtifactAccess(q.getId(), userId, PersistencePrivilege.pTypeAdmin,  d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			long cnt = ExecutionUserAccess.countArtifactAccess(q.getId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			System.out.println(cnt);
			BackupStorageList qR2 = d.getBackupStorageListByUserId(userId);
			System.out.println(qR2.toString());
			BackupStorageList qR21 = d.getBackupStorageListByOwnerId(userId);
			System.out.println(qR21.toString());
			BackupStorage qR1 = d.getBackupStorageById(q.getId());
			System.out.println(qR1.toString());
			BackupStorage qR3 = d.getBackupStorageByIdAndUser(q.getId(), userId);
			System.out.println(qR3.toString());
			BackupStorage qR31 = d.getBackupStorageByIdAndOwner(q.getId(), userId);
			System.out.println(qR31.toString());
			BackupStorageList qRList1 = d.getBackupStorageByMachineNameAndUser(machineName, userId);
			System.out.println(qRList1.toString());
			BackupStorageList qRList3 = d.getBackupStorageByMachineNameAndUser(userId, machineName, 0, DateTimeUtils.millisecondsSinceEpoch());
			System.out.println(qRList3.toString());
			BackupStorageList qRList4 = d.getBackupStorageByMachineNameAndUserAndStorageType(userId, machineName, storageType);
			System.out.println(qRList4.toString());

			BackupStorageList lst = d.getBackupStorageByUser(userId, 0, DateTimeUtils.millisecondsSinceEpoch());
			System.out.println(lst.toString());
			BackupStorage last1 = d.getLastBackupStorageByUserAndGroupAndSource(userId, q.getGroupId(), Constants.adhocShort);
			System.out.println(last1.toString());
			BackupStorage last2 = d.getLastBackupStorageByUser(userId);
			System.out.println(last2.toString());
			final String fileName = "fileName.txt";
			final String fullFileName = "c:/path/";
			final long fileSize = 1024;
			final long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			final long lastMod = timeStamp - 3600;
			final InternalFileStorageRecord f = new InternalFileStorageRecord(-1,
																				last2.getId(),
																				StringUtils.generateUniqueString(16),
																				fileName,
																				fullFileName,
																				fileSize,
																				lastMod,
																				timeStamp,
																				MimeTypes.MIME_APPLICATION_JSON,
																				functionRegularFile);
			d.addFileToBackup(f);
			InternalFileStorageList fList = d.getAllBackupFiles(last2.getId());
			System.out.println(fList.toString());
			InternalFileStorageRecord fl =
			d.getFileById(last2.getId(), fList.getFileStorageTableDbRecordLst().get(0).getStorageId());
			System.out.println(fl.toString());
			d.deleteFileRecord(last2.getId(), fList.getFileStorageTableDbRecordLst().get(0).getStorageId());
			d.addFileToBackup(f);
			fList = d.getAllBackupFiles(last2.getId());
			System.out.println(fList.toString());
			d.deleteAllBackupFileRecords(last2.getId());

			d.endBackupStorage(q.getId(), DateTimeUtils.millisecondsSinceEpoch());
			ExecutionUserAccess.deleteArtifactAccess(q.getId(), userId, d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
			d.deleteBackup(q.getId());

		} catch(Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
		}

	}




}

