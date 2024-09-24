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


package com.widescope.sqlThunder.utils;

public class FileCharacteristic {
	
	private String folderPath;
	private String absolutePath;
	private String canonicalPath;
	private String relativePath;
	private String fileName;
	private String isFile;
	private String isFolder;
	private String root;
	private String isHidden;
	private String isSymbolicLink;
	private String canExecute;
	private String canRead;
	private String canWrite;
	private String isPresent;
	
	private long creationTime; //the value in milliseconds, since the epoch (1970-01-01T00:00:00Z)
	private long lastAccessTime; //the value in milliseconds, since the epoch (1970-01-01T00:00:00Z)
	private long lastModifiedTime; //the value in milliseconds, since the epoch (1970-01-01T00:00:00Z)
	
	private String isBackedup;
	private String fileKey;
	private String isErrorGettingCharacteristics;
	private String isErrorBackup;
    
	public FileCharacteristic() {

	}
	
	
	public FileCharacteristic(String isPresent) {
		this.setIsPresent(isPresent);
		setIsErrorGettingCharacteristics("N");
		setIsErrorBackup("N");
	}
	
	

	public String getFolderPath() {	return folderPath; }
	public void setFolderPath(String folderPath) { this.folderPath = folderPath; }

	public String getAbsolutePath() { return absolutePath; }
	public void setAbsolutePath(String absolutePath) { this.absolutePath = absolutePath; }

	public String getCanonicalPath() { return canonicalPath; }
	public void setCanonicalPath(String canonicalPath) { this.canonicalPath = canonicalPath; }

	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getIsFile() { return isFile; }
	public void setIsFile(String isFile) { this.isFile = isFile; }

	public String getIsFolder() { return isFolder; }
	public void setIsFolder(String isFolder) { this.isFolder = isFolder; }

	public String getRoot() { return root; }
	public void setRoot(String root) { this.root = root; }

	public String getIsHidden() { return isHidden; }
	public void setIsHidden(String isHidden) { this.isHidden = isHidden; }

	public String getIsSymbolicLink() { return isSymbolicLink; }
	public void setIsSymbolicLink(String isSymbolicLink) { this.isSymbolicLink = isSymbolicLink; }

	public String getCanExecute() { return canExecute; }
	public void setCanExecute(String canExecute) { this.canExecute = canExecute; }

	public String getCanRead() { return canRead; }
	public void setCanRead(String canRead) { this.canRead = canRead; }

	public String getCanWrite() { return canWrite; }
	public void setCanWrite(String canWrite) { this.canWrite = canWrite; }

	public String getIsErrorBackup() {	return isErrorBackup;	}
	public void setIsErrorBackup(String isErrorBackup) { this.isErrorBackup = isErrorBackup; }

	public String getIsErrorGettingCharacteristics() { return isErrorGettingCharacteristics; }
	public void setIsErrorGettingCharacteristics(String isErrorGettingCharacteristics) { this.isErrorGettingCharacteristics = isErrorGettingCharacteristics; }

	public String getFileKey() { return fileKey; }
	public void setFileKey(String fileKey) { this.fileKey = fileKey; }

	public String getIsBackedup() { return isBackedup; }
	public void setIsBackedup(String isBackedup) { this.isBackedup = isBackedup; }

	public long getLastModifiedTime() { return lastModifiedTime; }
	public void setLastModifiedTime(long lastModifiedTime) { this.lastModifiedTime = lastModifiedTime; }

	public long getLastAccessTime() { return lastAccessTime; }
	public void setLastAccessTime(long lastAccessTime) {this.lastAccessTime = lastAccessTime;  }

	public long getCreationTime() { return creationTime; }
	public void setCreationTime(long creationTime) { this.creationTime = creationTime; }

	public String getIsPresent() {	return isPresent; }
	public void setIsPresent(String isPresent) { this.isPresent = isPresent; }

	public String getRelativePath() { return relativePath; }
	public void setRelativePath(String relativePath) { this.relativePath = relativePath; }
	
}
