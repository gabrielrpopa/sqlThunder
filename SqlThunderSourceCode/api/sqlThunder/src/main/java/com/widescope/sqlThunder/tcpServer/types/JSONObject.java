package com.widescope.sqlThunder.tcpServer.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JSONObject {

    private String object;

    public JSONObject(final String o) {
        this.object=o;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static JSONObject toJSONObject(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, JSONObject.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
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
