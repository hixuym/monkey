/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.lifecycle.setup;

import io.sunflower.lifecycle.ExecutorServiceManager;
import io.sunflower.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * @author michael
 */
public class StandardThreadExecutorBuilder {

    private static Logger log = LoggerFactory.getLogger(StandardThreadExecutorBuilder.class);

    private final LifecycleEnvironment environment;
    private final String nameFormat;
    private int minWorkerThread;
    private int maxWorkerThread;
    private int workerQueueSize;
    private Duration keepAliveTime = Duration.minutes(1);
    private ThreadFactory threadFactory;
    private Duration shutdownTime;
    private RejectedExecutionHandler rejectedExecutionHandler;

    public StandardThreadExecutorBuilder(LifecycleEnvironment environment, String nameFormat,
                                         ThreadFactory factory) {
        this.environment = environment;
        this.nameFormat = nameFormat;
        this.minWorkerThread = 10;
        this.maxWorkerThread = 200;
        this.workerQueueSize = 1000;
        this.threadFactory = factory;
        this.shutdownTime = Duration.seconds(5);
        this.rejectedExecutionHandler = new AbortPolicy();
    }

    public StandardThreadExecutorBuilder(LifecycleEnvironment environment, String nameFormat) {
        this(environment, nameFormat, new ThreadFactoryBuilder().setNameFormat(nameFormat).build());
    }

    public StandardThreadExecutorBuilder shutdownTime(Duration time) {
        this.shutdownTime = time;
        return this;
    }

    public StandardThreadExecutorBuilder minWorkerThread(int minWorkerThread) {
        this.minWorkerThread = minWorkerThread;
        return this;
    }

    public StandardThreadExecutorBuilder maxWorkerThread(int maxWorkerThread) {
        this.maxWorkerThread = maxWorkerThread;
        return this;
    }

    public StandardThreadExecutorBuilder workerQueueSize(int workerQueueSize) {
        this.workerQueueSize = workerQueueSize;
        return this;
    }

    public StandardThreadExecutorBuilder maxIdleTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public StandardThreadExecutorBuilder threadFacotry(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public StandardThreadExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    public StandardThreadExecutor build() {

        StandardThreadExecutor executor = new StandardThreadExecutor(minWorkerThread, maxWorkerThread,
                keepAliveTime.toSeconds(), TimeUnit.SECONDS,
                workerQueueSize, threadFactory, rejectedExecutionHandler);

        environment.manage(new ExecutorServiceManager(executor, shutdownTime, nameFormat));

        executor.prestartAllCoreThreads();

        return executor;
    }
}
