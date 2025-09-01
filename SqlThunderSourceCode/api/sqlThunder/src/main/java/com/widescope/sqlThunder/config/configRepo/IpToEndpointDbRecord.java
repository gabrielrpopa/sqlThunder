package com.widescope.sqlThunder.config.configRepo;

import com.google.gson.Gson;


public class IpToEndpointDbRecord {

	private int id;
	private int idEndpoint;
	private String ipAddress;
	
	public IpToEndpointDbRecord (	final int id,
									final int idEndpoint,
							 		final String ipAddress
								) {
		this.setId(id);
		this.setIdEndpoint(idEndpoint);
		this.setIpAddress(ipAddress);
	}

	public int getId() { return id; }
	public void setId(int id) {	this.id = id; }

	public int getIdEndpoint() { return idEndpoint; }
	public void setIdEndpoint(int idEndpoint) { this.idEndpoint = idEndpoint; }

	public String getIpAddress() { return ipAddress; }
	public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
