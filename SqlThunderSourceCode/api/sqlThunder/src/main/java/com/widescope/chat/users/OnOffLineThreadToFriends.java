package com.widescope.chat.users;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;


import java.util.List;

/**
 * Sends notifications of On/Off status of requester to its Friends
 * */
public class OnOffLineThreadToFriends extends Thread {

    List<User> requesterFriends = null;
    UserToChat requester = null;
    public OnOffLineThreadToFriends(List<User> requesterFriends, UserToChat requester) {
        this.requester = requester;
        this.requesterFriends = requesterFriends;
    }

    public void run () {

        final String requestId = StaticUtils.getUUID();

        for(User u : requesterFriends) {
            if( WebSocketsWrapper.isUser(u.getUser())) {
                WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
                                                                    requester.getUserName(),
                                                                    u.getUser(),
                                                                    WebsocketMessageType.chatUserOffOn,
                                                                    requester,
                                                                    ClusterDb.ownBaseUrl);

                WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
            }
        }

        /*Do it twice after waiting 500 milliseconds*/
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(User u : requesterFriends) {
            if( WebSocketsWrapper.isUser(u.getUser())) {
                WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
                        requester.getUserName(),
                        u.getUser(),
                        WebsocketMessageType.chatUserOffOn,
                        requester,
                        ClusterDb.ownBaseUrl);

                WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);
            }
        }
    }
}
