package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DeleteUserTitleTask implements Callable<Boolean> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  long id;


    public DeleteUserTitleTask(final DbConnectionInfo connectionDetailInfo,
                               final long id) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteUserTitleTask.deleteTitle(connectionDetailInfo, id);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.thread);
            return null;
        }
    }



    public static boolean
    deleteTitleDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final long id) {
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        List<Boolean> ret = new ArrayList<>();
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteUserTitleTask(dbConn, id);
                Future<Boolean> future = executor.submit(task);
                ret.add(future.get());
            } catch(Exception e) {
                AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
                ret.add(false);
            }
        }
        executor.shutdown();
        return true;
    }


    public static boolean
    deleteTitle(final DbConnectionInfo connectionDetailInfo, final long id) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_title WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);
            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName())) ;
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.thread)) ;
        }
    }

}
