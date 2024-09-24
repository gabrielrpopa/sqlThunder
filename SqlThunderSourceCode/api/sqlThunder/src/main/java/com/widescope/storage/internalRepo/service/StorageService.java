package com.widescope.storage.internalRepo.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Service
public interface StorageService {

	public String uploadFolderPath = "../../upload";
	public String uploadFolderRoot = "../..";
	public String uploadFolderUpload = "upload";
	public String uploadFolderTemp = "temp";

	void init() throws Exception;

	String store(MultipartFile file, String machineName, String subfolder, String filename, long lastModified) throws Exception;
	boolean delete(String machineName, String subfolder, String filename, long lastModified) throws Exception;
	boolean isFile(String machineName, String subfolder, String filename, long lastModified);
	String getFilePath(String machineName, String subfolder, String filename, long lastModified);
	InputStreamResource download(String appName, String subfolder, String filename) throws Exception;
	Stream<Path> loadAll() throws Exception;
	Path load(String appName, String subFolder, String filename) throws Exception;
	Resource loadAsResource(String appName, String subfolder, String filename) throws Exception;
	void deleteAll() throws Exception;
	List<String> listFiles(String appName, String subfolder) throws Exception;
	
	
	/*TempFiles*/
	
	String storeTmp(MultipartFile file, String filename) throws Exception;
	boolean isTmpFile(String filename);
	String getTmpFilePath(String filename);
	String getTmpFolderPath(String filename);
	boolean deleteTmp(String filename);
	

}
