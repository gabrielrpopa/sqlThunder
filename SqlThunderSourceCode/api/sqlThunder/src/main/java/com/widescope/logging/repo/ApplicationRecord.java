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


package com.widescope.logging.repo;


import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class ApplicationRecord implements RestInterface{
	
	
	private long applicationId;
	private String application;
	private String partitionType;
	private String repositoryType;
	private long repositoryId;
	private List<ApplicationPartitionRecord> applicationPartitionRecordList;
	
	public ApplicationRecord(	final long applicationId, 
								final String application, 
								final String partitionType, 
								final String repositoryType,
								final long repositoryId, 
								final List<ApplicationPartitionRecord> applicationPartitionRecordList) {
		this.setApplicationId(applicationId);
		this.setApplication(application);
		this.setPartitionType(partitionType);
		this.setRepositoryType(repositoryType);
		this.setRepositoryId(repositoryId);
		this.setApplicationPartitionRecordList(applicationPartitionRecordList);
	}
	
	public ApplicationRecord() {
		this.setApplicationId(-1);
		this.setApplication(null);
		this.setPartitionType(null);
		this.setRepositoryType(null);
		this.setRepositoryId(-1);
		this.setApplicationPartitionRecordList(null);
	}

	

	public long getApplicationId() { return applicationId; }
	public void setApplicationId(long applicationId) { this.applicationId = applicationId; }
	
	public String getApplication() { return application; }
	public void setApplication(String application) { this.application = application; }

	public String getPartitionType() { return partitionType; }
	public void setPartitionType(String partitionType) { this.partitionType = partitionType; }

	public String getRepositoryType() { return repositoryType; }
	public void setRepositoryType(String repositoryType) { this.repositoryType = repositoryType; }
	
	public long getRepositoryId() { return repositoryId; }
	public void setRepositoryId(long repositoryId) { this.repositoryId = repositoryId; }
	
	public List<ApplicationPartitionRecord> getApplicationPartitionRecordList() {	return applicationPartitionRecordList; }
	public void setApplicationPartitionRecordList(List<ApplicationPartitionRecord> applicationPartitionRecordList) { this.applicationPartitionRecordList = applicationPartitionRecordList; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
