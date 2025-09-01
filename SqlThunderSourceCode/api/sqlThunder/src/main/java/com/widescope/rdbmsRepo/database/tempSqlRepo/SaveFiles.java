package com.widescope.rdbmsRepo.database.tempSqlRepo;


@FunctionalInterface
public interface SaveFiles {
	public boolean saveScript(String mainFolder,
                              String type,
                              String interpreter,
                              String toUserId,
                              String scriptName,
                              String shaHash);
	
}



