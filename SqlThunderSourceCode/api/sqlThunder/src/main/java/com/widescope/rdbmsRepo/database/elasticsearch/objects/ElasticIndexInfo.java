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

package com.widescope.rdbmsRepo.database.elasticsearch.objects;

import com.google.gson.Gson;

public class ElasticIndexInfo {
	
	private String health;
	private String status;
	private String index;
	private String uuid;
	private String pri;
	private String rep;
	private String docsCount;
	private String docsDeleted;
	private String storeSize;
	private String priStoreSize;
	public String getHealth() {	return health;}
	public void setHealth(String health) { this.health = health; }
	public String getStatus() { 	return status; }
	public void setStatus(String status) {	this.status = status; }
	public String getIndex() { return index; }
	public void setIndex(String index) { this.index = index; }
	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }
	public String getPri() { return pri; }
	public void setPri(String pri) { this.pri = pri; }
	public String getRep() { return rep; }
	public void setRep(String rep) { this.rep = rep; }
	public String getDocsCount() { return docsCount; }
	public void setDocsCount(String docsCount) { this.docsCount = docsCount; }
	public String getDocsDeleted() { return docsDeleted; }
	public void setDocsDeleted(String docsDeleted) { this.docsDeleted = docsDeleted; }
	public String getStoreSize() { return storeSize; }
	public void setStoreSize(String storeSize) { this.storeSize = storeSize; }
	public String getPriStoreSize() { return priStoreSize; }
	public void setPriStoreSize(String priStoreSize) { this.priStoreSize = priStoreSize; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
