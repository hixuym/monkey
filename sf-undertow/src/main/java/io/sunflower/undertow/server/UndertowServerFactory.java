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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.lifecycle.setup.StandardThreadExecutor;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * UndertowServerFactory
 *
 * @author michael created on 17/10/17 11:29
 */
@JsonTypeName("undertow")
public class UndertowServerFactory extends AbstractServerFactory {

  @NotEmpty
  private String applicationContextPath = "/";

  @NotEmpty
  private String adminContextPath = "/";

  @JsonProperty
  public String getApplicationContextPath() {
    return applicationContextPath;
  }

  @JsonProperty
  public void setApplicationContextPath(String applicationContextPath) {
    this.applicationContextPath = applicationContextPath;
  }

  @JsonProperty
  public String getAdminContextPath() {
    return adminContextPath;
  }

  @JsonProperty
  public void setAdminContextPath(String adminContextPath) {
    this.adminContextPath = adminContextPath;
  }

  @NotNull
  private Map<String, String> serverProperties = new LinkedHashMap<>(20);

  @JsonProperty("properties")
  @Override
  public Map<String, String> getServerProperties() {
    serverProperties.put("sf.undertowContextPath", getAdminContextPath());
    return serverProperties;
  }

  @JsonProperty("properties")
  public void setServerProperties(Map<String, String> properties) {
    this.serverProperties = properties;
  }


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
  protected Server constract(Environment environment) {

    Undertow.Builder undertowBuilder = Undertow.builder()
        // NOTE: should not use equals chars within its cookie values?
        .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true)
        .setServerOption(UndertowOptions.ENABLE_STATISTICS, isStatsEnabled());

    logger.info("Undertow h2 protocol (undertow.http2 = {})", false);

    HttpHandler applicationHandler = addAccessLogWrapper("app", environment, appHandlers);

    HttpHandler adminHandler = addAccessLogWrapper("admin", environment, adminHandlers);

    for (ConnectorFactory connectorFactory : getApplicationConnectors()) {

      Undertow.ListenerBuilder listenerBuilder = connectorFactory.build(environment);

      if ("/".equalsIgnoreCase(getApplicationContextPath())) {
        listenerBuilder.setRootHandler(applicationHandler);
      } else {
        listenerBuilder.setRootHandler(
            new PathHandler().addPrefixPath(getApplicationContextPath(), applicationHandler));
      }

      undertowBuilder.addListener(listenerBuilder);
    }

    for (ConnectorFactory connectorFactory : getAdminConnectors()) {
      Undertow.ListenerBuilder listenerBuilder = connectorFactory.build(environment);

      if ("/".equalsIgnoreCase(getAdminContextPath())) {
        listenerBuilder.setRootHandler(adminHandler);
      } else {
        listenerBuilder
            .setRootHandler(new PathHandler().addPrefixPath(getAdminContextPath(), adminHandler));
      }

      undertowBuilder.addListener(listenerBuilder);
    }

    return new UndertowServer(environment, undertowBuilder.build());
  }
}
