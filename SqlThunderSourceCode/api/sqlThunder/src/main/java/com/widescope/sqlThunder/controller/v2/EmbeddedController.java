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

import javax.validation.Valid;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.*;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableList;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticLowLevelWrapper;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.SearchSql;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload2;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.sql.ElasticSqlPayload;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.*;

import com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec.EmbeddedExecTableList;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.InMemDbs;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.ClusterTransfer;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.DataTransfer;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.ListRdbmsCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedClusterInfo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedClusterList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedClusterPermList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedClusterRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.utils.EmbeddedQueryUtils;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbConnection;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.structuredFiles.csv.CsvWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatOutput;
import com.widescope.rdbmsRepo.utils.SqlParser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
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
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.service.StorageService;

@CrossOrigin
@RestController
@Schema(title = "Embedded DBs and saved snapshots")
public class EmbeddedController {
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private ElasticClusterDb elasticClusterDb;
	
	@Autowired
	private	EmbeddedDbRepo embeddedDbRepo;
	

	@Autowired
	private StorageService storageService;

	
	
	/**
	 * Data Migration / Copy from Mongo/Elastic/RDBMS to RDBMS Embedded database tables
	 */

	@PostConstruct
	public void initialize() {

	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/mongodb/search:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy records from Mongodb simple search to Embedded table")
	public ResponseEntity<RestObject> 
	copyMongoSimpleSearchResultToEmbedded(	@RequestHeader(value="user") String user,
										  	@RequestHeader(value="requestId") String requestId,
											@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
											@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
											@RequestHeader(value="fromCollectionName") String fromCollectionName,
											@RequestHeader(value="itemToSearch") String itemToSearch,
											@RequestHeader(value="valueToSearch") String valueToSearch,
											@RequestHeader(value="valueToSearchType") String valueToSearchType,
											@RequestHeader(value="operator", defaultValue = "$eq") String operator,
											@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
											@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
											@RequestHeader(value="toCluster") String toCluster,
											@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
											@RequestHeader(value="toEmbeddedTableName", required = false) String toEmbeddedTableName) {

		try	{
			if(toEmbeddedTableName.isEmpty() || toEmbeddedTableName.isBlank()) {
				toEmbeddedTableName = fromCollectionName;
			}

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
																	false/*it determines metadata is false*/) ;
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet())); 
			
