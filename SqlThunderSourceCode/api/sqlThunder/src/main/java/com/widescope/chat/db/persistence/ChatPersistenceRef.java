package com.widescope.chat.db.persistence;

import com.widescope.chat.db.ChatDb;
import com.widescope.chat.db.ChatRecord;
import com.widescope.chat.users.ChatUser;
import com.widescope.chat.users.UserToChat;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatPersistenceRef {

    public static final
    String chat_external_user_table_postgres = "CREATE TABLE IF NOT EXISTS chat_external_user (id bigserial PRIMARY KEY, \r\n"
                                                    + "	userId bigint, \r\n"  /*long in H2*/
                                                    + "	firstName character varying(99),\r\n"
                                                    + "	lastName character varying(99) ,\r\n"
                                                    + "	email character varying(99) ,\r\n"
                                                    + "	department BIGINT DEFAULT 1,\r\n"
                                                    + "	title bigint DEFAULT 1,\r\n"
                                                    + "	manager BIGINT DEFAULT 1,\r\n"
                                                    + "	characteristic character varying(9999) DEFAULT '',\r\n"
                                                    + "	description character varying(999) DEFAULT '',\r\n"
                                                    + "	nodeId bigint \r\n"
                                                    + ")";

    public static final
    String chat_external_user_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_chat_external_user_1 ON chat_external_user(email);";

    public static final
    String chat_external_user_index2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_chat_external_user_1 ON chat_external_user(userId, nodeId);";

    public static final
    String chat_user = "CREATE TABLE IF NOT EXISTS chat_user (id bigserial PRIMARY KEY, \r\n"
                                                    + "	fromId bigint, \r\n"
                                                    + "	fromUser character varying(256), \r\n"
                                                    + "	isFromExt character varying(1) DEFAULT 'N', \r\n"
                                                    + "	toId bigint, \r\n"
                                                    + "	toUser character varying(256), \r\n"
                                                    + "	isToExt character varying(1) DEFAULT 'N', \r\n"
                                                    + " CHECK (isFromExt IN ('Y', 'N') ),"
                                                    + " CHECK (isToExt IN ('Y', 'N') )"
                                                    + ")";

    public static final
    String chat_user_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_chat_user_1 ON chat_user(fromUser, toUser);";


    public static final
    String chat = "CREATE TABLE IF NOT EXISTS chat (id bigserial PRIMARY KEY, \r\n"
                                                + "	fromUser character varying(256), \r\n"
                                                + "	toUser character varying(256), \r\n"
                                                + "	timestamp bigint, \r\n"
                                                + "	requestId character varying(256), \r\n"  /*unique generated id */
                                                + "	isDelivered character varying(1), \r\n"  /*Y/N */
                                                + " isWithAttachment character varying(1), \r\n"  /*Y/N */
                                                + "	mongoUniqueName character varying(1), \r\n"
                                                + " CHECK (isDelivered IN ('Y', 'N') ), \r\n"
                                                + " CHECK (isWithAttachment IN ('Y', 'N') )"
                                                + ")";
    public static final
    String chat_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_chatTable_1 ON chat(fromUser, toUser);";
    public static final
    String chat_index2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_chat_1 ON chat(fromUser, toUser, requestId);";

    public static final
    String chat_group = "CREATE TABLE IF NOT EXISTS chat_group (id bigserial PRIMARY KEY, groupName bigint )";

    public static final
    String chat_group_users = "CREATE TABLE IF NOT EXISTS chat_group_users (id bigserial PRIMARY KEY, groupId bigint, userId bigint )";

    public static final
    String chat_group_users_index_1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_chat_group_users_1 ON chat_group_users(groupId, userId);";


    public static void
    createAllTables(DbConnectionInfo dbConn) throws Exception{
        try {
            List<String> lst = new ArrayList<>();
            lst.add(chat_external_user_table_postgres);
            lst.add(chat_external_user_index2);
            lst.add(chat_external_user_index1);

            lst.add(ChatPersistenceRef.chat_user);
            lst.add(chat_user_index1);

            lst.add(chat);
            lst.add(chat_index1);
            lst.add(chat_index2);


            lst.add(chat_group);
            lst.add(chat_group_users);
            lst.add(chat_group_users_index_1);

            ChatPersistenceRef.createTables(dbConn, lst);
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public static void
    createTable(final DbConnectionInfo connectionDetailInfo, final String stm) throws Exception	{
         Class.forName(connectionDetailInfo.getJdbcDriver());
        Statement statement;
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword()) )
      	{
            statement = conn.createStatement();
            statement.executeUpdate(stm);
            statement.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public static void
    createTables(final DbConnectionInfo connectionDetailInfo, final List<String> stm) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        Statement statement;
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword()) ) {
            statement = conn.createStatement();
            for (String s: stm) {
                statement.executeUpdate(s);
            }
            statement.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }



    public static void
    insertUserTable(final DbConnectionInfo connectionDetailInfo,
                    final long fromId,
                    final String fromUser,
                    final long toId,
                    final String toUser) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final String insertUserSqlString = " INSERT INTO chat_user (fromId, fromUser, isFromExt, toId, toUser, isToExt) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
            PreparedStatement preparedStatement = conn.prepareStatement(insertUserSqlString)) {
            preparedStatement.setLong(1, fromId );
            preparedStatement.setString(2, fromUser );
            preparedStatement.setString(3, "N" );
            preparedStatement.setLong(4, toId );
            preparedStatement.setString(5, toUser );
            preparedStatement.setString(6, "N" );

            preparedStatement.executeUpdate();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public static boolean
    deleteUser(final DbConnectionInfo connectionDetailInfo,
               final long fromId,
               final long toId) throws Exception	{
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final String deleteUserSqlString = "DELETE user WHERE fromUser = ? AND toUser = ?";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(deleteUserSqlString)) {
            preparedStatement.setLong(1, fromId);
            preparedStatement.setLong(1, toId);
            int row = preparedStatement.executeUpdate();
            return row == 1;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public static List<ChatUser>
    getChatUsersTo(final DbConnectionInfo connectionDetailInfo,
                   final String fromUser) throws Exception {

        final String getUsersToSqlString = "SELECT fromId, fromUser, isFromExt, toId, toUser, isToExt FROM chat_user WHERE fromUser = ? ";
        Class.forName(connectionDetailInfo.getJdbcDriver());
        List<ChatUser> ret = new ArrayList<>();


        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getUsersToSqlString)) {
            preparedStatement.setString(1, fromUser);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                long fromId = rs.getLong("fromId");
                String isfromExt = rs.getString("isfromExt");

                long toId = rs.getLong("toId");
                String toUser = rs.getString("toUser");
                String isToExt = rs.getString("isToExt");

                ChatUser cu = new ChatUser(fromId, fromUser, isfromExt, toId, toUser, isToExt);
                ret.add(cu);
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }



        return ret;
    }


    public static List<ChatUser>
    getUsersMinusUser(final DbConnectionInfo connectionDetailInfo,
                      final String likeUser,
                      final String fromUser
                      ) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String sqlString = "SELECT id, fromId, fromUser, isFromExt, toId, toUser, isToExt FROM chat_user WHERE fromUser like ? ";
        List<ChatUser> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, '%' + likeUser.toLowerCase() + '%');

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ChatUser b = new ChatUser(  rs.getLong("fromId"),
                                            rs.getString("fromUser"),
                                            rs.getString("isFromExt"),
                                            rs.getLong("toId"),
                                            rs.getString("toUser"),
                                            rs.getString("isToExt")
                                 );

                if(!fromUser.equalsIgnoreCase(b.getFromUser()))
                    ret.add(b);
            }
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }

        return ret;
    }





    public static ChatUser
    getChatUserTo(final DbConnectionInfo connectionDetailInfo, final String userName) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        return null;
    }


    public static List<UserToChat>
    getUserToChat(final DbConnectionInfo connectionDetailInfo, final String userName) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        return new ArrayList<>();
    }


    public static ChatRecord
    getMessage(  final DbConnectionInfo connectionDetailInfo,
                 final String fromUser,
                 final String toUser,
                 final String requestId ) throws Exception {
        final String getMessagesSqlString = "SELECT id,	fromUser, toUser, timestamp, requestId, isDelivered, isWithAttachment, mongoUniqueName, mongoDatabase, mongoCollection, size  FROM chat WHERE fromUser = ? AND toUser = ? AND requestId=?";

        Class.forName(connectionDetailInfo.getJdbcDriver());
        ChatRecord ret = null;
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getMessagesSqlString)) {

            preparedStatement.setString(1, fromUser);
            preparedStatement.setString(2, toUser);
            preparedStatement.setString(3, requestId);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ret = new ChatRecord(	rs.getInt("id"),
                                        rs.getString("fromUser"),
                                        rs.getString("toUser"),
                                        rs.getLong("timestamp"),
                                        rs.getString("requestId"),
                                        rs.getString("isDelivered"),
                                        rs.getString("isWithAttachment"),
                                        rs.getString("mongoUniqueName"),
                                        rs.getString("mongoDatabase"),
                                        rs.getString("mongoCollection"),
                                        rs.getLong("size")
                );
            }
            rs.close();
            return ret;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public static ChatRecord
    setReadMessage(  final DbConnectionInfo connectionDetailInfo,
                     final String fromUser,
                     final String toUser,
                     final String requestId ) throws Exception {
        final String getMessagesSqlString = "UPDATE chat SET isDelivered = ? WHERE fromUser = ? AND toUser = ? AND requestId=?";

        Class.forName(connectionDetailInfo.getJdbcDriver());
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getMessagesSqlString)) {

            preparedStatement.setString(1, "Y");
            preparedStatement.setString(2, fromUser);
            preparedStatement.setString(3, toUser);
            preparedStatement.setString(4, requestId);
            preparedStatement.executeUpdate();
            int row = preparedStatement.executeUpdate();
            if(row == 1) {
                return new ChatRecord(fromUser, toUser, requestId, "Y");
            } else {
                return new ChatRecord(fromUser, toUser, requestId, "N");
            }
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public static ChatRecord
    deleteMessage(  final DbConnectionInfo connectionDetailInfo,
                     final String fromUser,
                     final String toUser,
                     final String requestId ) throws Exception {

        final String getMessagesSqlString = "DELETE chat WHERE fromUser = ? AND toUser = ? AND requestId = ?";

        Class.forName(connectionDetailInfo.getJdbcDriver());
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getMessagesSqlString)) {

            preparedStatement.setString(1, fromUser);
            preparedStatement.setString(2, toUser);
            preparedStatement.setString(3, requestId);
            preparedStatement.executeUpdate();
            int row = preparedStatement.executeUpdate();
            if(row == 1) {
                return new ChatRecord(fromUser, toUser, requestId, "Y");
            } else {
                return new ChatRecord(fromUser, toUser, requestId, "N");
            }
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public static List<ChatRecord>
    getMessages(final DbConnectionInfo connectionDetailInfo,final String fromUser, final String toUser,  final String isDelivered) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final String getMessagesSqlString = "SELECT id,	fromUser, toUser, timestamp, requestId, isDelivered, isWithAttachment, mongoUniqueName, mongoDatabase, mongoCollection, size FROM chat WHERE fromUser = ? AND toUser = ? AND isDelivered = ?";

        List<ChatRecord> ret = new ArrayList<ChatRecord>();
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getMessagesSqlString)) {
            preparedStatement.setString(1, fromUser);
            preparedStatement.setString(2, toUser);
            preparedStatement.setString(3, isDelivered);

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ChatRecord chat = new ChatRecord(   rs.getInt("id"),
                                                    rs.getString("fromUser"),
                                                    rs.getString("toUser"),
                                                    rs.getLong("timestamp"),
                                                    rs.getString("requestId"),
                                                    rs.getString("isDelivered"),
                                                    rs.getString("isWithAttachment"),
                                                    rs.getString("mongoUniqueName"),
                                                    rs.getString("mongoDatabase"),
                                                    rs.getString("mongoCollection"),
                                                    rs.getLong("size")
                                                );
                ret.add(chat);
            }
            rs.close();
            return ret;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }



    public static List<UserPairValue>
    getUsersWithOutstandingMessages(final DbConnectionInfo connectionDetailInfo, final String toUser,  final String isDelivered) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final String getMessagesSqlString = "SELECT fromUser FROM chat WHERE  toUser = ? AND isDelivered = ? group by fromUser";

        List<UserPairValue> ret = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getMessagesSqlString)) {

            preparedStatement.setString(1, toUser);
            preparedStatement.setString(2, isDelivered);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ret.add(new  UserPairValue( -1, rs.getString("fromUser")) );
            }
            rs.close();
            return ret;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }

    public static List<ChatRecord>
    getMessageHist(final DbConnectionInfo connectionDetailInfo,final String fromUser, final String toUser,  final long fromDate, final long toDate) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final String getMessagesSqlString = "SELECT id,	fromUser, toUser, timestamp, requestId, isDelivered, isWithAttachment, mongoUniqueName, mongoDatabase, mongoCollection, size FROM chat WHERE fromUser = ? AND toUser = ? AND timestamp > ? AND timestamp < ?";

        List<ChatRecord> ret = new ArrayList<ChatRecord>();
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(getMessagesSqlString)) {
            preparedStatement.setString(1, fromUser);
            preparedStatement.setString(2, toUser);
            preparedStatement.setLong(3, fromDate);
            preparedStatement.setLong(4, toDate);

            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
                ChatRecord chat = new ChatRecord(   rs.getInt("id"),
                                                    rs.getString("fromUser"),
                                                    rs.getString("toUser"),
                                                    rs.getLong("timestamp"),
                                                    rs.getString("requestId"),
                                                    rs.getString("isDelivered"),
                                                    rs.getString("isWithAttachment"),
                                                    rs.getString("mongoUniqueName"),
                                                    rs.getString("mongoDatabase"),
                                                    rs.getString("mongoCollection"),
                                                    rs.getLong("size")
                );
                ret.add(chat);
            }
            rs.close();
            return ret;
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public static long
    isUnreadMessages(final DbConnectionInfo connectionDetailInfo, final String toUser, final String isDelivered ) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        String isUnreadMessagesSqlString = "SELECT COUNT(1) AS count FROM chat WHERE toUser = ? AND isDelivered = ?";
        long ret = 0;
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(isUnreadMessagesSqlString))	{
            preparedStatement.setString(1, toUser);
            preparedStatement.setString(1, isDelivered);
            ResultSet rs = preparedStatement.executeQuery();
            ret = rs.getInt("count");
            rs.close();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
        return ret;
    }


    public static void
    mergeChat(final DbConnectionInfo connectionDetailInfo, final ChatRecord chatRecord)	throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final String mergeChatMessageSqlString = " INSERT INTO chat (fromUser, toUser, timestamp, requestId, isDelivered, isWithAttachment, mongoUniqueName, mongoDatabase, mongoCollection, size)   VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(mergeChatMessageSqlString)) {

            preparedStatement.setString(1, chatRecord.getFromUser());
            preparedStatement.setString(2, chatRecord.getToUser());
            preparedStatement.setLong(3, chatRecord.getTimeStamp());
            preparedStatement.setString(4, chatRecord.getIsDelivered());
            preparedStatement.setString(5, chatRecord.getRequestId());
            preparedStatement.setString(6, chatRecord.getIsWithAttachment());
            preparedStatement.setString(7, chatRecord.getMongoUniqueName());
            preparedStatement.setString(8, chatRecord.getMongoDatabase());
            preparedStatement.setString(9, chatRecord.getMongoCollection());
            preparedStatement.setLong(10, chatRecord.getSize());
            preparedStatement.execute();

        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


    public static void
    deleteChat(final DbConnectionInfo connectionDetailInfo, final ChatRecord chatRecord) throws Exception {
        Class.forName(connectionDetailInfo.getJdbcDriver());
        final  String deleteChatSqlString = "DELETE FROM chat WHERE fromUser = ? AND toUser = ? AND timestamp = ?";
        try (Connection conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
             PreparedStatement preparedStatement = conn.prepareStatement(deleteChatSqlString)) {
            preparedStatement.setString(1, chatRecord.getFromUser());
            preparedStatement.setString(2, chatRecord.getToUser());
            preparedStatement.setLong(3, chatRecord.getTimeStamp());
            preparedStatement.execute();
        } catch (SQLException e)	{
            throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
        } catch (Exception e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
        }
    }


}
