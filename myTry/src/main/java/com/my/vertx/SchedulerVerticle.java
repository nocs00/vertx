package com.my.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SchedulerVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private long timerId;

    private EventBus eventBus;

    private AtomicLong msgId = new AtomicLong(0L);

    @Override
    public void start() throws Exception {
        log.info("SchedulerVerticle started");
        eventBus = vertx.eventBus();

        timerId = vertx.setPeriodic(10, id -> {
            log.info("Triggering BookVerticle");

            eventBus.send("new.books", new Book("testName", "testAuthor"), addId(), ar -> {
                if (ar.succeeded()) {
                    log.info(ar.result().body().toString());
                } else if (ar.failed()) {
                    log.info("Failed reply: " + ar.result());
                } else {
                    log.info("Hz what happened");
                }
            });
        });
    }

    private DeliveryOptions addId() {
        return new DeliveryOptions()
                .addHeader("id", String.valueOf(msgId.incrementAndGet()));
    }

    @Override
    public void stop() throws Exception {
        vertx.cancelTimer(timerId);
    }
}
