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

package com.widescope.storage.dataExchangeRepo;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class UserToExchangeDbRecord implements RestInterface{
			
	private	long id;
	private long userId;
	private int exchangeId;
	private String isAdmin;
	
	public UserToExchangeDbRecord(	final long id, 
									final long userId, 
									final int exchangeId,
									final String isAdmin
								)
	{
		this.setId(id);
		this.setUserId(userId); 
		this.setExchangeId(exchangeId);
		this.setIsAdmin(isAdmin);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	
	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public int getExchangeId() { return exchangeId; }
	public void setExchangeId(int exchangeId) { this.exchangeId = exchangeId; }

	public String getIsAdmin() { return isAdmin; }
	public void setIsAdmin(String isAdmin) { this.isAdmin = isAdmin; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
