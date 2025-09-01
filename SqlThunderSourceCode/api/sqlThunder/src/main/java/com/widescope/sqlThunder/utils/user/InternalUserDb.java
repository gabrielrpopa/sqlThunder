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


package com.widescope.sqlThunder.utils.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.scripting.websock.ScriptHeaderOutput;
import com.widescope.sqlThunder.utils.restApiClient.RestApiWebSocket;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import org.springframework.stereotype.Component;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.security.SHA512Hasher;



@Component
public class InternalUserDb {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	public static DataWhales dataWhales = new DataWhales();
	public static Map<String, UserShort> loggedUsers = new ConcurrentHashMap<String, UserShort>();  // httpSession/UserShort
	public static Map<String, List<String> > loggedUserDevices = new ConcurrentHashMap<String, List<String>>();  // userName/List<DeviceToken>
	public static Map<String, String> timedUsers = new ConcurrentHashMap<String, String>();  // httpSession/username or email

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";
	private final String DB_URL_DISK = "jdbc:h2:file:./userRepo;MODE=PostgreSQL";

	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa";

	private static final long inactiveTime  = 1000 * 300;
	/**
	 * Used by global maintenance thread to clean-up inactive sessions
	 */
	public static void killTimedOutSessions() {
		long currentTimeStamp = DateTimeUtils.millisecondsSinceEpoch();
		InternalUserDb.loggedUsers.entrySet().removeIf(e -> {
			boolean ret = (currentTimeStamp - e.getValue().getTimeStamp() >= inactiveTime);
			//System.out.println("time diff:");
			//System.out.println(currentTimeStamp - e.getValue().getTimeStamp());
			if(  ret ) {  /*Kill the session for lingering unused for more than 5 minutes*/
				UserShort uShort =  e.getValue();
				System.out.println("!!!!!!!!!!!!! Killing session for user " +  uShort.getUser());
				WebsocketPayload wsPayload = new WebsocketPayload("-1", uShort.getUser(), uShort.getUser(), WebsocketMessageType.logOut, "", ClusterDb.ownBaseUrl);
				if( !RestApiWebSocket.sendToControlQueue(wsPayload, ClusterDb.ownBaseUrl) ) {
					AppLogger.logException(new Exception("sendToControlQueue failed to kill timeout session to user: " + uShort.getUser()), Thread.currentThread().getStackTrace()[1], AppLogger.thread);
				}
			}

			return ret;
		});
	}


	private static boolean isNotConsideredUserActivity(String uri) {
		return uri.equalsIgnoreCase("/sqlThunder/heartbeat") ||
				uri.equalsIgnoreCase("/sqlThunder/config:owner") ||
				uri.equalsIgnoreCase("/sqlThunder/client:ip") ||
				uri.equalsIgnoreCase("/sqlThunder/chat/fromUser/toUser/count:get") ||
				uri.equalsIgnoreCase("/sqlThunder/environment/be:version") ||
				uri.equalsIgnoreCase("/sqlThunder/users/user:check")
				;
	}

	public static boolean validateAndClearSession(final String user, final String session, final String uri) {
		long currentTimeStamp = DateTimeUtils.millisecondsSinceEpoch();
		UserShort us = InternalUserDb.loggedUsers.get(session);
		if( currentTimeStamp - us.getTimeStamp() >= inactiveTime || user.compareTo(us.getUser()) != 0 ) {
			InternalUserDb.loggedUsers.remove(session);
			return false;
		}

		if(user.compareTo(us.getUser()) == 0 && !isNotConsideredUserActivity(uri)) {
			InternalUserDb.loggedUsers.get(session).setTimeStamp(DateTimeUtils.millisecondsSinceEpoch());
		}

		return true;

	}
	
