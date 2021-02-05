package com.yytxdy.fim.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class SequenceGenerator {
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public long nextId(long senderId, long receiverId) {
        String key = senderId > receiverId ? receiverId + String.valueOf(senderId) : senderId + String.valueOf(receiverId);
        Long messageId = redisTemplate.boundValueOps(key).increment();
        return messageId;
    }
}