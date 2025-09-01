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


import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestInterface;


public class ServerEnvironment implements RestInterface {
	private String userid;	
	private String sessionId;
	private String serverVersion;
	private String environment;
	private DatabaseEnvironmentList dbList;
	private GenericResponse message;
	
	public String getUserID() {	return userid; }
	public void setUserID(final String userid) {	this.userid = userid; }
	
	public String getSessionId() {	return sessionId; }
	public void setSessionId(final String sessionId) {	this.sessionId = sessionId; }
	
	public String getServerVersion() {	return serverVersion; }
	public void setServerVersion(final String serverVersion) {	this.serverVersion = serverVersion; }
		
	public String getEnvironment() {	return environment; }
	public void setEnvironment(final String environment) {	this.environment = environment; }
	
	public DatabaseEnvironmentList getDatabaseEnvironmentList() {	return dbList; }
	public void setDatabaseEnvironmentList(final DatabaseEnvironmentList dbList) {	this.dbList = dbList; }
	
	public GenericResponse getMessage() {return message; }
	public void setMessage(final GenericResponse message) {this.message = message; }
	
	public ServerEnvironment(	final DatabaseEnvironmentList dbList, 
								final String userid, 
								final String sessionId,
								final String serverVersion, 
								final String environment, 
								final GenericResponse message) {
		this.dbList = dbList;
		this.userid = userid;
		this.sessionId = sessionId;
		this.serverVersion = serverVersion;
		this.environment = environment;
		this.message = message;
	}
}

