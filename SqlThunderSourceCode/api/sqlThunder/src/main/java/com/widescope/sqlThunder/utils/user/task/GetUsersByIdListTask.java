package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GetUsersByIdListTask implements Callable<List<User>> {
    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  List<Long> idList;


    public GetUsersByIdListTask(final DbConnectionInfo connectionDetailInfo,
                                final List<Long> idList) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.idList = idList;
    }

    @Override
    public List<User> call() throws Exception {
        try {
            return GetUsersByIdListTask.getUsers(connectionDetailInfo, idList);
        } catch(Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static List<User>
    getUsersDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final List<Long> idList) {
        List<User> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<User>> task = new GetUsersByIdListTask(dbConn, idList);
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
    getUsers(final DbConnectionInfo connectionDetailInfo, final List<Long> idList) throws Exception {

        if(idList.isEmpty()) return new ArrayList<User>();
        String commaList = idList.stream().map(String::valueOf).collect(Collectors.joining(","));
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, "
                        + " userType, "
                        + " userName, "
                        + " password, "
                        + " firstName, "
                        + " lastName, "
                        + " email, "
                        + " department, "
                        + " title, "
                        + " manager, "
                        + " characteristic, "
                        + " description, "
                        + " active "
                        + "FROM userTbl WHERE id IN (" + commaList + ")";
        List<User> ret = new ArrayList<User>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            //preparedStatement.setString(1, commaList);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                User u = new User(	rs.getLong("id"),
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
                ret.add(u);

            }
            rs.close();
        } catch(Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
        }

        return ret;
    }




}
