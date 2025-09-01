package com.widescope.chat;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class ChatConfirmation implements RestInterface {

    private long timeNano;
    private String isDelivered;

    private String isSaved;

    public ChatConfirmation (long timeNano_, String isDelivered_, final String isSaved_) {
        this.timeNano = timeNano_;
        this.isDelivered = isDelivered_;
        this.isSaved = isSaved_;
    }

    public long getTimeNano() {
        return timeNano;
    }

    public void setTimeNano(long timeNano) {
        this.timeNano = timeNano;
    }

    public String getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(String isDelivered) {
        this.isDelivered = isDelivered;
    }

    public String getIsSaved() { return isSaved; }
    public void setIsSaved(String isSaved) { this.isSaved = isSaved; }

    public static ChatConfirmation getChatConfirmationFail(final long timestampN) {
        return new ChatConfirmation(timestampN, "N", "N");
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
