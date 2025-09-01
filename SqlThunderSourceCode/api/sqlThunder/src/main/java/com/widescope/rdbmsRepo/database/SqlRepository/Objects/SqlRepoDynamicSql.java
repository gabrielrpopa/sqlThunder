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

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;




public class SqlRepoDynamicSql implements RestInterface {
	
	private long sql_id;
	public long getSqlId() {	return sql_id; }
	public void setSqlId(final long sql_id) { this.sql_id = sql_id; }
	
	
	private String sql_type;
	public String getSqlType() {	return sql_type; }
	public void setSqlType(final String sql_type) { this.sql_type = sql_type; }
	
	private String sql_category;
	public String getSqlCategory() {	return sql_category; }
	public void setSqlCategory(final String sql_category) { this.sql_category = sql_category; }
	
	private String sql_name;
	public String getSqlName() {	return sql_name; }
	public void setSqlName(final String sql_name) { this.sql_name = sql_name; }
	
	private String sql_description;
	public String getSqlDescription() {	return sql_description; }
	public void setSqlDescription(final String sql_description) { this.sql_description = sql_description; }
	
	private String sql_content;
	public String getSqlContent() {	return sql_content; }
	public void setSqlContent(final String sql_content) { this.sql_content = sql_content; }
	
	private String execution;
	public String getExecution() {	return execution; }
	public void setExecution(final String execution) { this.execution = execution; }

	private int active;
	public int getActive() {	return active; }
	public void setActive(final int active) { this.active = active; }
	
	private List<SqlRepoParam> sqlRepoParamList;
	public List<SqlRepoParam> getSqlRepoParamList() {	return sqlRepoParamList; }
	public void setSqlRepoParamList(final List<SqlRepoParam> sqlRepoParamList) { this.sqlRepoParamList = sqlRepoParamList; }
	public void addSqlRepoParamList(final SqlRepoParam sqlRepoParam) { this.sqlRepoParamList.add(sqlRepoParam) ; }
	public void clearSqlRepoParamList() { this.sqlRepoParamList.clear(); }
	
	private List<SqlParameter> sqlParamList;
	public List<SqlParameter> getParamList() {	return sqlParamList; }
	public void setParamList(final List<SqlParameter> sqlParamList) { this.sqlParamList = sqlParamList; }
	public void addParamList(final SqlParameter sqlParam) { this.sqlParamList.add(sqlParam); }
	public void clearParamList() { this.sqlParamList.clear(); }
	
	private List<SqlRepoDatabaseSchemaBridge> sqlRepoDatabaseSchemaBridgeList;
	public List<SqlRepoDatabaseSchemaBridge> getSqlRepoDatabaseSchemaBridgeList() {	return this.sqlRepoDatabaseSchemaBridgeList; }
	public void setSqlRepoDatabaseSchemaBridgeList(final List<SqlRepoDatabaseSchemaBridge> sqlRepoDatabaseSchemaBridgeList) { this.sqlRepoDatabaseSchemaBridgeList = sqlRepoDatabaseSchemaBridgeList; }
	
	
	public SqlRepoParam getSqlRepoParam(int dynamic_sql_param_id) {
		for (SqlRepoParam sqlRepoParam : sqlRepoParamList) {
            if(sqlRepoParam.getDynamicSqlParamId() == dynamic_sql_param_id)
            	return sqlRepoParam;
        }	
		return null;
	}
	
	
	public SqlRepoDynamicSql() {
		this.sql_id = 0;
		this.sql_type = "";
		this.sql_category = "";
		this.sql_name = "";
		this.sql_description = "";
		this.sql_content = "";
		this.active = 0;
		this.sqlRepoParamList = new ArrayList<SqlRepoParam>();
		this.sqlParamList = new ArrayList<SqlParameter>();
		this.sqlRepoDatabaseSchemaBridgeList = new ArrayList<SqlRepoDatabaseSchemaBridge>();
		
	}
	
	
	public SqlRepoDynamicSql(final long sql_id,
			                 final String sql_type, 
				             final String sql_category, 
				             final String sql_name,
				             final String sql_description,
				             final String sql_content,
				             final String execution,
				             final int active) throws Exception	{
		this.sql_id = sql_id;
		
		this.sql_type = sql_type;
		this.sql_category = sql_category;
		this.sql_name = sql_name;
		this.sql_description = sql_description;
		this.sql_content = sql_content;
		this.execution = execution;
		this.active = active;
		
		this.sqlRepoParamList = new ArrayList<SqlRepoParam>();
		this.sqlParamList = new ArrayList<SqlParameter>();
		this.sqlRepoDatabaseSchemaBridgeList = new ArrayList<SqlRepoDatabaseSchemaBridge>();
	}




	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
