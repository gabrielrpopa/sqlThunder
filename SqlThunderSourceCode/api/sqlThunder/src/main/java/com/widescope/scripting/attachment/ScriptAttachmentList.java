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

package com.widescope.scripting.attachment;


import com.google.gson.Gson;
import com.widescope.scripting.ScriptParamDetail;

public class ScriptAttachmentList {
	private ScriptAttachment[] scriptAttachmentLst;
	private ScriptParamDetail[] scriptParamDetailLst;
	private int[] nodeIdLst; 
	
	private int interpreterId; 	
	private String scriptContent; 
    private String mainScriptName; 
    private int scriptTypeId;
    private String isMarkupInterpolation;
    
    private String paramString;
    private String predictFile;
    private String predictFunc;
    
    public ScriptAttachmentList(final ScriptAttachment[] scriptAttachmentLst,
    							final ScriptParamDetail[] scriptParamDetailLst,
    							final int[] nodeIdLst,
    							final String mainScriptName,
    							final int interpreterId,
    							final String scriptContent,
    							final String paramString,
    							final String predictFile,
    							final String predictFunc,
    							final int scriptTypeId,
    							final String isMarkupInterpolation
    							) {
    	this.setScriptAttachmentLst(scriptAttachmentLst);
    	this.setScriptParamDetailLst(scriptParamDetailLst);
    	this.setNodeIdLst(nodeIdLst);
    	this.setMainScriptName(mainScriptName);
    	this.setInterpreterId(interpreterId);
		this.setScriptContent(scriptContent);
		this.setParamString(paramString);
		this.setPredictFile(predictFile);
		this.setPredictFunc(predictFunc);
		this.setScriptTypeId(scriptTypeId);
		this.setIsMarkupInterpolation(isMarkupInterpolation);
    }
    
    public ScriptAttachmentList() {
		this.setScriptAttachmentLst(null);
		this.setMainScriptName(null);
		this.setNodeIdLst(null);
		this.setScriptParamDetailLst(null);
		this.setInterpreterId(-1);
		this.setScriptContent(null);
		this.setParamString(null);
		this.setPredictFile(null);
		this.setPredictFunc(null);
		this.setScriptTypeId(1);
		this.setIsMarkupInterpolation("N");
	}

	public ScriptAttachment[] getScriptAttachmentLst() { return scriptAttachmentLst; }
	public void setScriptAttachmentLst(ScriptAttachment[] scriptAttachmentLst) { this.scriptAttachmentLst = scriptAttachmentLst; }
	
	public int[] getNodeIdLst() { return nodeIdLst; }
	public void setNodeIdLst(int[] nodeIdLst) { this.nodeIdLst = nodeIdLst; }
	
	public ScriptParamDetail[] getScriptParamDetailLst() { return scriptParamDetailLst; }
	public void setScriptParamDetailLst(ScriptParamDetail[] scriptParamDetailLst) {	this.scriptParamDetailLst = scriptParamDetailLst; }

	public String getMainScriptName() { return mainScriptName; }
	public void setMainScriptName(String mainScriptName) { this.mainScriptName = mainScriptName; }
	
	public int getInterpreterId() {	return interpreterId; }
	public void setInterpreterId(int interpreterId) { this.interpreterId = interpreterId; }
	
	public String getScriptContent() { return scriptContent; }
	public void setScriptContent(String scriptContent) { this.scriptContent = scriptContent; }
	
	public String getParamString() { return paramString; }
	public void setParamString(String paramString) { this.paramString = paramString; }

	public String getPredictFile() { return predictFile; }
	public void setPredictFile(String predictFile) { this.predictFile = predictFile; }

	public String getPredictFunc() { return predictFunc; }
	public void setPredictFunc(String predictFunc) { this.predictFunc = predictFunc; }
	
	public int getScriptTypeId() { return scriptTypeId; }
	public void setScriptTypeId(int scriptTypeId) { this.scriptTypeId = scriptTypeId; }
	
	public String getIsMarkupInterpolation() { return isMarkupInterpolation; }
	public void setIsMarkupInterpolation(String isMarkupInterpolation) { this.isMarkupInterpolation = isMarkupInterpolation; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
