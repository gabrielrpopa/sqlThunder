package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.InternalUsersPersistenceRef;
import com.widescope.sqlThunder.utils.user.User;
import java.sql.*;
import java.util.concurrent.*;

public class ApproveRegisteredUserTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  String userName;



    public ApproveRegisteredUserTask(final DbConnectionInfo connectionDetailInfo,
                                     final String userName) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.userName = userName;
    }

    @Override
    public Boolean call() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            User u = getRegisteringUser(connectionDetailInfo, userName);
            InternalUsersPersistenceRef.addUser(connectionDetailInfo,u);
            deleteRegisteredUser(connectionDetailInfo, userName);
            return true;
        } catch(Exception ex) {
            AppLogger.logException(ex, className, methodName, AppLogger.db);
            return false;
        }
    }



    public static boolean
    approveDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String userName) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new ApproveRegisteredUserTask(dbConn, userName);
                Future<Boolean> future = executor.submit(task);
            } catch(Exception ignored) {
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    deleteRegisteredUser(final DbConnectionInfo connectionDetailInfo,
                         final String userTblReg) throws Exception	{
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_registration WHERE userName = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, userTblReg);
            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }

    public static User
    getRegisteringUser(final DbConnectionInfo connectionDetailInfo,
                       final String userName) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email FROM user_registration WHERE userName = ?";
        User ret = null;

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
                ret = new User(	rs.getLong("id"),
                                            rs.getString("userType"),
                                            rs.getString("userName"),
                                            rs.getString("password"),
                                            rs.getString("firstName"),
                                            rs.getString("lastName"),
                                            rs.getString("email"),
                                            -1,
                                            -1,
                                            -1,
                                            "",
                                            "",
                                            "",
                                            "",
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
