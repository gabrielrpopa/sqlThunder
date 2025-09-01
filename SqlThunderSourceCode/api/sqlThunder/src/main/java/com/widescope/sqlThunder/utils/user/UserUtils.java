package com.widescope.sqlThunder.utils.user;

import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;

public class UserUtils {

    public static boolean isWebSocket(final String u) {
        return u != null && !u.isEmpty() && !u.isBlank() && WebSocketsWrapper.isUser(u);
    }

}
