package com.caostudy.wiki.service;

import org.springframework.scheduling.annotation.Async;

public interface WsService {
    @Async
    void sendInfo(String message, String logId);
}
