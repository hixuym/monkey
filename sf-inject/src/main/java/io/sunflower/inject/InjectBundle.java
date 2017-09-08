package io.sunflower.inject;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import javax.validation.Validator;

import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.inject.lifecycle.LifecycleSupport;
import io.sunflower.inject.scheduler.SchedulerSupport;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * Created by michael on 17/8/31.
 */
public class InjectBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private final Application application;

    private List<Module> moduleToLoad = Lists.newArrayList();
    private Stage stage = Stage.PRODUCTION;

    public InjectBundle(Application application) {
        this.application = application;
    }

    public InjectBundle modules(Module... modules) {
        this.moduleToLoad.addAll(Arrays.asList(modules));
        return this;
    }

    public InjectBundle stage(Stage stage) {
        this.stage = stage;
        return this;
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        Injector parent = Guice.createInjector(stage, new AbstractModule() {
            @Override
            @SuppressWarnings("unchecked")
            protected void configure() {
                bind(application.getConfigurationClass()).toInstance(configuration);
                bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
                bind(Validator.class).toInstance(environment.getValidator());
                bind(MetricRegistry.class).toInstance(environment.metrics());
                bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
            }
        });

        moduleToLoad.add(LifecycleSupport.getModule());
        moduleToLoad.add(SchedulerSupport.getModule());

        environment.putInstance(Injector.class, parent.createChildInjector(moduleToLoad));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        //nothing to do
    }
}
