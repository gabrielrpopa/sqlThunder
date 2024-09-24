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

public class UpdateUsersAboutUserActivity {
	private String message; // message
	private String messageType; // type, see WebsocketMessageType
	private String userList; // comma separated user list
	private String userId; // userId
	private String httpSessionId; // userId


	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getMessageType() { return messageType; }
	public void setMessageType(String messageType) { this.messageType = messageType; }
	public String getUserList() { return userList; }
	public void setUserList(String userList) { this.userList = userList; }
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public String getHttpSessionId() { return httpSessionId; }
	public void setHttpSessionId(String httpSessionId) { this.httpSessionId = httpSessionId; }
}
