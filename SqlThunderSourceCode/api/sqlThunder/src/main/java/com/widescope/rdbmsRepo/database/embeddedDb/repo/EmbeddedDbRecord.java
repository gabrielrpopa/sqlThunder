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

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;

public class EmbeddedDbRecord implements RestInterface {
    private long dbId;
    private	String fileName;
    private String type;
    private long userId;
    private long clusterId;
    private String path;
    private String info;
    private List<TableDefinition> tableDefinitions;



	public EmbeddedDbRecord() {

    }

    public EmbeddedDbRecord(final long dbId,
                            final String fileName,
                            final String type,
                            final long userId,
                            final long clusterId,
                            final String path,
                            final String info) {
        this.setDbId(dbId);
        this.setFileName(fileName);
        this.setType(type);
        this.setUserId(userId);
        this.setClusterId(clusterId);
        this.setPath(path);
        this.setInfo(info);
        this.setTableDefinitions(new ArrayList<TableDefinition>());
    }
    
    public EmbeddedDbRecord(final long dbId) {
		this.setDbId(dbId);
		this.setFileName(null);
		this.setType(null);
		this.setUserId(-1);
		this.setClusterId(-1);
		this.setPath(path);
		this.setInfo(info);
		this.setTableDefinitions(new ArrayList<TableDefinition>());
		
	}

	public long getDbId() {	return dbId; }
	public void setDbId(long dbId) { this.dbId = dbId; }

	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

	public void setTableDefinitions(List<TableDefinition> tableDefinitions) { this.tableDefinitions = tableDefinitions; }
	public void addTableDefinitions(TableDefinition tableDefinitions) { this.tableDefinitions.add(tableDefinitions); }
	public List<TableDefinition> getTableDefinitions() { return tableDefinitions; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	


}
