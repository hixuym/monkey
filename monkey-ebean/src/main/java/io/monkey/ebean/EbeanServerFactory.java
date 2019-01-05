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

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.ebeaninternal.server.lib.ShutdownManager;
import io.monkey.datasource.ManagedDataSource;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.lifecycle.Managed;
import io.monkey.setup.Environment;

import java.util.Map;
import java.util.Properties;

/**
 * @author michael
 */
class EbeanServerFactory {

    EbeanServer build(EbeanBundle<?> bundle,
                      Environment environment,
                      PooledDataSourceFactory dbConfig) {
        final ManagedDataSource dataSource = dbConfig.build(environment.metrics(), environment.healthChecks());
        return build(bundle, environment, dbConfig, dataSource);
    }

    private EbeanServer build(EbeanBundle<?> bundle,
                              Environment environment,
                              PooledDataSourceFactory dbConfig,
                              ManagedDataSource dataSource) {

        ServerConfig serverConfig = new ServerConfig();

        Properties properties = new Properties();

        for (Map.Entry<String, String> e : dbConfig.getProperties().entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }

        serverConfig.loadFromProperties(properties);

        serverConfig.setName(dbConfig.getDatabaseName());
        serverConfig.setDataSource(dataSource);
        serverConfig.setDefaultServer(dbConfig.isDefault());
        serverConfig.setRegister(true);

        bundle.configure(serverConfig);

        EbeanServer ebeanServer = io.ebean.EbeanServerFactory.create(serverConfig);

        FlywayMigrationEngine flywayMigrationEngine = new FlywayMigrationEngine(ebeanServer, environment.getName());

        flywayMigrationEngine.generate();

        flywayMigrationEngine.migrate();

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
