package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class UpdateMyPasswordTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo ;
    private final  long id ;
    private final  String password ;


    public UpdateMyPasswordTask(final DbConnectionInfo connectionDetailInfo,
                                long id,
                                final String password) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.password = password;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return UpdateMyPasswordTask.updateMePassword(connectionDetailInfo, id, password);
        } catch(Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }



    public static boolean
    updateMyPasswordDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final long id, final String password) {
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new UpdateMyPasswordTask(dbConn, id, password);
                Future<Boolean> future = executor.submit(task);
            } catch(Exception e) {
                AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    updateMePassword(final DbConnectionInfo connectionDetailInfo,
                     final long id,
                     final String password) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = " UPDATE user_reference SET password = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, password);
            preparedStatement.setLong(2, id);


            int row = preparedStatement.executeUpdate();
            return row == 1;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
