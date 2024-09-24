package com.widescope.chat.db.persistence;

import com.widescope.chat.db.ChatRecord;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetOutstandingUsersFromSqlDBTask implements Callable<List<UserPairValue>> {

    private final DbConnectionInfo connectionDetailInfo;
    private final String toUser;

    public GetOutstandingUsersFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                            final String toUser) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.toUser = toUser;

    }


    @Override
    public List<UserPairValue> call() throws Exception {
        try {
            return ChatPersistenceRef.getUsersWithOutstandingMessages(connectionDetailInfo, toUser, "N");

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return null;
        }
    }



    public static List<UserPairValue>
    getGetOutstandingUsersFromSqlDBTask(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String toUser) {
        List<UserPairValue> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<UserPairValue>> task = new GetOutstandingUsersFromSqlDBTask(dbConn, toUser);
                Future<List<UserPairValue>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }



}
