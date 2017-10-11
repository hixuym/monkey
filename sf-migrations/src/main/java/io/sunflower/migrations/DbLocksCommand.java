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

import static com.google.common.base.MoreObjects.firstNonNull;

import com.google.common.annotations.VisibleForTesting;
import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import java.io.PrintStream;
import liquibase.Liquibase;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class DbLocksCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

  private PrintStream printStream = System.out;

  public DbLocksCommand(DatabaseConfiguration<T> strategy, Class<T> configurationClass,
      String migrationsFileName) {
    super("locks", "Manage database migration locks", strategy, configurationClass,
        migrationsFileName);
  }

  @VisibleForTesting
  void setPrintStream(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);

    subparser.addArgument("-l", "--list")
        .dest("list")
        .action(Arguments.storeTrue())
        .setDefault(Boolean.FALSE)
        .help("list all open locks");

    subparser.addArgument("-r", "--force-release")
        .dest("release")
        .action(Arguments.storeTrue())
        .setDefault(Boolean.FALSE)
        .help("forcibly release all open locks");
  }

  @Override
  public void run(Namespace namespace, Liquibase liquibase) throws Exception {
    final boolean list = firstNonNull(namespace.getBoolean("list"), false);
    final boolean release = firstNonNull(namespace.getBoolean("release"), false);

    if (list == release) {
      throw new IllegalArgumentException("Must specify either --list or --force-release");
    } else if (list) {
      liquibase.reportLocks(printStream);
    } else {
      liquibase.forceReleaseLocks();
    }
  }
}
