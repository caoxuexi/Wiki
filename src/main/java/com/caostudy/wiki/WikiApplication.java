package com.caostudy.wiki;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Cao Study
 * @description WikiApplication
 * @date 2021/8/24 12:40
 */
@SpringBootApplication
@EnableScheduling
@MapperScan(value = "com.caostudy.wiki.mapper")
@EnableAsync
public class WikiApplication {
    @Value("${server.port:8080}")
    private String port;
    private static final Logger LOG= LoggerFactory.getLogger(WikiApplication.class);

    public static void main(String[] args) {
        SpringApplication app=new SpringApplication(WikiApplication.class);
        Environment env =app.run(args).getEnvironment();
        LOG.info("启动成功！");
        LOG.info("地址：\t http://127.0.0.1:{}",env.getProperty("server.port"));

    }
}
