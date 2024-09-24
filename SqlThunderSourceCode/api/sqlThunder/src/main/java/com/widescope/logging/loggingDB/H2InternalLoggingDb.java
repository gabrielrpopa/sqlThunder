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

package com.widescope.logging.loggingDB;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;
import com.widescope.sqlThunder.utils.FileUtilWrapper;



@Component
public class H2InternalLoggingDb  {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private String DB_URL_DISK = "jdbc:h2:mem:./storage/log/@application@/@from@_@to@;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
	   
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public String getDbUrl() {
		return DB_URL_DISK;
	}
	
	public H2InternalLoggingDb() {
		
	}
	
	public H2InternalLoggingDb(	String application, 
								final long from, 
								final long to) {
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@application@", application);
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@from@", String. valueOf(from) );
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@to@", String. valueOf(to));
	}
	
	
	
	public 
	static 
	H2InternalLoggingDb 
	setH2InternalLoggingDb(	String application, 
							long from, 
							long to) throws Exception
	{
		H2InternalLoggingDb h2InternalLoggingDb = new H2InternalLoggingDb();
		h2InternalLoggingDb.generateSchema(application, from, to);
		return h2InternalLoggingDb;
	}
		   
	
	
	
	private static void closeHandles(Connection conn, Statement statement, ResultSet rs) {
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	
			
	private boolean createSchema(	List<String> ddlList,
									String application, 
									long from, 
									long to) throws Exception {
		boolean isOK = false;
		Connection conn = null; 
		Statement statement = null; 
		try { 
			// STEP 1: Register JDBC driver 
			Class.forName(JDBC_DRIVER); 
	             
			//STEP 2: Open a connection 
			String connString = this.DB_URL_DISK;
			
			
			
			connString = this.DB_URL_DISK.replace("@application@", application);
			connString = connString.replace("@from@", String. valueOf(from) );
			connString = connString.replace("@to@", String. valueOf(to));
			conn = DriverManager.getConnection(connString, USER, PASS);  
	         
			//STEP 3: Execute a query 
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
		}  finally {
			closeHandles(conn, statement, null);
	    } 
	}

	
		

	
	
	public void generateSchema(	String application, 
								long from, 
								long to) throws Exception {
		try {
			List<String> ddlList = new java.util.ArrayList<String>();

			ddlList.add(H2InternalLoggingDb.host);
			ddlList.add(H2InternalLoggingDb.host_index1);

			ddlList.add(H2InternalLoggingDb.logTable);
			ddlList.add(H2InternalLoggingDb.logTable_index1);
			ddlList.add(H2InternalLoggingDb.logTable_index2);
			ddlList.add(H2InternalLoggingDb.logTable_const1);
			ddlList.add(H2InternalLoggingDb.logTable_const2);
			createSchema(ddlList, application, from, to);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
	}
	
	public 
	static 
	String 
	getPartitionLogPath(String application, 
						long fromMilliseconds, 
						long toMilliseconds) {
		String path = "./storage/log/@application@/@from@_@to@";
		path = path.replace("@application@", application);
		path = path.replace("@from@", String. valueOf(fromMilliseconds) );
		path = path.replace("@to@", String. valueOf(toMilliseconds) );
		return path;
	}
	
	public 
	static 
	boolean 
	isPartition(String application, 
				long fromMilliseconds, 
				long toMilliseconds) {
		String filePath = getPartitionLogPath(application, fromMilliseconds, toMilliseconds);
		filePath = filePath + ".mv.db";
		return FileUtilWrapper.isFilePresent(filePath);
	}
	
	
	
	
	public static String getApplicationLogPath(String application) {
		String path = "./storage/log/@application@";	
		path = path.replace("@application@", application);
		return path;
	}	
	
	
	public 
	static 
	String 
	host = "CREATE TABLE IF NOT EXISTS hostTable (\r\n"
		 				+ "	hostId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
						+ "	hostName VARCHAR(999)\r\n"
						+ ")";
	
	public static String host_index1 = "CREATE INDEX IF NOT EXISTS idx_host_1 ON hostTable(host);";
	

	public static String logTable = "CREATE TABLE IF NOT EXISTS logTable (\r\n"
										+ "	id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
										+ "	hostId BIGINT,\r\n"
										+ "	userId BIGINT,\r\n"
										+ "	timestamp BIGINT,\r\n"
										+ "	message VARCHAR(4000),\r\n"
										+ "	messageType VARCHAR(1), \r\n"
										+ "	artifactName VARCHAR(99), \r\n"
										+ "	artifactType VARCHAR(1) \r\n"
										+ ")";
	
	public static String logTable_index1 = "CREATE INDEX IF NOT EXISTS idx_job_1 ON logTable(userId);";
	public static String logTable_index2 = "CREATE INDEX IF NOT EXISTS idx_job_2 ON logTable(hostId);";
	/*'J' - JSON, 'T' - TEXT, 'B' - BINARY, 'Z' - ZIPPED*/
	public static String logTable_const1 = "ALTER TABLE logTable ADD CONSTRAINT IF NOT EXISTS ck_log_1  CHECK (messageType IN ('J', 'T', 'B', 'Z') );";
	public static String logTable_const2 = "ALTER TABLE logTable ADD CONSTRAINT IF NOT EXISTS ck_log_2  CHECK (artifactType IN ('J', 'T', 'B', 'Z') );";


	public long addHost(final String host) throws Exception {
		long hostId = getHostId(host);
		if(hostId == -1) {
			addHost(host);
			return getHostId(host);
		} else {
			return hostId;
		}
	}
	
	
	public Map<Long, String> 
	addHosts( final List<LogRecordIncoming> lstLogs ) throws Exception {
			List<LogRecordIncoming> newLstLogs = new ArrayList<>();
		
		/*Filter out existing hosts, only new ones are inserted*/
		/*We could have tried insert all, but exceptions from db are expensive*/
		Map<Long, String> allHosts = this.getHostIds();
		for(LogRecordIncoming rec: lstLogs) {
			if(! allHosts.containsValue(rec.getHostName())) {
				newLstLogs.add(rec);
			}
		}
		
		
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO hostTable (host) VALUES(?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			
			for(LogRecordIncoming rec: newLstLogs) {
				preparedStatement.setString(1, rec.getHostName());
				preparedStatement.setLong(2, rec.getUserId());
				preparedStatement.setLong(3, rec.getTimestamp());
				preparedStatement.setString(4, rec.getMessage());
				preparedStatement.setString(5, rec.getMessageType());
				preparedStatement.setString(6, rec.getArtifactName());
				preparedStatement.setString(7, rec.getArtifactType());
	            preparedStatement.executeUpdate();
			}
			
            preparedStatement.close();
            
            return this.getHostIds();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public int 
	addHost_(final String host) throws Exception {
		int ret = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO hostTable (hostName) VALUES(?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, host);
            ret = preparedStatement.executeUpdate();
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	long 
	getHostId(final String host) throws Exception {
		long hostId = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT hostId FROM hostTable WHERE hostName =  ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, host);
			ResultSet rs = preparedStatement.executeQuery();
            
            if ( rs.next() ) {
            	hostId = rs.getLong("hostId");
            }
           
            rs.close();
            return hostId;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public
	List<Long> 
	getHostIds(final String hostName) throws Exception {
		List<Long>  hostList = new ArrayList<Long>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT hostId FROM hostTable WHERE hostName LIKE ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, "%" + host + "%");
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	hostList.add(rs.getLong("hostId"));
            }
           
            rs.close();
            return hostList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public
	Map<Long, String> 
	getHostIds() throws Exception {
		Map<Long, String>  hostMap = new HashMap<Long, String>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT hostId, hostName FROM hostTable";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	hostMap.put(rs.getLong("hostId"), rs.getString("hostName"))  ;
            }
           
            rs.close();
            return hostMap;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	/******************************  log */
	
	
	public int 
	addLog(	final long hostId,
			final long userId, 
			final long timestamp,
			final String message, 
			final String messageType,
			final String artifactName,
			final String artifactType
			) throws Exception	{

		int ret = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO logTable (hostId, userId, timestamp, message, messageType, artifactName,artifactType) VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, hostId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setLong(3, timestamp);
			preparedStatement.setString(4, message);
			preparedStatement.setString(5, messageType);
			preparedStatement.setString(6, artifactName);
			preparedStatement.setString(7, artifactType);
            ret = preparedStatement.executeUpdate();
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public int 
	addLogs( List<LogRecordIncomingWithHostId> lstLogs ) throws Exception {
		int ret = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO logTable (hostId, userId, timestamp, message, messageType, artifactName,artifactType) VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			
			for(LogRecordIncomingWithHostId rec: lstLogs) {
				preparedStatement.setLong(1, rec.getHostId());
				preparedStatement.setLong(2, rec.getUserId());
				preparedStatement.setLong(3, rec.getTimestamp());
				preparedStatement.setString(4, rec.getMessage());
				preparedStatement.setString(5, rec.getMessageType());
				preparedStatement.setString(6, rec.getArtifactName());
				preparedStatement.setString(7, rec.getArtifactType());
	            ret = preparedStatement.executeUpdate();
			}
			
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public List<LogRecord> 
	getLog( final long from, 
			final long to) throws Exception {

		List<LogRecord> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, "
								+ "hostId, "
								+ "userId, "
								+ "timestamp, "
								+ "message, "
								+ "messageType, "
								+ "artifactName, "
								+ "artifactType "
								+ "FROM logTable WHERE timestamp >=  ? AND timestamp <= ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, from );
			preparedStatement.setLong(2, to );
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	LogRecord b = new LogRecord(rs.getLong("id"),
											rs.getLong("hostId"),
											rs.getLong("userId"),
											rs.getLong("timestamp"),
											rs.getString("message"),
											rs.getString("messageType"),
											rs.getString("artifactName"),
											rs.getString("artifactType")
								    		);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public List<LogRecord> 
	getLog( final long from, 
			final long to,
			final long userId,
			final List<Long> hostList,
			final String message) throws Exception {

		if(from < 0 || to < 0 || from > to) {
			throw new Exception ("Range time is incorrect ");
		}
		
		List<LogRecord> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
				
		String sqlString = "SELECT id, "
								+ "hostId, "
								+ "userId, "
								+ "timestamp, "
								+ "message, "
								+ "messageType, "
								+ "artifactName, "
								+ "artifactType "
								+ "FROM logTable  WHERE  timestamp >=  ? AND timestamp <= ? ";
		
		if (userId > 0) {
			sqlString = sqlString + " AND userId = ? ";
		}
		
		if(message != null && !message.isEmpty()) {
			sqlString = sqlString + " AND message LIKE ? ";
		}
		
		if(hostList != null && !hostList.isEmpty()) {
			sqlString = sqlString + " AND hostId IN (?) ";
		}
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			
			preparedStatement.setLong(1, from );
			preparedStatement.setLong(2, to );
			if (userId > 0) {
				preparedStatement.setLong(3, userId );
			}
			if(message != null && !message.isEmpty()) {
				preparedStatement.setString(4, "%" + message + "%" );
			}
			if(hostList != null && !hostList.isEmpty()) {
				String in = hostList.stream().map(Object::toString).collect(Collectors.joining(", "));
				preparedStatement.setString(4, in );
			}
			
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	LogRecord b = new LogRecord(rs.getLong("id"),
											rs.getLong("hostId"),
											rs.getLong("userId"),
											rs.getLong("timestamp"),
											rs.getString("message"),
											rs.getString("messageType"),
											rs.getString("artifactName"),
											rs.getString("artifactType")
								    		);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	

	
	public List<LogRecord> 
	getLog(final long from, final long to, final String message) throws Exception {
		List<LogRecord> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, "
								+ "hostId, "
								+ "userId, "
								+ "timestamp, "
								+ "message, "
								+ "messageType, "
								+ "artifactName, "
								+ "artifactType "
								+ "FROM logTable "
								+ "WHERE timestamp >=  ? AND timestamp <= ? AND message like ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, from );
			preparedStatement.setLong(2, to );
			preparedStatement.setString(1, "%" + message + "%");
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	LogRecord b = new LogRecord(rs.getLong("id"),
														rs.getLong("hostId"),
														rs.getLong("userId"),
														rs.getLong("timestamp"),
														rs.getString("message"),
														rs.getString("messageType"),
														rs.getString("artifactName"),
														rs.getString("artifactType")
								    		);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	public List<LogRecord> 
	getLog(final long entryId) throws Exception {
		List<LogRecord> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, "
								+ "hostId, "
								+ "userId, "
								+ "timestamp, "
								+ "message, "
								+ "messageType, "
								+ "artifactName, "
								+ "artifactType "
								+ "WHERE id = ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, entryId );
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	LogRecord b = new LogRecord(rs.getLong("id"),
														rs.getLong("hostId"),
														rs.getLong("userId"),
														rs.getLong("timestamp"),
														rs.getString("message"),
														rs.getString("messageType"),
														rs.getString("artifactName"),
														rs.getString("artifactType")
								    		);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
}

