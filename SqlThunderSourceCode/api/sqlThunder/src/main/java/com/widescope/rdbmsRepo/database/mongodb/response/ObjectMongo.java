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

package com.widescope.rdbmsRepo.database.mongodb.response;

import com.google.gson.Gson;

public class ObjectMongo {
	private String id;
	private String syntheticId;
	private String objectString;
	private long countProcess;
	
	public ObjectMongo(	final String id,
						final String syntheticId,
						final String objectString,
						final long countProcess) {
		this.setId(id);
		this.setSyntheticId(syntheticId);
		this.setObjectString(objectString);
		this.setCountProcess(countProcess);
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getSyntheticId() { return syntheticId; }
	public void setSyntheticId(String syntheticId) { this.syntheticId = syntheticId; }

	public String getObjectString() { return objectString; }

	public void setObjectString(String objectString) { this.objectString = objectString; }

	public long getCountProcess() { return countProcess; }
	public void setCountProcess(long countProcess) { this.countProcess = countProcess; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
