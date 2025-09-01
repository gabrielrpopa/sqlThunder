package com.widescope.webSockets.userStreamingPortal.objects.payload;

import com.google.gson.Gson;

public class UserRegistrationSocket {
    private String email;

    public String getEmail() {	return email; }
    public void setEmail(String email) { this.email = email; }


    private String user;
    public String getUser() {	return user; }
    public void setUser(String user) { this.user = user; }

    public UserRegistrationSocket(String email_, String user_) {
        this.email = email_;
        this.user = user_;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
