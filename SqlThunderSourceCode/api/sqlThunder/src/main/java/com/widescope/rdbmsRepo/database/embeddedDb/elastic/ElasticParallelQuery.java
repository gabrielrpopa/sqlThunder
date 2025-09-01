package com.widescope.rdbmsRepo.database.embeddedDb.elastic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;

public class ElasticParallelQuery {

	public 
	static 
	List<RdbmsTableSetup>
	executeElasticQueryInParallel(final List<ElasticCompoundQuery>  lst, final String tblName) {
		List<RdbmsTableSetup> ret = new ArrayList<RdbmsTableSetup>();
		ExecutorService executor = Executors.newFixedThreadPool(lst.size());
		for (ElasticCompoundQuery r: lst) {
	        Callable<RdbmsTableSetup> task = new ElasticQueryTask(r, tblName);
            Future<RdbmsTableSetup> future = executor.submit(task);
            RdbmsTableSetup result = null;
			try { result = future.get(); } catch (InterruptedException | ExecutionException ignored) {  }
			ret.add(result);
        }
		executor.shutdown();
		return ret;
	}
	
	
}
