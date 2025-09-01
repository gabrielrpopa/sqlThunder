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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class MongoRepoDynamicMql implements RestInterface {
	
	private int mqlId;
	public int getMqlId() {	return mqlId; }
	public void setMqlId(final int mqlId) { this.mqlId = mqlId; }
	
	private String mqlReturnType;
	public String getMqlReturnType() {	return mqlReturnType; }
	public void setMqlReturnType(final String mqlReturnType) { this.mqlReturnType = mqlReturnType; }
	
	private String mqlCategory;
	public String getMqlCategory() {	return mqlCategory; }
	public void setMqlCategory(final String mqlCategory) { this.mqlCategory = mqlCategory; }
	
	private String mqlClass;
	public String getMqlClass() {	return mqlClass; }
	public void setMqlClass(final String mqlClass) { this.mqlClass = mqlClass; }
	
	private String type;
	public String getType() {	return type; }
	public void setType(final String type) { this.type = type; }
	
	private String mqlName;
	public String getMqlName() {	return mqlName; }
	public void setMqlName(final String mqlName) { this.mqlName = mqlName; }
	
	private String mqlDescription;
	public String getMqlDescription() {	return mqlDescription; }
	public void setMqlDescription(final String mqlDescription) { this.mqlDescription = mqlDescription; }
	
	private String mqlContent;
	public String getMqlContent() {	return mqlContent; }
	public void setMqlContent(final String mqlContent) { this.mqlContent = mqlContent; }
	
	private int active;
	public int getActive() {	return active; }
	public void setActive(final int active) { this.active = active; }
	
	private List<MongoRepoMqlParam> mongoRepoMqlParamList;
	public List<MongoRepoMqlParam> getMongoRepoMqlParamList() {	return mongoRepoMqlParamList; }
	public void setMongoRepoMqlParamList(final List<MongoRepoMqlParam> mongoRepoMqlParamList) { this.mongoRepoMqlParamList = mongoRepoMqlParamList; }
	

	
	public MongoRepoDynamicMql(final int mqlId,
					             final String mqlReturnType, 
					             final String mqlCategory, 
					             final String mqlClass,
					             final String type,
					             final String mqlName,
					             final String mqlDescription,
					             final String mqlContent,
					             final int active) throws Exception	{
		this.mqlId = mqlId;
		this.mqlReturnType = mqlReturnType;
		this.mqlCategory = mqlCategory;
		this.mqlClass = mqlClass;
		this.type = type;
		this.mqlName = mqlName;
		this.mqlDescription = mqlDescription;
		this.mqlContent = mqlContent;
		this.active = active;
		
		this.mongoRepoMqlParamList = new ArrayList<MongoRepoMqlParam>();
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
