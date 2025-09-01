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


package com.widescope.sqlThunder.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.widescope.logging.AppLogger;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNodeList;
import com.widescope.scripting.ScriptParam;
import org.springframework.web.multipart.MultipartFile;


public class FileUtilWrapper {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
	
	public static byte[]
	readFile(final String inputFile) throws IOException {
		long fileSize = new File(inputFile).length();
        byte[] allBytes = new byte[(int) fileSize];
        InputStream inputStream = new FileInputStream(inputFile);
        inputStream.read(allBytes);
        inputStream.close();
        return allBytes;
	}
	
	public static String 
	readFileToString(final String inputFile) throws IOException {
		long fileSize = new File(inputFile).length();
	    byte[] allBytes = new byte[(int) fileSize];
	    InputStream inputStream = new FileInputStream(inputFile);
	    inputStream.read(allBytes);
	    inputStream.close();
	    return new String(allBytes, StandardCharsets.UTF_8);
	}

	public static String
	readFileFromResToString(final String inputFile) throws IOException {
		Resource companyDataResource = new ClassPathResource(inputFile);
		File file = companyDataResource.getFile();
		return new String(Files.readAllBytes(file.toPath()));
	}

	
	public static void
	writeFile(	final String fullFileName, 
				final String content) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(fullFileName, true));
	    writer.append(content);
	    writer.close();
	}
	
	public static void
	overWriteFile(	final String fullFileName, 
					final String content) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(fullFileName, true));
	    writer.write(content);
	    writer.close();
	}
	
	public static boolean 
	writeFile(	final String fullFileName, 
				final byte[] content) throws IOException {
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(fullFileName), content);
	    return true;
	}

	/**
	 * Write to a new file with silent error handling
	 * @param fullFileName - full file path
	 * @param content - content of the file in text format
	 */
	public static void
	writeBufferedFile(final String fullFileName, final String content) {
		try( FileWriter fw = new FileWriter(fullFileName, true);  BufferedWriter bw = new BufferedWriter(fw);  PrintWriter pw = new PrintWriter(bw)) {
			pw.write(content + System.lineSeparator());
		} catch (IOException ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
		}
	}

	
	
	
	public static boolean
	createDirectory(final String path) {
		File file = new File(path);
		if(file.exists()) return true;
		return file.mkdirs();
	}
	
	
	public static boolean 
	deleteDirectoryWithAllContent(final String path) {
		File file = new File(path);
		if(file.exists()) {
			try {
				org.apache.commons.io.FileUtils.deleteDirectory(file);
				return true;
			} catch (IOException e) {
				AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
				return false;
			}
		}
		return false;
	}


	public static FileCharacteristicList
	getListOfFolders(final String folder) {
		if(!FileUtilWrapper.isFilePresent(folder)) { return new FileCharacteristicList(); }
		try {
			return new FileCharacteristicList(FileUtilWrapper.getListOfFilesFromFolder(folder));
		} catch (IOException e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new FileCharacteristicList();
		}
	}
		
	public static List<FileCharacteristic> 
	getListOfFilesExcluding(String fullFolderRelativePath, String fullRelativePathExcluding) throws IOException {
		String  fullFolderRelativePath_ = FileUtilWrapper.relativeToFullPath(fullFolderRelativePath);
		String  fullRelativePathExcluding_ = FileUtilWrapper.relativeToFullPath(fullRelativePathExcluding);
		List<FileCharacteristic> ret = new ArrayList<FileCharacteristic>();
		try (Stream<Path> stream = Files.walk(Paths.get(fullFolderRelativePath_))) {
		    stream.filter(Files::isRegularFile).forEach(
		    		x-> { 
						try {
							String  fullPathCurrent = FileUtilWrapper.relativeToFullPath(x.toString());
							if(!fullPathCurrent.equals(fullRelativePathExcluding_)) {
								String fileName = x.getFileName().toString();
								String relativePath = FileUtilWrapper.subtractPaths(fullPathCurrent, fullFolderRelativePath_);
								String y = "";
								if(relativePath.length() > fileName.length()) {
									y = relativePath.substring(0, relativePath.length() - fileName.length() - 1);
								} 
								
								FileCharacteristic fc = getFilePermissions(fullPathCurrent);
								fc.setRelativePath(y);
			    				ret.add(fc); 
			    			}
						} catch (IOException e) {
							AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
						}
		    			
		    		}
		    ); 
		}
		return ret;
	}
	
	
	public static List<FileCharacteristic> 
	getListOfFilesFromFolder(String folderRelativePath) throws IOException {
		String  folderRelativePath_ = FileUtilWrapper.relativeToFullPath(folderRelativePath);
		List<FileCharacteristic> ret = new ArrayList<FileCharacteristic>();
		try (Stream<Path> stream = Files.walk(Paths.get(folderRelativePath))) {
		    stream.filter(Files::isRegularFile).forEach(
		    		x-> { 
						try {
							String fullPathCurrent = x.toString();
							String fileName = x.getFileName().toString();
							String relativePath = FileUtilWrapper.subtractPaths(fullPathCurrent, folderRelativePath_);
							String y = "";
							if(relativePath.length() > fileName.length()) {
								y = relativePath.substring(0, relativePath.length() - fileName.length() - 1);
							} 
							
							FileCharacteristic fc = getFilePermissions(fullPathCurrent);
							fc.setRelativePath(y);
		    				ret.add(fc); 
						} catch (Exception e) {
							AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
						}
		    			
		    		}
		    ); 
		}
		return ret;
	}

	public static String[] 
	getListFolders(String path) throws IOException {
		File directoryPath = new File(path);
	    //List of all files and directories
	    return directoryPath.list() == null ? new String[0] : directoryPath.list();
	}

	public static String 
	getRelativePathFromTwoFullPaths(String fullPath, String basePath, String fileName) {
		String path = new File(basePath).toURI().relativize(new File(fullPath).toURI()).getPath();
		return path.substring(0, path.length() - fileName.length());
	}
	
	public static String 
	getFileExtension(String fullFilePath) {
	    if(fullFilePath==null || fullFilePath.isEmpty() || fullFilePath.isBlank()) {
	    	return null;
	    }
	    String fileName = new File(fullFilePath).getName();
	    int dotIndex = fileName.lastIndexOf('.');
	    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}
	
	public static String 
	getFileType(String fullFilePath) throws IOException {
		return java.nio.file.Files.probeContentType(Path.of(fullFilePath));
	}

	public static FileCharacteristic
	getFilePermissions(final String filePath)	{
		File file = new File(filePath);
		FileCharacteristic fc = new FileCharacteristic("Y");
		fc.setFileName(file.getName());
		fc.setFolderPath(file.getParent());
		fc.setAbsolutePath(file.getAbsolutePath());
		fc.setCanExecute((file.canExecute())? "Y":"N");
		fc.setCanRead((file.canRead())? "Y":"N");
		fc.setCanWrite((file.canWrite())? "Y":"N");
		fc.setIsFolder((file.isDirectory())? "Y":"N");
		fc.setIsFile((file.isFile())? "Y":"N");
		fc.setIsHidden((file.isHidden())? "Y":"N");
		fc.setRelativePath(getRelativePathFromTwoFullPaths(file.getAbsolutePath(), file.getParent(), file.getName()));
		Path thePath = Paths.get(filePath);
        BasicFileAttributes attr;
		try {
			fc.setCanonicalPath(file.getCanonicalPath());
			attr = Files.readAttributes(thePath, BasicFileAttributes.class);
			fc.setCreationTime(attr.creationTime().to(TimeUnit.MILLISECONDS));
			fc.setLastAccessTime(attr.lastAccessTime().to(TimeUnit.MILLISECONDS));
			fc.setLastModifiedTime(attr.lastModifiedTime().to(TimeUnit.MILLISECONDS));
			fc.setIsSymbolicLink((attr.isSymbolicLink())? "Y":"N");
			
			
			
			try { fc.setFileKey(attr.fileKey().toString()); }	catch (Exception ex) {fc.setFileKey("");}
			try { fc.setRoot(thePath.getRoot().toString()); }	catch (Exception ex) {fc.setRoot("");}
		} catch (Exception e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			fc.setFileKey("");
		}
		
		return fc;
		
	}

	public static List<FileCharacteristic> 
	getFolderContentRecursivelyWithAttr(final String folder) throws IOException {
		List<FileCharacteristic> lstOfFile = new ArrayList<>();
		Files.find( Paths.get(folder), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile() 
				                                                               || fileAttr.isSymbolicLink() 
				                                                               || fileAttr.isOther()).forEach( f-> {
		    String fileName = f.getParent().toString() + "\\" + f.getFileName().toString();
		    FileCharacteristic fc = getFilePermissions(fileName);
			lstOfFile.add(fc);				                                                            	   
			System.out.println(f.getParent().toString() + "\\" + f.getFileName().toString());
			});
		return lstOfFile;
	}
	
	public static List<String> 
	getFolderContentRecursivelyWithAttrForFolders_(final String folder) throws IOException {
		List<String> lstOfFile = new ArrayList<String>();
		Files.find( Paths.get(folder), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isDirectory() ).forEach( f-> {
		    String fileName = f.getParent().toString() + "\\" + f.getFileName().toString();
			lstOfFile.add(fileName);				                                                            	   
			System.out.println(f.getParent().toString() + "\\" + f.getFileName().toString());
			});
		return lstOfFile;
	}
	
	
	public static List<FileCharacteristic> 
	getFolderContentRecursivelyWithAttrForFolders(final String folder) throws IOException {
		List<FileCharacteristic> lstOfFile = new ArrayList<FileCharacteristic>();
		Files.find( Paths.get(folder), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isDirectory() ).forEach( f-> {
		    String fileName = f.getParent().toString() + "\\" + f.getFileName().toString();
		    FileCharacteristic fc = getFilePermissions(fileName);
			lstOfFile.add(fc);				                                                            	   
			System.out.println(f.getParent().toString() + "\\" + f.getFileName().toString());
			});
		return lstOfFile;
	}
	
	
	public static List<FileCharacteristic> 
	getFolderContentRecursively(final String folder) throws IOException {
		List<FileCharacteristic> lstOfFile = new ArrayList<FileCharacteristic>();
		//Path x = Paths.get(folder);
		Files.walk(Paths.get(folder)).filter(Files::isRegularFile).forEach(f->	
				{
					String fileName = f.getParent().toString() + "\\" + f.getFileName().toString();
				    FileCharacteristic fc = getFilePermissions(fileName);
					lstOfFile.add(fc);
					System.out.println(fileName);
				});
		
		return lstOfFile;
		
	}
	
	public static Set<String> 
	getFolders(final String folder) {
		try {
			File file = new File(folder);
			String[] directories = file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
            assert directories != null;
            return new HashSet<>(Arrays.asList(directories));
		}
		catch(Exception ex) {
			return new HashSet<>();
		}
		
		
	}
	
	
	
	
	
	public 
	static 
	boolean 
	createFile(final String fileName) {
		try	{
			File myObj = new File(fileName);
            return myObj.createNewFile();
        } catch(IOException ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
	    	return false;
	    }     
		
	}
	
	public 
	static 
	boolean 
	writeToFile(final String fileName, 
				final String txt) {
		try {
		      FileWriter myWriter = new FileWriter(fileName, true);
		      myWriter.write(txt);
		      myWriter.write("\n");
		      myWriter.flush();
		      myWriter.close();
		    } catch (IOException e) {
				AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
				return false;
		    }
		return true;
	}
	
	public 
	static 
	boolean 
	deleteFile(final String fileName) {
		File myObj = new File(fileName);
		if(myObj.exists())
        	return myObj.delete();
		else return true;
    }

	public static Path saveToTempFolder(MultipartFile file) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		Path tempPath = Paths.get(tempDir);
		if (!Files.exists(tempPath)) {
			Files.createDirectories(tempPath);
		}
		Path filePath = tempPath.resolve(Objects.requireNonNull(StringUtils.generateUniqueString()));
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		return filePath;
	}
	
	
	public 
	static 
	boolean 
	isFilePresent(final String fileName) {
		File myObj = new File(fileName);
        return myObj.exists();
    }
	
	
	
	
	public 
	static 
	boolean 
	deleteFolder(final String relativePath) {
		File myObj = new File(relativePath); 
	    try {
			FileUtils.deleteDirectory(myObj);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	
	public static boolean deleteFolder_(String folderName)
	{
		File myObj = new File(folderName);
        return myObj.delete();
	}
	
	
	public 
	static 
	String 
	relativeToFullPath(final String relativePath) throws IOException {
		File a = new File(relativePath);
		return a.getCanonicalPath();
	}
	

	public static String 
	subtractPaths(String longPath, String shortPath) {
		Path fromProjectRoot = Paths.get(longPath);
        Path projectRoot = Paths.get(shortPath);
        return projectRoot.relativize(fromProjectRoot).toString();
    }
	
	
	public 
	static 
	void
	createRecursiveFolder(final String recursiveFolder) {
		File theDir = new File(recursiveFolder);
		if(!theDir.exists()) {
			if (!theDir.mkdirs()) {
				AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Cannot create folder  " + recursiveFolder);
			}
		}
		else {
			AppLogger.logInfo(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Folder " + recursiveFolder + " already exists");
		}
	}

	public 
	static 
	boolean 
	isFolder(final String folderName) {
		File theDir = new File(folderName);
		return theDir.exists();
	}
	

	
	public 
	static 
	String 
	getFileContent(final String filePath) throws Exception {
		BufferedReader buf = null;
		try	{
			StringBuilder sb = new StringBuilder(); 
			InputStream is = new FileInputStream(filePath); 
			buf = new BufferedReader(new InputStreamReader(is)); 
			String line = buf.readLine(); 
			while(line != null) { 
				sb.append(line).append("\n"); 
				line = buf.readLine(); 
			}
			String fileAsString = sb.toString(); 
			System.out.println("Contents : " + fileAsString);
			return fileAsString;
		} catch(Exception e) {
			throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		} finally {
            assert buf != null;
            buf.close();
		}
	}
	
	
	
	
	
	public 
	static 
	boolean 
	overwriteFile(	final String folder,
					final String fileName,
					final String content) throws Exception {
		if(!isFolder(folder)) {
			Files.createDirectories(Paths.get(folder));
		}
		
		if( isFilePresent(folder + "/" + fileName) ) {
			deleteFile(folder + "/" + fileName);
		}

		return writeToFile(folder + "/" + fileName, content);

	}


	
	public 
	static 
	boolean 
	overwriteFile(	final String folder,
					final String fileName,
					final byte[] content) throws Exception {
		if(!isFolder(folder)) {
			Files.createDirectories(Paths.get(folder));
		}
		
		if( isFilePresent(folder + "/" + fileName) ) {
			deleteFile(folder + "/" + fileName);
		}

		FileUtils.writeByteArrayToFile(new File(folder + "\\" + fileName), content);
		return true;

	}
	

	
	
	public static void copyFolder(String source, String destination) throws IOException {
		try {
			File sourceDirectory = new File(source);
	        File destinationDirectory = new File(destination);
			FileUtils.copyDirectory(sourceDirectory, destinationDirectory);

        } catch (IOException e) {
			throw e;
        }
	}
	
	
	public static void replaceStringInFile(String filePath, Map<String, String> params ) throws IOException {
		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;

		String content = "";
		try {
			content = Files.readString(path, charset);
			for (Map.Entry<String,String> entry : params.entrySet()) {
	            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
	            content = content.replaceAll(entry.getKey(), entry.getValue());
			}
			Files.writeString(path, content, charset);
		} catch (IOException e) {
			throw e;
		}
	}
	
	
	public static void interpolateParamsInFiles(String folderPath, Map<String, String> params) throws IOException {
		List<FileCharacteristic> filePathList = new ArrayList<>();
        filePathList = getFolderContentRecursively(folderPath);
        for (FileCharacteristic fileCharacteristic : filePathList) {
            replaceStringInFile(fileCharacteristic.getCanonicalPath(), params);
        }

    }
	
	
	
	public static void replaceStringInFile(String filePath, List<ScriptParam> params) throws IOException {
		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;

		String content;
        content = new String(Files.readAllBytes(path), charset);
        for (ScriptParam entry : params) {
			System.out.println("Key = " + entry.getParamName() + ", Value = " + entry.getValue());
			content = content.replaceAll(entry.getParamName(), entry.getValue());
        }
        Files.writeString(path, content, charset);
    }
	
	
	public static void interpolateParams(String folderPath, List<ScriptParam> params) throws IOException {
		List<FileCharacteristic> filePathList = new ArrayList<>();
		try {
			filePathList = getFolderContentRecursively(folderPath);
            for (FileCharacteristic fileCharacteristic : filePathList) {
                replaceStringInFile(fileCharacteristic.getCanonicalPath(), params);
            }
		} catch (IOException e) {
			throw e;
		}
		
	}
	
	public static void interpolateParams(String folderPath,
										 String user,
										 String session, String requestId) throws IOException {
		Map<String, String> params = new HashMap<>();
		params.put("@user@", user);
		params.put("@session@", session);
		params.put("@request@", requestId);
		interpolateParamsInFiles(folderPath, params);
	}

	public static void interpolateParams(String folderPath,
										 String user,
										 String session,
										 String requestId,
										 String internalAdmin,
										 String internalAdminPasscode,
										 String http,
										 String host,
										 String port
	) throws IOException {
		Map<String, String> params = new HashMap<>();
		params.put("@user@", user);
		params.put("@session@", session);
		params.put("@request@", requestId);
		params.put("@internalAdmin@@", internalAdmin);
		params.put("@internalAdminPasscode@", internalAdminPasscode);
		params.put("@http@", http);
		params.put("@host@", host);
		params.put("@port@", port);
		interpolateParamsInFiles(folderPath, params);
	}
	
	
	public static String removeFileExtension(String filename, boolean removeAllExtensions) {
	    if (filename == null || filename.isEmpty()) {
	        return filename;
	    }

	    String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
	    return filename.replaceAll(extPattern, "");
	}

	public static void moveFile(String fullPathFrom, String pathTo) throws Exception {
		File file = new File(fullPathFrom);
        if(!file.renameTo(new File(pathTo))) {
			throw new Exception("Failed to move the file " + fullPathFrom);
		}
	}
	

	
	public static boolean 
	createFolder(String folderName)	{
		File theDir = new File(folderName);
		if (!theDir.exists()) {
		    System.out.println("Creating directory: " + theDir.getName());
			return theDir.mkdir();
		} else {
			return true; /*exists already*/
		}
	}
	
	

	public static void
	readMachineNodeListFile() {
		ClassPathResource  resource = new ClassPathResource("data/cluster.json");
		InputStream inputStream;
		try {
			inputStream = resource.getInputStream();
			byte[] bData = FileCopyUtils.copyToByteArray(inputStream);
		    String content = new String(bData, StandardCharsets.UTF_8);
		    MachineNodeList lst = MachineNodeList.toMachineNodeList(content);
		    ClusterDb c = new ClusterDb();
		    c.addNodes(lst);
		} catch (Exception e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
		}
	}



	
	
}
