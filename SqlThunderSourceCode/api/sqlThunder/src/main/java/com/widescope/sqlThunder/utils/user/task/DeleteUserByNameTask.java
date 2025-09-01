package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class DeleteUserByNameTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo;
    private final  String userName;


    public DeleteUserByNameTask(final DbConnectionInfo connectionDetailInfo,
                                final String userName) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.userName = userName;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteUserByNameTask.deleteUser(connectionDetailInfo, userName);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }



    public static boolean
    deleteUserByNameDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String userName) {
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteUserByNameTask(dbConn, userName);
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
               final String userName) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_reference WHERE userName = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            String uName = userName.toLowerCase();
            preparedStatement.setString(1, uName);

            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName())) ;
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
        }
    }

}
