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


package com.widescope.restApi.repo;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;

import com.widescope.restApi.repo.Objects.headers.HeaderValue;
import com.widescope.restApi.repo.Objects.headers.NewUserHeader;
import com.widescope.restApi.repo.Objects.headers.NewUserHeaders;
import com.widescope.restApi.repo.Objects.restApiRequest.UserRestApiRequest;
import com.widescope.restApi.repo.Objects.restApiRequest.UserRestApiRequestDetail;
import com.widescope.restApi.repo.Objects.restApiRequest.UserRestApiRequestDetailList;



@Component
public class RestApiDb {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./restApiRepo;MODE=PostgreSQL";  
	
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public RestApiDb()	{}
	
		   
	
	private 
	void 
	closeHandles(	Connection conn, 
					Statement statement, 
					ResultSet rs){
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
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
			closeHandles(conn, statement, null);
	    } 
	}


	
	
	public 
	static 
	void generateSchema() throws Exception {
		try {
			List<String> ddlList = new java.util.ArrayList<>();

			ddlList.add(RestApiDb.userHeaderTableTable);
			ddlList.add(RestApiDb.userHeaderTableIndex1);
			ddlList.add(RestApiDb.userHeaderValueTable);
			ddlList.add(RestApiDb.userHeaderValueTableIndex1);
			ddlList.add(RestApiDb.userHeaderValueTableFk1);

			ddlList.add(RestApiDb.userRestApiRequestTable);
			ddlList.add(RestApiDb.userRestApiRequestTableIndex1);
			ddlList.add(RestApiDb.userRestApiRequestTable_const1);

			RestApiDb restApiDb = new RestApiDb();
			restApiDb.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	

	
	/*NEW USER HEADERS*/
	
	public 
	static 
	String 
	userHeaderTableTable = "CREATE TABLE IF NOT EXISTS userHeaderTable (id BIGINT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1000) PRIMARY KEY,\r\n"
																+ "	name VARCHAR(999),"
																+ " userId BIGINT)";
	
	public 
	static 
	String 
	userHeaderTableIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idxNewUserHeader1 ON userHeaderTable(name, userId);";
	
	
	/*NEW USER HEADER VALUES*/
	
	public 
	static 
	String 
	userHeaderValueTable = "CREATE TABLE IF NOT EXISTS userHeaderValueTable (id BIGINT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																			+ "	userHeaderId BIGINT,"
																			+ " val VARCHAR(999) )";
	
	
	public 
	static 
	String 
	userHeaderValueTableIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idxUserHeaderValueTable1 ON userHeaderValueTable(userHeaderId, val);";

	
	public 
	static 
	String 
	userHeaderValueTableFk1 = "ALTER TABLE userHeaderValueTable ADD CONSTRAINT IF NOT EXISTS userHeaderValueTableFk1 FOREIGN KEY ( userHeaderId ) REFERENCES userHeaderValueTable( id );";

	
	
	/* SAVED USER REST API REQUESTS */

	public 
	static 
	String 
	userRestApiRequestTable = "CREATE TABLE IF NOT EXISTS userRestApiRequestTable (id BIGINT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																				+ "	name VARCHAR(999),"
																				+ "	description VARCHAR(999),"
																				+ " verbId INT,"
																				+ " userId BIGINT,"
																				+ " userRestApiRequest VARCHAR(9999)"
																				+ ")";
	
	public 
	static 
	String 
	userRestApiRequestTableIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idxUserRestApiRequestTable1 ON userRestApiRequestTable(name, userId);";
	
		
	
	public 
	static 
	String 
	userRestApiRequestTableIndex3 = "CREATE INDEX IF NOT EXISTS idxUserRestApiRequestTable1 ON userRestApiRequestTable(userId);";

	private
	static
	String 
	userRestApiRequestTable_const1 = "ALTER TABLE userRestApiRequestTable ADD CONSTRAINT IF NOT EXISTS ckuserRestApiRequestTable1  CHECK (verbId IN (1, 2, 3, 4, 5, 6, 7, 8, 9) );";

	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public 
	NewUserHeaders 
	getNewHeaders(final long userId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, name, userId FROM userHeaderTable WHERE userId = ?";
		NewUserHeaders newHeaders = new NewUserHeaders();
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            
			preparedStatement.setLong(1, userId);
			ResultSet rSet = preparedStatement.executeQuery(sqlString);
            while ( rSet.next() ) {
            	NewUserHeader header = new NewUserHeader(	rSet.getLong("id"), 
            												rSet.getString("name"), 
            												rSet.getLong("userId"),
            												new ArrayList<>());
            	newHeaders.addNewHeader(header);
            }
            rSet.close();
            return newHeaders;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public long 
	deleteUserHeaderTable( final long userId) throws Exception	{
		long ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE userHeaderTable WHERE userId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
            ret = preparedStatement.executeUpdate();
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public long 
	addUserHeader(final String name, 
				 final long userId) throws Exception {
		long id = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO userHeaderTable (name, userId) VALUES(?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, userId);
            
            preparedStatement.executeUpdate();
            String sqlStringGetId = "SELECT id FROM userHeaderTable WHERE name = ? AND userId = ?";
    		try (PreparedStatement preparedStatement2 = conn.prepareStatement(sqlStringGetId)) {
    			
    			preparedStatement2.setString(1, name);
    			preparedStatement2.setLong(2, userId);
    			
                ResultSet rs = preparedStatement2.executeQuery();
				if(rs.next()) {
					id =  rs.getInt("id");
				}
                rs.close();
			} catch (SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
			} catch (Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
            return id;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	List<HeaderValue> 
	getUserHeaderValues(final long userHeaderId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, val FROM userHeaderValueTable WHERE userHeaderId = ?";
		List<HeaderValue>  headerValueList = new ArrayList<HeaderValue> ();
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            
			preparedStatement.setLong(1, userHeaderId);
			ResultSet rSet = preparedStatement.executeQuery(sqlString);
            while ( rSet.next() ) {
            	HeaderValue headerValue = new HeaderValue(rSet.getInt("id"), rSet.getString("val"));
            	headerValueList.add(headerValue);
            }
            rSet.close();
            return headerValueList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
		
	public int 
	deleteUserHeaderValue( final long newUserHeaderId) throws Exception	{
		int ret = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE userHeaderTableValue WHERE userHeaderId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, newUserHeaderId);
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
	addUserHeaderValue(	final long userHeaderId, 
				 		final String value) throws Exception {
		int id = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO userHeaderValueTable (userHeaderId, val) VALUES(?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
            preparedStatement.setLong(1, userHeaderId);
            preparedStatement.setString(2, value);
            
            preparedStatement.executeUpdate();
            String sqlStringGetId = "SELECT id FROM userHeaderValueTable WHERE userHeaderId = ? AND val = ?";
    		try (PreparedStatement preparedStatement2 = conn.prepareStatement(sqlStringGetId)) {
    			preparedStatement2.setLong(1, userHeaderId);
    			preparedStatement2.setString(2, value);
                ResultSet rs = preparedStatement2.executeQuery();
				if(rs.next()) {
					id =  rs.getInt("id");
				}
                rs.close();
			} catch (SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
			} catch (Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
            return id;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	UserRestApiRequestDetailList 
	getUserRestApiRequests(final long userId, boolean isContent) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, name, description, verbId, userId, userRestApiRequest FROM userRestApiRequestTable WHERE userId = ?";
		UserRestApiRequestDetailList  userRequestDetailList = new UserRestApiRequestDetailList ();
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            
			preparedStatement.setLong(1, userId);
			ResultSet rSet = preparedStatement.executeQuery(sqlString);
            while ( rSet.next() ) {
            	UserRestApiRequestDetail userRequestDetail =null;
            	if(isContent) {
	            	userRequestDetail 
	            	= new UserRestApiRequestDetail(rSet.getLong("id"), 
	            							rSet.getString("name"),
	            							rSet.getString("description"),
	            							rSet.getInt("verbId"),
	            							rSet.getLong("userId"),
	            							UserRestApiRequest.toUserRestApiRequest(rSet.getString("userRestApiRequest"))
	            							);
            	} else {
   	            	userRequestDetail 
   	            	= new UserRestApiRequestDetail(rSet.getLong("id"), 
   	            							rSet.getString("name"),
   	            							rSet.getString("description"),
   	            							rSet.getInt("verbId"),
   	            							rSet.getLong("userId"),
   	            							null
   	            							);
            	}
            	userRequestDetailList.addUserRequestDetail(userRequestDetail);
            }
            rSet.close();
            return userRequestDetailList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	UserRestApiRequestDetail 
	getUserRestApiRequest(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, name, description, verbId, userId, userRestApiRequest FROM userRestApiRequestTable WHERE id = ?";
		UserRestApiRequestDetail  userRequestDetail = new UserRestApiRequestDetail ();
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            
			preparedStatement.setLong(1, id);
			ResultSet rSet = preparedStatement.executeQuery(sqlString);
			if(rSet.next()) {
				userRequestDetail
						= new UserRestApiRequestDetail(rSet.getLong("id"),
						rSet.getString("name"),
						rSet.getString("description"),
						rSet.getInt("verbId"),
						rSet.getLong("userId"),
						UserRestApiRequest.toUserRestApiRequest(rSet.getString("userRestApiRequest"))
				);
			}
            rSet.close();
            return userRequestDetail;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public long 
	deleteUserRestApiRequest( final long id) throws Exception {
		long ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE userRestApiRequestTable WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
            ret = preparedStatement.executeUpdate();
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public long 
	addUserRestApiRequest(	final String name, 
							final String description,
						 	final int verbId,
						 	final long userId,
						 	final UserRestApiRequest userRestApiRequest
						 	) throws Exception {
		long id = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO userRestApiRequestTable (name, description, verbId, userId, userRestApiRequest ) VALUES(?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, verbId);
            preparedStatement.setLong(4, userId);
            preparedStatement.setString(5, userRestApiRequest.toString());
            
            preparedStatement.executeUpdate();
            String sqlStringGetId = "SELECT id FROM userRestApiRequestTable WHERE name = ? AND userId = ?";
    		try (PreparedStatement preparedStatement2 = conn.prepareStatement(sqlStringGetId)) {
    			
    			preparedStatement2.setString(1, name);
    			preparedStatement2.setLong(2, userId);
    			
                ResultSet rs = preparedStatement2.executeQuery();
				if(rs.next()) {
					id =  rs.getInt("id");
				}
                rs.close();
			} catch (SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
			} catch (Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
            return id;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	

	
	
}
