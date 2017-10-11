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

import com.google.common.base.Joiner;
import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import java.util.List;
import liquibase.Liquibase;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class DbTestCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

  public DbTestCommand(DatabaseConfiguration<T> strategy, Class<T> configurationClass,
      String migrationsFileName) {
    super("test", "Apply and rollback pending change sets.", strategy, configurationClass,
        migrationsFileName);
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);

    subparser.addArgument("-i", "--include")
        .action(Arguments.append())
        .dest("contexts")
        .help("include change sets from the given context");
  }

  @Override
  public void run(Namespace namespace, Liquibase liquibase) throws Exception {
    liquibase.updateTestingRollback(getContext(namespace));
  }

  private String getContext(Namespace namespace) {
    final List<Object> contexts = namespace.getList("contexts");
    if (contexts == null) {
      return "";
    }
    return Joiner.on(',').join(contexts);
  }
}
