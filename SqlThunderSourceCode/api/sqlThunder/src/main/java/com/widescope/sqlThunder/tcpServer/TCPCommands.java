package com.widescope.sqlThunder.tcpServer;

import com.widescope.chat.db.ChatMessage;
import com.widescope.sqlThunder.tcpServer.types.*;

public class TCPCommands {

    public static boolean sendTcpMessageToUserWithText(final ChatMessage chatMessage, final String id ) {
        JSONResponse tcpResponse = new JSONResponse(TcpMessageType.TcpTypeChatMessage,
                                                    TcpMessageSubType.TcpTypeChatMessageSubTypeTextOnly,
                                                    chatMessage.toStringPretty(),
                                                    TcpMessageType.jsonrpc,
                                                    id,
                                                    JSONError.makeJSONError(0, ""));

        return ChannelRepository.sendTcpMessageToUserChannel(chatMessage.getToUser(), tcpResponse);
    }

    public static boolean sendTcpMessageToUserMultipart(final ChatMessage chatMessage, final String id ) {
        JSONResponse tcpResponse = new JSONResponse(TcpMessageType.TcpTypeChatMessage,
                                                    TcpMessageSubType.TcpTypeChatMessageSubTypeTextOnly,
                                                    chatMessage.toStringPretty(),
                                                    TcpMessageType.jsonrpc,
                                                    id,
                                                    JSONError.makeJSONError(0, ""));

        return ChannelRepository.sendTcpMessageToUserChannel(chatMessage.getToUser(), tcpResponse);
    }

}
