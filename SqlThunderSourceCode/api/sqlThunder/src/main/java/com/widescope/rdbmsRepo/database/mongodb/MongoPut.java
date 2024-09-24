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


package com.widescope.rdbmsRepo.database.mongodb;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mongodb.*;
import com.mongodb.client.model.*;
import com.widescope.chat.db.ChatMessage;
import com.widescope.logging.AppLogger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.HitsInner;
import com.widescope.rdbmsRepo.database.elasticsearch.objects.elasticPayload.dsl.ElasticPayload;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterActiveRecord;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterMaxRecord;


public class MongoPut {

    /** The options to use for inserting a single document. */
    private static final UpdateOptions UPDATE_WITH_UPSERT = new UpdateOptions().upsert(true);

	public static void createCollection(final MongoDbConnection mongoDbConnection, 
										final String dbName, 
										final String collectionName) throws Exception	{
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			database.createCollection(collectionName);
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	public static void dropCollection(final MongoDbConnection mongoDbConnection, 
										final String dbName, 
										final String collectionName) throws Exception	{
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			database.getCollection(collectionName).drop();
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	

	public static boolean
	addDocument(final MongoDbConnection mongoDbConnection,
				final String dbName,
				final String collectionName,
				final String jsonDocument) throws Exception	{
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			final Document doc = Document.parse(jsonDocument);
			database.getCollection(collectionName).insertOne(doc);
			return true;
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return false;
		}
	}

	public static boolean
	addDocument_(final MongoDbConnection mongoDbConnection,
				final String dbName,
				final String collectionName,
				final String jsonDocument) throws Exception	{
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			final Document doc = Document.parse(jsonDocument);
			database.getCollection(collectionName).insertOne(doc);
			return true;
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return false;
		}
	}

