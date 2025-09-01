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
import java.util.Map;

import com.widescope.sqlThunder.rest.RestInterface;




public class SqlRepoList implements RestInterface
{
	private List<SqlRepoDynamicSql> listOfRepoDynamicSql;
	public SqlRepoList() { }
	public SqlRepoList(List<SqlRepoDynamicSql> listOfRepoDynamicSql) { this.listOfRepoDynamicSql = listOfRepoDynamicSql; }
	public List<SqlRepoDynamicSql> getSqlRepoList() { return this.listOfRepoDynamicSql; }
	public void setSqlRepoList(List<SqlRepoDynamicSql> listOfRepoDynamicSql) { this.listOfRepoDynamicSql = listOfRepoDynamicSql; }
	

	/**
	 * static Object creation
	 * @param sqlRepoList
	 * @param mapOfRepoDynamicSql
	 * @return
	 */
	public static SqlRepoList setSqlRepoList(	SqlRepoList sqlRepoList, 
												final Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql) { 
		List<SqlRepoDynamicSql> listOfRepoDynamicSql = new ArrayList<SqlRepoDynamicSql>();
        listOfRepoDynamicSql.addAll(mapOfRepoDynamicSql.values());
		sqlRepoList.setSqlRepoList(listOfRepoDynamicSql);
		return sqlRepoList;
	}
	
	/**
	 * static Object creation
	 * @param mapOfRepoDynamicSql
	 * @return
	 */
	public static SqlRepoList setSqlRepoList(final Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql) { 
		SqlRepoList sqlRepoList = new SqlRepoList();

        List<SqlRepoDynamicSql> listOfRepoDynamicSql = new ArrayList<SqlRepoDynamicSql>(mapOfRepoDynamicSql.values());
		sqlRepoList.setSqlRepoList(listOfRepoDynamicSql);
		return sqlRepoList;
	}
	
	
	
	
	
		
	public static SqlRepoList removeAllParamsFromSqlRepoList(final Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql)	{ 
		SqlRepoList sqlRepoList = new SqlRepoList();
		
		List<SqlRepoDynamicSql> listOfRepoDynamicSql = new ArrayList<SqlRepoDynamicSql>();
		for (SqlRepoDynamicSql sqlRepoDynamicSql : mapOfRepoDynamicSql.values()) {
			listOfRepoDynamicSql.add(sqlRepoDynamicSql);
		}
		
		sqlRepoList.setSqlRepoList(listOfRepoDynamicSql);
		return sqlRepoList;
	}
	
	
}
