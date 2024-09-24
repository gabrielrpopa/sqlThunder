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

package com.widescope.rdbmsRepo.database.elasticsearch.repo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class ElasticHost {

	private	int hostId;
	private	int clusterId;
	
	private String server;
	private int port;
	private String protocol;
	
	private String description;
	
	
	private int tunnelLocalPort;
	private String tunnelRemoteHostAddress;
    private int  tunnelRemoteHostPort;
    private String  tunnelRemoteUser;
    private String tunnelRemoteUserPassword;
    private String tunnelRemoteRsaKey;
	
	public ElasticHost(final int hostId, 
 					   final int clusterId, 
					   final String server,
					   final int port,
					   final String protocol,
					   final String description
					  )
	{
		this.setHostId(hostId);
		this.setClusterId(clusterId);
		this.setServer(server);
		this.setPort(port);
		this.setProtocol(protocol);
		setDescription(description);
	}
	
	
	public ElasticHost(){
	}

	public int getHostId() { return hostId; }
	public void setHostId(int hostId) { this.hostId = hostId; }

	public int getClusterId() {	return clusterId; }
	public void setClusterId(int clusterId) { this.clusterId = clusterId; }
	
	public String getServer() { return server; }
	public void setServer(String server) { this.server = server; }

	public String getProtocol() { return protocol; }
	public void setProtocol(String protocol) { this.protocol = protocol; }
	
	public int getPort() { return port; }
	public void setPort(int port) { this.port = port; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public int getTunnelLocalPort() { return tunnelLocalPort; }
	public void setTunnelLocalPort(int tunnelLocalPort) { this.tunnelLocalPort = tunnelLocalPort; }

	public String getTunnelRemoteHostAddress() { return tunnelRemoteHostAddress; }
	public void setTunnelRemoteHostAddress(String tunnelRemoteHostAddress) { this.tunnelRemoteHostAddress = tunnelRemoteHostAddress; }

	public int getTunnelRemoteHostPort() { return tunnelRemoteHostPort; }
	public void setTunnelRemoteHostPort(int tunnelRemoteHostPort) { this.tunnelRemoteHostPort = tunnelRemoteHostPort; }

	public String getTunnelRemoteUser() { return tunnelRemoteUser; }
	public void setTunnelRemoteUser(String tunnelRemoteUser) { this.tunnelRemoteUser = tunnelRemoteUser; }

	public String getTunnelRemoteUserPassword() { return tunnelRemoteUserPassword; }
	public void setTunnelRemoteUserPassword(String tunnelRemoteUserPassword) { this.tunnelRemoteUserPassword = tunnelRemoteUserPassword; }

	public String getTunnelRemoteRsaKey() { return tunnelRemoteRsaKey; }
	public void setTunnelRemoteRsaKey(String tunnelRemoteRsaKey) { this.tunnelRemoteRsaKey = tunnelRemoteRsaKey; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ElasticHost toElasticHost(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ElasticHost.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
