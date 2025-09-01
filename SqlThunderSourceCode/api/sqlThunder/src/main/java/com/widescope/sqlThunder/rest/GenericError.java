package com.widescope.sqlThunder.rest;

public class GenericError implements RestInterface {

    private long timeNano;
    private String isDelivered;

    public GenericError (long timeNano_, String isDelivered_) {
        this.timeNano = timeNano_;
        this.isDelivered = isDelivered_;
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
}

