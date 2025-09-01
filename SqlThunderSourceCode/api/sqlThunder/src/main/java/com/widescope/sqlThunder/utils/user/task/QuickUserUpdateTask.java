package com.widescope.sqlThunder.utils.user.task;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.utils.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class QuickUserUpdateTask implements Callable<Boolean> {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final  DbConnectionInfo connectionDetailInfo;
    private final  User u;



    public QuickUserUpdateTask(final DbConnectionInfo connectionDetailInfo,
                               final User u) {
        this.connectionDetailInfo = connectionDetailInfo;
        this.u = u;
    }

    @Override
    public Boolean call() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {

            User usr = QuickUserUpdateTask.getUser(connectionDetailInfo, u.getUser());
            if(usr == null) {
                AppLogger.logError(className, methodName, AppLogger.db, u.getUser() + "user does not exist");
                // user doesn't exist
                return false;
            }
            /*if regular user just update, but if SUPER check if limit is between 1 and 10*/
            if(usr.getUserType().toUpperCase().compareTo("SUPER") == 0
                    || u.getUserType().toUpperCase().compareTo("SUPER") == 0) {
                List<User> supers = QuickUserUpdateTask.getAllUsersByType(connectionDetailInfo, "SUPER");
                if(supers.size() >= 9 && u.getUserType().compareToIgnoreCase("SUPER") == 0 && usr.getUserType().compareToIgnoreCase("SUPER") !=0) { /*we can have another SUPER*/
                    throw new Exception(AppLogger.logError(className, methodName, AppLogger.db, "No more SUPER users allowed"));
                } else {
                    return QuickUserUpdateTask.updateSuper(connectionDetailInfo, u);
                }
            } else { /*Regular user can be updated regardless*/
                return QuickUserUpdateTask.quickUserUpdate(connectionDetailInfo, u);
            }
        } catch(Exception ex) {
            AppLogger.logException(ex, className, methodName, AppLogger.db);
            return false;
        }
    }



    public static boolean
    quickUserUpdateDistributed(final ConcurrentMap<String, SqlRepoDatabase> dbs, final User u) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        boolean ret = true;
        ExecutorService executor = Executors.newFixedThreadPool(dbs.size());
        for (SqlRepoDatabase db: dbs.values()) {
            try {
                DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(db);
                Callable<Boolean> task = new QuickUserUpdateTask(dbConn, u);
                Future<Boolean> future = executor.submit(task);
            } catch(Exception ex) {
                AppLogger.logException(ex, className, methodName, AppLogger.db);
                ret = false;
            }
        }
        executor.shutdown();
        return ret;
    }


    public static boolean
    quickUserUpdate(final DbConnectionInfo connectionDetailInfo,
                    final User s) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = " UPDATE user_reference "
                                + " SET userType = ?, "
                                + " department = ?, "
                                + " title = ?, "
                                + " manager = ?, "
                                + " characteristic = ?, "
                                + " description = ?, "
                                + " active = ? "
                                + " WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{

            preparedStatement.setString(1, s.getUserType());
            preparedStatement.setLong(2, s.getDepartment());
            preparedStatement.setLong(3, s.getTitle());
            preparedStatement.setLong(4, s.getManager());
            preparedStatement.setString(5, s.getCharacteristic());
            preparedStatement.setString(6, s.getUserDescription());
            preparedStatement.setString(7, s.getActive());
            preparedStatement.setLong(8, s.getId());

            int row = preparedStatement.executeUpdate();

            if(row == 0) {
                AppLogger.logError(className, methodName, AppLogger.db, s.getEmail() + " was not updated");
            }

            return row == 1;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }


    public static boolean
    updateSuper(final DbConnectionInfo connectionDetailInfo,
                final User s) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = " INSERT INTO user_reference (id, userName, password, firstName, lastName, email, department, title, manager, characteristic, description)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, s.getId());
            preparedStatement.setString(3, s.getUser().toLowerCase());
            preparedStatement.setString(4, s.getPassword());
            preparedStatement.setString(5, s.getFirstName());
            preparedStatement.setString(6, s.getLastName());
            preparedStatement.setString(7, s.getEmail());
            preparedStatement.setLong(8, s.getDepartment());
            preparedStatement.setLong(9, s.getTitle());
            preparedStatement.setLong(10, s.getManager());
            preparedStatement.setString(11, s.getCharacteristic());
            preparedStatement.setString(12, s.getUserDescription());

            int row = preparedStatement.executeUpdate();
            return row == 1;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }


    public static List<User>
    getAllUsersByType(final DbConnectionInfo connectionDetailInfo,
                      final String userType) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM user_reference WHERE userType = ?";
        List<User> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userType);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                User b = new User(rs.getInt("id"),
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
