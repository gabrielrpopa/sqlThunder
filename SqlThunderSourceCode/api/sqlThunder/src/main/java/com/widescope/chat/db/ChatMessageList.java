package com.widescope.chat.db;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageList implements RestInterface {

    private List<ChatMessage> chatMessageList;
    public ChatMessageList(List<ChatMessage> chatMessageList) {
        this.setChatMessageList(chatMessageList);
    }
    public ChatMessageList() {
        this.setChatMessageList(new ArrayList<>());
    }
    public List<ChatMessage> getChatMessageList() { return chatMessageList; }
    public void setChatMessageList(List<ChatMessage> chatMessageList) { this.chatMessageList = chatMessageList; }
    public void addChatMessage(ChatMessage chatRecord) { this.chatMessageList.add(chatRecord);  }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
