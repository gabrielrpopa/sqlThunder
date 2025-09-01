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

package com.widescope.logging.loggingDB;

import com.google.gson.Gson;


public class LogRecordIncoming {
	private long applicationId;
	private String hostName;
	private long userId;
	private long timestamp;
	private String message;
	private String messageType;
	private String artifactName;
	private String artifactType;
	

	public LogRecordIncoming(	final long applicationId,
								final String hostName,
								final long userId,
								final long timestamp,
								final String message,
								final String messageType,
								final String artifactName,
								final String artifactType
								)
	{
		this.setApplicationId(applicationId);
		this.setHostName(hostName);
		this.setUserId(userId);
		this.setTimestamp(timestamp);
		this.setMessage(message);
		this.setMessageType(messageType);
		this.setArtifactName(artifactName);
		this.setArtifactType(artifactType);
	}
	
	public long getApplicationId() { return applicationId; }
	public void setApplicationId(long applicationId) { this.applicationId = applicationId; }

	
	public String getHostName() { return hostName; }
	public void setHostName(String hostName) { this.hostName = hostName; }

	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	
	public String getMessageType() { return messageType; }
	public void setMessageType(String messageType) { this.messageType = messageType; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
	public String getArtifactType() { return artifactType; }
	public void setArtifactType(String artifactType) { this.artifactType = artifactType; }

	public String getArtifactName() { return artifactName; }
	public void setArtifactName(String artifactName) { this.artifactName = artifactName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	
	



	

}
