package com.yytxdy.fim.server.service;

import com.yytxdy.fim.protocol.Fim;
import com.yytxdy.fim.server.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;


    @Transactional
    public void saveMessage(Fim.SendMessageRequest request) {

    }
}
