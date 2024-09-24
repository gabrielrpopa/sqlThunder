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

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.sqlThunder.utils.DateTimeUtils;

public class MachineNode implements RestInterface{
	private int id;
	private String baseUrl;
	private String type;
	private String isReachable;  
	private String isAccepted;
	private String isRegistered;
	
	private long totalMemory;  
	private long freeMemory;  
	private long cpuUsed;
	
	private long noScripts;

	private long lastTimeUpdated;
	private List<String> ipList;
	
	public MachineNode(){
		this.setIpList(new ArrayList<String>());
	}
	
	public MachineNode(	final int id,
						final String baseUrl, 
						final String type,
						final String isAccepted,
						final String isRegistered,
						final String isReachable)
	{
		this.setId(id);
		this.setBaseUrl(baseUrl);
		this.setType(type);
		this.setIsAccepted(isAccepted);
		this.setIsRegistered(isRegistered);
		this.setIsReachable(isReachable);
		
		
		this.setTotalMemory(0);
		this.setFreeMemory(0);
		this.setCpuUsed(0);
		this.setNoScripts(0);
		this.setIpList(new ArrayList<String>());
		this.setLastTimeUpdated(DateTimeUtils.millisecondsSinceEpoch());

	}


	public MachineNode(	final String baseUrl)
	{
		this.setId(-1);
		this.setBaseUrl(baseUrl);
		this.setType("");
		this.setIsAccepted("N");
		this.setIsRegistered("N");
		this.setIsReachable("N");


		this.setTotalMemory(0);
		this.setFreeMemory(0);
		this.setCpuUsed(0);
		this.setNoScripts(0);
		this.setIpList(new ArrayList<String>());
		this.setLastTimeUpdated(DateTimeUtils.millisecondsSinceEpoch());

	}

	public MachineNode(	final int id,
						final String baseUrl, 
						final String type,
						final String isAccepted,
						final String isRegistered,
						final String isReachable,
					    final long totalMemory,
					    final long freeMemory,
					    final long cpuUsed,
					    final long noScripts
					   )
	{
		this.setId(id);
		this.setBaseUrl(baseUrl);
		this.setType(type);
		this.setIsAccepted(isAccepted);
		this.setIsRegistered(isRegistered);
		this.setIsReachable(isReachable);
		this.setTotalMemory(totalMemory);
		this.setFreeMemory(freeMemory);
		this.setCpuUsed(cpuUsed);
		this.setNoScripts(noScripts);
		this.setIpList(new ArrayList<String>());
		this.setLastTimeUpdated(DateTimeUtils.millisecondsSinceEpoch());
	}
	
	public int getId() { return id;	}
	public void setId(int id) { this.id = id; }
	
	public String getBaseUrl() { return baseUrl; }
	public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getIsReachable() { return isReachable; }
	public void setIsReachable(String isReachable) { this.isReachable = isReachable; }

	public long getTotalMemory() { return totalMemory; }
	public void setTotalMemory(long totalMemory) { this.totalMemory = totalMemory; }
	
	public long getFreeMemory() { return freeMemory; }
	public void setFreeMemory(long freeMemory) { this.freeMemory = freeMemory; }
	
	public long getCpuUsed() { return cpuUsed; }
	public void setCpuUsed(long cpuUsed) { this.cpuUsed = cpuUsed; }
	
	public long getNoScripts() { return noScripts; }
	public void setNoScripts(long noScripts) { this.noScripts = noScripts; }

	public long getLastTimeUpdated() { return lastTimeUpdated; }
	public void setLastTimeUpdated(long lastTimeUpdated) { this.lastTimeUpdated = lastTimeUpdated; }

	public List<String> getIpList() { return ipList; }
	public void setIpList(List<String> ipList) { this.ipList = ipList; }


	public void setIsPong(String isPong) {
		if(isPong.compareTo("PONG") == 0) {
			this.isReachable = "Y";
		} else {
			this.isReachable = "N";
		}
	}


	public String getIsAccepted() {
		return isAccepted;
	}

	public void setIsAccepted(String isAccepted) {
		this.isAccepted = isAccepted;
	}

	public String getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(String isRegistered) {
		this.isRegistered = isRegistered;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	
}
