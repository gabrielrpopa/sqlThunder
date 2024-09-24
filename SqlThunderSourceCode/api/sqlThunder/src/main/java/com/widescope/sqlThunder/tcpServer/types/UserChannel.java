package com.widescope.sqlThunder.tcpServer.types;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class UserChannel {

    private io.netty.channel.Channel channel;
    private String userName;

    public UserChannel(final io.netty.channel.Channel channel, final String userName) {
        this.channel = channel;
        this.userName = userName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public static UserChannel toUserChannel(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, UserChannel.class);
        }
        catch(JsonSyntaxException ex) {
            System.out.println("Error UserChannel.toUserChannel: " + ex.getMessage());
            return null;
        }
    }
}
