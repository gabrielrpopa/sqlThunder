package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import org.apache.commons.lang3.SerializationUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedInMemCluster;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2InMem;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem.InMemClusterDest;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem.InMemDbDest;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem.InMemDbSource;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem.InMemProofOfWork;
import com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem.InMemSource;



public class InMemDbs implements RestInterface {
	private 
	HashMap<String /*sessionId*/, 
			HashMap<String /*requestId*/, 
					InMemProofOfWork > > inMemProofOfWorkMap = new HashMap<>();
	

	public InMemDbs () {
		this.setInMemProofOfWorkMap(new HashMap<>());
	}
	
	public HashMap<String, HashMap<String, InMemProofOfWork  > > getInMemProofOfWorkMap() {
		return inMemProofOfWorkMap;
	}

	public void setInMemProofOfWorkMap(HashMap<String, HashMap<String, InMemProofOfWork > > inMemProofOfWorkMap_) {
		this.inMemProofOfWorkMap = SerializationUtils.clone(inMemProofOfWorkMap_);
	}
	
	public void deepCopyInMemProofOfWorkMap(ConcurrentHashMap<String, ConcurrentHashMap<String, EmbeddedInMemCluster > > inmemDb_) {
		//Remove what it is not serializable
		for (Map.Entry<String, ConcurrentHashMap<String, EmbeddedInMemCluster> >  entry : inmemDb_.entrySet()) {
			this.inMemProofOfWorkMap.put(entry.getKey(), new HashMap<String, InMemProofOfWork >  ());
			for (Map.Entry<String,EmbeddedInMemCluster>   entry1 : entry.getValue().entrySet()) {
				
				EmbeddedInMemCluster e = entry1.getValue();
				Map<String, EmbeddedInterface> r = e.getCluster();
				InMemClusterDest inMemClusterDest = new InMemClusterDest(e.getClusterId());
				InMemProofOfWork inProof = 
						new InMemProofOfWork(com.widescope.sqlThunder.utils.DateTimeUtils.millisecondsSinceEpoch());
				inProof.setInMemClusterDest(inMemClusterDest);
				inProof.setComment(e.getComment());
				InMemSource inMemSource = new InMemSource(	"", e.getClusterId(), "", "");
				inProof.setInMemSource(inMemSource);
				
				
				
				for (var entry3 : r.entrySet()) {
				    H2InMem m =  (H2InMem)entry3.getValue();
				    InMemDbDest inMemDbDest = new InMemDbDest(m.getClusterId(),
																"", /*No schema in this case*/
																m.getDbName(),
																m.getUserTables());
				    
				    inProof.getInMemClusterDest().getInMemDbDestList().add(inMemDbDest);
				    InMemDbSource inMemDbSource = new InMemDbSource(m.getClusterId(),
				    												"",
																	m.getDbName(),
																	m.getUserTables());
				    inProof.getInMemSource().getInMemDbSourceList().add(inMemDbSource);
				}
				
				this.inMemProofOfWorkMap.get(entry.getKey()).put(entry1.getKey(), inProof);
				
			}
		}
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

}
