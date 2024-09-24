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

public class SqlSchema {
	
	public java.util.List<String> execution;

	SqlSchema()	{
		execution = new java.util.ArrayList<String>();

		String repo_database = "CREATE TABLE repo_database\r\n"
				+ "(\r\n"
				+ "	database_id SERIAL PRIMARY KEY,\r\n"
				+ "	database_type character varying(99),\r\n"
				+ "	database_name character varying(99),\r\n"
				+ "	database_warehouse_name character varying(99),\r\n"
				+ "	database_server character varying(99),\r\n"
				+ "	database_port character varying(99),\r\n"
				+ "	database_description character varying(999),\r\n"
				+ "	database_active boolean NOT NULL DEFAULT TRUE\r\n"
				+ ")";
		execution.add(repo_database);
		String repo_database_index1 = "CREATE INDEX idx_repo_database_1 ON repo_database(database_name)";
		execution.add(repo_database_index1);
		String repo_database_const1 = "ALTER TABLE repo_database ADD CONSTRAINT ck_repo_database_1 CHECK (database_type IN ('ORACLE', 'POSTGRESQL', 'SQLSERVER', 'SYBASE', 'MONGODB', 'CASSANDRA', 'MARIADB', 'MYSQL', 'DB2', 'REDIS', 'ELASTICSEARCH', 'SPLUNK', 'TERADATA', 'HIVE', 'SOLR', 'HBASE', 'NEO4J' ,'SNOWFLAKE','GENERIC') );";
		execution.add(repo_database_const1);
		
	}
	
}
