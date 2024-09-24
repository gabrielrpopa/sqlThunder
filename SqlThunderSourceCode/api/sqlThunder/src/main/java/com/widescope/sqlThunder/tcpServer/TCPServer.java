package com.widescope.sqlThunder.tcpServer;


import com.widescope.logging.AppLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;


@RequiredArgsConstructor
@Component

public class TCPServer {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


    private final ServerBootstrap serverBootstrap;

    private InetSocketAddress tcpPort;

    private Channel serverChannel;

    public TCPServer(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
        this.tcpPort = new InetSocketAddress(0);
    }

    public void start(int port)  {
        try {
            this.tcpPort = new InetSocketAddress(port);
            ChannelFuture serverChannelFuture = serverBootstrap.bind(tcpPort).sync();
            AppLogger.logInfo(className, "start", AppLogger.obj, "TCP Raw Server is started : port " + tcpPort.getPort());
            serverChannel = serverChannelFuture.channel().closeFuture().sync().channel();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void stop() {
        if ( serverChannel != null ) {
            serverChannel.close();
            serverChannel.parent().close();
        }
    }


}
