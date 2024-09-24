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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.FlowDetailExecutionType;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamFlowObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamListObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamListObjWithSqlId;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlExecutionType;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDynamicSql;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoFlow;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoFlowDetail;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmType;




/**
 * Thread mostly used for DDLs, (definitely not DQL), leave 
 * @author Gabriel Popa
 * @since   August 2020
 */
class SqlFlowExternalThread extends Thread {

	SqlRepoDynamicSql sqlRepoDynamicSql ;
    ParamObj paramObj;
    String uniqueSchemaName = "";
    SqlFlowExternalThread( SqlRepoDynamicSql sqlRepoDynamicSql, String uniqueSchemaName, ParamObj paramObj ) {
        this.sqlRepoDynamicSql = sqlRepoDynamicSql;
        this.paramObj = paramObj;
        this.uniqueSchemaName = uniqueSchemaName;
    }

    public void run() {
    	try	{
    		@SuppressWarnings("unused")
			ResultQuery resultQuery = new ResultQuery(); // never used because is a spin-off thread that yields no useful results, such as drop table
    		if(sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.updateSql) == 0 
					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.insertSql) == 0
					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.merge) == 0
					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.deleteSql) == 0)	{
    			if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0)	{
					resultQuery = SqlQueryRepoUtils.execDynamicDml(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
				} else if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.staticExecution) == 0) {
					String sqlString = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(sqlRepoDynamicSql.getSqlId(), paramObj);
					resultQuery = SqlQueryRepoUtils.execStaticDml(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, sqlString);
				}
			} else if( sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.droptable) == 0
					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.truncate) == 0) {
				if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0)	{
					resultQuery = SqlQueryRepoUtils.execDynamicDdl(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
				} else if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.staticExecution) == 0) {
					resultQuery = SqlQueryRepoUtils.execStaticDdl(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
				}
			}
    		
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
    }
}




class SqlFlowParallelThread extends Thread {
	SqlRepoDynamicSql sqlRepoDynamicSql;
    ParamObj paramObj;
    SqlFlowUtil sqlFlowUtil;
    String uniqueSchemaName = "";
    SqlFlowParallelThread(SqlRepoDynamicSql sqlRepoDynamicSql, String uniqueSchemaName, ParamObj paramObj, SqlFlowUtil sqlFlowUtil ) {
        this.sqlRepoDynamicSql = sqlRepoDynamicSql;
        this.paramObj = paramObj;
        this.sqlFlowUtil = sqlFlowUtil;
        this.uniqueSchemaName = uniqueSchemaName;
    }

    public void run() {
    	try	{
    		sqlFlowUtil.flowLock.acquire();
    		
    		try {
        		if(sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.updateSql) == 0 
    					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.insertSql) == 0
    					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.merge) == 0
    					|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.deleteSql) == 0)	{
        			int recordsaffected = 0;
        			if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0)	{
        				recordsaffected = SqlQueryRepoUtils.execDynamicDmlWithRecordsAffected(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
    				} else {
    					String sqlString = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(sqlRepoDynamicSql.getSqlId(), paramObj);
    					recordsaffected = SqlQueryRepoUtils.execStaticDmlWithRecordsAffected(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, sqlString);
    				}
        			sqlFlowUtil.mapThreadedCount.put(this.sqlRepoDynamicSql.getSqlId(), recordsaffected);
    			}
    			else if( sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.querySql) == 0 ) {
    				//ResultSet ret = new ResultSet();
    				//if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0)
    				//{
    				//	ret = QueryRepoUtil.execDynamicQueryWithResultSet(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
    				//}
    				//else 
    				//{
    				//	String sqlString = QueryRepoUtil.compileQueryForPreparedStatementFromLocalRepo(sqlRepoDynamicSql.getSqlId(), paramObj);
    				//	ret = QueryRepoUtil.execStaticQueryWithResultSet(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, sqlString);
    				//}
    				
    				//sqlFlowUtil.mapThreadedResultSet.put(this.sqlRepoDynamicSql.getSqlId(), ret);
    			}
    			else if( sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.droptable) == 0
						|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.truncate) == 0)	{
    				boolean ret;
    				if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.dynamicExecution) == 0)	{
    					ret = SqlQueryRepoUtils.execDynamicDdlWithBoolean(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
    				} else {
    					ret = SqlQueryRepoUtils.execStaticDdlWithBoolean(sqlRepoDynamicSql.getSqlId(), uniqueSchemaName, paramObj);
    				}
    				sqlFlowUtil.mapThreadedBoolean.put(this.sqlRepoDynamicSql.getSqlId(), ret);
    			}
    		}
    		catch(Exception ex)	{
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
    		}
    		
    		this.notifyAll();
    		
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
    	
    	sqlFlowUtil.flowLock.release();
    	
    }
}



