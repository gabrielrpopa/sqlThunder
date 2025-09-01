package com.widescope.sqlThunder.tcpServer.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class TcpLogin {

    private String sessionId;
    private String userName;

    public TcpLogin(final String sessionId, final String userName) {
        this.sessionId = sessionId;
        this.userName = userName;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }


    public String toStringPretty() {
        try	{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        catch(Exception ex) {
            return null;
        }
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static TcpLogin toTcpLogin(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, TcpLogin.class);
        }
        catch(JsonSyntaxException ex) {
            System.out.println("Error TcpLogin.toTcpLogin: " + ex.getMessage());
            return null;
        }
    }
}
