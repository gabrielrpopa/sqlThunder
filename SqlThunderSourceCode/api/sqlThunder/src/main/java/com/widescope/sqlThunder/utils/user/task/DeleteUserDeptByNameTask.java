package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class DeleteUserDeptByNameTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo;
    private final  String name ;


    public DeleteUserDeptByNameTask(final DbConnectionInfo connectionDetailInfo,
                                    final String name) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.name = name;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return DeleteUserDeptByNameTask.deleteDepartment(connectionDetailInfo, name);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }



    public static boolean
    deleteDeptByNameDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String name) {
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new DeleteUserDeptByNameTask(dbConn, name);
                Future<Boolean> future = executor.submit(task);
            } catch(Exception ignored) {
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    deleteDepartment(final DbConnectionInfo connectionDetailInfo, final String name) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_department WHERE departmentName = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, name);

            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName());
            return false;
        } catch (Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return false;
        }
    }

}
