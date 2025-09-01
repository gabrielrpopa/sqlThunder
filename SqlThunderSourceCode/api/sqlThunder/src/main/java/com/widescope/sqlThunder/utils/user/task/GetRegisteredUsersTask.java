package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetRegisteredUsersTask implements Callable<List<User>> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo ;
    private final  String likeName;


    public GetRegisteredUsersTask(final DbConnectionInfo connectionDetailInfo,
                                  final String likeName) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.likeName = likeName;
    }

    @Override
    public List<User> call() throws Exception {
        try {
            return GetRegisteredUsersTask.getRegisteringUsers(connectionDetailInfo, likeName);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static List<User>
    getRegisteredUsersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String likeTitle) {
        List<User> uList = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<User>> task = new GetRegisteredUsersTask(dbConn, likeTitle);
                Future<List<User>> future = executor.submit(task);
                uList.addAll(future.get());
            } catch(Exception ex) {
                AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            }
        }
        executor.shutdown();
        return uList;
    }


    public static List<User>
    getRegisteringUsers(final DbConnectionInfo connectionDetailInfo, final String likeUser) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, firstName, lastName, email FROM userTblReg WHERE userName like ? OR firstName like ? OR lastName like ? ";
        List<User> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(2, '%' + likeUser.toLowerCase() + '%');
            preparedStatement.setString(3, '%' + likeUser.toLowerCase() + '%');
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                User u = new User(	rs.getLong("id"),
                                    rs.getString("userType"),
                                    rs.getString("userName"),
                                    "",
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
                ret.add(u);
            }
            rs.close();
        } catch(Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            throw e;
        }

        return ret;
    }

}
