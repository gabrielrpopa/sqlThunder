package com.widescope.webSockets.userStreamingPortal.objects.payload;

import com.google.gson.Gson;

public class WebsocketPayload {

    private String fromRequestId;

    public String getFromRequestId() {
        return fromRequestId;
    }

    public void setFromRequestId(String fromRequestId) {
        this.fromRequestId = fromRequestId;
    }

    private String fromUser;

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }


    private String toUser;

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    private String messageType; /*WebsocketMessageType.detailScript*/

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


    private Object message;

    public Object getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String callComposition;

    public String getCallComposition() {
        return callComposition;
    }

    public void setCallComposition(String callComposition) {
        this.callComposition = callComposition;
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String isEncrypted;

    public String getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(String isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public WebsocketPayload(final String fromRequestId,
                            final String fromUser,
                            final String toUser,
                            final String messageType,
                            final Object message,
                            final String baseUrl) {

        this.fromRequestId = fromRequestId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageType = messageType;
        this.message = message;
        this.baseUrl = baseUrl;

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
