package com.my.vertx;


import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Vertx vertx;
    @Autowired
    private SchedulerVerticle schedulerVerticle;
    @Autowired
    private BookVerticle bookVerticle;

    @PostConstruct
    public void deployVerticle() {
        log.info("Started deploying verticles...");
        vertx.eventBus().registerDefaultCodec(Book.class, new BookCodec());
//        vertx.deployVerticle(schedulerVerticle);
        vertx.deployVerticle(bookVerticle);
    }

    @PreDestroy
    public void shutdown() {
        vertx.close();
    }
}
