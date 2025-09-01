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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widescope.sqlThunder.rest.RestInterface;


public class ParamListObj implements RestInterface {
	
	private final List<ParamObj> plistlist;
	public List<ParamObj> getplistlist() {	return plistlist; }
	
		
	public ParamListObj() {
		this.plistlist = new ArrayList<ParamObj>();
	}
	public ParamListObj(final List<ParamObj> plistlist)	{
		this.plistlist = plistlist;
	}



	public static ParamListObj convertStringToParamListObj(final String jsonObjSqlParamList_) {
		try	{
			Gson g = new Gson();
            return g.fromJson(jsonObjSqlParamList_, ParamListObj.class);
		}
		catch(Exception ex)	{
			return null;
		}
	}
	

	public static String printNiceFormat(final ParamObj paramObj)	{
		List<ParamObj> plistlist = new ArrayList<ParamObj>();
		plistlist.add(paramObj);
		plistlist.add(paramObj);
		ParamListObj paramListObj = new ParamListObj(plistlist);
		Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();
        return gson_pretty.toJson(paramListObj);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
