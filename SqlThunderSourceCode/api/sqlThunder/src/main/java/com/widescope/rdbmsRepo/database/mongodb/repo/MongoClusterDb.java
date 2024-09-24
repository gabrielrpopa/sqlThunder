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

import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;

import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationTable;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationTableList;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationToCollectionTable;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationToCollectionTableList;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationToQueryTable;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationToQueryTableList;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoCollectionTable;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoCollectionTableList;
import com.widescope.rdbmsRepo.database.mongodb.sql.query.RangeMongoSearch;
import com.widescope.rdbmsRepo.database.mongodb.sql.query.SimpleMongoSearch;



@Component
public class MongoClusterDb {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./mongoRepo;MODE=PostgreSQL";  
	
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public MongoClusterDb()	{}
	
		   
	
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
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement();
			for (String ddl : ddlList) {
				statement.executeUpdate(ddl);
	        }
			
			statement.close();
			conn.commit();
			conn.close();
	    } catch(SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
	    } catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
	    } finally	{ 
			closeHandles(conn, statement, null);
	    } 
	}

	
		

	
	
	public 
	static 
	void generateSchema() throws Exception {
		List<String> ddlList = new java.util.ArrayList<String>();
		ddlList.add(MongoClusterDb.mongoRef);
		ddlList.add(MongoClusterDb.mongoRef_index1);
		ddlList.add(MongoClusterDb.mongoRef_index2);
		ddlList.add(MongoClusterDb.mongoRef_server);
		//ddlList.add(MongoClusterDb.mongoRef_server_1);
		ddlList.add(MongoClusterDb.mongoRef_const1);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable);
		
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_const1);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_const2);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_const3);
		
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_index2);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_insert1);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_insert2);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_insert3);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_insert4);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_insert5);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_fk1);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_const1);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_const2);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_index1);
		
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert1);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert2);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert3);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert4);
		
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert5);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert6);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert7);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert8);
		
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert9);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert10);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert11);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert12);
		ddlList.add(MongoClusterDb.repoDynamicMqlTable_param_insert13);
		
		
		ddlList.add(MongoClusterDb.repoMqlToClusterTable);
		ddlList.add(MongoClusterDb.repoMqlToClusterTable_index0);
		ddlList.add(MongoClusterDb.repoMqlToClusterTable_index1);
		ddlList.add(MongoClusterDb.repoDslToClusterTable_index2);
		ddlList.add(MongoClusterDb.repoMqlToClusterTable_fk1);
		ddlList.add(MongoClusterDb.repoMqlToClusterTable_fk2);
		/*Associations*/
		ddlList.add(MongoClusterDb.repoAssociationTable);
		ddlList.add(MongoClusterDb.repoAssociationTable_index0 );
		ddlList.add(MongoClusterDb.repoAssociationToQueryTable);
		ddlList.add(MongoClusterDb.repoAssociationToQueryTable_index0);

		ddlList.add(MongoClusterDb.repoCollectionTable);
		ddlList.add(MongoClusterDb.repoCollectionTable_index0);

		ddlList.add(MongoClusterDb.repoAssociationToCollectionTable);
		ddlList.add(MongoClusterDb.repoAssociationToCollectionTable_index0);
		
		ddlList.add(MongoClusterDb.repoBucketsTable);
		ddlList.add(MongoClusterDb.repoBucketsTable_index1);
				
		MongoClusterDb mongoClusterDb = new MongoClusterDb();
		mongoClusterDb.createSchema(ddlList);
	}
	

	
	////////////////////////////////////// MONGOREF table - Describes connection to Clusters/Servers///////////////////////////////////////////////////////////////////////////////////////////
	public static String mongoRef = "CREATE TABLE IF NOT EXISTS mongoRef (clusterId INT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																		+ "	uniqueName VARCHAR(999), \r\n"
																		+ "	connString VARCHAR(999), \r\n"
																		+ "	storageType VARCHAR(999) DEFAULT 'HISTORY', \r\n"
																		+ "	controllerId INT, \r\n"
																		+ "	startPeriod BIGINT, \r\n"
																		+ "	endPeriod BIGINT, \r\n"
																		+ "	tunnelLocalPort VARCHAR(99),\r\n"
																		+ "	tunnelRemoteHostAddress VARCHAR(999),\r\n"
																		+ "	tunnelRemoteHostPort VARCHAR(99),\r\n"
																		+ "	tunnelRemoteHostUser VARCHAR(999),\r\n"
																		+ "	tunnelRemoteHostUserPassword VARCHAR(999),\r\n"
																		+ "	tunnelRemoteHostRsaKey VARCHAR(999)\r\n"
																		+ ")";
		
	public static String mongoRef_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_mongoRef_1 ON mongoRef(uniqueName);";
	public static String mongoRef_index2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_mongoRef_2 ON mongoRef(connString);";
	public static String mongoRef_const1 = "ALTER TABLE mongoRef ADD CONSTRAINT IF NOT EXISTS ck_mongoRef_1  CHECK (storageType IN ('HISTORY', 'TRANSACTIONAL', 'WAREHOUSE') );";

	public static String mongoLocalDb = "localMongoDb";
	public static String mongoRef_server = "MERGE INTO mongoRef (uniqueName,  "
											+ "connString, "
											+ "storageType, "
											+ "controllerId, "
											+ "startPeriod, "
											+ "endPeriod, "
											+ "tunnelLocalPort, "
											+ "tunnelRemoteHostAddress, "
											+ "tunnelRemoteHostPort, "
											+ "tunnelRemoteHostUser, "
											+ "tunnelRemoteHostUserPassword, "
											+ "tunnelRemoteHostRsaKey) KEY(uniqueName) "
											+ "VALUES('" + mongoLocalDb + "', "
											+ "'mongodb://localhost:27017', "
											+ "'HISTORY', "
											+ "0, "
											+ "-1, "
											+ "-1, "
											+ "'',"
											+ "'',"
											+ "'',"
											+ "'',"
											+ "'',"
											+ "'')";


	/*
	public static String mongoRef_server_1 = "MERGE INTO mongoRef (uniqueName,  "
												+ "connString, "
												+ "storageType, "
												+ "controllerId, "
												+ "startPeriod, "
												+ "endPeriod, "
												+ "tunnelLocalPort, "
												+ "tunnelRemoteHostAddress, "
												+ "tunnelRemoteHostPort, "
												+ "tunnelRemoteHostUser, "
												+ "tunnelRemoteHostUserPassword, "
												+ "tunnelRemoteHostRsaKey) KEY(uniqueName) "
												+ "VALUES('ChatterMongoDb', "
												+ "'mongodb://thinkcentre2019:27017', "
												+ "'CHATTER', "
												+ "0, "
												+ "-1, "
												+ "-1, "
												+ "'',"
												+ "'',"
												+ "'',"
												+ "'',"
												+ "'',"
												+ "'')";
	
	*/
	
	
	public static String 
	repoBucketsTable = "CREATE TABLE IF NOT EXISTS repoBucketsTable (bucketId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
															+ "	clusterId BIGINT,\r\n"
															+ "	dbName VARCHAR(99) ,\r\n"
															+ "	bucketName VARCHAR(99) )";
	
	public static String 
	repoBucketsTable_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoBucketsTable_1 ON repoBucketsTable(clusterId, dbName, bucketName);";
	
	
	public static String 
	repoDynamicMqlTable = "CREATE TABLE IF NOT EXISTS repoDynamicMqlTable (\r\n"
																+ "	mqlId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																+ "	clusterId BIGINT,\r\n"
																+ "	mqlReturnType VARCHAR(99) ,\r\n"
																+ "	mqlCategory VARCHAR(99) ,\r\n"
																+ "	mqlClass VARCHAR(99) ,\r\n"
																+ "	type VARCHAR(99) ,\r\n"
																+ "	mqlName VARCHAR(99),\r\n"
																+ "	mqlDescription VARCHAR(999),\r\n"
																+ "	mqlContent CLOB,\r\n"
																+ "	active int DEFAULT 1)";




	
	public static String 
	repoDynamicMqlTable_const1 = "ALTER TABLE repoDynamicMqlTable ADD CONSTRAINT IF NOT EXISTS ck_repoDynamicMqlTable_1 "
							+ "CHECK (mqlReturnType IN ('Bson', 'Raw') );";
	
	public static String 
	repoDynamicMqlTable_const2 = "ALTER TABLE repoDynamicMqlTable ADD CONSTRAINT IF NOT EXISTS ck_repoDynamicMqlTable_2 "
							+ "CHECK (mqlClass IN ('SimpleMongoSearch', 'RangeMongoSearch', 'ComplexAndMongoSearch', 'Adhoc') );";
	
	public static String 
	repoDynamicMqlTable_const3 = "ALTER TABLE repoDynamicMqlTable ADD CONSTRAINT IF NOT EXISTS ck_repoDynamicMqlTable_3 "
							+ "CHECK (type IN ('Query', 'Delete', 'Update', 'Insert') );";


	
	public static String 
	repoDynamicMqlTable_index2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoDynamicMqlTable_2 ON repoDynamicMqlTable(mqlName);";


	private static String s1 = new SimpleMongoSearch().toJsonString();
	
	private static String s2 = new RangeMongoSearch().toString();
	
	public static String 
	repoDynamicMqlTable_insert1 = "INSERT INTO repoDynamicMqlTable(clusterId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active) "
														+ "VALUES(1, 'Bson', 'TEST', 'SimpleMongoSearch', 'Query' , 'SimpleMongoSearch', 'Default SimpleMongoSearch', '" + s1 + "', 1);";
	
	public static String 
	repoDynamicMqlTable_insert2 = "INSERT INTO repoDynamicMqlTable(clusterId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active) "
														+ "VALUES(2, 'Bson', 'TEST', 'RangeMongoSearch', 'Query' , 'RangeMongoSearch', 'Default RangeMongoSearch', '" + s2 + "', 1);";

	public static String 
	repoDynamicMqlTable_insert3 = "INSERT INTO repoDynamicMqlTable(clusterId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active) "
														+ "VALUES(3, 'Bson', 'TEST', 'Adhoc', 'Query' , 'AdhocInList', 'Example Adhoc Query In List', '{ \"age\": { \"$in\": [ @value1@, @value2@ ] } }', 1);";

	
	public static String 
	repoDynamicMqlTable_insert4 = "INSERT INTO repoDynamicMqlTable(clusterId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active) "
														+ "VALUES(4, 'Bson', 'TEST', 'Adhoc', 'Query' , 'AdhocGreaterThan', 'Example Adhoc Query Greater Than', '{ \"age\": { \"$gt\": @value@ } }', 1);";

	
	public static String 
	repoDynamicMqlTable_insert5 = "INSERT INTO repoDynamicMqlTable(clusterId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active) "
														+ "VALUES(5, 'Bson', 'TEST', 'Adhoc', 'Query' , 'AdhocBetween', 'Example Adhoc Query Between Range Of Values ', '{\"age\": {\"$gt\": @from@, \"$lt\": @to@}}', 1);";

	

	public static String 
	repoDynamicMqlTable_param = """
            CREATE TABLE IF NOT EXISTS repoDynamicMqlParamTable (\r
            	mqlParamId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	mqlId BIGINT,\r
            	mqlParamName VARCHAR(99),\r
            	mqlParamDefaultValue VARCHAR(999) ,\r
            	mqlParamType VARCHAR(20) ,\r
            	mqlParamPosition VARCHAR(20),\r
            	mqlParamOrder INT)""";
	

	
	public static String 
	repoDynamicMqlTable_param_fk1 = "ALTER TABLE repoDynamicMqlParamTable ADD CONSTRAINT IF NOT EXISTS repoDynamicMqlParamTableFk_2 FOREIGN KEY ( mqlId ) REFERENCES repoDynamicMqlTable( mqlId );";
	
	public static String 
	repoDynamicMqlTable_param_const1 = "ALTER TABLE repoDynamicMqlParamTable ADD CONSTRAINT IF NOT EXISTS ck_repoDynamicMqlTable_param_1 "
									+ "CHECK (mqlParamType IN ('INTEGER', 'DOUBLE', 'FLOAT', 'LONG', 'STRING') );";
	public static String 
	repoDynamicMqlTable_param_const2 = "ALTER TABLE repoDynamicMqlParamTable ADD CONSTRAINT IF NOT EXISTS ck_repoDynamicMqlParamTable_2 "
									+ "CHECK (mqlParamPosition IN ('INPUT', 'OUTPUT') );";
	
	public static String 
	repoDynamicMqlTable_param_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoDynamicMqlParamTable_1 ON repoDynamicMqlParamTable(mqlId, mqlParamName);";
	
	

	public static String 
	repoDynamicMqlTable_param_insert1 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder)"
			+ " VALUES(1, '@itemToSearch@', 'age', 'STRING' , 'INPUT', 0);";
	
	public static String
	repoDynamicMqlTable_param_insert2 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(1, '@valueToSearch@', '30', 'INTEGER' , 'INPUT', 1);";

	public static String
	repoDynamicMqlTable_param_insert3 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(1, '@operator@', '$gt', 'STRING' , 'INPUT', 2);";

	public static String
	repoDynamicMqlTable_param_insert4 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(1, '@valueToSearchType@', 'INTEGER', 'STRING' , 'INPUT', 3);";

	
	
	
	
	public static String 
	repoDynamicMqlTable_param_insert5 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder)"
			+ " VALUES(2, '@itemToSearch@', 'age', 'STRING' , 'INPUT', 0);";
	
	public static String
	repoDynamicMqlTable_param_insert6 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(2, '@fromValue@', '0', 'INTEGER' , 'INPUT', 1);";

	public static String
	repoDynamicMqlTable_param_insert7 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(2, '@toValue@', '30', 'INTEGER' , 'INPUT', 2);";

	public static String
	repoDynamicMqlTable_param_insert8 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(2, '@valueSearchType@', 'INTEGER', 'STRING' , 'INPUT', 3);";

	
	public static String
	repoDynamicMqlTable_param_insert9 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(3, '@value1@', '26', 'INTEGER' , 'INPUT', 0);";

	public static String
	repoDynamicMqlTable_param_insert10 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(3, '@value2@', '51', 'INTEGER' , 'INPUT', 1);";


	public static String
	repoDynamicMqlTable_param_insert11 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(4, '@value@', '0', 'INTEGER' , 'INPUT', 0);";

	
	public static String
	repoDynamicMqlTable_param_insert12 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(5, '@from@', '0', 'INTEGER' , 'INPUT', 0);";
	
	public static String
	repoDynamicMqlTable_param_insert13 = "INSERT INTO repoDynamicMqlParamTable(mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) "
			+ "VALUES(5, '@to@', '100', 'INTEGER' , 'INPUT', 1);";
	
	
	
	
	public static String 
	repoMqlToClusterTable = """
            CREATE TABLE IF NOT EXISTS repoMqlToClusterTable (\r
            	id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	mqlId BIGINT,\r
            	clusterId BIGINT,\r
            	active int DEFAULT 1)""";
	public static String 
	repoMqlToClusterTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repomqlToClusterTable_1 ON repoMqlToClusterTable(mqlId, clusterId);";

	
	public static String 
	repoMqlToClusterTable_index1 = "CREATE INDEX IF NOT EXISTS idx_repoMqlToClusterTable_1 ON repoMqlToClusterTable(clusterId) ;";
	public static String 
	repoDslToClusterTable_index2 = "CREATE INDEX IF NOT EXISTS idx_repoMqlToClusterTable_2 ON repoMqlToClusterTable(mqlId) ;";
	
	public static String 
	repoMqlToClusterTable_fk1 = "ALTER TABLE repoMqlToClusterTable ADD CONSTRAINT IF NOT EXISTS repoMqlToClusterTable_fk_1 FOREIGN KEY ( mqlId ) REFERENCES repoDynamicMqlTable( mqlId );";

	public static String 
	repoMqlToClusterTable_fk2 = "ALTER TABLE repoMqlToClusterTable ADD CONSTRAINT IF NOT EXISTS repoMqlToClusterTable_fk_2 FOREIGN KEY ( clusterId ) REFERENCES mongoRef( clusterId );";


	/* collections Association  */

	public static String 
	repoAssociationTable = """
            CREATE TABLE IF NOT EXISTS repoAssociationTable (\r
            	associationId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	associationName VARCHAR(100))""";

	public static String 
	repoAssociationTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoAssociationTable_1 ON repoAssociationTable(associationName);";

	
	public static String 
	repoAssociationToQueryTable = """
            CREATE TABLE IF NOT EXISTS repoAssociationToQueryTable (\r
            	associationToQueryId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	associationId BIGINT,\
            	queryId BIGINT)""";

	public static String 
	repoAssociationToQueryTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoAssociationToQueryTable_1 ON repoAssociationToQueryTable(associationId, queryId);";


	
	public static String 
	repoCollectionTable = """
            CREATE TABLE IF NOT EXISTS repoCollectionTable (\r
            	collectionId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	clusterId BIGINT,\
            	collectionName VARCHAR(100))""";

	public static String 
	repoCollectionTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoCollectionTable_1 ON repoCollectionTable(collectionName);";

	
	
	
	
	
	public static String 
	repoAssociationToCollectionTable = """
            CREATE TABLE IF NOT EXISTS repoAssociationToCollectionTable (\r
            	associationToCollectionId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	associationId BIGINT,\
            	collectionId BIGINT)""";

	public static String 
	repoAssociationToCollectionTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoAssociationToCollectionTable_1 ON repoAssociationToCollectionTable(associationId, collectionId);";

	public
	MongoClusterRecord 
	getCluster(final String uniqueName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT clusterId, "
								+ "uniqueName, "
								+ "connString, "
								+ "storageType, "
								+ "controllerId, "
								+ "startPeriod, "
								+ "endPeriod, "
								+ "tunnelLocalPort, "
								+ "tunnelRemoteHostAddress, "
								+ "tunnelRemoteHostPort, "
								+ "tunnelRemoteHostUser, "
								+ "tunnelRemoteHostUserPassword, "
								+ "tunnelRemoteHostRsaKey "
								+ "FROM mongoRef WHERE uniqueName = ?";
		
		MongoClusterRecord mongoClusterRecord = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, uniqueName);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				mongoClusterRecord = new MongoClusterRecord(rs.getInt("clusterId"),
															rs.getString("uniqueName"),
															rs.getString("connString"),
															rs.getString("storageType"),
															rs.getInt("controllerId"),
															rs.getLong("startPeriod"),
															rs.getLong("endPeriod"),
															rs.getString("tunnelLocalPort"),
															rs.getString("tunnelRemoteHostAddress"),
															rs.getString("tunnelRemoteHostPort"),
															rs.getString("tunnelRemoteHostUser"),
															rs.getString("tunnelRemoteHostUserPassword"),
															rs.getString("tunnelRemoteHostRsaKey")
															);
			}
            rs.close();
            return mongoClusterRecord;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	MongoClusterRecord 
	getCluster(final int id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT clusterId, "
								+ "uniqueName, "
								+ "connString, "
								+ "storageType, "
								+ "controllerId, "
								+ "startPeriod, "
								+ "endPeriod, "
								+ "tunnelLocalPort, "
								+ "tunnelRemoteHostAddress, "
								+ "tunnelRemoteHostPort, "
								+ "tunnelRemoteHostUser, "
								+ "tunnelRemoteHostUserPassword, "
								+ "tunnelRemoteHostRsaKey "
								+ "FROM mongoRef WHERE clusterId = ?";
		
		MongoClusterRecord mongoClusterRecord = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				mongoClusterRecord = new MongoClusterRecord(rs.getInt("clusterId"),
															rs.getString("uniqueName"),
															rs.getString("connString"),
															rs.getString("storageType"),
															rs.getInt("controllerId"),
															rs.getLong("startPeriod"),
															rs.getLong("endPeriod"),
															rs.getString("tunnelLocalPort"),
															rs.getString("tunnelRemoteHostAddress"),
															rs.getString("tunnelRemoteHostPort"),
															rs.getString("tunnelRemoteHostUser"),
															rs.getString("tunnelRemoteHostUserPassword"),
															rs.getString("tunnelRemoteHostRsaKey")
															);
			}

            rs.close();
            return mongoClusterRecord;
        } catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
	}
	
	public List<MongoClusterDbRecord> getAll() throws Exception	{
		Class.forName(JDBC_DRIVER);
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);) {
            List<MongoClusterDbRecord> mongoClusterDbRecordList = new ArrayList<>();
            String sqlString = "SELECT clusterId, uniqueName, connString, storageType, controllerId, startPeriod, endPeriod FROM mongoRef";
        
	            
            Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlString);
            while ( rs.next() ) {
            	MongoClusterDbRecord syslogCompressed = new MongoClusterDbRecord(	rs.getInt("id"), 
															            			rs.getString("uniqueName"), 
															            			rs.getString("connString"),
															            			rs.getString("storageType"),
															            			rs.getInt("controllerId"),
															            			rs.getLong("startPeriod"),
															            			rs.getLong("endPeriod")
			            			                                              );
            	mongoClusterDbRecordList.add(syslogCompressed);
            }
            rs.close();
            return mongoClusterDbRecordList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	Map<String, MongoClusterRecord> 
	getAllCluster() throws Exception {
		Class.forName(JDBC_DRIVER);
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); ) {
			Map<String, MongoClusterRecord> mongoClusterDbRecordMap = new HashMap<>();
            String sqlString = "SELECT clusterId, "
            						+ "uniqueName, "
            						+ "connString, "
            						+ "storageType, "
            						+ "controllerId, "
            						+ "startPeriod, "
            						+ "endPeriod, "
    								+ "tunnelLocalPort, "
    								+ "tunnelRemoteHostAddress, "
    								+ "tunnelRemoteHostPort, "
    								+ "tunnelRemoteHostUser, "
    								+ "tunnelRemoteHostUserPassword, "
    								+ "tunnelRemoteHostRsaKey "
            						+ "FROM mongoRef";
        
	            
            Statement preparedStatement = conn.createStatement();
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	MongoClusterRecord mongoClusterRecord = new MongoClusterRecord(	rs.getInt("clusterId"), 
														            			rs.getString("uniqueName"), 
														            			rs.getString("connString"),
														            			rs.getString("storageType"),
														            			rs.getInt("controllerId"),
														            			rs.getLong("startPeriod"),
														            			rs.getLong("endPeriod"),
														            			rs.getString("tunnelLocalPort"),
														            			rs.getString("tunnelRemoteHostAddress"),
														            			rs.getString("tunnelRemoteHostPort"),
														            			rs.getString("tunnelRemoteHostUser"),
														            			rs.getString("tunnelRemoteHostUserPassword"),
														            			rs.getString("tunnelRemoteHostRsaKey")
			            			                                            );
            	mongoClusterDbRecordMap.put(mongoClusterRecord.getUniqueName(), mongoClusterRecord) ;
            }
            rs.close();
            return mongoClusterDbRecordMap;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	
	
	public 
	void 
	addCluster(final MongoClusterRecord mongoClusterRecord)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "INSERT INTO mongoRef (uniqueName, "
												+ "connString, "
												+ "storageType, "
												+ "controllerId, "
												+ "startPeriod, "
												+ "endPeriod,"
												+ "tunnelLocalPort, "
			    								+ "tunnelRemoteHostAddress, "
			    								+ "tunnelRemoteHostPort, "
			    								+ "tunnelRemoteHostUser, "
			    								+ "tunnelRemoteHostUserPassword, "
			    								+ "tunnelRemoteHostRsaKey "
												+ ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, mongoClusterRecord.getUniqueName());
			preparedStatement.setString(2, mongoClusterRecord.getConnString());
			preparedStatement.setString(3, mongoClusterRecord.getStorageType());
			preparedStatement.setInt(4, mongoClusterRecord.getControllerId());
			preparedStatement.setLong(5, mongoClusterRecord.getStartPeriod());
			preparedStatement.setLong(6, mongoClusterRecord.getEndPeriod());
			
			preparedStatement.setString(7, mongoClusterRecord.getTunnelLocalPort());
			preparedStatement.setString(8, mongoClusterRecord.getTunnelRemoteHostAddress());
			preparedStatement.setString(9, mongoClusterRecord.getTunnelRemoteHostPort());
			preparedStatement.setString(10, mongoClusterRecord.getTunnelRemoteUser());
			preparedStatement.setString(11, mongoClusterRecord.getTunnelRemoteUserPassword());
			preparedStatement.setString(12, mongoClusterRecord.getTunnelRemoteRsaKey());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	void 
	updateCluster(final MongoClusterRecord mongoClusterRecord) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "UPDATE mongoRef SET uniqueName = ?,"
											+ "connString = ?, "
											+ "storageType = ?, "
											+ "controllerId = ?, "
											+ "startPeriod = ?, "
											+ "endPeriod = ?, "
											+ "tunnelLocalPort = ?, "
		    								+ "tunnelRemoteHostAddress = ?, "
		    								+ "tunnelRemoteHostPort = ?, "
		    								+ "tunnelRemoteHostUser = ?, "
		    								+ "tunnelRemoteHostUserPassword = ?, "
		    								+ "tunnelRemoteHostRsaKey = ? "
											+ "WHERE clusterId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			preparedStatement.setString(1, mongoClusterRecord.getUniqueName());
			preparedStatement.setString(2, mongoClusterRecord.getConnString());
			preparedStatement.setString(3, mongoClusterRecord.getStorageType());
			preparedStatement.setInt(4, mongoClusterRecord.getControllerId());
			preparedStatement.setLong(5, mongoClusterRecord.getStartPeriod());
			preparedStatement.setLong(6, mongoClusterRecord.getEndPeriod());
			preparedStatement.setString(7, mongoClusterRecord.getTunnelLocalPort());
			preparedStatement.setString(8, mongoClusterRecord.getTunnelRemoteHostAddress());
			preparedStatement.setString(9, mongoClusterRecord.getTunnelRemoteHostPort());
			preparedStatement.setString(10, mongoClusterRecord.getTunnelRemoteUser());
			preparedStatement.setString(11, mongoClusterRecord.getTunnelRemoteUserPassword());
			preparedStatement.setString(12, mongoClusterRecord.getTunnelRemoteRsaKey());
			preparedStatement.setInt(13, mongoClusterRecord.getClusterId());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public 
	void 
	deleteCluster(final String uniqueName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE FROM mongoRef WHERE uniqueName=?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setString(1, uniqueName);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
////////////////////////////Mongo MQL Tables //////////////////////////////////////////////////////
	
	public List<MongoRepoDynamicMql>
	getMql( final int mqlId) throws Exception {

		List<MongoRepoDynamicMql> ret = new ArrayList<>();
		String select = "SELECT mqlId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active FROM repoDynamicMqlTable WHERE mqlId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			preparedStatement.setInt(1, mqlId);
			
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				MongoRepoDynamicMql mongoRepoDynamicMql 
				= new MongoRepoDynamicMql(	rs.getInt("mqlId"), 
											rs.getString("mqlReturnType"), 
											rs.getString("mqlCategory"),
											rs.getString("mqlClass"),
											rs.getString("type"),
											rs.getString("mqlName"),
											rs.getString("mqlDescription"),
											rs.getString("mqlContent"),
											rs.getInt("active")
				       			          );
				
				mongoRepoDynamicMql.setMongoRepoMqlParamList(getMqlParams( mongoRepoDynamicMql.getMqlId()));
				ret.add(mongoRepoDynamicMql);
			}
			rs.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public List<MongoRepoDynamicMql>
	getMqlByName( final String mqlName) throws Exception {
		List<MongoRepoDynamicMql> ret = new ArrayList<>();
		String select = "SELECT mqlId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active FROM repoDynamicMqlTable WHERE mqlName = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			preparedStatement.setString(1, mqlName);
			
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				MongoRepoDynamicMql mongoRepoDynamicMql 
				= new MongoRepoDynamicMql(	rs.getInt("mqlId"), 
											rs.getString("mqlReturnType"), 
											rs.getString("mqlCategory"),
											rs.getString("mqlClass"),
											rs.getString("type"),
											rs.getString("mqlName"),
											rs.getString("mqlDescription"),
											rs.getString("mqlContent"),
											rs.getInt("active")
				       			          );
				
				mongoRepoDynamicMql.setMongoRepoMqlParamList(getMqlParams( mongoRepoDynamicMql.getMqlId()));
				ret.add(mongoRepoDynamicMql);
			}
			rs.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public List<MongoRepoDynamicMql> 
	getMql( final String strtoSearch) throws Exception {
		List<MongoRepoDynamicMql> ret = new ArrayList<>();
		String select;
		if(strtoSearch != null && !strtoSearch.isBlank() && !strtoSearch.isEmpty()) {
			select = "SELECT mqlId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active FROM repoDynamicMqlTable "
					+ " WHERE mqlName LIKE ? OR mqlDescription LIKE ? OR mqlContent LIKE ?";
		} else {
			select = "SELECT mqlId, mqlReturnType, mqlCategory, mqlClass, type, mqlName, mqlDescription, mqlContent, active FROM repoDynamicMqlTable ";	
		}
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			if(strtoSearch != null && !strtoSearch.isEmpty()) {
				preparedStatement.setString(1, "%" + strtoSearch + "%");
				preparedStatement.setString(2, "%" + strtoSearch + "%");
				preparedStatement.setString(3, "%" + strtoSearch + "%");
			}
			
			
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				MongoRepoDynamicMql mongoRepoDynamicMql = new MongoRepoDynamicMql(	rs.getInt("mqlId"), 
																					rs.getString("mqlReturnType"), 
																					rs.getString("mqlCategory"),
																					rs.getString("mqlClass"),
																					rs.getString("type"),
																					rs.getString("mqlName"),
																					rs.getString("mqlDescription"),
																					rs.getString("mqlContent"),
																					rs.getInt("active")
			       			                        								);
			
				mongoRepoDynamicMql.setMongoRepoMqlParamList(getMqlParams( mongoRepoDynamicMql.getMqlId()));
				ret.add(mongoRepoDynamicMql);
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
	mergeMql(	final String mqlReturnType, 
				final String mqlCategory,
				final String mqlClass,
				final String type,
				final String mqlName,
				final String mqlDescription,
				final String mqlContent,
				final int active) throws Exception {

 		String addSql = "MERGE INTO repoDynamicMqlTable(mqlReturnType, mqlCategory, mqlClass, type,  mqlName,  mqlDescription,  mqlContent,  active) "
						+ "	KEY (mqlName) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(addSql))	{
			
			preparedStatement.setString(1, mqlReturnType);
			preparedStatement.setString(2, mqlCategory);
			preparedStatement.setString(3, mqlClass);
			preparedStatement.setString(4, type);
			
			preparedStatement.setString(5, mqlName);
			preparedStatement.setString(6, mqlDescription);
			preparedStatement.setString(7, mqlContent);
			preparedStatement.setInt(8, active);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void 
	addMql(	final String mqlReturnType, 
				final String mqlCategory,
				final String mqlClass,
				final String type,
				final String mqlName,
				final String mqlDescription,
				final String mqlContent,
				final int active) throws Exception {

 		String addSql = "INSERT INTO repoDynamicMqlTable(mqlReturnType, mqlCategory, mqlClass, type,  mqlName,  mqlDescription,  mqlContent,  active) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(addSql))	{
			
			preparedStatement.setString(1, mqlReturnType);
			preparedStatement.setString(2, mqlCategory);
			preparedStatement.setString(3, mqlClass);
			preparedStatement.setString(4, type);
			
			preparedStatement.setString(5, mqlName);
			preparedStatement.setString(6, mqlDescription);
			preparedStatement.setString(7, mqlContent);
			preparedStatement.setInt(8, active);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public void 
	deleteMql(final int mqlId) throws Exception {
		if(mqlId <= 0)
			throw new Exception("Mql Id is null");
	
		String deleteDslParam = "DELETE repoDynamicMqlParamTable WHERE mqlId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
			
			preparedStatement.setInt(1, mqlId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		
		deleteMqlParams(mqlId);
		
	}
	
	
	public void 
	deleteMqlParams(final int mqlId) throws Exception {
		if(mqlId <= 0)
			throw new Exception("Mql Id is null");
		String deleteStm = "DELETE repoDynamicMqlTable  WHERE mqlId = ?" ;
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteStm))	{
			preparedStatement.setInt(1, mqlId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
	}
	
	
	public List<MongoRepoMqlParam> 
	getMqlParams( final long l) throws Exception {
		List<MongoRepoMqlParam> ret = new ArrayList<>();
		String select = "SELECT mqlParamId , mqlId , mqlParamName , mqlParamDefaultValue , mqlParamType , mqlParamPosition , mqlParamOrder  FROM repoDynamicMqlParamTable WHERE mqlId = ?";
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, l);
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				MongoRepoMqlParam p = new MongoRepoMqlParam(rs.getInt("mqlParamId") ,
						                                    rs.getInt("mqlId"),
													        rs.getString("mqlParamName"),
													        rs.getString("mqlParamDefaultValue"),
													        rs.getString("mqlParamType"),
													        rs.getString("mqlParamPosition"),
													        rs.getInt("mqlParamOrder") );
				ret.add(p);
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	public void 
	insertMqlParam(	final long mqlId,
					final String mqlParamName,
					final String mqlParamDefaultValue, 
					final String mqlParamType,
					final String mqlParamPosition,
					final int mqlParamOrder) throws Exception {
		String addParam = "INSERT INTO repoDynamicMqlParamTable (mqlId, mqlParamName, mqlParamDefaultValue, mqlParamType, mqlParamPosition, mqlParamOrder) VALUES (?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(addParam))	{
			
			preparedStatement.setLong(1, mqlId);
			preparedStatement.setString(2, mqlParamName);
			preparedStatement.setString(3, mqlParamDefaultValue);
			preparedStatement.setString(4, mqlParamType);
			preparedStatement.setString(5, mqlParamPosition);
			preparedStatement.setInt(6, mqlParamOrder);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	
	public void 
	deleteMqlParam(	final long mqlId, 
					final long mqlParamId) throws Exception {
		String deleteParam = "DELETE repoDynamicMqlParamTable  WHERE mqlId = ? AND mqlParamId = ?";
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteParam))	{
			
			preparedStatement.setLong(1, mqlId);
			preparedStatement.setLong(2, mqlParamId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	/*MQL to cluster bridges*/
	
	
	public MongoDynamicMqlToClusterBridgeList 
	getMqltoClusterBridges(final long mqlId) throws Exception {
		MongoDynamicMqlToClusterBridgeList ret = new MongoDynamicMqlToClusterBridgeList();
		String select = "SELECT b.id , b.mqlId , b.clusterId , m.uniqueName, b.active FROM repoMqlToClusterTable b JOIN mongoRef m ON m.clusterId = b.clusterId WHERE mqlId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, mqlId);
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				MongoDynamicMqlToClusterBridge r = new MongoDynamicMqlToClusterBridge(	rs.getLong("id") ,
																            			rs.getLong("mqlId"), 
																            			rs.getLong("clusterId"),
																            			rs.getString("uniqueName"),
																            			rs.getInt("active")
													            			 			);
				ret.addMongoDynamicMqlToClusterBridge(r);
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	
	public MongoDynamicMqlToClusterBridge 
	getMqltoClusterBridge(final long clusterId, final long mqlId) throws Exception {
    	MongoDynamicMqlToClusterBridge ret = new MongoDynamicMqlToClusterBridge();
		String select = "SELECT b.id, b.mqlId, b.clusterId, m.uniqueName,  b.active FROM repoMqlToClusterTable b JOIN mongoRef m  ON m.clusterId = b.clusterId WHERE b.mqlId=? AND b.clusterId=? ";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			preparedStatement.setLong(1, mqlId);
			preparedStatement.setLong(2, clusterId);
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() ) {
				ret = new MongoDynamicMqlToClusterBridge(	rs.getLong("id") ,
															rs.getLong("mqlId"), 
															rs.getLong("clusterId"),
															rs.getString("uniqueName"),
															rs.getInt("active")
													    );
				
				
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public void 
	mergeMqlToClusterBridge(final long mqlId,
							final long clusterId,
							final long active) throws Exception {

		String addDslParam = "MERGE INTO repoMqlToClusterTable (mqlId, clusterId, active) KEY (mqlId, clusterId) VALUES (?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			PreparedStatement preparedStatement = conn.prepareStatement(addDslParam))	{
			preparedStatement.setLong(1, mqlId);
			preparedStatement.setLong(2, clusterId);
			preparedStatement.setLong(3, active);
			preparedStatement.execute();
    	} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public void 
	deleteMqlToClusterBridge(final long id) throws Exception {
		String deleteSqlParam = "DELETE repoMqlToClusterTable  WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(deleteSqlParam))	{
			preparedStatement.setLong(1, id);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	/*Association functions*/
	public 
	RepoAssociationTableList 
	getRepoAssociationTable(final String associationName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT associationId, associationName FROM repoAssociationTable ";
		if(associationName != null && !associationName.isEmpty() && !associationName.isBlank()) {
			sqlString += "WHERE associationName LIKE ?";
		}
		
		
		RepoAssociationTableList rList = new RepoAssociationTableList(new ArrayList<RepoAssociationTable>());
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			if(associationName != null && !associationName.isBlank()) {
				preparedStatement.setString(1, "%" + associationName + "%");
			}
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	RepoAssociationTable repoAssociationTable 
            	= new RepoAssociationTable(	rs.getLong("associationId"), 
            								rs.getString("associationName")
					            		   );
            	rList.addRepoAssociationTable(repoAssociationTable);
            }
            rs.close();
            return rList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void 
	updateRepoAssociationTable(	final long associationId,
								final String associationName ) throws Exception {

		String addSqlParam = "UPDATE repoAssociationTable SET associationName WHERE associationId = ? ";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, associationId);
			preparedStatement.setString(2, associationName);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	insertRepoAssociationTable(final String associationName) throws Exception {
		String addSqlParam = "INSERT repoAssociationTable(associationName) VALUES ( ? ) ";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{

			preparedStatement.setString(1, associationName);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteRepoAssociationTable(final long associationId) throws Exception {
		String addSqlParam = "DELETE FROM repoAssociationTable WHERE associationId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, associationId);
			preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	RepoAssociationToQueryTableList 
	getRepoAssociationToQueryTable(final long associationId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT associationToQueryId, "
								+ "associationId, "
								+ "queryId "
								+ "FROM repoAssociationToQueryTable WHERE associationId = ?";
		
		RepoAssociationToQueryTableList rList = new RepoAssociationToQueryTableList(new ArrayList<RepoAssociationToQueryTable>());
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, associationId);
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	RepoAssociationToQueryTable repoAssociationToQueryTable 
            	= new RepoAssociationToQueryTable(	rs.getLong("associationToQueryId"),
            										rs.getLong("associationId"), 
            										rs.getLong("associationName")
					            		   			);
            	rList.addRepoAssociationToQueryTable(repoAssociationToQueryTable);
            }
            rs.close();
            return rList;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	mergeRepoAssociationToQueryTable(	final long associationToQueryId,
										final long associationId, 
										final long queryId
									) throws Exception {
		String addSqlParam = "MERGE INTO repoAssociationToQueryTable "
							+ " (associationToQueryId, associationId, queryId) "
							+ " KEY (associationId, queryId)  "
							+ "VALUES (	?, ?, ?)";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{

			preparedStatement.setLong(1, associationToQueryId);
			preparedStatement.setLong(2, associationId);
			preparedStatement.setLong(3, queryId);
			preparedStatement.execute();


		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteRepoAssociationToQueryTable(final long associationToQueryId) throws Exception {
		String addSqlParam = "DELETE FROM repoAssociationToQueryTable WHERE associationId = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, associationToQueryId);
			preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	RepoAssociationToCollectionTableList 
	getRepoAssociationToCollectionTable(final long associationId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT associationToCollectionId, "
								+ "associationId, "
								+ "indexId "
								+ "FROM repoAssociationToCollectionTable WHERE associationId = ?";
		
		RepoAssociationToCollectionTableList rList = new RepoAssociationToCollectionTableList(new ArrayList<RepoAssociationToCollectionTable>());
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, associationId);
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	RepoAssociationToCollectionTable r
            	= new RepoAssociationToCollectionTable(	rs.getLong("associationToCollectionId"),
            											rs.getLong("associationId"),
            											rs.getLong("collectionId")
					            		   				);
            	rList.addRepoAssociationToCollectionTable(r);
            }
            rs.close();
            return rList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	mergeRepoAssociationToCollectionTable(	final long associationToIndexId,
											final long associationId, 
											final long collectionId) throws Exception {
		String addSqlParam = "MERGE INTO repoAssociationToCollectionTable "
							+ " (associationToCollectionId, associationId, collectionId) "
							+ " KEY (associationId, collectionId)  "
							+ "VALUES (	?, ?, ?)";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{

			preparedStatement.setLong(1, associationToIndexId);
			preparedStatement.setLong(2, associationId);
			preparedStatement.setLong(3, collectionId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteRepoAssociationToCollectionTable(	final long associationId, 
											final long collectionId) throws Exception {
		String addSqlParam = "DELETE FROM repoAssociationToCollectionTable "
							+ " WHERE associationId = ? AND collectionId = ?";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, associationId);
			preparedStatement.setLong(2, collectionId);
			preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	RepoCollectionTableList 
	getRepoCollectionTable(final String indexName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT indexId, "
								+ "clusterId, "
								+ "indexName "
								+ "FROM repoIndexTable WHERE indexName LIKE ?";
		RepoCollectionTableList rList = new RepoCollectionTableList(new ArrayList<RepoCollectionTable>());
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, "%" + indexName + "%'");
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	RepoCollectionTable r 
            	= new RepoCollectionTable(	rs.getLong("collectionId"),
                                            rs.getLong("clusterId"),
                                            rs.getString("collectionName")
                                        );
            	rList.addRepoCollectionTable(r);
            }
            rs.close();
            return rList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	mergeRepoCollectionTable(	final long collectionId,
								final long clusterId, 
								final String collectionName	) throws Exception {
		String addSqlParam = "MERGE INTO repoIndexTable "
							+ " (collectionId, clusterId, collectionName) "
							+ " KEY (clusterId, collectionName)  "
							+ "VALUES (	?, ?, ?)";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{

			preparedStatement.setLong(1, collectionId);
			preparedStatement.setLong(2, clusterId);
			preparedStatement.setString(3, collectionName);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteRepoCollectionTable(final long collectionId) throws Exception {
		String addSqlParam = "DELETE FROM repoAssociationToCollectionTable WHERE collectionId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, collectionId);
			preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	void 
	addBucket(	final long clusterId,
				final String dbName,
				final String bucketName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO repoBucketsTable (clusterId, dbName, bucketName) KEY (clusterId, dbName, bucketName) VALUES(?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, clusterId);
			preparedStatement.setString(2, dbName);
			preparedStatement.setString(3, bucketName);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	void 
	deleteBucket(	final long clusterId,
					final String dbName,
					final String bucketName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE FROM repoBucketsTable WHERE clusterId=? AND dbName=? AND bucketName = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setLong(1, clusterId);
			preparedStatement.setString(2, dbName);
			preparedStatement.setString(3, bucketName);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	boolean 
	isBucket(	final long clusterId,
				final String dbName,
				final String bucketName) throws Exception {

		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT bucketName FROM repoBucketsTable "
						+ "	WHERE clusterId=? AND dbName=? AND bucketName = ?";
		
		List<String> rList = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, clusterId);
			preparedStatement.setString(2, dbName);
			preparedStatement.setString(3, bucketName);
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	String b = rs.getString("bucketName");
            	rList.add(b);
            }
            rs.close();
            return rList.size() == 1;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
}

