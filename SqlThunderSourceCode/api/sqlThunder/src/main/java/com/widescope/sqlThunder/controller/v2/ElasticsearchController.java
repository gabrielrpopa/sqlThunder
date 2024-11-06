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


import java.util.*;
import java.util.stream.Collectors;


import com.widescope.logging.AppLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpHost;
import org.json.simple.JSONObject;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.rest.GenericResponse;
import com.widescope.rest.JsonResponse;
import com.widescope.rest.RestObject;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQueryJsonRows;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.CreateIndexLowApi;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticLowLevelWrapper;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.SearchApi;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.SearchSql;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.ElasticQueryObj;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.ResponseElasticQuery;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationTableList;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.HitsInner;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticQuery.QueryInputWrapper;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticQuery.QueryType;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.management.IndexCharacteristic;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.management.IndexCharacteristicList;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.responses.ElasticResponse;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.sql.SqlResponse;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterList;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticHost;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticQuery;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticQueryList;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticQueryExec;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticQueryParam;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticQueryParamList;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.QueryToClusterBridgeList;
import com.widescope.rdbmsRepo.database.embeddedDb.elastic.ElasticParallelQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.elastic.ListElasticCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2InMem;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotElasticDbRepo;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbConnection;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
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
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.service.StorageService;




@CrossOrigin
@RestController
@Schema(title = "Elasticsearch Repo Control and Execution")
public class ElasticsearchController {
	
	@Autowired
	private AppConstants appConstants;
	
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private ElasticClusterDb elasticClusterDb;
	
	@Autowired
	private StorageService storageService;

