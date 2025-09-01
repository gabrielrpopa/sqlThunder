package com.widescope.sqlThunder.utils.restApiClient;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class RestApiUserClient {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	@Autowired
	RestTemplate restTemplate;
	
	
	
	public static boolean 
	cleanUp(String baseUrl,
	        String localSession) {
		
		String testAdminAccount = baseUrl + "/users:cleanup";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("session", localSession);
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(testAdminAccount, HttpMethod.POST, entity, String.class);
			return Objects.requireNonNull(restObject.getBody()).equalsIgnoreCase("OK");
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
		
	
	
}
