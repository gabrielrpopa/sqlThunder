package com.widescope.rdbmsRepo.database.embeddedDb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsCompoundQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;

public class RdbmsParallelQuery {

	public 
	static 
	List<RdbmsTableSetup>
	executeQueryInParallel(final List<RdbmsCompoundQuery>  lst, final String tblName) {
		List<RdbmsTableSetup> ret = new ArrayList<RdbmsTableSetup>();
		ExecutorService executor = Executors.newFixedThreadPool(lst.size());
		for (RdbmsCompoundQuery r: lst) {
	        Callable<RdbmsTableSetup> task = new QueryTask(r, tblName);
            Future<RdbmsTableSetup> future = executor.submit(task);
            RdbmsTableSetup result = null;
			try {
				result = future.get();
			} catch (InterruptedException | ExecutionException ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			}

			ret.add(result);
        }
		executor.shutdown();
		return ret;
	}

	
	
	
}
