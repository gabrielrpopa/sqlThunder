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

public class TableRow {
	
	private Map<Integer, Object> tRow;
	public TableRow() {
		this.tRow = new HashMap<Integer, Object>();
	}
	public TableRow(final Map<Integer, Object> tRow) {
		this.tRow = tRow;
	}

	public Map<Integer, Object> getRow() { return tRow; }
	public void settRow(final Map<Integer, Object> tRow) { this.tRow = tRow; }
	public void addCell(final Integer index, 
						final Object tableCell) { 
		this.tRow.put(index, tableCell) ; 
	}

	
	@Override
    public String toString() {
		return new Gson().toJson(this);
    }
	
	public static String tableRowToJson(final TableRow tableRow) {
		Gson gson = new Gson();
		return gson.toJson(tableRow);
	}
	
}
