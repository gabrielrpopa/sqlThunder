package com.widescope.sqlThunder.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class WebSocketClientHandler extends TextWebSocketHandler {

    @Override
    public void handleMessage(@NotNull WebSocketSession session,
                              WebSocketMessage<?> message) {
        String classMethodName = Thread.currentThread().getStackTrace()[1].getClassName()
                                    + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(classMethodName + " -> message: " + message);
        System.out.println(classMethodName + " -> payload: " + message.getPayload());
    }
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        String classMethodName = Thread.currentThread().getStackTrace()[1].getClassName()
                                    + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(classMethodName + " -> principal : " + session.getPrincipal());
    }
}
