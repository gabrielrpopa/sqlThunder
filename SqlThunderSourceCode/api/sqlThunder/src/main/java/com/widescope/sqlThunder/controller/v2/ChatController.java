package com.widescope.sqlThunder.controller.v2;


import com.google.firebase.messaging.*;

import com.widescope.chat.ChatConfirmation;
import com.widescope.chat.db.*;
import com.widescope.chat.db.persistence.ChatPersistence;
import com.widescope.chat.db.persistence.UserPairValue;
import com.widescope.chat.fileService.MessageMetadataList;
import com.widescope.chat.users.UserToChat;
import com.widescope.chat.users.UserToChatList;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.tcpServer.TCPCommands;
import com.widescope.sqlThunder.utils.firebase.FirebaseWrapper;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.WebsocketMessageType;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

import static com.widescope.webSockets.userStreamingPortal.objects.WebsocketPayloadKey.messageType;


@CrossOrigin
@RestController
@Schema(title = "Chat Controller")
@RequestMapping(value = "/chat")
public class ChatController {


    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private AppConstants appConstants;

    @Autowired
    private ChatDb chatDb;

    @Autowired
    private ChatPersistence chatPersistence;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final FirebaseMessaging fcm;

    public ChatController(FirebaseMessaging fcm) {
        this.fcm = fcm;
    }

    @PostConstruct
    public void initialize() {

    }

