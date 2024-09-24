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

package com.widescope.rdbmsRepo.database.mongodb.repo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MongoCollectionInfo {
	private String name;
	private long countObjects;
	private String type;  // SYSTEM or USER
	private String subType;  // COLLECTION or BUCKET
	private String objSignature;

	public MongoCollectionInfo(final String name,
								final long countObjects,
								final String type,
								final String subType,
								final String objSignature) {
		this.setName(name);
		this.setCountObjects(countObjects);
		this.setType(type);
		this.setSubType(subType);
		this.setObjSignature(objSignature);
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public long getCountObjects() { return countObjects; }
	public void setCountObjects(long countObjects) { this.countObjects = countObjects; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public String getSubType() { return subType; }
	public void setSubType(String subType) { this.subType = subType; }
	
	public String getObjSignature() { return objSignature; }
	public void setObjSignature(String objSignature) { this.objSignature = objSignature; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
