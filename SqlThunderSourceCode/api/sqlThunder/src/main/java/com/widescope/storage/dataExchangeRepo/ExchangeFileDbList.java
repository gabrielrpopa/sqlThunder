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

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;

public class ExchangeFileDbList implements RestInterface{
	
	private List<ExchangeFileDbRecord> exchangeCompanyDbRecordLst;
	
	public ExchangeFileDbList(List<ExchangeFileDbRecord> exchangeCompanyDbRecordLst) {
		this.setExchangeCompanyDbRecordLst(exchangeCompanyDbRecordLst);
	}
			
	public ExchangeFileDbList() {
		this.setExchangeCompanyDbRecordLst(new ArrayList<ExchangeFileDbRecord>());
	}
	public List<ExchangeFileDbRecord> getExchangeCompanyDbRecordLst() 
	{ return exchangeCompanyDbRecordLst; }
	
	public void setExchangeCompanyDbRecordLst(List<ExchangeFileDbRecord> exchangeCompanyDbRecordLst) 
	{ this.exchangeCompanyDbRecordLst = exchangeCompanyDbRecordLst; }
	
	public void addExchangeCompanyDbRecord(ExchangeFileDbRecord exchangeCompanyDbRecord) 
	{ this.exchangeCompanyDbRecordLst.add(exchangeCompanyDbRecord); }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ExchangeFileDbList toExchangeFileDbList(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ExchangeFileDbList.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
}
