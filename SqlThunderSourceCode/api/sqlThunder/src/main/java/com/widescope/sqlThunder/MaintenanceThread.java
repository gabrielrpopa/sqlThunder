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


import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.cluster.management.healthCheck.PingNodes;
import com.widescope.cluster.management.healthCheck.PingResult;
import com.widescope.cluster.management.healthCheck.SystemInfo;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.mongodb.MongoGet;
import com.widescope.sqlThunder.utils.restApiClient.RestApiWebSocket;

import com.widescope.cache.service.GlobalStorage;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.restApiClient.RestApiUserClient;
import com.widescope.sqlThunder.utils.user.AuthUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*Change it to ScheduledExecutorService */
public class MaintenanceThread extends Thread {
	public static volatile boolean exit = false;
	private static final int webSocketHeartBeatSeconds = 10;
	private static long webSocketHeartBeatLastExec = 10;
	private static final int jobPeriodSeconds = 10;
	private static long timeOfFromLastExec = 0;
	private static long timeOfCheckingCluster = 0;
	private static long timeOfCheckingWebsockets = 0;
	private static int counterDeleteOldTempDocuments = 0;

	private final String instanceType;
	private final long sleep;
	private final int serverPort;


	public MaintenanceThread (String _instanceType,
							  long _sleep,
							  int _serverPort) {
		this.instanceType = _instanceType;
		this.sleep = _sleep;
		this.serverPort = _serverPort;
	}

	public void run () {
		AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.thread, "started");
		oneTimeCall();
		while(!exit) {
			try { TimeUnit.MILLISECONDS.sleep(sleep); } catch (Exception ex) { AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.thread); }
			manageCounters();
			//RestApiWebSocket.triggerHeartBeat(null);
			//cacheServicesCleanUp();
			//killUnusedSessions();
			//checkNodesStatus();
			//System.out.println("Maintenance heartbeat");
		}
		AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.thread,"MaintenanceThread.run Exiting..... Done");
	}

	void oneTimeCall() {
		try {
			if(instanceType.compareToIgnoreCase("GATE") == 0) {
				List<PingResult> clusterPing = SystemInfo.discoverCluster(serverPort);
				List<MachineNode> clusterInfo = PingNodes.queryAllCounterparties_(clusterPing);
				com.widescope.cluster.management.miscellaneous.ClusterNodesWrapper.loadClusterNodeFile(clusterInfo);
			}
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.thread);
		}

	}

	 void manageCounters() {
		try {
			timeOfCheckingCluster++;
			timeOfCheckingWebsockets++;
			counterDeleteOldTempDocuments++;
			if (counterDeleteOldTempDocuments == 60) {
				MongoGet.deleteOldTempDocuments();
				counterDeleteOldTempDocuments = 0;
			}
		} catch(Exception ex ) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.thread);
		}

	}


	void killUnusedSessions() {
		try {
			/*allow 10 minutes of inactivity then kill the session*/
			if(timeOfCheckingWebsockets == 600)	{
				timeOfCheckingWebsockets = 0;
				String localSession = StringUtils.generateUniqueString32();
				AuthUtil.setLocalSession(localSession);
				RestApiUserClient.cleanUp(ClusterDb.ownBaseUrl, localSession);
			}
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.thread);
		}
	}


	void cacheServicesCleanUp() {
		try {
			// Cache Services Cleanup
			long tmp = GlobalStorage.secondsSinceEpoch();
			if(tmp - MaintenanceThread.timeOfFromLastExec > MaintenanceThread.jobPeriodSeconds)	{
				GlobalStorage.cleanupStringStorage();
				MaintenanceThread.timeOfFromLastExec = GlobalStorage.secondsSinceEpoch();
				GlobalStorage.internalFreeMemory = Runtime.getRuntime().freeMemory();
			}
			if(tmp - MaintenanceThread.webSocketHeartBeatLastExec > MaintenanceThread.webSocketHeartBeatSeconds)	{
				MaintenanceThread.webSocketHeartBeatLastExec = GlobalStorage.secondsSinceEpoch();
			}
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.thread);
		}
	}


	void checkNodesStatus() {
		try {
			/* check status of nodes in the cluster*/
			if(timeOfCheckingCluster == 60)	{
				timeOfCheckingCluster = 0;
				ClusterDb.updateClusterStatus();
			}
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.thread);
		}
	}

}
