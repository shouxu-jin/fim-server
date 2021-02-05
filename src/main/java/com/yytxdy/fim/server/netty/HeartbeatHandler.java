package com.yytxdy.fim.server.netty;

import com.yytxdy.fim.server.service.ChannelHolderService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
    @Autowired
    private ChannelHolderService channelHolderService;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("remove channel: " + ctx.channel().id());
                channelHolderService.remove(ctx.channel());
                ctx.channel().close();
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
