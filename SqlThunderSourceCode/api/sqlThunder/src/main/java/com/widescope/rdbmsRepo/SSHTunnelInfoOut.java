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


package com.widescope.rdbmsRepo.database;

import com.jcraft.jsch.Session;

public class SSHTunnelInfoOut {
	private Session session = null;
	public Session getSession() { return session; }
	public void setSession(final Session session) { this.session = session; }
	
	private int assignedPort;
	public int getAssignedPort() { return assignedPort; }
	public void setAssignedPort(final int assignedPort) {	this.assignedPort = assignedPort; }
	
	private String errorMessage = "";
	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage(final String errorMessage) { this.errorMessage = errorMessage; }
	
	private int errorCode = 0;
	public int getErrorCode() {	return errorCode; }
	public void setErrorCode(final int errorCode) { this.errorCode = errorCode; }
	
	
	public SSHTunnelInfoOut() {}
	public SSHTunnelInfoOut(final Session session, final int assignedPort) {
		this.session = 	session;
		this.assignedPort = assignedPort;
		this.errorCode = 0;
		this.errorMessage = "";
	}
	
	public SSHTunnelInfoOut(int errorCode, String errorMessage) {
		this.session = 	null;
		this.assignedPort = 0;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
