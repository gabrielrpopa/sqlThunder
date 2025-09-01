package com.widescope.sqlThunder.tcpServer;

import com.widescope.sqlThunder.tcpServer.types.*;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class SimpleChatServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private AuthUtil authUtil;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Tcp connection coming from : " + ctx.channel().remoteAddress().toString() + ", channel:" + ctx.channel().id());
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        System.out.println("Tcp Channel package is coming from : " + ctx.channel().remoteAddress().toString() + ", channel:" + ctx.channel().id());
        if( checkIfHttpRequest(msg.toString()) ) {
            JSONError error = JSONError.makeJSONError(1, "User not Authenticated, please try again");
            JSONResponse tError = JSONResponse.genericErrorMessage(error);
            System.out.println("Replied to socket.io: " + tError.toStringPretty());
            ctx.writeAndFlush( JSONResponse.getWithTerminator(tError.toString() ) );
            return;
        }
        processRequest(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Caught Exception TCP message handling: " + cause.getMessage());
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        System.out.println("Tcp Channel is Inactive, closed socket: " + ctx.channel().remoteAddress().toString()); /*remove ctx.channel()*/
        ChannelRepository.closeChannel(ctx.channel());
    }


    private boolean checkIfHttpRequest(String message) {
        return message.contains("/socket.io/") || message.contains("Connection: keep-alive");
    }

    private void processRequest(ChannelHandlerContext ctx, Object msg) {
        try {
            JSONRequest jsonRequest = JSONRequest.toJSONRequest(msg.toString());
            if(jsonRequest == null) {
                throw new RuntimeException("JSONRequest is null");
            }

            if(jsonRequest.getParams() == null || jsonRequest.getT() == null || jsonRequest.getT().isEmpty() || jsonRequest.getS() == null || jsonRequest.getS().isEmpty()) {
                throw new RuntimeException("JSONRequest is null");
            }
            System.out.println("Received: " + jsonRequest.toStringPretty());
            /*Workflow entry point*/
            if(jsonRequest.getT().compareToIgnoreCase(TcpMessageType.TcpTypeLogin) == 0) {  /*Login*/
                TcpLogin tcpLogin = TcpLogin.toTcpLogin(jsonRequest.getParams().toString());
                if(tcpLogin == null) {
                    throw new RuntimeException("Object is null");
                }
                if( !authUtil.isSessionAuthenticated(tcpLogin.getUserName(), tcpLogin.getSessionId()) ) {
                    /*The user was not authenticated prior to this transaction via the Login API */
                    JSONError error = JSONError.makeJSONError(1, "User not Authenticated, please try again");
                    JSONResponse tError = JSONResponse.genericErrorMessage(error);
                    System.out.println("Replied: " + tError.toStringPretty());
                    ctx.writeAndFlush( JSONResponse.getWithTerminator(tError.toString() ) );
                } else {
                    /*User fully authenticated, save channel and user */
                    ChannelRepository.openChannel(ctx.channel(), tcpLogin.getUserName());
                    JSONResponse ret = TcpMessageParser.getMessage(jsonRequest);
                    System.out.println("Replied: " + ret.toStringPretty());
                    ctx.writeAndFlush(JSONResponse.getWithTerminator(ret.toString()));
                }
            } else { /*regular data after login*/
                /* Check if channel was authenticated*/
                if(!ChannelRepository.isChannelApproved(ctx.channel())) { /* Check is channel was authenticated*/
                    throw new RuntimeException("Not approved");
                }
                JSONResponse ret = TcpMessageParser.getMessage(jsonRequest);
                System.out.println("Replied: " + ret.toStringPretty());
                ctx.writeAndFlush(JSONResponse.getWithTerminator( ret.toString() ) );

            }
        } catch(ClassCastException ex) {
            throw new RuntimeException("JSONRequest is null");
        }
    }


}