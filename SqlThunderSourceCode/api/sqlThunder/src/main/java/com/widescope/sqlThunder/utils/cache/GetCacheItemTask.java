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


package com.widescope.sqlThunder.utils.cache;
import java.util.concurrent.Callable;

public class GetCacheItemTask implements Callable<CacheObject> {

	String serverGetAddress; 
    String passcode; 
    String userId;
    String key;
	
	public GetCacheItemTask(String serverGetAddress, String passcode, String userId, String key) {
		this.serverGetAddress = serverGetAddress;
		this.passcode = passcode;
		this.userId = userId;
		this.key = key;
	}
 
	@Override
	public CacheObject call() throws Exception {
        return RestApiCacheClient.get(serverGetAddress, passcode, userId, key);
    }
	
}