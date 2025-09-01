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

package com.widescope.logging.loggingDB;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class LogRecordList implements RestInterface{
	private List<LogRecord> logRecordFromDbList;
	
	public List<LogRecord> getLogRecordFromDbList() { return logRecordFromDbList; }
	public void setLogRecordFromDbList(List<LogRecord> logRecordFromDbList) { 
		this.logRecordFromDbList = logRecordFromDbList; 
	}
	
	public void addLogRecordFromDbList(List<LogRecord> logRecordFromDbList) { 
		this.logRecordFromDbList.addAll(logRecordFromDbList); 
	}
	public LogRecordList() {
		this.setLogRecordFromDbList(new ArrayList<LogRecord>());
	}
	
	public LogRecordList(final List<LogRecord> logRecordFromDbList) {
		this.setLogRecordFromDbList(logRecordFromDbList);
		
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
