/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.widescope.rdbmsRepo.database.embeddedDb.embedded;



import com.widescope.logging.AppLogger;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlQueryExecUtils;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.utils.SqlParser;
import com.widescope.sqlThunder.utils.security.HashWrapper;
import com.widescope.sqlThunder.utils.user.User;
import org.springframework.stereotype.Component;
import java.sql.*;

@Component
public class H2Test implements  EmbeddedInterface {

	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "org.h2.Driver";
	private String DB_URL_DISK = "jdbc:h2:file:@path@;MODE=PostgreSQL";

	//  Database credentials
	String USER = "sa";
	String PASS = "sa";

	public H2Test() {

	}

	public H2Test(final String path) {
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@path@", path);
	}
	
	public H2Test(final String path, final String user, final String password) {
		this.DB_URL_DISK = this.DB_URL_DISK.replace("@path@", path);
		this.USER = user;
		this.PASS = password;
	}

	public String getUserName() { return this.USER; }
	public String getUserPassword() { return this.PASS; }
	public void setUserName(final String user) { this.USER = user; }
	public void setUserPassword(final String password) { this.PASS = password; }


	@Override
	public Connection getConnection() throws Exception {
		Class.forName(JDBC_DRIVER);
		return DriverManager.getConnection(DB_URL_DISK, USER, PASS);
	}

	
	@Override
	public String getDbType() {
		return "H2";
	}
	
	
	
	@Override
	public ResultQuery execStaticQueryWithResultSet(String staticQuery) throws Exception {
		if(!SqlParser.isSqlDQL(staticQuery)) {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Not a Query"));
		}
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultQuery ret;
		long hash = HashWrapper.hash64FNV(staticQuery);
		
		try	{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(staticQuery);
			ret = SqlQueryExecUtils.buildUpJsonFromResultSet(rs, SqlQueryExecUtils.buildUpMetadataFromResultSet(rs));
			ret.setOutputPackaging("plain");
			ret.setOutputFormat("json");
			ret.setSqlHash(hash);
			
			int rows = 0 ;
			rs.beforeFirst();
			if (rs.last()) {
				rows = rs.getRow();
			}	
			ret.setRecordsAffected(rows);

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally {
			DbUtil.closeHandles(conn, statement,rs);
		}
		return ret;
	}


	@Override
	public TableFormatMap execStaticQueryWithTableFormat(String staticQuery) throws Exception {
		if(!SqlParser.isSqlDQL(staticQuery)) {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "Not a Query"));
		}
		TableFormatMap ret;
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try	{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(staticQuery);
			ret = SqlQueryExecUtils.buildUpJsonFromMigrationReturn(rs, SqlQueryExecUtils.buildUpMetadataWithReturnTableFormat(rs));
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeHandles(conn, statement,rs);
		}

		return ret;
	}


	@Override
	public boolean copyEmbeddedFileBasedDb(final EmbeddedInterface db) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public ResultQuery
	execStaticQueryToWebsocket(final User u,
							   final String methodName,
							   final String requestId,
							   final String jobId,
							   final String staticQuery,
							   final String sqlName,
							   final String httpSession,
							   final String persist,
							   final String comment,
							   final long groupId,
							   final PersistenceWrap pWrap)  {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public boolean bulkInsert(ResultQuery staticQuery, String sqlName)  {
		// TODO Auto-generated method stub
		return false;
	}

}

