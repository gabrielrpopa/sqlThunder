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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;



public class ScriptOutputDetail implements RestInterface {

	private String interpreterName;
	private String output;
	private String scriptContent;
	private int scriptId;
	private String scriptName;
	private long timestamp;
	private String userName;

	public ScriptOutputDetail() { }

    public String getInterpreterName() { return interpreterName; }
    public void setInterpreterName(String interpreterName) { this.interpreterName = interpreterName; }
	public String getScriptContent() { return scriptContent; }
	public void setScriptContent(String scriptContent) { this.scriptContent = scriptContent; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    public int getScriptId() { return scriptId; }
    public void setScriptId(int scriptId) { this.scriptId = scriptId; }
    public String getScriptName() { return scriptName; }
    public void setScriptName(String scriptName) { this.scriptName = scriptName; }
    public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static ScriptOutputDetail toScriptOutputDetail(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ScriptOutputDetail.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}


}
