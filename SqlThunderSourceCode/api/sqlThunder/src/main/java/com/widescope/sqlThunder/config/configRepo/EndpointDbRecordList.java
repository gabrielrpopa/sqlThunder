package com.widescope.sqlThunder.config.configRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class EndpointDbRecordList implements RestInterface{
	private List<EndPointDbRecord> endpointDbRecordLst;
	public EndpointDbRecordList(final List<EndPointDbRecord> endpointDbRecordLst) {
		this.setEndpointDbRecordLst(endpointDbRecordLst);
	}
	
	public EndpointDbRecordList() {
		this.setEndpointDbRecordLst(new ArrayList<EndPointDbRecord>());
	}
	public List<EndPointDbRecord> getEndpointDbRecordLst() {
		return endpointDbRecordLst;
	}
	public void setEndpointDbRecordLst(List<EndPointDbRecord> endpointDbRecordLst) {
		this.endpointDbRecordLst = endpointDbRecordLst;
	}
	
	public void addEndpointDbRecordLst(final EndPointDbRecord endpointDbRecord) {
		this.endpointDbRecordLst.add(endpointDbRecord);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
