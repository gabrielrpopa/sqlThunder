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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.widescope.logging.AppLogger;
import com.widescope.logging.loggingDB.H2InternalLoggingDb;
import com.widescope.logging.loggingDB.LogRecord;
import com.widescope.logging.loggingDB.LogRecordList;
import com.widescope.logging.repo.H2LogRepoDb;
import com.widescope.logging.repo.ApplicationRecord;
import com.widescope.logging.repo.ApplicationPartitionRecord;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

/**  
 * WRAPPER Functions for application management, their storage type and partitions
 */


public final class EnvironmentManagement {

	private
	static final
    Map<Long, ApplicationRecord> logApps= new ConcurrentHashMap<>();
	
	public
	static final
	Set<String>
	partitions = new HashSet<>(Arrays.asList("HOUR", "DAY", "MONTH"));
	
	public
	static final
	Set<String>	repositoryTypes = new HashSet<>(Arrays.asList("MONGODB", "RDBMS", "ELASTICSEARCH", "H2"));
    
	//'J' - JSON, 'T' - TEXT, 'B' - BINARY, 'Z' - ZIPPED
	public
	static final
	Set<String> messageType = new HashSet<>(Arrays.asList("J", "T", "B", "Z"));

    
    
	
	
	public 
	static 
	List<ApplicationRecord> 
	getApplications(final String application) throws Exception {
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		return  h2PartitionDb.getApplicationByName(application);
	}
	
	public 
	static 
	List<ApplicationRecord> 
	getAllApplications() throws Exception {
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		return  h2PartitionDb.getAllApplications();
	}
	
	public 
	static 
	List<ApplicationPartitionRecord> 
	getAllApplicationPartitions(long applicationId) throws Exception {
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		return  h2PartitionDb.getApplicationPartitions(applicationId);
	}
	
	public 
	static 
	ApplicationRecord 
	getApplication(final long applicationId) throws Exception {
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		return h2PartitionDb.getApplication(applicationId);
	}
	
	public 
	static 
	ApplicationRecord 
	setApplication(	final String application, 
					final String partitionType,
					final String repositoryType,
					final long repositoryId) throws Exception {
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		h2PartitionDb.addApplication(application, 
										partitionType, 
										repositoryType,
										repositoryId);
		
		return h2PartitionDb.getApplication(application);
	}
	
	
	
	
	
	
	public 
	static 
	boolean 
	createH2LogDatabase(final long applicationId, 
						final long timestamp) throws Exception {
		
		
		boolean isCreated = false; 
		
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		ApplicationRecord appEntry =  h2PartitionDb.getApplication(applicationId);
		if(appEntry == null) return false;
		
		long fromTime = -1;
		long toTime = -1;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		List<ApplicationPartitionRecord> partitionEntries = null;
		
		if(appEntry.getPartitionType().compareTo("HOUR") == 0) {
			fromTime = hour - 1;
			toTime = hour;
		} else if(appEntry.getPartitionType().compareTo("DAY") == 0) {
			fromTime = day - 1;
			toTime = day;
		} else if(appEntry.getPartitionType().compareTo("MONTH") == 0) {
			fromTime = month - 1;
			toTime = month;
		} 
		
		partitionEntries =  h2PartitionDb.getApplicationPartitionByRange(applicationId, fromTime, toTime);
		
		if(partitionEntries == null || partitionEntries.isEmpty()) {
			int recNo = h2PartitionDb.addPartitionToApplication(appEntry.getApplicationId(),
																String.valueOf(fromTime - toTime), 
																fromTime, 
																toTime);
			if(recNo > 0) {
				H2InternalLoggingDb h2InternalLoggingDb = new H2InternalLoggingDb();
				h2InternalLoggingDb.generateSchema(	appEntry.getApplication(), 
													fromTime, 
													toTime);

				partitionEntries = h2PartitionDb.getApplicationPartitionByRange(appEntry.getApplicationId(), 
																				fromTime, 
																				toTime) ;
				if(partitionEntries.size() == 1 
						&& H2InternalLoggingDb.isPartition(appEntry.getApplication(), fromTime, toTime) ) isCreated = true;
				else isCreated = true;
			}

		} else {
			return false;
		}
		return isCreated;
	}
	
	
	public 
	static 
	boolean 
	removeApplication(final long id)  {
		try {
			H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
			ApplicationRecord appEntry =  h2PartitionDb.getApplication(id);
			FileUtilWrapper.deleteDirectoryWithAllContent(appEntry.getApplication());
			h2PartitionDb.deleteAllApplicationPartitions(appEntry.getApplicationId());
			h2PartitionDb.deleteApplication(appEntry.getApplicationId());
			return true;
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return false;
		}
	}

