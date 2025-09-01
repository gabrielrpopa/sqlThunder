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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.widescope.logging.AppLogger;
import com.widescope.persistence.PersistenceWrap;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.sqlThunder.utils.user.User;


public interface EmbeddedInterface {

	public Connection getConnection() throws Exception;
    public String getDbType();
    public ResultQuery execStaticQueryWithResultSet(final String staticQuery) throws Exception;
    public TableFormatMap execStaticQueryWithTableFormat(final String staticQuery) throws Exception;



    ResultQuery execStaticQueryToWebsocket(User u,
                                           String methodName,
                                           String requestId,
                                           String jobId,
                                           String staticQuery,
                                           String sqlName,
                                           String httpSession,
                                           String persist,
                                           String comment,
                                           long groupId,
                                           PersistenceWrap pWrap
    ) throws Exception;

    boolean bulkInsert(final ResultQuery staticQuery,
                       final String sqlName) throws Exception;
    

    
   
	 
    default 
	List<String>
	getUserTables(Connection conn) throws Exception {
		List<String> ret = new ArrayList<>();
		String sqlString = "SELECT tName FROM __userTable__";

		try (PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				ret.add(rs.getString("tName"));
			}
			rs.close();
			return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
    

	public boolean copyEmbeddedFileBasedDb(EmbeddedInterface db) throws Exception;

}
