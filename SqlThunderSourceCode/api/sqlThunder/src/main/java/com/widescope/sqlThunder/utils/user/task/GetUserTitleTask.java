package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.Title;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUserTitleTask implements Callable<Title> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  int id;


    public GetUserTitleTask(final DbConnectionInfo connectionDetailInfo,
                            final int id) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.id = id;
    }

    @Override
    public Title call() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            return GetUserTitleTask.getTitle(connectionDetailInfo, id);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, methodName, AppLogger.db);
            return null;
        }
    }



    public static Title
    getTitleDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final int id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        List<Title> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Title> task = new GetUserTitleTask(dbConn, id);
                Future<Title> future = executor.submit(task);
                ret.add(future.get());
            } catch(Exception e) {
                AppLogger.logException(e, className, methodName, AppLogger.db);
            }
        }
        executor.shutdown();
        if(!ret.isEmpty())
            return ret.get(0);
        else
            return null;
    }


    public static Title
    getTitle(final DbConnectionInfo connectionDetailInfo, final int id) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, title, description FROM user_title WHERE id = ? ";
        Title ret = null;

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() )	{
                ret = new Title(rs.getInt("id"), rs.getString("title"), rs.getString("description"));
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }

        return ret;
    }

}
