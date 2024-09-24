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


package com.widescope.cache.service;
import com.widescope.logging.AppLogger;

import java.io.Serial;
import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalStorage implements Serializable {

	@Serial
	private static final long serialVersionUID = -4224085104042782300L;

	
	public static short counterKeyNotificationMap = 1;
	// measured in bytes
	public static long internalFreeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	public static long internalObjectSize = 0; 
	private static volatile Instrumentation globalInstrumentation;
	/*
	 * Servers storages, notifications will be fired to clients who subscribed to receive notifications.
	 * Not thread safe
	 * 
	 */
	// key is a UUID as String, value is the url or ip/HostName:port as String
	public static Map<String, NotificationInfo> notificationMap = new HashMap<String, NotificationInfo>(); 
	/* Storage and their validity tables */
	// Storage for String Objects, key is a string given by client, value is a String given by client
	public static Map<String, CacheStringPayload> stringMap = new ConcurrentHashMap<String, CacheStringPayload>(10000);
	///////////////// Global Functions ////////////////////////////
	public static String insertNotificationMap(final NotificationInfo notificationInfo) {
		counterKeyNotificationMap++;
		String key = GlobalStorage.notificationMapContainsValue(notificationInfo);
		if(key != null) return key;
		
		while(GlobalStorage.notificationMap.containsKey(String.valueOf(counterKeyNotificationMap)))	{
			counterKeyNotificationMap++;
		}
		
		key = String.valueOf(counterKeyNotificationMap);
		GlobalStorage.notificationMap.put(key, notificationInfo);
		return key;
	}
	
	public static String notificationMapContainsValue(NotificationInfo notificationInfo) {
		for (Map.Entry<String, NotificationInfo> mapElement : GlobalStorage.notificationMap.entrySet()) {
			String key = (String)mapElement.getKey();
			NotificationInfo value = (NotificationInfo)mapElement.getValue();
			if(  (value.getIp_host().compareTo(notificationInfo.getIp_host()) == 0) &&
				(value.getUrl().compareTo(notificationInfo.getUrl()) == 0) &&
				(value.getPort() == notificationInfo.getPort()) )	{
				return key;
			}
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * @return time in milliseconds Since EPOCH
	 */
	public static long secondsSinceEpoch() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.clear();
		calendar.setTime(Calendar.getInstance().getTime());
        return calendar.getTimeInMillis() / 1000L;
	}
	
	
	public static void cleanupStringStorage() {

		try	{
            // Iterate over all the elements
            GlobalStorage.stringMap.entrySet().removeIf(entry -> entry.getValue().getExpiry() <= GlobalStorage.secondsSinceEpoch());
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
		}
	}

	
	
	
	public static long getObjectSize(final Object object) {
        if (globalInstrumentation == null) {
            throw new IllegalStateException("Agent not initialized.");
        }
        return globalInstrumentation.getObjectSize(object);
    }
	
}
