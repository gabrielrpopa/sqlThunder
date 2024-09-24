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

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;




public class SqlRepoListShortFormat implements RestInterface {
	private List<SqlRepoDynamicSqlShort> listOfRepoDynamicSqlShort;
	public SqlRepoListShortFormat() { }
	public SqlRepoListShortFormat(final List<SqlRepoDynamicSqlShort> listOfRepoDynamicSqlShort) { 
		this.listOfRepoDynamicSqlShort = listOfRepoDynamicSqlShort; 
	}
	
	public List<SqlRepoDynamicSqlShort> getSqlRepoListShortFormat() { return this.listOfRepoDynamicSqlShort; }
	public void setSqlRepoListShortFormat(final List<SqlRepoDynamicSqlShort> listOfRepoDynamicSqlShort) { 
		this.listOfRepoDynamicSqlShort = listOfRepoDynamicSqlShort; 
	}
	
	public void addSqlRepoListShortFormat(SqlRepoDynamicSqlShort repoDynamicSqlShort) { 
		this.listOfRepoDynamicSqlShort.add(repoDynamicSqlShort); 
	}
	
	
	
	
	public static SqlRepoListShortFormat
	setSqlRepoListShortFormat(	SqlRepoListShortFormat sqlRepoListShortFormat,
								  final Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql)	{
		List<SqlRepoDynamicSqlShort> listOfRepoDynamicSqlShort = new ArrayList<SqlRepoDynamicSqlShort>();
		for (SqlRepoDynamicSql sqlRepoDynamicSql : mapOfRepoDynamicSql.values()) {
			
			            
			SqlRepoDynamicSqlShort sqlRepoDynamicSqlShort = new SqlRepoDynamicSqlShort(sqlRepoDynamicSql.getSqlId(), 
					                                                                   sqlRepoDynamicSql.getSqlType(),
					                                                                   sqlRepoDynamicSql.getSqlName(),
					                                                                   sqlRepoDynamicSql.getSqlDescription()
					                                                                   );
			
			listOfRepoDynamicSqlShort.add(sqlRepoDynamicSqlShort);
		}
		
		sqlRepoListShortFormat.setSqlRepoListShortFormat(listOfRepoDynamicSqlShort);
		
		return sqlRepoListShortFormat;
	}
	
	

	public static SqlRepoListShortFormat
	setSqlRepoListShortFormat(Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql) { 
		SqlRepoListShortFormat sqlRepoListShortFormat = new SqlRepoListShortFormat();
		List<SqlRepoDynamicSqlShort> listOfRepoDynamicSqlShort = new ArrayList<SqlRepoDynamicSqlShort>();
		
		
		for (SqlRepoDynamicSql sqlRepoDynamicSql : mapOfRepoDynamicSql.values()) {
			SqlRepoDynamicSqlShort sqlRepoDynamicSqlShort = new SqlRepoDynamicSqlShort(sqlRepoDynamicSql.getSqlId(), 
					                                                                   sqlRepoDynamicSql.getSqlType(),
					                                                                   sqlRepoDynamicSql.getSqlName(),
					                                                                   sqlRepoDynamicSql.getSqlDescription()
					                                                                   );
			
			listOfRepoDynamicSqlShort.add(sqlRepoDynamicSqlShort);
		}
		
		sqlRepoListShortFormat.setSqlRepoListShortFormat(listOfRepoDynamicSqlShort);
		
		return sqlRepoListShortFormat;
	}
	
	

	public static SqlRepoListShortFormat 
	setSqlRepoListShortFormat(	Map<Long, SqlRepoDynamicSql> mapOfRepoDynamicSql, 
								String filter)	{ 
		SqlRepoListShortFormat sqlRepoListShortFormat = new SqlRepoListShortFormat();
		List<SqlRepoDynamicSqlShort> listOfRepoDynamicSqlShort = new ArrayList<SqlRepoDynamicSqlShort>();
		
		for (SqlRepoDynamicSql sqlRepoDynamicSql : mapOfRepoDynamicSql.values()) {
			if(filter != null && !filter.isBlank() && !filter.isEmpty()) {
				if(sqlRepoDynamicSql.getSqlDescription().contains(filter) 
		        		|| sqlRepoDynamicSql.getSqlName().contains(filter) 
		        		|| sqlRepoDynamicSql.getSqlContent().contains(filter)) {
					SqlRepoDynamicSqlShort sqlRepoDynamicSqlShort = new SqlRepoDynamicSqlShort(sqlRepoDynamicSql.getSqlId(), 
																		                        sqlRepoDynamicSql.getSqlType(),
																		                        sqlRepoDynamicSql.getSqlName(),
																		                        sqlRepoDynamicSql.getSqlDescription()
																		                        );
	
					listOfRepoDynamicSqlShort.add(sqlRepoDynamicSqlShort);
		        }
			}
			else {
				SqlRepoDynamicSqlShort sqlRepoDynamicSqlShort = new SqlRepoDynamicSqlShort(sqlRepoDynamicSql.getSqlId(), 
                        sqlRepoDynamicSql.getSqlType(),
                        sqlRepoDynamicSql.getSqlName(),
                        sqlRepoDynamicSql.getSqlDescription()
                        );

				listOfRepoDynamicSqlShort.add(sqlRepoDynamicSqlShort);
			}
		}
		sqlRepoListShortFormat.setSqlRepoListShortFormat(listOfRepoDynamicSqlShort);
		return sqlRepoListShortFormat;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
