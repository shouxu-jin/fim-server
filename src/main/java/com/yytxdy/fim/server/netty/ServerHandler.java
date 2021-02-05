package com.yytxdy.fim.server.netty;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.server.service.protocolHandler.ProtocolHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    private List<ProtocolHandler> protocolHandlers;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        Fim.Protocol protocol = (Fim.Protocol) msg;
        logger.info(protocol.toString());
        Fim.Protocol.DataType dataType = protocol.getDataType();
        for (ProtocolHandler handler : protocolHandlers) {
            if (handler.support(dataType)) {
                handler.handler(protocol, ctx);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("channelActive: " + ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("", cause);
    }
}
