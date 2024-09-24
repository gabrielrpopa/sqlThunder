package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class DeleteUserByIdTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


    private final  DbConnectionInfo connectionDetailInfo;
    private final  long id;


    public DeleteUserByIdTask(final DbConnectionInfo connectionDetailInfo,
                              final long id) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteUserByIdTask.deleteUser(connectionDetailInfo, id);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }



    public static boolean
    deleteUserByIdDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final long id) {
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteUserByIdTask(dbConn, id);
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
    deleteUser(final DbConnectionInfo connectionDetailInfo,
               final long id) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_reference WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setLong(1, id);
            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
