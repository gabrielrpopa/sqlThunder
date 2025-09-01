package com.widescope.chat.db.persistence;

import com.widescope.chat.db.*;
import com.widescope.chat.fileService.ChatFileService;
import com.widescope.chat.users.*;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbTransaction;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class ChatPersistence {

    public static String mongoDatabase = "chatDb";
    public static String mongoCollection = "chatCollection";
    public static String mongoBucket = "chatBucket";
    public static long maxSizeMessage = 16777216L;


    public void addChatUser(ChatDb chatDb,
                            AuthUtil authUtil,
                            final long fromId,
                            final String fromUser,
                            final long toId,
                            final String toUser) throws Exception {


        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            UserToChatList u = getUserToChatList(chatDb, fromUser, authUtil);
            if(!u.getUserToChatList().isEmpty()) {
                return;
            }

            List<SqlRepoDatabase> mapDbConn =  InternalUserDb.dataWhales.getSqlDbRefsList().values().stream().sorted((o1, o2) -> Math.min(o1.getTotalRecords(), o2.getTotalRecords())).toList();
            DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(mapDbConn.get(0));

            ChatPersistenceDb.insertUserTable(dbConn,fromId,fromUser,toId, toUser);
            mapDbConn.get(0).incrementTotalRecords();
        } else {
            chatDb.addUser( fromId, fromUser, "N", toId, toUser,"N");
        }
    }



    public String addMessage(ChatDb chatDb,
                              AppConstants appConstants,
                              final ChatMessage cMessage,
                              final String requestId,
                              final String isDelivered,
                              final long size) throws Exception {
        ChatRecord chatRecord = new ChatRecord(-1, cMessage.getFromUser(), cMessage.getToUser(), cMessage.getMillisecondsEpoch(), requestId, isDelivered, cMessage.getAttachList().isEmpty() ? "N": "Y", "", "", "", size);
        String ret;
        if(!InternalUserDb.dataWhales.getMongoClusterList().isEmpty()) {
            List<MongoClusterRecord> mapMongoConn = InternalUserDb.dataWhales.getMongoClusterList().values().stream().sorted((o1, o2) -> Math.max(o1.getCountObjects(), o2.getCountObjects())).toList();
            mapMongoConn.get(0).incrementCountObjects();
            if(size < maxSizeMessage - 2048) {
                /*Insert Object into MongoDB*/
                MongoDbTransaction.addChatMessageToCollection(mapMongoConn.get(0).getUniqueName(), ChatPersistence.mongoDatabase, ChatPersistence.mongoCollection, cMessage, requestId);
                chatRecord.setMongoUniqueName(mapMongoConn.get(0).getUniqueName());
            } else {
                byte[] payload = cMessage.toString().getBytes();
                cMessage.setAttachList(new ArrayList<>());
                MongoDbTransaction.addLargeChatMessageToBucket(mapMongoConn.get(0).getUniqueName(),
                                                                ChatPersistence.mongoDatabase,
                                                                ChatPersistence.mongoBucket,
                                                                requestId,
                                                                cMessage.toString(),
                                                                payload);
                chatRecord.setMongoUniqueName(mapMongoConn.get(0).getUniqueName());
            }
        } else {
            /*if Mongo not available, stream it down to local disk*/
            ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
            chatFileService.saveMessage(cMessage.getFromUser(), cMessage.getToUser(), requestId, cMessage);
        }


        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            /*Insert ChatRecord*/
            List<SqlRepoDatabase> mapDbConn =  InternalUserDb.dataWhales.getSqlDbRefsList().values().stream().sorted((o1, o2) -> Math.max(o1.getTotalRecords(), o2.getTotalRecords())).toList();
            DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(mapDbConn.get(0));

            try { ChatPersistenceDb.mergeChat(dbConn, chatRecord); ret = "Y"; } catch(Exception ex) { ret = "N"; }
            mapDbConn.get(0).incrementTotalRecords();
        } else {
            try { chatDb.mergeChat(chatRecord); ret = "Y"; } catch(Exception ex) { ret = "N"; }
        }


        return ret;

    }


    public ChatMessage
    getMessageFromStore(ChatDb chatDb,
                        AppConstants appConstants,
                        final String fromUser,
                        final String toUser,
                        final String requestId) throws Exception {

        ChatRecord chatRecord;

        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            chatRecord = GetChatRecordFromSqlDBTask.getChatRecordDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), fromUser, toUser,  requestId);
        } else {
            chatRecord = chatDb.getMessage(fromUser, toUser, requestId);
        }

        ChatMessage chatMessage;
        if(chatRecord.getMongoUniqueName() != null && chatRecord.getMongoDatabase() != null && chatRecord.getMongoCollection() != null) {
            MongoClusterRecord  mapMongoConn = InternalUserDb.dataWhales.getMongoClusterList().get(chatRecord.getMongoUniqueName());
            chatMessage = MongoDbTransaction.getChatMessageById(mapMongoConn.getUniqueName(), ChatPersistence.mongoDatabase, ChatPersistence.mongoCollection, requestId);
        } else {
            ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
            chatMessage = chatFileService.readMessage(fromUser, toUser, requestId);
        }

        if(chatMessage == null) {
            GetChatMessageFromMongoTask.getChatMessageDistributed(InternalUserDb.dataWhales.getMongoClusterList(), fromUser, toUser, requestId) ;
        }

        return chatMessage;

    }


    public List<ChatMessage>
    getNewMessagesFromStore(ChatDb chatDb,
                            AppConstants appConstants,
                            final String fromUser,
                            final String toUser,
                            final long fromDateTime) throws Exception {
        List<ChatRecord> chatRecords;
        List<ChatMessage> chatMessages = new ArrayList<>();


        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            chatRecords = GetChatRecordsFromSqlDBTask.getUnreadChatRecordsDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), fromUser, toUser, fromDateTime);
        } else {
            chatRecords = chatDb.getMessages(fromUser, toUser, "N", fromDateTime);
        }

        for(ChatRecord chatRecord: chatRecords) {
            ChatMessage chatMessage;
            if(chatRecord.getMongoUniqueName() != null && chatRecord.getMongoDatabase() != null && chatRecord.getMongoCollection() != null) {
                MongoClusterRecord  mapMongoConn = InternalUserDb.dataWhales.getMongoClusterList().get(chatRecord.getMongoUniqueName());
                chatMessage = MongoDbTransaction.getChatMessageById(mapMongoConn.getUniqueName(), ChatPersistence.mongoDatabase, ChatPersistence.mongoCollection, chatRecord.getRequestId());
            } else {
                ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
                chatMessage = chatFileService.readMessage(fromUser, toUser, chatRecord.getRequestId());
            }
            if(chatMessage!=null)
                chatMessages.add(chatMessage);
        }
        return chatMessages;
    }


    public List<UserPairValue>
    getListOfUsersWithNewMessages(  ChatDb chatDb,
                                    final String toUser) throws Exception {
        List<UserPairValue> userList;

        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            userList = GetOutstandingUsersFromSqlDBTask.getGetOutstandingUsersFromSqlDBTask(InternalUserDb.dataWhales.getSqlDbRefsList(), toUser);
        } else {
            userList = chatDb.getUsersWithOutstandingMessages(toUser, "N");
        }
        return userList;
    }


    public ChatMessage
    getMessageFromStore_(AppConstants appConstants,
                         final ChatRecord chatRecord) throws Exception {

        ChatMessage chatMessage;
        if(chatRecord.getMongoUniqueName() != null && chatRecord.getMongoDatabase() != null && chatRecord.getMongoCollection() != null) {
            MongoClusterRecord  mapMongoConn = InternalUserDb.dataWhales.getMongoClusterList().get(chatRecord.getMongoUniqueName());
            chatMessage = MongoDbTransaction.getChatMessageById(mapMongoConn.getUniqueName(), ChatPersistence.mongoDatabase, ChatPersistence.mongoCollection, chatRecord.getRequestId());
        } else {
            ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
            chatMessage = chatFileService.readMessage(chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId());
        }

        if(chatMessage == null) {
            GetChatMessageFromMongoTask.getChatMessageDistributed(InternalUserDb.dataWhales.getMongoClusterList(), chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId()) ;
        }
        return chatMessage;
    }



    public ChatRecord
    setReadMessageStatus(ChatDb chatDb,
                         AppConstants appConstants,
                         final ChatRecord  cRec) throws Exception {


        ChatRecord chatRecord;


        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            chatRecord = GetChatRecordFromSqlDBTask.getChatRecordDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        } else {
            chatRecord = chatDb.getMessage(cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        }

        if(chatRecord!=null && cRec.getRequestId().compareToIgnoreCase(chatRecord.getRequestId()) == 0 && !InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            chatRecord = GetChatRecordFromSqlDBTask.setReadChatRecordDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        } else {
            chatRecord = chatDb.setReadMessage(cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        }


        ChatMessage m;
        if(chatRecord.getMongoUniqueName() != null && chatRecord.getMongoDatabase() != null && chatRecord.getMongoCollection() != null) {
            MongoClusterRecord  mapMongoConn = InternalUserDb.dataWhales.getMongoClusterList().get(chatRecord.getMongoUniqueName());
            m = MongoDbTransaction.setReadChatMessageById(mapMongoConn.getUniqueName(), ChatPersistence.mongoDatabase, ChatPersistence.mongoCollection, chatRecord.getRequestId());
            ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
            chatFileService.saveMessage(chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId(), m);
        } else {
            ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
            m = chatFileService.readMessage(chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId());
            m.setIsRead(true);
            chatFileService.saveMessage(chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId(), m);
        }

        return chatRecord;
    }

    public ChatRecord
    deleteMessage(ChatDb chatDb,
                  AppConstants appConstants,
                  final ChatRecord  cRec) throws Exception {
        ChatRecord chatRecord;
        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            chatRecord = GetChatRecordFromSqlDBTask.getChatRecordDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        } else {
            chatRecord = chatDb.getMessage(cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        }

        if(chatRecord!=null && cRec.getRequestId().compareToIgnoreCase(chatRecord.getRequestId()) == 0 && !InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            GetChatRecordFromSqlDBTask.deleteChatRecordDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        } else {
            chatDb.deleteMessage(cRec.getFromUser(), cRec.getToUser(), cRec.getRequestId());
        }

        assert chatRecord != null;
        chatRecord.setIsDelivered("N");
        if(chatRecord.getMongoUniqueName() != null && chatRecord.getMongoDatabase() != null && chatRecord.getMongoCollection() != null) {
            MongoClusterRecord  mapMongoConn = InternalUserDb.dataWhales.getMongoClusterList().get(chatRecord.getMongoUniqueName());
            MongoDbTransaction.deleteChatMessageById(mapMongoConn.getUniqueName(), ChatPersistence.mongoDatabase, ChatPersistence.mongoCollection, chatRecord.getRequestId());
        } else {
            ChatFileService chatFileService = new ChatFileService(appConstants.getStoragePath() + Constants.chatFolder);
            chatFileService.readMessage(chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId());
            boolean isDeleted = chatFileService.delete(chatRecord.getFromUser(), chatRecord.getToUser(), chatRecord.getRequestId());
            if(isDeleted)
                chatRecord.setIsDelivered("Y");

        }

        return chatRecord;
    }

    public ChatRecordList getMessageHistList(ChatDb chatDb, final String fromUser, final String toUser, final long fromDate, final long toDate) throws Exception {
        List<ChatRecord> lstChatRecords;
        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            lstChatRecords = GetChatRecordsFromSqlDBTask.getUnreadChatRecordsDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), fromUser, toUser, fromDate);
        } else {
            lstChatRecords = chatDb.getMessageHist(fromUser, toUser, fromDate, toDate);
        }
        return new ChatRecordList(lstChatRecords);
    }

    public ChatRecordList getUnReadMessageList(ChatDb chatDb, final String fromUser, final String toUser, final long fromDate) throws Exception {
        List<ChatRecord> lstChatRecords;
        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            lstChatRecords = GetChatRecordsFromSqlDBTask.getUnreadChatRecordsDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), fromUser, toUser, fromDate);
        } else {
            lstChatRecords = chatDb.getMessages(fromUser, toUser, "N", fromDate);
        }
        return new ChatRecordList(lstChatRecords);
    }


    public long getUnReadMessagesCount(ChatDb chatDb, final String toUser) throws Exception {
        long count;
        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            count = GetCountUnreadRecordsFromSqlDBTask.getUnreadCountRecordsDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), toUser, "N");
        } else {
            count = chatDb.isUnreadMessages(toUser, "N" );
        }
        return count;
    }


















    public UserToChatList
    getUserToChatList(ChatDb chatDb, final String fromUser, final AuthUtil authUtil) throws Exception {
        List<ChatUser> chatUser;

        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            chatUser = getChatUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), fromUser);
        } else {
            chatUser = chatDb.getUsersTo(fromUser);
        }

        //chatUser = chatDb.getUsersTo(fromUser);
        List<User> lst = authUtil.getUsers(chatUser.stream().map(ChatUser::getToId).collect(Collectors.toList()) );
        return UserToChatList.populate(lst);
    }



    /**
     * Returns the list of chat users for a specific user, after login to populate user tabs
     * @param dbs the list of db
     * @param fromUser the user's friends
     * @return List<ChatUser>
     */
    public List<ChatUser>
    getChatUsersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser) {
        List<ChatUser> ret = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<ChatUser>> task = new GetChatUsersFromSqlDBTask(dbConn, fromUser);
                Future<List<ChatUser>> future = executor.submit(task);
                List<ChatUser> l = future.get();
                ret.addAll(l);
            } catch(Exception ignored) {

            }
        }
        executor.shutdown();
        return ret;
    }


    public UserToChatList
    getUserToChatDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String likeUser, final String fromUser) {
        UserToChatList ret = new UserToChatList();
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<UserToChat>> task = new GetUserToChatFromSqlDBTask(dbConn, likeUser, fromUser);
                Future<List<UserToChat>> future = executor.submit(task);
                List<UserToChat> l = future.get();
                ret.addAllUserToChatList(l);
            } catch(Exception ignored) {

            }
        }
        executor.shutdown();
        return ret;
    }


    /**
     * Get the details of a user, usually own
     */
    public UserToChat
    getUserToChat(final String userName, final AuthUtil authUtil) {
        User u = authUtil.getUser(userName);
        return new UserToChat(u);
    }


    public UserToChatList
    searchChatUsers(final String userName, final String patternToSearch, final AuthUtil authUtil) throws Exception {
        if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
            return getUserToChatDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), patternToSearch, userName);
        } else {
            User u = authUtil.getUser(userName);
            List<User> listOfUsers = authUtil.getUsersMinusUser(patternToSearch, u);
            return UserToChatList.populate(listOfUsers);
        }
    }


}
