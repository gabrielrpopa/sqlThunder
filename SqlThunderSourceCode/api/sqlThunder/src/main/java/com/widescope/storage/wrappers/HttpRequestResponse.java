package com.widescope.storage.wrappers;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDbRecord;
import com.widescope.storage.dataExchangeRepo.ExchangeFileDbList;
import com.widescope.storage.dataExchangeRepo.ExchangeRecord;
import com.widescope.storage.dataExchangeRepo.FileDescriptor;
import com.widescope.storage.dataExchangeRepo.FileDescriptorList;

public class HttpRequestResponse {



	public static ExchangeFileDbList 
	queryFromRemoteExchange(final String exchangeAddress,
							final String externalUserEmail,
							final String externalUserPassword) {
		
		// request body parameters
		Map<String, String> headers = new HashMap<>();
		headers.put("externalUserEmail", externalUserEmail);
		headers.put("externalUserPassword", externalUserPassword);
		RestTemplate  restTemplate = new RestTemplate ();
        String resourceUrl = "exchangeAddress" + "/exchange/remote:query";
        // Fetch JSON response as String wrapped in ResponseEntity
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, headers, String.class);
        ExchangeFileDbList ret = new ExchangeFileDbList();
        if (response.getStatusCode() == HttpStatus.OK) {
        	String retJson = response.getBody();
        	return ExchangeFileDbList.toExchangeFileDbList(retJson);
        } else {
            return ret;
        }
    }
	
	
	
	public static Resource getTestFile(String path) throws IOException {
        return new FileSystemResource(path);
    }
	
	
	public static ResponseEntity<RestObject> 
	uploadFileFromRemoteExchange(	String requestURL,
									FileDescriptorList fileDescriptorList,
									Map<String, String> headersMap) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        for (Map.Entry<String, String> set : headersMap.entrySet()) {
        	headers.add(set.getKey(), set.getValue());
		}
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for(FileDescriptor  fileDescriptor  :fileDescriptorList.getFileDescriptorList()) {
        	body.add("file", getTestFile(fileDescriptor.getPath()));
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(requestURL, requestEntity, RestObject.class);
    }
	
	
	
	public static ResponseEntity<Resource> 
	downloadFileFromRemoteExchange(	String requestURL,
									Map<String, String> headersMap) throws IOException {
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        for (Map.Entry<String, String> set : headersMap.entrySet()) {
        	headers.add(set.getKey(), set.getValue());
		}
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.execute(requestURL, HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
        return null;
    }
	

	
}
