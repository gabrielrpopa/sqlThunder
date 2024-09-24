package com.widescope.sqlThunder.controller.v2;


import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.payload.UserRegistrationSocket;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

import static com.widescope.webSockets.userStreamingPortal.objects.WebsocketPayloadKey.messageType;


@RestController
@Schema(title = "Web Socket Controller")
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private AuthUtil authUtil;



    @PostConstruct
    public void initialize() {

    }


    @MessageMapping("/chat.register") /*STOMP messages to be routed to @Controller*/
    public void
    subscribe(@Payload UserRegistrationSocket message,
              Principal user,
              @Header("simpSessionId") String sessionId) {

        WebSocketsWrapper.addUserSession(message.getEmail(), user.getName());
        UserRegistrationSocket u = new UserRegistrationSocket(message.getEmail(), user.getName());
        WebsocketPayload wsPayload = new WebsocketPayload("N/A", message.getEmail(), message.getEmail(), WebsocketMessageType.authenticate,u, "");
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        simpMessagingTemplate.convertAndSendToUser(user.getName(), "/queue/register", wsPayload, headers);
    }






    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/push/user/queue/update", method = RequestMethod.POST)
    @Operation(summary = "Push Data to user",	description= "Push Data to user")
    public boolean
    sendToIndividualUser(  @RequestBody WebsocketPayload message,
                           HttpServletRequest request)	{

        if(!ConfigRepoDb.isLocalHost(request) ) {
            return false;
        }

        try {
            String userName = WebSocketsWrapper.getUserName(message.getToUser()).trim();
            if(!userName.isEmpty()) {
                SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
                accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
                accessor.setLeaveMutable(true);
                MessageHeaders headers = accessor.getMessageHeaders();
                simpMessagingTemplate.convertAndSendToUser(userName, "/queue/update", message, headers);
                return true;
            } else {
                return false;
            }
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/push/user/queue/multipart", method = RequestMethod.POST)
    @Operation(summary = "Push Data to user with attachments")
    public boolean
    sendToIndividualUserMultipart( @RequestHeader(value="fromUser", required = true) String fromUser,
                                   @RequestHeader(value="fromId", required = true) String fromId,
                                   @RequestHeader(value="session", required = true) String session,
                                   @RequestHeader(value="toUser", required = true) String toUser,
                                   @RequestHeader(value="toId", required = true) String toId,
                                   @RequestHeader(value="message", required = true) String message,
                                   @RequestParam("files") List<MultipartFile> files,
                                   @RequestParam("filesMetadata") String fMetadata) 	{


        try {
            String userName = WebSocketsWrapper.getUserName(toUser).trim();
            if(userName.isEmpty()) { return false; }
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
            accessor.setContentType(MimeTypeUtils.ALL);
            accessor.setNativeHeader("fromUser", fromUser);
            accessor.setNativeHeader("fromId", fromId);
            accessor.setNativeHeader("toUser", toUser);
            accessor.setNativeHeader("toId", toId);
            accessor.setNativeHeader("messageType", messageType);
            accessor.setLeaveMutable(true);
            MessageHeaders headers = accessor.getMessageHeaders();
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/multipart", message, headers);
            return true;
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }



    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/user/queue/typing", method = RequestMethod.POST)
    @Operation(summary = "Push Data to user")
    public boolean
    sendTypingToIndividualUser(@RequestBody WebsocketPayload message)	{

        try {
            String userName = WebSocketsWrapper.getUserName(message.getToUser()).trim();
            if(userName.isEmpty()) { return false; }
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
            accessor.setLeaveMutable(true);
            MessageHeaders headers = accessor.getMessageHeaders();
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/typing", message, headers);
            return true;
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/user/queue/video", method = RequestMethod.POST)
    @Operation(summary = "Push Video Data to individual user")
    public boolean
    sendVideoStreamingToIndividualUser(@RequestBody WebsocketPayload message)	{

        try {
            String userName = WebSocketsWrapper.getUserName(message.getToUser()).trim();
            if(userName.isEmpty()) { return false; }
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
            accessor.setLeaveMutable(true);
            MessageHeaders headers = accessor.getMessageHeaders();
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/video", message, headers);
            return true;
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/user/queue/audio", method = RequestMethod.POST)
    @Operation(summary = "Push Sound Data to individual user")
    public boolean
    sendSoundStreamingToIndividualUser(@RequestBody WebsocketPayload message)	{

        try {
            String userName = WebSocketsWrapper.getUserName(message.getToUser()).trim();
            if(userName.isEmpty()) { return false; }
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
            accessor.setLeaveMutable(true);
            MessageHeaders headers = accessor.getMessageHeaders();
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/audio", message, headers);
            return true;
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/user/queue/control", method = RequestMethod.POST)
    @Operation(summary = "Push Command Data to Individual User")
    public boolean
    sendControlCommandToIndividualUser(@RequestBody WebsocketPayload message)	{

        try {
            String userName = WebSocketsWrapper.getUserName(message.getToUser()).trim();
            if(userName.isEmpty()) { return false; }
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
            accessor.setLeaveMutable(true);
            MessageHeaders headers = accessor.getMessageHeaders();
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/control", message, headers);
            return true;
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/timer", method = RequestMethod.POST)
    @Operation(summary = "50 milliseconds timer",	description= "Push timer to subscribed users, for sampling video and audio data")
    public boolean
    triggerTimerBeat() {
        simpMessagingTemplate.convertAndSend("/topic/timer", "");
        return true;
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    @Operation(summary = "Heartbeat")
    public boolean
    triggerHeartBeat() {
        simpMessagingTemplate.convertAndSend("/topic/heartbeat", "");
        return true;
    }







    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/push/topic/room/", method = RequestMethod.POST)
    @Operation(summary = "Push Data to all users")
    public boolean
    sendToAllUsers(@RequestBody WebsocketPayload message) {
        try {
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
            accessor.setLeaveMutable(true);
            MessageHeaders headers = accessor.getMessageHeaders();
            simpMessagingTemplate.convertAndSend("/topic/room/", message, headers);
            return true;
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }
    }

}