	@PostConstruct
	public void initialize() {

	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo:list", method = RequestMethod.GET)
	@Operation(summary = "Query the Elastic Db Repository") 
	public ResponseEntity<RestObject> 
	elasticRepo(@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="uniqueName", required = false) final String uniqueName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(uniqueName);
			ElasticClusterList elasticClusterList = new ElasticClusterList(clusterMap);
			elasticClusterList.blockPassword();
			return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	
	   

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new Elastic cluster with all node connections") 
	public ResponseEntity<RestObject> 
	addElasticCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="clusterDescription") final String clusterDescription,
						@RequestBody String hostListStr)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<ElasticHost> hostList = null;
			ObjectMapper mapper = new ObjectMapper();
			try {
				hostList = mapper.readValue(hostListStr, new TypeReference<>(){});
			} catch(Exception ex) {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
			}
			
			
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.isEmpty()) {
				elasticClusterDb.mergeElasticCluster(clusterUniqueName, clusterDescription);
            } else {
				elasticClusterDb.deleteClusterHosts(clusterMap.get(clusterUniqueName).getClusterId());
            }
            if(hostList!=null && !hostList.isEmpty())
                elasticClusterDb.mergeElasticClusterHosts(hostList);

            Map<String, ElasticCluster> cMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			ElasticClusterList elasticClusterList = new ElasticClusterList(cMap);
			return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster:update", method = RequestMethod.POST)
	@Operation(summary = "Update current elastic cluster info (cluster name and description)") 
	public ResponseEntity<RestObject> 
	updateElasticCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterId") final String clusterId,
							@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
							@RequestHeader(value="clusterDescription") final String clusterDescription)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			List<ElasticHost> clusterMap = elasticClusterDb.getElasticClusterHosts(Integer.parseInt(clusterId) );
			if(!clusterMap.isEmpty()) {
				elasticClusterDb.mergeElasticCluster(Integer.parseInt(clusterId), clusterUniqueName, clusterDescription);
				Map<String, ElasticCluster> cMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
				ElasticClusterList elasticClusterList = new ElasticClusterList(cMap);
				return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove Elasticsearch server/cluster connection")
	public ResponseEntity<RestObject>
	removeElasticCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							 @RequestHeader(value="clusterUniqueName") final String clusterUniqueName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);

			if(clusterMap.size() == 1) {
				elasticClusterDb.deleteElasticCluster(clusterMap.get(clusterUniqueName).getClusterId());
				Map<String, ElasticCluster> cMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
				ElasticClusterList elasticClusterList = new ElasticClusterList(cMap);
				return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster cannot be removed");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster/host:add", method = RequestMethod.PUT)
    @Operation(summary = "Add a new host to an existing Elastic cluster") 
	public ResponseEntity<RestObject> 
	addElasticHost(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
					@RequestBody String hostStr)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticHost host = ElasticHost.toElasticHost(hostStr);
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				elasticClusterDb.addClusterHost(host);
				Map<String, ElasticCluster> cMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
				ElasticClusterList elasticClusterList = new ElasticClusterList(cMap);
				return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not existCannot add host");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster/host:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Elastic Host from Cluster ")
	public ResponseEntity<RestObject> 
	deleteElasticHost(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="hostId") final String hostId)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				elasticClusterDb.deleteClusterHost(clusterMap.get(clusterUniqueName).getClusterId(),  Integer.parseInt(hostId));
				Map<String, ElasticCluster> cMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
				ElasticClusterList elasticClusterList = new ElasticClusterList(cMap);
				return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cannot delete host");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster/host:update", method = RequestMethod.POST)
	@Operation(summary = "Update an existing Elastic cluster")
	public ResponseEntity<RestObject> 
	updateElasticHost(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestBody final ElasticHost host)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				elasticClusterDb.updateClusterHost(host);
				Map<String, ElasticCluster> cMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
				ElasticClusterList elasticClusterList = new ElasticClusterList(cMap);
				return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Could not update host");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	
	
	
	/////////////////////////////// Database Queries (Sql and DSL) management///////////////////////////////////////////
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new SQL/DSL statement to the repo")
	public ResponseEntity<RestObject> 
	addQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestBody final String queryObj) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticQuery elasticQuery = ElasticQuery.toElasticQuery(queryObj);

            assert elasticQuery != null;
            elasticClusterDb.insertQuery(elasticQuery.getVerb(),
										elasticQuery.getQueryReturnType(),
										elasticQuery.getQueryType(),
										elasticQuery.getElasticApi(),
										elasticQuery.getIndexName(),
										elasticQuery.getQueryCategory(),
										elasticQuery.getQueryName(),
										elasticQuery.getQueryDescription(),
										elasticQuery.getEndPoint(),
										elasticQuery.getQueryContent(),
										elasticQuery.getActive());
			
			
			List<ElasticQuery> lstQuery = elasticClusterDb.getExactQuery(elasticQuery.getQueryName());
			ElasticQueryList elasticQueryList = new ElasticQueryList(lstQuery);
			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete query statement against Elasticsearch cluster/server")
	public ResponseEntity<RestObject> 
	deleteElasticQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="queryId") final String queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteQuery(Integer.parseInt(queryId));
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:search", method = RequestMethod.GET)
	@Operation(summary = "Search Dsl/Sql statement by searching a keyword")
	public ResponseEntity<RestObject> 
	searchElasticQuery( @RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="stringToSearch") final String stringToSearch) {
		List<ElasticQuery> lstQuery;
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			int queryId_ = -1;
			if(!stringToSearch.isEmpty() && !stringToSearch.isBlank()) {
				try { queryId_ = Integer.parseInt(stringToSearch);	} catch(Exception ignored) { }
			}
			if(queryId_ > 0) {
				lstQuery = elasticClusterDb.getQuery(queryId_);
			} else {
				lstQuery = elasticClusterDb.getQuery(stringToSearch);
			}

			ElasticQueryList elasticQueryList = new ElasticQueryList(lstQuery);
			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:cluster", method = RequestMethod.GET)
	@Operation(summary = "Get the list of queries associated with a cluster")
	public ResponseEntity<RestObject> 
	getQueriesForCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterName") final String clusterName) {
		List<ElasticQuery> lstQuery;
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			lstQuery = elasticClusterDb.getQueriesForCluster(clusterName);
			for(ElasticQuery eQuery: lstQuery) {
				List<ElasticQueryParam> lstParams = elasticClusterDb.getQueryParams(eQuery.getQueryId());
				eQuery.setElasticQueryParamList(lstParams);
			}
			ElasticQueryList elasticQueryList = new ElasticQueryList(lstQuery);
			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:get", method = RequestMethod.GET)
	@Operation(summary = "Get the Dsl/Sql statement by searching a keyword")
	public ResponseEntity<RestObject> 
	getSpecificQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="queryId") final String queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			int qId = Integer.parseInt(queryId);
			ElasticQueryList elasticQueryList = new ElasticQueryList();
			List<ElasticQuery> lstDsl = elasticClusterDb.getQuery(qId);
			elasticQueryList.addElasticQueryLst(lstDsl);
			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/params:get", method = RequestMethod.GET)
	@Operation(summary = "Get all params of the query")
	public ResponseEntity<RestObject> 
	getElasticQueryParams(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final String queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(queryId == null || queryId.isBlank() || queryId.isEmpty()) {
				throw new Exception("queryId is null or empty");
			}
			
			int qId = Integer.parseInt(queryId);
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			List<ElasticQueryParam> lstQueryParam = elasticClusterDb.getQueryParams(qId);
			elasticQueryParamList.setElasticQueryParamLst(lstQueryParam);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/param:get", method = RequestMethod.GET)
	@Operation(summary = "Get param of the query by name")
	public ResponseEntity<RestObject> 
	getElasticQueryParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final String queryId,
							@RequestHeader(value="paramName") final String paramName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(queryId == null || queryId.isBlank() || queryId.isEmpty() ||	paramName == null || paramName.isBlank() || paramName.isEmpty() ) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Query Id or Param Name is null/empty")) ;
			}
			
			int qId = Integer.parseInt(queryId);
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			
			List<ElasticQueryParam> lstQueryParam = elasticClusterDb.getQueryParam(qId, paramName);
			elasticQueryParamList.setElasticQueryParamLst(lstQueryParam);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/param:add", method = RequestMethod.PUT)
	@Operation(summary = "Add query params", description= "")
	public ResponseEntity<RestObject> 
	addElasticQueryParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestBody final ElasticQueryParam elasticQueryParam) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.mergeQueryParam(	elasticQueryParam.getQueryId(),
												elasticQueryParam.getQueryParamName(),
												elasticQueryParam.getQueryParamDefault(), 
												elasticQueryParam.getQueryParamType(),
												elasticQueryParam.getQueryParamPosition(),
												elasticQueryParam.getQueryParamOrder());
			
			
			
			List<ElasticQueryParam> lstParams = elasticClusterDb.getQueryParam( elasticQueryParam.getQueryId(),
																				elasticQueryParam.getQueryParamName());
			
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			elasticQueryParamList.setElasticQueryParamLst(lstParams);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/param:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete query params") 
	public ResponseEntity<RestObject> 
	deleteElasticQueryParam(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final String queryId,
							@RequestHeader(value="paramId") final String paramId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteQueryParam(Integer.parseInt(queryId), Integer.parseInt(paramId) );
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			List<ElasticQueryParam> lstQueryParam = elasticClusterDb.getQueryParams(Integer.parseInt(queryId));
			elasticQueryParamList.setElasticQueryParamLst(lstQueryParam);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/signature", method = RequestMethod.GET)
	@Operation(summary = "Get Input Object for Query execution") 
	public ResponseEntity<RestObject> 
	getQueryInputObject(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			QueryInputWrapper queryInputWrapper = new QueryInputWrapper();
			return RestObject.retOKWithPayload(queryInputWrapper, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	/////////////////////// Query to Cluster Bridges ////////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/bridge:get", method = RequestMethod.GET)
	@Operation(summary = "Set Query Bridge To Cluster") 
	public ResponseEntity<RestObject> 
	getQueryBridgeToCluster(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final String queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			QueryToClusterBridgeList queryToClusterBridgeList = new QueryToClusterBridgeList(elasticClusterDb.getQueryToClusterBridge(Integer.parseInt(queryId)));
			return RestObject.retOKWithPayload(queryToClusterBridgeList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/bridge:add", method = RequestMethod.PUT)
	@Operation(summary = "Set Query Bridge To Cluster") 
	public ResponseEntity<RestObject> 
	addQueryBridgeToCluster(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final String queryId,
							@RequestHeader(value="clusterId") final String clusterId,
							@RequestHeader(value="active", defaultValue = "1") String active) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			long queryId_ = Integer.parseInt(queryId);
			long clusterId_ = Integer.parseInt(clusterId);
			int active_ = Integer.parseInt(active);
			elasticClusterDb.mergeQueryToClusterBridge(queryId_, clusterId_, active_ );
			
			QueryToClusterBridgeList queryToClusterBridgeList = new QueryToClusterBridgeList(elasticClusterDb.getQueryToClusterBridge(Integer.parseInt(queryId)));
			return RestObject.retOKWithPayload(queryToClusterBridgeList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/bridge:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Query Bridge To Cluster") 
	public ResponseEntity<RestObject> 
	deleteQueryBridgeToCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="queryId") final String queryId,
								@RequestHeader(value="clusterId") final String clusterId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteActiveQueryToClusterBridge(Integer.parseInt(queryId), Integer.parseInt(clusterId));
			QueryToClusterBridgeList queryToClusterBridgeList = new QueryToClusterBridgeList(elasticClusterDb.getQueryToClusterBridge(Integer.parseInt(queryId)));
			return RestObject.retOKWithPayload(queryToClusterBridgeList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	//////////////////////  Elastic Specific Operations////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index:create", method = RequestMethod.PUT)
	@Operation(summary = "Create Elasticsearch Index") 
	public ResponseEntity<RestObject> 
	createIndex(@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="indexName") final String indexName,
				@RequestHeader(value="numberOfShards") final String numberOfShards,
				@RequestHeader(value="numberOfReplicas") final String numberOfReplicas) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				if(CreateIndexLowApi.indexExists(elasticLowLevelWrapper, indexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index already exists");
				}
				
								
				boolean isCreated 
				= CreateIndexLowApi.createIndex( elasticLowLevelWrapper, 
												indexName,
												Integer.parseInt(numberOfShards) ,
												Integer.parseInt(numberOfReplicas));

				elasticLowLevelWrapper.disconnect();
				if(isCreated)
					return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				else
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cannot create index");
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster/index:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Remove elasticsearch index")
	public ResponseEntity<RestObject>
	deleteElasticIndex(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
					   @RequestHeader(value="indexName") final String indexName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				boolean ret = CreateIndexLowApi.indexDelete(elasticLowLevelWrapper,	indexName);
				elasticLowLevelWrapper.disconnect();

				if(ret)	{
					return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				} else {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index cannot be removed");
				}
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}

		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



	/** Copy to Elastic Operations*/ 
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/embedded:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy to index from an Embedded Db query")
	public ResponseEntity<RestObject> 
	copyFromEmbeddedQueryToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
									@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
									@RequestHeader(value="fromClusterId") final String fromClusterId,
									@RequestHeader(value="fromEmbeddedDatabaseName") final String fromEmbeddedDatabaseName,
									@RequestHeader(value="fromEmbeddedSchemaName") final String fromEmbeddedSchemaName,
									@RequestBody String sqlContent) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist, Please create one for this operation");
				}
				
				H2Static h2Db = new H2Static(Long.parseLong(fromClusterId), fromEmbeddedDatabaseName );
				TableFormatMap recordSet = h2Db.execStaticQueryWithTableFormat(sqlContent);
				
				if(recordSet.getRows().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retOKWithPayload("Sql statement generates 0 records", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
				

				String lastRecId = SearchApi.getDocIDLastOrFirstInserted(elasticLowLevelWrapper,toElasticIndexName, true);
				long startWith = Long.parseLong(lastRecId);
				long noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, recordSet.getListOfRows(), startWith);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/rdbms:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy to index from an RDBMS query")
	public ResponseEntity<RestObject> 
	copyFromRDBMSQueryToElastic(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
								@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
								@RequestHeader(value="fromRdbmsSchemaName") final String fromRdbmsSchemaName,
								@RequestBody final String sqlContent) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist, Please create one for this operation");
				}
				
				ResultQueryJsonRows resultQuery = SqlQueryRepoUtils.execStaticQueryWithJsonRows(fromRdbmsSchemaName, sqlContent);
				
				if(resultQuery.getResultQueryRows().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retOKWithPayload("Sql statement generates 0 records", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
				
				long startWith = 0;
				String lastRecirdId = "0";
				try {
					lastRecirdId = SearchApi.getDocIDLastOrFirstInserted(elasticLowLevelWrapper,toElasticIndexName, true);	
					startWith = Long.parseLong(lastRecirdId);
				} catch(Exception ignored) {}
				long noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, resultQuery.getResultQueryRows(), startWith);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo/adhoc:mql", method = RequestMethod.PUT)
	@Operation(summary = "Copy to index from Mongo simple search")
	public ResponseEntity<RestObject> 
	copyFromMongoAdhocToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
									@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
									@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
									@RequestHeader(value="batchValue", defaultValue = "0") final String batchValue,
									@RequestBody String sqlContent) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exists. Please create one for this operation");
				}
				
				MongoResultSet ret = MongoGet.execDynamicQuery(	fromMongoClusterName, 
																fromMongoDatabaseName, 
																fromMongoCollectionName,
																sqlContent,
																true);
			

				if(ret.getResultSet().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Collection " +
																			fromMongoCollectionName + 
																			" is empty, 0 documents added to index " + 
																			toElasticIndexName);
				}

				long startWith = 0;
				long noDocsAdded = 0;
				int batch = Integer.parseInt(batchValue);
				if(batch <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticIndexName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), batch);						

				
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy to index from Mongo simple search")
	public ResponseEntity<RestObject> 
	copyFromMongoSimpleQueryToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
										@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
										@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
										@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
										@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
										@RequestHeader(value="itemToSearch") final String itemToSearch,
										@RequestHeader(value="valueToSearch") final String valueToSearch,
										@RequestHeader(value="valueToSearchType") final String valueToSearchType,
										@RequestHeader(value="operator", defaultValue = "$eq") final String operator,
										@RequestHeader(value="batchValue", defaultValue = "0") final String batchValue) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exists. Please create one for this operation");
				}
				
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				MongoResultSet ret = MongoGet.searchDocument(mongoDbConnection, 
																		fromMongoDatabaseName, 
																		fromMongoCollectionName,
																		itemToSearch,  
																		valueToSearch,
																		operator,
																		valueToSearchType,
																		true,
																		false);  /*determine metadata is false*/
				if(ret.getResultSet().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Collection " +
																			fromMongoCollectionName + 
																			" is empty, 0 documents added to index " + 
																			toElasticIndexName);
				}

				long startWith = 0;
				long noDocsAdded = 0;
				int batch = Integer.parseInt(batchValue);
				if(batch <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticIndexName,MongoResultSet.getResultSetAsJson(ret.getResultSet()), batch);						

				
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy to Elastic Index from a Mongo range search")
	public ResponseEntity<RestObject> 
	copyFromMongoRangeToElastic(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
								@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
								@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
								@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
								@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
								@RequestHeader(value="itemToSearch") final String itemToSearch,
								@RequestHeader(value="fromValue") final String fromValue,
								@RequestHeader(value="toValue") final String toValue,
								@RequestHeader(value="valueSearchType") final String valueSearchType,
								@RequestHeader(value="batchValue", defaultValue = "0") final String batchValue) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one for this operation");
				}
				
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				MongoResultSet ret = MongoGet.searchDocumentRange(	mongoDbConnection, 
																				fromMongoDatabaseName, 
																				fromMongoCollectionName,
																				itemToSearch,  
																				fromValue,
																				toValue,
																				valueSearchType,
																				true
																			) ;
				
				if(ret.getResultSet().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Collection " +
																			fromMongoCollectionName + 
																			" is empty, 0 documents added to index " + 
																			toElasticClusterName);
				}

				long startWith = 0;
				long noDocsAdded = 0;
				int batch = Integer.parseInt(batchValue);
				if(batch <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticClusterName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticClusterName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), batch);						

				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy from Mongo collection to Elastic") 
	public ResponseEntity<RestObject> 
	copyFromMongoFullCollectionToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
											@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
											@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
											@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
											@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
											@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
											@RequestHeader(value="batchCount", defaultValue = "0") final String batchCount) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}
				
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(fromMongoClusterName);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
				List<String> mongoResultSet = MongoGet.getAllCollectionDocumentsAsPlainInList(	mongoDbConnection, 
																								fromMongoDatabaseName, 
																								fromMongoCollectionName,
																								true
																								) ;
				if(mongoResultSet.isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					String message = "Collection " + fromMongoDatabaseName + " is empty, 0 documents added to index " + toElasticIndexName;
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(),message);
				}
	
				long noDocsAdded = 0;
				int batch = Integer.parseInt(batchCount);
				if(batch <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, mongoResultSet, 0);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticIndexName, mongoResultSet, batch);						

				
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse("Added " + noDocsAdded + " documents"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/elastic:dsl", method = RequestMethod.PUT)
	@Operation(summary = "Copy to Elastic Index from another Elastic Dsl query")
	public ResponseEntity<RestObject> 
	copyElasticToElasticViaDsl(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="fromElasticClusterName") final String fromElasticClusterName,
								@RequestHeader(value="fromIndexName") final String fromIndexName,
								@RequestHeader(value="fromHttpVerb") final String fromHttpVerb,
								@RequestHeader(value="fromElasticApi") final String fromElasticApi,
								@RequestHeader(value="fromEndPoint") final String fromEndPoint,
								@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
								@RequestHeader(value="toIndexName") final String toIndexName,
								@RequestHeader(value="batchValue", required = false, defaultValue = "0") final String batchValue,
								@RequestBody (required = false) final String fromHttpPayload) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] toHttpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper toElasticLowLevelWrapper = new ElasticLowLevelWrapper(toHttpHostArray);
				
				if(!CreateIndexLowApi.indexExists(toElasticLowLevelWrapper, toIndexName)) {
					toElasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}
				
			
				List<String> elasticFromResultSet = new ArrayList<>();
				
				Map<String, ElasticCluster> fromClusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
				if(fromClusterMap.size() == 1) {
					HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(fromClusterMap, fromElasticClusterName);
					ElasticLowLevelWrapper fromElasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
					Object ret = ElasticInfo.executeGeneric(	fromElasticLowLevelWrapper, 
																fromHttpVerb, 
																fromEndPoint, 
																"Y", 
																fromHttpPayload);		
					fromElasticLowLevelWrapper.disconnect();
					JSONObject jsonObject = JsonResponse.getJsonObj(ret);
					String str = Objects.requireNonNull(ElasticPayload.toElasticPayload(jsonObject.toJSONString())).getHits().getHits().toJSONString();
					HitsInner[] hitsInner = HitsInner.toListHitsInner(str);
                    assert hitsInner != null;
                    for (HitsInner temp : hitsInner ) {
			            elasticFromResultSet.add(temp.get_source().toJSONString());
			        }
				}

				long startWith = 0;
				long noDocsAdded = 0;
				
				
				int batch = Integer.parseInt(batchValue);
				if(batch <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, batch);						

				
				toElasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/elastic:sql", method = RequestMethod.PUT)
	@Operation(summary = "Copy to Elastic Index from another Elastic Sql query")
	public ResponseEntity<RestObject> 
	copyElasticToElasticViaSql(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="fromElasticClusterName") final String fromElasticClusterName,
								@RequestHeader(value="fromIndexName") final String fromIndexName,
								@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
								@RequestHeader(value="toIndexName") final String toIndexName,
								@RequestHeader(value="fetchSize") final int fetchSize,
								@RequestHeader(value="batchValue", required = false) final int batchValue,
								@RequestBody (required = false) final String sqlPayload) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] toHttpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper toElasticLowLevelWrapper = new ElasticLowLevelWrapper(toHttpHostArray);
				
				if(!CreateIndexLowApi.indexExists(toElasticLowLevelWrapper, toIndexName)) {
					toElasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}

				List<String> elasticFromResultSet;
				
				Map<String, ElasticCluster> fromClusterMap = elasticClusterDb.getElasticCluster(fromElasticClusterName);
				if(fromClusterMap.size() == 1) {
					HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
					ElasticLowLevelWrapper fromElasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
					JSONObject ret = SearchSql.searchSql(fromElasticLowLevelWrapper, sqlPayload, fetchSize);
					elasticFromResultSet = Objects.requireNonNull(SqlResponse.toSqlResponse(ret.toJSONString())).toListOfJsonStrings();

					fromElasticLowLevelWrapper.disconnect();
					JSONObject jsonObject = JsonResponse.getJsonObj(ret);
					String str = Objects.requireNonNull(ElasticPayload.toElasticPayload(jsonObject.toJSONString())).getHits().getHits().toJSONString();
					HitsInner[] hitsInner = HitsInner.toListHitsInner(str);
                    assert hitsInner != null;
                    for (HitsInner temp : hitsInner ) {
			            elasticFromResultSet.add(temp.get_source().toJSONString());
					}
				} else {
					elasticFromResultSet = new ArrayList<>();
				}

				long startWith = 0;
				long noDocsAdded = 0;

				if(batchValue <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, batchValue);

				
				toElasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv to Elastic Index") 
	public ResponseEntity<RestObject> 
	copyCsvToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="fileType", defaultValue = "text/csv") final String fileType,
						@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
						@RequestHeader(value="toIndexName") final String toIndexName,
						@RequestParam("file") final MultipartFile file) {
		String fileName = StringUtils.generateUniqueString32();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] toHttpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper toElasticLowLevelWrapper = new ElasticLowLevelWrapper(toHttpHostArray);
				
				if(!CreateIndexLowApi.indexExists(toElasticLowLevelWrapper, toIndexName)) {
					toElasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}
				
				String isCompressed = "N";
				if( fileType.equals("text/csv") ) {
					isCompressed = "N";
				} else if( fileType.equals("application/x-zip-compressed")) {
					isCompressed = "Y";
				} else {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Unknown File Type");
				}
				
				String fullFilePath = storageService.storeTmp(file, fileName);
				String csvContent = CsvWrapper.readFile(fullFilePath);
				List<String> jList = CsvWrapper.stringToJsonList(csvContent,isCompressed);

				long noDocsAdded = 
						CreateIndexLowApi.addDocumentToIndex(	toElasticLowLevelWrapper, 
																toIndexName,
																jList,
																0);
				
				toElasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
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
	@RequestMapping(value = "/elastic-repo/execute/adhoc/multiple:aggregate", method = RequestMethod.PUT, consumes = "text/plain")
	@Operation(summary = "Execute Sql or Dsl on multiple clusters / indexes and aggregate results with Sql") 
	public ResponseEntity<RestObject> 
	executeAdhocMultipleIndex(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="session") final String session,
								@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestBody final String strObj)  {
		ListElasticCompoundQuery listElasticCompoundQuery = ListElasticCompoundQuery.toListElasticCompoundQuery(strObj);
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
            assert listElasticCompoundQuery != null;
            List<RdbmsTableSetup> lst = ElasticParallelQuery.executeElasticQueryInParallel( listElasticCompoundQuery.getLst(),  listElasticCompoundQuery.getTableName());
			H2InMem h2InMem = new H2InMem("", inMemDbName, "QUERY", session, requestId, user);
			h2InMem.loadRdbmsQueriesInMem(lst);
			ResultQuery ret  = SqlQueryExecUtils.execStaticQuery(h2InMem.getConnection(), listElasticCompoundQuery.getSqlAggregator());
			h2InMem.removeInMemDb();
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/dsl:fuzzy", method = RequestMethod.POST)
	@Operation(summary = "Query index via native DSL")
	public ResponseEntity<RestObject> 
	searchFuzzyIndex(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="indexName") final String indexName,
						@RequestHeader(value="fromRecno") final Integer fromRecno,
						@RequestHeader(value="size") final Integer size,
						@RequestBody final QueryType queryType) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				List<JSONObject> docList = SearchApi.searchFuzzy(elasticLowLevelWrapper, 
																indexName, 
																queryType, 
																fromRecno, 
																size);
				ResponseElasticQuery responseElasticQuery = new ResponseElasticQuery();
				responseElasticQuery.setDocs(docList);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(responseElasticQuery, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/management:list", method = RequestMethod.GET)
	@Operation(summary = "List of indexes") 
	public ResponseEntity<RestObject> 
	listIndexes(@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="indexName", required = false) String indexName) throws Exception {

		if(clusterUniqueName.isEmpty()) {
			throw new Exception ("Empty Cluster Name provided");
		}
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			indexName = (indexName == null ? "" : indexName);
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				List<IndexCharacteristic> docList = SearchApi.listIndexes(elasticLowLevelWrapper, indexName);
				 
				Comparator<IndexCharacteristic> compareByTypeThenIndexName 
				= Comparator.comparing( IndexCharacteristic::getType).thenComparing( IndexCharacteristic::getIndex );
				
				List<IndexCharacteristic> sortedIndex 
				= docList.stream().sorted(compareByTypeThenIndexName).collect(Collectors.toList());
				
				IndexCharacteristicList indexCharacteristicList = new IndexCharacteristicList(sortedIndex);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(indexCharacteristicList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/mapping:update", method = RequestMethod.POST)
	@Operation(summary = "Update index mapping/properties") 
	public ResponseEntity<RestObject> 
	updateIndexMapping(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="indexName") final String indexName,
						@RequestBody final JSONObject properties) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				CreateIndexLowApi.updateRawMappingIndex(elasticLowLevelWrapper, indexName, properties);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/sql:translate", method = RequestMethod.POST)
	@Operation(summary = "Translate Sql to Dsl") 
	public ResponseEntity<RestObject> 
	translateSqlToDsl(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestBody final String sqlContent) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				JSONObject ret = SearchSql.translateSql(elasticLowLevelWrapper, sqlContent);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(new JsonResponse(ret), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	

	////////////////////////////  RUN Queries  /////////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/dsl/adhoc:run", method = RequestMethod.POST)
	@Operation(summary = "Execute a generic adhoc Dsl query") 
	public ResponseEntity<RestObject> 
	runAdhocDsl(@RequestHeader(value="user") String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="httpVerb") final String httpVerb,
				@RequestHeader(value="elasticApi") final String elasticApi,
				@RequestHeader(value="indexName") final String indexName,
				@RequestHeader(value="endPoint") final String endPoint,
				@RequestHeader(value="isOriginalFormat") final String isOriginalFormat,
				@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				@RequestHeader(value="comment", required = false) final String comment,
				@RequestHeader(value="sqlName") final String sqlName,
				@RequestBody (required = false) final String httpPayload) {

		requestId = StringUtils.generateRequestId(requestId);
		if( !ElasticClusterDb.isElasticApi(elasticApi) || !ElasticClusterDb.isVerb(httpVerb)) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "No conforming api");
		}

		try	{
			User u = authUtil.getUser(user);
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				Object ret = ElasticInfo.executeGeneric(	elasticLowLevelWrapper, 
															httpVerb, 
															endPoint, 
															isOriginalFormat, 
															httpPayload);		
				elasticLowLevelWrapper.disconnect();
				JSONObject jsonObject = JsonResponse.getJsonObj(ret);
				ElasticResponse response
				= new ElasticResponse(	httpVerb,
										elasticApi,
										"DSL",
										indexName,
										jsonObject);
				
				if( persist.compareToIgnoreCase("Y") == 0 ) {
					String folder = "./snapshots/" + u.getId();
					try {
						String fileName = StringUtils.generateUniqueString32();
						boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, response.toString());
						if(isOk) {
							long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
							Map<String, String> map = new HashMap<>();
							map.put("httpPayload", httpPayload);
							map.put("clusterUniqueName", clusterUniqueName);
							map.put("httpVerb", httpVerb);
							map.put("elasticApi", elasticApi);
							map.put("indexName", indexName);
							map.put("endPoint", endPoint);
							if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
								map.put("comment", comment);
							}
							JSONObject jo = new JSONObject(map);
							
							ElasticQueryObj queryObj = new ElasticQueryObj("DSL", sqlName, jo); 
							SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "ResultQuery", u.getId(), timestamp, queryObj.toString());
							SnapshotElasticDbRepo snapshotDbRepo = new SnapshotElasticDbRepo();
							snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
							long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
							snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
						}
					} catch(Exception ex) {
						throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
					}
				}

				long tStamp = DateTimeUtils.millisecondsSinceEpoch();
				String mainFolder = appConstants.getHistStatementPath();
				HistFileManagement.addNewStatement(u.getId(), httpPayload, comment, tStamp, mainFolder, "adhoc", "es");
				return RestObject.retOKWithPayload(response, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/sql/adhoc:run", method = RequestMethod.POST)
	@Operation(summary = "Execute an adhoc SQL statement against an index") 
	public ResponseEntity<RestObject> 
	runAdhocSql(@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="fetchSize") final Integer fetchSize,
				@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				@RequestHeader(value="comment", required = false) final String comment,
				@RequestHeader(value="sqlName") final String sqlName,
				@RequestBody final String sqlContent) {
		try	{
			requestId = StringUtils.generateRequestId(requestId);
			User u = authUtil.getUser(user);
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticCluster(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				JSONObject ret = SearchSql.searchSql(elasticLowLevelWrapper, sqlContent, fetchSize);
				elasticLowLevelWrapper.disconnect();

				ElasticResponse response
				= new ElasticResponse(	"",
										"",
										"SQL",
										"",
										ret);
				
				if( persist.compareToIgnoreCase("Y") == 0 ) {
					String folder = "./snapshots/" + u.getId();
					try {
						String fileName = StringUtils.generateUniqueString32();
						boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, response.toString());
						if(isOk) {
							long timestamp = com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch() ;
							Map<String, String> map = new HashMap<>();
							map.put("sqlContent", sqlContent);
							map.put("clusterUniqueName", clusterUniqueName);

							if(comment!=null && !comment.isEmpty() && !comment.isBlank()) {
								map.put("comment", comment);
							}
							JSONObject jo = new JSONObject(map);
							ElasticQueryObj queryObj = new ElasticQueryObj("SQL", sqlName, jo); 
							SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord(0,	fileName, sqlName, "ResultQuery", u.getId(), timestamp, queryObj.toString());
							SnapshotElasticDbRepo snapshotDbRepo = new SnapshotElasticDbRepo();
							snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
							long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
							snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
						}
					} catch(Exception ex) {
						AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
					}
				}
				
				long tStamp = DateTimeUtils.millisecondsSinceEpoch();
				String mainFolder = appConstants.getHistStatementPath();
				HistFileManagement.addNewStatement(u.getId(), sqlContent, comment, tStamp, mainFolder, "adhoc", "es");
				
				return RestObject.retOKWithPayload(response, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	/*To-Be-Completed in the next iteration*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/query:run", method = RequestMethod.POST)
	@Operation(summary = "Execute a repo elastic query")
	public ResponseEntity<RestObject> 
	runElasticQueryFromRepo(@RequestHeader(value="user") final String user,
							@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
							@RequestHeader(value="queryId") final String queryId,
							@RequestHeader(value="queryType") final String queryType,
							@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
							@RequestHeader(value="comment", required = false) final String comment,
							@RequestBody String paramObj) {
		requestId = StringUtils.generateRequestId(requestId);
		ElasticQueryList elasticQueryList = new ElasticQueryList();
		ElasticQueryExec elasticQueryExec = null;
		try { elasticQueryExec = ElasticQueryExec.toElasticQueryExec(paramObj); } finally {}

		try	{
			if(elasticQueryExec == null) {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "ERROR_BODY");
			}

			if( persist.compareToIgnoreCase("Y") == 0 ) {
				User u = authUtil.getUser(user);
				String folder = "./snapshots/" + u.getId();
				try {
					String fileName = StringUtils.generateUniqueString32();
					boolean isOk = FileUtilWrapper.overwriteFile(folder, fileName, elasticQueryList.toString());
					if(isOk) {
						Map<String, String> map = new HashMap<>();
						map.put("paramObj", paramObj);
						JSONObject jo = new JSONObject(map);
						//ElasticQueryObj queryObj = new ElasticQueryObj("REPO", clusterUniqueName, jo);
						SnapshotDbRecord snapshotDbRecord = new SnapshotDbRecord();
						SnapshotElasticDbRepo snapshotDbRepo = new SnapshotElasticDbRepo();
						snapshotDbRepo.addSnapshotDb(snapshotDbRecord);
						long id = snapshotDbRepo.getSnapshot(snapshotDbRecord.getTimestamp(), snapshotDbRecord.getFileName());
						snapshotDbRepo.addSnapshotDbAccess(id, u.getId());
					}
				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
				}
			}


			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/*                    SQL Snapshots                                     */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/snapshot:history", method = RequestMethod.GET)
	@Operation(summary = "Get a list of snapshots to visualize") 
	public ResponseEntity<RestObject> 
	getElasticSnapshotHistory(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="ownerId") final String ownerId,
								@RequestHeader(value="startTime") final String startTime,
								@RequestHeader(value="endTime") final String endTime,
								@RequestHeader(value="sqlStatement", required = false) final String sqlStatement) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SnapshotElasticDbRepo snp = new SnapshotElasticDbRepo();
			SnapshotDbRecordList ret = snp.getUserSnapshotDb(	Long.parseLong(ownerId), 
																Long.parseLong(startTime),
																Long.parseLong(endTime),
																sqlStatement);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/snapshot:get", method = RequestMethod.GET)
	@Operation(summary = "Get snapshot to visualize") 
	public ResponseEntity<RestObject> 
	getElasticSnapshot(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="snapshotId") final String snapshotId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SnapshotElasticDbRepo snp = new SnapshotElasticDbRepo();
			SnapshotDbRecord snapshotDbRecord = snp.getSnapshot(Long.parseLong(snapshotId));
			String filePath = "./snapshots/" + snapshotDbRecord.getUserId() + "/" + snapshotDbRecord.getFileName();
			String fileContent = new String(FileUtilWrapper.readFile(filePath));
			ElasticResponse elasticResponseSql = ElasticResponse.toElasticResponse(fileContent);
			return RestObject.retOKWithPayload(elasticResponseSql, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/snapshot:delete", method = RequestMethod.POST)
	@Operation(summary = "Delete snapshot") 
	public ResponseEntity<RestObject> 
	deleteElasticSnapshot(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="snapshotId") final String snapshotId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			SnapshotElasticDbRepo snp = new SnapshotElasticDbRepo();
			SnapshotDbRecord snapshotDbRecord = snp.getSnapshot(Long.parseLong(snapshotId));
			snp.deleteSnapshotDbAccessForSnapshot(Long.parseLong(snapshotId));
			snp.deleteSnapshotDb(Long.parseLong(snapshotId));
			String filePath = "./snapshots/" + snapshotDbRecord.getUserId() + "/" + snapshotDbRecord.getFileName();
			FileUtilWrapper.deleteFile(filePath);
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	/*  Associations*/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:get", method = RequestMethod.GET)
	@Operation(summary = "Get elastic repo associations")
	public ResponseEntity<RestObject> 
	getElasticRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationName") final String associationName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			RepoAssociationTableList repoAssociationTableList = elasticClusterDb.getRepoAssociationTable( associationName );
			return RestObject.retOKWithPayload(repoAssociationTableList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:add", method = RequestMethod.PUT)
	@Operation(summary = "Add elastic repo association")
	public ResponseEntity<RestObject> 
	addElasticRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationName") final String associationName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.insertRepoAssociationTable(associationName);
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:update", method = RequestMethod.PUT)
	@Operation(summary = "Update elastic repo association")
	public ResponseEntity<RestObject> 
	updateElasticRepoAssociation(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationId") final String associationId,
									@RequestHeader(value="associationName") final String associationName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.updateRepoAssociationTable(Integer.parseInt(associationId) , associationName);
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete elastic repo association")
	public ResponseEntity<RestObject> 
	deleteElasticRepoAssociation(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationId") final String associationId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteRepoAssociationTable(Integer.parseInt(associationId));
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	/*History */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/history/stm:get", method = RequestMethod.GET)
	@Operation(summary = "Get the List of executed sql statements") 
	public ResponseEntity<RestObject> 
	getElasticHistStm(	@RequestHeader(value="user") final String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="type") final String type,
						@RequestHeader(value="stext", required = false, defaultValue = "") final String sText) {
		List<HistoryStatement> lstSql = new ArrayList<>();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			
			String mainFolder = appConstants.getHistStatementPath();
			List<String> lstSha = HistFileManagement.getStmts(userId, mainFolder, type, "es");
			for(String s: lstSha) {
				HistoryStatement t = HistFileManagement.getStm_(userId, s, mainFolder, type, "es");
				if(sText != null && !sText.isBlank() && !sText.isEmpty()) {
					if( t.getContent().contains(sText) )
						lstSql.add(t);	
				} else {
					lstSql.add(t);
				}
			}
			HistSqlList ret =  HistSqlList.getTempSqlList(lstSql);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/history/stm:copy", method = RequestMethod.POST)
	@Operation(summary = "Copy sql statements to another user") 
	public ResponseEntity<RestObject> 
	copyEsHistStm(	@RequestHeader(value="user") final String user,
				  	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="toUserId") final String toUserId,
					@RequestHeader(value="shaHash") final String shaHash,
					@RequestHeader(value="type") final String type) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			HistFileManagement.addExistingStmToNewUser(userId, Long.parseLong(toUserId), shaHash, mainFolder, type, "es");
			return RestObject.retOKWithPayload(new GenericResponse("OK"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/history/stm:remove", method = RequestMethod.DELETE)
	@Operation(summary = "Delete an executed sql statement from your profile") 
	public ResponseEntity<RestObject> 
	deleteEsHistStmt(	@RequestHeader(value="user") String user,
						@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="shaHash") String shaHash,
						@RequestHeader(value="type") String type) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			User u = authUtil.getUser(user);
			long userId = u.getId();
			String mainFolder = appConstants.getHistStatementPath();
			boolean ret = HistFileManagement.deleteStatement(userId, shaHash, mainFolder, type, "es");
			return RestObject.retOKWithPayload(new GenericResponse(String.valueOf(ret)), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	
}
