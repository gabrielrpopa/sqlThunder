package com.widescope.chat.users;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;

import java.util.List;

/**
 * Sends notifications to requester about the status of its friends
 * */
public class OnOffLineThreadFromFriends extends Thread {

    List<User> requesterFriends = null;
    UserToChat requester = null;
    public OnOffLineThreadFromFriends(List<User> requesterFriends, UserToChat requester) {
        this.requesterFriends = requesterFriends;
        this.requester = requester;
    }

    public void run () {

        final String requestId = StaticUtils.getUUID();

        for(User u : requesterFriends) {
            UserToChat mToChatStatus = new UserToChat(u);
            if( !WebSocketsWrapper.isUser(u.getUser())) {
                mToChatStatus.setIsOn("N");
            }
            WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
                                                                mToChatStatus.getUserName(),
                                                                requester.getUserName(),
                                                                WebsocketMessageType.chatUserOffOn,
                                                                mToChatStatus,
                                                                ClusterDb.ownBaseUrl);

            WebSocketsWrapper.sendSingleMessageToUserFromServer( wsPayload);



        }
    }
}
