package com.widescope.chat.users;

import com.widescope.chat.db.persistence.ChatPersistenceRef;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import java.util.concurrent.Callable;

public class GetChatUserFromSqlDBTask implements Callable<ChatUser> {

    private final DbConnectionInfo connectionDetailInfo;
    private final String fromUser;


    public GetChatUserFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                    final String fromUser) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.fromUser = fromUser;
    }

    @Override
    public ChatUser call() throws Exception {
        try {
            return ChatPersistenceRef.getChatUserTo(connectionDetailInfo, fromUser);
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return null;
        }
    }


}