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

package com.widescope.rdbmsRepo.database.SqlRepository.Objects;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class RdbmsRepoDatabaseList implements RestInterface {
	private List<SqlRepoDatabase> sqlRepoDatabaseList;
	public List<SqlRepoDatabase> getSqlRepoDatabaseList() {	return sqlRepoDatabaseList;	}
	public void setSqlRepoDatabaseList(List<SqlRepoDatabase> sqlRepoDatabaseList) {	this.sqlRepoDatabaseList = sqlRepoDatabaseList;	}
	
	public RdbmsRepoDatabaseList(SqlRepoDatabase sqlRepoDatabase) {
		sqlRepoDatabaseList = new ArrayList<>();
		sqlRepoDatabaseList.add(sqlRepoDatabase);
	}

	public RdbmsRepoDatabaseList(List<SqlRepoDatabase> sqlRepoDatabaseList) {
		this.sqlRepoDatabaseList = sqlRepoDatabaseList;
	}
	
	public RdbmsRepoDatabaseList() {
		this.sqlRepoDatabaseList = new ArrayList<>();
	}
	
	public void addSqlRepoDatabase(SqlRepoDatabase sqlRepoDatabase)	{
		sqlRepoDatabaseList.add(sqlRepoDatabase);
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public void blockPassword() {
		for(SqlRepoDatabase x: sqlRepoDatabaseList) {
			x.setSchemaPassword("**********");
			x.setTunnelRemoteHostUserPassword("**********");
			x.setTunnelRemoteHostRsaKey("**********");;
		}
	}
}

