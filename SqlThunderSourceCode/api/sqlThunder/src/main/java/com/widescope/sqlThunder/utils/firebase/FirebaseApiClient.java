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


package com.widescope.sqlThunder.utils.firebase;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class FirebaseApiClient {

	private final static String XRequestedWith = "FIREBASE";

	public static void
	sendToToken(final String deviceToken, final String message) {
		String sendFirebaseNotificationQueue = ClusterDb.ownBaseUrl + "/firebase/clients/" + deviceToken;
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(message, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(sendFirebaseNotificationQueue, entity, String.class);
			if (response.getStatusCode() != HttpStatus.ACCEPTED) {
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error " + response.getStatusCode() + " Body: "+ response.getBody());
			}
		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}

	public static void
	sendToTopic(final String topic, final String message) {
		String sendFirebaseNotificationQueue = ClusterDb.ownBaseUrl + "/firebase/topics/" + topic;
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(message, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(sendFirebaseNotificationQueue, entity, String.class);
			if (response.getStatusCode() != HttpStatus.ACCEPTED) {
				System.out.println("Error " + response.getStatusCode() + " Body: "+ response.getBody());
			}

		} catch(Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}
	
}
