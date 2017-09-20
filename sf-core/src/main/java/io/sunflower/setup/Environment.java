package io.sunflower.setup;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.name.Names;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.validation.Validator;

import io.sunflower.inject.setup.GuicyEnvironment;
import io.sunflower.lifecycle.setup.LifecycleEnvironment;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import static java.util.Objects.requireNonNull;

/**
 * A sunflower application's environment.
 */
public class Environment {
    private final String name;
    private final MetricRegistry metricRegistry;
    private final HealthCheckRegistry healthCheckRegistry;

    private final ObjectMapper objectMapper;
    private Validator validator;

    private HttpHandler applicationHandler;

    private final LifecycleEnvironment lifecycleEnvironment;
    private final GuicyEnvironment guicyEnvironment;

    private final AdminEnvironment adminEnvironment;

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
                       Validator validator,
                       MetricRegistry metricRegistry,
                       ClassLoader classLoader,
                       HealthCheckRegistry healthCheckRegistry) {
        this.name = name;
        this.objectMapper = objectMapper;
        this.metricRegistry = metricRegistry;
        this.healthCheckRegistry = healthCheckRegistry;
        this.validator = validator;
        this.classLoader = classLoader;

        this.guicyEnvironment = new GuicyEnvironment();

        this.guicyEnvironment.addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("application.name")).to(getName());
                bind(ObjectMapper.class).toInstance(objectMapper);
                bind(MetricRegistry.class).toInstance(metricRegistry);
                bind(HealthCheckRegistry.class).toInstance(healthCheckRegistry);
                bind(Validator.class).toInstance(validator);
                bind(Environment.class).toInstance(Environment.this);
            }
        });

        this.lifecycleEnvironment = new LifecycleEnvironment();

        this.adminEnvironment = new AdminEnvironment(lifecycleEnvironment, healthCheckRegistry, metricRegistry);

        this.healthCheckExecutorService = this.lifecycle().executorService("TimeBoundHealthCheck-pool-%d")
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
     * Creates an environment with default health check registry
     */
    public Environment(String name,
                       ObjectMapper objectMapper,
                       Validator validator,
                       MetricRegistry metricRegistry,
                       ClassLoader classLoader) {
        this(name, objectMapper, validator, metricRegistry, classLoader, new HealthCheckRegistry());
    }

    /**
     * Returns an {@link ExecutorService} to run time bound health checks
     */
    public ExecutorService getHealthCheckExecutorService() {
        return healthCheckExecutorService;
    }

    /**
     * Returns the application's {@link AdminEnvironment}.
     */
    public AdminEnvironment admin() {
        return adminEnvironment;
    }

    /**
     * Returns the application's {@link LifecycleEnvironment}.
     */
    public LifecycleEnvironment lifecycle() {
        return lifecycleEnvironment;
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
    public Validator getValidator() {
        return validator;
    }

    /**
     * Sets the application's {@link Validator}.
     */
    public void setValidator(Validator validator) {
        this.validator = requireNonNull(validator);
    }

    /**
     * Returns the application's {@link MetricRegistry}.
     */
    public MetricRegistry metrics() {
        return metricRegistry;
    }

    public GuicyEnvironment guicy() {
        return this.guicyEnvironment;
    }

    /**
     *
     * @return application's {@link ClassLoader}.
     */
    public ClassLoader classLoader() {
        return this.classLoader;
    }

    /**
     * Returns the application's {@link HealthCheckRegistry}.
     */
    public HealthCheckRegistry healthChecks() {
        return healthCheckRegistry;
    }

    /**
     * Internal Accessors
     */
    public HttpHandler getApplicationHandler() {

        if (this.applicationHandler == null) {

            this.applicationHandler = exchange -> {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("It Works.");
            };

        }

        return applicationHandler;
    }

    public void setApplicationHandler(HttpHandler applicationHandler) {
        this.applicationHandler = applicationHandler;
    }
}
