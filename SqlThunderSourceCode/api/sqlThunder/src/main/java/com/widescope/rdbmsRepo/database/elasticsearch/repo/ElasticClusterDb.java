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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import org.apache.http.HttpHost;
import org.springframework.stereotype.Component;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationTable;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationTableList;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationToIndexTable;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationToIndexTableList;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationToQueryTable;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationToQueryTableList;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoIndexTable;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoIndexTableList;

@Component
public class ElasticClusterDb {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./elasticRepo;MODE=PostgreSQL";  
	
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	public ElasticClusterDb()	{}
	
		   
	
	private 
	void 
	closeHandles(	Connection conn, 
					Statement statement, 
					ResultSet rs){
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	

	private static final String[] elasticApi = new String[] {"_search",
										"_mapping",
										"_sql",
										"_cat",
										"_create",
										"_doc",
										"_source",
										"_delete_by_query",
										"_update",
										"_update_by_query",
										"_mget",
										"_bulk",
										"_reindex",
										"_termvectors",
										"_mtermvectors",
										"_enrich",
										"_enrich/_stats",
										"_eql",
										"_features",
										"_features/_reset",
										"_fleet",
										"_fleet/_fleet_search",
										"_fleet/_fleet_msearch",
										"_text_structure/find_structure",
										"_graph/explore",
										"_alias",
										"_aliases",
										"_analyze",
										"_disk_usage",
										"_cache/clear",
										"_clone",
										"_close"};
	
	
	
	private static String[] verbApi = new String[] { "GET", "POST", "PUT", "DELETE"};
	
	public static boolean isVerb (final String verb) {
		return Arrays.asList(verbApi).contains(verb);
	}
	
	public static boolean isElasticApi (final String api) {
		return Arrays.asList(elasticApi).contains(api);
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
			List<String> ddlList = new java.util.ArrayList<String>();
			ddlList.add(ElasticClusterDb.elasticCluster);
			ddlList.add(ElasticClusterDb.elasticClusterIndex1);
			ddlList.add(ElasticClusterDb.elasticHost);
			ddlList.add(ElasticClusterDb.elasticHostIndex2);
			ddlList.add(ElasticClusterDb.elasticHostFk1);

			ddlList.add(ElasticClusterDb.newElasticCluster);
			ddlList.add(ElasticClusterDb.newElasticClusterHost);

			ddlList.add(ElasticClusterDb.repoQueryTable);
			ddlList.add(ElasticClusterDb.repoQueryTable_const1);
			ddlList.add(ElasticClusterDb.repoQueryTable_const2);
			ddlList.add(ElasticClusterDb.repoQueryTable_const3);
			ddlList.add(ElasticClusterDb.repoQueryTable_const4);
			ddlList.add(ElasticClusterDb.repoQueryTable_index1);

			ddlList.add(ElasticClusterDb.repoQueryTable_insert1);
			ddlList.add(ElasticClusterDb.repoQueryTable_insert2);
			ddlList.add(ElasticClusterDb.repoQueryTable_insert3);

			ddlList.add(ElasticClusterDb.repoQueryParamTable);
			ddlList.add(ElasticClusterDb.repoQueryParamTable_const1);
			ddlList.add(ElasticClusterDb.repoQueryParamTable_const1);
			ddlList.add(ElasticClusterDb.repoQueryParamTable_const2);
			ddlList.add(ElasticClusterDb.repoQueryParamTable_index1);

			ddlList.add(ElasticClusterDb.repoQueryParamTable_insert2);
			ddlList.add(ElasticClusterDb.repoQueryParamTable_insert3);

			ddlList.add(ElasticClusterDb.repoQueryToClusterTable);
			ddlList.add(ElasticClusterDb.repoQueryToClusterTable_index0);
			ddlList.add(ElasticClusterDb.repoQueryToClusterTable_index1);
			ddlList.add(ElasticClusterDb.repoQueryToClusterTable_index2);
			ddlList.add(ElasticClusterDb.repoQueryToClusterTable_fk1);
			ddlList.add(ElasticClusterDb.repoQueryToClusterTable_fk2);

			ddlList.add(ElasticClusterDb.repoQueryToClusterTable_insert1);


			ddlList.add(ElasticClusterDb.repoAssociationTable);
			ddlList.add(ElasticClusterDb.repoAssociationTable_index0 );
			ddlList.add(ElasticClusterDb.repoAssociationToQueryTable);
			ddlList.add(ElasticClusterDb.repoAssociationToQueryTable_index0);

			ddlList.add(ElasticClusterDb.repoIndexTable);
			ddlList.add(ElasticClusterDb.repoIndexTable_index0);

			ddlList.add(ElasticClusterDb.repoAssociationToIndexTable);
			ddlList.add(ElasticClusterDb.repoAssociationToIndexTable_index0);

			ElasticClusterDb elasticClusterDb = new ElasticClusterDb();
			elasticClusterDb.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	

	
	////////////////////////////////////// Elastic table - Describes connection to Clusters/Servers///////////////////////////////////////////////////////////////////////////////////////////
	public static String 
	elasticCluster = "CREATE TABLE IF NOT EXISTS elasticClusterTable (clusterId INT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																		+ "	uniqueName VARCHAR(999), \r\n"
																		+ "	description VARCHAR(9999) DEFAULT '')";
	
	public static String 
	elasticClusterIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_elasticClusterTable_1 ON elasticClusterTable(uniqueName);";
	
	
	public static String 
	elasticHost = "CREATE TABLE IF NOT EXISTS elasticClusterHostTable (hostId INT  GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																+ "	clusterId INT, \r\n"
																+ "	server VARCHAR(999), \r\n"
																+ "	port INT, \r\n"
																+ "	protocol VARCHAR(999), \r\n"
																+ "	description VARCHAR(9999) DEFAULT '')";
	

		
	public static String 
	elasticHostIndex2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_elasticClusterHostTable_1 ON elasticClusterHostTable(clusterId, server);";
	
	public static String 
	elasticHostFk1 = "ALTER TABLE elasticClusterHostTable ADD CONSTRAINT IF NOT EXISTS elasticClusterHostTableFk1 FOREIGN KEY ( clusterId ) REFERENCES elasticClusterTable( clusterId );";
	
		

	public static String newElasticCluster = "INSERT INTO elasticClusterTable(clusterId, uniqueName, description) \r\n"
			+ " VALUES (1, 'localElasticCluster', 'Elastic Cluster')";

	public static String newElasticClusterHost = "INSERT INTO elasticClusterHostTable(hostId, clusterId, server, port, protocol, description) \r\n"
					+ " VALUES (1, 1, 'localhost', 9200, 'http', 'DB Server')";


	/*
	public static String newElasticCluster_1 = "INSERT INTO elasticClusterTable(clusterId, uniqueName, description) \r\n"
			+ " VALUES (2, 'thinkcentre', 'Think Centre Elastic Cluster')";

	public static String newElasticClusterHost_1 = "INSERT INTO elasticClusterHostTable(hostId, clusterId, server, port, protocol, description) \r\n"
			+ " VALUES (1,2, 'thinkcentre2019', 9200, 'http', 'Thinkcentre2019 DB Server')";
	
	*/
	
	////////////////////////////////// QUERY Tables ///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String 
	repoQueryTable = "CREATE TABLE IF NOT EXISTS repoQueryTable (\r\n"
																+ "	queryId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																+ "	verb VARCHAR(99) ,\r\n"
																+ "	queryReturnType VARCHAR(99) ,\r\n"
																+ "	queryType VARCHAR(99) DEFAULT 'DSL',\r\n"
																+ "	elasticApi VARCHAR(999) DEFAULT '_search',\r\n"
																+ "	indexName VARCHAR(999),\r\n"
																+ "	queryCategory VARCHAR(99) ,\r\n"
																+ "	queryName VARCHAR(99),\r\n"
																+ "	queryDescription VARCHAR(999),\r\n"
																+ "	endPoint VARCHAR(999),\r\n"
																+ "	queryContent CLOB,\r\n"
																+ "	active int DEFAULT 1)";



	
	public static String 
	repoQueryTable_const1 = "ALTER TABLE repoQueryTable ADD CONSTRAINT IF NOT EXISTS ck_repoQueryTable_1 "
									+ "CHECK (queryReturnType IN ('JSON', 'NUMBER', 'STRING', 'BINARY') );";
	public static String
	repoQueryTable_const2 = "ALTER TABLE repoQueryTable ADD CONSTRAINT IF NOT EXISTS ck_repoQueryTable_2 "
									+ "CHECK (queryType IN ('DSL', 'SQL') );";

	public static String
	repoQueryTable_const3 = "ALTER TABLE repoQueryTable ADD CONSTRAINT IF NOT EXISTS ck_repoQueryTable_3 "
									+ "CHECK (verb IN ('GET', 'POST', 'PUT', 'DELETE') );";
	
	public static String
	repoQueryTable_const4 = "ALTER TABLE repoQueryTable ADD CONSTRAINT IF NOT EXISTS ck_repoQueryTable_4 "
									+ "CHECK (elasticApi IN ('_search', "
															+ "'_mapping', "
															+ "'_sql', "
															+ "'_cat', "
															+ "'_create', "
															+ "'_doc', "
															+ "'_source', "
															+ "'_delete_by_query', "
															+ "'_update', "
															+ "'_update_by_query', "
															+ "'_mget', "
															+ "'_bulk', "
															+ "'_reindex', "
															+ "'_termvectors', "
															+ "'_mtermvectors', "
															+ "'_enrich', "
															+ "'_enrich/_stats', "
															+ "'_eql', "
															+ "'_features', "
															+ "'_features/_reset', "
															+ "'_fleet', "
															+ "'_fleet/_fleet_search', "
															+ "'_fleet/_fleet_msearch', "
															+ "'_text_structure/find_structure', "
															+ "'_graph/explore', "
															+ "'_alias', "
															+ "'_aliases',"
															+ "'_analyze', "
															+ "'_disk_usage', "
															+ "'_cache/clear', "
															+ "'_clone', "
															+ "'_close') );";
	
	
	public static String 
	repoQueryTable_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoQueryTable_1 ON repoQueryTable(queryName);";



	public static String 
	repoQueryTable_insert1 = "INSERT INTO repoQueryTable(queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active) \r\n"
			+ " VALUES (1, 'GET', 'JSON', 'DSL', '_cat', '', 'SYSTEM', 'firstDslQuery', 'Search All Indeses', '/_cat/indices?format=json&pretty=true', '', 1)";

	
	public static String 
	repoQueryTable_insert2 = "INSERT INTO repoQueryTable(queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active) \r\n"
			+ " VALUES (2, 'GET', 'JSON', 'DSL', '_search', '', 'SEARCH', 'secondDslQuery', 'Search index', '/@INDEXNAME@/_search', '{ \"query\": {\"match_all\": {} } }', 1)";
	
	/*POST /_sql
	{
	  "query":"SELECT * FROM test3"
	}
	*/
	public static String 
	repoQueryTable_insert3 = "INSERT INTO repoQueryTable(queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active) \r\n"
			+ " VALUES (3, 'POST', 'JSON', 'SQL', '_sql', '', 'SEARCH', 'firstSqlQuery', 'Search index', '/_sql', '{ \"query\": SELECT * FROM @INDEXNAME@', 1)";

	
	
	
	
	public static String 
	repoQueryParamTable = "CREATE TABLE IF NOT EXISTS repoQueryParamTable (\r\n"
												+ "	paramId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
												+ "	queryId BIGINT,\r\n"
												+ "	paramName VARCHAR(99),\r\n"
												+ "	paramDefaultValue VARCHAR(999) ,\r\n"
												+ "	paramType VARCHAR(20) DEFAULT 'STRING' ,\r\n"
												+ "	paramPosition VARCHAR(20) DEFAULT 'INPUT',\r\n"
												+ "	paramOrder INT)";
	

	
	public static String 
	repoQueryParamTable_fk1 = "ALTER TABLE repoQueryParamTable ADD CONSTRAINT IF NOT EXISTS repoQueryParamTableFk_2 FOREIGN KEY ( queryId ) REFERENCES repoQueryTable( queryId );";
	
	public static String 
	repoQueryParamTable_const1 = "ALTER TABLE repoQueryParamTable ADD CONSTRAINT IF NOT EXISTS ck_repoQueryParamTable_1 "
									+ "CHECK (paramType IN ('STRING', 'FLOAT', 'DECIMAL', 'DOUBLE', 'REAL', 'INTEGER', 'BIGINT', 'SMALLINT', 'BOOL', 'DATE', 'TIMESTAMP', 'LOB', 'NAMESTRING') );";
	public static String 
	repoQueryParamTable_const2 = "ALTER TABLE repoQueryParamTable ADD CONSTRAINT IF NOT EXISTS ck_repoQueryParamTable_2 "
									+ "CHECK (paramPosition IN ('OUTPUT', 'INPUT') );";
	
	public static String 
	repoQueryParamTable_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoQueryQableParam_1 ON repoQueryParamTable(queryId, paramName);";
	
	public static String
	repoQueryParamTable_insert2 = "INSERT INTO repoQueryParamTable(paramId, queryId, paramName, paramDefaultValue, paramType, paramPosition, paramOrder) \r\n"
			+ " VALUES (1, 2, '@INDEXNAME@', 'test3', 'STRING', 'OUTPUT', 1)";
	
	
	public static String
	repoQueryParamTable_insert3 = "INSERT INTO repoQueryParamTable(paramId, queryId, paramName, paramDefaultValue, paramType, paramPosition, paramOrder) \r\n"
			+ " VALUES (2, 3, '@INDEXNAME@', 'test3', 'STRING', 'OUTPUT', 1)";


	//////////////////////////////////BRIDGES ///////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String 
	repoQueryToClusterTable = """
            CREATE TABLE IF NOT EXISTS repoQueryToClusterTable (\r
            	id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	queryId BIGINT,\r
            	clusterId BIGINT,\r
            	active int DEFAULT 1)""";
	public static String 
	repoQueryToClusterTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoQueryToClusterTable_1 ON repoQueryToClusterTable(queryId, clusterId);";

	
	public static String 
	repoQueryToClusterTable_index1 = "CREATE INDEX IF NOT EXISTS idx_repoQueryToClusterTable_1 ON repoQueryToClusterTable(clusterId) ;";
	public static String 
	repoQueryToClusterTable_index2 = "CREATE INDEX IF NOT EXISTS idx_repoQueryToClusterTable_2 ON repoQueryToClusterTable(queryId) ;";
	
	public static String 
	repoQueryToClusterTable_fk1 = "ALTER TABLE repoQueryToClusterTable ADD CONSTRAINT IF NOT EXISTS repoQueryToClusterTable_fk_1 FOREIGN KEY ( queryId ) REFERENCES repoQueryTable( queryId );";

	public static String 
	repoQueryToClusterTable_fk2 = "ALTER TABLE repoQueryToClusterTable ADD CONSTRAINT IF NOT EXISTS repoQueryToClusterTable_fk_2 FOREIGN KEY ( clusterId ) REFERENCES elasticClusterTable( clusterId );";

	public static String repoQueryToClusterTable_insert1 = "INSERT INTO repoQueryToClusterTable(id, queryId, clusterId, active) \r\n"
			+ " VALUES (1, 2, 1, 1)";
	
	
	/*Index Associations*/
	public static String 
	repoAssociationTable = """
            CREATE TABLE IF NOT EXISTS repoAssociationTable (\r
            	associationId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r
            	associationName VARCHAR(100))""";

	public static String 
	repoAssociationTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoAssociationTable_1 ON repoAssociationTable(associationName);";

		
	public static String 
	repoAssociationToQueryTable = "CREATE TABLE IF NOT EXISTS repoAssociationToQueryTable (\r\n"
																+ "	associationToQueryId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																+ "	associationId BIGINT," 
																+ "	queryId BIGINT)";

	public static String 
	repoAssociationToQueryTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoAssociationToQueryTable_1 ON repoAssociationToQueryTable(associationId, queryId);";

		
	public static String 
	repoIndexTable = "CREATE TABLE IF NOT EXISTS repoIndexTable (\r\n"
																	+ "	indexId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																	+ "	clusterId BIGINT,"
																	+ "	indexName VARCHAR(100))";

	public static String 
	repoIndexTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoIndexTable_1 ON repoIndexTable(indexName);";

	public static String 
	repoAssociationToIndexTable = "CREATE TABLE IF NOT EXISTS repoAssociationToIndexTable (\r\n"
																	+ "	associationToIndexId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
																	+ "	associationId BIGINT,"
																	+ "	indexId BIGINT)";

	public static String 
	repoAssociationToIndexTable_index0 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repoAssociationToIndexTable_1 ON repoAssociationToIndexTable(associationId, indexId);";

	
	////////////////////////////////////////Helper Func//////////////////////////////////////////////////////////////////////////////////////////////
	
	public HttpHost[] 
	getHostArray(final Map<String, ElasticCluster> clusterMap, 
				 final String clusterUniqueName) {
		HttpHost[] httpHostArray = new HttpHost[clusterMap.get(clusterUniqueName).getListElasticHosts().size()];
		int counter = 0;
		for( ElasticHost elasticHost:  clusterMap.get(clusterUniqueName).getListElasticHosts() ) {
			HttpHost httpHost = new HttpHost(elasticHost.getServer(), elasticHost.getPort(), elasticHost.getProtocol());
			httpHostArray[counter] = httpHost;
			counter++;
		}
		return httpHostArray;
	}
	
	
	
	
	////////////////////////////////////////Clusters procedures ///////////////////////////////////////////////////////////////////////////////////////
	public 
	Map<String, ElasticCluster> 
	getElasticClusters(final int clusterId)	throws Exception {
		Map<String, ElasticCluster>  ret = new HashMap<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = null;
		if(clusterId <= 0)
			sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable";
		else
			sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable WHERE clusterId = " + clusterId;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	int clusterIdTmp = rs.getInt("clusterId");
            	String uniqueNameTmp = rs.getString("uniqueName");
            	String descriptionTmp = rs.getString("description");
            	List<ElasticHost> listHosts = getElasticClusterHosts(clusterIdTmp);
            	List<ElasticIndex> listDocs = new ArrayList<ElasticIndex>();

            	ElasticCluster elasticCluster = new ElasticCluster(clusterIdTmp, 
            														uniqueNameTmp, 
            														descriptionTmp, 
            														listHosts, 
            														listDocs
            														);
            	ret.put(uniqueNameTmp, elasticCluster);
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
	Map<String, ElasticCluster> 
	getElasticClusters(final String uniqueName)	throws Exception {
		Map<String, ElasticCluster>  ret = new HashMap<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = null;
		if(uniqueName == null || uniqueName.isEmpty() || uniqueName.isBlank())
			sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable";
		else
			sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable WHERE clusterId LIKE '%" + uniqueName + "%'";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	int clusterIdTmp = rs.getInt("clusterId");
            	String uniqueNameTmp = rs.getString("uniqueName");
            	String descriptionTmp = rs.getString("description");
            	List<ElasticHost> listHosts = getElasticClusterHosts(clusterIdTmp);
            	List<ElasticIndex> listDocs = new ArrayList<ElasticIndex>();

            	ElasticCluster elasticCluster = new ElasticCluster(clusterIdTmp, 
            														uniqueNameTmp, 
            														descriptionTmp, 
            														listHosts, 
            														listDocs
            														);
            	ret.put(uniqueNameTmp, elasticCluster);
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
	Map<String, ElasticCluster> 
	getElasticCluster(final String uniqueName)	throws Exception {
		Map<String, ElasticCluster>  ret = new HashMap<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString;
		if(uniqueName == null || uniqueName.isEmpty() || uniqueName.isBlank())
			sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable";
		else
			sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable WHERE uniqueName = '" + uniqueName + "'";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	int clusterIdTmp = rs.getInt("clusterId");
            	String uniqueNameTmp = rs.getString("uniqueName");
            	String descriptionTmp = rs.getString("description");
            	List<ElasticHost> listHosts = getElasticClusterHosts(clusterIdTmp);
            	if(listHosts == null) {
            		listHosts = new ArrayList<>();
            	}
            	
            	ElasticCluster elasticCluster = new ElasticCluster(clusterIdTmp, 
            														uniqueNameTmp, 
            														descriptionTmp, 
            														listHosts, 
            														null
            														);
            	ret.put(uniqueNameTmp, elasticCluster);
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
	Map<String, ElasticCluster>
	getElasticClusters()	throws Exception {
		Map<String, ElasticCluster>  ret = new HashMap<>();
		Class.forName(JDBC_DRIVER);
		String sqlString = null;
		sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				int clusterIdTmp = rs.getInt("clusterId");
				String uniqueNameTmp = rs.getString("uniqueName");
				String descriptionTmp = rs.getString("description");
				List<ElasticHost> listHosts = getElasticClusterHosts(clusterIdTmp);
				if(listHosts == null) {
					listHosts = new ArrayList<ElasticHost>();
				}

				ElasticCluster elasticCluster = new ElasticCluster(clusterIdTmp,
						uniqueNameTmp,
						descriptionTmp,
						listHosts,
						null
				);
				ret.put(uniqueNameTmp, elasticCluster);
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
	List<ElasticHost> 
	getElasticClusterHosts(final int clusterId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT hostId, "
								+ "clusterId, "
								+ "server, "
								+ "port, "
								+ "protocol, "
								+ "description "
								+ "FROM elasticClusterHostTable WHERE clusterId = ?";
		
		List<ElasticHost> listElasticHost = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, clusterId);
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ElasticHost elasticHost = new ElasticHost(	rs.getInt("hostId"), 
									            			rs.getInt("clusterId"), 
									            			rs.getString("server"),
									            			rs.getInt("port"),
									            			rs.getString("protocol"),
									            			rs.getString("description")
			            			                        );
            	listElasticHost.add(elasticHost);
            }
            rs.close();
            return listElasticHost;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public 
	List<ElasticCluster> 
	getElasticClusterList(final String uniqueName)	throws Exception {
		List<ElasticCluster>  ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT clusterId, uniqueName, description FROM elasticClusterTable WHERE uniqueName LIKE ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, "%" + uniqueName + "%");
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	int clusterIdTmp = rs.getInt("clusterId");
            	String uniqueNameTmp = rs.getString("uniqueName");
            	String descriptionTmp = rs.getString("description");
            	List<ElasticHost> listHosts = getElasticClusterHosts(clusterIdTmp);
            	List<ElasticIndex> listDocs = getElasticDocuments(clusterIdTmp);
            	ElasticCluster elasticCluster = new ElasticCluster(clusterIdTmp, uniqueNameTmp, descriptionTmp, listHosts, listDocs);
            	ret.add(elasticCluster);
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
	void 
	mergeElasticCluster(final String uniqueName, String description)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO elasticClusterTable (uniqueName, description) KEY (uniqueName) VALUES(?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, uniqueName);
			preparedStatement.setString(2, description);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	void 
	mergeElasticCluster(final long clusterId, final String uniqueName, String description)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO elasticClusterTable (clusterId, uniqueName, description) KEY (clusterId) VALUES(?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, clusterId);
			preparedStatement.setString(2, uniqueName);
			preparedStatement.setString(3, description);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	void 
	deleteElasticCluster(final int clusterId)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE FROM elasticClusterTable WHERE clusterId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, clusterId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		sqlString = "DELETE FROM elasticClusterHostTable WHERE clusterId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, clusterId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
	}
	
	
	
	public 
	void 
	mergeElasticClusterHosts(final List<ElasticHost> listElasticHost)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO elasticClusterHostTable (clusterId, server, port) KEY (clusterId, server, port) "
						+ " VALUES(?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			for(ElasticHost elasticHost:listElasticHost) {
				preparedStatement.setInt(1, elasticHost.getClusterId());
				preparedStatement.setString(2, elasticHost.getServer());
				preparedStatement.setInt(3, elasticHost.getPort());
				preparedStatement.setString(4, elasticHost.getProtocol());
				preparedStatement.execute();
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	void 
	addClusterHost(	final ElasticHost elasticHost)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO elasticClusterHostTable (clusterId, server, port, protocol, description) KEY (clusterId, server, port) "
						+ " VALUES(?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, elasticHost.getClusterId());
			preparedStatement.setString(2, elasticHost.getServer());
			preparedStatement.setInt(3, elasticHost.getPort());
			preparedStatement.setString(4, elasticHost.getProtocol());
			preparedStatement.setString(5, elasticHost.getDescription());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public 
	void 
	updateClusterHost(final ElasticHost elasticHost)	throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO elasticClusterHostTable (hostId, clusterId, server, port, protocol, description) KEY (hostId, clusterId) "
						+ " VALUES(?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, elasticHost.getHostId());
			preparedStatement.setInt(2, elasticHost.getClusterId());
			preparedStatement.setString(3, elasticHost.getServer());
			preparedStatement.setInt(4, elasticHost.getPort());
			preparedStatement.setString(5, elasticHost.getProtocol());
			preparedStatement.setString(6, elasticHost.getDescription());
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	void 
	deleteClusterHost(final long clusterId, 
					  final long hostId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE FROM elasticClusterHostTable WHERE clusterId = ? AND hostId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setLong(1, clusterId);
			preparedStatement.setLong(2, hostId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	void 
	deleteClusterHosts(final long clusterId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE FROM elasticClusterHostTable WHERE clusterId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setLong(1, clusterId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
		
	
	
	
	/*                        Elastic Indices                   */

	public 
	List<ElasticIndex> 
	getElasticDocuments(final int clusterId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT docId, "
								+ "clusterId, "
								+ "description "
								+ "FROM elasticDocument WHERE clusterId = ?";

		List<ElasticIndex> listElasticDocument = new ArrayList<ElasticIndex>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, clusterId);
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	ElasticIndex elasticHost = new ElasticIndex(rs.getInt("docId"), 
									            			rs.getInt("clusterId"), 
									            			rs.getString("description")
			            			                        
			            			                        );
            	listElasticDocument.add(elasticHost);
            }
            rs.close();
            return listElasticDocument;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	List<ElasticIndex> 
	getElasticDocuments(final String desc) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT docId, "
								+ "clusterId, "
								+ "description "
								+ "FROM elasticDocument WHERE description LIKE ?";
		
		List<ElasticIndex> listElasticDocument = new ArrayList<ElasticIndex>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, "%" + desc + "%");
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	ElasticIndex elasticHost = new ElasticIndex(rs.getInt("docId"), 
									            			rs.getInt("clusterId"), 
									            			rs.getString("description")
					            			                );
            	listElasticDocument.add(elasticHost);
            }
            rs.close();
            return listElasticDocument;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	List<ElasticIndex> 
	getElasticDocuments(final int clusterId, 
						final String desc) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT docId, "
								+ "clusterId, "
								+ "description "
								+ "FROM elasticDocument WHERE clusterId = ? AND description LIKE ?";
		
		List<ElasticIndex> listElasticDocument = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, clusterId);
            preparedStatement.setString(2, "%" + desc + "%");
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	ElasticIndex elasticHost = new ElasticIndex(rs.getInt("docId"), 
									            			rs.getInt("clusterId"), 
									            			rs.getString("description")
					            			                );
            	listElasticDocument.add(elasticHost);
            }
            rs.close();
            return listElasticDocument;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public 
	void 
	deleteDocument(final long docId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE FROM elasticDocument WHERE docId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setLong(1, docId);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	
	
	//////////////////////////// Elastic Query Tables //////////////////////////////////////////////////////
	
	public List<ElasticQuery>
	getQuery( final int queryId) throws Exception {
		List<ElasticQuery> ret = new ArrayList<>();
		String select = null;
		select = "SELECT queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active FROM repoQueryTable "
				+ " WHERE queryId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			preparedStatement.setInt(1, queryId);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			while ( resultSet.next() ) {
				ElasticQuery elasticQuery = new ElasticQuery(resultSet.getInt("queryId"), 
															resultSet.getString("verb"),
															resultSet.getString("queryReturnType"),
													 		resultSet.getString("queryType"), 
													 		resultSet.getString("elasticApi"),
													 		resultSet.getString("indexName"), 
													 		resultSet.getString("queryCategory"),
															resultSet.getString("queryName"),
															resultSet.getString("queryDescription"),
															resultSet.getString("endPoint"),
															resultSet.getString("queryContent"),
															resultSet.getInt("active")
															);
				ret.add(elasticQuery);
			}
			resultSet.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	public List<ElasticQuery>
	getQueriesForCluster( final String clusterName) throws Exception {
		List<ElasticQuery> ret = new ArrayList<>();
		String select = "SELECT q.queryId, "
						+ "q.verb, "
						+ "q.queryReturnType, "
						+ "q.queryType, "
						+ "q.elasticApi, "
						+ "q.indexName, "
						+ "q.queryCategory, "
						+ "q.queryName, "
						+ "q.queryDescription, "
						+ "q.endPoint, "
						+ "q.queryContent, "
						+ "q.active "
						+ "FROM repoQueryTable q JOIN repoQueryToClusterTable c ON c.queryId=q.queryId "
						+ "JOIN elasticClusterTable t ON c.clusterId = t.clusterId"
						+ " WHERE t.uniqueName = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			preparedStatement.setString(1, clusterName);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			while ( resultSet.next() ) {
				ElasticQuery elasticQuery = new ElasticQuery(	resultSet.getInt("queryId"), 
																resultSet.getString("verb"),
																resultSet.getString("queryReturnType"),
																resultSet.getString("queryType"), 
																resultSet.getString("elasticApi"),
																resultSet.getString("indexName"),
																resultSet.getString("queryCategory"),
																resultSet.getString("queryName"),
																resultSet.getString("queryDescription"),
																resultSet.getString("endPoint"),
																resultSet.getString("queryContent"),
																resultSet.getInt("active")
															);
				ret.add(elasticQuery);
			}
			resultSet.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	public List<ElasticQuery> 
	getQuery( final String strtoSearch) throws Exception {

		List<ElasticQuery> ret = new ArrayList<ElasticQuery>();
		String select;
		if(strtoSearch != null && !strtoSearch.isEmpty()) {
			select = "SELECT queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active FROM repoQueryTable "
					+ " WHERE queryName LIKE ? OR queryDescription LIKE ? OR queryContent LIKE ?";
		} else {
			select = "SELECT queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active FROM repoQueryTable ";	
		}
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			if(strtoSearch != null && !strtoSearch.isEmpty()) {
				preparedStatement.setString(1, "%" + strtoSearch + "%");
				preparedStatement.setString(2, "%" + strtoSearch + "%");
				preparedStatement.setString(3, "%" + strtoSearch + "%");
			}
			
			
			ResultSet resultSet = preparedStatement.executeQuery();
			while ( resultSet.next() ) {
				ElasticQuery elasticQuery = new ElasticQuery(resultSet.getInt("queryId"), 
															resultSet.getString("verb"),
															resultSet.getString("queryReturnType"),
															resultSet.getString("queryType"),
															resultSet.getString("elasticApi"),
															resultSet.getString("indexName"),
															resultSet.getString("queryCategory"),
															resultSet.getString("queryName"),
															resultSet.getString("queryDescription"),
															resultSet.getString("endPoint"),
															resultSet.getString("queryContent"),
															resultSet.getInt("active")
															);
		
		
				ret.add(elasticQuery);
			}
			resultSet.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public List<ElasticQuery> 
	getExactQuery( final String queryName) throws Exception {
		List<ElasticQuery> ret = new ArrayList<ElasticQuery>();
		String theSelect = "SELECT queryId, verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName, queryDescription, endPoint, queryContent, active FROM repoQueryTable "
						+ " WHERE queryName = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(theSelect))	{
			preparedStatement.setString(1, queryName );
						
			
			ResultSet resultSet = preparedStatement.executeQuery();
			while ( resultSet.next() ) {
				ElasticQuery elasticQuery = new ElasticQuery(resultSet.getInt("queryId"), 
															resultSet.getString("verb"),
															resultSet.getString("queryReturnType"),
															resultSet.getString("queryType"),
															resultSet.getString("elasticApi"),
															resultSet.getString("indexName"),
															resultSet.getString("queryCategory"),
															resultSet.getString("queryName"),
															resultSet.getString("queryDescription"),
															resultSet.getString("endPoint"),
															resultSet.getString("queryContent"),
															resultSet.getInt("active")
															);
		
		
				ret.add(elasticQuery);
			}
			resultSet.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	
	public void 
	mergeQuery(	final String verb,
				final String queryReturnType,
				final String queryType, 
				final String elasticApi,
				final String indexName,
				final String queryCategory,
				final String queryName,
				final String queryDescription,
				final String endPoint,
				final String queryContent,
				final int active) throws Exception {

		String addSql = "MERGE INTO repoQueryTable(verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName,  queryDescription,  endPoint, queryContent,  active) "
					+ "	KEY (queryName) "
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSql))	{
			
			preparedStatement.setString(1, verb);
			preparedStatement.setString(2, queryReturnType);
			preparedStatement.setString(3, queryType);
			preparedStatement.setString(4, elasticApi);
			preparedStatement.setString(5, indexName);
			preparedStatement.setString(6, queryCategory);
			preparedStatement.setString(7, queryName);
			preparedStatement.setString(8, queryDescription);
			preparedStatement.setString(9, endPoint);
			preparedStatement.setString(10, queryContent);
			preparedStatement.setInt(11, active);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public void 
	insertQuery(final String verb,
				final String queryReturnType,
				final String queryType, 
				final String elasticApi,
				final String indexName,
				final String queryCategory,
				final String queryName,
				final String queryDescription,
				final String endPoint,
				final String queryContent,
				final int active) throws Exception {

		String addSql = "INSERT INTO repoQueryTable(verb, queryReturnType, queryType, elasticApi, indexName, queryCategory, queryName,  queryDescription,  endPoint, queryContent,  active) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSql))	{
			
			preparedStatement.setString(1, verb);
			preparedStatement.setString(2, queryReturnType);
			preparedStatement.setString(3, queryType);
			preparedStatement.setString(4, elasticApi);
			preparedStatement.setString(5, indexName);
			preparedStatement.setString(6, queryCategory);
			preparedStatement.setString(7, queryName);
			preparedStatement.setString(8, queryDescription);
			preparedStatement.setString(9, endPoint);
			preparedStatement.setString(10, queryContent);
			preparedStatement.setInt(11, active);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void 
	deleteQuery(final int queryId) throws Exception {
		if(queryId <= 0)
			throw new Exception("Query Id is null");
		
		String deleteDslParam = "DELETE repoQueryParamTable WHERE queryId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
			
			preparedStatement.setInt(1, queryId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		
		String deleteDsl = "DELETE repoQueryTable  WHERE queryId = ?" ;
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(deleteDsl))	{
			
			preparedStatement.setInt(1, queryId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
	}
	
	public List<ElasticQueryParam> 
	getQueryParams( final int queryId) throws Exception {

		List<ElasticQueryParam> ret = new ArrayList<ElasticQueryParam>();
		String select = null;
		select = "SELECT paramId, queryId, paramName, paramDefaultValue, paramType, paramPosition, paramOrder "
				+ "FROM repoQueryParamTable WHERE queryId = ?";
		
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			preparedStatement.setInt(1, queryId);
			
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ElasticQueryParam elasticQueryParam = new ElasticQueryParam(rs.getInt("paramId") ,
															            	rs.getInt("queryId"), 
															            	rs.getString("paramName"),
															            	rs.getString("paramDefaultValue"),
															            	rs.getString("paramType"), 
															            	rs.getString("paramPosition"), 
															            	rs.getInt("paramOrder") );
            	
            	
            	ret.add(elasticQueryParam);
            }


		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public List<ElasticQueryParam> 
	getQueryParam( final int queryId,
					final String paramName) throws Exception {

		List<ElasticQueryParam> ret = new ArrayList<ElasticQueryParam>();
		String select = null;
		select = "SELECT paramId, queryId, paramName, paramDefaultValue, paramType, paramPosition, paramOrder "
				+ "FROM repoQueryParamTable WHERE queryId = ? AND paramName = ?";
		
	
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(select))	{
			
			preparedStatement.setInt(1, queryId);
			preparedStatement.setString(2, paramName);
			
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ElasticQueryParam elasticQueryParam = new ElasticQueryParam(rs.getInt("paramId") ,
															            	rs.getInt("queryId"), 
															            	rs.getString("paramName"),
															            	rs.getString("paramDefaultValue"),
															            	rs.getString("paramType"), 
															            	rs.getString("paramPosition"), 
															            	rs.getInt("paramOrder") );
            	
            	
            	ret.add(elasticQueryParam);
            }


		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	public void 
	mergeQueryParam(final int queryId,
					final String queryParamName,
					final String queryParamDefaultValue, 
					final String queryParamType,
					final String queryParamPosition,
					final int queryParamOrder) throws Exception {

		String addDslParam = "MERGE INTO repoQueryParamTable (queryId, paramName, paramDefaultValue, paramType, paramPosition, paramOrder) "
							+ " KEY (queryId, paramName) "
							+ " VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addDslParam))	{
			
			preparedStatement.setInt(1, queryId);
			preparedStatement.setString(2, queryParamName);
			preparedStatement.setString(3, queryParamDefaultValue);
			preparedStatement.setString(4, queryParamType);
			preparedStatement.setString(5, queryParamPosition);
			preparedStatement.setInt(6, queryParamOrder);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public void 
	deleteQueryParam(final int queryId, 
					final int queryParamId) throws Exception {
		if(queryId <= 0)
			throw new Exception("Query Id is null");
		if(queryParamId <= 0)
			throw new Exception("Query Param Id is null");
		
		String deleteSqlParam = "DELETE repoQueryParamTable  WHERE queryId = ? AND paramId = ?";
		
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(deleteSqlParam))	{
			
			preparedStatement.setInt(1, queryId);
			preparedStatement.setInt(2, queryParamId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
		
	///////////////////////////////// BRIDGES //////////////////////////////////////////////////////////////////////////////////
	
	public List<QueryToClusterBridge> 
	getQueryToClusterBridge(final int queryId) throws Exception {
		List<QueryToClusterBridge> ret = new ArrayList<>();
		String addSqlParam = "SELECT q.id, c.clusterId, c.uniqueName, q.active "
							+ " FROM repoQueryToClusterTable q JOIN elasticClusterTable c "
							+ " on c.clusterId = q.clusterId WHERE q.queryId = ?";		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			
			preparedStatement.setInt(1, queryId);
			
			
			ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	QueryToClusterBridge queryToClusterBridge 
            	= new QueryToClusterBridge(	rs.getLong("id") ,
            								queryId,
            								rs.getInt("clusterId") ,
            								rs.getString("uniqueName") ,
            								rs.getInt("active") 
											)	;
            	
            	
            	ret.add(queryToClusterBridge);
            }
    		return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public void 
	mergeQueryToClusterBridge(	final long queryId,
								final long clusterId ,
								final int active   
								) throws Exception {

		String addSqlParam = "MERGE INTO repoQueryToClusterTable "
							+ " (queryId, clusterId, active) "
							+ " KEY (queryId, clusterId)  "
							+ "VALUES (	?,	?,  ?)";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			
			preparedStatement.setLong(1, queryId);
			preparedStatement.setLong(2, clusterId);
			preparedStatement.setInt(3, active);
			
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteActiveQueryToClusterBridge(final int queryId,
									final int clusterId) throws Exception {
		String addSqlParam = "DELETE FROM repoQueryToClusterTable WHERE queryId = ? AND clusterId = ?";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setInt(1, queryId);
			preparedStatement.setInt(2, clusterId);
			preparedStatement.executeUpdate();

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
			if(associationName != null && !associationName.isEmpty()) {
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
								final String associationName 
								) throws Exception {
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
		
		RepoAssociationToQueryTableList rList = new RepoAssociationToQueryTableList(new ArrayList<>());
		
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
										final long queryId) throws Exception {

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
	RepoAssociationToIndexTableList 
	getRepoAssociationToIndexTable(final long associationId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT 	associationToIndexId, "
								+ "associationId, "
								+ "indexId "
								+ "FROM repoAssociationToIndexTable WHERE associationId = ?";
		
		RepoAssociationToIndexTableList rList = new RepoAssociationToIndexTableList(new ArrayList<RepoAssociationToIndexTable>());
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, associationId);
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	RepoAssociationToIndexTable r 
            	= new RepoAssociationToIndexTable(	rs.getLong("associationToIndexId"),
            										rs.getLong("associationId"),
            										rs.getLong("INDEXId")
					            		   		  );
            	rList.addRepoAssociationToIndexTable(r);
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
	mergeRepoAssociationToIndexTable(	final long associationToIndexId,
										final long associationId, 
										final long indexId) throws Exception {
		String addSqlParam = "MERGE INTO repoAssociationToIndexTable "
							+ " (associationToIndexId, associationId, indexId) "
							+ " KEY (associationId, indexId)  "
							+ "VALUES (	?, ?, ?)";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{

			preparedStatement.setLong(1, associationToIndexId);
			preparedStatement.setLong(2, associationId);
			preparedStatement.setLong(3, indexId);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteRepoAssociationToIndexTable(final long associationId, final long indexId) throws Exception {
		String addSqlParam = "DELETE FROM repoAssociationToIndexTable WHERE associationId = ? AND indexId = ?";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, associationId);
			preparedStatement.setLong(2, indexId);
			preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public 
	RepoIndexTableList 
	getRepoIndexTable(final String indexName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT indexId, "
								+ "clusterId, "
								+ "indexName "
								+ "FROM repoIndexTable WHERE indexName LIKE ?";
		
		RepoIndexTableList rList = new RepoIndexTableList(new ArrayList<RepoIndexTable>());
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, "%" + indexName + "%'");
            
			ResultSet rs = preparedStatement.executeQuery(sqlString);
            while ( rs.next() ) {
            	RepoIndexTable repoIndexTable 
            	= new RepoIndexTable(	rs.getLong("indexId"),
            							rs.getLong("clusterId"),
            							rs.getString("indexName")
					            	);
            	rList.addRepoIndexTable(repoIndexTable);
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
	mergeRepoIndexTable(final long indexId,
						final long clusterId, 
						final String indexName) throws Exception {
		String addSqlParam = "MERGE INTO repoIndexTable (indexId, clusterId, indexName) KEY (clusterId, indexName)  "
							+ "VALUES (	?, ?, ?)";
		

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{

			preparedStatement.setLong(1, indexId);
			preparedStatement.setLong(2, clusterId);
			preparedStatement.setString(3, indexName);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	deleteRepoIndexTable(final long indexId) throws Exception {
		String addSqlParam = "DELETE FROM repoAssociationToIndexTable WHERE indexId = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(addSqlParam))	{
			preparedStatement.setLong(1, indexId);
			preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
}

