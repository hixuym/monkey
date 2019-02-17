/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.context;

import io.micronaut.context.*;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.env.*;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanConfiguration;
import io.micronaut.inject.BeanDefinitionReference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Michael
 * Created at: 2019/2/17 16:28
 */
public class MonkeyApplicationContext extends DefaultApplicationContext {

    private final ClassPathResourceLoader resourceLoader;
    private final ConversionService conversionService;
    private Environment environment;

    private Iterable<BeanConfiguration> resolvedConfigurations;
    private List<BeanDefinitionReference> resolvedBeanReferences;

    /**
     * Construct a new ApplicationContext for the given environment name.
     *
     * @param environmentNames The environment names
     */
    public MonkeyApplicationContext(String... environmentNames) {
        this(ClassPathResourceLoader.defaultLoader(DefaultBeanContext.class.getClassLoader()), environmentNames);
    }

    /**
     * Construct a new ApplicationContext for the given environment name and classloader.
     *
     * @param environmentNames The environment names
     * @param resourceLoader   The class loader
     */
    public MonkeyApplicationContext(ClassPathResourceLoader resourceLoader, String... environmentNames) {
        super(resourceLoader, environmentNames);
        this.conversionService = getConversionService();
        this.resourceLoader = resourceLoader;
        this.environment = new RuntimeConfigureNoDeducedEnvironment(environmentNames);
    }

    @Override
    protected Iterable<BeanConfiguration> resolveBeanConfigurations() {
        if (resolvedConfigurations != null) {
            return resolvedConfigurations;
        }
        return super.resolveBeanConfigurations();
    }

    @Override
    protected List<BeanDefinitionReference> resolveBeanDefinitionReferences() {
        if (resolvedBeanReferences != null) {
            return resolvedBeanReferences;
        }
        return super.resolveBeanDefinitionReferences();
    }

    @Override
    protected DefaultEnvironment createEnvironment(String... environmentNames) {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }


    /**
     * Run the {@link ApplicationContext}. This method will instantiate a new {@link ApplicationContext} and
     * call {@link #start()}.
     *
     * @param environments The environments to use
     * @return The running {@link ApplicationContext}
     */
    public static ApplicationContext run(String... environments) {
        return build(environments).start();
    }

