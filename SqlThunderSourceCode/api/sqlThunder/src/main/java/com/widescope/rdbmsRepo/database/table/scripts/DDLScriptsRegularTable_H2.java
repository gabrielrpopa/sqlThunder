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


import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.widescope.rdbmsRepo.database.table.model.TableColumn;
import com.widescope.rdbmsRepo.database.table.model.TableHeader;

public class DDLScriptsRegularTable_H2 {

	public static String createTableString(	final String tableName, 
											final TableHeader tableHeader) throws SQLException	{	
		String sql =  "CREATE TABLE " + tableName + "(" ;
	
		final int maxCount = tableHeader.gethList().size();
		int count = 0;
		for (TableColumn tableColumn : tableHeader.gethList().values())	{
			String columnName = tableColumn.getColumnName();
			
			if(tableColumn.getColumnTypeId() == java.sql.Types.VARCHAR) {
				sql =  sql + columnName + " VARCHAR(" + tableColumn.getLength() + ")";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.NVARCHAR 
					|| tableColumn.getColumnTypeId() == java.sql.Types.NCHAR 
					|| tableColumn.getColumnTypeId() == java.sql.Types.LONGNVARCHAR) {
				sql =  sql + columnName + " NVARCHAR(" + tableColumn.getLength() + ")";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.INTEGER )	{
				sql =  sql + columnName  + " INTEGER";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.BIGINT ) {
				sql =  sql + columnName + " BIGINT";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.SMALLINT)	{
				sql =  sql + columnName + " SMALLINT";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.FLOAT) {
				sql =  sql + columnName + " DECIMAL(" + tableColumn.getLength() + ", " + tableColumn.getScale()  + ")";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.DOUBLE 
					|| tableColumn.getColumnTypeId() == java.sql.Types.DECIMAL) {
				sql =  sql + columnName + " DOUBLE";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.TIMESTAMP || tableColumn.getColumnTypeId() == java.sql.Types.TIMESTAMP_WITH_TIMEZONE) {
				sql =  sql + columnName + " TIME WITH TIME ZONE";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.DATE) {
				sql =  sql + columnName + " DATE";
			}
			else if(tableColumn.getColumnTypeId() == java.sql.Types.CLOB)	{
				sql =  sql + columnName + " CLOB(" + tableColumn.getLength() + ")";
			}
			else {
		
			}
		
			if(count < maxCount )
				sql =  sql + ", ";
	
	
			count++;
		}
	
		sql =  sql + "); ";   
	
		return sql;
	}
	
	
	
	
	
	
	
