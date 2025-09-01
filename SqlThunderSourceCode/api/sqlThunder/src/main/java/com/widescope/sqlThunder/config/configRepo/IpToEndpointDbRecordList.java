package com.widescope.sqlThunder.config.configRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class IpToEndpointDbRecordList implements RestInterface{
	private List<IpToEndpointDbRecord> ipToEndpointDbRecordLst;
	public IpToEndpointDbRecordList(final List<IpToEndpointDbRecord> ipToEndpointDbRecordLst) {
		this.setIpToEndpointDbRecordLst(ipToEndpointDbRecordLst);
	}
	
	public IpToEndpointDbRecordList() {
		this.setIpToEndpointDbRecordLst(new ArrayList<IpToEndpointDbRecord>());
	}
	public List<IpToEndpointDbRecord> getIpToEndpointDbRecordLst() {
		return ipToEndpointDbRecordLst;
	}

	public void setIpToEndpointDbRecordLst(List<IpToEndpointDbRecord> endpointDbRecordLst) {
		this.ipToEndpointDbRecordLst = endpointDbRecordLst;
	}
	
	public void addIpToEndpointDbRecordLst(final IpToEndpointDbRecord ipToEndpointDbRecord) {
		this.ipToEndpointDbRecordLst.add(ipToEndpointDbRecord);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
