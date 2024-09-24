package com.widescope.chat.fileService;

import com.google.gson.Gson;


public class MessageMetadata {
    private String type;
    private String name;
    private long lastModified;
    private String msg;



    public MessageMetadata() {
        this.setType("NONE");
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
