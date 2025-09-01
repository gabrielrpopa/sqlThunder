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

public class WebsocketMessageType {
	
	public static final String heartbeat = "hb";
	public static final String message = "mb";
	
	public static final String headerSql = "hq";
	public static final String detailSql = "dq";
	public static final String footerSql = "fq";
	
	/**The following 3 IDs are related to scripts stdin logs captured at runtime to be sent to frontend*/
	public static final String headerScript = "hs";
	public static final String detailScript = "ds";
	public static final String footerScript = "fs";
	
	/**The following 3 IDs are related to scripts sending notifications / non-stdin as above*/
	public static final String headerScriptData = "hd";
	public static final String detailScriptData = "dd";
	public static final String footerScriptData = "fd";
	
	
	public static final String open = "on";
	public static final String join = "jn";
	public static final String selfjoin = "sj";
	public static final String selfjoinRejected = "sr";
	public static final String close = "cs";
	public static final String authenticate = "au";
	public static final String updateUserUsers = "us";

	public static final String errorScript = "es";

	public static final String chatUserToUser = "uu";
	public static final String chatUserToGroup = "ug";
	public static final String chatUserOffOn = "uo";

	public static final String chatUserTyping = "ut";
	public static final String chatMessageDelete = "md";

	public static final String socketDisconnect = "sd";
	public static final String logOut = "lo";



}


