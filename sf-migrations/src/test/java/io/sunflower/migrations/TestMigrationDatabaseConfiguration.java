package io.sunflower.migrations;

import io.sunflower.datasource.DataSourceFactory;
import io.sunflower.datasource.DatabaseConfiguration;

public class TestMigrationDatabaseConfiguration implements DatabaseConfiguration<TestMigrationConfiguration> {

    @Override
    public DataSourceFactory getDataSourceFactory(TestMigrationConfiguration configuration) {
        return configuration.getDataSource();
    }
}
