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

public class ExchangeRecord implements RestInterface{
	
	private long id;
	private String exchangeAddress;
	private String exchangeName;
	private String exchangeUid;

	public String getExchangeAddress() {
		return exchangeAddress;
	}
	public void setExchangeAddress(String exchangeAddress) {
		this.exchangeAddress = exchangeAddress;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public String getExchangeUid() {
		return exchangeUid;
	}
	public void setExchangeUid(String exchangeUid) {
		this.exchangeUid = exchangeUid;
	}
	public long getId() { return id;	}
	public void setId(long id) { this.id = id; }

	public ExchangeRecord(	final long id,
							final String exchangeAddress,
							final String exchangeName,
							final String exchangeUid
							) {
		this.setId(id);
		this.setExchangeAddress(exchangeAddress);
		this.setExchangeName(exchangeName);
		this.setExchangeUid(exchangeUid);
	}
			
	public ExchangeRecord() {
		this.setId(-1);
		this.setExchangeAddress("");
		this.setExchangeName("");
		this.setExchangeUid("");
		
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
