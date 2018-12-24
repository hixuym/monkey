/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.cli.Command;
import io.sunflower.cli.ServerCommand;
import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.lifecycle.Managed;
import io.sunflower.server.Server;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * A test support class for starting and stopping your application at the commit and end of a test
 * class. <p> By default, the {@link Application} will be constructed using reflection to invoke the
 * nullary constructor. If your application does not provide a public nullary constructor, you will
 * need to override the {@link #newApplication()} method to provide your application instance(s).
 * </p>
 *
 * @author michael
 * @param <C> the configuration type
 */
public class SunflowerTestSupport<C extends Configuration> {

    protected final Class<? extends Application<C>> applicationClass;
    protected final String configPath;
    protected final Set<ConfigOverride> configOverrides;
    protected final Optional<String> customPropertyPrefix;
    protected final Function<Application<C>, Command> commandInstantiator;

    /**
     * Flag that indicates whether instance was constructed with an explicit Configuration object or
     * not; handling of the two cases differ. Needed because state of {@link #configuration} changes
     * during lifecycle.
     */
    protected final boolean explicitConfig;

    protected C configuration;
    protected Application<C> application;
    protected Environment environment;
    protected List<ServiceListener<C>> listeners = new ArrayList<>();

    protected Server server;

    public SunflowerTestSupport(Class<? extends Application<C>> applicationClass,
                                @Nullable String configPath,
                                ConfigOverride... configOverrides) {
        this(applicationClass, configPath, Optional.empty(), configOverrides);
    }

    public SunflowerTestSupport(Class<? extends Application<C>> applicationClass, String configPath,
                                Optional<String> customPropertyPrefix, ConfigOverride... configOverrides) {
        this(applicationClass, configPath, customPropertyPrefix, ServerCommand::new, configOverrides);
    }

    public SunflowerTestSupport(Class<? extends Application<C>> applicationClass, String configPath,
                                Optional<String> customPropertyPrefix,
                                Function<Application<C>, Command> commandInstantiator,
                                ConfigOverride... configOverrides) {
        this.applicationClass = applicationClass;
        this.configPath = configPath;
        this.configOverrides = ImmutableSet
                .copyOf(firstNonNull(configOverrides, new ConfigOverride[0]));
        this.customPropertyPrefix = customPropertyPrefix;
        explicitConfig = false;
        this.commandInstantiator = commandInstantiator;
    }

    /**
     * Alternative constructor that may be used to directly provide Configuration to use, instead of
     * specifying resource path for locating data to create Configuration.
     *
     * @param applicationClass Type of Application to create
     * @param configuration    Pre-constructed configuration object caller provides; will not be
     *                         manipulated in any way, no overriding
     * @since 0.9
     */
    public SunflowerTestSupport(Class<? extends Application<C>> applicationClass,
                                C configuration) {
        this(applicationClass, configuration, ServerCommand::new);
    }


    /**
     * Alternate constructor that allows specifying the command the Dropwizard application is started
     * with.
     *
     * @param applicationClass    Type of Application to create
     * @param configuration       Pre-constructed configuration object caller provides; will not be
     *                            manipulated in any way, no overriding
     * @param commandInstantiator The {@link Function} used to instantiate the {@link Command} used to
     *                            commit the Application
     * @since 1.1.0
     */
    public SunflowerTestSupport(Class<? extends Application<C>> applicationClass,
                                C configuration, Function<Application<C>,
            Command> commandInstantiator) {
        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Can not pass null configuration for explicitly configured instance");
        }
        this.applicationClass = applicationClass;
        configPath = "";
        configOverrides = ImmutableSet.of();
        customPropertyPrefix = Optional.empty();
        this.configuration = configuration;
        explicitConfig = true;
        this.commandInstantiator = commandInstantiator;
    }

    public void before() {
        applyConfigOverrides();
        startIfRequired();
    }

    public void after() {
        try {
            stopIfRequired();
        } finally {
            resetConfigOverrides();
        }
    }

    private void stopIfRequired() {
        if (server != null) {
            for (ServiceListener<C> listener : listeners) {
                try {
                    listener.onStop(this);
                } catch (Exception ignored) {
                }
            }
            try {
                server.stop();
            } catch (Exception e) {
                Throwables.throwIfUnchecked(e);
                throw new RuntimeException(e);
            } finally {
                server = null;
            }
        }

        // Don't leak appenders into other test cases
        configuration.getLoggingFactory().reset();
    }

    private void startIfRequired() {
        if (server != null) {
            return;
        }
        try {
            application = newApplication();

            final Bootstrap<C> bootstrap = new Bootstrap<C>(application) {
                @Override
                public void run(C configuration, Environment environment) throws Exception {
                    environment.lifecycle()
                            .addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
                                @Override
                                public void lifeCycleStarted(LifeCycle event) {
                                    if (event instanceof Server) {
                                        server = (Server) event;
                                    }
                                }
                            });
                    SunflowerTestSupport.this.configuration = configuration;
                    SunflowerTestSupport.this.environment = environment;
                    super.run(configuration, environment);
                    for (ServiceListener<C> listener : listeners) {
                        try {
                            listener.onRun(configuration, environment, SunflowerTestSupport.this);
                        } catch (Exception ex) {
                            throw new RuntimeException("Error running app rule commit listener", ex);
                        }
                    }
                }
            };

            if (explicitConfig) {
                bootstrap.setConfigurationFactoryFactory((klass, validator, objectMapper, propertyPrefix) ->
                        new PojoConfigurationFactory<>(configuration));
            } else if (customPropertyPrefix.isPresent()) {
                bootstrap.setConfigurationFactoryFactory((klass, validator, objectMapper, propertyPrefix) ->
                        new YamlConfigurationFactory<>(klass, validator, objectMapper,
                                customPropertyPrefix.get()));
            }

            application.initialize(bootstrap);
            final Command command = commandInstantiator.apply(application);

            final ImmutableMap.Builder<String, Object> file = ImmutableMap.builder();
            if (!Strings.isNullOrEmpty(configPath)) {
                file.put("file", configPath);
            }
            final Namespace namespace = new Namespace(file.build());

            command.run(bootstrap, namespace);
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    public SunflowerTestSupport<C> addListener(ServiceListener<C> listener) {
        this.listeners.add(listener);
        return this;
    }

    public SunflowerTestSupport<C> manage(final Managed managed) {
        return addListener(new ServiceListener<C>() {
            @Override
            public void onRun(C configuration, Environment environment, SunflowerTestSupport<C> rule)
                    throws Exception {
                environment.lifecycle().manage(managed);
            }
        });
    }

    private void resetConfigOverrides() {
        for (ConfigOverride configOverride : configOverrides) {
            configOverride.removeFromSystemProperties();
        }
    }

    private void applyConfigOverrides() {
        for (ConfigOverride configOverride : configOverrides) {
            configOverride.addToSystemProperties();
        }
    }

    public C getConfiguration() {
        return configuration;
    }

    public Application<C> newApplication() {
        try {
            return applicationClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Injector getInjector() {
        return environment.getInjector();
    }

    @SuppressWarnings("unchecked")
    public <A extends Application<C>> A getApplication() {
        return (A) application;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public ObjectMapper getObjectMapper() {
        return getEnvironment().getObjectMapper();
    }

    public abstract static class ServiceListener<T extends Configuration> {

        public void onRun(T configuration, Environment environment, SunflowerTestSupport<T> rule)
                throws Exception {
            // Default NOP
        }

        public void onStop(SunflowerTestSupport<T> rule) throws Exception {
            // Default NOP
        }
    }
}
