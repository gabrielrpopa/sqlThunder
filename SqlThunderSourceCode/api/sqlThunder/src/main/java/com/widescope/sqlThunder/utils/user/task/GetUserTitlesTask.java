package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.Title;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUserTitlesTask implements Callable<List<Title>> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  String likeTitle;


    public GetUserTitlesTask(final DbConnectionInfo connectionDetailInfo,
                             final String likeTitle) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.likeTitle = likeTitle;
    }

    @Override
    public List<Title> call() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            return GetUserTitlesTask.getTitles(connectionDetailInfo, likeTitle);
        } catch (SQLException e)	{
            AppLogger.logDb(e, className, methodName);
            return new ArrayList<>();
        } catch (Exception e) {
            AppLogger.logException(e, className, methodName, AppLogger.db);
            return new ArrayList<>();
        }
    }



    public static List<Title>
    getTitlesDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String likeTitle) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        List<Title> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<Title>> task = new GetUserTitlesTask(dbConn, likeTitle);
                Future<List<Title>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch (SQLException e)	{
                AppLogger.logDb(e, className, methodName);
            } catch (Exception e) {
                AppLogger.logException(e, className, methodName, AppLogger.db);
            }
        }
        executor.shutdown();
        return ret;
    }


    public static List<Title>
    getTitles(final DbConnectionInfo connectionDetailInfo, final String likeTitle) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, title, description FROM user_title WHERE title like ? ";
        List<Title> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeTitle.toLowerCase() + '%');

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() )	{
                Title b = new Title(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"));
                ret.add(b);
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
