package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class UpdateMyEmailAndUserNameTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo;
    private final  long id;
    private final  String userName;
    private final  String email;


    public UpdateMyEmailAndUserNameTask(final DbConnectionInfo connectionDetailInfo,
                                        final long id,
                                        final String userName,
                                        final String email) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.userName = userName;
        this.email = email;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return UpdateMyEmailAndUserNameTask.updateMeEmailUserName(connectionDetailInfo, id, userName, email);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }



    public static boolean
    updateMyEmailAndUSerNameDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final long id, final String userName, final String email) {
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new UpdateMyEmailAndUserNameTask(dbConn, id, userName, email);
                Future<Boolean> future = executor.submit(task);
            } catch(Exception ignored) {
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    updateMeEmailUserName(	final DbConnectionInfo connectionDetailInfo,
                              final long id,
                              final String userName,
                              final String email) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "UPDATE user_reference SET userName = ?, email = ? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, email);

            int row = preparedStatement.executeUpdate();
            return row == 1;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
