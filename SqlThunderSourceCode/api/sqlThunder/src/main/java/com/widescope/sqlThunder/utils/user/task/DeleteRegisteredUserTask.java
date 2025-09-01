package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class DeleteRegisteredUserTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  String userName;


    public DeleteRegisteredUserTask(final DbConnectionInfo connectionDetailInfo,
                                    final String userName) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.userName = userName;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteRegisteredUserTask.deleteRegisteredUser(connectionDetailInfo, userName);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static boolean
    deleteTitleDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String userName) {
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        boolean ret = true;
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteRegisteredUserTask(dbConn, userName);
                executor.submit(task);
            } catch(Exception ex) {
                AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    deleteRegisteredUser(final DbConnectionInfo connectionDetailInfo,
                         final String userTblReg) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_registration WHERE userName = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, userTblReg);
            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName())) ;
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
        }
    }

}
