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

package com.widescope.chat.db;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widescope.rest.RestInterface;

public class ChatRecord implements RestInterface{

	private long id;
	private String fromUser;
	private String toUser;
	private long timestamp; /*millisecondsEpoch in ChatMessage*/
	private String requestId;  /*messageId in ChatMessage*/
	private String isWithAttachment;
	private String isDelivered;
	private String mongoUniqueName;
	private String mongoDatabase;
	private String mongoCollection;
	private long size;

	public ChatRecord(final int id,
                      final String fromUser,
                      final String toUser,
                      final long timestamp,
					  final String requestId,
					  final String isDelivered,
					  final String isWithAttachment,
					  final String mongoUniqueName,
					  final String mongoDatabase,
					  final String mongoCollection,
					  final long size
	) {
		this.setId(id);
		this.setFromUser(fromUser);
		this.setToUser(toUser);
		this.setTimeStamp(timestamp);
		this.setRequestId(requestId);
		this.setIsDelivered(isDelivered);
		this.setIsWithAttachment(isWithAttachment);
		this.setMongoUniqueName(mongoUniqueName);
		this.setMongoDatabase(mongoDatabase);
		this.setMongoCollection(mongoCollection);
		this.setSize(size);
	}


	public ChatRecord(final String fromUser,
					  final String toUser,
					  final String requestId,
					  final String isDelivered) {
		this.setId(-1);
		this.setFromUser(fromUser);
		this.setToUser(toUser);
		this.setTimeStamp(-1);
		this.setRequestId(requestId);
		this.setIsDelivered(isDelivered);
		this.setIsWithAttachment("?");
		this.setMongoUniqueName("");
		this.setMongoDatabase("");
		this.setMongoCollection("");
		this.setSize(-1);
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String toStringPretty() {
		try	{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}


	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public long getTimeStamp() {
		return timestamp;
	}
	public void setTimeStamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getId() { return id;	}
	public void setId(int id) { this.id = id; }

	public String getRequestId() { return this.requestId; }
	public void setRequestId(String requestId) { this.requestId = requestId; }

	public String getIsDelivered() { return this.isDelivered; }
	public void setIsDelivered(String isDelivered) { this.isDelivered = isDelivered; }

	public String getMongoUniqueName() { return mongoUniqueName; }
	public void setMongoUniqueName(String mongoUniqueName) { this.mongoUniqueName = mongoUniqueName; }

	public String getIsWithAttachment() { return isWithAttachment; }
	public void setIsWithAttachment(String isWithAttachment) { this.isWithAttachment = isWithAttachment; }

	public String getMongoDatabase() {	return mongoDatabase; }
	public void setMongoDatabase(String mongoDatabase) { this.mongoDatabase = mongoDatabase; }
	public String getMongoCollection() { return mongoCollection; }
	public void setMongoCollection(String mongoCollection) { this.mongoCollection = mongoCollection; }
	public long getSize() { return size; }
	public void setSize(long size) { this.size = size; }
}
