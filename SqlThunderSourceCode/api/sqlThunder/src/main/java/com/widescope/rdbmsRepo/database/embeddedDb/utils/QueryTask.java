package com.widescope.rdbmsRepo.database.embeddedDb.utils;

import java.util.concurrent.Callable;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryRepoUtils;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;


public class QueryTask implements Callable<RdbmsTableSetup> {

	private final RdbmsCompoundQuery r;
	private final String tableName;

	public QueryTask(	RdbmsCompoundQuery rdbmsCompoundQuery,
						final String tblName) {
		this.r = rdbmsCompoundQuery;
		this.tableName = tblName;
	}
 
	@Override
	public RdbmsTableSetup call() throws Exception {
		RdbmsTableSetup setup = new RdbmsTableSetup();
		try {
			TableFormatMap tableFormatMap = SqlQueryRepoUtils.execStaticQueryWithTableFormatMap(r.getSchemaUniqueName(), r.getSqlContent());
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
