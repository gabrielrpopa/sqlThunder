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
import com.widescope.sqlThunder.utils.StringUtils;

public class ExchangeFileDbRecord implements RestInterface{
			
	private	long id;
	private	String requestId;
	private long exchangeId;
	private long fromUserId;
	private long toUserId;
	private String fileName;
	private	String fileType;

	private String comment;
	private	String fullFilePath;
	private	String group; /*user defined group. not enforced*/
	private	String source; /*A-ADHOC or R-REPO*/
	private int flag;   /*See ExecutedStatementFlag*/
	private int accessRec;   /*no users having access to it*/
	
	/*
	 * To store this file on exchange, use path constructed this way:
	 * exchangeId/direction/fileUid/fileName
	 * */
	
	public ExchangeFileDbRecord(final long id,
								final String requestId,
								final long exchangeId,
								final long fromUserId,
								final long toUserId,
								final String fileName, 
								final String fileType,
								final String comment,
								final String fullFilePath,
								final String group,
								final String source
								)
	{
		this.setId(id);
		this.setRequestId(requestId);
		this.setExchangeId(exchangeId);
		this.setFromUserId(fromUserId);
		this.setToUserId(toUserId);
		this.setFileName(fileName);
		this.setFileType(fileType);
		this.setComment(comment);
		this.setFullFilePath(fullFilePath);
		this.setGroup(group);
		this.setSource(source);
		this.setFlag(-1);
		this.setAccessRec(-1);
	}
	
	public ExchangeFileDbRecord() {
		this.setId(-1);
		this.setExchangeId(-1);
		this.setRequestId(StringUtils.generateRequestId());
		this.setFromUserId(-1);
		this.setToUserId(-1);
		this.setFileName("");
		this.setFileType("");
		this.setComment("");
		this.setFullFilePath("");
		this.setGroup("");
		this.setSource("A");
		this.setFlag(-1);
		this.setAccessRec(-1);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getRequestId() { return requestId; }
	public void setRequestId(String requestId) { this.requestId = requestId; }

	public long getExchangeId() { return exchangeId; }
	public void setExchangeId(long exchangeId) {	this.exchangeId = exchangeId; }
	
	public long getFromUserId() { return fromUserId; }
	public void setFromUserId(long fromUserId) { this.fromUserId = fromUserId; }
	
	public long getToUserId() { return toUserId; }
	public void setToUserId(long toUserId) { this.toUserId = toUserId; }
	
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getFileType() { return fileType; }
	public void setFileType(String fileType) { this.fileType = fileType; }

	public String getFullFilePath()  { return fullFilePath; }
	public void setFullFilePath(String fullFilePath) { this.fullFilePath = fullFilePath; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }

	public String getGroup() { return group; }
	public void setGroup(String group) { this.group = group; }

	public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }

	public int getFlag() { return flag; }
	public void setFlag(int flag) { this.flag = flag; }

	public int getAccessRec() { return accessRec; }
	public void setAccessRec(int accessRec) { this.accessRec = accessRec; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}



}