	public static List<String> getIndexStrings(	final String tableName, 
												final TableHeader tableHeader)
	{
		List<String> ret = new ArrayList<String>();
		int counter = 1;
		final String indexTemplate = "CREATE INDEX IF NOT EXISTS idx_@tableName@_@cnt@ ON @tableName@(@column@) @ORDER@;";
		for(TableColumn col: tableHeader.gethList().values()) {
			String indexTemplateTmp = indexTemplate;
			indexTemplateTmp = indexTemplateTmp.replace("@tableName@", tableName);
			indexTemplateTmp = indexTemplateTmp.replace("@tableName@", tableName);
			indexTemplateTmp = indexTemplateTmp.replace("@column@", col.getColumnName() );
			indexTemplateTmp = indexTemplateTmp.replace("@cnt@", String.valueOf(counter) );
			ret.add(indexTemplateTmp);
			counter++;
		}
		return ret;
	}
	
	
	public static boolean isNumber(final String val) {
		boolean ret = false;
		try	{ Integer.parseInt(val); ret = true; }	catch(Exception ex) { }		
		try	{ Float. parseFloat(val); ret = true; } catch(Exception ex)	{ }
		try	{ Double. parseDouble(val);	ret = true; } catch(Exception ex) { }
		try	{ Long.parseLong(val) ;	ret = true; } catch(Exception ex) { }
		try	{ new BigInteger(val) ;	ret = true; } catch(Exception ex) { }
		return ret; 
		
	}

	
	
		
	public static String getSearchSqlALL(final String tableName, final TableHeader tableHeader, final boolean isCount) {
		String searchCountSql = "";
		StringBuilder indexTemplate = new StringBuilder();
		
		if(isCount)	{
			indexTemplate.append("SELECT COUNT(*) FROM @tableName@ WHERE  @@");
		}
		else {
			indexTemplate.append("SELECT * FROM @tableName@ WHERE  @@");
		}

		for(TableColumn col: tableHeader.gethList().values()) {
			if(col.getColumnTypeId() == java.sql.Types.VARCHAR 
					|| col.getColumnTypeId() == java.sql.Types.NVARCHAR 
					|| col.getColumnTypeId() == java.sql.Types.NCHAR 
					|| col.getColumnTypeId() == java.sql.Types.LONGNVARCHAR
					|| col.getColumnTypeId() == java.sql.Types.TIMESTAMP 
					|| col.getColumnTypeId() == java.sql.Types.TIMESTAMP_WITH_TIMEZONE
					)
			{
				if(indexTemplate.toString().contains("@@")	) {
					indexTemplate.delete(indexTemplate.toString().length() - 2, indexTemplate.toString().length());
					indexTemplate.append(col.getColumnName() + " LIKE " + "%@VALUE@%");
				}
				else {
					indexTemplate.append(" OR " + col.getColumnName() + " LIKE " + "%@VALUE@%");
				}
				
			}
			else if(col.getColumnTypeId() == java.sql.Types.FLOAT
					|| col.getColumnTypeId() == java.sql.Types.DOUBLE
					|| col.getColumnTypeId() == java.sql.Types.DECIMAL
					|| col.getColumnTypeId() == java.sql.Types.BIGINT
					|| col.getColumnTypeId() == java.sql.Types.INTEGER
					|| col.getColumnTypeId() == java.sql.Types.SMALLINT
					)
			{
				if(indexTemplate.toString().contains("@@")	) {
					indexTemplate.delete(indexTemplate.toString().length() - 2, indexTemplate.toString().length());
					indexTemplate.append(col.getColumnName() + "=" + "'@VALUE@'");
				}
				else {
					indexTemplate.append(" OR " + col.getColumnName() + "=" + "'@VALUE@'");
				}
				
				
			}
			else if(col.getColumnTypeId() == java.sql.Types.CLOB)	{
				indexTemplate.append(" OR " + col.getColumnName() + " LIKE " + "%@VALUE@%");
			}
		}
		return searchCountSql;
	}

	public static String getSearchSqlVARCHAR(	final String tableName, 
												final TableHeader tableHeader, 
												final boolean isCount) {
		String searchCountSql = "";
		StringBuilder indexTemplate = new StringBuilder();
		if(isCount)	{
			indexTemplate.append("SELECT COUNT(*) FROM @tableName@ WHERE  @@");
		}
		else {
			indexTemplate.append("SELECT * FROM @tableName@ WHERE  @@");
		}
		for(TableColumn col: tableHeader.gethList().values()) {
			
			if(col.getColumnTypeId() == java.sql.Types.VARCHAR 
					|| col.getColumnTypeId() == java.sql.Types.NVARCHAR 
					|| col.getColumnTypeId() == java.sql.Types.NCHAR 
					|| col.getColumnTypeId() == java.sql.Types.LONGNVARCHAR
					|| col.getColumnTypeId() == java.sql.Types.TIMESTAMP 
					|| col.getColumnTypeId() == java.sql.Types.TIMESTAMP_WITH_TIMEZONE
					)
			{
				if(indexTemplate.toString().contains("@@")	)	{
					indexTemplate.delete(indexTemplate.toString().length() - 2, indexTemplate.toString().length());
					indexTemplate.append(col.getColumnName() + " LIKE " + "%@VALUE@%");
				}
				else {
					indexTemplate.append(" OR " + col.getColumnName() + " LIKE " + "%@VALUE@%");
				}
			}
		}
		return searchCountSql;
	}
	
	
	
	
	
	
}
