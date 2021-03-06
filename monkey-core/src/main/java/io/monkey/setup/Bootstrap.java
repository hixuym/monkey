package io.monkey.setup;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.monkey.Application;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.cli.Command;
import io.monkey.cli.ConfiguredCommand;
import io.monkey.configuration.ConfigurationFactoryFactory;
import io.monkey.configuration.ConfigurationSourceProvider;
import io.monkey.configuration.DefaultConfigurationFactoryFactory;
import io.monkey.configuration.FileConfigurationSourceProvider;
import io.monkey.jackson.Jackson;
import io.monkey.validation.BaseValidator;

import javax.validation.ValidatorFactory;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The pre-commit application environment, containing everything required to bootstrap a monkey command.
 *
 * @param <T> the configuration type
 * @author michael
 */
public class Bootstrap<T extends Configuration> {

    private final Application<T> application;
    private final List<ConfiguredBundle<? super T>> configuredBundles;
    private final List<Command> commands;

    private ObjectMapper objectMapper;

    private MetricRegistry metricRegistry;
    private ConfigurationSourceProvider configurationSourceProvider;
    private ClassLoader classLoader;
    private ConfigurationFactoryFactory<T> configurationFactoryFactory;
    private ValidatorFactory validatorFactory;

    private boolean metricsAreRegistered;
    private HealthCheckRegistry healthCheckRegistry;

    /**
     * Creates a new {@link Bootstrap} for the given application.
     *
     * @param application a monkey {@link Application}
     */
    public Bootstrap(Application<T> application) {
        this.application = application;
        this.objectMapper = Jackson.newObjectMapper();

        this.configuredBundles = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.validatorFactory = BaseValidator.newConfiguration().buildValidatorFactory();
        this.metricRegistry = new MetricRegistry();
        this.configurationSourceProvider = new FileConfigurationSourceProvider();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.configurationFactoryFactory = new DefaultConfigurationFactoryFactory<>();
        this.healthCheckRegistry = new HealthCheckRegistry();
    }

    /**
     * Registers the JVM metrics to the metric register and commit to report the register metrics via
     * JMX.
     */
    public void registerMetrics() {
        if (metricsAreRegistered) {
            return;
        }
        getMetricRegistry().register("jvm.attribute", new JvmAttributeGaugeSet());
        getMetricRegistry().register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        getMetricRegistry().register("jvm.classloader", new ClassLoadingGaugeSet());
        getMetricRegistry().register("jvm.filedescriptor", new FileDescriptorRatioGauge());
        getMetricRegistry().register("jvm.gc", new GarbageCollectorMetricSet());
        getMetricRegistry().register("jvm.memory", new MemoryUsageGaugeSet());
        getMetricRegistry().register("jvm.threads", new ThreadStatesGaugeSet());

        metricsAreRegistered = true;
    }

    /**
     * Returns the bootstrap's {@link Application}.
     */
    public Application<T> getApplication() {
        return application;
    }

    /**
     * Returns the bootstrap's {@link ConfigurationSourceProvider}.
     */
    public ConfigurationSourceProvider getConfigurationSourceProvider() {
        return configurationSourceProvider;
    }

    /**
     * Sets the bootstrap's {@link ConfigurationSourceProvider}.
     */
    public void setConfigurationSourceProvider(ConfigurationSourceProvider provider) {
        this.configurationSourceProvider = requireNonNull(provider);
    }

    /**
     * Returns the bootstrap's class loader.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the bootstrap's class loader.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Adds the given bundle to the bootstrap.
     *
     * @param bundle a {@link ConfiguredBundle}
     */
    public void addBundle(ConfiguredBundle<? super T> bundle) {
        bundle.initialize(this);
        configuredBundles.add(bundle);
    }

    /**
     * Adds the given command to the bootstrap.
     *
     * @param command a {@link Command}
     */
    public void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Adds the given command to the bootstrap.
     *
     * @param command a {@link ConfiguredCommand}
     */
    public void addCommand(ConfiguredCommand<T> command) {
        commands.add(command);
    }

    /**
     * Returns the bootstrap's {@link ObjectMapper}.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Sets the given {@link ObjectMapper} to the bootstrap. <p<b>WARNING:</b> The mapper should be
     * created by {@link Jackson#newMinimalObjectMapper()} or {@link Jackson#newObjectMapper()},
     * otherwise it will not work with Dropwizard.</p>
     *
     * @param objectMapper an {@link ObjectMapper}
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Runs the bootstrap's bundles with the given configuration and environment.
     *
     * @param configuration the parsed configuration
     * @param environment   the application environment
     * @throws Exception if a bundle throws an exception
     */
    public void run(T configuration, Environment environment) throws Exception {

        for (ConfiguredBundle<? super T> bundle : configuredBundles) {
            bundle.run(configuration, environment);
        }

    }

    /**
     * Returns the application's commands.
     */
    public ImmutableList<Command> getCommands() {
        return ImmutableList.copyOf(commands);
    }

    /**
     * Returns the application metrics.
     */
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    /**
     * Sets a custom register for the application metrics.
     *
     * @param metricRegistry a custom metric register
     */
    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    /**
     * Returns the application's validator factory.
     */
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public ConfigurationFactoryFactory<T> getConfigurationFactoryFactory() {
        return configurationFactoryFactory;
    }

    public void setConfigurationFactoryFactory(
            ConfigurationFactoryFactory<T> configurationFactoryFactory) {
        this.configurationFactoryFactory = configurationFactoryFactory;
    }

    /**
     * returns the health check register
     */
    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    public void setHealthCheckRegistry(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

}
