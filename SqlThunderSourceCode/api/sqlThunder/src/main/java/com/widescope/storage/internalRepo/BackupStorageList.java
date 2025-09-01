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
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class BackupStorageList implements RestInterface{

	private List<BackupStorage> backupStorageLst;

	public BackupStorageList(List<BackupStorage> backupStorageLst) {
		this.setBackupStorageList(backupStorageLst);
	}

	public BackupStorageList() {
		this.setBackupStorageList(new ArrayList<>());
	}
	public List<BackupStorage> getBackupStorageList() { return backupStorageLst; }
	public void setBackupStorageList(List<BackupStorage> backupStorageLst) { this.backupStorageLst = backupStorageLst; }
	public void addBackupStorageList(List<BackupStorage> backupStorageLst) { this.backupStorageLst.addAll(backupStorageLst); }
	public void addBackupStorage(BackupStorage backupStorage) { this.backupStorageLst.add(backupStorage); }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
