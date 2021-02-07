package com.yytxdy.fim.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class SequenceGenerator {
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public long nextId(long receiverId) {
        return redisTemplate.boundValueOps(receiverId + "-id").increment();
    }
}