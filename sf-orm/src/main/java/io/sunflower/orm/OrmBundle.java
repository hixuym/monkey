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

package io.sunflower.orm;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import io.ebean.config.ServerConfig;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.datasource.DatabaseConfiguration;
import io.sunflower.datasource.PooledDataSourceFactory;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.google.inject.matcher.Matchers.*;

/**
 * @author michael
 */
public abstract class OrmBundle<T extends Configuration>
        implements ConfiguredBundle<T>, DatabaseConfiguration<T> {

    private static final AbstractMatcher<Method> DECLARED_BY_OBJECT = new AbstractMatcher<Method>() {
        @Override
        public boolean matches(Method method) {
            return method.getDeclaringClass() == Object.class;
        }
    };

    private static final AbstractMatcher<Method> SYNTHETIC = new AbstractMatcher<Method>() {
        @Override
        public boolean matches(Method method) {
            return method.isSynthetic();
        }
    };

    private final EbeanServerFactory ebeanServerFactory;

    private final ImmutableList<String> scanPkgs;

    protected OrmBundle(String... scanPkgs) {
        this(new EbeanServerFactory(), scanPkgs);
    }

    protected OrmBundle(EbeanServerFactory ebeanServerFactory, String... scanPkgs) {

        ImmutableList.Builder<String> builder = ImmutableList.builder();

        builder.addAll(Arrays.asList(scanPkgs));

        this.scanPkgs = builder.build();

        this.ebeanServerFactory = ebeanServerFactory;
    }

    @Override
    public void run(T configuration, Environment environment) {
        final PooledDataSourceFactory dbConfig = getDataSourceFactory(configuration);

        this.ebeanServerFactory.build(this, environment, dbConfig, scanPkgs);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

        bootstrap.injectorFacotry().register(new AbstractModule() {
            @Override
            protected void configure() {
                // class-level @Txn
                LocalTxnInterceptor txnInterceptor = new LocalTxnInterceptor();

                bindInterceptor(any(),
                        not(SYNTHETIC).and(not(DECLARED_BY_OBJECT)).and(annotatedWith(Transactional.class)), txnInterceptor);
                // Intercept classes annotated with Transactional, but avoid "double"
                // interception when a mathod is also annotated inside an annotated
                // class.
                bindInterceptor(annotatedWith(Transactional.class),
                        not(SYNTHETIC).and(not(DECLARED_BY_OBJECT)).and(not(annotatedWith(Transactional.class))), txnInterceptor);
            }
        });
        bootstrap.addCommand(new DbMigrationCommand());
    }

    /**
     * Override to initialize the name of the bundle (It's used for the bundle health check and
     * database pool getMetricRegistry), default use app name + _db
     */
    protected String name() {
        return null;
    }

    protected void configure(ServerConfig serverConfig) {
    }

}
