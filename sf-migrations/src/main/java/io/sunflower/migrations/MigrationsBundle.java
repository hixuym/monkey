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

import io.sunflower.Bundle;
import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

public abstract class MigrationsBundle<T extends Configuration> implements Bundle,
    DatabaseConfiguration<T> {

  private static final String DEFAULT_NAME = "db";
  private static final String DEFAULT_MIGRATIONS_FILE = "migrations.xml";

  @Override
  @SuppressWarnings("unchecked")
  public final void initialize(Bootstrap<?> bootstrap) {
    final Class<T> klass = (Class<T>) bootstrap.getApplication().getConfigurationClass();
    bootstrap.addCommand(new DbCommand<>(name(), this, klass, getMigrationsFileName()));
  }

  public String getMigrationsFileName() {
    return DEFAULT_MIGRATIONS_FILE;
  }

  public String name() {
    return DEFAULT_NAME;
  }

  @Override
  public final void run(Environment environment) {
    // nothing doing
  }
}
