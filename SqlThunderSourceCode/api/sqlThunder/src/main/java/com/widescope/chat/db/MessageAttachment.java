package com.widescope.chat.db;

import com.google.gson.Gson;
import com.widescope.chat.fileService.MessageMetadata;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.sql.SQLException;


public class MessageAttachment {

    private MessageMetadata messageMetadata;

    private String str;

    private Object file;


    public MessageAttachment() {
        this.setMessageMetadata(new MessageMetadata());
        this.setFile(null);
        this.setStr(null);
    }

    public MessageAttachment(MessageMetadata messageMetadata, String str, Object file)  {
        this.setMessageMetadata(messageMetadata);
        this.setStr(str);
        this.setFile(file);

    }



    public String getStr() {return this.str;}
    public void setStr(String f) { this.str = f; }


    public Object getFile() { return file; }
    public void setFile(Object file) { this.file = file; }


    public MessageMetadata getMessageMetadata() { return messageMetadata; }
    public void setMessageMetadata(MessageMetadata messageMetadata) { this.messageMetadata = messageMetadata; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
