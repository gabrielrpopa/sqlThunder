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

package com.widescope.rdbmsRepo.database.mongodb.repo;

import com.google.gson.Gson;

/**
 * 
 * @author Gabriel Popa
 * @since   August 2020
 */

public class MongoRepoMqlParamInput 
{
	private String dynamicMqlParamName;
	public String getDynamicMqlParamName() {	return dynamicMqlParamName; }
	public void setDynamicMqlParamName(final String dynamic_mql_param_name) { this.dynamicMqlParamName = dynamic_mql_param_name; }
	
	private String dynamicMqlParamDefault;
	public String getDynamicMqlParamDefault() {	return dynamicMqlParamDefault; }
	public void setDynamicMqlParamDefault(final String dynamic_mql_param_default) { this.dynamicMqlParamDefault = dynamic_mql_param_default; }
	
	private String dynamicMqlParamType;
	public String getDynamicMqlParamType() {	return dynamicMqlParamType; }
	public void setDynamicMqlParamType(final String dynamic_mql_param_type) { this.dynamicMqlParamType = dynamic_mql_param_type; }
	
	private String dynamicMqlParamPosition;
	public String getDynamicMqlParamPosition() {	return dynamicMqlParamPosition; }
	public void setDynamicMqlParamPosition(final String dynamic_mql_param_position) { this.dynamicMqlParamPosition = dynamic_mql_param_position; }

	private int dynamicMqlParamOrder;
	public int getDynamicMqlParamOrder() {	return dynamicMqlParamOrder; }
	public void setDynamicMqlParamOrder(final int dynamic_mql_param_order) { this.dynamicMqlParamOrder = dynamic_mql_param_order; }

	private Object value;
	public Object getValue() {	return value; }
	public void setValue(final Object value) { this.value = value; }

	public MongoRepoMqlParamInput() {
	}

	public MongoRepoMqlParamInput(boolean isTag) {
		if(isTag) {
			this.dynamicMqlParamName = "@dynamic_mql_param_name@";
			this.dynamicMqlParamDefault = "@dynamic_mql_param_default@";
			this.dynamicMqlParamType = "@dynamic_mql_param_type@";
			this.dynamicMqlParamPosition = "@dynamic_mql_param_position@";
			this.dynamicMqlParamOrder = 0;
			this.value = "@value@";
		} else {
			this.dynamicMqlParamName = "";
			this.dynamicMqlParamDefault = "";
			this.dynamicMqlParamType = "";
			this.dynamicMqlParamPosition = "";
			this.dynamicMqlParamOrder = 0;
			this.value = null;
		}
		
		
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
