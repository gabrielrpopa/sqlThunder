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


package com.widescope.rdbmsRepo.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.widescope.logging.AppLogger;

import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.MultipleSqlOnDbList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamListObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryAsList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlExecutionType;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDynamicSql;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmToDb;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmType;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQuery;
import com.widescope.rdbmsRepo.database.mongodb.MongoPut;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.security.HashWrapper;
import com.widescope.sqlThunder.utils.user.User;


public class SqlRepoExecWrapper {






	public static 
	SqlRepoExecReturn 
	execSqlRepoParallel(final long sqlId,
						final String dbIdList,
						final String jsonBody,
						final long batchCount,
						final String user,
						final String persist)  {
		SqlRepoExecReturn ret = new SqlRepoExecReturn();
		List<String> lstString = Arrays.asList(dbIdList.split(",", -1));
		List<Integer> lstInts = StaticUtils.convertStringListToIntList(lstString, Integer::parseInt);
		List<SqlRepoDatabase> lstSqlRepoDatabase = SqlRepoUtils.sqlRepoDatabaseMap.values()
													.stream()
													.filter( s -> lstInts.contains(s.getDatabaseId()))
													.toList();
		
		ExecutorService executor = Executors.newFixedThreadPool(lstSqlRepoDatabase.size());
        List<Future<SqlRepoExecReturn>> list = new ArrayList<>();
        for (SqlRepoDatabase sqlRepoDb : lstSqlRepoDatabase) {
            Callable<SqlRepoExecReturn> callable = new MultiDbAggregator(sqlRepoDb,
																		sqlId,
																		jsonBody,
																		batchCount,
																		user,
																		persist);
            Future<SqlRepoExecReturn> future = executor.submit(callable);
            list.add(future);
        }
              
        ResultQueryAsList resultsAsList = new ResultQueryAsList();
        for(Future<SqlRepoExecReturn> fut : list){
            try {
            	if(fut.get().getErrorCode() == 0) {
            		if(fut.get().getResultsAsList().getRecordsAffected() != 0) {
                        ResultQueryAsList.compound(resultsAsList, fut.get().getResultsAsList());
                    }
            	} else {
            		ret.setErrorCode(ret.getErrorCode() - 1);
            		ret.setErrorMessage(ret.getErrorMessage() + "; " + fut.get().getErrorMessage() );
            	}
            } catch (Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        //shut down the executor service now
        executor.shutdown();
        ret.setResultsAsList(resultsAsList);
        return ret;
	}
	
	
	public static 
	SqlRepoExecReturn 
	execSqlRepoParallel(MultipleSqlOnDbList multipleSqlOnDbLst,
						final String jsonBody,
						final long batchCount,
						final String user,
						final String persist)  {
		SqlRepoExecReturn ret = new SqlRepoExecReturn();
		ExecutorService executor = Executors.newFixedThreadPool(multipleSqlOnDbLst.getMultipleSqlOnDblst().size());
        List<Future<SqlRepoExecReturn>> list = new ArrayList<Future<SqlRepoExecReturn>>();
        for(int i=0; i < multipleSqlOnDbLst.getMultipleSqlOnDblst().size(); i++){
        	SqlStmToDb sqlToDb = multipleSqlOnDbLst.getMultipleSqlOnDblst().get(i);
        	
        	SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.sqlRepoDatabaseMap.values().stream().filter( p -> p.getDatabaseId()==sqlToDb.getDbId()).findAny().orElse(null);
        	SqlRepoDynamicSql  sqlRepoDynamicSql  = SqlRepoUtils.sqlRepoDynamicSqlMap.values().stream().filter( p -> p.getSqlId()==sqlToDb.getSqlId()).findAny().orElse(null);
        	if(sqlRepoDatabase != null && sqlRepoDynamicSql != null) {
        		Callable<SqlRepoExecReturn> callable = new MultiDbAggregator(	sqlRepoDatabase, 
        																		sqlRepoDynamicSql.getSqlId(),
        																		jsonBody, 
        																		batchCount,
        																		user,
        																		persist	);
                Future<SqlRepoExecReturn> future = executor.submit(callable);
                list.add(future);
        	}
        }
              
        ResultQueryAsList resultsAsList = new ResultQueryAsList();
        for(Future<SqlRepoExecReturn> fut : list){
            try {
            	if(fut.get().getErrorCode() == 0) {
            		if(fut.get().getResultsAsList().getRecordsAffected() != 0) {
                        ResultQueryAsList.compound(resultsAsList, fut.get().getResultsAsList());
                    }
            	} else {
            		ret.setErrorCode(ret.getErrorCode() - 1);
            		ret.setErrorMessage(ret.getErrorMessage() + "; " + fut.get().getErrorMessage() );
            	}

            } catch (Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        //shut down the executor service now
        executor.shutdown();
        ret.setResultsAsList(resultsAsList);
        return ret;
	}
	
	
	
	
	
	
	public static 
	SqlRepoExecReturn 
	execSqlRepoForResultQueryAsList(final long sqlId,
									final String schemaUniqueName,
									final String jsonBody,
									final long batchCount,
									final String user,
									final String persist) throws Exception {

		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId);
		ResultQueryAsList resultQuery = new ResultQueryAsList();
				
		
		ParamObj paramObj = ParamObj.convertStringToParamObj(jsonBody);
		ParamListObj paramListObj = ParamListObj.convertStringToParamListObj(jsonBody);
		
		// Check if it's a single row SQL
		if(paramObj != null) {
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.querySql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicQueryAsList(schemaUniqueName, sqlId, paramObj );
					
				}
				else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(sqlId, paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticQueryAsList(schemaUniqueName,	sqlCompiledString);
					
				}
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			} else if( 	queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
						queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
						queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					int recAffected = SqlQueryRepoUtils.execDynamicDml_(sqlId, schemaUniqueName, paramObj);
					resultQuery.setRecordsAffected(recAffected);
				} else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(sqlId, paramObj);
					int recAffected = SqlQueryRepoUtils.execStaticDml_(sqlId, schemaUniqueName, sqlCompiledString);
					resultQuery.setRecordsAffected(recAffected);
				}
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			}
			else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.createtable) ==0 ||
					queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.truncate) ==0 ||
					queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.droptable) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					int recAffected = SqlQueryRepoUtils.execDynamicDdl_(sqlId, schemaUniqueName, paramObj);
					resultQuery.setRecordsAffected(recAffected);
				} else {
					int recAffected = SqlQueryRepoUtils.execStaticDdl_(sqlId, schemaUniqueName, paramObj);
					resultQuery.setRecordsAffected(recAffected);
				}
				
				resultQuery.setSqlType(Constants.RDBMS_DML);
				
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1,
												"Error Executing Sql Repo",
															AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Cannot find SQL Type"));
				
			}
		} else if(paramListObj != null) { // Check if it's a multiple/Bulk/Batch SQL

			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
				queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
				queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					 resultQuery = SqlQueryRepoUtils.execDynamicDmlBatchCommitAsList(sqlId,
																					schemaUniqueName, 
																					paramListObj, 
																					batchCount);
				} else {
					resultQuery = SqlQueryRepoUtils.execStaticDmlBatchCommitAsList(sqlId,
																				schemaUniqueName, 
																				paramListObj, 
																				batchCount);
				}
				
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1,
												"Error Executing Sql Repo",
															AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Cannot find SQL Type"));
			}
		} else {
			sqlRepoExecReturn = new SqlRepoExecReturn(-1,
											"Error Executing Sql Repo",
														AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: This is neither single SQL/DML nor bulk DML"));
		}
			
		sqlRepoExecReturn.setResultsAsList(resultQuery);
		return sqlRepoExecReturn;
	}
	
	
	public static 
	SqlRepoExecReturn 
	execSqlRepoForResultQuery(	final long sqlId,
								final String schemaUniqueName,
								final String jsonObjSqlParam,
								final String outputType,
								final String requestId,
								final long batchCount,
								final String comment,
								final String outputCompression,
								final String user,
								final long userId,
								final String persist) throws Exception {
		
		
		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId);

		ResultQuery resultQuery = new ResultQuery();
		ParamObj paramObj = ParamObj.convertStringToParamObj(jsonObjSqlParam);
		ParamListObj paramListObj = ParamListObj.convertStringToParamListObj(jsonObjSqlParam);
		
		// Check if it's a single row SQL
		if(paramObj != null) {
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.querySql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicQuery(sqlId, schemaUniqueName, paramObj, outputType);
				}
				else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(sqlId, paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticQuery(schemaUniqueName, sqlCompiledString, outputType);
				}
				
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			} else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDml(sqlId, schemaUniqueName, paramObj);
				} else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(sqlId,	paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticDml(sqlId, schemaUniqueName, sqlCompiledString);
				}
				resultQuery.setSqlType(Constants.RDBMS_DML);
			}
			else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.createtable) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.truncate) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.droptable) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDdl(sqlId, schemaUniqueName, paramObj);
				} else {
					resultQuery = SqlQueryRepoUtils.execStaticDdl(sqlId, schemaUniqueName, paramObj);
				}
				
				resultQuery.setSqlType(Constants.RDBMS_DDL);
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: Cannot find SQL Type"));
			}
		}
		// Check if it's a multiple/Bulk/Batch SQL
		else if(paramListObj != null) {
		    if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0)	{
		    	if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
		    		resultQuery = SqlQueryRepoUtils.execDynamicDmlBatchCommit(sqlId, schemaUniqueName, paramListObj, batchCount);
				} else {
		    		resultQuery = SqlQueryRepoUtils.execStaticDmlBatchCommit(sqlId, schemaUniqueName, paramListObj, batchCount);
		    	}
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: Cannot find SQL Type"));
			}
		} else {
			sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: This is neither single SQL/DML nor bulk DML"));
		}
					
		// Now check the request for Output payload compression
		if(outputCompression!= null && !outputCompression.isEmpty()) {
			resultQuery =  SqlQueryRepoUtils.compressPayload(outputCompression, resultQuery);
		}
					
		resultQuery.setSqlId(sqlId);
		resultQuery.setSqlStm("");
		resultQuery.setUser(user);
		resultQuery.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
		resultQuery.setSqlName(queryObj.getSqlName());
		
		
		if(comment != null && !comment.isEmpty() ) {
			resultQuery.setComment(comment);
			try {
				if(!MongoPut.addTempDocumentThreaded(user, resultQuery.toString(), true))
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "-Cannot save sql result: " + user);

			} catch (Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			}
		}
		sqlRepoExecReturn.setResults(resultQuery);
		return sqlRepoExecReturn;
		
	}
	
	
	public static
	SqlRepoExecReturn 
	execSqlRepoToWebSocket(final long sqlId,
						   final String schemaUniqueName,
						   final String jsonObjSqlParam,
						   final String requestId,
						   final long batchCount,
						   final String comment,
						   final String outputCompression,
						   final String httpSession,
						   final String jobId,
						   final User u,
						   final long groupId,
						   final String persist,
						   final PersistenceWrap pWrap) throws Exception {


		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId);
		ResultQuery resultQuery = new ResultQuery();
		ParamObj paramObj = ParamObj.convertStringToParamObj(jsonObjSqlParam);
		ParamListObj paramListObj = ParamListObj.convertStringToParamListObj(jsonObjSqlParam);

		if(paramObj != null) {
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.querySql) ==0) {
				
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					SqlQueryRepoUtils.execDynamicQueryToWebsocket(  sqlId,
																	schemaUniqueName,
																	queryObj.getSqlName(),
																	paramObj,
																	httpSession,
																	jobId,
																	u,
																	groupId,
																	persist,
																	requestId,
																	comment,
																	pWrap);
					
				}
				else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(sqlId,	paramObj);
					
					SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName,
																sqlCompiledString, 
																queryObj.getSqlName(),
																u,
																jobId,
																requestId,
																httpSession,
																persist,
																comment,
																groupId,
																pWrap);
				}
				
				resultQuery.setSqlType(Constants.RDBMS_DQL);
				
			} else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDml (sqlId, schemaUniqueName, paramObj);
				} else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(sqlId, paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticDml(sqlId, schemaUniqueName, sqlCompiledString);
				}
				resultQuery.setSqlType(Constants.RDBMS_DML);
			}
			else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.createtable) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.truncate) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.droptable) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDdl(sqlId, schemaUniqueName, paramObj);
				} else {
					resultQuery = SqlQueryRepoUtils.execStaticDdl(sqlId, schemaUniqueName, paramObj);
				}
				resultQuery.setSqlType(Constants.RDBMS_DDL);
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: Cannot find SQL Type"));
				resultQuery.setSqlType(null);
			}
		}
		// Check if it's a multiple/Bulk/Batch SQL
		else if(paramListObj != null) {
		    if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0)	{
		    	if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
		    		resultQuery = SqlQueryRepoUtils.execDynamicDmlBatchCommit(sqlId,
		    																schemaUniqueName, 
		    																paramListObj, 
		    																batchCount);
				} else {
		    		resultQuery = SqlQueryRepoUtils.execStaticDmlBatchCommit(sqlId,
		    																schemaUniqueName, 
		    																paramListObj, 
		    																batchCount);
		    	}
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: Cannot find SQL Type"));
			}
		} else {
			sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "This is neither single SQL/DML nor bulk DML"));
		}
					
		// Now check the request for Output payload compression
		if(outputCompression!= null && !outputCompression.isEmpty()) {
			resultQuery =  SqlQueryRepoUtils.compressPayload(outputCompression, resultQuery);
		}
					
		resultQuery.setSqlId(sqlId);
		resultQuery.setSqlStm("");
		resultQuery.setUser(httpSession);
		resultQuery.setTimestamp(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
		resultQuery.setSqlName(queryObj.getSqlName());
		
		
		if(comment != null && !comment.isEmpty() && !comment.isBlank()) {
			resultQuery.setComment(comment);
			try {
				if(!MongoPut.addTempDocumentThreaded(httpSession, resultQuery.toString(), true))
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "-Cannot save sql result: " + httpSession);
			} catch (Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			}
		}
		sqlRepoExecReturn.setResults(resultQuery);
		
		return sqlRepoExecReturn;

	}
	
	
	
	
	//////////////////  Adhoc //////////////////////////////////////////////////////////////////
	
	public static 
	SqlRepoExecReturn 
	execAdhocSqlForResultQuery(final User u,
							   final String schemaUniqueName,
							   final String requestId,
							   final String comment,
							   final int sqlId,
							   String sqlName,
							   String sqlType,
							   final String source,
							   final long groupId,
							   final String sqlStatement,
							   final String jsonParam,
							   final String persist,
							   final PersistenceWrap pWrap) throws Exception {

		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(schemaUniqueName);
		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		if(sqlStatement == null || sqlStatement.trim().isBlank()) {
			sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Sql Statement is empty", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Statement is empty"));
		}
		ResultQuery resultQuery;
		if(sqlType.equalsIgnoreCase(Constants.RDBMS_DQL)) {
			resultQuery = SqlQueryRepoUtils.execStaticQueryWithResultSet(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(Constants.RDBMS_DQL);
		} else if(sqlType.equalsIgnoreCase(Constants.RDBMS_DML)) {
			resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(Constants.RDBMS_DML);
		} else if(sqlType.equalsIgnoreCase(Constants.RDBMS_DDL)) {
			resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(Constants.RDBMS_DDL);
		} else {
			if(SqlParser.isSqlDQL(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticQueryWithResultSet(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			} else if(SqlParser.isSqlDML(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(Constants.RDBMS_DML);
			} else if(SqlParser.isSqlDDL(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(Constants.RDBMS_DDL);
			} else {
				resultQuery = SqlQueryRepoUtils.execStaticQueryWithResultSet(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			}
		}

		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		if(sqlName == null || sqlName.isEmpty())	sqlName = timeStamp + "-" + u.getId();
        assert sqlStatement != null;
        long hash = HashWrapper.hash64FNV(sqlStatement);
		resultQuery.setSqlHash(hash);
		resultQuery.setSqlId(sqlId);
		resultQuery.setUser(u.getUser());
		resultQuery.setSqlStm(sqlStatement);
		resultQuery.setTimestamp(timeStamp);
		resultQuery.setSqlName(sqlName);
		RdbmsExecutedQuery rec = new RdbmsExecutedQuery(-1, requestId, db.getDatabaseId(), sqlId,    sqlName, sqlType, sqlStatement, "Y", jsonParam, db.getDatabaseType(), source, groupId, u.getId(), timeStamp, "", comment, -1);
		pWrap.saveExecution(rec, resultQuery.toString(), persist);
		sqlRepoExecReturn.setResults(resultQuery);
		return sqlRepoExecReturn;
		
	}
	
	
	public static
	SqlRepoExecReturn 
	execAdhocSqlToWebSocket(final String sqlStatement,
							String sqlName,
							String sqlType,
							final String schemaUniqueName,
							final String requestId,
							final String comment,
							final String httpSession,
							final String jobId,
							final User u,
							final long groupId,
							final String persist,
							final PersistenceWrap pWrap) throws Exception {

		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		if(sqlStatement == null || sqlStatement.trim().isBlank()) {
			sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Sql Statement is empty", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Statement is empty"));
		}
		
		ResultQuery resultQuery = new ResultQuery();
		long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch();


        assert sqlStatement != null;
        long hash = HashWrapper.hash64FNV(sqlStatement);
		resultQuery.setSqlHash(hash);
		resultQuery.setSqlId(-1);
		resultQuery.setUser(u.getUser());
		resultQuery.setSqlStm(sqlStatement);
		resultQuery.setTimestamp(timestamp);
		resultQuery.setSqlName(sqlName);

		if(sqlType.equalsIgnoreCase(Constants.RDBMS_DQL)) {
			SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName, sqlName, sqlStatement, u, jobId, requestId, httpSession, persist, comment, groupId, pWrap);
			resultQuery.setSqlType(Constants.RDBMS_DQL);
		} else if(sqlType.equalsIgnoreCase(Constants.RDBMS_DML)) {
			resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(Constants.RDBMS_DML);
		} else if(sqlType.equalsIgnoreCase(Constants.RDBMS_DDL)) {
			resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(Constants.RDBMS_DDL);
		} else {
			if(SqlParser.isSqlDQL(sqlStatement)) {
				SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName, sqlName, sqlStatement, u, jobId, requestId, httpSession, persist, comment, groupId, pWrap);
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			} else if(SqlParser.isSqlDML(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(Constants.RDBMS_DML);
			} else if(SqlParser.isSqlDDL(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(Constants.RDBMS_DDL);
			} else {
				SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName, sqlName, sqlStatement, u, jobId, requestId, httpSession, persist, comment, groupId, pWrap);
				resultQuery.setSqlType(Constants.RDBMS_DQL);
			}
		}
		sqlRepoExecReturn.setResults(resultQuery);
		return sqlRepoExecReturn;

	}
	
	
	
	
	
}
