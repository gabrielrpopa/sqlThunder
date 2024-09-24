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
import com.widescope.rest.RestInterface;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@BsonDiscriminator
@Document
public class ChatRecordTest implements RestInterface{

	@BsonId
	@BsonRepresentation(BsonType.OBJECT_ID)
	private long id;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String fromUser;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String toUser;
	@BsonProperty()
	@BsonRepresentation(BsonType.INT64)
	private long timestamp;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String requestId;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String isWithAttachment;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String isDelivered;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String mongoUniqueName;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String mongoDatabase;
	@BsonProperty()
	@BsonRepresentation(BsonType.STRING)
	private String mongoCollection;
	@BsonProperty()
	@BsonRepresentation(BsonType.INT64)
	private long size;
	@BsonProperty()
	@BsonRepresentation(BsonType.OBJECT_ID)
	private ObjectId _id = new ObjectId();

	public ChatRecordTest() {
		this.id = -1;
		this.fromUser = "";;
		this.toUser  = "";
		this.timestamp = -1;
		this.requestId = "";;
		this.isWithAttachment = "";;
		this.isDelivered = "";;
		this.mongoUniqueName = "";;
		this.mongoDatabase = "";;
		this.mongoCollection = "";
		this.size = -1;
		this._id = new ObjectId();
	}





	@Override
	public String toString() {
		return new Gson().toJson(this);
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

	public ObjectId get_id() { return _id; }
	public void set_id(ObjectId _id) { this._id = _id; }
}
