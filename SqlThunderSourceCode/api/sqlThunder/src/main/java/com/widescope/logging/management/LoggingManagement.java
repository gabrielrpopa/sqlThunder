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

package com.widescope.logging.management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.widescope.logging.loggingDB.H2InternalLoggingDb;
import com.widescope.logging.loggingDB.LogRecordIncoming;
import com.widescope.logging.loggingDB.LogRecordIncomingWithHostId;
import com.widescope.logging.repo.ApplicationRecord;




/**  
 * WRAPPER Functions for actual logging functionality 
 */


public final class LoggingManagement {


	private static volatile short loggingBank = 0;
	public static short getBank() {	return loggingBank; }
	public static void switchBank() { 
		if(loggingBank == 0) {
			loggingBank = 1;
		} else if(loggingBank == 1) {
			loggingBank = 0;
		} else {
			loggingBank = 0;
		}
	}
	
	/*
	 * These are the in-memory storage banks for incoming logs, 
	 * applicationId is the key
	 * 
	 * */ 
	private	static final Map<Long, List<LogRecordIncoming> > logBank1= new ConcurrentHashMap<>();
	private	static final Map<Long, List<LogRecordIncoming> > logBank2= new ConcurrentHashMap<>();
	
	/**
	 * Adds a record to in-mem storage  
	 * @param logRecordIncoming
	 */
	public static void addLogRecord(LogRecordIncoming logRecordIncoming) {
		if(loggingBank == 0) {
			if(!logBank1.containsKey(logRecordIncoming.getApplicationId())) {
				logBank1.put(logRecordIncoming.getApplicationId(), new ArrayList<>());
			}
			
			logBank1.get(logRecordIncoming.getApplicationId()).add(logRecordIncoming);
		} else if(loggingBank == 1) {
			if(!logBank2.containsKey(logRecordIncoming.getApplicationId())) {
				logBank2.put(logRecordIncoming.getApplicationId(), new ArrayList<>());
			}
			
			logBank2.get(logRecordIncoming.getApplicationId()).add(logRecordIncoming);
		} 
	}
	
	/**
	 * Serialize data to local store. This method, breaks the bank into 2 buckets of one hour
	 * @param lstLogs
	 * @param applicationRecord
	 * @throws Exception 
	 */
	private static void processLogBankToH2(	List<LogRecordIncoming> lstLogs,
											ApplicationRecord applicationRecord) throws Exception {
	switch( applicationRecord.getPartitionType() ) {
		case "HOUR" : 
			/*Break up the logs collected into hourly chunks*/
			Calendar now = Calendar.getInstance() ;
			int year = now.get(Calendar.YEAR) - 1970;
			int month = now.get(Calendar.MONTH);
			int day = now.get(Calendar.DAY_OF_MONTH);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			
			long millisecondsBucket1Start = (long) year * month * day * (hour -1) * 3600 * 1000;
			long millisecondsBucket1End = (long) year * month * day * (hour) * 3600 * 1000 - 1;
			
			List<LogRecordIncoming> b1 = lstLogs.stream().filter(p -> p.getTimestamp() > millisecondsBucket1Start 
														&&  p.getTimestamp() <= millisecondsBucket1End).collect(Collectors.toList());
			
			H2InternalLoggingDb b1Db = new H2InternalLoggingDb(	applicationRecord.getApplication(), 
																millisecondsBucket1Start, 
																millisecondsBucket1End);
			Map<Long, String> allHosts1 = b1Db.addHosts(b1);
			Map<String, Long> allHostsReversed1 = new HashMap<String, Long>();
			allHosts1.forEach((key, value) -> allHostsReversed1.put(value, key));
			
			List<LogRecordIncomingWithHostId> b1WithHostId = new ArrayList<LogRecordIncomingWithHostId>();
			for(LogRecordIncoming rec: b1) {
				long hostId =  allHostsReversed1.get(rec.getHostName());
				LogRecordIncomingWithHostId newrec 
				= new LogRecordIncomingWithHostId(	rec.getApplicationId(),
													hostId,
													rec.getUserId(),
													rec.getTimestamp(),
													rec.getMessage(),
													rec.getMessageType(),
													rec.getArtifactName(),
													rec.getArtifactType());
				b1WithHostId.add(newrec);
			}
			b1Db.addLogs(b1WithHostId);
			
			
		
			
			long millisecondsBucket2Start = (long) year * month * day * (hour) * 3600 * 1000;
			long millisecondsBucket2End = (long) year * month * day * (hour + 1) * 3600 * 1000 - 1;
			List<LogRecordIncoming> b2 = lstLogs.stream().filter(p -> p.getTimestamp() > millisecondsBucket2Start 
					&&  p.getTimestamp() <= millisecondsBucket2End).collect(Collectors.toList());
	
			

			H2InternalLoggingDb b2Db = new H2InternalLoggingDb(	applicationRecord.getApplication(), 
																millisecondsBucket2Start, 
																millisecondsBucket2End);
			Map<Long, String> allHosts2 = b2Db.addHosts(b2);
			Map<String, Long> allHostsReversed2 = new HashMap<String, Long>();
			allHosts2.forEach((key, value) -> allHostsReversed2.put(value, key));
			
			List<LogRecordIncomingWithHostId> b2WithHostId = new ArrayList<LogRecordIncomingWithHostId>();
			for(LogRecordIncoming rec: b1) {
				long hostId =  allHostsReversed1.get(rec.getHostName());
				LogRecordIncomingWithHostId newrec 
				= new LogRecordIncomingWithHostId(	rec.getApplicationId(),
													hostId,
													rec.getUserId(),
													rec.getTimestamp(),
													rec.getMessage(),
													rec.getMessageType(),
													rec.getArtifactName(),
													rec.getArtifactType());
				b2WithHostId.add(newrec);
			}
			b2Db.addLogs(b2WithHostId);
			
			
			break;
		
		case "DAY" : 
			break;
	
		case "MONTH" : 
			break;
	
		
		}
		
	} 
	