	public 
	static 
	boolean 
	removeApplicationLogs(	final long applicationId, 
							final long fromTimeMilliSeconds, 
							final long toTimeMilliSeconds) throws Exception {
		try {
			H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
			ApplicationRecord appEntry =  h2PartitionDb.getApplication(applicationId);
			if(appEntry.getApplicationId() == -1) { return false; }
			List<ApplicationPartitionRecord> partitions 
			= h2PartitionDb.getApplicationPartitionByRange(appEntry.getApplicationId(), fromTimeMilliSeconds, toTimeMilliSeconds);
			for(ApplicationPartitionRecord logRefDbRecordPartition : partitions) {
				FileUtilWrapper.deleteFile(logRefDbRecordPartition.getFile());
			}
			h2PartitionDb.deleteApplicationPartition(appEntry.getApplicationId(), fromTimeMilliSeconds, toTimeMilliSeconds);
			return true;
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return false;
		}
	}
	
	
	public 
	static 
	boolean 
	addNewLogEntry(	final long applicationId, 
					final String host,
					final long userId,
					final String message,
					final String messageType,
					final long timeMilliSeconds,
					final String artifactName,
					final String artifactType
					) throws Exception {
		
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		ApplicationRecord appEntry =  h2PartitionDb.getApplication(applicationId);
		
		long fromTime = -1;
		long toTime = -1;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMilliSeconds);
		
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		if(appEntry.getPartitionType().compareTo("HOUR") == 0) {
			fromTime = hour - 1;
			toTime = hour;
		} else if(appEntry.getPartitionType().compareTo("DAY") == 0) {
			fromTime = day - 1;
			toTime = day;
		} else if(appEntry.getPartitionType().compareTo("MONTH") == 0) {
			fromTime = month - 1;
			toTime = month;
		} 
		
		
		
		H2InternalLoggingDb h2InternalLoggingDb =
		H2InternalLoggingDb.setH2InternalLoggingDb(	appEntry.getApplication(), 
													fromTime, 
													toTime);
		
		
		final long hostId = h2InternalLoggingDb.addHost(host);
			
		h2InternalLoggingDb.addLog(	hostId,
									userId, 
									timeMilliSeconds,
									message,
									messageType, 
									artifactName,
									artifactType
									);
		
