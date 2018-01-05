package com.my.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration bean.
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@Configuration
public class AppConfiguration {

  @Bean
  public Vertx vertxNonClusteredInstance() {
    return Vertx.vertx();
  }

//  @Bean
//  public DeliveryOptions deliveryOptions() {
//    return new DeliveryOptions().setCodecName(BookCodec.class.getSimpleName());
//  }

  @Bean("workerPool")
  public WorkerExecutor workerPool(AppProperties properties, Vertx vertx) {
    return vertx.createSharedWorkerExecutor("vertx-worker", properties.getWorkerPoolSize(), properties.getMaxExecuteTime());
  }

}
