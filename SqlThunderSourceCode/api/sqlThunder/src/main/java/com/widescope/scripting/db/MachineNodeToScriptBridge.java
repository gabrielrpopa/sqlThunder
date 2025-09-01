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

public class MachineNodeToScriptBridge {
	
	private int	id;
	private int	nodeId ;
	private String nodeName;
	private int	scriptId ;
	private String scriptName;
	
	
	public MachineNodeToScriptBridge(final int id,
									 final int nodeId ,
									 final String nodeName,
									 final int scriptId,
									 final String scriptName) {
		this.setId(id);
		this.setNodeId(nodeId);
		this.setNodeName(nodeName);
		this.setScriptId(scriptId);
		this.setScriptName(scriptName);
	}

	public MachineNodeToScriptBridge() {
		this.setId(-1);
		this.setNodeId(11);
		this.setScriptId(-1);
		this.setNodeName(null);
		this.setScriptName(null);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public int getNodeId() { return nodeId; }
	public void setNodeId(int nodeId) { this.nodeId = nodeId; }
	
	public String getNodeName() { return nodeName; }
	public void setNodeName(String nodeName) { this.nodeName = nodeName; }

	public int getScriptId() { return scriptId; }
	public void setScriptId(int scriptId) { this.scriptId = scriptId; }

	public String getScriptName() {	return scriptName; }
	public void setScriptName(String scriptName) { this.scriptName = scriptName; }

	
	


}
