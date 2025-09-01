package com.widescope.rdbmsRepo.database.embeddedDb.mongo;


import java.util.concurrent.Callable;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;

public class MongoQueryTask implements Callable<RdbmsTableSetup>{

	private final MongoCompoundQuery r;
	private final String tableName;

	public MongoQueryTask(MongoCompoundQuery elasticCompoundQuery,
							final String tblName) {
		this.r = elasticCompoundQuery;
		this.tableName = tblName;
	}
 
	@Override
	public RdbmsTableSetup call() throws Exception {
		RdbmsTableSetup setup = new RdbmsTableSetup();
		try {
			MongoResultSet ret = MongoGet.execDynamicQuery(	r.getClusterUniqueName(), 
															r.getMongoDbName(), 
															r.getCollectionName(),
															r.getQueryContent(),
															false);
			
			
			
			TableFormatMap tableFormatMap = new TableFormatMap();
			
			tableFormatMap.setMetadata(ret.getMetadata());
			tableFormatMap.setRows(MongoResultSet.getRecords(ret.getResultSet()));
			tableFormatMap.setColCount(ret.getResultSet().size());
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