			fromMongoDbConnection.disconnect();
			long clusterId_ = Long.parseLong(toCluster);
			RecordsAffected recordsAffected = 
			EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
															toEmbeddedDatabaseName,
															toEmbeddedSchemaName,
															toEmbeddedTableName,
															MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
															mongoResultSet.getMetadata());

			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															toEmbeddedDatabaseName,
															toEmbeddedType,
									                        u.getId(),
									                        clusterId_,
									                        "",
									                        "");
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}
			
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/mongodb/search:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy Mongodb collection(s) range search result to Embedded table") 
	public ResponseEntity<RestObject> 
	copyMongoRangeSearchResultToEmbedded(	@RequestHeader(value="user") String user,
											@RequestHeader(value="requestId") String requestId,
											@RequestHeader(value="fromClusterUniqueName") String fromClusterUniqueName,
											@RequestHeader(value="fromMongoDbName") String fromMongoDbName,
											@RequestHeader(value="fromCollectionName") String fromCollectionName,
											@RequestHeader(value="itemToSearch") String itemToSearch,
											@RequestHeader(value="fromValue") String fromValue,
											@RequestHeader(value="toValue") String toValue,
											@RequestHeader(value="valueSearchType") String valueSearchType,
											@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
											@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
											@RequestHeader(value="toCluster") String toCluster,
											@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
											@RequestHeader(value="toEmbeddedTableName", required = false) String toEmbeddedTableName) {
		try	{
			if(toEmbeddedTableName.isEmpty() || toEmbeddedTableName.isBlank()) {
				toEmbeddedTableName = fromCollectionName;
			}
			
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
			
			long clusterId_ = Long.parseLong(toCluster);
			RecordsAffected recordsAffected = 
			EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
															toEmbeddedDatabaseName,
															toEmbeddedSchemaName,
															toEmbeddedTableName,
															MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
															mongoResultSet.getMetadata());
			
			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															toEmbeddedDatabaseName,
															toEmbeddedType,
									                        u.getId(),
									                        clusterId_,
									                        "",
									                        "");
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/mongodb:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy full Mongodb collection to Embedded table") 
	public ResponseEntity<RestObject> 
	copyMongoFullCollectionToEmbedded(@RequestHeader(value="user") String user,
									  @RequestHeader(value="requestId") String requestId,
									  @RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
									  @RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
									  @RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
									  @RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
									  @RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
									  @RequestHeader(value="toCluster") String toCluster,
									  @RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
									  @RequestHeader(value="toEmbeddedTableName") String toEmbeddedTableName) {
		try	{

			if(toEmbeddedTableName.isEmpty() || toEmbeddedTableName.isBlank()) {
				toEmbeddedTableName = fromMongoCollectionName;
			}
			
			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(),
																			fromMongoClusterRecord.getClusterId(), 
																			fromMongoClusterRecord.getUniqueName());
			
			MongoResultSet mongoResultSet = MongoGet.getAllCollectionDocuments(	fromMongoDbConnection, 
																				fromMongoDatabaseName, 
																				fromMongoCollectionName) ;
			
			fromMongoDbConnection.disconnect();
			long clusterId_ = Long.parseLong(toCluster);
			RecordsAffected recordsAffected = 
					EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
																	toEmbeddedDatabaseName,
																	toEmbeddedSchemaName,
																	toEmbeddedTableName,
																	MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
																	mongoResultSet.getMetadata());

			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															toEmbeddedDatabaseName,
															toEmbeddedType,
									                        u.getId(),
									                        clusterId_,
									                        "",
									                        "");
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/mongodb:adhoc", method = RequestMethod.PUT)
	@Operation(summary = "Copy Mongodb ad-hoc search result to Embedded table") 
	public ResponseEntity<RestObject> 
	copyMongoAdhocResultToEmbedded(	@RequestHeader(value="user") String user,
									@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromClusterUniqueName") String fromMongoClusterName,
									@RequestHeader(value="fromMongoDbName") String fromMongoDatabaseName,
									@RequestHeader(value="fromCollectionName") String fromMongoCollectionName,
									@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
									@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
									@RequestHeader(value="toClusterId") String toCluster,
									@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
									@RequestHeader(value="toEmbeddedTableName", required = false) String toEmbeddedTableName,
									@RequestBody String bsonQuery) {

		try	{
			if(toEmbeddedTableName.isEmpty() || toEmbeddedTableName.isBlank()) {
				toEmbeddedTableName = fromMongoCollectionName;
			}
			
			MongoResultSet mongoResultSet 
			= MongoGet.execDynamicQuery(	fromMongoClusterName, 
											fromMongoDatabaseName, 
											fromMongoCollectionName,
											bsonQuery,
											true);
			
			long clusterId_ = Long.parseLong(toCluster);
			RecordsAffected recordsAffected = 
					EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
																	toEmbeddedDatabaseName,
																	toEmbeddedSchemaName,
																	toEmbeddedTableName,
																	MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
																	mongoResultSet.getMetadata());

			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															toEmbeddedDatabaseName,
															toEmbeddedType,
									                        u.getId(),
									                        clusterId_,
									                        "",
									                        "");
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Copy Elastic DSL query result to Embedded table")
	public ResponseEntity<RestObject> 
	copyElasticDslResultToEmbedded(	@RequestHeader(value="user") String user,
									@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
									@RequestHeader(value="fromElasticHttpVerb", defaultValue = "GET") String fromElasticHttpVerb,
									@RequestHeader(value="fromElasticEndPoint") String fromElasticEndPoint,
									@RequestHeader(value="toEmbeddedType", required = false, defaultValue = "H2") String toEmbeddedType,
									@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
									@RequestHeader(value="toCluster") String toCluster,
									@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
									@RequestHeader(value="toEmbeddedTableName") String toEmbeddedTableName,
									@RequestBody String httpPayload) {

		try	{

			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				ElasticPayload2 payload =
				ElasticInfo.executeGenericForPayload2(	elasticLowLevelWrapper, fromElasticHttpVerb, fromElasticEndPoint, httpPayload);
				Map<String, String> metadata = ElasticInfo.getMetadata(payload);
				List<Map<String,Object>> rows = ElasticInfo.getRows(payload);
				elasticLowLevelWrapper.disconnect();
				/*Transfer Data from Elastic to RDBMS table*/
				long clusterId_ = Long.parseLong(toCluster);
				RecordsAffected recordsAffected = 
						EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
																		toEmbeddedDatabaseName,
																		toEmbeddedSchemaName,
																		toEmbeddedTableName,
																		rows, 
																		metadata);
				
				EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
				if(r.getEmbeddedDbRecordList()	.stream()
												.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
					
					User u = authUtil.getUser(user);
					EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
																toEmbeddedDatabaseName,
																toEmbeddedType,
										                        u.getId(),
										                        clusterId_,
										                        "",
										                        "");
					
					embeddedDbRepo.addEmbeddedDb(rec);	
				}
				
				return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Elastic Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/elastic:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy Elastic SQL query result to Embedded table") 
	public ResponseEntity<RestObject> 
	copyElasticSqlResultToEmbedded(	@RequestHeader(value="user") String user,
									@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
									@RequestHeader(value="fromElasticFetchSize") Integer fromElasticFetchSize,
									@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
									@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
									@RequestHeader(value="toCluster") String toCluster,
									@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
									@RequestHeader(value="toEmbeddedTableName") String toEmbeddedTableName,
									@RequestBody String sqlContent) {
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				ElasticSqlPayload ret = SearchSql.searchSqlAsElasticSqlPayload(elasticLowLevelWrapper, sqlContent, fromElasticFetchSize);
				elasticLowLevelWrapper.disconnect();
				Map<String, String> metadata = ElasticSqlPayload.getMetadataAsMap(ret);
				List<Map<String,Object>> rows = ElasticSqlPayload.getRowAsListOfMap(ret);
				elasticLowLevelWrapper.disconnect();
				long clusterId_ = Long.parseLong(toCluster);
				/*Transfer Data from Elastic to RDBMS table*/
				RecordsAffected recordsAffected = 
						EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
																		toEmbeddedDatabaseName,
																		toEmbeddedSchemaName,
																		toEmbeddedTableName,
																		rows, 
																		metadata);
				
				
				EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
				if(r.getEmbeddedDbRecordList()	.stream()
												.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
					
					User u = authUtil.getUser(user);
					EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
																toEmbeddedDatabaseName,
																toEmbeddedType,
										                        u.getId(),
										                        clusterId_,
										                        "",
										                        "");
					
					embeddedDbRepo.addEmbeddedDb(rec);	
				}
				
				return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Elastic Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/sqlrepo:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy Rdbms Sql result to Embedded table") 
	public ResponseEntity<RestObject> 
	copyRdbmsSqlResultToEmbedded(	@RequestHeader(value="user") String user,
									@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromRdbmsSchemaUniqueName") String fromRdbmsSchemaUniqueName,
									@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
									@RequestHeader(value="toClusterId") String toClusterId,
									@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
									@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
									@RequestHeader(value="toEmbeddedTableName") String toEmbeddedTableName,
									@RequestBody String sqlContent) {
		try	{
			long clusterId_ = Long.parseLong(toClusterId);
			TableFormatMap recordSet=
			SqlMetadataWrapper.execAdhocForMigration(fromRdbmsSchemaUniqueName, sqlContent);
			/*Transfer Data from Elastic to RDBMS table*/
			RecordsAffected recordsAffected = 
					EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_ ,
																	toEmbeddedDatabaseName,
																	toEmbeddedSchemaName,
																	toEmbeddedTableName,
																	recordSet.getRows(), 
																	recordSet.getMetadata());
			
			
			
			
			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															toEmbeddedDatabaseName,
															toEmbeddedType,
									                        u.getId(),
									                        clusterId_,
									                        "",
									                        "");
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}

			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/embedded:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy Rdbms Sql Embedded result to Embedded table")
	public ResponseEntity<RestObject> 
	copyEmbeddedSqlResultToEmbedded(@RequestHeader(value="user") String user,
									@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromEmbeddedType", defaultValue = "H2") String fromEmbeddedType,
									@RequestHeader(value="fromClusterId") String fromClusterId,
									@RequestHeader(value="fromEmbeddedDatabaseName") String fromEmbeddedDatabaseName,
									@RequestHeader(value="fromEmbeddedSchemaName") String fromEmbeddedSchemaName,
									@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
									@RequestHeader(value="toClusterId") String toClusterId,
									@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
									@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
									@RequestHeader(value="toEmbeddedTableName") String toEmbeddedTableName,
									@RequestBody String sqlContent) {

		try	{
			
			H2Static h2Db = new H2Static(Long.parseLong(fromClusterId), fromEmbeddedDatabaseName );
			TableFormatMap recordSet=
					h2Db.execStaticQueryWithTableFormat(sqlContent);
			
			/*Transfer Data from Elastic to RDBMS table*/
			RecordsAffected recordsAffected = 
					EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	Long.parseLong(toClusterId) ,
																	toEmbeddedDatabaseName,
																	toEmbeddedSchemaName,
																	toEmbeddedTableName,
																	recordSet.getRows(), 
																	recordSet.getMetadata());
			
			
			
			
			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(toClusterId) );
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
				
				User u = authUtil.getUser(user);
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															toEmbeddedDatabaseName,
															toEmbeddedType,
									                        u.getId(),
									                        Long.parseLong(toClusterId),
									                        "",
									                        "");
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}

			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/copy/embedded/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv to embedded table")
	public ResponseEntity<RestObject> 
	copyCsvToEmbeddedTable(	@RequestHeader(value="user") String user,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="tableScript", defaultValue = "") String tableScript,
							@RequestHeader(value="toEmbeddedType", defaultValue = "H2") String toEmbeddedType,
							@RequestHeader(value="toClusterId") String toClusterId,
							@RequestHeader(value="toEmbeddedDatabaseName") String toEmbeddedDatabaseName,
							@RequestHeader(value="toEmbeddedSchemaName") String toEmbeddedSchemaName,
							@RequestHeader(value="toEmbeddedTableName") String toEmbeddedTableName,
							@RequestParam("file") MultipartFile file  /*or @RequestPart*/) {
		String fileName = StringUtils.generateUniqueString32();
		
		try	{
			String fullFilePath = storageService.storeTmp(file, fileName);
			String csvContent = CsvWrapper.readFile(fullFilePath);
			long clusterId_ = Long.parseLong(toClusterId);
			H2Static h2Db = new H2Static(clusterId_, toEmbeddedDatabaseName );
			RecordsAffected recordsAffected;
			try (Connection conn = h2Db.getConnection()) {
				if(!tableScript.isEmpty() && !SqlMetadataWrapper.isTable(toEmbeddedTableName, conn)) {
					if(SqlParser.isSqlDML(tableScript))
						SqlQueryRepoUtils.execStaticDdl(tableScript, conn);
				}
				String isCompressed;
				if(Objects.equals(file.getContentType(), "text/csv")) {
					isCompressed = "N";
				} else if(Objects.equals(file.getContentType(), "application/x-zip-compressed")) {
					isCompressed = "Y";
				} else {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Unknown File Type");
				}
				
				
				TableMetadata tM = SqlMetadataWrapper.getTableColumns(toEmbeddedTableName, conn);
				TableFormatMap recordSet= CsvWrapper.stringToTable(csvContent, tM,  isCompressed);
				/*Transfer Data from Csv to RDBMS table*/
				
				recordsAffected = 
						EmbeddedQueryUtils.insertBulkIntoEmbeddedTable(	clusterId_,
																		toEmbeddedDatabaseName,
																		toEmbeddedSchemaName,
																		toEmbeddedTableName,
																		recordSet.getRows(), 
																		recordSet.getMetadata());
				
				EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_ );
				if(r.getEmbeddedDbRecordList()	.stream()
												.noneMatch(x-> x.getFileName().equalsIgnoreCase(toEmbeddedDatabaseName))) {
					
					User u = authUtil.getUser(user);
					EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
																toEmbeddedDatabaseName,
																toEmbeddedType,
										                        u.getId(),
										                        clusterId_,
										                        "",
										                        "");
					
					embeddedDbRepo.addEmbeddedDb(rec);	
				}
				
		    } catch (SQLException ex) {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		    } 
			
			
			return RestObject.retOKWithPayload(recordsAffected, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}  finally {
			storageService.deleteTmp(fileName);
		}
	}
	
	/*In Memory Query operations START */
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/table:empty", method = RequestMethod.PUT)
    @Operation(summary = "Create empty table into in-mem db") 
	public ResponseEntity<RestObject> 
	createEmptyTablesInMemDb(	@RequestHeader(value="user") String user,
								 @RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="session") String session,
								@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
								@RequestBody List<TableDefinition> tableDefinition) {
		String fileName = StringUtils.generateUniqueString32();
		String toDbName = StringUtils.generateUniqueString16();
		
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			H2InMem h2InMem = new H2InMem(session, requestId, user, toDbName);
			try {
				EmbeddedDbRecord eDbRec = h2InMem.createEmptyTable("", toDbName, fileName, userId, tableDefinition, comment);
				ClusterTransfer ret =EmbeddedWrapper.loadEmptyDbInMem( eDbRec, session, requestId, user, h2InMem, comment);
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw ex;
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/table:append", method = RequestMethod.PUT)
	@Operation(summary = "Append in-mem db table")
	public ResponseEntity<RestObject> 
	appendInMemDbTables(@RequestHeader(value="user") String user,
						@RequestHeader(value="session") String session,
						@RequestHeader(value="requestId") String requestId,
						@RequestBody List<RowValue> tableDefinition) {
		String toDbName = StringUtils.generateUniqueString16();
		try	{
			H2InMem h2InMem = new H2InMem(session, requestId, user, toDbName);
			try {
				return RestObject.retOKWithPayload("" ,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw ex;
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem:cluster", method = RequestMethod.PUT)
    @Operation(summary = "Load cluster in memory") 
	public ResponseEntity<RestObject> 
	loadEmbeddedClusterInMem(@RequestHeader(value="user") String user,
							 @RequestHeader(value="session") String session,
							 @RequestHeader(value="requestId") String requestId,
							 @RequestHeader(value="clusterId") String clusterId,
							 @RequestHeader(value="comment", required = false, defaultValue = "") String comment) {
		
		if(comment.isEmpty()) { comment = "In-mem cluster id: " + clusterId; }

		try	{
			ClusterTransfer ret = EmbeddedWrapper.loadH2ClusterInMem(Integer.parseInt(clusterId), session, requestId, user, comment);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem:database", method = RequestMethod.PUT)
	@Operation(summary = "Load database in memory.Database is part of a cluster")
	public ResponseEntity<RestObject> 
	loadEmbeddedDbInMem(@RequestHeader(value="user") String user,
						@RequestHeader(value="session") String session,
						@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterId") String clusterId,
						@RequestHeader(value="dbId") String dbId,
						@RequestHeader(value="comment", required = false, defaultValue = "") String comment) {

		try	{
			EmbeddedDbRecord eDbRec = embeddedDbRepo.getEmbeddedDb(Long.parseLong(clusterId), Long.parseLong(dbId));
			ClusterTransfer clusterTransfer 
			= EmbeddedWrapper.loadH2ClusterInMem(	Long.parseLong(clusterId),
													eDbRec,
													session,
													requestId,
													user,
													comment);
			
			return RestObject.retOKWithPayload(clusterTransfer, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem:query", method = RequestMethod.PUT)
	@Operation(summary = "Load query result in memory")
	public ResponseEntity<RestObject> 
	loadEmbeddedQueryInMem(	@RequestHeader(value="user") String user,
							@RequestHeader(value="session") String session,
							@RequestHeader(value="fromEmbeddedType", defaultValue = "H2") String fromEmbeddedType,
							@RequestHeader(value="fromClusterId") String fromClusterId,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="fromEmbeddedDatabaseName") String fromEmbeddedDatabaseName,
							@RequestHeader(value="fromEmbeddedSchemaName") String fromEmbeddedSchemaName,
							@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
							@RequestBody String sqlContent) {
		if(comment.isEmpty()) {
			comment = "In-mem cluster id: " + fromClusterId + " sql :" + sqlContent;
		}
		
		try	{
			H2InMem h2InMem = new H2InMem(	Long.parseLong(fromClusterId), 
											fromEmbeddedDatabaseName, 
											"QUERY",
											session,
											requestId,
											user);
			DataTransfer ret = h2InMem.loadH2QueryInMem(sqlContent,	comment);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
		
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/rdbms:query", method = RequestMethod.PUT)
	@Operation(summary = "Load RDBMS query result in memory") 
	public ResponseEntity<RestObject> 
	loadRdbmsQueryInMem(@RequestHeader(value="user") String user,
						@RequestHeader(value="session") String session,
						@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="schemaUniqueName") String schemaUniqueName,
						@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
						@RequestBody String sqlContent) {
		if(comment.isEmpty()) {
			comment = "In-mem query from : " + schemaUniqueName + " sql :" + sqlContent;
		}
		
		
		try	{
			if(SqlParser.isSqlDQL(sqlContent)) {
				TableFormatMap tableFormatMap = new TableFormatMap();
				DataTransfer ret = new DataTransfer();
				tableFormatMap = SqlQueryRepoUtils.execStaticQueryWithTableFormatMap(schemaUniqueName, sqlContent);

				final List<String> _userTables = new ArrayList<>();
				H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
												-1, /*_dbId*/
												"QUERY", 
												session, 
												requestId, 
												user,
												_userTables,
												"__RDBMS__",
												schemaUniqueName
												);
				
				
				String tableName = EmbeddedWrapper.getSqlStr(sqlContent);
				ret = h2InMem.loadRdbmsQueryInMem(	tableFormatMap, 
													tableName, 
													sqlContent, 
													schemaUniqueName,
													comment);
				return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Not a query" ));
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/rdbms:queries", method = RequestMethod.PUT)
	@Operation(summary = "Load RDBMS query result in memory") 
	public ResponseEntity<RestObject> 
	loadRdbmsQueriesInMem(	@RequestHeader(value="user") String user,
							@RequestHeader(value="session") String session,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="schemaUniqueName") String schemaUniqueName,
							@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
							@RequestBody ListRdbmsCompoundQuery listRdbmsCompoundQuery) {
		if(comment.isEmpty()) {
			comment = "in mem query from : " + schemaUniqueName ;
		}
		
		try	{
			for(RdbmsCompoundQuery r: listRdbmsCompoundQuery.getLst() ) {
				if(!SqlParser.isSqlDQL(r.getSqlContent())) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Not a query" ));
				}
			}
			List<RdbmsTableSetup> lst = new ArrayList<>();
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
			for(RdbmsCompoundQuery r: listRdbmsCompoundQuery.getLst() ) {
				TableFormatMap tableFormatMap = SqlQueryRepoUtils.execStaticQueryWithTableFormatMap(schemaUniqueName, r.getSqlContent());
				RdbmsTableSetup setup = new RdbmsTableSetup();
				setup.setTableFormatMap(tableFormatMap);
				String tableName = "TBL_" + EmbeddedWrapper.getSqlStr(r.getSqlContent());
				setup.setTableName(tableName);
				String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(tableFormatMap.getMetadata(), tableName);
				setup.setCreateTableStm(createTableStm);
				String insertStm = SqlMetadataWrapper.generateInsertTableStm(tableFormatMap.getMetadata(), "", tableName);
				setup.setInsertTableStm(insertStm);
				lst.add(setup);
			}
			H2InMem h2InMem = new H2InMem(schemaUniqueName, inMemDbName, "QUERY", session, requestId, user);
			DataTransfer ret = h2InMem.loadRdbmsQueriesInMem(lst, schemaUniqueName,	comment);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/rdbms:tables", method = RequestMethod.PUT)
	@Operation(summary = "Load RDBMS query result in memory") 
	public ResponseEntity<RestObject> 
	loadRdbmsTablesInMem(	@RequestHeader(value="user") String user,
							@RequestHeader(value="session") String session,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="schemaUniqueName") String schemaUniqueName,
							@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
							@RequestBody List<String> listRdbmsTables) {
		if(comment.isEmpty()) {
			comment = "In-mem query from : " + schemaUniqueName + " multiple tables " ;
		}
		
		try	{
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(schemaUniqueName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			

			List<RdbmsTableSetup> setupSets = new ArrayList<>();
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
			for(String tblName: listRdbmsTables ) {
				TableMetadata m = SqlMetadataWrapper.getTableColumns(connectionDetailInfo, tblName);
				RdbmsTableSetup setup = SqlMetadataWrapper.createTableStm(m, "", tblName, "H2");
				TableFormatMap tableFormatMap = SqlQueryRepoUtils.execStaticQueryWithTableFormatMap(schemaUniqueName, "SELECT * FROM " + tblName);
				setup.setTableFormatMap(tableFormatMap);
				setup.setTableName(tblName);
				setupSets.add(setup);
			}
			
			H2InMem h2InMem = new H2InMem(schemaUniqueName, inMemDbName, "TABLE", session, requestId, user);
			DataTransfer ret = h2InMem.loadRdbmsTablesInMem(setupSets, schemaUniqueName, comment);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	/*In Memory ElasticSearch*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Load Elastic Index Dsl query result in memory") 
	public ResponseEntity<RestObject> 
	loadElasticIndexInMemViaDsl(@RequestHeader(value="user") String user,
								@RequestHeader(value="session") String session,
								@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
								@RequestHeader(value="fromIndexName", defaultValue = "") String fromIndexName,
								@RequestHeader(value="fromHttpVerb") String fromHttpVerb,
								@RequestHeader(value="fromElasticApi") String fromElasticApi,
								@RequestHeader(value="fromEndPoint") String fromEndPoint,
								@RequestHeader(value="batchValue", required = false, defaultValue = "0") String batchValue,
								@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
								@RequestBody (required = false) String dslStatement) {

		if(comment.isEmpty()) {
			comment = "In-mem Elastic DSL query from : " + fromElasticClusterName + " dsl :" + dslStatement;
		}
		
		if(fromIndexName.isEmpty()) {
			fromIndexName = "elastic_" + StringUtils.generateUniqueString8();
		}
		
		try	{
			Map<String, ElasticCluster> fromClusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(fromClusterMap.size() == 1) {
				HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(fromClusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper fromElasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
				ElasticPayload2 payload =
						ElasticInfo.executeGenericForPayload2(	fromElasticLowLevelWrapper, 
																fromHttpVerb, 
																fromEndPoint, 
																dslStatement);

				
				fromElasticLowLevelWrapper.disconnect();
				Map<String, String> metadata = ElasticInfo.getMetadata(payload);
				List<Map<String,Object>> rows = ElasticInfo.getRows(payload);
				
				if(metadata.isEmpty() || rows.isEmpty()) {
					final DataTransfer ret = new DataTransfer();
					return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}

				
				final List<String> _userTables = new ArrayList<>();
								
				H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
												-1, /*_dbId*/
												"QUERY", 
												session, 
												requestId, 
												user,
												_userTables,
												"__ELASTIC__",
												fromElasticClusterName
												);
					
				
				final DataTransfer ret 
				=  h2InMem.createAndInsertBulkIntoTable("",
														fromIndexName,
														rows, 
														metadata,
														comment);
				
				
				
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cannot find Elastic cluster: " + fromElasticClusterName);
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/elastic:sql", method = RequestMethod.PUT)
	@Operation(summary = "Load Elastic Index Sql query result in memory") 
	public ResponseEntity<RestObject> 
	loadElasticIndexInMemViaSql(@RequestHeader(value="user") String user,
								@RequestHeader(value="session") String session,
								@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="fromElasticClusterName") String fromElasticClusterName,
								@RequestHeader(value="fromIndexName", defaultValue = "") String fromIndexName,
								@RequestHeader(value="fromHttpVerb") String fromHttpVerb,
								@RequestHeader(value="fromElasticApi") String fromElasticApi,
								@RequestHeader(value="fromEndPoint") String fromEndPoint,
								@RequestHeader(value="batchValue", required = false, defaultValue = "0") String batchValue,
								@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
								@RequestBody (required = false) String sqlStatement) {
		
		if(comment.isEmpty()) {
			comment = "In-mem Elastic Sql query from : " + fromElasticClusterName + " sql :" + sqlStatement;
		}
		
		if(fromIndexName.isEmpty()) {
			fromIndexName = "elastic_" + StringUtils.generateUniqueString8();
		}
		
		
		try	{

			Map<String, ElasticCluster> fromClusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
			if(fromClusterMap.size() == 1) {
				HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(fromClusterMap, fromElasticClusterName);
				ElasticLowLevelWrapper fromElasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
				ElasticPayload2 payload =
				ElasticInfo.executeGenericForPayload2(	fromElasticLowLevelWrapper, 
														fromHttpVerb, 
														fromEndPoint, 
														sqlStatement);
				
				
				fromElasticLowLevelWrapper.disconnect();
				
				Map<String, String> metadata = ElasticInfo.getMetadata(payload);
				List<Map<String,Object>> rows = ElasticInfo.getRows(payload);
				
				if(metadata.isEmpty() || rows.isEmpty()) {
					final DataTransfer ret = new DataTransfer();
					return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}

				final List<String> _userTables = new ArrayList<>();
				
				H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
												-1, /*_dbId*/
												"QUERY", 
												session, 
												requestId, 
												user,
												_userTables,
												"__ELASTIC__",
												fromElasticClusterName
												);
				
				
				final DataTransfer ret 
				=  h2InMem.insertBulkIntoTable(	"",
												fromIndexName,
												rows, 
												metadata,
												comment);
				
				
				
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Cannot find Elastic cluster: " + fromElasticClusterName ));
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	/*In Memory Mongodb*/


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/mongodb/search:simple", method = RequestMethod.PUT)
	@Operation(summary = "Load Simple Search Mongodb Result In Memory") 
	public ResponseEntity<RestObject> 
	loadMongoSimpleSearchResultInMem(@RequestHeader(value="user") String user,
									 @RequestHeader(value="session") String session,
									 @RequestHeader(value="requestId") String requestId,
									 @RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
									 @RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
									 @RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
									 @RequestHeader(value="itemToSearch") String itemToSearch,
									 @RequestHeader(value="valueToSearch") String valueToSearch,
									 @RequestHeader(value="valueToSearchType") String valueToSearchType,
									 @RequestHeader(value="operator") String operator,
									 @RequestHeader(value="batchCount", required = false, defaultValue = "0") String batchCount,
									 @RequestHeader(value="comment", required = false, defaultValue = "") String comment) {

		if(comment.isEmpty()) {
			comment = "In-mem Mongodb SimpleSearch from : " + fromMongoClusterName + ", database :" + fromMongoDatabaseName + ", collection" + fromMongoCollectionName;
		}
		
		try	{

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(),
																			fromMongoClusterRecord.getClusterId(), 
																			fromMongoClusterRecord.getUniqueName());
			
		
			MongoResultSet mongoResultSet = MongoGet.searchDocument(fromMongoDbConnection, 
																	fromMongoDatabaseName, 
																	fromMongoCollectionName,
																	itemToSearch,  
																	valueToSearch,
																	operator,
																	valueToSearchType,
																	true,
																	false/*determine metadata is false*/) ;
			mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet())); 
			
			fromMongoDbConnection.disconnect();
			
			if(mongoResultSet.getMetadata().isEmpty() || mongoResultSet.getResultSet().isEmpty()) {
				final DataTransfer ret = new DataTransfer();
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			
			

			final List<String> _userTables = new ArrayList<>();
			

			
			H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
											-1, /*_dbId*/
											"QUERY", 
											session, 
											requestId, 
											user,
											_userTables,
											"__MONGODB__",
											fromMongoClusterName);
		
			try {
				final DataTransfer ret 
				=  h2InMem.insertBulkIntoTable(	"",
												fromMongoCollectionName,
												MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
												mongoResultSet.getMetadata(),
												comment);
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/mongodb/search:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to RDBMS table from another Mongodb collection(s) range search") 
	public ResponseEntity<RestObject> 
	loadMongoRangeSearchResultInMem(@RequestHeader(value="user") String user,
									@RequestHeader(value="session") String session,
									@RequestHeader(value="requestId") String requestId,
									@RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
									@RequestHeader(value="itemToSearch") String itemToSearch,
									@RequestHeader(value="fromValue") String fromValue,
									@RequestHeader(value="toValue") String toValue,
									@RequestHeader(value="valueSearchType") String valueSearchType,
									@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
									@RequestHeader(value="comment", required = false, defaultValue = "") String comment) {

		if(comment.isEmpty()) {
			comment = "In-mem Mongodb RangeSearch from : " + fromMongoClusterName + ", database :" + fromMongoDatabaseName + ", collection" + fromMongoCollectionName;
		}
		
		try	{

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(), 
																			fromMongoClusterRecord.getUniqueName());
			
			MongoResultSet mongoResultSet = MongoGet.searchDocumentRange(	fromMongoDbConnection, 
																			fromMongoDatabaseName, 
																			fromMongoCollectionName,
																			itemToSearch,  
																			fromValue,
																			toValue,
																			valueSearchType,
																			true) ;
			fromMongoDbConnection.disconnect();
			
			if(mongoResultSet.getMetadata().isEmpty() || mongoResultSet.getResultSet().isEmpty()) {
				final DataTransfer ret = new DataTransfer();
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}

			final List<String> _userTables = new ArrayList<>();
			

			
			H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
											-1, /*_dbId*/
											"QUERY", 
											session, 
											requestId, 
											user,
											_userTables,
											"__MONGODB__",
											fromMongoClusterName
											);
			try {		
				final DataTransfer ret 
				=  h2InMem.insertBulkIntoTable(	"",
												fromMongoCollectionName,
												MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
												mongoResultSet.getMetadata(),
												comment);
				
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/mongodb:collection", method = RequestMethod.PUT)
	@Operation(summary = "Load results in memory full Mongodb collection") 
	public ResponseEntity<RestObject> 
	loadMongoFullCollectionInMem(@RequestHeader(value="user") String user,
								 @RequestHeader(value="session") String session,
								 @RequestHeader(value="requestId") String requestId,
								 @RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
								 @RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
								 @RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
								 @RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
								 @RequestHeader(value="comment", required = false, defaultValue = "") String comment) {

		if(comment.isEmpty()) {
			comment = "In-mem Mongodb FullCollection from : " + fromMongoClusterName + ", database :" + fromMongoDatabaseName + ", collection" + fromMongoCollectionName;
		}
		
		try	{

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(),
																			fromMongoClusterRecord.getClusterId(), 
																			fromMongoClusterRecord.getUniqueName());
			
			MongoResultSet mongoResultSet = MongoGet.getAllCollectionDocuments(	fromMongoDbConnection, 
																				fromMongoDatabaseName, 
																				fromMongoCollectionName) ;
			
			fromMongoDbConnection.disconnect();
			
			if(mongoResultSet.getMetadata().isEmpty() || mongoResultSet.getResultSet().isEmpty()) {
				final DataTransfer ret = new DataTransfer();
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}

			final List<String> _userTables = new ArrayList<>();
			
			H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
											-1, /*_dbId*/
											"COLLECTION", 
											session, 
											requestId, 
											user,
											_userTables,
											"__MONGODB__",
											fromMongoCollectionName
											);
			
			try {
				final DataTransfer ret 
				=  h2InMem.insertBulkIntoTable(	"",
												fromMongoCollectionName,
												MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
												mongoResultSet.getMetadata(),
												comment);
				
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/mongodb:adhoc", method = RequestMethod.PUT)
	@Operation(summary = "Copy records to in-mem RDBMS table from Mongodb ad-hoc search")
	public ResponseEntity<RestObject> 
	loadMongoAdhocResultInMem(	@RequestHeader(value="user") String user,
								@RequestHeader(value="session") String session,
								@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="fromMongoClusterName") String fromMongoClusterName,
								@RequestHeader(value="fromMongoDatabaseName") String fromMongoDatabaseName,
								@RequestHeader(value="fromMongoCollectionName") String fromMongoCollectionName,
								@RequestHeader(value="batchCount", defaultValue = "0") String batchCount,
								@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
								@RequestBody String bsonQuery) {

		if(comment.isEmpty()) {
			comment = "In-mem Mongodb Adhoc from : " + fromMongoClusterName + ", database :" + fromMongoDatabaseName + ", collection" + fromMongoCollectionName + ", query: "+ bsonQuery;
		}
		
		try	{

			MongoClusterRecord fromMongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
			MongoDbConnection fromMongoDbConnection = new MongoDbConnection(fromMongoClusterRecord.getConnString(), 
																			fromMongoClusterRecord.getClusterId(), 
																			fromMongoClusterRecord.getUniqueName());
			
			
			
			MongoResultSet mongoResultSet 
			= MongoGet.execDynamicQuery(	fromMongoClusterName, 
											fromMongoDatabaseName, 
											fromMongoCollectionName,
											bsonQuery,
											false);
			
			

			fromMongoDbConnection.disconnect();
			
			if(mongoResultSet.getMetadata().isEmpty() || mongoResultSet.getResultSet().isEmpty()) {
				final DataTransfer ret = new DataTransfer();
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}

			final List<String> _userTables = new ArrayList<>();
			
			H2InMem h2InMem = new H2InMem(	-1, /*_clusterId*/
											-1, /*_dbId*/
											"QUERY", 
											session, 
											requestId, 
											user,
											_userTables,
											"__MONGODB__",
											fromMongoCollectionName
											);
			
			try {
				final DataTransfer ret 
				=  h2InMem.insertBulkIntoTable(	"",
												fromMongoCollectionName,
												MongoResultSet.getRecords(mongoResultSet.getResultSet()), 
												mongoResultSet.getMetadata(),
												comment);
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv to in mem table")
	public ResponseEntity<RestObject> 
	copyCsvToInMemDb(	@RequestHeader(value="user") String user,
						@RequestHeader(value="session") String session,
						@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="tableScript", required = false, defaultValue = "") String tableScript,
						@RequestHeader(value="comment", required = false, defaultValue = "") String comment,
						@RequestParam("file") MultipartFile file) {

		if(comment.isEmpty()) {
			comment = "In-mem CSV : " + file.getName();
		}
		
		String fileName = StringUtils.generateUniqueString32();
		
		String toDbName = StringUtils.generateUniqueString16();
		String toEmbeddedTableName = StringUtils.generateUniqueString16();
		
		try	{
			
			String isCompressed = "N";
			if( Objects.equals(file.getContentType(), "text/csv") || Objects.equals(file.getContentType(), "application/vnd.ms-excel")) {
				isCompressed = "N";
			} else if(Objects.equals(file.getContentType(), "application/x-zip-compressed")) {
				isCompressed = "Y";
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Unknown File Type.");
			}
			
			String fullFilePath = storageService.storeTmp(file, fileName);
			String csvContent = CsvWrapper.readFile(fullFilePath);
			H2InMem h2InMem = new H2InMem(	session, 
											requestId, 
											user, 
											toDbName);
			
			try {
				
				if(!tableScript.isEmpty()) {
					if(SqlParser.isSqlDDL(tableScript) ) {
						Connection conn = h2InMem.getConnection();
						SqlQueryRepoUtils.execStaticDdl(tableScript, conn);	
					}
					
				} 
				
				TableMetadata tM = null;
				TableFormatMap recordSet= CsvWrapper.stringToTable(csvContent, tM,  isCompressed);
				/*Transfer Data from Csv to RDBMS table*/
				h2InMem.createAndInsertBulkIntoTable(	"",
														toEmbeddedTableName,
														recordSet.getRows(), 
														recordSet.getMetadata(),
														comment);
	
	            
				EmbeddedDbRecord embeddedDbRecord = new EmbeddedDbRecord(-1,toDbName, "H2", -1, -1, "", "");
				ClusterTransfer ret =EmbeddedWrapper.loadCsvInMem( embeddedDbRecord, session, requestId, user, h2InMem, comment);
				return RestObject.retOKWithPayload(ret,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} catch (Exception ex) {
				h2InMem.removeInMemDb();
				throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			storageService.deleteTmp(fileName);
		}
	}
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem/stores/remove:request", method = RequestMethod.POST)
	@Operation(summary = "Remove all in-mem storage for request")
	public ResponseEntity<RestObject> 
	removeRequestInMemoryDbs(	@RequestHeader(value="sessionId") String sessionId,
								@RequestHeader(value="requestId") String requestId) {


		try	{
			EmbeddedWrapper.removeInMemDbRequestId(sessionId, requestId);
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/inmem:stores", method = RequestMethod.GET)
	@Operation(summary = "Get a list of in-mem db")
	public ResponseEntity<RestObject> 
	getInMemoryDbs(	@RequestHeader(value="requestId") String requestId) {
		try	{
			InMemDbs ret = EmbeddedWrapper.getInMemDbs();
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/execute/inmem/adhoc:single", method = RequestMethod.POST)
	@Operation(summary = "Execute Adhoc Sql")
	public ResponseEntity<RestObject>
	executeInMemAdhocSql(	@RequestHeader(value="sessionId") String sessionId,
							@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="dbName") String dbName,
							@RequestHeader(value="sqlType", required = false, defaultValue = "") String sqlType, /*DQL/DML/DDL*/
							@RequestBody String sqlContent)  {

		try {
			EmbeddedInMemCluster inMemCluster =EmbeddedWrapper.getInmemDb(sessionId,requestId);
			H2InMem inMem = (H2InMem)inMemCluster.getCluster().get(dbName);
			if(inMem == null ) {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "exception: in mem is null" ));
			}
			ResultQuery ret = new ResultQuery();
			if(sqlType.equalsIgnoreCase("DQL")) {
				ret = SqlQueryExecUtils.execStaticQuery(inMem.getConnection(), sqlContent);
			} else if(sqlType.equalsIgnoreCase("DML")) {
				int count = SqlQueryExecUtils.execStaticDml(inMem.getConnection(), sqlContent);
				ret.setRecordsAffected(count);
				ret.setSqlType("DML");
			} else if(sqlType.equalsIgnoreCase("DDL")) {
				boolean isExecuted = SqlQueryExecUtils.execStaticDdl(inMem.getConnection(), sqlContent);
				ret.setRecordsAffected(isExecuted ? 1:0);
				ret.setSqlType("DDL");
			} else {
				if(SqlParser.isSqlDQL(sqlContent)) {
					ret = SqlQueryExecUtils.execStaticQuery(inMem.getConnection(), sqlContent);
				} else if(SqlParser.isSqlDML(sqlContent)) {
					int count = SqlQueryExecUtils.execStaticDml(inMem.getConnection(), sqlContent);
					ret.setRecordsAffected(count);
					ret.setSqlType("DML");
				} else if(SqlParser.isSqlDDL(sqlContent)) {
					boolean isExecuted = SqlQueryExecUtils.execStaticDdl(inMem.getConnection(), sqlContent);
					ret.setRecordsAffected(isExecuted ? 1:0);
					ret.setSqlType("DDL");
				} else {
					ret = SqlQueryExecUtils.execStaticQuery(inMem.getConnection(), sqlContent);
				}
			}
			
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 

	}
	
	
	
	
	/*In Memory Query operations END */

	
	
	/*****************************Environment specific endpoints ********************/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/dbtypes:get", method = RequestMethod.GET)
	@Operation(summary = "Get list of supported database types")
	public ResponseEntity<RestObject> 
	getEmbeddedDbTypes(	@RequestHeader(value="requestId") String requestId) {
		try	{
			return RestObject.retOKWithPayload(new EmbeddedDbTypes(), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	   
	}
	
	/**TO-BE-REWORKED*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/staticinfo:get", method = RequestMethod.GET)
	@Operation(summary = "Get list of Sql Commands")
	public ResponseEntity<RestObject> 
	getEmbeddedSqlCommands(	@RequestHeader(value="requestId") String requestId) {
		try	{
			return RestObject.retOKWithPayload(new EmbeddedStaticInfo(), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}

	/**************************  Exec Endpoints ************************/
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/execute/adhoc:single", method = RequestMethod.POST, consumes = "text/plain")
	@Operation(summary = "Execute Adhoc Sql")
	public ResponseEntity<RestObject>
	executeEmbeddedAdhocSql(@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="sqlType", required = false, defaultValue = "") String sqlType, /*DQL/DML/DDL*/
							@RequestHeader(value="clusterId") String clusterId,
							@RequestHeader(value="dbId") String dbId,
							@RequestBody String sqlContent)  {

		try {
			EmbeddedDbRecord e = embeddedDbRepo.getEmbeddedDb(Long.parseLong(clusterId) , Long.parseLong(dbId));
			ResultQuery ret = new ResultQuery();
			if(e.getType().compareToIgnoreCase("H2") == 0) {
				H2Static inMem = new H2Static(Integer.parseInt(clusterId), Integer.parseInt(dbId));
				
				if(sqlType.equalsIgnoreCase("DQL")) {
					ret = SqlQueryExecUtils.execStaticQuery(inMem.getConnection(), sqlContent);
				} else if(sqlType.equalsIgnoreCase("DML")) {
					int count = SqlQueryExecUtils.execStaticDml(inMem.getConnection(), sqlContent);
					ret.setRecordsAffected(count);
					ret.setSqlType("DML");
				} else if(sqlType.equalsIgnoreCase("DDL")) {
					boolean isExecuted = SqlQueryExecUtils.execStaticDdl(inMem.getConnection(), sqlContent);
					ret.setRecordsAffected(isExecuted ? 1:0);
					ret.setSqlType("DDL");
				} else {
					if(SqlParser.isSqlDQL(sqlContent)) {
						ret = SqlQueryExecUtils.execStaticQuery(inMem.getConnection(), sqlContent);
					} else if(SqlParser.isSqlDML(sqlContent)) {
						int count = SqlQueryExecUtils.execStaticDml(inMem.getConnection(), sqlContent);
						ret.setRecordsAffected(count);
						ret.setSqlType("DML");
					} else if(SqlParser.isSqlDDL(sqlContent)) {
						boolean isExecuted = SqlQueryExecUtils.execStaticDdl(inMem.getConnection(), sqlContent);
						ret.setRecordsAffected(isExecuted ? 1:0);
						ret.setSqlType("DDL");
					} else {
						ret = SqlQueryExecUtils.execStaticQuery(inMem.getConnection(), sqlContent);
					}
				}
				
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Unknown Db Type"));
			}

			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/execute/adhoc:cluster", method = RequestMethod.POST)
	@Operation(summary = "Execute Adhoc Sql on a multiple dbs or an entire cluster")
	public ResponseEntity<RestObject>
	executeAdhocSqlOnCluster(	@RequestHeader(value="user") String user,
								@RequestHeader(value="session") String session,
								@RequestHeader(value="requestId") String requestId,
								@Valid @RequestBody EmbeddedExecTableList cmd) {

		try {
			ResponseEntity<RestObject> ret = EmbeddedParallelExec.checkClusterCommand(cmd, embeddedDbRepo,requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			if(ret != null) {
				return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} 
			
			TableFormatOutput tbl = EmbeddedParallelExec.assemble(cmd, session, requestId, user);
			return RestObject.retOKWithPayload(tbl, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/adhoc/cluster/validate", method = RequestMethod.POST)
	@Operation(summary = "Validate Adhoc Sql on a multiple dbs or an entire cluster")
	public ResponseEntity<RestObject>
	validateAdhocSqlOnCluster(	@RequestHeader(value="requestId") String requestId,
								@Valid @RequestBody EmbeddedExecTableList cmd) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			if(null != EmbeddedParallelExec.checkClusterCommand(cmd, embeddedDbRepo, requestId, methodName) ) {
				return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, methodName);
			} else {
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl,"Invalid"));
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}

	}
	
	/**************************************  Clusters Management ***********************************************/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/clusters:get", method = RequestMethod.GET)
	@Operation(summary = "Get list of Embedded Clusters")
	public ResponseEntity<RestObject> 
	getEmbeddedClusters(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedClusterList e = embeddedDbRepo.getClusters();
			return RestObject.retOKWithPayload(e, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/clusters:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Embedded Cluster")
	public ResponseEntity<RestObject> 
	addEmbeddedCluster(	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="clusterName") String clusterName,
						@RequestHeader(value="description") String description,
						@Valid @RequestBody EmbeddedClusterInfo clusterInfo) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedClusterRecord embeddedClusterRecord = new EmbeddedClusterRecord(-1, clusterName, description, clusterInfo.getClusterRule(), clusterInfo.getSqlRule());
			embeddedDbRepo.addEmbeddedCluster(embeddedClusterRecord);
			EmbeddedClusterRecord e = embeddedDbRepo.getCluster(clusterName);
			
			return RestObject.retOKWithPayload(e, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/clusters:delete", method = RequestMethod.DELETE)
    @Operation(summary = "Delete Embedded Cluster")
	public ResponseEntity<RestObject> 
	deleteEmbeddedCluster(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterId") String clusterId) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedClusterRecord embeddedClusterRecord = embeddedDbRepo.getCluster(Long.parseLong(clusterId));
			embeddedDbRepo.deleteCluster(Long.parseLong(clusterId));
			return RestObject.retOKWithPayload(embeddedClusterRecord, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	/*******************************  Cluster Embedded Db Management  ***********************************/  
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/db:add", method = RequestMethod.PUT)
	@Operation(summary = "Add Embedded Databases to a cluster")
	public ResponseEntity<RestObject> 
	addEmbeddedDbToCluster( @RequestHeader(value="requestId") String requestId,
							@Valid @RequestBody EmbeddedDbRecord embeddedDbRecord) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			embeddedDbRepo.addEmbeddedDb(embeddedDbRecord);
			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(embeddedDbRecord.getClusterId());
			return RestObject.retOKWithPayload(r, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/db:get", method = RequestMethod.GET)
	@Operation(summary = "Get list of Embedded Databases for a cluster")
	public ResponseEntity<RestObject> 
	getEmbeddedDbToCluster(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterId") String clusterId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedDbRecordList r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId));
			return RestObject.retOKWithPayload(r, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/db:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Embedded Databases of a cluster")
	public ResponseEntity<RestObject> 
	deleteEmbeddedDbToCluster(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="dbId") String dbId,
								@RequestHeader(value="clusterId") String clusterId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedDbRecord e = embeddedDbRepo.getEmbeddedDb(Long.parseLong(clusterId), Long.parseLong(dbId));
			embeddedDbRepo.deleteEmbeddedDb(Long.parseLong(dbId));
			FileUtilWrapper.deleteFile(e.getPath() + ".mv.db");
			FileUtilWrapper.deleteFile(e.getPath() + ".trace.db");
			EmbeddedDbRecordList r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId));
			return RestObject.retOKWithPayload(r, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	/*************************************** Cluster access Management  ************************************************/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/access:add", method = RequestMethod.PUT)
	@Operation(summary = "Add user permission to cluster")
	public ResponseEntity<RestObject> 
	addUserAccessToCluster( @RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="userId") String userId,
							@RequestHeader(value="clusterId") String clusterId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			embeddedDbRepo.addEmbeddedDbAccess(Long.parseLong(clusterId), Long.parseLong(userId) );
			return RestObject.retOK(requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/access:get", method = RequestMethod.GET)
	@Operation(summary = "Get list of Embedded Databases for a cluster")
	public ResponseEntity<RestObject> 
	getUserAccessToCluster(	@RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterId") String clusterId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedClusterPermList r = embeddedDbRepo.getClusterPermission(Long.parseLong(clusterId));
			return RestObject.retOKWithPayload(r, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/access:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete User access to cluster")
	public ResponseEntity<RestObject> 
	deleteUserAccessToCluster(	@RequestHeader(value="requestId") String requestId,
								@RequestHeader(value="userId") String userId,
								@RequestHeader(value="clusterId") String clusterId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedDbRecordList r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId));
			embeddedDbRepo.deleteEmbeddedDbAccess(Long.parseLong(clusterId), Long.parseLong(userId));
			return RestObject.retOKWithPayload(r, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/database/schemas", method = RequestMethod.GET)
	@Operation(summary = "Get Schemas of an embedded db belonging to a cluster")
	public ResponseEntity<RestObject> 
	getSchemas(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="clusterId") String clusterId,
				@RequestHeader(value="dbId") String dbId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedDbRecordList r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId));
			Optional<EmbeddedDbRecord> db = r.getEmbeddedDbRecordList().stream().filter(c -> c.getDbId() == Long.parseLong(dbId) ).findFirst();
			DbConnectionInfo conn = DbConnectionInfo.makeH2ConnectionInfo(db.get().getFileName(), clusterId, H2Static.getUserName(), H2Static.getUserPassword());
			TableList ret = new TableList (SqlMetadataWrapper.getSchemas(conn));
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/database/tables", method = RequestMethod.GET)
	@Operation(summary = "Get Db Tables")
	public ResponseEntity<RestObject> 
	getTables(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="clusterId") String clusterId,
				@RequestHeader(value="dbId") String dbId,
				@RequestHeader(value="schema") String schema) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			EmbeddedDbRecordList r = embeddedDbRepo.getClusterEmbeddedDb(Long.parseLong(clusterId));
			Optional<EmbeddedDbRecord> db =	r.getEmbeddedDbRecordList()
												.stream()
												.filter(c -> c.getDbId() == Long.parseLong(dbId))
												.findFirst();
			TableList ret = new TableList();
			if(db.isEmpty())  return RestObject.retOKWithPayload(ret, requestId, methodName);
			if(db.get().getType().compareToIgnoreCase("H2") == 0) {
				H2Static h2 = new H2Static(Integer.parseInt(clusterId) , Integer.parseInt(dbId));
				List<String> uTables = h2.getUserTables(h2.getConnection());
				DbConnectionInfo conn = DbConnectionInfo.makeH2ConnectionInfo(db.get().getFileName(), clusterId, H2Static.getUserName(), H2Static.getUserPassword());
				List<String> tableList = SqlMetadataWrapper.getAllTableList(conn,"");
				List<String> filteredList = uTables	.stream()
													.filter(uTbl -> tableList.stream().anyMatch(uTbl::equals))
													.collect(Collectors.toList());
				ret = new TableList (filteredList);
			} else {
				ret = new TableList (new ArrayList<>());
			}

			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/execute/adhoc:ddl", method = RequestMethod.POST, consumes = "text/plain")
	@Operation(summary = "Execute Adhoc Ddl")
	public ResponseEntity<RestObject>
	executeDdl(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="clusterId") String clusterId,
				@RequestHeader(value="fileName") String fileName,
				@RequestHeader(value="type") String type,
				@RequestHeader(value="schema") String schema,
				@RequestBody String sqlContent)  {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

		try {
			EmbeddedClusterRecord rec = embeddedDbRepo.getCluster(Long.parseLong(clusterId));
			EmbeddedDbRecord embeddedDbRecord = new EmbeddedDbRecord(-1,
												                    fileName,
												                    "H2",
												                    0,
												                    Long.parseLong(clusterId),
												                    "",
												                    "");
			if(type.compareToIgnoreCase("H2") == 0) {
				
				DbConnectionInfo conn = DbConnectionInfo.makeH2ConnectionInfo(fileName, clusterId, H2Static.getUserName(), H2Static.getUserPassword());
				boolean isOK2 = H2Static.createUserTable(conn,sqlContent);
				if(isOK2) {
					embeddedDbRepo.addEmbeddedDb(embeddedDbRecord);
					EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(rec.getClusterId());
					return RestObject.retOKWithPayload(r, requestId, methodName);
				} else {
					return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Cannot execute DLL"));
				}
				
			} else {
				return RestObject.retException(requestId, methodName, AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Unknown Type"));
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 

	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/execute/adhoc/statement/table:create", method = RequestMethod.POST, consumes = "text/plain")
	@Operation(summary = "Execute Adhoc DDL")
	public ResponseEntity<RestObject>
	getCreateTableStmFromSql(@RequestHeader(value="requestId") String requestId,
							 @RequestBody String sqlContent) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			String createTblStm = SqlParser.getCreateTableStatementFromSql(sqlContent);
			return RestObject.retOKWithPayload(new GenericResponse(createTblStm), requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 

	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/embedded/cluster/db:new", method = RequestMethod.PUT)
	@Operation(summary = "Create an empty database as is part of a cluster ")
	public ResponseEntity<RestObject> 
	newEmbeddedDbToCluster( @RequestHeader(value="user") String user,
						    @RequestHeader(value="requestId") String requestId,
							@RequestHeader(value="clusterId") String clusterId,
							@RequestHeader(value="dbName") String dbName,
							@RequestHeader(value="dbType") String dbType) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			
			int clusterId_ = Integer.parseInt(clusterId);
			String path = H2Static.generateEmptySchema(clusterId_, dbName);

			
			EmbeddedDbRecordList  r = embeddedDbRepo.getClusterEmbeddedDb(clusterId_);
			if(r.getEmbeddedDbRecordList()	.stream()
											.noneMatch(x-> x.getFileName().equalsIgnoreCase(dbName))) {
				
				User u = authUtil.getUser(user);
                
				EmbeddedDbRecord rec = new EmbeddedDbRecord(-1,
															dbName,
															dbType,
									                        u.getId(),
									                        clusterId_,
									                        path, 
									                        "{}" /*info*/);
				
				embeddedDbRepo.addEmbeddedDb(rec);	
			}
			
			
			EmbeddedDbRecord ret = embeddedDbRepo.getEmbeddedDb(clusterId_, dbName);
			
			return RestObject.retOKWithPayload(ret, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	
}
