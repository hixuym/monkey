package io.sunflower.undertow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.setup.Environment;
import io.sunflower.validation.PortRange;
import io.undertow.Undertow;

/**
 * Builds HTTP connectors.
 */
@JsonTypeName("http")
public class HttpConnectorFactory implements ConnectorFactory {

  public static ConnectorFactory application() {
    final HttpConnectorFactory factory = new HttpConnectorFactory();
    factory.port = 8080;
    return factory;
  }

  public static ConnectorFactory admin() {
    final HttpConnectorFactory factory = new HttpConnectorFactory();
    factory.port = 8081;
    return factory;
  }

  @PortRange
  private int port = 8080;

  private String bindHost = null;

  @JsonProperty
  public int getPort() {
    return port;
  }

  @JsonProperty
  public void setPort(int port) {
    this.port = port;
  }

  @JsonProperty
  public String getBindHost() {
    return bindHost;
  }

  @JsonProperty
  public void setBindHost(String bindHost) {
    this.bindHost = bindHost;
  }

  @Override
  public Undertow.ListenerBuilder build(Environment environment) {

    Undertow.ListenerBuilder builder = new Undertow.ListenerBuilder();

    builder.setType(Undertow.ListenerType.HTTP);
    builder.setHost(bindHost);
    builder.setPort(port);

    return builder;
  }
}
