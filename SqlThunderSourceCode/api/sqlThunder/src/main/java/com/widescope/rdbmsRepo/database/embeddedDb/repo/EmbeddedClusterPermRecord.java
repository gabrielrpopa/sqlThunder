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

package com.widescope.rdbmsRepo.database.embeddedDb.repo;


import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class EmbeddedClusterPermRecord implements RestInterface{
	private long accessId;
    private long clusterId;
    private long userId;

    public EmbeddedClusterPermRecord(final long accessId,
    								final long clusterId,
		                            final long userId
		                            ) {
    	this.setAccessId(accessId);
        this.setClusterId(clusterId);
        this.setUserId(userId);
    }

    public long getAccessId() {	return accessId; }
	public void setAccessId(long accessId) { this.accessId = accessId; }
	
	public long getClusterId() {	return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
