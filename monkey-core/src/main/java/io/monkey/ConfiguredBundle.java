package io.monkey;

import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;

/**
 * A reusable bundle of functionality, used to define blocks of application behavior that are
 * conditional on configuration parameters.
 *
 * @param <T> the required configuration interface
 * @author michael
 */
public interface ConfiguredBundle<T> {

    /**
     * Initializes the application bootstrap.
     *
     * @param bootstrap the application bootstrap
     */
    void initialize(Bootstrap<?> bootstrap);

    /**
     * Initializes the environment.
     *
     * @param configuration the configuration object
     * @param environment   the application's {@link Environment}
     * @throws Exception if something goes wrong
     */
    void run(T configuration, Environment environment);
}
