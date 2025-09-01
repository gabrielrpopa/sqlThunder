package com.widescope.rdbmsRepo.database.embeddedDb.elastic;

import java.util.List;
import java.util.Map;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.ElasticLowLevelWrapper;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.SearchSql;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload2;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.sql.ElasticSqlPayload;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;

public class ElasticExecWrapper {

	public static TableFormatMap 
	execElasticViaSql(	final String elasticClusterName,
						final String sqlStatement,
						final int fetchSize) throws Exception {
		TableFormatMap ret = new TableFormatMap();
		ElasticClusterDb elasticClusterDb = new ElasticClusterDb();
		Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(elasticClusterName);
		if(clusterMap.size() == 1) {
			org.apache.http.HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(clusterMap, elasticClusterName);
			ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
			ElasticSqlPayload payLoad = SearchSql.searchSqlAsElasticSqlPayload(elasticLowLevelWrapper, sqlStatement, fetchSize);
			elasticLowLevelWrapper.disconnect();
			Map<String, String> metadata = ElasticSqlPayload.getMetadataAsMap(payLoad);
			List<Map<String,Object>> rows = ElasticSqlPayload.getRowAsListOfMap(payLoad);
			ret.setMetadata(metadata);
			ret.setRows(rows);
		}
		return ret;
	}
	
	
	
	
	public static TableFormatMap 
	execElasticViaDsl(	final String elasticClusterName,
						final String httpVerb,
						final String endPoint,
						final String jsonCommand,
						final int fetchSize) throws Exception {
		
		TableFormatMap ret = new TableFormatMap();
		ElasticClusterDb elasticClusterDb = new ElasticClusterDb();
		Map<String, ElasticCluster> clusterMap = elasticClusterDb.getElasticClusters(elasticClusterName);
		if(clusterMap.size() == 1) {
			org.apache.http.HttpHost[] fromHttpHostArray = elasticClusterDb.getHostArray(clusterMap, elasticClusterName);
			ElasticLowLevelWrapper elasticLowLevelWrapper = new ElasticLowLevelWrapper(fromHttpHostArray);
			ElasticPayload2 payload = ElasticInfo.executeGenericForPayload2(elasticLowLevelWrapper,
																			httpVerb,
																			endPoint,
																			jsonCommand);
			Map<String, String> metadata = ElasticInfo.getMetadata(payload);
			List<Map<String,Object>> rows = ElasticInfo.getRows(payload);
			ret.setMetadata(metadata);
			ret.setRows(rows);
		}
		
		return ret;
	}
	
	
}
