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

import java.sql.SQLException;

import io.sunflower.db.ManagedDataSource;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class CloseableLiquibaseWithClassPathMigrationsFile extends CloseableLiquibase implements
    AutoCloseable {

  CloseableLiquibaseWithClassPathMigrationsFile(
      ManagedDataSource dataSource,
      Database database,
      String file
  ) throws LiquibaseException, SQLException {
    super(file,
        new ClassLoaderResourceAccessor(),
        database,
        dataSource);
  }

  public CloseableLiquibaseWithClassPathMigrationsFile(
      ManagedDataSource dataSource,
      String file
  ) throws LiquibaseException, SQLException {
    this(dataSource,
        DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection())),
        file);
  }
}
