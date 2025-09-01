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

import java.util.List;
import java.util.Map;

import com.widescope.sqlThunder.rest.RestInterface;



public class SqlRepoParamListDetail implements RestInterface {
	private List<SqlRepoParam> listOfSqlRepoParam;
	public SqlRepoParamListDetail() { }
	public SqlRepoParamListDetail(final  List<SqlRepoParam> listOfSqlRepoParam) { this.listOfSqlRepoParam = listOfSqlRepoParam; }
	public List<SqlRepoParam> getSqlRepoParamListDetail() { return this.listOfSqlRepoParam; }
	public void setSqlRepoParamListDetail(final List<SqlRepoParam> listOfSqlRepoParam) { this.listOfSqlRepoParam = listOfSqlRepoParam; }
	
	
	
	
	public static SqlRepoParamListDetail setSqlRepoParamListDetail(	SqlRepoParamListDetail sqlRepoParamListDetail, 
																	final Map<Integer, SqlRepoDynamicSql> mapOfRepoDynamicSql, 
																	final Integer sqlID) {
		sqlRepoParamListDetail.setSqlRepoParamListDetail(mapOfRepoDynamicSql.get(sqlID).getSqlRepoParamList());  
		return sqlRepoParamListDetail;
	}
	
	public static SqlRepoParamListDetail setSqlRepoParamListDetail(	final Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql, 
																	final Long sqlID) {
		List<SqlRepoParam> listOfSqlRepoParam = mapOfRepoDynamicSql.get(sqlID).getSqlRepoParamList();
        return new SqlRepoParamListDetail(listOfSqlRepoParam);
	}
}
