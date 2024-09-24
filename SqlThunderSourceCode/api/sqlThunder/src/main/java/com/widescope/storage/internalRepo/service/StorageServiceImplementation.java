package com.widescope.storage.internalRepo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.widescope.logging.AppLogger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import kotlin.io.FileAlreadyExistsException;
import java.io.FileInputStream;



@Service
public class StorageServiceImplementation implements StorageService {
	private Path rootLocation;
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	@Override
	public void init() throws Exception {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new Exception (AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}

	}

	@Override
	public String store(MultipartFile file, 
						String machineName, 
						String subfolder, 
						String filename,
						long lastModified) throws Exception {

		try {
			
			String folder = uploadFolderRoot + "/" + uploadFolderUpload + "/" + machineName + "/" + subfolder + "/" + filename;
			File dir = new File(folder);
			dir.mkdirs();

			
			this.rootLocation = Paths.get(folder + "/" + lastModified);

			if (file.isEmpty()) {
				throw new Exception("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new Exception (AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Cannot store file with relative path outside current directory" + filename));
			}
			try (InputStream inputStream = file.getInputStream()) {
				File theFile = new File(this.rootLocation.toAbsolutePath().toString());
				if (!theFile.exists()) {
					Files.copy(inputStream, this.rootLocation.toAbsolutePath());
				}
				return this.rootLocation.toAbsolutePath().toString();
			}
			catch (FileAlreadyExistsException e) {
				return this.rootLocation.toAbsolutePath().toString();
			}
			catch (Exception e) {
				throw new Exception (AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
			}
		}
		catch (IOException e) {
			throw new Exception (AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}

	}

	@Override
	public Stream<Path> loadAll() throws Exception {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new Exception (AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Failed to read stored files"));
		}
	}

	@Override
	public Path load(String appName, String subFolder, String filename) throws Exception {
		this.rootLocation = Paths.get(uploadFolderRoot + "/" + uploadFolderUpload + "/" + subFolder);
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String appName, String subfolder, String filename) throws Exception {
		try {
			Path file = load(appName, subfolder, filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new Exception (AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Could not read file: " + filename));
			}
		} catch (MalformedURLException e) {
			throw new Exception (AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Could not read file: " + filename));
		}
	}

	@Override
	public void deleteAll() throws Exception {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public boolean delete(String machineName, String subfolder, String filename, long lastModified) throws Exception {
		boolean exists =  isFile(machineName, subfolder, filename, lastModified);
		if(!exists) return true;
		try {
			File file2Delete = new File(uploadFolderRoot + "/" + uploadFolderUpload + "/" + machineName + "/" + subfolder + "/" + filename + "/" + lastModified);
			return file2Delete.delete();
		}
		catch (Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	@Override
	public boolean isFile(String machineName, String subfolder, String filename, long lastModified) {
		File f = new File(uploadFolderRoot + "/" + uploadFolderUpload + "/" + machineName + "/" + subfolder + "/" + filename + "/" + lastModified);
		return f.exists();
	}
	
	@Override
	public String getFilePath(String machineName, String subfolder, String filename, long lastModified) {
		return uploadFolderRoot + "/" + uploadFolderUpload + "/" + machineName + "/" + subfolder + "/" + filename + "/" + lastModified;
	}

	@Override
	public InputStreamResource download(String appName, String subfolder, String filename) throws Exception {
		File file2Download = new File(uploadFolderRoot + "/" + uploadFolderUpload + "/" + appName + "/" + subfolder + "/" + filename);
        return new InputStreamResource(new FileInputStream(file2Download));
	}

	@Override
	public List<String> listFiles(String appName, String subfolder) throws Exception {
		List<String> lstOfFileToReturn = new ArrayList<String>();

		try (Stream<Path> walk = Files.walk(Paths.get(uploadFolderPath + "/" + appName + "/" + subfolder))) {
			List<String> lstOfFile = walk.filter(Files::isRegularFile).map(Path::toString).toList();
			for (String name : lstOfFile) {
				File f = new File(name);
				lstOfFileToReturn.add(f.getName());
			}
			return lstOfFileToReturn;
		}
		catch (IOException e) {
			throw new Exception(e);
		}
	}

	@Override
	public String storeTmp(MultipartFile file, String filename) throws Exception {
		boolean isFolder = false;
		try {

			File newRootFolder = new File(uploadFolderRoot, uploadFolderTemp);
			if (!newRootFolder.exists()) {
				isFolder = newRootFolder.mkdir();
				if (!isFolder) {
					throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl, "storeTmp not created: " + newRootFolder.getAbsolutePath()));
				}
			}
			
			this.rootLocation = Paths.get(uploadFolderRoot + "/" + uploadFolderTemp + "/" + filename );

			if (file.isEmpty()) {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl, "Failed to store empty file " + filename));
			}
			
			try (InputStream inputStream = file.getInputStream()) {
				File theFile = new File(this.rootLocation.toAbsolutePath().toString());
				if (!theFile.exists()) {
					Files.copy(inputStream, this.rootLocation.toAbsolutePath());
				}
				
				return this.rootLocation.toAbsolutePath().toString();
			}
			catch (Exception e) {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl, "Failed to store file " + filename));
			}
		}
		catch (IOException e) {
			throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl, "Failed to store file " + filename));
		}

	}
	
	@Override
	public boolean isTmpFile(String filename) {
		File f = new File(uploadFolderRoot + "/" + uploadFolderTemp + "/" + filename );
		return f.exists();
	}
	
	@Override
	public String getTmpFilePath(String filename) {
		return uploadFolderRoot + "/" + uploadFolderTemp + "/" + filename;
	}
	
	@Override
	public String getTmpFolderPath(String folderName) {
		return uploadFolderRoot + "/" + uploadFolderTemp + "/" + folderName;
	}
	
	
	@Override
	public boolean deleteTmp(String filename) {
		boolean exists =  isTmpFile(filename);
		if(!exists) return true;
		
		try {
			File file2Delete = new File(uploadFolderRoot + "/" + uploadFolderTemp + "/" + filename);
			file2Delete.delete();
		}
		catch (Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
		return !isTmpFile(filename);
	}
}
