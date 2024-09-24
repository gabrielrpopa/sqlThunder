package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.User;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUsersTask implements Callable<List<User>> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  String likeStr;


    public GetUsersTask(final DbConnectionInfo connectionDetailInfo,
                        final String likeStr) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.likeStr = likeStr;
    }

    @Override
    public List<User> call() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            return GetUsersTask.getUsers(connectionDetailInfo, likeStr);
        } catch(Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static List<User>
    getUsersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String likeStr) {
        List<User> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<User>> task = new GetUsersTask(dbConn, likeStr);
                Future<List<User> > future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception e) {
                AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }


    public static List<User>
    getUsers(final DbConnectionInfo connectionDetailInfo,
             final String likeUser) throws Exception {

        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM user_reference WHERE userName like ? OR firstName like ? OR lastName like ?  OR description like ? ";
        List<User> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(4, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                User b = new User(rs.getLong("id"),
                        rs.getString("userType"),
                        rs.getString("userName"),
                        "",
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getInt("department"),
                        rs.getInt("title"),
                        rs.getInt("manager"),
                        rs.getString("characteristic"),
                        rs.getString("description"),
                        rs.getString("active"),
                        "N",
                        null);
                ret.add(b);
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }

        return ret;
    }

}
