package com.widescope.chat.users;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.user.User;

public class UserToChat implements RestInterface  {


    private long id;
    private String firstName;
    private String lastName;
    private String isOn;
    private String userName;
    private long lastTimeSeen;
    private boolean isGroup;

    private int tabNo;
    private boolean isNewMessage;
    private int typingCount;

    private String avatarUrl;

    public UserToChat(final User u) {
        this.id = u.getId();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.isOn = "Y";
        this.userName = u.getEmail();
        this.lastTimeSeen = DateTimeUtils.millisecondsSinceEpoch();
        this.isGroup = false;
        this.tabNo = -1;
        this.avatarUrl = u.getAvatarUrl();
    }

    public UserToChat() {
        this.id = -1;
        this.firstName = "";
        this.lastName = "";
        this.isOn = "N";
        this.userName = "";
        this.lastTimeSeen = DateTimeUtils.millisecondsSinceEpoch();
        this.isGroup = false;
        this.tabNo = -1;
        this.avatarUrl = "";
    }




    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName;}
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIsOn() { return isOn; }
    public void setIsOn(String isOn) { this.isOn = isOn; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public long getLastTimeSeen() { return lastTimeSeen; }
    public void settLastTimeSeen(long lastTimeSeen) { this.lastTimeSeen = lastTimeSeen; }

    public boolean getIsGroup() { return isGroup; }
    public void setIsGroup(boolean isGroup) { this.isGroup = isGroup; }

    public int getTabNo() { return tabNo; }
    public void setTabNo(int tabNo) { this.tabNo = tabNo; }

    public boolean getIsNewMessage() { return isNewMessage; }
    public void setIsNewMessage(boolean isNewMessage) { this.isNewMessage = isNewMessage; }

    public int getTypingCount() { return typingCount; }
    public void setTypingCount(int typingCount) { this.typingCount = typingCount; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }




    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
