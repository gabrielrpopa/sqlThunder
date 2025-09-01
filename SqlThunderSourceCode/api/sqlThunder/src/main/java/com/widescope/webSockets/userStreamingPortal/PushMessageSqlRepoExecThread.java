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
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.SqlRepoExecWrapper;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.MultipleSqlOnDbList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDynamicSql;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmToDb;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.sqlThunder.utils.user.User;


public class PushMessageSqlRepoExecThread extends Thread {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
	
	final long sqlId;
	final String schemaUniqueUserName;
	final String jsonObjSqlParam;
	final String outputType;
	final String requestId;
	final long batchCount;
	final String comment;
	final String outputCompression;
	final String httpSession;
	final String host;
	final String jobId;
	final User u;
	final String persist;
	final long groupId;
	PersistenceWrap pWrap = null;


	private PushMessageSqlRepoExecThread(final long sqlId,
										 final String schemaUniqueUserName,
										 final String jsonObjSqlParam,
										 final String outputType,
										 final String requestId,
										 final long batchCount,
										 final String comment,
										 final String outputCompression,
										 final String httpSession,
										 final String host,
										 final String jobId,
										 final User u,
										 final String persist,
										 final long groupId,
										 final PersistenceWrap pWrap) {
		
		
		this.sqlId = sqlId;
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
		this.u = u;
		this.persist = persist;
		this.groupId = groupId;
		this.pWrap = pWrap;

	}
	
	public void run(){
		try {
			SqlRepoExecWrapper.execSqlRepoToWebSocket(	sqlId,
														schemaUniqueUserName,
														jsonObjSqlParam,
														requestId,
														batchCount,
														comment,
														outputCompression,
														httpSession,
														jobId,
														u,
														groupId,
														persist,
														pWrap
														);
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
		}
			
			
			
		
	
	}
	

	
	
	
	
	public static boolean execPushMessageParallel(final long sqlId,
												  final List<SqlRepoDatabase> dbIdList,
												  final String jsonObjSqlParam,
												  final String outputType,
												  final String requestId,
												  final long batchCount,
												  final String comment,
												  final String outputCompression,
												  final User u,
												  final long groupId,
												  final String host,
												  final String sessionId,
												  final String persist,
												  final PersistenceWrap pWrap) {

		try {
			for(SqlRepoDatabase sqlRepoDatabase: dbIdList) {
				PushMessageSqlRepoExecThread 
				pushMessageSqlRepoExecThread = new PushMessageSqlRepoExecThread(sqlId,
																				sqlRepoDatabase.getSchemaUniqueUserName(),
																				jsonObjSqlParam,
																				outputType,
																				requestId,
																				batchCount,
																				comment,
																				outputCompression,
																				sessionId,
																				host,
																				requestId,
																				u,
																				persist,
																				groupId,
																				pWrap);
				pushMessageSqlRepoExecThread.start();
			}
			
			
			return true;
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public static boolean execPushMessageParallel(MultipleSqlOnDbList multipleSqlOnDbList,
												  final String jsonBody,
												  final String inputCompression,
												  final String outputType,
												  final String requestId,
												  final long batchCount,
												  final String comment,
												  final String outputCompression,
												  final User u,
												  final String methodName,
												  final String host,
												  final String sessionId,
												  final String persist,
												  final long groupId,
												  final PersistenceWrap pWrap) {
		try {
			for(int i=0; i < multipleSqlOnDbList.getMultipleSqlOnDblst().size(); i++){
				SqlStmToDb sqlToDb = multipleSqlOnDbList.getMultipleSqlOnDblst().get(i);

				SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.sqlRepoDatabaseMap.values().stream().filter( p -> p.getDatabaseId()==sqlToDb.getDbId()).findAny().orElse(null);
				SqlRepoDynamicSql  sqlRepoDynamicSql  = SqlRepoUtils.sqlRepoDynamicSqlMap.values().stream().filter( p -> p.getSqlId()==sqlToDb.getSqlId()).findAny().orElse(null);
				if(sqlRepoDatabase != null && sqlRepoDynamicSql != null) {

					PushMessageSqlRepoExecThread
					pushMessageSqlRepoExecThread = new PushMessageSqlRepoExecThread(sqlRepoDynamicSql.getSqlId(),
																					sqlRepoDatabase.getSchemaUniqueUserName(),
																					jsonBody,
																					outputType,
																					requestId,
																					batchCount,
																					comment,
																					outputCompression,
																					sessionId,
																					host,
																					requestId,
																					u,
																					persist,
																					groupId,
																					pWrap);
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
