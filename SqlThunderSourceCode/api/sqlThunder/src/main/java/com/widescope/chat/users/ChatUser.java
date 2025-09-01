package com.widescope.chat.users;

import com.google.gson.Gson;

public class ChatUser {

        private long fromId;
        private String fromUser;
        private String isFromExt;
        private long  toId;
        private String  toUser;
        private String isToExt;

        public ChatUser(final long fromId,
                        final String fromUser,
                        final String isFromExt,
                        final long  toId,
                        final String  toUser,
                        final String isToExt) {
            this.fromId = fromId;
            this.fromUser = fromUser;
            this.isFromExt = isFromExt;
            this.toId = toId;
            this.toUser = toUser;
            this.isToExt = isToExt;

        }

    public ChatUser() {

    }

    public long getFromId() { return fromId; }
    public void setFromId(long fromId) { this.fromId = fromId; }
    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }
    public String getIsFromExt() { return isFromExt; }
    public void setIsFromExt(String isFromExt) { this.isFromExt = isFromExt; }
    public long getToId() { return toId; }
    public void setToId(long toId) { this.toId = toId; }
    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }
    public String getIsToExt() { return isToExt; }
    public void setIsToExt(String isToExt) { this.isToExt = isToExt; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
