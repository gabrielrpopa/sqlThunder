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


package com.widescope.rdbmsRepo.database.elasticsearch.objects;

import com.google.gson.Gson;

public class ClusterInfo {
	
	private String errorMessage;
	private int errorCode;
	private String name;
	private String clusterName;
	private String clusterUuid;
	private ClusterVersion clusterVersion;
	
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getClusterName() { return clusterName; }
	public void setClusterName(String clusterName) { this.clusterName = clusterName; }
	public String getClusterUuid() { return clusterUuid; }
	public void setClusterUuid(String clusterUuid) { this.clusterUuid = clusterUuid; }
	public ClusterVersion getClusterVersion() { return clusterVersion; }
	public void setClusterVersion(ClusterVersion clusterVersion) { this.clusterVersion = clusterVersion; }
	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
	public int getErrorCode() { return errorCode; }
	public void setErrorCode(int errorCode) { this.errorCode = errorCode; }
	
	

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