    /*The message is sent via websockets to the other user, no saving in memory*/
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/text:send", method = RequestMethod.PUT)
    @Operation(summary = "Send a message to a particular user")
    public ResponseEntity<RestObject>
    sendMessageToUserWithText(  @RequestHeader(value="requestId") String requestId,
                                @RequestHeader(value="fromUser") String fromUser,
                                @RequestHeader(value="fromId") String fromId,
                                @RequestHeader(value="toUser") String toUser,
                                @RequestHeader(value="toId") String toId,
                                @RequestHeader(value="message") String message,
                                @RequestHeader(value="isEncrypt") String isEncrypt) {

        long timestampN = InMemoryChat.getMillisecondsSinceEpoch();
        try	{
            ChatMessage chatMessage = new ChatMessage(message, fromUser, Long.parseLong(fromId), toUser, Long.parseLong(toId), timestampN, isEncrypt);
            chatMessage.setReadCounter(2);
            boolean isWebSent = sendTextNotificationToUser(fromUser, Long.parseLong(fromId), requestId, toUser, Long.parseLong(toId), message, timestampN);
            boolean isTCPSent =  TCPCommands.sendTcpMessageToUserWithText(chatMessage, requestId);
            String isDelivered = String.valueOf(isWebSent || isTCPSent);
            String isSaved = chatPersistence.addMessage(chatDb, appConstants, chatMessage, requestId, isDelivered, chatMessage.toString().length(),toUser );
            List<String> devices = authUtil.getUserDevices(toUser);
            for(String deviceToken: devices) {
                FirebaseWrapper.sendToToken(deviceToken, message, fcm);
                FirebaseWrapper.sendToToken(deviceToken, message);
            }
            ChatConfirmation cc = new ChatConfirmation(timestampN, isDelivered, isSaved);
            return RestObject.retOKWithPayload(cc, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{ 
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
        return RestObject.retOKWithPayload(ChatConfirmation.getChatConfirmationFail(timestampN), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }


    /*The message notification is sent via websockets to the other user, which in turn pulls it from server, once notification is received*/
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/attachments:send", method = RequestMethod.PUT)
    @Operation(summary = "Send a message to a particular user")
    public ResponseEntity<RestObject>
    sendMessageToUserMultipart( @RequestHeader(value="requestId") String requestId,
                                @RequestHeader(value="fromUser") String fromUser,
                                @RequestHeader(value="fromId") String fromId,
                                @RequestHeader(value="toUser") String toUser,
                                @RequestHeader(value="toId") String toId,
                                @RequestHeader(value="message") String message,
                                @RequestHeader(value="isEncrypt") String isEncrypt,
                                @RequestParam(value="filesMetadata") String fMetadata,
                                @RequestParam(value ="files", required = false) MultipartFile[] files)	{

        long timestamp = InMemoryChat.getMillisecondsSinceEpoch();
        try	{
            MessageMetadataList metadataList = MessageMetadataList.toMessageMetadataList(fMetadata);
            ChatMessage chatMessage = new ChatMessage(  message,
                                                        files,
                                                        metadataList,
                                                        fromUser,
                                                        Long.parseLong(fromId),
                                                        toUser, Long.parseLong(toId) ,
                                                        timestamp,
                                                        isEncrypt,
                                                        requestId,
                                                        2);

            InMemoryChat.addChatRecord(fromUser, toUser, requestId, chatMessage);
            boolean isWebSent =  sendMultipartNotificationToUser(fromUser, Long.parseLong(fromId), requestId, toUser, Long.parseLong(toId), message, timestamp, isEncrypt);
            boolean isTCPSent =  TCPCommands.sendTcpMessageToUserMultipart(chatMessage, requestId);
            String isDelivered = String.valueOf(isWebSent || isTCPSent);
            String isSaved = chatPersistence.addMessage(chatDb, appConstants, chatMessage, requestId, isDelivered, chatMessage.toString().length(), toUser);
            ChatConfirmation cc = new ChatConfirmation(timestamp, isDelivered, isSaved);
            return RestObject.retOKWithPayload(cc, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }

        return RestObject.retOKWithPayload(ChatConfirmation.getChatConfirmationFail(timestamp), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }






    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/message:get", method = RequestMethod.POST)
    @Operation(summary = "Get a multipart message after receiving notification")
    public ResponseEntity<RestObject>
    getMessageToUser(  @RequestHeader(value="fromUser") String fromUser,
                       @RequestHeader(value="toUser") String toUser,
                       @RequestHeader(value="requestId") String requestId,
                       @RequestBody ChatRecord chatRecord)	{

        try	{
            ChatMessage cm = InMemoryChat.getChatRecord(fromUser, toUser, requestId); /*This must be the reverse order*/
            if(cm == null) { /*If not in mem anymore lets search in MongoDB*/
                if(chatRecord == null || chatRecord.getMongoUniqueName()== null || chatRecord.getMongoUniqueName().isEmpty()) {
                    cm = chatPersistence.getMessageFromStore(chatDb, appConstants, fromUser, toUser, requestId);
                }
                else {
                    cm = chatPersistence.getMessageFromStore_(chatDb, appConstants, chatRecord);
                }
            } else {
                InMemoryChat.deleteChatRecord(fromUser, toUser, requestId); /*delete the message*/
            }
            return RestObject.retOKWithPayload(cm, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
        long timestampN = InMemoryChat.getMillisecondsSinceEpoch();

        ChatMessage t = new ChatMessage(null, fromUser, -1, toUser, -1, timestampN, "N");
        return RestObject.retOKWithPayload(t, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/messages/new:get", method = RequestMethod.POST)
    @Operation(summary = "Get a list of messages from a specific user if any")
    public ResponseEntity<RestObject>
    getNewMessagesFromUser(  @RequestHeader(value="requestId") String requestId,
                             @RequestHeader(value="fromUser") String fromUser,
                             @RequestHeader(value="toUser") String toUser,
                             @RequestHeader(value="fromDate") String fromDate)	{
        try	{
            List<ChatMessage> lstFromStore = chatPersistence.getNewMessagesFromStore(chatDb,appConstants, fromUser, toUser, Long.parseLong(fromDate));
            return RestObject.retOKWithPayload(new ChatMessageList(lstFromStore), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }

        List<ChatMessage> lstFromStore = new ArrayList<>();
        return RestObject.retOKWithPayload(new ChatMessageList(lstFromStore), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/server/time:get", method = RequestMethod.GET)
    @Operation(summary = "Get server time in milliseconds since EPOCH")
    public ResponseEntity<RestObject>
    getServerTime(  @RequestHeader(value="requestId") String requestId) 	{
       long timestampN = InMemoryChat.getMillisecondsSinceEpoch();
       return RestObject.retOKWithPayload(new GenericResponse(String.valueOf(timestampN)), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }



    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/toUser/messages/new:get", method = RequestMethod.POST)
    @Operation(summary = "Get a list of users sending messages to user")
    public ResponseEntity<RestObject>
    getUsersWithOutstandingMessages(@RequestHeader(value="requestId") String requestId,
                                    @RequestHeader(value="toUser") String toUser) {
        try	{
            List<UserPairValue> lstFromStore = chatPersistence.getListOfUsersWithNewMessages(chatDb,appConstants, toUser);
            return RestObject.retOKWithPayload(new UserPairValueList(lstFromStore), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }

        return RestObject.retOKWithPayload(new UserPairValueList(new ArrayList<>()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }



    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/message:set", method = RequestMethod.POST)
    @Operation(summary = "Set transfer status for a message(received or read)")
    public ResponseEntity<RestObject>
    setTransferFlag(@RequestHeader(value="requestId") String requestId,
                    @RequestBody ChatRecord chatRecord)	{
        try	{
            ChatRecord ret = chatPersistence.setReadMessageStatus(chatDb, appConstants, chatRecord);
            return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }

        return RestObject.retOKWithPayload(chatRecord, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());

    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/message:delete", method = RequestMethod.POST)
    @Operation(summary = "Delete message permanently")
    public ResponseEntity<RestObject>
    deleteMessage(@RequestHeader(value="requestId") String requestId,
                  @RequestBody ChatRecord chatRecord) {
        try	{
            ChatRecord ret = chatPersistence.deleteMessage(chatDb, appConstants, chatRecord);
            /*send notification to all parties that this message is no longer available*/
            sendNotificationDeleteMessage(ret);
            return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }

        return RestObject.retOKWithPayload(chatRecord, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());

    }



    /*Used by clients when login for the first time*/
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser:get", method = RequestMethod.POST)
    @Operation(summary = "Get unread message list for a particular user")
    public ResponseEntity<RestObject>
    getUnreadMessageList(@RequestHeader(value="requestId") String requestId,
                         @RequestHeader(value="fromUser") String fromUser,
                         @RequestHeader(value="toUser") String toUser,
                         @RequestHeader(value="fromDate") String fromDate) {
        try	{
            ChatRecordList ret = chatPersistence.getUnreadMessageList(chatDb, fromUser, toUser, Long.parseLong(fromDate));
            return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
        long timestampN = InMemoryChat.getMillisecondsSinceEpoch();

        ChatMessage t = new ChatMessage(null, fromUser, -1, toUser, -1, timestampN, "N");
        return RestObject.retOKWithPayload(t, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());

    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/history:get", method = RequestMethod.GET)
    @Operation(summary = "Get a multipart message after receiving notification")
    public ResponseEntity<RestObject>
    getHistMessageList(@RequestHeader(value="user") String user,
                       @RequestHeader(value="requestId") String requestId,
                       @RequestHeader(value="fromUser") String fromUser,
                       @RequestHeader(value="fromDate") String fromDate,
                       @RequestHeader(value="toDate") String toDate)	{
        try	{
            ChatRecordList ret = chatPersistence.getMessageHistList(chatDb, fromUser, user, Long.parseLong(fromDate), Long.parseLong(toDate));
            return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        } catch(Throwable ex)	{
            AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
        long timestampN = InMemoryChat.getMillisecondsSinceEpoch();

        ChatMessage t = new ChatMessage(null, fromUser, -1, user, -1, timestampN, "N");
        return RestObject.retOKWithPayload(t, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());

    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toUser/count:get", method = RequestMethod.GET)
    @Operation(summary = "Check if freshly connected user has any new messages")
    public ResponseEntity<RestObject>
    getCountUnreadMessages( @RequestHeader(value="user") String user,
                            @RequestHeader(value="requestId") String requestId) {


        long count;
        try	{
            count = chatPersistence.getUreadMessagesCount(chatDb, user);
        } catch(Exception ex)	{
            return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), ex.getMessage(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        } catch(Throwable ex)	{
            return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }
        return RestObject.retOKWithPayload(String.valueOf(count), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }



    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/users:query", method = RequestMethod.POST)
    @Operation(summary = "Get All available users")
    public ResponseEntity<RestObject>
    searchChatUsers(@RequestHeader(value="user") String user,
                    @RequestHeader(value="requestId") String requestId,
                    @RequestHeader(value="patternToSearch") String patternToSearch) {
        try {
            UserToChatList uList = chatPersistence.searchChatUsers(user, patternToSearch, authUtil);
            return RestObject.retOKWithPayload(uList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex) {
            return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), ex.getMessage(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        } catch(Throwable ex)	{
            return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }

    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/fromUser/toGroup:send", method = RequestMethod.PUT)
    @Operation(summary = "Send a message to a particular user")
    public ResponseEntity<RestObject>
    sendMessageToGroup( @RequestHeader(value="fromUser") String fromUser,
                        @RequestHeader(value="fromId") String fromId,
                        @RequestHeader(value="session") String session,
                        @RequestHeader(value="toGroup") String toGroup,
                        @RequestHeader(value="toGroupId") String toGroupId,
                        @RequestHeader(value="requestId") String requestId,
                        /*@RequestParam("files") MultipartFile[] files,*/
                        @RequestHeader(value="message") String message) {

        long timestamp = InMemoryChat.getMillisecondsSinceEpoch();
        String isSaved = "N";
        String isDelivered = "N";
        try	{
            ChatMessage chatMessage = new ChatMessage(message, fromUser, Long.parseLong(fromId), toGroup, Long.parseLong(toGroupId), timestamp, "N");
            chatMessage.setIsGroup(true);
            ChatConfirmation cc = new ChatConfirmation(timestamp, isDelivered, isSaved);
            return RestObject.retOKWithPayload(cc, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex)	{
            return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        } catch(Throwable ex)	{
            return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }
    }






    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/user:get", method = RequestMethod.POST)
    @Operation(summary = "Get a specific Chat User")
    public ResponseEntity<RestObject>
    getChatUser(@RequestHeader(value="requestId") String requestId,
                @RequestHeader(value="userName") String userName) {
        try {
            /*Fix this to also use InternalUsersPersistenceRef*/
            UserToChat ret =  chatPersistence.getUserToChat(userName, authUtil);
            return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex) {
            return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        } catch(Throwable ex)	{
            return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }

    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/user:add", method = RequestMethod.POST)
    @Operation(summary = "Get Users minus current")
    public ResponseEntity<RestObject>
    addChatUser(@RequestHeader(value="user") String user,
                @RequestHeader(value="fromId") String fromId,
                @RequestHeader(value="toId") String toId,
                @RequestHeader(value="toUser") String toUser,
                @RequestHeader(value="requestId") String requestId) {

        try {
            chatPersistence.addChatGroup(chatDb, authUtil, Long.parseLong(fromId), user, Long.parseLong(toId), toUser);
            GenericResponse response =  new GenericResponse("OK");
            return RestObject.retOKWithPayload(response, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex) {
            return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        } catch(Throwable ex)	{
            return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }

    }



    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/users:chat", method = RequestMethod.POST)
    @Operation(summary = "Get Users in the chat")
    public ResponseEntity<RestObject>
    getChatUsers(@RequestHeader(value="requestId") String requestId,
                 @RequestHeader(value="fromUser") String fromUser) {

        try {
            UserToChatList uList =  chatPersistence.getUserToChatList(chatDb, fromUser, authUtil);
            return RestObject.retOKWithPayload(uList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            List<User> u = new ArrayList<>();
            UserToChatList uList = UserToChatList.populate(u);
            return RestObject.retOKWithPayload(uList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch(Throwable ex)	{
            return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }

    }



    private boolean sendTextNotificationToUser(   final String fromUser,
                                                  final long fromId,
                                                  final String requestId,
                                                  final String toUser,
                                                  final long toId,
                                                  final String message,
                                                  final long timeStampN) {

        if( !WebSocketsWrapper.isUser(toUser)) {
            return false;
        }


        ChatMessage chatMessage = new ChatMessage(  message, fromUser, fromId,  toUser, toId, timeStampN, "N");
        WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
                                                            fromUser,
                                                            toUser,
                                                            WebsocketMessageType.chatUserToUser,
                                                            chatMessage,
                                                            ClusterDb.ownBaseUrl);


        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setNativeHeader("fromUser", fromUser);
        accessor.setNativeHeader("fromId", String.valueOf(fromId) );
        accessor.setNativeHeader("toUser", toUser);
        accessor.setNativeHeader("toId", String.valueOf(toId) );
        accessor.setNativeHeader("messageType", messageType);
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        String userName = WebSocketsWrapper.getUserName(toUser).trim();
        try {
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/update", wsPayload, headers);
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }

        return true;
    }



    private
    boolean
    sendMultipartNotificationToUser(final String fromUser,
                                    final long fromId,
                                    final String requestId,
                                    final String toUser,
                                    final long toId,
                                    final String message,
                                    final long timestampN,
                                    final String isEncrypt)  {

        if( WebSocketsWrapper.isUser(toUser)) {
            return false;
        }

        ChatMessage chatMessage = new ChatMessage(  message, fromUser, fromId, toUser, toId, timestampN, isEncrypt );
        WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
                                                            fromUser,
                                                            toUser,
                                                            WebsocketMessageType.chatUserToUser,
                                                            chatMessage,
                                                            ClusterDb.ownBaseUrl);


        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setNativeHeader("fromUser", fromUser);
        accessor.setNativeHeader("fromId", String.valueOf(fromId) );
        accessor.setNativeHeader("toUser", toUser);
        accessor.setNativeHeader("toId", String.valueOf(toId) );
        accessor.setNativeHeader("requestId", requestId);
        accessor.setNativeHeader("messageType", messageType);
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        String userName = WebSocketsWrapper.getUserName(toUser).trim();
        try {
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/multipart", wsPayload, headers);
            System.out.println("Sending notification from " + fromUser + " " + toUser + " via /queue/multipart");
            return true;
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return false;
        }


    }




    private
    void
    sendNotificationToUser(final String fromUser,
                            final long fromId,
                            final String requestId,
                            final String toUser,
                            final long toId,
                            final String operation,
                            final ChatRecord chatRecord) {

        WebsocketPayload wsPayload = new WebsocketPayload(  requestId,
                                                            fromUser,
                                                            toUser,
                                                            operation, /*WebsocketMessageType.chatMessageDelete*/
                                                            chatRecord,
                                                            ClusterDb.ownBaseUrl);


        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setNativeHeader("fromUser", fromUser);
        accessor.setNativeHeader("fromId", String.valueOf(fromId) );
        accessor.setNativeHeader("toUser", toUser);
        accessor.setNativeHeader("toId", String.valueOf(toId) );
        accessor.setNativeHeader("requestId", requestId);
        accessor.setNativeHeader("messageType", messageType);
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        String userName = WebSocketsWrapper.getUserName(toUser).trim();
        try {
            simpMessagingTemplate.convertAndSendToUser(userName, "/queue/operation", wsPayload, headers);
            System.out.println("Sending notification from " + fromUser + " " + toUser + " via /queue/operation");
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
    }


    private void sendNotificationDeleteMessage(ChatRecord chatRecord) {
        try {
            if( WebSocketsWrapper.isUser(chatRecord.getToUser())) {
                sendNotificationToUser(chatRecord.getFromUser(), -1, chatRecord.getRequestId(), chatRecord.getToUser(), -1, WebsocketMessageType.chatMessageDelete, chatRecord);
            }

            if( WebSocketsWrapper.isUser(chatRecord.getToUser())) {
                sendNotificationToUser(chatRecord.getToUser(), -1, chatRecord.getRequestId(), chatRecord.getFromUser(), -1, WebsocketMessageType.chatMessageDelete, chatRecord);
            }
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
        }
    }

}
