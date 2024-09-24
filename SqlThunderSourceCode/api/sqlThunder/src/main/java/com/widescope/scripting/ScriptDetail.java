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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;
import com.widescope.scripting.db.MachineNodeToScriptBridge;


public class ScriptDetail implements RestInterface {
	
	// Interpreter
	private int  interpreterId;
	private String interpreterName;
	private String interpreterVersion;
	private String interpreterPath;
	private String command;
	// script
	private int scriptId;
	private String scriptName;
	private String mainFile;
	private long userCreatorId;
	private String userEmail;
	private String paramString;
	private String predictFile;
	private String predictFunc;
	
	
	// scriptVersion
	private int scriptVersion;
	private String compliance;
	private List<ScriptParamDetail> scriptParamDetailList;
	private List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList;
	
	
	
	public ScriptDetail(final int interpreterId, 
						final String interpreterName, 
						final String interpreterVersion, 
						final String interpreterPath,
						final String command,
									
						final int scriptId,
						final String scriptName,
						final String mainFile,
						final long userCreatorId,
						final String userEmail,
						final String paramString,
						final String predictFile,
						final String predictFunc,
						final int scriptVersion,
						final String compliance
						) {
		
		this.setInterpreterId(interpreterId);
		this.setInterpreterName(interpreterName);
		this.setInterpreterVersion(interpreterVersion);
		this.setInterpreterPath(interpreterPath);
		this.setCommand(command);
		
		this.setScriptId(scriptId);
		this.setScriptName(scriptName);
		this.setMainFile(mainFile);
		this.setUserCreatorId(userCreatorId);
		this.setUserEmail(userEmail);
		this.setParamString(paramString);
		this.setPredictFile(predictFile);
		this.setPredictFunc(predictFunc);
		this.setScriptVersion(scriptVersion);
		this.setCompliance(compliance);
		this.setScriptParamDetailList(new ArrayList<ScriptParamDetail>());
		this.setMachineNodeToScriptBridgeList(new ArrayList<MachineNodeToScriptBridge>());
		
		
	}
	
	
	public ScriptDetail() {
		this.setInterpreterId(-1);
		this.setInterpreterName(null);
		this.setInterpreterVersion(null);
		this.setInterpreterPath(null);
		this.setCommand(null);
		this.setScriptId(-1);
		this.setScriptName(null);
		this.setMainFile(null);
		
		this.setUserCreatorId(-1);
		this.setUserEmail(null);
		this.setParamString(null);
		this.setPredictFile(null);
		this.setPredictFunc(null);
		this.setScriptVersion(-1);
		this.setCompliance("Y");
		this.setScriptParamDetailList(new ArrayList<ScriptParamDetail>());
		this.setMachineNodeToScriptBridgeList(new ArrayList<MachineNodeToScriptBridge>());
		
	}


	public int getInterpreterId() {	return interpreterId; }
	public void setInterpreterId(int interpreterId) { this.interpreterId = interpreterId; }

	public String getInterpreterName() { return interpreterName; }
	public void setInterpreterName(String interpreterName) { this.interpreterName = interpreterName; }

	public String getInterpreterVersion() { return interpreterVersion; }
	public void setInterpreterVersion(String interpreterVersion) { this.interpreterVersion = interpreterVersion; }

	public String getInterpreterPath() { return interpreterPath; }
	public void setInterpreterPath(String interpreterPath) { this.interpreterPath = interpreterPath; }

	public String getCommand() { return command; }
	public void setCommand(String command) { this.command = command; }

	public int getScriptId() { return scriptId; }
	public void setScriptId(int scriptId) { this.scriptId = scriptId; }

	public String getScriptName() { return scriptName; }
	public void setScriptName(String scriptName) { this.scriptName = scriptName; }

	public String getMainFile() { return mainFile; }
	public void setMainFile(String mainFile) { this.mainFile = mainFile; }

	public int getScriptVersion() {	return scriptVersion; }
	public void setScriptVersion(int scriptVersion) { this.scriptVersion = scriptVersion; }
	
	public long getUserCreatorId() { return userCreatorId; }
	public void setUserCreatorId(long userCreatorId) { this.userCreatorId = userCreatorId; }
	
	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
	
	public String getParamString() { return paramString; }
	public void setParamString(String paramString) { this.paramString = paramString; }

	public String getPredictFile() { return predictFile; }
	public void setPredictFile(String predictFile) { this.predictFile = predictFile; }

	public String getPredictFunc() { return predictFunc; }
	public void setPredictFunc(String predictFunc) { this.predictFunc = predictFunc; }
	
	public List<ScriptParamDetail> getScriptParamDetailList() {	return scriptParamDetailList; }
	public void setScriptParamDetailList(List<ScriptParamDetail> scriptParamDetailList) { this.scriptParamDetailList = scriptParamDetailList; }

	public List<MachineNodeToScriptBridge> getMachineNodeToScriptBridgeList() { return machineNodeToScriptBridgeList; }
	public void setMachineNodeToScriptBridgeList(List<MachineNodeToScriptBridge> machineNodeToScriptBridgeList) { this.machineNodeToScriptBridgeList = machineNodeToScriptBridgeList; }
	
	public String getCompliance() { return compliance; }
	public void setCompliance(String compliance) { this.compliance = compliance; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static ScriptDetail toScriptDetail(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ScriptDetail.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}
}
