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

import com.google.gson.Gson;

public class ScriptParamRepoList {
	private List<ScriptParamList2> scriptParamRepoList;
	public List<ScriptParamList2> getScriptParamRepoList() {	return scriptParamRepoList; }
	public void setScriptRepoParam(final List<ScriptParamList2> scriptParamRepoList) { this.scriptParamRepoList = scriptParamRepoList; }
	
	private int scriptId;
	public int getScriptId() { return scriptId; }
	public void setScriptId(int scriptId) { this.scriptId = scriptId; }
	
	private int interpreterId;
	public int getInterpreterId() { return interpreterId; }
	public void setInterpreterId(int interpreterId) { this.interpreterId = interpreterId; }
	
	private String requestId;
	public String getRequestId() { return requestId; }
	public void setRequestId(String requestId) { this.requestId = requestId; }


	public ScriptParamRepoList(	final List<ScriptParamList2> scriptParamRepoList) {
		this.scriptParamRepoList = scriptParamRepoList;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
