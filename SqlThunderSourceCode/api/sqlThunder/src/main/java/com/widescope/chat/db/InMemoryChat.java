package com.widescope.chat.db;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChat {


    /**
     * The internal Map of the Chat table
     * If toId is not on-line (there is no open websocket from the toId, this will be serialized into the database)
     * Once the message is delivered to toId, the message and associated attachments will be erased from server
     */
    private static final ConcurrentHashMap< String /*fromUser*/,
                            ConcurrentHashMap<String /*toUser*/,
                                    ConcurrentHashMap<String, ChatMessage> /*requestId*/>> chat = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap< String, String >  usersToChatAvatar = new ConcurrentHashMap<>();   /*key is userName*/

    public static synchronized long getMillisecondsSinceEpoch() {
        return DateTimeUtils.millisecondsSinceEpoch();
    }



    public static Map<String, ChatMessage>
    getChatRecords(String fromUser, String toUser) {
        return chat.get(fromUser).get(toUser);
    }

    public static ChatMessage
    getChatRecord(String fromUser, String toUser, String requestId) {
        try {
            return  chat.get(fromUser).get(toUser).get(requestId);
        } catch(Exception ex) {
            return null;
        }
    }

    public static boolean
    isMessage(String fromUser, String toUser, String requestId) {
        try {
            return  chat.get(fromUser).get(toUser).containsKey(requestId);
        } catch(Exception ex) {
            return false;
        }
    }

    public static ChatMessage
    addChatRecord(String fromUser, String toUser, String requestId, ChatMessage chatMessage) {
        chat.putIfAbsent(fromUser, new ConcurrentHashMap<>());
        chat.get(fromUser).putIfAbsent(toUser, new ConcurrentHashMap<>());
        chat.get(fromUser).get(toUser).putIfAbsent(requestId, chatMessage);
        return getChatRecord(fromUser, toUser, requestId);
    }

    public static synchronized void
    deleteChatRecord(String fromUser, String toUser, String requestId) {
        try{
            /*Delete message only when the readCounter reached 0*/
            chat.get(fromUser).get(toUser).get(requestId).decrementCounter();
            if(chat.get(fromUser).get(toUser).get(requestId).getReadCounter() == 0) {
                chat.get(fromUser).get(toUser).remove(requestId);
                System.out.println("Message deleted from memory");
            }

            if(chat.get(fromUser).get(toUser).isEmpty()) {
               chat.get(fromUser).remove(toUser);
               if(chat.get(fromUser).isEmpty()) {
                   chat.remove(fromUser);
               }
            }
        } catch(Exception ex){
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }

    }

    public static String getAvatarUrl(String httpSession) {
        return InternalUserDb.loggedUsers.get(httpSession).getAvatarUrl();
    }

    public static void setAvatarUrl(String httpSession, String avatarUrl) {
        InternalUserDb.loggedUsers.get(httpSession).setAvatarUrl(avatarUrl);
    }


}
