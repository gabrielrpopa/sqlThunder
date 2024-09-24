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


package com.widescope.sqlThunder.utils;


import com.widescope.logging.AppLogger;

class NotificationThread extends Thread {
	

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	String serverBaseAddress;
	String jobID;
	String application;
	String module;
	String environment;
	String username;
	String sessionID;
	String status;
	String percentage;
	String message;
	boolean isNotification;

	NotificationThread(String serverBaseAddress,
						String jobID,
						String application,
						String module,
						String environment,
						String username,
						String sessionID,
						String status,
						String percentage,
						String message,
						boolean isNotification) {
	this.serverBaseAddress = serverBaseAddress;
	this.jobID = jobID;
	this.application = application;
	this.module = module;
	this.environment = environment;
	this.username = username;
	this.sessionID = sessionID;
	this.status = status;
	this.percentage = percentage;
	this.message = message;
	this.isNotification = isNotification;
	}

	public void run()
	{
		try {
			RestSqlRepoApiClient.jobStatusNotificationToAll(serverBaseAddress, 
													jobID, 
													"SQLREPOAPI", 
													"ExecuteSqlRepo", 
													environment, 
													username, 
													sessionID, 
													status, 
													"0%", 
													"", 
													isNotification);
		} 
		catch (Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
		}
	}
}


public class NotificationUtils {

	
	
	public NotificationUtils() {
		
	}

	
	public static void fireNotificationThread(String serverBaseAddress,
										String jobID,
										String application,
										String module,
										String environment,
										String username,
										String sessionID,
										String status,
										String percentage,
										String message,
										boolean isNotification)
	{
		NotificationThread p = new NotificationThread(	serverBaseAddress, 
														jobID,
														"QLREPOAPI", 
														"ExecuteSqlRepo", 
														environment, 
														username, 
														sessionID, 
														status, 
														percentage, 
														message, 
														isNotification);
		p.start();
	}

	

}
