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

package io.monkey.testing.junit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.monkey.Application;
import io.monkey.Configuration;
import io.monkey.cli.Command;
import io.monkey.cli.ServerCommand;
import io.monkey.lifecycle.Managed;
import io.monkey.setup.Environment;
import io.monkey.testing.ConfigOverride;
import io.monkey.testing.MonkeyTestSupport;
import org.junit.rules.ExternalResource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author michael
 */
public class MonkeyAppRule<C extends Configuration> extends ExternalResource {

    private final MonkeyTestSupport<C> testSupport;

    private final AtomicInteger recursiveCallCount = new AtomicInteger(0);

    public MonkeyAppRule(Class<? extends Application<C>> applicationClass) {
        this(applicationClass, (String) null);
    }

    public MonkeyAppRule(Class<? extends Application<C>> applicationClass,
                         @Nullable String configPath,
                         ConfigOverride... configOverrides) {
        this(applicationClass, configPath, Optional.empty(), configOverrides);
    }

    public MonkeyAppRule(Class<? extends Application<C>> applicationClass, String configPath,
                         Optional<String> customPropertyPrefix, ConfigOverride... configOverrides) {
        this(applicationClass, configPath, customPropertyPrefix, ServerCommand::new, configOverrides);
    }

    public MonkeyAppRule(Class<? extends Application<C>> applicationClass, String configPath,
                         Optional<String> customPropertyPrefix, Function<Application<C>,
            Command> commandInstantiator, ConfigOverride... configOverrides) {
        this(new MonkeyTestSupport<>(applicationClass, configPath, customPropertyPrefix,
                commandInstantiator,
                configOverrides));
    }

    /**
     * Alternate constructor that allows specifying exact Configuration object to use, instead of
     * reading a resource and binding it as Configuration object.
     *
     * @since 0.9
     */
    public MonkeyAppRule(Class<? extends Application<C>> applicationClass,
                         C configuration) {
        this(new MonkeyTestSupport<>(applicationClass, configuration));
    }

    /**
     * Alternate constructor that allows specifying the command the Dropwizard application is started
     * with.
     *
     * @since 1.1.0
     */
    public MonkeyAppRule(Class<? extends Application<C>> applicationClass,
                         C configuration, Function<Application<C>, Command> commandInstantiator) {
        this(new MonkeyTestSupport<>(applicationClass, configuration, commandInstantiator));
    }

    public MonkeyAppRule(MonkeyTestSupport<C> testSupport) {
        this.testSupport = testSupport;
    }

    public MonkeyAppRule<C> addListener(final ServiceListener<C> listener) {
        this.testSupport.addListener(new MonkeyTestSupport.ServiceListener<C>() {
            @Override
            public void onRun(C configuration, Environment environment, MonkeyTestSupport<C> rule)
                    throws Exception {
                listener.onRun(configuration, environment, MonkeyAppRule.this);
            }

            @Override
            public void onStop(MonkeyTestSupport<C> rule) throws Exception {
                listener.onStop(MonkeyAppRule.this);
            }
        });
        return this;
    }

    public MonkeyAppRule<C> manage(final Managed managed) {
        return addListener(new ServiceListener<C>() {
            @Override
            public void onRun(C configuration, Environment environment, MonkeyAppRule<C> rule)
                    throws Exception {
                environment.lifecycle().manage(managed);
            }
        });
    }

    @Override
    protected void before() {
        if (recursiveCallCount.getAndIncrement() == 0) {
            testSupport.before();
        }
    }

    @Override
    protected void after() {
        if (recursiveCallCount.decrementAndGet() == 0) {
            testSupport.after();
        }
    }

    public C getConfiguration() {
        return testSupport.getConfiguration();
    }

    public Application<C> newApplication() {
        return testSupport.newApplication();
    }

    @SuppressWarnings("unchecked")
    public <A extends Application<C>> A getApplication() {
        return testSupport.getApplication();
    }

    public Environment getEnvironment() {
        return testSupport.getEnvironment();
    }

    public ObjectMapper getObjectMapper() {
        return testSupport.getObjectMapper();
    }

    public abstract static class ServiceListener<T extends Configuration> {

        public void onRun(T configuration, Environment environment, MonkeyAppRule<T> rule)
                throws Exception {
            // Default NOP
        }

        public void onStop(MonkeyAppRule<T> rule) throws Exception {
            // Default NOP
        }
    }

    public MonkeyTestSupport<C> getTestSupport() {
        return testSupport;
    }

    public void inject(Object test) {
        testSupport.getInjector().injectMembers(test);
    }
}
