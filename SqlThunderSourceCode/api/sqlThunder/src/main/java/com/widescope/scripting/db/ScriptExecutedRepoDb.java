package com.widescope.scripting.db;

import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.*;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.storage.RepoHistoryInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScriptExecutedRepoDb implements RepoHistoryInterface {

    @Override
    public String getRepoTitle() { return RepoStaticDesc.scriptRepo; }

    // JDBC driver name and database URL
    private final String JDBC_DRIVER = "org.h2.Driver";
    private final String DB_URL_DISK = "jdbc:h2:file:./scriptExecutedRepoDb;MODE=PostgreSQL";
    //  Database credentials
    private final String USER = "sa";
    private final String PASS = "sa";

    // Getters for access wrappers
    public String getDB_URL_DISK() { return DB_URL_DISK; }
    public String getUSER() { return USER; }
    public String getJDBC_DRIVER() { return JDBC_DRIVER; }
    public String getPASS()  { return PASS; }

    public ScriptExecutedRepoDb()	{

    }


    public static void createDatabase() {
        String fileName = "./scriptExecutedRepoDb.mv.db";
        if(!FileUtilWrapper.isFilePresent(fileName)) {
            try {
                ScriptExecutedRepoDb.generateSchema();
                AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ScriptExecutedRepoDb created");
            } catch(Exception e) {
                AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            }
        }
    }

    private
    void
    closeHandles(	Connection conn,
                     Statement statement,
                     ResultSet rs){
        try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
        try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
        try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
    }



    private
    void
    createSchema(final List<String> ddlList) throws Exception	{
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
            statement = conn.createStatement();
            for (String ddl : ddlList) {
                statement.executeUpdate(ddl);
            }

            statement.close();
            conn.commit();
            conn.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        } finally	{
            closeHandles(conn, statement, null);
        }
    }






    public
    static
    void generateSchema() throws Exception {

        try {
            final List<String> ddlList = getStrings();
            ScriptExecutedRepoDb scriptExecutedRepoDb = new ScriptExecutedRepoDb();
            scriptExecutedRepoDb.createSchema(ddlList);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }


    }

    @NotNull
    private static List<String> getStrings() {
        List<String> ddlList = new java.util.ArrayList<>();

        ddlList.add(ExecutionGroup.groupTable);
        ddlList.add(ExecutionGroup.groupTableIndex1);
        ddlList.add(ExecutionGroup.createTestGroup);

        ddlList.add(ExecutionUserAccess.accessRefTable);
        ddlList.add(ExecutionUserAccess.accessRefTableIndex1);
        ddlList.add(ExecutionUserAccess.accessRefTableConst1);

        ddlList.add(ScriptExecutedRepoDb.executionScriptTable);
        ddlList.add(ScriptExecutedRepoDb.executionScriptTable_const1);
        ddlList.add(ScriptExecutedRepoDb.executionScriptTable_const2);
        ddlList.add(ScriptExecutedRepoDb.executionScriptTable_index2);
        ddlList.add(ScriptExecutedRepoDb.executionScriptTable_index3);
        ddlList.add(ScriptExecutedRepoDb.executionScriptTableFk3);
        ddlList.add(ScriptExecutedRepoDb.accessRefTableFk3);
        return ddlList;
    }




    /*scriptName is provided by executing user, For Adhoc is an instant name, for repo could be the same as repo script or a variation of it. This is not enforced or suggested*/
    public static final
    String executionScriptTable = "CREATE TABLE IF NOT EXISTS executionScriptTable(id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY, " +
                                                                                        "scriptId BIGINT DEFAULT -1, " +  /*id found in repo table, adhoc scripts won't have scriptId, defaulted to -1 */
                                                                                        "requestId VARCHAR(MAX), " +
                                                                                        "scriptName VARCHAR(MAX), " +
                                                                                        "sourceMachine VARCHAR(MAX), " +
                                                                                        "destinationMachine VARCHAR(MAX), " +
                                                                                        "interpreterId BIGINT, " +
                                                                                        "groupId BIGINT, " +              /* user defined group. reference to groupTable defined in ExecutionGroup class */
                                                                                        "src VARCHAR(1) DEFAULT 'A', " +   /*A-ADHOC or R-REPO defined in Constants class adhocShort/repoShort */
                                                                                        "userId BIGINT, " +
                                                                                        "scriptContent VARCHAR(MAX), " +
                                                                                        "jsonParam VARCHAR(MAX), " +
                                                                                        "repPath VARCHAR(MAX), " +
                                                                                        "comment VARCHAR(MAX), " +
                                                                                        "timestamp BIGINT," +
                                                                                        "isValid VARCHAR(1) DEFAULT 'N'  ) ";


    /*A-ADHOC or R-REPO*/
    public static final
    String executionScriptTable_const1 = "ALTER TABLE executionScriptTable ADD CONSTRAINT IF NOT EXISTS ck_executionScriptTable_1 "
            + "CHECK (src IN (" + PersistenceSourceList.genCommaSeparatedSourceList() + ") );"; /*A-adhoc, R-repository*/

    private
    static final
    String executionScriptTable_const2 = "ALTER TABLE executionScriptTable ADD CONSTRAINT IF NOT EXISTS ck_executionScriptTable_2  CHECK (isValid IN ('Y', 'N') );";



    public static final
    String executionScriptTable_index2 = "CREATE INDEX IF NOT EXISTS  idx_executionScriptTable_2 ON executionScriptTable(userId, timestamp);";

    public static final
    String executionScriptTable_index3 = "ALTER TABLE executionScriptTable ALTER COLUMN scriptName SET NOT NULL;";

    public static
    String executionScriptTableFk3 = "ALTER TABLE executionScriptTable ADD CONSTRAINT IF NOT EXISTS backupTableFk1 FOREIGN KEY ( groupId ) REFERENCES groupTable( groupId );";


    private
    static final
    String accessRefTableFk3 = "ALTER TABLE accessRefTable ADD CONSTRAINT IF NOT EXISTS executedQueriesAccessRefTableFk_1 FOREIGN KEY ( objectId ) REFERENCES executionScriptTable( id );";


    private ScriptExecutedRecordList getScriptExecutedRecordList(PreparedStatement preparedStatement) throws SQLException {
        ScriptExecutedRecordList ret = new ScriptExecutedRecordList();

        ResultSet rs = preparedStatement.executeQuery();

        while ( rs.next() ) {
            ScriptExecutedRecord r = new ScriptExecutedRecord(	rs.getLong("id"),
                                                                rs.getLong("scriptId"),
                                                                rs.getString("requestId"),
                                                                rs.getString("scriptName"),
                                                                rs.getString("sourceMachine"),
                                                                rs.getString("destinationMachine"),
                                                                rs.getLong("interpreterId"),
                                                                rs.getLong("groupId"),
                                                                rs.getString("src"),
                                                                rs.getLong("userId"),
                                                                rs.getString("scriptContent"),
                                                                rs.getString("jsonParam"),
                                                                rs.getString("repPath"),
                                                                rs.getString("comment"),
                                                                rs.getLong("timestamp"),
                                                                rs.getInt("cntAccess"),
                                                                rs.getString("isValid")
                                                        );
            ret.addScriptExecutedRecord(r);
        }
        rs.close();

        return ret;
    }


    private ScriptExecutedRecordList getScriptExecutedRecordListWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
        ScriptExecutedRecordList ret = new ScriptExecutedRecordList();

        ResultSet rs = preparedStatement.executeQuery();

        while ( rs.next() ) {
            ScriptExecutedRecord r = new ScriptExecutedRecord(	rs.getLong("id"),
                                                                rs.getLong("scriptId"),
                                                                rs.getString("requestId"),
                                                                rs.getString("scriptName"),
                                                                rs.getString("sourceMachine"),
                                                                rs.getString("destinationMachine"),
                                                                rs.getLong("interpreterId"),
                                                                rs.getLong("groupId"),
                                                                rs.getString("src"),
                                                                rs.getLong("userId"),
                                                                rs.getString("scriptContent"),
                                                                rs.getString("jsonParam"),
                                                                rs.getString("repPath"),
                                                                rs.getString("comment"),
                                                                rs.getLong("timestamp"),
                                                                -1,
                                                                rs.getString("isValid")
            );
            ret.addScriptExecutedRecord(r);
        }
        rs.close();

        return ret;
    }

    private ScriptExecutedRecord getScriptExecutedRecord(PreparedStatement preparedStatement) throws SQLException {
        ScriptExecutedRecord ret = new ScriptExecutedRecord();
        try(ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                ret = new ScriptExecutedRecord(rs.getLong("id"),
                                                rs.getLong("scriptId"),
                                                rs.getString("requestId"),
                                                rs.getString("scriptName"),
                                                rs.getString("sourceMachine"),
                                                rs.getString("destinationMachine"),
                                                rs.getLong("interpreterId"),
                                                rs.getLong("groupId"),
                                                rs.getString("src"),
                                                rs.getLong("userId"),
                                                rs.getString("scriptContent"),
                                                rs.getString("jsonParam"),
                                                rs.getString("repPath"),
                                                rs.getString("comment"),
                                                rs.getLong("timestamp"),
                                                rs.getInt("cntAccess"),
                                                rs.getString("isValid"));
            }
        }
        return ret;
    }

    private ScriptExecutedRecord getScriptExecutedRecordWithoutCnt(PreparedStatement preparedStatement) throws SQLException {
        ScriptExecutedRecord ret = new ScriptExecutedRecord();
        try(ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                ret = new ScriptExecutedRecord(rs.getLong("id"),
                                                rs.getLong("scriptId"),
                                                rs.getString("requestId"),
                                                rs.getString("scriptName"),
                                                rs.getString("sourceMachine"),
                                                rs.getString("destinationMachine"),
                                                rs.getLong("interpreterId"),
                                                rs.getLong("groupId"),
                                                rs.getString("src"),
                                                rs.getLong("userId"),
                                                rs.getString("scriptContent"),
                                                rs.getString("jsonParam"),
                                                rs.getString("repPath"),
                                                rs.getString("comment"),
                                                rs.getLong("timestamp"),
                                                -1,
                                                rs.getString("isValid"));
            }
        }
        return ret;
    }

    private final String selectStr = "SELECT d.id, " +
                                            "d.scriptId, " +
                                            "requestId," +
                                            "d.scriptName, " +
                                            "d.sourceMachine, " +
                                            "d.destinationMachine, " +
                                            "d.interpreterId, " +
                                            "d.groupId, " +
                                            "d.src, " +
                                            "d.userId, " +
                                            "d.scriptContent, " +
                                            "d.jsonParam, " +
                                            "d.repPath, " +
                                            "d.comment, " +
                                            "d.timestamp," +
                                            "d.isValid ";
    private final String cntColumn = ", (SELECT SUM(userId) FROM accessRefTable WHERE userId = ?) AS cntAccess ";
    private final String fromTables = " FROM executionScriptTable d JOIN accessRefTable a ON d.userId = a.userId ";
    private final String fromTable = " FROM executionScriptTable d ";

    public ScriptExecutedRecordList
    getAllExecutedScriptsByUser(final long userId,
                                final long fromDateTime,
                                final long toDateTime) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + cntColumn + fromTables +  " WHERE a.userId = ? AND d.timestamp >= ? AND d.timestamp <= ?" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, fromDateTime);
            preparedStatement.setLong(4, toDateTime);
            return getScriptExecutedRecordList(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public ScriptExecutedRecordList
    getAllExecutedScriptsByUser(final long userId, final long fromDateTime, final long toDateTime, final String statement) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.timestamp >= ? AND d.timestamp <= ? AND d.scriptContent LIKE ? " ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, fromDateTime);
            preparedStatement.setLong(4, toDateTime);
            preparedStatement.setString(5, '%' + statement + '%');
            return getScriptExecutedRecordList(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public ScriptExecutedRecordList
    getAllExecutedScriptsByUser(final long userId, final String statement) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.scriptContent LIKE ? " ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setString(3, '%' + statement + '%');
            return getScriptExecutedRecordList(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public ScriptExecutedRecordList
    getAllExecutedScriptsByUser(final long userId) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? " ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            return getScriptExecutedRecordList(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public ScriptExecutedRecordList
    getUserExecutedScripts(final long userId, final String scriptName, final String src) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + cntColumn + fromTables + " WHERE a.userId = ? AND d.scriptName = ? AND d.src = ? ORDER BY d.timestamp DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setString(3, '%' + scriptName + '%');
            preparedStatement.setString(4, src);
            return getScriptExecutedRecordList(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }



    public ScriptExecutedRecordList
    getScriptByName(final String scriptName) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr +  fromTable + " WHERE d.scriptName = ?" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setString(1, scriptName);
            return getScriptExecutedRecordListWithoutCnt(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    /**
     * Used to uniquely identify a record.
     */
    public ScriptExecutedRecord
    identifyScript(final String requestId, final long timeStamp) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr +  fromTable + " WHERE d.requestId = ? AND d.timestamp = ?" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setString(1, requestId);
            preparedStatement.setLong(2, timeStamp);
            return getScriptExecutedRecordWithoutCnt(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }



    public ScriptExecutedRecord
    getLastUserScript(final long userId, final long groupId, final String src) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + cntColumn + fromTables + " WHERE d.userId = ? AND d.grp = ? AND d.src = ? ORDER BY d.timestamp DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, groupId);
            preparedStatement.setString(4, src);
            return getScriptExecutedRecord(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    /*Function used to get script record based of report path after serializing script result on disk with a random name. */
    public ScriptExecutedRecord
    getScriptByRepPath(final String repPath) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + fromTable + " WHERE d.repPath = ?" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setString(1, repPath);
            return getScriptExecutedRecordWithoutCnt(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public ScriptExecutedRecord
    getScriptById(final long id) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + fromTable + " WHERE d.id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, id);
            return getScriptExecutedRecordWithoutCnt(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public ScriptExecutedRecordList
    getScriptByScriptId(final long scriptId) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + fromTable + " WHERE d.scriptId = ?" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, scriptId);
            return getScriptExecutedRecordListWithoutCnt(preparedStatement);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }



    public List<Long>
    getUserScriptId(final long userId) throws Exception {
        List<Long> ret = new ArrayList<>();
        String select = "SELECT id FROM executionScriptTable WHERE userId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ret.add(rs.getLong("id")) ;
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
        return ret;
    }

    public List<Long>
    getAllGroupsByUser(final long userId) throws Exception {
        List<Long> ret = new ArrayList<>();
        String select = "SELECT groupId FROM executionScriptTable WHERE userId = ? GROUP BY groupId" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ret.add(rs.getLong("groupId")) ;
            }
            rs.close();
            return ret;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public List<Long>
    getAllGroups() throws Exception {
        List<Long> ret = new ArrayList<>();
        String select = "SELECT groupId FROM executionScriptTable GROUP BY groupId" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS))	{
            ResultSet rs = conn.createStatement().executeQuery(select);
            while ( rs.next() ) {
                ret.add(rs.getLong("groupId")) ;
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
        return ret;
    }



    public List<String>
    getAllScriptNamesByUser(final long userId) throws Exception {
        List<String> ret = new ArrayList<>();
        String select = "SELECT scriptName FROM executionScriptTable WHERE userId = ? GROUP BY scriptName" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setLong(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ret.add(rs.getString("scriptName")) ;
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
        return ret;
    }

    public List<String>
    getAllScriptNames() throws Exception {
        List<String> ret = new ArrayList<>();
        String select = "SELECT scriptName FROM executionScriptTable GROUP BY scriptName" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS))	{
            ResultSet rs = conn.createStatement().executeQuery(select);
            while ( rs.next() ) {
                ret.add(rs.getString("scriptName")) ;
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
        return ret;
    }


    public boolean
    isScriptName(final String statementName) throws Exception {
        Class.forName(JDBC_DRIVER);
        String select = selectStr + fromTable + " WHERE d.scriptName = ?" ;
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(select))	{
            preparedStatement.setString(1, "%" + statementName + "%");
            return getScriptExecutedRecordListWithoutCnt(preparedStatement).getScriptExecutedRecordList().size() == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public void
    addScript(final long scriptId,
              final String requestId,
              final String scriptName,
              final String sourceMachine,
              final String destinationMachine,
              final long interpreterId,
              final long groupId,
              final String src,
              final long userId,
              final String scriptContent,
              final String jsonParam,
              final String repPath,
              final String comment,
              final long timestamp,
              final String isValid) throws Exception {

        String addMql = "INSERT INTO executionScriptTable(scriptId, requestId, scriptName, sourceMachine, destinationMachine, interpreterId, groupId, src, userId, scriptContent, jsonParam, repPath, comment, timestamp, isValid) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";



        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(addMql))	{
            preparedStatement.setLong(1, scriptId);
            preparedStatement.setString(2, requestId);
            preparedStatement.setString(3, scriptName);
            preparedStatement.setString(4, sourceMachine);
            preparedStatement.setString(5, destinationMachine);
            preparedStatement.setLong(6, interpreterId);
            preparedStatement.setLong(7, groupId);
            preparedStatement.setString(8, src);
            preparedStatement.setLong(9, userId);
            preparedStatement.setString(10, scriptContent);
            preparedStatement.setString(11, jsonParam);
            preparedStatement.setString(12, repPath);
            preparedStatement.setString(13, comment);
            preparedStatement.setLong(14, timestamp);
            preparedStatement.setString(15, isValid);
            preparedStatement.execute();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public void
    addScript(final ScriptExecutedRecord s) throws Exception {
        addScript(s.getScriptId(),
                    s.getRequestId(),
                    s.getScriptName(),
                    s.getSourceMachine(),
                    s.getDestinationMachine(),
                    s.getInterpreterId(),
                    s.getGroupId(),
                    s.getSource(),
                    s.getUserId(),
                    s.getScriptContent(),
                    s.getJsonParam(),
                    s.getRepPath(),
                    s.getComment(),
                    s.getTimestamp(),
                    s.getIsValid());
    }


    /*Delete execution by owner */
    public void
    deleteScriptByIdAndUser(final long id, final long userId) throws Exception {
        String deleteDslParam = "DELETE executionScriptTable WHERE id = ? and userId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public void
    deleteScriptById(final long id) throws Exception {
        String deleteDslParam = "DELETE executionScriptTable WHERE id = ? ";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public void
    deleteAllScriptExecutionsByUser(final long userId) throws Exception {
        String deleteDslParam = "DELETE executionScriptTable WHERE userId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(deleteDslParam))	{
            preparedStatement.setLong(1, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public void
    addExecutedScript(final ScriptExecutedRecord script) throws Exception {
        addScript(script);
        ScriptExecutedRecord savedQuery = getScriptByRepPath(script.getRepPath());
        ExecutionUserAccess.addArtifactAccess(  savedQuery.getId(),
                                                savedQuery.getUserId(),
                                                PersistencePrivilege.pTypeAdmin,
                                                JDBC_DRIVER,
                                                DB_URL_DISK,
                                                USER, PASS);
    }


    /*Compound access functions*/


    /*Delete executed script access.
    If the access to be removed belongs to owner, the script execution is deleted entirely with all associated access of all third party users
    If the access belongs to a third party user, the access of the third party is removed, but execution and owner access is preserved.
    */
    public ScriptExecutedRecord
    deleteExecutedScriptAccess(final long id, final long userId) throws Exception {
        ScriptExecutedRecord ret = getScriptById(id); /*Get the record before potentially deleting it*/
        long countUsers = ExecutionUserAccess.countArtifactAccess(id, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
        ExecutionUserAccess.deleteArtifactAccess(id, userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
        ret.setFlag(ExecutedStatementFlag.accessRecordDeleted);
        if(countUsers == 1 && ret.getUserId() == userId) {
            deleteScriptByIdAndUser(id, userId);
            ret.setFlag(ExecutedStatementFlag.executionRecordDeleted);
        }
        return ret;
    }


    /**
     *  Deletes all execution and associated access to users to own execution scripts
     */
    public List<ScriptExecutedRecord>
    deleteExecutedScript(final List<Long> executionIdList, final long userId, final boolean force) throws Exception {
        List<ScriptExecutedRecord> ret = new ArrayList<>();
        for (Long id :  executionIdList) {
            ScriptExecutedRecord rec = getScriptById(id); /*Get the record before potentially deleting it*/
            long countUsers = ExecutionUserAccess.countArtifactAccess(id, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
            if(rec.getUserId() == userId) {
                if(countUsers == 1 || force) {
                    deleteScriptByIdAndUser(id, userId);
                    ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
                    rec.setFlag(ExecutedStatementFlag.accessRecordDeleted);
                }
            }
            ret.add(rec);
        }
        return ret;
    }


    /*Blunt operation to clean up database for the owner of multiple executions. The operation deletes ALL executions of the owner */
    public List<ScriptExecutedRecord>
    deleteAllExecutedScriptsByUserId(final long userId) throws Exception {
        List<ScriptExecutedRecord> ret = new ArrayList<>();
        ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
        List<Long> lst = getUserScriptId(userId);
        for (Long id :  lst) {
            ScriptExecutedRecord rec = getScriptById(id);
            if(rec.getUserId() == userId) {
                deleteScriptByIdAndUser(id, userId);
                ExecutionUserAccess.deleteArtifactAccess(userId, JDBC_DRIVER, DB_URL_DISK, USER, PASS);
            }
            ret.add(rec);
        }
        return ret;
    }







    public static void
    testExecutedScriptsDb() {
        try {
            final String pathF = "c:/f.txt";
            final long userId = 1L;
            final long groupId = -1L;
            final String scriptContent = "script";
            ScriptExecutedRepoDb.createDatabase();

            ScriptExecutedRepoDb d = new ScriptExecutedRepoDb();
            ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            PersistenceGroupList pgList = ExecutionGroup.getArtifactGroups("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            PersistenceGroup pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            ExecutionGroup.deleteArtifactGroup(pg.getGroupId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            ExecutionGroup.addArtifactGroup("groupNameTest", "This is test group", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            pg = ExecutionGroup.getArtifactGroup("groupNameTest", d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);

            ScriptExecutedRecord q = new ScriptExecutedRecord(-1L, -1L, "###", "t999", "machine1", "machine2", -1, groupId, "A", userId, scriptContent, "", pathF, "com", -1L, -1, "N");
            d.addScript(q);
            ScriptExecutedRecord qR1 = d.getScriptByRepPath(pathF);
            List<Long> gList1 = d.getAllGroups();
            List<Long> lst4 = d.getAllGroupsByUser(userId);
            ScriptExecutedRecordList l = d.getScriptByScriptId(qR1.getScriptId());
            d.deleteScriptById(qR1.getId());
            ScriptExecutedRecord qR = d.getScriptByRepPath(pathF);
            List<String> lstScriptNames1 =d.getAllScriptNamesByUser(userId);
            List<String> lstScriptNames2 = d.getAllScriptNames();
            ExecutionUserAccess.addArtifactAccess(qR.getId(), userId, PersistencePrivilege.pTypeAdmin, d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            long cnt = ExecutionUserAccess.countArtifactAccess(qR.getId(), d.JDBC_DRIVER, d.DB_URL_DISK, d.USER, d.PASS);
            System.out.println(cnt);
            ScriptExecutedRecordList lst = d.getAllExecutedScriptsByUser(-1L, 0L, DateTimeUtils.millisecondsSinceEpoch());
            System.out.println(lst.toString());
            lst = d.getAllExecutedScriptsByUser(-1L, 0, DateTimeUtils.millisecondsSinceEpoch(), scriptContent);
            System.out.println(lst.toString());
            lst = d.getAllExecutedScriptsByUser(-1L, scriptContent);
            System.out.println(lst.toString());
            ScriptExecutedRecord last = d.getLastUserScript(-1L, groupId, "A");
            System.out.println(last.toString());
            qR = d.getScriptById(qR.getId());
            System.out.println(qR.toString());
            q = d.deleteExecutedScriptAccess(qR.getId(), -1L);
            System.out.println(q.toString());

        } catch(Exception e) {
            AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db);
        }

    }







}
