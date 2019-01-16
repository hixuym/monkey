/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.ebean.setup;

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
class ServerConfigFactory {

    ServerConfig build(Environment environment, PooledDataSourceFactory dbConfig) {
        final ManagedDataSource dataSource = dbConfig.build(environment);

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

        environment.lifecycle().manage(new Managed() {
            @Override
            public void stop() {
                ShutdownManager.shutdown();
            }
        });

        return serverConfig;
    }
}
