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

package io.sunflower.ebean;

import com.google.common.collect.ImmutableList;
import com.google.inject.name.Names;

import java.util.Arrays;

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.db.DatabaseConfiguration;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.util.Duration;

public abstract class EbeanBundle<T extends Configuration> implements ConfiguredBundle<T>, DatabaseConfiguration<T> {
    public static final String DEFAULT_NAME = "default";

    private EbeanServer ebeanServer;
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

    public boolean isDefault() {
        return DEFAULT_NAME.equalsIgnoreCase(name());
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        final PooledDataSourceFactory dbConfig = getDataSourceFactory(configuration);

        this.ebeanServer = this.ebeanServerFactory.build(this, environment, dbConfig, scanPkgs, name());

        environment.guicey().addModule((binder) -> {
            if (isDefault()) {
                binder.bind(EbeanServer.class).toInstance(ebeanServer);
            } else {
                binder.bind(EbeanServer.class).annotatedWith(Names.named(name())).toInstance(ebeanServer);
            }
        });

        environment.healthChecks().register(name(),
            new EbeanServerHealthCheck(
                environment.getHealthCheckExecutorService(),
                dbConfig.getValidationQueryTimeout().orElse(Duration.seconds(5)),
                ebeanServer,
                dbConfig.getValidationQuery()));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addCommand(new DbMigrationCommand());
    }

    /**
     * Override to configure the name of the bundle (It's used for the bundle health check and database pool metrics)
     */
    protected String name() {
        return DEFAULT_NAME;
    }

    protected void configure(ServerConfig serverConfig) {
    }

    public EbeanServer getEbeanServer() {
        return ebeanServer;
    }
}
