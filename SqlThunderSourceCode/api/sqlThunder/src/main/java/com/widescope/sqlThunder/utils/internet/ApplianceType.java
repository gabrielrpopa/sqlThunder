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


package com.widescope.sqlThunder.utils.internet;

import com.widescope.sqlThunder.utils.StringUtils;

public class ApplianceType {

	private short applianceRelatioship;
	public short getApplianceRelatioship() { return applianceRelatioship; }
	public void setApplianceRelatioship(short applianceRelatioship) { this.applianceRelatioship = applianceRelatioship; }
	
	private String host;
	public String getHost() { return host; }
	public void setHost(String host) { this.host = host; }
	
    private int port;
	public int getPort() { return port; }
	public void setPort(int port) {	this.port = port; }
	
	private String httpPrefix;
	public String getHttpPrefix() { return httpPrefix; }
	public void setHttpPrefix(String httpPrefix) { this.httpPrefix = httpPrefix; }
	
	private String serverName;
	public String getServerName() { return serverName; }
	public void setServerName(String serverName) { this.serverName = serverName; }
	
	private long memoryUsed;
	public long getMemoryUsed() { return memoryUsed; }
	public void setMemoryUsed(long memoryUsed) { this.memoryUsed = memoryUsed; }
	
	private long internalMemoryUsed;
	public long getInternalMemoryUsed() { return internalMemoryUsed; }
	public void setInternalMemoryUsed(long internalMemoryUsed) { this.internalMemoryUsed = internalMemoryUsed; }
	
	private long memoryAvailable;
	public long getMemoryAvailable() { return memoryAvailable; }
	public void setMemoryAvailable(long memoryAvailable) { this.memoryAvailable = memoryAvailable; }
	
	private int itemStored;
	public int getItemStored() { return itemStored;	}
	public void setItemStored(int itemStored) { this.itemStored = itemStored; }
	
	public ApplianceType(String host, int port, String httpPrefix, String serverName, long memoryUsed, long internalMemoryUsed, long memoryAvailable, int itemStored)
	{
		this.host = host;
		this.port = port;
		this.httpPrefix = httpPrefix;
		this.serverName = serverName;
		this.memoryUsed = memoryUsed;
		this.internalMemoryUsed = internalMemoryUsed;
		this.memoryAvailable = memoryAvailable;
		this.itemStored = itemStored;
				
	}
	
	
	public static String getHash(ApplianceType applianceType)
	{
		String toHash;
		
		try	{
			toHash = applianceType.host + applianceType.port + applianceType.serverName + applianceType.getHttpPrefix();
		}
		catch(Exception ex)	{
			return null;
		}
		
		try	{
			toHash = applianceType.host + applianceType.port + applianceType.serverName + applianceType.getHttpPrefix();
			return StringUtils.getStringHashValue(toHash);
		} catch(Exception ex) {
			return StringUtils.getStringUniqueID(toHash);
		}
	}
}
