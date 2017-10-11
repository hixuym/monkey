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

import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import liquibase.Liquibase;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class DbDropAllCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

  public DbDropAllCommand(DatabaseConfiguration<T> strategy, Class<T> configurationClass,
      String migrationsFileName) {
    super("drop-all", "Delete all user-owned objects from the database.", strategy,
        configurationClass, migrationsFileName);
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);
    subparser.addArgument("--confirm-delete-everything")
        .action(Arguments.storeTrue())
        .required(true)
        .help("indicate you understand this deletes everything in your database");
  }

  @Override
  public void run(Namespace namespace, Liquibase liquibase) throws Exception {
    liquibase.dropAll();
  }
}
