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


/**
 * Types: 'QUERY', 'INSERT', 'UPDATE', 'DELETE', 'FUNCTION', 'PROCEDURE', 'CREATETABLE', 'DROPTABLE', 'TRUNCATE', 'MERGE', 'ALTERSESSION'
 */

public class SqlStmType {
	public static final String querySql = "QUERY";
	public static final String dql = "DQL";
	public static final String insertSql = "INSERT";
	public static final String updateSql = "UPDATE";
	public static final String deleteSql = "DELETE";
	public static final String dml = "DML";
	public static final String ddl = "DDL";
	public static final String execute = "EXECUTE";
	public static final String function = "FUNCTION";
	public static final String procedure = "PROCEDURE";
	public static final String createtable = "CREATETABLE";
	public static final String droptable = "DROPTABLE";
	public static final String truncate = "TRUNCATE";
	public static final String merge = "MERGE";
	public static final String altersession = "ALTERSESSION";
	public static final String explain = "EXPLAIN";
}
