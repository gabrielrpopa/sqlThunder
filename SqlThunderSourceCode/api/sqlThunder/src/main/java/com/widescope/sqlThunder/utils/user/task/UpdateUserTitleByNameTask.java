package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.Title;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UpdateUserTitleByNameTask implements Callable<Boolean> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo;
    private final  Title title ;


    public UpdateUserTitleByNameTask(final DbConnectionInfo connectionDetailInfo,
                                     final Title title) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.title = title;
    }

    @Override
    public Boolean call() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            return UpdateUserTitleByNameTask.updateTitle(connectionDetailInfo, title);
        } catch(Exception e) {
            AppLogger.logException(e, className, methodName, AppLogger.obj);
            return null;
        }
    }



    public static boolean
    updateTitleDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final Title title) {
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        List<Boolean> ret = new ArrayList<>();
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new UpdateUserTitleByNameTask(dbConn, title);
                Future<Boolean> future = executor.submit(task);
                ret.add(future.get());
            } catch(Exception e) {
                AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
                ret.add(false);
            }
        }
        executor.shutdown();
        return ret.stream().allMatch(n -> n); /*if only one item is FALSE, it returns FALSE*/
    }


    public static boolean
    updateTitle(final DbConnectionInfo connectionDetailInfo, final Title t) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "UPDATE user_title SET title = ?, description = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

            preparedStatement.setString(1, t.getTitle().toLowerCase());
            preparedStatement.setString(2, t.getDescription());
            preparedStatement.setLong(3, t.getId());

            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

}
