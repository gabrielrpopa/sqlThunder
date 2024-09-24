/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.widescope.rdbmsRepo.database.embeddedDb.embedded;


import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.widescope.logging.AppLogger;

import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.InMemDbs;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn.ClusterTransfer;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecordList;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;



public class EmbeddedWrapper {

	private static final
    ConcurrentHashMap<	String /*sessionId*/,
						ConcurrentHashMap<	String /*requestId*/, 
											EmbeddedInMemCluster > > 
	inmemDb = new ConcurrentHashMap<>();

	public static InMemDbs getInMemDbs() {
		InMemDbs ret = new InMemDbs();
		ret.deepCopyInMemProofOfWorkMap(EmbeddedWrapper.inmemDb);
		return ret;
	}
	
	public static void addInmemDb(	final EmbeddedInMemCluster c) {
		if(EmbeddedWrapper.inmemDb.containsKey(c.getSessionId())) {
			EmbeddedWrapper.inmemDb.get(c.getSessionId()).put(c.getRequestId(), c);
		} else {
			EmbeddedWrapper.inmemDb.put(c.getSessionId(), new ConcurrentHashMap<> ());
			EmbeddedWrapper.inmemDb.get(c.getSessionId()).put(c.getRequestId(), c);
		}
	}
	
	public static EmbeddedInMemCluster 
	getInmemDb(	final String sessionId, 
				final String requestId) {
		return EmbeddedWrapper.inmemDb.get(sessionId).get(requestId);
	}
	
	
	
	public static void addInMemEmbeddedQuery( final EmbeddedInterface o, final String comment) {
		Map<String, EmbeddedInterface> cluster_ = new HashMap<String, EmbeddedInterface>();
		cluster_.put(((H2InMem)o).getDbName(), o);
		EmbeddedInMemCluster c = new  EmbeddedInMemCluster(((H2InMem)o).getClusterId(), 
				cluster_,
				((H2InMem)o).getSessionId(),
				((H2InMem)o).getRequestId(),
				((H2InMem)o).getUserId(),
				comment);
		EmbeddedWrapper.addInmemDb(c);
		
		
		EmbeddedInMemCluster inMemCluster =EmbeddedWrapper.getInmemDb(((H2InMem)o).getSessionId(),((H2InMem)o).getRequestId());
		H2InMem inMem = (H2InMem)inMemCluster.getCluster().get(((H2InMem)o).getDbName());
	}


	public static String getSqlStr(final String sqlString) {
		String dbId;
		try {
			dbId = com.widescope.sqlThunder.utils.StringUtils.getStringHashValue(sqlString);
		} catch (NoSuchAlgorithmException e) {
			dbId = com.widescope.sqlThunder.utils.StringUtils.generateUniqueString();
		}
		return dbId;
	}


	public static
	ClusterTransfer 
	loadH2ClusterInMem(	final int clusterId, 
						final String sessionId,
						final String requestId,
						final String userId,
						final String comment) throws Exception {
		ClusterTransfer ret = new ClusterTransfer();
		EmbeddedDbRepo repo = new EmbeddedDbRepo();
		EmbeddedDbRecordList r = repo.getClusterEmbeddedDb(clusterId);

		EmbeddedInMemCluster c = new EmbeddedInMemCluster(clusterId,  sessionId, requestId, userId, comment);
		for(EmbeddedDbRecord embDb: r.getEmbeddedDbRecordList() ) {
			H2InMem h2InMem = loadH2DatabaseInMem(clusterId,embDb, sessionId, requestId, userId );
			ret.getEmbeddedDbRecordList().add(embDb);
			c.getCluster().put(embDb.getFileName(), h2InMem);
		}
		EmbeddedWrapper.addInmemDb(c);
		return ret;
	}
	
	
	public static 
	ClusterTransfer 
	loadCsvInMem(	final EmbeddedDbRecord e,
					final String sessionId,
					final String requestId,
					final String userId,
					final H2InMem h2InMem,
					final String comment) {
		ClusterTransfer ret = new ClusterTransfer();
		EmbeddedInMemCluster c = new EmbeddedInMemCluster(-1,  sessionId, requestId, userId, comment);
		ret.getEmbeddedDbRecordList().add(e);
		c.getCluster().put(e.getFileName(), h2InMem);
		EmbeddedWrapper.addInmemDb(c);
		return ret;
	}
	
	
	public static 
	ClusterTransfer 
	loadEmptyDbInMem(	final EmbeddedDbRecord e,
						final String sessionId,
						final String requestId,
						final String userId,
						final H2InMem h2InMem,
						final String comment) {
		ClusterTransfer ret = new ClusterTransfer();
		EmbeddedInMemCluster c = new EmbeddedInMemCluster(-1,  sessionId, requestId, userId, comment);
		ret.getEmbeddedDbRecordList().add(e);
		c.getCluster().put(e.getFileName(), h2InMem);
		EmbeddedWrapper.addInmemDb(c);
		return ret;
	}
	
	
	
