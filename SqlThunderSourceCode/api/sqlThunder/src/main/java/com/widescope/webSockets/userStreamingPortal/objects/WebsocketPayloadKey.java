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


package com.widescope.webSockets.userStreamingPortal.objects;

public class WebsocketPayloadKey {
	public static final String message = "ms"; // message
	public static final String messageType = "mt"; // message type
	public static final String baseUrl = "url"; // baseUrl source
	public static final String totalCount = "tc"; // total count in the batch, id it's detail of a larger bathc of messages such as a table
	public static final String currentPosition = "cp"; // // current position in that table, batch of messages
	public static final String requestId = "id"; // job id
	public static final String userList = "ul"; // comma separated user list
	public static final String userId = "ui"; // userId
	public static final String httpSessionId = "hs"; // http session
	public static final String firstName = "fn"; // firstName
	public static final String lastName = "ln"; // firstName
	public static final String senderId = "si"; // sender, is either "server" or a registered ID
}
