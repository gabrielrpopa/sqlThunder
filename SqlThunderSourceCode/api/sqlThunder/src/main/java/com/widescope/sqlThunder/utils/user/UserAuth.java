package com.widescope.sqlThunder.utils.user;

public class UserAuth {

    private String user;
    public String getUser() {	return user; }
    public void setUser(final String user) { this.user = user; }


    private String session;
    public String getSession() {	return session; }
    public void setSession(final String session) { this.session = session; }


    public UserAuth(final String user, final String session) {
        this.user = user;
        this.session = session;
    }


}
