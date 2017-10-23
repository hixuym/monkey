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

package io.sunflower.undertow.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import com.codahale.metrics.Gauge;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.TypeLiteral;
import io.sunflower.guicey.Injectors;
import io.sunflower.lifecycle.setup.StandardThreadExecutor;
import io.sunflower.server.Server;
import io.sunflower.server.ServerFactory;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.AdminHandlers;
import io.sunflower.undertow.AppHandlers;
import io.sunflower.util.Duration;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractServerFactory
 *
 * @author michael created on 17/10/17 11:04
 */
public abstract class AbstractServerFactory implements ServerFactory {

  @JsonIgnore
  protected Logger logger = LoggerFactory.getLogger(getClass());

  @JsonIgnore
  protected final PathHandler adminHandlers = new PathHandler();
  @JsonIgnore
  protected final PathHandler appHandlers = new PathHandler();

  @Override
  public Server build(Environment environment) {

    Injectors.mapOf(environment.injector(),
        new TypeLiteral<Map<String, HttpHandler>>() {
        }, AdminHandlers.class).forEach(adminHandlers::addPrefixPath);

    Injectors.mapOf(environment.injector(),
        new TypeLiteral<Map<String, HttpHandler>>() {
        }, AppHandlers.class).forEach(appHandlers::addPrefixPath);

    return constract(environment);
  }

  /**
   * build the server
   * @param environment
   * @return
   */
  protected abstract Server constract(Environment environment);

  @Override
  public void configure(Environment environment) {
    StandardThreadExecutor executor = environment.lifecycle()
        .standardThreadExecutor("undertowWorkerTask-pool-%d")
        .maxWorkerThread(maxWorkerThreads)
        .minWorkerThread(minWorkerThreads)
        .workerQueueSize(workerQueueSize)
        .maxIdleTime(maxIdleTime)
        .build();

    environment.metrics().register("undertow.workerTaskPool.submittedTasksCount", (Gauge) executor::getSubmittedTasksCount);
    environment.metrics().register("undertow.workerTaskPool.maxSubmittedTaskCount", (Gauge) executor::getMaxSubmittedTaskCount);

    environment.guicey().register(executor);
  }

  private boolean statsEnabled = false;

  /**
   * access log enabled when accessLogFormat configured.
   */
  private String accessLogFormat;
  private boolean accessLogRotate = true;
  private String accessLogPath = "./logs";

  private int minWorkerThreads = 10;
  private int maxWorkerThreads = 200;
  private int workerQueueSize = 2000;
  private Duration maxIdleTime = Duration.minutes(1);

  @JsonProperty
  public Duration getMaxIdleTime() {
    return maxIdleTime;
  }

  @JsonProperty
  public void setMaxIdleTime(Duration maxIdleTime) {
    this.maxIdleTime = maxIdleTime;
  }

  @JsonProperty
  public int getMinWorkerThreads() {
    return minWorkerThreads;
  }

  @JsonProperty
  public void setMinWorkerThreads(int minWorkerThreads) {
    this.minWorkerThreads = minWorkerThreads;
  }

  @JsonProperty
  public int getMaxWorkerThreads() {
    return maxWorkerThreads;
  }

  @JsonProperty
  public void setMaxWorkerThreads(int maxWorkerThreads) {
    this.maxWorkerThreads = maxWorkerThreads;
  }

  @JsonProperty
  public int getWorkerQueueSize() {
    return workerQueueSize;
  }

  @JsonProperty
  public void setWorkerQueueSize(int workerQueueSize) {
    this.workerQueueSize = workerQueueSize;
  }

  @JsonProperty
  public boolean isStatsEnabled() {
    return statsEnabled;
  }

  @JsonProperty
  public void setStatsEnabled(boolean statsEnabled) {
    this.statsEnabled = statsEnabled;
  }

  @JsonProperty
  public String getAccessLogFormat() {
    return accessLogFormat;
  }

  @JsonProperty
  public void setAccessLogFormat(String accessLogFormat) {
    this.accessLogFormat = accessLogFormat;
  }

  @JsonProperty
  public boolean isAccessLogRotate() {
    return accessLogRotate;
  }

  @JsonProperty
  public void setAccessLogRotate(boolean accessLogRotate) {
    this.accessLogRotate = accessLogRotate;
  }

  @JsonProperty
  public String getAccessLogPath() {
    return accessLogPath;
  }

  @JsonProperty
  public void setAccessLogPath(String accessLogPath) {
    this.accessLogPath = accessLogPath;
  }

  @JsonIgnore
  protected HttpHandler addAccessLogWrapper(String baseName, Environment environment,
      HttpHandler httpHandler) {
    String format = getAccessLogFormat();

    if (StringUtils.isNotEmpty(format)) {

      ExecutorService accessLogExecutor = environment.lifecycle()
          .executorService("AccessLog-pool-%d-" + baseName)
          .maxThreads(2)
          .minThreads(1)
          .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
          .rejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
          .build();

      Path log = Paths.get(getAccessLogPath());

      if (!log.toFile().exists()) {
        boolean r = log.toFile().mkdirs();
      }

      AccessLogReceiver receiver = DefaultAccessLogReceiver.builder()
          .setLogBaseName(baseName)
          .setLogWriteExecutor(accessLogExecutor)
          .setOutputDirectory(log)
          .setRotate(isAccessLogRotate())
          .build();

      return new AccessLogHandler(httpHandler, receiver, format, environment.classLoader());
    }

    return httpHandler;
  }
}
