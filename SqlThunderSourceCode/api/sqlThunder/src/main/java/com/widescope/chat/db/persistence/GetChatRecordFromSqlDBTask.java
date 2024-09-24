package com.widescope.chat.db.persistence;

import com.widescope.chat.db.ChatRecord;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetChatRecordFromSqlDBTask implements Callable<ChatRecord> {

    private final DbConnectionInfo connectionDetailInfo;
    private final String fromUser;
    private final String toUser;
    private final String requestId;
    private final String operation; /*SET/DELETE*/

    public GetChatRecordFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                      final String fromUser,
                                      final String toUser,
                                      String requestId,
                                      String operation) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.requestId = requestId;
        this.operation = operation;
    }

    @Override
    public ChatRecord call() throws Exception {
        try {
            if(this.operation.compareToIgnoreCase("GET") == 0)
                return ChatPersistenceRef.getMessage(connectionDetailInfo, fromUser, toUser, requestId);
            else if(this.operation.compareToIgnoreCase("SET") == 0)
                return ChatPersistenceRef.setReadMessage(connectionDetailInfo, fromUser, toUser, requestId);
            else
                return ChatPersistenceRef.deleteMessage(connectionDetailInfo, fromUser, toUser, requestId);

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return null;
        }
    }

    public static ChatRecord
    getChatRecordDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser, final String toUser, final String requestId) {
        List<ChatRecord> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<ChatRecord> task = new GetChatRecordFromSqlDBTask(dbConn, fromUser, toUser, requestId, "GET");
                Future<ChatRecord> future = executor.submit(task);
                ret.add(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret.get(0);
    }

    public static ChatRecord
    setReadChatRecordDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser, final String toUser, final String requestId) {
        List<ChatRecord> ret = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<ChatRecord> task = new GetChatRecordFromSqlDBTask(dbConn, fromUser, toUser, requestId, "SET");
                Future<ChatRecord> future = executor.submit(task);
                ret.add(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret.get(0);
    }
    public static void
    deleteChatRecordDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser, final String toUser, final String requestId) {
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<ChatRecord> task = new GetChatRecordFromSqlDBTask(dbConn, fromUser, toUser, requestId, "DELETE");
                Future<ChatRecord> future = executor.submit(task);
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
    }




}
