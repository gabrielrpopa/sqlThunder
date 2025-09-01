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

public class ScriptParamList2 {
	private List<ScriptParam> scriptParamList;
	public List<ScriptParam> getScriptParamList() {	return scriptParamList; }
	public void setScriptParamList(final List<ScriptParam> scriptParamList) { this.scriptParamList = scriptParamList; }
	
	
	private String nodeUrl;
	public String getNodeUrl() { return nodeUrl; }
	public void setNodeUrl(final String nodeUrl) { this.nodeUrl = nodeUrl; }


	public ScriptParamList2(	final List<ScriptParam> scriptParamList,
							final String nodeUrl) {
		this.scriptParamList = scriptParamList;
		this.nodeUrl = nodeUrl;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
