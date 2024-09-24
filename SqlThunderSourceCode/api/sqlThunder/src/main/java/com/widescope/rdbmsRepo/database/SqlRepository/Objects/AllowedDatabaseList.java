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
import com.widescope.rest.RestInterface;

public class AllowedDatabaseList extends AllowedDatabase implements RestInterface {

	private List<String> database;
		
	public AllowedDatabaseList() {
		database = new ArrayList<>(getDatabaseList()); 
	}

	public List<String> getDatabase() {	return database; }
	public void setDatabase(List<String> database) { this.database = database; }
	
	public static boolean isDatabase(String db) { 
		AllowedDatabaseList a = new AllowedDatabaseList();
		return a.getDatabaseList().contains(db.toUpperCase());
	}
	
}
