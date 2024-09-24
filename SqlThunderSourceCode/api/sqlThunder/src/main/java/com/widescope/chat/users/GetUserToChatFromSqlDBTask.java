package com.widescope.chat.users;

import com.widescope.chat.db.persistence.ChatPersistenceRef;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import java.util.List;
import java.util.concurrent.Callable;

public class GetUserToChatFromSqlDBTask implements Callable<List<UserToChat>> {

    private final DbConnectionInfo connectionDetailInfo;
    private final String fromUser;
    private final String likeUser;


    public GetUserToChatFromSqlDBTask(final DbConnectionInfo connectionDetailInfo,
                                      final String likeUser,
                                      final String fromUser) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.likeUser = likeUser;
        this.fromUser = fromUser;
    }

    @Override
    public List<UserToChat> call() throws Exception {
        try {
            List<ChatUser> uList = ChatPersistenceRef.getUsersMinusUser(connectionDetailInfo, likeUser, fromUser);
            return ChatPersistenceRef.getUserToChat(connectionDetailInfo, fromUser);
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return null;
        }
    }


}
