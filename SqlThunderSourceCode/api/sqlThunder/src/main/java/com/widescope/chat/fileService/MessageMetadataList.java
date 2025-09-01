package com.widescope.chat.fileService;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class MessageMetadataList {

    @Autowired
    private List<MessageMetadata> messageMetadata;

    public List<MessageMetadata> getMessageMetadata() {
        return this.messageMetadata;
    }




    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public static MessageMetadataList toMessageMetadataList(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, MessageMetadataList.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }

    }
}