	public static String
	addChatMessage(	final MongoDbConnection mongoDbConnection,
					final String dbName,
					final String collectionName,
					final ChatMessage c,
					final String requestId) throws Exception	{
		try	{

			CodecProvider pojoCodecProvider = PojoCodecProvider	.builder()
																.register("com.widescope.chat.fileService")
																.register("com.widescope.chat.db")
																.build();
			CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);;
			MongoCollection<ChatMessage> collection = database.getCollection(collectionName, ChatMessage.class).withCodecRegistry(pojoCodecRegistry);
			c.setMessageId(requestId);
			collection.insertOne(c);
			return "Y";
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return "N";
		}
	}


	public static ChatMessage setReadChatMessageById( final MongoDbConnection mongoDbConnection,
													  final String dbName,
													  final String collectionName,
													  final String id) {
		try	{
			CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
																.register("com.widescope.chat.fileService")
																.register("com.widescope.chat.db")
																.build();
			CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);;
			MongoCollection<ChatMessage> collection = database.getCollection(collectionName, ChatMessage.class).withCodecRegistry(pojoCodecRegistry);

			ChatMessage m = collection.find(eq("_id", id)).first();
			Document filterById = new Document("_id", id);
			FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
			if(m!=null) {
				m = collection.findOneAndReplace(filterById, m, returnDocAfterReplace);
			}
			return m;

		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return null;
		}
	}


	
	
	
	public static void bulkInsert(	final MongoDbConnection mongoDbConnection, 
									final String dbName, 
									final String collectionName, 
									final List<Document> jsonDocument) {
		try {
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			MongoCollection<Document> coll = database.getCollection(collectionName);
			coll.insertMany(jsonDocument);
		} catch (MongoException e) {
			AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "The bulk write operation failed due to an error");
		}
	}
	
	
	
	
	public static int addDocumentsToCollection(	final MongoDbConnection mongoDbConnection, 
												final String dbName, 
												final String collectionName,
												final List<String> jsonDocument,
												final int batchCount,
												final int idStart) throws Exception {
		if(jsonDocument == null ||
				dbName == null ||
				dbName.isBlank() || 
				collectionName == null || 
				collectionName.isBlank()) {
			throw new Exception("Null or Empty parameters");
		}
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		MongoCollection<Document> coll = database.getCollection(collectionName);
		int total = 0;
		if(batchCount == 0) {
			for (String str : jsonDocument) {
				try {
					final Document doc = Document.parse(str);
					coll.insertOne(doc);
					total++;
				} catch(Exception ignored) {	}
			}
		} else {
			int counter = 0;
			List<Document> batchList = new ArrayList<Document>();
			for (int i = 0; i < jsonDocument.size(); i++)  {
				try {
					batchList.add(Document.parse(jsonDocument.get(i)));
					counter++;
					total++;
					if(counter == batchCount || i == jsonDocument.size() - 1) {
						coll.insertMany(batchList);
					}
				} catch(Exception ignored) {	}
				
			}
		}
		return total;
	}


	public static void bulkInsert1(	final MongoDbConnection mongoDbConnection, 
									final String dbName, 
									final String collectionName, 
									final List<Document> jsonDocument) {
		try {
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			MongoCollection<Document> mongoCollection = database.getCollection(collectionName);
			List<UpdateOneModel<Document>> updates = new ArrayList<UpdateOneModel<Document>>(jsonDocument.size());

			for (Document doc : jsonDocument) {
				updates.add(new UpdateOneModel<>(new Document("_id", doc.get("_id")), doc, UPDATE_WITH_UPSERT));
			}
			mongoCollection.bulkWrite(updates);
		
		} catch (MongoException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
	}
	
	
	
	public static boolean addTempDocumentThreaded(	String userName, 
													String jsonDocument,
													boolean isThreaded) throws Exception	{
		try	{
			MongoClusterActiveRecord mongoClusterActiveRecord = getLeastUsedTempDatabase();
			if(mongoClusterActiveRecord.getUniqueNameActiveConnection() != null 
					&& !mongoClusterActiveRecord.getUniqueNameActiveConnection().isEmpty())
			{
				String activeConnection = mongoClusterActiveRecord.getUniqueNameActiveConnection();
				MongoClusterRecord cr = SqlRepoUtils.mongoDbMap.get(activeConnection);
				MongoClusterMaxRecord  mongoClusterMaxRecord = cr.getMinDbTemp();
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(activeConnection);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(), 
																			mongoClusterRecord.getClusterId(),
																			mongoClusterRecord.getUniqueName());
				if(isThreaded) {
					new Thread(() -> {
						try {
							addDocument(mongoDbConnection, mongoClusterMaxRecord.getUniqueName() , userName, jsonDocument);
						} catch (Exception e) {
							AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
						}
					}).start();
                }
				else {
					addDocument(mongoDbConnection, mongoClusterMaxRecord.getUniqueName() , userName, jsonDocument);
                }
                return true;

            }
			else
				return false;
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error adding Temp Document to Collection")) ;
		}
	}
	
	public static boolean addUserDocumentThreaded(	String userName, 
													String jsonDocument,
													boolean isThreaded) throws Exception	{
		try	{
			MongoClusterActiveRecord mongoClusterActiveRecord = getLeastUsedUserDatabase();
			if(mongoClusterActiveRecord.getUniqueNameActiveConnection() != null
					&& !mongoClusterActiveRecord.getUniqueNameActiveConnection().isEmpty())
			{
				String activeConnection = mongoClusterActiveRecord.getUniqueNameActiveConnection();
				MongoClusterRecord cr = SqlRepoUtils.mongoDbMap.get(activeConnection);
				MongoClusterMaxRecord  mongoClusterMaxRecord = cr.getMinDbUser();
				MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(activeConnection);
				MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
																			mongoClusterRecord.getClusterId(),
																			mongoClusterRecord.getUniqueName());

				if(isThreaded) {
					new Thread(() -> {
						try {
							addDocument(mongoDbConnection, mongoClusterMaxRecord.getUniqueName() , userName, jsonDocument);
						} catch (Exception e) {
							AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
						}
					}).start();
                } else {
					addDocument(mongoDbConnection, mongoClusterMaxRecord.getUniqueName() , userName, jsonDocument);
                }
                return true;
            } else
				return false;
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static MongoClusterActiveRecord getLeastUsedTempDatabase() {
		MongoClusterActiveRecord mongoClusterActiveRecord = new MongoClusterActiveRecord();
		
		for (MongoClusterRecord val : SqlRepoUtils.mongoDbMap.values()) {
		    if(mongoClusterActiveRecord.getUniqueNameActiveConnection() == null) {
		    	mongoClusterActiveRecord.setUniqueNameActiveConnection(val.getUniqueName());
		    	mongoClusterActiveRecord.addMaxActiveTemp(val.getMaxCountTemp());
		    }
		    else {
		    	
		    	mongoClusterActiveRecord.setUniqueNameActiveConnection(val.getUniqueName());
		    	mongoClusterActiveRecord.addMaxActiveTemp(val.getMaxCountTemp());
		    	
		    	if(val.getMaxCountTemp() > mongoClusterActiveRecord.getMaxActiveTemp()) {
		    		mongoClusterActiveRecord.setUniqueNameActiveConnection(val.getUniqueName());
			    	mongoClusterActiveRecord.addMaxActiveTemp(val.getMaxCountTemp());
		    	}
		    }
		}
		
		return mongoClusterActiveRecord;
	}
	
	public static MongoClusterActiveRecord getLeastUsedUserDatabase() {
		MongoClusterActiveRecord mongoClusterActiveRecord = new MongoClusterActiveRecord();
		
		for (MongoClusterRecord val : SqlRepoUtils.mongoDbMap.values()) {
		    if(mongoClusterActiveRecord.getUniqueNameActiveConnection() == null) {
		    	mongoClusterActiveRecord.setUniqueNameActiveConnection(val.getUniqueName());
		    	mongoClusterActiveRecord.addMaxActiveUser(val.getMaxCountUser());
		    }
		    else {
		    	
		    	mongoClusterActiveRecord.setUniqueNameActiveConnection(val.getUniqueName());
		    	mongoClusterActiveRecord.addMaxActiveUser(val.getMaxCountUser());
		    	
		    	if(val.getMaxCountTemp() > mongoClusterActiveRecord.getMaxActiveTemp()) {
		    		mongoClusterActiveRecord.setUniqueNameActiveConnection(val.getUniqueName());
			    	mongoClusterActiveRecord.addMaxActiveUser(val.getMaxCountUser());
		    	}
		    }
		}
		
		return mongoClusterActiveRecord;
	}
	
	public static int pushElasticToCollection (	ElasticPayload payload,
												final MongoDbConnection mongoDbConnection, 
												final String dbName, 
												final String collectionName,
												int batchCount) throws Exception {
		int totalProcessed = 0;
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		Gson gson = new GsonBuilder().create();
		try {
			Object[] listObjects = payload.getHits().getHitsAsArray();
            for (Object listObject : listObjects) {
                String str = gson.toJson(listObject);
                HitsInner hitsInner = HitsInner.toHitsInner(str);
                assert hitsInner != null;
                final Document doc = Document.parse(hitsInner.get_source().toString());
                database.getCollection(collectionName).insertOne(doc);
                totalProcessed++;
            }
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		return totalProcessed;
	}
	
	
	
	
	public static void createIndex (final MongoDbConnection mongoDbConnection, 
									final String dbName, 
									final String collectionName,
									final String fieldName) throws Exception {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		MongoCollection<Document> mongoCollection = database.getCollection(collectionName);
		
		try {
			mongoCollection.createIndex(new BasicDBObject(fieldName, "text"));
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
		
	}
	
	public static void deleteIndex (final MongoDbConnection mongoDbConnection, 
									final String dbName, 
									final String collectionName,
									final String indexName) throws Exception {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		MongoCollection<Document> mongoCollection = database.getCollection(collectionName);
		
		try {
			mongoCollection.dropIndex(indexName);
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	public static long deleteDocumentById(final MongoDbConnection mongoDbConnection, 
											final String dbName, 
											final String collectionName,
											final String idObjectStr) {
		try	{
			DeleteResult deleteResult = mongoDbConnection.getMongoClient()
														.getDatabase(dbName)
														.getCollection(collectionName)
														.deleteOne(new Document("_id", new ObjectId(idObjectStr)));
			

			return deleteResult.getDeletedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	public static long updateDocumentById(	final MongoDbConnection mongoDbConnection, 
											final String dbName, 
											final String collectionName,
											final String idObjectStr,
											final String object) 	{
		try	{
			BasicDBObject search = new BasicDBObject("_id", new ObjectId(idObjectStr));
			BasicDBObject replacement = new BasicDBObject("$set", Document.parse(object));
			
			
			UpdateResult ret = mongoDbConnection.getMongoClient()
												.getDatabase(dbName)
												.getCollection(collectionName)
												.updateOne(search, replacement);

			return ret.getModifiedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	public static long replaceDocumentById(	final MongoDbConnection mongoDbConnection, 
											final String dbName, 
											final String collectionName,
											final String idObjectStr,
											final String object) {
		try	{
			ReplaceOptions options = new ReplaceOptions().upsert(true);
			Document search = new Document("_id", new ObjectId(idObjectStr));
			final Document doc = Document.parse(object);
			UpdateResult ret = mongoDbConnection.getMongoClient()
												.getDatabase(dbName)
												.getCollection(collectionName)
												.replaceOne(search, doc, options);
			return ret.getModifiedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	
	public static long replaceDocumentEqualById(final MongoDbConnection mongoDbConnection, 
												final String dbName, 
												final String collectionName,
												final String fieldName,
												final String fieldValue,
												final String object) {
		try	{
			ReplaceOptions options = new ReplaceOptions().upsert(true);
			Bson search = eq(fieldName, fieldValue);
			final Document doc = Document.parse(object);
			
			UpdateResult ret = mongoDbConnection.getMongoClient()
								.getDatabase(dbName)
								.getCollection(collectionName)
								.replaceOne(search, doc, options);
			return ret.getModifiedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	
	
	public static long deleteDocumentByListId(	final MongoDbConnection mongoDbConnection, 
												final String dbName, 
												final String collectionName,
												final List<String> idList) {
		try	{
			final Document inQuery = new Document("_id", new Document(com.mongodb.QueryOperators.IN, idList));
			DeleteResult deleteResult = mongoDbConnection	.getMongoClient()
															.getDatabase(dbName)
															.getCollection(collectionName)
															.deleteMany(inQuery);
			return deleteResult.getDeletedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}

	public static long 
	deleteManyRecordsSimpleTextSearch (	final MongoDbConnection mongoDbConnection, 
										final String dbName, 
										final String collectionName,
										final String itemToSearchAndDelete,
										final String language
	 								  ) throws Exception {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		MongoCollection<Document> mongoCollection = database.getCollection(collectionName);
		
		BasicDBObject search = new BasicDBObject("$search", itemToSearchAndDelete);
		BasicDBObject textSearch ;
		if(language != null && !language.isBlank())
			textSearch = new BasicDBObject("$text", search.append("$language", language));
		else
			textSearch = new BasicDBObject("$text", search);
		
		
		try {
			DeleteResult  docCursor = mongoCollection.deleteMany(textSearch);
			return docCursor.getDeletedCount();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	
	public static long 
	deleteManyRecords( 	final MongoDbConnection mongoDbConnection, 
						final String dbName, 
						final String collectionName,
						final String itemToSearch,  /*The item in the JSON onject such as name or price*/
						final String valueToSearch, /*single value, not a list, an INT or LONG or VARCHAR*/
						final String operator,
						final String valueToSearchType
						) {
		try	{
			Set<String> typeValue = new HashSet<>();
			typeValue.add("INTEGER");
			typeValue.add("LONG");
			typeValue.add("DOUBLE"); 
			typeValue.add("FLOAT"); 
			typeValue.add("DATETIME"); 
			typeValue.add("STRING"); 
			if(!typeValue.contains(valueToSearchType)) {
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "-incorrect type: " + valueToSearchType);
				return -1;
			}
			
			
			
			Map<String, String> operators = new HashMap<>();
			operators.put("$eq", "equal"); 
			operators.put("$gt", "greater than"); // greater than
			operators.put("$gte", "greater than or equal to"); // greater than or equal to
			operators.put("$in", "in"); // in
			operators.put("$lt", "less than"); // less than
			operators.put("$lte", "less than or equal to"); // less than or equal to
			operators.put("$ne", "not equal");  // not equal
			operators.put("$nin", "not in"); // not in
			
			if(!operators.containsKey(operator)) {
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "-incorrect operator: " + operator);
				return -1;
			}
			var simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, valueToSearch));
			if(operator.equals("$in") ) {
				if(valueToSearchType.equalsIgnoreCase("STRING")) {
					List<String> lstStr = Arrays.asList(valueToSearch.split(","));
					simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, lstStr));
				} else if(valueToSearchType.equalsIgnoreCase("INTEGER")) {
					String[] lstStr = valueToSearch.split(",");
					List<Integer> lstInt = new ArrayList<>();
					for(String s : lstStr) lstInt.add(Integer.valueOf(s.trim()));
					simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, lstInt));
				} else if(valueToSearchType.equalsIgnoreCase("DOUBLE") ) {
					String[] lstStr = valueToSearch.split(",");
					List<Double> lstDbl = new ArrayList<>();
					for(String s : lstStr) lstDbl.add(Double.valueOf(s.trim()));
					simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, lstDbl));
				} else if(valueToSearchType.equalsIgnoreCase("FLOAT") ) {
					String[] lstStr = valueToSearch.split(",");
					List<Float> lstFlt = new ArrayList<>();
					for(String s : lstStr) lstFlt.add(Float.valueOf(s.trim()));
					simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, lstFlt));
				} else if(valueToSearchType.equalsIgnoreCase("LONG") ){
					String[] lstStr = valueToSearch.split(",");
					List<Long> lstLong = new ArrayList<>();
					for(String s : lstStr) lstLong.add(Long.valueOf(s.trim()));
					simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, lstLong));
				} else {
					simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, valueToSearch));
				}
			} else {
				Object valueToSearch_ = null;
				if(valueToSearchType.equalsIgnoreCase("INTEGER")) {
					valueToSearch_ = Integer.valueOf(valueToSearch);
				} else if(valueToSearchType.equalsIgnoreCase("DOUBLE")) {
					valueToSearch_ = Double.valueOf(valueToSearch);
				} else if(valueToSearchType.equalsIgnoreCase("FLOAT")) {
					valueToSearch_ = Float.valueOf(valueToSearch);
				} else if(valueToSearchType.equalsIgnoreCase("LONG")) {
					valueToSearch_ = Long.valueOf(valueToSearch);
				} else {
					valueToSearch_ = valueToSearch;
				}
				simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, valueToSearch_));
			}

			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			DeleteResult  docCursor = database.getCollection(collectionName).deleteMany(simpleQuery);
			return docCursor.getDeletedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	
	
	public static long
	deleteManyRecordsRange(	final MongoDbConnection mongoDbConnection, 
							final String dbName, 
							final String collectionName,
							final String itemToSearch,
							final String from,
							final String to,
							final String valueSearchType)  {
		try	{
			Set<String> typeValue = new HashSet<>();
			typeValue.add("INTEGER");
			typeValue.add("LONG");
			typeValue.add("DOUBLE"); 
			typeValue.add("FLOAT"); 
			typeValue.add("DATETIME"); 
			typeValue.add("STRING");
			if(!typeValue.contains(valueSearchType)) {
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "incorrect type: " + valueSearchType);
				return -1;
			}
			
			Object fromValue_ = null;
			Object toValue_ = null;
			if(valueSearchType.equalsIgnoreCase("INTEGER")) {
				fromValue_ = Integer.valueOf(from);
				toValue_ = Integer.valueOf(to);
			} else if(valueSearchType.equalsIgnoreCase("DOUBLE")) {
				fromValue_ = Double.valueOf(from);
				toValue_ = Double.valueOf(to);
			} else if(valueSearchType.equalsIgnoreCase("FLOAT")) {
				fromValue_ = Float.valueOf(from);
				toValue_ = Float.valueOf(to);
			} else if(valueSearchType.equalsIgnoreCase("LONG")) {
				fromValue_ = Long.valueOf(from);
				toValue_ = Long.valueOf(to);
			} else {
				fromValue_ = from;
				toValue_ = to;
			}
			
			BasicDBObject betweenQuery = new BasicDBObject();
			betweenQuery.put(itemToSearch, new BasicDBObject("$gt", fromValue_).append("$lt", toValue_));
			
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			DeleteResult  docCursor = database.getCollection(collectionName).deleteMany(betweenQuery);
			return docCursor.getDeletedCount();
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	
	public static long 
	deleteManyRecordsComplexAnd(	final MongoDbConnection mongoDbConnection, 
									final String dbName, 
									final String collectionName,
									final ComplexAndSearch complexAndSearch	) throws Exception {
		try	{
			BasicDBObject theQuery = new BasicDBObject();
			List<BasicDBObject> andObj = new ArrayList<BasicDBObject>();
			
			for(Map.Entry<String, Object> elem: complexAndSearch.getEqual().entrySet()) {
				andObj.add(new BasicDBObject( elem.getKey(), elem.getValue() ));
			}
			
			for(Map.Entry<String, Object > elem: complexAndSearch.getGreaterThan().entrySet()) {
				BasicDBObject greaterThanQuery = new BasicDBObject(elem.getKey(),  new BasicDBObject("$gte", elem.getValue()) ) ;
				andObj.add(greaterThanQuery);
			}
			
			for(Map.Entry<String, Object > elem: complexAndSearch.getLessThan().entrySet()) {
				BasicDBObject lessThanQuery = new BasicDBObject(elem.getKey(),  new BasicDBObject("$lte", elem.getValue()) ) ;
				andObj.add(lessThanQuery);
			}
			
			for(Map.Entry<String, Object> elem: complexAndSearch.getLike().entrySet()) {
				BasicDBObject likeQuery = new BasicDBObject(elem.getKey(),  new BasicDBObject("$regex", elem.getValue()).append("options", "i"));
				andObj.add(likeQuery);
			}
			
			for(Map.Entry<String, Range> elem: complexAndSearch.getRange().entrySet()) {
				BasicDBObject rangeQuery = new BasicDBObject(elem.getKey(),  BasicDBObjectBuilder.start("$gte", elem.getValue().getFrom()).add("$lte", elem.getValue().getTo())   ) ;
				andObj.add(rangeQuery);
			}
			
			for(Map.Entry<String, List<Object> > elem: complexAndSearch.getIn().entrySet()) {
				BasicDBObject inQuery = new BasicDBObject(elem.getKey(),  new BasicDBObject("$in", elem.getValue()) ) ;
				andObj.add(inQuery);
			}
			
			for(Map.Entry<String, List<Object> > elem: complexAndSearch.getNotIn().entrySet()) {
				BasicDBObject notInQuery = new BasicDBObject(elem.getKey(),  new BasicDBObject("$ne", elem.getValue()) ) ;
				andObj.add(notInQuery);
			}
			
			theQuery.put("$and", andObj);
			
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			DeleteResult  docCursor = database.getCollection(collectionName).deleteMany(theQuery);
			return docCursor.getDeletedCount();
		} catch(Exception ex)	{
			AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error deleting documents");
			return -1;
		}
	}
	
	
	public static void 
	runScript(	final MongoDbConnection mongoDbConnection, 
				final String dbName, 
				final String collectionName,
				String json	)  {
		try	{
			Document command = new Document();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
	        json = "function() { return (" + json + ").toArray(); }";
	        command.put("eval", json);
	        database.runCommand(command);
	    } catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
		
	}


	
	
}
