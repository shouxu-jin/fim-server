package com.yytxdy.fim.server.service.protocolHandler;

import com.yytxdy.fim.protocol.Fim;
import io.netty.channel.ChannelHandlerContext;

public interface ProtocolHandler {
    boolean support(Fim.Protocol.DataType dataType);

    void handler(Fim.Protocol protocol, ChannelHandlerContext ctx);
}
