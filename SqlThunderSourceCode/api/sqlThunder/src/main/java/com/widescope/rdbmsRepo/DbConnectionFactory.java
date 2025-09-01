/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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

package com.widescope.rdbmsRepo.database;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.AbstractFactoryBean;



public class DbConnectionFactory extends AbstractFactoryBean<DbConnectionInfo> {
	private DbConnectionInfo dbConnectionInfo;
	public DbConnectionInfo getConnectionDetails()	{ return dbConnectionInfo;	}
	public void setConnectionDetails(final DbConnectionInfo dbConnectionInfo)	{ this.dbConnectionInfo = dbConnectionInfo;}

	@NotNull
	@Override
	protected DbConnectionInfo createInstance() throws Exception {	return dbConnectionInfo; }
	
	@Override
	public Class<DbConnectionInfo> getObjectType() { return DbConnectionInfo.class; }
}
