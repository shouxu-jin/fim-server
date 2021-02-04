package com.yytxdy.fim.server.mapper;

import com.yytxdy.fim.server.entity.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMapper {
    void save(Message message);
}
