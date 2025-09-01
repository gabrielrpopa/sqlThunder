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


package com.widescope.cluster.management.healthCheck;

//import java.net.InetAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.logging.AppLogger;

import com.widescope.scripting.execution.InMemScriptData;
import com.widescope.sqlThunder.utils.Ip4NetUtils;
import com.widescope.sqlThunder.utils.internet.InternetProtocolUtils;


public class SystemInfo {

	public 
	static 
	MachineNode 
	getHealthCheck(String baseUrl, String type) throws UnknownHostException {
		MachineNode machineNode = new MachineNode(	-1,
													baseUrl,
													type,
													"?",
													"?",
													"Y",
													Runtime.getRuntime().totalMemory(),
													Runtime.getRuntime().freeMemory(),
													Runtime.getRuntime().availableProcessors(),
													InMemScriptData.getCurrentScriptsRunning());
		
		machineNode.setIpList( Ip4NetUtils.getLocalIpAddresses() );
        return machineNode;
	}


	public static List<PingResult> discoverCluster(int port) {
		try {
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String startIp =  localIp.substring(0, localIp.lastIndexOf(".") + 1) + "1";
			String endIp = localIp.substring(0, localIp.lastIndexOf(".") + 1) + "254";
			List<PingResult> pingClusterHttp = PingNodes.pingClusterHttp(InternetProtocolUtils.ipToLong(startIp), InternetProtocolUtils.ipToLong(endIp), port);
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Discovery of nodes yielded :" + pingClusterHttp.size() + " nodes");
			return pingClusterHttp;
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}





}
