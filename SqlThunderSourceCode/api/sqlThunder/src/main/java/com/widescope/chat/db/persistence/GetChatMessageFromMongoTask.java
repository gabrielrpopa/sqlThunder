package com.widescope.chat.db.persistence;

import com.widescope.chat.db.ChatMessage;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbTransaction;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/*Search a specific message, based on message ID/requestId, result is one message or none*/
public class GetChatMessageFromMongoTask implements Callable<ChatMessage>  {

    private final MongoClusterRecord mongoCluster;
    private final String mongoDB;
    private final String mongoCollection;
    private final String fromUser;
    private final String toUser;
    private final String requestId;

    private final static String operator = "$eq";
    private final static String valueToSearchType = "STRING";


    public GetChatMessageFromMongoTask( final MongoClusterRecord mongoCluster,
                                        final String mongoDB,
                                        final String mongoCollection,
                                        final String fromUser,
                                        final String toUser,
                                        String requestId) {
        this.mongoCluster = mongoCluster;
        this.mongoDB = mongoDB;
        this.mongoCollection = mongoCollection;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.requestId = requestId;
    }

    @Override
    public ChatMessage call() throws Exception {
        try {
            ChatMessage m = MongoDbTransaction.getChatMessageById(mongoCluster.getUniqueName(), mongoDB, mongoCollection, requestId);
            assert m != null;
            if(m.getFromUser().equalsIgnoreCase(this.fromUser)  && m.getFromUser().equalsIgnoreCase(this.toUser))
                return m;

            return null; /*didn't find it*/

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return null;
        }


    }


    public static ChatMessage
    getChatMessageDistributed(final ConcurrentMap<String, MongoClusterRecord> dbs,
                              final String fromUser,
                              final String toUser,
                              final String requestId) {
        List<ChatMessage> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (MongoClusterRecord db: dbs.values()) {
            try {
                Callable<ChatMessage> task = new GetChatMessageFromMongoTask(db, "local", "chats", fromUser, toUser, requestId);
                Future<ChatMessage> future = executor.submit(task);
                if(future.get()!=null)
                    ret.add(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        if(ret.size() == 1) return ret.get(0);
        else return null;
    }

}
