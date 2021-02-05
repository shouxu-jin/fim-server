package com.yytxdy.fim.server.service.protocolHandler;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.server.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifyResponseHandler implements ProtocolHandler {
    @Autowired
    private MessageService messageService;

    @Override
    public boolean support(Fim.Protocol.DataType dataType) {
        return Fim.Protocol.DataType.NotifyResponseType.equals(dataType);
    }

    @Override
    public void handler(Fim.Protocol protocol, ChannelHandlerContext ctx) {
        Fim.NotifyResponse response = protocol.getNotifyResponse();
        messageService.deleteOfflineMessage(response.getReceiverId(), response.getMessageId());
    }
}
