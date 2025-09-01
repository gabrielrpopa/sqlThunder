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

package com.widescope.sqlThunder.rest.about;


import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;


public class LogContent implements RestInterface
{
	public LogContent()	{
		content = new ArrayList<String>();
	}
	
	private List<String> content;	
	public List<String> getLogContent() {	return content; }
	public void setLogContent(final List<String> content) {	this.content = content; }
	public void addLogContent(final String line) {	this.content.add(line); }
	
	
}

