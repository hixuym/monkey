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

import java.util.Collections;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.annotation.Platform;
import io.ebean.config.DbMigrationConfig;
import io.ebean.config.ServerConfig;
import io.ebean.dbmigration.DbMigration;
import io.ebeaninternal.dbmigration.DbOffline;
import io.sunflower.cli.Command;
import io.sunflower.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 *
 * @author michael
 */
public class DbMigrationCommand extends Command {

  public DbMigrationCommand() {
    super("ebean", "generate database migrations.");
  }

  @Override
  public void configure(Subparser subparser) {

    subparser.addArgument("--pkgs")
        .nargs("*")
        .dest("pkgs")
        .setDefault(Collections.singletonList(""))
        .help("scan models packages.");

    subparser.addArgument("--platform")
        .nargs("?")
        .dest("platform")
        .setDefault("MYSQL")
        .choices("MYSQL", "DB2", "ORACLE", "POSTGRES", "H2", "SQLSERVER")
        .help("database type");

    subparser.addArgument("-o", "--output")
        .nargs("?")
        .dest("output")
        .setDefault("src/main/resources")
        .help("database migration file output dir.");

    subparser.addArgument("--name")
        .dest("name")
        .nargs("?")
        .setDefault("db_schame")
        .help("database migration name.");
  }

  @Override
  public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {

    try {
      String platform = namespace.getString("platform");

      DbOffline.setPlatform(platform);
      DbOffline.setGenerateMigration();

      ServerConfig serverConfig = new ServerConfig();

      serverConfig.setPackages(namespace.getList("pkgs"));

      DbMigrationConfig dbMigrationConfig = new DbMigrationConfig();

      dbMigrationConfig.setPlatform(Platform.valueOf(namespace.getString("platform")));

      dbMigrationConfig.setName(namespace.getString("name"));

      serverConfig.setMigrationConfig(dbMigrationConfig);

      EbeanServer server = EbeanServerFactory.create(serverConfig);

      DbMigration dbMigration = DbMigration.create();

      dbMigration.setServer(server);

      dbMigration.setPathToResources(namespace.getString("output"));

      dbMigration.generateMigration();
    } finally {
      DbOffline.reset();
    }
  }

}
