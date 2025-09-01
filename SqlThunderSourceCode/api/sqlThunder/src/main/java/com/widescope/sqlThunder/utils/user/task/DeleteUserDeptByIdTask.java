package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.*;
import java.util.concurrent.*;

public class DeleteUserDeptByIdTask implements Callable<Boolean> {


    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final DbConnectionInfo connectionDetailInfo;
    private final int id;


    public DeleteUserDeptByIdTask(final DbConnectionInfo connectionDetailInfo,
                                  final int id) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteUserDeptByIdTask.deleteDepartment(connectionDetailInfo, id);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }



    public static boolean
    deleteDeptByIdDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final int id) {
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteUserDeptByIdTask(dbConn, id);
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
    deleteDepartment(final DbConnectionInfo connectionDetailInfo, final int id) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_department WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setInt(1, id);

            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
