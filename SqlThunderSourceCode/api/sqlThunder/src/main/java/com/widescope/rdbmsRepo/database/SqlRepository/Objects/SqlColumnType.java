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


public class SqlColumnType {
	/*QUOTED COLUMNS*/
	public static final String stringColumn = "STRING"; // java.sql.Types.NVARCHAR,  java.sql.Types.VARCHAR
	public static final String booleanColumn = "BOOL"; // java.sql.Types.BOOLEAN
	public static final String dateColumn = "DATE"; // java.sql.Types.DATE
	public static final String timestampColumn = "TIMESTAMP"; // java.sql.Types.TIMESTAMP
	public static final String lobColumn = "LOB";  // java.sql.Types.NCLOB
	
	/*UNQUOTED COLUMNS*/
	public static final String floatColumn = "FLOAT";  // java.sql.Types.FLOAT
	public static final String doubleColumn = "DOUBLE"; // java.sql.Types.DOUBLE
	public static final String decimalColumn = "DECIMAL"; // java.sql.Types.DECIMAL
	public static final String realColumn = "REAL"; // columnType == java.sql.Types.REAL
	public static final String intColumn = "INTEGER"; // java.sql.Types.INTEGER 
	public static final String bigintColumn = "BIGINT"; // columnType == java.sql.Types.BIGINT 
	public static final String smallintColumn = "SMALLINT"; //columnType == java.sql.Types.SMALLINT
	
	public static final String nameStringColumn = "NAMESTRING"; //special case to create database object names such as CREATE TABLE PRODUCTSxxxx
	
}


