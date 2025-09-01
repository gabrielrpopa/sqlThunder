package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetUserDeptsTask implements Callable<List<Department>> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private final  DbConnectionInfo connectionDetailInfo;
    private final  String likeDept;


    public GetUserDeptsTask(final DbConnectionInfo connectionDetailInfo,
                            final String likeDept) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.likeDept = likeDept;
    }

    @Override
    public List<Department> call() throws Exception {
        try {
            return GetUserDeptsTask.getDepartments(connectionDetailInfo, likeDept);
        } catch(Exception ex) {
            AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
        }
    }



    public static List<Department>
    getDeptsDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final String likeTitle) {
        List<Department> ret = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<List<Department>> task = new GetUserDeptsTask(dbConn, likeTitle);
                Future<List<Department>> future = executor.submit(task);
                ret.addAll(future.get());
            } catch(Exception e) {
                AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            }
        }
        executor.shutdown();
        return ret;
    }


    public static List<Department>
    getDepartments(final DbConnectionInfo connectionDetailInfo, final String likeDept) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, departmentName, description FROM user_department WHERE departmentName like ? ";
        List<Department> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeDept.toLowerCase() + '%');

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                Department b = new Department(rs.getInt("id"),
                        rs.getString("departmentName"),
                        rs.getString("description"));
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
