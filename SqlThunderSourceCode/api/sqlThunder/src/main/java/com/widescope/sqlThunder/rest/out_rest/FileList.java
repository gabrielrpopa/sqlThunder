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

package com.widescope.sqlThunder.rest.out_rest;


import com.widescope.sqlThunder.rest.RestInterface;

import java.util.List;



public class FileList implements RestInterface {
	
	private List<String> listOfFiles;
	public FileList() { }
	
	public FileList(final List<String> listOfFiles) { 
		this.listOfFiles = listOfFiles; 
	}
	
	public List<String> getFiles() { return listOfFiles; }
	public void setFiles(final List<String> listOfFiles) { 
		this.listOfFiles = listOfFiles;
	}	

}
