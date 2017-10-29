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

package io.sunflower.testing.junit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.cli.Command;
import io.sunflower.cli.ServerCommand;
import io.sunflower.client.HttpClientBuilder;
import io.sunflower.lifecycle.Managed;
import io.sunflower.setup.Environment;
import io.sunflower.testing.ConfigOverride;
import io.sunflower.testing.SunflowerTestSupport;
import org.apache.http.client.HttpClient;
import org.junit.rules.ExternalResource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author michael
 */
public class SunflowerAppRule<C extends Configuration> extends ExternalResource {

    private final SunflowerTestSupport<C> testSupport;

    private final AtomicInteger recursiveCallCount = new AtomicInteger(0);

    private HttpClient client;

    public SunflowerAppRule(Class<? extends Application<C>> applicationClass) {
        this(applicationClass, (String) null);
    }

    public SunflowerAppRule(Class<? extends Application<C>> applicationClass,
                            @Nullable String configPath,
                            ConfigOverride... configOverrides) {
        this(applicationClass, configPath, Optional.empty(), configOverrides);
    }

    public SunflowerAppRule(Class<? extends Application<C>> applicationClass, String configPath,
                            Optional<String> customPropertyPrefix, ConfigOverride... configOverrides) {
        this(applicationClass, configPath, customPropertyPrefix, ServerCommand::new, configOverrides);
    }

    public SunflowerAppRule(Class<? extends Application<C>> applicationClass, String configPath,
                            Optional<String> customPropertyPrefix, Function<Application<C>,
            Command> commandInstantiator, ConfigOverride... configOverrides) {
        this(new SunflowerTestSupport<>(applicationClass, configPath, customPropertyPrefix,
                commandInstantiator,
                configOverrides));
    }

    /**
     * Alternate constructor that allows specifying exact Configuration object to use, instead of
     * reading a resource and binding it as Configuration object.
     *
     * @since 0.9
     */
    public SunflowerAppRule(Class<? extends Application<C>> applicationClass,
                            C configuration) {
        this(new SunflowerTestSupport<>(applicationClass, configuration));
    }

    /**
     * Alternate constructor that allows specifying the command the Dropwizard application is started
     * with.
     *
     * @since 1.1.0
     */
    public SunflowerAppRule(Class<? extends Application<C>> applicationClass,
                            C configuration, Function<Application<C>, Command> commandInstantiator) {
        this(new SunflowerTestSupport<>(applicationClass, configuration, commandInstantiator));
    }

    public SunflowerAppRule(SunflowerTestSupport<C> testSupport) {
        this.testSupport = testSupport;
    }

    public SunflowerAppRule<C> addListener(final ServiceListener<C> listener) {
        this.testSupport.addListener(new SunflowerTestSupport.ServiceListener<C>() {
            @Override
            public void onRun(C configuration, Environment environment, SunflowerTestSupport<C> rule)
                    throws Exception {
                listener.onRun(configuration, environment, SunflowerAppRule.this);
            }

            @Override
            public void onStop(SunflowerTestSupport<C> rule) throws Exception {
                listener.onStop(SunflowerAppRule.this);
            }
        });
        return this;
    }

    public SunflowerAppRule<C> manage(final Managed managed) {
        return addListener(new ServiceListener<C>() {
            @Override
            public void onRun(C configuration, Environment environment, SunflowerAppRule<C> rule)
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

        public void onRun(T configuration, Environment environment, SunflowerAppRule<T> rule)
                throws Exception {
            // Default NOP
        }

        public void onStop(SunflowerAppRule<T> rule) throws Exception {
            // Default NOP
        }
    }

    public SunflowerTestSupport<C> getTestSupport() {
        return testSupport;
    }

    public void inject(Object test) {
        testSupport.getInjector().injectMembers(test);
    }

    /**
     * Returns a new HTTP Client for performing HTTP requests against the tested Sunflower server. The
     * client can be reused across different tests and automatically closed along with the server. The
     * client can be augmented by overriding the {@link #clientBuilder()} method.
     *
     * @return a new {@link HttpClient} managed by the rule.
     */
    public HttpClient client() {
        synchronized (this) {
            if (client == null) {
                client = clientBuilder().build(getApplication().getName());
            }

            return client;
        }
    }

    protected HttpClientBuilder clientBuilder() {
        return new HttpClientBuilder(getEnvironment());
    }

}
