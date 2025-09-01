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

package com.widescope.rdbmsRepo.database.embeddedDb.objects;
import com.google.gson.Gson;



public class Metadata {
	private String columnName;
	public String getColumnName() {	return columnName; }
	public void setColumnName(final String columnName) { this.columnName = columnName; }

	private SqlType sqlType;
	public SqlType getSqlType() {	return sqlType; }
	public void setSqlType(final SqlType sqlType) { this.sqlType = sqlType; }

	private int length; // applies to all from varchar to double
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }

	private int scale;   // applies to numeric types. value after comma
	public int getScale() { return scale; }
	public void setScale(int scale) { this.scale = scale; }

	private boolean isIndex;   // 'Y' or 'N'
	public boolean getIsIndex() { return isIndex; }
	public void setIsIndex(boolean isIndex) { this.isIndex = isIndex; }

	private String indexAscending; // 'ASCENDING' or 'DESCENDING'
	public String getIndexAscending() { return indexAscending; }
	public void setIndexAscending(String indexAscending) throws Exception {
		if(!isIndex) {
			throw new Exception("Index is not set");
		}
		final String str = indexAscending.trim().toUpperCase();
		if(!str.equals("ASCENDING") && !str.equals("DESCENDING") ) {
			throw new Exception("Index term not accepted");
		}
		this.indexAscending = str;
	}

	private String uniqueIndex; // 'UNIQUE' or ''
	public String getUniqueIndex() { return uniqueIndex; }
	public void setUniqueIndex(String uniqueIndex) throws Exception {
		if(!isIndex) {
			throw new Exception("Index is not set");
		}
		final String str = uniqueIndex.trim().toUpperCase();
		if(!str.equals("UNIQUE") | str.isBlank()) {
			throw new Exception("Index term not accepted");
		}
		this.uniqueIndex = str;
	}

	private String isPrimaryKey; // 'Y' or 'N'
	public String getIsPrimaryKey() { return isPrimaryKey; }
	public void setIsPrimaryKey(String isPrimaryKey) { this.isPrimaryKey = isPrimaryKey; }
	

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
