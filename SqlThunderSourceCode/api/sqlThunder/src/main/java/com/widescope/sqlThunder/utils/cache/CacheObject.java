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

package com.widescope.sqlThunder.utils.cache;

import java.util.ArrayList;
import java.util.List;

public class CacheObject {
	
	private String key;
	private String value;
	private String userId;
	private String message;
	
	private List<String> valueList;
	
	
	private CacheObject(String key, String value, String userId, String message) {
		this.setKey(key);
		this.setValue(value);
		this.setUserId(userId);
		this.setMessage(message);
		this.setValueList(new ArrayList<String> ());
	}


	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }

	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }

	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
	
	public static CacheObject makeCacheObject(String key, String value, String userId, String message){
		return new CacheObject(key, value, userId, message);
	}

	public List<String> getValueList() { return valueList; }
	public void setValueList(List<String> valueList) { this.valueList = valueList; }
	public void setValueList(String value) { this.valueList.add(value) ; }

}
