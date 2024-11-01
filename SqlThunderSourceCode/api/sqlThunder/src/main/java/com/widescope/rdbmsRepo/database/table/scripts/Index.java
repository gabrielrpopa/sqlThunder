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


import java.util.List;
import java.util.TreeSet;

import com.widescope.rdbmsRepo.database.table.model.TableColumn;

public class Index<T> {
	
	private TableColumn tableColumn;
	private TreeSet<T> tree = new TreeSet<T>();  
	
	private Index(final List<T> list)	{
        this.tree.addAll(list);
	}

	private Index()	{
		this.tree = new TreeSet<T>();  
	}
	
	public TableColumn getColumn() {	return tableColumn;	}
	public void setColumn(final TableColumn tableColumn) {	this.tableColumn = tableColumn; }


}
