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
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRepo;
import com.widescope.rdbmsRepo.database.mongodb.MongoPut;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.security.HashWrapper;



public class SqlRepoExecWrapper {

	private static final String DDL = "DDL";
	private static final String DML = "DML";
	private static final String DQL = "DQL";

	public static 
	SqlRepoExecReturn 
	execSqlRepoParallel(final String sqlID,
						final String dbIdList,
						final String jsonBody,
						final String batchCount,
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
																		sqlID,
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
						final String batchCount,
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
        																		String.valueOf(sqlRepoDynamicSql.getSqlId()), 
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
	execSqlRepoForResultQueryAsList(final String sqlID,
									final String schemaUniqueName,
									final String jsonBody,
									final String batchCount,
									final String user,
									final String persist) throws Exception {

		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(sqlID));
		ResultQueryAsList resultQuery = new ResultQueryAsList();
				
		
		ParamObj paramObj = ParamObj.convertStringToParamObj(jsonBody);
		ParamListObj paramListObj = ParamListObj.convertStringToParamListObj(jsonBody);
		
		// Check if it's a single row SQL
		if(paramObj != null) {
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.querySql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicQueryAsList(schemaUniqueName, Integer.parseInt(sqlID), paramObj );
					
				}
				else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(Integer.parseInt(sqlID), paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticQueryAsList(schemaUniqueName,	sqlCompiledString);
					
				}
				resultQuery.setSqlType(DQL);
			} else if( 	queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
						queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
						queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					int recAffected = SqlQueryRepoUtils.execDynamicDml_(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
					resultQuery.setRecordsAffected(recAffected);
				} else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(Integer.parseInt(sqlID), paramObj);
					int recAffected = SqlQueryRepoUtils.execStaticDml_(Integer.parseInt(sqlID), schemaUniqueName, sqlCompiledString);
					resultQuery.setRecordsAffected(recAffected);
				}
				resultQuery.setSqlType(DQL);
			}
			else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.createtable) ==0 ||
					queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.truncate) ==0 ||
					queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.droptable) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					int recAffected = SqlQueryRepoUtils.execDynamicDdl_(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
					resultQuery.setRecordsAffected(recAffected);
				} else {
					int recAffected = SqlQueryRepoUtils.execStaticDdl_(Integer.parseInt(sqlID),	schemaUniqueName, paramObj);
					resultQuery.setRecordsAffected(recAffected);
				}
				
				resultQuery.setSqlType(DML);
				
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1,
												"Error Executing Sql Repo",
															AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Cannot find SQL Type"));
				
			}
		} else if(paramListObj != null) { // Check if it's a multiple/Bulk/Batch SQL
			int tmpBatchCount = 1;
			if(!batchCount.isEmpty()) {
				try	{
					tmpBatchCount = Integer.parseInt( batchCount);
				} catch( NumberFormatException ex) {
					sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error: batchCount must be an integer", AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
				}
			}
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
				queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
				queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					 resultQuery = SqlQueryRepoUtils.execDynamicDmlBatchCommitAsList(Integer.parseInt(sqlID),
																					schemaUniqueName, 
																					paramListObj, 
																					tmpBatchCount);  
				} else {
					resultQuery = SqlQueryRepoUtils.execStaticDmlBatchCommitAsList(Integer.parseInt(sqlID),
																				schemaUniqueName, 
																				paramListObj, 
																				tmpBatchCount);  
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
	execSqlRepoForResultQuery(	final String sqlID,
								final String schemaUniqueName,
								final String jsonObjSqlParam,
								final String outputType,
								final String requestId,
								final String batchCount,
								final String comment,
								final String outputCompression,
								final String user,
								final long userId,
								final String persist) throws Exception {
		
		
		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(sqlID));

		ResultQuery resultQuery = new ResultQuery();
		ParamObj paramObj = ParamObj.convertStringToParamObj(jsonObjSqlParam);
		ParamListObj paramListObj = ParamListObj.convertStringToParamListObj(jsonObjSqlParam);
		
		// Check if it's a single row SQL
		if(paramObj != null) {
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.querySql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicQuery(Integer.parseInt(sqlID), schemaUniqueName, paramObj, outputType);
				}
				else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(Integer.parseInt(sqlID), paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticQuery(schemaUniqueName, sqlCompiledString, outputType);
				}
				
				resultQuery.setSqlType(DQL);
			} else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDml(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
				} else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(Integer.parseInt(sqlID),	paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticDml(Integer.parseInt(sqlID), schemaUniqueName, sqlCompiledString);
				}
				resultQuery.setSqlType(DML);
			}
			else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.createtable) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.truncate) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.droptable) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDdl(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
				} else {
					resultQuery = SqlQueryRepoUtils.execStaticDdl(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
				}
				
				resultQuery.setSqlType(DDL);
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: Cannot find SQL Type"));
			}
		}
		// Check if it's a multiple/Bulk/Batch SQL
		else if(paramListObj != null) {
			int tmpBatchCount = 1;
			if(!batchCount.isEmpty()) {
				try	{
					tmpBatchCount = Integer.parseInt( batchCount);
				} catch( NumberFormatException ex) {
					sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error: batchCount must be an integer", AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
				}
			}
			
			
		    if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0)	{
		    	if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
		    		resultQuery = SqlQueryRepoUtils.execDynamicDmlBatchCommit(Integer.parseInt(sqlID), schemaUniqueName, paramListObj, tmpBatchCount);
				} else {
		    		resultQuery = SqlQueryRepoUtils.execStaticDmlBatchCommit(Integer.parseInt(sqlID), schemaUniqueName, paramListObj, tmpBatchCount);
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
					
		resultQuery.setSqlId(Integer.parseInt(sqlID));
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
	execSqlRepoToWebSocket(	final String sqlID,
							final String schemaUniqueName,
							final String jsonObjSqlParam,
							final String outputType,
							final String requestId,
							final String batchCount,
							final String comment,
							final String outputCompression,
							final String httpSession,
							final String jobId,
							final String user,
							final long userId,
							final String persist) throws Exception {


		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(sqlID));
		ResultQuery resultQuery = new ResultQuery();
		ParamObj paramObj = ParamObj.convertStringToParamObj(jsonObjSqlParam);
		ParamListObj paramListObj = ParamListObj.convertStringToParamListObj(jsonObjSqlParam);

		if(paramObj != null) {
			if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.querySql) ==0) {
				
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					SqlQueryRepoUtils.execDynamicQueryToWebsocket(Integer.parseInt(sqlID),
																schemaUniqueName, 
																queryObj.getSqlName(),
																paramObj, 
																httpSession,
																jobId,
																user,
																userId,
																persist,
																requestId);
					
				}
				else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(Integer.parseInt(sqlID),	paramObj);
					
					SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName,
																sqlCompiledString, 
																queryObj.getSqlName(),
																user,
																userId,
																jobId,
																requestId,
																httpSession,
																persist);
				}
				
				resultQuery.setSqlType(DQL);
				
			} else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0) {
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDml (Integer.parseInt(sqlID), schemaUniqueName, paramObj);
				} else {
					String sqlCompiledString = SqlQueryRepoUtils.compileQueryFromLocalRepo(Integer.parseInt(sqlID), paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticDml(Integer.parseInt(sqlID), schemaUniqueName, sqlCompiledString);
				}
				resultQuery.setSqlType(DML);
			}
			else if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.createtable) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.truncate) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.droptable) ==0)	{
				if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execDynamicDdl(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
				} else {
					resultQuery = SqlQueryRepoUtils.execStaticDdl(Integer.parseInt(sqlID), schemaUniqueName, paramObj);
				}
				resultQuery.setSqlType(DDL);
			} else {
				sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error Executing Sql Repo", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error: Cannot find SQL Type"));
				resultQuery.setSqlType(null);
			}
		}
		// Check if it's a multiple/Bulk/Batch SQL
		else if(paramListObj != null) {
			int tmpBatchCount = 1;
			if(!batchCount.isEmpty()) {
				try	{
					tmpBatchCount = Integer.parseInt( batchCount);
				} catch( NumberFormatException ex) {
					sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Error: batchCount must be an integer", AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
				}
			}
			
			
		    if( queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.insertSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.updateSql) ==0 ||
					 queryObj.getSqlType().toUpperCase().compareTo(SqlStmType.deleteSql) ==0)	{
		    	if(queryObj.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0) {
		    		resultQuery = SqlQueryRepoUtils.execDynamicDmlBatchCommit(Integer.parseInt(sqlID),
		    																schemaUniqueName, 
		    																paramListObj, 
		    																tmpBatchCount);  
				} else {
		    		resultQuery = SqlQueryRepoUtils.execStaticDmlBatchCommit(Integer.parseInt(sqlID),
		    																schemaUniqueName, 
		    																paramListObj, 
		    																tmpBatchCount);  
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
					
		resultQuery.setSqlId(Integer.parseInt(sqlID));
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
	execAdhocSqlForResultQuery(	final String sqlStatement,
								final String schemaUniqueName,
								final String requestId,
								final String comment,
								String sqlName,
								String sqlType,
								final String user,
								final long userId,
								final String persist) throws Exception {

		SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
		if(sqlStatement == null || sqlStatement.trim().isBlank()) {
			sqlRepoExecReturn = new SqlRepoExecReturn(-1, "Sql Statement is empty", AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Sql Statement is empty"));
		}
		ResultQuery resultQuery;
		if(sqlType.equalsIgnoreCase(DQL)) {
			resultQuery = SqlQueryRepoUtils.execStaticQueryWithResultSet(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(DQL);
		} else if(sqlType.equalsIgnoreCase(DML)) {
			resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(DML);
		} else if(sqlType.equalsIgnoreCase(DDL)) {
			resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(DDL);
		} else {
			if(SqlParser.isSqlDQL(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticQueryWithResultSet(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(DQL);
			} else if(SqlParser.isSqlDML(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(DML);
			} else if(SqlParser.isSqlDDL(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(DDL);
			} else {
				resultQuery = SqlQueryRepoUtils.execStaticQueryWithResultSet(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(DQL);
			}
		}
		
		long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch();
		if(sqlName == null || sqlName.isEmpty())	sqlName = timestamp + "-" + userId;
        assert sqlStatement != null;
        long hash = HashWrapper.hash64FNV(sqlStatement);
		resultQuery.setSqlHash(hash);
		resultQuery.setSqlId(-1);
		resultQuery.setUser(user);
		resultQuery.setSqlStm(sqlStatement);
		resultQuery.setTimestamp(timestamp);
		resultQuery.setSqlName(sqlName);
		if( persist.compareToIgnoreCase("Y") == 0 ) {
			String fName = timestamp + "_" + sqlName;
			String folder = "./snapshots/" + userId;
			boolean isOk = FileUtilWrapper.overwriteFile(folder, fName, resultQuery.toString());
			if(isOk) {
				SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fName, sqlName, "ResultQuery", userId, timestamp, sqlStatement);
				SnapshotDbRepo snapshotDbRepo = new SnapshotDbRepo();
				snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
				long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
				snapshotDbRepo.addSnapshotDbAccess(id, userId);
			}
			
		}
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
							final long userId,
							final String user,
							final String persist) throws Exception {

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
		resultQuery.setUser(user);
		resultQuery.setSqlStm(sqlStatement);
		resultQuery.setTimestamp(timestamp);
		resultQuery.setSqlName(sqlName);
		
		if(sqlType.equalsIgnoreCase(DQL)) {
			SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName,
														sqlStatement, 
														sqlName,
														user,
														userId,
														jobId,
														requestId,
														httpSession,
														persist	);
			resultQuery.setSqlType(DQL);
		} else if(sqlType.equalsIgnoreCase(DML)) {
			resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(DML);
		} else if(sqlType.equalsIgnoreCase(DDL)) {
			resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
			resultQuery.setSqlType(DDL);
		} else {
			if(SqlParser.isSqlDQL(sqlStatement)) {
				SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName,
															sqlStatement, 
															sqlName,
															user,
															userId,
															jobId,
															requestId,
															httpSession,
															persist	);
				resultQuery.setSqlType(DQL);
			} else if(SqlParser.isSqlDML(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDml(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(DML);
			} else if(SqlParser.isSqlDDL(sqlStatement)) {
				resultQuery = SqlQueryRepoUtils.execStaticDdl(schemaUniqueName, sqlStatement);
				resultQuery.setSqlType(DDL);
			} else {
				SqlQueryRepoUtils.execStaticQueryToWebsocket(schemaUniqueName,
															sqlStatement, 
															sqlName,
															user,
															userId,
															jobId,
															requestId,
															httpSession,
															persist );
				resultQuery.setSqlType(DQL);
			}
		}
		sqlRepoExecReturn.setResults(resultQuery);
		return sqlRepoExecReturn;

	}
	
	
	
	
	
}
