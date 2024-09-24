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

package com.widescope.rdbmsRepo.database.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.widescope.chat.db.ChatMessage;
import com.widescope.logging.AppLogger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoCollectionInfo;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoDatabaseInfo;
import com.widescope.rdbmsRepo.database.mongodb.repo.timestamp.CollectionTimestamp;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class MongoGet {

	public static Set<String> getAllCollectionNames(final MongoDbConnection mongoDbConnection, 
													final String dbName) {
		try	{
			Set<String> ret = new HashSet<String>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			MongoIterable<String> collectionList = database.listCollectionNames();
			for(String collName: collectionList) {
				ret.add(collName);
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new HashSet<>();
		}
	}
	
	public static List<String> getAllCollectionNames_(	final MongoDbConnection mongoDbConnection, 
														final String dbName) 	{
		try	{
			List<String> ret = new ArrayList<>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			MongoIterable<String> collectionList = database.listCollectionNames();
			for(String collName: collectionList) {
				ret.add(collName);
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	public static List<MongoCollectionInfo> getCollectionInfo(final MongoDbConnection mongoDbConnection, 
															  final String dbName)	{
		try	{
			List<MongoCollectionInfo> ret = new ArrayList<>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			MongoIterable<String> collectionList = database.listCollectionNames();
			for(String collName: collectionList) {
				
				if(collName.endsWith(".chunks")) {
					continue;
				}
				String collName_ = collName;
				long countObj = getCollectionDocumentsCount(mongoDbConnection, dbName, collName, "Y");
				String signature = getFirstDocument(mongoDbConnection, dbName, collName);
				String subType = "COLLECTION";
				String type = "USER";
				if(collName.endsWith(".files")) {
					subType = "BUCKET";
					collName_ = collName_.replace(".files", ""); 
				} 
				
				if(collName.endsWith("system.version") ) {
					type = "SYSTEM";
				}

				if( ret.stream().filter(x-> x.getName().isEmpty()).findFirst().isEmpty() ) {
					MongoCollectionInfo mongoCollectionInfo 
					= new MongoCollectionInfo(	collName_,
												countObj,
												type,
												subType,
												signature);
					ret.add(mongoCollectionInfo);
				}
			}
			return ret;
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	
	
	
	public static boolean isCollection(	final MongoDbConnection mongoDbConnection, 
										final String dbName,
										final String colName) {

		boolean isCollection = false;
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			MongoIterable<String> collectionList = database.listCollectionNames();
			
			for(String tmp: collectionList) {
				if(colName.equalsIgnoreCase(tmp)) {
					isCollection = true;
					break;
				}
			}
			return isCollection;
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return isCollection;
		}
	}
	
	
	
	public static Set<String> getAllDatabaseNames(final MongoDbConnection mongoDbConnection)	{
		try	{
			Set<String> ret = new HashSet<String>();
			MongoIterable<String> databaseList = mongoDbConnection.getMongoClient().listDatabaseNames();
			for(String database: databaseList) {
				ret.add(database);
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new HashSet<>();
		}
	}
	
	public static List<MongoDatabaseInfo> getDatabasesInfo(final MongoDbConnection mongoDbConnection) 	{
		try	{
			List<MongoDatabaseInfo> ret = new ArrayList<MongoDatabaseInfo>();
			MongoIterable<String> databaseList = mongoDbConnection.getMongoClient().listDatabaseNames();
			for(String databaseName: databaseList) {
				String type = "USER";
				if(databaseName.equalsIgnoreCase("admin") || databaseName.equalsIgnoreCase("config")) {
					type = "SYSTEM";
				} 
				int counter = mongoDbConnection.getMongoClient().getDatabase(databaseName).listCollectionNames().into(new ArrayList<>()).size();
				MongoDatabaseInfo mongoDatabaseInfo = new MongoDatabaseInfo(databaseName, counter, type);
				ret.add(mongoDatabaseInfo);
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	public static List<String> getAllDatabaseNames_(final MongoDbConnection mongoDbConnection)	{
		try	{
			List<String> ret = new ArrayList<String>();
			MongoIterable<String> databaseList = mongoDbConnection.getMongoClient().listDatabaseNames();
			for(String database: databaseList) {
				ret.add(database);
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	
	
	
	/*                       DOCUMENTS                                                              */
	
	public static long getCollectionDocumentsCount(	final MongoDbConnection mongoDbConnection, 
													final String dbName, 
													final String collectionName,
													final String isEstimate) 	{
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			if(isEstimate.equalsIgnoreCase("Y"))
				return database.getCollection(collectionName).estimatedDocumentCount();
			else
				return database.getCollection(collectionName).countDocuments();
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return -1;
		}
	}
	
	
	public static Set<String> getAllCollectionDocumentsAsListOfString(	final MongoDbConnection mongoDbConnection, 
																		final String dbName, 
																		final String collectionName) throws Exception	{
		try	{
			Set<String> ret = new HashSet<String>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find();

            for(Document doc: docCursor) {
                //docTmp.remove("_id");
				ObjectMapper mapper = new ObjectMapper();
				try	{
					ResultQuery resultQuery = mapper.readValue(doc.toJson(), ResultQuery.class);
					resultQuery.getMongoDbId().setClusterName(mongoDbConnection.getClusterName()) ;
					resultQuery.getMongoDbId().setDocId(doc.getObjectId("_id").toString()) ;
					resultQuery.getMongoDbId().setcName(collectionName) ;
					resultQuery.getMongoDbId().setDbName(dbName) ;
					ret.add(doc.toJson());
				}
				catch(JsonProcessingException ignored) { }
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new HashSet<>();
		}
	}
	
	
	
	public static List<String> getFirstCollectionNDocuments(final MongoDbConnection mongoDbConnection, 
															final String dbName, 
															final String collectionName,
															final int limit) 	{
		try	{
			List<String> ret = new ArrayList<String>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find().limit(limit);
			for(Document doc: docCursor) {
				try	{
					ret.add(doc.toJson());
				}
				catch(Exception ignored) { }
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	public static MongoResultSet getAllCollectionDocuments(final MongoDbConnection mongoDbConnection, 
															final String dbName, 
															final String collectionName)	{
		MongoResultSet ret = new MongoResultSet();
		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find();

			
			for(Document doc: docCursor) {
				doc.remove("_id");
				try	{
					ret.addResultSet(doc, false);
				}
				catch(Exception ignored) { }
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
	}
	
	public static List<String> getAllCollectionDocuments_(	final MongoDbConnection mongoDbConnection, 
															final String dbName, 
															final String collectionName) {
		try	{
			List<String> ret = new ArrayList<String>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find();

			
			for(Document doc: docCursor) {
				ObjectMapper mapper = new ObjectMapper();
				try	{
					ResultQuery resultQuery = mapper.readValue(doc.toJson(), ResultQuery.class);
					resultQuery.getMongoDbId().setClusterName(mongoDbConnection.getClusterName()) ;
					resultQuery.getMongoDbId().setDocId(doc.getObjectId("_id").toString()) ;
					resultQuery.getMongoDbId().setcName(collectionName) ;
					resultQuery.getMongoDbId().setDbName(dbName) ;
					ret.add(doc.toJson());
				}
				catch(JsonProcessingException ignored) { }
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	
	public static List<String> getAllCollectionDocumentsAsPlainInList(	final MongoDbConnection mongoDbConnection, 
																		final String dbName, 
																		final String collectionName,
																		final boolean isIdRemoved) 	{
		try	{
			List<String> ret = new ArrayList<String>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find();

			for(Document doc: docCursor) {
				String objectId = doc.getObjectId("_id").toHexString();
				doc.remove("_id");
				if(!isIdRemoved) {
					doc.append("_id", objectId);
				}
				try	{
					ret.add(doc.toJson());
				}
				catch(Exception ignored) { }
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	public static List<CollectionTimestamp> getAllCollectionDocumentTimestamp(	final MongoDbConnection mongoDbConnection, 
																				final String dbName, 
																				final String collectionName) {
		try	{
			List<CollectionTimestamp> ret = new ArrayList<CollectionTimestamp>();
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find();

			for(Document doc: docCursor) {
				CollectionTimestamp collectionTimestamp = new CollectionTimestamp(	doc.getObjectId("_id").toString(), 
																					doc.getObjectId("_id").getDate().getTime(),
																					dbName,
																					collectionName);
				ret.add(collectionTimestamp);
			}
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new ArrayList<>();
		}
	}
	
	public static String getDocumentById(	final MongoDbConnection mongoDbConnection, 
											final String dbName, 
											final String collectionName,
											final String id) {
		try	{
			BasicDBObject equiQuery = new BasicDBObject();
			org.bson.types.ObjectId r = new ObjectId(id);
			equiQuery.put("_id", r );

			
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find(equiQuery);

			//Bson filter = Filters.eq("_id", r);
			//FindIterable<Document> docCursor = database.getCollection(collectionName).find(filter);

			for(Document doc: docCursor) {
               	ObjectMapper mapper = new ObjectMapper();
				try	{
					ResultQuery resultQuery = mapper.readValue(doc.toJson(), ResultQuery.class);
					resultQuery.getMongoDbId().setClusterName(mongoDbConnection.getClusterName()) ;
					resultQuery.getMongoDbId().setDocId(id) ;
					resultQuery.getMongoDbId().setcName(collectionName) ;
					resultQuery.getMongoDbId().setDbName(dbName) ;
					return resultQuery.toString();
				}
				catch(JsonProcessingException ex) {
					return doc.toJson();
				}
			}
			return null;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return null;
		}
	}


	public static ChatMessage getChatMessageById_( final MongoDbConnection mongoDbConnection,
											 final String dbName,
											 final String collectionName,
											 final String id) {
		try	{
			BasicDBObject equiQuery = new BasicDBObject();
			org.bson.types.ObjectId r = new ObjectId(id);
			equiQuery.put("_id", r );


			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			FindIterable<Document> docCursor = database.getCollection(collectionName).find(equiQuery);

			for(Document doc: docCursor) {
				doc.remove("_id");
				try	{
					String c = doc.toJson();
                    return ChatMessage.toChatMessage(c);
				}
				catch(Exception ex) {
					AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
					return null;
				}
			}


			return null;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return null;
		}
	}


	public static ChatMessage getChatMessageById( final MongoDbConnection mongoDbConnection,
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
			return collection.find(eq("_id", id)).first();
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return null;
		}
	}

	public static ChatMessage deleteMessageById( final MongoDbConnection mongoDbConnection,
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
			if(m!=null) {
				DeleteResult d = collection.deleteOne(eq("_id", id));
				if(d.getDeletedCount() == 0) {
					m = null;
				}
			}

			return m;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return null;
		}
	}


	
	
	
	public static String 
	getFirstDocument(	final MongoDbConnection mongoDbConnection, 
						final String dbName, 
						final String collectionName	){

		try	{
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			return  Objects.requireNonNull(database.getCollection(collectionName).find().first()).toJson();
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new JsonObject().toString();
		}
	}
	
	
	
	public static <T> MongoResultSet 
	searchDocumentSimpleText(	final MongoDbConnection mongoDbConnection, 
								final String dbName, 
								final String collectionName,
								final String itemToSearch,
								final String language,
								final String isHighestScore	) {

		try	{
			List<org.bson.Document> rSet = new ArrayList<org.bson.Document>();
			
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			long maxCountCollection = database.getCollection(collectionName).estimatedDocumentCount();
			
			BasicDBObject search = new BasicDBObject("$search", itemToSearch);
			BasicDBObject textSearch = null;
			if(language != null && !language.isBlank())
				textSearch = new BasicDBObject("$text", search.append("$language", language));
			else
				textSearch = new BasicDBObject("$text", search);
			
			FindIterable<Document> docCursor = null;
			long cursorCount = 0 ;
			if(isHighestScore.equalsIgnoreCase("Y")) {
				docCursor = database.getCollection(collectionName).find(Filters.text(itemToSearch))
                .projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score"));
			} else {
				docCursor = database.getCollection(collectionName).find(textSearch);
			}

			for(Document doc: docCursor) {
				String objectId = doc.getObjectId("_id").toHexString();
				doc.remove("_id");
				doc.append("_id", objectId);
				rSet.add(doc);
				cursorCount++;
			}
			return new MongoResultSet(rSet, cursorCount, maxCountCollection);
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new MongoResultSet();
		}
	}



	public static <T> MongoResultSet 
	searchDocument( final MongoDbConnection mongoDbConnection, 
					final String dbName, 
					final String collectionName,
					final String itemToSearch,  /*The item in the JSON object such as name or price*/
					final String valueToSearch, /*single value, not a list, an INT or LONG or VARCHAR*/
					final String operator,
					final String valueToSearchType,
					final boolean isRemoveId,
					final boolean isMetadata
					) throws Exception	{

		try	{
			Set<String> typeValue = new HashSet<>();
			typeValue.add("INTEGER");
			typeValue.add("LONG");
			typeValue.add("DOUBLE"); 
			typeValue.add("FLOAT"); 
			typeValue.add("DATETIME"); 
			typeValue.add("STRING"); 
			if(!typeValue.contains(valueToSearchType)) {
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "incorrect type:" + valueToSearchType);
				return new MongoResultSet();
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
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "incorrect operator:" + operator);
				return new MongoResultSet();
			}
			List<org.bson.Document> rSet = new ArrayList<org.bson.Document>();
			var simpleQuery = new BasicDBObject(itemToSearch, new BasicDBObject(operator, valueToSearch));
			if(operator.equals("$in")) {
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
			long cursorCount = database.getCollection(collectionName).countDocuments(simpleQuery);
			long maxCountCollection = database.getCollection(collectionName).estimatedDocumentCount();
			
		
			
			
			FindIterable<org.bson.Document> docCursor = database.getCollection(collectionName).find(simpleQuery);

            MongoResultSet m = new MongoResultSet(rSet, cursorCount, maxCountCollection);
	
			for(Document doc: docCursor) {
				String objectId = doc.getObjectId("_id").toHexString();
				doc.remove("_id");
				if(!isRemoveId) {
					doc.append("_id", objectId);
				}
				m.addResultSet(doc, isMetadata);
			}
			return m;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new MongoResultSet();
		}
	}
	

	public static <T> MongoResultSet 
	searchDocumentRange(final MongoDbConnection mongoDbConnection, 
						final String dbName, 
						final String collectionName,
						final String itemToSearch,
						final String from,
						final String to,
						final String valueSearchType,
						final boolean isRemoveId) throws Exception {

		try	{
			
			Set<String> typeValue = new HashSet<>();
			typeValue.add("INTEGER");
			typeValue.add("LONG");
			typeValue.add("DOUBLE"); 
			typeValue.add("FLOAT"); 
			typeValue.add("DATETIME"); 
			typeValue.add("STRING"); 
			if(!typeValue.contains(valueSearchType)) {
				AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "incorrect type:" + valueSearchType);
				return new MongoResultSet();
			}
			
			
			List<org.bson.Document> rSet = new ArrayList<org.bson.Document>();
			
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
			} else if(valueSearchType.equalsIgnoreCase("STRING")) {
				fromValue_ = from;
				toValue_ = to;
			} else {
				fromValue_ = from;
				toValue_ = to;
			}
			
			BasicDBObject betweenQuery = new BasicDBObject();
			betweenQuery.put(itemToSearch, new BasicDBObject("$gt", fromValue_).append("$lt", toValue_));
			
			MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
			long cursorCount = database.getCollection(collectionName).countDocuments(betweenQuery);
			long maxCountCollection = database.getCollection(collectionName).estimatedDocumentCount();
			//String x1 = betweenQuery.toJson();
			FindIterable<Document> docCursor = database.getCollection(collectionName).find(betweenQuery);

			for(Document doc: docCursor) {
				String objectId = doc.getObjectId("_id").toHexString();
				doc.remove("_id");
				if(!isRemoveId) {
					doc.append("_id", objectId);
				}
				rSet.add(doc);
			}
			return new MongoResultSet(rSet, cursorCount, maxCountCollection);
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new MongoResultSet();
		}
	}


	public static <T> MongoResultSet 
	searchDocumentComplexAnd(	final MongoDbConnection mongoDbConnection, 
								final String dbName, 
								final String collectionName,
								final ComplexAndSearch complexAndSearch)  {
		try	{
			List<org.bson.Document> rSet = new ArrayList<org.bson.Document>();
			
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
			long cursorCount = database.getCollection(collectionName).countDocuments(theQuery);
			long maxCountCollection = database.getCollection(collectionName).estimatedDocumentCount();
			
			
			FindIterable<Document> docCursor = null;
			if(!complexAndSearch.getSort().isEmpty()) {
  
				if(complexAndSearch.getFromRow() >= 0 && complexAndSearch.getNoRow() > 0)
					docCursor = database.getCollection(collectionName).find(theQuery).sort(new BasicDBObject(complexAndSearch.getSort())).skip(complexAndSearch.getFromRow()).limit(complexAndSearch.getNoRow());
				else if(complexAndSearch.getFromRow() < 0 && complexAndSearch.getNoRow() > 0)
					docCursor = database.getCollection(collectionName).find(theQuery).sort(new BasicDBObject(complexAndSearch.getSort())).limit(complexAndSearch.getNoRow());
				else if(complexAndSearch.getFromRow() >= 0 && complexAndSearch.getNoRow() <= 0)
					docCursor = database.getCollection(collectionName).find(theQuery).sort(new BasicDBObject(complexAndSearch.getSort())).skip(complexAndSearch.getNoRow());
				else if(complexAndSearch.getFromRow() < 0 && complexAndSearch.getNoRow() <= 0)
					docCursor = database.getCollection(collectionName).find(theQuery).sort(new BasicDBObject(complexAndSearch.getSort()));
				else
					docCursor = database.getCollection(collectionName).find(theQuery).sort(new BasicDBObject(complexAndSearch.getSort()));
			}
			else {
				if(complexAndSearch.getFromRow() >= 0 && complexAndSearch.getNoRow() > 0) 
					docCursor = database.getCollection(collectionName).find(theQuery).skip(complexAndSearch.getFromRow()).limit(complexAndSearch.getNoRow());
				else if(complexAndSearch.getFromRow() < 0 && complexAndSearch.getNoRow() > 0)
					docCursor = database.getCollection(collectionName).find(theQuery).limit(complexAndSearch.getNoRow());
				else if(complexAndSearch.getFromRow() >= 0 && complexAndSearch.getNoRow() <= 0)
					docCursor = database.getCollection(collectionName).find(theQuery).skip(complexAndSearch.getNoRow());
				else if(complexAndSearch.getFromRow() < 0 && complexAndSearch.getNoRow() <= 0)
					docCursor = database.getCollection(collectionName).find(theQuery);
				else
					docCursor = database.getCollection(collectionName).find(theQuery);
			}
			
			

			
			for(Document doc: docCursor) {
				String objectId = doc.getObjectId("_id").toHexString();
				doc.remove("_id");
				doc.append("_id", objectId);
				rSet.add(doc);
			}
			return new MongoResultSet(rSet, cursorCount, maxCountCollection);
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new MongoResultSet();
		}
	}
	
	
	public static <T> MongoResultSet searchDocumentParallel(final String dbType, /*TEMP/USER*/
															final String user,
															final ComplexAndSearch complexAndSearch)  {
		MongoResultSet ret = new MongoResultSet();
		try	{
			int maxThreads = 0;
			if(dbType.equalsIgnoreCase("USER")) {
				for (MongoClusterRecord val : SqlRepoUtils.mongoDbMap.values()) {
					maxThreads+=val.getMongoDbUserDbsMap().size();
				}
			}
			else {
				for (MongoClusterRecord val : SqlRepoUtils.mongoDbMap.values()) {
					maxThreads+=val.getMongoDbTempDbsMap().size();
				}
			}
			
			ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
			
			for (MongoClusterRecord val : SqlRepoUtils.mongoDbMap.values()) {
				
				MongoDbConnection mongoDbConnection = new MongoDbConnection(val.getConnString(),
																			val.getClusterId(),
																			val.getUniqueName());
				
				if(dbType.equalsIgnoreCase("USER")) {
					for (String dbName : val.getMongoDbUserDbsMap().keySet()) {
						SearchThread<T> searchThread = new SearchThread<T>(
																		mongoDbConnection,
																		dbName,
																		user,
																		complexAndSearch);
			            Future< MongoResultSet > result = executor.submit(searchThread);
			            ret.add(result.get());
					}
				}
				else {
					for (String dbName : val.getMongoDbTempDbsMap().keySet()) {
						SearchThread<T> searchThread = new SearchThread<T>(
																	mongoDbConnection,
																	dbName,
																	user,
																	complexAndSearch);
			            Future< MongoResultSet > result = executor.submit(searchThread);
			            ret.add(result.get());
					}
				}
				
			}
			
			return ret;
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new MongoResultSet();
		}
	}
	
	
	public static <T> MongoResultSet queryResults(	final Class<T> objectType, /*RESULTQUERY/RESULTQUERYHEADER/STRING*/
													final String dbType, /*TEMP/USER*/
													String user, 
													int sqlId, 
													long fromDateTime, 
													long toDateTime) throws Exception {

		Map<String, Object> equal = new HashMap<String, Object>();
		equal.put("sqlId", sqlId);
		Map<String, Object> lessThan = new HashMap<String, Object>();
		lessThan.put("timestamp", toDateTime);
		Map<String, Object> greaterThan = new HashMap<String, Object>();
		greaterThan.put("timestamp", fromDateTime);
		Map<String, Integer> sort = new HashMap<String, Integer>();
		sort.put("timestamp", 1);
		
		ComplexAndSearch complexAndSearch = new ComplexAndSearch(new HashMap<String, Range>(),
											equal,
											lessThan,
											greaterThan,
											new HashMap<String, Object>(),
											new HashMap<String, List<Object>>(),
											new HashMap<String, List<Object>>(),
											sort,
											0,
											0);
		return searchDocumentParallel(dbType, user, complexAndSearch);
	}
	
	
	public static void deleteOldTempDocuments() throws Exception {
		try	{
			int maxThreads = 3;
			ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
			for (MongoClusterRecord val : SqlRepoUtils.mongoDbMap.values()) {
				MongoDbConnection mongoDbConnection = new MongoDbConnection(val.getConnString(),
																			val.getClusterId(),
																			val.getUniqueName());
				long nowMilliseconds = System.currentTimeMillis();
				for (String dbName : val.getMongoDbTempDbsMap().keySet()) {
					Set<String> colls = getAllCollectionNames(mongoDbConnection, dbName);
					for(String colName : colls) {
						TimestampThread searchThread = new TimestampThread(mongoDbConnection, dbName, colName);
						Future< List<CollectionTimestamp> > result = executor.submit(searchThread);
						List<CollectionTimestamp> lstToDelete = result.get();
						for(CollectionTimestamp itemToDelete : lstToDelete) {
							if(nowMilliseconds - itemToDelete.getTimeStamp() > 60 * 1000)
								MongoPut.deleteDocumentById(mongoDbConnection, dbName, colName, itemToDelete.getObjectId());
						}
					}
				}
			}	
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	
	

	public static MongoResultSet
	execDynamicQuery(	final String clusterName,
						final String dbName,
						final String collectionName,
						final String commandStr,
						final boolean isRemoveId
					) throws Exception {
		
		MongoResultSet ret = new MongoResultSet();
		
		MongoClusterRecord mongoClusterRecordSource = SqlRepoUtils.mongoDbMap.get(clusterName);
		if(mongoClusterRecordSource == null) {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Incorrect cluster Name")) ;
		}
		
		MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecordSource);
		final Document jsonDoc = Document.parse(commandStr);
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		FindIterable<Document> docCursor = database.getCollection(collectionName).find(jsonDoc);

		long count = database.getCollection(collectionName).countDocuments(); // or estimatedDocumentCount()
		ret.setCountCollection(count);
		for(Document doc: docCursor) {
            String objectId = doc.getObjectId("_id").toHexString();
			doc.remove("_id");
			if(!isRemoveId) {
				doc.append("_id", objectId);
			}
			ret.addResultSet(doc, false);
		}
		
		Map<String, String> meta = MongoResultSet.analyseSchemaDeep(ret.getResultSet());
		ret.setMetadata(meta);
		mongoDbConnection.disconnect();
		return ret;
	}
	
	
}
