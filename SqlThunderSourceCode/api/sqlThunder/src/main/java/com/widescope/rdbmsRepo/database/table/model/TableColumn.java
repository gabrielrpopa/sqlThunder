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

public class TableColumn {

	private int index;
	private String columnName;
	private int columnTypeId;
	private String columnTypeName;
	private int length; // applies to all from varchar to double
	private int scale;   // applies to numeric types. value after comma
 
	
	
	public TableColumn(	final int index,
						final String columnName, 
						final int columnTypeId, 
						final String columnTypeName,
						final int length, 
						final int scale) throws Exception {
		this.setIndex(index);
		this.setColumnName(columnName);
		this.setColumnTypeId(columnTypeId);
		this.setColumnTypeName(columnTypeName);
		this.setLength(length);
		this.setScale(scale);
	} 
	
	public int getIndex() {	return index; }
	public void setIndex(int index) { this.index = index; }
	public String getColumnName() {	return columnName; }
	public void setColumnName(String columnName) { this.columnName = columnName; }
	public int getColumnTypeId() { return columnTypeId; }
	public void setColumnTypeId(int columnTypeId) { this.columnTypeId = columnTypeId; }
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
	public int getScale() { return scale; }
	public void setScale(int scale) { this.scale = scale; }
	public String getColumnTypeName() {	return columnTypeName;	}
	public void setColumnTypeName(String columnTypeName) {	this.columnTypeName = columnTypeName; }

	
}
