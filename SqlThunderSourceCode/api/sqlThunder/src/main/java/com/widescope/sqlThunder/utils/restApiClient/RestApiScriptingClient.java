package com.widescope.sqlThunder.utils.restApiClient;

import java.util.Objects;

import com.widescope.logging.AppLogger;
import com.widescope.scripting.ScriptDetail;
import com.widescope.sqlThunder.config.AppConstants;
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
import com.widescope.sqlThunder.rest.RestObjectShort;
import com.widescope.scripting.ScriptingReturnObject;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

public class RestApiScriptingClient {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	/**
	 *
	 * @param toBaseUrl
	 * @param user
	 * @param session
	 * @param internalUser
	 * @param internalPassword
	 * @param scriptName
	 * @param interpreter
	 * @param requestId
	 * @param scriptContent
	 * @param timeStamp
	 * @return
	 */
	public static ScriptingReturnObject
	runNodeAdhocScript(final String toBaseUrl,
					   final String user,
					   final String session,
					   final String internalUser,
					   final String internalPassword,
					   final String scriptName,
					   final String interpreter,
					   final String requestId,
					   final String scriptContent,
					   final long timeStamp
					   ) {
		String endPoint = toBaseUrl + "/scripting/adhoc/node/script:run";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("requestId", requestId);
			headers.add("internalUser", internalUser);
			headers.add("internalPassword", internalPassword);
			headers.add("scriptName", scriptName);
			headers.add("interpreter", interpreter );
			headers.add("senderBaseUrl", ClusterDb.ownBaseUrl);
			headers.add("timeStamp", String.valueOf(timeStamp) );
			HttpEntity<String> entity = new HttpEntity<String>(scriptContent, headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if(restObject.hasBody()) {
				return ScriptingReturnObject.toScriptingReturnObject(Objects.requireNonNull(restObject.getBody()).getPayload());
			}
			return new ScriptingReturnObject(requestId, "N");
			
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return new ScriptingReturnObject(requestId, "N");
		}
	}


	/**
	 * To send an repo script/project execution request to node
	 * @param toBaseUrl
	 * @param user
	 * @param session
	 * @param appC
	 * @param scriptInfo
	 * @param requestId
	 * @param filePath
	 * @return
	 */
	public static ScriptingReturnObject
	runNodeRepoScript(final String toBaseUrl,
					  final String user,
					  final String session,
					  final AppConstants appC,
					  final ScriptDetail scriptInfo,
					  final String requestId,
					  final String filePath) {

		String endPoint = toBaseUrl + "/scripting/repo/node/script:run";
		
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("internalUser", appC.getUser());
			headers.add("internalPassword", appC.getUserPasscode());
			headers.add("mainFileName", scriptInfo.getMainFile());
			headers.add("scriptName", scriptInfo.getScriptName());
			headers.add("interpreterId", String.valueOf(scriptInfo.getInterpreterId()));
			headers.add("requestId", requestId);
			headers.add("baseUrl", ClusterDb.ownBaseUrl);
			byte[] content = FileUtilWrapper.readFile(filePath);
						
	        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
	        ContentDisposition contentDisposition = ContentDisposition
										                .builder("form-data")
										                .name("file")
										                .filename("file")
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
			return new ScriptingReturnObject(requestId, "N");
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return new ScriptingReturnObject(requestId, "N");
		}
	}


	public static void
	updateGateSink(final String user,
				   final String session,
				   final String requestId,
				   final String baseUrl,
				   final ScriptingReturnObject obj) throws Exception {
		String endPoint = baseUrl + "/scripting/node/sink:data";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("user", user);
			headers.add("session", session);
			headers.add("requestId", requestId);
			headers.add("fromBaseUrl", ClusterDb.ownBaseUrl);
			HttpEntity<ScriptingReturnObject> entity = new HttpEntity<>(obj, headers);
			ResponseEntity<RestObjectShort> restObject = restTemplate.postForEntity(endPoint, entity, RestObjectShort.class);
			if( Objects.requireNonNull(restObject.getBody()).getErrorCode() != 0 ) {
				throw new Exception("Error updateGateSink: " + Objects.requireNonNull(restObject.getBody()));
			}


		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			throw ex;
		}
	}


	public static void
	loopbackScriptStdin(final String baseUrl,
						final String user, 
						final String session,
						final String internalUser,
						final String internalPassword,
						final String requestId,
						final String websocketMessageType,
						final String line) throws Exception {
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
			if( Objects.requireNonNull(restObject.getBody()).getErrorCode() != 0 ) {
				throw new Exception("Error updateGateSink: " + Objects.requireNonNull(restObject.getBody()));
			}
			
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			throw ex;
		}
	}




	
}
