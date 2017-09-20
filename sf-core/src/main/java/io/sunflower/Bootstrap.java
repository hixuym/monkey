package io.sunflower.setup;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import io.sunflower.Application;
import io.sunflower.Bundle;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.cli.Command;
import io.sunflower.cli.ConfiguredCommand;
import io.sunflower.configuration.ConfigurationFactoryFactory;
import io.sunflower.configuration.ConfigurationSourceProvider;
import io.sunflower.configuration.DefaultConfigurationFactoryFactory;
import io.sunflower.configuration.FileConfigurationSourceProvider;
import io.sunflower.extension.ExtensionLoader;
import io.sunflower.jackson.Jackson;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.validation.BaseValidator;

import static java.util.Objects.requireNonNull;

/**
 * The pre-start application environment, containing everything required to bootstrap a sunflower
 * command.
 *
 * @param <T> the configuration type
 */
public class Bootstrap<T extends Configuration> {
    private final Application<T> application;
    private final List<Command> commands;

    private final List<Module> modules;

    private ObjectMapper objectMapper;
    private MetricRegistry metricRegistry;
    private ConfigurationSourceProvider configurationSourceProvider;
    private ClassLoader classLoader;
    private ConfigurationFactoryFactory<T> configurationFactoryFactory;
    private ValidatorFactory validatorFactory;

    private boolean metricsAreRegistered;
    private HealthCheckRegistry healthCheckRegistry;

    private final ContainerLifeCycle lifeCycle;

    /**
     * Creates a new {@link Bootstrap} for the given application.
     *
     * @param application a sunflower {@link Application}
     */
    public Bootstrap(Application<T> application) {
        this.application = application;
        this.objectMapper = Jackson.newObjectMapper();
        this.modules = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.validatorFactory = BaseValidator.newConfiguration().buildValidatorFactory();
        this.metricRegistry = new MetricRegistry();
        this.configurationSourceProvider = new FileConfigurationSourceProvider();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.configurationFactoryFactory = new DefaultConfigurationFactoryFactory<>();
        this.healthCheckRegistry = new HealthCheckRegistry();
        this.lifeCycle = new ContainerLifeCycle();
    }

    public ContainerLifeCycle lifeCycle() {
        return this.lifeCycle;
    }

    /**
     * Registers the JVM metrics to the metric registry and start to report
     * the registry metrics via JMX.
     */
    public void registerMetrics() {
        if (metricsAreRegistered) {
            return;
        }
        getMetricRegistry().register("jvm.attribute", new JvmAttributeGaugeSet());
        getMetricRegistry().register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory
            .getPlatformMBeanServer()));
        getMetricRegistry().register("jvm.classloader", new ClassLoadingGaugeSet());
        getMetricRegistry().register("jvm.filedescriptor", new FileDescriptorRatioGauge());
        getMetricRegistry().register("jvm.gc", new GarbageCollectorMetricSet());
        getMetricRegistry().register("jvm.memory", new MemoryUsageGaugeSet());
        getMetricRegistry().register("jvm.threads", new ThreadStatesGaugeSet());

        JmxReporter.forRegistry(metricRegistry).build().start();
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

    public void addModule(Module... module) {
        this.modules.addAll(Arrays.asList(module));
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
     * Sets the given {@link ObjectMapper} to the bootstrap.
     * <p<b>WARNING:</b> The mapper should be created by {@link Jackson#newMinimalObjectMapper()}
     * or {@link Jackson#newObjectMapper()}, otherwise it will not work with sunflower.</p>
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
     * @throws Exception if a bundle throws an exception
     */
    public Injector run(T configuration) throws Exception {

        List<Bundle> bundles = ExtensionLoader.of(Bundle.class).getExtensions();

        for (Bundle bundle : bundles) {
            bundle.initialize(this);
        }

        List<ConfiguredBundle> configuredBundles = ExtensionLoader.of(ConfiguredBundle.class).getExtensions();

        for (ConfiguredBundle<? super T> bundle : configuredBundles) {
            bundle.initialize(this, configuration);
        }

        Injector parent = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(application.getConfigurationClass()).toInstance(configuration);
                bind(ValidatorFactory.class).toInstance(getValidatorFactory());
                bind(Validator.class).toInstance(getValidatorFactory().getValidator());
                bind(MetricRegistry.class).toInstance(getMetricRegistry());
                bind(HealthCheckRegistry.class).toInstance(getHealthCheckRegistry());
                bind(ObjectMapper.class).toInstance(getObjectMapper());
                bind(ContainerLifeCycle.class).toInstance(lifeCycle);
            }
        });

        Injector injector = parent.createChildInjector(this.modules);

        for (Bundle bundle : bundles) {
            bundle.run(injector);
        }

        for (ConfiguredBundle<? super T> bundle : configuredBundles) {
            bundle.run(injector);
        }

        return injector;
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
     * Sets a custom registry for the application metrics.
     *
     * @param metricRegistry a custom metric registry
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

    public void setConfigurationFactoryFactory(ConfigurationFactoryFactory<T> configurationFactoryFactory) {
        this.configurationFactoryFactory = configurationFactoryFactory;
    }

    /**
     * returns the health check registry
     */
    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    public void setHealthCheckRegistry(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }
}
