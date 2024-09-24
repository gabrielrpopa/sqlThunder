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


package com.widescope.scripting;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class ScriptParamObject implements RestInterface {
	private List<ScriptParam> plist;
	public List<ScriptParam> getplist() { return plist; }
	public void setPList(final List<ScriptParam> plist) { this.plist = plist; }
	
		
	public ScriptParam getScriptParam(final String paramName)	{
		for (ScriptParam scriptParam : plist) {
            if(scriptParam.getParamName().equals(paramName) )
            	return scriptParam;
        }	
		return null;
	}
	
	
	public void addScriptParam(final ScriptParam scriptParam)	{
		boolean itExists = false;
		for (ScriptParam tmpScriptParam : plist) {
            if (Objects.equals(tmpScriptParam.getParamName(), scriptParam.getParamName())) {
                itExists = true;
                break;
            }
        }	
		
		if(!itExists) {
			plist.add(scriptParam);
		}
	}
	
	public void addScriptParam(final ScriptParamDetail scriptParamDetail)	{
		boolean itExists = false;
		for (ScriptParam tmpScriptParam : plist) {
            if (Objects.equals(tmpScriptParam.getParamName(), scriptParamDetail.getParamName())) {
                itExists = true;
                break;
            }
        }	
		
		if(!itExists) {
			plist.add(  new ScriptParam( scriptParamDetail ) );
		}
	}
	
	
	public ScriptParamObject()	{
		this.plist = new ArrayList<ScriptParam>();
	}
	
	
	public ScriptParamObject(final List<ScriptParam> paramList) throws Exception {
		this.plist = paramList;
	}


	public static ScriptParamObject convertStringList(String jsonObjScriptParam_) {
		try	{
			ObjectMapper mapper = new ObjectMapper();
			ScriptParamObject ret = new ScriptParamObject();
			List<ScriptParam> lst = mapper.readValue(jsonObjScriptParam_, new TypeReference<List<ScriptParam>>(){});
			ret.setPList(lst);
			return ret;
		}
		catch(Exception ex)	{
			return null;
		}
	}
	
	public static Map<String, String> convertStringListToMap(String jsonObjScriptParam_) {
		try	{
			ObjectMapper mapper = new ObjectMapper();
			List<ScriptParam> lst = mapper.readValue(jsonObjScriptParam_, new TypeReference<List<ScriptParam>>(){});
			
			Map<String, String> ret = new HashMap<>();
			for(ScriptParam scriptParam: lst) {
				ret.put(scriptParam.getParamName(), scriptParam.getValue());
			}
			return ret;
		}
		catch(Exception ex)	{
			return null;
		}
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
