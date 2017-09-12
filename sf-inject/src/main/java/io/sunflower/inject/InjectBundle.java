package io.sunflower.inject;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.doclint.Env;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    private Injector injector;

    public InjectBundle(Application application) {
        this.application = application;
    }

    public InjectBundle install(Module... modules) {
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
            protected void configure() {
                bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
                bind(Validator.class).toInstance(environment.getValidator());
                bind(MetricRegistry.class).toInstance(environment.metrics());
                bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
                bind(Environment.class).toInstance(environment);
                TypeToInstanceMap<Object> typeToInstanceMap = environment.getTypeToInstanceMap();

                for (Map.Entry<TypeToken<? extends Object>, Object> e : typeToInstanceMap.entrySet()) {
                    bind((Class) e.getKey().getType()).toInstance(e.getValue());
                }
            }
        });

        moduleToLoad.add(LifecycleSupport.getModule());
        moduleToLoad.add(SchedulerSupport.getModule());

        this.injector = parent.createChildInjector(moduleToLoad);

        environment.bind(Injector.class, injector);
    }

    public Injector getInjector() {
        return injector;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        //nothing to do
    }
}
