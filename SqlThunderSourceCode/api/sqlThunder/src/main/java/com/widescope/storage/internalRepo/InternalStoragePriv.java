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


public class InternalStoragePriv {

	private	long privId;
	private long storageId;
	private long userId;
	private String privType;
	
	public InternalStoragePriv(	final long privId, 
									final long storageId, 
									final long userId, 
									final String privType){
		this.setPrivId(privId);
		this.setStorageId(storageId);
		this.setUserId(userId);
		this.setPrivType(privType);
		
	}

	public long getPrivId() { return privId; }
	public void setPrivId(long privId) { this.privId = privId; }

	public long getStorageId() { return storageId; }
	public void setStorageId(long storageId) { this.storageId = storageId; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public String getPrivType() { return privType; }
	public void setPrivType(String privType) { this.privType = privType; }
}
