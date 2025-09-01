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

package com.widescope.rdbmsRepo.database.table.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Defines the header of a database table, which has a number of columns defined by TableColumn
 * @author popa_
 *
 */
public class TableHeader {
	private Map<Integer, TableColumn> hList; // <index, TableColumn>
	public Map<Integer, TableColumn> gethList() { return hList; }
	public void sethList(final Map<Integer, TableColumn> hList) { this.hList = hList; }
	private Map<String, Integer> hListNameToIndex; // <columnName, index>
	private Map<Integer, String> hListIndexToName; // <index, columnName>
	public Map<String, Integer> gethListNameToIndex() { return hListNameToIndex; }
	public Map<Integer, String> gethListIndexToName() { return hListIndexToName; }

	public void addColumn(	final int index, 
							final TableColumn col) { 
		this.hList.put(index, col);
		this.hListNameToIndex.put(col.getColumnName(), index);
		this.hListIndexToName.put(index, col.getColumnName());
	}

	public TableHeader() {
		this.hList = new HashMap<Integer, TableColumn>();
		this.hListNameToIndex = new HashMap<String, Integer>();
		this.hListIndexToName = new HashMap<Integer, String>();
	}
	
	public TableHeader(final Map<Integer, TableColumn> hList) {
		this.hList = hList;
		for (Map.Entry<Integer, TableColumn> entry : hList.entrySet()) {
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		    this.hListNameToIndex.put(entry.getValue().getColumnName(), entry.getKey());
			this.hListIndexToName.put(entry.getKey(), entry.getValue().getColumnName());
		}
	}

	@Override
    public String toString() {
		return new Gson().toJson(this);
	}



}
