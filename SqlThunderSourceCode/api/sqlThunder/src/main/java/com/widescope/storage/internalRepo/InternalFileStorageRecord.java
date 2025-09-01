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


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.persistence.execution.PersistencePrivilegeList;
import com.widescope.sqlThunder.rest.RestInterface;

public class InternalFileStorageRecord implements RestInterface {
	private	long storageId;
	private	long backupId;
	private String backupName;  /*This is the file name of the backup, sored in a backup folder ending with the backup id*/
	private String fileName; /*original file name of the file that is backed up*/
	private String fullFilePath;  /*This is the path the file originally exists on the host for backup, not including file name, only folder */
	private String mimeType;
	private String function;  /*file function specific*/
	private long size; /*size in bytes*/
	private long lastModified;
	private long timeStamp;

	public InternalFileStorageRecord(final long storageId,
									 long backupId) {
		this.setStorageId(storageId);
		this.setBackupId(backupId);
		this.setFileName(null);
		this.setFullFilePath(null);
		this.setLastModified(-1);
		this.setTimeStamp(-1);
		this.setMimeType(null);
		this.setFunction(null);
		this.setSize(-1);
	}
	public InternalFileStorageRecord(final long storageId,
									 final long backupId,
									 final String backupName,
									 final String fileName,
									 final String fullFilePath,
									 final long size,
									 final long lastModified,
									 final long timeStamp,
									 final String mimeType,
									 final String function)
	{
		this.setStorageId(storageId);
		this.setBackupId(backupId);
		this.setBackupName(backupName);
		this.setFileName(fileName);
		this.setFullFilePath(fullFilePath);
		this.setSize(size);
		this.setLastModified(lastModified);
		this.setTimeStamp(timeStamp);
		this.setMimeType(mimeType);
		this.setFunction(function);
	}

	public long getStorageId() { return storageId; }
	public void setStorageId(long storageId) { this.storageId = storageId; }
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName;}
	public long getLastModified() { return lastModified; }
	public String getBackupName() { return backupName; }
	public void setBackupName(String backupName) { this.backupName = backupName; }
	public void setLastModified(long lastModified) { this.lastModified = lastModified;}
	public long getTimeStamp() { return timeStamp; }
	public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }
	public String getFullFilePath() { return fullFilePath; }
	public void setFullFilePath(String fullFilePath) { this.fullFilePath = fullFilePath; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
	public String getFunction() { return function; }
	public void setFunction(String function) { this.function = function; }
    public long getBackupId() { return backupId; }
    public void setBackupId(long backupId) { this.backupId = backupId; }
    public long getSize() { return size; }
	public void setSize(long size) { this.size = size; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
