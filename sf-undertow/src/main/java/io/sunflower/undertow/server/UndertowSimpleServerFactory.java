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

import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * UndertowSimpleServerFactory
 *
 * @author michael created on 17/10/17 11:01
 */
@JsonTypeName("undertow-simple")
public class UndertowSimpleServerFactory extends AbstractServerFactory {

  private ConnectorFactory connector = HttpConnectorFactory.application();

  @NotEmpty
  private String applicationContextPath = "/app";
  @NotEmpty
  private String adminContextPath = "/admin";

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
    serverProperties.put("sf.undertowContextPath", getApplicationContextPath());
    return serverProperties;
  }

  @JsonProperty("properties")
  public void setServerProperties(Map<String, String> properties) {
    this.serverProperties = properties;
  }

  @JsonProperty
  public ConnectorFactory getConnector() {
    return connector;
  }

  @JsonProperty
  public void setConnector(ConnectorFactory connector) {
    this.connector = connector;
  }

  @Override
  protected Server constract(Environment env) {

    Undertow.Builder undertowBuilder = Undertow.builder()
        // NOTE: should not use equals chars within its cookie values?
        .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true)
        .setServerOption(UndertowOptions.ENABLE_STATISTICS, isStatsEnabled());

    logger.info("Undertow h2 protocol (undertow.http2 = {})", false);

    Undertow.ListenerBuilder listenerBuilder = getConnector().build(env);

    PathHandler rootHandler = new PathHandler();

    rootHandler.addPrefixPath(getApplicationContextPath(), appHandlers);
    rootHandler.addPrefixPath(getAdminContextPath(), adminHandlers);

    listenerBuilder.setRootHandler(addAccessLogWrapper("access", env, rootHandler));

    undertowBuilder.addListener(listenerBuilder);

    return new UndertowServer(env, undertowBuilder.build());
  }

  @Override
  public void configure(Environment environment) {
  }
}
