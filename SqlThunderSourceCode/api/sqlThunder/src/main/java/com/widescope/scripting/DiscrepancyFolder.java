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

public class DiscrepancyFolder {
	private List<ScriptDetail> plusScriptDetail;
	private List<String> plusFolder;
	
	public DiscrepancyFolder(	final List<ScriptDetail> plusScriptDetail,
								final List<String> plusFolder) {
		this.setPlusScriptDetail(plusScriptDetail);
		this.setPlusFolder(plusFolder);
	}
	
	public DiscrepancyFolder() {
		this.setPlusScriptDetail(new ArrayList<ScriptDetail>());
		this.setPlusFolder(new ArrayList<String>());;
	}

	public List<ScriptDetail> getPlusScriptDetail() { return plusScriptDetail; }
	public void setPlusScriptDetail(List<ScriptDetail> plusScriptDetail) { this.plusScriptDetail = plusScriptDetail; }
	public void setPlusScriptDetail(ScriptDetail scriptDetail) { this.plusScriptDetail.add(scriptDetail); }

	public List<String> getPlusFolder() { return plusFolder; }
	public void setPlusFolder(List<String> plusFolder) { this.plusFolder = plusFolder; }
	public void setPlusFolder(String plusFolder) { this.plusFolder.add(plusFolder); }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
