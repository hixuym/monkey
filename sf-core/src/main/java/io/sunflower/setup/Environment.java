package io.sunflower.setup;

import com.google.common.collect.Maps;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import javax.validation.Validator;

import static java.util.Objects.requireNonNull;

/**
 * A Sunflower application's environment.
 */
public class Environment {
    private final String name;
    private final MetricRegistry metricRegistry;
    private final HealthCheckRegistry healthCheckRegistry;
    private final ObjectMapper objectMapper;

    private Validator validator;

    /**
     *
     */
    private final Map<String, Object> attributes = Maps.newConcurrentMap();

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

    public void setAttribute(String key, Object val) {
        this.attributes.put(key, val);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
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

    /**
     * Returns the application's {@link HealthCheckRegistry}.
     */
    public HealthCheckRegistry healthChecks() {
        return healthCheckRegistry;
    }
}
