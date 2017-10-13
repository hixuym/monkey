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

package io.sunflower.migrations;

import java.util.UUID;

import io.sunflower.db.DataSourceFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Subparser;

public class AbstractMigrationTest {

  static {
    ArgumentParsers.setTerminalWidthDetection(false);
  }

  protected static final String UTF_8 = "UTF-8";

  protected static Subparser createSubparser(AbstractLiquibaseCommand<?> command) {
    final Subparser subparser = ArgumentParsers.newArgumentParser("db")
        .addSubparsers()
        .addParser(command.getName())
        .description(command.getDescription());
    command.configure(subparser);
    return subparser;
  }

  protected static TestMigrationConfiguration createConfiguration(String databaseUrl) {
    final DataSourceFactory dataSource = new DataSourceFactory();
    dataSource.setDriverClass("org.h2.Driver");
    dataSource.setUser("sa");
    dataSource.setUrl(databaseUrl);
    return new TestMigrationConfiguration(dataSource);
  }

  protected static String getDatabaseUrl() {
    return "jdbc:h2:mem:" + UUID.randomUUID() + ";db_close_delay=-1";
  }
}
