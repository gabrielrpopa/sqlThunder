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

package com.widescope.rdbmsRepo.database.types;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ColumnTypeTable {
	
	public final static int BIT             		= java.sql.Types.BIT;
    public final static int TINYINT         		= java.sql.Types.TINYINT;
    public final static int SMALLINT        		= java.sql.Types.SMALLINT;
    public final static int INTEGER         		= java.sql.Types.INTEGER;
    public final static int BIGINT          		= java.sql.Types.BIGINT;
    public final static int FLOAT           		= java.sql.Types.FLOAT;
    public final static int REAL           			= java.sql.Types.REAL;
    public final static int DOUBLE          		= java.sql.Types.DOUBLE;
    public final static int NUMERIC         		= java.sql.Types.NUMERIC;
    public final static int DECIMAL         		= java.sql.Types.DECIMAL;
    public final static int CHAR            		= java.sql.Types.CHAR;
    public final static int VARCHAR         		= java.sql.Types.VARCHAR;
	public final static int LONGVARCHAR     		= java.sql.Types.LONGVARCHAR;
	public final static int DATE            		= java.sql.Types.DATE;
	public final static int TIME            		= java.sql.Types.TIME;
	public final static int TIMESTAMP       		= java.sql.Types.TIMESTAMP;
    public final static int BINARY          		= java.sql.Types.BINARY;
    public final static int VARBINARY       		= java.sql.Types.VARBINARY;
    public final static int LONGVARBINARY   		= java.sql.Types.LONGVARBINARY;
    public final static int NULL            		= java.sql.Types.NULL;
    public final static int OTHER           		= java.sql.Types.OTHER;
    public final static int JAVA_OBJECT     		= java.sql.Types.JAVA_OBJECT;
    public final static int DISTINCT            	= java.sql.Types.DISTINCT;
    public final static int STRUCT              	= java.sql.Types.STRUCT;
    public final static int ARRAY               	= java.sql.Types.ARRAY;
    public final static int BLOB                	= java.sql.Types.BLOB;
    public final static int CLOB                	= java.sql.Types.CLOB;
    public final static int REF                 	= java.sql.Types.REF;
	public final static int DATALINK 				= java.sql.Types.DATALINK;
	public final static int BOOLEAN 				= java.sql.Types.BOOLEAN;
	public final static int ROWID 					= java.sql.Types.ROWID;
	public static final int NCHAR 					= java.sql.Types.NCHAR;
	public static final int NVARCHAR				= java.sql.Types.NVARCHAR;
	public static final int LONGNVARCHAR 			= java.sql.Types.LONGNVARCHAR;
	public static final int NCLOB 					= java.sql.Types.NCLOB;
	public static final int SQLXML 					= java.sql.Types.SQLXML;
	public static final int REF_CURSOR 				= java.sql.Types.REF_CURSOR;
	public static final int TIME_WITH_TIMEZONE 		= java.sql.Types.TIME_WITH_TIMEZONE;
	public static final int TIMESTAMP_WITH_TIMEZONE = java.sql.Types.TIMESTAMP_WITH_TIMEZONE;
	
	
	public Map<Integer, String> columnIdToName = new HashMap<Integer, String>();
	
	public Map<String, Map<String, String> > columnIdToNameMapping = new HashMap<String, Map<String, String>>(); // <javaColumnsName <DbSystem, tabelColumn> >
	
	public Set<String> columnQuote = new HashSet<String>(); // columns types requiring quotes 
	public Set<String> columnNoQuote = new HashSet<String>(); // columns types requiring no quotes
	
	
	
	public ColumnTypeTable()	{
		columnIdToName.put(java.sql.Types.BIT, "BIT");
		
		columnIdToName.put(java.sql.Types.TINYINT, "TINYINT");
		columnIdToName.put(java.sql.Types.SMALLINT, "SMALLINT");
		columnIdToName.put(java.sql.Types.INTEGER, "INTEGER");
		columnIdToName.put(java.sql.Types.BIGINT, "BIGINT");
		String[] array1 = {"TINYINT", "SMALLINT", "INTEGER", "BIGINT"};
		columnNoQuote.addAll(Arrays.asList(array1));
		
		columnIdToName.put(java.sql.Types.FLOAT, "FLOAT");
		columnIdToName.put(java.sql.Types.REAL, "REAL");
		columnIdToName.put(java.sql.Types.DOUBLE, "DOUBLE");
		columnIdToName.put(java.sql.Types.NUMERIC, "NUMERIC");
		columnIdToName.put(java.sql.Types.DECIMAL, "DECIMAL");
		String[] array2 = {"TINYINT", "SMALLINT", "INTEGER", "BIGINT"};
		columnNoQuote.addAll(Arrays.asList(array2));
		
		columnIdToName.put(java.sql.Types.CHAR, "CHAR");
		columnIdToName.put(java.sql.Types.VARCHAR, "VARCHAR");
		columnIdToName.put(java.sql.Types.NCHAR, "NCHAR");
		columnIdToName.put(java.sql.Types.NVARCHAR, "NVARCHAR");
		String[] array3 = {"CHAR", "VARCHAR", "NCHAR", "NVARCHAR"};
		columnQuote.addAll(Arrays.asList(array3));
		
		columnIdToName.put(java.sql.Types.DATE, "DATE");
		columnIdToName.put(java.sql.Types.TIME, "TIME");
		columnIdToName.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
		columnIdToName.put(java.sql.Types.TIME_WITH_TIMEZONE, "TIME_WITH_TIMEZONE");
		columnIdToName.put(java.sql.Types.TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE");
		String[] array4 = {"DATE", "TIME", "TIMESTAMP", "TIME_WITH_TIMEZONE", "TIME_WITH_TIMEZONE"};
		columnQuote.addAll(Arrays.asList(array4));
		
		
		columnIdToName.put(java.sql.Types.BINARY, "BINARY");
		columnIdToName.put(java.sql.Types.VARBINARY, "VARBINARY");
		columnIdToName.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
		String[] array5 = {"BINARY", "VARBINARY", "LONGVARBINARY"};
		columnQuote.addAll(Arrays.asList(array5));
		
		columnIdToName.put(java.sql.Types.NULL, "NULL");
		columnIdToName.put(java.sql.Types.OTHER, "OTHER");
		String[] array6 = {"NULL", "OTHER"};
		columnNoQuote.addAll(Arrays.asList(array6));
		
		
		columnIdToName.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
		columnIdToName.put(java.sql.Types.DISTINCT, "DISTINCT");
		columnIdToName.put(java.sql.Types.STRUCT, "STRUCT");
		columnIdToName.put(java.sql.Types.ARRAY, "ARRAY");
		String[] array7 = {"JAVA_OBJECT", "DISTINCT", "STRUCT", "ARRAY"};
		columnNoQuote.addAll(Arrays.asList(array7));
		
		
		columnIdToName.put(java.sql.Types.BLOB, "BLOB");
		columnIdToName.put(java.sql.Types.CLOB, "CLOB");
		String[] array8 = {"BLOB", "CLOB"};
		columnQuote.addAll(Arrays.asList(array8));
		
		columnIdToName.put(java.sql.Types.BOOLEAN, "BOOLEAN");
		columnNoQuote.add("BOOLEAN");
		
		columnIdToName.put(java.sql.Types.REF, "REF");
		columnQuote.add("REF");
		columnIdToName.put(java.sql.Types.DATALINK, "DATALINK");
		columnQuote.add("DATALINK");
		columnIdToName.put(java.sql.Types.ROWID, "ROWID");
		columnQuote.add("ROWID");
		
		columnIdToName.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
		columnQuote.add("LONGNVARCHAR");
		columnIdToName.put(java.sql.Types.NCLOB, "NCLOB");
		columnQuote.add("NCLOB");
		columnIdToName.put(java.sql.Types.SQLXML, "SQLXML");
		columnQuote.add("SQLXML");
		columnIdToName.put(java.sql.Types.REF_CURSOR, "REF_CURSOR");
		columnQuote.add("REF_CURSOR");
		
		
		columnIdToName.put(Integer.valueOf(6000), "JSON");
		columnQuote.add("JSON");
		
		
		Map<String, String> mariadbMapping = new HashMap<String, String>();
		mariadbMapping.put("BIT", "BIT(precision)");
		mariadbMapping.put("TINYINT", "INTEGER");
		mariadbMapping.put("SMALLINT", "INTEGER");
		mariadbMapping.put("INTEGER", "INTEGER");
		mariadbMapping.put("BIGINT", "INTEGER");
		
		mariadbMapping.put("FLOAT", "NUMERIC(precision, scale)");
		mariadbMapping.put("REAL", "NUMERIC(precision, scale)");
		mariadbMapping.put("DOUBLE", "NUMERIC(precision, scale)");
		mariadbMapping.put("NUMERIC", "NUMERIC(precision, scale)");
		mariadbMapping.put("DECIMAL", "NUMERIC(precision, scale)");
		
		mariadbMapping.put("CHAR", "VARCHAR(precision)");
		mariadbMapping.put("VARCHAR", "VARCHAR(precision)");
		mariadbMapping.put("NCHAR", "NVARCHAR(precision)");
		mariadbMapping.put("NVARCHAR", "NVARCHAR(precision)");
		
		mariadbMapping.put("DATE", "DATE");
		mariadbMapping.put("TIME", "TIME(3)");
		mariadbMapping.put("TIMESTAMP", "TIMESTAMP(3)");
		
		columnIdToNameMapping.put("MARIADB", mariadbMapping);
		
		
		Map<String, String> postgresMapping = new HashMap<String, String>();
		postgresMapping.put("BIT", "BIT VARYING(precision)");
		postgresMapping.put("TINYINT", "INTEGER");
		postgresMapping.put("SMALLINT", "INTEGER");
		postgresMapping.put("INTEGER", "INTEGER");
		postgresMapping.put("BIGINT", "INTEGER");
		
		
		postgresMapping.put("FLOAT", "NUMERIC(precision, scale)");
		postgresMapping.put("REAL", "NUMERIC(precision, scale)");
		postgresMapping.put("DOUBLE", "NUMERIC(precision, scale)");
		postgresMapping.put("NUMERIC", "NUMERIC(precision, scale)");
		postgresMapping.put("DECIMAL", "NUMERIC(precision, scale)");
		
		postgresMapping.put("CHAR", "VARCHAR(precision)");
		postgresMapping.put("VARCHAR", "VARCHAR(precision)");
		postgresMapping.put("NCHAR", "NVARCHAR(precision)");
		postgresMapping.put("NVARCHAR", "NVARCHAR(precision)");
		
		postgresMapping.put("DATE", "DATE");
		postgresMapping.put("TIME", "TIME(3)");
		postgresMapping.put("TIMESTAMP", "TIMESTAMP(3)");
		
		columnIdToNameMapping.put("POSTGRESQL", postgresMapping);
		
		Map<String, String> mysqlMapping = new HashMap<String, String>();
		mysqlMapping.put("BIT", "BIT(precision)");
		mysqlMapping.put("TINYINT", "INTEGER");
		mysqlMapping.put("SMALLINT", "INTEGER");
		mysqlMapping.put("INTEGER", "INTEGER");
		mysqlMapping.put("BIGINT", "INTEGER");
		
		mysqlMapping.put("FLOAT", "NUMERIC(precision, scale)");
		mysqlMapping.put("REAL", "NUMERIC(precision, scale)");
		mysqlMapping.put("DOUBLE", "NUMERIC(precision, scale)");
		mysqlMapping.put("NUMERIC", "NUMERIC(precision, scale)");
		mysqlMapping.put("DECIMAL", "NUMERIC(precision, scale)");
		
		mysqlMapping.put("CHAR", "VARCHAR(precision)");
		mysqlMapping.put("VARCHAR", "VARCHAR(precision)");
		mysqlMapping.put("NCHAR", "NVARCHAR(precision)");
		mysqlMapping.put("NVARCHAR", "NVARCHAR(precision)");
		
		mysqlMapping.put("DATE", "DATE");
		mysqlMapping.put("TIME", "TIME(3)");
		mysqlMapping.put("TIMESTAMP", "TIMESTAMP(3)");
		
		columnIdToNameMapping.put("MYSQL", mysqlMapping);
		
		Map<String, String> oracleMapping = new HashMap<String, String>();
		oracleMapping.put("BIT", "BIT(precision)");
		oracleMapping.put("TINYINT", "INTEGER");
		oracleMapping.put("SMALLINT", "INTEGER");
		oracleMapping.put("INTEGER", "INTEGER");
		oracleMapping.put("BIGINT", "INTEGER");
		
		oracleMapping.put("FLOAT", "NUMERIC(precision, scale)");
		oracleMapping.put("REAL", "NUMERIC(precision, scale)");
		oracleMapping.put("DOUBLE", "NUMERIC(precision, scale)");
		oracleMapping.put("NUMERIC", "NUMERIC(precision, scale)");
		oracleMapping.put("DECIMAL", "NUMERIC(precision, scale)");
		
		oracleMapping.put("CHAR", "VARCHAR(precision)");
		oracleMapping.put("VARCHAR", "VARCHAR(precision)");
		oracleMapping.put("NCHAR", "NVARCHAR(precision)");
		oracleMapping.put("NVARCHAR", "NVARCHAR(precision)");
		
		oracleMapping.put("DATE", "DATE");
		oracleMapping.put("TIME", "TIME(3)");
		oracleMapping.put("TIMESTAMP", "TIMESTAMP(3)");
		
		columnIdToNameMapping.put("ORACLE", oracleMapping);
		
		Map<String, String> sqlServerMapping = new HashMap<String, String>();
		sqlServerMapping.put("BIT", "BIT(precision)");
		sqlServerMapping.put("TINYINT", "NUMERIC(precision, scale)");
		sqlServerMapping.put("SMALLINT", "INTEGER");
		sqlServerMapping.put("INTEGER", "INTEGER");
		
		sqlServerMapping.put("FLOAT", "NUMERIC(precision, scale)");
		sqlServerMapping.put("REAL", "NUMERIC(precision, scale)");
		sqlServerMapping.put("DOUBLE", "NUMERIC(precision, scale)");
		sqlServerMapping.put("NUMERIC", "NUMERIC(precision, scale)");
		sqlServerMapping.put("DECIMAL", "NUMERIC(precision, scale)");
		
		sqlServerMapping.put("CHAR", "VARCHAR(precision)");
		sqlServerMapping.put("VARCHAR", "VARCHAR(precision)");
		sqlServerMapping.put("NCHAR", "NVARCHAR(precision)");
		sqlServerMapping.put("NVARCHAR", "NVARCHAR(precision)");
		
		sqlServerMapping.put("DATE", "DATE");
		sqlServerMapping.put("TIME", "TIME(3)");
		sqlServerMapping.put("TIMESTAMP", "TIMESTAMP(3)");
		
		columnIdToNameMapping.put("SQLSERVER", sqlServerMapping);
		
		Map<String, String> snowflakeMapping = new HashMap<String, String>();
		snowflakeMapping.put("BIT", "BIT(precision)");
		snowflakeMapping.put("TINYINT", "NUMERIC(precision, scale)");
		snowflakeMapping.put("SMALLINT", "INTEGER");
		snowflakeMapping.put("INTEGER", "INTEGER");
		snowflakeMapping.put("BIGINT", "INTEGER");
		
		snowflakeMapping.put("FLOAT", "NUMERIC(precision, scale)");
		snowflakeMapping.put("REAL", "NUMERIC(precision, scale)");
		snowflakeMapping.put("DOUBLE", "NUMERIC(precision, scale)");
		snowflakeMapping.put("NUMERIC", "NUMERIC(precision, scale)");
		snowflakeMapping.put("DECIMAL", "NUMERIC(precision, scale)");
		
		snowflakeMapping.put("CHAR", "VARCHAR(precision)");
		snowflakeMapping.put("VARCHAR", "VARCHAR(precision)");
		snowflakeMapping.put("NCHAR", "NVARCHAR(precision)");
		snowflakeMapping.put("NVARCHAR", "NVARCHAR(precision)");
		
		snowflakeMapping.put("DATE", "DATE");
		snowflakeMapping.put("TIME", "TIME(3)");
		snowflakeMapping.put("TIMESTAMP", "TIMESTAMP(3)");
		
		columnIdToNameMapping.put("NOWFLAKE", snowflakeMapping);
		
		
		
		
	}
	
	public boolean isValidColumnType(final String columnType)	{ return columnIdToName.containsValue(columnType); }
	public boolean isValidColumnType(final int columnType) { return columnIdToName.containsKey( Integer.valueOf(columnType) );}
	
	public static boolean isValidColumnType_(final String columnType)	{
		ColumnTypeTable cType = new ColumnTypeTable();
		return cType.isValidColumnType(columnType);
	}
	public static boolean isValidColumnType_(final int columnType)	{
		ColumnTypeTable cType = new ColumnTypeTable();
		return cType.isValidColumnType(columnType);
	}
}
