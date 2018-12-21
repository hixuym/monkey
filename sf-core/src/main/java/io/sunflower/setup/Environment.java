package io.sunflower.setup;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Injector;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.AbstractLifeCycle.AbstractLifeCycleListener;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.lifecycle.Managed;
import io.sunflower.lifecycle.setup.LifecycleEnvironment;
import io.sunflower.server.Server;
import io.sunflower.server.ServerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.Objects.requireNonNull;

/**
 * A sunflower application's environment.
 *
 * @author michael
 */
public class Environment {

    private final String name;
    private final MetricRegistry metricRegistry;
    private final HealthCheckRegistry healthCheckRegistry;

    private final ObjectMapper objectMapper;

    private ValidatorFactory validatorFactory;

    private final LifecycleEnvironment lifecycleEnvironment;
    private final GuiceEnvironment guiceEnvironment;

    private final ExecutorService healthCheckExecutorService;
    private final ClassLoader classLoader;

    /**
     * Creates a new environment.
     *
     * @param name         the name of the application
     * @param objectMapper the {@link ObjectMapper} for the application
     */
    public Environment(String name,
                       ObjectMapper objectMapper,
                       ValidatorFactory validatorFactory,
                       MetricRegistry metricRegistry,
                       ClassLoader classLoader,
                       HealthCheckRegistry healthCheckRegistry) {
        this.name = name;
        this.classLoader = classLoader;

        this.objectMapper = objectMapper;
        this.metricRegistry = metricRegistry;
        this.healthCheckRegistry = healthCheckRegistry;
        this.healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck());
        this.validatorFactory = validatorFactory;

        this.guiceEnvironment = new GuiceEnvironment();
        this.guiceEnvironment.enableLifecycle();
        this.guiceEnvironment.enableMetrics();
        this.guiceEnvironment.register(new BootModule(this));

        this.lifecycleEnvironment = new LifecycleEnvironment();
        this.lifecycleEnvironment.manage(new Managed() {
            @Override
            public void stop() throws Exception {
                validatorFactory.close();
            }
        });

        lifecycleEnvironment.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                logHealthChecks();
            }
        });

        this.healthCheckExecutorService = this.lifecycle()
                .executorService("TimeBoundHealthCheck-pool-%d")
                .workQueue(new ArrayBlockingQueue<>(1))
                .minThreads(1)
                .maxThreads(4)
                .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
                .rejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();

        try {
            SharedMetricRegistries.getDefault();
        } catch (IllegalStateException e) {
            SharedMetricRegistries.setDefault("default", metricRegistry);
        }
        try {
            SharedHealthCheckRegistries.getDefault();
        } catch (IllegalStateException e) {
            SharedHealthCheckRegistries.setDefault("default", healthCheckRegistry);
        }
    }

    /**
     * Creates an environment with default health check register
     */
    public Environment(String name,
                       ObjectMapper objectMapper,
                       ValidatorFactory validatorFactory,
                       MetricRegistry metricRegistry,
                       ClassLoader classLoader) {
        this(name, objectMapper, validatorFactory, metricRegistry, classLoader, new HealthCheckRegistry());
    }

    /**
     * Returns the application's {@link LifecycleEnvironment}.
     */
    public LifecycleEnvironment lifecycle() {
        return lifecycleEnvironment;
    }

    /**
     * Returns the application's {@link MetricRegistry}.
     */
    public MetricRegistry metrics() {
        return metricRegistry;
    }

    /**
     * Returns the application's {@link HealthCheckRegistry}.
     */
    public HealthCheckRegistry healthChecks() {
        return healthCheckRegistry;
    }

    /**
     * returns the guice configuration environment
     * @return guice configure env
     */
    public GuiceEnvironment guice() {
        return guiceEnvironment;
    }

    /**
     * Returns an {@link ExecutorService} to run time bound health checks
     */
    public ExecutorService getHealthCheckExecutorService() {
        return healthCheckExecutorService;
    }

    /**
     * Returns the application's {@link ObjectMapper}.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Returns the application's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the application's {@link Validator}.
     */
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    /**
     * Sets the application's {@link Validator}.
     */
    public void setValidatorFactory(ValidatorFactory validator) {
        this.validatorFactory = requireNonNull(validator);
    }

    public Injector injector() {
        return guiceEnvironment.getInjector();
    }

    /**
     * @return application's {@link ClassLoader}.
     */
    public ClassLoader classLoader() {
        return this.classLoader;
    }

    public void addServerLifecycleListener(final ServerLifecycleListener lifecycleListener) {
        lifecycleEnvironment.addLifeCycleListener(new AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarted(LifeCycle event) {
                if (event instanceof Server) {
                    lifecycleListener.serverStarted((Server) event);
                }
            }
        });
    }

    private static Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private void logHealthChecks() {
        if (healthChecks().getNames().size() <= 1) {
            LOGGER.warn(String.format(
                    "%n" +  "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
                            "!    THIS APPLICATION HAS NO HEALTHCHECKS. THIS MEANS YOU WILL NEVER KNOW      !%n" +
                            "!     IF IT DIES IN PRODUCTION, WHICH MEANS YOU WILL NEVER KNOW IF YOU'RE      !%n" +
                            "!    LETTING YOUR USERS DOWN. YOU SHOULD ADD A HEALTHCHECK FOR EACH OF YOUR    !%n" +
                            "!         APPLICATION'S DEPENDENCIES WHICH FULLY (BUT LIGHTLY) TESTS IT.       !%n" +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            ));
        }
        LOGGER.info("health checks = {}", healthChecks().getNames());
    }
}
