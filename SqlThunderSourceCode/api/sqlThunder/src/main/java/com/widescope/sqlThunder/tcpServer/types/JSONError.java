package com.widescope.sqlThunder.tcpServer.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.HashMap;
public class JSONError {
    private int code;
    private String message;
    private Map<String, String> data = new HashMap<>();

    public JSONError(final int code, final String message, final Map<String, String> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }


    public static JSONError makeJSONError(final int code, final String message) {
        return new JSONError(code, message, new HashMap<>());
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static JSONError toJSONError(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, JSONError.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }


}
