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

import com.google.common.base.Stopwatch;
import io.ebean.config.ServerConfig;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.datasource.DatabaseConfiguration;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author michael
 */
public class EbeanBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static Logger logger = LoggerFactory.getLogger(EbeanBundle.class);

    private final DatabaseConfiguration<T> databaseConfiguration;

    public EbeanBundle(DatabaseConfiguration<T> databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    public void run(T configuration, Environment environment) {
        Stopwatch sw = Stopwatch.createStarted();

        final PooledDataSourceFactory dbConfig = databaseConfiguration.getDataSourceFactory(configuration);

        final ServerConfig serverConfig = new ServerConfigFactory().build(environment, dbConfig);

        if (serverConfigConsumer != null) {
            serverConfigConsumer.accept(serverConfig);
        }

        environment.guicify().register(new EbeanModule(serverConfig));

        sw.stop();

        logger.info("EbeanBundle initialized {}.", sw);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    //custom server config
    private Consumer<ServerConfig> serverConfigConsumer;

    public void setServerConfigConsumer(Consumer<ServerConfig> serverConfigConsumer) {
        this.serverConfigConsumer = serverConfigConsumer;
    }

}
