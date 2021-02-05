package com.yytxdy.fim.server.service.protocolHandler;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.server.entity.OfflineMessage;
import com.yytxdy.fim.server.service.ChannelHolderService;
import com.yytxdy.fim.server.service.MessageService;
import com.yytxdy.fim.server.utils.Errors;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginRequestHandler implements ProtocolHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private ChannelHolderService channelHolderService;
    @Autowired
    private MessageService messageService;

    @Override
    public boolean support(Fim.Protocol.DataType dataType) {
        return Fim.Protocol.DataType.LoginRequestType.equals(dataType);
    }

    @Override
    public void handler(Fim.Protocol protocol, ChannelHandlerContext ctx) {
        Fim.LoginRequest request = protocol.getLoginRequest();
        long userId = request.getUserId();
        String token = request.getToken();
        String tokenInRedis = (String) redisTemplate.boundValueOps(String.valueOf(userId)).get();
        if (StringUtils.equals(token, tokenInRedis)) {
            // 登陆成功 记录登陆信息到redis
            try {
                redisTemplate.boundValueOps(userId + "-login").set(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                logger.error("", e);
            }
            // 登陆时查询所有离线信息给客户端
            List<OfflineMessage> offlineMessages = messageService.listOfflineMessage(userId);
            Fim.LoginResponse.Builder builder = Fim.LoginResponse.newBuilder();
            builder.setSuccess(true).getMessageIdList().addAll(offlineMessages.stream().map(o -> o.getMessageId()).collect(Collectors.toList()));
            Fim.LoginResponse response = builder.build();
            ctx.channel().writeAndFlush(response);
            // 添加用户连接缓存
            channelHolderService.add(userId, ctx.channel());
        } else {
            // 登陆失败 返回错误信息
            Fim.LoginResponse response = Fim.LoginResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorCode(Errors.INVALID_TOKEN.getErrorCode())
                    .setErrorMessage(Errors.INVALID_TOKEN.getErrorMessage())
                    .build();
            ctx.channel().writeAndFlush(response);
        }
    }
}
