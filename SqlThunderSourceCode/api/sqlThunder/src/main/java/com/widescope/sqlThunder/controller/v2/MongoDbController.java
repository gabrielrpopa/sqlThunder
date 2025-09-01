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



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import com.widescope.logging.AppLogger;
import com.widescope.persistence.Persistence;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.mongodb.*;
import com.widescope.rdbmsRepo.database.mongodb.MongoRepoDynamicMqlList;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileSummaryList;
import com.widescope.rdbmsRepo.database.mongodb.repo.*;
import com.widescope.sqlThunder.rest.*;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.*;
import com.widescope.sqlThunder.utils.compression.ZipDirectory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
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
import org.apache.http.HttpHost;
import org.springframework.core.io.Resource;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
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
import com.widescope.rdbmsRepo.database.mongodb.associations.RepoAssociationTableList;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFile;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileMetaList;
import com.widescope.rdbmsRepo.database.mongodb.response.ObjectList;
import com.widescope.rdbmsRepo.database.mongodb.response.ObjectMongo;
import com.widescope.rdbmsRepo.database.mongodb.response.StringList;
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
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.service.StorageService;

@CrossOrigin
@RestController
@Schema(title = "MongoDb Repo")
public class MongoDbController {

	@Autowired
	private AuthUtil authUtil;

	/*Elasticsearch Statement Repository*/
	@Autowired
	private ElasticClusterDb elasticClusterDb;

	/*Mongo Statement Repository*/
	@Autowired
	private MongoClusterDb mongoClusterDb;

	/*Mongo Statement Execution History Database*/
	@Autowired
	private	MongoExecutedQueriesRepoDb execMongoDb;

	/* File System Storage, in this service used for temp files */
	@Autowired
	private StorageService storageService;

	@Autowired
	private PersistenceWrap pWrap;

