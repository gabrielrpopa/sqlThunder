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


package com.widescope.scripting.db;

public class ScriptType {
	
	private int	scriptTypeId;
	private String scriptTypeName;
	private String scriptSubType1Name;
	private String scriptSubType2Name;
	private String scriptSubType3Name;
	private String scriptSubType4Name;
	private String scriptSubType5Name;
	private String scriptSubType6Name;
	private String scriptTypeDescription;
	
	public ScriptType(	final int	scriptTypeId,
						final String scriptTypeName,
						final String scriptSubType1Name,
						final String scriptSubType2Name,
						final String scriptSubType3Name,
						final String scriptSubType4Name,
						final String scriptSubType5Name,
						final String scriptSubType6Name,
						final String scriptTypeDescription) {
		
		this.setScriptTypeId(scriptTypeId);
		this.setScriptTypeName(scriptTypeName);
		this.setScriptSubType1Name(scriptSubType1Name);
		this.setScriptSubType2Name(scriptSubType2Name);
		this.setScriptSubType3Name(scriptSubType3Name);
		this.setScriptSubType4Name(scriptSubType4Name);
		this.setScriptSubType5Name(scriptSubType5Name);
		this.setScriptSubType6Name(scriptSubType6Name);
		this.setScriptTypeDescription(scriptTypeDescription);
		
	}

	public ScriptType() {
	
		this.setScriptTypeId(0);
		this.setScriptTypeName(null);
		this.setScriptSubType1Name(null);
		this.setScriptSubType2Name(null);
		this.setScriptSubType3Name(null);
		this.setScriptSubType4Name(null);
		this.setScriptSubType5Name(null);
		this.setScriptSubType6Name(null);
		this.setScriptTypeDescription(null);
	
	}

	public int getScriptTypeId() { return scriptTypeId; }
	public void setScriptTypeId(int scriptTypeId) { this.scriptTypeId = scriptTypeId; }

	public String getScriptTypeName() {	return scriptTypeName; }
	public void setScriptTypeName(String scriptTypeName) { this.scriptTypeName = scriptTypeName; }

	public String getScriptSubType1Name() { return scriptSubType1Name; }
	public void setScriptSubType1Name(String scriptSubType1Name) { this.scriptSubType1Name = scriptSubType1Name; }

	public String getScriptSubType2Name() { return scriptSubType2Name; }
	public void setScriptSubType2Name(String scriptSubType2Name) { this.scriptSubType2Name = scriptSubType2Name; }

	public String getScriptSubType3Name() { return scriptSubType3Name; }
	public void setScriptSubType3Name(String scriptSubType3Name) { this.scriptSubType3Name = scriptSubType3Name; }

	public String getScriptSubType4Name() { return scriptSubType4Name; }
	public void setScriptSubType4Name(String scriptSubType4Name) { this.scriptSubType4Name = scriptSubType4Name; }

	public String getScriptSubType5Name() { return scriptSubType5Name; }
	public void setScriptSubType5Name(String scriptSubType5Name) { this.scriptSubType5Name = scriptSubType5Name; }

	public String getScriptSubType6Name() { return scriptSubType6Name; }
	public void setScriptSubType6Name(String scriptSubType6Name) { this.scriptSubType6Name = scriptSubType6Name; }

	public String getScriptTypeDescription() { return scriptTypeDescription; }
	public void setScriptTypeDescription(String scriptTypeDescription) { this.scriptTypeDescription = scriptTypeDescription; }
}
