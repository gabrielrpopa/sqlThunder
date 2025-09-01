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

package com.widescope.rdbmsRepo.database.mongodb.sql.toH2;

import com.google.gson.Gson;

public class H2DbRef {
	private long id ;
	private String user;
	private	String dbUniqueName;
	private	long createdAt;
	private	long closedAt;
	private	String inMem;
	private	int	inMemTime;
	
	public H2DbRef(	final long id,
					final String user,
					final String dbUniqueName,
					final long createdAt,
					final long closedAt,
					final String inMem,
					final int inMemTime) {
		this.setId(id);
		this.setUser(user);
		this.setDbUniqueName(dbUniqueName);
		this.setCreatedAt(createdAt);
		this.setClosedAt(closedAt);
		this.setInMem(inMem);
		this.setInMemTime(inMemTime);
		
	}
	

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }

	public String getDbUniqueName() { return dbUniqueName; }
	public void setDbUniqueName(String dbUniqueName) { this.dbUniqueName = dbUniqueName; }

	public long getCreatedAt() { return createdAt; }
	public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

	public long getClosedAt() { return closedAt; }
	public void setClosedAt(long closedAt) { this.closedAt = closedAt; }

	public String getInMem() { return inMem; }
	public void setInMem(String inMem) { this.inMem = inMem; }

	public int getInMemTime() { return inMemTime; }
	public void setInMemTime(int inMemTime) { this.inMemTime = inMemTime; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
