package com.yytxdy.fim.server.service.protocolHandler;

import com.yytxdy.fim.protocol.Fim;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

@Service
public class ReadReceiptRequestHandler implements ProtocolHandler {
    @Override
    public boolean support(Fim.Protocol.DataType dataType) {
        return Fim.Protocol.DataType.ReadReceiptRequestType.equals(dataType);
    }

    @Override
    public void handler(Fim.Protocol protocol, ChannelHandlerContext ctx) {

    }
}
