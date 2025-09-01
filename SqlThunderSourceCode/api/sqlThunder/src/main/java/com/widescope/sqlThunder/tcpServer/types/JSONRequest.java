package com.widescope.sqlThunder.tcpServer.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

public class JSONRequest {

    private String t; /*Message Type*/
    private String s; /*Message SubType*/
    private List<String> l; /*Recipient List*/


    private String jsonrpc = "RPC 2.0";
    private String id;
    private String method = "rpc.";
    private Object params; /*Message itself*/

    public JSONRequest(final String tcpType,
                       final String tcpSubType,
                       final List<String> recipientList,
                       final String jsonrpc,
                       final String id,
                       final String method,
                       final Object params
                       ) {
        this.t = tcpType;
        this.s = tcpSubType;
        this.l = recipientList;
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public String getT() { return t; }
    public void setT(String tcpType) { this.t = tcpType; }
    public String getS() { return s; }
    public void setS(String tcpSubType) { this.s = tcpSubType; }
    public List<String> getL() { return l; }
    public void setL(List<String> l) { this.l = l; }
    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Object getParams() { return params; }
    public void setParams(Object params) { this.params = params; }


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

    public static JSONRequest toJSONRequest(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, JSONRequest.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }


    public static String generateSeparator() {
        return "{" + (char) 1 + (char) 2 + "}";
    }


}
