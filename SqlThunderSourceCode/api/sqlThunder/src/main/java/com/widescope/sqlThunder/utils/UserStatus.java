package com.widescope.sqlThunder.utils;

import com.widescope.sqlThunder.rest.RestInterface;

public class UserStatus implements RestInterface {

    private String userName;
    private boolean isSession;
    private boolean isSocket;

    public UserStatus(String userName, boolean isSession, boolean isSocket) {
        this.userName = userName;
        this.isSession = isSession;
        this.isSocket = isSocket;
    }


    public UserStatus(String userName) {
        this.userName = userName;
        this.isSession = false;
        this.isSocket = false;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public boolean getIsSession() { return isSession; }
    public void setIsSession(boolean isSession) { this.isSession = isSession; }

    public boolean getIsSocket() { return isSocket; }
    public void setIsSocket(boolean isSocket) { this.isSocket = isSocket; }
}
