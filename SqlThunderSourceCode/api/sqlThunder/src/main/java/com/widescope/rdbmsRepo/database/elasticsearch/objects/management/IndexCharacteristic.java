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


package com.widescope.rdbmsRepo.database.elasticsearch.objects.management;

import com.google.gson.Gson;

public class IndexCharacteristic {

	private int position;
	private String health;
	private String status;
	private String index;
	private String uuid;
	private String pri;
	private String rep;
	private String docs_count;
	private String docs_deleted;
	private String store_size;
	private String pri_store_size;
	private String type;
    	
	public IndexCharacteristic() {
		this.setPosition(0);
		this.setHealth("");
		this.setStatus("");
		this.setIndex("");
		this.setUuid("");
		this.setPri("");
		this.setRep("");
		this.setDocs_count("");
		this.setDocs_deleted("");
		this.setStore_size("");
		this.setPri_store_size("");
		this.setType("");
	}

	public int getPosition() { return position; }
	public void setPosition(int position) { this.position = position; }
	
	public String getHealth() {	return health; }
	public void setHealth(String health) { this.health = health; }

	public String getStatus() { return status; }
	public void setStatus(String status) {this.status = status; }

	public String getIndex() { return index; }
	public void setIndex(String index) { this.index = index; }

	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }

	public String getPri() {return pri; }
	public void setPri(String pri) { this.pri = pri; }

	public String getRep() { return rep; }
	public void setRep(String rep) { this.rep = rep; }

	public String getDocs_count() {	return docs_count; }
	public void setDocs_count(String docs_count) { this.docs_count = docs_count; }
	
	public String getDocs_deleted() { return docs_deleted; }
	public void setDocs_deleted(String docs_deleted) { this.docs_deleted = docs_deleted; }
	
	public String getStore_size() { return store_size; }
	public void setStore_size(String store_size) { this.store_size = store_size; }

	public String getPri_store_size() { return pri_store_size; }
	public void setPri_store_size(String pri_store_size) { this.pri_store_size = pri_store_size; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
