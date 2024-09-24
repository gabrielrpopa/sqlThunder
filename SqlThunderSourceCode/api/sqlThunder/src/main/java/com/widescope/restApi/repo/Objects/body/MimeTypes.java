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


package com.widescope.restApi.repo.Objects.body;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class MimeTypes {
	
	private List<MimeType> listOfMimeTypes = null;
	
	public MimeTypes() {
		this.setListOfMimeTypes(new ArrayList<MimeType>());
		
	}
	
	public MimeTypes(final List<MimeType> listOfMimeTypes) {
		this.setListOfMimeTypes(listOfMimeTypes);
	}

	public List<MimeType> getListOfMimeTypes() { return listOfMimeTypes; }
	public void setListOfMimeTypes(List<MimeType> listOfMimeTypes) { this.listOfMimeTypes = listOfMimeTypes; }
	
	
	public void populate() {
		listOfMimeTypes.add(new MimeType(1, "JSON") ) ;
		listOfMimeTypes.add(new MimeType(2, "XML") ) ;
		listOfMimeTypes.add(new MimeType(3, "HTML") ) ;
		listOfMimeTypes.add(new MimeType(4, "CSS") ) ;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
	
