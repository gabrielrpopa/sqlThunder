
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
package com.widescope.rdbmsRepo.database.mongodb;

import org.bson.Document;
import org.bson.conversions.Bson;


import com.mongodb.client.MongoDatabase;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;

public class MongoGeneric {

	public static Document execBson(final String clusterName,
									final String dbName,
									final Bson command
									) throws Exception {
		MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(clusterName);
		MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecordSource);
		
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
        return database.runCommand(command);
	}
	
	
	public static Document execBson(final String clusterName,
									final String dbName,
									final String commandStr
									) throws Exception {
		MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(clusterName);
		if(mongoClusterRecordSource == null) {
			throw new Exception("Incorrect cluster Name");
		}
		
		MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecordSource);
		
		final Document doc = Document.parse(commandStr);
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
        return database.runCommand(doc);
	}

}