    /**
     * Run the {@link ApplicationContext}. This method will instantiate a new {@link ApplicationContext} and
     * call {@link #start()}.
     *
     * @return The running {@link ApplicationContext}
     */
    public static ApplicationContext run() {
        return run(StringUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Run the {@link ApplicationContext} with the given type. Returning an instance of the type. Note this method
     * should not be used.
     * If the {@link ApplicationContext} requires graceful shutdown unless the returned bean takes responsibility for
     * shutting down the context.
     *
     * @param properties   Additional properties
     * @param environments The environment names
     * @return The running {@link ApplicationContext}
     */
    public static ApplicationContext run(Map<String, Object> properties, String... environments) {
        PropertySource propertySource = PropertySource.of(PropertySource.CONTEXT, properties, SystemPropertiesPropertySource.POSITION + 100);
        return run(propertySource, environments);
    }

    /**
     * Run the {@link ApplicationContext} with the given type. Returning an instance of the type. Note this method
     * should not be used.
     * If the {@link ApplicationContext} requires graceful shutdown unless the returned bean takes responsibility for
     * shutting down the context.
     *
     * @param properties   Additional properties
     * @param environments The environment names
     * @return The running {@link ApplicationContext}
     */
    public static ApplicationContext run(PropertySource properties, String... environments) {
        return build(environments)
            .propertySources(properties)
            .start();
    }

    /**
     * Run the {@link ApplicationContext} with the given type. Returning an instance of the type. Note this method
     * should not be used.
     * If the {@link ApplicationContext} requires graceful shutdown unless the returned bean takes responsibility for
     * shutting down the context.
     *
     * @param type         The type of the bean to run
     * @param environments The environments to use
     * @param <T>          The type
     * @return The running bean
     */
    public static <T extends AutoCloseable> T run(Class<T> type, String... environments) {
        return run(type, Collections.emptyMap(), environments);
    }

    /**
     * Run the {@link ApplicationContext} with the given type. Returning an instance of the type. Note this method
     * should not be used.
     * If the {@link ApplicationContext} requires graceful shutdown unless the returned bean takes responsibility for
     * shutting down the context.
     *
     * @param type         The type of the bean to run
     * @param properties   Additional properties
     * @param environments The environment names
     * @param <T>          The type
     * @return The running bean
     */
    public static <T extends AutoCloseable> T run(Class<T> type, Map<String, Object> properties, String... environments) {
        PropertySource propertySource = PropertySource.of(PropertySource.CONTEXT, properties, SystemPropertiesPropertySource.POSITION + 100);
        return run(type, propertySource, environments);
    }

    /**
     * Run the {@link ApplicationContext} with the given type. Returning an instance of the type. Note this method
     * should not be used.
     * If the {@link ApplicationContext} requires graceful shutdown unless the returned bean takes responsibility for
     * shutting down the context.
     *
     * @param type           The environment to use
     * @param propertySource Additional properties
     * @param environments   The environment names
     * @param <T>            The type
     * @return The running {@link BeanContext}
     */
    public static <T extends AutoCloseable> T run(Class<T> type, PropertySource propertySource, String... environments) {
        T bean = build(environments)
            .mainClass(type)
            .propertySources(propertySource)
            .start()
            .getBean(type);
        if (bean != null) {
            if (bean instanceof LifeCycle) {
                LifeCycle lifeCycle = (LifeCycle) bean;
                if (!lifeCycle.isRunning()) {
                    lifeCycle.start();
                }
            }
        }

        return bean;
    }

    /**
     * Build a {@link ApplicationContext}.
     *
     * @param environments The environments to use
     * @return The built, but not yet running {@link ApplicationContext}
     */
    public static ApplicationContextBuilder build(String... environments) {
        return new MonkeyApplicationContextBuilder()
            .environments(environments);
    }

    /**
     * Build a {@link ApplicationContext}.
     *
     * @param properties   The properties
     * @param environments The environments to use
     * @return The built, but not yet running {@link ApplicationContext}
     */
    public static ApplicationContextBuilder build(Map<String, Object> properties, String... environments) {
        return new MonkeyApplicationContextBuilder()
            .properties(properties)
            .environments(environments);
    }

    /**
     * Build a {@link ApplicationContext}.
     *
     * @return The built, but not yet running {@link ApplicationContext}
     */
    public static ApplicationContextBuilder build() {
        return new MonkeyApplicationContextBuilder();
    }

    /**
     * Run the {@link BeanContext}. This method will instantiate a new {@link BeanContext} and call {@link #start()}
     *
     * @param classLoader  The classloader to use
     * @param environments The environments to use
     * @return The running {@link ApplicationContext}
     */
    public static ApplicationContext run(ClassLoader classLoader, String... environments) {
        return build(classLoader, environments).start();
    }

    /**
     * Build a {@link ApplicationContext}.
     *
     * @param classLoader  The classloader to use
     * @param environments The environment to use
     * @return The built, but not yet running {@link ApplicationContext}
     */
    public static ApplicationContextBuilder build(ClassLoader classLoader, String... environments) {
        return build(environments)
            .classLoader(classLoader);
    }

    /**
     * Build a {@link ApplicationContext}.
     *
     * @param mainClass    The main class of the application
     * @param environments The environment to use
     * @return The built, but not yet running {@link ApplicationContext}
     */
    public static ApplicationContextBuilder build(Class mainClass, String... environments) {
        return build(environments)
            .mainClass(mainClass);
    }

    /**
     * Bootstraop property source implementation.
     */
    @SuppressWarnings("MagicNumber")
    private static class BootstrapPropertySource implements PropertySource {
        private final PropertySource delegate;

        BootstrapPropertySource(PropertySource bootstrapPropertySource) {
            this.delegate = bootstrapPropertySource;
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public PropertyConvention getConvention() {
            return delegate.getConvention();
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public Object get(String key) {
            return delegate.get(key);
        }

        @Override
        public Iterator<String> iterator() {
            return delegate.iterator();
        }

        @Override
        public int getOrder() {
            // lower priority than application property sources
            return delegate.getOrder() + 10;
        }
    }

    /**
     * Bootstrap environment.
     */
    private static class BootstrapEnvironment extends DefaultEnvironment {
        BootstrapEnvironment(ClassPathResourceLoader resourceLoader, ConversionService conversionService, String... activeEnvironments) {
            super(resourceLoader, conversionService, activeEnvironments);
        }

        @Override
        protected String getPropertySourceRootName() {
            String bootstrapName = System.getProperty(BOOTSTRAP_NAME_PROPERTY);
            return StringUtils.isNotEmpty(bootstrapName) ? bootstrapName : BOOTSTRAP_NAME;
        }

        @Override
        protected boolean shouldDeduceEnvironments() {
            return false;
        }
    }

    /**
     * Bootstrap application context.
     */
    private class BootstrapApplicationContext extends DefaultApplicationContext {
        private final MonkeyApplicationContext.BootstrapEnvironment bootstrapEnvironment;

        BootstrapApplicationContext(MonkeyApplicationContext.BootstrapEnvironment bootstrapEnvironment, String... activeEnvironments) {
            super(resourceLoader, activeEnvironments);
            this.bootstrapEnvironment = bootstrapEnvironment;
        }

        @Override
        public Environment getEnvironment() {
            return bootstrapEnvironment;
        }

        @Override
        protected MonkeyApplicationContext.BootstrapEnvironment createEnvironment(String... environmentNames) {
            return bootstrapEnvironment;
        }

        @Override
        protected List<BeanDefinitionReference> resolveBeanDefinitionReferences() {
            List<BeanDefinitionReference> refs = super.resolveBeanDefinitionReferences();
            // we cache the resolved beans in a local field to avoid the I/O cost of resolving them twice
            // once for the bootstrap context and again for the main context
            resolvedBeanReferences = refs;
            return refs.stream()
                .filter(ref -> ref.isAnnotationPresent(BootstrapContextCompatible.class))
                .collect(Collectors.toList());
        }

        @Override
        protected Iterable<BeanConfiguration> resolveBeanConfigurations() {
            Iterable<BeanConfiguration> beanConfigurations = super.resolveBeanConfigurations();
            // we cache the resolved configurations in a local field to avoid the I/O cost of resolving them twice
            // once for the bootstrap context and again for the main context
            resolvedConfigurations = beanConfigurations;
            return beanConfigurations;
        }

        @Override
        protected void startEnvironment() {
            registerSingleton(Environment.class, bootstrapEnvironment);
        }

        @Override
        protected void initializeEventListeners() {
            // no-op .. Bootstrap context disallows bean event listeners
        }

        @Override
        protected void initializeContext(List<BeanDefinitionReference> contextScopeBeans, List<BeanDefinitionReference> processedBeans) {
            // no-op .. @Context scope beans are not started for bootstrap
        }

        @Override
        protected void processParallelBeans() {
            // no-op
        }

        @Override
        public void publishEvent(Object event) {
            // no-op .. the bootstrap context shouldn't publish events
        }

    }

    /**
     * Runtime configured environment.
     */
    private class RuntimeConfigureNoDeducedEnvironment extends DefaultEnvironment {

        private final boolean isRuntimeConfigured;
        private BootstrapPropertySourceLocator bootstrapPropertySourceLocator;
        private MonkeyApplicationContext.BootstrapEnvironment bootstrapEnvironment;

        RuntimeConfigureNoDeducedEnvironment(String... environmentNames) {
            super(MonkeyApplicationContext.this.resourceLoader, MonkeyApplicationContext.this.conversionService, environmentNames);
            this.isRuntimeConfigured = Boolean.getBoolean(Environment.BOOTSTRAP_CONTEXT_PROPERTY) ||
                MonkeyApplicationContext.this.resourceLoader.getResource(Environment.BOOTSTRAP_NAME + ".yml").isPresent() ||
                MonkeyApplicationContext.this.resourceLoader.getResource(Environment.BOOTSTRAP_NAME + ".properties").isPresent();
        }

        boolean isRuntimeConfigured() {
            return isRuntimeConfigured;
        }

        @Override
        public Environment stop() {
            return super.stop();
        }

        @Override
        protected synchronized List<PropertySource> readPropertySourceList(String name) {

            if (isRuntimeConfigured) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Reading Startup environment from bootstrap.yml");
                }

                Set<String> activeNames = getActiveNames();
                String[] environmentNamesArray = activeNames.toArray(new String[0]);
                if (this.bootstrapEnvironment == null) {
                    this.bootstrapEnvironment = createBootstrapEnvironment(environmentNamesArray);
                }
                BootstrapPropertySourceLocator bootstrapPropertySourceLocator = resolveBootstrapPropertySourceLocator(environmentNamesArray);

                for (PropertySource propertySource : bootstrapPropertySourceLocator.findPropertySources(bootstrapEnvironment)) {
                    addPropertySource(propertySource);
                }

                Collection<PropertySource> bootstrapPropertySources = bootstrapEnvironment.getPropertySources();
                for (PropertySource bootstrapPropertySource : bootstrapPropertySources) {
                    addPropertySource(new MonkeyApplicationContext.BootstrapPropertySource(bootstrapPropertySource));
                }
                return super.readPropertySourceList(name);
            } else {
                return super.readPropertySourceList(name);
            }
        }

        private BootstrapPropertySourceLocator resolveBootstrapPropertySourceLocator(String... environmentNames) {
            if (this.bootstrapPropertySourceLocator == null) {

                MonkeyApplicationContext.BootstrapApplicationContext bootstrapContext = new MonkeyApplicationContext.BootstrapApplicationContext(bootstrapEnvironment, environmentNames);
                bootstrapContext.start();
                if (bootstrapContext.containsBean(BootstrapPropertySourceLocator.class)) {
                    initializeTypeConverters(bootstrapContext);
                    bootstrapPropertySourceLocator = bootstrapContext.getBean(BootstrapPropertySourceLocator.class);
                } else {
                    bootstrapPropertySourceLocator = BootstrapPropertySourceLocator.EMPTY_LOCATOR;
                }
            }
            return this.bootstrapPropertySourceLocator;
        }

        private MonkeyApplicationContext.BootstrapEnvironment createBootstrapEnvironment(String... environmentNames) {
            MonkeyApplicationContext.BootstrapEnvironment bootstrapEnvironment = new MonkeyApplicationContext.BootstrapEnvironment(
                resourceLoader,
                conversionService,
                environmentNames);

            for (PropertySource source : propertySources.values()) {
                bootstrapEnvironment.addPropertySource(source);
            }
            bootstrapEnvironment.start();
            for (String pkg : bootstrapEnvironment.getPackages()) {
                addPackage(pkg);
            }

            return bootstrapEnvironment;
        }

        @Override
        protected boolean shouldDeduceEnvironments() {
            return false;
        }
    }

}
