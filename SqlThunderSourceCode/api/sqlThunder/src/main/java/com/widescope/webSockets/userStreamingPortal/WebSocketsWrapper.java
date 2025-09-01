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

package com.widescope.webSockets.userStreamingPortal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.restApiClient.RestApiWebSocket;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;

public class WebSocketsWrapper {
    private static final Map<String, String > sessionContextMap = new ConcurrentHashMap<>(); // email / userName

    public WebSocketsWrapper() {

    }

    public static boolean isUser(String user) {
        try {
            return sessionContextMap.containsKey(user);
        } catch (Exception ex) {
            return false;
        }
    }


    public static void removeUser(String user) {
        sessionContextMap.remove(user);
    }

    public static void addUserSession(String email, String userName) {
        sessionContextMap.put(email, userName);
    }

    public static String getUserName(String email) {
        return sessionContextMap.get(email);
    }




    public static void
    sendSingleMessageToUserFromServer(	final WebsocketPayload message) {
        try {
            boolean b = RestApiWebSocket.sendToUpdateQueue(message, message.getBaseUrl());
            System.out.println("sendSingleMessageToUserFromServer: " + b);
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }
    }



}
