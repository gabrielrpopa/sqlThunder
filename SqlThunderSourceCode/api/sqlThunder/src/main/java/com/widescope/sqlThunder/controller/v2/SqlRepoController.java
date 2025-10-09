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
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.*;
import com.widescope.rdbmsRepo.database.rdbmsRepository.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.http.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;
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
import com.widescope.rdbmsRepo.database.embeddedDb.utils.RdbmsParallelQuery;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbConnection;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.rdbmsRepo.database.structuredFiles.csv.CsvWrapper;
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

	/* File System Storage, in this service used for temp files */
	@Autowired
	private StorageService storageService;

	@Autowired
	private PersistenceWrap pWrap;

	@PostConstruct
	public void initialize() {

	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/databases", method = RequestMethod.GET)
	@Operation(summary = "Get the List of available Databases")
	public ResponseEntity<RestObject> 
	getDatabase(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="databaseName", required = false) final String databaseName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try	{
			requestId = StringUtils.generateRequestId(requestId);
			final RdbmsRepoDatabaseList ret = getRdbmsRepoDatabaseList(databaseName);
			ret.blockPassword();
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@NotNull
	private static RdbmsRepoDatabaseList getRdbmsRepoDatabaseList(final String databaseName) {
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
	@RequestMapping(value = "/sqlRepo/database/add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new database connection to the list of available Databases/schema connections")
	public ResponseEntity<RestObject> 
	addDatabase(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="databaseType") final String databaseType,
					@RequestHeader(value="databaseName") final String databaseName,
					@RequestHeader(value="databaseServer") final String databaseServer,
					@RequestHeader(value="databasePort") final int databasePort,
					@RequestHeader(value="databaseDescription", required = false) final String databaseDescription,
					@RequestHeader(value="databaseWarehouseName", required = false) final String databaseWarehouseName,
					@RequestHeader(value="schemaName", required = false) final String schemaName,
					@RequestHeader(value="schemaService", required = false) final String schemaService,
					@RequestHeader(value="schemaPassword", required = false) final String schemaPassword,
					@RequestHeader(value="schemaUniqueUserName") final String schemaUniqueUserName,
					@RequestHeader(value="tunnelLocalPort", required = false) final int tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false) final String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false)  final int tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false) final String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) final String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) final String tunnelRemoteHostRsaKey) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.getDatabase(connectionInfo, schemaUniqueUserName);
			if(sqlRepoDatabase == null) {
				SqlRepoUtils.addDatabase(connectionInfo, 
										 databaseType,
										 databaseName,
										 databaseServer,
										 databasePort,
										 databaseDescription,
										 databaseWarehouseName,
										 schemaName,
										 schemaService,
										 schemaPassword,
										 schemaUniqueUserName,
										 tunnelLocalPort,
										 tunnelRemoteHostAddress,
										 tunnelRemoteHostPort,
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
										databasePort,
										databaseDescription,
										databaseWarehouseName,
										schemaName,  
										schemaService,
										schemaPassword,
										schemaUniqueUserName,
										tunnelLocalPort,
										tunnelRemoteHostAddress,
										tunnelRemoteHostPort,
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/update", method = RequestMethod.PUT)
	@Operation(summary = "Update database connection")
	public ResponseEntity<RestObject> 
	updateDatabase(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="databaseId") final int databaseId,
					@RequestHeader(value="databaseType") final String databaseType,
					@RequestHeader(value="databaseName") final String databaseName,
					@RequestHeader(value="databaseServer") final String databaseServer,
					@RequestHeader(value="databasePort") final String databasePort,
					@RequestHeader(value="databaseDescription") final String databaseDescription,
					@RequestHeader(value="databaseWarehouseName") final String databaseWarehouseName,
					@RequestHeader(value="schemaName") final String schemaName,
					@RequestHeader(value="schemaService") final String schemaService,
					@RequestHeader(value="schemaPassword") final String schemaPassword,
					@RequestHeader(value="schemaUniqueUserName") final String schemaUniqueUserName,
					@RequestHeader(value="tunnelLocalPort", required = false, defaultValue = "") final String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false, defaultValue = "") final String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false, defaultValue = "") final String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false, defaultValue = "") final String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false, defaultValue = "") final String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false, defaultValue = "") final String tunnelRemoteHostRsaKey,
					@RequestHeader(value="isActive", defaultValue = "1") final int isActive) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if( SqlRepoUtils.sqlRepoDatabaseMap.containsKey(schemaUniqueUserName) )	{
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl,"Schema Name conflict"));
			}
			
			
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			      
			SqlRepoUtils.updateDatabase(connectionInfo, 
										databaseId,
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
										isActive );
			
			SqlRepoDatabase sqlRepoDatabase = SqlRepoUtils.getDatabase(connectionInfo, schemaUniqueUserName);
			SqlRepoUtils.sqlRepoDatabaseMap.put(sqlRepoDatabase.getSchemaUniqueUserName(), sqlRepoDatabase);
			return RestObject.retOKWithPayload(new GenericResponse("The database connection has been updated"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete database/schema connection")
	public ResponseEntity<RestObject> 
	databaseDelete(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="databaseId") final int databaseId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoDatabase sqlRepoDatabase1 = SqlRepoUtils.getDatabase(connectionInfo, databaseId);
			
			SqlRepoUtils.deleteDatabase(connectionInfo, databaseId) ;
			SqlRepoDatabase sqlRepoDatabase2 = SqlRepoUtils.getDatabase(connectionInfo, databaseId);
			if(sqlRepoDatabase2 == null) {
				SqlRepoUtils.sqlRepoDatabaseMap.remove(sqlRepoDatabase1.getSchemaUniqueUserName());
			}
			return RestObject.retOKWithPayload(new GenericResponse("The database connection has been deleted"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/connection/validate:new-connection", method = RequestMethod.GET)
	@Operation(summary = "Validate a new Database/schema connection")
	public ResponseEntity<RestObject> 
	validateDatabase(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="databaseType") final String databaseType,
						@RequestHeader(value="databaseName") final String databaseName,
						@RequestHeader(value="databaseServer") final String databaseServer,
						@RequestHeader(value="databasePort") final String databasePort,
						@RequestHeader(value="databaseDescription", required = false) final String databaseDescription,
						@RequestHeader(value="databaseWarehouseName", required = false) final String databaseWarehouseName,
						@RequestHeader(value="schemaName", required = false) final String schemaName,
						@RequestHeader(value="schemaService", required = false) final String schemaService,
						@RequestHeader(value="schemaPassword", required = false) final String schemaPassword,
						@RequestHeader(value="schemaUniqueUserName", required = false) final String schemaUniqueUserName,
						@RequestHeader(value="account", required = false) final String account,
						@RequestHeader(value="other_parameter", required = false) final String otherParameter,
						@RequestHeader(value="tunnelLocalPort", required = false) final String tunnelLocalPort,
						@RequestHeader(value="tunnelRemoteHostAddress", required = false) final String tunnelRemoteHostAddress,
						@RequestHeader(value="tunnelRemoteHostPort", required = false) final String tunnelRemoteHostPort,
						@RequestHeader(value="tunnelRemoteHostUser", required = false) final String tunnelRemoteHostUser,
						@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) final String tunnelRemoteHostUserPassword,
						@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) final String tunnelRemoteHostRsaKey) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
				return RestObject.retOKWithPayload(new GenericResponse( Boolean.toString(true)), requestId, methodName);
			} else {
				return RestObject.retException(new GenericResponse( Boolean.toString(false)), requestId, methodName, "Cannot connect to database. Connection to database is invalid or database is down");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/connection/validate:connection", method = RequestMethod.GET)
	@Operation(summary = "Validate an existing Database/schema connection")
	public ResponseEntity<RestObject>
	validateSqlRepoDatabase(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="databaseName", required = false) final String databaseName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
					return RestObject.retOKWithPayload(new GenericResponse( Boolean.toString(true)), requestId, methodName);
				} else {
					return RestObject.retException(new GenericResponse( Boolean.toString(false)), requestId, methodName, "Cannot connect to database. Connection to database is invalid or database is down");
				}
			} else {
				return RestObject.retException(new GenericResponse( Boolean.toString(false)), requestId, methodName, "Cannot connect to database. Connection to database is invalid or database is down");
			}

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}





	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/reload", method = RequestMethod.GET)
	@Operation(summary = "Reload Repo List")
	public ResponseEntity<RestObject> 
	reloadSqlRepo(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SqlRepoUtils.populateRepo(appConstants.getActiveRepo());
			SqlRepoList sqlRepoList = SqlRepoList.setSqlRepoList(SqlRepoUtils.sqlRepoDynamicSqlMap);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new Sql statement to the repo")
	public ResponseEntity<RestObject> 
	addSql(@RequestHeader(value="user") final String user,
		   @RequestHeader(value="session") final String session,
		   @RequestHeader(value="requestId", defaultValue = "") String requestId,
		   @RequestHeader(value="sqlType", defaultValue = "QUERY") final String sqlType,
		   @RequestHeader(value="sqlReturnType", defaultValue = "RECORDSET") final String sqlReturnType,
		   @RequestHeader(value="sqlReturnType") final String sqlCategory,
		   @RequestHeader(value="sqlName") final String sqlName,
		   @RequestHeader(value="sqlDescription") final String sqlDescription,
		   @RequestHeader(value="sqlContent") final String sqlContent,
		   @RequestHeader(value="execution") final String execution,
		   @RequestHeader(value="active", defaultValue = "1") final int active) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);

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
								active );
			
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Add a new Sql statement to the repo")
	public ResponseEntity<RestObject> 
	deleteSql(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="sqlId") final long sqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			
			SqlRepoUtils.deleteSqlToDbBridge(connectionInfo, sqlId);
			SqlRepoUtils.deleteAllSqlParam(connectionInfo, sqlId);
			SqlRepoUtils.deleteSql(connectionInfo, sqlId);
			SqlRepoUtils.sqlRepoDynamicSqlMap.remove( sqlId);
			return RestObject.retOKWithPayload(new GenericResponse("New Sql Is Updated"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql:update", method = RequestMethod.PUT)
	@Operation(summary = "Update Sql statement to the repo")
	public ResponseEntity<RestObject> 
	updateSql(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="sqlId") final long sqlId,
				@RequestHeader(value="databaseId") final int databaseId,
				@RequestHeader(value="sqlType", defaultValue = "QUERY") final String sqlType,
				@RequestHeader(value="sqlReturnType", defaultValue = "RECORDSET") final String sqlReturnType,
				@RequestHeader(value="sqlCategory") final String sqlCategory,
				@RequestHeader(value="sqlName") final String sqlName,
				@RequestHeader(value="sqlDescription") final String sqlDescription,
				@RequestHeader(value="sqlContent") final String sqlContent,
				@RequestHeader(value="execution") final String execution,
				@RequestHeader(value="active") final int active) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoUtils.updateSql(connectionInfo, sqlId, databaseId, sqlType, sqlReturnType, sqlCategory, sqlName, sqlDescription, sqlContent, execution, active );
			SqlRepoUtils.populateDynamicSql(connectionInfo);
			return RestObject.retOKWithPayload(new GenericResponse("New Sql Is Updated"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	  
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sqlparam:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Sql Param to Sql Statement")
	public ResponseEntity<RestObject> 
	addSqlParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="sqlId") final long sqlId,
					@RequestHeader(value="sqlParamName") final String sqlParamName,
					@RequestHeader(value="sqlParamType", defaultValue = "STRING") final String sqlParamType,
					@RequestHeader(value="sqlParamDefaultValue") final String sqlParamDefaultValue,
					@RequestHeader(value="sqlParamPosition", defaultValue = "IN") final String sqlParamPosition,
					@RequestHeader(value="sqlParamOrder") final int sqlParamOrder,
					@RequestHeader(value="sqlParamOriginTbl") final String sqlParamOriginTbl,
					@RequestHeader(value="sqlParamOriginCol") final String sqlParamOriginCol) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);

			SqlRepoUtils.addSqlParam(connectionInfo,
									sqlId,
									sqlParamName, 
									sqlParamDefaultValue, 
									sqlParamType, 
									sqlParamPosition, 
									sqlParamOrder,
									sqlParamOriginTbl, 
									sqlParamOriginCol);
			
			
			
			SqlRepoUtils.populateDynamicSql(connectionInfo);
			
			List<SqlRepoParam> lst  = SqlRepoUtils.getSqlParam(connectionInfo, sqlParamName);
			SqlRepoParamListDetail sqlRepoParamListDetail = new SqlRepoParamListDetail(lst);
			return RestObject.retOKWithPayload(sqlRepoParamListDetail, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sqlparam:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Sql Param")
	public ResponseEntity<RestObject> 
	deleteSqlParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="sqlId") final long sqlId,
					@RequestHeader(value="sqlParamId") final long sqlParamId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoUtils.deleteSqlParam(connectionInfo, sqlId, sqlParamId);
			SqlRepoUtils.populateDynamicSqlParam(connectionInfo);
			return RestObject.retOKWithPayload(new GenericResponse("Sql Param Is Deleted"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Repo List")
	public ResponseEntity<RestObject> 
	getSqlRepoList(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="filter", required = false, defaultValue = "") final String filter,
					@RequestHeader(value="databaseId", required = false, defaultValue = "") final int databaseId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<SqlRepoDynamicSql> ret = new ArrayList<>();
			if(!filter.isEmpty()) {
				// gather all records that contain like text
				List<SqlRepoDynamicSql> flt =  SqlRepoUtils.sqlRepoDynamicSqlMap.values()
						.stream()
						.filter(element -> element.getSqlName().contains(filter) ||
								           element.getSqlContent().contains(filter) ||
								           element.getSqlDescription().contains(filter) )
						.toList();
				
				// use only those record that have allowed to run on db id
				try {
					for (SqlRepoDynamicSql entry : flt ) {
						if(entry.getSqlRepoDatabaseSchemaBridgeList().stream().anyMatch(element -> element.getDatabaseId() == databaseId)) {
							ret.add(entry);
				        }
					}
				} catch(Exception ex) {
					ret.addAll(flt);
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			else {
				try {
					List<SqlRepoDynamicSql> flt =  SqlRepoUtils
													.sqlRepoDynamicSqlMap
													.values()
													.stream()
													.filter(element -> element.getSqlRepoDatabaseSchemaBridgeList().stream().anyMatch(element2 -> element2.getDatabaseId() == databaseId))
													.toList();
					ret.addAll(flt);
					
				} catch(Exception ex) {
					return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
				}
				
			}
			
			SqlRepoList sqlRepoList = new SqlRepoList(ret);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Repo List Without Params")
	public ResponseEntity<RestObject> 
	getSqlRepoListWithNoParams(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="filter", required = false) final String filter,
								@RequestHeader(value="databaseId", required = false, defaultValue = "") final String databaseId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql/summary", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Repo List Summary Format")
	public ResponseEntity<RestObject> 
	getSqlRepoListSummary(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="filter", required = false) final String filter) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SqlRepoListShortFormat sqlRepoList = SqlRepoListShortFormat.setSqlRepoListShortFormat(SqlRepoUtils.sqlRepoDynamicSqlMap, filter);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sql/detail", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Detail")
	public ResponseEntity<RestObject> 
	getSqlDetail(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="sqlID", required = false) final long sqlId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			List<SqlRepoParam> listOfParams = SqlRepoUtils.getSqlParams(connectionInfo, sqlId);
			SqlRepoDynamicSql sqlRepoDynamicSql  = SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId );
			
			// !!!! -> We are having a potential bug, so for now we try to mitigate that 
			if(sqlRepoDynamicSql.getSqlRepoParamList().isEmpty() && !listOfParams.isEmpty()) {
				sqlRepoDynamicSql.clearSqlRepoParamList();
				sqlRepoDynamicSql.setSqlRepoParamList(listOfParams);
				sqlRepoDynamicSql.clearParamList();
				for (SqlRepoParam sqlRepoParam : listOfParams) {
					SqlParameter sqlParam = new SqlParameter(sqlRepoParam);
					sqlRepoDynamicSql.addParamList(sqlParam);
				}
				SqlRepoUtils.sqlRepoDynamicSqlMap.put(sqlId, sqlRepoDynamicSql );
			}

			Map<Long, SqlRepoDynamicSql> _sqlRepoDynamicSqlMap = new HashMap<>();
			_sqlRepoDynamicSqlMap.put(sqlId, sqlRepoDynamicSql);
			SqlRepoList sqlRepoList = SqlRepoList.setSqlRepoList(_sqlRepoDynamicSqlMap);
			return RestObject.retOKWithPayload(sqlRepoList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/param/detail", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Param List Detail")
	public ResponseEntity<RestObject> 
	getSqlParamListDetail(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="sqlId") final long sqlId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SqlRepoParamListDetail 
			sqlRepoParamListDetail
					= SqlRepoParamListDetail.setSqlRepoParamListDetail(SqlRepoUtils.sqlRepoDynamicSqlMap, sqlId);
			
			return RestObject.retOKWithPayload(sqlRepoParamListDetail, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/params", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Param List")
	public ResponseEntity<RestObject> 
	getSqlParamList(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="sqlId") final long sqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ParamObj paramObj = new ParamObj();
			paramObj.setPList(SqlRepoUtils.sqlRepoDynamicSqlMap, sqlId);
			return RestObject.retOKWithPayload(paramObj, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/param/bulk", method = RequestMethod.GET)
	@Operation(summary = "Get Sql Param List for Bulk DML. DQLs and DDLs are excluded")
	public ResponseEntity<RestObject>
	getSqlParamListBulk(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="sqlId") final long sqlId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SqlRepoDynamicSql sqlRepoDynamicSql = SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId);
			if(sqlRepoDynamicSql == null) {
				return RestObject.retException(requestId, methodName, "SQL ID not found");
			}

			ParamListObj ret = SqlQueryRepoUtils.getBulkParamToPopulateJson(sqlId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sqlToDb/mapping:update", method = RequestMethod.PUT)
	@Operation(summary = "Assign sql statement to a certain database")
	public ResponseEntity<RestObject> 
	addSqlToDbMapping(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="sqlId") final long sqlId,
						@RequestHeader(value="dbId") final int dbId,
						@RequestHeader(value="active") final int active) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoUtils.mergeSqlToDbBridge(connectionInfo, sqlId, dbId, active);
			SqlRepoUtils.populateSqlToDbBridge(connectionInfo);
			SqlStmToDbBridgeList ret = SqlRepoUtils.getSqlToDbBridge(connectionInfo, sqlId, dbId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sqlToDb/mapping:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete association of sql statement to database")
	public ResponseEntity<RestObject> 
	deleteSqlToDbMapping(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="sqlId") final long sqlId,
							@RequestHeader(value="dbId") final int dbId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlRepoUtils.deleteSqlToDbBridge(connectionInfo, sqlId, dbId);
			SqlRepoUtils.populateSqlToDbBridge(connectionInfo);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/sqlToDb/mapping:list", method = RequestMethod.GET)
	@Operation(summary = "Get mapping of sql statements to databases")
	public ResponseEntity<RestObject> 
	listSqlToDbMapping(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="sqlId") final long sqlId,
						@RequestHeader(value="dbId") final int dbId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String sqlRepoName = appConstants.getActiveRepo();
			DbConnectionInfo connectionInfo = DbUtil.connectionDetailsTable.get(sqlRepoName);
			SqlStmToDbBridgeList ret = SqlRepoUtils.getSqlToDbBridge(connectionInfo, sqlId, dbId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/execute/adhoc", method = RequestMethod.POST, consumes = "text/plain")
	@Operation(summary = "Execute Adhoc Sql")
	public ResponseEntity<RestObject> 
	executeAdhocSql(@RequestHeader(value="user") final String user,
					@RequestHeader(value="session") final String session,
					@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="schemaUniqueName") final String schemaUniqueName,
					@RequestHeader(value="outputCompression", required = false) final String outputCompression,
					@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
					@RequestHeader(value="forceNoPush", required = false, defaultValue = "Y") final String forceNoPush,
					@RequestHeader(value="sqlType", required = false, defaultValue = "") final String sqlType, /*DQL/DML/DDL*/
					@RequestHeader(value="comment", required = false) final String comment,
					@RequestHeader(value="sqlName") final String sqlName,
					@RequestHeader(value="groupId", required = false ,defaultValue = "2")  final long groupId, /*Default WEB*/
					@RequestBody final String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if(sqlContent == null || sqlContent.isEmpty()) {
			return RestObject.retException(requestId, methodName, "SQL Content missing", "SQL Content missing");
		}

		try	{
			User u = authUtil.getUser(user);
			SqlRepoExecReturn sqlRepoExecReturn;

			//Check that user is connected to websocket
			if( user != null &&  !user.isEmpty() &&  !user.isBlank() &&
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
																					u,
																					groupId,
																					persist,
																					pWrap
																					);
					sqlRepoExecReturn.getResults().setStreaming("Y");
					
						
			} else {
				// return data (full payload)
				sqlRepoExecReturn  = SqlRepoExecWrapper.execAdhocSqlForResultQuery(	u,
																					schemaUniqueName,
																					requestId,
																					comment,
																					-1, /*adhoc*/
																					sqlName,
																					sqlType,
																					"A", /*adhoc*/
																					groupId,
																					sqlContent,
																					"", /*don't have params as adhoc*/
																					persist, /*do we persist recordset?*/
																					pWrap
																					);
				
				
						
				sqlRepoExecReturn.getResults().setStreaming("N");

			}


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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/generate:script", method = RequestMethod.PUT)
	@Operation(summary = "Get the List of all user tables in database schema")
	public ResponseEntity<RestObject> 
	generateCreateScriptForTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="fromRdbmsSchemaUniqueName", required = false) final String fromRdbmsSchemaUniqueName,
									@RequestHeader(value="tableName", required = false) final String tableName,
									@RequestBody final String sqlContent) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String script = SqlMetadataWrapper.createRdbmsTableStm(fromRdbmsSchemaUniqueName, sqlContent, tableName);
			return RestObject.retOKWithPayload(new GenericResponse(script), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/tables", method = RequestMethod.GET)
	@Operation(summary = "Get the List of all user tables in database schema")
	public ResponseEntity<RestObject>
	getDatabaseTables(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="connectionUniqueName") final String connectionUniqueName,
						@RequestHeader(value="schema") final String schema) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			TableList ret = SqlQueryRepoUtils.getTableList(connectionUniqueName, schema);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/database/schemas", method = RequestMethod.GET)
	@Operation(summary = "Get the List of database schemas")
	public ResponseEntity<RestObject> 
	getDatabaseSchemas( @RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="connectionUniqueName", required = false) final String connectionUniqueName) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			TableList ret = SqlQueryRepoUtils.getSchemaList(connectionUniqueName);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/execute:singleDb", 
					method = RequestMethod.POST, 
					consumes = "application/json", 
					produces={MediaType.APPLICATION_JSON_VALUE}
					)
	@Operation(summary = "Execute Sql Repo")
	public ResponseEntity<RestObject> 
	executeSqlRepo(@RequestHeader(value="user") final String user,
				   @RequestHeader(value="session") final String session,
				   @RequestHeader(value="requestId", defaultValue = "") String requestId,
				   @RequestHeader(value="sqlId") final long sqlId,
				   @RequestHeader(value="schemaUniqueName") final String schemaUniqueName,
				   @RequestHeader(value="outputCompression", required = false) final String outputCompression,
				   @RequestHeader(value="outputType", defaultValue = "JSON") final String outputType,
				   @RequestHeader(value="batchCount", required = false, defaultValue = "1") final long batchCount,
				   @RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				   @RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
				   @RequestHeader(value="groupId", required = false ,defaultValue = "2")  final long groupId, /*Default WEB*/
				   @Valid @RequestBody final String jsonObjSqlParam)  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			SqlRepoExecReturn sqlRepoExecReturn;
			//Check that user is connected to websocket
			if( user != null && !user.isEmpty() && !user.isBlank() && WebSocketsWrapper.isUser(user)) {
				//send data via websocket
				sqlRepoExecReturn = SqlRepoExecWrapper.execSqlRepoToWebSocket(	sqlId,
																				schemaUniqueName,
																				jsonObjSqlParam,
																				requestId,
																				batchCount,
																				comment,
																				outputCompression,
																				session,
																				requestId,
																				u,
																				groupId,
																				persist,
																				pWrap
																				);
				sqlRepoExecReturn.getResults().setStreaming("Y");
			} else {
				// return data as HTTP payload
				sqlRepoExecReturn  = SqlRepoExecWrapper.execSqlRepoForResultQuery(	sqlId,
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/execute:multipleDb", method = RequestMethod.POST, consumes = "application/json")
	@Operation(summary = "Execute Sql On multiple DBs and aggregate results")
	public ResponseEntity<RestObject> 
	executeSqlRepoMultiple(@RequestHeader(value="user") final String user,
						   @RequestHeader(value="session") final String session,
						   @RequestHeader(value="requestId", defaultValue = "") String requestId,
						   @RequestHeader(value="sqlId") final long sqlId,
						   @RequestHeader(value="outputCompression", required = false) final String outputCompression,
						   @RequestHeader(value="outputType", defaultValue = "JSON") final String outputType,
						   @RequestHeader(value="batchCount", required = false, defaultValue = "1") final long batchCount,
						   @RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
						   @RequestHeader(value="dbIdList", required = false, defaultValue = "") final String dbIdList,  /*comma separated*/
						   @RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
						   @RequestHeader(value="groupId", required = false ,defaultValue = "2")  final long groupId, /*Default WEB*/
						   @RequestHeader final HttpHeaders incomingHeaders,
						   @RequestBody final String jsonObjSqlParam)  {
		
		String host = Objects.requireNonNull(incomingHeaders.getHost()).getHostName();
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		RestObject transferableObject = new RestObject(null, methodName, requestId);

		try	{

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
				PushMessageSqlRepoExecThread.execPushMessageParallel(	sqlId,
																		lstSqlRepoDatabase,
																		jsonObjSqlParam,
																		outputType,
																		requestId,
																		batchCount,
																		comment,
																		outputCompression,
																		u,
																		groupId,
																		host,
																		session,
																		persist,
																		pWrap
																		);
				
				return RestObject.retOKWithPayload(new GenericResponse(null) , requestId, methodName);
			} else {
				SqlRepoExecReturn sqlRepoExecReturn = SqlRepoExecWrapper.execSqlRepoParallel(	sqlId,
																								dbIdList,
																								jsonObjSqlParam,
																								batchCount,
																								user,
																								persist);
				return RestObject.retOKWithPayload(sqlRepoExecReturn.getResults(), requestId, methodName);
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/execute/adhoc/multipledb:aggregate", method = RequestMethod.PUT, consumes = "text/plain")
	@Operation(summary = "Execute Sql On multiple DBs and aggregate results")
	public ResponseEntity<RestObject> 
	executeSqlAdhocMultiple(@RequestHeader(value="user") final String user,
							@RequestHeader(value="session") final String session,
							@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestBody final String strObj)  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/migrate", method = RequestMethod.POST, consumes = "application/json")
	@Operation(summary = "Create and Insert table from Sql Repo execution")
	public ResponseEntity<RestObject> 
	executeSqlRepoToMigrateData(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="sqlId") final long sqlId,
									@RequestHeader(value="sourceConnectionName") final String sourceConnectionName,
									@RequestHeader(value="inputCompression", required = false) final String inputCompression,
									@RequestHeader(value="destinationConnectionName") final String destinationConnectionName,
									@RequestHeader(value="destinationSchema", required = false) final String destinationSchema,
									@RequestHeader(value="destinationTable", required = false) final String destinationTable,
									@RequestBody final String jsonObjSqlParam) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		int countInserted;
		try	{
			SqlRepoDynamicSql queryObj = SqlRepoUtils.sqlRepoDynamicSqlMap.get(sqlId);
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
				countInserted = SqlQueryRepoUtils.execDynamicQueryAndTransferData(	sqlId,
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	

	
	
	
	
	/**
	 * Data Migration / Copy from Mongo/Elastic to RDBMS table
	 */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/embedded/adhoc:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy records from Embedded Sql to RDBMS table")
	public ResponseEntity<RestObject> 
	copyEmbeddedSqlResultToRdbmsTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="fromEmbeddedType", defaultValue = "H2") final String fromEmbeddedType,
										@RequestHeader(value="fromClusterId") final String fromClusterId,
										@RequestHeader(value="fromEmbeddedDatabaseName") final String fromEmbeddedDatabaseName,
										@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName,
										@RequestBody final String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/mongodb/search:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy records from Mongodb simple search to RDBMS table")
	public ResponseEntity<RestObject> 
	copyMongoSimpleSearchResultToRdbmsTable(@RequestHeader(value="requestId", defaultValue = "") String requestId,
											@RequestHeader(value="fromClusterUniqueName") final String fromClusterUniqueName,
											@RequestHeader(value="fromMongoDbName") final String fromMongoDbName,
											@RequestHeader(value="fromCollectionName") final String fromCollectionName,
											@RequestHeader(value="itemToSearch") final String itemToSearch,
											@RequestHeader(value="valueToSearch") final String valueToSearch,
											@RequestHeader(value="valueToSearchType") final String valueToSearchType,
											@RequestHeader(value="operator", defaultValue = "$eq") final String operator,
											@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
											@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
											@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/mongodb/search:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from another Mongodb collection(s) range search")
	public ResponseEntity<RestObject> 
	copyMongoRangeSearchResultToRdbmsTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
											@RequestHeader(value="fromClusterUniqueName") final String fromClusterUniqueName,
											@RequestHeader(value="fromMongoDbName")final  String fromMongoDbName,
											@RequestHeader(value="fromCollectionName") final String fromCollectionName,
											@RequestHeader(value="itemToSearch") final String itemToSearch,
											@RequestHeader(value="fromValue") final String fromValue,
											@RequestHeader(value="toValue") final String toValue,
											@RequestHeader(value="valueSearchType") final String valueSearchType,
											@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
											@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
											@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/mongodb:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from full Mongodb collection")
	public ResponseEntity<RestObject> 
	copyMongoFullCollectionToRdbmsTable(@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
										@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
										@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
										@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/mongodb:adhoc", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from Mongodb ad-hoc search")
	public ResponseEntity<RestObject> 
	copyMongoAdhocResultToRdbmsTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="fromClusterUniqueName") final String fromClusterUniqueName,
										@RequestHeader(value="fromMongoDbName") final String fromMongoDbName,
										@RequestHeader(value="fromCollectionName") final String fromCollectionName,
										@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName,
										@RequestBody String bsonQuery) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoResultSet mongoResultSet = MongoGet.execDynamicQuery(	fromClusterUniqueName,
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to Rdbms table from Elastic DSL query")
	public ResponseEntity<RestObject> 
	copyElasticDslResultToRdbmsTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="fromElasticClusterName") final String fromElasticClusterName,
										@RequestHeader(value="fromElasticHttpVerb", defaultValue = "GET") final String fromElasticHttpVerb,
										@RequestHeader(value="fromElasticEndPoint") final String fromElasticEndPoint,
										@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
										@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
										@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName,
										@RequestBody (required = false) final String httpPayload) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(fromElasticClusterName);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/elastic:sql", method = RequestMethod.PUT)
	@Operation(summary = "Create/add records to collection from Elastic SQL query")
	public ResponseEntity<RestObject> 
	copyElasticSqlResultToRdbmsTable(@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="fromElasticClusterName") final String fromElasticClusterName,
									@RequestHeader(value="fromElasticFetchSize") final Integer fromElasticFetchSize,
									@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
									@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
									@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName,
									@RequestBody final String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(fromElasticClusterName);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/sqlrepo:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy Rdbms Sql result records to another Rdbms System Table")
	public ResponseEntity<RestObject> 
	copyRdbmsSqlResultToRdbmsTable(@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="fromRdbmsSchemaUniqueName") final String fromRdbmsSchemaUniqueName,
									@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
									@RequestHeader(value="toRdbmsSchemaName") final String toRdbmsSchemaName,
									@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName,
									@RequestBody final String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/copy/sqlRepo/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv to table")
	public ResponseEntity<RestObject> 
	copyCsvToRdbmsTable(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="fileType", defaultValue = "N") final String fileType,
						@RequestHeader(value="tableScript", defaultValue = "") final String tableScript,
						@RequestHeader(value="toRdbmsConnectionName") final String toRdbmsConnectionName,
						@RequestHeader(value="toRdbmsSchemaName", required = false, defaultValue = "") final String toRdbmsSchemaName,
						@RequestHeader(value="toRdbmsTableName") final String toRdbmsTableName,
						@RequestParam("file") final MultipartFile file) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		String fileName = StringUtils.generateUniqueString32();
		
		try	{
			String fullFilePath = storageService.storeTmp(file, fileName);
			
			String csvContent = CsvWrapper.readFile(fullFilePath);
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(toRdbmsConnectionName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			RecordsAffected recordsAffected;
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			storageService.deleteTmp(fileName);
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sqlRepo/validate:sql", method = RequestMethod.PUT)
	@Operation(summary = "Validate Sql")
	public ResponseEntity<RestObject> 
	validateSql(@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestBody final String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


}
