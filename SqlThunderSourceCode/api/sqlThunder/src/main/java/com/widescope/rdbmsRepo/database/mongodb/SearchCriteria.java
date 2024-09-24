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

public class SearchCriteria {

	private String type;
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

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public Map<String, Range> getRange() { return range; }
	public void setRange(Map<String, Range> range) { this.range = range; }

	public Map<String, Object> getEqual() { return equal; }
	public void setEqual(Map<String, Object> equal) { this.equal = equal; }

	public Map<String, Object> getLessThan() { return lessThan; }
	public void setLessThan(Map<String, Object> lessThan) { this.lessThan = lessThan; }

	public Map<String, Object> getGreaterThan() { return greaterThan; }
	public void setGreaterThan(Map<String, Object> greaterThan) { this.greaterThan = greaterThan; }

	public Map<String, Object> getLike() {
		return like;
	}
	public void setLike(Map<String, Object> like) {
		this.like = like;
	}

	public Map<String, List<Object>> getIn() {
		return in;
	}
	public void setIn(Map<String, List<Object>> in) {
		this.in = in;
	}

	public Map<String, List<Object>> getNotIn() {
		return notIn;
	}
	public void setNotIn(Map<String, List<Object>> notIn) {
		this.notIn = notIn;
	}

	public Map<String, Integer> getSort() {
		return sort;
	}
	public void setSort(Map<String, Integer> sort) {
		this.sort = sort;
	}

	public int getFromRow() {
		return fromRow;
	}
	public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}

	public int getNoRow() {
		return noRow;
	}
	public void setNoRow(int noRow) {
		this.noRow = noRow;
	}

}