	/**
	 * Serialize data to MongoDB 
	 * @param lstLogs
	 * @param applicationRecord
	 */
	private static void processLogBankToMongoDB(List<LogRecordIncoming> lstLogs,
												ApplicationRecord applicationRecord) {
		
		
	}
	
	/**
	 * Serialize data to Elasticsearch
	 * @param lstLogs
	 * @param applicationRecord
	 */
	private static void processLogBankToElasticSearch(	List<LogRecordIncoming> lstLogs,
														ApplicationRecord applicationRecord) {
		
		
	}

	/**
	 * Serialize data to an RDMBS
	 * @param lstLogs
	 * @param applicationRecord
	 */
	private static void processLogBankToRDBMS(	List<LogRecordIncoming> lstLogs,
												ApplicationRecord applicationRecord) {


	}




	public static void processInMemLogs() throws Exception {
		/*Get first the id of the bank we need to process */
		final short currentBank = loggingBank;
		/*Then, switch banks so logging will serialize new logs to this empty bank*/
		switchBank();
		/*Now process the bank and save the logs to the permanent store*/
		if(currentBank == 0) {
			for (Long applicationId : logBank1.keySet()) {
				ApplicationRecord logApp = EnvironmentManagement.getLogApplication(applicationId);
				List<LogRecordIncoming> logs = logBank1.get(applicationId);
				if(logApp.getRepositoryType().compareTo("H2") == 0) {
					processLogBankToH2(logs, logApp);
				} else if(logApp.getRepositoryType().compareTo("MONGODB") == 0) {
					processLogBankToMongoDB(logs, logApp);
				} else if(logApp.getRepositoryType().compareTo("ELASTICSEARCH") == 0) {
					processLogBankToElasticSearch(logs, logApp);
				}
			}
			logBank1.clear();
		} else if(currentBank == 1) {
			for (Long applicationId : logBank2.keySet()) {
				ApplicationRecord logApp = EnvironmentManagement.getLogApplication(applicationId);
				List<LogRecordIncoming> logs = logBank2.get(applicationId);
				if(logApp.getRepositoryType().compareTo("H2") == 0) {
					processLogBankToH2(logs, logApp);
				} else if(logApp.getRepositoryType().compareTo("MONGODB") == 0) {
					processLogBankToMongoDB(logs, logApp);
				} else if(logApp.getRepositoryType().compareTo("ELASTICSEARCH") == 0) {
					processLogBankToElasticSearch(logs, logApp);
				} else {
					processLogBankToRDBMS(logs, logApp);
				} 
			}
			
			logBank2.clear();
		} 
	}
}
