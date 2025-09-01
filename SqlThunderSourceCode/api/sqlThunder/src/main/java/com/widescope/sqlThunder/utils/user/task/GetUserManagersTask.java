package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.ManagerShort;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUserManagersTask implements Callable<List<ManagerShort>> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  String likeName;


    public GetUserManagersTask(final DbConnectionInfo connectionDetailInfo,
                               final String likeName) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.likeName = likeName;
    }

    @Override
    public List<ManagerShort> call() throws Exception {
        try {
            return GetUserManagersTask.getManagers(connectionDetailInfo, likeName);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static List<ManagerShort>
    getUserManagersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String likeTitle) {
        List<ManagerShort> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<ManagerShort>> task = new GetUserManagersTask(dbConn, likeTitle);
                Future<List<ManagerShort>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }


    public static List<ManagerShort>
    getManagers(final DbConnectionInfo connectionDetailInfo,
                final String likeUser) throws Exception {

        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userName, firstName, lastName FROM user_reference WHERE userName like ? OR firstName like ? OR lastName like ?  OR description like ? ";
        List<ManagerShort> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(4, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ManagerShort b = new ManagerShort(rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                );
                ret.add(b);
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName())) ;
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
        }

        return ret;
    }

}
