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


package com.widescope.sqlThunder;

import com.widescope.logging.AppLogger;
import com.widescope.logging.management.EnvironmentManagement;
import com.widescope.logging.management.LoggingManagement;

import java.util.concurrent.TimeUnit;

public class LoggingThread extends Thread {

	public static volatile boolean exit = false;
	private final long sleep;

	public LoggingThread (long _sleep) {
		this.sleep = _sleep;
	}
	
	public void run () {
		AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Logging thread started");
		EnvironmentManagement.LoadLogApps();
		while(!exit) {
			try {
				TimeUnit.MILLISECONDS.sleep(sleep);
				LoggingManagement.processInMemLogs();
			} catch (Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
		AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Logging thread ended");
	}
}
