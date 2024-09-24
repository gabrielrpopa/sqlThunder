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

public class UserDbRecord implements RestInterface{

			
	private	long id;
	private long internalUserId;
	private	String email;
	private int exchangeId;
	private String isAdmin;
	private String userPassword;

	
	public UserDbRecord(final long id, 
						final long internalUserId, 
						final String email,
						final int exchangeId,
						final String isAdmin,
						final String userPassword
						)
	{
		this.setId(id);
		this.setInternalUserId(internalUserId); 
		this.setEmail(email);
		this.setExchangeId(exchangeId);
		this.setIsAdmin(isAdmin);
		this.setUserPassword(userPassword);
				
	}
	
	public UserDbRecord() {
		this.setId(-1);
		this.setInternalUserId(-1); 
		this.setEmail("");
		this.setExchangeId(-1);
		this.setIsAdmin("N");
		this.setUserPassword("");
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public long getInternalUserId() { return internalUserId; }
	public void setInternalUserId(long internalUserId) { this.internalUserId = internalUserId; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public int getExchangeId() { return exchangeId; }
	public void setExchangeId(int exchangeId) { this.exchangeId = exchangeId; }

	public String getIsAdmin() { return isAdmin; }
	public void setIsAdmin(String isAdmin) { this.isAdmin = isAdmin; }
	
	
	public String getUserPassword() { return userPassword; }
	public void setUserPassword(String userPassword) { this.userPassword = userPassword; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
