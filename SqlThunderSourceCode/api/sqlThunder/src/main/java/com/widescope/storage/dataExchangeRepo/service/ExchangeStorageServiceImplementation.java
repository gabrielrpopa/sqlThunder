package com.widescope.storage.dataExchangeRepo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.widescope.storage.dataExchangeRepo.ExchangeFileDbRecord;
import kotlin.io.FileAlreadyExistsException;





@Service
public class ExchangeStorageServiceImplementation implements ExchangeStorageService {

	private Path rootLocation;

	@Override
	public void init() throws Exception {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}

	}
	
	
	
	@Override
	public String store(MultipartFile file, 
						final ExchangeFileDbRecord r,
						final String requestId) throws Exception {

		String folder = exchangeIn + "/" + r.getToUserId() + "/" + r.getFromUserId() + "/" + requestId;
		File dir = new File(folder);
		dir.mkdirs();

		try (InputStream inputStream = file.getInputStream()) {
			File theFile = new File(this.rootLocation.toAbsolutePath().toString());
			if (!theFile.exists()) {
				Files.copy(inputStream, this.rootLocation.toAbsolutePath());
			}
			return this.rootLocation.toAbsolutePath().toString();
		}
		catch (FileAlreadyExistsException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return this.rootLocation.toAbsolutePath().toString();
		}
		catch (Exception e) {
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Failed to store file " + r.getFileName())) ;
		}
	}

	@Override
	public boolean delete(final ExchangeFileDbRecord r, final String requestId) throws Exception {
		try {
			String folder = exchangeIn + "/" + r.getToUserId() + "/" + r.getFromUserId() + "/" + requestId;
			File file2Delete = new File(folder);
			return file2Delete.delete();
		} catch (Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}
	
	@Override
	public String getFilePath(final ExchangeFileDbRecord r, final String requestId) {
		String folder = exchangeIn + "/" + r.getToUserId() + "/" + r.getFromUserId() + "/" + requestId;
		return folder + "/" + r.getFileName();
	}
}
