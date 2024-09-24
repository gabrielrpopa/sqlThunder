package com.widescope.rdbmsRepo.database.embeddedDb.objects.operationReturn;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRecord;

public class ClusterTransfer implements RestInterface{

	private long clusterId;
	private List<EmbeddedDbRecord> embeddedDbRecordList;
	public List<EmbeddedDbRecord> getEmbeddedDbRecordList() {
		return embeddedDbRecordList;
	}
	public void setEmbeddedDbRecordList(List<EmbeddedDbRecord> embeddedDbRecordList) {
		this.embeddedDbRecordList = embeddedDbRecordList;
	}

	public long getClusterId() {
		return clusterId;
	}
	public void setClusterId(long clusterId) {
		this.clusterId = clusterId;
	}
	
	public ClusterTransfer() {
		this.setClusterId(-1);
		this.setEmbeddedDbRecordList(new ArrayList<EmbeddedDbRecord>());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