	@PostConstruct
	public void initialize() {

	}






	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo:reload", method = RequestMethod.POST)
	@Operation(summary = "Reload Repo List")
	public ResponseEntity<RestObject>
	reloadMongoRepo(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo:list", method = RequestMethod.GET)
	@Operation(summary = "Get the Mongo Db Repository")
	public ResponseEntity<RestObject>
	getMongoRepo(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoClusterList mongoClusterList = new MongoClusterList(mongoClusterDb.getAllCluster());
			mongoClusterList.blockPassword();
			return RestObject.retOKWithPayload(mongoClusterList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}






	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new Mongo database/cluster connection to the list of available databases/cluster connections")
	public ResponseEntity<RestObject>
	addMongoRepo(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
					@RequestHeader(value="connString") final String connString,
					@RequestHeader(value="storageType", required = false) final String storageType,
					@RequestHeader(value="startPeriod", required = false) final long startPeriod,
					@RequestHeader(value="endPeriod", required = false) final long endPeriod,
					@RequestHeader(value="tunnelLocalPort", required = false) final String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false) final String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false) final String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false) final String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) final String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) final String tunnelRemoteHostRsaKey)	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
				return new ResponseEntity<> (transferableObject, HttpStatus.CONFLICT);
			}

			MongoClusterRecord mongoClusterRecord = new MongoClusterRecord(0,
																			clusterUniqueName,
																			connString,
																			storageType,
																			0,
																			startPeriod,
																			endPeriod,
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster:update", method = RequestMethod.POST)
	@Operation(summary = "Update a Mongo database/cluster connection")
	public ResponseEntity<RestObject>
	updateMongoRepo(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="clusterId") final int clusterId,
					@RequestHeader(value="uniqueName") final String uniqueName,
					@RequestHeader(value="connString") final String connString,
					@RequestHeader(value="storageType", required = false) final String storageType,
					@RequestHeader(value="startPeriod", required = false) final long startPeriod,
					@RequestHeader(value="endPeriod", required = false) final long endPeriod,
					@RequestHeader(value="tunnelLocalPort", required = false) final String tunnelLocalPort,
					@RequestHeader(value="tunnelRemoteHostAddress", required = false) final String tunnelRemoteHostAddress,
					@RequestHeader(value="tunnelRemoteHostPort", required = false) final String tunnelRemoteHostPort,
					@RequestHeader(value="tunnelRemoteHostUser", required = false) final String tunnelRemoteHostUser,
					@RequestHeader(value="tunnelRemoteHostUserPassword", required = false) final String tunnelRemoteHostUserPassword,
					@RequestHeader(value="tunnelRemoteHostRsaKey", required = false) final String tunnelRemoteHostRsaKey) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try {
			MongoClusterRecord oldMongoClusterRecord = mongoClusterDb.getCluster(clusterId);
			MongoClusterRecord mongoClusterRecord = new MongoClusterRecord(	clusterId,
																			uniqueName,
																			connString,
																			storageType,
																			0,
																			startPeriod,
																			endPeriod,
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

			MongoClusterRecord newMongoClusterRecord = mongoClusterDb.getCluster(clusterId);
			return RestObject.retOKWithPayload(newMongoClusterRecord, requestId, methodName);

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove a Mongo database/cluster connection")
	public ResponseEntity<RestObject>
	removeMongoRepo(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="clusterUniqueName") final String clusterUniqueName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
				throw new Exception("MongoDb cluster/server does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/databases:list", method = RequestMethod.GET)
	@Operation(summary = "Get the list of databases in a cluster/MongoDB Server")
	public ResponseEntity<RestObject>
	mongoDatabaseList(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName) {
		requestId = StringUtils.generateRequestId(requestId);
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error getting Mongo databases");
		}

		try	{
			MongoClusterDbList mongoClusterDbList = MongoDbTransaction.getMongoDatabaseList( clusterUniqueName);
			return RestObject.retOKWithPayload(mongoClusterDbList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collections:list", method = RequestMethod.GET)
	@Operation(summary = "Get the list of collections of a single database of a cluster/Mongo Server")
	public ResponseEntity<RestObject>
	mongoDatabaseCollectionList(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								@RequestHeader(value="databaseName") final String databaseName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error getting Mongo database collections");
		}

		try	{
			MongoClusterDbCollectionList ret = MongoDbTransaction.getMongoClusterDbCollectionList(clusterUniqueName, databaseName);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection:add", method = RequestMethod.PUT)
	@Operation(summary = "Add new collection to a Mongo Database")
	public ResponseEntity<RestObject>
	addMongoDatabaseCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								@RequestHeader(value="databaseName") final String databaseName,
								@RequestHeader(value="collectionName") final String collectionName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database collection");
		}

		try	{
			MongoDbTransaction.addCollection (clusterUniqueName, databaseName, collectionName);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket:add", method = RequestMethod.PUT)
	@Operation(summary = "Add new bucket to a Mongo Database")
	public ResponseEntity<RestObject>
	addMongoDatabaseBucket(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
							@RequestHeader(value="databaseName") final String databaseName,
							@RequestHeader(value="bucketName") final String bucketName) {


		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database bucket");
		}

		try	{
			MongoDbTransaction.addBucket(clusterUniqueName, databaseName, bucketName, mongoClusterDb);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete bucket from Mongo Database")
	public ResponseEntity<RestObject>
	deleteMongoDatabaseBucket(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								@RequestHeader(value="databaseName") final String databaseName,
								@RequestHeader(value="bucketName") final String bucketName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) )	{
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database bucket");
		}

		try	{
			MongoDbTransaction.deleteBucket(clusterUniqueName, databaseName, bucketName, mongoClusterDb);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collections:drop", method = RequestMethod.DELETE)
	@Operation(summary = "Drop collection from Mongo Database")
	public ResponseEntity<RestObject>
	deleteMongoDatabaseCollection(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
									@RequestHeader(value="databaseName") final String databaseName,
									@RequestHeader(value="collectionName") final String collectionName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if( !SqlRepoUtils.mongoDbMap.containsKey(clusterUniqueName) ) {
			return RestObject.retException(requestId, methodName, "Error creating new Mongo database collection");
		}

		try	{
			MongoDbTransaction.deleteCollectionFromDatabase(clusterUniqueName, databaseName, collectionName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/index:create", method = RequestMethod.PUT)
	@Operation(summary = "Create an index for collection")
	public ResponseEntity<RestObject>
	createMongoCollectionIndex(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								@RequestHeader(value="databaseName") final String databaseName,
								@RequestHeader(value="collectionName") final String collectionName,
								@RequestHeader(value="fieldName") final String fieldName )  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String indexName = MongoDbTransaction.addIndexToCollection(clusterUniqueName, databaseName,collectionName,fieldName);
			return RestObject.retOKWithPayload(new GenericResponse(indexName), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/index:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Create an index for collection")
	public ResponseEntity<RestObject>
	deleteMongoCollectionIndex(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								@RequestHeader(value="databaseName") final String databaseName,
								@RequestHeader(value="collectionName") final String collectionName,
								@RequestHeader(value="fieldName") final String fieldName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoDbTransaction.deleteIndexToCollection(clusterUniqueName, databaseName,collectionName,fieldName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	//////////////////////////Update Records ////////////////////////////////////////////////////////////////////////////

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/replace-update:single", method = RequestMethod.POST)
	@Operation(summary = "Replace/update object by id")
	public ResponseEntity<RestObject>
	replaceDocumentById(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterUniqueName") final String mongoClusterName,
							@RequestHeader(value="databaseName") final String mongoDatabaseName,
							@RequestHeader(value="collectionName") final String mongoCollectionName,
							@RequestHeader(value="idObject") final String idObject,
							@RequestBody final String object) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long countOperation = MongoDbTransaction.replaceDocumentById( mongoClusterName, mongoDatabaseName, mongoCollectionName, idObject, object);
			ObjectMongo o = new ObjectMongo(idObject, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}

	///////////////////////// DELETE records ////////////////////////////////////////////////////////////////////////////////////////

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/delete:single", method = RequestMethod.DELETE)
    @Operation(summary = "Delete object by id")
	public ResponseEntity<RestObject>
	deleteMongoDocumentById(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterUniqueName") final String mongoClusterName,
							@RequestHeader(value="databaseName") final String mongoDatabaseName,
							@RequestHeader(value="collectionName") final String mongoCollectionName,
							@RequestHeader(value="idObject") final String idObject) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long count = MongoDbTransaction.deleteDocFromCollection(mongoClusterName, mongoDatabaseName, mongoCollectionName, idObject);
			ObjectMongo o = new ObjectMongo(idObject, null, null, count);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/delete:multiple", method = RequestMethod.POST)
    @Operation(summary = "Delete Records from a list of ids")
	public ResponseEntity<RestObject>
	deleteMongoMultipleDocuments(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								  @RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								  @RequestHeader(value="databaseName") final String databaseName,
								  @RequestHeader(value="collectionName") final String collectionName,
								  @RequestBody final List<String> jsonDocument) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long countOperation = MongoDbTransaction.deleteMultipleDocuments(clusterUniqueName,databaseName, collectionName, jsonDocument);
			ObjectMongo o = new ObjectMongo(null, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/delete:command", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Records")
	public ResponseEntity<RestObject>
	deleteMongoRecords(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
					   @RequestHeader(value="databaseName") final String databaseName,
					   @RequestHeader(value="collectionName") final String collectionName,
					   @RequestHeader(value="command") final String command) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long countOperation = MongoDbTransaction.deleteRecords(clusterUniqueName, databaseName, collectionName, command);
			return RestObject.retOKWithPayload(new GenericResponse(countOperation), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/delete:condition", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Records")
	public ResponseEntity<RestObject>
	deleteMongoRecords(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="databaseName") final String databaseName,
						@RequestHeader(value="collectionName") final String collectionName,
						@RequestHeader(value="itemToSearch") final String itemToSearch,
						@RequestHeader(value="valueToSearch") final String valueToSearch,
						@RequestHeader(value="operator", defaultValue = "$in") final String operator,
						@RequestHeader(value="valueToSearchType", defaultValue = "STRING") final String valueToSearchType) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long countOperation = MongoDbTransaction.deleteManyRecords(clusterUniqueName, databaseName, collectionName, itemToSearch, valueToSearch, operator, valueToSearchType);
			ObjectMongo o = new ObjectMongo(null, null, null, countOperation);
			ObjectList objectList = new ObjectList(o);
			return RestObject.retOKWithPayload(objectList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/delete:range", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Records in a range")
	public ResponseEntity<RestObject>
	deleteMongoRecordsByRange(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
								@RequestHeader(value="databaseName") final String databaseName,
								@RequestHeader(value="collectionName") final String collectionName,
								@RequestHeader(value="itemToSearch") final String itemToSearch,
								@RequestHeader(value="from") final String from,
								@RequestHeader(value="to") final String to,
								@RequestHeader(value="valueToSearchType") final String valueToSearchType) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long countOperation = MongoDbTransaction.deleteManyRecordsRange(clusterUniqueName, databaseName, collectionName, itemToSearch, from, to, valueToSearchType);
			return RestObject.retOKWithPayload(new GenericResponse(countOperation), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}




	///////////////////////////Query////////////////////////////////////////////////////////////////////////////////////////////////

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/count", method = RequestMethod.PUT)
	@Operation(summary = "Get count of all documents in collection")
	public ResponseEntity<RestObject>
	getCollectionDocsCount(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterUniqueName") final String clusterName,
							@RequestHeader(value="databaseName") final String dbName,
							@RequestHeader(value="collectionName") final String cName,
							@RequestHeader(value="isEstimate") final String isEstimate) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			long count = MongoDbTransaction.getCollectionDocsCount(clusterName, dbName,	cName, isEstimate);
			return RestObject.retOKWithPayload(new GenericResponse(count), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/add:single", method = RequestMethod.PUT)
	@Operation(summary = "Add single document to collection")
	public ResponseEntity<RestObject>
	addDocumentToCollection(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterUniqueName") final String clusterName,
								@RequestHeader(value="databaseName") final String dbName,
								@RequestHeader(value="collectionName") final String cName,
								@RequestBody final String jsonDocument) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(MongoDbTransaction.addDocumentToCollection(clusterName, dbName, cName, jsonDocument))
				return RestObject.retOK(requestId, methodName);
			else
				return RestObject.retException(requestId, methodName, "Could not add Mongo document");

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}

	/** Copy from other storage systems */

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/embedded/adhoc:sql", method = RequestMethod.PUT)
    @Operation(summary = "Copy records to collection from Embedded adhoc query")
	public ResponseEntity<RestObject>
	copyEmbeddedQueryToCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								  @RequestHeader(value="fromEmbeddedType", defaultValue = "H2") final String fromEmbeddedType,
								  @RequestHeader(value="fromClusterId") final long fromClusterId,
								  @RequestHeader(value="fromEmbeddedDatabaseName") final String fromEmbeddedDatabaseName,
								  @RequestHeader(value="fromEmbeddedSchemaName") final String fromEmbeddedSchemaName,
								  @RequestHeader(value="toMongoClusterName") final String toMongoClusterName,
								  @RequestHeader(value="toMongoDbName") final String toMongoDbName,
								  @RequestHeader(value="toMongoCollectionName") final String toMongoCollectionName,
								  @RequestHeader(value="toBatchCount", defaultValue = "0") final int toBatchCount,
								  @RequestBody String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);

			if(!MongoGet.isCollection(mongoDbConnection, toMongoDbName, toMongoCollectionName)) {
				RestObject transferableObject = new RestObject(	new GenericResponse(ErrorCode.WARNING),
																requestId,
																"Collection does not exist :" + toMongoCollectionName,
																"Collection does not exist :" + toMongoCollectionName,
																ErrorCode.WARNING,
																Sources.SQLTHUNDER,
																ErrorSeverity.LOW,
																methodName);
				return new ResponseEntity<> (transferableObject, HttpStatus.OK);
			}


			H2Static h2Db = new H2Static(fromClusterId, fromEmbeddedDatabaseName );
			TableFormatMap recordSet = h2Db.execStaticQueryWithTableFormat(sqlContent);
			int retOperation = MongoPut.addDocumentsToCollection(	mongoDbConnection,
																	toMongoDbName,
																	toMongoCollectionName,
																	recordSet.getListOfRows(),
																	toBatchCount,
																	0);


			mongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse(retOperation), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/rdbms:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from RDBMS query")
	public ResponseEntity<RestObject>
	copyRDBMSQueryToCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							   @RequestHeader(value="fromRdbmsSchemaUniqueName") final String fromRdbmsSchemaUniqueName,
							   @RequestHeader(value="toMongoClusterName") final String toMongoClusterName,
							   @RequestHeader(value="toMongoDbName") final String toMongoDbName,
							   @RequestHeader(value="toMongoCollectionName") final String toMongoCollectionName,
							   @RequestHeader(value="batchCount", defaultValue = "0") final long batchCount,
							   @RequestBody final String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);

			if(!MongoGet.isCollection(mongoDbConnection, toMongoDbName, toMongoCollectionName)) {
				RestObject transferableObject = new RestObject(	new GenericResponse(ErrorCode.WARNING),
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
																	batchCount,
																	0);


			mongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse(retOperation), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from Elastic DSL query")
	public ResponseEntity<RestObject>
	copyElasticDslToCollection( @RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="fromElasticClusterName") final String fromElasticClusterName,
								@RequestHeader(value="fromElasticHttpVerb", defaultValue = "GET") final String fromElasticHttpVerb,
								@RequestHeader(value="fromElasticEndPoint") final String fromElasticEndPoint,
							    @RequestHeader(value="toMongoClusterName") final String toMongoClusterName,
								@RequestHeader(value="toMongoDatabaseName") final String toMongoDatabaseName,
								@RequestHeader(value="toMongoCollectionName") final String toMongoCollectionName,
								@RequestHeader(value="batchCount", defaultValue = "0") final long batchCount,
								@RequestBody final String httpPayload) {


		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				Object ret = ElasticInfo.executeGeneric(elasticLowLevelWrapper, fromElasticHttpVerb, fromElasticEndPoint, "Y", httpPayload);
				elasticLowLevelWrapper.disconnect();
				ElasticPayload payload = ElasticInfo.parseResponse(ret);
				/*Transfer Data from Elastic to MongoDb*/
				int countProcessed = MongoPut.pushElasticToCollection (	payload,
																		mongoDbConnection,
																		toMongoDatabaseName,
																		toMongoCollectionName,
																		batchCount );
				mongoDbConnection.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse(countProcessed), requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Elastic Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/elastic:sql", method = RequestMethod.PUT)
    @Operation(summary = "Create/add records to collection from Elastic SQL query")
	public ResponseEntity<RestObject>
	copyElasticSqlToCollection( @RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="fromElasticClusterName") final String fromElasticClusterName,
								@RequestHeader(value="toMongoClusterName") final String toMongoClusterName,
								@RequestHeader(value="toMongoDatabaseName") final String toMongoDatabaseName,
								@RequestHeader(value="toMongoCollectionName") final String toMongoCollectionName,
								@RequestHeader(value="batchCount") final int batchCount,
								@RequestBody final String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				if(!MongoGet.isCollection(mongoDbConnection, toMongoDatabaseName, toMongoCollectionName) ){
					return RestObject.retException(requestId, methodName, "Collection " + toMongoDatabaseName + " does not exist");
				}
				JSONObject ret = SearchSql.searchSql(elasticLowLevelWrapper, sqlContent);
				List<String> elasticFromResultSet = Objects.requireNonNull(SqlResponse.toSqlResponse(ret.toJSONString())).toListOfJsonStrings();
				elasticLowLevelWrapper.disconnect();
				/*Transfer Data*/
				long countProcessed
				= MongoPut.addDocumentsToCollection(mongoDbConnection,
													toMongoDatabaseName,
													toMongoCollectionName,
													elasticFromResultSet,
													batchCount,
													0);

				mongoDbConnection.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse( countProcessed ), requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, "Elastic Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb/search:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from another Mongodb collection(s) simple search")
	public ResponseEntity<RestObject>
	copySimpleSearchToCollection(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="fromClusterUniqueName") final String fromClusterUniqueName,
									@RequestHeader(value="fromMongoDbName") final String fromMongoDbName,
									@RequestHeader(value="fromCollectionName") final String fromCollectionName,
									@RequestHeader(value="itemToSearch") final String itemToSearch,
									@RequestHeader(value="valueToSearch") final String valueToSearch,
									@RequestHeader(value="valueToSearchType") final String valueToSearchType,
									@RequestHeader(value="operator", defaultValue = "$eq") final String operator,
									@RequestHeader(value="toClusterUniqueName") final String toClusterUniqueName,
									@RequestHeader(value="toMongoDbName") final String toMongoDbName,
									@RequestHeader(value="toCollectionName") final String toCollectionName,
									@RequestHeader(value="batchCount") final int batchCount) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
																	batchCount,
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse( countProcessed ), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb/search:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from another Mongodb collection(s)")
	public ResponseEntity<RestObject>
	copyRangeSearchToCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="fromClusterUniqueName") final String fromClusterUniqueName,
								@RequestHeader(value="fromMongoDbName") final String fromMongoDbName,
								@RequestHeader(value="fromCollectionName") final String fromCollectionName,
								@RequestHeader(value="itemToSearch") final String itemToSearch,
								@RequestHeader(value="fromValue") final String fromValue,
								@RequestHeader(value="toValue") final String toValue,
								@RequestHeader(value="valueSearchType") final String valueSearchType,
								@RequestHeader(value="toMongoClusterName") final String toClusterUniqueName,
								@RequestHeader(value="toMongoDatabaseName") final String toMongoDbName,
								@RequestHeader(value="toMongoCollectionName") final String toCollectionName,
								@RequestHeader(value="batchCount") final int batchCount) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
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
																	batchCount,
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse(countProcessed), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy full collection")
	public ResponseEntity<RestObject>
	copyFullCollectionToCollection(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
									@RequestHeader(value="toMongoClusterName") final String toClusterUniqueName,
									@RequestHeader(value="toMongoDatabaseName") final String toMongoDbName,
									@RequestHeader(value="toMongoCollectionName") final String toCollectionName,
									@RequestHeader(value="batchCount") final int batchCount) {
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
																	batchCount,
																	0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse(countProcessed), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/mongodb:adhoc", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to collection from full Mongodb collection")
	public ResponseEntity<RestObject>
	copyMongoAdhocMqlToCollection(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
									@RequestHeader(value="toMongoClusterName") final String toClusterUniqueName,
									@RequestHeader(value="toMongoDatabaseName") final String toMongoDbName,
									@RequestHeader(value="toMongoCollectionName") String toCollectionName,
									@RequestHeader(value="batchCount") final int batchCount,
									@RequestBody String bsonQuery) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			MongoClusterRecord toMongoClusterRecord ;
			MongoDbConnection toMongoDbConnection;

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(),
																			fromMongoClusterRecord.getClusterId(),
																			fromMongoClusterRecord.getUniqueName());

			MongoResultSet ret = MongoGet.rawQueryCollection(fromMongoDbConnection, fromMongoDatabaseName, fromMongoCollectionName, bsonQuery);
			if(fromMongoClusterName.compareToIgnoreCase(toClusterUniqueName) != 0) {
				fromMongoDbConnection.disconnect();
				toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
				toMongoDbConnection = new MongoDbConnection(toMongoClusterRecord.getConnString(), toMongoClusterRecord.getClusterId(), toMongoClusterRecord.getUniqueName());
			} else {
				toMongoDbConnection = fromMongoDbConnection;
			}

			int countProcessed = MongoPut.addDocumentsToCollection(	toMongoDbConnection,
																	toMongoDbName,
																	toCollectionName,
																	MongoResultSet.getResultSetAsJson(ret.getResultSet()),
																	batchCount,
																	0);

			if(fromMongoClusterName.compareToIgnoreCase(toClusterUniqueName) == 0) {
				fromMongoDbConnection.disconnect();
			}

			toMongoDbConnection.disconnect();

			return RestObject.retOKWithPayload(new GenericResponse(countProcessed), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	/**MultipartFile file is CSV format*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/copy/csv:load",
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE },
					method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv file to collection. First row is always the header")
	public ResponseEntity<RestObject>
	copyCsvToCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="fileType", defaultValue = "text/csv") final String fileType,  /*CSV in clear or compressed */
						@RequestHeader(value="toMongoClusterName") final String toClusterUniqueName,
						@RequestHeader(value="toMongoDatabaseName") final String toMongoDbName,
						@RequestHeader(value="toMongoCollectionName") final String toCollectionName,
						@RequestHeader(value="batchCount") final int batchCount,
						@RequestParam("attachment") MultipartFile attachment) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String fileName = StringUtils.generateUniqueString32();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String fullFilePath = storageService.storeTmp(attachment, fileName);
			String csvContent = CsvWrapper.readFile(fullFilePath);
			String isCompressed;
			if( fileType.equals(MimeTypes.MIME_TEXT_CSV) ) {
				isCompressed = "N";
			} else if( fileType.equals(MimeTypes.MIME_APPLICATION_X_ZIP)) {
				isCompressed = "Y";
			} else {
				return RestObject.retException(requestId, methodName, "Unknown File Type");
			}
			List<String> jList = CsvWrapper.stringToJsonList(csvContent,isCompressed);
			MongoClusterRecord toMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(toClusterUniqueName);
			MongoDbConnection toMongoDbConnection = new MongoDbConnection(	toMongoClusterRecord.getConnString(),
																			toMongoClusterRecord.getClusterId(),
																			toMongoClusterRecord.getUniqueName());

			int countProcessed
					= MongoPut.addDocumentsToCollection(toMongoDbConnection, toMongoDbName, toCollectionName, jList, batchCount, 0);
			toMongoDbConnection.disconnect();
			return RestObject.retOKWithPayload(new GenericResponse(countProcessed), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			storageService.deleteTmp(fileName);
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/add:many",
			method = RequestMethod.PUT,
			consumes = "application/json; charset=utf-8")
	@Operation(summary = "Add multiple records to a collection")
	public ResponseEntity<RestObject>
	addManyDocumentsToCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								 @RequestHeader(value="toMongoClusterName") final String clusterName,
								 @RequestHeader(value="toMongoDatabaseName") final String dbName,
								 @RequestHeader(value="toMongoCollectionName") final String cName,
								 @RequestBody final List<String> lstObjects ) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
		MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
		requestId = StringUtils.generateRequestId(requestId);
		try {
			MongoPut.bulkInsert_(mongoDbConnection, dbName, cName, lstObjects);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			mongoDbConnection.disconnect();
		}

	}




	/**MultipartFile file is either CSV format or ZIP file with one or more CSV files with first row always the header */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/add:batch", 
					method = RequestMethod.POST,
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	@Operation(summary = "Add multiple records to a collection delivered in a zip file containing one or more CSVs, first row is always the header")
	public ResponseEntity<RestObject> 
	addBatchDocumentToCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								 @RequestHeader(value="toMongoClusterName") final String clusterName,
								 @RequestHeader(value="toMongoDatabaseName") final String dbName,
								 @RequestHeader(value="toMongoCollectionName") final String cName,
								 @RequestHeader(value="origFileName") final String origFileName,
								 @RequestHeader(value="batchCount", defaultValue = "0") final int bCount,
								 @RequestParam("attachment") final MultipartFile attachment ) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
		MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
		requestId = StringUtils.generateRequestId(requestId);
		String newFolder = StringUtils.generateUniqueString16();
		try	{
			File attachmentFile = MongoWrapperFunction.copyFileToTempFolder(attachment, origFileName);
			Path path = new File(attachmentFile.getPath()).toPath();
			String mimeType = Files.probeContentType(path);
			int count = 0;
            switch (mimeType) {
                case MimeTypes.MIME_TEXT_CSV, MimeTypes.MIME_TEXT_PLAIN -> count = MongoWrapperFunction.addCsvRecords(attachmentFile.getPath(), mongoDbConnection, dbName, cName, bCount);
				case MimeTypes.MIME_APPLICATION_ZIP -> {
                    boolean isZip = true;
                    try { (new ZipFile(attachmentFile)).close(); } catch (ZipException e) { isZip = false; }
					if(isZip) {
						final String folderPath = attachmentFile.getParent() + "/" + newFolder;
						ZipDirectory.unzip(attachmentFile.getPath(), folderPath);
						List<FileCharacteristic> lstFiles=FileUtilWrapper.getListOfFilesFromFolder(folderPath);
						for(FileCharacteristic f: lstFiles) {
							String mType = Files.probeContentType(new File(f.getAbsolutePath()).toPath());
							if(mType.compareToIgnoreCase(MimeTypes.MIME_TEXT_CSV) == 0 || mType.compareToIgnoreCase(MimeTypes.MIME_TEXT_PLAIN) == 0) {
								count += MongoWrapperFunction.addCsvRecords(f.getAbsolutePath(), mongoDbConnection, dbName, cName, bCount);
							}
						}
						FileUtilWrapper.deleteDirectoryWithAllContent(folderPath);
					}
                }
                default -> throw new Exception("Unrecognized file mime type");
            }
			FileUtilWrapper.deleteFile(attachmentFile.getAbsoluteFile().toString());
			return RestObject.retOKWithPayload(new GenericResponse(count), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			mongoDbConnection.disconnect();
		}

	}

	
	
	
	
	//////////////////////////////////////////////  Search Documents //////////////////////////////////////////////////////
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document:get", method = RequestMethod.GET)
	@Operation(summary = "Get previously saved document/result-set")
	public ResponseEntity<RestObject> 
	getDocument(@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterName") final String clusterName,
				@RequestHeader(value="dbName") final String dbName,
				@RequestHeader(value="cName") final String cName,
				@RequestHeader(value="docId") final String docId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
			MongoResultSet mongoResultSet = MongoGet.getDocumentById_(mongoDbConnection,dbName, cName, docId);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document:firstN", method = RequestMethod.GET)
	@Operation(summary = "Get first N documents in the collection")
	public ResponseEntity<RestObject> 
	getFirstNDocuments( @RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterName") final String clusterName,
						@RequestHeader(value="databaseName") final String databaseName,
						@RequestHeader(value="collectionName") final String collectionName,
						@RequestHeader(value="limit") final String limit) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(databaseName.isEmpty() || collectionName.isEmpty()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Collection or Database is empty")) ;
			}
			StringList lst = MongoDbTransaction.getFirstNDocuments(clusterName, databaseName, collectionName, limit);
			return RestObject.retOKWithPayload(lst, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket/document:firstN", method = RequestMethod.GET)
	@Operation(summary = "Get first N documents in the bucket")
	public ResponseEntity<RestObject>
	getFirstNBucketDocuments(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="clusterName") final String clusterName,
								@RequestHeader(value="databaseName") final String databaseName,
								@RequestHeader(value="bucketName") final String bucketName,
								@RequestHeader(value="limit") final String limit) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(databaseName.isEmpty() || bucketName.isEmpty()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Bucket or Database is empty")) ;
			}
			LargeMongoBinaryFileMetaList ret = MongoDbTransaction.getFirstNBucketDocuments(clusterName, databaseName, bucketName, limit);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket/document:all", method = RequestMethod.GET)
	@Operation(summary = "Get all document metadata in the bucket")
	public ResponseEntity<RestObject>
	getAllBucketDocumentMetadata(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								 @RequestHeader(value="clusterName") final String clusterName,
								 @RequestHeader(value="databaseName") final String databaseName,
								 @RequestHeader(value="bucketName") final String bucketName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(databaseName.isEmpty() || bucketName.isEmpty()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Bucket or Database is empty")) ;
			}
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
																		mongoClusterRecord.getClusterId(),
																		mongoClusterRecord.getUniqueName());
			LargeMongoBinaryFileSummaryList ret = new LargeMongoBinaryFileSummaryList(MongoBucket.getAllBucketDocsMetadata(mongoDbConnection, databaseName, bucketName));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/bucket/document:filter", method = RequestMethod.GET)
	@Operation(summary = "Get filtered  document metadata in the bucket")
	public ResponseEntity<RestObject>
	getFilteredBucketDocumentMetadata(@RequestHeader(value="requestId", defaultValue = "") String requestId,
									  @RequestHeader(value="clusterName") final String clusterName,
									  @RequestHeader(value="databaseName") final String databaseName,
									  @RequestHeader(value="bucketName") final String bucketName,
									  @RequestHeader(value="filter") final String filter) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(databaseName.isEmpty() || bucketName.isEmpty()) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Bucket or Database is empty")) ;
			}
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
																		mongoClusterRecord.getClusterId(),
																		mongoClusterRecord.getUniqueName());
			LargeMongoBinaryFileSummaryList ret = new LargeMongoBinaryFileSummaryList(MongoBucket.getFilteredBucketDocsMetadata(mongoDbConnection, databaseName, bucketName, filter));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:simple", method = RequestMethod.GET)
	@Operation(summary = "Simple Search, providing item to search, value to search and value type")
	public ResponseEntity<RestObject> 
	searchDocumentSimple(	@RequestHeader(value="user") String user,
							@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterName") final String clusterName,
							@RequestHeader(value="databaseName") final String databaseName,
							@RequestHeader(value="collectionName") final String collectionName,
							@RequestHeader(value="itemToSearch") final String itemToSearch,
							@RequestHeader(value="valueToSearch") final String valueToSearch,
							@RequestHeader(value="valueToSearchType") final String valueToSearchType,
							@RequestHeader(value="operator", defaultValue = "$eq") final String operator,
							@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
							@RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
							@RequestHeader(value="sqlName", defaultValue = "") String sqlName,
							 @RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId
							) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoResultSet mongoResultSet = MongoDbTransaction.searchDocument (clusterName, databaseName, collectionName, itemToSearch, valueToSearch, operator,valueToSearchType);
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			SimpleMongoSearch command = new SimpleMongoSearch(itemToSearch, valueToSearch, operator, valueToSearchType);
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMQL, command.toString(), "Y", "", mongoClusterRecord.getClusterId(), databaseName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:range", method = RequestMethod.GET)
	@Operation(summary = "Search for a range of Documents")
	public ResponseEntity<RestObject> 
	searchDocumentRange(	@RequestHeader(value="user") final String user,
							@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterName") final String clusterName,
							@RequestHeader(value="databaseName") final String databaseName,
							@RequestHeader(value="collectionName") final String collectionName,
							@RequestHeader(value="itemToSearch") final String itemToSearch,
							@RequestHeader(value="fromValue") final String fromValue,
							@RequestHeader(value="toValue") final String toValue,
							@RequestHeader(value="valueSearchType") final String valueSearchType,
							@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
							@RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
							@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
							@RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			RangeMongoSearch command = new RangeMongoSearch(itemToSearch,	fromValue, toValue, valueSearchType);
			MongoResultSet mongoResultSet = MongoDbTransaction.searchDocumentRange(clusterName,	databaseName, collectionName, itemToSearch,	fromValue, toValue, valueSearchType);
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMQL, command.toString(), "Y", "", mongoClusterRecord.getClusterId(), databaseName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:complex-and", method = RequestMethod.POST)
	@Operation(summary = "Search for a range by Complex And Statements")
	public ResponseEntity<RestObject> 
	searchDocumentComplexAnd(@RequestHeader(value="user") final String user,
							 @RequestHeader(value="requestId", defaultValue = "") String requestId,
							 @RequestHeader(value="clusterName") final String clusterName,
							 @RequestHeader(value="databaseName") final String databaseName,
							 @RequestHeader(value="collectionName") final String collectionName,
							 @RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
							 @RequestHeader(value="comment", required = false) final String comment,
							 @RequestHeader(value="sqlName") String sqlName,
							 @RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId,
							 @RequestBody final ComplexAndSearch command) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
			MongoResultSet mongoResultSet = MongoGet.searchDocumentComplexAnd(	mongoDbConnection, databaseName, collectionName, command ) ;
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMQL, command.toString(), "Y", "", mongoClusterRecord.getClusterId(), databaseName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document:move", method = RequestMethod.POST)
	@Operation(summary = "Move document/result set from one collection to another across clusters and databases")
	public ResponseEntity<RestObject> 
	moveMongoDocument(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					 @RequestHeader(value="docId") final String docId,
					 @RequestHeader(value="clusterNameSource") final String clusterNameSource,
					 @RequestHeader(value="dbNameSource") final String dbNameSource,
					 @RequestHeader(value="cNameSource") final String cNameSource,
					 @RequestHeader(value="clusterNameDest") final String clusterNameDest,
					 @RequestHeader(value="dbNameDest") final String dbNameDest,
					 @RequestHeader(value="cNameDest") final String cNameDest) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Document docToMove = MongoDbTransaction.getDocument(clusterNameSource, dbNameSource, cNameSource, docId);
			if(!MongoDbTransaction.addDocumentToCollection(clusterNameDest, dbNameDest, cNameDest, docToMove.toJson())){
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Cannot move document " + docId));
			}
			MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(clusterNameSource);
			MongoPut.deleteDocumentById(new MongoDbConnection(mongoClusterRecordSource), dbNameSource, cNameSource, docId);
			RestObject transferableObject = new RestObject(new GenericResponse(ErrorCode.OK), requestId, methodName);
			return new ResponseEntity<> (transferableObject, HttpStatus.OK);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/rdbms/table:create", method = RequestMethod.PUT)
	@Operation(summary = "Create RDBMS table from ResultQuery document")
	public ResponseEntity<RestObject> 
	createRdbmsTableFromDocument(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="mongoDocId") final String mongoDocId,
									@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
									@RequestHeader(value="rdbmsConnectionName") final String rdbmsConnectionName,
									@RequestHeader(value="rdbmsSchema", required = false) final String rdbmsSchema,
									@RequestHeader(value="rdbmsTable", required = false) final String rdbmsTable) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection mongoDbConnectionSource = new MongoDbConnection(mongoClusterRecordSource);
			String ret = MongoGet.getDocumentById(mongoDbConnectionSource, fromMongoDatabaseName, fromMongoCollectionName, mongoDocId);
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(rdbmsConnectionName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			ResultQuery resultQuery = ResultQuery.toResultQuery(ret);
			DdlDmlUtils.createTable(resultQuery, connectionDetailInfo.getDbType(), rdbmsConnectionName, rdbmsSchema, rdbmsTable);
            assert resultQuery != null;
            int countInserted = DdlDmlUtils.insertTable(resultQuery, rdbmsConnectionName, rdbmsSchema, rdbmsTable);
			return RestObject.retOKWithPayload(new GenericResponse(countInserted), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/rdbms/table:compound", method = RequestMethod.PUT)
	@Operation(summary = "Compound Document")
	public ResponseEntity<RestObject> 
	compoundMongoDocuments(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="rdbmsConnectionName") final String rdbmsConnectionName,
							@RequestHeader(value="rdbmsSchema", required = false) final String rdbmsSchema,
							@RequestHeader(value="rdbmsTable", required = false) final String rdbmsTable,
							@RequestBody List<MongoObjectRef> listOfMongoObjects) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			
			MongoToH2Sql mongoToH2Sql = new MongoToH2Sql();
			int totalCount = mongoToH2Sql.addMongoObjects(	listOfMongoObjects, rdbmsConnectionName, rdbmsSchema, rdbmsTable);
			return RestObject.retOKWithPayload(new GenericResponse(totalCount), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}





	/*Executions*/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:repo", method = RequestMethod.POST)
	@Operation(summary = "Execute Repo Mql")
	public ResponseEntity<RestObject> 
	runRepoMql(@RequestHeader(value="user") String user,
			   @RequestHeader(value="requestId", defaultValue = "") String requestId,
			   @RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
			   @RequestHeader(value="mongoDbName") final String mongoDbName,
			   @RequestHeader(value="collectionName") final String collectionName,
			   @RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
			   @RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
			   @RequestHeader(value="statement") String statement,
			   @RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId,
			   @RequestBody final String parameters) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoRepoDynamicMqlExecution mongoRepoDynamicMqlExecution = MongoRepoDynamicMqlExecution.toMongoRepoDynamicMqlExecution(parameters);
			MongoRepoDynamicMql mongoRepoDynamicMql = MongoRepoDynamicMqlExecution.toMongoRepoDynamicMql(mongoRepoDynamicMqlExecution);
            assert mongoRepoDynamicMql != null;
            String mqlString = MongoRepoDynamicMqlExecution.toRawMql(mongoRepoDynamicMql);
			Document d = MongoGeneric.execBson(clusterUniqueName, mongoDbName, mqlString);
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, mongoRepoDynamicMql.getMqlId(), "", Constants.langMQL, mqlString, "Y", parameters, mongoClusterRecord.getClusterId(), mongoDbName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, d.toJson(), persist);
			return RestObject.retOKWithPayload(new JsonResponse(d.toJson()), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}




	/*Query as Json String*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/document/search:raw", method = RequestMethod.GET)
	@Operation(summary = "Search collection with a raw mongodb query")
	public ResponseEntity<RestObject>
	runRawQuery(@RequestHeader(value="user") String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="databaseName") final String databaseName,
				@RequestHeader(value="collectionName") final String collectionName,
				@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				@RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
				@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
				@RequestHeader(value="query") final String query,
				@RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
			MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
			MongoResultSet mongoResultSet = MongoGet.rawQueryCollection(mongoDbConnection, databaseName, collectionName, query);
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMQL, query, "Y", "", mongoClusterRecord.getClusterId(), databaseName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:bson", method = RequestMethod.POST)
	@Operation(summary = "Execute a generic Mql command providing it in Bson/Json format")
	public ResponseEntity<RestObject>
	runAdhocBson(	@RequestHeader(value="user") final String user,
					 @RequestHeader(value="requestId", defaultValue = "") String requestId,
					 @RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
					 @RequestHeader(value="mongoDbName") final String mongoDbName,
					 @RequestHeader(value="collectionName") final String collectionName,
					 @RequestHeader(value="group", required = false, defaultValue = "~") final String group,
					 @RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
					 @RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
					 @RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
					 @RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId,
					 @RequestBody final String query) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoResultSet mongoResultSet = MongoGet.execDynamicQuery(	clusterUniqueName, mongoDbName, collectionName, query, false);
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMQL, query, "Y", "", mongoClusterRecord.getClusterId(), mongoDbName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/collection/query:adhoc", method = RequestMethod.POST)
	@Operation(summary = "Execute a generic Mql command")
	public ResponseEntity<RestObject>
	runAdhocMql(@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="mongoDbName") final String mongoDbName,
				@RequestHeader(value="collectionName") final String collectionName,
				@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				@RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
				@RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
				@RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId,
				@RequestBody final String query) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			MongoResultSet mongoResultSet = MongoGet.execDynamicQuery(	clusterUniqueName, mongoDbName, collectionName, query, false);
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMQL, query, "Y", "", -1, mongoDbName, collectionName, groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/execute/adhoc/multiple:aggregate", method = RequestMethod.PUT)
	@Operation(summary = "Execute Mql on multiple clusters / collections and aggregate results with Sql")
	public ResponseEntity<RestObject>
	executeAdhocMultipleCollection(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								   @RequestHeader(value="user") String user,
								   @RequestHeader(value="session") final String session,
								   @RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
								   @RequestHeader(value="comment", required = false, defaultValue = "") final String comment,
								   @RequestHeader(value="sqlName", required = false, defaultValue = "") String sqlName,
								   @RequestHeader(value="groupId", required = false, defaultValue = "-1") long groupId,
								   @RequestBody final ListMongoCompoundQuery listMongoCompoundQuery)  {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
			assert listMongoCompoundQuery != null;
			List<RdbmsTableSetup> lst = MongoParallelQuery.executeMongoQueryInParallel( listMongoCompoundQuery.getLst(),  listMongoCompoundQuery.getTableName());
			H2InMem h2InMem = new H2InMem("", inMemDbName, H2InMem.query, session, requestId, user);
			DataTransfer dataTransfer = h2InMem.loadRdbmsQueriesInMem(lst, false);
			if(!dataTransfer.getIsSuccess()) {
				throw new Exception("unsuccessful operation");
			}
			ResultQuery mongoResultSet  = SqlQueryExecUtils.execStaticQuery(h2InMem.getConnection(), listMongoCompoundQuery.getSqlAggregator());
			h2InMem.removeInMemDb();
			MongoExecutedQuery rec = new MongoExecutedQuery(-1, requestId, -1, sqlName, Constants.langMixed, listMongoCompoundQuery.toString(), "Y", "", -1, "", "", groupId, Constants.adhocShort, u.getId(), "", comment, timeStamp, -1);
			pWrap.saveExecution(rec, mongoResultSet.toString(), persist);
			return RestObject.retOKWithPayload(mongoResultSet, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	/** MQL Repo Management  */


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:last", method = RequestMethod.GET)
	@Operation(summary = "Get the last user statement")
	public ResponseEntity<RestObject>
	getLastMongoStmt(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="user", defaultValue = "") String user,
				 		@RequestHeader(value="groupId", defaultValue = "") long groupId,
				 		@RequestHeader(value="source", defaultValue = "") String src /*A-ADHOC or R-REPO*/) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			MongoExecutedQuery lastMql = execMongoDb.getLastUserStatement(u.getId(), groupId, src);
			return RestObject.retOKWithPayload(lastMql, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:search", method = RequestMethod.GET)
	@Operation(summary = "Get the Mql statement by searching a keyword")
	public ResponseEntity<RestObject>
	searchMongoQuery(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="stringToSearch") String stringToSearch) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<MongoRepoDynamicMql> lstMql = mongoClusterDb.getMql(stringToSearch);
			MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList(lstMql);
			return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new MQL statement or update an existing one")
	public ResponseEntity<RestObject> 
	addMongoQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestBody final MongoRepoDynamicMql mongoRepoDynamicMql) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(mongoRepoDynamicMql != null) {
				mongoClusterDb.addMql(mongoRepoDynamicMql);
				
				List<MongoRepoDynamicMql> lstMql = mongoClusterDb.getMqlByName(mongoRepoDynamicMql.getMqlName());
				MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList(lstMql);
				return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
				
			} else {
				RestObject transferableObject = new RestObject(	new GenericResponse(ErrorCode.ERROR), requestId, methodName);
				return new ResponseEntity<> (transferableObject, HttpStatus.OK);
			}

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:get", method = RequestMethod.GET)
	@Operation(summary = "Get the Mql statement by id")
	public ResponseEntity<RestObject> 
	getMongoQueryById(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="id") final int id) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<MongoRepoDynamicMql> lstDslId = mongoClusterDb.getMql(id);
			mongoRepoDynamicMqlList.setMongoRepoDynamicMqlLst(lstDslId);
			return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Mql statement")
	public ResponseEntity<RestObject> 
	deleteMongoQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="mqlId") final int mqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.deleteMql(mqlId);
			mongoClusterDb.deleteMqlParams(mqlId);
			mongoClusterDb.deleteMqlToClusterBridge(mqlId);
			List<MongoRepoDynamicMql> lstMql = mongoClusterDb.getMql(mqlId);
			MongoRepoDynamicMqlList mongoRepoDynamicMqlList = new MongoRepoDynamicMqlList(lstMql);
			return RestObject.retOKWithPayload(mongoRepoDynamicMqlList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/param:add", method = RequestMethod.PUT)
	@Operation(summary = "Add params to an existing MQL statement")
	public ResponseEntity<RestObject> 
	addMongoQueryParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="mqlId") final long mqlId,
						@RequestBody final MongoRepoMqlParamInput paramInput) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.insertMqlParam(	mqlId,
											paramInput.getDynamicMqlParamName(), 
											paramInput.getDynamicMqlParamDefault(), 
											paramInput.getDynamicMqlParamType(), 
											paramInput.getDynamicMqlParamPosition(), 
											paramInput.getDynamicMqlParamOrder()
										);

			List<MongoRepoMqlParam> p = mongoClusterDb.getMqlParams(mqlId);
			MongoRepoDynamicMqlParamList ret = new MongoRepoDynamicMqlParamList(p);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/param:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete MongoDb Query Parameter")
	public ResponseEntity<RestObject> 
	deleteMongoQueryParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="mqlId") final long mqlId,
							@RequestHeader(value="paramId") final long paramId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.deleteMqlParam(mqlId, paramId);
			List<MongoRepoMqlParam> p = mongoClusterDb.getMqlParams(mqlId);
			MongoRepoDynamicMqlParamList ret = new MongoRepoDynamicMqlParamList(p);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/param:generate", method = RequestMethod.PUT)
	@Operation(summary = "Generate Query Param")
	public ResponseEntity<RestObject>
	generateQueryParam(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="paramNumber") final int paramNumber) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoRepoDynamicMqlParamInputList paramInputList = MongoRepoDynamicMqlParamInputList.generateParamInputList(paramNumber);
			return RestObject.retOKWithPayload(paramInputList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	/*MQL to cluster bridges*/

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge/all:get", method = RequestMethod.GET)
	@Operation(summary = "Get Query Bridges for a certain statement id")
	public ResponseEntity<RestObject>
	getAllMongoQueryBridges(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoDynamicMqlToClusterBridgeList lstBridges = mongoClusterDb.getAllMqlToClusterBridges();
			return RestObject.retOKWithPayload(lstBridges, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge:get", method = RequestMethod.GET)
	@Operation(summary = "Get Query Bridges for a certain statement id")
	public ResponseEntity<RestObject> 
	getMongoQueryBridges(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="mqlId") final long mqlId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			MongoDynamicMqlToClusterBridgeList 
			lstBridges = mongoClusterDb.getMqlToClusterBridges(mqlId);
			return RestObject.retOKWithPayload(lstBridges, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge:add", method = RequestMethod.PUT)
	@Operation(summary = "Bridge MQL to Cluster")
	public ResponseEntity<RestObject> 
	addMqlToClusterBridge(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="mqlId") final long mqlId,
							@RequestHeader(value="clusterId") final int clusterId,
							@RequestHeader(value="active") final int active) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.mergeMqlToClusterBridge(	mqlId, clusterId, active);
			MongoDynamicMqlToClusterBridge ret = mongoClusterDb.getMqlToClusterBridge(clusterId, mqlId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/query/bridge:delete", method = RequestMethod.DELETE)
    @Operation(summary = "Delete Bridge MQL to Cluster")
	public ResponseEntity<RestObject> 
	deleteMqlToClusterBridge(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="bridgeId") final int bridgeId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.deleteMqlToClusterBridge(bridgeId);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	
	/*  Associations*/

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association/all:get", method = RequestMethod.GET)
	@Operation(summary = "Get all associations")
	public ResponseEntity<RestObject>
	getMongoRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			RepoAssociationTableList repoAssociationTableList = mongoClusterDb.getAllRepoAssociationTable();
			return RestObject.retOKWithPayload(repoAssociationTableList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:get", method = RequestMethod.GET)
	@Operation(summary = "Get association ")
	public ResponseEntity<RestObject> 
	getMongoRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationName") final String associationName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			RepoAssociationTableList repoAssociationTableList = mongoClusterDb.getRepoAssociationTable( associationName );
			return RestObject.retOKWithPayload(repoAssociationTableList, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:add", method = RequestMethod.PUT)
	@Operation(summary = "Add association")
	public ResponseEntity<RestObject> 
	addMongoRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationName") final String associationName) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.insertRepoAssociationTable(associationName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:update", method = RequestMethod.PUT)
    @Operation(summary = "Update association")
	public ResponseEntity<RestObject> 
	updateRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="associationId") final int associationId,
								@RequestHeader(value="associationName") final String associationName) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.updateRepoAssociationTable(associationId , associationName);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/management/association:delete", method = RequestMethod.PUT)
	@Operation(summary = "Delete association")
	public ResponseEntity<RestObject> 
	deleteMongoRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="associationId") final int associationId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			mongoClusterDb.deleteRepoAssociationTable(associationId);
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	/*Uploading / downloading large attachment*/
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:add", 
					consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE },
					method = RequestMethod.POST)
	@Operation(summary = "Add large binary file to mongo database")
	public ResponseEntity<RestObject>
	addMongoBucketFile(@RequestHeader(value="user") final String user,
					   @RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="clusterName") final String clusterName,
					   @RequestHeader(value="databaseName") final String dbName,
					   @RequestHeader(value="bucketName") final String bucketName,
					   @RequestHeader(value="fileName") final String fileName,
					   @RequestHeader(value="metadata", required = false) final String metadata,
					   @RequestParam("attachment") final MultipartFile attachment) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String fileId = MongoDbTransaction.addFileToBucket(	mongoClusterDb,
																	clusterName,
																	dbName,
																	bucketName,
																	fileName,
																	metadata,
																	attachment,
																	authUtil.getUser(user).getId());
			return RestObject.retOK(fileId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:get", method = RequestMethod.GET)
	@Operation(summary = "Get large binary file from mongo database")
	public ResponseEntity<RestObject> 
	getMongoLargeAttachment(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterName") final String clusterName,
							@RequestHeader(value="databaseName") final String dbName,
							@RequestHeader(value="bucketName") final String bucketName,
							@RequestHeader(value="fileId") final String fileId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			LargeMongoBinaryFile ret = MongoDbTransaction.getFileFromBucket(clusterName, dbName, bucketName, fileId);
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:download", method = RequestMethod.GET)
	@Operation(summary = "Get large binary file from mongo database")
	public ResponseEntity<Resource> 
	downloadLargeAttachment(@RequestHeader(value="clusterName") final String clusterName,
							@RequestHeader(value="databaseName") final String dbName,
							@RequestHeader(value="bucketName") final String bucketName,
							@RequestHeader(value="fileId") final String fileId) {
		try	{
			LargeMongoBinaryFile retLargeObject = MongoDbTransaction.getFileFromBucket(clusterName, dbName, bucketName, fileId);
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
			InputStreamResource content = new InputStreamResource(new ByteArrayInputStream(retLargeObject.getFileStr().getBytes(StandardCharsets.UTF_8)));
			return ResponseEntity.ok()	.contentLength(retLargeObject.getFileSize())
										.contentType(MediaType.parseMediaType(MimeTypes.MIME_APPLICATION_OCTET_STREAM)).headers(headers)
										.body(content);
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(HttpStatusCode.valueOf(500));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete large binary file from mongo database")
	public ResponseEntity<RestObject> 
	deleteFIleFromBucket(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterName") final String clusterName,
							@RequestHeader(value="databaseName") final String dbName,
							@RequestHeader(value="bucketName") final String bucketName,
							@RequestHeader(value="fileId") final String fileId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			boolean ret = MongoDbTransaction.deleteFileFromBucket(clusterName, dbName, bucketName, fileId);
			return RestObject.retOKWithPayload(new GenericResponse(Boolean.toString(ret)), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/mongo-repo/cluster/large/many:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete large binary file from mongo database")
	public ResponseEntity<RestObject>
	deleteManyFilesFromBucket(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								  @RequestHeader(value="clusterName") final String clusterName,
								  @RequestHeader(value="databaseName") final String dbName,
								  @RequestHeader(value="bucketName") final String bucketName,
								  @RequestHeader(value="filter") final String filter) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			boolean ret = MongoDbTransaction.deleteManyFilesFromBucket(clusterName, dbName, bucketName, filter);
			return RestObject.retOKWithPayload(new GenericResponse(Boolean.toString(ret)), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

}
