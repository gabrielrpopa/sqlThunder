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


package com.widescope.rdbmsRepo.database.SqlRepository.Objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;



public abstract class AllowedDatabase {
	Set<String> database;
	final String[] DBTYPES = "MARIADB,POSTGRESQL,MYSQL,ORACLE,SQLSERVER,NOWFLAKE,H2".split(",");
	
	public static final String mariadb = "MARIADB";
	public static final String postgresql = "POSTGRESQL";
	public static final String mysql = "MYSQL";
	public static final String oracle = "ORACLE";
	public static final String sqlserver = "SQLSERVER";
	public static final String snowflake = "SNOWFLAKE";
	public static final String h2 = "H2";

	public Set<String> getDatabaseList() { 
		database = new HashSet<String>(Arrays.asList(DBTYPES));
		return database; 
	}
	
	
}
