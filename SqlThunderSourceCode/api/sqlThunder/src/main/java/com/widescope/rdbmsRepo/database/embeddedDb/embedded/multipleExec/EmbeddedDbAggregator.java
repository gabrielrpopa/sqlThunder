package com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec;

import java.util.concurrent.Callable;

import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Static;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;


public class EmbeddedDbAggregator implements Callable<TableFormatMap> {
	private final EmbeddedExecTable execTableCommand;
	private final long clusterId ;
	public EmbeddedDbAggregator(final long clusterId, final EmbeddedExecTable execTableCommand) {
		this.clusterId=clusterId;
		this.execTableCommand=execTableCommand;
	}

	@Override
	public TableFormatMap call() throws Exception {
		
		EmbeddedDbRepo embeddedDbRepo = new EmbeddedDbRepo();
		EmbeddedDbRecord dbRecord = embeddedDbRepo.getEmbeddedDb(clusterId, execTableCommand.getDbId());
		if(dbRecord.getType().compareToIgnoreCase("H2") == 0) {
			DbConnectionInfo conn 
			= DbConnectionInfo.makeH2ConnectionInfo(dbRecord.getFileName(), 
													clusterId,
													H2Static.getUserName(), 
													H2Static.getUserPassword());
			
			return SqlQueryExecUtils.execStaticQueryWithTable(conn, this.execTableCommand.getStaticSqlStm());
		} else {
			return new TableFormatMap();
		}
		

	}
	
}
