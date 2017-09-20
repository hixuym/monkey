package io.sunflower.setup;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.Validator;

import io.sunflower.metrics.MetricsModule;

public class EnvironmentModule extends AbstractModule {

    private final Environment environment;

    public EnvironmentModule(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("application.name")).to(environment.getName());
        bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
        bind(MetricRegistry.class).toInstance(environment.metrics());
        bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
        bind(Validator.class).toInstance(environment.getValidator());
        bind(Environment.class).toInstance(environment);

        install(new MetricsModule());
    }
}
