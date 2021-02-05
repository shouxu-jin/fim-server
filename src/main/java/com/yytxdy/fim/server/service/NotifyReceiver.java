package com.yytxdy.fim.server.service;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.protocol.NotifyReceiverGrpc;
import io.grpc.stub.StreamObserver;
import io.netty.channel.Channel;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GRpcService
public class NotifyReceiver extends NotifyReceiverGrpc.NotifyReceiverImplBase {
    @Autowired
    private ChannelHolderService channelHolderService;

    @Override
    public void receive(Fim.NotifyRequest request, StreamObserver<Fim.Empty> responseObserver) {
        Channel channel = channelHolderService.get(request.getReceiverId());
        if (null != channel) {
            channel.writeAndFlush(request);
        }
    }
}
