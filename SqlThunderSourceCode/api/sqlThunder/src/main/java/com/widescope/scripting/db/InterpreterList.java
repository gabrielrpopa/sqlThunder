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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;



public class InterpreterList implements RestInterface {

	private List<InterpreterType> interpreterList;

	public List<InterpreterType> getInterpreterList() {	return interpreterList; }
	public void setInterpreterList(List<InterpreterType> interpreterList) { this.interpreterList = interpreterList; }
	
	public InterpreterList(List<InterpreterType> interpreterList)	{
		this.interpreterList = interpreterList;
	}
	public InterpreterList(InterpreterType interpreter)	{
		this.interpreterList = new ArrayList<InterpreterType>();
		this.interpreterList.add(interpreter);
	}
	
	public InterpreterList() {
		this.interpreterList = new ArrayList<InterpreterType>();
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
