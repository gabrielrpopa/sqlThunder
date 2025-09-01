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
import com.widescope.sqlThunder.rest.RestInterface;

public class UserToExchangeDbRecordExtended implements RestInterface{

			
	private	long id;
	private long userId;
	private long internalUserId;
	private String userEmail; 
	private int exchangeId;
	private String exchangeUid;
	private String exchangeName;

	public UserToExchangeDbRecordExtended(	final long id, 
											final long userId, 
											final long internalUserId,
											final String userEmail,
											final int exchangeId,
											final String exchangeUid,
											final String exchangeName
											)
	{
		this.setId(id);
		this.setUserId(userId); 
		this.setInternalUserId(internalUserId);
		this.setUserEmail(userEmail);
		this.setExchangeId(exchangeId);
		this.setExchangeUid(exchangeUid);
		this.setExchangeName(exchangeName);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	
	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	
	public long getInternalUserId() { return internalUserId; }
	public void setInternalUserId(long internalUserId) { this.internalUserId = internalUserId; }

	public int getExchangeId() { return exchangeId; }
	public void setExchangeId(int exchangeId) { this.exchangeId = exchangeId; }

	public String getUserEmail() {	return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

	public String getExchangeUid() { return exchangeUid; }
	public void setExchangeUid(String exchangeUid) { this.exchangeUid = exchangeUid; }

	public String getExchangeName() { return exchangeName; }
	public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
