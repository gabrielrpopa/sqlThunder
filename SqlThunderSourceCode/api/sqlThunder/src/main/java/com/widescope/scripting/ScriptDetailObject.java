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

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class ScriptDetailObject implements RestInterface {
	private List<ScriptDetail> listOfScripts;
	public List<ScriptDetail> getListOfScripts() { return listOfScripts; }
	
	public void setListOfScripts(final List<ScriptDetail> slist) { this.listOfScripts = slist; }
		
	public ScriptDetail getScriptDetail(final String scriptName, int scriptVersion)	{
		for (ScriptDetail tmpScriptDetail : listOfScripts) {
            if(tmpScriptDetail.getScriptName().equals(scriptName) && 
            		tmpScriptDetail.getScriptVersion() == scriptVersion )
            	return tmpScriptDetail;
        }	
		return null;
	}

	public ScriptDetailObject()	{
		this.listOfScripts = new ArrayList<ScriptDetail>();
	}
	public ScriptDetailObject(final List<ScriptDetail> scriptDetailList) throws Exception {
		this.listOfScripts = scriptDetailList;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
