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

/**
 * 
 * @author Gabriel Popa
 * @since   August 2020
 */

public class Title implements RestInterface{
	
	private int id;
	public int getId() {	return id; }
	public void setId(final int id) { this.id = id; }
	
	private String title;
	public String getTitle() {	return title; }
	public void setTitle(final String title) { this.title = title; }
	
	private String description;
	public String getDescription() {	return description; }
	public void setDescription(final String description) { this.description = description; }
	
	
	public Title()
	{
		this.id = -1;
		this.title = null;
		this.description = null;
	}
	
	
	public Title(final String title, final String description) 
	{
		this.id = -1;
		this.title = title;
		this.description =description;
	}
	
	public Title(final int id, final String title, final String description) 
	{
		this.id = id;
		this.title = title;
		this.description =description;
	}
	
}
