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


package com.widescope.sqlThunder.utils.user;

import com.widescope.rest.RestInterface;

public class ManagerShort implements RestInterface { 
	private long id;
	public long getId() {	return id; }
	public void setId(final long id) { this.id = id; }
	
	private String user;
	public String getUser() {	return user; }
	public void setUser(final String user) { this.user = user; }
		
	private String firstName;
	public String getFirstName() {	return firstName; }
	public void setFirstName(final String firstName) { this.firstName = firstName; }
	
	private String lastName;
	public String getLastName() {	return lastName; }
	public void setLastName(final String lastName) { this.lastName = lastName; }
	
	public ManagerShort() {
		this.id = -1;
		this.user = null;
		this.firstName = null;
		this.lastName = null;
	}
	
	
	public ManagerShort(	final long id,
						 	final String user,
						 	final String firstName,
						 	final String lastName
				 		) {
		this.id = id;
		this.user =user;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
}
