package com.widescope.sqlThunder.config.configRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class EndPointDbRecord implements RestInterface{

	private int id;
	private String endpoint;
	private List<IpToEndpointDbRecord> ipToEndpointDbRecordLst;
	
	public EndPointDbRecord (final int id,
							 final String endpoint,
							 final List<IpToEndpointDbRecord> ipToEndpointDbRecord
							 ) {
		this.setId(id);
		this.setEndpoint(endpoint);
		this.setIpToEndpointDbRecordLst(ipToEndpointDbRecord);
	}
	
	public EndPointDbRecord () {
		this.setId(-1);
		this.setEndpoint("");
		this.setIpToEndpointDbRecordLst(new ArrayList<IpToEndpointDbRecord>());
	}

	public int getId() { return id; }
	public void setId(int id) {	this.id = id; }

	public String getEndpoint() { return endpoint; }
	public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
	
	public List<IpToEndpointDbRecord> getIpToEndpointDbRecordLst() {
		return ipToEndpointDbRecordLst;
	}

	public void setIpToEndpointDbRecordLst(final List<IpToEndpointDbRecord> ipToEndpointDbRecordLst) {
		this.ipToEndpointDbRecordLst = ipToEndpointDbRecordLst;
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
