
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



package com.widescope.rdbmsRepo.database.SqlRepository.Objects;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class RecordsAffected implements RestInterface {

	private String operation;
	private long recAffected;
	private long recFailed;
	private String message;
	
	public RecordsAffected(	final String operation,
							final long recAffected,
							final long recFailed) {
		this.setOperation(operation);
		this.setRecAffected(recAffected);
		this.setRecFailed(recFailed);
	}
	
	public RecordsAffected() {
		this.setOperation("NONE");
		this.setRecAffected(0);
		this.setRecFailed(0);
	}

	public String getOperation() { return operation; }
	public void setOperation(String operation) { this.operation = operation; }

	public long getRecAffected() { return recAffected; }
	public void setRecAffected(long recAffected) { this.recAffected = recAffected; }
	public void incrementRecAffected() { this.recAffected++ ; }
	public void addRecAffected(long recAffected) { this.recAffected += recAffected; }

	public long getRecFailed() { return recFailed; }
	public void setRecFailed(long recFailed) { this.recFailed = recFailed; }
	public void incrementRecFailed() { this.recFailed++; }
	public void addRecFailed(long recFailed) { this.recFailed += recFailed; }
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
