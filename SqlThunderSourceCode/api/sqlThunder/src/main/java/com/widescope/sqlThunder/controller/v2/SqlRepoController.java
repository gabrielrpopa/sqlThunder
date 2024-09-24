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

package com.widescope.sqlThunder.controller.v2;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.http.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.RecordsAffected;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ParamListObj;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlParameter;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.RdbmsRepoDatabaseList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDynamicSql;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoListShortFormat;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoParam;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoParamListDetail;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlStmToDbBridgeList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticLowLevelWrapper;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.SearchSql;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload2;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.sql.ElasticSqlPayload;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2InMem;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.DataTransfer;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.ListRdbmsCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.utils.RdbmsParallelQuery;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbConnection;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistoryStatement;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistFileManagement;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistSqlList;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.rdbmsRepo.database.structuredFiles.csv.CsvWrapper;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.service.StorageService;
import com.widescope.webSockets.userStreamingPortal.PushMessageSqlRepoExecThread;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;





@CrossOrigin
@RestController
@Schema(title = "Repo Control and Execution")
public class SqlRepoController {
	
	@Autowired
	private AppConstants appConstants;
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private ElasticClusterDb elasticClusterDb;
	
	
	@Autowired
	private StorageService storageService;

	@Autowired
	private SqlRepoDb sqlRepoDb;
	


