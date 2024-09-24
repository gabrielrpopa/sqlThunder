package com.widescope.chat.db.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class UserPairValue {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


    private long id;
    private String user;
    public UserPairValue(final long id, final String user) {
        this.id = id;
        this.user = user;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String toStringPretty() {
        try	{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        catch(Exception ex) {
            return null;
        }
    }

}
