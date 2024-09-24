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

public class SqlToDatabaseSchemaMappingList {
	private List<SqlStmToRdbmsSchema> sqlToDatabaseSchemaMappingList;
	public List<SqlStmToRdbmsSchema> getSqlRepoDatabaseList() {	return sqlToDatabaseSchemaMappingList;	}
	public void setSqlRepoDatabaseList(final List<SqlStmToRdbmsSchema> sqlToDatabaseSchemaMappingList) {	this.sqlToDatabaseSchemaMappingList = sqlToDatabaseSchemaMappingList;	}
	public SqlToDatabaseSchemaMappingList()	{
		sqlToDatabaseSchemaMappingList = new ArrayList<SqlStmToRdbmsSchema>();
	}
	
	public void addSqlToDatabaseSchemaMappingList(final SqlStmToRdbmsSchema sqlToDatabaseSchemaMapping) {
		sqlToDatabaseSchemaMappingList.add(sqlToDatabaseSchemaMapping);
	}
}
