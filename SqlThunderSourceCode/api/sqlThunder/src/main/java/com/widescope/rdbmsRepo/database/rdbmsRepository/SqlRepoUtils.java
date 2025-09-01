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


package com.widescope.rdbmsRepo.database.rdbmsRepository;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlParameter;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabaseSchemaBridge;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDynamicSql;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoFlow;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoFlowBridge;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoFlowDetail;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoParam;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmToDbBridge;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmToDbBridgeList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.sqlThunder.utils.StaticUtils;



public final class SqlRepoUtils {

	public static Set<String> databaseTypesMap = new HashSet<>(); // no need to be concurrent
	// sqlRepoDatabaseMap is a map with key schema_unique_user_name
	public static Map<String, SqlRepoDatabase> sqlRepoDatabaseMap = new ConcurrentHashMap<>();
	// SqlRepo bridge Map
	public static Map<Long, List<Long> > sqlRepoBridgeMap = new ConcurrentHashMap<>();
	// sqlRepoDynamicSqlMap is a map with Key SqlRepoDynamicSql id
	public static Map<Long, SqlRepoDynamicSql> sqlRepoDynamicSqlMap = new ConcurrentHashMap<>();
	// sqlRepoFlowMap is a map with Key SqlRepoFlow id
	public static Map<Long, SqlRepoFlow> sqlRepoFlowMap = new ConcurrentHashMap<>();
	
	// Mongodb clusters 
	public static Map<String, MongoClusterRecord> mongoDbMap = new ConcurrentHashMap<>();

	public static Map<String, ElasticCluster> elasticDbMap = new ConcurrentHashMap<>();


	
	
		
	
	public static boolean isDatabaseId(final long databaseId) {
		boolean ret = false;
		for (Map.Entry<String, SqlRepoDatabase> mapElement : sqlRepoDatabaseMap.entrySet()) {
            if(mapElement.getValue().getDatabaseId() == databaseId) {
            	ret = true;
            	break;
            }
        }
		return ret;
	}


