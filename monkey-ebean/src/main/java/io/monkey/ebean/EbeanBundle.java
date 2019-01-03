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

package io.monkey.ebean;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.datasource.DatabaseConfiguration;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.google.inject.matcher.Matchers.*;

/**
 * @author michael
 */
public abstract class EbeanBundle<T extends Configuration>
        implements ConfiguredBundle<T>, DatabaseConfiguration<T> {

    private static Logger logger = LoggerFactory.getLogger(EbeanBundle.class);

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

    protected EbeanBundle(String... scanPkgs) {
        this(new EbeanServerFactory(), scanPkgs);
    }

    protected EbeanBundle(EbeanServerFactory ebeanServerFactory, String... scanPkgs) {

        ImmutableList.Builder<String> builder = ImmutableList.builder();

        builder.addAll(Arrays.asList(scanPkgs));

        this.scanPkgs = builder.build();

        this.ebeanServerFactory = ebeanServerFactory;
    }

    @Override
    public void run(T configuration, Environment environment) {
        Stopwatch sw = Stopwatch.createStarted();
        final PooledDataSourceFactory dbConfig = getDataSourceFactory(configuration);

        final EbeanServer server = this.ebeanServerFactory.build(this, environment, dbConfig, scanPkgs);

        environment.guicify().register(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EbeanServer.class).toInstance(server);

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

        sw.stop();

        logger.info("EbeanBundle initialized {}.", sw);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

        bootstrap.addCommand(new DbMigrationCommand());
    }

    /**
     * Override to initialize the name of the bundle (It's used for the bundle health check and
     * database pool metrics), default use app name + _db
     */
    protected String name() {
        return null;
    }

    protected void configure(ServerConfig serverConfig) {
    }

}
