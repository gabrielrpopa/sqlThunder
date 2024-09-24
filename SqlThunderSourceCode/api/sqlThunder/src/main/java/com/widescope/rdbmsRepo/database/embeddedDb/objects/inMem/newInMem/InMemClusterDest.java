package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class InMemClusterDest implements RestInterface {
	private long clusterId;
	private List<InMemDbDest> inMemDbDestList;

	public InMemClusterDest(final long clusterId) {
		this.setClusterId(clusterId);
		this.setInMemDbDestList(new ArrayList<InMemDbDest>());
	}
	
	
	public InMemClusterDest() {
		this.setClusterId(-1);
		this.setInMemDbDestList(new ArrayList<InMemDbDest>());
	}


	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	public List<InMemDbDest> getInMemDbDestList() { return inMemDbDestList; }
	public void setInMemDbDestList(List<InMemDbDest> inMemDbDestList) { this.inMemDbDestList = inMemDbDestList; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
