package com.widescope.rdbmsRepo.database.tempSqlRepo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;


public class HistFileManagement {
	
	
	// type;       /*repo/adhoc*/
	// source;     /*es/rdbms/mongo*/


	public static String 
	addNewStatement(final long userId, 
					final String content, 
					final String comment,
					final long timeStamp,
					final String mainFolder,
					final String type,
					final String source	) throws Exception {
		final String shaHash = StringUtils.getSHA256Hash(content, ConfigRepoDb.configValues.get("save-adhoc-algo").getConfigValue() );
		if(shaHash == null) {
            return AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Error generating hash. Statement cannot be saved for later use");
		}
		
		final String folder = mainFolder + "/" + type + "/" + source + "/" + String.valueOf(userId) + "/" + shaHash;
		
		File dir = new File(folder);
		dir.mkdirs();
		FileUtilWrapper.overWriteFile(	folder + "/s.txt", content);
		FileUtilWrapper.overWriteFile(	folder + "/c.txt", comment);
		FileUtilWrapper.overWriteFile(	folder + "/t.txt", String.valueOf(timeStamp) );
		return "Statement has been saved for later use";
	}
	
	
	public static boolean 
	deleteStatement(final long userId, 
					final String shaHash, 
					final String mainFolder,
					final String type,
					final String source)  {
		final String folder = mainFolder + "/" + type + "/" + source + "/" + String.valueOf(userId) + "/" + shaHash;
		File fld = new File(folder );
		if (fld.exists()) {
			return FileUtilWrapper.deleteDirectoryWithAllContent(folder);
		} else {
			return false;
		}
	}
	
	
	
	public static String 
	addExistingStmToNewUser(final long fromUserId,
							final long toUserId, 
							final String shaHash, 
							final String mainFolder,
							final String type,
							final String source) throws Exception {
		final String content = getStm(fromUserId, shaHash,mainFolder, type, source);
		final String comment = getComment(fromUserId, shaHash,mainFolder, type, source);
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		return addNewStatement(toUserId, content, comment, timeStamp, mainFolder, type, source);
	}
	
	
	
	
	
	/*NOTE: MAKE SURE total length of folder is < 256 as SHAhash alone is 64*/
	
	
	public static List<String> 
	getStmts(	final long userId, 
				final String mainFolder,
				final String type,
				final String source) {
		List<String> ret = new ArrayList<>();
		final String folder = mainFolder + "/" + type + "/" + source + "/" + userId;
		try {
			String[] lst = FileUtilWrapper.getListFolders(folder);
            Collections.addAll(ret, lst);
			return ret;
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
		
	}
	
	
	
	public static String 
	getStm(	long userId, 
			final String shaHash,
			final String mainFolder,
			final String type,
			final String source) {
		final String file = mainFolder + "/" + type + "/" + source + "/" + userId + "/" + shaHash + "/s.txt" ;
		try {
			return FileUtilWrapper.readFileToString(file);
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return "";
		}
		
	}
	
	
	public static String 
	getComment(	long userId, 
			final String shaHash,
			final String mainFolder,
			final String type,
			final String source) {
		final String file = mainFolder + "/" + type + "/" + source + "/" + userId + "/" + shaHash + "/c.txt" ;
		try {
			return FileUtilWrapper.readFileToString(file);
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return "";
		}
	
	}
	
	public static String 
	getTimeStamp(long userId, 
			final String shaHash,
			final String mainFolder,
			final String type,
			final String source) {
		final String file = mainFolder + "/" + type + "/" + source + "/" + userId + "/" + shaHash + "/t.txt" ;
		try {
			return FileUtilWrapper.readFileToString(file);
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return "";
		}
	
	}
	
	
	
	
	
	public static HistoryStatement 
	getStm_(long userId, 
			final String shaHash,
			final String mainFolder,
			final String type,
			final String source) {
		HistoryStatement ret = new HistoryStatement(userId, shaHash);
		
		final String t = mainFolder + "/" + type + "/" + source + "/" + userId + "/" + shaHash + "/t.txt" ;
		final String c = mainFolder + "/" + type + "/" + source + "/" + userId + "/" + shaHash + "/c.txt" ;
		final String s = mainFolder + "/" + type + "/" + source + "/" + userId + "/" + shaHash + "/s.txt" ;
		try {
			ret.setComment(  FileUtilWrapper.readFileToString(c) );
			ret.setContent(  FileUtilWrapper.readFileToString(s) );
			ret.setSource(  source );
			ret.setType(  type );
			ret.setTimeStamp(  Long.parseLong(FileUtilWrapper.readFileToString(t)) );
			
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
		
		return ret;
	
	}
	
}
