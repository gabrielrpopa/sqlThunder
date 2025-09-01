package com.widescope.sqlThunder.utils.restApiClient;

import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.payload.TimeStamp;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class RestApiWebSocket {


    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


    @Autowired
    private static SimpMessagingTemplate simpMessagingTemplate;

    public static boolean
    sendToControlQueue(final WebsocketPayload message,
                       final String baseUrl) {

        String controlQueue = baseUrl + "/user/queue/control";
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebsocketPayload> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(controlQueue, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in sendToControlQueue: " + controlQueue);
            }
            return ret;
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return false;
        }
    }

    public static boolean
    sendToRegisterQueue(final WebsocketPayload message,
                        final String baseUrl) {

        String sendToUserViaQueue2 = baseUrl + "/user/queue/register";
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebsocketPayload> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(sendToUserViaQueue2, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in sendToRegisterQueue: " + sendToUserViaQueue2);
            }
            return ret;
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return false;
        }
    }


    public static boolean
    sendToUpdateQueue(final WebsocketPayload message,
                      final String baseUrl) {

        String sendToUserViaQueue2 = baseUrl + "/push/user/queue/update";
        //System.out.println(sendToUserViaQueue2);
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebsocketPayload> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(sendToUserViaQueue2, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in sendToUpdateQueue: " + sendToUserViaQueue2);
            }
            return ret;
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return false;
        }
    }


    public static boolean
    sendToMultipartQueue(final String fromUser,
                         final String fromId,
                         final String session,
                         final String toUser,
                         final String toId,
                         final String message,
                         final MultipartFile[] files,
                         final String fMetadata) throws Exception {

        String sendToUserViaQueue = ClusterDb.ownBaseUrl + "/push/user/queue/multipart";
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("fromUser", fromUser);
            headers.add("fromId", fromId);
            headers.add("session", session);
            headers.add("toUser", toUser);
            headers.add("toId", toId);
            headers.add("message", message);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for(MultipartFile m: files) {
                body.add("files", m);
            }
            body.add("filesMetadata", fMetadata);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(sendToUserViaQueue, requestEntity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in sendToMultipartQueue: " + sendToUserViaQueue);
            }
            return ret;
        } catch(Exception ex) {
            throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
        }
    }



    public static boolean
    sendToSpecificUser(final WebsocketPayload message,
                             final String baseUrl,
                             final String queue) throws Exception {
        String specificUserQueue = baseUrl + queue;
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebsocketPayload> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(specificUserQueue, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in sendToSpecificUser: " + specificUserQueue);
            }
            return ret;
        } catch(Exception ex) {
            throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
        }
    }

    public static void
    triggerHeartBeat(final WebsocketPayload message) {
        String sendToUserViaQueue2 = ClusterDb.ownBaseUrl + "/heartbeat";
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebsocketPayload> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(sendToUserViaQueue2, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in triggerHeartBeat: " + sendToUserViaQueue2);
            }

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }
    }


    public static void
    triggerTimerToUsers()  {
        final TimeStamp message = new TimeStamp();
        String sendToUserViaTimerQueue = ClusterDb.ownBaseUrl + "/timer";
        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TimeStamp> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(sendToUserViaTimerQueue, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in triggerTimerToUsers#: " + sendToUserViaTimerQueue);
            }

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }
    }







    public static boolean
    sendToAllUsers(final WebsocketPayload message,
                   final String baseUrl) throws Exception {
        String sendToAllUsers = baseUrl + "/push/topic/room/";

        try	{
            HttpHeaders headers =  SpringSecurityWrapper.getAuthorizationUserHeaders();
            HttpEntity<WebsocketPayload> entity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            boolean ret = Boolean.TRUE.equals(restTemplate.postForEntity(sendToAllUsers, entity, Boolean.class).getBody());
            if (!ret) {
                System.out.println("Error in sendToAllUsers: " +sendToAllUsers);
            }
            return ret;
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            return false;
        }
    }

}
