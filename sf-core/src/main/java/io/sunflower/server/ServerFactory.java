package io.sunflower.server;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sunflower.jackson.Discoverable;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.Server;

/**
 * A factory for building {@link Server} instances for sunflower applications.
 *
 * @see DefaultServerFactory
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultServerFactory.class)
public interface ServerFactory extends Discoverable {
    /**
     * Build a server for the given sunflower application.
     *
     * @param environment the application's environment
     * @return a {@link Server} running the sunflower application
     */
    Server build(Environment environment);

    /**
     * Configures the given environment with settings defined in the factory.
     *
     * @param environment the application's environment
     */
    void configure(Environment environment);
}
