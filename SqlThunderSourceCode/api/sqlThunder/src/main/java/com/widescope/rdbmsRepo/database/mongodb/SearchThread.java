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



import java.util.concurrent.Callable;

import com.widescope.logging.AppLogger;

public class SearchThread<T> implements Callable< MongoResultSet >  {

	private final MongoDbConnection mongoDbConnection;
	private final String dbName;
	private final String collectionName;
	private final ComplexAndSearch complexAndSearch ;
	 
    public SearchThread(final MongoDbConnection mongoDbConnection,
    					final String dbName,
    					final String collectionName,
    					final ComplexAndSearch complexAndSearch) {
    	this.mongoDbConnection = mongoDbConnection;
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.complexAndSearch = complexAndSearch;
    }
 
    @Override
    public MongoResultSet call() {
    	try {
	   		return MongoGet.searchDocumentComplexAnd(mongoDbConnection,
		    										dbName, 
		    										collectionName,
		    										complexAndSearch
		    										);

    	} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new MongoResultSet();
    	}
    	
    }
    
}
