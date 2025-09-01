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


package com.widescope.rdbmsRepo.database.table.scripts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.widescope.rdbmsRepo.database.table.model.TableColumn;
import com.widescope.rdbmsRepo.database.table.model.TableHeader;

public class DDLScriptsPivotTable_H2 {

	
	public static String createTableString(	final String tableName, 
											final TableHeader tableHeader) throws SQLException {	
		int maxCol = 1;
		for (TableColumn tableColumn : tableHeader.gethList().values())	{
			if(tableColumn.getLength() > maxCol ) maxCol = tableColumn.getLength();
		}
		StringBuilder sql =  new StringBuilder("CREATE TABLE " + tableName );
		sql.append("( columnValue VARCHAR (" + String.valueOf(maxCol) + ")");
		sql.append(", columnName VARCHAR (4000)");
		sql.append(", columnType SMALLINT");
		sql.append(", rowNumber BIGINT ");
		return sql.toString();
	}
	
	
	
	
	public static List<String> getIndexStrings(	final String tableName, 
												final TableHeader tableHeader) {
		List<String> ret = new ArrayList<String>();
		ret.add("CREATE INDEX IF NOT EXISTS idx_@tableName@_1 ON @tableName@(columnValue);");
		ret.add("CREATE INDEX IF NOT EXISTS idx_@tableName@_2 ON @tableName@(columnName) ;");
		ret.add("CREATE INDEX IF NOT EXISTS idx_@tableName@_3 ON @tableName@(rowNumber);");
		return ret;
	}
	
	
	
	
	public static String getSearchSqlALL(	final String tableName, 
											final TableHeader tableHeader, 
											final boolean isCount) {
		String searchCountSql = "";
		StringBuilder indexTemplate = new StringBuilder();
		if(isCount)	{
			indexTemplate.append("SELECT COUNT(*) FROM @tableName@ WHERE columnValue LIKE '%@value@%' ");
		}
		else {
			indexTemplate.append("SELECT * FROM @tableName@ WHERE rowNumber IN ( SELECT rowNumber FROM @tableName@ WHERE columnValue LIKE '%@value@%' ORDER BY rowNumber) ");
		}

		return searchCountSql;
	}

}
