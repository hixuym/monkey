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

import com.codahale.metrics.MetricRegistry;
import io.sunflower.Configuration;
import io.sunflower.cli.ConfiguredCommand;
import io.sunflower.db.DatabaseConfiguration;
import io.sunflower.db.ManagedDataSource;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.setup.Bootstrap;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.exception.ValidationFailedException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public abstract class AbstractLiquibaseCommand<T extends Configuration> extends
    ConfiguredCommand<T> {

  private final DatabaseConfiguration<T> strategy;
  private final Class<T> configurationClass;
  private final String migrationsFileName;

  protected AbstractLiquibaseCommand(String name,
      String description,
      DatabaseConfiguration<T> strategy,
      Class<T> configurationClass,
      String migrationsFileName) {
    super(name, description);
    this.strategy = strategy;
    this.configurationClass = configurationClass;
    this.migrationsFileName = migrationsFileName;
  }

  @Override
  protected Class<T> getConfigurationClass() {
    return configurationClass;
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);

    subparser.addArgument("--migrations")
        .dest("migrations-file")
        .help("the file containing the Liquibase migrations for the application");

    subparser.addArgument("--catalog")
        .dest("catalog")
        .help("Specify the database catalog (use database default if omitted)");

    subparser.addArgument("--schema")
        .dest("schema")
        .help("Specify the database schema (use database default if omitted)");
  }

  @Override
  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration)
      throws Exception {
    final PooledDataSourceFactory dbConfig = strategy.getDataSourceFactory(configuration);
    dbConfig.asSingleConnectionPool();

    try (final CloseableLiquibase liquibase = openLiquibase(dbConfig, namespace)) {
      run(namespace, liquibase);
    } catch (ValidationFailedException e) {
      e.printDescriptiveError(System.err);
      throw e;
    }
  }

  CloseableLiquibase openLiquibase(final PooledDataSourceFactory dataSourceFactory,
      final Namespace namespace)
      throws SQLException, LiquibaseException {
    final CloseableLiquibase liquibase;
    final ManagedDataSource dataSource = dataSourceFactory.build(new MetricRegistry(), "liquibase");
    final Database database = createDatabase(dataSource, namespace);
    final String migrationsFile = namespace.getString("migrations-file");
    if (migrationsFile == null) {
      liquibase = new CloseableLiquibaseWithClassPathMigrationsFile(dataSource, database,
          migrationsFileName);
    } else {
      liquibase = new CloseableLiquibaseWithFileSystemMigrationsFile(dataSource, database,
          migrationsFile);
    }

    return liquibase;
  }

  private Database createDatabase(
      ManagedDataSource dataSource,
      Namespace namespace
  ) throws SQLException, LiquibaseException {
    final DatabaseConnection conn = new JdbcConnection(dataSource.getConnection());
    final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);

    final String catalogName = namespace.getString("catalog");
    final String schemaName = namespace.getString("schema");

    if (database.supportsCatalogs() && catalogName != null) {
      database.setDefaultCatalogName(catalogName);
      database.setOutputDefaultCatalog(true);
    }
    if (database.supportsSchemas() && schemaName != null) {
      database.setDefaultSchemaName(schemaName);
      database.setOutputDefaultSchema(true);
    }

    return database;
  }

  protected abstract void run(Namespace namespace, Liquibase liquibase) throws Exception;
}
