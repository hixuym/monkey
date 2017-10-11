package io.sunflower.undertow;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sunflower.jackson.Discoverable;
import io.sunflower.setup.Environment;
import io.undertow.Undertow;

/**
 * A factory for creating Jetty {@link Undertow.ListenerBuilder}s.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ConnectorFactory extends Discoverable {

  /**
   * Create a new connector.
   *
   * @return a {@link Undertow.ListenerBuilder}
   */
  Undertow.ListenerBuilder build(Environment environment);
}