public class SqlFlowUtil {

	public Map<Long, ResultSet> mapThreadedResultSet = new ConcurrentHashMap<>();
	public Map<Long, Integer> mapThreadedCount = new ConcurrentHashMap<>();
	public Map<Long, Boolean> mapThreadedBoolean = new ConcurrentHashMap<>();
	
	
	public final int MAX_AVAILABLE = 50;
	public final Semaphore flowLock = new Semaphore(MAX_AVAILABLE, true);
	
	private Map<Integer, SqlRepoFlowDetail>
	getSqlRepoFlowDetailToMap(List<SqlRepoFlowDetail> listSqlRepoFlowDetail) {
		Map<Integer, SqlRepoFlowDetail> ret = new HashMap<Integer, SqlRepoFlowDetail>();
		for (SqlRepoFlowDetail sqlRepoFlowDetail : listSqlRepoFlowDetail ) {
			ret.put(sqlRepoFlowDetail.getDynamic_sql_flow_detail_id(), sqlRepoFlowDetail);
		}
		return ret;
	}
	
	

	private boolean
	waitForPreviousParallelStatementsToFinish(final int max_seconds_wait) throws InterruptedException {
		int secondsCount = 0;
		while(flowLock.availablePermits() != MAX_AVAILABLE)	{
			TimeUnit.SECONDS.sleep(1);
			secondsCount++;
			if(max_seconds_wait >= secondsCount) {
				return true;
			}
		}	
		return false;
	}
	
	
	
	
	
	
	public ResultQuery
	execFlowOnDefaultDB(final ParamFlowObj paramFlowObj, final String uniqueSchemaName, String output) throws Exception	{
		ResultQuery resultQuery = new ResultQuery();
		
		try	{
			paramFlowObj.getListOfParamListObjWithSqlId();
			SqlRepoFlow sqlRepoFlow = SqlRepoUtils.sqlRepoFlowMap.get((long) paramFlowObj.getFlowID());
			List<SqlRepoFlowDetail> listSqlRepoFlowDetail = sqlRepoFlow.getMapSqlRepoFlowDetail();
			Map<Integer, SqlRepoFlowDetail>  mapSqlRepoFlowDetail  = getSqlRepoFlowDetailToMap(listSqlRepoFlowDetail);
			int counter = 0;
			int max_seconds_wait = 0;
			long previousSqlId = 0;

			for (ParamListObjWithSqlId paramListObjWithSqlId : paramFlowObj.getListOfParamListObjWithSqlId() ) {
				String input_from_previous = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getInput_from_previous();
				// If we have a dependency on previous execution, wait for as long as Semaphore' permits are occupied, then when all free proceed to next statement
				if(input_from_previous.compareTo("Y") == 0) {// wait for the previous parallel statements to finish

					boolean isMaxWaitReached = waitForPreviousParallelStatementsToFinish(max_seconds_wait);
					if(isMaxWaitReached) {
						throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "waiting for too long on this transaction to finish" + previousSqlId)) ;
					}
				}
				
				counter++;
				ParamListObj  paramListObj  = paramListObjWithSqlId.getParamListObj();
				if(counter!=paramListObjWithSqlId.getExecution_order())
					throw new Exception ("The order of execution is changed ");
				int sqlID = paramListObjWithSqlId.getDynamic_sql_id();
				SqlRepoDynamicSql sqlRepoDynamicSql = SqlRepoUtils.sqlRepoDynamicSqlMap.get((long) sqlID);
				
				
				if(sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.updateSql) == 0 
						|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.insertSql) == 0
						|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.merge) == 0
						|| sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.deleteSql) == 0)
				{
					// if it's a DML we expect to have more than a row to update/delete/insert
					for (ParamObj paramObj : paramListObj.getplistlist() ) {
						max_seconds_wait = 0;
						if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.parallelExecution) == 0) {
							max_seconds_wait = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getMax_seconds_wait();
							SqlFlowParallelThread sqlFlowParallelThread = new SqlFlowParallelThread(sqlRepoDynamicSql, uniqueSchemaName, paramObj, this);
							sqlFlowParallelThread.start();
						}
						else if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.externalExecution) == 0) {
							max_seconds_wait = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getMax_seconds_wait();
							SqlFlowExternalThread sqlFlowExternalThread = new SqlFlowExternalThread(sqlRepoDynamicSql, uniqueSchemaName, paramObj);
							sqlFlowExternalThread.start();
						} else if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.serialExecution) == 0) {
							if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.staticExecution) == 0) {
								String sqlString = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(sqlID, paramObj);
								resultQuery = SqlQueryRepoUtils.execStaticDml(sqlID, uniqueSchemaName, sqlString);
							} else {
								resultQuery = SqlQueryRepoUtils.execDynamicDml(sqlID, uniqueSchemaName, paramObj);
							}
						}
					}
				}
				else if( sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.querySql) == 0 ) {
					max_seconds_wait = 0;
					ParamObj paramObj = paramListObj.getplistlist().get(0);
					
					if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.parallelExecution) == 0) {
						max_seconds_wait = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getMax_seconds_wait();
						SqlFlowParallelThread sqlFlowParallelThread = new SqlFlowParallelThread(sqlRepoDynamicSql, uniqueSchemaName, paramObj, this);
						sqlFlowParallelThread.start();
					} else if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.externalExecution) == 0) {
						max_seconds_wait = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getMax_seconds_wait();
						SqlFlowExternalThread sqlFlowExternalThread = new SqlFlowExternalThread(sqlRepoDynamicSql, uniqueSchemaName, paramObj);
						sqlFlowExternalThread.start();
					} else if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.serialExecution) == 0) {
						if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.staticExecution) == 0) {
							String sqlString = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(sqlID, paramObj);
							resultQuery = SqlQueryRepoUtils.execStaticQuery(uniqueSchemaName, sqlString, output);
						} else {
							resultQuery = SqlQueryRepoUtils.execDynamicQuery(sqlID, uniqueSchemaName, paramObj, output);
						}
					}
					
				} else if( sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.droptable) == 0 || sqlRepoDynamicSql.getSqlType().compareTo(SqlStmType.truncate) == 0) {
					max_seconds_wait = 0;
					ParamObj paramObj = paramListObj.getplistlist().get(0);
					if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.parallelExecution) == 0) {
						max_seconds_wait = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getMax_seconds_wait();
						SqlFlowParallelThread sqlFlowParallelThread = new SqlFlowParallelThread(sqlRepoDynamicSql, uniqueSchemaName, paramObj, this);
						sqlFlowParallelThread.start();
					} else if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.externalExecution) == 0) {
						max_seconds_wait = mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getMax_seconds_wait();
						SqlFlowExternalThread sqlFlowExternalThread = new SqlFlowExternalThread(sqlRepoDynamicSql, uniqueSchemaName, paramObj);
						sqlFlowExternalThread.start();
					} else if(mapSqlRepoFlowDetail.get(paramListObjWithSqlId.getDynamic_sql_flow_detail_id()).getExecution_type().compareTo(FlowDetailExecutionType.serialExecution) == 0)	{
						if(sqlRepoDynamicSql.getExecution().compareTo(SqlExecutionType.staticExecution) == 0) {
							String sqlString = SqlQueryRepoUtils.compileQueryForPreparedStatementFromLocalRepo(sqlID, paramObj);
							resultQuery = SqlQueryRepoUtils.execStaticQuery(uniqueSchemaName, sqlString, output);
						} else {
							resultQuery = SqlQueryRepoUtils.execDynamicQuery(sqlID, uniqueSchemaName, paramObj, output);
						}
					}
				}
				previousSqlId = sqlRepoDynamicSql.getSqlId();
	        }
			
			return resultQuery;
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}

}
