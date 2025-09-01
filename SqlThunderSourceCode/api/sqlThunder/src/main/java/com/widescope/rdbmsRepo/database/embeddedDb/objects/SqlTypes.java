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

package com.widescope.rdbmsRepo.database.embeddedDb.objects;


import java.sql.Types;
import java.util.*;


public class SqlTypes {
	
	private static final Map<Integer, String> sqlDataTypesById = new HashMap<Integer, String>();
	private static final Map<String, Integer> sqlDataTypesByName = new HashMap<String, Integer>();

	// stores a mapping between Java DataTypes to H2 DataTypes
	private static Map<String, String> h2 = new HashMap<String, String>();

	private void sqlDataTypesById() {
		SqlTypes.sqlDataTypesById.put(Types.BIT, "BIT");
		SqlTypes.sqlDataTypesById.put(Types.TINYINT, "TINYINT");
		SqlTypes.sqlDataTypesById.put(Types.SMALLINT, "SMALLINT");
		SqlTypes.sqlDataTypesById.put(Types.INTEGER, "INTEGER");
		SqlTypes.sqlDataTypesById.put(Types.BIGINT, "BIGINT");
		SqlTypes.sqlDataTypesById.put(Types.FLOAT, "FLOAT");
		SqlTypes.sqlDataTypesById.put(Types.REAL, "REAL");
		SqlTypes.sqlDataTypesById.put(Types.DOUBLE, "DOUBLE");
		SqlTypes.sqlDataTypesById.put(Types.NUMERIC, "NUMERIC");
		SqlTypes.sqlDataTypesById.put(Types.DECIMAL, "DECIMAL");
		SqlTypes.sqlDataTypesById.put(Types.CHAR, "CHAR");
		SqlTypes.sqlDataTypesById.put(Types.VARCHAR, "VARCHAR");
		SqlTypes.sqlDataTypesById.put(Types.LONGVARCHAR, "LONGVARCHAR");
		SqlTypes.sqlDataTypesById.put(Types.DATE, "DATE");
		SqlTypes.sqlDataTypesById.put(Types.TIME, "TIME");
		SqlTypes.sqlDataTypesById.put(Types.TIMESTAMP, "TIMESTAMP");
		SqlTypes.sqlDataTypesById.put(Types.BINARY, "BINARY");
		SqlTypes.sqlDataTypesById.put(Types.VARBINARY, "VARBINARY");
		SqlTypes.sqlDataTypesById.put(Types.LONGVARBINARY, "LONGVARBINARY");
		SqlTypes.sqlDataTypesById.put(Types.NULL, "NULL");
		SqlTypes.sqlDataTypesById.put(Types.OTHER, "OTHER");
		SqlTypes.sqlDataTypesById.put(Types.JAVA_OBJECT, "JAVA_OBJECT");
		SqlTypes.sqlDataTypesById.put(Types.DISTINCT, "DISTINCT");
		SqlTypes.sqlDataTypesById.put(Types.STRUCT, "STRUCT");
		SqlTypes.sqlDataTypesById.put(Types.ARRAY, "ARRAY");
		SqlTypes.sqlDataTypesById.put(Types.BLOB, "BLOB");
		SqlTypes.sqlDataTypesById.put(Types.CLOB, "CLOB");
		SqlTypes.sqlDataTypesById.put(Types.REF, "REF");
		SqlTypes.sqlDataTypesById.put(Types.DATALINK, "DATALINK");
		SqlTypes.sqlDataTypesById.put(Types.BOOLEAN, "BOOLEAN");
		SqlTypes.sqlDataTypesById.put(Types.ROWID, "ROWID");
		SqlTypes.sqlDataTypesById.put(Types.NCHAR, "NCHAR");
		SqlTypes.sqlDataTypesById.put(Types.NVARCHAR, "NVARCHAR");
		SqlTypes.sqlDataTypesById.put(Types.LONGNVARCHAR, "LONGNVARCHAR");
		SqlTypes.sqlDataTypesById.put(Types.NCLOB, "NCLOB");
		SqlTypes.sqlDataTypesById.put(Types.SQLXML, "SQLXML");
		SqlTypes.sqlDataTypesById.put(Types.REF_CURSOR, "REF_CURSOR");
		SqlTypes.sqlDataTypesById.put(Types.TIME_WITH_TIMEZONE, "TIME_WITH_TIMEZONE");
		SqlTypes.sqlDataTypesById.put(Types.TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE");
	}

	@SuppressWarnings("unused")
	private void sqlDataTypesByName() {
		SqlTypes.sqlDataTypesByName.put("BIT", Types.BIT);
		SqlTypes.sqlDataTypesByName.put("TINYINT", Types.TINYINT);
		SqlTypes.sqlDataTypesByName.put("SMALLINT", Types.SMALLINT);
		SqlTypes.sqlDataTypesByName.put("INTEGER", Types.INTEGER);
		SqlTypes.sqlDataTypesByName.put("BIGINT", Types.BIGINT);
		SqlTypes.sqlDataTypesByName.put("FLOAT", Types.FLOAT);
		SqlTypes.sqlDataTypesByName.put("REAL",Types.REAL);
		SqlTypes.sqlDataTypesByName.put("DOUBLE", Types.DOUBLE);
		SqlTypes.sqlDataTypesByName.put("NUMERIC", Types.NUMERIC);
		SqlTypes.sqlDataTypesByName.put("DECIMAL", Types.DECIMAL);
		SqlTypes.sqlDataTypesByName.put("CHAR", Types.CHAR);
		SqlTypes.sqlDataTypesByName.put("VARCHAR", Types.VARCHAR);
		SqlTypes.sqlDataTypesByName.put("LONGVARCHAR", Types.LONGVARCHAR);
		SqlTypes.sqlDataTypesByName.put("DATE", Types.DATE);
		SqlTypes.sqlDataTypesByName.put("TIME", Types.TIME);
		SqlTypes.sqlDataTypesByName.put("TIMESTAMP", Types.TIMESTAMP);
		SqlTypes.sqlDataTypesByName.put("BINARY", Types.BINARY);
		SqlTypes.sqlDataTypesByName.put("VARBINARY", Types.VARBINARY);
		SqlTypes.sqlDataTypesByName.put("LONGVARBINARY", Types.LONGVARBINARY);
		SqlTypes.sqlDataTypesByName.put("NULL", Types.NULL);
		SqlTypes.sqlDataTypesByName.put("OTHER", Types.OTHER);
		SqlTypes.sqlDataTypesByName.put("JAVA_OBJECT", Types.JAVA_OBJECT);
		SqlTypes.sqlDataTypesByName.put("DISTINCT", Types.DISTINCT);
		SqlTypes.sqlDataTypesByName.put("STRUCT", Types.STRUCT);
		SqlTypes.sqlDataTypesByName.put("ARRAY", Types.ARRAY);
		SqlTypes.sqlDataTypesByName.put("BLOB", Types.BLOB);
		SqlTypes.sqlDataTypesByName.put("CLOB", Types.CLOB);
		SqlTypes.sqlDataTypesByName.put("REF", Types.REF);
		SqlTypes.sqlDataTypesByName.put("DATALINK", Types.DATALINK);
		SqlTypes.sqlDataTypesByName.put("BOOLEAN", Types.BOOLEAN);
		SqlTypes.sqlDataTypesByName.put("ROWID", Types.ROWID);
		SqlTypes.sqlDataTypesByName.put("NCHAR", Types.NCHAR);
		SqlTypes.sqlDataTypesByName.put("NVARCHAR", Types.NVARCHAR);
		SqlTypes.sqlDataTypesByName.put("LONGNVARCHAR", Types.LONGNVARCHAR);
		SqlTypes.sqlDataTypesByName.put("NCLOB", Types.NCLOB);
		SqlTypes.sqlDataTypesByName.put("SQLXML", Types.SQLXML);
		SqlTypes.sqlDataTypesByName.put("REF_CURSOR", Types.REF_CURSOR);
		SqlTypes.sqlDataTypesByName.put("TIME_WITH_TIMEZONE", Types.TIME_WITH_TIMEZONE);
		SqlTypes.sqlDataTypesByName.put("TIMESTAMP_WITH_TIMEZONE", Types.TIMESTAMP_WITH_TIMEZONE);
	}

	/**
	 * Mapping of H2 column types to Java Sql Type
	 */
	public void h2Mapping() {

		SqlTypes.h2.put("CHARACTER", "VARCHAR");
		SqlTypes.h2.put("CHARACTER VARYING", "VARCHAR");
		SqlTypes.h2.put("CHARACTER LARGE OBJECT", "CLOB");
		SqlTypes.h2.put("VARCHAR_IGNORECASE", "VARCHAR");
		SqlTypes.h2.put("BINARY","BINARY");
		SqlTypes.h2.put("BINARY VARYING","NVARCHAR");
		SqlTypes.h2.put("BINARY LARGE OBJECT","ARRAY");
		SqlTypes.h2.put("BOOLEAN","BOOLEAN");
		SqlTypes.h2.put("TINYINT", "TINYINT");
		SqlTypes.h2.put("SMALLINT","SMALLINT");
		SqlTypes.h2.put("INTEGER", "INTEGER");
		SqlTypes.h2.put("BIGINT", "BIGINT");
		SqlTypes.h2.put("NUMERIC", "NUMERIC");
		SqlTypes.h2.put("REAL", "REAL");
		SqlTypes.h2.put("DOUBLE PRECISION", "DOUBLE");
		SqlTypes.h2.put("DECFLOAT","FLOAT");
		SqlTypes.h2.put("DATE", "DATE");
		SqlTypes.h2.put("TIME", "TIME");
		SqlTypes.h2.put("TIME WITH TIME ZONE", "TIME");
		SqlTypes.h2.put("TIMESTAMP", "TIMESTAMP");
		SqlTypes.h2.put("TIMESTAMP WITH TIME ZONE", "TIME");
		SqlTypes.h2.put("INTERVAL", "BIGINT");
		SqlTypes.h2.put("JAVA_OBJECT", "java.lang.Object");
		SqlTypes.h2.put("ENUM", "INTEGER");
		SqlTypes.h2.put("GEOMETRY", "VARCHAR");
		SqlTypes.h2.put("JSON", "VARCHAR");
		SqlTypes.h2.put("UUID", "VARCHAR");
		SqlTypes.h2.put("ARRAY", "VARCHAR");
		SqlTypes.h2.put("ROW","ResultSet");
	}

}


