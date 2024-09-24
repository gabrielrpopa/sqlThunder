package com.widescope.sqlThunder.utils.restApiClient;

import java.io.File;
import java.util.List;

import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.utils.FileUtilWrapper;



public class ExchangeEndPointWrapper {
		
	/**
	 * Sending to uploadFileFromRemoteExchange  ("/exchange/file/receive:remote")
	 */
	public static ResponseEntity<RestObject>  
	uploadFilesToExternalExchange(	final String externalUserEmail, /*email of the sender*/
									final String externalExchangeUid, /*Senders's Exchange*/
									final String externalUserPassword, /*password sender*/
									final String toUserEmail,
									final String remoteExchangeUrl,
									final List<String> listOfPaths
									) throws Exception {

		HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
    	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    	headers.add("externalUserEmail", externalUserEmail);
    	headers.add("externalExchangeUid", externalExchangeUid);
    	headers.add("toUserEmail", toUserEmail);
    	headers.add("externalUserEmail", externalUserEmail);
    	MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for(String path: listOfPaths) {
        	File uploadFile = new File(path);
        	body.add("files", FileUtilWrapper.getFileContent(uploadFile.getAbsolutePath()));
        }
    	HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    	String serverUrl = remoteExchangeUrl + "/exchange/file/receive:remote";
    	RestTemplate restTemplate = new RestTemplate();
    	return restTemplate.postForEntity(serverUrl, requestEntity, RestObject.class);
    }
	
	
	/**
	 * Sending to uploadFileFromRemoteExchange  ("/exchange/file/receive:remote")
	 */
	public static ResponseEntity<RestObject> 
	uploadFilesToExternalExchange(	final String externalUserEmail, /*email of the sender*/
									final String externalExchangeUid, /*Senders's Exchange*/
									final String externalUserPassword, /*password sender*/
									final String toUserEmail,
									final String remoteExchangeUrl,
									final MultipartFile[] multipartFiles
									) throws Exception {
		HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("externalUserEmail", externalUserEmail);
		headers.add("externalExchangeUid", externalExchangeUid);
		headers.add("toUserEmail", toUserEmail);
		headers.add("externalUserEmail", externalUserEmail);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		for(MultipartFile file: multipartFiles) {
			body.add(file.getName(), file.getBytes());
		}
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		String serverUrl = remoteExchangeUrl + "/exchange/file/receive:remote";
		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate.postForEntity(serverUrl, requestEntity, RestObject.class);
    }
}
