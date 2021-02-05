package com.yytxdy.fim.server.mapper;

import com.yytxdy.fim.server.entity.OfflineMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfflineMessageMapper {
    void save(OfflineMessage offlineMessage);

    List<OfflineMessage> list(long userId);

    void deleteOfflineMessage(long receiverId, long messageId);
}
