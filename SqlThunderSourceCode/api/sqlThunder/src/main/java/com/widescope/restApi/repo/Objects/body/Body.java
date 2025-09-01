/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.restApi.repo.Objects.body;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class Body {
	private int id;
	private String bodyName;
	private List<BodyValue> bodyValue;
	
	public Body() {
		this.setId(-1);
		this.setBodyName(null);
		this.setBodyValue(new ArrayList<BodyValue>());
	}
	
	public Body(	final int id,
					final String bodyName,
					final List<BodyValue> bodyValue) {
		this.setId(id);
		this.setBodyName(bodyName);
		this.setBodyValue(bodyValue);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getBodyName() { return bodyName; }
	public void setBodyName(String bodyName) { this.bodyName = bodyName; }

	public List<BodyValue> getBodyValue() { return bodyValue; }
	public void setBodyValue(List<BodyValue> bodyValue) {	this.bodyValue = bodyValue; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
