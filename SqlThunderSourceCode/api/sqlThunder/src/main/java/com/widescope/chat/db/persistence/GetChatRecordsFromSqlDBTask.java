package com.widescope.chat.db.persistence;

import com.widescope.chat.db.ChatRecord;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetChatRecordsFromSqlDBTask implements Callable<List<ChatRecord>> {

    private final DbConnectionInfo connectionDetailInfo;
    private final String fromUser;
    private final String toUser ;
    private final String isDelivered;
    private final long fromDate;
    private final long toDate;


    public GetChatRecordsFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                       final String fromUser,
                                       final String toUser,
                                       final String isDelivered,
                                       final long fromDate) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.isDelivered = isDelivered;
        this.fromDate = fromDate;
        this.toDate = 0;
    }

    public GetChatRecordsFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                       final String fromUser,
                                       final String toUser,
                                       final long fromDate,
                                       final long toDate
                                       ) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.isDelivered = null;
    }

    @Override
    public List<ChatRecord> call() throws Exception {
        try {
            if(isDelivered!=null && fromDate == 0 && toDate == 0)
                return ChatPersistenceDb.getMessages(connectionDetailInfo, fromUser, toUser, isDelivered);
            else if(isDelivered==null && fromDate > 0 && toDate > 0) {
                return ChatPersistenceDb.getMessageHist(connectionDetailInfo, fromUser, toUser, fromDate, toDate);
            } else {
                return ChatPersistenceDb.getMessageHist(connectionDetailInfo, fromUser, toUser, fromDate, toDate);
            }
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return null;
        }
    }

    public static List<ChatRecord>
    getUnreadChatRecordsDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser, final String toUser, final long fromDate) {
        List<ChatRecord> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<ChatRecord>> task = new GetChatRecordsFromSqlDBTask(dbConn, fromUser, toUser, "N", fromDate);
                Future<List<ChatRecord>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }








    public static List<ChatRecord>
    getChatRecordsHistDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser, final String toUser, final long fromDate, final long toDate) {
        List<ChatRecord> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<ChatRecord>> task = new GetChatRecordsFromSqlDBTask(dbConn, fromUser, toUser, fromDate, toDate);
                Future<List<ChatRecord>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }

    public static List<ChatRecord>
    getReadChatRecordsDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String fromUser, final String toUser, final long fromDateTime) {
        List<ChatRecord> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<ChatRecord>> task = new GetChatRecordsFromSqlDBTask(dbConn, fromUser, toUser, "Y", fromDateTime);
                Future<List<ChatRecord>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }
}
