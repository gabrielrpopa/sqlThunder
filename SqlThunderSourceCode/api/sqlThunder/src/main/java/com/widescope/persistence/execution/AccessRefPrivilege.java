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

package com.widescope.persistence.execution;


import com.widescope.sqlThunder.rest.RestInterface;

public class AccessRefPrivilege implements RestInterface {

	private	long privilegeId;
	private long backupId;
	private long userId;
	private String privilegeType;
	
	public AccessRefPrivilege(final long privilegeId,
							  final long backupId,
							  final long userId,
							  final String privilegeType){
		this.setPrivilegeId(privilegeId);
		this.setBackupId(backupId);
		this.setUserId(userId);
		this.setPrivilegeType(privilegeType);
		
	}


	public AccessRefPrivilege(final long backupId,
							  final long userId){
		this.setPrivilegeId(-1);
		this.setBackupId(backupId);
		this.setUserId(userId);
		this.setPrivilegeType(PersistencePrivilege.pTypeNone);

	}

	public long getPrivilegeId() { return privilegeId; }
	public void setPrivilegeId(long privilegeId) { this.privilegeId = privilegeId; }

	public long getBackupId() { return backupId; }
	public void setBackupId(long backupId) { this.backupId = backupId; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public String getPrivilegeType() { return privilegeType; }
	public void setPrivilegeType(String privilegeType) { this.privilegeType = privilegeType; }
}
