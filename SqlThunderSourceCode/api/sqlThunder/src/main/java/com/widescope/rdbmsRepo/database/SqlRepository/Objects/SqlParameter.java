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


public class SqlParameter {
	private long pid;
	public long getPid() {	return pid; }
	public void setPid(final long pid) { this.pid = pid; }
	
	private String pname;
	public String getPname() {	return pname; }
	public void setPname(final String pname) { this.pname = pname; }
	
	private String value;
	public String getValue() {	return value; }
	public void setValue(final String value) { this.value = value; }


	public SqlParameter(final long pId,
			        final String pName,
					final String value) {
		this.pid = pId;
		this.pname = pName;
		this.value = value;
	}
	
	
	public SqlParameter(final SqlRepoParam sqlRepoParam) {
		this.pid = sqlRepoParam.getDynamicSqlParamId();
		this.pname = sqlRepoParam.getDynamicSqlParamName();
		this.value = sqlRepoParam.getDynamicSqlParamDefault();
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
