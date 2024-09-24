package com.widescope.rdbmsRepo.database.embeddedDb.elastic;

import java.util.concurrent.Callable;

import com.widescope.logging.AppLogger;

import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;


public class ElasticQueryTask implements Callable<RdbmsTableSetup>{

	private final ElasticCompoundQuery r;
	private final String tableName;

	public ElasticQueryTask(ElasticCompoundQuery elasticCompoundQuery,
							final String tblName) {
		this.r = elasticCompoundQuery;
		this.tableName = tblName;
	}
 
	@Override
	public RdbmsTableSetup call() {
		RdbmsTableSetup setup = new RdbmsTableSetup();
		try {
			
			TableFormatMap tableFormatMap;
			if(r.getSqlType().equals("SQL")) {
				tableFormatMap = ElasticExecWrapper.execElasticViaSql(r.getClusterUniqueName(), r.getSqlContent(), 1);
			} else {
				tableFormatMap = ElasticExecWrapper.execElasticViaDsl(r.getClusterUniqueName(),r.getHttpVerb(),	r.getEndPoint(), r.getSqlContent(),	1);
			}
			setup.setTableFormatMap(tableFormatMap);
			setup.setTableName(this.tableName);
			String createTableStm = SqlMetadataWrapper.createRdbmsTableStm(tableFormatMap.getMetadata(), tableName);
			setup.setCreateTableStm(createTableStm);
			String insertStm = SqlMetadataWrapper.generateInsertTableStm(tableFormatMap.getMetadata(), "", tableName);
			setup.setInsertTableStm(insertStm);
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
		
		return setup;
    }
	
	
}
