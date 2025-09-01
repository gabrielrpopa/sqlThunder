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


public class Bodies {
	
	private List<Body> listOfBodies = null;
	
	public Bodies() {
		this.setListOfBodies(new ArrayList<Body>());
		
	}
	
	public Bodies(final List<Body> listOfHeaders) {
		this.setListOfBodies(listOfHeaders);
	}

	public List<Body> getListOfBodies() { return listOfBodies; }
	public void setListOfBodies(List<Body> listOfBodies) { this.listOfBodies = listOfBodies; }
	
	
	public void populate() {
		List<BodyValue> bodyValue1 = new ArrayList<BodyValue>();
		listOfBodies.add(new Body(1, "Raw Input", bodyValue1) ) ;
		
		List<BodyValue> bodyValue2 = new ArrayList<BodyValue>();
		bodyValue2.add(new BodyValue(1, "Name"));
		bodyValue2.add(new BodyValue(2, "Value"));
		listOfBodies.add(new Body(2,"application/X-WWW-form-urlencoded", bodyValue2));
		
		List<BodyValue> bodyValue3 = new ArrayList<BodyValue>();
		bodyValue3.add(new BodyValue(1, "Part Name"));
		bodyValue3.add(new BodyValue(2, "Part Value"));
		listOfBodies.add(new Body(3,"Multipart Form Data", bodyValue3));
		
		
		
		List<BodyValue> bodyValue4 = new ArrayList<BodyValue>();
		bodyValue4.add(new BodyValue(1, "File Path"));
		listOfBodies.add(new Body(4,"File", bodyValue4));
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
	
