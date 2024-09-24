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

public class SetCacheItemTask implements Callable<CacheObject> {

	String serverGetAddress; 
    String passcode; 
    String userId;
    String validFor;
    String key;
    String notificationProxy;
    String value;
	
	public SetCacheItemTask(String serverGetAddress, 
			                String passcode, 
			                String userId, 
			                String validFor, 
			                String notificationProxy, 
			                String value) {
		this.serverGetAddress = serverGetAddress;
		this.passcode = passcode;
		this.userId = userId;
		this.validFor = validFor;
		this.notificationProxy = notificationProxy;
		this.value = value;
	}
    
	@Override
	public CacheObject call() throws Exception {
        return RestApiCacheClient.set(serverGetAddress, passcode, userId, validFor, notificationProxy, value);
    }
	
}
