package com.widescope.cluster.management.miscellaneous;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ClusterNode {

    private	String host;
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    private	String type;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }


    public ClusterNode(final String host, final String type) {
        this.host = host;
        this.type = type;
    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public static ClusterNode toClusterNode (String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, ClusterNode.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }

    }

}
