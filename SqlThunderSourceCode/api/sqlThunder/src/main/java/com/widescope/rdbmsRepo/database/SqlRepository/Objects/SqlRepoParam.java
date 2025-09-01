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

public class SqlRepoParam
{
	private long dynamic_sql_param_id;
	public long getDynamicSqlParamId() {	return dynamic_sql_param_id; }
	public void setDynamicSqlParamId(final long dynamic_sql_param_id) { this.dynamic_sql_param_id = dynamic_sql_param_id; }
	
	private long dynamic_sql_id;
	public long getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final long dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }
	
	private String dynamic_sql_param_name;
	public String getDynamicSqlParamName() {	return dynamic_sql_param_name; }
	public void setDynamicSqlParamName(final String dynamic_sql_param_name) { this.dynamic_sql_param_name = dynamic_sql_param_name; }
	
	private String dynamic_sql_param_default;
	public String getDynamicSqlParamDefault() {	return dynamic_sql_param_default; }
	public void setDynamicSqlParamDefault(final String dynamic_sql_param_default) { this.dynamic_sql_param_default = dynamic_sql_param_default; }
	
	private String  dynamic_sql_param_type;
	public String getDynamicSqlParamType() {	return dynamic_sql_param_type; }
	public void setDynamicSqlParamType(final String dynamic_sql_param_type) { this.dynamic_sql_param_type = dynamic_sql_param_type; }
	
	private String dynamic_sql_param_position;
	public String getDynamicSqlParamPosition() {	return dynamic_sql_param_position; }
	public void setDynamicSqlParamPosition(final String dynamic_sql_param_position) { this.dynamic_sql_param_position = dynamic_sql_param_position; }

	private int dynamic_sql_param_order;
	public int getDynamicSqlParamOrder() {	return dynamic_sql_param_order; }
	public void setDynamicSqlParamOrder(final int dynamic_sql_param_order) { this.dynamic_sql_param_order = dynamic_sql_param_order; }

	
	private String dynamic_sql_param_origin_tbl;
	public String getDynamicSqlParamOriginTbl() {	return dynamic_sql_param_origin_tbl; }
	public void setDynamicSqlParamOriginTbl(final String dynamic_sql_param_origin_tbl) { this.dynamic_sql_param_origin_tbl = dynamic_sql_param_origin_tbl; }

	private String dynamic_sql_param_origin_col;
	public String getDynamicSqlParamOriginCol() {	return dynamic_sql_param_origin_col; }
	public void setDynamicSqlParamOriginCol(final String dynamic_sql_param_origin_col) { this.dynamic_sql_param_origin_col = dynamic_sql_param_origin_col; }

	public SqlRepoParam() {
		this.dynamic_sql_param_id = 0;
		this.dynamic_sql_id = 0;
		this.dynamic_sql_param_name = "";
		this.dynamic_sql_param_default = "";
		this.dynamic_sql_param_type = "";
		this.dynamic_sql_param_position = "";
		this.dynamic_sql_param_order = 0;
		this.dynamic_sql_param_origin_tbl = "";
		this.dynamic_sql_param_origin_col = "";
		
	}
	
	
	public SqlRepoParam(final long dynamic_sql_param_id,
			            final long dynamic_sql_id, 
			            final String dynamic_sql_param_name,
			            final String dynamic_sql_param_default,
				        final String dynamic_sql_param_type, 
				        final String dynamic_sql_param_position, 
				        final int dynamic_sql_param_order,
				        final String dynamic_sql_param_origin_tbl,
				        final String dynamic_sql_param_origin_col) throws Exception	{
		this.dynamic_sql_param_id = dynamic_sql_param_id;
		this.dynamic_sql_id = dynamic_sql_id;
		this.dynamic_sql_param_name = dynamic_sql_param_name;
		this.dynamic_sql_param_default = dynamic_sql_param_default;
		this.dynamic_sql_param_type = dynamic_sql_param_type;
		this.dynamic_sql_param_position = dynamic_sql_param_position;
		this.dynamic_sql_param_order = dynamic_sql_param_order;
		this.dynamic_sql_param_origin_tbl = dynamic_sql_param_origin_tbl;
		this.dynamic_sql_param_origin_col = dynamic_sql_param_origin_col;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
