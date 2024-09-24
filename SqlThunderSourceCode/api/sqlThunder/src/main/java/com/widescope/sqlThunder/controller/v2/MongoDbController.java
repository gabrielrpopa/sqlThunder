/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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



import java.util.*;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.mongodb.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.springframework.core.io.Resource;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.widescope.rest.GenericResponse;
import com.widescope.rest.JsonResponse;
import com.widescope.rest.RestObject;

import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryHeader;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryJsonRows;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticLowLevelWrapper;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.SearchSql;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.sql.SqlResponse;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2InMem;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.mongo.ListMongoCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.mongo.MongoParallelQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.DataTransfer;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotMongoDbRepo;
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationTableList;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFile;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileMetaList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDb;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDbCollectionList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDbList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoDynamicMqlToClusterBridge;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoDynamicMqlToClusterBridgeList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQueryList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoDynamicMql;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoDynamicMqlExecution;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoDynamicMqlParamInputList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoDynamicMqlParamList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoMqlParam;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoRepoMqlParamInput;
import com.widescope.rdbmsRepo.database.mongodb.response.ObjectList;
import com.widescope.rdbmsRepo.database.mongodb.response.ObjectMongo;
import com.widescope.rdbmsRepo.database.mongodb.response.StringList;
import com.widescope.rdbmsRepo.database.mongodb.sql.query.ComplexAndMongoSearch;
import com.widescope.rdbmsRepo.database.mongodb.sql.query.RangeMongoSearch;
import com.widescope.rdbmsRepo.database.mongodb.sql.query.SimpleMongoSearch;
import com.widescope.rdbmsRepo.database.mongodb.sql.toH2.MongoObjectRef;
import com.widescope.rdbmsRepo.database.mongodb.sql.toH2.MongoToH2Sql;
import com.widescope.rdbmsRepo.database.rdbmsRepository.DdlDmlUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.structuredFiles.csv.CsvWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistFileManagement;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistSqlList;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistoryStatement;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.JsonUtils;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.service.StorageService;



@CrossOrigin
@RestController
@Schema(title = "Repo Control and Execution")
public class MongoDbController {
	
	@Autowired
	private AppConstants appConstants;
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private MongoClusterDb mongoClusterDb;
	
	@Autowired
	private ElasticClusterDb elasticClusterDb;
	
	@Autowired
	private	MongoExecutedQueriesRepoDb mongoHist;

	@Autowired
	private StorageService storageService;


