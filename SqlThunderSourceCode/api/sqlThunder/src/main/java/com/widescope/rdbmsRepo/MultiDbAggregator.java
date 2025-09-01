/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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

package com.widescope.rdbmsRepo.database;

import java.util.concurrent.Callable;


import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;

public class MultiDbAggregator implements Callable<SqlRepoExecReturn> {

	private final SqlRepoDatabase sqlRepoDatabase;
	
	final long sqlId;
	final String jsonBody;
	final long batchCount;
	final String user;
	final String persist;
	
	public MultiDbAggregator(	final SqlRepoDatabase sqlRepoDatabase,
								final long sqlId,
								final String jsonBody,
								final long batchCount,
								final String user,
								final String persist
							) {
		this.sqlRepoDatabase=sqlRepoDatabase;
		this.sqlId = sqlId;
		this.jsonBody =jsonBody;
		this.batchCount = batchCount;
		this.user = user;
		this.persist = persist;
	}

	@Override
	public SqlRepoExecReturn call() throws Exception {

		return SqlRepoExecWrapper.execSqlRepoForResultQueryAsList(sqlId,
																	sqlRepoDatabase.getSchemaName(), 
																	jsonBody, 
																	batchCount,
																	user,
																	persist);
		
	}
}
