package com.widescope.sqlThunder.utils.restApiClient;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNodeList;
import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.sqlThunder.rest.RestObject;

import java.util.Objects;


public class RestApiCluster {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	@Autowired
	RestTemplate restTemplate;

	private static SimpleClientHttpRequestFactory getClientHttpRequestFactory(final int maxConnTimeout,
																			  final int maxReadTimeout) {

		SimpleClientHttpRequestFactory clientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(maxConnTimeout);
		clientHttpRequestFactory.setReadTimeout(maxReadTimeout);
		return clientHttpRequestFactory;
	}



	public static String ping(	String serverBaseAddress)  {
		String pongEndPoint = serverBaseAddress + "/cluster/node/ping:pong";
		try	{
			RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory(1000, 1000));
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			HttpEntity<String> entity = new HttpEntity<>("{}", headers);
			return restTemplate.exchange(pongEndPoint, HttpMethod.GET, entity, String.class).getBody();
		} catch(Exception ex) {
			/*Uncomment below line to capture ping search*/
			//AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj) ;
			return null;
		}
	}
	
	public static MachineNode info(	String serverBaseAddress, final int id) throws Exception {
		String infoPoint = serverBaseAddress + "/cluster/node:info";
		try	{
			RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory(1000, 1000));
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			HttpEntity<String> entity = new HttpEntity<String>("{}", headers);
			MachineNode ret =  restTemplate.exchange(infoPoint, HttpMethod.GET, entity, MachineNode.class).getBody();
            assert ret != null;
            ret.setId(id);
			return ret;
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj) ;
			return new MachineNode();
		}
	}
	
	
	public static boolean 
	testAdminAccount(String serverBaseAddress, 
	                 String admin,
		             String adminPasscode) {
		String testAdminAccount = serverBaseAddress + "/test/adminAccount";
		
		try {
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Admin", admin);
			headers.add("Admin-Passcode", adminPasscode);
			HttpEntity<String> entity = new HttpEntity<>(null, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(testAdminAccount, HttpMethod.GET, entity, String.class);
			return Objects.requireNonNull(restObject.getBody()).equalsIgnoreCase("OK");
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj) ;
			return false;
		}
	}
	
	public static boolean 
	testUserAccount(String serverBaseAddress, 
					String user,
					String userPasscode) {
		String testUserAccount = serverBaseAddress + "/test/userAccount";
		
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("User", user);
			headers.add("User-Passcode", userPasscode);
			HttpEntity<String> entity = new HttpEntity<>(null, headers);
			
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> restObject = restTemplate.exchange(testUserAccount, HttpMethod.GET, entity, String.class);
			return Objects.requireNonNull(restObject.getBody()).equalsIgnoreCase("OK");
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj) ;
			return false;
		}
	}
	
	
	public static MachineNodeList 
	getLoggingNodes(final String baseUrl,
					final String admin, 
					final String adminPasscode ) {
		String endPoint = baseUrl + "/cluster/get";
		try {
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.GET, entity, RestObject.class);
            return (MachineNodeList) Objects.requireNonNull(restObject.getBody()).getPayload();
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj) ;
			return new MachineNodeList();
		}
	}
	
	public static boolean 
	deleteLoggingNode(	final String baseUrl,
						final String admin, 
						final String adminPasscode,
						final String serverList) {
		String endPoint = baseUrl + "/cluster/delete";
		try {
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			headers.add("Node-Id-List-Comma-Separated", serverList);
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.DELETE, entity, RestObject.class);
			
			if(HttpStatus.OK == restObject.getStatusCode())	{
				return true;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), Objects.requireNonNull(restObject.getBody()).getErrorMessage())) ;
			}
		} catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj) ;
			return false;
		}
	}
	
	
	public static boolean 
	addLoggingNode(	final String serverBaseAddress,
					final String context,
					final String admin, 
					final String adminPasscode,
					final String newLoggingNodeType,
					final String newLoggingNodeControllerId,
					final String newLoggingNodeBaseAddress,
					final String newLoggingNodeUser,
					final String newLoggingNodeUserPasscode,
					final String newLoggingNodeAdmin,
					final String newLoggingNodeAdminPasscode,
					final String forceAdd ) throws Exception {
		String endPoint = serverBaseAddress + "/cluster/add";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			headers.add("New-Logging-Node-Type", newLoggingNodeType);
			headers.add("New-Logging-Controller-Id", newLoggingNodeControllerId);
			
			headers.add("New-Logging-Node-Base-Address", newLoggingNodeBaseAddress);
			headers.add("New-Logging-Node-User", newLoggingNodeUser);
			headers.add("New-Logging-Node-User-Passcode", newLoggingNodeUserPasscode);
			headers.add("New-Logging-Node-Admin", newLoggingNodeAdmin);
			headers.add("New-Logging-Node-Admin-Passcode", newLoggingNodeAdminPasscode);
			headers.add("Force-Add", forceAdd);
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.PUT, entity, RestObject.class);
			
			if(HttpStatus.OK == restObject.getStatusCode())	{
				return true;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), Objects.requireNonNull(restObject.getBody()).getErrorMessage())) ;
			}
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	
	public static boolean 
	updateNode(	final String serverBaseAddress,
				final String context,
				final String admin, 
				final String adminPasscode,
				final String newLoggingNodeType,
				final String newLoggingNodeControllerId
				) throws Exception	{
		String endPoint = serverBaseAddress + "/cluster/update";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			headers.add("New-Logging-Node-Type", newLoggingNodeType);
			headers.add("New-Logging-Controller-Id", newLoggingNodeControllerId);
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.POST, entity, RestObject.class);
			
			if(HttpStatus.OK == restObject.getStatusCode())	{
				return true;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), Objects.requireNonNull(restObject.getBody()).getErrorMessage())) ;
			}
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	public static boolean 
	updateNodeType(	final String serverBaseAddress,
					final String context,
					final String admin, 
					final String adminPasscode,
					final String newLoggingNodeType
					) throws Exception
	{
		String endPoint = serverBaseAddress + "/cluster/updateType";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			headers.add("New-Logging-Node-Type", newLoggingNodeType);
			
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.POST, entity, RestObject.class);
			
			if(HttpStatus.OK == restObject.getStatusCode())	{
				return true;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), Objects.requireNonNull(restObject.getBody()).getErrorMessage())) ;
			}
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	
	public static boolean 
	updateNodeController(final String serverBaseAddress,
						 final String context,
						 final String admin, 
						 final String adminPasscode,
						 final String newLoggingNodeControllerId
						 ) throws Exception
	{
		String endPoint = serverBaseAddress + "/cluster/updateController";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			headers.add("New-Logging-Controller-Id", newLoggingNodeControllerId);
			
			
			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.POST, entity, RestObject.class);
			
			if(HttpStatus.OK == restObject.getStatusCode())	{
				return true;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), Objects.requireNonNull(restObject.getBody()).getErrorMessage())) ;
			}
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	
	public static boolean 
	addNodes(final String serverBaseAddress,
			 final String context,
			 final String admin, 
			 final String adminPasscode,
			 final String newLoggingNodeControllerId
			 ) throws Exception {
		String endPoint = serverBaseAddress + "/cluster/node/multiple:add";
		try	{
			HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("This-Node-Current-Admin", admin);
			headers.add("This-Node-Current-Admin-Passcode", adminPasscode);
			headers.add("New-Logging-Controller-Id", newLoggingNodeControllerId);

			HttpEntity<RestObject> entity = new HttpEntity<RestObject>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(endPoint, HttpMethod.POST, entity, RestObject.class);
			
			if(HttpStatus.OK == restObject.getStatusCode())	{
				return true;
			} else {
				throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), Objects.requireNonNull(restObject.getBody()).getErrorMessage())) ;
			}
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
	
	
	
	public int 
	pushInfo(	String host,
				int port,
				MachineNode machineNode,
				String user,
				String session) throws Exception {

		HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("user", user);
		headers.add("session", session);

		String healthCheckEndPoint = "http://" + host + ":" + port + "/SqlThunder" + "/cluster:add";
		try	{
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<RestObject> restObject = restTemplate.exchange(	healthCheckEndPoint, 
													HttpMethod.GET, 
													entity, 
													RestObject.class);
			return Objects.requireNonNull(restObject.getBody()).getErrorCode();
		} catch(Exception ex) {
			throw new Exception(AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj)) ;
		}
	}
		
}
