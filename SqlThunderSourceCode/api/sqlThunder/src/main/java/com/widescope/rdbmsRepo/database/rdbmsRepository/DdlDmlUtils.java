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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

import com.widescope.logging.AppLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.types.ColumnTypeTable;


public class DdlDmlUtils {

	private String tableName;
	private String dbSystem;
	private String createTableStatement;
	private List<String> insertStatementList;

	public String getTableName() { return tableName; }
	public void setTableName(String tableName) { this.tableName = tableName; }


	public DdlDmlUtils() {
			this.setDbSystem(null);
			this.setCreateTableStatement(null);
			this.setInsertStatementList(new ArrayList<>());
	}

	public String getDbSystem() {	return dbSystem; }
	public void setDbSystem(String dbSystem) { this.dbSystem = dbSystem; }

	public String getCreateTableStatement() {	return createTableStatement; }
	public void setCreateTableStatement(String createTableStatement) { this.createTableStatement = createTableStatement; }

	public List<String> getInsertStatementList() { return insertStatementList; }
	public void setInsertStatementList(List<String> insertStatementList) { this.insertStatementList = insertStatementList; }
	
	
	public static String createTableStmt(	final ResultQuery resultQuery, 
											final String dbSystem,
											final String rdbmsSchema,
											final String rdbmsTable) throws Exception {
		if(dbSystem == null || !DbUtil.isDatabase(dbSystem))
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "No Db System name provided")) ;

			
		ColumnTypeTable columnTypeTable=new ColumnTypeTable();

		String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
		String tbl = rdbmsTable!= null && !rdbmsTable.isBlank() && !rdbmsTable.isEmpty() ? rdbmsTable : resultQuery.getSqlName();
		String statement = "CREATE TABLE "	+ schema +	tbl + " (#columns#);";

		StringBuilder columns = new StringBuilder();
		int size = resultQuery.getMetadata().size();
		try {
			for(ResultMetadata resultMetadata : resultQuery.getMetadata() ) {
				size--;
				String tableColumn = columnTypeTable.columnIdToNameMapping.get(dbSystem).get(resultMetadata.getColumnTypeName());
				tableColumn = tableColumn.replace("precision", String.valueOf(resultMetadata.getLength()) );
				tableColumn = tableColumn.replace("scale", String.valueOf(resultMetadata.getScale()) );
				
				
				columns.append(resultMetadata.getColumnName()).append(" ").append(tableColumn);
				if(size != 0) { columns.append(","); columns.append(" "); }
			}
			return statement.replace("#columns#", columns);
		} catch (Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
    }


	public static void createTable(	final ResultQuery resultQuery, 
									final String dbSystem,
									final String rdbmsConnectionName,
									final String rdbmsSchema,
									final String rdbmsTable) throws Exception {

		if(dbSystem == null || !DbUtil.isDatabase(dbSystem))
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "No Db System name provided")) ;
		
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsConnectionName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		ColumnTypeTable columnTypeTable=new ColumnTypeTable();
		String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
		String tbl = rdbmsTable!= null && !rdbmsTable.isBlank() && !rdbmsTable.isEmpty() ? rdbmsTable : resultQuery.getSqlName();
		String statement = "CREATE TABLE "	+ schema +	tbl + " (#columns#);";
				
		StringBuilder columns = new StringBuilder();
		int size = resultQuery.getMetadata().size();
		try {
			for(ResultMetadata resultMetadata : resultQuery.getMetadata() ) {
				size--;
				String tableColumn = columnTypeTable.columnIdToNameMapping.get(dbSystem).get(resultMetadata.getColumnTypeName());
				tableColumn = tableColumn.replace("precision", String.valueOf(resultMetadata.getLength()) );
				tableColumn = tableColumn.replace("scale", String.valueOf(resultMetadata.getScale()) );
				
				columns.append(resultMetadata.getColumnName()).append(" ").append(tableColumn);
				if(size != 0) { columns.append(","); columns.append(" "); }
			}
			statement = statement.replace("#columns#", columns);
			SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, statement) ;
		} catch (Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
	}
	
	
	public static void createTable(	final List<ResultMetadata> metadata, 
									final String dbSystem,
									final DbConnectionInfo connectionDetailInfo,
									final String rdbmsSchema,
									final String rdbmsTable) throws Exception {

		if(dbSystem == null || !DbUtil.isDatabase(dbSystem))
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "No Db System name provided")) ;
	
		ColumnTypeTable columnTypeTable=new ColumnTypeTable();
		String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
		String statement = "CREATE TABLE "	+ schema +	rdbmsTable + " (#columns#);";
		
		StringBuilder columns = new StringBuilder();
		int size = metadata.size();
		try {
			for(ResultMetadata resultMetadata : metadata ) {
				size--;
				String tableColumn = columnTypeTable.columnIdToNameMapping.get(dbSystem).get(resultMetadata.getColumnTypeName());
				tableColumn = tableColumn.replace("precision", String.valueOf(resultMetadata.getLength()) );
				tableColumn = tableColumn.replace("scale", String.valueOf(resultMetadata.getScale()) );
				
				columns.append(resultMetadata.getColumnName()).append(" ").append(tableColumn);
				if(size != 0) { columns.append(","); columns.append(" "); }
			}
			statement = statement.replace("#columns#", columns);
			SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, statement) ;
		} catch (Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	
	}


	public static List<String> insertTableStmt(	final ResultQuery resultQuery,
												final String rdbmsSchema,
												final String rdbmsTable) {
		List<String> ret = new ArrayList<>();
		Map<String, ResultMetadata> metadataListToMap = ResultQuery.metadataListToMap(resultQuery.getMetadata());
		List<HashMap<String, Object>> body = toResultQueryBody(resultQuery.getResultQueryJson().toJSONString());
		ColumnTypeTable columnTypeTable = new ColumnTypeTable();
		String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
		String tbl = rdbmsTable!= null && !rdbmsTable.isBlank() && !rdbmsTable.isEmpty() ? rdbmsTable : resultQuery.getSqlName();
		String stem = "INSERT INTO " +	schema ;

        assert body != null;
        for(HashMap<String, Object> row : body) { // rows
			StringBuilder insertStatement = new StringBuilder(stem + tbl + " VALUES (") ;
			for (Map.Entry<String, Object> entry : row.entrySet()) {
				String columnName = entry.getKey();
				Object value = entry.getValue();
				String columnTypeName = metadataListToMap.get(columnName).getColumnTypeName();
				if(columnTypeTable.columnNoQuote.contains(columnTypeName)) {
					insertStatement.append(value);
				}
				else if(columnTypeTable.columnQuote.contains(columnTypeName)) {
					insertStatement.append("'").append(value).append("'");
				}
			}
			insertStatement.append(")");
			ret.add(insertStatement.toString());
		}
		return ret;
	}
	
	
	public static int insertTable(	final ResultQuery resultQuery,
									final String rdbmsConnectionName,
									final String rdbmsSchema,
									final String rdbmsTable) throws Exception {
		
		int countInserted = 0;
		Map<String, ResultMetadata> metadataListToMap = ResultQuery.metadataListToMap(resultQuery.getMetadata());
		List<HashMap<String, Object>> body = toResultQueryBody(resultQuery.getResultQueryJson().toJSONString());
		ColumnTypeTable columnTypeTable = new ColumnTypeTable();
		
		
		String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
		String tbl = rdbmsTable!= null && !rdbmsTable.isBlank() && !rdbmsTable.isEmpty() ? rdbmsTable : resultQuery.getSqlName();
		
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsConnectionName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		int columnNumber = resultQuery.getMetadata().size();
        assert body != null;
        for(HashMap<String, Object> row : body) { // rows
			StringBuilder columnNames = new StringBuilder() ;
			StringBuilder columnValues = new StringBuilder() ;
			int count = columnNumber;
			for (Map.Entry<String, Object> entry : row.entrySet()) {
				count--;
				String columnName = entry.getKey();
				Object value = entry.getValue();
				String columnTypeName = metadataListToMap.get(columnName).getColumnTypeName();
				
				columnNames.append(columnName);
				if(columnTypeTable.columnNoQuote.contains(columnTypeName)) {
					columnValues.append(value);
				}
				else if(columnTypeTable.columnQuote.contains(columnTypeName)) {
					columnValues.append("'").append(value).append("'");
				}
				else {
					columnValues.append("'").append(value).append("'");
				}
				
				if(count !=0) {
					columnValues.append(",");
					columnNames.append(",");
				}
				
			}
			
			String insertStatement = "INSERT INTO " + schema + tbl + "(" + columnNames + ") VALUES (" + columnValues + ")";
			
			countInserted += SqlQueryExecUtils.execStaticDml(connectionDetailInfo, insertStatement) ;
		}
		return countInserted;
	}
	
	
	
	
	
	public static int insertTable(	final ResultSet rsSource,
									final String rdbmsConnectionName,
									final String rdbmsSchema,
									final String rdbmsTable) throws Exception {

		int countInserted = 0;
		String schema = rdbmsSchema != null && !rdbmsSchema.isBlank() && !rdbmsSchema.isEmpty() ? rdbmsSchema + ".": "";
        SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsConnectionName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		ResultSetMetaData metaData = rsSource.getMetaData();
		
		try {
			while (rsSource.next()) {
				
				StringBuilder columnNames = new StringBuilder() ;
				StringBuilder columnValues = new StringBuilder() ;
				
	        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
	        		String columnName = metaData.getColumnName(i);
	        		int columnType = metaData.getColumnType(i);
	        		
	        		columnNames.append(columnName);
	        		
	        		if(columnType == java.sql.Types.BIT) {
	        			columnValues.append("'").append(rsSource.getString(columnName)).append("'");
	        		}
	        		else if(columnType == java.sql.Types.NVARCHAR ) {
	        			columnValues.append("'").append(rsSource.getNString(columnName)).append("'");
	        		}
	        		else if(columnType == java.sql.Types.VARCHAR ) {
	        			columnValues.append("'").append(rsSource.getString(columnName)).append("'");
	        		}
	        		else if(columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.SMALLINT) {
	        			columnValues.append(rsSource.getInt(columnName));
	        		}
	        		else if(columnType == java.sql.Types.BIGINT ) {
	        			columnValues.append(rsSource.getLong(columnName));
	        		}
	        		else if(columnType == java.sql.Types.FLOAT )	{
	        			columnValues.append(rsSource.getFloat(columnType));
	        		}
	        		else if(columnType == java.sql.Types.DECIMAL || columnType == java.sql.Types.REAL)	{
	        			columnValues.append(rsSource.getBigDecimal(columnName));
	        		}
	           		else if(columnType == java.sql.Types.DOUBLE) {
	           			columnValues.append(rsSource.getBigDecimal(columnName));
	        		}
	        		else if(columnType == java.sql.Types.TIMESTAMP) {
	        			columnValues.append("'").append(rsSource.getTimestamp(columnName)).append("'");

	        		}
	        		else if(columnType == java.sql.Types.DATE) {
	        			columnValues.append("'").append(rsSource.getDate(columnName)).append("'");
	        		}
	        		else if(columnType == java.sql.Types.NCLOB)	{
	        			columnValues.append("'").append(rsSource.getNClob(columnName)).append("'");
	        		}
	        		else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY || columnType == java.sql.Types.VARBINARY) {
	        			columnValues.append("'").append(Arrays.toString(rsSource.getBytes(columnName))).append("'");
	        		}
	        		else if(columnType == java.sql.Types.BOOLEAN) {
	        			columnValues.append(rsSource.getBoolean(columnName));
	        		}
	        		else {
	        			columnValues.append("'").append(rsSource.getString(columnName)).append("'");
						AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "insertTable: Unknown column type " + columnType + " for " + columnName);
	        		}
	        		
	        		if(i < metaData.getColumnCount()) {
						columnValues.append(",");
						columnNames.append(",");
					}
	        	}
				String insertStatement = "INSERT INTO " + schema + rdbmsTable + "(" + columnNames + ") VALUES (" + columnValues + ")";
				countInserted += SqlQueryExecUtils.execStaticDml(connectionDetailInfo, insertStatement) ;
			}
		}
		catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
    	}
		return countInserted;
	}
	
	public static List<HashMap<String, Object>> toResultQueryBody(final String resultQuery) {
		List<HashMap<String, Object>> lst;
		TypeReference<HashMap<String, List<HashMap<String, Object>> >> typeRef = new TypeReference<>() {};
		try {
			HashMap<String, List<HashMap<String, Object>> > mapping =  new ObjectMapper().readValue(resultQuery, typeRef);
			lst = mapping.get("table");
			return lst;
		} catch (Exception e1) {
			return null;
		}
	}
	

	
	
}
