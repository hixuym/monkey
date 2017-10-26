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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.sunflower.db.ManagedDataSource;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michael
 */
public class EbeanServerFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(EbeanServerFactory.class);

  public EbeanServer build(EbeanBundle<?> bundle,
      Environment environment,
      PooledDataSourceFactory dbConfig,
      List<String> scanPkgs) {
    return build(bundle, environment, dbConfig, scanPkgs, EbeanBundle.DEFAULT_NAME);
  }

  public EbeanServer build(EbeanBundle<?> bundle,
      Environment environment,
      PooledDataSourceFactory dbConfig,
      List<String> scanPkgs,
      String name) {
    final ManagedDataSource dataSource = dbConfig.build(environment.metrics(), name);
    return build(bundle, environment, dbConfig, dataSource, scanPkgs, name);
  }

  public EbeanServer build(EbeanBundle<?> bundle,
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

    serverConfig.setPackages(scanPkgs);

    serverConfig.loadFromProperties(properties);

    serverConfig.setName(name);
    serverConfig.setDataSource(dataSource);
    serverConfig.setDefaultServer(bundle.isDefault());
    serverConfig.setRegister(true);

    bundle.configure(serverConfig);

    EbeanServer ebeanServer = io.ebean.EbeanServerFactory.create(serverConfig);

    environment.lifecycle().manage(new EbeanServerManager(dataSource));

    return ebeanServer;
  }

}
