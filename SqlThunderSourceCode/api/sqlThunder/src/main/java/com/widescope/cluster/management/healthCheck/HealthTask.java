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
import java.util.concurrent.Callable;

import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.sqlThunder.utils.restApiClient.RestApiCluster;

public class HealthTask implements Callable<MachineNode> {

	final String baseUrl;
	final int id;
	public HealthTask(String baseUrl, int id) {
		this.baseUrl = baseUrl;
		this.id = id;
	}
 
	@Override
	public MachineNode call() throws Exception {
		return RestApiCluster.info(this.baseUrl, id);
    }
	
}
