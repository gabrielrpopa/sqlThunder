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


package com.widescope.restApi.repo.Objects.restApiRequest;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.restApi.repo.Objects.body.Bodies;


public class RestApiRequest implements RestInterface{

	private int id;
	private String name;
	private Bodies bodies;


	public RestApiRequest(	final int id,
					final String name,
					final Bodies bodies
					) {
		this.setId(id);
		this.setName(name);
		this.setBodies(bodies);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public Bodies getBodies() { return bodies; }
	public void setBodies(Bodies bodies) { this.bodies = bodies; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
