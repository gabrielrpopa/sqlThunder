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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.sql.ResultSet;
import java.io.StringReader;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.widescope.logging.AppLogger;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlColumnType;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.RecordsAffected;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryAsList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryJsonRows;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlParameter;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoParam;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRepo;
import com.widescope.rdbmsRepo.database.table.model.TableColumn;
import com.widescope.rdbmsRepo.database.table.model.TableRow;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.types.ColumnTypeTable;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.security.HashWrapper;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.DmlBulkAffectedParam;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamListObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.DmlBatchAffectedParam;



public final class SqlQueryExecUtils {


	public static void closeDbHandles(	Connection conn, 
										Statement statement, 
										ResultSet rs) {
		try { if(rs!=null && !rs.isClosed())	rs.close(); } catch(SQLException se1)	{ System.out.print(se1.getMessage());}
		try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
	    try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
	}
	
	

	public static ResultQuery buildUpCsvFromResultSet(	final ResultSet rs, 
														ResultQuery ret) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		int recordsAffected = 0;
		while (rs.next()) {
			recordsAffected++;
        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
        		String columnName = metaData.getColumnName(i);
        		int columnType = metaData.getColumnType(i);

        		if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
        			sb.append("\"").append(rs.getString(columnName)).append("\"");
        		}
        		else if(columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.SMALLINT) {
    				sb.append(rs.getInt(columnName));
        		}
        		else if(columnType == java.sql.Types.BIGINT ) {
    				sb.append(rs.getLong(columnName));
        		}
        		else if(columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DECIMAL|| columnType == java.sql.Types.REAL)	{
        			sb.append(rs.getFloat(columnName));
        		}
           		else if(columnType == java.sql.Types.DOUBLE) {
        			sb.append(rs.getDouble(columnName));
        		}
        		else if(columnType == java.sql.Types.TIMESTAMP) {
        			sb.append("\"").append(rs.getTimestamp(columnName).toString()).append("\"");
        		}
        		else if(columnType == java.sql.Types.DATE) {
        			sb.append("\"").append(rs.getDate(columnName).toString()).append("\"");
        		}
        		else if(columnType == java.sql.Types.NCLOB)	{
        			sb.append("\"").append(rs.getClob(columnName)).append("\"");
        		}
        		else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY || columnType == java.sql.Types.VARBINARY) {
        			sb.append("\"").append(Arrays.toString(rs.getBytes(columnName))).append("\"");
        		}
        		else if(columnType == java.sql.Types.BOOLEAN) {
        			sb.append("\"").append(rs.getBoolean(columnName)).append("\"");
        		}
        		else {
        			sb.append("\"").append(rs.getString(columnName)).append("\"");
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Unknown column type " +  columnType + " column " + columnName);
	       		}
        		
        		if(i < metaData.getColumnCount()) sb.append(",");
            }
        	sb.append("\n");
        }
		ret.setRecordsAffected(recordsAffected);
		ret.setColumnsAffected(metaData.getColumnCount());
		if(!sb.isEmpty())
			ret.setResultQuery(sb.deleteCharAt(sb.length() - 1).toString());
		else
			ret.setResultQuery(sb.toString());
		return ret;
	}
	

	public static ResultQuery buildUpJsonFromResultSet(final ResultSet rs, 
														ResultQuery ret) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();
		
		List<JSONObject> wholeTableObject = new ArrayList<JSONObject>();
		int recordsAffected = 0;
        while (rs.next()) {
        	recordsAffected++;
        	HashMap<String, Object> rowObject = new HashMap<String, Object>();
        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
        		String columnName = metaData.getColumnName(i);
        		int columnType = metaData.getColumnType(i);

        		if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
        			String val = rs.getString(columnName);
        			if (rs.wasNull()) {
        				val = "";
        		    }
        			rowObject.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT || columnType == java.sql.Types.SMALLINT) {
        			Integer val = rs.getInt(columnName);
        			if (rs.wasNull()) {
        				val = null;
        		    }
    				rowObject.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE || columnType == java.sql.Types.DECIMAL|| columnType == java.sql.Types.REAL) {
        			Float val = rs.getFloat(columnName);
        			if (rs.wasNull()) {
        				val = null;
        		    }
    				rowObject.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.TIMESTAMP)	{
        			Timestamp val = rs.getTimestamp(columnName);
        			if (rs.wasNull())
        				rowObject.put(columnName, "");
        			else
        				rowObject.put(columnName, val.toString());
        		}
        		else if(columnType == java.sql.Types.DATE) {
        			Date val = rs.getDate(columnName);
        			if (rs.wasNull())
        				rowObject.put(columnName, "");
        			else
        				rowObject.put(columnName, val.toString());
        		}
        		else if(columnType == java.sql.Types.CLOB) {
        			rowObject.put(columnName, rs.getClob(columnName));
        		}
        		else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY || columnType == java.sql.Types.VARBINARY) {
        			rowObject.put(columnName, rs.getBytes(columnName) );
        		}
        		
        		else if(columnType == java.sql.Types.BOOLEAN) {
    				rowObject.put(columnName, rs.getBoolean(columnName));
        		}
        		else {
        			String val = rs.getString(columnName);
        			if (rs.wasNull()) {
        				val = "";
        		    }
        			rowObject.put(columnName, val);
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "QueryExecUtil.buildUpJsonFromResultSet Unknown column type " + columnType + " for: " + columnName);

        		}
            }
        	JSONObject result = new JSONObject(rowObject);
        	wholeTableObject.add(result);
        }
        
        HashMap<String, Object> wholeObject = new HashMap<String, Object>();
        wholeObject.put("table", wholeTableObject);
        JSONObject result = new JSONObject(wholeObject);
        ret.setRecordsAffected(recordsAffected);
        ret.setColumnsAffected(metaData.getColumnCount());
        ret.setResultQueryJson(result);
		return ret;
	}
	
	
	public static TableFormatMap buildUpJsonFromMigrationReturn(final ResultSet rs,
																TableFormatMap ret) throws SQLException {

		List<Map<String,Object>> rows = new ArrayList<>();
		ResultSetMetaData metaData = rs.getMetaData();
		
		while (rs.next()) {
			Map<String,Object> row = new HashMap<String,Object>();
        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
        		String columnName = metaData.getColumnName(i);
        		int columnType = metaData.getColumnType(i);
    		
        		if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
        			String val = rs.getString(columnName);
        			row.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.INTEGER 	|| columnType == java.sql.Types.BIGINT 
        														|| columnType == java.sql.Types.SMALLINT) {
        			Integer val = rs.getInt(columnName);
        			row.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.FLOAT 	|| columnType == java.sql.Types.DOUBLE 
        													|| columnType == java.sql.Types.DECIMAL
        													|| columnType == java.sql.Types.REAL) {
        			Float val = rs.getFloat(columnName);
        			row.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.TIMESTAMP)	{
        			Timestamp val = rs.getTimestamp(columnName);
        			row.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.DATE) {
        			Date val = rs.getDate(columnName);
        			row.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.CLOB) {
        			row.put(columnName, rs.getClob(columnName));
        		}
        		else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY 
        													|| columnType == java.sql.Types.VARBINARY) {
        			row.put(columnName, rs.getBytes(columnName));
        		}
        		
        		else if(columnType == java.sql.Types.BOOLEAN) {
        			row.put(columnName, rs.getBoolean(columnName));
        		}
        		else {
        			row.put(columnName, rs.getString(columnName));
        		}
            }
        	rows.add(row);
        }
		ret.setRows(rows);
		ret.setRowCount(rows.size());
		return ret;
	}
	
	

	public static ResultQueryAsList buildUpResultQueryAsList(final ResultSet rs, 
															ResultQueryAsList ret) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();
		
		List<HashMap<String, Object>> wholeTableObject = new ArrayList<>();
		
		while (rs.next()) {
			HashMap<String, Object> rowObject = new HashMap<>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				int columnType = metaData.getColumnType(i);
				
				if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
					String val = rs.getString(columnName);
					if (rs.wasNull()) {
						val = "";
					}
					rowObject.put(columnName, val);
				}
				else if(columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT || columnType == java.sql.Types.SMALLINT) {
					Integer val = rs.getInt(columnName);
					if (rs.wasNull()) {
						val = null;
					}
					rowObject.put(columnName, val);
				}
				else if(columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE || columnType == java.sql.Types.DECIMAL|| columnType == java.sql.Types.REAL) {
					Float val = rs.getFloat(columnName);
					if (rs.wasNull()) {
						val = null;
					}
					rowObject.put(columnName, val);
				}
				else if(columnType == java.sql.Types.TIMESTAMP)	{
					Timestamp val = rs.getTimestamp(columnName);
					if (rs.wasNull())
						rowObject.put(columnName, "");
					else
						rowObject.put(columnName, val.toString());
				}
				else if(columnType == java.sql.Types.DATE) {
					Date val = rs.getDate(columnName);
					if (rs.wasNull())
						rowObject.put(columnName, "");
					else
						rowObject.put(columnName, val.toString());
				}
				else if(columnType == java.sql.Types.CLOB) {
					rowObject.put(columnName, rs.getClob(columnName));
				}
				else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY || columnType == java.sql.Types.VARBINARY) {
					rowObject.put(columnName, rs.getBytes(columnName) );
				}
				
				else if(columnType == java.sql.Types.BOOLEAN) {
					rowObject.put(columnName, rs.getBoolean(columnName));
				}
				else {
					String val = rs.getString(columnName);
					if (rs.wasNull()) {
						val = "";
					}
					rowObject.put(columnName, val);
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "QueryExecUtil.buildUpJsonFromResultSet Unknown column type " + columnType + " for: " + columnName);
				}
			}
			wholeTableObject.add(rowObject);
		}
		ret.setResultQuery(wholeTableObject);
		return ret;
	}
	
	

	public static ResultQueryJsonRows buildUpJsonRowsFromResultSet(	final ResultSet rs, 
																	ResultQueryJsonRows resultQueryJsonRows) throws Exception {

		ResultSetMetaData metaData = rs.getMetaData();
		List<String> rows = new ArrayList<>();
        while (rs.next()) {
        	HashMap<String, Object> rowObject = new HashMap<>();
        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
        		String columnName = metaData.getColumnName(i);
        		int columnType = metaData.getColumnType(i);

        		if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
        			String val = rs.getString(columnName);
        			if (rs.wasNull()) {
        				val = "";
        		    }
        			rowObject.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT || columnType == java.sql.Types.SMALLINT) {
        			Integer val = rs.getInt(columnName);
        			if (rs.wasNull()) {
        				val = null;
        		    }
    				rowObject.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE || columnType == java.sql.Types.DECIMAL|| columnType == java.sql.Types.REAL) {
        			Float val = rs.getFloat(columnName);
        			if (rs.wasNull()) {
        				val = null;
        		    }
    				rowObject.put(columnName, val);
        		}
        		else if(columnType == java.sql.Types.TIMESTAMP)	{
        			Timestamp val = rs.getTimestamp(columnName);
        			if (rs.wasNull())
        				rowObject.put(columnName, "");
        			else
        				rowObject.put(columnName, val.toString());
        		}
        		else if(columnType == java.sql.Types.DATE) {
        			Date val = rs.getDate(columnName);
        			if (rs.wasNull())
        				rowObject.put(columnName, "");
        			else
        				rowObject.put(columnName, val.toString());
        		}
        		else if(columnType == java.sql.Types.CLOB) {
        			rowObject.put(columnName, rs.getClob(columnName));
        		}
        		else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY || columnType == java.sql.Types.VARBINARY) {
        			rowObject.put(columnName, rs.getBytes(columnName) );
        		}
        		
        		else if(columnType == java.sql.Types.BOOLEAN) {
    				rowObject.put(columnName, rs.getBoolean(columnName));
        		}
        		else {
        			String val = rs.getString(columnName);
        			if (rs.wasNull()) {
        				val = "";
        		    }
        			rowObject.put(columnName, val);
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "QueryExecUtil.buildUpJsonRowsFromResultSet Unknown column type " + columnType + " for: " + columnName);
        		}
        		
            }
        	
        	JSONObject result = new JSONObject(rowObject);
        	rows.add(result.toJSONString());
        	
        }
        resultQueryJsonRows.setResultQueryRows(rows);
		return resultQueryJsonRows;
	}
	
	
	
	
	private static ResultQuery buildUpTableFromResultSet(	final ResultSet rs, 
															ResultQuery ret) throws Exception	{

		ResultSetMetaData metaData = rs.getMetaData();
		int recordsAffected = 0;
		while (rs.next()) {
			recordsAffected++;
        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
        		String columnName = metaData.getColumnName(i);
        		int columnTypeId = metaData.getColumnType(i);
        		String columnTypeName = metaData.getColumnTypeName(i);
        		
        		if(recordsAffected == 1)
    				ret.getResultQueryTable().getHeader().addColumn(i, 
    																new TableColumn(i,
    																				columnName, 
    																				columnTypeId, 
    																				columnTypeName, 
    																				metaData.getPrecision(i), 
    																				metaData.getScale(i)) );

        		TableRow tr = new TableRow();
        		if(columnTypeId == java.sql.Types.NVARCHAR || columnTypeId == java.sql.Types.VARCHAR ) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getNString(columnName), columnTypeId));
        		}
        		if( columnTypeId == java.sql.Types.VARCHAR ) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getString(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.INTEGER || columnTypeId == java.sql.Types.SMALLINT) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(Integer.valueOf(rs.getInt(columnName)), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.BIGINT ) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(Long.valueOf(rs.getLong(columnName)), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.FLOAT) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(Float.valueOf(rs.getFloat(columnName)), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.DECIMAL) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getBigDecimal(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.REAL) {
        			//tr.addCell(Integer.valueOf(i), new TableCell( Float.valueOf(rs.getFloat(columnName)), columnTypeId));
        		}
           		else if(columnTypeId == java.sql.Types.DOUBLE) {
           			//tr.addCell(Integer.valueOf(i), new TableCell( Double.valueOf(rs.getDouble(columnName)), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.TIMESTAMP) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getTimestamp(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.DATE) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getDate(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.CLOB) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getClob(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.NCLOB ) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getNClob(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.BINARY ) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getBytes(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.LONGVARBINARY || columnTypeId == java.sql.Types.VARBINARY){
        			//tr.addCell(Integer.valueOf(i), new TableCell(rs.getBinaryStream(columnName), columnTypeId));
        		}
        		else if(columnTypeId == java.sql.Types.BOOLEAN) {
        			//tr.addCell(Integer.valueOf(i), new TableCell(Boolean.valueOf(rs.getBoolean(columnName)) , columnTypeId));
        		}
        		else {
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Unknown column type " + columnTypeId + " for: " + columnName);
        		}
        		
        		ret.getResultQueryTable().AddTableRow(tr);
            }
        }
		ret.setRecordsAffected(recordsAffected);
		ret.setColumnsAffected(metaData.getColumnCount());
		return ret;
	}
	
	
	
	
	
	
	
	

	public static PreparedStatement buildUpStatementForQuery(	final long queryId, 
																PreparedStatement statement, 
																final ParamObj paramObj) throws Exception {

		List<SqlRepoParam> paramList = SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlRepoParamList();
		if( paramList == null || paramList.isEmpty()) return statement;
		
		for (SqlParameter tempSqlParam : paramObj.getplist()) {
			SqlRepoParam sqlRepoParam = SqlQueryRepoUtils.getSqlRepoParam(paramList, tempSqlParam.getPid());
            assert sqlRepoParam != null;
            if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.stringColumn) == 0)	{
				statement.setString(sqlRepoParam.getDynamicSqlParamOrder(), tempSqlParam.getValue( ));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.floatColumn) == 0)	{
				statement.setFloat(sqlRepoParam.getDynamicSqlParamOrder(), Float.parseFloat(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.decimalColumn) == 0) {
				statement.setBigDecimal(sqlRepoParam.getDynamicSqlParamOrder(), java.math.BigDecimal.valueOf(Long.parseLong(tempSqlParam.getValue()) ));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.doubleColumn) == 0) {
				statement.setDouble(sqlRepoParam.getDynamicSqlParamOrder(), Double.parseDouble(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.realColumn) == 0) {
				statement.setFloat(sqlRepoParam.getDynamicSqlParamOrder(), Float.parseFloat(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.intColumn) == 0) {
				statement.setInt(sqlRepoParam.getDynamicSqlParamOrder(), Integer.parseInt(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.bigintColumn) == 0) {
				statement.setLong(sqlRepoParam.getDynamicSqlParamOrder(), Long.parseLong(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.smallintColumn) == 0)	{
				statement.setShort(sqlRepoParam.getDynamicSqlParamOrder(), Short.parseShort(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.booleanColumn) == 0) {
				statement.setBoolean(sqlRepoParam.getDynamicSqlParamOrder(), Boolean.parseBoolean(tempSqlParam.getValue()));
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.dateColumn) == 0) {
				if(tempSqlParam.getValue() == null || tempSqlParam.getValue().isEmpty())
					statement.setDate(sqlRepoParam.getDynamicSqlParamOrder(), null );
				else
					statement.setDate(sqlRepoParam.getDynamicSqlParamOrder(), Date.valueOf(tempSqlParam.getValue( )) );
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.timestampColumn) == 0)	{
				if(tempSqlParam.getValue() == null || tempSqlParam.getValue().isEmpty())
					statement.setTimestamp(sqlRepoParam.getDynamicSqlParamOrder(), null );
				else
					statement.setTimestamp(sqlRepoParam.getDynamicSqlParamOrder(), Timestamp.valueOf(tempSqlParam.getValue( )) );
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.lobColumn) == 0) {
				statement.setClob(sqlRepoParam.getDynamicSqlParamOrder(), new StringReader(tempSqlParam.getValue()), tempSqlParam.getValue().length());
			}
			else if( sqlRepoParam.getDynamicSqlParamType().toUpperCase().compareTo(SqlColumnType.nameStringColumn) == 0)  {  // the weird case to execute DDL/DML
				statement.setBytes(sqlRepoParam.getDynamicSqlParamOrder(), tempSqlParam.getValue().getBytes());
			}
			else  {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, " Unknown column: " + sqlRepoParam.getDynamicSqlParamType()));
			}
			
		}
		
		return statement;
	}
	

	public static ResultQuery buildUpMetadataFromResultSet(final ResultSet rs) throws Exception	{
		ColumnTypeTable columnTypeTable = new ColumnTypeTable();
		ResultQuery resultQuery = new ResultQuery();
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String colTypeName = columnTypeTable.columnIdToName.get(metaData.getColumnType(i)) ;
			if(colTypeName == null) {	colTypeName = metaData.getColumnTypeName(i);	}
    		ResultMetadata rm = new ResultMetadata(	metaData.getColumnName(i), 
    												metaData.getColumnType(i), 
    												colTypeName, 
    												metaData.getPrecision(i), 
    												metaData.getScale(i));
    		resultQuery.getMetadata().add(rm);
		}
		
		return resultQuery;
	}
	
	
	public static TableFormatMap
	buildUpMetadataWithReturnTableFormat(final ResultSet rs) throws Exception	{
		Map<String, String> m = new HashMap<String, String>();
		Map<String, ResultMetadata> mExt = new HashMap<String, ResultMetadata>();
		
		ColumnTypeTable columnTypeTable = new ColumnTypeTable();
		TableFormatMap ret = new TableFormatMap();
		
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String colTypeName = columnTypeTable.columnIdToName.get(metaData.getColumnType(i)) ;
			if(colTypeName == null) {	colTypeName = metaData.getColumnTypeName(i);	}
			m.put(metaData.getColumnName(i), colTypeName);
    		ResultMetadata rm = new ResultMetadata(	metaData.getColumnName(i), 
    												metaData.getColumnType(i), 
    												colTypeName, 
    												metaData.getPrecision(i), 
    												metaData.getScale(i));
    		mExt.put(metaData.getColumnName(i), rm);
		}
		ret.setExtendedMetadata(mExt);
		ret.setMetadata(m);
		ret.setColCount(m.size());
		return ret;
	}
	
	public static List<ResultMetadata>  buildUpMetadataFromResultSet_(final ResultSet rs) throws Exception	{

		try {
			ColumnTypeTable columnTypeTable = new ColumnTypeTable();
			List<ResultMetadata> ret = new ArrayList<ResultMetadata>();
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String colTypeName = columnTypeTable.columnIdToName.get(metaData.getColumnType(i)) ;
				if(colTypeName == null) {	colTypeName = metaData.getColumnTypeName(i);	}
				ResultMetadata rm = new ResultMetadata(	metaData.getColumnName(i),
						metaData.getColumnType(i),
						colTypeName,
						metaData.getPrecision(i),
						metaData.getScale(i));
				ret.add(rm);
			}

			return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

	}
	
	

	public static ResultQuery execStaticQueryWithCsvOutput(	final DbConnectionInfo connectionDetailInfo, 
															final String query) throws Exception	{

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret = new ResultQuery();
		long hash = HashWrapper.hash64FNV(query);
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpCsvFromResultSet(rs, ret);
			int rows = 0 ;
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			rs.beforeFirst();	
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("csv");
	        ret.setSqlHash(hash);
	        ret.setRecordsAffected(rows);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	

	public static ResultQueryAsList execStaticQueryWithContentList(	final DbConnectionInfo connectionDetailInfo, 
																	final String query) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQueryAsList ret = new ResultQueryAsList();
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			List<ResultMetadata> metadata = buildUpMetadataFromResultSet_(rs);
			ret.setMetadata(metadata);
			ret = buildUpResultQueryAsList(rs, ret);
			int rows = 0 ;
			rs.beforeFirst();
			if (rs.last()) {
			    rows = rs.getRow();
			}	
				
			ret.setRecordsAffected(rows);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	public static ResultQuery execStaticQueryWithTableOutput(	final DbConnectionInfo connectionDetailInfo, 
																final String query) throws Exception	{

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret = new ResultQuery();
		long hash = HashWrapper.hash64FNV(query);
		try {
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpTableFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("csv");
	        ret.setSqlHash(hash);
	        int rows = 0 ;
	        rs.beforeFirst();
			if (rs.last()) {
			    rows = rs.getRow();
			}	
	        ret.setRecordsAffected(rows);;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	

	public static ResultQuery execStaticQueryWithJsonOutput(final DbConnectionInfo connectionDetailInfo, 
															final String query) throws Exception {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret ;
		long hash = HashWrapper.hash64FNV(query);
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpJsonFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("json");
	        ret.setSqlHash(hash);
	        int rows = 0 ;
	        rs.beforeFirst();
			if (rs.last()) {
			    rows = rs.getRow();
			}	
	        ret.setRecordsAffected(rows);;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	
	public static ResultQueryJsonRows execStaticQueryWithJsonRows(	final DbConnectionInfo connectionDetailInfo, 
																	final String query) throws Exception	{

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQueryJsonRows ret = new ResultQueryJsonRows();
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			ResultQuery resultQuery = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret.setMetadata(resultQuery.getMetadata());
			return SqlQueryExecUtils.buildUpJsonRowsFromResultSet(rs, ret);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
	}
	
	
	
	

	public static TableFormatMap
	execStaticQueryWithTable(	final DbConnectionInfo connectionDetailInfo, 
								final String query) throws Exception	{
		TableFormatMap ret;
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			ret = SqlQueryExecUtils.buildUpMetadataWithReturnTableFormat(rs);
			return SqlQueryExecUtils.buildUpJsonFromMigrationReturn(rs, ret);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
	}
	
	

	public static TableFormatMap 
	execStaticQueryWithTableNoRs(	final DbConnectionInfo connectionDetailInfo, 
									final String query) throws Exception	{
		TableFormatMap ret;
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			ret = SqlQueryExecUtils.buildUpMetadataWithReturnTableFormat(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromMigrationReturn(	rs, ret);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}

		return ret;
	}
	
	
	public static ResultQuery execStaticQuery(	final DbConnectionInfo connectionDetailInfo, 
												final String query) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret;
		long hash = HashWrapper.hash64FNV(query);

		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);

			ret = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromResultSet(rs, ret);
			ret.setOutputPackaging("plain");
			ret.setOutputFormat("json");
			ret.setSqlHash(hash);

			int rows = 0 ;
			rs.beforeFirst();
			if (rs.last()) {
				rows = rs.getRow();
			}
			ret.setRecordsAffected(rows);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		return ret;
	}

	public static ResultQuery execStaticQuery(	final Connection connection, 
												final String query) throws Exception	{
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret;
		long hash = HashWrapper.hash64FNV(query);
		
		try	{
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			
			ret = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromResultSet(rs, ret);
			ret.setOutputPackaging("plain");
			ret.setOutputFormat("json");
			ret.setSqlHash(hash);
			
			int rows = 0 ;
			rs.beforeFirst();
			if (rs.last()) {
				rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(connection, statement, rs);
		}
		return ret;
			
	}
	
	
	
	public static ResultQuery execStaticQueryWithStore(	final DbConnectionInfo connectionDetailInfo, 
														final String query,
														final String storeBackupType,
														final String clusterUniqueName,
														final String databaseName,
														final String collectionName	) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret;
		long hash = HashWrapper.hash64FNV(query);
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			
			ret = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret = SqlQueryExecUtils.buildUpJsonFromResultSet(rs, ret);
			ret.setOutputPackaging("plain");
			ret.setOutputFormat("json");
			ret.setSqlHash(hash);
			int rows = 0 ;
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			rs.beforeFirst();	
			ret.setRecordsAffected(rows);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	
	
	

	public static void execStaticQuery_(final DbConnectionInfo connectionDetailInfo, 
										final String query) throws Exception	{
		Connection conn = null;
		Statement statement = null;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			statement.executeQuery(query);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
	}
	
	
	/**
	 * Executes a query string provided as parameter.  
	 * This version is better as it may prevent SQL Injection. 
	 * @param  connectionDetailInfo   a DbConnectionInfo object
	 * @param  queryId an integer representing query id
	 * @param  paramObjJson a string that is in JSON format
	 * @return ResultQuery object
	 * 
	 */
	public static ResultQuery execDynamicQueryWithCsvOutput(final DbConnectionInfo connectionDetailInfo, 
															final int queryId, 
															final String paramObjJson) throws Exception	{
		try	{
			Gson g = new Gson(); 
			ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
			return execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObj);
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	/**
	 * Executes a query string provided as parameter.  
	 * This version is better as it may prevent SQL Injection. 
	 *
	 */
	public static ResultQuery execDynamicQueryWithCsvOutput(	final DbConnectionInfo connectionDetailInfo, 
																final int queryId, 
																final ParamObj paramObj) throws Exception {

		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ResultQuery ret;
		
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			long hash = HashWrapper.hash64FNV(preparedQuery);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement =  buildUpStatementForQuery(	queryId,
														conn.prepareStatement(preparedQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY),
														paramObj);
		    rs = statement.executeQuery();
			ret = buildUpCsvFromResultSet(rs, buildUpMetadataFromResultSet(rs));
			int rows = 0 ;
			rs.beforeFirst();
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("csv");
	        ret.setSqlHash(hash);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	
	public static ResultQueryJsonRows execDynamicQueryWithJsonRows(	final DbConnectionInfo connectionDetailInfo, 
																	final int queryId, 
																	final ParamObj paramObj) throws Exception	{

		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ResultQueryJsonRows ret = new ResultQueryJsonRows();
		
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.prepareStatement(preparedQuery);
			statement =  buildUpStatementForQuery(queryId, statement, paramObj);
		    rs = statement.executeQuery();
		    ResultQuery resultQuery = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret.setMetadata(resultQuery.getMetadata());
			return SqlQueryExecUtils.buildUpJsonRowsFromResultSet(rs, ret);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
	}
	
	
	public static ResultQueryAsList execDynamicQueryAsObjectList(	final DbConnectionInfo connectionDetailInfo, 
																	final int queryId, 
																	final ParamObj paramObj) throws Exception	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ResultQueryAsList ret = new ResultQueryAsList();
		
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.prepareStatement(preparedQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement =  buildUpStatementForQuery(queryId, statement, paramObj);
			rs = statement.executeQuery();
			ResultQuery resultQuery = SqlQueryExecUtils.buildUpMetadataFromResultSet(rs);
			ret.setMetadata(resultQuery.getMetadata());
			ret = SqlQueryExecUtils.buildUpResultQueryAsList(rs, ret);
			rs.beforeFirst();
			int rows = 0 ;
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	
	public static ResultQuery execDynamicQueryWithTableOutput(	final DbConnectionInfo connectionDetailInfo, 
																final int queryId, 
																final ParamObj paramObj) throws Exception {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ResultQuery ret ;
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			long hash = HashWrapper.hash64FNV(preparedQuery);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.prepareStatement(preparedQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement =  buildUpStatementForQuery(queryId, statement, paramObj);
		    rs = statement.executeQuery();
		    ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpTableFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("csv");
	        ret.setSqlHash(hash);
	        rs.beforeFirst();	
	        int rows = 0 ;
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	
	public static ResultQuery resultSetToResultQueryWithCsvOutput(ResultSet rs) throws Exception {
		ResultQuery ret;
		try	{
		    ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpCsvFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("csv");
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	
	public static ResultQuery resultSetToResultQueryWithTableOutput(ResultSet rs) throws Exception {
		ResultQuery ret;
		try	{
		    ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpTableFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("csv");
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e,Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	
	public static ResultQuery resultSetToResultQueryWithJsonOutput(ResultSet rs) throws Exception {
		ResultQuery ret;
		try	{
			ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpJsonFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("json");
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	

	public static ResultSet execDynamicQuery(	final DbConnectionInfo connectionDetailInfo, 
												final int queryId, 
												final ParamObj paramObj) throws Exception	{

		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement =  buildUpStatementForQuery(queryId, conn.prepareStatement(preparedQuery), paramObj);
		    rs = statement.executeQuery();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		
		return rs;
	}
	

	public static ResultQuery execDynamicQueryWithJsonOutput(	final DbConnectionInfo connectionDetailInfo, 
																final int queryId, 
																final String paramObjJson) throws Exception {
		ResultQuery ret;
		try	{
			Gson g = new Gson(); 
			ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
			ret =  execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObj);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	
	
	
	

	public static ResultQuery execDynamicQueryWithJsonOutput(	final DbConnectionInfo connectionDetailInfo, 
																final int queryId, 
																final ParamObj paramObj) throws Exception {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ResultQuery ret ;
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.prepareStatement(preparedQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement =  buildUpStatementForQuery(queryId, statement, paramObj);
			rs = statement.executeQuery();
			ret = buildUpMetadataFromResultSet(rs);
			ret = buildUpJsonFromResultSet(rs, ret);
	        ret.setOutputPackaging("plain");
	        ret.setOutputFormat("json");
	        int rows = 0 ;
	        rs.beforeFirst();
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, rs);
		}
		
		return ret;
	}
	
	

	public static int execDynamicDml(	final DbConnectionInfo connectionDetailInfo, 
										final long queryId, 
										final ParamObj paramObj) throws Exception {

		Connection conn = null;
		PreparedStatement statement = null;
		int recordsAffected = 0;
		
		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement =  buildUpStatementForQuery(queryId, conn.prepareStatement(preparedQuery), paramObj);
			recordsAffected = statement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
		
		
		return recordsAffected;
	}
	
	
	
	
	

	public static DmlBulkAffectedParam execDynamicDmlBulkCommit(final DbConnectionInfo connectionDetailInfo, 
																final long queryId, 
																final ParamListObj paramObjList, 
																final int batchSize) throws Exception {

		Connection conn = null;
		PreparedStatement statement = null;
		DmlBulkAffectedParam ret = new DmlBulkAffectedParam(queryId, 0, new ArrayList<Integer>());
		try	{
			
			if(!paramObjList.getplistlist().isEmpty() && paramObjList.getplistlist().get(0) == null)
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "No elements found in input parameters")) ;

			
			
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObjList.getplistlist().get(0));
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			conn.setAutoCommit(false);
			PreparedStatement preStatement = conn.prepareStatement(preparedQuery);
			ret.setDynamicSqlId(queryId);

			int batchSizeTemp = 0;
			for (ParamObj tempParamObj : paramObjList.getplistlist())    {
				statement =  buildUpStatementForQuery(queryId, preStatement, tempParamObj);
				batchSizeTemp++;
				statement.addBatch();
				if(batchSizeTemp >= batchSize)	{
					batchSizeTemp = 0;
					int[] recordsAffectedArray = statement.executeBatch();
					for (int currentRecordsAffected : recordsAffectedArray)	{
						ret.addIndividualRecordAffected(currentRecordsAffected);
						ret.setGrandTotalRecordsAffected(ret.getGrandTotalRecordsAffected() + currentRecordsAffected);
					}
				}
	        }
			
			conn.commit(); // commit the entire set of data

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			if(conn!=null)	conn.setAutoCommit(true);
			closeDbHandles(conn, statement, null);
		}
		
		
		return ret;
	}
	
	
	

	public static DmlBatchAffectedParam execDynamicDmlBatchCommit(	final DbConnectionInfo connectionDetailInfo, 
																	final long queryId,
																	final ParamListObj paramObjList,
																	final int batchSize) throws Exception {
		Connection conn = null;
		PreparedStatement statement = null;
		List<ParamObj> tempRet = new ArrayList<ParamObj>();
		DmlBatchAffectedParam ret = new DmlBatchAffectedParam();
		
		try	{
			if(!paramObjList.getplistlist().isEmpty() && paramObjList.getplistlist().get(0) == null)
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "No elements found in input parameters")) ;

			
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObjList.getplistlist().get(0));
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			conn.setAutoCommit(false);
			PreparedStatement preStatement = conn.prepareStatement(preparedQuery);
			ret.setDynamicSqlId(queryId);

			int batchSizeTemp = 0;
			for (int count = 0; count < paramObjList.getplistlist().size(); count++) {
				ParamObj tempParamObj = paramObjList.getplistlist().get(count);
				statement =  buildUpStatementForQuery(queryId, preStatement, tempParamObj);
				batchSizeTemp++;
				statement.addBatch();
				tempRet.add(tempParamObj);
				if( batchSizeTemp >= batchSize  || count == paramObjList.getplistlist().size() - 1)	{
					try	{
						batchSizeTemp = 0;
						int[] recordsAffectedArray = statement.executeBatch();
						for (int currentRecordsAffected : recordsAffectedArray)
							ret.setGrandTotalRecordsAffected(ret.getGrandTotalRecordsAffected() + currentRecordsAffected);
						
						conn.commit();
						tempRet.clear();
					}
					catch(SQLException e) {
						conn.rollback();
						ret.addParamsNotCommited(tempRet);
						tempRet.clear();
					}
				}
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e,Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally {
			if(conn!=null)
				conn.setAutoCommit(true);
			closeDbHandles(conn, statement, null);
		}
		
		
		return ret;
	}
	
	
	public static DmlBatchAffectedParam execStaticDmlBatchCommit(	final DbConnectionInfo connectionDetailInfo, 
																	final long queryId, 
																	final ParamListObj paramObjList, 
																	final int batchSize) throws Exception {
		Connection conn = null;
		Statement statement = null;
		List<ParamObj> tempRet = new ArrayList<>();
		DmlBatchAffectedParam ret = new DmlBatchAffectedParam();
		
		try	{
			if(!paramObjList.getplistlist().isEmpty() && paramObjList.getplistlist().get(0) == null)
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "No elements found in input parameters")) ;

			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObjList.getplistlist().get(0));
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			conn.setAutoCommit(false);
			statement = conn.prepareStatement(preparedQuery);
			ret.setDynamicSqlId(queryId);

			int batchSizeTemp = 0;
			for (int count = 0; count < paramObjList.getplistlist().size(); count++) {
				ParamObj tempParamObj = paramObjList.getplistlist().get(count);
				statement = conn.createStatement();
				String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(queryId, tempParamObj);
				batchSizeTemp++;
				statement.addBatch(sqlCompiledString);
				tempRet.add(tempParamObj);
				if( batchSizeTemp >= batchSize  || count == paramObjList.getplistlist().size() - 1)	{
					try	{
						batchSizeTemp = 0;
						int[] recordsAffectedArray = statement.executeBatch();
						for (int currentRecordsAffected : recordsAffectedArray)
							ret.setGrandTotalRecordsAffected(ret.getGrandTotalRecordsAffected() + currentRecordsAffected);
						
						conn.commit();
						tempRet.clear();
					}
					catch(SQLException e) {
						conn.rollback();
						ret.addParamsNotCommited(tempRet);
						tempRet.clear();
					}
				}
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			if(conn!=null)
				conn.setAutoCommit(true);
			closeDbHandles(conn, statement, null);
		}
		
		
		return ret;
	}
	

	public static int execDynamicDml(	final DbConnectionInfo connectionDetailInfo, 
										final int queryId, 
										final String paramObjJson) throws Exception {

		Connection conn = null;
		PreparedStatement statement = null;
		int recordsAffected = 0;
		
		try	{
			Gson g = new Gson(); 
			ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement =  buildUpStatementForQuery(queryId, conn.prepareStatement(preparedQuery), paramObj);
			recordsAffected = statement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
		
		
		return recordsAffected;
	}
	
	

	public static int execStaticDml(final DbConnectionInfo connectionDetailInfo, 
									final String query) throws Exception {

		Connection conn = null;
		Statement statement = null;
		int recordsAffected;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			recordsAffected = statement.executeUpdate(query);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
		
		
		return recordsAffected;
	}
	
	
	public static int execStaticDml(final Connection conn, 
									final String query) throws Exception {

		Statement statement = null;
		int recordsAffected = 0;
		
		try	{
			statement = conn.createStatement();
			recordsAffected = statement.executeUpdate(query);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally {
			closeDbHandles(conn, statement, null);
		}
			
		
		return recordsAffected;
	}
	
	
	
	public static boolean execStaticDdl(final DbConnectionInfo connectionDetailInfo, 
										final String query) throws Exception {
		Connection conn = null;
		Statement statement = null;

		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement();
			return true;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
	}
	
	public static boolean execStaticDdl(	final Connection conn, 
											final String query) throws Exception {

		Statement statement = null;

		try	{
			statement = conn.createStatement();
			statement.executeUpdate(query);
			return true;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
		
	}
	
	
	public static boolean execDynamicDdl(	final DbConnectionInfo connectionDetailInfo, 
											final long queryId, 
											final String paramObjJson) throws Exception	{
		Connection conn = null;
		PreparedStatement statement = null;

		try	{
			Gson g = new Gson(); 
			ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement =  buildUpStatementForQuery(queryId, conn.prepareStatement(preparedQuery), paramObj);
			return true;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null);
		}
	}
	
	
	
	
	
	
	// Push notification executions via websockets
	public static void execDynamicQueryWithWebsocket(	final DbConnectionInfo connectionDetailInfo, 
														final long queryId, 
														final String sqlName,
														final ParamObj paramObj,
														final String httpSession,
														final String requestId,
														final String user,
														final long userId,
														final String persist,
														final String jobId) throws Exception	{
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection conn = null;

		try	{
			String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement =  buildUpStatementForQuery(queryId, conn.prepareStatement(preparedQuery), paramObj);
		    rs = statement.executeQuery();
		    long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch();
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				ResultQuery resultQuery = buildUpJsonFromResultSet(rs, buildUpMetadataFromResultSet(rs));
				resultQuery.setOutputPackaging("plain");
				resultQuery.setOutputFormat("json");
				
				String fName = timestamp + "_" + sqlName;
				String folder = "./snapshots/" + String.valueOf(userId);  
				FileUtilWrapper.overwriteFile(folder, fName, resultQuery.toString());
		
				SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fName, sqlName, "ResultQuery", userId, timestamp, preparedQuery);
				SnapshotDbRepo snapshotDbRepo = new SnapshotDbRepo();
				snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
				long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
				snapshotDbRepo.addSnapshotDbAccess(id, userId);
			}
			
		    streamRsToWebsocket(rs,user, requestId,httpSession,jobId);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally {
			closeDbHandles(conn, statement, rs) ;
		}

	}
	
	// Push notification executions via websockets
	public static void execStaticQueryWithWebsocket(final DbConnectionInfo connectionDetailInfo, 
													final String query,
													String sqlName,
													final String httpSession,
													final String requestId,
													final String user,
													final long userId,
													final String persist,
													final String jobId) throws Exception {
		Statement  statement = null;
		ResultSet rs = null;
		Connection conn = null;

		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(query);
			int rows = 0 ;
			if (rs.last()) {
			    rows = rs.getRow();
			}	
			rs.beforeFirst();	
			
			long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
			if(sqlName == null || sqlName.isEmpty())
				sqlName = timestamp + "-" + userId;
		    
			ResultQuery resultQuery;
			resultQuery = buildUpMetadataFromResultSet(rs);
            buildUpJsonFromResultSet(rs, resultQuery);
            resultQuery.setOutputPackaging("plain");
			resultQuery.setOutputFormat("json");
			resultQuery.setRecordsAffected(rows);
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				String folder = "./snapshots/" + userId;
				try {
					boolean isOk = FileUtilWrapper.overwriteFile(folder, sqlName, resultQuery.toString());
					if(isOk) {
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	sqlName, sqlName, "ResultQuery", userId, timestamp, query);
						SnapshotDbRepo snapshotDbRepo = new SnapshotDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, userId);
					}
				} catch(Exception ex) {
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
				}
			}
		    streamRsToWebsocket(rs, user, httpSession,requestId, jobId);
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally {
			closeDbHandles(conn, statement, rs) ;
		}

	}
	
	
	
	public static void streamRsToWebsocket(	final ResultSet rs,
											final String user,
											final String toSession,
											final String requestId,
											final String jobId) throws Exception {

		ColumnTypeTable columnTypeTable = new ColumnTypeTable();
		List<ResultMetadata> metadata = new ArrayList<>() ;
		ResultSetMetaData rsMetaData = rs.getMetaData();
		for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
		String colTypeName = columnTypeTable.columnIdToName.get(rsMetaData.getColumnType(i)) ;
		if(colTypeName == null) {	colTypeName = rsMetaData.getColumnTypeName(i);	}
			ResultMetadata rm = new ResultMetadata(	rsMetaData.getColumnName(i), 
							rsMetaData.getColumnType(i), 
							colTypeName, 
							rsMetaData.getPrecision(i), 
							rsMetaData.getScale(i));
			metadata.add(rm);
		}
		WebsocketPayload wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.headerSql, metadata, ClusterDb.ownBaseUrl);
		WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
		rs.beforeFirst();
		
		int i = 0;
		while (rs.next()) {
			List<Object> row = new ArrayList<Object>();
			for (i = 1; i <= rsMetaData.getColumnCount(); i++) {
				String columnName = rsMetaData.getColumnName(i);
				int columnType = rsMetaData.getColumnType(i);
				
				if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
					row.add(rs.getString(columnName));
				}
				else if(columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.SMALLINT) {
					row.add(rs.getInt(columnName));
				}
				else if(columnType == java.sql.Types.BIGINT ) {
					row.add(rs.getLong(columnName));
				}
				else if(columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DECIMAL|| columnType == java.sql.Types.REAL)	{
					row.add(rs.getFloat(columnName));
				}
				else if(columnType == java.sql.Types.DOUBLE) {
					row.add(rs.getDouble(columnName));
				}
				else if(columnType == java.sql.Types.TIMESTAMP) {
					row.add(rs.getTimestamp(columnName).toString() );
				}
				else if(columnType == java.sql.Types.DATE) {
					row.add(rs.getDate(columnName).toString());
				}
				else if(columnType == java.sql.Types.NCLOB)	{
					row.add(rs.getClob(columnName) );
				}
				else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY || columnType == java.sql.Types.VARBINARY) {
					row.add(rs.getBytes(columnName));
				}
				else if(columnType == java.sql.Types.BOOLEAN) {
					row.add(rs.getBoolean(columnName));
				}
				else {
					row.add( rs.getString(columnName));
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Unknown column type " + columnType + " for: " + columnName);
				}
			}
			//String jsonStrRow = new Gson().toJson(row);
			wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.detailSql, row, ClusterDb.ownBaseUrl);
			WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
		}
		wsPayload = new WebsocketPayload(requestId, user, user, WebsocketMessageType.footerSql, String.valueOf(i), ClusterDb.ownBaseUrl);
		WebSocketsWrapper.sendSingleMessageToUserFromServer(wsPayload);
	}
		
	
	
		
	
	public static RecordsAffected 
	insertBulkIntoTable(	final DbConnectionInfo connectionDetailInfo,
							final String schemaName,
							final String tableName,
							final List<Map<String, Object>> rows, 
							final Map<String, String> metadata) throws Exception {
		Connection conn = null;
		Statement statement = null;
		RecordsAffected ret = new RecordsAffected("INSERT", 0, 0);
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			String sqlStm = SqlMetadataWrapper.generateInsertTableStm(metadata, schemaName, tableName);
			int recordsAffected = 0;
			for(Map<String, Object> row: rows) {
				String execInsert = SqlMetadataWrapper.generateExecutableInsertTableStm(sqlStm,metadata,row);
				statement = conn.createStatement();
				try {
					recordsAffected = statement.executeUpdate(execInsert);	
					ret.addRecAffected(recordsAffected);
				} catch(Exception ex) {
					ret.incrementRecFailed();
					ret.setMessage(ex.getMessage());
				}
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null) ;
		}
		return ret;
	}
	

	public static RecordsAffected 
	insertBulkIntoTable_v2(	final DbConnectionInfo connectionDetailInfo,
							final String schemaName,
							final String tableName,
							final List<Map<String, Object>> rows, 
							final Map<String, String> metadata) throws Exception {
		Connection conn = null;
		Statement statement = null;
		RecordsAffected ret = new RecordsAffected("INSERT", 0, 0);
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			String sqlStm = SqlMetadataWrapper.generateInsertTableStatement(metadata, schemaName, tableName);
			int recordsAffected = 0;
			for(Map<String, Object> row: rows) {
				String execInsert = SqlMetadataWrapper.generateExecutableInsertTableStm(sqlStm,metadata,row);
				statement = conn.createStatement();
				try {
					recordsAffected = statement.executeUpdate(execInsert);	
					ret.addRecAffected(recordsAffected);
				} catch(Exception ex) {
					ret.incrementRecFailed();
					ret.setMessage(ex.getMessage());
				}
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null) ;
		}
		return ret;
	}
	
	
	public static RecordsAffected 
	insertBulkIntoTable_v2(	final Connection conn,
							final String schemaName,
							final String tableName,
							final List<Map<String, Object>> rows, 
							final Map<String, String> metadata) throws Exception	{


		Statement statement = null;
		RecordsAffected ret = new RecordsAffected("INSERT", 0, 0);
		try	{
			String sqlStm = SqlMetadataWrapper.generateInsertTableStatement(metadata, schemaName, tableName);
			int recordsAffected = 0;
			for(Map<String, Object> row: rows) {
				String execInsert = SqlMetadataWrapper.generateExecutableInsertTableStm(sqlStm,metadata,row);
				statement = conn.createStatement();
				try {
					recordsAffected = statement.executeUpdate(execInsert);	
					ret.addRecAffected(recordsAffected);
				} catch(Exception ex) {
					ret.incrementRecFailed();
					ret.setMessage(ex.getMessage());
				}
			}
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeDbHandles(conn, statement, null) ;
		}
		return ret;
	}





	
}
	
	