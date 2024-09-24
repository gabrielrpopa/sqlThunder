package com.widescope.sqlThunder.utils.user;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.security.SHA512Hasher;
import org.springframework.beans.factory.annotation.Autowired;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InternalUsersPersistenceRef {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


    public static final
    String repo_department = """
            CREATE TABLE IF NOT EXISTS user_department (id bigserial PRIMARY KEY,\r
            	departmentName character varying(99),\r
            	description character varying(999))""";


    public static final
    String repo_department_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_user_department_1 ON user_department(departmentName);";

    public static final
    String repo_department_first = " INSERT INTO user_department (departmentName,  description) 	VALUES('Supreme Department', 'Above All')";

    public static final
    String repo_department_second = " INSERT INTO user_department (departmentName,  description) VALUES('IT', 'Information Technology Department')";


    public static final
    String repo_title = """
            CREATE TABLE IF NOT EXISTS user_title (id bigserial PRIMARY KEY,\r
            	title character varying(999),\r
            	description character varying(999))""";


    public static final
    String repo_title_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_user_title_1 ON user_title(title);";

    public static final
    String repo_title_first = " INSERT INTO user_title (title,  description) VALUES('Supreme Being', 'Super User')";

    public static final
    String repo_title_second = " INSERT INTO user_title (title,  description) VALUES('Senior Developer', 'Software Developer')";




    public static final
    String repo_users = "CREATE TABLE IF NOT EXISTS user_reference (id bigserial PRIMARY KEY, "
                                                        + "	userType character varying(99) DEFAULT 'USER', "
                                                        + "	userName character varying(99), "
                                                        + "	password character varying(9999), "
                                                        + "	firstName character varying(99), "
                                                        + "	lastName character varying(99) , "
                                                        + "	email character varying(99) , "
                                                        + "	department BIGINT DEFAULT 1, "
                                                        + "	title BIGINT DEFAULT 1, "
                                                        + "	manager BIGINT DEFAULT 1, "
                                                        + "	characteristic character varying(9999), "
                                                        + "	description character varying(999), "
                                                        + "	active character varying(1) DEFAULT 'Y', "
                                                        + " CHECK (userType IN ('SUPER', 'ADMIN', 'USER', 'SERVICE') ) )";


    public static final
    String repo_users_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_user_reference_1 ON user_reference(userName);";


    public static final
    String repo_users_registration = "CREATE TABLE IF NOT EXISTS user_registration (id bigserial PRIMARY KEY,\r\n"
                                                                                    + "	userType character varying(99),\r\n"
                                                                                    + "	userName character varying(99),\r\n"
                                                                                    + "	password character varying(99),\r\n"
                                                                                    + "	firstName character varying(99),\r\n"
                                                                                    + "	lastName character varying(99) ,\r\n"
                                                                                    + "	email character varying(99) ,\r\n"
                                                                                    + "	timestamp BIGINT,  \r\n"
                                                                                    + " CHECK (userType IN ('SUPER', 'ADMIN', 'USER', 'SERVICE') )"
                                                                                    + ")";

    public static final
    String repo_usersReg_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_user_registration_1 ON user_registration(userName);";








    public static void
    createSchema(final DbConnectionInfo connectionDetailInfo, List<String> ddlList) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Connection conn = null;
        Statement statement = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(connectionDetailInfo.getJdbcDriver());

            //STEP 2: Open a connection
            conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
            //STEP 3: Execute a query
            statement = conn.createStatement();

            for (String ddl : ddlList) {
                statement.executeUpdate(ddl);
            }
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        } finally	{
            closeHandles(conn, statement);
        }
    }


    public static void generateSchema(final DbConnectionInfo connectionDetailInfo,
                                      final String superUser,
                                      final String superUserPassword,
                                      final String testUser,
                                      final String testUserPassword
                                      ) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        List<String> ddlList = new java.util.ArrayList<String>();
        try {
            ddlList.add(InternalUsersPersistenceRef.repo_department);
            ddlList.add(InternalUsersPersistenceRef.repo_department_index1);
            ddlList.add(InternalUsersPersistenceRef.repo_department_first);
            ddlList.add(InternalUsersPersistenceRef.repo_department_second);

            ddlList.add(InternalUsersPersistenceRef.repo_title);
            ddlList.add(InternalUsersPersistenceRef.repo_title_index1);
            ddlList.add(InternalUsersPersistenceRef.repo_title_first);
            ddlList.add(InternalUsersPersistenceRef.repo_title_second);

            ddlList.add(InternalUsersPersistenceRef.repo_users);
            ddlList.add(InternalUsersPersistenceRef.repo_users_index1);

            String sqlAdminUser = InternalUsersPersistenceRef.generateSuperUser(superUser, superUserPassword);
            if(sqlAdminUser!= null) ddlList.add(sqlAdminUser);

            String sqlTestUser = InternalUsersPersistenceRef.generateAnotherUser(testUser, testUserPassword);
            if(sqlTestUser!= null) ddlList.add(sqlTestUser);

            ddlList.add(InternalUsersPersistenceRef.repo_users_registration);
            ddlList.add(InternalUsersPersistenceRef.repo_usersReg_index1);

            InternalUsersPersistenceRef.createSchema(connectionDetailInfo, ddlList);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }


    private static void closeHandles(Connection conn, Statement statement) {
        try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
        try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
    }



    public static String generateSuperUser(String superUser, String superUserPassword) {
        if(superUser == null || superUser.isEmpty() || superUserPassword == null || superUserPassword.isEmpty()) {
            return null;
        }

        System.out.print("adding super user: " + superUser + "\n");
        System.out.print("initial password super user: " + superUserPassword + "\n");
        String hash = SHA512Hasher.hash(superUserPassword);

        return " INSERT INTO user_reference (userType,  userName, password, firstName, lastName, email, department, title, manager, description, active)  "
                + "	VALUES('SUPER', '" + superUser + "', '" + hash + "' , 'Super',  'Usr', '" + superUser + "', 1, 1, 0, 'Super User of all',  'Y')";
    }

    public static String generateAnotherUser(String testUser, String testPassword) {
        if(testUser == null || testUser.isEmpty() || testPassword == null || testPassword.isEmpty()) {
            return null;
        }

        System.out.print("adding user: " + testUser + "\n");
        System.out.print("initial password user: " + testPassword + "\n");
        String hash = SHA512Hasher.hash(testPassword);

        return " INSERT INTO user_reference (userType,  userName, password, firstName, lastName, email, department, title, manager, description, active) "
                + "	VALUES('USER', '" + testUser + "', '" + hash + "' , 'Infinite',  'Loop', '" + testUser + "', 1, 1, 0, 'Just a user',  'Y')";
    }







    public static boolean addUser(final DbConnectionInfo connectionDetailInfo,
                           final User u) throws Exception {

        return addUser(connectionDetailInfo, u.getUserType(),
                        u.getUser(),
                        u.getPassword(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        u.getDepartment(),
                        u.getTitle(),
                        u.getManager(),
                        u.getCharacteristic(),
                        u.getUserDescription(),
                        u.getActive() );
    }

    public static boolean
    addUser(final DbConnectionInfo connectionDetailInfo,
                           final String userType,
                           final String userName,
                           final String password,
                           final String firstName,
                           final String lastName,
                           final String email,
                           final int department,
                           final int title,
                           final int manager,
                           final String characteristic,
                           final String description,
                           final String active) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "INSERT INTO user_reference (userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            String uName = userName.toLowerCase();
            preparedStatement.setString(1, userType);
            preparedStatement.setString(2, uName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstName);
            preparedStatement.setString(5, lastName);

            preparedStatement.setString(6, email);
            preparedStatement.setInt(7, department);
            preparedStatement.setInt(8, title);
            preparedStatement.setInt(9, manager);
            preparedStatement.setString(10, characteristic);

            preparedStatement.setString(11, description);
            preparedStatement.setString(12, active);
            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }

    public static boolean
    addRegisteredUser(	final DbConnectionInfo connectionDetailInfo,
                        final String userType,
                        final String userName,
                        final String password,
                        final String firstName,
                        final String lastName,
                        final String email
    ) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = " MERGE INTO user_registration (userType, userName, password, firstName, lastName, email, timestamp) key(userName) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userType);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstName);
            preparedStatement.setString(5, lastName);
            preparedStatement.setString(6, email);
            preparedStatement.setLong(7, DateTimeUtils.millisecondsSinceEpoch());
            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }

    public static boolean
    addUser(	final DbConnectionInfo connectionDetailInfo,
                final String userType,
                final String userName,
                final String password,
                final String firstName,
                final String lastName,
                final String email,
                final String active
    ) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = " MERGE INTO user_reference (userType, userName, password, firstName, lastName, email, timestamp, active) key(userName) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userType);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstName);
            preparedStatement.setString(5, lastName);
            preparedStatement.setString(6, email);
            preparedStatement.setString(7, active);
            preparedStatement.setLong(7, DateTimeUtils.millisecondsSinceEpoch());
            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }



    public static List<User>
    getUsers(final DbConnectionInfo connectionDetailInfo,
             final List<Long> idList) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
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
                                + "FROM user_reference WHERE id IN (" + commaList + ")";
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
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }

        return ret;
    }





    public static List<User>
    getRegisteringUsers(final DbConnectionInfo connectionDetailInfo,
                        final String likeUser) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, firstName, lastName, email FROM user_registration WHERE userName like ? OR firstName like ? OR lastName like ? ";
        List<User> ret = new ArrayList<User>();

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
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }

        return ret;
    }

    public static User
    getUserById(final DbConnectionInfo connectionDetailInfo,
                final long id) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM user_reference WHERE id = ?";
        User ret = null;

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, id);
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











    public static List<User>
    getUsersMinusUser(final DbConnectionInfo connectionDetailInfo,
                      final String likeUser, final User u) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
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
                User b = new User(  rs.getLong("id"),
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

                if(u.getId() != b.getId())
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



    public static List<User>
    authUser(	final DbConnectionInfo connectionDetailInfo,
                final String userName,
                final String password) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active FROM user_reference WHERE userName = ? and password = ?";

        List<User> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, userName.toLowerCase());
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                User b = new User(rs.getLong("id"),
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
            preparedStatement.setInt(8, s.getDepartment());
            preparedStatement.setInt(9, s.getTitle());
            preparedStatement.setInt(10, s.getManager());
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

    public static boolean
    updateUser(final DbConnectionInfo connectionDetailInfo,
               final User s) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = " INSERT INTO user_reference (id, userType, userName, password, firstName, lastName, email, department, title, manager, characteristic, description, active)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, s.getId());
            preparedStatement.setString(2, s.getUserType());
            preparedStatement.setString(3, s.getUser().toLowerCase());
            preparedStatement.setString(4, s.getPassword());
            preparedStatement.setString(5, s.getFirstName());
            preparedStatement.setString(6, s.getLastName());
            preparedStatement.setString(7, s.getEmail());
            preparedStatement.setInt(8, s.getDepartment());
            preparedStatement.setInt(9, s.getTitle());
            preparedStatement.setInt(10, s.getManager());
            preparedStatement.setString(11, s.getCharacteristic());
            preparedStatement.setString(12, s.getUserDescription());
            preparedStatement.setString(13, s.getActive());

            int row = preparedStatement.executeUpdate();
            return row == 1;

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }









    //////////////////////////////////////////////////  department /////////////////////////////////////////////////////////////////////////////////
    public static void
    addDepartment(final DbConnectionInfo connectionDetailInfo, final Department d) throws Exception	{
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "INSERT INTO user_department (departmentName, description) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, d.getDepartment().toLowerCase());
            preparedStatement.setString(2, d.getDescription());
            preparedStatement.executeUpdate();

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }


    public static boolean
    deleteDepartment(final DbConnectionInfo connectionDetailInfo, final String departmentName) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "DELETE user_department WHERE department = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, departmentName.toLowerCase());

            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }

    public static List<Department>
    getDepartment(final DbConnectionInfo connectionDetailInfo, final String department) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, departmentName, description FROM user_department WHERE departmentName = ? ";
        List<Department> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, department.toLowerCase());

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                Department b = new Department(rs.getInt("id"),
                        rs.getString("departmentName"),
                        rs.getString("description"));
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

    public static Department
    getDepartmentById(final DbConnectionInfo connectionDetailInfo, final int id) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, departmentName, description FROM user_department WHERE id = ? ";
        Department ret = null;

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
                ret = new Department(rs.getInt("id"),
                        rs.getString("departmentName"),
                        rs.getString("description"));
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }

        return ret;
    }

    public static Department
    getDepartment(final DbConnectionInfo connectionDetailInfo, final int id) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, departmentName, description FROM user_department WHERE id = ? ";
        Department ret = null;

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
                ret = new Department(rs.getInt("id"),
                        rs.getString("departmentName"),
                        rs.getString("description"));
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }

        return ret;
    }


    //////////////////////////////////////////////////Titles /////////////////////////////////////////////////////////////////////////////////
    public static void
    addTitle(final DbConnectionInfo connectionDetailInfo, final Title t) throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "INSERT INTO user_title (title, description) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, t.getTitle().toLowerCase());
            preparedStatement.setString(2, t.getDescription());
            preparedStatement.executeUpdate();

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, className, methodName));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, className, methodName, AppLogger.db));
        }
    }















}
