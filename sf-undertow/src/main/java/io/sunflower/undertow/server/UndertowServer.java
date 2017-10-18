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

import java.util.List;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.undertow.Undertow;
import io.undertow.Undertow.ListenerInfo;
import io.undertow.server.ConnectorStatistics;

/**
 * UndertowServer
 *
 * @author michael created on 17/10/17 11:24
 */
public class UndertowServer extends Server {

  private final Undertow undertow;
  private final String undertowVersion;
  private boolean undertowStarted;

  public UndertowServer(Environment environment, Undertow undertow) {
    super(environment);
    this.undertow = undertow;
    this.undertowVersion = undertow.getClass().getPackage().getImplementationVersion();
  }

  @Override
  protected void boot() throws Exception {
    logger.info("Trying to start undertow v{}", undertowVersion);
    this.undertow.start();
    undertowStarted = true;
    logger.info("Started undertow v{}", undertowVersion);

    List<ListenerInfo> infos = undertow.getListenerInfo();

    for (ListenerInfo info : infos) {
      logListenerInfo(info);
      registryMetrics(info);
    }

  }

  protected void logListenerInfo(ListenerInfo info) {
    logger.info("Undertow listen at:" + info.getProtcol() + ":/" + info.getAddress().toString());
  }

  protected void registryMetrics(ListenerInfo info) {

    final ConnectorStatistics statistics = info.getConnectorStatistics();

    if (statistics != null) {
      MetricRegistry registry = getEnvironment().metrics();

      String name = MetricRegistry
          .name("undertow", info.getProtcol(), info.getAddress().toString());

      registry.register(name + "requestCount",
          (Gauge) statistics::getRequestCount);

      registry.register(name + "errorCount",
          (Gauge) statistics::getErrorCount);

      registry.register(name + "activeRequests",
          (Gauge) statistics::getActiveRequests);

      registry.register(name + "maxActiveRequests",
          (Gauge) statistics::getMaxActiveRequests);

      registry.register(name + "activeConnections",
          (Gauge) statistics::getActiveConnections);

      registry.register(name + "maxActiveConnections",
          (Gauge) statistics::getMaxActiveConnections);

      registry.register(name + "bytesReceived",
          (Gauge) statistics::getBytesReceived);

      registry.register(name + "bytesSent",
          (Gauge) statistics::getBytesSent);

      registry.register(name + "processingTime",
          (Gauge) statistics::getProcessingTime);

      registry.register(name + "maxProcessingTime",
          (Gauge) statistics::getMaxProcessingTime);
    }
  }

  @Override
  protected void shutdown() throws Exception {
    if (this.undertow != null && undertowStarted) {
      logger.info("Trying to stop undertow {}", undertowVersion);
      this.undertow.stop();
      logger.info("Stopped undertow v{}", undertowVersion);
    }
  }

  public Undertow getUndertow() {
    return undertow;
  }
}
