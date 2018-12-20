package io.sunflower.migrations;

import io.sunflower.db.DataSourceFactory;
import io.sunflower.db.DatabaseConfiguration;

public class TestMigrationDatabaseConfiguration implements DatabaseConfiguration<TestMigrationConfiguration> {

    @Override
    public DataSourceFactory getDataSourceFactory(TestMigrationConfiguration configuration) {
        return configuration.getDataSource();
    }
}
