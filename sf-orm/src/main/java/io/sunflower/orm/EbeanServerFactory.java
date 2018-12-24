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

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.ebeaninternal.server.lib.ShutdownManager;
import io.sunflower.datasource.ManagedDataSource;
import io.sunflower.datasource.PooledDataSourceFactory;
import io.sunflower.lifecycle.Managed;
import io.sunflower.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author michael
 */
public class EbeanServerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbeanServerFactory.class);

    private static final String DB_SUFFIX = "_db";

    public EbeanServer build(OrmBundle<?> bundle,
                             Environment environment,
                             PooledDataSourceFactory dbConfig,
                             List<String> scanPkgs) {
        String name = bundle.name() == null ? environment.getName() + DB_SUFFIX : bundle.name();
        final ManagedDataSource dataSource = dbConfig.build(environment.getMetricRegistry(), environment.getHealthCheckRegistry(), name);
        return build(bundle, environment, dbConfig, dataSource, scanPkgs, name);
    }

    public EbeanServer build(OrmBundle<?> bundle,
                             Environment environment,
                             PooledDataSourceFactory dbConfig,
                             ManagedDataSource dataSource,
                             List<String> scanPkgs,
                             String name) {

        ServerConfig serverConfig = new ServerConfig();

        Properties properties = new Properties();

        for (Map.Entry<String, String> e : dbConfig.getProperties().entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }

        serverConfig.loadFromProperties(properties);

        serverConfig.setPackages(scanPkgs);
        serverConfig.setName(name);
        serverConfig.setDataSource(dataSource);
        serverConfig.setDefaultServer((environment.getName() + DB_SUFFIX).equalsIgnoreCase(name));
        serverConfig.setRegister(true);

        bundle.configure(serverConfig);

        EbeanServer ebeanServer = io.ebean.EbeanServerFactory.create(serverConfig);

        environment.lifecycle().manage(new EbeanServerManager(dataSource));

        return ebeanServer;
    }

    private static class EbeanServerManager implements Managed {
        private final ManagedDataSource dataSource;

        public EbeanServerManager(ManagedDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void start() throws Exception {
            this.dataSource.start();
        }

        @Override
        public void stop() throws Exception {
            this.dataSource.stop();
            ShutdownManager.shutdown();
        }
    }
}