	public InternalUserDb()	{ }
		   
	
	private void closeHandles(	Connection conn, 
								Statement statement, 
								ResultSet rs) {
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	
	
	
	
	public void createSchema(List<String> ddlList) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Connection conn = null;
		Statement statement = null; 
		try { 
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement();
			for (String ddl : ddlList) {
				statement.executeUpdate(ddl);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		} finally	{
			closeHandles(conn, statement, null);
	    } 
	}

	
	


	
	
	public static InternalUserDb generateSchema() throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		List<String> ddlList = new java.util.ArrayList<String>();
		try {
			ddlList.add(InternalUserDb.repo_department);
			ddlList.add(InternalUserDb.repo_department_index1);
			ddlList.add(InternalUserDb.repo_department_first);
			ddlList.add(InternalUserDb.repo_department_second);
			
			ddlList.add(InternalUserDb.repo_title);
			ddlList.add(InternalUserDb.repo_title_index1);
			ddlList.add(InternalUserDb.repo_title_first);
			
			ddlList.add(InternalUserDb.repo_users);
			ddlList.add(InternalUserDb.repo_users_const1);
			ddlList.add(InternalUserDb.repo_users_index1);
			//ddlList.add(InternalUserDb.repo_users_index2); userName is identical to email now
			ddlList.add(InternalUserDb.generateSuperUser());
			ddlList.add(InternalUserDb.generateAnotherUser());

			ddlList.add(InternalUserDb.repo_users_registration);
			ddlList.add(InternalUserDb.repo_usersReg_const1);
			ddlList.add(InternalUserDb.repo_usersReg_index1);
			InternalUserDb internalUserDb = new InternalUserDb();
			internalUserDb.createSchema(ddlList);
			return internalUserDb;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public 
	static 
	String 
	repo_department = "CREATE TABLE IF NOT EXISTS departmentRef (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
											+ "	departmentName VARCHAR(99),"
											+ "	description VARCHAR(999))";


	public 
	static 
	String 
	repo_department_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_department_1 ON departmentRef(departmentName);";
	
	public 
	static 
	String 
	repo_department_first = " MERGE INTO departmentRef (departmentName,  description) KEY(departmentName) "
											+ "	VALUES('Supreme Department', 'Above All')";
	
	public 
	static 
	String 
	repo_department_second = " MERGE INTO departmentRef (departmentName,  description) KEY(departmentName) "
															+ "	VALUES('IT', 'Information Technology Department')";


	public
	static 
	String 
	repo_title = "CREATE TABLE IF NOT EXISTS titleRef (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
																+ "	title VARCHAR(MAX),"
																+ "	description VARCHAR(MAX))";


	public 
	static 
	String 
	repo_title_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_title_1 ON titleRef(title);";

	public 
	static 
	String 
	repo_title_first = " MERGE INTO titleRef (title,  description) KEY(title) VALUES('Supreme Being', 'Super User or samething')";

	public
	static 
	String 
	repo_title_second = " MERGE INTO titleRef (title,  description) KEY(title) VALUES('Senior Developer', 'Software Developer')";


																		

	public static final
	String repo_users = "CREATE TABLE IF NOT EXISTS userTbl (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
															+ "	userType VARCHAR(MAX) DEFAULT 'USER',"
															+ "	userName VARCHAR(MAX),"
															+ "	password VARCHAR(MAX) DEFAULT '',"
															+ "	firstName VARCHAR(MAX),"
															+ "	lastName VARCHAR(MAX) ,"
															+ "	email VARCHAR(MAX) ,"
															+ "	department BIGINT DEFAULT 1,"
															+ "	title BIGINT DEFAULT 1,"
															+ "	manager BIGINT DEFAULT 1,"
															+ "	characteristic VARCHAR(MAX) DEFAULT '',"
															+ "	description VARCHAR(MAX) DEFAULT '',"
															+ "	active VARCHAR(1) DEFAULT 'Y')";

	public 
	static 
	String 
	repo_users_const1 = "ALTER TABLE userTbl ADD CONSTRAINT IF NOT EXISTS ck_userTbl_1 CHECK (userType IN ('SUPER', 'ADMIN', 'USER', 'SERVICE') );";
	
	public 
	static 
	String 
	repo_users_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_userTbl_1 ON userTbl(userName);";


	public static String generateSuperUser() {
		final String superUser = "sqlthunder@gmail.com";
		final String superPassword = "BFLx6R51DY4}vs5&Y1xh";

		System.out.print("adding super user: " + superUser + "\n");
		System.out.print("initial password super user: " + superPassword + "\n");
		String hash = SHA512Hasher.hash(superPassword);

        return " MERGE INTO userTbl (userType,  userName, password, firstName, lastName, email, department, title, manager, description, active) KEY(userName) "
											+ "	VALUES('SUPER', '" + superUser + "', '" + hash + "' , 'Super',  'Usr', '" + superUser + "', 1, 1, 0, 'Super User of all',  'Y')";
	}

	public static String generateAnotherUser() {

		final String user = "infinite.loop.corp.limited@gmail.com";
		final String password = "BFLx6R51DY4}vs5&Y1xh";

		System.out.print("adding user: " + user + "\n");
		System.out.print("initial password user: " + password + "\n");
		String hash = SHA512Hasher.hash(password);

		return " MERGE INTO userTbl (userType,  userName, password, firstName, lastName, email, department, title, manager, description, active) KEY(userName) "
				+ "	VALUES('USER', '" + user + "', '" + hash + "' , 'Infinite',  'Loop', '" + user + "', 1, 1, 0, 'Just a user',  'Y')";
	}

	
	
	public 
	static 
	String 
	repo_users_registration = "CREATE TABLE IF NOT EXISTS userTblReg (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
																+ "	userType VARCHAR(MAX),"
																+ "	userName VARCHAR(MAX),"
																+ "	password VARCHAR(MAX),"
																+ "	firstName VARCHAR(MAX),"
																+ "	lastName VARCHAR(MAX) ,"
																+ "	email VARCHAR(MAX) ,"
																+ "	timestamp BIGINT "
																+ ")";

	public 
	static 
	String 
	repo_usersReg_const1 = "ALTER TABLE userTblReg ADD CONSTRAINT IF NOT EXISTS ck_userTblReg_1 CHECK (userType IN ('SUPER', 'ADMIN', 'USER', 'SERVICE') );";
	
	public
	static 
	String 
	repo_usersReg_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_userTblReg_1 ON userTblReg(userName);";
	

	
	

	public boolean addUser(final User u) throws Exception {
			return addUser(u.getUserType(), 
					u.getUser(), 
					u.getPassword(), 
					u.getFirstName(),
					u.getLastName(),
					u.getEmail(),
					u.getDepartment(),
					u.getTitle(),
					u.getManager(),
					u.getCharacteristic(),
					u.getUserDescription(),
					u.getActive() );
	}

	public boolean addUser(final String userType, 
						final String userName, 
						final String password, 
						final String firstName,
						final String lastName,
						final String email,
						final long department,
						final long title,
						final long manager,
						final String characteristic,
						final String description,
						final String active) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO userTbl (userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			String uName = userName.toLowerCase();
			preparedStatement.setString(1, userType);
            preparedStatement.setString(2, uName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstName);
            preparedStatement.setString(5, lastName);
            
            preparedStatement.setString(6, email);
            preparedStatement.setLong(7, department);
            preparedStatement.setLong(8, title);
            preparedStatement.setLong(9, manager);
            preparedStatement.setString(10, characteristic);
            
            preparedStatement.setString(11, description);
            preparedStatement.setString(12, active);
            preparedStatement.executeUpdate();
            
            return true;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean addRegisteredUser(	final String userType, 
										final String userName, 
										final String password, 
										final String firstName,
										final String lastName,
										final String email
										) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = " MERGE INTO userTblReg (userType, userName, password, firstName, lastName, email, timestamp) key(userName) VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, userType);
			preparedStatement.setString(2, userName);
			preparedStatement.setString(3, password);
			preparedStatement.setString(4, firstName);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, email);
			preparedStatement.setLong(7, DateTimeUtils.millisecondsSinceEpoch());
			preparedStatement.executeUpdate();
			
			return true;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean addUser(	final String userType, 
							final String userName, 
							final String password, 
							final String firstName,
							final String lastName,
							final String email,
							final String active
							) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = " MERGE INTO userTbl (userType, userName, password, firstName, lastName, email, timestamp, active) key(userName) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, userType);
			preparedStatement.setString(2, userName);
			preparedStatement.setString(3, password);
			preparedStatement.setString(4, firstName);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, email);
			preparedStatement.setString(7, active);
			preparedStatement.setLong(7, DateTimeUtils.millisecondsSinceEpoch());
			preparedStatement.executeUpdate();
			
			return true;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public boolean deleteUser(final String userName) throws Exception	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE userTbl WHERE userName = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			String uName = userName.toLowerCase();
            preparedStatement.setString(1, uName);
          
            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean deleteUser(final long id) throws Exception	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE userTbl WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setLong(1, id);
            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean deleteRegisteredUser(final String userTblReg) throws Exception	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE userTblReg WHERE userName = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, userTblReg);
            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}



	public User getUser(final String userName) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE userName = ?";
		User ret = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			String uName = userName.toLowerCase();
            preparedStatement.setString(1, uName);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new User(	rs.getLong("id"), 
            				  	rs.getString("userType"), 
            					rs.getString("userName"), 
            					rs.getString("password"), 
            					rs.getString("firstName"), 
            					rs.getString("lastName"), 
            					rs.getString("email"),
					            rs.getInt("department"),
					            rs.getInt("title"),
					            rs.getInt("manager"),
					            rs.getString("characteristic"),
            					rs.getString("description"),  
            					rs.getString("active"),
            					"N",
            					null);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}

		return ret;
	}

	
	
	
	public List<User> getUsers(final List<Long> idList) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(idList.isEmpty()) return new ArrayList<User>();
		String commaList = idList.stream().map(String::valueOf).collect(Collectors.joining(","));
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, "
								+ " userType, "
								+ " userName, "
								+ " password, "
								+ " firstName, "
								+ " lastName, "
								+ " email, "
								+ " department, "
								+ " title, "
								+ " manager, "
								+ " characteristic, "
								+ " description, "
								+ " active "
								+ "FROM userTbl WHERE id IN (" + commaList + ")";
		List<User> ret = new ArrayList<User>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	User u = new User(	rs.getLong("id"), 
            				  	rs.getString("userType"), 
            					rs.getString("userName"), 
            					rs.getString("password"), 
            					rs.getString("firstName"), 
            					rs.getString("lastName"), 
            					rs.getString("email"),
					            rs.getInt("department"),
					            rs.getInt("title"),
					            rs.getInt("manager"),
					            rs.getString("characteristic"),
            					rs.getString("description"),  
            					rs.getString("active"),
            					"N",
            					null);
            	ret.add(u);
            	
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	
	
	public User getRegisteringUser(final String userName) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email FROM userTblReg WHERE userName = ?";
		User ret = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new User(	rs.getLong("id"), 
            				  	rs.getString("userType"), 
            					rs.getString("userName"), 
            					rs.getString("password"), 
            					rs.getString("firstName"), 
            					rs.getString("lastName"), 
            					rs.getString("email"),
					            -1,
					            -1,
					            -1,
					            "",
            					"",  
            					"",
            					"",
            					null);

            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public List<User> getRegisteringUsers(final String likeUser) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, firstName, lastName, email FROM userTblReg WHERE userName like ? OR firstName like ? OR lastName like ? ";
		List<User> ret = new ArrayList<User>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	User u = new User(	rs.getLong("id"), 
	            				  	rs.getString("userType"), 
	            					rs.getString("userName"), 
	            					"", 
	            					rs.getString("firstName"), 
	            					rs.getString("lastName"), 
	            					rs.getString("email"),
						            -1,
						            -1,
						            -1,
						            "",
	            					"",  
	            					"",
	            					"",
	            					null);
            	ret.add(u);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public User getUserById(final long id) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE id = ?";
		User ret = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new User(	rs.getLong("id"), 
            				  	rs.getString("userType"), 
            					rs.getString("userName"), 
            					rs.getString("password"), 
            					rs.getString("firstName"), 
            					rs.getString("lastName"), 
            					rs.getString("email"),
					            rs.getInt("department"),
					            rs.getInt("title"),
					            rs.getInt("manager"),
					            rs.getString("characteristic"),
            					rs.getString("description"),  
            					rs.getString("active"),
            					"N",
            					null);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public List<User> getAllUsersByType(final String userType) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE userType = ?";
		List<User> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userType);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	User b = new User(rs.getInt("id"), 
            					  rs.getString("userType"), 
            					  rs.getString("userName"), 
            					  rs.getString("password"), 
            					  rs.getString("firstName"), 
            					  rs.getString("lastName"), 
            					  rs.getString("email"),
					              rs.getInt("department"),
					              rs.getInt("title"),
					              rs.getInt("manager"),
					              rs.getString("characteristic"),
            					  rs.getString("description"),  
            					  rs.getString("active"),
            					  "N",
            					  null);
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	

	
	
	public User getUser(final long id) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	return new User(rs.getLong("id"), 
            					  rs.getString("userType"), 
            					  rs.getString("userName"), 
            					  rs.getString("password"), 
            					  rs.getString("firstName"), 
            					  rs.getString("lastName"), 
            					  rs.getString("email"),
					              rs.getInt("department"),
					              rs.getInt("title"),
					              rs.getInt("manager"),
					              rs.getString("characteristic"),
            					  rs.getString("description"),  
            					  rs.getString("active"),
            					  "N",
            					  null);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return null;
	}
	
	
	public List<User> getUsers(final String likeUser) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE userName like ? OR firstName like ? OR lastName like ?  OR description like ? ";
		List<User> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(4, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	User b = new User(rs.getLong("id"), 
            					  rs.getString("userType"), 
            					  rs.getString("userName"), 
            					  "", 
            					  rs.getString("firstName"), 
            					  rs.getString("lastName"), 
            					  rs.getString("email"),
					              rs.getInt("department"),
					              rs.getInt("title"),
					              rs.getInt("manager"),
					              rs.getString("characteristic"),
            					  rs.getString("description"),  
            					  rs.getString("active"),
            					  "N",
            					  null);
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	
	public List<User> getUsersMinusUser(final String likeUser, final User u) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE userName like ? OR firstName like ? OR lastName like ?  OR description like ? ";
		List<User> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(4, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	User b = new User(rs.getLong("id"), 
            					  rs.getString("userType"), 
            					  rs.getString("userName"), 
            					  "", 
            					  rs.getString("firstName"), 
            					  rs.getString("lastName"), 
            					  rs.getString("email"),
					              rs.getInt("department"),
					              rs.getInt("title"),
					              rs.getInt("manager"),
					              rs.getString("characteristic"),
            					  rs.getString("description"),  
            					  rs.getString("active"),
            					  "N",
            					  null);
            	
            	if(u.getId() != b.getId())
            	  	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public List<ManagerShort> getManagers(final String likeUser) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userName, firstName, lastName FROM userTbl WHERE userName like ? OR firstName like ? OR lastName like ?  OR description like ? ";
		List<ManagerShort> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(4, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ManagerShort b = new ManagerShort(rs.getInt("id"), 
				            					  rs.getString("userName"), 
				            					  rs.getString("firstName"), 
				            					  rs.getString("lastName") 
				            					 );
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}

	public List<User> authUser(	final String userName, 
								final String password) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE userName = ? and password = ?";
		//String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM userTbl WHERE userName = ?";
		List<User> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userName.toLowerCase());
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	User b = new User(rs.getLong("id"), 
            					  rs.getString("userType"), 
            					  rs.getString("userName"), 
            					  rs.getString("password"), 
            					  rs.getString("firstName"), 
            					  rs.getString("lastName"), 
            					  rs.getString("email"),
					              rs.getInt("department"),
					              rs.getInt("title"),
					              rs.getInt("manager"),
					              rs.getString("characteristic"),
            					  rs.getString("description"),  
            					  rs.getString("active"),
            					  "N",
            					  null);
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public boolean updateSuper(final User s) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = " MERGE INTO userTbl (id, userName, password, firstName, lastName, email, department, title, manager, characteristic, description) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, s.getId());
            preparedStatement.setString(3, s.getUser().toLowerCase());
            preparedStatement.setString(4, s.getPassword());
            preparedStatement.setString(5, s.getFirstName());     
            preparedStatement.setString(6, s.getLastName());        
            preparedStatement.setString(7, s.getEmail());
            preparedStatement.setLong(8, s.getDepartment());
            preparedStatement.setLong(9, s.getTitle());
            preparedStatement.setLong(10, s.getManager());
            preparedStatement.setString(11, s.getCharacteristic());
            preparedStatement.setString(12, s.getUserDescription());

            int row = preparedStatement.executeUpdate();
            return row == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
		
	public boolean updateUser(final User s) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = " MERGE INTO userTbl (id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, s.getId());
            preparedStatement.setString(2, s.getUserType());
            preparedStatement.setString(3, s.getUser().toLowerCase());
            preparedStatement.setString(4, s.getPassword());
            preparedStatement.setString(5, s.getFirstName());     
            preparedStatement.setString(6, s.getLastName());        
            preparedStatement.setString(7, s.getEmail());
            preparedStatement.setLong(8, s.getDepartment());
            preparedStatement.setLong(9, s.getTitle());
            preparedStatement.setLong(10, s.getManager());
            preparedStatement.setString(11, s.getCharacteristic());
            preparedStatement.setString(12, s.getUserDescription());
            preparedStatement.setString(13, s.getActive());

            int row = preparedStatement.executeUpdate();
            return row == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean quickUserUpdate(final User s) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = " UPDATE userTbl "
							+ " SET userType = ?, "
				                + " department = ?, "
				                + " title = ?, "
				                + " manager = ?, "
				                + " characteristic = ?, "
				                + " description = ?, "
				                + " active = ? "
				                + " WHERE id = ?";
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
            preparedStatement.setString(1, s.getUserType());
            preparedStatement.setLong(2, s.getDepartment());
            preparedStatement.setLong(3, s.getTitle());
            preparedStatement.setLong(4, s.getManager());
            preparedStatement.setString(5, s.getCharacteristic());
            preparedStatement.setString(6, s.getUserDescription());
            preparedStatement.setString(7, s.getActive());
            preparedStatement.setLong(8, s.getId());
            
            int row = preparedStatement.executeUpdate();
            
            if(row == 0) {
				AppLogger.logError(className, methodName, AppLogger.db, s.getEmail() + " was not updated");
            }
            
            return row == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean updateMePassword(final long id, 
									final String password) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = " MERGE INTO userTbl (id, password) KEY(id) VALUES (?, ?)";
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
            preparedStatement.setString(2, password);
           

            int row = preparedStatement.executeUpdate();
            return row == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean updateMe(final long id, 
							final String firstName,
							final String lastName) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "MERGE INTO userTbl (id, firstName, lastName) KEY(id) VALUES (?, ?, ?)";
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
            preparedStatement.setString(2, firstName);     
            preparedStatement.setString(3, lastName);        

            int row = preparedStatement.executeUpdate();
            return row == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean updateMeEmailUserName(	final long id, 
											final String userName,
											final String email) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "MERGE INTO userTbl (id, userName, email) KEY(id) VALUES (?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
			preparedStatement.setString(2, userName);     
			preparedStatement.setString(3, email);        
			
			int row = preparedStatement.executeUpdate();
			return row == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	//////////////////////////////////////////////////  department /////////////////////////////////////////////////////////////////////////////////
	public void addDepartment(final Department d) throws Exception	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO departmentRef (departmentName, description) VALUES(?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, d.getDepartment().toLowerCase());
			preparedStatement.setString(2, d.getDescription());
			preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public boolean deleteDepartment(final String departmentName) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE departmentRef WHERE department = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, departmentName.toLowerCase());
          
            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	public boolean deleteDepartment(final long id) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE departmentRef WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setLong(1, id);
          
            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public boolean updateDepartment(final Department d) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "MERGE INTO departmentRef (id, departmentName, description) KEY(id) VALUES(?, ?, ?) ";
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, d.getId());
            preparedStatement.setString(2, d.getDepartment().toLowerCase());
            preparedStatement.setString(3, d.getDescription());

            int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public List<Department> getDepartments(final String likeDept) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, departmentName, description FROM departmentRef WHERE departmentName like ? ";
		List<Department> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeDept.toLowerCase() + '%');

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	Department b = new Department(rs.getLong("id"),
			            					  rs.getString("departmentName"), 
			            					  rs.getString("description"));
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public List<Department> getDepartment(final String department) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, departmentName, description FROM departmentRef WHERE departmentName = ? ";
		List<Department> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, department.toLowerCase());

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	Department b = new Department(rs.getInt("id"), 
			            					  rs.getString("departmentName"), 
			            					  rs.getString("description"));
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		return ret;
	}
	
	
	
	
	public Department getDepartmentById(final long id) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, departmentName, description FROM departmentRef WHERE id = ? ";
		Department ret = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new Department(rs.getLong("id"),
			      					  rs.getString("departmentName"), 
			       					  rs.getString("description"));

            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public Department getDepartment(final long id) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, departmentName, description FROM departmentRef WHERE id = ? ";
		Department ret = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new Department(rs.getInt("id"), 
			      					  rs.getString("departmentName"), 
			       					  rs.getString("description"));
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	
	//////////////////////////////////////////////////Titles /////////////////////////////////////////////////////////////////////////////////
	public void addTitle(final Title t) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO titleRef (title, description) VALUES(?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, t.getTitle().toLowerCase());
			preparedStatement.setString(2, t.getDescription());
			preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public boolean deleteTitle(final long id) throws Exception	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE titleRef WHERE id = ?";
			
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
			int row = preparedStatement.executeUpdate();
            return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	
	public boolean updateTitle(final Title t) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "MERGE INTO titleRef (id, title, description) KEY(id) VALUES(?, ?, ?) ";
			
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, t.getId());
			preparedStatement.setString(2, t.getTitle().toLowerCase());
			preparedStatement.setString(3, t.getDescription());
			int row = preparedStatement.executeUpdate();
			return row == 1;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
	}
	
	
	public List<Title> getTitles(final String likeTitle) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, title, description FROM titleRef WHERE title like ? ";
		List<Title> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, '%' + likeTitle.toLowerCase() + '%');
			
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() )	{
				Title b = new Title(rs.getInt("id"), 
									rs.getString("title"), 
									rs.getString("description"));
				ret.add(b);
			}
			rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public List<Title> getTitle(final String title) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, title, description FROM titleRef WHERE title = ? ";
		List<Title> ret = new ArrayList<>();
			
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, title.toLowerCase());
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() )	{
				Title b = new Title(rs.getInt("id"), 
									rs.getString("title"), 
									rs.getString("description"));
				ret.add(b);
			}
			rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	public Title getTitle(final long id) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT id, title, description FROM titleRef WHERE id = ? ";
		Title ret = null;
			
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() )	{
				ret = new Title(rs.getInt("id"), 
								rs.getString("title"), 
								rs.getString("description"));

			}
			rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, className, methodName));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
		}
		
		return ret;
	}
	
	
	
}

