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

import com.widescope.rest.RestInterface;

public class DatabaseEnvironment implements RestInterface {

	public DatabaseEnvironment() {}
	
	private String dbType;
	private String dbName;
	private String server;
	private String description;
	
	public String getDbType() {	return dbType; }
	public void setDbType(String dbType) {	this.dbType = dbType; }
	public String getDbName() {	return dbName; }
	public void setDbName(String dbName) {	this.dbName = dbName; }
	public String getServer() {	return server; }
	public void setServer(String server) {	this.server = server; }
	public String getDescription() {	return description; }
	public void setDescription(String description) {	this.description = description; }
	
	public DatabaseEnvironment(	final String dbType, 
								final String dbName, 
								final String server, 
								final String description)	{
		this.dbType = dbType;
		this.dbName = dbName;
		this.server = server;
		this.description = description;
	}
}

