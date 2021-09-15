package com.caostudy.wiki.service;

import org.springframework.scheduling.annotation.Async;

/**
 * @author Cao Study
 * @description WsService
 * @date 2021/9/15 17:40
 */
public interface WsService {

    @Async
    void sendInfo(String message, String logId);
}
