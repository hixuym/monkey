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

package io.sunflower.client.proxy;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sunflower.validation.OneOf;
import io.sunflower.validation.PortRange;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Configuration of access to a remote host through a proxy server
 * <p/>
 * <b>Configuration Parameters:</b> <table> <tr> <td>Name</td> <td>Default</td> <td>Description</td>
 * </tr> <tr> <td>{@code host}</td> <td>REQUIRED</td> <td>The proxy server host name or ip
 * address.</td> </tr> <tr> <td>{@code port}</td> <td>scheme default</td> <td>The proxy server port.
 * If the port is not set then the scheme default port is used.</td> </tr> <tr> <td>{@code
 * scheme}</td> <td>http</td> <td>The proxy server URI scheme. HTTP and HTTPS schemas are permitted.
 * By default HTTP scheme is used.</td> </tr> <tr> <td>{@code auth}</td> <td>(none)</td> <td> The
 * proxy server {@link AuthConfiguration} BASIC authentication credentials. If they are not set then
 * no credentials will be passed to the server. </td> </tr> <tr> <td>{@code nonProxyHosts}</td>
 * <td>(none)</td> <td> List of patterns of hosts that should be reached without proxy. The patterns
 * may contain symbol '*' as a wildcard. If a host matches one of the patterns it will be reached
 * through a direct connection. </td> </tr> </table>
 */
public class ProxyConfiguration {

  @NotEmpty
  private String host;

  @PortRange(min = -1)
  private Integer port = -1;

  @OneOf(value = {"http", "https"}, ignoreCase = true)
  private String scheme = "http";

  @Valid
  @Nullable
  private AuthConfiguration auth;

  @Nullable
  private List<String> nonProxyHosts;

  public ProxyConfiguration() {
  }

  public ProxyConfiguration(@NotNull String host) {
    this.host = host;
  }

  public ProxyConfiguration(@NotNull String host, int port) {
    this(host);
    this.port = port;
  }

  public ProxyConfiguration(@NotNull String host, int port, String scheme, AuthConfiguration auth) {
    this(host, port);
    this.scheme = scheme;
    this.auth = auth;
  }

  @JsonProperty
  public String getHost() {
    return host;
  }

  @JsonProperty
  public void setHost(String host) {
    this.host = host;
  }

  @JsonProperty
  public Integer getPort() {
    return port;
  }

  @JsonProperty
  public void setPort(Integer port) {
    this.port = port;
  }

  @JsonProperty
  public String getScheme() {
    return scheme;
  }

  @JsonProperty
  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  @JsonProperty
  public List<String> getNonProxyHosts() {
    return nonProxyHosts;
  }

  @JsonProperty
  public void setNonProxyHosts(List<String> nonProxyHosts) {
    this.nonProxyHosts = nonProxyHosts;
  }

  public AuthConfiguration getAuth() {
    return auth;
  }

  public void setAuth(AuthConfiguration auth) {
    this.auth = auth;
  }
}
