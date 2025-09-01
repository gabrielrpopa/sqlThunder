package com.widescope.chat.db;

import com.google.gson.Gson;
import com.widescope.chat.fileService.MessageMetadata;


public class MessageAttachmentStream {

    private MessageMetadata messageMetadata;

    private String str;



    public MessageAttachmentStream() {
        this.setMessageMetadata(new MessageMetadata());
        this.setStr(null);
    }

    public MessageAttachmentStream(MessageMetadata messageMetadata, String str, Object file)  {
        this.setMessageMetadata(messageMetadata);
        this.setStr(str);
    }

    public String getStr() {return this.str;}
    public void setStr(String f) { this.str = f; }

    public MessageMetadata getMessageMetadata() { return messageMetadata; }
    public void setMessageMetadata(MessageMetadata messageMetadata) { this.messageMetadata = messageMetadata; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
