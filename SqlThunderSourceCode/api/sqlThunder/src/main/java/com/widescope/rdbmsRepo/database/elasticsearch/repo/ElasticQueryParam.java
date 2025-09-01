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

package com.widescope.rdbmsRepo.database.elasticsearch.repo;

import com.google.gson.Gson;



public class ElasticQueryParam {
	private int queryParamId;
	public int getQueryParamId() {	return queryParamId; }
	public void setQueryParamId(final int queryParamId) { this.queryParamId = queryParamId; }
	
	private int queryId;
	public int getQueryId() {	return queryId; }
	public void setQueryId(final int queryId) { this.queryId = queryId; }
	
	private String queryParamName;
	public String getQueryParamName() {	return queryParamName; }
	public void setQueryParamName(final String queryParamName) { this.queryParamName = queryParamName; }
	
	private String queryParamDefault;
	public String getQueryParamDefault() {	return queryParamDefault; }
	public void setQueryParamDefault(final String queryParamDefault) { this.queryParamDefault = queryParamDefault; }
	
	private String  queryParamType;
	public String getQueryParamType() {	return queryParamType; }
	public void setQueryParamType(final String queryParamType) { this.queryParamType = queryParamType; }
	
	private String queryParamPosition;
	public String getQueryParamPosition() {	return queryParamPosition; }
	public void setQueryParamPosition(final String queryParamPosition) { this.queryParamPosition = queryParamPosition; }

	private int queryParamOrder;
	public int getQueryParamOrder() {	return queryParamOrder; }
	public void setQueryParamOrder(final int queryParamOrder) { this.queryParamOrder = queryParamOrder; }


	public ElasticQueryParam(	final int queryParamId,
					            final int queryId, 
					            final String queryParamName,
					            final String queryParamDefault,
						        final String queryParamType, 
						        final String queryParamPosition, 
						        final int queryParamOrder) throws Exception	{
		this.queryParamId = queryParamId;
		this.queryId = queryId;
		this.queryParamName = queryParamName;
		this.queryParamDefault = queryParamDefault;
		this.queryParamType = queryParamType;
		this.queryParamPosition = queryParamPosition;
		this.queryParamOrder = queryParamOrder;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
