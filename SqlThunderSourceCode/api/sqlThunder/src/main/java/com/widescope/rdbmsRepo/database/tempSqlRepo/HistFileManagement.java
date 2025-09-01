package com.widescope.rdbmsRepo.database.tempSqlRepo;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQuery;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQuery;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.scripting.db.ScriptExecutedRecord;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.storage.dataExchangeRepo.ExchangeFileDbRecord;
import com.widescope.storage.internalRepo.BackupStorage;
import com.widescope.storage.internalRepo.InternalFileStorageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class HistFileManagement {

	@Autowired
	private AppConstants appConstants;

	public String
	addNewArtifact(final RestInterface stm, Object output) throws Exception {
		String folder = generateFolder(stm);
		final String genFileName = StringUtils.generateUniqueString(32);
		boolean isOk = FileUtilWrapper.overwriteFile(folder, genFileName, output.toString());
		return isOk ? folder + "/" + genFileName: null;
	}

	public String
	addNewArtifactFromMultipartFile(final RestInterface stm, MultipartFile file) throws Exception {
		String folder = generateFolder(stm);
		final String genFileName = StringUtils.generateUniqueString(32);
		Path destinationPath = Paths.get(folder, genFileName);
		Files.createDirectories(destinationPath.getParent());
		long size = Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		return size > 0 ? folder + "/" + genFileName: null;
	}

	public void
	deleteArtifact(final RestInterface stm) throws Exception {
		boolean isOk = true;
		String fullPath = getFullPath(stm);
		if(FileUtilWrapper.isFilePresent(fullPath)) {
			isOk = FileUtilWrapper.deleteFile(fullPath);
		}
		if(!isOk) {
			throw new Exception("Artifact file could not be deleted");
		}
	}

	public void
	deleteFolderWithContent(final RestInterface stm) throws Exception {
		boolean isOk = true;
		String fullPath = getFullPath(stm);
		if(FileUtilWrapper.isFilePresent(fullPath)) {
			isOk = FileUtilWrapper.deleteDirectoryWithAllContent(fullPath);
		}
		if(!isOk) {
			throw new Exception("Backup Folder could not be deleted");
		}
	}





	public String
	getFullPath(final RestInterface stm) throws Exception {
		String fullPath;
		if(stm instanceof RdbmsExecutedQuery) {
			fullPath = ((RdbmsExecutedQuery) stm).getRepPath();
		} else if(stm instanceof ElasticExecutedQuery)  {
			fullPath =  ((ElasticExecutedQuery) stm).getRepPath() ;
		} else if(stm instanceof MongoExecutedQuery)  {
			fullPath =  ((MongoExecutedQuery) stm).getRepPath() ;
		} else if(stm instanceof ScriptExecutedRecord)  {
			fullPath = ((ScriptExecutedRecord) stm).getRepPath();
		} else if(stm instanceof BackupStorage)  {
			fullPath = ((BackupStorage) stm).getRepPath();
		} else if(stm instanceof InternalFileStorageRecord)  {
			fullPath = ((InternalFileStorageRecord) stm).getFullFilePath();
		} else if(stm instanceof ExchangeFileDbRecord)  {
			fullPath = ((ExchangeFileDbRecord) stm).getFullFilePath();
		} else {
			throw new Exception("Unknown RestInterface passed to HistFileManagement.getFullPath: " + stm.toString());
		}
		return fullPath;
	}



	public String
	generateFolder(final RestInterface stm) throws Exception {
		String folder = null;
		if(stm instanceof RdbmsExecutedQuery) {
			folder = appConstants.getStoragePath() + Constants.rdbmsFolder + "/" + ((RdbmsExecutedQuery) stm).getSource() + "/" + ((RdbmsExecutedQuery) stm).getUserId() + "/" + ((RdbmsExecutedQuery) stm).getRequestId() ;
		} else if(stm instanceof ElasticExecutedQuery)  {
			folder = appConstants.getStoragePath() + Constants.elasticFolder + "/" + ((ElasticExecutedQuery) stm).getSource() + "/" + ((ElasticExecutedQuery) stm).getUserId() + "/" + ((ElasticExecutedQuery) stm).getRequestId();
		} else if(stm instanceof MongoExecutedQuery)  {
			folder = appConstants.getStoragePath() + Constants.mongoFolder + "/" + ((MongoExecutedQuery) stm).getSource() + "/" + ((MongoExecutedQuery) stm).getUserId() + "/" + ((MongoExecutedQuery) stm).getRequestId();
		} else if(stm instanceof ScriptExecutedRecord)  {
			folder = appConstants.getStoragePath() + Constants.scriptFolder + "/" + ((ScriptExecutedRecord) stm).getSource() + "/" + ((ScriptExecutedRecord) stm).getUserId() + "/" + ((ScriptExecutedRecord) stm).getRequestId();
		} else if(stm instanceof BackupStorage)  {
			folder = appConstants.getStoragePath() + Constants.fileStorageFolder + "/" + ((BackupStorage) stm).getSource() + "/" + ((BackupStorage) stm).getUserId() + "/" + ((BackupStorage) stm).getMachineName()+ "/" + ((BackupStorage) stm).getRequestId();
		}  else if(stm instanceof ExchangeFileDbRecord)  {
			folder = appConstants.getStoragePath() + Constants.exchangeFolder + "/" + ((ExchangeFileDbRecord) stm).getSource() + "/" + ((ExchangeFileDbRecord) stm).getFromUserId() + "/" + ((ExchangeFileDbRecord) stm).getRequestId();
		}	else {
			throw new Exception("Unknown RestInterface passed to HistFileManagement.generateFolder: " + stm.toString());
		}
		return folder;
	}
}
