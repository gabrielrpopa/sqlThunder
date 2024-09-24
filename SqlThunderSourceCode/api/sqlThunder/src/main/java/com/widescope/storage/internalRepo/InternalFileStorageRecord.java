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

package com.widescope.storage.internalRepo;


public class InternalFileStorageRecord {

	private	long storageId;
	private long userId;
	private	String machineName;
	private String fileName;
	private String fullFilePath;
	private String storageType;
	private long lastModified;
	private long timeStamp;
	
	
	public InternalFileStorageRecord(final long storageId,
									final long userId,
									final String machineName,
									final String fileName, 
									final String fullFilePath,
									final String storageType,
									final long lastModified,
									final long timeStamp
									)
	{
		this.setStorageId(storageId);
		this.setUserId(userId);
		this.setMachineName(machineName);
		this.setFileName(fileName);
		this.setFullFilePath(fullFilePath);
		this.setStorageType(storageType);
		this.setLastModified(lastModified);
		this.setTimeStamp(timeStamp);
	}

	public long getStorageId() {
		return storageId;
	}
	public void setStorageId(long storageId) {
		this.storageId = storageId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStorageType() {
		return storageType;
	}
	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getFullFilePath() {
		return fullFilePath;
	}
	public void setFullFilePath(String fullFilePath) {
		this.fullFilePath = fullFilePath;
	}
}
