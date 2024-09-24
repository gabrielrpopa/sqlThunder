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

public class ExchangeFileDbRecord implements RestInterface{
			
	private	int id;
	private int exchangeId;
	private long fromUserId;
	private long toUserId;
	private String fileName;
	private	String fileType;

	
	/*
	 * To store this file on exchange, use path constructed this way:
	 * exchangeId/direction/fileUid/fileName
	 * */
	
	public ExchangeFileDbRecord(final int id, 
								final int exchangeId,
								final long fromUserId,
								final long toUserId,
								final String fileName, 
								final String fileType
								)
	{
		this.setId(id);
		this.setExchangeId(exchangeId);
		this.setFromUserId(fromUserId);
		this.setToUserId(toUserId);
		this.setFileName(fileName);
		this.setFileType(fileType);
	
	}
	
	public ExchangeFileDbRecord() {
		this.setId(-1);
		this.setExchangeId(-1);
		this.setFromUserId(-1);
		this.setToUserId(-1);
		this.setFileName("");
		this.setFileType("");
		
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public int getExchangeId() { return exchangeId; }
	public void setExchangeId(int exchangeId) {	this.exchangeId = exchangeId; }
	
	public long getFromUserId() { return fromUserId; }
	public void setFromUserId(long fromUserId) { this.fromUserId = fromUserId; }
	
	public long getToUserId() { return toUserId; }
	public void setToUserId(long toUserId) { this.toUserId = toUserId; }
	
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getFileType() { return fileType; }
	public void setFileType(String fileType) { this.fileType = fileType; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
