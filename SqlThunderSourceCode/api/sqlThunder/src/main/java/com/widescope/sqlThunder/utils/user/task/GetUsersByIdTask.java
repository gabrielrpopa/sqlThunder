package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class GetUsersByIdTask implements Callable<User> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  long id;


    public GetUsersByIdTask(final DbConnectionInfo connectionDetailInfo,
                            final long id) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.id = id;
    }

    @Override
    public User call() throws Exception {
        try {
            return GetUsersByIdTask.getUser(connectionDetailInfo, id);
        } catch(Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static User
    getUsersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final long id) {
        List<User> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<User> task = new GetUsersByIdTask(dbConn, id);
                Future<User> future = executor.submit(task);
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


    public static User
    getUser(final DbConnectionInfo connectionDetailInfo,
            final long id) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM user_reference WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
                return new User(rs.getLong("id"),
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
            throw new Exception(AppLogger.logDb(e, className, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }

        return null;
    }




}
