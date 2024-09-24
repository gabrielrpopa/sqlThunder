package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUserByNameTask implements Callable<User> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final DbConnectionInfo connectionDetailInfo;
    private final String userName;


    public GetUserByNameTask(final DbConnectionInfo connectionDetailInfo,
                             final String userName) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.userName = userName;
    }

    @Override
    public User call() throws Exception {
        try {
            return GetUserByNameTask.getUser(connectionDetailInfo, userName);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.db);
            return null;
        }
    }



    public static User
    getUsersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String userName) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        List<User> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<User> task = new GetUserByNameTask(dbConn, userName);
                Future<User> future = executor.submit(task);
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


    public static User
    getUser(final DbConnectionInfo connectionDetailInfo,
            final String userName) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM user_reference WHERE userName = ?";
        User ret = null;

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            String uName = userName.toLowerCase();
            preparedStatement.setString(1, uName);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
                ret = new User(	rs.getLong("id"),
                        rs.getString("userType"),
                        rs.getString("userName"),
                        rs.getString("password"),
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
