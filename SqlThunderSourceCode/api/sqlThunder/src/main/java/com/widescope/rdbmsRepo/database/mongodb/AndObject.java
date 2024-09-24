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

import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClient;

public class AndObject {

	private MongoClient mongoClient; 
	private String dbName; 
	private String collectionName;
	private Map<String, Range> range;
	private Map<String, Object> equal;
	private Map<String, Object> lessThan;
	private Map<String, Object> greaterThan;
	private Map<String, Object> like;
	private Map<String, List<Object>> in;
	private Map<String, List<Object>> notIn;
	private Map<String, Integer> sort;
	private int fromRow;
	private int noRow;


	public MongoClient getMongoClient() { return mongoClient; }
	public void setMongoClient(final MongoClient mongoClient) {	this.mongoClient = mongoClient; }


	public String getDbName() { return dbName; }
	public void setDbName(final String dbName) { this.dbName = dbName; }

	public String getCollectionName() { return collectionName; }
	public void setCollectionName(final String collectionName) { this.collectionName = collectionName; }

	public Map<String, Range> getRange() { return range; }
	public void setRange(final Map<String, Range> range) { this.range = range; }

	public Map<String, Object> getEqual() { return equal; }
	public void setEqual(final Map<String, Object> equal) { this.equal = equal; }

	public Map<String, Object> getLessThan() { return lessThan; }
	public void setLessThan(final Map<String, Object> lessThan) { this.lessThan = lessThan; }

	public Map<String, Object> getLike() { return like;	}
	public void setLike(final Map<String, Object> like) {	this.like = like; }

	public Map<String, List<Object>> getIn() { return in; }
	public void setIn(final Map<String, List<Object>> in) { this.in = in;	}

	public Map<String, Object> getGreaterThan() { return greaterThan; }
	public void setGreaterThan(final Map<String, Object> greaterThan) { this.greaterThan = greaterThan; }

	public Map<String, Integer> getSort() {	return sort; }
	public void setSort(final Map<String, Integer> sort) { this.sort = sort; }

	public Map<String, List<Object>> getNotIn() { return notIn; }
	public void setNotIn(final Map<String, List<Object>> notIn) { this.notIn = notIn;	}

	public int getFromRow() { return fromRow; }
	public void setFromRow(final int fromRow) { this.fromRow = fromRow; }


	public int getNoRow() { return noRow; }
	public void setNoRow(final int noRow) { this.noRow = noRow; }
	
}
