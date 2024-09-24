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

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class ScriptParamCompoundObject implements RestInterface {

	private ScriptParamDetailObject scriptParamDetailObject;
	private ScriptParamObject scriptParamObject;
	
	public ScriptParamCompoundObject() {
		this.setScriptParamDetailObject(new ScriptParamDetailObject());
		this.setScriptParamObject(new ScriptParamObject());
	}
	
	public ScriptParamCompoundObject(	final ScriptParamDetailObject scriptParamDetailObject,
										final ScriptParamObject scriptParamObject) {
		this.setScriptParamDetailObject(scriptParamDetailObject);
		this.setScriptParamObject(scriptParamObject);
	}

	public ScriptParamDetailObject getScriptParamDetailObject() { return scriptParamDetailObject; }
	public void setScriptParamDetailObject(ScriptParamDetailObject scriptParamDetailObject) { this.scriptParamDetailObject = scriptParamDetailObject; }

	public ScriptParamObject getScriptParamObject() { return scriptParamObject; }
	public void setScriptParamObject(ScriptParamObject scriptParamObject) { this.scriptParamObject = scriptParamObject; }
	
	
	public void addScriptParam(final ScriptParamDetail scriptParamDetail) {
		boolean itExists = false;
		for (ScriptParamDetail tmpScriptParamDetail : scriptParamDetailObject.getplist()) {
            if (Objects.equals(tmpScriptParamDetail.getParamName(), scriptParamDetail.getParamName())) {
                itExists = true;
                break;
            }
        }	
		
		if(!itExists) {
			scriptParamDetailObject.addScriptParamDetail(scriptParamDetail);
		}
		for (ScriptParam tmpScriptParam : scriptParamObject.getplist()) {
            if (Objects.equals(tmpScriptParam.getParamName(), scriptParamDetail.getParamName())) {
                itExists = true;
                break;
            }
        }	
		
		if(!itExists) {
			scriptParamObject.addScriptParam(scriptParamDetail);
		}
		
	}
	
	
	public void addScriptParamList(final List<ScriptParamDetail> scriptParamDetail) {
		for(ScriptParamDetail x: scriptParamDetail) {
			scriptParamDetailObject.addScriptParamDetail(x);
		}
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
