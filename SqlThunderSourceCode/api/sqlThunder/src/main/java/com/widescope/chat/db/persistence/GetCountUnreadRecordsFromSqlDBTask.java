package com.widescope.chat.db.persistence;


import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.util.concurrent.*;

public class GetCountUnreadRecordsFromSqlDBTask implements Callable<Long> {

    private final DbConnectionInfo connectionDetailInfo;
    private final String toUser;
    private final String isDelivered;


    public GetCountUnreadRecordsFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                              final String toUser,
                                              final String isDelivered) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.toUser = toUser;
        this.isDelivered = isDelivered;

    }

    @Override
    public Long call() throws Exception {
        try {
            return ChatPersistenceRef.isUnreadMessages(connectionDetailInfo, toUser, isDelivered);
        } catch (Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return 0L;
        }
    }

    public static long
    getUnreadCountRecordsDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String toUser, final String isDelivered) {
        long ret = 0;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db : dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Long> task = new GetCountUnreadRecordsFromSqlDBTask(dbConn, toUser, isDelivered);
                Future<Long> future = executor.submit(task);
                ret += future.get();
            } catch (Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }
}