package io.sunflower.setup;

import com.google.common.collect.Maps;
import com.google.common.reflect.MutableTypeToInstanceMap;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import io.sunflower.lifecycle.setup.LifecycleEnvironment;

/**
 * A Sunflower application's environment.
 */
public class Environment {
    private final String name;
    private final MetricRegistry metricRegistry;
    private final HealthCheckRegistry healthCheckRegistry;
    private final ObjectMapper objectMapper;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private final LifecycleEnvironment lifecycleEnvironment;

    private final ExecutorService healthCheckExecutorService;

    private final Map<String, Object> attributes = Maps.newConcurrentMap();

    private final TypeToInstanceMap<Object> typeToInstanceMap = new MutableTypeToInstanceMap<>();

    /**
     * Creates a new environment.
     *
     * @param name         the name of the application
     * @param objectMapper the {@link ObjectMapper} for the application
     */
    public Environment(String name,
                       ObjectMapper objectMapper,
                       Validator validator,
                       ValidatorFactory validatorFactory,
                       MetricRegistry metricRegistry,
                       ClassLoader classLoader,
                       HealthCheckRegistry healthCheckRegistry) {
        this.name = name;
        this.objectMapper = objectMapper;
        this.metricRegistry = metricRegistry;
        this.healthCheckRegistry = healthCheckRegistry;
        this.validator = validator;
        this.validatorFactory = validatorFactory;

        this.lifecycleEnvironment = new LifecycleEnvironment();

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
                       ValidatorFactory validatorFactory,
                       MetricRegistry metricRegistry,
                       ClassLoader classLoader) {
        this(name, objectMapper, validator, validatorFactory, metricRegistry, classLoader, new HealthCheckRegistry());
    }

    public void setAttribute(String key, Object val) {
        this.attributes.put(key, val);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public <T> void bind(Class<T> type, T instance) {
        this.typeToInstanceMap.putInstance(type, instance);
    }

    public <T> T getInstance(Class<T> type) {
        return this.typeToInstanceMap.getInstance(type);
    }

    public TypeToInstanceMap<Object> getTypeToInstanceMap() {
        return typeToInstanceMap;
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

    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
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
     * Returns the application's {@link LifecycleEnvironment}.
     */
    public LifecycleEnvironment lifecycle() {
        return lifecycleEnvironment;
    }

    /**
     * Returns an {@link ExecutorService} to run time bound health checks
     */
    public ExecutorService getHealthCheckExecutorService() {
        return healthCheckExecutorService;
    }

}
