package com.my.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class BookVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    @Qualifier("workerPool")
    private WorkerExecutor workerPool;

    private EventBus eventBus;

    private long timerId;

    private AtomicLong tickId = new AtomicLong(0L);



    @Override
    public void start() throws Exception {
        log.info("BookVerticle started");
        eventBus = vertx.eventBus();

        timerId = vertx.setPeriodic(1, id -> {
            log.info("Tick " + tickId.incrementAndGet());
            for (int i = 0; i < 5; i++) saveBook(new Book("testName", "testAuthor"), bookResultHandler());
        });
    }

    @Override
    public void stop() throws Exception {
        vertx.cancelTimer(timerId);
    }

    private void subsribeToEventBus() {
        MessageConsumer<Book> consumer = eventBus.consumer("new.books");
        consumer.handler(message -> {
            log.info("Received msg from event bus: " + message.headers().get("id"));
            saveBook(message.body(), bookResultHandler(message));
            //            message.reply("Processed msg: " + message.headers().get("id"));
        });
    }

    private Handler<AsyncResult<Book>> bookResultHandler() {
        return res -> log.info("[Blocking] Saved book: " + res.result());
    }

    private Handler<AsyncResult<Book>> bookResultHandler(Message<Book> message) {
        return res -> {
            log.info("[Blocking] Saved book: " + res.result());
            message.reply("Processed msg: " + message.headers().get("id"));
        };
    }

    private void saveBook(Book book, Handler<AsyncResult<Book>> resultHandler) {
        boolean serialExecution = false;
        workerPool.executeBlocking(future -> {
//            log.info("[Blocking] DB insert: " + book);
            Book saved = bookRepository.save(book);
            log.info("done, tick " + tickId.get());
//            try {
//                Thread.sleep(100L);
//            } catch (Throwable e) {
//                log.info("shit!");
//            }
            future.complete(saved);
        }, serialExecution, resultHandler);
    }
}
