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

package io.sunflower.gizmo.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

@JsonTypeName("gizmo-simple")
public class SimpleGizmoServerFactory extends AbstractGizmoServerFactory {

  private ConnectorFactory connector = HttpConnectorFactory.application();

  @JsonProperty
  public ConnectorFactory getConnector() {
    return connector;
  }

  @JsonProperty
  public void setConnector(ConnectorFactory connector) {
    this.connector = connector;
  }

  @Override
  public Server buildServer(Environment environment) {
    Undertow.Builder undertowBuilder = Undertow.builder()
        // NOTE: should ninja not use equals chars within its cookie values?
        .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

    logger.info("Undertow h2 protocol (undertow.http2 = {})", isHttp2Enabled());

    HttpHandler applicationHandler = createApplicationHandler(environment.guicey().injector());

    HttpHandler adminHandler = createAdminHandler();

    Undertow.ListenerBuilder listenerBuilder = getConnector().build(environment);

    PathHandler rootHandler = new PathHandler();

    rootHandler.addPrefixPath(getApplicationContextPath(), applicationHandler);
    rootHandler.addPrefixPath(getAdminContextPath(), adminHandler);

    listenerBuilder.setRootHandler(addAccessLogWrapper("access", environment, rootHandler));

    undertowBuilder.addListener(listenerBuilder);

    return new GizmoServer(environment, undertowBuilder.build());
  }

  private String applicationContextPath = "/app";
  private String adminContextPath = "/admin";

  @JsonProperty
  @Override
  public String getApplicationContextPath() {
    return applicationContextPath;
  }

  @JsonProperty
  @Override
  public void setApplicationContextPath(String applicationContextPath) {
    this.applicationContextPath = applicationContextPath;
  }

  @JsonProperty
  @Override
  public String getAdminContextPath() {
    return adminContextPath;
  }

  @JsonProperty
  @Override
  public void setAdminContextPath(String adminContextPath) {
    this.adminContextPath = adminContextPath;
  }

}
