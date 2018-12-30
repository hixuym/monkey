package io.monkey.setup;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Injector;
import io.monkey.Mode;
import io.monkey.ModeHelper;
import io.monkey.lifecycle.AbstractLifeCycle.AbstractLifeCycleListener;
import io.monkey.lifecycle.LifeCycle;
import io.monkey.lifecycle.Managed;
import io.monkey.lifecycle.setup.LifecycleEnvironment;
import io.monkey.server.Server;
import io.monkey.server.ServerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.Objects.requireNonNull;

/**
 * A monkey application's environment.
 *
 * @author michael
 */
public final class Environment {

    private final String name;
    private final MetricRegistry metricRegistry;
    private final HealthCheckRegistry healthCheckRegistry;

    private final ObjectMapper objectMapper;

    private ValidatorFactory validatorFactory;

    private final LifecycleEnvironment lifecycleEnvironment;

    private final ExecutorService healthCheckExecutorService;
    private final ClassLoader classLoader;

    private GuicifyEnvironment guicifyEnvironment;

    private Injector injector;

    private final Mode mode;

    /**
     * Creates a new environment.
     *
     * @param bootstrap    the pre commited env
     */
    public Environment(Bootstrap bootstrap) {
        this.name = bootstrap.getApplication().getName();
        this.classLoader = bootstrap.getClassLoader();
        this.objectMapper = bootstrap.getObjectMapper();
        this.metricRegistry = bootstrap.getMetricRegistry();
        this.healthCheckRegistry = bootstrap.getHealthCheckRegistry();
        this.healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck());
        this.validatorFactory = bootstrap.getValidatorFactory();

        this.lifecycleEnvironment = new LifecycleEnvironment();
        this.lifecycleEnvironment.manage(new Managed() {
            @Override
            public void stop() throws Exception {
                validatorFactory.close();
            }
        });

        lifecycleEnvironment.addLifeCycleListener(new AbstractLifeCycleListener() {
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

        this.guicifyEnvironment = new GuicifyEnvironment(this);
        this.mode = ModeHelper.determineModeFromSystemPropertiesOrDevIfNotSet();

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
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    /**
     * Returns the application's {@link HealthCheckRegistry}.
     */
    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
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
        checkCommited();
        this.validatorFactory = requireNonNull(validator);
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

    public GuicifyEnvironment guicify() {
        checkCommited();
        return guicifyEnvironment;
    }

    public Injector getInjector() {
        Preconditions.checkArgument(injector != null, "injector only available in commited enviroment.");
        return injector;
    }

    private void checkCommited() {
        if (injector != null) {
            throw new IllegalStateException("Enviroment already commited.");
        }
    }

    void setInjector(Injector injector) {
        this.injector = injector;
    }

    public Mode getMode() {
        return mode;
    }

    private static Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private void logHealthChecks() {
        if (getHealthCheckRegistry().getNames().size() <= 1) {
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
        LOGGER.info("health checks = {}", getHealthCheckRegistry().getNames());
    }
}
