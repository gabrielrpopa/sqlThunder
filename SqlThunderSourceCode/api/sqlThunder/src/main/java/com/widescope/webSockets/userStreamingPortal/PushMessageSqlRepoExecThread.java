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

package com.widescope.webSockets.userStreamingPortal;

import java.util.List;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.SqlRepoExecWrapper;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.MultipleSqlOnDbList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDynamicSql;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmToDb;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;



public class PushMessageSqlRepoExecThread extends Thread {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
	
	final String sqlID;
	final String schemaUniqueUserName;
	final String jsonObjSqlParam;
	final String outputType;
	final String requestId;
	final String batchCount;
	final String comment;
	final String outputCompression;
	final String httpSession;
	final String host;
	final String jobId;

	final String user;
	final long userId;
	final String persist;
	
	
	
	


	private PushMessageSqlRepoExecThread(	final String sqlID,
											final String schemaUniqueUserName,
											final String jsonObjSqlParam,
											final String outputType,
											final String requestId,
											final String batchCount,
											final String comment,
											final String outputCompression,
											final String httpSession,
											final String host,
											final String jobId,
											final String user,
											final long userId,
											final String persist) {
		
		
		this.sqlID = sqlID;
		this.schemaUniqueUserName = schemaUniqueUserName;
		this.jsonObjSqlParam = jsonObjSqlParam;
		this.outputType = outputType;
		this.requestId = requestId;
		this.batchCount = batchCount;
		this.comment = comment;
		this.outputCompression = outputCompression;
		this.httpSession = httpSession;
		this.host = host;
		this.jobId = jobId;
		this.user = user;
		this.userId = userId;
		this.persist = persist;
	}
	
	public void run(){
		try {
			SqlRepoExecWrapper.execSqlRepoToWebSocket(	sqlID,
														schemaUniqueUserName,
														jsonObjSqlParam,
														outputType,
														requestId,
														batchCount,
														comment,
														outputCompression,
														httpSession,
														jobId,
														user,
														userId,
														persist
														);
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
		}
			
			
			
		
	
	}
	

	
	public static boolean runPushMessageThread(	final String sqlID,
												final String schemaUniqueUserName,
												final String jsonObjSqlParam,
												final String inputCompression,
												final String outputType,
												final String requestId,
												final String batchCount,
												final String comment,
												final String outputCompression,
												final String user,
												final String host,
												final String sessionId,
												final long userId,
												final String persist) {
		
		try {
			PushMessageSqlRepoExecThread 
			pushMessageSqlRepoExecThread 
			= new PushMessageSqlRepoExecThread(	sqlID,
												schemaUniqueUserName,
												jsonObjSqlParam,
												outputType,
												requestId,
												batchCount,
												comment,
												outputCompression,
												user,
												host,
												sessionId,
												user,
												userId,
												persist);
			pushMessageSqlRepoExecThread.start();
			return true;
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	
	
	
	
	
	public static boolean execPushMessageParallel(	final String sqlID,
													final List<SqlRepoDatabase> dbIdList,
													final String jsonObjSqlParam,
													final String outputType,
													final String requestId,
													final String batchCount,
													final String comment,
													final String outputCompression,
													final String user,
													final String host,
													final String sessionId,
													final long userId,
													final String persist) {

		try {
			
			for(SqlRepoDatabase sqlRepoDatabase: dbIdList) {
				PushMessageSqlRepoExecThread 
				pushMessageSqlRepoExecThread = new PushMessageSqlRepoExecThread(sqlID,
																				sqlRepoDatabase.getSchemaUniqueUserName(),
																				jsonObjSqlParam,
																				outputType,
																				requestId,
																				batchCount,
																				comment,
																				outputCompression,
																				user,
																				host,
																				sessionId,
																				user,
																				userId,
																				persist);
				pushMessageSqlRepoExecThread.start();
			}
			
			
			return true;
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public static boolean execPushMessageParallel(	MultipleSqlOnDbList multipleSqlOnDbList,
													final String jsonBody,
													final String inputCompression,
													final String outputType,
													final String requestId,
													final String batchCount,
													final String comment,
													final String outputCompression,
													final String user,
													final String host,
													final String sessionId,
													final long userId,
													final String persist) {

		try {


			for(int i=0; i < multipleSqlOnDbList.getMultipleSqlOnDblst().size(); i++){
				SqlStmToDb sqlToDb = multipleSqlOnDbList.getMultipleSqlOnDblst().get(i);

				SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.sqlRepoDatabaseMap.values().stream().filter( p -> p.getDatabaseId()==sqlToDb.getDbId()).findAny().orElse(null);
				SqlRepoDynamicSql  sqlRepoDynamicSql  = SqlRepoUtils.sqlRepoDynamicSqlMap.values().stream().filter( p -> p.getSqlId()==sqlToDb.getSqlId()).findAny().orElse(null);
				if(sqlRepoDatabase != null && sqlRepoDynamicSql != null) {
					PushMessageSqlRepoExecThread
					pushMessageSqlRepoExecThread = new PushMessageSqlRepoExecThread(String.valueOf(sqlRepoDynamicSql.getSqlId()),
																					sqlRepoDatabase.getSchemaUniqueUserName(),
																					jsonBody,
																					outputType,
																					requestId,
																					batchCount,
																					comment,
																					outputCompression,
																					user,
																					host,
																					sessionId,
																					user,
																					userId,
																					persist);
					pushMessageSqlRepoExecThread.start();
				}
			}

			return true;
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
}
