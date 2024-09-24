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


package com.widescope.sqlThunder;


import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.tcpServer.TCPServer;


public class TCPServerThread extends Thread {

	public static volatile boolean exit = false;

	AppConstants appConstants;
	TCPServer tcpServer;

	public TCPServerThread(AppConstants _appConstants, TCPServer _tcpServer) {
		this.appConstants = _appConstants;
		this.tcpServer = _tcpServer;
	}

	public void run () {
		tcpServer.start(Integer.parseInt(appConstants.getTcpServerPort()));
	}
}
