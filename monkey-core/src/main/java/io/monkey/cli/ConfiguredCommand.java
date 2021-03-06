package io.monkey.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import io.monkey.Configuration;
import io.monkey.configuration.ConfigurationException;
import io.monkey.configuration.ConfigurationFactory;
import io.monkey.configuration.ConfigurationFactoryFactory;
import io.monkey.configuration.ConfigurationSourceProvider;
import io.monkey.setup.Bootstrap;
import io.monkey.util.Generics;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validator;
import java.io.IOException;

/**
 * A command whose first parameter is the location of a YAML configuration file. That file is parsed
 * into an instance of a {@link Configuration} subclass, which is then validated. If the
 * configuration is valid, the command is run.
 *
 * @param <T> the {@link Configuration} subclass which is loaded from the configuration file
 * @author michael
 * @see Configuration
 */
public abstract class ConfiguredCommand<T extends Configuration> extends Command {

    private static final String PROPERTY_OVERRIDE_PREFIX = "mk";

    static Logger LOG = LoggerFactory.getLogger(ConfiguredCommand.class);

    private boolean asynchronous;

    private T configuration;

    protected ConfiguredCommand(String name, String description) {
        super(name, description);
        this.asynchronous = false;
    }

    /**
     * Returns the {@link Class} of the configuration type.
     *
     * @return the {@link Class} of the configuration type
     */
    protected Class<T> getConfigurationClass() {
        return Generics.getTypeParameter(getClass(), Configuration.class);
    }

    /**
     * Configure the command's {@link Subparser}. <p><strong> N.B.: if you override this method, you
     * <em>must</em> call {@code super.override(subparser)} in order to preserve the configuration
     * file parameter in the subparser. </strong></p>
     *
     * @param subparser the {@link Subparser} specific to the command
     */
    @Override
    public void configure(Subparser subparser) {
        addFileArgument(subparser);
    }

    /**
     * Adds the configuration file argument for the configured command.
     *
     * @param subparser The subparser to register the argument on
     * @return the register argument
     */
    protected Argument addFileArgument(Subparser subparser) {
        return subparser.addArgument("file")
                .nargs("?")
                .help("application configuration file");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(Bootstrap<?> wildcardBootstrap, Namespace namespace) throws Exception {
        final Bootstrap<T> bootstrap = (Bootstrap<T>) wildcardBootstrap;
        Stopwatch sw = Stopwatch.createStarted();

        configuration = parseConfiguration(bootstrap.getConfigurationFactoryFactory(),
                bootstrap.getConfigurationSourceProvider(),
                bootstrap.getValidatorFactory().getValidator(),
                namespace.getString("file"),
                getConfigurationClass(),
                bootstrap.getObjectMapper());

        try {

            sw.stop();

            LOG.info("application configuration file parse time: {}", sw);

            run(bootstrap, namespace, configuration);
        } finally {
            if (!asynchronous) {
                cleanup();
            }
        }
    }

    protected void cleanupAsynchronously() {
        this.asynchronous = true;
    }

    protected void cleanup() {
        if (configuration != null) {
            configuration.getLoggingFactory().stop();
        }
    }

    /**
     * Runs the command with the given {@link Bootstrap} and {@link Configuration}.
     *
     * @param bootstrap     the bootstrap bootstrap
     * @param namespace     the parsed command line namespace
     * @param configuration the configuration object
     * @throws Exception if something goes wrong
     */
    protected abstract void run(Bootstrap<T> bootstrap,
                                Namespace namespace,
                                T configuration) throws Exception;

    private T parseConfiguration(ConfigurationFactoryFactory<T> configurationFactoryFactory,
                                 ConfigurationSourceProvider provider,
                                 Validator validator,
                                 String path,
                                 Class<T> klass,
                                 ObjectMapper objectMapper) throws IOException, ConfigurationException {
        final ConfigurationFactory<T> configurationFactory = configurationFactoryFactory
                .create(klass, validator, objectMapper, PROPERTY_OVERRIDE_PREFIX);
        if (path != null) {
            return configurationFactory.build(provider, path);
        }
        return configurationFactory.build();
    }
}
