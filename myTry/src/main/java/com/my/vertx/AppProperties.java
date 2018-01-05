package com.my.vertx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my")
public class AppProperties {

    private int workerPoolSize;
    private long maxExecuteTime;

    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }

    public void setMaxExecuteTime(long maxExecuteTime) {
        this.maxExecuteTime = maxExecuteTime;
    }

    public int getWorkerPoolSize() {
        return workerPoolSize;
    }

    public long getMaxExecuteTime() {
        return maxExecuteTime;
    }
}
