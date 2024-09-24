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

import java.util.List;

import com.google.gson.Gson;
import com.widescope.restApi.repo.Objects.body.Bodies;


public class RestApiRequests {
	
	private List<RestApiRequest> listOfRequests = null;

	public RestApiRequests(final List<RestApiRequest> listOfRequests) {
		this.setListOfRequests(listOfRequests);
	}

	public List<RestApiRequest> getListOfRequests() { return listOfRequests; }
	public void setListOfRequests(List<RestApiRequest> listOfRequests) { this.listOfRequests = listOfRequests; }
	public void addRequest(RestApiRequest request) { this.listOfRequests.add(request);}
	
	public void populate() {
		
		Bodies b1 = new Bodies();
		this.listOfRequests.add(new RestApiRequest(1, "GET", b1)) ;
		
		Bodies b2 = new Bodies();
		b2.populate();
		this.listOfRequests.add(new RestApiRequest(2, "POST", b2)) ;
		
		Bodies b3 = new Bodies();
		b3.populate();
		this.listOfRequests.add(new RestApiRequest(3, "PUT", b3)) ;
		
		Bodies b4 = new Bodies();
		b4.populate();
		this.listOfRequests.add(new RestApiRequest(4, "DELETE", b4)) ;
		
		Bodies b5 = new Bodies();
		b5.populate();
		this.listOfRequests.add(new RestApiRequest(5, "PATCH", b5)) ;
		
		Bodies b6 = new Bodies();
		this.listOfRequests.add(new RestApiRequest(6, "HEAD", b6)) ;
		
		Bodies b7 = new Bodies();
		b7.populate();
		this.listOfRequests.add(new RestApiRequest(7, "CONNECT", b7)) ;
		
		Bodies b8 = new Bodies();
		b8.populate();
		this.listOfRequests.add(new RestApiRequest(8, "OPTIONS", b8)) ;
		
		Bodies b9 = new Bodies();
		b9.populate();
		this.listOfRequests.add(new RestApiRequest(9, "TRACE", b9)) ;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
	
