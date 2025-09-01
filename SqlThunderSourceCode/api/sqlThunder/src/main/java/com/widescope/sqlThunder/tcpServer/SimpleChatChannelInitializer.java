package com.widescope.sqlThunder.tcpServer;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;
@Component

public class SimpleChatChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SimpleChatServerHandler simpleChatServerHandler;
    private final StringEncoder stringEncoder = new StringEncoder();
    private final StringDecoder stringDecoder = new StringDecoder();

    public SimpleChatChannelInitializer(SimpleChatServerHandler simpleChatServerHandler) {
        this.simpleChatServerHandler = simpleChatServerHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(stringDecoder);
        pipeline.addLast(stringEncoder);
        pipeline.addLast(simpleChatServerHandler);
    }
}