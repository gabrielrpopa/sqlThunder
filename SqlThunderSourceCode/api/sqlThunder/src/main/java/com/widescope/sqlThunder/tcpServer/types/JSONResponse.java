package com.widescope.sqlThunder.tcpServer.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

public class JSONResponse {

    private String t;  /*Message Type*/
    private String s; /*Message SubType*/
    private String result; /*Message itself*/
    private String jsonrpc = "RPC 2.0";
    private String id;
    private JSONError error;

    public JSONResponse(final String tcpType,
                        final String tcpSubType,
                        final String result,
                        final String jsonrpc,
                        final String id,
                        final JSONError error
                        ) {
        this.t = tcpType;
        this.s = tcpSubType;
        this.result = result;
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.error = error;
    }

    public String getT() { return t; }
    public void setT(String tcpType) { this.t = tcpType; }
    public String getS() { return s; }
    public void setS(String tcpSubType) { this.s = tcpSubType; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public JSONError getError() { return error; }
    public void setError(JSONError error) { this.error = error; }


    public static JSONResponse genericErrorMessage(final JSONError error)  {
        return new JSONResponse(TcpMessageType.TcpTypeError,
                                TcpMessageSubType.TcpTypeJsonSubTypeErrorText,
                               error.toString(),
                                "2.0",
                                "",
                                error);
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

    public static JSONResponse toJSONResponse(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, JSONResponse.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }


    public static String generateSeparator() {
        return "{" + (char) 1 + (char) 2 + "}";
    }

    /*Required terminator in TCP data exchange*/
    public static String getWithTerminator(final String str) {
        return str + "\r\n";
    }




}
