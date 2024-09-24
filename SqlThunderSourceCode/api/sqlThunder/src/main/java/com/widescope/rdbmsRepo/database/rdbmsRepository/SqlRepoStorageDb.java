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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.List;

import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;





@Component
public class SqlRepoStorageDb {

	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "org.h2.Driver";
	private String DB_URL_DISK = "jdbc:h2:file:./@dbname@;MODE=PostgreSQL";
	// conn1: url=jdbc:h2:file:./h2-sqlRepo user=SA

	//  Database credentials
	private final String USER = "sa";
	private final String PASS = "sa";

	public SqlRepoStorageDb() {	}


	public SqlRepoStorageDb(String dbName) 	{
		this.DB_URL_DISK = this.DB_URL_DISK.replaceFirst("@dbname@", dbName);
	}



	private void closeHandles(Connection conn,
							  Statement statement,
							  ResultSet rs)	{
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}




	public void createSchema(final List<String> ddlList) throws Exception {
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
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}  finally	{
			closeHandles(conn, statement, null);
		}

	}







	public static void generateSchema(final String dbName) throws Exception {


		try {
			List<String> ddlList = new java.util.ArrayList<String>();
			ddlList.add(SqlRepoStorageDb.repo_db_types);
			ddlList.add(SqlRepoStorageDb.repo_db_types_const1);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert1);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert2);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert3);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert4);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert5);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert6);
			ddlList.add(SqlRepoStorageDb.repo_db_types_insert7);

			ddlList.add(SqlRepoStorageDb.repo_database);
			ddlList.add(SqlRepoStorageDb.repo_database_index1);
			ddlList.add(SqlRepoStorageDb.repo_database_index2);

			ddlList.add(SqlRepoStorageDb.repo_database_const1);

			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_const1);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_const2);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_const3);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_index2);


			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_param);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_param_fk1);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_param_const1);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_param_const2);
			ddlList.add(SqlRepoStorageDb.repo_dynamic_sql_param_index1);

			ddlList.add(SqlRepoStorageDb.sql_to_database_bridge_Table);
			ddlList.add(SqlRepoStorageDb.sql_to_database_bridge_index1);
			ddlList.add(SqlRepoStorageDb.sql_to_database_bridge_index2);
			ddlList.add(SqlRepoStorageDb.sql_to_database_bridge_index3);
			ddlList.add(SqlRepoStorageDb.sql_to_database_bridge_fk1);
			ddlList.add(SqlRepoStorageDb.sql_to_database_bridge_fk2);



			ddlList.add(SqlRepoStorageDb.repo_sql_execution);


			ddlList.add(SqlRepoStorageDb.db2);
			//ddlList.add(SqlRepoStorageDb.db3);
			ddlList.add(SqlRepoStorageDb.sql1);
			ddlList.add(SqlRepoStorageDb.sql2);
			ddlList.add(SqlRepoStorageDb.param1);
			ddlList.add(SqlRepoStorageDb.param2);
			ddlList.add(SqlRepoStorageDb.param3);




			SqlRepoStorageDb sqlRepoStorageDb = new SqlRepoStorageDb(dbName);
			sqlRepoStorageDb.createSchema(ddlList);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}



	}


	public static String repo_db_types = "CREATE TABLE IF NOT EXISTS repo_db_types (\r\n"
			+ "	db_type_id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	database_type VARCHAR(99) )";

	public static String repo_db_types_const1 = "ALTER TABLE repo_db_types ADD CONSTRAINT IF NOT EXISTS "
			+ "ck_repo_db_types_1 CHECK (database_type IN ('ORACLE', 'POSTGRESQL', 'SQLSERVER', 'MARIADB', 'MYSQL', 'SNOWFLAKE', 'H2') );";

	public static String repo_db_types_insert1 = "INSERT INTO repo_db_types (database_type) VALUES('ORACLE')";
	public static String repo_db_types_insert2 = "INSERT INTO repo_db_types (database_type) VALUES('POSTGRESQL')";
	public static String repo_db_types_insert3 = "INSERT INTO repo_db_types (database_type) VALUES('SQLSERVER')";
	public static String repo_db_types_insert4 = "INSERT INTO repo_db_types (database_type) VALUES('MARIADB')";
	public static String repo_db_types_insert5 = "INSERT INTO repo_db_types (database_type) VALUES('MYSQL')";
	public static String repo_db_types_insert6 = "INSERT INTO repo_db_types (database_type) VALUES('SNOWFLAKE')";
	public static String repo_db_types_insert7 = "INSERT INTO repo_db_types (database_type) VALUES('H2')";


	public static String repo_database = "CREATE TABLE IF NOT EXISTS repo_database (\r\n"
			+ "	database_id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	database_type VARCHAR(99),\r\n"
			+ "	database_name VARCHAR(99),\r\n"
			+ "	database_warehouse_name VARCHAR(99) ,\r\n"
			+ "	database_server VARCHAR(99),\r\n"
			+ "	database_port VARCHAR(99),\r\n"
			+ "	database_description VARCHAR(999),\r\n"
			+ "	schema_name VARCHAR(99) ,\r\n"
			+ "	schema_service VARCHAR(99) ,\r\n"
			+ "	schema_password VARCHAR(999) ,\r\n"
			+ "	schema_unique_user_name VARCHAR(999),\r\n"
			+ "	account VARCHAR(999),\r\n"
			+ "	other VARCHAR(999),\r\n"
			+ "	tunnel_local_port VARCHAR(99),\r\n"
			+ "	tunnel_remote_host_address VARCHAR(999),\r\n"
			+ "	tunnel_remote_host_port VARCHAR(99),\r\n"
			+ "	tunnel_remote_host_user VARCHAR(999),\r\n"
			+ "	tunnel_remote_host_user_password VARCHAR(999),\r\n"
			+ "	tunnel_remote_host_rsa_Key VARCHAR(999),\r\n"
			+ "	active int DEFAULT 1)";




	public static String repo_database_index1 = "CREATE INDEX IF NOT EXISTS idx_repo_database_1 ON repo_database(database_name, schema_name)";
	public static String repo_database_index2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repo_database_2 ON repo_database(schema_unique_user_name)";
	public static String repo_database_const1 = "ALTER TABLE repo_database ADD CONSTRAINT IF NOT EXISTS ck_repo_database_1 "
			+ "CHECK (database_type IN ('ORACLE', 'POSTGRESQL', 'SQLSERVER', 'MARIADB', 'MYSQL', 'SNOWFLAKE', 'H2') );";

	public static String schema_unique_user_name = "localhostDbServer";


	public static String db2 = "INSERT INTO repo_database (database_type, "
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
														+ "active"
														+ ") "
														+ "VALUES('POSTGRESQL', "
														+ "'testdb', "
														+ "'localhost', "
														+ "'5432', "
														+ "'localhost PostgreSql Db Server', "
														+ "'', "
														+ "'postgres', "
														+ "'postgres', "
														+ "'postgres', "
														+ "'" + schema_unique_user_name + "', "
														+ "'', "
														+ "'', "
														+ "1)";


	public static String db3 = "INSERT INTO repo_database (database_type, "
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
			+ "active"
			+ ") "
			+ "VALUES('POSTGRESQL', "
			+ "'sqltdb1', "
			+ "'SQLT-DB1', "
			+ "'5432', "
			+ "'Chat Database Server', "
			+ "'', "
			+ "'postgres', "
			+ "'postgres', "
			+ "'postgres', "
			+ "'dbServer-postgres-postgres', "
			+ "'', "
			+ "'', "
			+ "1)";




	public static String repo_dynamic_sql = "CREATE TABLE IF NOT EXISTS repo_dynamic_sql (\r\n"
			+ "	sql_id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	sql_type VARCHAR(99) DEFAULT 'QUERY',\r\n"
			+ "	sql_return_type VARCHAR(99) DEFAULT 'RECORDSET',\r\n"
			+ "	sql_category VARCHAR(99) ,\r\n"
			+ "	sql_name VARCHAR(99),\r\n"
			+ "	sql_description VARCHAR(999),\r\n"
			+ "	sql_content CLOB,\r\n"
			+ "	execution VARCHAR(2),\r\n"
			+ "	active int DEFAULT 1)";



	public static String repo_dynamic_sql_const1 = "ALTER TABLE repo_dynamic_sql ADD CONSTRAINT IF NOT EXISTS ck_repo_dynamic_sql_1 "
			+ "CHECK (sql_type IN ('DQL', 'DML', 'DDL', 'FUNCTION', 'COMMAND'"
			+ "'PROCEDURE', 'CREATETABLE', 'DROPTABLE', 'TRUNCATE', 'MERGE', 'ALTERSESSION', 'EXPLAIN') );";

	public static String repo_dynamic_sql_const2 = "ALTER TABLE repo_dynamic_sql ADD CONSTRAINT IF NOT EXISTS ck_repo_dynamic_sql_2 "
			+ "CHECK (sql_return_type IN ('RECORDSET', 'VOID', 'STRING', 'FLOAT', 'INTEGER', 'BOOL', 'DATE', "
			+ "'DATETIME', 'BYTE', 'ARRAY', 'LOB') );";

	public static String repo_dynamic_sql_const3 = "ALTER TABLE repo_dynamic_sql ADD CONSTRAINT IF NOT EXISTS ck_repo_dynamic_sql_3 "
			+ "CHECK (execution IN ('DN', 'ST') );";


	public static String repo_dynamic_sql_index2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repo_dynamic_sql_2 ON repo_dynamic_sql(sql_name);";


	public static String sql1 = "INSERT INTO repo_dynamic_sql(sql_type, sql_return_type, sql_category, sql_name, sql_description, sql_content, execution, active) "
			+ "VALUES ('DQL', 'RECORDSET', 'N/A', 'SqlFirst', 'What a description', 'SELECT * from \"postgres\".testschema.\"employee\" WHERE age = @age@ ', 'DN', 1)";

	public static String sql2 = "INSERT INTO repo_dynamic_sql(sql_type, sql_return_type, sql_category, sql_name, sql_description, sql_content, execution, active) "
			+ "VALUES ('DQL', 'RECORDSET', 'N/A', 'SqlSecond', 'What a description', 'SELECT * from \"postgres\".testschema.\"employee\" WHERE age > @age@ AND name like @like@ ', 'DN', 1)";

	public static String repo_dynamic_sql_param = "CREATE TABLE IF NOT EXISTS repo_dynamic_sql_param (\r\n"
			+ "	sql_param_id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	sql_id BIGINT,\r\n"
			+ "	sql_param_name VARCHAR(99),\r\n"
			+ "	sql_param_default_value VARCHAR(999) ,\r\n"
			+ "	sql_param_type VARCHAR(20) DEFAULT 'STRING',\r\n"
			+ "	sql_param_position VARCHAR(20),\r\n"
			+ "	sql_param_order INT,\r\n"
			+ "	sql_param_origin_tbl VARCHAR(999),\r\n"
			+ "	sql_param_origin_col VARCHAR(999) )";

	public static String repo_dynamic_sql_param_fk1 = "ALTER TABLE repo_dynamic_sql_param ADD CONSTRAINT IF NOT EXISTS fk_2 FOREIGN KEY ( sql_id ) REFERENCES repo_dynamic_sql( sql_id );";

	public static String repo_dynamic_sql_param_const1 = "ALTER TABLE repo_dynamic_sql_param ADD CONSTRAINT IF NOT EXISTS ck_repo_dynamic_sql_param_1 "
			+ "CHECK (sql_param_type IN ('STRING', 'FLOAT', 'DECIMAL', 'DOUBLE', 'REAL', 'INTEGER', 'BIGINT', 'SMALLINT', 'BOOL', 'DATE', 'TIMESTAMP', 'LOB', 'NAMESTRING') );";
	public static String repo_dynamic_sql_param_const2 = "ALTER TABLE repo_dynamic_sql_param ADD CONSTRAINT IF NOT EXISTS ck_repo_dynamic_sql_param_2 "
			+ "CHECK (sql_param_position IN ('OUTPUT', 'INPUT') );";
	public static String repo_dynamic_sql_param_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repo_dynamic_sql_param_1 ON repo_dynamic_sql_param(sql_id, sql_param_name);";


	public static String param1 = "INSERT INTO repo_dynamic_sql_param(sql_id, sql_param_name, sql_param_default_value, sql_param_type, sql_param_position, sql_param_order, sql_param_origin_tbl, sql_param_origin_col ) "
			+ "VALUES (1, '@age@', '0', 'INTEGER', 'INPUT', '1', '', '')";



	public static String param2 = "INSERT INTO repo_dynamic_sql_param(sql_id, sql_param_name, sql_param_default_value, sql_param_type, sql_param_position, sql_param_order, sql_param_origin_tbl, sql_param_origin_col ) "
			+ "VALUES (2, '@age@', '0', 'INTEGER', 'INPUT', '1', '', '')";



	public static String param3 = "INSERT INTO repo_dynamic_sql_param(sql_id, sql_param_name, sql_param_default_value, sql_param_type, sql_param_position, sql_param_order, sql_param_origin_tbl, sql_param_origin_col ) "
			+ "VALUES (2, '@like@', '%1%', 'STRING', 'INPUT', '2', '', '')";

	/*SQL Compatibility to a certain database*/
	public static String repo_sql_compat = "CREATE TABLE IF NOT EXISTS repo_sql_compat (\r\n"
			+ "	compat_id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	sql_id BIGINT,\r\n"
			+ "	database_id BIGINT,\r\n"
			+ "	is_compat VARCHAR(999) )";
	public static String repo_sql_compat_fk1 = "ALTER TABLE repo_sql_compat ADD CONSTRAINT IF NOT EXISTS fk_3 FOREIGN KEY  ( database_id ) REFERENCES repo_database( database_id );";
	public static String repo_sql_compat_fk2 = "ALTER TABLE repo_sql_compat ADD CONSTRAINT IF NOT EXISTS fk_4 FOREIGN KEY  ( sql_id ) REFERENCES repo_dynamic_sql( sql_id );";
	public static String repo_sql_compat_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_repo_sql_compat_1 ON repo_sql_compat(database_id, sql_id);";


	public static String repo_sql_compat_sql1 = "INSERT INTO repo_sql_compat(database_id, database_id, is_compat) VALUES (1, 1, 1)";
	public static String repo_sql_compat_sql2 = "INSERT INTO repo_sql_compat(database_id, database_id, is_compat) VALUES (2, 1, 1)";



	public static String repo_sql_execution = "CREATE TABLE IF NOT EXISTS repo_sql_execution (\r\n"
			+ "	execution_id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	user_id BIGINT,\r\n"
			+ "	sql_id BIGINT,\r\n"
			+ "	time_stamp INT )";


	public static String
			sql_to_database_bridge_Table = "CREATE TABLE IF NOT EXISTS sql_to_database_bridge (\r\n"
			+ "	id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,\r\n"
			+ "	sql_id BIGINT,\r\n"
			+ "	database_id BIGINT,\r\n"
			+ "	active int DEFAULT 1)";
	public static String
			sql_to_database_bridge_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_sql_to_database_bridge_1 ON sql_to_database_bridge(sql_id, database_id);";


	public static String
			sql_to_database_bridge_index2 = "CREATE INDEX IF NOT EXISTS idx_sql_to_database_bridge_2 ON sql_to_database_bridge(sql_id) ;";

	public static String
			sql_to_database_bridge_index3 = "CREATE INDEX IF NOT EXISTS idx_sql_to_database_bridge_3 ON sql_to_database_bridge(database_id) ;";

	public static String
			sql_to_database_bridge_fk1 = "ALTER TABLE sql_to_database_bridge ADD CONSTRAINT IF NOT EXISTS sql_to_database_bridge_fk_1 FOREIGN KEY ( sql_id ) REFERENCES repo_dynamic_sql( sql_id );";

	public static String
			sql_to_database_bridge_fk2 = "ALTER TABLE sql_to_database_bridge ADD CONSTRAINT IF NOT EXISTS sql_to_database_bridge_fk_2 FOREIGN KEY ( database_id ) REFERENCES repo_database( database_id );";



}

