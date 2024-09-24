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


package com.widescope.rdbmsRepo.database.table.scripts;

import java.util.HashMap;
import java.util.Map;

import com.widescope.rdbmsRepo.database.table.model.Table;

public class IndexedTable {
	 
	private Table table;
	private Map<Integer, Object> lIndex;
	
	public IndexedTable() {
		lIndex = new HashMap<Integer, Object>();
	}

	public IndexedTable(final Table table, final Map<Integer, Object> lIndex) {
		this.table = table;
		this.lIndex = lIndex;
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(final Table table) {
		this.table = table;
	}

	public Map<Integer, Object> getlIndex() { return lIndex; }
	public void setlIndex(final Map<Integer, Object> lIndex) { this.lIndex = lIndex; }
	
	public void addStringIndex(final Integer id, final Index<String> lIndex) { this.lIndex.put(id, lIndex) ; }
	public void addIntegerIndex(final Integer id, final Index<Integer> lIndex) { this.lIndex.put(id, lIndex) ; }
	public void addFloatIndex(final Integer id, final Index<Float> lIndex) { this.lIndex.put(id, lIndex) ; }
	public void addDoubleIndex(final Integer id, final Index<Double> lIndex) { this.lIndex.put(id, lIndex) ; }
	public void addBooleanIndex(final Integer id,final Index<Boolean> lIndex) { this.lIndex.put(id, lIndex) ; }
	public void addLongIndex(final Integer id, final Index<Long> lIndex) { this.lIndex.put(id, lIndex) ; }
	
}
