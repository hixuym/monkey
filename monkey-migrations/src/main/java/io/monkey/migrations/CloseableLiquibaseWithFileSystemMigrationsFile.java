package io.monkey.migrations;

import io.monkey.datasource.ManagedDataSource;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

import java.sql.SQLException;

public class CloseableLiquibaseWithFileSystemMigrationsFile extends CloseableLiquibase implements AutoCloseable {

    CloseableLiquibaseWithFileSystemMigrationsFile(
        ManagedDataSource dataSource,
        Database database,
        String file
    ) throws LiquibaseException, SQLException {
        super(file,
              new FileSystemResourceAccessor(),
              database,
              dataSource);
    }

    public CloseableLiquibaseWithFileSystemMigrationsFile(
        ManagedDataSource dataSource,
        String file
    ) throws LiquibaseException, SQLException {
        this(dataSource,
            DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection())),
            file);
    }
}
