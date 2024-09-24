package com.widescope.sqlThunder.utils.restApiClient;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.rest.RestObjectShort;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

public class RestApiScriptingClient {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	public static ScriptingReturnObject 
	runAdhocScriptViaNode(	final String toBaseUrl,
							final String user, 
							final String session,
							final String internalUser,
							final String internalPassword,
							final String scriptName,
							final int interpreterId,
							final String requestId,
							final String scriptContent) {
		String endPoint = toBaseUrl + "/scripting/script/adhoc/node:run";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("scriptName", scriptName);
			headers.add("interpreterId", String.valueOf(interpreterId) );
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			
			HttpEntity<String> entity = new HttpEntity<String>(scriptContent, headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return ScriptingReturnObject.toScriptingReturnObject(Objects.requireNonNull(restObject.getBody()).getPayload());
			}
			return new ScriptingReturnObject();
			
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return new ScriptingReturnObject();
		}
	}
	
	
	public static ScriptingReturnObject 
	runRepoScriptViaNodeMultipart(	final String toBaseUrl,
									final String user, 
									final String session,
									final String internalUser,
									final String internalPassword,
									final String mainFileName,
									final int interpreterId,
									final String requestId,
									final String fileName,
									final String filePath,
									final String baseFolder	) {
		String endPoint = toBaseUrl + "/scripting/script/repo/multipart/node:run";
		
		try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("mainFileName", mainFileName);
			headers.add("interpreterId", String.valueOf(interpreterId));
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			headers.add("baseFolder", baseFolder);
			
			byte[] content = FileUtilWrapper.readFile(filePath);
						
	        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
	        ContentDisposition contentDisposition = ContentDisposition
										                .builder("form-data")
										                .name("file")
										                .filename(fileName)
										                .build();
	        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
	        HttpEntity<byte[]> fileEntity = new HttpEntity<>(content, fileMap);

	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("file", fileEntity);
	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, requestEntity, RestObjectShort.class);
			
			if(restObject.hasBody()) {
				return ScriptingReturnObject.toScriptingReturnObject(Objects.requireNonNull(restObject.getBody()).getPayload());
			}
			return new ScriptingReturnObject();
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return new ScriptingReturnObject();
		}
	}
	
	
	
	public static String 
	loopbackScriptdataHeader(	final String toBaseUrl,
								final String user, 
								final String session,
								final String internalUser,
								final String internalPassword,
								final String requestId,
								final TableDefinition tDefinition)  {
		String endPoint = toBaseUrl + "/scripting/loopback/data:header";
		RestTemplate restTemplate = new RestTemplate();
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			HttpEntity<String> entity = new HttpEntity<String>(tDefinition.toString(), headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return Objects.requireNonNull(restObject.getBody()).getPayload();
			}
			return "ERROR";
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return "ERROR";
		}
	}
	
	
	
	public static String 
	loopbackScriptDataFooter(	final String baseUrl,
								final String user, 
								final String session,
								final String internalUser,
								final String internalPassword,
								final String requestId,
								final RowValue rValue)  {
		String endPoint = baseUrl + "/scripting/loopback/data:footer";
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			HttpEntity<String> entity = new HttpEntity<String>(rValue.toString(), headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return Objects.requireNonNull(restObject.getBody()).getPayload();
			}
			return "ERROR";
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return "ERROR";
		}
	}
	
	public static String 
	loopbackScriptDataDetail(	final String baseUrl,
								final String user, 
								final String session,
								final String internalUser,
								final String internalPassword,
								final String requestId,
								final RowValue rValue)  {
		String endPoint = baseUrl + "/scripting/loopback/data:detail";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			HttpEntity<String> entity = new HttpEntity<String>(rValue.toString(), headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return Objects.requireNonNull(restObject.getBody()).getPayload();
			}
			return "ERROR";
			
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return "ERROR";
		}
	}
	
	
	public static String 
	loopbackScriptDataDetails(	final String toBaseUrl,
								final String user, 
								final String session,
								final String internalUser,
								final String internalPassword,
								final String requestId,
								final List<RowValue> rValues) {
		String endPoint = toBaseUrl + "/scripting/loopback/data:details";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			HttpEntity<String> entity = new HttpEntity<String>(rValues.toString(), headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return Objects.requireNonNull(restObject.getBody()).getPayload();
			}
			return "ERROR";
			
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return "ERROR";
		}
	}
	
	
	
	public static String 
	loopbackScriptStdin(final String baseUrl,
						final String user, 
						final String session,
						final String internalUser,
						final String internalPassword,
						final String requestId,
						final String websocketMessageType,
						final String line) {
		String endPoint = baseUrl + "/scripting/loopback/log:stdin";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			headers.add("websocketMessageType", websocketMessageType);
			HttpEntity<String> entity = new HttpEntity<String>(line, headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return Objects.requireNonNull(restObject.getBody()).getPayload();
			}
			return "ERROR";
			
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return "ERROR";
		}
	}
	
}
