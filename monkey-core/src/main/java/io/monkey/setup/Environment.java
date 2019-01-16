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
import io.monkey.inject.validation.InjectingConstraintValidatorFactory;
import io.monkey.jackson.Jackson;
import io.monkey.lifecycle.AbstractLifeCycle.AbstractLifeCycleListener;
import io.monkey.lifecycle.LifeCycle;
import io.monkey.lifecycle.setup.LifecycleEnvironment;
import io.monkey.server.Server;
import io.monkey.server.ServerLifecycleListener;
import io.monkey.validation.BaseValidator;
import io.monkey.validation.MutableValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A monkey application's environment.
 *
 * @author michael
 */
public class Environment {

    private final String name;
    private final MetricRegistry metricRegistry;
    private final HealthCheckRegistry healthCheckRegistry;

    private final ObjectMapper objectMapper;

    private Validator validator;

    private final LifecycleEnvironment lifecycleEnvironment;

    private final ExecutorService healthCheckExecutorService;
    private final ClassLoader classLoader;

    private GuicifyEnvironment guicifyEnvironment;

    private Injector injector;

    private final Mode mode;

    /**
     * Creates a new environment.
     *
     */
    public Environment(String name,
                       ObjectMapper objectMapper,
                       ValidatorFactory validatorFactory,
                       MetricRegistry metricRegistry,
                       @Nullable ClassLoader classLoader,
                       HealthCheckRegistry healthCheckRegistry) {

        this.name = name;
        this.classLoader = classLoader;
        this.objectMapper = objectMapper;
        this.metricRegistry = metricRegistry;
        this.healthCheckRegistry = healthCheckRegistry;
        this.healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck());
        this.validator = validatorFactory.getValidator();

        this.lifecycleEnvironment = new LifecycleEnvironment();

        lifecycleEnvironment.addLifeCycleListener(new AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                logHealthChecks();

                enableInjectingConstraintValidatorFactory(validatorFactory);
            }

            @Override
            public void lifeCycleStopping(LifeCycle event) {
                validatorFactory.close();
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

    private void enableInjectingConstraintValidatorFactory(ValidatorFactory validatorFactory) {
        ConstraintValidatorFactory constraintValidatorFactory = validatorFactory.getConstraintValidatorFactory();

        if (constraintValidatorFactory instanceof MutableValidatorFactory) {
            MutableValidatorFactory mutableValidatorFactory = (MutableValidatorFactory) constraintValidatorFactory;
            final ConstraintValidatorFactory injectableValidatorFactory =
                getInjector().getInstance(InjectingConstraintValidatorFactory.class);

            mutableValidatorFactory.setValidatorFactory(injectableValidatorFactory);

            LOGGER.info("Setup InjectingConstraintValidatorFactory.");
        }
    }

    /**
     * simple created, just for test
     */
    public Environment() {
        this("Test",
            Jackson.newObjectMapper(),
            BaseValidator.newConfiguration().buildValidatorFactory(),
            new MetricRegistry(),
            Environment.class.getClassLoader(),
            new HealthCheckRegistry());
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
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
