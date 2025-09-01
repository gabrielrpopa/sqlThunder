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
 * @since   August 2022
 */

public class MongoRepoMqlParam 
{
	private int dynamicMqlParamId;
	public int getDynamicMqlParamId() {	return dynamicMqlParamId; }
	public void setDynamicMqlParamId(final int dynamicMqlParamId) { this.dynamicMqlParamId = dynamicMqlParamId; }
	
	private int dynamicMqlId;
	public int getDynamicMqlId() {	return dynamicMqlId; }
	public void setDynamicMqlId(final int dynamicMqlId) { this.dynamicMqlId = dynamicMqlId; }
	
	private String dynamicMqlParamName;
	public String getDynamicMqlParamName() {	return dynamicMqlParamName; }
	public void setDynamicMqlParamName(final String dynamicMqlParamName) { this.dynamicMqlParamName = dynamicMqlParamName; }
	
	private String dynamicMqlParamDefault;
	public String getDynamicMqlParamDefault() {	return dynamicMqlParamDefault; }
	public void setDynamicMqlParamDefault(final String dynamicMqlParamDefault) { this.dynamicMqlParamDefault = dynamicMqlParamDefault; }
	
	private String  dynamicMqlParamType;
	public String getDynamicMqlParamType() {	return dynamicMqlParamType; }
	public void setDynamicMqlParamType(final String dynamicMqlParamType) { this.dynamicMqlParamType = dynamicMqlParamType; }
	
	private String dynamicMqlParamPosition;
	public String getDynamicMqlParamPosition() {	return dynamicMqlParamPosition; }
	public void setDynamicMqlParamPosition(final String dynamicMqlParamPosition) { this.dynamicMqlParamPosition = dynamicMqlParamPosition; }

	private int dynamicMqlParamOrder;
	public int getDynamicMqlParamOrder() {	return dynamicMqlParamOrder; }
	public void setDynamicMqlParamOrder(final int dynamicMqlParamOrder) { this.dynamicMqlParamOrder = dynamicMqlParamOrder; }
	
	
	private Object value;
	public Object getValue() {	return value; }
	public void setValue(final Object value) { this.value = value; }

	public MongoRepoMqlParam(final int dynamicMqlParamId,
			            final int dynamicMqlId, 
			            final String dynamicMqlParamName,
			            final String dynamicMqlParamDefault,
				        final String dynamicMqlParamType, 
				        final String dynamicMqlParamPosition, 
				        final int dynamicMqlParamOrder
				        ) throws Exception	{
		this.dynamicMqlParamId = dynamicMqlParamId;
		this.dynamicMqlId = dynamicMqlId;
		this.dynamicMqlParamName = dynamicMqlParamName;
		this.dynamicMqlParamDefault = dynamicMqlParamDefault;
		this.dynamicMqlParamType = dynamicMqlParamType;
		this.dynamicMqlParamPosition = dynamicMqlParamPosition;
		this.dynamicMqlParamOrder = dynamicMqlParamOrder;
		this.value=null;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
