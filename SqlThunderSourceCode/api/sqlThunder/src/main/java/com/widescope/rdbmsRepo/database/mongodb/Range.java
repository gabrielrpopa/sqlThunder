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

public class Range {
	private Object from_;
	private Object to_;

	public Range(final Object from, final Object to) {
		this.setFrom_(from);
		this.setTo_(to);
	}

	public Object getFrom_() {
		return from_;
	}

	public void setFrom_(Object from_) {
		this.from_ = from_;
	}

	public Object getTo_() {
		return to_;
	}

	public void setTo_(Object to_) {
		this.to_ = to_;
	}
	
	public static Range getRangeQuery(final String name) {
		return new Range("@" + name + "_from@", "@" + name + "to@" ) ;
	}
}
