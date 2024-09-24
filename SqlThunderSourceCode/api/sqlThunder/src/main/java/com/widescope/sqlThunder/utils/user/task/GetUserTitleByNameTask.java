package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.Title;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUserTitleByNameTask implements Callable<Title> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo;
    private final  String title;


    public GetUserTitleByNameTask(final DbConnectionInfo connectionDetailInfo,
                                  final String title) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.title = title;
    }

    @Override
    public Title call() throws Exception {
        try {
            return GetUserTitleByNameTask.getTitle(connectionDetailInfo, title);
        } catch(Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static Title
    getTitleDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String title) {
        List<Title> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Title> task = new GetUserTitleByNameTask(dbConn, title);
                Future<Title> future = executor.submit(task);
                ret.add(future.get());
            } catch(Exception e) {
                AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            }
        }
        executor.shutdown();
        if(!ret.isEmpty())
            return ret.get(0);
        else
            return null;
    }


    public static Title getTitle(final DbConnectionInfo connectionDetailInfo, final String title) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, title, description FROM user_title WHERE title = ? ";
        List<Title> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, title.toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() )	{
                Title b = new Title(rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getString("description"));
                ret.add(b);
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName())) ;
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
        }

        if(!ret.isEmpty())
            return ret.get(0);
        else
            return null;
    }

}
