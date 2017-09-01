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
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * Created by michael on 17/8/31.
 */
public class InjectionBundle<T extends Configuration> implements ConfiguredBundle<T> {

    public static final String SF_INJECTOR = "$sf_injector_context_key$";

    private final Application application;

    private List<Module> moduleToLoad = Lists.newArrayList();

    public InjectionBundle(Application application) {
        this.application = application;
    }

    public void addModule(Module... modules) {
        this.moduleToLoad.addAll(Arrays.asList(modules));
    }

    public static Injector getInjector(Environment environment) {
        Injector injector = (Injector) environment.getAttribute(SF_INJECTOR);

        if (injector == null) {
            throw new RuntimeException("please add InjectionBundle to Bootstrap.");
        }

        return injector;
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        moduleToLoad.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(application.getConfigurationClass()).toInstance(configuration);
                bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
                bind(Validator.class).toInstance(environment.getValidator());
                bind(MetricRegistry.class).toInstance(environment.metrics());
                bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
            }
        });

        Injector injector = Guice.createInjector(Stage.PRODUCTION, moduleToLoad);

        environment.setAttribute(SF_INJECTOR, injector);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        //nothing to do
    }
}
