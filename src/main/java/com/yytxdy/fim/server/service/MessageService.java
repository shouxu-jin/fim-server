package com.yytxdy.fim.server.service;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.server.entity.Message;
import com.yytxdy.fim.server.entity.OfflineMessage;
import com.yytxdy.fim.server.mapper.MessageMapper;
import com.yytxdy.fim.server.mapper.OfflineMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private SequenceGenerator sequenceGenerator;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private OfflineMessageMapper offlineMessageMapper;

    public List<OfflineMessage> listOfflineMessage(long userId) {
        return offlineMessageMapper.list(userId);
    }

    @Transactional
    public long save(Fim.SendMessageRequest request) {
        long messageId = sequenceGenerator.nextId(request.getReceiverId());
        Message message = new Message();
        message.setMessageId(messageId);
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setMessageType(request.getMessageType().getNumber());
        message.setContent(request.getContent());
        OfflineMessage offlineMessage = new OfflineMessage();
        offlineMessage.setMessageId(messageId);
        offlineMessage.setReceiverId(request.getReceiverId());
        messageMapper.save(message);
        offlineMessageMapper.save(offlineMessage);
        return messageId;
    }

    @Transactional
    public void deleteOfflineMessage(long receiverId, long messageId) {
        offlineMessageMapper.deleteOfflineMessage(receiverId, messageId);
    }
}
