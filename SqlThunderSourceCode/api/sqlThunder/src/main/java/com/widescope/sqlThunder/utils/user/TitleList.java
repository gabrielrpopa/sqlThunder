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

import com.widescope.rest.RestInterface;

public class TitleList implements RestInterface
{
	private List<Title> listOfTitles;

	public TitleList(final List<Title> listOfTitles) {
		setListOfTitles(new ArrayList<Title>(listOfTitles));
	}

	public TitleList(final Title title) {
		List<Title> t = new ArrayList<Title>();
		t.add(title);
		setListOfTitles(new ArrayList<Title>(t));
	}
	
	
	public List<Title> getListOfTitles() { return listOfTitles; }
	public void setListOfTitles(final List<Title> listOfTitles) { this.listOfTitles = listOfTitles; }
	public void addUser(Title title) {
		listOfTitles.add(title);
	}
	
}
