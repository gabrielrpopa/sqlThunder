package com.widescope.sqlThunder.config;


import com.google.gson.Gson;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.sqlThunder.utils.restApiClient.RestApiWebSocket;
import com.widescope.webSockets.userStreamingPortal.WebSocketsWrapper;
import com.widescope.webSockets.userStreamingPortal.objects.payload.WebsocketPayload;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.messaging.support.ChannelInterceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.security.Principal;
import java.util.Map;



//
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/", "/queue1/", "/queue2/");
        config.setApplicationDestinationPrefixes("/app");
        System.out.println("Configuring Websocket Queues");
    }



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .addInterceptors(new HttpHandshakeInterceptor())
                .setHandshakeHandler(new DefaultHandshakeHandler() {

                    @Override
                    protected Principal determineUser(@NotNull ServerHttpRequest request,
                                                      @NotNull WebSocketHandler wsHandler,
                                                      @NotNull Map<String, Object> attributes) {
                        HttpHeaders headers = request.getHeaders();
                        Principal principal = request.getPrincipal();
                        if (principal == null) {
                            principal = new AnonymousPrincipal();
                            String uniqueName = UUID.randomUUID().toString();
                            ((AnonymousPrincipal) principal).setName(uniqueName);
                        }
                        return principal;
                    }
                }).setAllowedOrigins("http://localhost:4200", "https://sqlThunder.ca");

        registry.addEndpoint("/websocket")
                .addInterceptors(new HttpHandshakeInterceptor())
                .setHandshakeHandler(new DefaultHandshakeHandler() {

                    @Override
                    protected Principal determineUser(@NotNull ServerHttpRequest request,
                                                      @NotNull WebSocketHandler wsHandler,
                                                      @NotNull Map<String, Object> attributes) {
                        HttpHeaders authHeader = request.getHeaders();
                        Principal principal = request.getPrincipal();
                        if (principal == null) {
                            principal = new AnonymousPrincipal();
                            String uniqueName = UUID.randomUUID().toString();
                            ((AnonymousPrincipal) principal).setName(uniqueName);
                        }
                        return principal;
                    }
                }).setAllowedOrigins("http://localhost:4200", "https://sqlThunder.ca").withSockJS();

    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(1024 * 1024)
                .setSendBufferSizeLimit(1024 * 1024)
                .setSendTimeLimit(15 * 1000);
        WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registry);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {System.out.println("Configuring Websocket Queues");
        System.out.println("configureClientInboundChannel");

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                assert accessor != null;
                String payload = "";

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("X-Authorization");
                }

                if (StompCommand.SEND.equals(accessor.getCommand())) {

                    try{
                        payload =  new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                    } catch(Exception ex) {
                        System.out.println("##################s: " + ex.getMessage());
                    }


                    boolean ret = sendCommandToPartyUsers(accessor.getMessageHeaders(), payload);
                    System.out.println("COMMAND registration.interceptors: " + accessor.getCommand() + ", "+ ret);
                }


                return message;
            }
        });

        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);

    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(auctionHandler(), "/websocket/**").addInterceptors(auctionInterceptor());
    }

    @Bean
    public HandshakeInterceptor auctionInterceptor() {
        return new HandshakeInterceptor() {
            public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                           @NotNull ServerHttpResponse response,
                                           @NotNull WebSocketHandler wsHandler,
                                           @NotNull Map<String, Object> attributes) throws Exception {
                /*Do nothing here*/
                return true;
            }

            public void afterHandshake(@NotNull ServerHttpRequest request,
                                       @NotNull ServerHttpResponse response,
                                       @NotNull WebSocketHandler wsHandler,
                                       Exception exception) {
                /*Do nothing here*/

            }
        };
    }

    @Bean
    public WebSocketHandler auctionHandler() {
        return new TextWebSocketHandler() {

            @Override
            public void handleTextMessage(@NotNull WebSocketSession session,
                                          @NotNull TextMessage message)  {
                /*Do nothing here*/
            }


        };
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024 * 1024);
        container.setMaxBinaryMessageBufferSize(1024 * 1024);
        container.setMaxSessionIdleTimeout(2048L * 2048L);
        container.setAsyncSendTimeout(2048L * 2048L);
        return container;
    }





    private boolean sendCommandToPartyUsers(final org.springframework.messaging.MessageHeaders m, final String payload)  {
        try {
            JSONParser parser = new JSONParser();
            String nativeHeaders = new Gson().toJson(m.get("nativeHeaders"));

            if(!nativeHeaders.contains("destination") || !nativeHeaders.contains("wType") ) {
                System.out.println("SOCKET Error, Cannot find destination or wType.  Bailing out: " + nativeHeaders + " !");
                return false;
            }
            JSONObject nativeHeadersObj = (JSONObject) parser.parse(nativeHeaders);
            String destinationJson = new Gson().toJson(nativeHeadersObj.get("destination"));
            JSONArray destinationArray = (JSONArray) parser.parse(destinationJson);
            String destination = destinationArray.get(0).toString();
            if(destination.compareToIgnoreCase("/user/queue/typing") != 0 &&
                    destination.compareToIgnoreCase("/user/queue/control") != 0 &&
                    destination.compareToIgnoreCase("/user/queue/audio") != 0 &&
                    destination.compareToIgnoreCase("/user/queue/video") != 0
                ) {
                System.out.println("Websocket destination is not allowed (typing/control/audio/video) . Bailing out");
                return false;
            }

            String toJson = new Gson().toJson(nativeHeadersObj.get("to"));
            JSONArray toArray = (JSONArray) parser.parse(toJson);
            String to = toArray.get(0).toString();


            if( !WebSocketsWrapper.isUser( to)) {
                System.out.println("Destination user is not on-line: " + to );
                return false;
            }


            String fromJson = new Gson().toJson(nativeHeadersObj.get("from"));
            JSONArray fromArray = (JSONArray) parser.parse(fromJson);
            String from = fromArray.get(0).toString();


            String wTypeJson = new Gson().toJson(nativeHeadersObj.get("wType"));
            JSONArray wTypeArray = (JSONArray) parser.parse(wTypeJson);
            String wType = wTypeArray.get(0).toString();



            String callComposition = "";
            if(nativeHeadersObj.get("composition")!=null ) {
                String callCompositionJson = new Gson().toJson(nativeHeadersObj.get("composition"));
                JSONArray callCompositionArray = (JSONArray) parser.parse(callCompositionJson);
                callComposition = callCompositionArray.get(0).toString();
            }


            String status = "";
            if(nativeHeadersObj.get("status")!=null) {
                String statusJson = new Gson().toJson(nativeHeadersObj.get("status"));
                JSONArray statusArray = (JSONArray) parser.parse(statusJson);
                status = statusArray.get(0).toString();
            }

            String isEncrypted = "";
            if(nativeHeadersObj.get("isEncrypted")!=null) {
                String isEncryptedJson = new Gson().toJson(nativeHeadersObj.get("isEncrypted"));
                JSONArray isEncryptedArray = (JSONArray) parser.parse(isEncryptedJson);
                isEncrypted = isEncryptedArray.get(0).toString();
            }



            final WebsocketPayload theMessage = new WebsocketPayload("", from, to, wType, payload, ClusterDb.ownBaseUrl);
            theMessage.setCallComposition(callComposition);
            theMessage.setStatus(status);
            theMessage.setIsEncrypted(isEncrypted);

            if(destination.compareToIgnoreCase("/user/queue/control") == 0) {
                System.out.println("Control command: " + theMessage.toString());
            }

            return RestApiWebSocket.sendToSpecificUser(theMessage, ClusterDb.ownBaseUrl, destination);
        } catch(Exception ex) {
            System.out.println("sendCommandToPartyUsers Error : " +  ex.getMessage());
            return false;
        }
    }

}