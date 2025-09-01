/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.cluster.management.clusterManagement.ClusterDb;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;


public class MachineNodeList implements RestInterface{

	private List<MachineNode> serverCounterpartyList;
	
	public void addServer(MachineNode s ) {
		serverCounterpartyList.add(s);
	}
	
	public void addServers(List<MachineNode> _serverCounterpartyList ) {
		serverCounterpartyList.addAll(_serverCounterpartyList);
	}
	
	public MachineNodeList() {
		serverCounterpartyList = new ArrayList<MachineNode>();
	}
	
	public MachineNodeList(Map<String, MachineNode> s) {
		serverCounterpartyList = new ArrayList<MachineNode>(s.values());
	}
	
	public MachineNodeList(List<MachineNode> s) {
		serverCounterpartyList = new ArrayList<MachineNode>(s);
	}
	
	public List<MachineNode> getServerCounterpartyList() { return serverCounterpartyList; }
	public void setServerCounterpartyList( final List<MachineNode> serverCounterpartyList) { this.serverCounterpartyList = serverCounterpartyList; }
	public void addServerCounterparty( MachineNode loggingNode) { this.serverCounterpartyList.add(loggingNode); }




	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static MachineNodeList toMachineNodeList(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, MachineNodeList.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
