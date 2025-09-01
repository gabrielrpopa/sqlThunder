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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import com.widescope.persistence.Persistence;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.*;
import com.widescope.sqlThunder.utils.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableList;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.utils.PayloadCompression;
import com.fasterxml.jackson.databind.ObjectMapper;




public final class SqlQueryRepoUtils{
	
	private static final Logger LOG = LogManager.getLogger(SqlQueryRepoUtils.class);
	
	
	
	public 
	static 
	String 
	compileQueryFromLocalRepo(	final long queryId, 
								final ParamObj paramObj) throws Exception	{
		String ret;
		
		try	{
			ret = SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlContent();
			for (SqlRepoParam tempSqlRepoParam : SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(queryId)).getSqlRepoParamList()) {
				String paramValue = paramObj.getSqlParam(tempSqlRepoParam.getDynamicSqlParamId()).getValue();
				String paramName = tempSqlRepoParam.getDynamicSqlParamName();
				String paramType = tempSqlRepoParam.getDynamicSqlParamType();
				if(paramValue != null)
					if(paramType.compareTo(SqlColumnType.stringColumn) == 0 
						|| paramType.compareTo(SqlColumnType.dateColumn) == 0 
						|| paramType.compareTo(SqlColumnType.timestampColumn) == 0 
						|| paramType.compareTo(SqlColumnType.lobColumn) == 0 
						|| paramType.compareTo(SqlColumnType.booleanColumn) == 0) /*QUOTED COLUMNS*/
					{
						ret = ret.replace(tempSqlRepoParam.getDynamicSqlParamName(), "'" + paramValue + "'");
					}
					else if(paramType.compareTo(SqlColumnType.floatColumn) == 0 
							|| paramType.compareTo(SqlColumnType.doubleColumn) == 0 
							|| paramType.compareTo(SqlColumnType.decimalColumn) == 0
							|| paramType.compareTo(SqlColumnType.realColumn) == 0
							|| paramType.compareTo(SqlColumnType.intColumn) == 0
							|| paramType.compareTo(SqlColumnType.bigintColumn) == 0
							|| paramType.compareTo(SqlColumnType.smallintColumn) == 0
							|| paramType.compareTo(SqlColumnType.nameStringColumn) == 0) /*UNQUOTED COLUMNS*/ {
						// Avoid SQL injection...
						CheckForSqlInjectionForNumericValues(paramValue, paramType );
						// Replace String safely
						ret = ret.replace(tempSqlRepoParam.getDynamicSqlParamName(), paramValue);
					}
					else  {
						ret = ret.replace(tempSqlRepoParam.getDynamicSqlParamName(), paramValue);
						AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "type param issue: " + paramType + " -> please look into it!!!");
					}
				
				else {
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Param Value is null");
				}
	        }
			
		}
		catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}


		return ret;
	}
	
	
	public 
	static 
	void 
	CheckForSqlInjectionForNumericValues(	final String paramValue, 
											final String paramType ) throws Exception, NumberFormatException {
		try
		{
			if(paramType.compareTo(SqlColumnType.floatColumn) == 0)	{
				Float.parseFloat(paramValue);
			}
			else if(paramType.compareTo(SqlColumnType.doubleColumn) == 0) {
				Double.parseDouble(paramValue);
			}
			else if(paramType.compareTo(SqlColumnType.decimalColumn) == 0) {
				Double.parseDouble(paramValue);
			}
			else if (paramType.compareTo(SqlColumnType.realColumn) == 0) {
				Long.parseLong(paramValue);
			}
			else if(paramType.compareTo(SqlColumnType.intColumn) == 0) {
				Integer.parseInt(paramValue);
			}
			else if(paramType.compareTo(SqlColumnType.bigintColumn) == 0) {
				Long.parseLong(paramValue);
			}
			else if(paramType.compareTo(SqlColumnType.smallintColumn) == 0) {
				Short.parseShort(paramValue);
			}
			else if (paramType.compareTo(SqlColumnType.nameStringColumn) == 0) {
				if( paramValue.contains(" ") ) throw new Exception("it contains spaces, possibility of SQL injection ");
			}
			else {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Unknown Column Type")) ;
			}
		
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		
		
	}
	
	
	
	public static String compileQueryForPreparedStatementFromLocalRepo(	final long queryId, 
																		final ParamObj paramObj) throws Exception {
		String ret;
		
		try	{
			ret = SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlContent();
			
			for (SqlRepoParam tempSqlRepoParam : SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlRepoParamList()) {
				String paramValue = paramObj.getSqlParam(tempSqlRepoParam.getDynamicSqlParamId()).getValue();
				String paramName = tempSqlRepoParam.getDynamicSqlParamName();
				
				if(paramValue != null)
					ret = ret.replace(tempSqlRepoParam.getDynamicSqlParamName(), "?");
				else {
					throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Param " + paramName + " value is null")) ;
				}
	        }
		}
		catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}

		return ret;
	}
	
	
	public 
	static 
	String 
	compileQueryFromLocalRepo(	final int queryId, 
								final String ParamObjJson) throws Exception{
		String ret;
		try	{
			Gson g = new Gson(); 
			ParamObj paramObj = g.fromJson(ParamObjJson, ParamObj.class);
			ret = compileQueryFromLocalRepo(queryId, paramObj);
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		return ret;
	}
	
	
	
	public 
	static 
	String 
	compileQueryForPreparedStatementFromLocalRepo(	final int queryId, 
													final String ParamObjJson) throws Exception {
		String ret;
		try	{
			Gson g = new Gson(); 
			ParamObj paramObj = g.fromJson(ParamObjJson, ParamObj.class);
			ret = compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		return ret;
	}
	
	
	
	public 
	static 
	ResultQuery 
	execStaticQuery(	final String uniqueSchemaName, 
						final String staticQuery, 
						final String outputType) throws Exception	{

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);

		ResultQuery ret ;
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execStaticQueryWithCsvOutput(connectionDetailInfo, staticQuery);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execStaticQueryWithJsonOutput(connectionDetailInfo, staticQuery);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error execStaticQuery, no known document format ")) ;

		return ret;
	}
	
	public 
	static 
	ResultQueryAsList 
	execStaticQueryWithJsonListRows(final String uniqueSchemaName, 
									final String staticQuery ) throws Exception	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQueryWithContentList(connectionDetailInfo, staticQuery);
	}
	
	public 
	static 
	ResultQueryAsList 
	execStaticQueryAsList(	final String uniqueSchemaName, 
							final String staticQuery ) throws Exception	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQueryWithContentList(connectionDetailInfo, staticQuery);
	}
	
	
	public 
	static 
	void 
	execStaticQueryToWebsocket(final String uniqueSchemaName,
							   final String staticQuery,
							   final String sqlName,
							   final User u,
							   final String jobId,
							   final String requestId,
							   final String httpSession,
							   final String persist,
							   final String comment,
							   final long groupId,
							   final PersistenceWrap pWrap) throws Exception	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);

		SqlQueryExecUtils.execStaticQueryWithWebsocket(	connectionDetailInfo, 
														staticQuery,
														sqlName,
														httpSession,
														requestId,
														u,
														groupId,
														persist,
														jobId,
														comment,
														pWrap);
		
	}
	
	public
	static
	ResultQuery
	execStaticQueryWithResultSet(	final Integer queryId,
									final String uniqueSchemaName,
									final String staticQuery) throws Exception	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQuery(connectionDetailInfo, staticQuery);
	}
	
	
	
	public 
	static 
	ResultQueryJsonRows 
	execStaticQueryWithJsonRows(	final String uniqueSchemaName, 
									final String staticQuery) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQueryWithJsonRows(connectionDetailInfo, staticQuery);
	}
	
	
	public 
	static 
	ResultQuery 
	execStaticQueryWithResultSet(	final String uniqueSchemaName, 
									final String staticQuery) throws Exception {
	
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQuery(connectionDetailInfo, staticQuery);
	}
	
	public 
	static 
	TableFormatMap 
	execStaticQueryWithTableFormatMap(	final String uniqueSchemaName, 
										final String staticQuery) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQueryWithTableNoRs(connectionDetailInfo, staticQuery);
	}
	
	
	
	
	public 
	static 
	ResultQuery 
	execStaticQuery(	final Integer queryId, 
						final String uniqueSchemaName, 
						final String staticQuery, 
						final String outputType, 
						final int databaseId,
						final int schemaId) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);

		ResultQuery ret;
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execStaticQueryWithCsvOutput(connectionDetailInfo, staticQuery);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execStaticQueryWithJsonOutput(connectionDetailInfo, staticQuery);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error execStaticQuery, no known document format ")) ;

		return ret;
	}
	
	public static void execDynamicQueryToWebsocket(final long queryId,
												   final String uniqueSchemaName,
												   final String sqlName,
												   final ParamObj paramObj,
												   final String httpSession,
												   final String jobId,
												   final User u,
												   final long groupId,
												   final String persist,
												   final String requestId,
												   final String comment,
												   final PersistenceWrap pWrap) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		if(db == null) {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Schema Name cannot be found")) ;
		}
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		SqlQueryExecUtils.execDynamicQueryWithWebsocket(connectionDetailInfo,
														db.getDatabaseId(),
														queryId,
														sqlName,
														paramObj,
														httpSession,
														requestId,
														u,
														groupId,
														persist,
														jobId,
														comment,
														pWrap
														);
	}
	
	
	public static ResultQuery validateStaticQuery(	final String staticQuery, 
													final String uniqueSchemaName) throws Exception	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticQueryWithJsonOutput(connectionDetailInfo, staticQuery);
	}
	

	public static ResultQuery execStaticQuery(	final long queryId,
												final String staticQuery, 
												final String outputType, 
												final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret ;
		
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execStaticQueryWithCsvOutput(connectionDetailInfo, staticQuery);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execStaticQueryWithJsonOutput(connectionDetailInfo, staticQuery);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format ")) ;

		return ret;
	}
	
	

	public static ResultQuery execDynamicQuery(	final long queryId,
												final String uniqueSchemaName, 
												final String paramObjJson, 
												final String outputType) throws Exception {
		ResultQuery ret;
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObjJson);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObjJson);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format ")) ;

		
		return ret;
	}
	
	
	public static ResultQuery execDynamicQuery(	final long queryId,
												final String uniqueSchemaName, 
												final String paramObjJson, 
												final String outputType, 
												final int databaseId, 
												final int schemaId) throws Exception {
		ResultQuery ret;
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObjJson);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObjJson);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format")) ;

		return ret;
	}
	
	
	
	public static ResultQuery execDynamicQuery(	final long queryId,
												final String paramObjJson, 
												final String outputType, 
												final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret;
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObjJson);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObjJson);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format ")) ;

		return ret;
	}
	
	
	
	public static int execDynamicQueryAndTransferData(	final long queryId,
														final String sourceConnectionName,
														final String destinationConnectionName, 
														final String destinationSchema,
														final String destinationTable,
														final ParamObj paramObj) throws Exception {

		PreparedStatement statementSource;
		ResultSet rsSource;
		Connection connSource ;

		//Source
		SqlRepoDatabase dbSource = SqlRepoUtils.sqlRepoDatabaseMap.get(sourceConnectionName);
		if(dbSource == null) {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Schema Name provided is wrong")) ;
		}
		DbConnectionInfo connectionDetailInfoSource = DbConnectionInfo.makeDbConnectionInfo(dbSource);
		
		//Destination
		SqlRepoDatabase dbDestination = SqlRepoUtils.sqlRepoDatabaseMap.get(destinationConnectionName);
		if(dbDestination == null) {
			throw new Exception("Schema Name provided is wrong");
		}
		DbConnectionInfo connectionDetailInfoDestination = DbConnectionInfo.makeDbConnectionInfo(dbDestination);
		
		String preparedQuery = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(queryId, paramObj);
		
		Class.forName(connectionDetailInfoSource.getJdbcDriver());
		connSource = DriverManager.getConnection(	connectionDetailInfoSource.getDbUrl(),
													connectionDetailInfoSource.getUserName(), 
													connectionDetailInfoSource.getPassword());
		
		statementSource =  SqlQueryExecUtils.buildUpStatementForQuery(queryId, connSource.prepareStatement(preparedQuery), paramObj);
	    rsSource = statementSource.executeQuery();
	    
	    List<ResultMetadata> metadataSource = SqlQueryExecUtils.buildUpMetadataFromResultSet_(rsSource);
	    
	    DdlDmlUtils.createTable(metadataSource, connectionDetailInfoDestination.getDbType(), connectionDetailInfoDestination, destinationSchema, destinationTable);
		return DdlDmlUtils.insertTable(	rsSource, 								/*ResultSet rsSource*/
										destinationConnectionName,  			/*String rdbmsConnectionName*/
										destinationSchema, 						/*String rdbmsSchema*/
										destinationTable 						/*String rdbmsTable*/
										);
	    


	}
	
	
	
	public static ResultQuery execDynamicQuery(	final long queryId,
												final String uniqueSchemaName, 
												final ParamObj paramObj, 
												final String outputType) throws Exception {
		
		
		ResultQuery ret;
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		if(db == null) {
			throw new Exception("Schema Name provided is wrong");
		}
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObj);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObj);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format")) ;

		return ret;
	}
	
	
	
	
	public static ResultQueryJsonRows execDynamicQueryWithJsonRows(	final String uniqueSchemaName, 
																	final long queryId,
																	final ParamObj paramObj) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execDynamicQueryWithJsonRows(connectionDetailInfo, queryId, paramObj);
	}
	
	public static ResultQueryAsList execDynamicQueryAsList(	final String uniqueSchemaName, 
																final long queryId,
																final ParamObj paramObj) throws Exception {
	
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execDynamicQueryAsObjectList(connectionDetailInfo, queryId, paramObj);
	}

	
	public static ResultSet execDynamicQueryWithResultSet(	final long queryId,
															final String uniqueSchemaName, 
															final ParamObj paramObj) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execDynamicQuery(connectionDetailInfo, queryId, paramObj);
	}
	
	
	
	
	
	
	public static ResultQuery execDynamicQuery(	final int queryId, 
												final String uniqueSchemaName,
												final ParamObj paramObj, 
												final String outputType, 
												final int databaseId, 
												final int schemaId) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);

		ResultQuery ret;
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObj);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObj);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format ")) ;

		return ret;
	}
	
	
	
	public static ResultQuery execDynamicQuery(	final int queryId, 
												final ParamObj paramObj, 
												final String outputType, 
												final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret;
		if(outputType.toLowerCase().compareTo("csv") == 0)
			ret = SqlQueryExecUtils.execDynamicQueryWithCsvOutput(connectionDetailInfo, queryId, paramObj);
		else if(outputType.toLowerCase().compareTo("json") ==0)
			ret = SqlQueryExecUtils.execDynamicQueryWithJsonOutput(connectionDetailInfo, queryId, paramObj);
		else throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "no known document format ")) ;
		return ret;
	}
	
	

	public static ResultQuery execStaticDml(final long queryId, 
											final String uniqueSchemaName, 
											final String staticQuery) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		int recordsAffected = SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	
	public static int execStaticDml_(final long queryId,
									 final String uniqueSchemaName,
									 final String staticQuery) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		return SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
	}
	
	public static ResultQuery execStaticDml(final String uniqueSchemaName, 
											final String staticQuery) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		
		int recordsAffected = SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	
	
	public static int execStaticDmlWithRecordsAffected(	final long queryId, 
														final String uniqueSchemaName, 
														final String staticQuery) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
	}
	
	

	public static ResultQuery execStaticDml(final long queryId, 
											final String uniqueSchemaName, 
											final String staticQuery, 
											final long databaseId) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		int recordsAffected = SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}

	public static ResultQuery execStaticDml(final String staticQuery,
											final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret = new ResultQuery();
		int recordsAffected = SqlQueryExecUtils.execStaticDml(connectionDetailInfo, staticQuery);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	

	public static ResultQuery execDynamicDml(	final long queryId, 
												final String uniqueSchemaName, 
												final String paramObjJson) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		Gson g = new Gson(); 
		ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
		int recordsAffected = SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	

	public static ResultQuery execDynamicDmlBatchCommit(final long queryId, 
														final String uniqueSchemaName, 
														final ParamListObj paramListObj, 
														long batchSize) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execDynamicDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		ret.setRecordsAffected(dmlBatchAffectedParam.getGrandTotalRecordsAffected());
		ObjectMapper mapper = new ObjectMapper();
		String DmlBatchAffectedParamStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dmlBatchAffectedParam);
		JSONParser parser = new JSONParser(); 
		JSONObject DmlBatchAffectedParamJson = (JSONObject) parser. parse(DmlBatchAffectedParamStr);
		ret.setResultQueryJson(DmlBatchAffectedParamJson);
		return ret;
	}
	
	public static ResultQueryAsList execDynamicDmlBatchCommitAsList(final long queryId, 
																	final String uniqueSchemaName, 
																	final ParamListObj paramListObj, 
																	long batchSize) throws Exception {
		ResultQueryAsList ret = new ResultQueryAsList();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execDynamicDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		ret.setRecordsAffected(dmlBatchAffectedParam.getGrandTotalRecordsAffected());
		return ret;
	}
	
	public static int execDynamicDmlBatchCommit_(final long queryId, 
												final String uniqueSchemaName, 
												final ParamListObj paramListObj, 
												int batchSize) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execDynamicDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		return dmlBatchAffectedParam.getGrandTotalRecordsAffected();
	}
	

	public static ResultQuery execStaticDmlBatchCommit(	final long queryId, 
														final String uniqueSchemaName, 
														final ParamListObj paramListObj, 
														final long batchSize) throws Exception
	{
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execStaticDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		ret.setRecordsAffected(dmlBatchAffectedParam.getGrandTotalRecordsAffected());
		ObjectMapper mapper = new ObjectMapper();
		String DmlBatchAffectedParamStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dmlBatchAffectedParam);
		JSONParser parser = new JSONParser(); 
		JSONObject DmlBatchAffectedParamJson = (JSONObject) parser. parse(DmlBatchAffectedParamStr);
		ret.setResultQueryJson(DmlBatchAffectedParamJson);
		return ret;
	}
	
	public static ResultQueryAsList execStaticDmlBatchCommitAsList(	final long queryId, 
																	final String uniqueSchemaName, 
																	final ParamListObj paramListObj, 
																	final long batchSize) throws Exception
	{
		ResultQueryAsList ret = new ResultQueryAsList();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execStaticDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		ret.setRecordsAffected(dmlBatchAffectedParam.getGrandTotalRecordsAffected());
		return ret;
	}
	
	public static int execStaticDmlBatchCommit_(final int queryId, 
												final String uniqueSchemaName, 
												final ParamListObj paramListObj, 
												final int batchSize) throws Exception
	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execStaticDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		return dmlBatchAffectedParam.getGrandTotalRecordsAffected();
	}
	
	

	public static ResultQuery execDynamicDml(	final long queryId, 
												final String uniqueSchemaName, 
												final String paramObjJson, 
												final long databaseId) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		Gson g = new Gson(); 
		ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
		int recordsAffected = SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	


	public static ResultQuery execStaticDmlBatchCommit(	final long queryId, 
														final String uniqueSchemaName, 
														final ParamListObj paramListObj, 
														final long databaseId,
														final int batchSize) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execStaticDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		ret.setRecordsAffected(dmlBatchAffectedParam.getGrandTotalRecordsAffected());
		ObjectMapper mapper = new ObjectMapper();
		String DmlBatchAffectedParamStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dmlBatchAffectedParam);
		JSONParser parser = new JSONParser(); 
		JSONObject DmlBatchAffectedParamJson = (JSONObject) parser. parse(DmlBatchAffectedParamStr);
		ret.setResultQueryJson(DmlBatchAffectedParamJson);
		return ret;
	}
	
	

	public static ResultQuery execDynamicDml(	final long queryId, 
												final String paramObjJson, 
												final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret = new ResultQuery();
		
		Gson g = new Gson(); 
		ParamObj paramObj = g.fromJson(paramObjJson, ParamObj.class);
		
		
		int recordsAffected = SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	

	public static ResultQuery execDynamicDmlBatchCommit(final int queryId, 
														final ParamListObj paramListObj, 
														final DbConnectionInfo connectionDetailInfo, 
														final int batchSize) throws Exception {
		ResultQuery ret = new ResultQuery();
		DmlBatchAffectedParam dmlBatchAffectedParam = SqlQueryExecUtils.execDynamicDmlBatchCommit(connectionDetailInfo, queryId, paramListObj, batchSize);
		ret.setRecordsAffected(dmlBatchAffectedParam.getGrandTotalRecordsAffected());
		ObjectMapper mapper = new ObjectMapper();
		String DmlBatchAffectedParamStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dmlBatchAffectedParam);
		JSONParser parser = new JSONParser(); 
		JSONObject DmlBatchAffectedParamJson = (JSONObject) parser. parse(DmlBatchAffectedParamStr);
		ret.setResultQueryJson(DmlBatchAffectedParamJson);
		return ret;
	}
	

	public static ResultQuery execDynamicDml(	final long queryId, 
												final String uniqueSchemaName, 
												final ParamObj paramObj) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		int recordsAffected = SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	
	public static int execDynamicDml_(	final long queryId, 
										final String uniqueSchemaName, 
										final ParamObj paramObj) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
	}
	

	public static int execDynamicDmlWithRecordsAffected(final long queryId, 
														final String uniqueSchemaName, 
														final ParamObj paramObj) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
	}
	
		

	public static ResultQuery execDynamicDml(	final long queryId, 
												final String uniqueSchemaName, 
												final ParamObj paramObj, 
												long databaseId) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		int recordsAffected = SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	
	

	public static ResultQuery execDynamicDml(	final long queryId, 
												final ParamObj paramObj, 
												final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret = new ResultQuery();
		int recordsAffected = SqlQueryExecUtils.execDynamicDml(connectionDetailInfo, queryId, paramObj);
		ret.setRecordsAffected(recordsAffected);
		return ret;
	}
	
	
	
	

	public static ResultQuery execStaticDdl(final String staticQuery, 
											final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret = new ResultQuery();
		boolean IsDone = SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, staticQuery);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		return ret;
	}
	
	public static ResultQuery execStaticDdl(final String staticQuery, 
											final Connection conn) throws Exception {
		ResultQuery ret = new ResultQuery();
		boolean IsDone = SqlQueryExecUtils.execStaticDdl(conn, staticQuery);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		return ret;
	}

	public static ResultQuery execStaticDdl(final long queryId, 
											final String uniqueSchemaName, 
											final ParamObj paramObj) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		String sqlCompiledString = compileQueryFromLocalRepo(queryId, paramObj);
		
		boolean IsDone = SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, sqlCompiledString);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		
		return ret;
	}
	
	
	public static int execStaticDdl_(	final long queryId, 
										final String uniqueSchemaName, 
										final ParamObj paramObj) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		String sqlCompiledString = compileQueryFromLocalRepo(queryId, paramObj);
		if(SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, sqlCompiledString))
			return 1;
		else
			return 0;
	}
	
	public static ResultQuery execStaticDdl(final String uniqueSchemaName, 
											final String sqlCompiledString) throws Exception	{
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		boolean IsDone = SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, sqlCompiledString);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		
		return ret;
	}
	
	
	public static boolean execStaticDdlWithBoolean(	final long queryId, 
													final String uniqueSchemaName, 
													final ParamObj paramObj) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		String sqlCompiledString = compileQueryFromLocalRepo(queryId, paramObj);
		return SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, sqlCompiledString);
	}
	

	public static ResultQuery execStaticDdl(final long queryId, 
											final String uniqueSchemaName, 
											final ParamObj paramObj, 
											final long databaseId) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		String sqlCompiledString = compileQueryFromLocalRepo(queryId, paramObj);

		boolean IsDone = SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, sqlCompiledString);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		
		return ret;
	}
	

	public static ResultQuery execDynamicDdlResultQuery(final long queryId, 
														final String uniqueSchemaName, 
														final ParamObj paramObj) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		ObjectMapper mapper = new ObjectMapper();
		String paramObjStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramObj);
		
		boolean IsDone = SqlQueryExecUtils.execDynamicDdl(connectionDetailInfo, queryId, paramObjStr);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		
		return ret;
	}
	
	
	public static int execDynamicDdl_(	final long queryId, 
										final String uniqueSchemaName, 
										final ParamObj paramObj) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		ObjectMapper mapper = new ObjectMapper();
		String paramObjStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramObj);
		
		if(SqlQueryExecUtils.execDynamicDdl(connectionDetailInfo, queryId, paramObjStr) )
			return 1;
		else
			return 0;
	}

	public static boolean execDynamicDdlWithBoolean(final long queryId, 
													final String uniqueSchemaName, 
													final ParamObj paramObj) throws Exception	{
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		ObjectMapper mapper = new ObjectMapper();
		String paramObjStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramObj);
		return SqlQueryExecUtils.execDynamicDdl(connectionDetailInfo, queryId, paramObjStr);
	}
	

	public static ResultQuery execDynamicDdl(	final long queryId, 
												final String uniqueSchemaName, 
												final ParamObj paramObj) throws Exception {
		ResultQuery ret = new ResultQuery();
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		ObjectMapper mapper = new ObjectMapper();
		String paramObjStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramObj);
		boolean IsDone = SqlQueryExecUtils.execDynamicDdl(connectionDetailInfo, queryId, paramObjStr);
		
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		
		return ret;
	}
	
	
	

	public static ResultQuery execDynamicDdl(	final int queryId, 
												final ParamObj paramObj, 
												final DbConnectionInfo connectionDetailInfo) throws Exception {
		ResultQuery ret = new ResultQuery();

		ObjectMapper mapper = new ObjectMapper();
		String paramObjStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramObj);
		
		boolean IsDone = SqlQueryExecUtils.execDynamicDdl(connectionDetailInfo, queryId, paramObjStr);
		if(IsDone)
			ret.setRecordsAffected(1);
		else
			ret.setRecordsAffected(0);
		return ret;
	}
	


	public static String getParamToPopulateJsonString(final Long queryId) throws Exception {
		String ret;
		try {
			ParamObj paramObj = new  ParamObj(SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getParamList());  
			Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();
			ret = gson_pretty.toJson(paramObj);
			System.out.println(ret);
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		
		return ret;
	}
	

	public static String getBulkParamToPopulateJsonString(final Long queryId) throws Exception {
		String ret;
		try	{
			SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId);
			if(queryObj.getSqlType().compareTo("INSERT") == 0 
					|| queryObj.getSqlType().compareTo("UPDATE") == 0 
					|| queryObj.getSqlType().compareTo("DELETE") == 0)	{
				ParamObj paramObj = new  ParamObj(SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getParamList()); 
				ret = ParamListObj.printNiceFormat(paramObj);
				System.out.println(ret);
			}
			else {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Not a DML")) ;
			}
		}
		catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	
		return ret;
	}
	

	public static ParamListObj  getBulkParamToPopulateJson(final long queryId) throws Exception {
		ParamListObj  ret = null;
		try	{
			
			SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId);
			if(queryObj.getSqlType().compareTo("INSERT") == 0 
					|| queryObj.getSqlType().compareTo("UPDATE") == 0 
					|| queryObj.getSqlType().compareTo("DELETE") == 0)
			{
				ParamObj paramObj = new  ParamObj(SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getParamList());
				List<ParamObj> plistlist = new java.util.ArrayList<>();
				plistlist.add(paramObj);
				plistlist.add(paramObj);
				ret = new ParamListObj(plistlist);
			}
			else {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, queryId + " Not a DML")) ;
			}
		}
		catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		return ret;
	}
	

	public static String getParamJsonString(final Long queryId) throws Exception	{
		String ret;
		try	{
			List<SqlRepoParam> sqlRepoParamList = SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlRepoParamList();
			Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();
			ret = gson_pretty.toJson(sqlRepoParamList);
		}
		catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		return ret;
	}
	
	

	public static SqlRepoParam getSqlRepoParam(	final List<SqlRepoParam> paramList, 
												final long paramID ) throws Exception{
		try	{
			for (SqlRepoParam tempSqlRepoParam : paramList) {
				if(tempSqlRepoParam.getDynamicSqlParamId() == paramID)
					return tempSqlRepoParam;
			}
			
			return null;
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	}
	

	public static void printAllQueries() throws Exception {
		try	{
			// Print values
			for (SqlRepoDynamicSql sqlRepoDynamicSql : SqlRepoUtils.sqlRepoDynamicSqlMap.values()) {
				int lengthDec = Math.min(sqlRepoDynamicSql.getSqlDescription().length(), 50);
                System.out.println("id:" + sqlRepoDynamicSql.getSqlId() + "   name:" + sqlRepoDynamicSql.getSqlName().trim() +  "   Desc:" + sqlRepoDynamicSql.getSqlDescription().substring(0, lengthDec));
			}
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	}
	

	public static void printQueryDescription(final Long queryId) throws Exception {
		try	{
			System.out.println(SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlDescription());
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	}
	
	

	public static void printQueryString(final Long queryId) throws Exception {
		try	{
			System.out.println(SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId).getSqlContent());
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	}	
	
	

	public static void printObject(final Long queryId) throws Exception {
		try	{
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(SqlRepoUtils.sqlRepoDynamicSqlMap.get(queryId));
			System.out.println(json);
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
	}
	


	public static ResultQuery compressPayload(	final String compression, 
												final ResultQuery resultQuery) {
		ResultQuery ret = resultQuery;
		try	{
			if(compression.compareTo("GZIP") == 0) {
				if(ret.getOutputFormat().toUpperCase().compareTo("JSON") == 0) {
					JSONObject payload = ret.getResultQueryJson();
					byte[] compressedPayload = PayloadCompression.GZIPCompress(payload.toJSONString());
					ret.setResultQueryByteArray(compressedPayload);
					ret.setResultQueryJson(null);
				}
				else if(ret.getOutputFormat().toUpperCase().compareTo("CSV") == 0) {
					String payload = ret.getResultQuery();
					byte[] compressedPayload = PayloadCompression.GZIPCompress(payload);
					ret.setResultQueryByteArray(compressedPayload);
					ret.setResultQuery(null);
				}
				ret.setOutputPackaging("GZIP");
			}
			else if(compression.compareTo("ZIP") == 0) {
				if(ret.getOutputFormat().toUpperCase().compareTo("JSON") == 0) {
					JSONObject payload = ret.getResultQueryJson();
					byte[] compressedPayload = PayloadCompression.ZIPCompress(payload.toJSONString());
					ret.setResultQueryByteArray(compressedPayload);
					ret.setResultQueryJson(null);
				}
				else if(ret.getOutputFormat().toUpperCase().compareTo("CSV") == 0) {
					String payload = ret.getResultQuery();
					byte[] compressedPayload = PayloadCompression.ZIPCompress(payload);
					ret.setResultQueryByteArray(compressedPayload);
					ret.setResultQuery(null);
				}
				ret.setOutputPackaging("ZIP");
			}
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
		
		return ret;
	}
	

	public static ResultQuery decompressPayload(final ResultQuery resultQuery) {

        try	{
			if(resultQuery.getOutputPackaging().toUpperCase().compareTo("GZIP") == 0) {
				if(resultQuery.getOutputFormat().toUpperCase().compareTo("JSON") == 0) {
					String decompressedPayload = PayloadCompression.GZIPDecompress( resultQuery.getResultQueryByteArray()  );
					JSONParser parser = new JSONParser(); 
					JSONObject jsonObject = (JSONObject) parser. parse(decompressedPayload);
					resultQuery.setResultQueryJson(jsonObject);
					resultQuery.setOutputPackaging("PLAIN");
					resultQuery.setResultQueryByteArray(null);
					
				}
				else if(resultQuery.getOutputFormat().toUpperCase().compareTo("CSV") == 0) {
					String decompressedPayload = PayloadCompression.GZIPDecompress( resultQuery.getResultQueryByteArray()  );
					resultQuery.setResultQuery(decompressedPayload);
					resultQuery.setOutputPackaging("PLAIN");
					resultQuery.setResultQueryByteArray(null);
				}
			}
			else if(resultQuery.getOutputPackaging().toUpperCase().compareTo("ZIP") == 0) {
				if(resultQuery.getOutputFormat().toUpperCase().compareTo("JSON") == 0) {
					String decompressedPayload = PayloadCompression.ZIPDecompress( resultQuery.getResultQueryByteArray()  );
					JSONParser parser = new JSONParser(); 
					JSONObject jsonObject = (JSONObject) parser. parse(decompressedPayload);
					resultQuery.setResultQueryJson(jsonObject);
					resultQuery.setOutputPackaging("PLAIN");
					resultQuery.setResultQueryByteArray(null);
					
				}
				else if(resultQuery.getOutputFormat().toUpperCase().compareTo("CSV") == 0) {
					String decompressedPayload = PayloadCompression.ZIPDecompress( resultQuery.getResultQueryByteArray()  );
					resultQuery.setResultQuery(decompressedPayload);
					resultQuery.setOutputPackaging("PLAIN");
					resultQuery.setResultQueryByteArray(null);
				}
			}
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return resultQuery;
		}
		
		return resultQuery;
	}
	
	
	
	

	public static String compressString(final String compression, 
										final String str) {
		String ret = str;
		try	{
			if(compression.toUpperCase().compareTo("GZIP") == 0) {
				byte[] compressedPayload = PayloadCompression.GZIPCompress(str);
				ret = java.util.Base64.getEncoder().encodeToString(compressedPayload);
			}
			else if(compression.toUpperCase().compareTo("ZIP") == 0) {
				byte[] compressedPayload = PayloadCompression.ZIPCompress(str);
				ret = java.util.Base64.getEncoder().encodeToString(compressedPayload);
			}
		}
		catch(Exception e)	{
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
		
		return ret;
	}
	
	

	public static String decompressString(	final String compression, 
											final String compressedStr)	{
		String ret = compressedStr;
		try	{
			if(compression.toUpperCase().compareTo("GZIP") == 0) {
				ret = PayloadCompression.GZIPDecompress( compressedStr.getBytes()  );
			}
			else if(compression.toUpperCase().compareTo("ZIP") == 0) {
				ret = PayloadCompression.ZIPDecompress( compressedStr.getBytes()  );
			}
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
		
		return ret;
	}
	
	
	
	public static TableList getTableList(final String uniqueSchemaName,
										final String schemaName) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo conn = DbConnectionInfo.makeDbConnectionInfo(db);
        return new TableList (SqlMetadataWrapper.getAllTableList(conn, schemaName));
	}
	
	public static TableList getSchemaList(	final String uniqueSchemaName) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo conn = DbConnectionInfo.makeDbConnectionInfo(db);
		List<String> schemas = SqlMetadataWrapper.getSchemas(conn);
		return new TableList (schemas);
	}
	
	
	public static RecordsAffected insertBulkIntoTable(	final String uniqueSchemaName,
											final String schemaName, 
											final String tableName, 
											final List<Map<String, Object>> records, 
											final Map<String, String> metadata) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.insertBulkIntoTable(connectionDetailInfo, schemaName, tableName, records, metadata);
	}
	
	public static RecordsAffected insertBulkIntoTable_v2(	final String uniqueSchemaName,
															final String schemaName, 
															final String tableName, 
															final List<Map<String, Object>> records, 
															final Map<String, String> metadata) throws Exception {
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
		return SqlQueryExecUtils.insertBulkIntoTable_v2(connectionDetailInfo, schemaName, tableName, records, metadata);
	}
	
	public static RecordsAffected insertBulkIntoTable_v2(	final Connection conn,
															final String schemaName, 
															final String tableName, 
															final List<Map<String, Object>> records, 
															final Map<String, String> metadata) throws Exception {
		return SqlQueryExecUtils.insertBulkIntoTable_v2(conn, schemaName, tableName, records, metadata);
	}


	public static boolean createTable(final String rdbmsConnectionName,
									  final String statement) {
		try {
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsConnectionName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			SqlQueryExecUtils.execStaticDdl(connectionDetailInfo, statement);
			return true;
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return false;
		}
	}
}

	

	
	