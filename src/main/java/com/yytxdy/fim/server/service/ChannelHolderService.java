package com.yytxdy.fim.server.service;

import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChannelHolderService {
    private Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();
    private Map<Channel, Long> channelUserMap = new ConcurrentHashMap<>();

    public void add(Long userId, Channel channel) {
        userChannelMap.put(userId, channel);
        channelUserMap.put(channel, userId);
    }

    public void remove(Channel channel) {
        Long userId = channelUserMap.remove(channel);
        if (null != userId) {
            userChannelMap.remove(userId);
        }
    }

    public Channel get(Long userId) {
        return userChannelMap.get(userId);
    }
}
