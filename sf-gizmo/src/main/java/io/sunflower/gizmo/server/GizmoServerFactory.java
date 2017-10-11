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
import com.google.common.base.Strings;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonTypeName("gizmo")
public class GizmoServerFactory extends AbstractGizmoServerFactory {

  @Valid
  @NotNull
  private List<ConnectorFactory> applicationConnectors = Collections
      .singletonList(HttpConnectorFactory.application());

  @Valid
  @NotNull
  private List<ConnectorFactory> adminConnectors = Collections
      .singletonList(HttpConnectorFactory.admin());

  @JsonProperty
  public List<ConnectorFactory> getApplicationConnectors() {
    return applicationConnectors;
  }

  @JsonProperty
  public void setApplicationConnectors(List<ConnectorFactory> applicationConnectors) {
    this.applicationConnectors = applicationConnectors;
  }

  @JsonProperty
  public List<ConnectorFactory> getAdminConnectors() {
    return adminConnectors;
  }

  @JsonProperty
  public void setAdminConnectors(List<ConnectorFactory> adminConnectors) {
    this.adminConnectors = adminConnectors;
  }

  @Override
  public Server buildServer(Environment environment) {

    Undertow.Builder undertowBuilder = Undertow.builder()
        // NOTE: should ninja not use equals chars within its cookie values?
        .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

    logger.info("Undertow h2 protocol (undertow.http2 = {})", isHttp2Enabled());

    HttpHandler applicationHandler = addAccessLogWrapper("app", environment,
        createApplicationHandler(environment.guicey().injector()));

    HttpHandler adminHandler = addAccessLogWrapper("admin", environment, createAdminHandler());

    for (ConnectorFactory connectorFactory : getApplicationConnectors()) {

      Undertow.ListenerBuilder listenerBuilder = connectorFactory.build(environment);

      if (Strings.isNullOrEmpty(getApplicationContextPath())) {
        listenerBuilder.setRootHandler(applicationHandler);
      } else {
        listenerBuilder.setRootHandler(
            new PathHandler().addPrefixPath(getApplicationContextPath(), applicationHandler));
      }

      undertowBuilder.addListener(listenerBuilder);
    }

    for (ConnectorFactory connectorFactory : getAdminConnectors()) {
      Undertow.ListenerBuilder listenerBuilder = connectorFactory.build(environment);

      if (Strings.isNullOrEmpty(getAdminContextPath())) {
        listenerBuilder.setRootHandler(adminHandler);
      } else {
        listenerBuilder
            .setRootHandler(new PathHandler().addPrefixPath(getAdminContextPath(), adminHandler));
      }

      undertowBuilder.addListener(listenerBuilder);
    }

    return new GizmoServer(environment, undertowBuilder.build());
  }

}
