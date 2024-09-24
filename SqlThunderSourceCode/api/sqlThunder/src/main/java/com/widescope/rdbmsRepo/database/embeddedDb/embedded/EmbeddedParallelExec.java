package com.widescope.rdbmsRepo.database.embeddedDb.embedded;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.widescope.logging.AppLogger;
import org.springframework.http.ResponseEntity;
import com.widescope.rest.RestObject;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec.EmbeddedDbAggregator;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec.EmbeddedExecTable;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec.EmbeddedExecTableList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedClusterRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatOutput;
import com.widescope.rdbmsRepo.utils.SqlParser;


public class EmbeddedParallelExec {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	private static List<TableFormatMap> 
	execParallelEmbedded(final EmbeddedExecTableList tList) {
		List<TableFormatMap> ret = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(tList.getLst().size());
		List<Future<TableFormatMap>> retLst = new ArrayList<Future<TableFormatMap>>();
		for(int i=0; i < tList.getLst().size(); i++){
			EmbeddedExecTable embeddedTblCmd = tList.getLst().get(i);
        	Callable<TableFormatMap> callable = new EmbeddedDbAggregator(tList.getClusterId(), embeddedTblCmd);
            Future<TableFormatMap> future = executor.submit(callable);
            retLst.add(future);
        }
		for(Future<TableFormatMap> f: retLst) {
			try {
				ret.add(f.get());
			} catch (InterruptedException | ExecutionException ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			}
		}
		return ret;
	}
	
	
	
	public static TableFormatOutput assemble(	final EmbeddedExecTableList execp, 
												final String sessionId_,
												final String requestId_,
												final String userId_) throws Exception {
		List<TableFormatMap> step1 = execParallelEmbedded( execp);
		TableFormatOutput ret = new TableFormatOutput(step1, execp.getAssembly());
		/*Now copy table in-mem embedded table and query it with final query*/
		List<String> tblNames = SqlParser.getTableNamesFromSql(execp.getSqlFinal());
		String stmCreateTable = H2InMem.getCreateTableStm(ret.getExtendedMetadata(), tblNames.get(0));
		H2InMem h2InMem = new H2InMem(execp, sessionId_, requestId_, userId_);
		if( h2InMem.createUserTable(stmCreateTable) ) {
			h2InMem.insertBag(ret, tblNames.get(0));
		}
		//TableFormatMap t = h2InMem.execStaticQueryWithTable(stmCreateTable);
		return ret;
	}
	
	
	public static ResponseEntity<RestObject> 
	checkClusterCommand(final EmbeddedExecTableList cmd,
						EmbeddedDbRepo embeddedDbRepo,
						final String requestId, 
						final String methodName)  {
		if(cmd.getAssembly().compareToIgnoreCase("UNION") != 0 
				&& cmd.getAssembly().compareToIgnoreCase("GLUE") != 0 ) {
			return RestObject.retException(requestId, methodName, AppLogger.logError(className, methodName, AppLogger.obj, "incorrect assembly: " + cmd.getAssembly()));
		}
		if(cmd.getClusterId() <= 0 ) {
			return RestObject.retException(requestId, methodName, AppLogger.logError(className, methodName, AppLogger.obj, "incorrect cluster: " + cmd.getClusterId()));
		} else {
			try {
				EmbeddedClusterRecord embeddedClusterRecord = embeddedDbRepo.getCluster(cmd.getClusterId());
				if(embeddedClusterRecord.getClusterId() <= 0) {
					return RestObject.retException(requestId, methodName, AppLogger.logError(className, methodName, AppLogger.obj, "incorrect cluster: " + cmd.getClusterId()));
				}
			} catch (Exception e1) {
				return RestObject.retException(requestId, methodName, AppLogger.logException(e1, className, methodName, AppLogger.obj));
			}
		}
		
		if(cmd.getLst().isEmpty()) {
			return RestObject.retException(requestId, methodName, AppLogger.logError(className, methodName, AppLogger.obj, "no sqls"));
		}
		
		if(!cmd.getSqlFinal().isEmpty() && cmd.getAssembly().compareToIgnoreCase("SQL") == 0) {
			if(!SqlParser.isSqlDQL(cmd.getSqlFinal()) 
					&& SqlParser.getTableNamesFromSql(cmd.getSqlFinal() ).size() != 1) {
				return RestObject.retException(requestId, methodName, AppLogger.logError(className, methodName, AppLogger.obj, "uncompilable final SQL: " + cmd.getSqlFinal()));
			}
		}
		for(EmbeddedExecTable e:  cmd.getLst() ) {
			if(!SqlParser.isSqlDQL(e.getStaticSqlStm())) {
				return RestObject.retException(requestId, methodName, AppLogger.logError(className, methodName, AppLogger.obj, "uncompilable partition SQL: " + cmd.getSqlFinal()));
			}
		}
		
		return null;
	}
	
	
	
}
