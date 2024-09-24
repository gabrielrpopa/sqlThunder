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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * 
 * @author Gabriel Popa
 * @since   August 2020
 */

public class MongoRepoMqlParamInput 
{
	private String dynamic_mql_param_name;
	public String getDynamicMqlParamName() {	return dynamic_mql_param_name; }
	public void setDynamicMqlParamName(final String dynamic_mql_param_name) { this.dynamic_mql_param_name = dynamic_mql_param_name; }
	
	private String dynamic_mql_param_default;
	public String getDynamicMqlParamDefault() {	return dynamic_mql_param_default; }
	public void setDynamicMqlParamDefault(final String dynamic_mql_param_default) { this.dynamic_mql_param_default = dynamic_mql_param_default; }
	
	private String  dynamic_mql_param_type;
	public String getDynamicMqlParamType() {	return dynamic_mql_param_type; }
	public void setDynamicMqlParamType(final String dynamic_mql_param_type) { this.dynamic_mql_param_type = dynamic_mql_param_type; }
	
	private String dynamic_mql_param_position;
	public String getDynamicMqlParamPosition() {	return dynamic_mql_param_position; }
	public void setDynamicMqlParamPosition(final String dynamic_mql_param_position) { this.dynamic_mql_param_position = dynamic_mql_param_position; }

	private int dynamic_mql_param_order;
	public int getDynamicMqlParamOrder() {	return dynamic_mql_param_order; }
	public void setDynamicMqlParamOrder(final int dynamic_mql_param_order) { this.dynamic_mql_param_order = dynamic_mql_param_order; }

	private Object value;
	public Object getValue() {	return value; }
	public void setValue(final Object value) { this.value = value; }

	public MongoRepoMqlParamInput(boolean isTag) {
		if(isTag) {
			this.dynamic_mql_param_name = "@dynamic_mql_param_name@";
			this.dynamic_mql_param_default = "@dynamic_mql_param_default@";
			this.dynamic_mql_param_type = "@dynamic_mql_param_type@";
			this.dynamic_mql_param_position = "@dynamic_mql_param_position@";
			this.dynamic_mql_param_order = 0;
			this.value = "@value@";
		} else {
			this.dynamic_mql_param_name = "";
			this.dynamic_mql_param_default = "";
			this.dynamic_mql_param_type = "";
			this.dynamic_mql_param_position = "";
			this.dynamic_mql_param_order = 0;
			this.value = null;
		}
		
		
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
