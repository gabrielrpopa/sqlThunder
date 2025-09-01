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

import java.util.ArrayList;
import java.util.List;

import com.widescope.sqlThunder.rest.RestInterface;

public class DepartmentList implements RestInterface
{
	private List<Department> listOfDepartments;

	public DepartmentList(final List<Department> listOfDepartments)	{
		setListOfDepartment(new ArrayList<Department>(listOfDepartments));
	}
	
	public DepartmentList(final Department dept)	{
		List<Department> l = new ArrayList<Department>();
		l.add(dept);
		setListOfDepartment(l);
	}

	public List<Department> getListOfDepartments() { return listOfDepartments; }
	public void setListOfDepartment(final List<Department> listOfDepartments) { this.listOfDepartments = listOfDepartments; }
	
	public void addDepartment(Department department) {
		listOfDepartments.add(department);
	}
	
}
