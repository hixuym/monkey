package io.monkey.migrations;

import io.monkey.datasource.DataSourceFactory;
import io.monkey.datasource.DatabaseConfiguration;

public class TestMigrationDatabaseConfiguration implements DatabaseConfiguration<TestMigrationConfiguration> {

    @Override
    public DataSourceFactory getDataSourceFactory(TestMigrationConfiguration configuration) {
        return configuration.getDataSource();
    }
}
