package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.service.WsService;
import com.caostudy.wiki.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsServiceImpl implements WsService {
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    @Async
    public void sendInfo(String message){
        webSocketServer.sendInfo(message);
    }
}
