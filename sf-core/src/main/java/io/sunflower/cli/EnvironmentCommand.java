package io.sunflower.cli;

import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.setup.BootModule;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * A command which executes with a configured {@link Environment}.
 *
 * @param <T> the {@link Configuration} subclass which is loaded from the configuration file
 * @author michael
 * @see Configuration
 */
public abstract class EnvironmentCommand<T extends Configuration> extends ConfiguredCommand<T> {

    private final Application<T> application;

    /**
     * Creates a new environment command.
     *
     * @param application the application providing this command
     * @param name        the name of the command, used for command line invocation
     * @param description a description of the command's purpose
     */
    protected EnvironmentCommand(Application<T> application, String name, String description) {
        super(name, description);
        this.application = application;
    }

    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration)
            throws Exception {

        final Environment environment = new Environment(bootstrap);

        environment.guicify().register(new BootModule(environment));
        environment.guicify().register(configuration);

        configuration.getServerFactory().configure(environment);
        configuration.getLoggingFactory().configure(environment.getMetricRegistry(), environment.getName());
        configuration.getMetricsFactory().configure(environment.lifecycle(), environment.getMetricRegistry());

        bootstrap.run(configuration, environment);

        application.run(configuration, environment);

        environment.guicify().commit();

        run(environment, namespace, configuration);
    }

    /**
     * Runs the command with the given {@link Environment} and {@link Configuration}.
     *
     * @param environment   the configured environment
     * @param namespace     the parsed command line namespace
     * @param configuration the configuration object
     * @throws Exception if something goes wrong
     */
    protected abstract void run(Environment environment, Namespace namespace, T configuration)
            throws Exception;
}
