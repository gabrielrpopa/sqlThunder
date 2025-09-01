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


public class ResultMetadata {
	private String columnName;
	public String getColumnName() {	return columnName; }
	public void setColumnName(final String columnName) { this.columnName = columnName; }
	
	private int columnTypeId;
	public int getColumnTypeId() {	return columnTypeId; }
	public void setColumnTypeId(final int columnTypeId) { this.columnTypeId = columnTypeId; }
	
	private String columnTypeName;
	public String getColumnTypeName() {	return columnTypeName; }
	public void setColumnTypeName(final String columnType) { this.columnTypeName = columnType; }
	
	private int length; // applies to all from varchar to double
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
	
	
	private int scale;   // applies to numeric types. value after comma
	public int getScale() { return scale; }
	public void setScale(int scale) { this.scale = scale; }
	
	
	public ResultMetadata() {
		this.setColumnName("");
		this.setColumnTypeId(0);
		this.setColumnTypeName("");
		this.setLength(0);
		this.setScale(0);
	}
	
	
	public ResultMetadata(	final String columnName, 
							final int columnTypeId, 
							final String columnType, 
							int length, 
							int scale) throws Exception{
		this.setColumnName(columnName);
		this.setColumnTypeId(columnTypeId);
		this.setColumnTypeName(columnType);
		this.setLength(length);
		this.setScale(scale);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
