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


public class ScriptAttachment {
	private String scriptName; 
	private int interpreterId; 
	private String scriptRelativePath; 
	private String scriptContent;
	private String isMarkupInterpolation;
	
	public ScriptAttachment(final String scriptName,
							final int interpreterId,
							final String scriptRelativePath,
							final String scriptContent,
							final String isMarkupInterpolation) {
		this.setScriptName(scriptName);
		this.setInterpreterId(interpreterId);
		this.setScriptRelativePath(scriptRelativePath);
		this.setScriptContent(scriptContent);
		this.setIsMarkupInterpolation(isMarkupInterpolation);
	}
	public ScriptAttachment() {
		this.setScriptName(null);
		this.setInterpreterId(0);
		this.setScriptRelativePath(null);
		this.setScriptContent(null);
		this.setIsMarkupInterpolation(null);
	}
	public String getScriptName() {	return scriptName; }
	public void setScriptName(String scriptName) { this.scriptName = scriptName; }
	
	public int getInterpreterId() {	return interpreterId; }
	public void setInterpreterId(int interpreterId) { this.interpreterId = interpreterId; }
	
	public String getScriptRelativePath() {	return scriptRelativePath;	}
	public void setScriptRelativePath(String scriptRelativePath) { this.scriptRelativePath = scriptRelativePath; }
	
	public String getScriptContent() { return scriptContent; }
	public void setScriptContent(String scriptContent) { this.scriptContent = scriptContent; }
	
	public String getIsMarkupInterpolation() { return isMarkupInterpolation; }
	public void setIsMarkupInterpolation(String isMarkupInterpolation) { this.isMarkupInterpolation = isMarkupInterpolation; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
