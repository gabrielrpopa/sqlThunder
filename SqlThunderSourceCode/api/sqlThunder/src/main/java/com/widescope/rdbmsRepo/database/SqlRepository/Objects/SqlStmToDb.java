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

import com.google.gson.Gson;

public class SqlStmToDb {
	private int sqlId;
	private int dbId;
	
	public SqlStmToDb(final int sqlId, final int dbId) {
		this.setSqlId(sqlId);
		this.setDbId(dbId);
	}
	public SqlStmToDb() {
		this.setSqlId(-1);
		this.setDbId(-1);
	}
	public int getSqlId() {	return sqlId; }
	public void setSqlId(int sqlId) { this.sqlId = sqlId; }
	public int getDbId() { return dbId; }
	public void setDbId(int dbId) { this.dbId = dbId; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
