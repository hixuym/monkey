package io.sunflower.cli;

import com.google.common.util.concurrent.Service;

import net.sourceforge.argparse4j.inf.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.lifecycle.LifecycleListener;
import io.sunflower.lifecycle.LifecycleManager;
import io.sunflower.server.Server;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * A command which executes with a configured {@link Environment}.
 *
 * @param <T> the {@link Configuration} subclass which is loaded from the configuration file
 * @see Configuration
 */
public class ServerCommand<T extends Configuration> extends ConfiguredCommand<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerCommand.class);

    private final Application<T> application;

    /**
     * Creates a new environment command.
     *
     * @param application the application providing this command
     */
    public ServerCommand(Application<T> application) {
        super("server", "Runs the Sunflower application as an HTTP server");
        this.application = application;
    }

    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception {
        final Environment environment = new Environment(bootstrap.getApplication().getName(),
            bootstrap.getObjectMapper(),
            bootstrap.getValidatorFactory().getValidator(),
            bootstrap.getMetricRegistry(),
            bootstrap.getClassLoader(),
            bootstrap.getHealthCheckRegistry());

        configuration.getMetricsFactory().configure(environment.lifecycle(), environment.metrics());
        configuration.getServerFactory().configure(environment);
        Server server = configuration.getServerFactory().build(environment);

        environment.lifecycle().manage(server);

        environment.lifecycle().addListener(new LifecycleListener() {
            @Override
            public void stopped() {
                cleanup();
            }

            @Override
            public void failured(Service service) {
                LOGGER.error("Unable to start server, shutting down");
                cleanup();
                server.stopAsync().awaitTerminated();
            }
        });

        cleanupAsynchronously();

        bootstrap.run(configuration, environment);

        application.run(configuration, environment);

        LifecycleManager lifecycleManager = new LifecycleManager(environment.lifecycle());

        lifecycleManager.start();
    }
}
