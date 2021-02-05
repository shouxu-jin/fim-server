package com.yytxdy.fim.server.service.protocolHandler;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.protocol.NotifyReceiverGrpc;
import com.yytxdy.fim.server.service.ChannelHolderService;
import com.yytxdy.fim.server.service.MessageService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class SendMessageRequestHandler implements ProtocolHandler {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ChannelHolderService channelHolderService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Value("${grpc.port}")
    private int grpcPort;

    @Override
    public boolean support(Fim.Protocol.DataType dataType) {
        return Fim.Protocol.DataType.SendMessageRequestType.equals(dataType);
    }

    @Override
    public void handler(Fim.Protocol protocol, ChannelHandlerContext ctx) {
        Fim.SendMessageRequest request = protocol.getSendMessageRequest();
        // 保存消息入库
        long messageId = messageService.save(request);
        // 返回通知到发送端
        Fim.SendMessageResponse response = Fim.SendMessageResponse.newBuilder().setSuccess(true).setMessageId(messageId).build();
        ctx.channel().writeAndFlush(response);
        // 通知在线的接收方
        Fim.NotifyRequest notify = Fim.NotifyRequest.newBuilder()
                .setSenderId(request.getSenderId())
                .setMessageId(messageId)
                .setMessageType(request.getMessageType())
                .setContent(request.getContent())
                .build();
        // 如果接收方在本机 直接通知接收方
        Channel channel = channelHolderService.get(request.getReceiverId());
        if (null != channel) {
            channel.writeAndFlush(notify);
        } else {
            // 接收方不在本机 通过rpc调用通知接收方
            String receiverIp = (String) redisTemplate.boundValueOps(request.getReceiverId() + "-login").get();
            if (StringUtils.isNotBlank(receiverIp)) {
                ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(receiverIp, grpcPort).usePlaintext().build();
                NotifyReceiverGrpc.newStub(managedChannel).receive(notify, null);
            }
        }
    }
}
