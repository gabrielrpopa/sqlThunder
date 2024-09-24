package com.widescope.chat.users;

import com.widescope.rest.RestInterface;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.sqlThunder.utils.user.UserShort;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserToChatList implements RestInterface {

    private List<UserToChat> userToChatList;

    public UserToChatList() {
        this.userToChatList = new ArrayList<UserToChat>();
    }

    public List<UserToChat> getUserToChatList() {
        return userToChatList;
    }

    public void setUserToChatList(List<UserToChat> userToChatList) {
        this.userToChatList = userToChatList;
    }

    public void addUserToChatList(UserToChat userToChat) {
        this.userToChatList.add(userToChat);
    }
    public void addAllUserToChatList(List<UserToChat> userToChatList) {
        this.userToChatList.addAll(userToChatList);
    }

    public static UserToChatList populate(List<User> listOfUsers) {
        UserToChatList ret = new UserToChatList();
        if(listOfUsers == null) return ret;
        for(User u: listOfUsers) {
            UserToChat c = new UserToChat(u);
            if( !WebSocketsWrapper.isUser(c.getUserName())) {
                c.setIsOn("N");
            }
            Optional<UserShort> matchingObject = InternalUserDb.loggedUsers.values().stream().
                    filter(p -> p.getUser().equals(c.getUserName())).findFirst();

            matchingObject.ifPresent(userShort -> c.setAvatarUrl(userShort.getAvatarUrl()));
            ret.addUserToChatList(c);
        }
        return ret;
    }




}
