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


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.controller.v2.ChatController;
import com.widescope.sqlThunder.utils.internet.InternetProtocolUtils;

import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.sqlThunder.utils.restApiClient.RestApiCluster;



public class PingNodes {

	public 
	static 
	List<PingResult>
	pingAllCounterparties(final List<MachineNode> lst) {
		List<PingResult> ret = new ArrayList<PingResult>();
		List<Future<PingResult>> list = new ArrayList<Future<PingResult>>();
		ExecutorService executor = Executors.newFixedThreadPool(lst.size());
		for (MachineNode node: lst) {
	        Callable<PingResult> task = new PingTask(node.getBaseUrl());
            Future<PingResult> future = executor.submit(task);
			list.add(future);
        }
		for(Future<PingResult> future : list){
			try {
				PingResult result = future.get();
				ret.add(result);
			} catch (InterruptedException | ExecutionException e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			}
		}
		executor.shutdown();
		return ret;
	}



	public
	static
	List<PingResult>
	pingClusterHttp(long ipv4Start, long ipv4End, int port) {
		List<PingResult> ret = new ArrayList<PingResult>();
		List<Future<PingResult>> list = new ArrayList<Future<PingResult>>();
		long localIp;
		try {
			localIp = InternetProtocolUtils.ipToLong(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

		ExecutorService executor = Executors.newFixedThreadPool((int) (ipv4End - ipv4Start));
		for(long ipAdress = ipv4Start; ipAdress <= ipv4End; ipAdress++ ) {
			if(localIp != ipAdress) {
				String baseUrlHttp = "http://" + InternetProtocolUtils.longToIp(ipAdress) + ":" + port + "/sqlThunder";
				Callable<PingResult> task = new PingTask(baseUrlHttp);
				Future<PingResult> future = executor.submit(task);
				list.add(future);
			}
		}


		for(Future<PingResult> future : list){
			try {
				PingResult result = future.get();
				if(result.getIsReachable().compareToIgnoreCase("Y") == 0)
					ret.add(result);
			} catch (InterruptedException | ExecutionException ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}

		executor.shutdown();
		return ret;
	}







	public static PingResult pingLocal() {
		try {
			String pong = RestApiCluster.ping(ClusterDb.ownBaseUrl);
			PingResult ret = new PingResult( ClusterDb.ownBaseUrl,"N");
			if(pong!= null && pong.equals("PONG")) {
				ret.setIsReachable("Y");
			} else {
				ret.setIsReachable("N");
			}
			return ret;

		} catch (Exception e) {
			return null;
		}
	}


	
	public 
	static 
	List<MachineNode>
	pingAllCounterparties_(final List<MachineNode> lst) {
		if(lst.isEmpty()) return new ArrayList<>();
		List<MachineNode> ret = new ArrayList<MachineNode>();
		List<Future<PingResult>> list = new ArrayList<Future<PingResult>>();
		ExecutorService executor = Executors.newFixedThreadPool(lst.size());
		for (MachineNode node: lst) {
	        Callable<PingResult> task = new PingTask(node.getBaseUrl());
            Future<PingResult> future = executor.submit(task);
			list.add(future);
        }

		for(Future<PingResult> future : list){
			try {
				PingResult result = future.get();
				MachineNode m = lst.stream().filter(node -> node.getBaseUrl().equals(result.getBaseUrl())).findFirst().orElse(new MachineNode());
				m.setIsReachable(result.getIsReachable());
				ret.add(m);
			} catch (InterruptedException | ExecutionException ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}
		executor.shutdown();
		return ret;
	}



	public
	static
	List<MachineNode>
	queryAllCounterparties(final List<MachineNode> lst) {
		List<MachineNode> ret = new ArrayList<>();
		List<Future<MachineNode>> list = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(lst.size());
		for (MachineNode node: lst) {

			Callable<MachineNode> task = new InfoTask(node.getBaseUrl(), node.getId());
			Future<MachineNode> future = executor.submit(task);
			list.add(future);
		}

		for(Future<MachineNode> future : list){
			try {
				MachineNode result = future.get();
				if(result!=null && result.getBaseUrl()!=null && !result.getBaseUrl().isEmpty())
					ret.add(result);
			} catch (InterruptedException | ExecutionException ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}

		}
		executor.shutdown();
		return ret;
	}


	public
	static
	List<MachineNode>
	queryAllCounterparties_(final List<PingResult> lst) {
		if(lst == null || lst.isEmpty()) return new ArrayList<>();
		List<MachineNode> ret = new ArrayList<MachineNode>();
		List<Future<MachineNode>> list = new ArrayList<Future<MachineNode>>();
		ExecutorService executor = Executors.newFixedThreadPool(lst.size());
		for (PingResult node: lst) {
			Callable<MachineNode> task = new InfoTask(node.getBaseUrl(), -1);
			Future<MachineNode> future = executor.submit(task);
			list.add(future);
		}

		for(Future<MachineNode> future : list){
			try {
				MachineNode result = future.get();
				if(result!=null && !result.getBaseUrl().isEmpty() && !result.getBaseUrl().isBlank())
					ret.add(result);
			} catch (InterruptedException | ExecutionException ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}

		}
		executor.shutdown();
		return ret;
	}
	
}
