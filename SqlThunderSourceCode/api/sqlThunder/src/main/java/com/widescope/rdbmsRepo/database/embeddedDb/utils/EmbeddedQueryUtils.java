package com.widescope.rdbmsRepo.database.embeddedDb.utils;

import java.util.List;
import java.util.Map;

import com.widescope.logging.AppLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.widescope.rdbmsRepo.database.SqlRepository.Objects.RecordsAffected;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;

public class EmbeddedQueryUtils {

	public static RecordsAffected 
	insertBulkIntoEmbeddedTable(final long clusterId,
								final String databaseName,
								final String schemaName,
								final String tableName,
								final List<Map<String, Object>> rows, 
								final Map<String, String> metadata) throws Exception
	{
		
		RecordsAffected ret = new RecordsAffected("INSERT", 0, 0);
		try	{
			H2Static h2Db = new H2Static(clusterId, databaseName );
			ret = h2Db.insertBulkIntoEmbeddedTable(schemaName, tableName, rows, metadata);
		}
		catch (Exception e)	{
			String message = Thread.currentThread().getStackTrace()[1] + ", Error: " + clusterId + "/" + databaseName + "." + schemaName + "." + tableName + ": " + e.getMessage() ;
			AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, message);
			ret.setMessage(message);
		}
		return ret;
	}
	
	
}
