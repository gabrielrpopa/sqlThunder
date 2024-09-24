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

package com.widescope.webSockets.userStreamingPortal.objects.payload;

public class WebsocketFullPayload {
	
	private String message; // message
	private String messageType; // type, see WebsocketMessageType
	private int totalCount; // total count in the batch, id it's detail of a larger bathc of messages such as a table
	private int currentPosition; // // current position in that table, batch of messages
	private String jobId; // job id
	private String userList; // comma separated user list
	private String userId; // userId
	private String httpSessionId; // userId
	private String firstName; // firstName
	private String lastName; // firstName


	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getMessageType() { return messageType; }
	public void setMessageType(String messageType) { this.messageType = messageType; }
	public int getCurrentPosition() { return currentPosition; }
	public void setCurrentPosition(int currentPosition) { this.currentPosition = currentPosition; }
	public int getTotalCount() { return totalCount; }
	public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
	public String getJobId() { return jobId; }
	public void setJobId(String jobId) { this.jobId = jobId; }
	public String getUserList() { return userList; }
	public void setUserList(String userList) { this.userList = userList; }
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public String getHttpSessionId() { return httpSessionId; }
	public void setHttpSessionId(String httpSessionId) { this.httpSessionId = httpSessionId; }
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) {	this.lastName = lastName; }
	
}