	public static void populateDatabaseTypes(final DbConnectionInfo connectionDetailInfo) {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0)
				rs = statement.executeQuery("SELECT database_type FROM repo_db_types; ");
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0)
				rs = statement.executeQuery("SELECT database_type FROM repodb.sqlrepo.repo_db_types; ");
			else
				rs = statement.executeQuery("SELECT database_type FROM sqlrepo.repo_db_types; ");
			
			
	        while (rs.next()) {
	        	String database_type = rs.getString("database_type");
	        	databaseTypesMap.add(database_type);
	        }
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
		}
		finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	

	public static void populateDatabase(final DbConnectionInfo connectionDetailInfo) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try	{

			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			
			String sqlString = "SELECT database_id, "
									+ "database_type, "
									+ "database_name, "
									+ "database_server, "
									+ "database_port, "
									+ "database_description, "
									+ "database_warehouse_name, "
									+ "schema_name, "
									+ "schema_service, "
									+ "schema_password, "
									+ "schema_unique_user_name, "
									+ "account, "
									+ "other, "
									+ "tunnel_local_port, "
									+ "tunnel_remote_host_address, "
									+ "tunnel_remote_host_port,	"
									+ "tunnel_remote_host_user, "
									+ "tunnel_remote_host_user_password, "
									+ "tunnel_remote_host_rsa_Key "
									+ "FROM #repodb##sqlrepo#repo_database WHERE active = 1; ";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			
			rs = statement.executeQuery(sqlString);
	        while (rs.next()) {
	        	int database_id = rs.getInt("database_id");
	        	String database_type = rs.getString("database_type");
	        	String database_name  = rs.getString("database_name");
	        	String database_server = rs.getString("database_server");
	        	String database_port = rs.getString("database_port");
	        	String database_description = rs.getString("database_description");
	        	String warehouse = rs.getString("database_warehouse_name");
	        	String schema_name = rs.getString("schema_name");
	        	String schema_service = rs.getString("schema_service");
	        	String schema_password = rs.getString("schema_password");
	        	String schema_unique_user_name = rs.getString("schema_unique_user_name");
	        	
	        	String account = rs.getString("account");
	        	String other = rs.getString("other");
	        	
	        	String  tunnel_local_port = rs.getString("tunnel_local_port");
	        	String  tunnel_remote_host_address = rs.getString("tunnel_remote_host_address");
	        	String tunnel_remote_host_port = rs.getString("tunnel_remote_host_port");
				String  tunnel_remote_host_user = rs.getString("tunnel_remote_host_user");
				String  tunnel_remote_host_user_password = rs.getString("tunnel_remote_host_user_password");
				String  tunnel_remote_host_rsa_Key = rs.getString("tunnel_remote_host_rsa_Key");
	        	
	        	
	        	SqlRepoDatabase sqlRepoDatabase = new SqlRepoDatabase(database_id, 
	        			                                              database_type, 
	        			                                              database_name, 
	        			                                              database_server, 
	        			                                              database_port, 
	        			                                              database_description, 
	        			                                              warehouse, 
	        			                                              account,
	        			                                              other,
	        			                                              schema_name,
	        			                                              schema_service,
	        			                                              schema_password,
	        			                                              schema_unique_user_name,
	        			                                              tunnel_local_port, 
	        			      										  tunnel_remote_host_address, 
	        			      										  tunnel_remote_host_port,
	        			      										  tunnel_remote_host_user,
	        			      										  tunnel_remote_host_user_password,
	        			      										  tunnel_remote_host_rsa_Key,
	        			                                              1);
	        	sqlRepoDatabaseMap.put(schema_unique_user_name, sqlRepoDatabase);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	

	public static void populateDynamicSql(final DbConnectionInfo connectionDetailInfo) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT sql_id, "
									+ "sql_type, "
									+ "sql_category, "
									+ "sql_name, "
									+ "sql_description, "
									+ "sql_content, "
									+ "execution "
									+ "FROM #repodb##sqlrepo#repo_dynamic_sql WHERE active = 1; ";
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			
			rs = statement.executeQuery(sqlString);
	        while (rs.next()) {
	        	long sql_id = rs.getInt("sql_id");
	        	String sql_type = rs.getString("sql_type");
	        	String sql_category = rs.getString("sql_category");
	        	String sql_name = rs.getString("sql_name");
	        	String sql_description = rs.getString("sql_description");
	        	String sql_content = rs.getString("sql_content");
	        	String execution = rs.getString("execution");

	        	
	        	
	        	SqlRepoDynamicSql sqlRepoDynamicSql = new SqlRepoDynamicSql(sql_id,
															                sql_type, 
																            sql_category, 
																            sql_name,
																            sql_description,
																            sql_content,
																            execution,
																            1);
	        	SqlRepoUtils.sqlRepoDynamicSqlMap.put(sql_id, sqlRepoDynamicSql);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	

	public static void populateDynamicSqlParam(final DbConnectionInfo connectionDetailInfo) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try	{

			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT sql_param_id, "
									+ "sql_id, "
									+ "sql_param_name, "
									+ "sql_param_default_value, "
									+ "sql_param_type, "
									+ "sql_param_position, "
									+ "sql_param_order, "
									+ "sql_param_origin_tbl, "
									+ "sql_param_origin_col FROM #repodb##sqlrepo#repo_dynamic_sql_param ORDER BY sql_id, sql_param_id";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			
			rs = statement.executeQuery(sqlString);
			
	        while (rs.next()) {
	        	long sql_param_id = rs.getInt("sql_param_id");
	        	long sql_id = rs.getInt("sql_id");
	        	String sql_param_name  = rs.getString("sql_param_name");
	        	String sql_param_default_value = rs.getString("sql_param_default_value");
	        	String sql_param_type = rs.getString("sql_param_type");
	        	String sql_param_position = rs.getString("sql_param_position");
	        	int sql_param_order = rs.getInt("sql_param_order");
	        	String sql_param_origin_tbl = rs.getString("sql_param_origin_tbl");
	        	String sql_param_origin_col = rs.getString("sql_param_origin_col");
	        	
	        	SqlRepoParam sqlRepoParam = new SqlRepoParam(sql_param_id, 
	        			                                     sql_id, 
	        			                                     sql_param_name, 
	        			                                     sql_param_default_value, 
	        			                                     sql_param_type,
	        			                                     sql_param_position, 
	        			                                     sql_param_order,
	        			                                     sql_param_origin_tbl, 
	        			                                     sql_param_origin_col);
	        	
	        	
	        	sqlRepoDynamicSqlMap.get(sql_id).getSqlRepoParamList().add(sqlRepoParam);
	        	SqlParameter sqlParam = new SqlParameter(sql_param_id, sql_param_name, "");
	        	sqlRepoDynamicSqlMap.get(sql_id).getParamList().add(sqlParam);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	
	
	

	public static void populateDynamicSqlFlow(final DbConnectionInfo connectionDetailInfo) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try	{

			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT sql_flow_id, sql_flow_name, sql_description, sql_flow_active FROM #repodb##sqlrepo#repo_dynamic_sql_flow WHERE dynamic_sql_flow_active = true; ";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
					
			rs = statement.executeQuery(sqlString);
	        while (rs.next()) {
	        	int dynamic_sql_flow_id = rs.getInt("sql_flow_id");
	        	String dynamic_sql_flow_name = rs.getString("sql_flow_name");
	        	String dynamic_sql_description  = rs.getString("sql_description");
	        	boolean dynamic_sql_flow_active = rs.getBoolean("sql_flow_active");
	        	SqlRepoFlow sqlRepoFlow = new SqlRepoFlow(dynamic_sql_flow_id, dynamic_sql_flow_name, dynamic_sql_description, dynamic_sql_flow_active);
	        	sqlRepoFlowMap.put((long) dynamic_sql_flow_id, sqlRepoFlow);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	

	public static void populateDynamicSqlFlowDetail(final DbConnectionInfo connectionDetailInfo) throws Exception {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try	{

			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT sql_flow_detail_id, "
									+ "sql_flow_id, "
									+ "sql_id, "
									+ "database_id, "
									+ "schema_id, "
									+ "execution_type, "
									+ "max_seconds_wait, "
									+ "is_result, "
									+ "flow_order, "
									+ "input_from_previous FROM #repodb##sqlrepo#repo_dynamic_sql_flow_detail ORDER BY dynamic_sql_flow_id, flow_order";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
					
			rs = statement.executeQuery(sqlString);
			
	        while (rs.next()) {
	        	int dynamic_sql_flow_detail_id = rs.getInt("sql_flow_detail_id");
	        	int dynamic_sql_flow_id = rs.getInt("sql_flow_id");
	        	int dynamic_sql_id  = rs.getInt("sql_id");
	        	int database_id  = rs.getInt("database_id");
	        	int schema_id = rs.getInt("schema_id");
	        	String execution_type = rs.getString("execution_type");
	        	int max_seconds_wait = rs.getInt("max_seconds_wait");
	        	String is_result = rs.getString("is_result");
	        	int flow_order = rs.getInt("flow_order");
	        	String input_from_previous = rs.getString("input_from_previous");
	        		        	
	        	SqlRepoFlowDetail sqlRepoFlowDetail = new SqlRepoFlowDetail(dynamic_sql_flow_detail_id,
																    		dynamic_sql_flow_id,
																    		dynamic_sql_id,
																    		database_id,
																    		schema_id,
																    		execution_type,
																    		max_seconds_wait,
																    		is_result,
																    		flow_order,
																    		input_from_previous);
	        	
	        	
	        	sqlRepoFlowMap.get((long) dynamic_sql_flow_id).getMapSqlRepoFlowDetail().add(sqlRepoFlowDetail) ;
	        	
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	
	public static void populateDynamicSqlFlowBridge(final DbConnectionInfo connectionDetailInfo) throws Exception	{

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT sql_flow_bridge_id, "
									+ " sql_flow_id, "
									+ " database_id, "
									+ " schema_id, "
									+ " dynamic_sql_flow_bridge_active "
									+ " FROM #repodb##sqlrepo#repo_dynamic_sql_flow_bridge WHERE sql_flow_bridge_active = True ORDER BY sql_flow_id";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
					
			rs = statement.executeQuery(sqlString);
	        while (rs.next()) {
	        	int database_schema_bridge_id = rs.getInt("database_schema_bridge_id");
	        	int dynamic_sql_id = rs.getInt("dynamic_sql_id");
	        	int database_id = rs.getInt("database_id");
	        	int schema_id = rs.getInt("schema_id");
	        	SqlRepoFlowBridge sqlRepoFlowBridge = new SqlRepoFlowBridge(database_schema_bridge_id, 
	        																dynamic_sql_id, 
	        																database_id, 
	        																schema_id, 
	        																true);
	        	sqlRepoFlowMap	.get((long) dynamic_sql_id)
	        					.getMapSqlRepoFlowBridge()
	        					.put(database_schema_bridge_id, sqlRepoFlowBridge);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	

	


	public static void populateRepo(String sqlRepoName) throws Exception {
		try {

			DbConnectionInfo connectionInfoSave =
					DbConnectionInfo.makeDbConnectionInfo("H2","","",	sqlRepoName,"localhost","0","sa","sa","Sql Repo DB",				"",
							"",
							"",
							"0",
							"",
							"0",
							"",
							"",
							"");
			DbUtil.connectionDetailsTable.put(sqlRepoName, connectionInfoSave);
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			if(connectionInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				String fileName = "./" + sqlRepoName + ".mv.db";
				SqlRepoStorageDb.generateSchema(sqlRepoName);
			}

			SqlRepoUtils.populateDatabaseTypes(connectionInfo);
			SqlRepoUtils.populateDatabase(connectionInfo);
			SqlRepoUtils.populateDynamicSql(connectionInfo);
			SqlRepoUtils.populateDynamicSqlParam(connectionInfo);
			SqlRepoUtils.populateSqlToDbBridge(connectionInfo);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}





	public static void addDatabase(final DbConnectionInfo connectionDetailInfo, 
						           final String database_type, 
						           final String database_name, 
						           final String database_server,
						           final int database_port, 
						           final String database_description,
						           final String database_warehouse_name,
						           final String schema_name,  
						           final String schema_service,
						           final String schema_password,
						           final String schema_unique_user_name,
						           final int tunnel_local_port, 
								   final String tunnel_remote_host_address, 
								   final int tunnel_remote_host_port,
								   final String tunnel_remote_host_user,
								   final String tunnel_remote_host_user_password,
								   final String tunnel_remote_host_rsa_Key,
						           final int database_active) throws Exception {

		Connection conn = null;
		Statement statement = null;

		try	{

			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "INSERT INTO #repodb##sqlrepo#repo_database (database_type, "
														+ "database_name, "
														+ "database_server, "
														+ "database_port, "
														+ "database_description, "
														+ "database_warehouse_name, "
														+ "schema_name, "
														+ "schema_service, "
														+ "schema_password, "
														+ "schema_unique_user_name,"
														+ "tunnel_local_port, "
														+ "tunnel_remote_host_address, "
														+ "tunnel_remote_host_port,	"
														+ "tunnel_remote_host_user, "
														+ "tunnel_remote_host_user_password, "
														+ "tunnel_remote_host_rsa_Key, "
														+ "active "
											+ ") VALUES('#database_type#', "
														+ "'#database_name#', "
														+ "'#database_server#', "
														+ "'#database_port#', "
														+ "'#database_description#', "
														+ "'#database_warehouse_name#', "
														+ "'#schema_name#', "
														+ "'#schema_service#', "
														+ "'#schema_password#', "
														+ "'#schema_unique_user_name#',"
														+ "#tunnel_local_port#, "
														+ "'#tunnel_remote_host_address#', "
														+ "#tunnel_remote_host_port#,	"
														+ "'#tunnel_remote_host_user#', "
														+ "'#tunnel_remote_host_user_password#', "
														+ "'#tunnel_remote_host_rsa_Key#', "
														+ "#active# "
														+ ")";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
				
			sqlString = sqlString.replace("#database_type#", database_type== null ? "":database_type);
			sqlString = sqlString.replace("#database_name#", database_name== null ? "":database_name);
			sqlString = sqlString.replace("#database_server#", database_server== null ? "":database_server );
			sqlString = sqlString.replace("#database_port#", String.valueOf(database_port));
			sqlString = sqlString.replace("#database_description#", database_description== null ? "":database_description );
			sqlString = sqlString.replace("#database_warehouse_name#", database_warehouse_name== null ? "":database_warehouse_name  );
			sqlString = sqlString.replace("#schema_name#", schema_name== null ? "":schema_name );
			sqlString = sqlString.replace("#schema_service#", schema_service== null ? "":schema_service );
			sqlString = sqlString.replace("#schema_password#", schema_password== null ? "":schema_password );		
			sqlString = sqlString.replace("#schema_unique_user_name#", schema_unique_user_name== null ? "":schema_unique_user_name );
			sqlString = sqlString.replace("#tunnel_local_port#",String.valueOf(tunnel_local_port) );
			sqlString = sqlString.replace("#tunnel_remote_host_address#", tunnel_remote_host_address== null ? "":tunnel_remote_host_address );
			sqlString = sqlString.replace("#tunnel_remote_host_port#",String.valueOf(tunnel_remote_host_port) );
			sqlString = sqlString.replace("#tunnel_remote_host_user#", tunnel_remote_host_user== null ? "":tunnel_remote_host_user );
			sqlString = sqlString.replace("#tunnel_remote_host_user_password#", tunnel_remote_host_user== null ? "":tunnel_remote_host_user );
			sqlString = sqlString.replace("#tunnel_remote_host_rsa_Key#", tunnel_remote_host_rsa_Key== null ? "":tunnel_remote_host_rsa_Key );
			sqlString = sqlString.replace("#active#", String.valueOf(database_active) );
			statement.executeUpdate(sqlString);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	
	public static SqlRepoDatabase getDatabase(	final DbConnectionInfo connectionDetailInfo, 
												final String schema_unique_user_name) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		SqlRepoDatabase sqlRepoDatabase = null;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT database_id, "
									+ "database_type, "
									+ "database_name, "
									+ "database_server, "
									+ "database_port, "
									+ "database_description, "
									+ "database_warehouse_name, "
									+ "account, "
									+ "other, "
									+ "schema_name, "
									+ "schema_service, "
									+ "schema_password, "
									+ "schema_unique_user_name,"
									+ "tunnel_local_port, "
									+ "tunnel_remote_host_address, "
									+ "tunnel_remote_host_port,	"
									+ "tunnel_remote_host_user, "
									+ "tunnel_remote_host_user_password, "
									+ "tunnel_remote_host_rsa_Key "
									+ " FROM #repodb##sqlrepo#repo_database WHERE schema_unique_user_name = '" + schema_unique_user_name + "'" ;
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			rs = statement.executeQuery(sqlString);
	        while (rs.next()) {
	        	int database_id = rs.getInt("database_id");
	        	String database_type = rs.getString("database_type");
	        	String database_name  = rs.getString("database_name");
	        	String database_server = rs.getString("database_server");
	        	String database_port = rs.getString("database_port");
	        	String database_description = rs.getString("database_description");
	        	String warehouse = rs.getString("database_warehouse_name");
	        	String account = rs.getString("account");
	        	String other = rs.getString("other");
	        	String schema_name = rs.getString("schema_name");
	        	String schema_service = rs.getString("schema_service");
	        	String schema_password = rs.getString("schema_password");
	        	String  tunnel_local_port = rs.getString("tunnel_local_port");
	        	String  tunnel_remote_host_address = rs.getString("tunnel_remote_host_address");
	        	String tunnel_remote_host_port = rs.getString("tunnel_remote_host_port");
				String  tunnel_remote_host_user = rs.getString("tunnel_remote_host_user");
				String  tunnel_remote_host_user_password = rs.getString("tunnel_remote_host_user_password");
				String  tunnel_remote_host_rsa_Key = rs.getString("tunnel_remote_host_rsa_Key");
	        	
	        	sqlRepoDatabase = new SqlRepoDatabase(database_id, 
	        			                              database_type, 
	        			                              database_name, 
	        			                              database_server, 
	        			                              database_port, 
	        			                              database_description, 
	        			                              warehouse, 
	        										  account,
	        		                                  other,
	        			                              schema_name,
	        			                              schema_service,
	        			                              schema_password,
	        			                              schema_unique_user_name,
	        			                              tunnel_local_port, 
		      										  tunnel_remote_host_address, 
		      										  tunnel_remote_host_port,
		      										  tunnel_remote_host_user,
		      										  tunnel_remote_host_user_password,
		      										  tunnel_remote_host_rsa_Key,
	        			                              1);
	        
	        	
	        }
	        
	        return sqlRepoDatabase;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	public static SqlRepoDatabase getDatabase(	final DbConnectionInfo connectionDetailInfo, 
												final long databaseId) throws Exception {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		SqlRepoDatabase sqlRepoDatabase = null;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT database_id, "
									+ "database_type, "
									+ "database_name, "
									+ "database_server, "
									+ "database_port, "
									+ "database_description, "
									+ "database_warehouse_name, "
									+ "account, "
									+ "other, "
									+ "schema_name, "
									+ "schema_service, "
									+ "schema_password, "
									+ "schema_unique_user_name,"
									+ "tunnel_local_port, "
									+ "tunnel_remote_host_address, "
									+ "tunnel_remote_host_port,	"
									+ "tunnel_remote_host_user, "
									+ "tunnel_remote_host_user_password, "
									+ "tunnel_remote_host_rsa_Key "
									+ " FROM #repodb##sqlrepo#repo_database WHERE database_Id = " + databaseId ;
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			rs = statement.executeQuery(sqlString);
			while (rs.next()) {
				int database_id = rs.getInt("database_id");
				String database_type = rs.getString("database_type");
				String database_name  = rs.getString("database_name");
				String database_server = rs.getString("database_server");
				String database_port = rs.getString("database_port");
				String database_description = rs.getString("database_description");
				String warehouse = rs.getString("database_warehouse_name");
				String account = rs.getString("account");
				String other = rs.getString("other");
				String schema_name = rs.getString("schema_name");
				String schema_service = rs.getString("schema_service");
				String schema_password = rs.getString("schema_password");
				String schema_unique_user_name = rs.getString("schema_unique_user_name");
				String  tunnel_local_port = rs.getString("tunnel_local_port");
				String  tunnel_remote_host_address = rs.getString("tunnel_remote_host_address");
				String tunnel_remote_host_port = rs.getString("tunnel_remote_host_port");
				String  tunnel_remote_host_user = rs.getString("tunnel_remote_host_user");
				String  tunnel_remote_host_user_password = rs.getString("tunnel_remote_host_user_password");
				String  tunnel_remote_host_rsa_Key = rs.getString("tunnel_remote_host_rsa_Key");
				
				sqlRepoDatabase = new SqlRepoDatabase(database_id, 
				                  database_type, 
				                  database_name, 
				                  database_server, 
				                  database_port, 
				                  database_description, 
				                  warehouse, 
								  account,
				                  other,
				                  schema_name,
				                  schema_service,
				                  schema_password,
				                  schema_unique_user_name,
				                  tunnel_local_port, 
								  tunnel_remote_host_address, 
								  tunnel_remote_host_port,
								  tunnel_remote_host_user,
								  tunnel_remote_host_user_password,
								  tunnel_remote_host_rsa_Key,
				                  1);
			
			
			}
			
			return sqlRepoDatabase;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	public static boolean isDatabase(final DbConnectionInfo connectionDetailInfo, 
			                         final String database_type,
			                         final String database_server,
			                         final String database_port,
			                         final String schema_name) throws Exception {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		boolean ret = false;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT database_id FROM #sqlrepo#repo_database WHERE database_type = ? AND database_server = ? AND database_port = ? AND schema_name = ? ";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			
			rs = statement.executeQuery(sqlString);
	        if (rs.next()) {
	        	ret = true;
	        }
	        
	        return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	public static void updateDatabase(final DbConnectionInfo connectionDetailInfo, 
									  final long database_id,
							          final String database_type, 
								      final String database_name, 
								      final String database_server,
								      final int database_port, 
								      final String database_description,
								      final String database_warehouse_name,
								      final String schema_name,  
								      final String schema_service,
								      final String schema_password,
								      final String schema_unique_user_name,
								      final int tunnel_local_port, 
								      final String tunnel_remote_host_address, 
								      final int tunnel_remote_host_port,
									  final String tunnel_remote_host_user,
									  final String tunnel_remote_host_user_password,
									  final String tunnel_remote_host_rsa_Key,
									  final int database_active) throws Exception {

		Connection conn = null;
		Statement statement = null;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "UPDATE #repodb##sqlrepo#repo_database SET 	database_type           = #database_type#, "
					+ "                                  			database_name           = '#database_name#', "
					+ "                                  			database_server         = '#database_server#',"
					+ "                                  			database_port           = #database_port#,  "
					+ "                                  			database_description    = '#database_description#', "
					+ "                                  			database_warehouse_name = '#database_warehouse_name#', "
					+ "                                          	schema_name             = '#schema_name#', "
					+ "                                          	schema_service          = '#schema_service#', "
					+ "                                          	schema_password         = '#schema_password#', "
					+ "                                          	schema_unique_user_name = #'schema_unique_user_name#',"
					+ "                                          	tunnel_local_port       = #tunnel_local_port#,"
					+ "                                          	tunnel_remote_host_address = '#tunnel_remote_host_address#',"
					+ "                                          	tunnel_remote_host_port = #tunnel_remote_host_port#,"
					+ "                                          	tunnel_remote_host_user = '#tunnel_remote_host_user#',"
					+ "                                          	tunnel_remote_host_user_password = '#tunnel_remote_host_user_password#',"
					+ "                                          	tunnel_remote_host_rsa_Key = '#tunnel_remote_host_rsa_Key#',"
					+ "                                          	database_active         = #database_active#"
					+ "         WHERE  database_id     = #database_id#";
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			
			sqlString = sqlString.replace("#database_type#", database_type== null ? "":database_type);
			sqlString = sqlString.replace("#database_name#", database_name== null ? "":database_name);
			sqlString = sqlString.replace("#database_server#", database_server== null ? "":database_server);
			sqlString = sqlString.replace("#database_port#", Integer.toString(database_port));
			sqlString = sqlString.replace("#database_description#", database_description== null ? "":database_description);
			sqlString = sqlString.replace("#database_warehouse_name#", database_warehouse_name== null ? "":database_warehouse_name);
			sqlString = sqlString.replace("#schema_name#", schema_name== null ? "":schema_name);
			sqlString = sqlString.replace("#schema_service#", schema_service== null ? "":schema_service);
			sqlString = sqlString.replace("#schema_password#", schema_password== null ? "":schema_password);
			sqlString = sqlString.replace("#schema_unique_user_name#", schema_unique_user_name== null ? "":schema_unique_user_name);
			
			sqlString = sqlString.replace("#tunnel_local_port#", Integer.toString(tunnel_local_port).toString() );
			sqlString = sqlString.replace("#tunnel_remote_host_address#", tunnel_remote_host_address== null ? "":tunnel_remote_host_address);
			sqlString = sqlString.replace("#tunnel_remote_host_port#", Integer.toString(tunnel_remote_host_port).toString() );
			sqlString = sqlString.replace("#tunnel_remote_host_user#", tunnel_remote_host_user== null ? "":tunnel_remote_host_user);
			sqlString = sqlString.replace("#tunnel_remote_host_user_password#", tunnel_remote_host_user_password== null ? "":tunnel_remote_host_user_password);
			sqlString = sqlString.replace("#tunnel_remote_host_rsa_Key#", tunnel_remote_host_rsa_Key== null ? "":tunnel_remote_host_rsa_Key);
			sqlString = sqlString.replace("#database_active#", String.valueOf(database_active) );
			sqlString = sqlString.replace("#database_id#", String.valueOf(database_id) );
			statement.executeUpdate(sqlString);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	public static void deleteDatabase(final DbConnectionInfo connectionDetailInfo, 
									  final long database_id) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();

			String sqlString = "DELETE FROM #repodb##sqlrepo#repo_database WHERE database_id = " + database_id;
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}

			statement.executeUpdate(sqlString);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}



	public static List<String> getDatabaseNameList(	final DbConnectionInfo connectionDetailInfo, 
									            	final String database_id_list) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		List<String> ret = new ArrayList<String>();
		
		try	{
			
			// Check if all are integers then use to check all references exist
			List<String> lstString = Arrays.asList(database_id_list.split(",", -1));
			List<Integer> lstInts = StaticUtils.convertStringListToIntList(lstString, Integer::parseInt);
			String ss = org.apache.commons.lang3.StringUtils.join(lstInts, ',');
			
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String sqlString = "SELECT database_name FROM #sqlrepo#repo_database WHERE database_id IN (" + ss + ")";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "" );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "repodb." );
			}
			else { 
				sqlString = sqlString.replace("#sqlrepo#", "sqlrepo." );
				sqlString = sqlString.replace("#repodb#", "" );
			}
			
			
			rs = statement.executeQuery(sqlString);
			while (rs.next()) {
				ret.add( rs.getString("database_name") );
			}
			
			if(ret.size() != lstInts.size()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Error : List of IDs provided are not all found in database"));
			}
			
			return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	
	public static List<String> getDbNameListFromCache(	final DbConnectionInfo connectionDetailInfo, 
        												final String database_id_list )  {
        return new ArrayList<String>();
	}
	
	
	
	public static List<SqlRepoDynamicSql> 
	getSql(	final DbConnectionInfo connectionDetailInfo, 
			final String sqlName) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		List<SqlRepoDynamicSql> ret = new ArrayList<SqlRepoDynamicSql>();
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String getSql = "SELECT sql_id, "
								+ " sql_type, "
								+ " sql_return_type, "
								+ " sql_category, "
								+ " sql_name, "
								+ " sql_description,"
								+ " sql_content, "
								+ " execution, active "
								+ " from #repodb##sqlrepo#repo_dynamic_sql WHERE sql_name = '#sqlname#'";
			
			
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				getSql = getSql.replace("#sqlrepo#", "" );
				getSql = getSql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				getSql = getSql.replace("#sqlrepo#", "sqlrepo." );
				getSql = getSql.replace("#repodb#", "repodb." );
			}
			else { 
				getSql = getSql.replace("#sqlrepo#", "sqlrepo." );
				getSql = getSql.replace("#repodb#", "" );
			}
			
			getSql = getSql.replace("#sqlname#", sqlName );
			
			rs = statement.executeQuery(getSql);
			if (rs.next()) {
				SqlRepoDynamicSql sqlStatement = new SqlRepoDynamicSql();
				sqlStatement.setSqlId(rs.getInt("sql_id"));
				
				sqlStatement.setSqlType(rs.getString("sql_type"));
				sqlStatement.setSqlCategory(rs.getString("sql_category"));
				sqlStatement.setSqlName(rs.getString("sql_name"));
				sqlStatement.setSqlDescription(rs.getString("sql_description"));
				sqlStatement.setSqlContent(rs.getString("sql_content"));
				sqlStatement.setExecution(rs.getString("execution"));
				sqlStatement.setActive(rs.getInt("active"));
				sqlStatement.setSqlRepoParamList(null);
				sqlStatement.setParamList(null);
				ret.add(sqlStatement);

			}
			
			return ret;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	public static void addSql(final DbConnectionInfo connectionDetailInfo, 
							  final String sqlType,
							  final String sqlReturnType, 
							  final String sqlCategory,
							  final String sqlName,
							  final String sqlDescription,
							  final String sqlContent,
							  final String execution,
							  final int active) throws Exception {
		Connection conn = null;
		Statement statement = null;

		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String addSql = "INSERT INTO #repodb##sqlrepo#repo_dynamic_sql(sql_type, "
																+ "sql_return_type, "
																+ "sql_category, "
																+ "sql_name, "
																+ "sql_description, "
																+ "sql_content, "
																+ "execution, "
																+ "active) "
																+ "VALUES (#sqlType#', "
																		+ "'#sqlReturnType#', "
																		+ "'#sqlCategory#', "
																		+ "'#sqlName#', "
																		+ "'#sqlDescription#', "
																		+ "'#sqlContent#', "
																		+ "'#execution#', "
																		+ "#active#)";
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				addSql = addSql.replace("#sqlrepo#", "" );
				addSql = addSql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				addSql = addSql.replace("#sqlrepo#", "sqlrepo." );
				addSql = addSql.replace("#repodb#", "repodb." );
			}
			else { 
				addSql = addSql.replace("#sqlrepo#", "sqlrepo." );
				addSql = addSql.replace("#repodb#", "" );
			}
			

			addSql = addSql.replace("#sqlType#", sqlType== null ? "":sqlType);
			addSql = addSql.replace("#sqlReturnType#", sqlReturnType== null ? "":sqlReturnType );
			addSql = addSql.replace("#sqlCategory#", sqlCategory== null ? "":sqlCategory );
			addSql = addSql.replace("#sqlName#", sqlName== null ? "":sqlName );
			addSql = addSql.replace("#sqlDescription#", sqlDescription== null ? "":sqlDescription );
			addSql = addSql.replace("#sqlContent#", sqlContent== null ? "":sqlContent );
			addSql = addSql.replace("#execution#", execution== null ? "":execution );
			addSql = addSql.replace("#active#", String.valueOf(active));
			statement.executeUpdate(addSql);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	
	public static void updateSql(final DbConnectionInfo connectionDetailInfo,
								  final long sqlId,
							      final long databaseId,
								  final String sqlType,
								  final String sqlReturnType, 
								  final String sqlCategory,
								  final String sqlName,
								  final String sqlDescription,
								  final String sqlContent,
								  final String execution,
								  final int active) throws Exception {

		Connection conn = null;
		Statement statement = null;
	
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String updateSql = "UPDATE #repodb##sqlrepo#repo_dynamic_sql "
								+ "SET database_id = #databaseId#, "
								+ "sql_type = '#sqlType#', "
								+ "sql_return_type='#sqlReturnType#', "
								+ "sql_category = '#sqlCategory#', "
								+ "sql_name = '#sqlName#', "
								+ "sql_description = '#sqlDescription#', "
								+ "sql_content = '#sqlContent#', "
								+ "execution = '#execution#', "
								+ "active = #active# "
								+ "WHERE sql_id = #sqlId# ";
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				updateSql = updateSql.replace("#sqlrepo#", "" );
				updateSql = updateSql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				updateSql = updateSql.replace("#sqlrepo#", "sqlrepo." );
				updateSql = updateSql.replace("#repodb#", "repodb." );
			}
			else { 
				updateSql = updateSql.replace("#sqlrepo#", "sqlrepo." );
				updateSql = updateSql.replace("#repodb#", "" );
			}
			
				
			updateSql = updateSql.replace("#sqlId#", String.valueOf(sqlId) );
			updateSql = updateSql.replace("#databaseId#", String.valueOf(databaseId) );
			updateSql = updateSql.replace("#sqlType#", sqlType== null ? "":sqlType);
			updateSql = updateSql.replace("#sqlReturnType#", sqlReturnType== null ? "":sqlReturnType );
			updateSql = updateSql.replace("#sqlCategory#", sqlCategory== null ? "":sqlCategory );
			updateSql = updateSql.replace("#sqlName#", sqlName== null ? "":sqlName );
			updateSql = updateSql.replace("#sqlDescription#", sqlDescription== null ? "":sqlDescription );
			updateSql = updateSql.replace("#sqlContent#", sqlContent== null ? "":sqlContent );
			updateSql = updateSql.replace("#execution#", execution== null ? "":execution );
			updateSql = updateSql.replace("#active#", String.valueOf(active) );
			
			statement.executeUpdate(updateSql);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
		
	
	public static void deleteSql(final DbConnectionInfo connectionDetailInfo,  
								 final long sqlId) throws Exception {
		if(sqlId <= 0) throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Id is null")) ;

		
		Connection conn = null;
		Statement statement = null;
	
		try {
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String deleteSql = "DELETE #repodb##sqlrepo#repo_dynamic_sql  WHERE sql_id = " + sqlId;
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				deleteSql = deleteSql.replace("#sqlrepo#", "" );
				deleteSql = deleteSql.replace("#repodb#", "" );
			} else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				deleteSql = deleteSql.replace("#sqlrepo#", "sqlrepo." );
				deleteSql = deleteSql.replace("#repodb#", "repodb." );
			} else {
				deleteSql = deleteSql.replace("#sqlrepo#", "sqlrepo." );
				deleteSql = deleteSql.replace("#repodb#", "" );
			}
			
			statement.executeUpdate(deleteSql);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	
	
	

	
	public static List<SqlRepoParam> 
	getSqlParam(final DbConnectionInfo connectionDetailInfo, 
				final String sqlParamName) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		List<SqlRepoParam> ret = new ArrayList<SqlRepoParam>();
		
		
		String getSql = "SELECT sql_param_id, "
				+ " sql_id, "
				+ " sql_param_name, "
				+ " sql_param_default_value, "
				+ " sql_param_type, "
				+ " sql_param_position, "
				+ " sql_param_order,"
				+ " sql_param_origin_tbl, "
				+ " sql_param_origin_col "
				+ " from #repodb##sqlrepo#repo_dynamic_sql_param WHERE sql_param_name = '#sqlParamName#'";
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();

			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				getSql = getSql.replace("#sqlrepo#", "" );
				getSql = getSql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				getSql = getSql.replace("#sqlrepo#", "sqlrepo." );
				getSql = getSql.replace("#repodb#", "repodb." );
			}
			else { 
				getSql = getSql.replace("#sqlrepo#", "sqlrepo." );
				getSql = getSql.replace("#repodb#", "" );
			}
			
			getSql = getSql.replace("#sqlParamName#", sqlParamName );
			
			rs = statement.executeQuery(getSql);
			while (rs.next()) {
				SqlRepoParam sqlStatement = new SqlRepoParam();
				sqlStatement.setDynamicSqlParamId(rs.getInt("sql_param_id"));
				sqlStatement.setDynamicSqlId(rs.getInt("sql_id"));
				sqlStatement.setDynamicSqlParamName(rs.getString("sql_param_name"));
				sqlStatement.setDynamicSqlParamDefault(rs.getString("sql_param_default_value"));
				sqlStatement.setDynamicSqlParamType(rs.getString("sql_param_type"));
				sqlStatement.setDynamicSqlParamPosition(rs.getString("sql_param_position"));
				sqlStatement.setDynamicSqlParamOrder(rs.getInt("sql_param_order"));
				sqlStatement.setDynamicSqlParamOriginTbl(rs.getString("sql_param_origin_tbl"));
				sqlStatement.setDynamicSqlParamOriginCol(rs.getString("sql_param_origin_col"));
				ret.add(sqlStatement);
			}
			
			return ret;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	
	public static List<SqlRepoParam> 
	getSqlParams(	final DbConnectionInfo connectionDetailInfo, 
					final long sqlId) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs;
		List<SqlRepoParam> ret = new ArrayList<SqlRepoParam>();
		
		
		String getSql = "SELECT sql_param_id, "
				+ " sql_id, "
				+ " sql_param_name, "
				+ " sql_param_default_value, "
				+ " sql_param_type, "
				+ " sql_param_position, "
				+ " sql_param_order,"
				+ " sql_param_origin_tbl, "
				+ " sql_param_origin_col "
				+ " from #repodb##sqlrepo#repo_dynamic_sql_param WHERE sql_id = #sql_id#";
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			
			
			
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				getSql = getSql.replace("#sqlrepo#", "" );
				getSql = getSql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				getSql = getSql.replace("#sqlrepo#", "sqlrepo." );
				getSql = getSql.replace("#repodb#", "repodb." );
			}
			else { 
				getSql = getSql.replace("#sqlrepo#", "sqlrepo." );
				getSql = getSql.replace("#repodb#", "" );
			}
			
			getSql = getSql.replace("#sql_id#", String.valueOf(sqlId) );
			
			rs = statement.executeQuery(getSql);
			while (rs.next()) {
				SqlRepoParam sqlStatement = new SqlRepoParam();
				sqlStatement.setDynamicSqlParamId(rs.getInt("sql_param_id"));
				sqlStatement.setDynamicSqlId(rs.getInt("sql_id"));
				sqlStatement.setDynamicSqlParamName(rs.getString("sql_param_name"));
				sqlStatement.setDynamicSqlParamDefault(rs.getString("sql_param_default_value"));
				sqlStatement.setDynamicSqlParamType(rs.getString("sql_param_type"));
				sqlStatement.setDynamicSqlParamPosition(rs.getString("sql_param_position"));
				sqlStatement.setDynamicSqlParamOrder(rs.getInt("sql_param_order"));
				sqlStatement.setDynamicSqlParamOriginTbl(rs.getString("sql_param_origin_tbl"));
				sqlStatement.setDynamicSqlParamOriginCol(rs.getString("sql_param_origin_col"));
				ret.add(sqlStatement);
			}
			
			return ret;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	public static void addSqlParam(final DbConnectionInfo connectionDetailInfo, 
							      final long sqlId,
								  final String sqlParamName,
								  final String sqlParamDefaultValue, 
								  final String sqlParamType,
								  final String sqlParamPosition,
								  final int sqlParamOrder,
								  final String sqlParamOriginTbl,
								  final String sqlParamOriginCol) throws Exception {

		Connection conn = null;
		Statement statement = null;

		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String addSqlParam = "INSERT INTO #repodb##sqlrepo#repo_dynamic_sql_param(sql_id, sql_param_name, sql_param_default_value, sql_param_type, sql_param_position, sql_param_order, sql_param_origin_tbl, sql_param_origin_col) VALUES (#sqlId#, '#sqlParamName#', '#sqlParamDefaultValue#', '#sqlParamType#', '#sqlParamPosition#', '#sqlParamOrder#', '#sqlParamOriginTbl#', '#sqlParamOriginCol#')";
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				addSqlParam = addSqlParam.replace("#sqlrepo#", "" );
				addSqlParam = addSqlParam.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				addSqlParam = addSqlParam.replace("#sqlrepo#", "sqlrepo." );
				addSqlParam = addSqlParam.replace("#repodb#", "repodb." );
			}
			else { 
				addSqlParam = addSqlParam.replace("#sqlrepo#", "sqlrepo." );
				addSqlParam = addSqlParam.replace("#repodb#", "" );
			}
			
						
						
			if(sqlId <= 0) throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Id is null")) ;
			if(sqlParamName == null) throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Param Name is null")) ;
			if(sqlParamType == null) throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Param Type Value is null")) ;
			if(sqlParamPosition == null) throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Param Position Value is null")) ;
			if(sqlParamOrder <= 0)	throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Param Order is null")) ;

						
			addSqlParam = addSqlParam.replace("#sqlId#", String.valueOf(sqlId) );
			addSqlParam = addSqlParam.replace("#sqlParamName#", sqlParamName);
			addSqlParam = addSqlParam.replace("#sqlParamDefaultValue#", sqlParamDefaultValue== null ? "":sqlParamDefaultValue);
			addSqlParam = addSqlParam.replace("#sqlParamType#", sqlParamType);
			addSqlParam = addSqlParam.replace("#sqlParamPosition#", sqlParamPosition);
			addSqlParam = addSqlParam.replace("#sqlParamOrder#", String.valueOf(sqlParamOrder) );
			addSqlParam = addSqlParam.replace("#sqlParamOriginTbl#", sqlParamOriginTbl== null ? "":sqlParamOriginTbl);
			addSqlParam = addSqlParam.replace("#sqlParamOriginCol#", sqlParamOriginCol== null ? "":sqlParamOriginCol);
			
			statement.executeUpdate(addSqlParam);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	

	public static void updateSqlParam(final DbConnectionInfo connectionDetailInfo, 
									  final int sqlParamId,
								      final int sqlId,
									  final String sqlParamName,
									  final String sqlParamDefaultValue, 
									  final String sqlParamType,
									  final String sqlParamPosition,
									  final int sqlParamOrder,
									  final String sqlParamOriginTbl,
									  final String sqlParamOriginCol) throws Exception	{

		Connection conn = null;
		Statement statement = null;

		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String updateSqlParam = "UPDATE INTO #repodb##sqlrepo#repo_dynamic_sql_param "
									+ "SET sql_id=#sqlId#, "
									+ "sql_param_name='#sqlParamName#', "
									+ "sql_param_default_value='#sqlParamDefaultValue#', "
									+ "sql_param_type='#sqlParamType#', "
									+ "sql_param_position='#sqlParamPosition#', "
									+ "sqlParamOrder='#sqlParamOrder#', "
									+ "sql_param_origin_tbl='#sqlParamOriginTbl#', "
									+ "sql_param_origin_col='#sqlParamOriginCol#' "
									+ "WHERE sql_param_id = #sqlParamId#";
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				updateSqlParam = updateSqlParam.replace("#sqlrepo#", "" );
				updateSqlParam = updateSqlParam.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				updateSqlParam = updateSqlParam.replace("#sqlrepo#", "sqlrepo." );
				updateSqlParam = updateSqlParam.replace("#repodb#", "repodb." );
			}
			else { 
				updateSqlParam = updateSqlParam.replace("#sqlrepo#", "sqlrepo." );
				updateSqlParam = updateSqlParam.replace("#repodb#", "" );
			}
			
			if(sqlId <= 0) throw new Exception("sql id is null");
			if(sqlParamName == null) throw new Exception("Sql Param Name is null");
			if(sqlParamType == null) throw new Exception("Sql Param Type Value is null");
			if(sqlParamPosition == null)	throw new Exception("Sql Param Position Value is null"); 
			if(sqlParamOrder <= 0)	throw new Exception("Sql Param Order is null");
			
			
			updateSqlParam = updateSqlParam.replace("#sqlParamId#", String.valueOf(sqlParamId) );
			updateSqlParam = updateSqlParam.replace("#sqlId#", String.valueOf(sqlId));
			updateSqlParam = updateSqlParam.replace("#sqlParamName#", sqlParamName);
			updateSqlParam = updateSqlParam.replace("#sqlParamDefaultValue#", sqlParamDefaultValue== null ? "":sqlParamDefaultValue);
			updateSqlParam = updateSqlParam.replace("#sqlParamType#", sqlParamType);
			updateSqlParam = updateSqlParam.replace("#sqlParamPosition#", sqlParamPosition);
			updateSqlParam = updateSqlParam.replace("#sqlParamOrder#", String.valueOf(sqlParamOrder) );
			updateSqlParam = updateSqlParam.replace("#sqlParamOriginTbl#", sqlParamOriginTbl== null ? "":sqlParamOriginTbl);
			updateSqlParam = updateSqlParam.replace("#sqlParamOriginCol#", sqlParamOriginCol== null ? "":sqlParamOriginCol);
			
			statement.executeUpdate(updateSqlParam);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	
	

	public static void deleteSqlParam(	final DbConnectionInfo connectionDetailInfo,  
										final long sqlId, 
										final long sqlParamId) throws Exception {

		Connection conn = null;
		Statement statement = null;
	
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String deleteSqlParam = "DELETE #repodb##sqlrepo#repo_dynamic_sql_param  WHERE sql_id = #sqlId# AND sql_param_id = #sqlParamId#";
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				deleteSqlParam = deleteSqlParam.replace("#sqlrepo#", "" );
				deleteSqlParam = deleteSqlParam.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				deleteSqlParam = deleteSqlParam.replace("#sqlrepo#", "sqlrepo." );
				deleteSqlParam = deleteSqlParam.replace("#repodb#", "repodb." );
			}
			else { 
				deleteSqlParam = deleteSqlParam.replace("#sqlrepo#", "sqlrepo." );
				deleteSqlParam = deleteSqlParam.replace("#repodb#", "" );
			}
		
			if(sqlId <= 0)	throw new Exception("Sql Id is null");
			if(sqlParamId <= 0)	throw new Exception("Sql Param Id is null");
			
			deleteSqlParam = deleteSqlParam.replace("#sqlId#", String.valueOf(sqlId) );
			deleteSqlParam = deleteSqlParam.replace("#sqlParamId#", String.valueOf(sqlParamId) );	
			
			statement.executeUpdate(deleteSqlParam);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	
	

	public static void deleteAllSqlParam(	final DbConnectionInfo connectionDetailInfo,  
											final long sqlId) throws Exception {

		Connection conn = null;
		Statement statement = null;
	
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			String deleteSqlParam = "DELETE #repodb##sqlrepo#repo_dynamic_sql  WHERE sql_id = #sqlId# ";
			
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				deleteSqlParam = deleteSqlParam.replace("#sqlrepo#", "" );
				deleteSqlParam = deleteSqlParam.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				deleteSqlParam = deleteSqlParam.replace("#sqlrepo#", "sqlrepo." );
				deleteSqlParam = deleteSqlParam.replace("#repodb#", "repodb." );
			}
			else { 
				deleteSqlParam = deleteSqlParam.replace("#sqlrepo#", "sqlrepo." );
				deleteSqlParam = deleteSqlParam.replace("#repodb#", "" );
			}
			
			if(sqlId <= 0)	throw new Exception("Sql Id is null");
			
			deleteSqlParam = deleteSqlParam.replace("#sqlId#", String.valueOf(sqlId));
			statement.executeUpdate(deleteSqlParam);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, null);
		}
	}
	

	/**                            SQL To Db Bridge             	 */

	public static void 
	mergeSqlToDbBridge(	final DbConnectionInfo connectionDetailInfo, 
						final long sql_id,
						final int database_id,
						final int active) throws Exception {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try	{
			
			String sql = "MERGE INTO  #repodb##sqlrepo#sql_to_database_bridge(sql_id, database_id, active) "
								+ "	KEY (sql_id, database_id) "
								+ " VALUES (?, ?, ?)";
			
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sql = sql.replace("#sqlrepo#", "" );
				sql = sql.replace("#repodb#", "" );
			} else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "repodb." );
			} else {
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "" );
			}
			
			
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setLong(1, sql_id);
			preparedStatement.setInt(2, database_id);
			preparedStatement.setInt(3, active);
			preparedStatement.execute();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, preparedStatement, null);
		}
	}
	
	
	public static SqlStmToDbBridgeList 
	getSqlToDbBridge(	final DbConnectionInfo connectionDetailInfo, 
						final long sql_id,
						final int database_id) throws Exception {

		Connection conn = null;
		ResultSet rs = null;
		Statement statement = null;
		String sql = "SELECT b.id, b.sql_id, b.database_id, d.database_name, b.active FROM  #repodb##sqlrepo#sql_to_database_bridge b JOIN repo_database d ON b.database_id = d.database_id WHERE 1=1 ";
		SqlStmToDbBridgeList sqlToDbBridgeList = new SqlStmToDbBridgeList();
		try	{
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sql = sql.replace("#sqlrepo#", "" );
				sql = sql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "repodb." );
			}
			else { 
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "" );
			}
			
			
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			
			if(sql_id > 0 ) { sql += " AND b.sql_id = " + sql_id; }
			if(database_id > 0 ) { sql += " AND b.database_id = " + database_id; }
			statement = conn.createStatement();;
			rs = statement.executeQuery(sql);
			while ( rs.next() ) {
				SqlStmToDbBridge sqlToDbBridge = new SqlStmToDbBridge(rs.getLong("id"), 
																rs.getLong("sql_id"), 
																rs.getLong("database_id"),
																rs.getString("database_name"), 
																rs.getInt("active")
			       			                        			);
				sqlToDbBridgeList.addSqlRepoSchema(sqlToDbBridge);
			}
			return sqlToDbBridgeList;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	public static void 
	populateSqlToDbBridge(final DbConnectionInfo connectionDetailInfo) throws Exception {

		Connection conn = null;
		ResultSet rs = null;
		Statement statement = null;
		String sql = "SELECT id, sql_id, database_id, active FROM #repodb##sqlrepo#sql_to_database_bridge";

		try	{
			
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sql = sql.replace("#sqlrepo#", "" );
				sql = sql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "repodb." );
			}
			else { 
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "" );
			}
			
			
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			
			rs = statement.executeQuery(sql);
			while ( rs.next() ) {
				long id = rs.getLong("id");
				long sqlId = (long) rs.getInt("sql_id");
				long dbId = (long) rs.getInt("database_id");
				int active = rs.getInt("active");
				
				if(SqlRepoUtils.sqlRepoBridgeMap.containsKey(sqlId)) {
					SqlRepoUtils.sqlRepoBridgeMap.get(sqlId).add(dbId);
				} else {
					SqlRepoUtils.sqlRepoBridgeMap.put(sqlId, new ArrayList<Long>());
				}
				
				List<SqlRepoDatabase> sqlRepoDatabaseList = SqlRepoUtils.sqlRepoDatabaseMap.values().stream().filter(element -> element.getDatabaseId() == dbId).collect(Collectors.toList());
				if(sqlRepoDatabaseList.size() == 1) {
					SqlRepoDatabaseSchemaBridge sqlRepoDatabaseSchemaBridge = new SqlRepoDatabaseSchemaBridge(id, sqlId, dbId, sqlRepoDatabaseList.get(0).getDatabaseName(), active);
					SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId).getSqlRepoDatabaseSchemaBridgeList().add(sqlRepoDatabaseSchemaBridge);
				}
				
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, statement, rs);
		}
	}
	
	
	public static void 
	deleteSqlToDbBridge(final DbConnectionInfo connectionDetailInfo, 
						final long sql_id,
						final int database_id
						) throws Exception {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE  #repodb##sqlrepo#sql_to_database_bridge WHERE sql_id = ? AND database_id = ?";
		
		try	{
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sql = sql.replace("#sqlrepo#", "" );
				sql = sql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "repodb." );
			}
			else { 
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "" );
			}
			
			
			
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setLong(1, sql_id);
			preparedStatement.setInt(2, database_id);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, preparedStatement, null);
		}
	}
	
	public static void 
	deleteSqlToDbBridge(final DbConnectionInfo connectionDetailInfo, 
						final long sql_id) throws Exception {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE  #repodb##sqlrepo#sql_to_database_bridge WHERE sql_id = ?";
		try	{
			if(connectionDetailInfo.getDbType().compareTo(DbUtil.h2) == 0) {
				sql = sql.replace("#sqlrepo#", "" );
				sql = sql.replace("#repodb#", "" );
			}
			else if(connectionDetailInfo.getDbType().compareTo(DbUtil.snowflake) == 0) {
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "repodb." );
			}
			else { 
				sql = sql.replace("#sqlrepo#", "sqlrepo." );
				sql = sql.replace("#repodb#", "" );
			}
			
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setLong(1, sql_id);
			preparedStatement.execute();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, preparedStatement, null);
		}
	}
}
	
	