package com.caostudy.wiki.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;

/**
 * @author Cao Study
 * @description WebSocketServer
 * @date 2021/9/14 19:43
 */
@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG= LoggerFactory.getLogger(WebSocketServer.class);
    /**
     * 每个客户端一个token
     */
    private String token="";

    private static HashMap<String, Session> map=new HashMap<>();

    /**
     * 连接成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token"){
        map.put(token,session);
        this.token=token;
        LOG.info("有新连接：token：{}，session id：{}，当前连接数：{}", token, session.getId(), map.size());    }



}
