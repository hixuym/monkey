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

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import liquibase.Liquibase;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class DbFastForwardCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

  private PrintStream printStream = System.out;

  protected DbFastForwardCommand(DatabaseConfiguration<T> strategy, Class<T> configurationClass,
      String migrationsFileName) {
    super("fast-forward",
        "Mark the next pending change set as applied without running it",
        strategy,
        configurationClass,
        migrationsFileName);
  }

  @VisibleForTesting
  void setPrintStream(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);

    subparser.addArgument("-n", "--dry-run")
        .action(Arguments.storeTrue())
        .dest("dry-run")
        .setDefault(Boolean.FALSE)
        .help("output the DDL to stdout, don't run it");

    subparser.addArgument("-a", "--all")
        .action(Arguments.storeTrue())
        .dest("all")
        .setDefault(Boolean.FALSE)
        .help("mark all pending change sets as applied");

    subparser.addArgument("-i", "--include")
        .action(Arguments.append())
        .dest("contexts")
        .help("include change sets from the given context");
  }

  @Override
  public void run(Namespace namespace,
      Liquibase liquibase) throws Exception {
    final String context = getContext(namespace);
    if (namespace.getBoolean("all")) {
      if (namespace.getBoolean("dry-run")) {
        liquibase
            .changeLogSync(context, new OutputStreamWriter(printStream, StandardCharsets.UTF_8));
      } else {
        liquibase.changeLogSync(context);
      }
    } else {
      if (namespace.getBoolean("dry-run")) {
        liquibase.markNextChangeSetRan(context,
            new OutputStreamWriter(printStream, StandardCharsets.UTF_8));
      } else {
        liquibase.markNextChangeSetRan(context);
      }
    }
  }

  private String getContext(Namespace namespace) {
    final List<Object> contexts = namespace.getList("contexts");
    if (contexts == null) {
      return "";
    }
    return Joiner.on(',').join(contexts);
  }
}