		return true;
	}
	
	
	
	public 
	static 
	ApplicationRecord 
	getLogFiles(final long applicationId, 
				final long fromTimeMilliSeconds,
				final long toTimeMilliSeconds) throws Exception {
		
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		ApplicationRecord applicationRecord =  h2PartitionDb.getApplication(applicationId);
		List<ApplicationPartitionRecord> recs 
		=  h2PartitionDb.getApplicationPartitionByRange(applicationId, 
														fromTimeMilliSeconds, 
														toTimeMilliSeconds); 
		applicationRecord.setApplicationPartitionRecordList(recs); 
		return applicationRecord;
	}
	
	
	public 
	static 
	ApplicationRecord 
	getLogEntry(final long applicationId, 
				final long fromTimeMilliSeconds,
				final long toTimeMilliSeconds,
				final long logEntry) throws Exception {
		
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		ApplicationRecord applicationRecord =  h2PartitionDb.getApplication(applicationId);
		List<ApplicationPartitionRecord> recs 
		=  h2PartitionDb.getApplicationPartitionByRange(applicationId, 
														fromTimeMilliSeconds, 
														toTimeMilliSeconds); 
		applicationRecord.setApplicationPartitionRecordList(recs); 
		return applicationRecord;
	}
	
	
	public 
	static 
	LogRecordList 
	getLogEntries(	final ApplicationRecord applicationRecord, /*gotten from getLogFiles. see above*/
					final long fromTimeMilliSeconds, /* greater than or equal */
					final long toTimeMilliSeconds, /* less than or equal */
					final long userId,  /*equal*/
					final String host,  /*like*/
					final String message /*like*/
					) throws Exception {
		
		LogRecordList LogRecordList = new LogRecordList();
		for(ApplicationPartitionRecord partition : applicationRecord.getApplicationPartitionRecordList()) {
			H2InternalLoggingDb h2InternalLoggingDb = new H2InternalLoggingDb(	applicationRecord.getApplication(),
																				partition.getFromTime(),
																				partition.getToTime()
																				);
			final List<Long> hostList = h2InternalLoggingDb.getHostIds(host);
			List<LogRecord> recs = h2InternalLoggingDb.getLog(	fromTimeMilliSeconds, 
																toTimeMilliSeconds,
																userId,
																hostList,
																message); 
			LogRecordList.addLogRecordFromDbList(recs);
		}
		return LogRecordList;
	}
	
	public
	static
	LogRecordList 
	getLogEntries(	final ApplicationRecord applicationRecord, /*gotten from getLogFiles. see above*/
					final long fromTimeMilliSeconds, /* greater than or equal */
					final long toTimeMilliSeconds, /* less than or equal */
					final String message /*like*/
					) throws Exception {
		
		LogRecordList LogRecordList = new LogRecordList();
		for(ApplicationPartitionRecord partition : applicationRecord.getApplicationPartitionRecordList()) {
			H2InternalLoggingDb h2InternalLoggingDb = new H2InternalLoggingDb(	applicationRecord.getApplication(),
																				partition.getFromTime(),
																				partition.getToTime()
																				);

			List<LogRecord> recs = h2InternalLoggingDb.getLog(	fromTimeMilliSeconds, 
																toTimeMilliSeconds,
																message); 
			LogRecordList.addLogRecordFromDbList(recs);
		}
		return LogRecordList;
	}
	
	public
	static
	LogRecordList 
	getLogEntries(	final ApplicationRecord applicationRecord, /*gotten from getLogFiles. see above*/
					final long fromTimeMilliSeconds, /* greater than or equal */
					final long toTimeMilliSeconds, /* less than or equal */
					final long entryId 
					) throws Exception {
		
		LogRecordList LogRecordList = new LogRecordList();
		for(ApplicationPartitionRecord partition : applicationRecord.getApplicationPartitionRecordList()) {
			H2InternalLoggingDb h2InternalLoggingDb = new H2InternalLoggingDb(	applicationRecord.getApplication(),
																				partition.getFromTime(),
																				partition.getToTime()
																				);

			List<LogRecord> recs = h2InternalLoggingDb.getLog(entryId); 
			LogRecordList.addLogRecordFromDbList(recs);
		}
		return LogRecordList;
	}
	
	/**
	 * This method, is used in both at startup or regularly during runtime
	 * in order to cleanup in mem applications
	 * */
	public static void LoadLogApps() {
		long currentTime = DateTimeUtils.millisecondsSinceEpoch();
		H2LogRepoDb h2PartitionDb = new H2LogRepoDb();
		try {
			/*Load data from permanent storage*/
			List<ApplicationRecord> apps = h2PartitionDb.getAllApplications();
			List<Long> appsIdList= apps.stream().map(ApplicationRecord::getApplicationId).toList();
			/*First remove existing app entries that were deleted in prior operations by admins*/
            logApps.entrySet().removeIf(entry -> appsIdList.stream().filter(x -> x.equals(entry.getKey())).findFirst().isEmpty());
			
			
			/*Secondly update */
			for(ApplicationRecord app: apps) {
				List<ApplicationPartitionRecord>  partList = h2PartitionDb.getApplicationPartitions(app.getApplicationId());
				List<ApplicationPartitionRecord>  partListFinal = new ArrayList<>();
				for(ApplicationPartitionRecord p: partList) {
					if(p.getFromTime() > currentTime && p.getToTime() < currentTime) {
						partListFinal.add(p);
					}
				}
				app.setApplicationPartitionRecordList(partListFinal);
				logApps.put(app.getApplicationId(), app);
			}
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
		
	}
	
	public static ApplicationRecord getLogApplication(Long applicationId) {
		return logApps.get(applicationId);
	}
	public static boolean isApplication(Long applicationId) {
		return logApps.containsKey(applicationId);
	}
	

}