	public static 
	ClusterTransfer 
	loadH2ClusterInMem(	final long clusterId, 
						final EmbeddedDbRecord e,
						final String sessionId,
						final String requestId,
						final String userId,
						final String comment
					   ) throws Exception {
		ClusterTransfer ret = new ClusterTransfer();
		EmbeddedInMemCluster c = new EmbeddedInMemCluster(clusterId,  sessionId, requestId, userId, comment);
		H2InMem h2InMem = loadH2DatabaseInMem(clusterId,e, sessionId, requestId, userId );
		
		ret.getEmbeddedDbRecordList().add(e);
		c.getCluster().put(e.getFileName(), h2InMem);
		EmbeddedWrapper.addInmemDb(c);
		return ret;
	}
	
	
	public static 
	H2InMem 
	loadH2DatabaseInMem(final long clusterId,
						final EmbeddedDbRecord embDb,
						final String sessionId,
						final String requestId,
						final String userId	) throws Exception {

		H2InMem h2InMem = new H2InMem(clusterId, embDb.getDbId(), "TABLE", sessionId, requestId, userId);
		H2Static h2Static = new H2Static(clusterId , embDb.getDbId());
		Connection conn = h2Static.getConnection();
		List<String> uTables = h2Static.getUserTables(conn);
		for(String tName: uTables) {
			TableMetadata tMetadata = SqlMetadataWrapper.getTableColumns(tName, conn);
			RdbmsTableSetup  setup = SqlMetadataWrapper.createTableStm(tMetadata, "", tName, "H2");
			String q = "SELECT * FROM " + tName;
			TableFormatMap result = h2Static.execStaticQueryWithTableFormat(q);
			boolean isCreated = h2InMem.createUserTable(setup.getCreateTableStm());
			if(isCreated) {
				String insertStm = SqlMetadataWrapper.generateInsertTableStm(result.getMetadata(), "", tName);
                try (Statement statement = h2InMem.getConnection().createStatement()) {
                    for (Map<String, Object> m : result.getRows()) {
                        String insert = SqlMetadataWrapper.generateExecutableInsertTableStm(insertStm, result.getMetadata(), m);
                        try {
                            statement.executeUpdate(insert);
                        } catch (Exception ex) {
                            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
                        }
                    }
                }
            }
		}

		h2InMem.setType("TABLE");
		h2InMem.setUserTables(uTables);
		return h2InMem;
	}
	
	public static void 
	removeInMemDbRequestId(final String sesId, final String requestId) {
		if(!inmemDb.isEmpty() && sesId != null) {
			Iterator<Map.Entry<String,EmbeddedInMemCluster>> it = inmemDb.get(sesId).entrySet().iterator();
	        while (it.hasNext()) {
	        	if(it.next().getKey().equals(requestId)) {
	        		EmbeddedInMemCluster c = it.next().getValue();
				    System.out.println("removing request in mem dbs: " + c.getRequestId());
                    for (Map.Entry<String, EmbeddedInterface> stringEmbeddedInterfaceEntry : c.getCluster().entrySet()) {
                        H2InMem m = (H2InMem) stringEmbeddedInterfaceEntry.getValue();
                        System.out.println(m.getDbName() + " removed from memory for request " + m.getRequestId());
                        m.removeInMemDb();
                    }
			        
			        it.remove();
	        	}
	        }
		}
	}
	
	
	public static void 
	removeInMemDbSessionId(final String sesId) {
		if(!inmemDb.isEmpty() && sesId != null) {
			Iterator<Map.Entry<String,EmbeddedInMemCluster>> it = inmemDb.get(sesId).entrySet().iterator();
	        while (it.hasNext()) {
			    EmbeddedInMemCluster c = it.next().getValue();
			    System.out.println("removing request in mem dbs: " + c.getRequestId());
                for (Map.Entry<String, EmbeddedInterface> stringEmbeddedInterfaceEntry : c.getCluster().entrySet()) {
                    H2InMem m = (H2InMem) stringEmbeddedInterfaceEntry.getValue();
                    System.out.println(m.getDbName() + " removed from memory for request " + m.getRequestId());
                    m.removeInMemDb();
                }
		        it.remove();
	        }
			inmemDb.remove(sesId);
		}
	}
	
		
}
