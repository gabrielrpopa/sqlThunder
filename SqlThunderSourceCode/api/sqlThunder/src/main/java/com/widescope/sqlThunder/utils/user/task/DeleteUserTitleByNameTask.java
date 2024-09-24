package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class DeleteUserTitleByNameTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo ;
    private final  String title;


    public DeleteUserTitleByNameTask(final DbConnectionInfo connectionDetailInfo,
                                     final String title) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.title = title;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteUserTitleByNameTask.deleteTitle(connectionDetailInfo, title);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static boolean
    deleteTitleDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String title) {
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        boolean ret = true;
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteUserTitleByNameTask(dbConn, title);
                Future<Boolean> future = executor.submit(task);

            } catch(Exception ex) {
                AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    deleteTitle(final DbConnectionInfo connectionDetailInfo, final String title) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_title WHERE title = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, title);
            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
