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



import com.widescope.logging.AppLogger;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import com.widescope.sqlThunder.utils.compression.ZipDirectory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.File;
import java.util.concurrent.TimeUnit;


public class MLApiUtils {


	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	
	public static Properties
	fetchProperties() throws Exception{
        Properties properties = new Properties();
        try {
            File file = ResourceUtils.getFile("classpath:application.properties");
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
	        throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
        return properties;
    }
	
	
	public static void
	runMlServer(String folder,
				String mlApiName,
				String server,
				String port,
				String user,
				String password) throws Exception{
		Process p = null;
		String mlApiPath = folder + "/" +mlApiName+ "/runserver.py";
		String mlApiCommand = folder + "/" +mlApiName+ "/runserver.py" + " --server=" + server + " --port=" + port + " --user=" + user + " --password=" + password;
		try {
			File script = new File(mlApiPath);
			if(script.exists()) {
				
				p = Runtime.getRuntime().exec("python " + mlApiCommand);
				
				
				Thread.sleep(30000);
				while(true) {
					boolean ret = ping("localhost" , Integer.parseInt(port));
					if(!ret) {
						break;
					}
					TimeUnit.SECONDS.sleep(5);
				}
				
				p.destroy();
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "Script Stopped Working")) ;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, mlApiPath + " does not exists")) ;
			}
		}
		catch(Exception e) {
			throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }
	
	
	public static void
	createNewMlApiMlServer(String zipSource, String folderDestination, String mlApiNameDestination) throws  Exception {
		try {
			Resource resourceSource = new ClassPathResource(zipSource);
			if(resourceSource.exists()) {
				InputStream content = resourceSource.getInputStream();
				FileUtilWrapper.createRecursiveFolder(folderDestination);
				String jarFile = folderDestination + "/" + mlApiNameDestination + ".zip";
				File targetFile = new File(jarFile);
			    FileUtils.copyInputStreamToFile(content, targetFile);
			    ZipDirectory.unzip(jarFile, folderDestination + "/" + mlApiNameDestination);
			} else {
				String fullPath = resourceSource.getFile().getAbsolutePath();
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, fullPath + " does not exists")) ;
			}
			
		}
		catch(Exception e) {
			throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }
	
	
	
	public List<String>
	getAllFiles() throws  Exception {
		List<String> lstOfFiles = new ArrayList<>();
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		try {
			Resource[] resources = resolver.getResources("classpath:*");
			for(Resource r: resources) {
				lstOfFiles.add(r.getFile().getAbsolutePath());
			}
		} catch (IOException e) {
			throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
		}
		return lstOfFiles;
    }
	
	
	
	public 
	static 
	boolean 
	ping(String mlApiUrl, int mlApiPort) {
		String pingEndPoint = mlApiUrl + ":" + mlApiPort + "/ping";
		try	{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("timestamp", StaticUtils.currentTimeAndDate());
			
			HttpEntity<String> entity = new HttpEntity<>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> restObject = restTemplate.exchange(pingEndPoint, HttpMethod.GET, entity, JSONObject.class);
			// "ping": "pong"
			String ping = Objects.requireNonNull(restObject.getBody()).get("ping").toString();
            return ping.toUpperCase().compareTo("PONG") == 0;
			
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	
	
	public String
	readFilesFromResource(String filePath) {
		StringBuilder sb = new StringBuilder();
		try (InputStream in = this.getClass().getResourceAsStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))) {
			String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append("\n");
	        }
		} catch (IOException e) {

			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
		return sb.toString();
		
	}
	
	
	public List<String>
	getResourceFiles(String path) {
	    List<String> filenames = new ArrayList<>();
	    try (
	    	InputStream in = getResourceAsStream(path);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
	        String resource;

	        while ((resource = br.readLine()) != null) {
	            filenames.add(resource);
	        }
	    } catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
	    }

	    return filenames;
	}
	
	private InputStream getResourceAsStream(String resource) {
	    final InputStream in
	            = getContextClassLoader().getResourceAsStream(resource);

	    return in == null ? getClass().getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
	    return Thread.currentThread().getContextClassLoader();
	}
	
	
	
	
}
