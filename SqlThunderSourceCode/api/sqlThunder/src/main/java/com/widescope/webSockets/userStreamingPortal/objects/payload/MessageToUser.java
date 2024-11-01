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

import com.google.gson.Gson;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketPayloadKey;
import org.json.JSONObject;


public class MessageToUser {

	private String ms;

	public MessageToUser(final String message,
						 final String messageType,
						 final String baseUrl,
						 final String requestId
					) {
		this.ms = new JSONObject() 	.put(WebsocketPayloadKey.message, message)  //message
									.put(WebsocketPayloadKey.messageType, messageType)
									.put(WebsocketPayloadKey.baseUrl, baseUrl)
									.put(WebsocketPayloadKey.requestId, requestId).toString() ;
	}

	public String getMs() {	return ms; }
	public void setMs(String ms) { this.ms = ms; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
