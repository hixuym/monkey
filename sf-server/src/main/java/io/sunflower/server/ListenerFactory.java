package io.sunflower.server;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.sunflower.jackson.Discoverable;
import io.undertow.Undertow;

/**
 * A factory for creating Jetty {@link Undertow.ListenerBuilder}s.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ListenerFactory extends Discoverable {

    /**
     * Create a new connector.
     *
     * @return a {@link Undertow.ListenerBuilder}
     */
    Undertow.ListenerBuilder build();
}
