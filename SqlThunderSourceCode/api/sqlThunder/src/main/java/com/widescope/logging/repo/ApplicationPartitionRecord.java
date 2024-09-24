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

import com.google.gson.Gson;

public class ApplicationPartitionRecord {

	private long partitionId;
	private long  applicationId;
	private String file;
	private long fromTime;
	private long toTime;
	
	public ApplicationPartitionRecord(long partitionId, 
								long applicationId,
								String file, 
								long fromTime, 
								long toTime) {
		this.setPartitionId(partitionId);
		this.setApplicationId(applicationId);
		this.setFile(file);
		this.setFromTime(fromTime);
		this.setToTime(toTime);
	}
	
	public ApplicationPartitionRecord(	) {
		this.setPartitionId(-1);
		this.setApplicationId(-1);
		this.setFile(null);
		this.setFromTime(-1);
		this.setToTime(-1);
	}

	public long getPartitionId() { return partitionId; }
	public void setPartitionId(long partitionId) { this.partitionId = partitionId; }

	public long getApplicationId() { return applicationId; }
	public void setApplicationId(long applicationId) { this.applicationId = applicationId; }

	public String getFile() { return file; }
	public void setFile(String file) { this.file = file; }

	public long getFromTime() { return fromTime; }
	public void setFromTime(long fromTime) { this.fromTime = fromTime; }
	
	public long getToTime() { return toTime; }
	public void setToTime(long toTime) { this.toTime = toTime; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
