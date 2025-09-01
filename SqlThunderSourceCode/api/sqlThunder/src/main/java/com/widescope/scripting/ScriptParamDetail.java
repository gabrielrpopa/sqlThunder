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
import com.widescope.sqlThunder.rest.RestInterface;

public class ScriptParamDetail implements RestInterface{
			
	private int scriptParamId;
	public int getScriptParamId() {	return scriptParamId; }
	public void setScriptParamId(final int scriptParamId) { this.scriptParamId = scriptParamId; }
	
	private int scriptId;
	public int getScriptId() {	return scriptId; }
	public void setScriptId(final int scriptId) { this.scriptId = scriptId; }
	
	private String paramName;
	public String getParamName() {	return paramName; }
	public void setParamName(final String paramName) { this.paramName = paramName; }
	
	private String paramType;
	public String getParamType() {	return paramType; }
	public void setParamType(final String paramType) { this.paramType = paramType; }
	
	private String paramDimension;
	public String getParamDimension() {	return paramDimension; }
	public void setParamDimension(final String paramDimension) { this.paramDimension = paramDimension; }
		
	private String paramDefaultValue;
	public String getDefaultValue() {	return paramDefaultValue; }
	public void setDefaultValue(final String paramDefaultValue) { this.paramDefaultValue = paramDefaultValue; }
	
	private String paramPosition;
	public String getParamPosition() {	return paramPosition; }
	public void setParamPosition(final String paramPosition) { this.paramPosition = paramPosition; }
	
	private int paramOrder;
	public int getParamOrder() {	return paramOrder; }
	public void setParamOrder(final int paramOrder) { this.paramOrder = paramOrder; }
	
	
	public ScriptParamDetail() {
		this.scriptParamId = 0;
		this.scriptId = 0;
		this.paramName = null;
		this.paramType = null;
		this.paramDimension = null;
		this.paramDefaultValue = null;
		this.paramPosition = null;
		this.paramOrder = 0;
	}
	
	
	public ScriptParamDetail(	final int scriptParamId,
								final int scriptId,
								final String paramName,
								final String paramType,
								final String paramDimension,
								final String paramDefaultValue,
								final String paramPosition,
								final int paramOrder) {
		this.scriptParamId = scriptParamId;
		this.scriptId = scriptId;
		this.paramName = paramName;
		this.paramType = paramType;
		this.paramDimension = paramDimension;
		this.paramDefaultValue = paramDefaultValue;
		this.paramPosition = paramPosition;
		this.paramOrder = paramOrder;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
