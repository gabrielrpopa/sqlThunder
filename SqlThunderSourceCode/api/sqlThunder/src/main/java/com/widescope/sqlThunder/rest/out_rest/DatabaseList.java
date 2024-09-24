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

package com.widescope.sqlThunder.rest.out_rest;

import java.util.List;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;

public class DatabaseList implements com.widescope.rest.RestInterface
{
	private List<DbConnectionInfo> listOfDbs;
	public DatabaseList() { }
	public DatabaseList(final List<DbConnectionInfo> listOfDbs) { this.listOfDbs = listOfDbs; }
	public List<DbConnectionInfo> getDatabaseList() { return this.listOfDbs; }
	public void setDatabaseList(final List<DbConnectionInfo> listOfDbs) { this.listOfDbs = listOfDbs; }	
}
