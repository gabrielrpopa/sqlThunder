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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class ScriptParamDetailObject implements RestInterface {
	private List<ScriptParamDetail> plist;
	public List<ScriptParamDetail> getplist() { return plist; }
	
	public void setPList(final List<ScriptParamDetail> plist) { this.plist = plist; }
	
		
	public ScriptParamDetail getScriptParam(final String paramName)	{
		for (ScriptParamDetail scriptParam : plist) {
            if(scriptParam.getParamName().equals(paramName) )
            	return scriptParam;
        }	
		return null;
	}
	
	
	public void addScriptParamDetail(final ScriptParamDetail scriptParamDetail)	{
		boolean itExists = false;
		for (ScriptParamDetail tmpScriptParamDetail : plist) {
            if (Objects.equals(tmpScriptParamDetail.getParamName(), scriptParamDetail.getParamName())) {
                itExists = true;
                break;
            }
        }	
		
		if(!itExists) {
			plist.add(scriptParamDetail);
		}
	}
	
	
	public ScriptParamDetailObject()	{
		this.plist = new ArrayList<ScriptParamDetail>();
	}
	
	
	public ScriptParamDetailObject(final List<ScriptParamDetail> paramList) throws Exception {
		this.plist = paramList;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
