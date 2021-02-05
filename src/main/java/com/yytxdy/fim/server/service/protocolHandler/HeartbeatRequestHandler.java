package com.yytxdy.fim.server.service.protocolHandler;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.server.entity.OfflineMessage;
import com.yytxdy.fim.server.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeartbeatRequestHandler implements ProtocolHandler {
    @Autowired
    private MessageService messageService;

    @Override
    public boolean support(Fim.Protocol.DataType dataType) {
        return Fim.Protocol.DataType.HeartbeatRequestType.equals(dataType);
    }

    @Override
    public void handler(Fim.Protocol protocol, ChannelHandlerContext ctx) {
        Fim.HeartbeatRequest request = protocol.getHeartbeatRequest();
        Fim.HeartbeatResponse response = Fim.HeartbeatResponse.newBuilder().build();
        // 如果心跳要求同步消息 返回所有离线数据
        if (request.getSyncMessage()) {
            List<OfflineMessage> offlineMessages = messageService.listOfflineMessage(request.getUserId());
            response.getMessageIdList().addAll(offlineMessages.stream().map(o -> o.getMessageId()).collect(Collectors.toList()));
        }
        ctx.channel().writeAndFlush(response);
    }
}
