package com.widescope.scripting.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.widescope.logging.AppLogger;

import com.widescope.rdbmsRepo.database.tempSqlRepo.SaveFiles;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;




public class HistScriptFileManagement {

	public static String 
	addNewScript(	final String mainFolder,
					final String type,
					final String interpreter,
					final long userId, 
					final String scriptName,
					final String content, 
					final String comment) throws Exception {

		final String shaHash = StringUtils.getSHA256Hash(content, ConfigRepoDb.configValues.get("save-adhoc-algo").getConfigValue());
		
		if(shaHash == null) {
			return AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj,"Error generating SHA256 and therefore could not save script");
		}
		
		long timeStamp = DateTimeUtils.millisecondsSinceEpoch();
		final String folder = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash;
		
		File dir = new File(folder);
		dir.mkdirs();
		FileUtilWrapper.overWriteFile(	folder + "/s.txt", content);
		FileUtilWrapper.overWriteFile(	folder + "/c.txt", comment);
		FileUtilWrapper.overWriteFile(	folder + "/t.txt", String.valueOf(timeStamp) );
		return "Script has been saved for later use";
	}
	
	
	public static boolean 
	deleteScript(	final String mainFolder,
					final String type,
					final String interpreter,
					final long userId, 
					final String scriptName	)  {
		final String folder = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName ;
		File fld = new File(folder );
		if (fld.exists()) {
			return FileUtilWrapper.deleteDirectoryWithAllContent(folder);
		} else {
			return false;
		}
	}
	
	
	
	public static void 
	addExistingScriptToNewUser(	final long fromUserId,
								final String mainFolder,
								final String type,
								final String interpreter,
								final long toUserId, 
								final String scriptName,
								final String shaHash) {
		SaveFiles s = (	_mainFolder, _type, _interpreter, _toUserId, _scriptName, _shaHash ) -> {
					final String content = HistScriptFileManagement.getContent(_mainFolder, _type, _interpreter, fromUserId, _scriptName, _shaHash);
					final String comment = HistScriptFileManagement.getComment(_mainFolder, _type, _interpreter, fromUserId, _scriptName, _shaHash);
					try {
						HistScriptFileManagement.addNewScript(mainFolder, type, interpreter, toUserId, scriptName, content, comment);
					} catch (Exception e) {
						AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
						return false;
					}
					return true;
				};
		s.saveScript(mainFolder, type, interpreter, toUserId, scriptName, shaHash);
	}
	
	
	
	
	
	
	
	/*NOTE: MAKE SURE total length of folder is < 256 as SHAhash alone is 64*/
	
	
	public static HistScriptList 
	getScripts(	final String mainFolder,
				final String type,
				final String interpreter,
				final long userId) {
		HistScriptList ret = new HistScriptList();
		final String folder = mainFolder + "/" + type + "/" + interpreter + "/" + userId;
		try {
			String[] lst = FileUtilWrapper.getListFolders(folder);
            for (String s : lst) {

                HistoryScript p = new HistoryScript(userId, type, interpreter, s, new ArrayList<>());
                final String folderVer = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + s;
                String[] lstVer = FileUtilWrapper.getListFolders(folderVer);
                for (String string : lstVer) {
                    HistoryScriptVersion v =
                            getScriptVer(mainFolder,
                                    type,
                                    interpreter,
                                    userId,
                                    s,
                                    string
                            );
                    p.getHistScriptList().add(v);
                }

                ret.getScriptList().add(p);
            }
			return ret;
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return ret;
		}
	}
	
	
	
	
	
	
	
	public static String 
	getContent(final String mainFolder,
						final String type,
						final String interpreter,
						final long userId,
						final String scriptName,
						final String shaHash) {
		final String file = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash + "/s.txt" ;
		try {
			return FileUtilWrapper.readFileToString(file);
		} catch (IOException e) {
			return "";
		}
		
	}
	
	
	public static String 
	getComment(	final String mainFolder,
				final String type,
				final String interpreter,
				final long userId,
				final String scriptName,
				final String shaHash) {

		final String file = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash + "/c.txt" ;
		try {
			return FileUtilWrapper.readFileToString(file);
		} catch (IOException e) {
			return "";
		}
	
	}
	
	public static String 
	getTimeStamp(	final String mainFolder,
					final String type,
					final String interpreter,
					final long userId,
					final String scriptName,
					final String shaHash) {
		final String file = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash + "/t.txt" ;
		try {
			return FileUtilWrapper.readFileToString(file);
		} catch (IOException e) {
			return "";
		}
	}
	
	
	
	
	
	public static HistoryScriptVersion 
	getScriptVer(	final String mainFolder,
					final String type,
					final String interpreter,
					final long userId, 
					final String scriptName,
					final String shaHash) {
		HistoryScriptVersion ret = new HistoryScriptVersion(shaHash, null, null, -1);
		final String t = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash + "/t.txt" ;
		final String c = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash + "/c.txt" ;
		final String s = mainFolder + "/" + type + "/" + interpreter + "/" + userId + "/" + scriptName + "/" + shaHash + "/s.txt" ;
		try {
			ret.setComment(  FileUtilWrapper.readFileToString(c) );
			ret.setContent(  FileUtilWrapper.readFileToString(s) );
			ret.setTimeStamp(  Long.parseLong(FileUtilWrapper.readFileToString(t)) );
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		}
		
		return ret;
	
	}
	
}
