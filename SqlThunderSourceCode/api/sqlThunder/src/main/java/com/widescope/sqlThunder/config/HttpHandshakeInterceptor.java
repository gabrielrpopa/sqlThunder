package com.widescope.sqlThunder.config;
import com.widescope.sqlThunder.StaticApplicationProperties;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor, ChannelInterceptor {

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                   @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler,
                                   @NotNull Map<String, Object> attributes) throws Exception {




        if (request instanceof ServletServerHttpRequest servletRequest) {
            java.net.URI uri = servletRequest.getURI();
            String staticIPAndPort = StaticApplicationProperties.staticIp + ":" + StaticApplicationProperties.localHttpPort;
            String localHost = StaticApplicationProperties.localHost + ":" + StaticApplicationProperties.localHttpPort;
            if(!uri.getAuthority().equals(localHost) && !uri.getAuthority().equals(staticIPAndPort)) {
                return false;
            }

            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("HTTP sessionId: ", session.getId());
        } else if (request instanceof ServletWebSocketHandlerRegistration) {
            System.out.println("FYI WebSocket principal: " + request.getPrincipal());
        } else {
            System.out.println("FYI, Neither HTTP nor websocket?" );
            return false;
        }
        return true;
    }

    public void afterHandshake(@NotNull ServerHttpRequest request,
                               @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler,
                               Exception ex) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession();
            System.out.println("sessionId: " + session.getId());
        } else if (request instanceof ServletWebSocketHandlerRegistration) {
            System.out.println(" FYI Principal: " + request.getPrincipal());
        } else {
            System.out.println("Neither HTTP nor websocket?" );
        }

    }




    @Override
    public Message<?> preSend(@NotNull Message<?> message,
                              @NotNull MessageChannel channel) {
        System.out.println("after handshake: " + message);
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
