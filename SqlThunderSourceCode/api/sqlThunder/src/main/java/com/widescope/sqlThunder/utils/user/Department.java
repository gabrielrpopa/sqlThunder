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

import com.widescope.sqlThunder.rest.RestInterface;


public class Department implements RestInterface {
	
	private long id;
	public long getId() {	return id; }
	public void setId(final long id) { this.id = id; }
	
	private String department;
	public String getDepartment() {	return department; }
	public void setDepartment(final String department) { this.department = department; }
	
	private String description;
	public String getDescription() {	return description; }
	public void setDescription(final String description) { this.description = description; }
	
	
	public Department() {
		this.id = -1;
		this.department = null;
		this.description = null;
	}
	
	public Department(final String departmentName, final String description) {
		this.id = -1;
		this.department = departmentName;
		this.description =description;
	}
	
	
	public Department(final long id, final String departmentName, final String description) {
		this.id = id;;
		this.department = departmentName;
		this.description =description;
	}
	
}
