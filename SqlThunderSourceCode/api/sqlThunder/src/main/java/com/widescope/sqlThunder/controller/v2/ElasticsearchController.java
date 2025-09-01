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
import com.widescope.persistence.Persistence;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.associations.RepoAssociationTable;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.*;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.widescope.sqlThunder.rest.JsonResponse;
import com.widescope.sqlThunder.rest.RestObject;
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
import com.widescope.rdbmsRepo.database.embeddedDb.elastic.ElasticParallelQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.elastic.ListElasticCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2InMem;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbConnection;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.structuredFiles.csv.CsvWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.service.StorageService;




@CrossOrigin
@RestController
@Schema(title = "Elasticsearch Repo Control and Execution")
public class ElasticsearchController {

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private ElasticClusterDb elasticClusterDb;
	
	@Autowired
	private StorageService storageService;

	@Autowired
	private ElasticExecutedQueriesRepoDb executedQueriesRepoDb;

	@Autowired
	private Persistence persistence;

	@Autowired
	private PersistenceWrap pWrap;

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
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(uniqueName);
			ElasticClusterList elasticClusterList = new ElasticClusterList(clusterMap);
			elasticClusterList.blockPassword();
			return RestObject.retOKWithPayload(elasticClusterList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	
	
	   

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new Elastic cluster with all node connections") 
	public ResponseEntity<RestObject> 
	addElasticCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="clusterDescription") final String clusterDescription,
						@RequestBody ElasticHostList hostList)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.UpdateElasticCluster(clusterUniqueName, clusterDescription);
			ElasticCluster cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			if(hostList!=null && !hostList.getElasticHostLst().isEmpty()) {
				for (ElasticHost eh: hostList.getElasticHostLst()) {
					eh.setClusterId(cluster.getClusterId());
					elasticClusterDb.insertClusterHost(eh);
				}
			}
			cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			return RestObject.retOKWithPayload(cluster, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster:update", method = RequestMethod.POST)
	@Operation(summary = "Update current elastic cluster info (cluster name and description)") 
	public ResponseEntity<RestObject> 
	updateElasticCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="clusterId") final Integer clusterId,
							@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
							@RequestHeader(value="clusterDescription") final String clusterDescription)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.UpdateElasticCluster(clusterId, clusterUniqueName, clusterDescription);
			ElasticCluster cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			return RestObject.retOKWithPayload(cluster, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			ElasticCluster cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			elasticClusterDb.deleteActiveQueryToClusterBridge(cluster.getClusterId());
			elasticClusterDb.deleteClusterHosts(cluster.getClusterId());
			elasticClusterDb.deleteElasticCluster(cluster.getClusterId());
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster/host:add", method = RequestMethod.PUT)
    @Operation(summary = "Add a new host to an existing Elastic cluster") 
	public ResponseEntity<RestObject> 
	addElasticHost(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
					@RequestBody ElasticHost hostStr)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.mergeClusterHost(hostStr);
			ElasticCluster cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			return RestObject.retOKWithPayload(cluster, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/cluster/host:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Elastic Host from Cluster ")
	public ResponseEntity<RestObject> 
	deleteElasticHost(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="hostId") final int hostId)	{
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticCluster cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			elasticClusterDb.deleteClusterHost(cluster.getClusterId(),  hostId);
			cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			return RestObject.retOKWithPayload(cluster, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			elasticClusterDb.updateClusterHost(host);
			ElasticCluster cluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
			return RestObject.retOKWithPayload(cluster, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	

	
	
	
	/////////////////////////////// Database Queries (Sql and DSL) management///////////////////////////////////////////
	
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:add", method = RequestMethod.PUT)
	@Operation(summary = "Add a new SQL/DSL statement to the repo")
	public ResponseEntity<RestObject> 
	addElasticQuery(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestBody final ElasticQuery elasticQuery) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
            assert elasticQuery != null;
            elasticClusterDb.insertQuery(elasticQuery);
			ElasticQuery query = elasticClusterDb.getQueryByName(elasticQuery.getQueryName());
			for(ElasticQueryParam p: elasticQuery.getElasticQueryParamList()) {
				p.setQueryId(query.getQueryId());
				elasticClusterDb.addQueryParamAfterQuery(p);
			}
			List<ElasticQueryParam> pList = elasticClusterDb.getQueryParams(query.getQueryId());
			query.setElasticQueryParamList(pList);
			return RestObject.retOKWithPayload(query, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete query statement against Elasticsearch cluster/server")
	public ResponseEntity<RestObject> 
	deleteElasticQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="queryId") final long queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteQuery(queryId);
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query:get", method = RequestMethod.GET)
	@Operation(summary = "Get the Dsl/Sql statement by searching a keyword")
	public ResponseEntity<RestObject> 
	getSpecificQuery(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="queryId") final long queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticQueryList elasticQueryList = new ElasticQueryList();
			List<ElasticQuery> lstDsl = elasticClusterDb.getQuery(queryId);
			elasticQueryList.addElasticQueryLst(lstDsl);
			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/params:get", method = RequestMethod.GET)
	@Operation(summary = "Get all params of the query")
	public ResponseEntity<RestObject> 
	getElasticQueryParams(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final int queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			List<ElasticQueryParam> lstQueryParam = elasticClusterDb.getQueryParams(queryId);
			elasticQueryParamList.setElasticQueryParamLst(lstQueryParam);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/param:get", method = RequestMethod.GET)
	@Operation(summary = "Get param of the query by name")
	public ResponseEntity<RestObject> 
	getElasticQueryParam(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final int queryId,
							@RequestHeader(value="paramName") final String paramName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			if(paramName == null || paramName.isBlank() || paramName.isEmpty() ) {
				throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Query Id or Param Name is null/empty")) ;
			}
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			List<ElasticQueryParam> lstQueryParam = elasticClusterDb.getQueryParam(queryId, paramName);
			elasticQueryParamList.setElasticQueryParamLst(lstQueryParam);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			elasticClusterDb.addQueryParam(	elasticQueryParam.getQueryId(),
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/param:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete query params") 
	public ResponseEntity<RestObject> 
	deleteElasticQueryParam(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final int queryId,
							@RequestHeader(value="paramId") final int paramId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteQueryParam(queryId, paramId);
			ElasticQueryParamList elasticQueryParamList = new ElasticQueryParamList();
			List<ElasticQueryParam> lstQueryParam = elasticClusterDb.getQueryParams(queryId);
			elasticQueryParamList.setElasticQueryParamLst(lstQueryParam);
			return RestObject.retOKWithPayload(elasticQueryParamList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/signature", method = RequestMethod.GET)
	@Operation(summary = "Get Input Object for Query execution") 
	public ResponseEntity<RestObject>
	getElasticQueryInputObject(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			/*TO BE FIXED*/
			QueryInputWrapper queryInputWrapper = new QueryInputWrapper();
			return RestObject.retOKWithPayload(queryInputWrapper, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	/////////////////////// Query to Cluster Bridges ////////////////////////////////////////////////////////////
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/bridge:get", method = RequestMethod.GET)
	@Operation(summary = "Set Query Bridge To Cluster") 
	public ResponseEntity<RestObject> 
	getQueryBridgeToCluster(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final int queryId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			QueryToClusterBridgeList queryToClusterBridgeList = new QueryToClusterBridgeList(elasticClusterDb.getQueryToClusterBridge(queryId));
			return RestObject.retOKWithPayload(queryToClusterBridgeList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/bridge:add", method = RequestMethod.PUT)
	@Operation(summary = "Set Query Bridge To Cluster") 
	public ResponseEntity<RestObject> 
	addQueryBridgeToCluster(@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="queryId") final int queryId,
							@RequestHeader(value="clusterId") final int clusterId,
							@RequestHeader(value="active", defaultValue = "1") int active) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.mergeQueryToClusterBridge(queryId, clusterId, active);
			QueryToClusterBridgeList queryToClusterBridgeList = new QueryToClusterBridgeList(elasticClusterDb.getQueryToClusterBridge(queryId));
			return RestObject.retOKWithPayload(queryToClusterBridgeList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/management/query/bridge:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Query Bridge To Cluster") 
	public ResponseEntity<RestObject> 
	deleteQueryBridgeToCluster(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="queryId") final int queryId,
								@RequestHeader(value="clusterId") final int clusterId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteActiveQueryToClusterBridge(queryId, clusterId);
			QueryToClusterBridgeList queryToClusterBridgeList = new QueryToClusterBridgeList(elasticClusterDb.getQueryToClusterBridge(queryId));
			return RestObject.retOKWithPayload(queryToClusterBridgeList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
				@RequestHeader(value="numberOfShards") final int numberOfShards,
				@RequestHeader(value="numberOfReplicas") final int numberOfReplicas) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				if(CreateIndexLowApi.indexExists(elasticLowLevelWrapper, indexName)) {
					return RestObject.retException(new GenericResponse("Index already exists"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index already exists");
				}

				boolean isCreated = CreateIndexLowApi.createIndex( elasticLowLevelWrapper, indexName, numberOfShards, numberOfReplicas);
				elasticLowLevelWrapper.disconnect();
				if(isCreated)
					return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				else
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cannot create index");
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
									@RequestHeader(value="fromClusterId") final long fromClusterId,
									@RequestHeader(value="fromEmbeddedDatabaseName") final String fromEmbeddedDatabaseName,
									@RequestHeader(value="fromEmbeddedSchemaName") final String fromEmbeddedSchemaName,
									@RequestBody String sqlContent) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist, Please create one for this operation");
				}
				
				H2Static h2Db = new H2Static(fromClusterId, fromEmbeddedDatabaseName );
				TableFormatMap recordSet = h2Db.execStaticQueryWithTableFormat(sqlContent);
				
				if(recordSet.getRows().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retOKWithPayload("Sql statement generates 0 records", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
				}
				

				String lastRecId = SearchApi.getDocIDLastOrFirstInserted(elasticLowLevelWrapper,toElasticIndexName, true);
				long startWith = Long.parseLong(lastRecId);
				long noDocsAdded = CreateIndexLowApi.addDocumentToIndex_(elasticLowLevelWrapper, toElasticIndexName, recordSet.getListOfRows(), startWith);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
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
				long noDocsAdded = CreateIndexLowApi.addDocumentToIndex_(elasticLowLevelWrapper, toElasticIndexName, resultQuery.getResultQueryRows(), startWith);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo/adhoc:mql", method = RequestMethod.PUT)
	@Operation(summary = "Copy to index from Mongo simple search")
	public ResponseEntity<RestObject> 
	copyMongoAdhocToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
									@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
									@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
									@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
									@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
									@RequestHeader(value="batchValue", defaultValue = "0") final int batchValue,
									@RequestBody String bsonQuery) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				
				if(!CreateIndexLowApi.indexExists(elasticLowLevelWrapper, toElasticIndexName)) {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exists. Please create one for this operation");
				}
				
				MongoResultSet ret = MongoGet.execDynamicQuery(	fromMongoClusterName, 
																fromMongoDatabaseName, 
																fromMongoCollectionName,
																bsonQuery,
																true);
			

				if(ret.getResultSet().isEmpty()) {
					elasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Collection " +
																			fromMongoCollectionName + 
																			" is empty, 0 documents added to index " + 
																			toElasticIndexName);
				}

				long noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, ret.getResultSet(), 0);
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse(noDocsAdded), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo:simple", method = RequestMethod.PUT)
	@Operation(summary = "Copy to index from Mongo simple search")
	public ResponseEntity<RestObject> 
	copyMongoSimpleQueryToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
										@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
										@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
										@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
										@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
										@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
										@RequestHeader(value="itemToSearch") final String itemToSearch,
										@RequestHeader(value="valueToSearch") final String valueToSearch,
										@RequestHeader(value="valueToSearchType") final String valueToSearchType,
										@RequestHeader(value="operator", defaultValue = "$eq") final String operator,
										@RequestHeader(value="batchValue") final int batchValue) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
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

				long noDocsAdded = 0;
				if(batchValue > 0)
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticIndexName,MongoResultSet.getResultSetAsJson(ret.getResultSet()), 0);
				else
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex_(elasticLowLevelWrapper, toElasticIndexName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), 0);


				
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse(noDocsAdded), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo:range", method = RequestMethod.PUT)
	@Operation(summary = "Copy to Elastic Index from a Mongo range search")
	public ResponseEntity<RestObject> 
	copyMongoRangeToElastic(@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
								@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
								@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
								@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
								@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
								@RequestHeader(value="itemToSearch") final String itemToSearch,
								@RequestHeader(value="fromValue") final String fromValue,
								@RequestHeader(value="toValue") final String toValue,
								@RequestHeader(value="valueSearchType") final String valueSearchType,
								@RequestHeader(value="batchValue") final int batchValue) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
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

				long noDocsAdded;
				if(batchValue > 0)
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticIndexName, MongoResultSet.getResultSetAsJson(ret.getResultSet()), batchValue);
				else
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex(elasticLowLevelWrapper, toElasticIndexName, ret.getResultSet());


				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse(noDocsAdded) , requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/mongo:collection", method = RequestMethod.PUT)
	@Operation(summary = "Copy from Mongo collection to Elastic") 
	public ResponseEntity<RestObject> 
	copyMongoFullCollectionToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
											@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
											@RequestHeader(value="toElasticIndexName") final String toElasticIndexName,
											@RequestHeader(value="fromMongoClusterName") final String fromMongoClusterName,
											@RequestHeader(value="fromMongoDatabaseName") final String fromMongoDatabaseName,
											@RequestHeader(value="fromMongoCollectionName") final String fromMongoCollectionName,
											@RequestHeader(value="batchCount", defaultValue = "0") final int batchCount) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
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
				if(batchCount <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex_(elasticLowLevelWrapper, toElasticIndexName, mongoResultSet, 0);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(elasticLowLevelWrapper, toElasticIndexName, mongoResultSet, batchCount);

				
				elasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload(new GenericResponse("Added " + noDocsAdded + " documents"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
								@RequestHeader(value="batchValue", required = false, defaultValue = "0") final int batchValue,
								@RequestBody (required = false) final String fromHttpPayload) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{

			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] toHttpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper toElasticLowLevelWrapper = new ElasticLowLevelWrapper(toHttpHostArray);
				
				if(!CreateIndexLowApi.indexExists(toElasticLowLevelWrapper, toIndexName)) {
					toElasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}
				
			
				List<String> elasticFromResultSet = new ArrayList<>();
				
				Map<String, ElasticCluster> fromClusterMap = elasticClusterDb.getElasticClusters(fromElasticClusterName);
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
				if(batchValue <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex_(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, batchValue);

				
				toElasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
								@RequestHeader(value="batchValue", required = false) final int batchValue,
								@RequestBody (required = false) final String sqlPayload) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] toHttpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper toElasticLowLevelWrapper = new ElasticLowLevelWrapper(toHttpHostArray);
				
				if(!CreateIndexLowApi.indexExists(toElasticLowLevelWrapper, toIndexName)) {
					toElasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}

				List<String> elasticFromResultSet;
				
				Map<String, ElasticCluster> fromClusterMap = elasticClusterDb.getElasticClusters(fromElasticClusterName);
				if(fromClusterMap.size() == 1) {
					HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(clusterMap, fromElasticClusterName);
					ElasticLowLevelWrapper fromElasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
					JSONObject ret = SearchSql.searchSql(fromElasticLowLevelWrapper, sqlPayload);
					elasticFromResultSet = Objects.requireNonNull(SqlResponse.toSqlResponse(ret)).toListOfJsonStrings();
					fromElasticLowLevelWrapper.disconnect();
				} else {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster destination does not exist");
				}

				long startWith = 0;
				long noDocsAdded = 0;

				if(batchValue <= 0)
					noDocsAdded = CreateIndexLowApi.addDocumentToIndex_(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, startWith);
				else
					noDocsAdded = CreateIndexLowApi.addBulkDocumentsToIndex(toElasticLowLevelWrapper, toIndexName, elasticFromResultSet, batchValue);

				
				toElasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster source does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/copy/csv:load", method = RequestMethod.PUT)
	@Operation(summary = "Copy Csv to Elastic Index") 
	public ResponseEntity<RestObject> 
	copyCsvToElastic(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="fileType") final String fileType,
						@RequestHeader(value="toElasticClusterName") final String toElasticClusterName,
						@RequestHeader(value="toIndexName") final String toIndexName,
						@RequestParam("attachment") final MultipartFile attachment) {
		String fileName = StringUtils.generateUniqueString32();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(toElasticClusterName);
			if(clusterMap.size() == 1) {
				HttpHost[] toHttpHostArray = elasticClusterDb.getHostArray(clusterMap, toElasticClusterName);
				ElasticLowLevelWrapper toElasticLowLevelWrapper = new ElasticLowLevelWrapper(toHttpHostArray);
				
				if(!CreateIndexLowApi.indexExists(toElasticLowLevelWrapper, toIndexName)) {
					toElasticLowLevelWrapper.disconnect();
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Index does not exist. Please create one before this operation");
				}
				String fullFilePath = storageService.storeTmp(attachment, fileName);
				String csvContent = CsvWrapper.readFile(fullFilePath);

				List<String> jList;
				if( fileType.toLowerCase().contains("text/csv")
						|| fileType.toLowerCase().contains("application/vnd.ms-excel") ) {
					jList = CsvWrapper.stringToJsonList(csvContent,"N");
				} else if( fileType.toLowerCase().contains("application/x-zip-compressed")) {
					jList = CsvWrapper.stringToJsonList(csvContent,"Y");
				} else {
					return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Unknown File Type");
				}
				



				long noDocsAdded = 
						CreateIndexLowApi.addDocumentToIndex_(	toElasticLowLevelWrapper,
																toIndexName,
																jList,
																0);
				
				toElasticLowLevelWrapper.disconnect();
				return RestObject.retOKWithPayload("Added " + noDocsAdded + " documents",requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} finally {
			storageService.deleteTmp(fileName);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/execute/adhoc/multiple:aggregate", method = RequestMethod.PUT)
	@Operation(summary = "Execute Sql or Dsl on multiple clusters / indexes and aggregate results with Sql") 
	public ResponseEntity<RestObject> 
	executeAdhocMultipleIndex(	@RequestHeader(value="user") final String user,
								@RequestHeader(value="session") final String session,
								@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestBody final ListElasticCompoundQuery listElasticCompoundQuery)  {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			String inMemDbName = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
            assert listElasticCompoundQuery != null;
            List<RdbmsTableSetup> lst = ElasticParallelQuery.executeElasticQueryInParallel( listElasticCompoundQuery.getLst(),  listElasticCompoundQuery.getTableName());
			H2InMem h2InMem = new H2InMem("", inMemDbName, "QUERY", session, requestId, user);
			h2InMem.loadRdbmsQueriesInMem(lst, false);
			ResultQuery ret  = SqlQueryExecUtils.execStaticQuery(h2InMem.getConnection(), listElasticCompoundQuery.getSqlAggregator());
			h2InMem.removeInMemDb();
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	/**Python API call not working. Fix it*/
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/dsl:fuzzy", method = RequestMethod.POST)
	@Operation(summary = "Query index via native DSL")
	public ResponseEntity<RestObject> 
	searchFuzzyIndex(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
						@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
						@RequestHeader(value="indexName") final String indexName,
						@RequestHeader(value="fromRecno") final int fromRecno,
						@RequestHeader(value="size") final int size,
						@RequestBody final QueryType queryType) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
			
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
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
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
				@RequestHeader(value="isOriginalFormat", required = false, defaultValue = "Y") final String isOriginalFormat,
				@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				@RequestHeader(value="comment", required = false) final String comment,
				@RequestHeader(value="queryName") String queryName,
				@RequestHeader(value="groupId", required = false, defaultValue = "-1" ) long groupId,
				@RequestBody (required = false) final String httpPayload) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		if( !ElasticClusterDb.isElasticApi(elasticApi) || !ElasticClusterDb.isVerb(httpVerb)) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "No conforming api");
		}
		try	{
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				Object ret = ElasticInfo.executeGeneric(elasticLowLevelWrapper, httpVerb, endPoint, isOriginalFormat, httpPayload);
				elasticLowLevelWrapper.disconnect();
				JSONObject jsonObject = JsonResponse.getJsonObj(ret);
				ElasticResponse response = new ElasticResponse(	httpVerb, elasticApi, Constants.langDsl, indexName, jsonObject);
				JSONObject jo =  ElasticQueryObj.generateSerializedDslObject(httpPayload, clusterUniqueName, httpVerb, elasticApi, indexName, endPoint, comment);
				ElasticCluster eCluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
				ElasticExecutedQuery rec = new ElasticExecutedQuery(-1, requestId, -1, queryName, Constants.langSql, httpPayload, "Y", "", groupId, Constants.adhocShort, u.getId(), eCluster.getClusterId(), "", "", "", "", "Y", comment, "", timeStamp, -1);
				pWrap.saveExecution(rec, jo, persist);
				return RestObject.retOKWithPayload(response, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/index/sql/adhoc:run", method = RequestMethod.POST)
	@Operation(summary = "Execute an adhoc SQL statement against an index") 
	public ResponseEntity<RestObject> 
	runAdhocSql(@RequestHeader(value="user") final String user,
				@RequestHeader(value="requestId", defaultValue = "") String requestId,
				@RequestHeader(value="clusterUniqueName") final String clusterUniqueName,
				@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
				@RequestHeader(value="comment", required = false) final String comment,
				@RequestHeader(value="queryName") String queryName,
				@RequestHeader(value="groupId", required = false, defaultValue = "-1" ) long groupId,
				@RequestBody final String sqlContent) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			requestId = StringUtils.generateRequestId(requestId);
			User u = authUtil.getUser(user);
			long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
			Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(clusterUniqueName);
			if(clusterMap.size() == 1) {
				HttpHost[] httpHostArray = elasticClusterDb.getHostArray(clusterMap, clusterUniqueName);
				ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(httpHostArray);
				JSONObject ret = SearchSql.searchSql(elasticLowLevelWrapper, sqlContent);
				elasticLowLevelWrapper.disconnect();
				ElasticResponse response = new ElasticResponse(ret.toJSONString());
				if( persist.compareToIgnoreCase("Y") == 0 ) {
					try {
						JSONObject jo =  ElasticQueryObj.generateSerializedDslObject(sqlContent, clusterUniqueName, "", "", "", "", comment);
						ElasticCluster eCluster = elasticClusterDb.getElasticClusterByName(clusterUniqueName);
						ElasticExecutedQuery rec = new ElasticExecutedQuery(-1, requestId, -1, queryName, Constants.langSql, sqlContent, "Y", "", groupId, Constants.adhocShort, u.getId(), eCluster.getClusterId(), "", "", "", "", "Y", comment, "", timeStamp, -1);
						pWrap.saveExecution(rec, jo, persist);
					} catch(Exception ex) {
						AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
					}
				}
				return RestObject.retOKWithPayload(response, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "Cluster does not exist");
			}


		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
							@RequestHeader(value="queryId") final int queryId,
							@RequestHeader(value="queryType") final String queryType,
							@RequestHeader(value="persist", required = false, defaultValue = "N") final String persist,
							@RequestHeader(value="comment", required = false) final String comment,
							@RequestBody ElasticQueryExec elasticQueryExec) {
		requestId = StringUtils.generateRequestId(requestId);
		ElasticQueryList elasticQueryList = new ElasticQueryList();

		try	{

			if( persist.compareToIgnoreCase("Y") == 0 ) {
				User u = authUtil.getUser(user);

				try {

				} catch(Exception ex) {
					throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
				}
			}

			return RestObject.retOKWithPayload(elasticQueryList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	/*                    SQL Snapshots                                     */
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/snapshot:history", method = RequestMethod.GET)
	@Operation(summary = "Get a list of executed snapshots")
	public ResponseEntity<RestObject> 
	getElasticSnapshotHistory(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="ownerId") final long ownerId,
								@RequestHeader(value="startTime") final long startTime,
								@RequestHeader(value="endTime") final long endTime,
								@RequestHeader(value="statement", required = false, defaultValue = "") final String statement) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticExecutedQueryList ret = executedQueriesRepoDb.getAllStatementsByUser(ownerId, startTime, endTime, statement);
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/snapshot:get", method = RequestMethod.GET)
	@Operation(summary = "Get snapshot to visualize") 
	public ResponseEntity<RestObject> 
	getElasticSnapshot(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					   @RequestHeader(value="snapshotId") final long snapshotId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticExecutedQuery stm = executedQueriesRepoDb.getStatementById( snapshotId);
			ElasticResponse response = ElasticResponse.toElasticResponse( persistence.deserializeAsString(stm.getRepPath()) );
			return RestObject.retOKWithPayload(response, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/snapshot:delete", method = RequestMethod.POST)
	@Operation(summary = "Delete snapshot") 
	public ResponseEntity<RestObject> 
	deleteElasticSnapshot(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@RequestHeader(value="snapshotId") final long snapshotId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			ElasticExecutedQuery stm = executedQueriesRepoDb.getStatementById( snapshotId);
			executedQueriesRepoDb.deleteStatementById(snapshotId);
			persistence.delete(stm.getRepPath());
			return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	/*  Associations*/

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:get", method = RequestMethod.GET)
	@Operation(summary = "Get elastic repo associations")
	public ResponseEntity<RestObject> 
	getElasticRepoAssociationTable(	@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			RepoAssociationTableList repoAssociationTableList = elasticClusterDb.getRepoAssociationTable();
			return RestObject.retOKWithPayload(repoAssociationTableList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
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
			RepoAssociationTable repoAssociationTable = elasticClusterDb.getRepoAssociation( associationName );
			return RestObject.retOKWithPayload(repoAssociationTable, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:update", method = RequestMethod.PUT)
	@Operation(summary = "Update elastic repo association")
	public ResponseEntity<RestObject> 
	updateElasticRepoAssociation(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationId") final long associationId,
									@RequestHeader(value="associationName") final String associationName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.updateRepoAssociationTable(associationId , associationName);
			RepoAssociationTable repoAssociationTable = elasticClusterDb.getRepoAssociation( associationName );
			return RestObject.retOKWithPayload(repoAssociationTable, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());

		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/association:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete elastic repo association")
	public ResponseEntity<RestObject> 
	deleteElasticRepoAssociation(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="associationId") final long associationId) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteRepoAssociationTable(associationId);
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"),requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/elastic-repo/associationByName:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete elastic repo association")
	public ResponseEntity<RestObject>
	deleteElasticRepoAssociation(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									 @RequestHeader(value="associationName") final String associationName) {
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			elasticClusterDb.deleteRepoAssociationTable(associationName);
			return RestObject.retOKWithPayload(new GenericResponse("DELETED"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}



}
