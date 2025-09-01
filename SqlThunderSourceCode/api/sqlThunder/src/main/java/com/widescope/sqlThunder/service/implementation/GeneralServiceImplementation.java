
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

package com.widescope.sqlThunder.service.implementation;


import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.DbUtil;
import org.springframework.stereotype.Service;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.rest.about.DatabaseEnvironment;
import com.widescope.sqlThunder.rest.about.LogContent;
import com.widescope.sqlThunder.service.GeneralService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;





@Service
public class GeneralServiceImplementation implements GeneralService {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	@Autowired
	AppConstants appConstants;
	

	@Override
	public List<DatabaseEnvironment> getAllAvailableDatabases() throws Exception {
		List<DatabaseEnvironment> ret = new ArrayList<DatabaseEnvironment>();
		for(String key : DbUtil.connectionDetailsTable.keySet()) {
			DbConnectionInfo value = DbUtil.connectionDetailsTable.get(key);
			ret.add( new DatabaseEnvironment(value.getDbType(), value.getDbName(), value.getServer(), value.getDescription()) );
		}
		return ret;
	}
	
	@Override
	public LogContent getLogContent(final String stringToSearch)	{
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LogContent logList = new LogContent();
		java.io.BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(appConstants.getLoggingFile()));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if(stringToSearch != null) {
					if(line.contains(stringToSearch))
						logList.addLogContent(line);
				} else {
					logList.addLogContent(line);
				}
				

			}
			reader.close();
		} catch (Exception e) {
			AppLogger.logException(e, className, methodName, AppLogger.obj);
		}
		return logList;
		
	}
	
	
}