	@PostConstruct
	public void initialize() {

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/databases", method = RequestMethod.GET)
	@Operation(summary = "Get the List of available Databases")
	public ResponseEntity<RestObject> 
	getDatabase(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="databaseName", required = false) String databaseName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try	{
			final RdbmsRepoDatabaseList ret = getRdbmsRepoDatabaseList(databaseName);
			ret.blockPassword();
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@NotNull
	private static RdbmsRepoDatabaseList getRdbmsRepoDatabaseList(String databaseName) {
		List<SqlRepoDatabase> sqlRepoDatabaseList = new ArrayList<>();
		if(databaseName != null && !databaseName.isEmpty())	{
			for (Map.Entry<String, SqlRepoDatabase> element : SqlRepoUtils.sqlRepoDatabaseMap.entrySet()) {
				SqlRepoDatabase sqlRepoDatabase = new SqlRepoDatabase(element.getValue());
				if( sqlRepoDatabase.getDatabaseName().contains(databaseName) ) {
					sqlRepoDatabaseList.add(sqlRepoDatabase);
				}
			}
		}
		else {
			for (Map.Entry<String, SqlRepoDatabase> element : SqlRepoUtils.sqlRepoDatabaseMap.entrySet()) {
				SqlRepoDatabase sqlRepoDatabase = new SqlRepoDatabase(element.getValue());
				sqlRepoDatabaseList.add(sqlRepoDatabase);
			}
		}

        return new RdbmsRepoDatabaseList(sqlRepoDatabaseList);
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new database connection to the list of available Databases/schema connections")
	public ResponseEntity<RestObject> 
	addDatabase(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="databaseType", required = false) String databaseType,
					@RequestHeader(value="databaseName", required = false) String databaseName,
					@RequestHeader(value="databaseServer", required = false) String databaseServer,
					@RequestHeader(value="databasePort", required = false) String databasePort,
					@RequestHeader(value="databaseDescription", required = false) String databaseDescription,
					@RequestHeader(value="databaseWarehouseName", required = false) String databaseWarehouseName,
					@RequestHeader(value="schemaName", required = false) String schemaName,
					@RequestHeader(value="schemaService", required = false) String schemaService,
					@RequestHeader(value="schemaPassword", required = false) String schemaPassword,
					@RequestHeader(value="schemaUniqueUserName", required = false) String schemaUniqueUserName,
					@RequestHeader(value="tunnelLocalPort", required = false) String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false) String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false) String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false) String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) String tunnelRemoteHostRsaKey) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.getDatabase(connectionInfo, schemaUniqueUserName);
			if(sqlRepoDatabase == null) {
				SqlRepoUtils.addDatabase(connectionInfo, 
	                     databaseType, 
				         databaseName, 
				         databaseServer,
				         Integer.parseInt(databasePort), 
				         databaseDescription,
				         databaseWarehouseName,
				         schemaName,  
				         schemaService,
				         schemaPassword,
				         schemaUniqueUserName,
				         Integer.parseInt(tunnelLocalPort) ,
				         tunnelRemoteHostAddress,
				         Integer.parseInt(tunnelRemoteHostPort),
				         tunnelRemoteHostUser,
				         tunnelRemoteHostUserPassword,
				         tunnelRemoteHostRsaKey,
				         1);
			} else {
				
			      
				SqlRepoUtils.updateDatabase(connectionInfo, 
										sqlRepoDatabase.getDatabaseId(),
										databaseType, 
										databaseName, 
										databaseServer,
										Integer.parseInt(databasePort), 
										databaseDescription,
										databaseWarehouseName,
										schemaName,  
										schemaService,
										schemaPassword,
										schemaUniqueUserName,
										Integer.parseInt(tunnelLocalPort) ,
										tunnelRemoteHostAddress,
										Integer.parseInt(tunnelRemoteHostPort),
										tunnelRemoteHostUser,
										tunnelRemoteHostUserPassword,
										tunnelRemoteHostRsaKey,
										1);
			}
			
			
			
			sqlRepoDatabase = SqlRepoUtils.getDatabase(connectionInfo, schemaUniqueUserName);
			if(sqlRepoDatabase!=null) {
				SqlRepoUtils.sqlRepoDatabaseMap.put(sqlRepoDatabase.getSchemaUniqueUserName(), sqlRepoDatabase);
				RdbmsRepoDatabaseList lst = new RdbmsRepoDatabaseList(sqlRepoDatabase);
				return RestObject.retOKWithPayload(lst, requestId, methodName);
			} else {
				return RestObject.retExceptionWithPayload(new GenericResponse("Cannot add database connection"), requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/update", method = RequestMethod.PUT)
	@Operation(summary = "Add a new database connection to the list of available Databases/schema connections")
	public ResponseEntity<RestObject> 
	updateDatabase(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="databaseId") String databaseId,
					@RequestHeader(value="databaseType") String databaseType,
					@RequestHeader(value="databaseName") String databaseName,
					@RequestHeader(value="databaseServer") String databaseServer,
					@RequestHeader(value="databasePort") String databasePort,
					@RequestHeader(value="databaseDescription") String databaseDescription,
					@RequestHeader(value="databaseWarehouseName") String databaseWarehouseName,
					@RequestHeader(value="schemaName") String schemaName,
					@RequestHeader(value="schemaService") String schemaService,
					@RequestHeader(value="schemaPassword") String schemaPassword,
					@RequestHeader(value="schemaUniqueUserName") String schemaUniqueUserName,
					@RequestHeader(value="tunnelLocalPort", required = false, defaultValue = "") String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false, defaultValue = "") String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false, defaultValue = "") String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false, defaultValue = "") String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false, defaultValue = "") String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false, defaultValue = "") String tunnelRemoteHostRsaKey,
					@RequestHeader(value="isActive", required = false) String isActive) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if( SqlRepoUtils.sqlRepoDatabaseMap.containsKey(schemaUniqueUserName) )	{
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl,"Schema Name conflict"));
			}
			
			
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			      
			SqlRepoUtils.updateDatabase(connectionInfo, 
									Integer.parseInt(databaseId),
				                    databaseType, 
							        databaseName, 
							        databaseServer,
							        Integer.parseInt(databasePort), 
							        databaseDescription,
							        databaseWarehouseName,
							        schemaName,  
							        schemaService,
							        schemaPassword,
							        schemaUniqueUserName,
							        Integer.parseInt(tunnelLocalPort) ,
							        tunnelRemoteHostAddress,
							        Integer.parseInt(tunnelRemoteHostPort),
							        tunnelRemoteHostUser,
							        tunnelRemoteHostUserPassword,
							        tunnelRemoteHostRsaKey,
							        Integer.parseInt(isActive) );
			
			SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.getDatabase(connectionInfo, schemaUniqueUserName);
			SqlRepoUtils.sqlRepoDatabaseMap.put(sqlRepoDatabase.getSchemaUniqueUserName(), sqlRepoDatabase);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete database/schema connection")
	public ResponseEntity<RestObject> 
	databaseDelete(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="databaseId", required = false) String databaseId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoDatabase sqlRepoDatabase1 = SqlRepoUtils.getDatabase(connectionInfo, Integer.parseInt(databaseId));
			
			SqlRepoUtils.deleteDatabase(connectionInfo, Integer.parseInt(databaseId)) ;
			SqlRepoDatabase sqlRepoDatabase2 = SqlRepoUtils.getDatabase(connectionInfo, Integer.parseInt(databaseId));
			if(sqlRepoDatabase2 == null) {
				SqlRepoUtils.sqlRepoDatabaseMap.remove(sqlRepoDatabase1.getSchemaUniqueUserName());
			}
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/connection/validate:new-connection", method = RequestMethod.GET)
	@Operation(summary = "Validate a new Database/schema connection")
	public ResponseEntity<RestObject> 
	validateDatabase(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="databaseType", required = false) String databaseType,
						@RequestHeader(value="databaseName", required = false) String databaseName,
						@RequestHeader(value="databaseServer", required = false) String databaseServer,
						@RequestHeader(value="databasePort", required = false) String databasePort,
						@RequestHeader(value="databaseDescription", required = false) String databaseDescription,
						@RequestHeader(value="databaseWarehouseName", required = false) String databaseWarehouseName,
						@RequestHeader(value="schemaName", required = false) String schemaName,
						@RequestHeader(value="schemaService", required = false) String schemaService,
						@RequestHeader(value="schemaPassword", required = false) String schemaPassword,
						@RequestHeader(value="schemaUniqueUserName", required = false) String schemaUniqueUserName,
						@RequestHeader(value="account", required = false) String account,
						@RequestHeader(value="other_parameter", required = false) String otherParameter,
						@RequestHeader(value="tunnelLocalPort", required = false) String tunnelLocalPort,
						@RequestHeader(value="tunnelRemoteHostAddress", required = false) String tunnelRemoteHostAddress,
						@RequestHeader(value="tunnelRemoteHostPort", required = false) String tunnelRemoteHostPort,
						@RequestHeader(value="tunnelRemoteHostUser", required = false) String tunnelRemoteHostUser,
						@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) String tunnelRemoteHostUserPassword,
						@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) String tunnelRemoteHostRsaKey) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			DbConnectionInfo connectionDetailInfo 
			= DbConnectionInfo.makeDbConnectionInfo(databaseType, 
													  schemaService, 
											          schemaName, 
											          databaseName, 
											          databaseServer, 
											          databasePort, 
											          schemaService, 
											          schemaPassword, 
											          databaseDescription,
											          databaseWarehouseName,
											          account,
											          otherParameter,
											          tunnelLocalPort,
											          tunnelRemoteHostAddress,
											          tunnelRemoteHostPort,
											          tunnelRemoteHostUser,
											          tunnelRemoteHostUserPassword,
											          tunnelRemoteHostRsaKey);
		
			
			
			boolean IsOK = DbUtil.checkConnection(connectionDetailInfo);
			if(IsOK ) {
				return RestObject.retOKWithPayload(new GenericResponse( Boolean.toString(IsOK)), requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Connection to database is invalid. Cannot connect to database");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/connection/validate:connection", method = RequestMethod.GET)
	@Operation(summary = "Validate an existing Database/schema connection")
	public ResponseEntity<RestObject>
	validateSqlRepoDatabase(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="databaseName", required = false) String databaseName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			RdbmsRepoDatabaseList sqlRepoDatabaseList = new RdbmsRepoDatabaseList();

			if(databaseName != null && !databaseName.isEmpty())	{
				for (Map.Entry<String, SqlRepoDatabase> element : SqlRepoUtils.sqlRepoDatabaseMap.entrySet()) {
					SqlRepoDatabase sqlRepoDatabase = element.getValue();
					if( sqlRepoDatabase.getDatabaseName().contains(databaseName) ) {
						sqlRepoDatabaseList.addSqlRepoDatabase(sqlRepoDatabase);
					}
			    }
			}
			else {
				for (Map.Entry<String, SqlRepoDatabase> element : SqlRepoUtils.sqlRepoDatabaseMap.entrySet()) {
					SqlRepoDatabase sqlRepoDatabase = element.getValue();
					sqlRepoDatabaseList.addSqlRepoDatabase(sqlRepoDatabase);
			    }
			}

			if(sqlRepoDatabaseList.getSqlRepoDatabaseList().size() == 1) {
				DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo_(sqlRepoDatabaseList.getSqlRepoDatabaseList().get(0));
				boolean IsOK = DbUtil.checkConnection(connectionDetailInfo);
				if(IsOK ) {
					return RestObject.retOK(requestId, methodName);
				} else {
					return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], "Connection to database is invalid. Cannot connect to database", AppLogger.ctrl));
				}
			} else {
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Connection to database is invalid. Cannot connect to database"));
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}











	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/reload", method = RequestMethod.GET)
	@Operation(summary = "Reload Repo List")
	public ResponseEntity<RestObject> 
	reloadSqlRepo(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SqlRepoUtils.populateRepo(appConstants.getActiveRepo());
			SqlRepoList sqlRepoList = SqlRepoList.setSqlRepoList(SqlRepoUtils.sqlRepoDynamicSqlMap);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sql:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new Sql statement to the repo")
	public ResponseEntity<RestObject> 
	addSql(	@RequestHeader(value="user") String user,
			@RequestHeader(value="session") String session,
			@RequestHeader(value="sqlType", defaultValue = "QUERY") String sqlType,
			@RequestHeader(value="sqlReturnType", defaultValue = "RECORDSET") String sqlReturnType,
			@RequestHeader(value="sqlCategory") String sqlCategory,
			@RequestHeader(value="sqlName") String sqlName,
			@RequestHeader(value="sqlDescription") String sqlDescription,
			@RequestHeader(value="sqlContent") String sqlContent,
			@RequestHeader(value="execution") String execution,
			@RequestHeader(value="active", defaultValue = "1") String active) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String requestId = StaticUtils.getUUID();
		if( !authUtil.isSessionAuthenticated(user, session) )	{
			return RestObject.retAuthError(requestId);
		}
		
		
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);

			SqlRepoUtils.addSql(connectionInfo, 
							sqlType, 
							sqlReturnType, 
							sqlCategory, 
							sqlName, 
							sqlDescription, 
							sqlContent, 
							execution, 
							Integer.parseInt(active) );
			
			List<SqlRepoDynamicSql> sqlList = SqlRepoUtils.getSql(connectionInfo, sqlName);
			if(sqlList.size() == 1) {
				SqlRepoUtils.sqlRepoDynamicSqlMap.put( sqlList.get(0).getSqlId() , sqlList.get(0));
				SqlRepoList sqlRepoList = new SqlRepoList(sqlList);
				return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
			} else {
				SqlRepoUtils.populateDynamicSql(connectionInfo);
				SqlRepoList sqlRepoList = new SqlRepoList(sqlList);
				return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sql:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Add a new Sql statement to the repo")
	public ResponseEntity<RestObject> 
	deleteSql(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="sqlId") String sqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			SqlRepoUtils.deleteSqlToDbBridge(connectionInfo, Long.parseLong(sqlId));
			SqlRepoUtils.deleteAllSqlParam(connectionInfo, Long.parseLong(sqlId));
			SqlRepoUtils.deleteSql(connectionInfo, Long.parseLong(sqlId));
			SqlRepoUtils.sqlRepoDynamicSqlMap.remove( Long.parseLong(sqlId));
			return RestObject.retOKWithPayload(new GenericResponse("New Sql Is Updated"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sql:update", method = RequestMethod.PUT)
	@Operation(summary = "Update Sql statement to the repo")
	public ResponseEntity<RestObject> 
	updateSql(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="sqlId") String sqlId,
				@RequestHeader(value="databaseId") String databaseId,
				@RequestHeader(value="sqlType", defaultValue = "QUERY") String sqlType,
				@RequestHeader(value="sqlReturnType", defaultValue = "RECORDSET") String sqlReturnType,
				@RequestHeader(value="sqlCategory") String sqlCategory,
				@RequestHeader(value="sqlName") String sqlName,
				@RequestHeader(value="sqlDescription") String sqlDescription,
				@RequestHeader(value="sqlContent") String sqlContent,
				@RequestHeader(value="execution") String execution,
				@RequestHeader(value="active", defaultValue = "1") String active) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			SqlRepoUtils.updateSql(connectionInfo, 
							Integer.parseInt(sqlId),
							Integer.parseInt(databaseId), 
							sqlType, 
							sqlReturnType, 
							sqlCategory, 
							sqlName, 
							sqlDescription, 
							sqlContent, 
							execution, 
							Integer.parseInt(active) );
			
			SqlRepoUtils.populateDynamicSql(connectionInfo);
			return RestObject.retOKWithPayload(new GenericResponse("New Sql Is Updated"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	  
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sqlparam:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Sql Param to Sql Statement")
	public ResponseEntity<RestObject> 
	addSqlParam(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="sqlId") String sqlId,
					@RequestHeader(value="sqlParamName") String sqlParamName,
					@RequestHeader(value="sqlParamType", defaultValue = "STRING") String sqlParamType,
					@RequestHeader(value="sqlParamDefaultValue") String sqlParamDefaultValue,
					@RequestHeader(value="sqlParamPosition", defaultValue = "IN") String sqlParamPosition,
					@RequestHeader(value="sqlParamOrder", defaultValue = "1") String sqlParamOrder,
					@RequestHeader(value="sqlParamOriginTbl") String sqlParamOriginTbl,
					@RequestHeader(value="sqlParamOriginCol") String sqlParamOriginCol) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);

			SqlRepoUtils.addSqlParam(	connectionInfo, 
									Integer.parseInt(sqlId), 
									sqlParamName, 
									sqlParamDefaultValue, 
									sqlParamType, 
									sqlParamPosition, 
									Integer.parseInt(sqlParamOrder), 
									sqlParamOriginTbl, 
									sqlParamOriginCol);
			
			
			
			SqlRepoUtils.populateDynamicSql(connectionInfo);
			
			List<SqlRepoParam> lst  = SqlRepoUtils.getSqlParam(connectionInfo, sqlParamName);
			SqlRepoParamListDetail sqlRepoParamListDetail = new SqlRepoParamListDetail(lst);
			return RestObject.retOKWithPayload(sqlRepoParamListDetail, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sqlparam:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Sql Param")
	public ResponseEntity<RestObject> 
	deleteSqlParam(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="sqlId") String sqlId,
					@RequestHeader(value="sqlParamId") String sqlParamId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);

			SqlRepoUtils.deleteSqlParam(connectionInfo, 
									Integer.parseInt(sqlId), 
									Integer.parseInt(sqlParamId));
			
			
			
			SqlRepoUtils.populateDynamicSqlParam(connectionInfo);
			return RestObject.retOKWithPayload(new GenericResponse("Sql Param Is Deleted"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Repo List")
	public ResponseEntity<RestObject> 
	getSqlRepoList(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="filter", required = false, defaultValue = "") String filter,
					@RequestHeader(value="databaseId", required = false, defaultValue = "") String databaseId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			List<SqlRepoDynamicSql> ret = new ArrayList<>();
			if(!filter.isEmpty() &&  !databaseId.trim().isEmpty()) {
				// gather all records that contain like text
				List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
						.stream()
						.filter(element -> element.getSqlName().contains(filter) ||
								           element.getSqlContent().contains(filter) ||
								           element.getSqlDescription().contains(filter) )
						.toList();
				
				// use only those record that have allowed to run on db id
				try {
					final long dbId = Integer.parseInt(databaseId);
					for (SqlRepoDynamicSql entry : flt ) {
						if(entry.getSqlRepoDatabaseSchemaBridgeList().stream().anyMatch(element -> element.getDatabaseId() == dbId)) {
							ret.add(entry);
				        }
					}
				} catch(Exception ex) {
					ret.addAll(flt);
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			else if(filter.isEmpty() &&  !databaseId.trim().isEmpty()) {
				try {
					final long dbId = Integer.parseInt(databaseId);
					
					List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
							.stream()
							.filter(element -> element.getSqlRepoDatabaseSchemaBridgeList().stream().anyMatch(element2 -> element2.getDatabaseId() == dbId))
							.toList();
					ret.addAll(flt);
					
				} catch(Exception ex) {
					return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
				}
				
			} else if(!filter.isEmpty()) {
				// gather all records that contain like text
				List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
																			.stream()
																			.filter(element -> element.getSqlName().contains(filter) ||
																					           element.getSqlContent().contains(filter) ||
																					           element.getSqlDescription().contains(filter) )
																			.toList();
				ret.addAll(flt);
				
			} else {
				ret = new ArrayList<>(SqlRepoUtils.sqlRepoDynamicSqlMap.values());
			}
			
			SqlRepoList sqlRepoList = new SqlRepoList(ret);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Repo List Without Params")
	public ResponseEntity<RestObject> 
	getSqlRepoListWithNoParams(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="filter", required = false) String filter,
								@RequestHeader(value="databaseId", required = false, defaultValue = "") String databaseId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			List<SqlRepoDynamicSql> ret = new ArrayList<>();
			if(!filter.isEmpty() &&  !databaseId.trim().isEmpty()) {
				// gather all records that contain like text
				List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
						.stream()
						.filter(element -> element.getSqlName().contains(filter) ||
								           element.getSqlContent().contains(filter) ||
								           element.getSqlDescription().contains(filter) )
						.toList();
				
				// use only those record that have allowed to run on db id
				try {
					final long dbId = Integer.parseInt(databaseId);
					for (SqlRepoDynamicSql entry : flt ) {
						if(entry.getSqlRepoDatabaseSchemaBridgeList().stream().anyMatch(element -> element.getDatabaseId() == dbId)) {
							ret.add(entry);
				        }
					}
				} catch(Exception ex) {
					ret.addAll(flt);
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			else if(filter.isEmpty() &&  !databaseId.trim().isEmpty()) {
				try {
					final long dbId = Integer.parseInt(databaseId);
					
					List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
							.stream()
							.filter(element -> element.getSqlRepoDatabaseSchemaBridgeList().stream().anyMatch(element2 -> element2.getDatabaseId() == dbId))
							.toList();
					ret.addAll(flt);
					
				} catch(Exception ex) {
					return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
				}
				
			} else if(!filter.isEmpty()) {
				// gather all records that contain like text
				List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
																			.stream()
																			.filter(element -> element.getSqlName().contains(filter) ||
																					           element.getSqlContent().contains(filter) ||
																					           element.getSqlDescription().contains(filter) )
																			.toList();
				ret.addAll(flt);
				
			} else {
				ret = new ArrayList<>(SqlRepoUtils.sqlRepoDynamicSqlMap.values());
			}
			
			SqlRepoList newRet = new SqlRepoList(ret);
			for (SqlRepoDynamicSql sqlRepoDynamicSql : newRet.getSqlRepoList())	{
				sqlRepoDynamicSql.getParamList().clear();
			}
			return RestObject.retOKWithPayload(newRet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sql/summary", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Repo List Summary Format")
	public ResponseEntity<RestObject> 
	getSqlRepoListSummary(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="filter", required = false) String filter) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SqlRepoListShortFormat sqlRepoList = SqlRepoListShortFormat.setSqlRepoListShortFormat(SqlRepoUtils.sqlRepoDynamicSqlMap, filter);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sql/detail", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Detail")
	public ResponseEntity<RestObject> 
	getSqlDetail(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="sqlID", required = false) String sqlId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			List<SqlRepoParam> listOfParams = SqlRepoUtils.getSqlParams(connectionInfo, Long.parseLong(sqlId));
			SqlRepoDynamicSql sqlRepoDynamicSql  = SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(sqlId) );
			
			// !!!! -> We are having a potential bug, so for now we try to mitigate that 
			if(sqlRepoDynamicSql.getSqlRepoParamList().isEmpty() && !listOfParams.isEmpty()) {
				sqlRepoDynamicSql.clearSqlRepoParamList();
				sqlRepoDynamicSql.setSqlRepoParamList(listOfParams);
				sqlRepoDynamicSql.clearParamList();
				for (SqlRepoParam sqlRepoParam : listOfParams) {
					SqlParameter sqlParam = new SqlParameter(sqlRepoParam);
					sqlRepoDynamicSql.addParamList(sqlParam);
				}
				SqlRepoUtils.sqlRepoDynamicSqlMap.put(Long.valueOf(sqlId), sqlRepoDynamicSql );
			}

			Map<Long, SqlRepoDynamicSql> _sqlRepoDynamicSqlMap = new HashMap<Long, SqlRepoDynamicSql>();
			_sqlRepoDynamicSqlMap.put(Long.valueOf(sqlId) , sqlRepoDynamicSql);
			SqlRepoList sqlRepoList = SqlRepoList.setSqlRepoList(_sqlRepoDynamicSqlMap);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/param/detail", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Param List Detail")
	public ResponseEntity<RestObject> 
	getSqlParamListDetail(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="sqlID", required = false) String sqlID) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SqlRepoParamListDetail 
			sqlRepoParamListDetail = SqlRepoParamListDetail.setSqlRepoParamListDetail(SqlRepoUtils.sqlRepoDynamicSqlMap, 
																					   Long.valueOf(sqlID));
			
			return RestObject.retOKWithPayload(sqlRepoParamListDetail, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/params", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Param List")
	public ResponseEntity<RestObject> 
	getSqlParamList(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="sqlID", required = false) String sqlID) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			ParamObj paramObj = new ParamObj();
			paramObj.setPList(SqlRepoUtils.sqlRepoDynamicSqlMap, Long.valueOf(sqlID));
			return RestObject.retOKWithPayload(paramObj, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/param/bulk", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Param List for Bulk DML. DQLs and DDLs are excluded")
	public ResponseEntity<RestObject>
	getSqlParamListBulk(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="sqlID", required = false) String sqlID) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SqlRepoDynamicSql sqlRepoDynamicSql = SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(sqlID));
			if(sqlRepoDynamicSql == null) {
				return RestObject.retException(requestId, methodName, "SQL ID not found");
			}

			ParamListObj ret = SqlQueryRepoUtils.getBulkParamToPopulateJson(Integer.parseInt(sqlID));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sqlToDb/mapping:update", method = RequestMethod.PUT)
	@Operation(summary = "Map a sql statement to multiple databases")
	public ResponseEntity<RestObject> 
	addSqlToDbMapping(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="sqlId", required = false) String sqlId,
						@RequestHeader(value="dbId", required = false) String dbId,
						@RequestHeader(value="active", required = false) String active) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			int sqlID_ = Integer.parseInt(sqlId);
			int dbID_ = Integer.parseInt(dbId);
			int active_ = Integer.parseInt(active);
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoUtils.mergeSqlToDbBridge(connectionInfo, sqlID_, dbID_, active_);
			SqlRepoUtils.populateSqlToDbBridge(connectionInfo);
			SqlStmToDbBridgeList ret = SqlRepoUtils.getSqlToDbBridge(connectionInfo, sqlID_, dbID_);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sqlToDb/mapping:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete mapping of sql statement to multiple databases")
	public ResponseEntity<RestObject> 
	deleteSqlToDbMapping(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="sqlId", required = false) String sqlId,
							@RequestHeader(value="dbId", required = false) String dbId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			int sqlID_ = Integer.parseInt(sqlId);
			int dbID_ = Integer.parseInt(dbId);
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoUtils.deleteSqlToDbBridge(connectionInfo, sqlID_, dbID_);
			SqlRepoUtils.populateSqlToDbBridge(connectionInfo);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/sqlToDb/mapping:list", method = RequestMethod.GET)
	@Operation(summary = "Get mapping of sql statements to databases")
	public ResponseEntity<RestObject> 
	listSqlToDbMapping(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="sqlId") String sqlId,
						@RequestHeader(value="dbId", required = false) String dbId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			int sqlID_ = -1;
			int dbID_ = -1;
			try { sqlID_ = Integer.parseInt(sqlId); } catch (Exception ignored) {} 
			try { dbID_ = Integer.parseInt(dbId); } catch (Exception ignored) {}
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlStmToDbBridgeList ret = SqlRepoUtils.getSqlToDbBridge(connectionInfo, sqlID_, dbID_);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/execute/adhoc", method = RequestMethod.POST, consumes = "text/plain")
	@Operation(summary = "Execute Adhoc Sql")
	public ResponseEntity<RestObject> 
	executeAdhocSql(@RequestHeader(value="user") String user,
					@RequestHeader(value="session") String session,
					@RequestHeader(value="schemaUniqueName") String schemaUniqueName,
					@RequestHeader(value="outputCompression", required = false) String outputCompression,
					@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
					@RequestHeader(value="forceNoPush", required = false, defaultValue = "Y") String forceNoPush,
					@RequestHeader(value="sqlType", required = false, defaultValue = "") String sqlType, /*DQL/DML/DDL*/
					@RequestHeader(value="comment", required = false) String comment,
					@RequestHeader(value="sqlName") String sqlName,
					@RequestHeader(value="requestId") String requestId,
					@RequestBody String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(sqlContent == null || sqlContent.isEmpty()) {
			return RestObject.retException(requestId, methodName, "SQL Content missing", "SQL Content missing");
		}
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
			//Check that user is connected to websocket
			if( user != null && 
				!user.isEmpty() && 
				!user.isBlank() && 
				WebSocketsWrapper.isUser(user) && forceNoPush.equals("N") ) {
					//send data via websocket
					sqlRepoExecReturn = SqlRepoExecWrapper.execAdhocSqlToWebSocket(	sqlContent,
																					sqlName,
																					sqlType,
																					schemaUniqueName,
																					requestId,
																					comment,
																					session,
																					requestId,
																					userId,
																					user,
																					persist
																					);
					sqlRepoExecReturn.getResults().setStreaming("Y");
					
						
			} else {
				// return data (full payload)
				sqlRepoExecReturn  = SqlRepoExecWrapper.execAdhocSqlForResultQuery(	sqlContent,
																					schemaUniqueName,
																					requestId,
																					comment,
																					sqlName,
																					sqlType,
																					user,
																					userId,
																					persist);
				
				
						
				sqlRepoExecReturn.getResults().setStreaming("N");		
						
			}
			
			
			String mainFolder = appConstants.getHistStatementPath();
			HistFileManagement.addNewStatement(userId, sqlContent, comment, timeStamp, mainFolder, "adhoc", "rdbms");
				
			// Now check the request for Output payload compression
			if(outputCompression!= null && !outputCompression.isEmpty()) {
				sqlRepoExecReturn.setResults(SqlQueryRepoUtils.compressPayload(outputCompression, sqlRepoExecReturn.getResults()));
			}
			
			if(sqlRepoExecReturn.getErrorCode() == 0) {
				return RestObject.retOKWithPayload(sqlRepoExecReturn.getResults(), requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Error: Cannot find SQL Type");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/generate:script", method = RequestMethod.PUT)
	@Operation(summary = "Get the List of all user tables in database schema")
	public ResponseEntity<RestObject> 
	generateCreateScriptForTable(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromRdbmsSchemaUniqueName", required = false) String fromRdbmsSchemaUniqueName,
									@RequestHeader(value="tableName", required = false) String tableName,
									@RequestBody String sqlContent) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String script = SqlMetadataWrapper.createRdbmsTableStm(fromRdbmsSchemaUniqueName, sqlContent, tableName);
			return RestObject.retOKWithPayload(new GenericResponse(script), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/tables", method = RequestMethod.GET)
	@Operation(summary = "Get the List of all user tables in database schema")
	public ResponseEntity<RestObject>
	getDatabaseTables(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="connectionUniqueName", required = false) String connectionUniqueName,
						@RequestHeader(value="schema", required = false) String schema) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			TableList ret = SqlQueryRepoUtils.getTableList(connectionUniqueName, schema);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/database/schemas", method = RequestMethod.GET)
	@Operation(summary = "Get the List of database schemas")
	public ResponseEntity<RestObject> 
	getDatabaseSchemas( @RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="connectionUniqueName", required = false) String connectionUniqueName) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			TableList ret = SqlQueryRepoUtils.getSchemaList(connectionUniqueName);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/execute:singleDb", 
					method = RequestMethod.POST, 
					consumes = "application/json", 
					produces={MediaType.APPLICATION_JSON_VALUE}
					)
	@Operation(summary = "Execute Sql Repo")
	public ResponseEntity<RestObject> 
	executeSqlRepo(	@RequestHeader(value="user") String user,
					@RequestHeader(value="session") String session,
					@RequestHeader(value="sqlID") String sqlID,
					@RequestHeader(value="schemaUniqueName") String schemaUniqueName,
					@RequestHeader(value="outputCompression", required = false) String outputCompression,
					@RequestHeader(value="outputType", defaultValue = "JSON") String outputType,
					@RequestHeader(value="batchCount", required = false, defaultValue = "1") String batchCount,
					@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
					@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
					@RequestHeader(value="requestId", required = false, defaultValue = "") String requestId,
					@Valid @RequestBody String jsonObjSqlParam)  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			SqlRepoExecReturn sqlRepoExecReturn = new SqlRepoExecReturn();
			//Check that user is connected to websocket
			if( user != null && !user.isEmpty() && 
					!user.isBlank() && 
					WebSocketsWrapper.isUser(user)) {
				//send data via websocket
				sqlRepoExecReturn = SqlRepoExecWrapper.execSqlRepoToWebSocket(	sqlID,
																				schemaUniqueName,
																				jsonObjSqlParam,
																				outputType,
																				requestId,
																				batchCount,
																				comment,
																				outputCompression,
																				session,
																				requestId,
																				user,
																				u.getId(),
																				persist
																				);
				sqlRepoExecReturn.getResults().setStreaming("Y");
			} else {
				// return data as HTTP payload
				sqlRepoExecReturn  = SqlRepoExecWrapper.execSqlRepoForResultQuery(sqlID,
															schemaUniqueName,
															jsonObjSqlParam,
															outputType,
															requestId,
															batchCount,
															comment,
															outputCompression,
															user,
															u.getId(),
															persist);
				
				
				sqlRepoExecReturn.getResults().setStreaming("N");
			}
			
			if(sqlRepoExecReturn.getErrorCode() == 0) {
				return RestObject.retOKWithPayload(sqlRepoExecReturn.getResults(), requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Error Executing Sql");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/execute:multipleDb", method = RequestMethod.POST, consumes = "application/json")
	@Operation(summary = "Execute Sql On multiple DBs and aggregate results")
	public ResponseEntity<RestObject> 
	executeSqlRepoMultiple(	@RequestHeader(value="user") String user,
							@RequestHeader(value="session") String session,
							@RequestHeader(value="sqlID") String sqlID,
							@RequestHeader(value="outputCompression", required = false) String outputCompression,
							@RequestHeader(value="outputType", defaultValue = "JSON") String outputType,
							@RequestHeader(value="batchCount", required = false, defaultValue = "1") String batchCount,
							@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
							@RequestHeader(value="dbIdList", required = false, defaultValue = "") String dbIdList,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
							@RequestHeader HttpHeaders incomingHeaders,
							@RequestBody String jsonObjSqlParam)  {
		
		String host = Objects.requireNonNull(incomingHeaders.getHost()).getHostName();
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		RestObject transferableObject = new RestObject(null, methodName, requestId);

		try	{

			String jsonBody = jsonObjSqlParam;
			// Now check if body was compressed
			transferableObject.setRequestId(requestId);
			if( user != null && !user.isEmpty() && !user.isBlank() && WebSocketsWrapper.isUser(user)) {
				List<String> lstString = Arrays.asList(dbIdList.split(",", -1));
				List<Integer> lstInts = StaticUtils.convertStringListToIntList(lstString, Integer::parseInt);
				List<SqlRepoDatabase> lstSqlRepoDatabase = SqlRepoUtils.sqlRepoDatabaseMap.values()
															.stream()
															.filter( s -> lstInts.contains(s.getDatabaseId()))
															.collect(Collectors.toList());
				User u = authUtil.getUser(user);
				PushMessageSqlRepoExecThread.execPushMessageParallel(	sqlID,
																		lstSqlRepoDatabase,
																		jsonObjSqlParam,
																		outputType,
																		requestId,
																		batchCount,
																		comment,
																		outputCompression,
																		user,
																		host,
																		session,
																		u.getId(),
																		persist
																		);
				
				return RestObject.retOKWithPayload(new GenericResponse(null) , requestId, methodName);
			} else {
				SqlRepoExecReturn sqlRepoExecReturn = SqlRepoExecWrapper.execSqlRepoParallel(	sqlID,	
																								dbIdList, 
																								jsonBody,	
																								batchCount,
																								user,
																								persist);
				return RestObject.retOKWithPayload(sqlRepoExecReturn.getResults(), requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/execute/adhoc/multipledb:aggregate", method = RequestMethod.PUT, consumes = "text/plain")
	@Operation(summary = "Execute Sql On multiple DBs and aggregate results")
	public ResponseEntity<RestObject> 
	executeSqlAdhocMultiple(@RequestHeader(value="user") String user,
							@RequestHeader(value="session") String session,
							@RequestHeader(value="requestId") String requestId,
							@RequestBody String strObj)  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		ListRdbmsCompoundQuery listRdbmsCompoundQuery = ListRdbmsCompoundQuery.toListRdbmsCompoundQuery(strObj);
		try	{
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
			List<RdbmsTableSetup> lst = RdbmsParallelQuery.executeQueryInParallel( listRdbmsCompoundQuery.getLst(),  listRdbmsCompoundQuery.getTableName());
			H2InMem h2InMem = new H2InMem("", inMemDbName, "QUERY", session, requestId, user);
			DataTransfer dataTransfer = h2InMem.loadRdbmsQueriesInMem(lst);
			ResultQuery ret  = SqlQueryExecUtils.execStaticQuery(h2InMem.getConnection(), listRdbmsCompoundQuery.getSqlAggregator());
			h2InMem.removeInMemDb();
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/migrate", method = RequestMethod.POST, consumes = "application/json")
	@Operation(summary = "Create and Insert table from Sql Repo execution")
	public ResponseEntity<RestObject> 
	executeSqlRepoToMigrateData(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="jobID", required = false) String jobID,
									@RequestHeader(value="sqlID") String sqlID,
									@RequestHeader(value="sourceConnectionName") String sourceConnectionName,
									@RequestHeader(value="inputCompression", required = false) String inputCompression,
									@RequestHeader(value="destinationConnectionName") String destinationConnectionName,
									@RequestHeader(value="destinationSchema", required = false) String destinationSchema,
									@RequestHeader(value="destinationTable", required = false) String destinationTable,
									@RequestBody String jsonObjSqlParam) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		int countInserted;
		try	{
			SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(Long.valueOf(sqlID));
			if(!queryObj.getSqlType().equalsIgnoreCase("QUERY")) {
				return RestObject.retException(requestId, methodName, "Error: not a query");
			}
			String jsonBody = jsonObjSqlParam;
			// Now check if body was compressed 
			if(inputCompression!= null && !inputCompression.isEmpty())	{
				jsonBody = SqlQueryRepoUtils.decompressString(inputCompression, jsonObjSqlParam);
			}
			
			
			ParamObj paramObj = ParamObj.convertStringToParamObj(jsonBody);
			
			// Check if it's a single row SQL
			if(paramObj != null) {
				countInserted = SqlQueryRepoUtils.execDynamicQueryAndTransferData(	Integer.valueOf(sqlID).intValue(), 
																				sourceConnectionName,
																				destinationConnectionName, 
																				destinationSchema,
																				destinationTable,
																				paramObj);
			} else {
				return RestObject.retException(requestId, methodName, "Error Executing Sql Repo");
			}
			
			return RestObject.retOKWithPayload(new GenericResponse("Inserted: " + String.valueOf(countInserted) + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	/*                    SQL Snapshots                                     */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/snapshot:history", method = RequestMethod.GET)
	@Operation(summary = "Get a list of snapshots to visualize")
	public ResponseEntity<RestObject> 
	getRdbmsSnapshotHistory(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="ownerId") String ownerId,
							@RequestHeader(value="startIime") String startIime,
							@RequestHeader(value="endTime") String endTime,
							@RequestHeader(value="sqlStatement", required = false) String sqlStatement) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SnapshotDbRepo snp = new SnapshotDbRepo();
			SnapshotDbRecordList ret = snp.getUserSnapshotDb(	Long.parseLong(ownerId), 
															Long.parseLong(startIime), 
															Long.parseLong(endTime),
															sqlStatement);
			
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/snapshot:get", method = RequestMethod.GET)
	@Operation(summary = "Get snapshot to visualize")
	public ResponseEntity<RestObject> 
	getRdbmsSnapshot(@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="snapshotId") String snapshotId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			SnapshotDbRepo snp = new SnapshotDbRepo();
			SnapshotDbRecord snapshotDbRecord = snp.getSnapshot(Long.parseLong(snapshotId));
			
			String filePath = "./snapshots/" + snapshotDbRecord.getUserId() + "/" + snapshotDbRecord.getFileName();
			ResultQuery resultQuery = ResultQuery.toResultQuery(new String(FileUtilWrapper.readFile(filePath)));
			return RestObject.retOKWithPayload(resultQuery, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/snapshot:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete snapshots")
	public ResponseEntity<RestObject> 
	deleteSnapshot(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="snapshotId") String snapshotId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SnapshotDbRepo snp = new SnapshotDbRepo();
			SnapshotDbRecord snapshotDbRecord = snp.getSnapshot(Long.parseLong(snapshotId));
			snp.deleteSnapshotDbAccessForSnapshot(Long.parseLong(snapshotId));
			snp.deleteSnapshotDb(Long.parseLong(snapshotId));
			String filePath = "./snapshots/" + String.valueOf(snapshotDbRecord.getUserId()) + "/" + snapshotDbRecord.getFileName();
			FileUtilWrapper.deleteFile(filePath);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	/**
	 * Data Migration / Copy from Mongo/Elastic to RDBMS table
	 */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/embedded/adhoc:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy records from Embedded Sql to RDBMS table")
	public ResponseEntity<RestObject> 
	copyEmbeddedSqlResultToRdbmsTable(	@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="fromEmbeddedType", defaultValue = "H2") String fromEmbeddedType,
										@RequestHeader(value="fromClusterId") String fromClusterId,
										@RequestHeader(value="fromEmbeddedDatabaseName") String fromEmbeddedDatabaseName,
										@RequestHeader(value="fromEmbeddedSchemaName") String fromEmbeddedSchemaName,
										@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
										@RequestBody (required = true) String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			H2Static h2Db = new H2Static(Long.parseLong(fromClusterId), fromEmbeddedDatabaseName );
			TableFormatMap recordSet= h2Db.execStaticQueryWithTableFormat(sqlContent);
			RecordsAffected recordsAffected = SqlQueryRepoUtils.insertBulkIntoTable(toRdbmsConnectionName,
																					toRdbmsSchemaName,
																					toRdbmsTableName,
																					recordSet.getRows(),
																					recordSet.getMetadata());

			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/mongodb/search:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy records from Mongodb simple search to RDBMS table")
	public ResponseEntity<RestObject> 
	copyMongoSimpleSearchResultToRdbmsTable(@RequestHeader(value="requestId") String requestId,
											@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
											@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
											@RequestHeader(value="fromCollectionName") String fromCollectionName,
											@RequestHeader(value="itemToSearch") String itemToSearch,
											@RequestHeader(value="valueToSearch") String valueToSearch,
											@RequestHeader(value="valueToSearchType") String valueToSearchType,
											@RequestHeader(value="operator", defaultValue = "$eq") String operator,
											@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
											@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
											@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
											@RequestHeader(value="batchCount", defaultValue = "0") String batchCount) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromClusterUniqueName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(),
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());

			MongoResultSet mongoResultSet = MongoGet.searchDocument(fromMongoDbConnection, 
																	fromMongoDbName, 
																	fromCollectionName,
																	itemToSearch,  
																	valueToSearch,
																	operator,
																	valueToSearchType,
																	true,
																	false/*determine metadata is false*/) ;
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet())); 
			
			fromMongoDbConnection.disconnect();
			
			RecordsAffected recordsAffected = 
					SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
														toRdbmsSchemaName,
														toRdbmsTableName, 
														MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
														mongoResultSet.getMetadata());
			

			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/mongodb/search:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from another Mongodb collection(s) range search")
	public ResponseEntity<RestObject> 
	copyMongoRangeSearchResultToRdbmsTable(	@RequestHeader(value="requestId") String requestId,
											@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
											@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
											@RequestHeader(value="fromCollectionName") String fromCollectionName,
											@RequestHeader(value="itemToSearch") String itemToSearch,
											@RequestHeader(value="fromValue") String fromValue,
											@RequestHeader(value="toValue") String toValue,
											@RequestHeader(value="valueSearchType") String valueSearchType,
											@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
											@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
											@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
											@RequestHeader(value="batchCount", defaultValue = "0") String batchCount) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromClusterUniqueName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());
			
			MongoResultSet mongoResultSet = MongoGet.searchDocumentRange(	fromMongoDbConnection, 
																			fromMongoDbName, 
																			fromCollectionName,
																			itemToSearch,  
																			fromValue,
																			toValue,
																			valueSearchType,
																			true) ;
			
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			fromMongoDbConnection.disconnect();
			
			RecordsAffected recordsAffected = 
			SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
												toRdbmsSchemaName,
												toRdbmsTableName, 
												MongoResultSet.getRecords(mongoResultSet.getResultSet()),
												mongoResultSet.getMetadata());
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/mongodb:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from full Mongodb collection")
	public ResponseEntity<RestObject> 
	copyMongoFullCollectionToRdbmsTable(@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
										@RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
										@RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
										@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
										@RequestHeader(value="batchCount", defaultValue = "0") String batchCount) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());
			
			MongoResultSet mongoResultSet = MongoGet.getAllCollectionDocuments(	fromMongoDbConnection, 
																				fromMongoDatabaseName, 
																				fromMongoCollectionName) ;
			
			fromMongoDbConnection.disconnect();

			RecordsAffected recordsAffected = 
					SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
														toRdbmsSchemaName,
														toRdbmsTableName, 
														MongoResultSet.getRecords(mongoResultSet.getResultSet()),
														mongoResultSet.getMetadata());

			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/mongodb:adhoc", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from Mongodb ad-hoc search")
	public ResponseEntity<RestObject> 
	copyMongoAdhocResultToRdbmsTable(	@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
										@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
										@RequestHeader(value="fromCollectionName") String fromCollectionName,
										@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
										@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
										@RequestBody String bsonQuery) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoResultSet mongoResultSet
			= MongoGet.execDynamicQuery(	fromClusterUniqueName, 
											fromMongoDbName, 
											fromCollectionName,
											bsonQuery,
											true);
				
			RecordsAffected recordsAffected =  
					SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
														toRdbmsSchemaName,
														toRdbmsTableName, 
														MongoResultSet.getRecords(mongoResultSet.getResultSet()),
														mongoResultSet.getMetadata());

			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to Rdbms table from Elastic DSL query")
	public ResponseEntity<RestObject> 
	copyElasticDslResultToRdbmsTable(	@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
										@RequestHeader(value="fromElasticHttpVerb", defaultValue = "GET") String fromElasticHttpVerb,
										@RequestHeader(value="fromElasticEndPoint") String fromElasticEndPoint,
										@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
										@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
										@RequestBody (required = false) String httpPayload) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				ElasticPayload2 payload =
				ElasticInfo.executeGenericForPayload2(	elasticLowLevelWrapper, fromElasticHttpVerb, fromElasticEndPoint, httpPayload);
				if(payload == null) {
					elasticLowLevelWrapper.disconnect();
					throw new Exception("The query cannot be exported to rdbms. No hits found. Is it a system query?"); 
				}
				
				Map<String, String> metadata = ElasticInfo.getMetadata(payload);
				List<Map<String,Object>> rows = ElasticInfo.getRows(payload);

				/*Transfer Data from Elastic to RDBMS table*/
				RecordsAffected recordsAffected = 
						SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
															toRdbmsSchemaName,
															toRdbmsTableName, 
															rows, 
															metadata);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Elastic Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/elastic:sql", method = RequestMethod.PUT)
	@Operation(summary = "Create/add records to collection from Elastic SQL query")
	public ResponseEntity<RestObject> 
	copyElasticSqlResultToRdbmsTable(@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
									@RequestHeader(value="fromElasticFetchSize") Integer fromElasticFetchSize,
									@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
									@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
									@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
									@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
									@RequestBody String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				ElasticSqlPayload ret = SearchSql.searchSqlAsElasticSqlPayload(elasticLowLevelWrapper, sqlContent, Integer.valueOf(fromElasticFetchSize));
				
				
				elasticLowLevelWrapper.disconnect();
				Map<String, String> metadata = ElasticSqlPayload.getMetadataAsMap(ret);
				List<Map<String,Object>> rows = ElasticSqlPayload.getRowAsListOfMap(ret);

				/*Transfer Data from Elastic to RDBMS table*/
				RecordsAffected recordsAffected =
						SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
															toRdbmsSchemaName,
															toRdbmsTableName, 
															rows, 
															metadata);
				

				return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Elastic Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/sqlrepo:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy Rdbms Sql result records to another Rdbms System Table")
	public ResponseEntity<RestObject> 
	copyRdbmsSqlResultToRdbmsTable(@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromRdbmsSchemaUniqueName") String fromRdbmsSchemaUniqueName,
									@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
									@RequestHeader(value="toRdbmsSchemaName") String toRdbmsSchemaName,
									@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
									@RequestBody String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			TableFormatMap recordSet=
					SqlMetadataWrapper.execAdhocForMigration(fromRdbmsSchemaUniqueName, sqlContent);

			/*Transfer Data from Elastic to RDBMS table*/
			RecordsAffected recordsAffected = 
					SqlQueryRepoUtils.insertBulkIntoTable(	toRdbmsConnectionName,
														toRdbmsSchemaName,
														toRdbmsTableName, 
														recordSet.getRows(), 
														recordSet.getMetadata());

			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/copy/sqlrepo/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv to table")
	public ResponseEntity<RestObject> 
	copyCsvToRdbmsTable(@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="fileType", defaultValue = "N") String fileType,
						@RequestHeader(value="tableScript", defaultValue = "") String tableScript,
						@RequestHeader(value="toRdbmsConnectionName") String toRdbmsConnectionName,
						@RequestHeader(value="toRdbmsSchemaName", required = false, defaultValue = "") String toRdbmsSchemaName,
						@RequestHeader(value="toRdbmsTableName") String toRdbmsTableName,
						@RequestParam("file") MultipartFile file) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String fileName = StringUtils.generateUniqueString32();
		
		try	{
			String fullFilePath = storageService.storeTmp(file, fileName);
			
			String csvContent = CsvWrapper.readFile(fullFilePath);
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(toRdbmsConnectionName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			RecordsAffected recordsAffected = new RecordsAffected();
			try (Connection conn = connectionDetailInfo.getConnection()) {
				
				if(!tableScript.isEmpty() && !SqlMetadataWrapper.isTable(toRdbmsTableName, conn)) {
					SqlQueryRepoUtils.execStaticDdl(tableScript, conn);
				}
				
				String isCompressed;
				if( fileType.equals("text/csv") ) {
					isCompressed = "N";
				} else if( fileType.equals("application/x-zip-compressed")) {
					isCompressed = "Y";
				} else {
					return RestObject.retException(requestId, methodName, "Unknown File Type");
				}
				
				
				TableMetadata tM = SqlMetadataWrapper.getTableColumns(toRdbmsTableName, conn);
				TableFormatMap recordSet= CsvWrapper.stringToTable(csvContent, tM,  isCompressed);
				/*Transfer Data from Csv to RDBMS table*/
				recordsAffected = SqlQueryRepoUtils.insertBulkIntoTable_v2(	toRdbmsConnectionName,
																		toRdbmsSchemaName,
																		toRdbmsTableName, 
																		recordSet.getRows(), 
																		recordSet.getMetadata());
		    } catch (SQLException ex) {
				return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		    }
			
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			storageService.deleteTmp(fileName);
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/validate:sql", method = RequestMethod.PUT)
	@Operation(summary = "Validate Sql")
	public ResponseEntity<RestObject> 
	validateSql(@RequestHeader(value="requestId") String requestId,
				@RequestBody String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			GenericResponse g = new GenericResponse();
			if(SqlParser.isSqlDQL(sqlContent)) {
				g.setGenericPayload("DQL");
			} else if(SqlParser.isSqlDML(sqlContent)) {
				g.setGenericPayload("DML");
			} else if(SqlParser.isSqlDDL(sqlContent)) {
				g.setGenericPayload("DDL");
			} else {
				g.setGenericPayload("NONE");
			}
			return RestObject.retOKWithPayload(g, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/*History */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/history/stm:get", method = RequestMethod.GET)
	@Operation(summary = "Get the List of executed sql statements")
	public ResponseEntity<RestObject> 
	getSqlHistStm(  @RequestHeader(value="user") String user,
				    @RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="type") String type,
					@RequestHeader(value="stext", required = false, defaultValue = "") String stext) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		List<HistoryStatement> lstSql = new ArrayList<HistoryStatement>();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			
			String mainFolder = appConstants.getHistStatementPath();
			List<String> lstSha = HistFileManagement.getStmts(userId, mainFolder, type, "rdbms");
			for(String s: lstSha) {
				HistoryStatement t = HistFileManagement.getStm_(userId, s, mainFolder, type, "rdbms");
				if(stext != null && !stext.isBlank() && !stext.isEmpty()) {
					if( t.getContent().contains(stext) )
						lstSql.add(t);	
				} else {
					lstSql.add(t);
				}
				
			}
			HistSqlList ret =  HistSqlList.getTempSqlList(lstSql);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/history/stm:copy", method = RequestMethod.POST)
	@Operation(summary = "Copy sql statements to another user")
	public ResponseEntity<RestObject> 
	copySqlHistStm(	@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="toUserId") String toUserId,
					@RequestHeader(value="shaHash") String shaHash,
					@RequestHeader(value="type") String type) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			HistFileManagement.addExistingStmToNewUser(userId, Long.parseLong(toUserId), shaHash, mainFolder, type, "rdbms");
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlrepo/history/stm:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Delete an executed sql statement from your profile")
	public ResponseEntity<RestObject> 
	deleteSqlHistStmt(  @RequestHeader(value="user") String user,
					    @RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="shaHash") String shaHash,
						@RequestHeader(value="type") String type) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			boolean ret = HistFileManagement.deleteStatement(userId, shaHash, mainFolder, type, "rdbms");
			return RestObject.retOKWithPayload(new GenericResponse(String.valueOf(ret)), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
}