	@PostConstruct
	public void initialize() {
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo:reload", method = RequestMethod.POST)
	@Operation(summary = "Reload Repo List")
	public ResponseEntity<RestObject> 
	reloadMongoRepo(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String fileName = "./mongoRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				MongoClusterDb.generateSchema();
			}
			SqlRepoUtils.mongoDbMap = mongoClusterDb.getAllCluster();
			MongoClusterList mongoClusterList = new MongoClusterList(SqlRepoUtils.mongoDbMap);
			mongoClusterList.blockPassword();
			return RestObject.retOKWithPayload(mongoClusterList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	   
	}
	
		

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo:list", method = RequestMethod.GET)
	@Operation(summary = "Get the Mongo Db Repository")
	public ResponseEntity<RestObject> 
	mongoRepo(	@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterList mongoClusterList = new MongoClusterList(mongoClusterDb.getAllCluster());
			mongoClusterList.blockPassword();
			return RestObject.retOKWithPayload(mongoClusterList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	
	   

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new Mongo database/cluster connection to the list of available databases/cluster connections")
	public ResponseEntity<RestObject> 
	mongoRepoAdd(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
					@RequestHeader(value="connString") String connString,
					@RequestHeader(value="storageType", required = false) String storageType,
					@RequestHeader(value="startPeriod", required = false) String startPeriod,
					@RequestHeader(value="endPeriod", required = false) String endPeriod,
					@RequestHeader(value="tunnelLocalPort", required = false) String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false) String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false) String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false) String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) String tunnelRemoteHostRsaKey)	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{

			if( SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) ) {
				RestObject transferableObject = new RestObject(new GenericResponse("CONFLICT"), 
																requestId, 
																"Error adding a new Mongo database",  
																"Error: Unique name Already exists" , 
																ErrorCode.ERROR, 
																Sources.DATABASE, 
																ErrorSeverity.HIGH, 
																methodName);
				return new ResponseEntity< RestObject > (transferableObject, HttpStatus.CONFLICT);
			}

			MongoClusterRecord mongoClusterRecord = new MongoClusterRecord(0,	
																			clusterUniqueName, 
																			connString, 
																			storageType, 
																			0, 
																			Long.parseLong(startPeriod), 
																			Long.parseLong(endPeriod),
																			tunnelLocalPort,
																			tunnelRemoteHostAddress,
																			tunnelRemoteHostPort,
																			tunnelRemoteHostUser,
																			tunnelRemoteHostUserPassword,
																			tunnelRemoteHostRsaKey
																			);
			mongoClusterDb.addCluster(mongoClusterRecord);
			mongoClusterRecord = mongoClusterDb.getCluster(clusterUniqueName);
			SqlRepoUtils.mongoDbMap.put(clusterUniqueName, mongoClusterRecord);
			return RestObject.retOKWithPayload(mongoClusterRecord, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster:update", method = RequestMethod.POST)
	@Operation(summary = "Update a Mongo database/cluster connection")
	public ResponseEntity<RestObject> 
	mongoRepoUpdate(@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="clusterId") String clusterId,
					@RequestHeader(value="uniqueName") String uniqueName,
					@RequestHeader(value="connString") String connString,
					@RequestHeader(value="storageType", required = false) String storageType,
					@RequestHeader(value="startPeriod", required = false) String startPeriod,
					@RequestHeader(value="endPeriod", required = false) String endPeriod,
					@RequestHeader(value="tunnelLocalPort", required = false) String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false) String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false) String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false) String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) String tunnelRemoteHostRsaKey) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			MongoClusterRecord oldMongoClusterRecord = mongoClusterDb.getCluster(Integer.parseInt(clusterId));
			
			MongoClusterRecord mongoClusterRecord = new MongoClusterRecord(	Integer.parseInt(clusterId),	
																			uniqueName, 
																			connString, 
																			storageType, 
																			0, 
																			Long.parseLong(startPeriod), 
																			Long.parseLong(endPeriod),
																			tunnelLocalPort,
																			tunnelRemoteHostAddress,
																			tunnelRemoteHostPort,
																			tunnelRemoteHostUser,
																			tunnelRemoteHostUserPassword,
																			tunnelRemoteHostRsaKey
																			);

			mongoClusterDb.updateCluster(mongoClusterRecord);
			SqlRepoUtils.mongoDbMap.remove(oldMongoClusterRecord.getUniqueName());
			SqlRepoUtils.mongoDbMap.put(uniqueName, mongoClusterRecord);
			
			MongoClusterRecord newMongoClusterRecord = mongoClusterDb.getCluster(Integer.parseInt(clusterId));
			return RestObject.retOKWithPayload(newMongoClusterRecord, requestId, methodName);

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove a Mongo database/cluster connection")
	public ResponseEntity<RestObject> 
	mongoRepoRemove(@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="clusterUniqueName") String clusterUniqueName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error Removing Mongo database");
		}
		
		try	{
			
			if(SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName)) {
				MongoClusterRecord mongoClusterRecord = mongoClusterDb.getCluster(clusterUniqueName);
				SqlRepoUtils.mongoDbMap.remove(clusterUniqueName);
				mongoClusterDb.deleteCluster(clusterUniqueName);
				return RestObject.retOKWithPayload(mongoClusterRecord, requestId, methodName);
			}
			else {
				return RestObject.retException(requestId, methodName, "MongoDb cluster/server does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/databases:list", method = RequestMethod.GET)
	@Operation(summary = "Get the list of databases in a cluster/MongoDB Server")
	public ResponseEntity<RestObject> 
	mongoDatabaseList(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterUniqueName") String clusterUniqueName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error getting Mongo databases");
		}
		
		try	{
			MongoClusterDbList mongoClusterDbList = MongoDbTransaction.getMongoDatabaseList( clusterUniqueName);
			return RestObject.retOKWithPayload(mongoClusterDbList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collections:list", method = RequestMethod.GET)
	@Operation(summary = "Get the list of collections of a single database of a cluster/Mongo Server")
	public ResponseEntity<RestObject> 
	mongoDatabaseCollectionList(@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
								@RequestHeader(value="databaseName") String databaseName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error getting Mongo database collections");
		}
		
		try	{
			MongoClusterDbCollectionList ret = MongoDbTransaction.getMongoClusterDbCollectionList(clusterUniqueName, databaseName);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection:add", method = RequestMethod.PUT)
	@Operation(summary = "Add new collection to a Mongo Database")
	public ResponseEntity<RestObject> 
	mongoDatabaseCollectionAdd(@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
								@RequestHeader(value="databaseName") String databaseName,
								@RequestHeader(value="collectionName") String collectionName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database collection");
		}
		
		try	{
			MongoDbTransaction.addCollection (clusterUniqueName, databaseName, collectionName);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket:add", method = RequestMethod.PUT)
	@Operation(summary = "Add new bucket to a Mongo Database")
	public ResponseEntity<RestObject> 
	mongoDatabaseBucketAdd(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
							@RequestHeader(value="databaseName") String databaseName,
							@RequestHeader(value="bucketName") String bucketName) {

		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database bucket");
		}
		
		try	{
			MongoDbTransaction.addBucket(clusterUniqueName, databaseName, bucketName, mongoClusterDb);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete bucket from Mongo Database")
	public ResponseEntity<RestObject> 
	mongoDatabaseBucketDelete(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
								@RequestHeader(value="databaseName") String databaseName,
								@RequestHeader(value="bucketName") String bucketName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database bucket");
		}
		
		try	{
			MongoDbTransaction.deleteBucket(clusterUniqueName, databaseName, bucketName, mongoClusterDb);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collections:drop", method = RequestMethod.DELETE)
	@Operation(summary = "Drop collection from Mongo Database")
	public ResponseEntity<RestObject> 
	mongoDatabaseCollectionDelete(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
									@RequestHeader(value="databaseName") String databaseName,
									@RequestHeader(value="collectionName") String collectionName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) ) {
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database collection");
		}
		
		try	{
			MongoDbTransaction.deleteCollectionFromDatabase(clusterUniqueName, databaseName, collectionName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/index:create", method = RequestMethod.PUT)
	@Operation(summary = "Create an index for collection")
	public ResponseEntity<RestObject> 
	mongoCollectionIndexCreate(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
								@RequestHeader(value="databaseName") String databaseName,
								@RequestHeader(value="collectionName") String collectionName,
								@RequestHeader(value="fieldName") String fieldName )  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoDbTransaction.addIndexToCollection(clusterUniqueName, databaseName,collectionName,fieldName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/index:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Create an index for collection")
	public ResponseEntity<RestObject> 
	mongoCollectionIndexDelete(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
								@RequestHeader(value="databaseName") String databaseName,
								@RequestHeader(value="collectionName") String collectionName,
								@RequestHeader(value="fieldName") String fieldName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoDbTransaction.deleteIndexToCollection(clusterUniqueName, databaseName,collectionName,fieldName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	//////////////////////////Update Records ////////////////////////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/replace-update:single", method = RequestMethod.POST)
	@Operation(summary = "Replace/update object by id")
	public ResponseEntity<RestObject> 
	replaceDocumentById(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="mongoClusterName") String mongoClusterName,
							@RequestHeader(value="mongoDatabaseName") String mongoDatabaseName,
							@RequestHeader(value="mongoCollectionName") String mongoCollectionName,
							@RequestHeader(value="idObject") String idObject,
							@RequestHeader(value="operation", defaultValue = "UPDATE",  required = true) String operation, // UPDATE/REPLACE
							@RequestBody String object) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long countOperation = MongoDbTransaction.replaceDocumentById( mongoClusterName, mongoDatabaseName, mongoCollectionName, idObject, object);
			ObjectMongo o = new ObjectMongo(idObject, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	///////////////////////// DROP records ////////////////////////////////////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/delete:single", method = RequestMethod.DELETE)
    @Operation(summary = "Drop object by id")
	public ResponseEntity<RestObject> 
	deleteDocumentById(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="mongoClusterName") String mongoClusterName,
						@RequestHeader(value="mongoDatabaseName") String mongoDatabaseName,
						@RequestHeader(value="mongoCollectionName") String mongoCollectionName,
						@RequestHeader(value="idObject") String idObject) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long count = MongoDbTransaction.deleteDocumentFromCollection(mongoClusterName, mongoDatabaseName, mongoCollectionName, idObject);
			ObjectMongo o = new ObjectMongo(idObject, null, null, count);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/delete:multiple", method = RequestMethod.POST)
    @Operation(summary = "Drop Records from a list of ids")
	public ResponseEntity<RestObject> 
	deleteMultipleDocuments(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
							@RequestHeader(value="databaseName") String databaseName,
							@RequestHeader(value="collectionName") String collectionName,
							@RequestBody List<String> jsonDocument) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long countOperation = MongoDbTransaction.deleteMultipleDocuments(clusterUniqueName,databaseName, collectionName, jsonDocument);
			ObjectMongo o = new ObjectMongo(null, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/drop:simple-text", method = RequestMethod.DELETE)
	@Operation(summary = "Drop Records")
	public ResponseEntity<RestObject> 
	deleteManyRecordsSimpleTextSearch(	@RequestHeader(value="requestId") String requestId,
										@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
										@RequestHeader(value="databaseName") String databaseName,
										@RequestHeader(value="collectionName") String collectionName,
										@RequestHeader(value="fieldName") String itemToSearchAndDelete,
										@RequestHeader(value="language") String language) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long countOperation = MongoDbTransaction.deleteManyRecordsSimpleTextSearch(clusterUniqueName, databaseName,	collectionName,	itemToSearchAndDelete, language);
			ObjectMongo o = new ObjectMongo(null, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/drop:many", method = RequestMethod.DELETE)
	@Operation(summary = "Drop Records")
	public ResponseEntity<RestObject> 
	deleteManyRecords(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
						@RequestHeader(value="databaseName") String databaseName,
						@RequestHeader(value="collectionName") String collectionName,
						@RequestHeader(value="itemToSearch") String itemToSearch,
						@RequestHeader(value="valueToSearch") String valueToSearch,
						@RequestHeader(value="operator", defaultValue = "$in") String operator,
						@RequestHeader(value="valueToSearchType", defaultValue = "STRING") String valueToSearchType) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long countOperation = MongoDbTransaction.deleteManyRecords(clusterUniqueName, databaseName, collectionName, itemToSearch, valueToSearch, operator, valueToSearchType);
			ObjectMongo o = new ObjectMongo(null, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/drop:range", method = RequestMethod.DELETE)
	@Operation(summary = "Drop Records in a range")
	public ResponseEntity<RestObject> 
	deleteManyRecordsRange(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
							@RequestHeader(value="databaseName") String databaseName,
							@RequestHeader(value="collectionName") String collectionName,
							@RequestHeader(value="itemToSearch") String itemToSearch,
							@RequestHeader(value="from") String from,
							@RequestHeader(value="to") String to,
							@RequestHeader(value="valueSearchType") String valueSearchType) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long countOperation = MongoDbTransaction.deleteManyRecordsRange(clusterUniqueName, databaseName, collectionName, itemToSearch, from, to, valueSearchType);
			ObjectMongo o = new ObjectMongo(null, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	///////////////////////////Query ////////////////////////////////////////////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:repo", method = RequestMethod.GET)
	@Operation(summary = "Query previously saved record set")
	public ResponseEntity<RestObject> 
	queryDocuments(	@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="dbType", defaultValue = "TEMP") String dbType,
					@RequestHeader(value="objectType", defaultValue = "RESULTQUERYHEADER") String objectType,
					@RequestHeader(value="sqlId") String sqlId,
					@RequestHeader(value="fromDateTime") String fromDateTime,
					@RequestHeader(value="toDateTime") String toDateTime) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			MongoResultSet mongoResultSet = MongoGet.queryResults(	ResultQueryHeader.class, 
																	objectType, 
																	user, 
																	Integer.parseInt(sqlId), 
																	Long.parseLong(fromDateTime), 
																	Long.parseLong(toDateTime) );
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:text", method = RequestMethod.GET)
	@Operation(summary = "Search collection for text")
	public ResponseEntity<RestObject> 
	searchSimpleText(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
						@RequestHeader(value="databaseName") String databaseName,
						@RequestHeader(value="collectionName") String collectionName,
						@RequestHeader(value="language") String language,
						@RequestHeader(value="itemToSearch") String itemToSearch,
						@RequestHeader(value="isHighestScore") String isHighestScore) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoResultSet mongoResultSet = MongoDbTransaction.searchSimpleText(clusterUniqueName, databaseName, collectionName, itemToSearch, language, isHighestScore);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/count", method = RequestMethod.PUT)
	@Operation(summary = "Get count of all documents in collection")
	public ResponseEntity<RestObject> 
	getCollectionDocsCount(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterName") String clusterName,
							@RequestHeader(value="databaseName") String dbName,
							@RequestHeader(value="collectionName") String cName,
							@RequestHeader(value="isEstimate") String isEstimate) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long count = MongoDbTransaction.getCollectionDocsCount(clusterName, dbName,	cName, isEstimate);
			return RestObject.retOKWithPayload(new GenericResponse(Long.toString(count)), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/add:single", method = RequestMethod.PUT)
	@Operation(summary = "Add single document to collection")
	public ResponseEntity<RestObject> 
	addDocumentToCollection(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterName") String clusterName,
								@RequestHeader(value="databaseName") String dbName,
								@RequestHeader(value="collectionName") String cName,
								@RequestBody String jsonDocument) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			boolean ret = MongoDbTransaction.addDocumentToCollection(clusterName, dbName, cName, jsonDocument);
			if(ret)
				return RestObject.retOK(requestId, methodName);
			else
				return RestObject.retException(requestId, methodName, "Could not add Mongo document");

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	/** Copy from other storage systems */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/embedded/adhoc:sql", method = RequestMethod.PUT)
    @Operation(summary = "Copy records to collection from Embedded adhoc query")
	public ResponseEntity<RestObject> 
	copyEmbeddedQueryToCollection(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="toMongoClusterName") String toMongoClusterName,
									@RequestHeader(value="toMongoDbName") String toMongoDbName,
									@RequestHeader(value="toMongoCollectionName") String toMongoCollectionName,
									@RequestHeader(value="toBatchCount", defaultValue = "0") String toBatchCount,
									@RequestHeader(value="fromEmbeddedType", defaultValue = "H2") String fromEmbeddedType,
									@RequestHeader(value="fromClusterId") String fromClusterId,
									@RequestHeader(value="fromEmbeddedDatabaseName") String fromEmbeddedDatabaseName,
									@RequestHeader(value="fromEmbeddedSchemaName") String fromEmbeddedSchemaName,
									@RequestBody String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
			
			if(!MongoGet.isCollection(mongoDbConnection, toMongoDbName, toMongoCollectionName)) {
				RestObject transferableObject = new RestObject(	new GenericResponse("WARNING"), 
																requestId, 
																"Collection does not exist :" + toMongoCollectionName,  
																"Collection does not exist :" + toMongoCollectionName, 
																ErrorCode.WARNING, 
																Sources.SQLTHUNDER, 
																ErrorSeverity.LOW, 
																methodName);
				return new ResponseEntity<> (transferableObject, HttpStatus.OK);
			}
			
			
			H2Static h2Db = new H2Static(Long.parseLong(fromClusterId), fromEmbeddedDatabaseName );
			TableFormatMap recordSet=
					h2Db.execStaticQueryWithTableFormat(sqlContent);
			
			
			
			int retOperation = MongoPut.addDocumentsToCollection(	mongoDbConnection, 
																	toMongoDbName, 
																	toMongoCollectionName,
																	recordSet.getListOfRows(),
																	Integer.parseInt(toBatchCount),
																	0);
			
			
			mongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + retOperation + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/rdbms:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from RDBMS query")
	public ResponseEntity<RestObject> 
	copyRDBMSQueryToCollection(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="toMongoClusterName") String toMongoClusterName,
								@RequestHeader(value="toMongoDbName") String toMongoDbName,
								@RequestHeader(value="toMongoCollectionName") String toMongoCollectionName,
								@RequestHeader(value="fromRdbmsSchemaUniqueName") String fromRdbmsSchemaUniqueName,
								@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
								@RequestBody String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
			
			if(!MongoGet.isCollection(mongoDbConnection, toMongoDbName, toMongoCollectionName)) {
				RestObject transferableObject = new RestObject(	new GenericResponse("WARNING"), 
																requestId, 
																"Collection does not exist :" + toMongoCollectionName,  
																"Collection does not exist :" + toMongoCollectionName, 
																ErrorCode.WARNING, 
																Sources.SQLTHUNDER, 
																ErrorSeverity.LOW, 
																methodName);
				return new ResponseEntity<> (transferableObject, HttpStatus.OK);
			}
			
			ResultQueryJsonRows resultQuery = SqlQueryRepoUtils.execStaticQueryWithJsonRows(fromRdbmsSchemaUniqueName, sqlContent);
			int retOperation = MongoPut.addDocumentsToCollection(	mongoDbConnection, 
					toMongoDbName, 
																	toMongoCollectionName,
																	resultQuery.getResultQueryRows(),
																	Integer.parseInt(batchCount),
																	0);
			
			
			mongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + retOperation + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from Elastic DSL query")
	public ResponseEntity<RestObject> 
	copyElasticDslToCollection( @RequestHeader(value="requestId") String requestId,
							    @RequestHeader(value="toMongoClusterName") String toMongoClusterName,
								@RequestHeader(value="toMongoDatabaseName") String toMongoDatabaseName,
								@RequestHeader(value="toMongoCollectionName") String toMongoCollectionName,
								@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
								@RequestHeader(value="fromElasticHttpVerb", defaultValue = "GET") String fromElasticHttpVerb,
								@RequestHeader(value="fromElasticEndPoint") String fromElasticEndPoint,
								@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
								@RequestBody String httpPayload) {


		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				if(!MongoGet.isCollection(mongoDbConnection, toMongoDatabaseName, toMongoCollectionName) ){
					return RestObject.retException(requestId, methodName, "Collection " + toMongoCollectionName + " does not exist");
				}
				Object ret = ElasticInfo.executeGeneric(elasticLowLevelWrapper, fromElasticHttpVerb, fromElasticEndPoint, "Y", httpPayload);
				elasticLowLevelWrapper.disconnect();
				ElasticPayload payload = ElasticInfo.parseResponse(ret);
				/*Transfer Data from Elastic to MongoDb*/
				int countProcessed = MongoPut.pushElasticToCollection (	payload,
																		mongoDbConnection, 
																		toMongoDatabaseName, 
																		toMongoCollectionName,
																		Integer.parseInt(batchCount) );	
				mongoDbConnection.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
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
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/elastic:sql", method = RequestMethod.PUT)
    @Operation(summary = "Create/add records to collection from Elastic SQL query")
	public ResponseEntity<RestObject> 
	copyElasticSqlToCollection( @RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="toMongoClusterName") String toMongoClusterName,
								@RequestHeader(value="toMongoDatabaseName") String toMongoDatabaseName,
								@RequestHeader(value="toMongoCollectionName") String toMongoCollectionName,
								@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
								@RequestHeader(value="fromElasticFetchSize") Integer fromElasticFetchSize,
								@RequestHeader(value="fetchSize", defaultValue = "0") String fetchSize,
								@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
								@RequestBody String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{

			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				if(!MongoGet.isCollection(mongoDbConnection, toMongoDatabaseName, toMongoCollectionName) ){
					return RestObject.retException(requestId, methodName, "Collection " + toMongoDatabaseName + " does not exist");
				}
				JSONObject ret = SearchSql.searchSql(elasticLowLevelWrapper, sqlContent, Integer.valueOf(fetchSize) );
				List<String> elasticFromResultSet = Objects.requireNonNull(SqlResponse.toSqlResponse(ret.toJSONString())).toListOfJsonStrings();
				elasticLowLevelWrapper.disconnect();
				/*Transfer Data*/
				long countProcessed 
				= MongoPut.addDocumentsToCollection(mongoDbConnection, 
													toMongoDatabaseName, 
													toMongoCollectionName,
													elasticFromResultSet,
													Integer.parseInt(batchCount),
													0);
				
				mongoDbConnection.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
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
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb/search:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from another Mongodb collection(s) simple search")
	public ResponseEntity<RestObject> 
	copySimpleSearchToCollection(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
									@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
									@RequestHeader(value="fromCollectionName") String fromCollectionName,
									@RequestHeader(value="itemToSearch") String itemToSearch,
									@RequestHeader(value="valueToSearch") String valueToSearch,
									@RequestHeader(value="valueToSearchType") String valueToSearchType,
									@RequestHeader(value="operator", defaultValue = "$eq") String operator,
									@RequestHeader(value="toClusterUniqueName") String toClusterUniqueName,
									@RequestHeader(value="toMongoDbName") String toMongoDbName,
									@RequestHeader(value="toCollectionName") String toCollectionName,
									@RequestHeader(value="batchCount", defaultValue = "0") String batchCount) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromClusterUniqueName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());
			
		
			MongoResultSet ret = MongoGet.searchDocument(fromMongoDbConnection, 
																	fromMongoDbName, 
																	fromCollectionName,
																	itemToSearch,  
																	valueToSearch,
																	operator,
																	valueToSearchType,
																	true,
																	false /*determine metadata is false*/) ;
			

			fromMongoDbConnection.disconnect();
			
			
			MongoClusterRecord toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
			MongoDbConnection toMongoDbConnection = new MongoDbConnection(	toMongoClusterRecord.getConnString(), 
																			toMongoClusterRecord.getClusterId(),
																			toMongoClusterRecord.getUniqueName());

			int countProcessed = MongoPut.addDocumentsToCollection(	toMongoDbConnection, 
																	toMongoDbName, 
																	toCollectionName,
																	MongoResultSet.getResultSetAsJson(ret.getResultSet()),
																	Integer.parseInt(batchCount),
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb/search:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from another Mongodb collection(s)")
	public ResponseEntity<RestObject> 
	copyRangeSearchToCollection(@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
								@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
								@RequestHeader(value="fromCollectionName") String fromCollectionName,
								@RequestHeader(value="itemToSearch") String itemToSearch,
								@RequestHeader(value="fromValue") String fromValue,
								@RequestHeader(value="toValue") String toValue,
								@RequestHeader(value="valueSearchType") String valueSearchType,
								@RequestHeader(value="toMongoClusterName") String toClusterUniqueName,
								@RequestHeader(value="toMongoDatabaseName") String toMongoDbName,
								@RequestHeader(value="toMongoCollectionName") String toCollectionName,
								@RequestHeader(value="batchCount", defaultValue = "0") String batchCount) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromClusterUniqueName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());
			
			MongoResultSet ret = MongoGet.searchDocumentRange(	fromMongoDbConnection, 
																			fromMongoDbName, 
																			fromCollectionName,
																			itemToSearch,  
																			fromValue,
																			toValue,
																			valueSearchType,
																			true) ;
			

			fromMongoDbConnection.disconnect();
			
			
			MongoClusterRecord toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
			MongoDbConnection toMongoDbConnection = new MongoDbConnection(	toMongoClusterRecord.getConnString(), 
																			toMongoClusterRecord.getClusterId(),
																			toMongoClusterRecord.getUniqueName());
			

			int countProcessed = MongoPut.addDocumentsToCollection(	toMongoDbConnection, 
																	toMongoDbName, 
																	toCollectionName,
																	MongoResultSet.getResultSetAsJson(ret.getResultSet()),
																	Integer.parseInt(batchCount),
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy full collection")
	public ResponseEntity<RestObject> 
	copyFullCollectionToCollection(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
												
									@RequestHeader(value="toMongoClusterName") String toClusterUniqueName,
									@RequestHeader(value="toMongoDatabaseName") String toMongoDbName,
									@RequestHeader(value="toMongoCollectionName") String toCollectionName,
												
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
			
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			fromMongoDbConnection.disconnect();
			
			
			MongoClusterRecord toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
			MongoDbConnection toMongoDbConnection = new MongoDbConnection(	toMongoClusterRecord.getConnString(),
																			toMongoClusterRecord.getClusterId(),
																			toMongoClusterRecord.getUniqueName());
			
			int countProcessed = MongoPut.addDocumentsToCollection(	toMongoDbConnection, 
																	toMongoDbName, 
																	toCollectionName,
																	MongoResultSet.getResultSet(mongoResultSet.getResultSet()),
																	Integer.parseInt(batchCount),
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb:adhoc", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from full Mongodb collection")
	public ResponseEntity<RestObject> 
	copyMongoAdhocMqlToCollection(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
									@RequestHeader(value="toMongoClusterName") String toClusterUniqueName,
									@RequestHeader(value="toMongoDatabaseName") String toMongoDbName,
									@RequestHeader(value="toMongoCollectionName") String toCollectionName,
									@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
									@RequestBody String bsonQuery) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());

			MongoResultSet ret = MongoGet.execDynamicQuery(	fromMongoClusterName, 
															fromMongoDatabaseName, 
															fromMongoCollectionName,
															bsonQuery,
															false);

			fromMongoDbConnection.disconnect();

			MongoClusterRecord toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
			MongoDbConnection toMongoDbConnection = new MongoDbConnection(	toMongoClusterRecord.getConnString(), 
																			toMongoClusterRecord.getClusterId(),
																			toMongoClusterRecord.getUniqueName());

			int countProcessed = MongoPut.addDocumentsToCollection(	toMongoDbConnection, 
																	toMongoDbName, 
																	toCollectionName,
																	MongoResultSet.getResultSetAsJson(ret.getResultSet()),
																	Integer.parseInt(batchCount),
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv file to collection")
	public ResponseEntity<RestObject> 
	copyCsvToCollection(@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="fileType", defaultValue = "N") String fileType,				
						@RequestHeader(value="toMongoClusterName") String toClusterUniqueName,
						@RequestHeader(value="toMongoDatabaseName") String toMongoDbName,
						@RequestHeader(value="toMongoCollectionName") String toCollectionName,
						@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
						@RequestParam("file") MultipartFile file) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String fileName = StringUtils.generateUniqueString32();
		try	{
			String fullFilePath = storageService.storeTmp(file, fileName);
			String csvContent = CsvWrapper.readFile(fullFilePath);
			String isCompressed = "N";
			if( fileType.equals("text/csv") ) {
				isCompressed = "N";
			} else if( fileType.equals("application/x-zip-compressed")) {
				isCompressed = "Y";
			} else {
				return RestObject.retException(requestId, methodName, "Unknown File Type");
			}
			List<String> jList = CsvWrapper.stringToJsonList(csvContent,isCompressed);
			MongoClusterRecord toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
			MongoDbConnection toMongoDbConnection = new MongoDbConnection(	toMongoClusterRecord.getConnString(), 
																			toMongoClusterRecord.getClusterId(),
																			toMongoClusterRecord.getUniqueName());
			

			int countProcessed = MongoPut.addDocumentsToCollection(	toMongoDbConnection, 
																	toMongoDbName, 
																	toCollectionName,
																	jList,
																	Integer.parseInt(batchCount),
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse("Added " + countProcessed + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			storageService.deleteTmp(fileName);
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/add:batch", 
					method = RequestMethod.POST, 
					consumes = "multipart/form-data; charset=utf-8")
	@Operation(summary = "Add Docs to Collection")
	public ResponseEntity<RestObject> 
	addBatchDocumentToCollection(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="clusterName") String clusterName,
									@RequestHeader(value="databaseName") String dbName,
									@RequestHeader(value="collectionName") String cName,
									@RequestHeader(value="bulkCount", defaultValue = "0") String bCount,
									@RequestParam("attachment") MultipartFile attachment ) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
		MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
		
		try	{
			File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), "temp");
			FileOutputStream o = new FileOutputStream(attachmentFile);
			IOUtils.copy(attachment.getInputStream(), o);
			o.close();
			boolean isZip = true;
			int bulkCnt = 0;
			try { (new ZipFile(attachmentFile)).close(); } catch (ZipException e) { isZip = false; }
			try { bulkCnt = Integer.parseInt(bCount); } catch (Exception ignored) {  }
			try {
				
				List<Document> jsonDocument = new ArrayList<Document>();
				int count = 0;
				String line;
				if(isZip ) { // handle ZIP files
					ZipFile zipFile = new ZipFile(attachmentFile);
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while(entries.hasMoreElements()) {
						ZipEntry zipEntry = entries.nextElement();
						if(!zipEntry.isDirectory()) {
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
							while((line = bufferedReader.readLine()) != null){
								count++;
								if(JsonUtils.isJsonValid(line)) {
									if(bulkCnt == 0) {
										MongoPut.addDocument(mongoDbConnection, dbName, cName, line);
									} else {
										if(count == bulkCnt) {
											jsonDocument.add(Document.parse(line) );
											MongoPut.bulkInsert(mongoDbConnection, dbName, cName, jsonDocument);
											jsonDocument.clear();
										} else {
											jsonDocument.add(Document.parse(line) );
										}
									}
								} else {
									String jsonString = JsonUtils.commastringToJsonString(line);
									if(bulkCnt == 0) {
										MongoPut.addDocument(mongoDbConnection, dbName, cName, jsonString);
									} else {
										if(count == bulkCnt) {
											jsonDocument.add(Document.parse(line) );
											MongoPut.bulkInsert(mongoDbConnection, dbName, cName, jsonDocument);
											jsonDocument.clear();
										} else {
											jsonDocument.add(Document.parse(jsonString) );
										}
									}
								}
							}
							if(!jsonDocument.isEmpty())
								MongoPut.bulkInsert(mongoDbConnection, dbName, cName, jsonDocument);
							
							bufferedReader.close();
						}
					}
					zipFile.close();
				} else { // handle regular files
						BufferedReader objReader = new BufferedReader(new FileReader(attachmentFile.getAbsolutePath()));
					while ((line = objReader.readLine()) != null) {
						count++;
						if(JsonUtils.isJsonValid(line)) {
							if(bulkCnt == 0) {
								MongoPut.addDocument(mongoDbConnection, dbName, cName, line);
							} else {
								if(count == bulkCnt) {
									MongoPut.bulkInsert(mongoDbConnection, dbName, cName, jsonDocument);
									jsonDocument.clear();
								} else {
									jsonDocument.add(Document.parse(line) );
								}
							}
						} else {
							String jsonString = JsonUtils.commastringToJsonString(line);
							if(bulkCnt == 0) {
								MongoPut.addDocument(mongoDbConnection, dbName, cName, jsonString);
							} else {
								if(count == bulkCnt) {
									MongoPut.bulkInsert(mongoDbConnection, dbName, cName, jsonDocument);
									jsonDocument.clear();
								} else {
									jsonDocument.add(Document.parse(jsonString) );
								}
							}
						}
					}
					MongoPut.bulkInsert(mongoDbConnection, dbName, cName, jsonDocument);
					objReader.close();
				}
			}	catch (Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}	finally {
				attachmentFile.deleteOnExit();
			}
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	
	//////////////////////////////////////////////  Search Documents //////////////////////////////////////////////////////
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document:get", method = RequestMethod.GET)
	@Operation(summary = "Get previously saved document/result-set")
	public ResponseEntity<RestObject> 
	getDocument(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="clusterName") String clusterName,
				@RequestHeader(value="dbName") String dbName,
				@RequestHeader(value="cName") String cName,
				@RequestHeader(value="docId") String docId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String count = MongoDbTransaction.getDocumentById(clusterName,dbName, cName, docId);
			return RestObject.retOKWithPayload(ResultQuery.toResultQuery(count), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document:firstn", method = RequestMethod.GET)
	@Operation(summary = "Get first N documents in the collection")
	public ResponseEntity<RestObject> 
	getFirstNDocuments( @RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterName") String clusterName,
						@RequestHeader(value="databaseName") String databaseName,
						@RequestHeader(value="collectionName") String collectionName,
						@RequestHeader(value="limit") String limit) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if(databaseName.isEmpty() || collectionName.isEmpty()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Collection or Database is empty")) ;
			}
			StringList lst = MongoDbTransaction.getFirstNDocuments(clusterName, databaseName, collectionName, limit);
			return RestObject.retOKWithPayload(lst, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket/document:firstn", method = RequestMethod.GET)
	@Operation(summary = "Get first N documents in the bucket")
	public ResponseEntity<RestObject> 
	getFirstNBucketDocuments(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterName") String clusterName,
								@RequestHeader(value="databaseName") String databaseName,
								@RequestHeader(value="bucketName") String bucketName,
								@RequestHeader(value="limit") String limit) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if(databaseName.isEmpty() || bucketName.isEmpty()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Bucket or Database is empty")) ;
			}
			LargeMongoBinaryFileMetaList ret = MongoDbTransaction.getFirstNBucketDocuments(clusterName, databaseName, bucketName, limit);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:simple", method = RequestMethod.GET)
	@Operation(summary = "Simple Search, providing item to search, value to search and value type")
	public ResponseEntity<RestObject> 
	searchDocumentSimple(	@RequestHeader(value="user") String user,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterName") String clusterName,
							@RequestHeader(value="databaseName") String databaseName,
							@RequestHeader(value="collectionName") String collectionName,
							@RequestHeader(value="itemToSearch") String itemToSearch,
							@RequestHeader(value="valueToSearch") String valueToSearch,
							@RequestHeader(value="valueToSearchType") String valueToSearchType,
							@RequestHeader(value="operator", defaultValue = "$eq") String operator,
							@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
							@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
							@RequestHeader(value="sqlName", defaultValue = "") String sqlName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoResultSet mongoResultSet = MongoDbTransaction.searchDocument (clusterName, databaseName, collectionName, itemToSearch, valueToSearch, operator,valueToSearchType);
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));

			if( persist.compareToIgnoreCase("Y") == 0 ) {
				User u = authUtil.getUser(user);
				String folder = "./snapshots/" + String.valueOf(u.getId());  
				try {
					String fileName = StringUtils.generateUniqueString32();
					if(sqlName.isBlank()) {
						sqlName = fileName;
					}
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, mongoResultSet.toString());
					if(isOk) {
						long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
						
						SimpleMongoSearch s = new SimpleMongoSearch(itemToSearch,
																	valueToSearch,
																	operator,
																	valueToSearchType);
						
						MongodbQueryObj queryObj = new MongodbQueryObj("SimpleMongoSearch", clusterName, databaseName, collectionName, s.toString());
						Map<String, String> map = new HashMap<>();
						map.put("type", "searchDocumentSimple");
						map.put("clusterUniqueName", clusterName);
						map.put("mongoDbName", databaseName);
						map.put("collectionName", collectionName);
						map.put("MongodbQueryObj", queryObj.toString());
						if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
							map.put("comment", comment);
						}
						JSONObject jo = new JSONObject(map);
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "SimpleMongoSearch", u.getId(), timestamp, jo.toString());
						SnapshotMongoDbRepo snapshotDbRepo = new SnapshotMongoDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
				}
			}
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:range", method = RequestMethod.GET)
	@Operation(summary = "Search for a range of Documents")
	public ResponseEntity<RestObject> 
	searchDocumentRange(	@RequestHeader(value="user") String user,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterName") String clusterName,
							@RequestHeader(value="databaseName") String databaseName,
							@RequestHeader(value="collectionName") String collectionName,
							@RequestHeader(value="itemToSearch") String itemToSearch,
							@RequestHeader(value="fromValue") String fromValue,
							@RequestHeader(value="toValue") String toValue,
							@RequestHeader(value="valueSearchType") String valueSearchType,
							@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
							@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
							@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoResultSet mongoResultSet = MongoDbTransaction.searchDocumentRange(clusterName,	databaseName, collectionName, itemToSearch,	fromValue, toValue, valueSearchType);
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				User u = authUtil.getUser(user);
				String folder = "./snapshots/" + String.valueOf(u.getId());  
				try {
					String fileName = StringUtils.generateUniqueString32();
					if(sqlName.isBlank()) {
						sqlName = fileName;
					}
					
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, mongoResultSet.toString());
					if(isOk) {
						long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
						RangeMongoSearch r = new RangeMongoSearch(	itemToSearch,
																	Integer.parseInt(fromValue),
																	Integer.parseInt(toValue),
																	valueSearchType);
						
						MongodbQueryObj queryObj = new MongodbQueryObj("RangeMongoSearch", clusterName, databaseName, collectionName, r.toString());
						Map<String, String> map = new HashMap<>();
						map.put("type", "searchDocumentRange");
						map.put("clusterUniqueName", clusterName);
						map.put("mongoDbName", databaseName);
						map.put("collectionName", collectionName);
						map.put("MongodbQueryObj", queryObj.toString());
						if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
							map.put("comment", comment);
						}
						JSONObject jo = new JSONObject(map);
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "RangeMongoSearch", u.getId(), timestamp, jo.toString());
						SnapshotMongoDbRepo snapshotDbRepo = new SnapshotMongoDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:complex-and", method = RequestMethod.POST)
	@Operation(summary = "Search for a range by Complex And Statements")
	public ResponseEntity<RestObject> 
	searchDocumentComplexAnd(	@RequestHeader(value="user") String user,
								@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="clusterName") String clusterName,
								@RequestHeader(value="databaseName") String databaseName,
								@RequestHeader(value="collectionName") String collectionName,
								@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
								@RequestHeader(value="comment", required = false) String comment,
								@RequestHeader(value="sqlName") String sqlName,
								@RequestBody String complexAndQuery) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
			MongoResultSet mongoResultSet = MongoGet.searchDocumentComplexAnd(	mongoDbConnection, 
																				databaseName, 
																				collectionName,
																				ComplexAndSearch.toComplexAndSearch(complexAndQuery)
																				) ;
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				User u = authUtil.getUser(user);
				String folder = "./snapshots/" + String.valueOf(u.getId());  
				String fileName = StringUtils.generateUniqueString32();
				if(sqlName.isBlank()) {
					sqlName = fileName;
				}
				
				try {
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, mongoResultSet.toString());
					if(isOk) {
						long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
						ComplexAndMongoSearch r = ComplexAndMongoSearch.fromComplexAndSearch(Objects.requireNonNull(ComplexAndSearch.toComplexAndSearch(complexAndQuery)));
						MongodbQueryObj queryObj = new MongodbQueryObj("ComplexAndQuery", clusterName, databaseName, collectionName, r.toString());
						Map<String, String> map = new HashMap<>();
						map.put("type", "searchDocumentComplexAnd");
						map.put("clusterUniqueName", clusterName);
						map.put("mongoDbName", databaseName);
						map.put("collectionName", collectionName);
						map.put("MongodbQueryObj", queryObj.toString());
						if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
							map.put("comment", comment);
						}
						JSONObject jo = new JSONObject(map);
						
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "ComplexAndMongoSearch", u.getId(), timestamp, jo.toString());
						SnapshotMongoDbRepo snapshotDbRepo = new SnapshotMongoDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
				}
			}
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document:move", method = RequestMethod.POST)
	@Operation(summary = "Move document/result set from one collection to another across clusters and databases")
	public ResponseEntity<RestObject> 
	moveDocument(	@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="docId") String docId,
					@RequestHeader(value="cNameSource") String cNameSource,
					@RequestHeader(value="dbNameSource") String dbNameSource,
					@RequestHeader(value="clusterNameSource") String clusterNameSource) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String ret = MongoDbTransaction.getDocumentById(clusterNameSource, dbNameSource, cNameSource, docId);
			MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(clusterNameSource);
			if(!MongoPut.addUserDocumentThreaded(user, ret, false)) {
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Cannot move document " + docId));
			}
			MongoPut.deleteDocumentById(new MongoDbConnection(mongoClusterRecordSource), dbNameSource, cNameSource, docId);
			RestObject transferableObject = new RestObject(new GenericResponse("OK"), requestId, methodName);
			return new ResponseEntity<> (transferableObject, HttpStatus.OK);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/rdbms/table:create", method = RequestMethod.PUT)
	@Operation(summary = "Create RDBMS table from ResultQuery document")
	public ResponseEntity<RestObject> 
	createRdbmsTableFromDocument(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="mongoDocId") String mongoDocId,
									@RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
									@RequestHeader(value="rdbmsConnectionName") String rdbmsConnectionName,
									@RequestHeader(value="rdbmsSchema", required = false) String rdbmsSchema,
									@RequestHeader(value="rdbmsTable", required = false) String rdbmsTable) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection mongoDbConnectionSource = new MongoDbConnection(mongoClusterRecordSource);
			String ret = MongoGet.getDocumentById(	mongoDbConnectionSource, 
													fromMongoDatabaseName,
													fromMongoCollectionName,
													mongoDocId);
			
			
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsConnectionName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			
			ResultQuery resultQuery = ResultQuery.toResultQuery(ret);
			DdlDmlUtils.createTable(resultQuery, 
									connectionDetailInfo.getDbType(), 
									rdbmsConnectionName, 
									rdbmsSchema, 
									rdbmsTable);

            assert resultQuery != null;
            int countInserted = DdlDmlUtils.insertTable(resultQuery, 			/*ResultQuery resultQuery*/
														rdbmsConnectionName,  	/*String rdbmsConnectionName*/
														rdbmsSchema, 			/*String rdbmsSchema*/
														rdbmsTable 				/*String rdbmsTable*/
														);
			return RestObject.retOKWithPayload(new GenericResponse("Inserted: " + String.valueOf(countInserted) + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/rdbms/table:compound", method = RequestMethod.PUT)
	@Operation(summary = "Compound Document")
	public ResponseEntity<RestObject> 
	compoundDocument(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="rdbmsConnectionName") String rdbmsConnectionName,
						@RequestHeader(value="rdbmsSchema", required = false) String rdbmsSchema,
						@RequestHeader(value="rdbmsTable", required = false) String rdbmsTable,
						@RequestBody List<MongoObjectRef> listOfMongoObjects) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			MongoToH2Sql mongoToH2Sql = new MongoToH2Sql();
			int totalCount = mongoToH2Sql.addMongoObjects(	listOfMongoObjects, 
															rdbmsConnectionName, 
															rdbmsSchema,
															rdbmsTable);
			
			return RestObject.retOKWithPayload(new GenericResponse("Compounded: " + String.valueOf(totalCount) + " records"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:repo", method = RequestMethod.POST)
	@Operation(summary = "Execute Repo Mql")
	public ResponseEntity<RestObject> 
	repoMql(	@RequestHeader(value="user") String user,
				@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
				@RequestHeader(value="mongoDbName") String mongoDbName,
				@RequestHeader(value="mqlId") String mqlId,
				@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
				@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
				@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
				@RequestBody String parameters) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoRepoDynamicMqlExecution mongoRepoDynamicMqlExecution = MongoRepoDynamicMqlExecution.toMongoRepoDynamicMqlExecution(parameters);
			MongoRepoDynamicMql mongoRepoDynamicMql = MongoRepoDynamicMqlExecution.toMongoRepoDynamicMql(mongoRepoDynamicMqlExecution);
            assert mongoRepoDynamicMql != null;
            String mqlString = MongoRepoDynamicMqlExecution.toRawMql(mongoRepoDynamicMql);
			Document d = MongoGeneric.execBson(clusterUniqueName, mongoDbName, mqlString);
			
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				User u = authUtil.getUser(user);
				String folder = "./snapshots/" + String.valueOf(u.getId());  
				try {
					String fileName = StringUtils.generateUniqueString32();
					if(sqlName.isBlank()) {
						sqlName = fileName;
					}
					
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, d.toJson());
					if(isOk) {
						long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
						Map<String, String> map = new HashMap<>();
						map.put("type", "repoMql");
						map.put("clusterUniqueName", clusterUniqueName);
						map.put("mongoDbName", mongoDbName);
						map.put("mqlId", mqlId);
						
						if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
							map.put("comment", comment);
						}
						JSONObject jo = new JSONObject(map);
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "ResultQuery", u.getId(), timestamp, jo.toString());
						SnapshotMongoDbRepo snapshotDbRepo = new SnapshotMongoDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
				}
			}
			
			
			return RestObject.retOKWithPayload(new JsonResponse(d.toJson()), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/** MQL Repo Management  */
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:search", method = RequestMethod.GET)
	@Operation(summary = "Get the Mql statement by searching a keyword")
	public ResponseEntity<RestObject> 
	searchQuery(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="stringToSearch") String stringToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			List<MongoRepoDynamicMql> lstMql = mongoClusterDb.getMql(stringToSearch);
			MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList(lstMql);
			return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new MQL statement or update an existing one")
	public ResponseEntity<RestObject> 
	addMongoQuery(	@RequestHeader(value="requestId") String requestId,
					@RequestBody MongoRepoDynamicMql mongoRepoDynamicMql) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if(mongoRepoDynamicMql != null) {
				mongoClusterDb.addMql(mongoRepoDynamicMql.getMqlReturnType(), 
										mongoRepoDynamicMql.getMqlCategory(),
										mongoRepoDynamicMql.getMqlClass(),
										mongoRepoDynamicMql.getType(),
										mongoRepoDynamicMql.getMqlName(),
										mongoRepoDynamicMql.getMqlDescription(),
										mongoRepoDynamicMql.getMqlContent(),
										mongoRepoDynamicMql.getActive());
				
				List<MongoRepoDynamicMql> lstMql = mongoClusterDb.getMqlByName(mongoRepoDynamicMql.getMqlName());
				MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList(lstMql);
				return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
				
			} else {
				RestObject transferableObject = new RestObject(	new GenericResponse("ERROR"), 
																requestId, 
																methodName);

				return new ResponseEntity<> (transferableObject, HttpStatus.OK);
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo//management/query:get", method = RequestMethod.GET)
	@Operation(summary = "Get the Mql statement by id")
	public ResponseEntity<RestObject> 
	getQueryById(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="id") String id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList();
		
		try	{
			int mqlId_ = Integer.parseInt(id);
			List<MongoRepoDynamicMql> lstDslId = mongoClusterDb.getMql(mqlId_);
			mongoRepoDynamicMqlList.setMongoRepoDynamicMqlLst(lstDslId);
			return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Mql statement")
	public ResponseEntity<RestObject> 
	deleteMongoQuery(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="mqlId") String mqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			int mqlId_ = Integer.parseInt(mqlId);	
			if(mqlId_ > 0) {
				mongoClusterDb.deleteMql(mqlId_);
				mongoClusterDb.deleteMqlParams(mqlId_);
				mongoClusterDb.deleteMqlToClusterBridge(mqlId_);
				
				List<MongoRepoDynamicMql> lstMql = mongoClusterDb.getMql(mqlId_);
				MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList(lstMql);
				return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Negative mqlId");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/param:add", method = RequestMethod.PUT)
	@Operation(summary = "Add params to an existing MQL statement")
	public ResponseEntity<RestObject> 
	addMongoQueryParam(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="mqlId") String mqlId,
						@RequestBody MongoRepoMqlParamInput paramInput) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			mongoClusterDb.insertMqlParam(	Long.parseLong(mqlId), 
											paramInput.getDynamicMqlParamName(), 
											paramInput.getDynamicMqlParamDefault(), 
											paramInput.getDynamicMqlParamType(), 
											paramInput.getDynamicMqlParamPosition(), 
											paramInput.getDynamicMqlParamOrder()
										);
			
			
			
			List<MongoRepoMqlParam> p = mongoClusterDb.getMqlParams(Long.parseLong(mqlId));
			MongoRepoDynamicMqlParamList ret = new MongoRepoDynamicMqlParamList(p);
			
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/param:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete MongoDb Query Parameter")
	public ResponseEntity<RestObject> 
	deleteMongoQueryParam(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="mqlId") String mqlId,
							@RequestHeader(value="paramId") String paramId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			long mqlId_ = Long.parseLong(mqlId);
			long paramId_ = Long.parseLong(paramId);
			mongoClusterDb.deleteMqlParam(mqlId_, paramId_);
			List<MongoRepoMqlParam> p = mongoClusterDb.getMqlParams(mqlId_);
			MongoRepoDynamicMqlParamList ret = new MongoRepoDynamicMqlParamList(p);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/param:generate", method = RequestMethod.PUT)
	@Operation(summary = "Generate Query Param")
	public ResponseEntity<RestObject> 
	generateQueryParam(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="noParams", defaultValue = "1") String noParams) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoRepoDynamicMqlParamInputList paramInputList = MongoRepoDynamicMqlParamInputList.generateParamInputList(Integer.parseInt(requestId));
			return RestObject.retOKWithPayload(paramInputList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	/*MQL to cluster bridges*/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge:get", method = RequestMethod.GET)
	@Operation(summary = "Get Query Bridges")
	public ResponseEntity<RestObject> 
	getQueryBridges(@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="mqlId") String mqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoDynamicMqlToClusterBridgeList 
			lstBridges = mongoClusterDb.getMqltoClusterBridges(Integer.parseInt(mqlId));
			return RestObject.retOKWithPayload(lstBridges, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge:add", method = RequestMethod.PUT)
	@Operation(summary = "Bridge MQL to Cluster")
	public ResponseEntity<RestObject> 
	addMqlToClusterBridge(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="mqlId") String mqlId,
							@RequestHeader(value="clusterId") String clusterId,
							@RequestHeader(value="active") String active) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		long mqlId_ = Integer.parseInt(mqlId);
		long clusterId_ = Integer.parseInt(clusterId);
		long active_ = Integer.parseInt(active);
		
		if(clusterId_ < 0 || mqlId_ < 0 || active_ < 0 || active_ > 1) {
			return RestObject.retException(requestId, methodName, "");
		}
		
		try	{
			mongoClusterDb.mergeMqlToClusterBridge(	mqlId_, 
													clusterId_, 
													active_);
			
			MongoDynamicMqlToClusterBridge ret = mongoClusterDb.getMqltoClusterBridge(clusterId_, mqlId_);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge:delete", method = RequestMethod.DELETE)
    @Operation(summary = "Delete Bridge MQL to Cluster")
	public ResponseEntity<RestObject> 
	deleteMqlToClusterBridge(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="bridgeId") String bridgeId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			mongoClusterDb.deleteMqlToClusterBridge(Integer.parseInt(bridgeId));
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/*Executions*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:bson", method = RequestMethod.POST)
	@Operation(summary = "Execute a generic Mql command providing it in Bson/Json format")
	public ResponseEntity<RestObject> 
	runAdhocBson(	@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
					@RequestHeader(value="mongoDbName") String mongoDbName,
					@RequestHeader(value="collectionName") String collectionName,
					@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
					@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
					@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
					@RequestBody String command) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			// Some examples :
			// { "age" : { "$gt" : 0 } }
			// {"age": {"$gt": 0, "$lt": 100}}
			// { "age" : { "$in" : [ 25, 52 ] } }
			MongoResultSet ret = MongoGet.execDynamicQuery(	clusterUniqueName, 
															mongoDbName, 
															collectionName,
															command,
															false);
			
			mongoHist.addMql("A", "A", user, command, "", DateTimeUtils.millisecondsSinceEpoch());
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			HistFileManagement.addNewStatement(userId, command, comment, timeStamp, mainFolder, "adhoc", "mongo");
			
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				
				String folder = "./snapshots/" + String.valueOf(u.getId());  
				try {
					String fileName = StringUtils.generateUniqueString32();
					if(sqlName.isBlank()) {
						sqlName = fileName;
					}
					
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, ret.toString());
					if(isOk) {
						long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
						Map<String, String> map = new HashMap<>();
						map.put("type", "runAdhocBson");
						map.put("clusterUniqueName", clusterUniqueName);
						map.put("mongoDbName", mongoDbName);
						map.put("collectionName", collectionName);
						map.put("command", command);
						if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
							map.put("comment", comment);
						}
						JSONObject jo = new JSONObject(map);
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "ResultQuery", u.getId(), timestamp, jo.toString());
						SnapshotMongoDbRepo snapshotDbRepo = new SnapshotMongoDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
				}
			}
			
			
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:adhoc", method = RequestMethod.POST)
	@Operation(summary = "Execute a generic Mql command")
	public ResponseEntity<RestObject> 
	runAdhocMql(@RequestHeader(value="user") String user,
				@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="clusterUniqueName") String clusterUniqueName,
				@RequestHeader(value="mongoDbName") String mongoDbName,
				@RequestHeader(value="collectionName") String collectionName,
				@RequestHeader(value="persist", required = false, defaultValue = "N") String persist,
				@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
				@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
				@RequestBody String command) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			User u = authUtil.getUser(user);
			// Some examples :
			// { "age" : { "$gt" : 0 } }
			// {"age": {"$gt": 0, "$lt": 100}}
			// { "age" : { "$in" : [ 25, 52 ] } }
			MongoResultSet ret = MongoGet.execDynamicQuery(	clusterUniqueName, 
															mongoDbName, 
															collectionName,
															command,
															false);
						
						
			
			if( persist.compareToIgnoreCase("Y") == 0 ) {
				
				String folder = "./snapshots/" + String.valueOf(u.getId());  
				try {
					String fileName = StringUtils.generateUniqueString32();
					if(sqlName.isBlank()) {
						sqlName = fileName;
					}
					
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, ret.toString());
					if(isOk) {
						long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
						Map<String, String> map = new HashMap<>();
						map.put("type", "runAdhocMql");
						map.put("clusterUniqueName", clusterUniqueName);
						map.put("mongoDbName", mongoDbName);
						map.put("collectionName", collectionName);
						map.put("command", command);
						if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
							map.put("comment", comment);
						}
						JSONObject jo = new JSONObject(map);
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "ResultQuery", u.getId(), timestamp, jo.toString());
						SnapshotMongoDbRepo snapshotDbRepo = new SnapshotMongoDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
				}
			}
			
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			HistFileManagement.addNewStatement(userId, command, comment, timeStamp, mainFolder, "adhoc", "mongo");
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/execute/adhoc/multiple:aggregate", method = RequestMethod.PUT, consumes = "text/plain")
	@Operation(summary = "Execute Mql on multiple clusters / collections and aggregate results with Sql")
	public ResponseEntity<RestObject> 
	executeAdhocMultipleCollection(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="user") String user,
									@RequestHeader(value="session") String session,
									@RequestBody String strObj)  {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		ListMongoCompoundQuery listMongoCompoundQuery = ListMongoCompoundQuery.toListMongoCompoundQuery(strObj);
		try	{
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
            assert listMongoCompoundQuery != null;
            List<RdbmsTableSetup> lst = MongoParallelQuery.executeMongoQueryInParallel( listMongoCompoundQuery.getLst(),  listMongoCompoundQuery.getTableName());
			H2InMem h2InMem = new H2InMem("", inMemDbName, "QUERY", session, requestId, user);
			DataTransfer dataTransfer = h2InMem.loadRdbmsQueriesInMem(lst);
			ResultQuery ret  = SqlQueryExecUtils.execStaticQuery(h2InMem.getConnection(), listMongoCompoundQuery.getSqlAggregator());
			h2InMem.removeInMemDb();
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/*                    Mongodb SQL Snapshots                                     */

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/snapshot:history", method = RequestMethod.GET)
	@Operation(summary = "Get a list of snapshots to visualize")
	public ResponseEntity<RestObject> 
	getMongoSnapshotHistory(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="ownerId") String ownerId,
							@RequestHeader(value="startIime") String startIime,
							@RequestHeader(value="endTime") String endTime,
							@RequestHeader(value="sqlStatement", required = false) String sqlStatement) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SnapshotMongoDbRepo snp = new SnapshotMongoDbRepo();
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
	@RequestMapping(value = "/mongo-repo/snapshot:get", method = RequestMethod.GET)
	@Operation(summary = "Get snapshot to visualize")
	public ResponseEntity<RestObject> 
	getSnapshot(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="snapshotId") String snapshotId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			SnapshotMongoDbRepo snp = new SnapshotMongoDbRepo();
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
	@RequestMapping(value = "/mongo-repo/snapshot:delete", method = RequestMethod.POST)
	@Operation(summary = "Delete snapshot")
	public ResponseEntity<RestObject> 
	deleteMangoSnapshot(@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="snapshotId") String snapshotId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			SnapshotMongoDbRepo snp = new SnapshotMongoDbRepo();
			SnapshotDbRecord snapshotDbRecord = snp.getSnapshot(Long.parseLong(snapshotId));
			snp.deleteSnapshotDbAccessForSnapshot(Long.parseLong(snapshotId));
			snp.deleteSnapshotDb(Long.parseLong(snapshotId));
			String filePath = "./snapshots/" + String.valueOf(snapshotDbRecord.getUserId()) + "/" + snapshotDbRecord.getFileName();
			FileUtilWrapper.deleteFile(filePath);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
/*  Associations*/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:get", method = RequestMethod.GET)
	@Operation(summary = "Get associations")
	public ResponseEntity<RestObject> 
	getMongoRepoAssociationTable(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="associationName") String associationName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			RepoAssociationTableList repoAssociationTableList = mongoClusterDb.getRepoAssociationTable( associationName );
			return RestObject.retOKWithPayload(repoAssociationTableList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:add", method = RequestMethod.PUT)
	@Operation(summary = "Add association")
	public ResponseEntity<RestObject> 
	addMongoRepoAssociationTable(	@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="associationName") String associationName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			mongoClusterDb.insertRepoAssociationTable(associationName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:update", method = RequestMethod.PUT)
    @Operation(summary = "Update association")
	public ResponseEntity<RestObject> 
	updateRepoAssociationTable(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="associationId") String associationId,
								@RequestHeader(value="associationName") String associationName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			mongoClusterDb.updateRepoAssociationTable(Integer.parseInt(associationId) , associationName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:delete", method = RequestMethod.PUT)
	@Operation(summary = "Delete association")
	public ResponseEntity<RestObject> 
	deleteMongoRepoAssociationTable(@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="associationId") String associationId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			mongoClusterDb.deleteRepoAssociationTable(Integer.parseInt(associationId));
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	/*Uploading / downloading large attachment*/
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:add", 
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
					method = RequestMethod.POST)
	@Operation(summary = "Add large binary file to mongo database")
	public ResponseEntity<RestObject> 
	addLargeAttachment(	@RequestHeader(value="user") String user,
						@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterName") String clusterName,
						@RequestHeader(value="databaseName") String dbName,
						@RequestHeader(value="bucketName") String bucketName,
						@RequestHeader(value="fileName") String fileName,
						@RequestHeader(value="metadata", required = false) String metadata,
						@RequestParam("attachment") MultipartFile attachment) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			String fileId = MongoDbTransaction.addLargeAttachmentToBucket(	mongoClusterDb,
																	clusterName,
																	dbName,
																	bucketName,
																	fileName,
																	metadata,
																	attachment,
																	authUtil.getUser(user).getId());
			return RestObject.retOK(fileId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:get", method = RequestMethod.GET)
	@Operation(summary = "Get large binary file from mongo database")
	public ResponseEntity<RestObject> 
	getLargeAttachment(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterName") String clusterName,
						@RequestHeader(value="databaseName") String dbName,
						@RequestHeader(value="bucketName") String bucketName,
						@RequestHeader(value="fileId") String fileId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			LargeMongoBinaryFile ret = MongoDbTransaction.getLargeAttachmentFromBucket(clusterName, dbName, bucketName, fileId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:download", method = RequestMethod.GET)
	@Operation(summary = "Get large binary file from mongo database")
	public ResponseEntity<Resource> 
	downloadLargeAttachment(@RequestHeader(value="clusterName") String clusterName,
							@RequestHeader(value="databaseName") String dbName,
							@RequestHeader(value="bucketName") String bucketName,
							@RequestHeader(value="fileId") String fileId) {
		try	{
			LargeMongoBinaryFile retLargeObject = MongoDbTransaction.getLargeAttachmentFromBucket(clusterName, dbName, bucketName, fileId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDispositionFormData("attachment", retLargeObject.getFilename());
			headers.clearContentHeaders();
			headers.add("Access-Control-Expose-Headers", "*");
			headers.add("fileName", retLargeObject.getFilename());
			headers.add("fileSize", String.valueOf(retLargeObject.getFileSize()));
			headers.add("metadata", String.valueOf(retLargeObject.getLargeObjectAssociatedMetadata().toString()));
			headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.set("Pragma", "no-cache");
			headers.set("Expires", "0");
			InputStreamResource content = new InputStreamResource(new ByteArrayInputStream(retLargeObject.getFile()));
			return ResponseEntity.ok()	.contentLength(retLargeObject.getFileSize())
										.contentType(MediaType.parseMediaType("application/octet-stream")).headers(headers)
										.body(content);
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		} catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete large binary file from mongo database")
	public ResponseEntity<RestObject> 
	deleteLargeAttachment(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterName") String clusterName,
							@RequestHeader(value="databaseName") String dbName,
							@RequestHeader(value="bucketName") String bucketName,
							@RequestHeader(value="fileId") String fileId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			boolean ret = MongoDbTransaction.deleteLargeAttachmentFromBucket(clusterName, dbName, bucketName, fileId);
			return RestObject.retOKWithPayload(new GenericResponse(Boolean.toString(ret)), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	/*History */
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/history/user:personal", method = RequestMethod.GET)
	@Operation(summary = "Get Execution History for Current User")
	public ResponseEntity<RestObject> 
	getMongoHistory(@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			MongoExecutedQueryList p = mongoHist.getMqlByUser(user);
			return RestObject.retOKWithPayload(p, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	/*History */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/history/stm:get", method = RequestMethod.GET)
    @Operation(summary = "Get the List of executed sql statements")
	public ResponseEntity<RestObject> 
	getMongoHistStm(@RequestHeader(value="user") String user,
					@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="type") String type,
					@RequestHeader(value="stext", required = false, defaultValue = "") String stext) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		List<HistoryStatement> lstSql = new ArrayList<HistoryStatement>();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			
			String mainFolder = appConstants.getHistStatementPath();
			List<String> lstSha = HistFileManagement.getStmts(userId, mainFolder, type, "mongo");
			for(String s: lstSha) {
				HistoryStatement t = HistFileManagement.getStm_(userId, s, mainFolder, type, "mongo");
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
	@RequestMapping(value = "/mongo-repo/history/stm:copy", method = RequestMethod.POST)
	@Operation(summary = "Copy sql statements to another user")
	public ResponseEntity<RestObject> 
	copyMongoHistStm(@RequestHeader(value="user") String user,
					 @RequestHeader(value="requestId") String requestId,
					 @RequestHeader(value="toUserId") String toUserId,
					 @RequestHeader(value="shaHash") String shaHash,
					 @RequestHeader(value="type") String type) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			HistFileManagement.addExistingStmToNewUser(userId, Long.parseLong(toUserId), shaHash, mainFolder, type, "mongo");
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/history/stm:remove", method = RequestMethod.DELETE)
    @Operation(summary = "Delete an executed sql statement from your profile")
	public ResponseEntity<RestObject> 
	deleteMongoHistStmt(@RequestHeader(value="user") String user,
						@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="shaHash") String shaHash,
						@RequestHeader(value="type") String type) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			boolean ret = HistFileManagement.deleteStatement(userId, shaHash, mainFolder, type, "mongo");
			return RestObject.retOKWithPayload(new GenericResponse(String.valueOf(ret)), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
}
