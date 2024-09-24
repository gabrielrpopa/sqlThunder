package com.widescope.rdbmsRepo.database.tempSqlRepo;


@FunctionalInterface
public interface SaveFiles {
	public boolean saveScript(	String mainFolder,
									String type,
									String interpreter,
									long toUserId, 
									String scriptName,
									String shaHash);
	
}



