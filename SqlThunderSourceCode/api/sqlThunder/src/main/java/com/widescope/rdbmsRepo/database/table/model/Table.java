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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Table {

	private TableHeader tableHeader;
	public TableHeader getHeader() { return tableHeader;	}
	public void setHeader(final TableHeader tableHeader) {	this.tableHeader = tableHeader; }
	
	private String searchCountSqlAll;
	public String getSearchCountSqlAll() {	return searchCountSqlAll; }
	public void setSearchCountSqlAll(final String searchCountSqlAll) { this.searchCountSqlAll = searchCountSqlAll; }
	
	private String searchCountSqlVarchar;
	public String getSearchCountSqlVarchar() { return searchCountSqlVarchar; }
	public void setSearchCountSqlVarchar(final String searchCountSqlVarchar) { this.searchCountSqlVarchar = searchCountSqlVarchar; }
	
	private String searchResultSql;
	public String getSearchResultSql() { return searchResultSql; }
	public void setSearchResultSql(final String searchResultSql) { this.searchResultSql = searchResultSql; }
	
	private List<TableRow> body;
	public List<TableRow> getBody() { return body; }
	public void setBody(final List<TableRow> body) { this.body = body; }
	public void AddTableRow(final TableRow tableRow) { this.body.add(tableRow) ; }
	
	
	
	public Table() {
		this.tableHeader = new TableHeader();
		this.body = new ArrayList<TableRow>();
		this.setSearchCountSqlAll("");
		this.setSearchCountSqlVarchar("");
		this.searchResultSql = "";
	}
	
	public Table(	final TableHeader tableHeader, 
					final List<TableRow> body) {
		this.tableHeader = tableHeader;
		this.body = body;
	}
	
	
	@Override
    public String toString() {
		return new Gson().toJson(this);
	}


}
