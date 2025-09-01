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

public class ManagerShortList implements RestInterface
{
	private List<ManagerShort> listOfUsers;
	public ManagerShortList(final List<ManagerShort> listOfUsers)	{
		setListOfUsers(new ArrayList<ManagerShort>(listOfUsers));
	}

	public List<ManagerShort> getListOfUsers() { return listOfUsers; }
	public void setListOfUsers(final List<ManagerShort> listOfUsers) { this.listOfUsers = listOfUsers; }
	public void addUser(ManagerShort user)	{
		listOfUsers.add(user);
	}
	
}
