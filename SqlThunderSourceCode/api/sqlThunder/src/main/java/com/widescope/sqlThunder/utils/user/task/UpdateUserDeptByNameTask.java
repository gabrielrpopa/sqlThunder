package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.Department;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class UpdateUserDeptByNameTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final DbConnectionInfo connectionDetailInfo;
    private final Department department;


    public UpdateUserDeptByNameTask(final DbConnectionInfo connectionDetailInfo,
                                    final Department d) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.department = d;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return UpdateUserDeptByNameTask.updateDepartment(connectionDetailInfo, department);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static boolean
    updateDeptDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final Department d) {
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        boolean ret = true;
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new UpdateUserDeptByNameTask(dbConn, d);
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
    updateDepartment(final DbConnectionInfo connectionDetailInfo, final Department d) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "UPDATE user_department SET departmentName = ?, description = ? WHERE id = ? ";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

            preparedStatement.setString(1, d.getDepartment().toLowerCase());
            preparedStatement.setString(2, d.getDescription());
            preparedStatement.setInt(3, d.getId());

            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
